package LLD.flipkart;

import LLD.util.repository.IRepository;
import lombok.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


enum ProductCategory{
    ELECTRONICS,
    DAILY_CARE,
    FOOT_WEAR;
}

@ToString
@AllArgsConstructor
@Getter
class Product{
    String id;
    String name;
    String sellerId;
    ProductCategory category;
    double price;
    boolean available;
}

class Customer{
    String id;
    String name;
    String mobile;
    String address;
    List<Order> orders;

}

@AllArgsConstructor
class ProductOrder{
    enum Status {Ordered, Shipped, Delivered}
    String id;
    String productId;
    int quantity;
    double price;
    Status status;
}

@Builder
class Order{
    String id;
    String userid;
    List<ProductOrder> subOrders;

}

//  <------ interfaceRepository -------->
interface IUserRepository{ }

interface IInventoryRepository{
    void addItems(String productId, int qty);
    void subsItems(String productId, int qty);
    Integer getItem(String productId);
}

interface IProductRepository extends IRepository<Product> {
}

class ProductRepository implements IProductRepository{
    Map<String, Product> productMap = new HashMap<>(){{
        put("p1", new Product("p1", "p1", "s1", ProductCategory.ELECTRONICS, 100.0, true));
        put("p2", new Product("p2", "p2", "s1", ProductCategory.ELECTRONICS, 100.0, true));
        put("p3", new Product("p3", "p3", "s1", ProductCategory.FOOT_WEAR, 200.0, true));
    }};

    @Override
    public void save(Product entity) {
        productMap.put(entity.getId(), entity);
    }

    @Override
    public void update(Product entity) {
        productMap.put(entity.getId(), entity);
    }

    @Override
    public void delete(Product entity) {
        productMap.remove(entity.getId());
    }

    @Override
    public Product findById(String id) {
        return productMap.get(id);
    }

    @Override
    public List<Product> findAll() {
        return productMap.values().stream().toList();
    }
}

class InventoryRepository implements IInventoryRepository{
    Map<String, Integer> inventory =  new ConcurrentHashMap<>(){{
        put("p1", 2);
        put("p2", 0);
    }};
    public void addItems(String productId, int quantity){
        inventory.put(productId, inventory.getOrDefault(productId, 0)+quantity);
    }

    public void subsItems(String productId, int quantity){
        if (!inventory.containsKey(productId))
            throw new IllformedLocaleException(String.format("Item %s is out of stock", productId));
        int q = inventory.get(productId);
        if (q < quantity)
            throw new IllformedLocaleException("Inventory quantity is lesser.");
        if (q == quantity)
            inventory.remove(productId);
        else
            inventory.put(productId, inventory.get(productId)-quantity);
    }

    public Integer getItem(String productId){
        return inventory.get(productId);
    }
}

interface ICartRepository{
    void addToCart(String userId, String productId);
    void removeFromCart(String userId, String productId);
    double checkOut(String userId);
    Map<String, Integer> getCart(String userId);
}

class CartRepository implements ICartRepository{
    Map<String, Map<String, Integer>> userCart = new HashMap<>();
    ProductRepository productRepository;
    InventoryRepository inventoryRepository;

    CartRepository(ProductRepository productRepository, InventoryRepository inventoryRepository){
        this.productRepository =  productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public Map<String, Integer> getCart(String userId){
        return userCart.get(userId);
    }

    public void addToCart(String userId, String productId){
        // check Inventory
        if(inventoryRepository.getItem(productId) == null || inventoryRepository.getItem(productId) <=0){
            System.out.printf("Item %s is out of stock%n", productId);
        }

        Map<String, Integer> cart = userCart.get(userId);
        if (cart == null){
            cart =  new ConcurrentHashMap<>();
        }
        cart.put(productId, cart.getOrDefault(productId, 0)+1);
        userCart.put(userId, cart);
    }

    public void removeFromCart(String userId, String productId){
        Map<String, Integer> cart = userCart.get(userId);
        if (Objects.isNull(cart) || !cart.containsKey(productId))
            throw new IllformedLocaleException("Item not found in cart");
        cart.remove(productId);
        userCart.put(userId, cart);
    }

    public double checkOut(String userId){
        Map<String, Integer> cart = userCart.get(userId);
        double[] totalAmount = new double[2];
        cart.forEach((productId, qty) -> {
            totalAmount[0] += qty * productRepository.findById(productId).getPrice();
        });
        return totalAmount[0];
    }

    public void printCart(String userId){
        Map<String, Integer> cart = userCart.get(userId);
        cart.forEach((productId, qty) -> {
            System.out.println(productRepository.findById(productId).getName() + " : " + qty);
        });
    }
}

interface IShoppingPlatform {
    List<Product> browse(Optional<Map<String, String>> queryFilter);
    void addToCart(String userId, String productId);
    double checkOut(String userId);
    void printCart(String userId);
    Order placeOrder(String userId);
}

public class ShoppingPlatform implements IShoppingPlatform{
    ProductRepository productRepository = new ProductRepository();
    InventoryRepository inventoryRepository = new InventoryRepository();
    CartRepository cartRepository = new CartRepository(productRepository, inventoryRepository);

    @Override
    public List<Product> browse(Optional<Map<String, String>> queryFilterOptional) {
        if (queryFilterOptional.isEmpty())
            return productRepository.findAll();
        final Predicate<Product>[] predicate = new Predicate[]{p -> true};
        queryFilterOptional.get().forEach((key, value) ->{
            switch (key){
                case "Category":
                    predicate[0] = predicate[0].and(p -> p.category.name().equals(value));
                    break;
                case "isAvailable":
                    predicate[0] = predicate[0].and(p -> p.isAvailable());
            }
        });
        return productRepository.findAll().stream()
                .filter(predicate[0])
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void addToCart(String userId, String productId) {
        cartRepository.addToCart(userId, productId);
        System.out.println(productId + " is added to cart for userId " +userId);
    }

    @Override
    public double checkOut(String userId) {
        return cartRepository.checkOut(userId);
    }

    @Override
    public void printCart(String userId) {
        cartRepository.printCart(userId);
    }

    @Override
    @Synchronized
    public Order placeOrder(String userId) {
        double amount = cartRepository.checkOut(userId);
        Map<String, Integer> cart = cartRepository.getCart(userId);
        List<ProductOrder> productOrders = new ArrayList<>();
        cart.forEach((pId, qty) -> {
            productOrders.add(new ProductOrder(UUID.randomUUID().toString(), pId, qty, 100, ProductOrder.Status.Ordered));
            inventoryRepository.subsItems(pId, qty);
            cartRepository.removeFromCart(userId, pId);
        });
        Order order  =  Order.builder()
                .id(UUID.randomUUID().toString())
                .userid(userId)
                .subOrders(productOrders)
                .build();
        System.out.println("Order create for user "+userId);
        return order;
        // orderRepository.save(order);
    }

    public static void main(String[] args) {
        ShoppingPlatform shoppingPlatform = new ShoppingPlatform();
        System.out.println(shoppingPlatform.browse(Optional.empty()));

        // add to cart
        shoppingPlatform.addToCart("user1", "p1");
        shoppingPlatform.printCart("user1");

        // place order
        shoppingPlatform.placeOrder("user1");


        // concurrent item access
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<Void> callable = () -> {
            shoppingPlatform.addToCart("user1", "p1");
            return null;
        };
        Callable<Void> callable2 = () -> {
            shoppingPlatform.addToCart("user2", "p1");
            return null;
        };
        try {
            executorService.invokeAll(List.of(callable, callable2));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
        shoppingPlatform.printCart("user1");
        shoppingPlatform.printCart("user2");

        // concurrent order
        executorService = Executors.newFixedThreadPool(2);
        Callable<Order> callableOrder = () -> {
            shoppingPlatform.placeOrder("user1");
            return null;
        };
        Callable<Order> callableOrder2 = () -> {
            shoppingPlatform.placeOrder("user2");
            return null;
        };
        Future<Order> f1 = executorService.submit(callableOrder);
        Future<Order> f2 = executorService.submit(callableOrder2);
        try {
            System.out.println(f1.get());
            System.out.println(f2.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
    }
}










