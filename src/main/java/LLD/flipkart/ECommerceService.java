package LLD.flipkart;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public interface ECommerceService {
    void addToCart(String userId, String productId);
    Cart getCart(String userId);
    void printCart(String userId);
    Order placeOrder(String userId, Cart cart);
}

@ToString
@Getter
@AllArgsConstructor
// User.java
class User {
    private String id;
    private String name;
    private String email;
    private String password;
    Cart cart;// In a real system, use hashed passwords for security
    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.cart = new Cart();
    }
}

@ToString
@Getter
class Cart{
    Map<Product2, Integer> items =  new HashMap<>(); // product -> qty

    void addToCart(Product2 product, int qty){
        items.put(product, items.getOrDefault(product, 0)+qty);
    }

    public void removeItem(Product product) {
        items.remove(product);
    }
    public Map<Product2, Integer> getItems() {
        return items;
    }
}

@Getter
class Product2{
    enum Category {FOOT_WEAR, TOP_WEAR, BOTTOM_WEAR}
    String id;
    String name;
    String seller;
    double price;
    int qty;
    boolean available;
    Category category;
    ReentrantLock lock = new ReentrantLock();

    public Product2(String id, String seller, double price, int qty, Category category) {
        this.id = id;
        this.seller = seller;
        this.price = price;
        this.qty = qty;
        this.category = category;
    }

    public void updateQty(int qty){
        lock.lock();
        try {
            if (qty < 0){
                throw new RuntimeException("Product out of stock");
            }
            this.qty -= qty;
            this.available = qty > 0;
        } finally {
            lock.unlock();
        }
    }

    public int hashCode(){
        return id.hashCode();
    }

    public boolean equals(Object obj){
        if(obj instanceof Product2){
            Product2 other = (Product2) obj;
            return this.id.equals(other.id);
        }
        return false;
    }

    public String toString(){
        return id;
    }
}

@Getter
class OrderService implements ECommerceService{
    Map<String, User> userRepo = new HashMap<>(){{
        put("user1", new User("user1", "user1", "user1", "user1"));
        put("user2", new User("user2", "user2", "user2", "user2"));
    }};

    Map<String, Product2> product2Repo = new HashMap<>(){{
        put("p1", new Product2("p1", "p1", 100, 2, Product2.Category.TOP_WEAR));
        put("p2", new Product2("p2", "p2", 100, 2, Product2.Category.TOP_WEAR));
        put("p3", new Product2("p3", "p3", 100, 2, Product2.Category.TOP_WEAR));

    }};


    @Override
    public void addToCart(String userId, String productId) {
        userRepo.get(userId).getCart().addToCart(product2Repo.get(productId), 1);
    }

    @Override
    public Cart getCart(String userId) {
        return userRepo.get(userId).getCart();
    }

    @Override
    public void printCart(String userId){
        System.out.printf("%s cart:\n", userId);
        userRepo.get(userId).getCart().getItems().forEach((product, qty) -> {
            System.out.println(product.getId() + " : " + qty);
        });
    }

    @Override
    public Order placeOrder(String userId, Cart cart) {
        Map<Product2, Integer> items = cart.getItems();
        double[] amount = new double[]{0};
        List<ProductOrder> productOrders = new ArrayList<>();
        items.forEach((product, qty) -> {
            // check inventory and update qty
            product.updateQty(product.getQty() - qty);
            product2Repo.put(product.getId(), product);

            // add amouunt
            amount[0] += product.getPrice() * qty;

            // create product order
            productOrders.add(new ProductOrder(UUID.randomUUID().toString(), product.getId(), qty, product.getPrice(), ProductOrder.Status.Ordered));
        });
        return Order.builder()
                .userid(userId)
                .subOrders(productOrders)
                .build();
    }
}

class Runner{
    public static void main(String[] args) {
        OrderService orderService = new OrderService();
        System.out.println(orderService.product2Repo.values());

        // add to cart
        orderService.addToCart("user1", "p1");
        orderService.printCart("user1");

        // place order
        orderService.placeOrder("user1", orderService.getCart("user1"));


        // concurrent item access
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<Void> callable = () -> {
            orderService.addToCart("user1", "p1");
            return null;
        };
        Callable<Void> callable2 = () -> {
            orderService.addToCart("user2", "p1");
            return null;
        };
        try {
            executorService.invokeAll(List.of(callable, callable2));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
        orderService.printCart("user1");
        orderService.printCart("user2");

        // concurrent order
        executorService = Executors.newFixedThreadPool(2);
        Callable<Order> callableOrder = () -> {
            orderService.placeOrder("user1", orderService.getCart("user1"));
            return null;
        };
        Callable<Order> callableOrder2 = () -> {
            orderService.placeOrder("user2", orderService.getCart("user2"));
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




