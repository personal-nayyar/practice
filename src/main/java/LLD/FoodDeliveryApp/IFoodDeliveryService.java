package LLD.FoodDeliveryApp;

import LLD.util.address.Address;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import utils.ThreadUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/*
User
    - Customer
    -DeliveryAgent
Restaurant
Order
MenuItem
OrderStatus
* */
abstract class User{
    String id;
    String name;
    String email;
    String phone;
    User(String id, String name){
        this.id = id;
        this.name = name;
    }
}

class Customer extends User{

    String customerId;
    Address address;
    List<Order> orders;

    Customer(String id, String name) {
        super(id, name);
        this.orders = new ArrayList<>();
    }
}

class DeliveryAgent extends User{
    String agentId;
    Queue<Order> orders;

    DeliveryAgent(String id, String name) {
        super(id, name);
        this.orders = new ArrayDeque<>();
    }
}

class Restaurant{
    String id;
    String name;
    String city;
    Address address;
    List<MenuItem> menu;

    Restaurant(String name, String city, List<MenuItem> menuItems){
        this.name = name;
        this.city = city;
        this.menu = menuItems;
    }
}

@Getter
class MenuItem{
    String id;
    String restaurantId;
    String name;
    double price;
    String description;
    String category;
    Map<String, Integer> recipe;
    volatile boolean available;


    MenuItem(String name, double price){
        this.name = name;
        this.price =price;
    }
}

enum OrderStatus{
    PENDING,
    ACCEPTED,
    BEING_PREPARED,
    BEING_DELIVERED,
    DELIVERED,
    CANCELLED
}

@ToString
@Getter
@Setter
class Order{
    String id;
    OrderStatus status;
    String customerId;
    String deliveryAgentId;
    String restaurantId;
    Map<MenuItem, Integer> items;
    double totalAmount;
    Address address;
}


interface IFoodDeliveryService {
    List<Restaurant> browseRestaurant(String city);
    List<MenuItem> viewCatalog(String restaurantId);
    Order placeOrder(String customerId, String restaurantId, Map<MenuItem, Integer> items);
    OrderStatus trackOrder(String orderId);
    void cancelOrder(String orderId);
    void assignDeliveryPartner(String orderId, String userId);
    List<Order> getOrdersForCustomer(String customerId);
}

class DeliveryService implements IFoodDeliveryService{
    Map<String, Customer> customerRepo =  new HashMap<>(){{
        put("cust1", new Customer("cust1", "cust1"));
        put("cust2", new Customer("cust2", "cust2"));
    }};

    Map<String, DeliveryAgent> deliveryAgentRepo =  new HashMap<>(){{
        put("agent1", new DeliveryAgent("agent1", "agent1"));
        put("agent2", new DeliveryAgent("agent2", "agent2"));
    }};

    Map<String, Restaurant> restaurantRepo = new HashMap<>(){{
        put("rest1", new Restaurant("rest1", "city1", Arrays.asList(new MenuItem("item1", 100.0), new MenuItem("item2", 200.0))));
        put("rest2", new Restaurant("rest2", "city2", Arrays.asList(new MenuItem("item3", 100.0), new MenuItem("item4", 200.0))));
    }};

    Map<String, Order> orderRepo = new ConcurrentHashMap<>();
    Map<String, ReentrantLock> locks = new ConcurrentHashMap<>(); // lock per order

    public ReentrantLock lockFor(String orderId){
        locks.putIfAbsent(orderId, new ReentrantLock());
        return locks.get(orderId);
    }


    @Override
    public List<Restaurant> browseRestaurant(String city) {
        return restaurantRepo.values().stream().filter(restaurant -> restaurant.city.equals(city)).collect(Collectors.toList());
    }

    @Override
    public List<MenuItem> viewCatalog(String restaurantId) {
        return restaurantRepo.get(restaurantId).menu;
    }

    @Override
    public Order placeOrder(String customerId, String restaurantId, Map<MenuItem, Integer> items) {
        Customer customer = customerRepo.get(customerId);

        synchronized (lockFor(customerId)){
            Order order = new Order();
            order.setId(UUID.randomUUID().toString());
            order.setCustomerId(customerId);
            order.setRestaurantId(restaurantId);
            order.setAddress(customer.address);
            order.setStatus(OrderStatus.ACCEPTED);
            order.setTotalAmount(items.entrySet().stream().mapToDouble((entry -> entry.getKey().getPrice() * entry.getValue())).sum());
            order.setItems(items);
            orderRepo.put(order.getId(), order);
            return order;
        }
    }

    @Override
    public OrderStatus trackOrder(String orderId) {
        return orderRepo.get(orderId).getStatus();
    }

    @Override
    public void cancelOrder(String orderId) {
        boolean locked =  lockFor(orderId).tryLock();
        if (!locked){
            throw new ConcurrentModificationException("Concurrent modification failed");
        }
        try{
            orderRepo.get(orderId).setStatus(OrderStatus.CANCELLED);
            ThreadUtils.sleepSeconds(2);
        } finally {
            lockFor(orderId).unlock();
        }
    }

    @Override
    public void assignDeliveryPartner(String orderId, String userId) {
        orderRepo.get(orderId).setDeliveryAgentId(userId);
        boolean locked =  lockFor(orderId).tryLock();
        if (!locked){
            throw new ConcurrentModificationException("Concurrent modification failed");
        }
        try{
            orderRepo.get(orderId).setStatus(OrderStatus.BEING_DELIVERED);
            ThreadUtils.sleepSeconds(2);
        } finally {
            lockFor(orderId).unlock();
        }
    }

    @Override
    public List<Order> getOrdersForCustomer(String customerId) {
        return orderRepo.values().stream().filter(order -> order.getCustomerId().equals(customerId)).collect(Collectors.toList());
    }
}

class Runner{
    public static void main(String[] args) {
        DeliveryService foodDeliveryService = new DeliveryService();

        Order order = foodDeliveryService.placeOrder("cust1", "rest1", Map.of(new MenuItem("item1", 100.0), 2));
        System.out.println(foodDeliveryService.trackOrder(order.getId()));
        foodDeliveryService.assignDeliveryPartner(order.getId(), "agent1");
        System.out.println(foodDeliveryService.trackOrder(order.getId()));
        foodDeliveryService.cancelOrder(order.getId());
        System.out.println(foodDeliveryService.getOrdersForCustomer("cust1"));

        Order order2 = foodDeliveryService.placeOrder("cust2", "rest1", Map.of(new MenuItem("item1", 100.0), 2));
        // concurrent access
        Callable<Void> runnable = () -> {
            foodDeliveryService.cancelOrder(order2.getId());
            return null;
        };
        Callable<Void>  runnable1  = () -> {
            foodDeliveryService.assignDeliveryPartner(order2.getId(), "agent1");
            return null;
        };
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            List<Future<Void>> futures = executorService.invokeAll(List.of(runnable, runnable1));
            for (Future<Void> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Task failed: " + e.getCause());
//            throw new RuntimeException(e);
        }
        executorService.shutdown();
        System.out.println(foodDeliveryService.trackOrder(order2.getId()));

    }
}



