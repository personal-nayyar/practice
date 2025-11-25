package LLD.FoodDeliveryApp.v2;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

enum OrderStatus { CREATED, CONFIRMED, PREPARING, READY_FOR_PICKUP, PICKED_UP, DELIVERING, DELIVERED, CANCELLED }

class MenuItem {
    final long id;
    final String name;
    volatile double price; // changeable by restaurant
    volatile boolean available;

    public MenuItem(long id, String name, double price, boolean available) {
        this.id = id; this.name = name; this.price = price; this.available = available;
    }
}

class Menu {
    public final Map<Long, MenuItem> items = new ConcurrentHashMap<>();
    public void addItem(MenuItem item) { items.put(item.id, item); }
    public void removeItem(long id) { items.remove(id); }
    public Collection<MenuItem> getItems() { return items.values(); }
    public MenuItem getItem(long id) { return items.get(id); }
}

class Restaurant {
    final long id;
    final String name;
    final Menu menu = new Menu();
    volatile boolean isOpen = true;

    public Restaurant(long id, String name) { this.id = id; this.name = name; }
}


class DeliveryAgent {
    final long id;
    final String name;
    volatile boolean available = true;

    public DeliveryAgent(long id, String name) { this.id = id; this.name = name; }
}

class Customer {
    final long id;
    final String name;
    final String phone;

    public Customer(long id, String name, String phone) { this.id = id; this.name = name; this.phone = phone; }
}

class OrderItem {
    final MenuItem item;
    final int qty;
    final double priceAtOrder;

    public OrderItem(MenuItem item, int qty) {
        this.item = item; this.qty = qty; this.priceAtOrder = item.price;
    }
}

class Order {
    final long id;
    final long restaurantId;
    final long customerId;
    final List<OrderItem> items;
    final double total;
    volatile OrderStatus status;
    volatile Long deliveryAgentId; // may be null until assigned
    final long createdAt = System.currentTimeMillis();

    public Order(long id, long restaurantId, long customerId, List<OrderItem> items) {
        this.id = id; this.restaurantId = restaurantId; this.customerId = customerId;
        this.items = List.copyOf(items);
        this.total = items.stream().mapToDouble(i -> i.priceAtOrder * i.qty).sum();
        this.status = OrderStatus.CREATED;
    }
}
//  ------------- interface/Repository --------------

// ---------- Notifications (simple observer-like) ----------

// ---------- Payment (Strategy + Factory) ----------

class FoodDeliveryService{
    // in-memory stores
    private final Map<Long, Restaurant> restaurants = new ConcurrentHashMap<>();
    private final Map<Long, Customer> customers = new ConcurrentHashMap<>();
    private final Map<Long, DeliveryAgent> agents = new ConcurrentHashMap<>();
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();

    private final AtomicLong orderIdGen = new AtomicLong(1);
    private final ExecutorService notificationPool = Executors.newCachedThreadPool();


    public void registerRestaurant(long id, String name) {
        restaurants.put(id, new Restaurant(id, name));
    }

    public void addMenuItem(long restaurantId, long itemId, String name, double price) {
        Restaurant r = restaurants.get(restaurantId);
        if (r != null) r.menu.items.put(itemId, new MenuItem(itemId, name, price, true));
    }

    public void registerCustomer(long id, String name) {
        customers.put(id, new Customer(id, name, ""));
    }

    public void registerDeliveryAgent(long id, String name) {
        agents.put(id, new DeliveryAgent(id, name));
    }

    public List<Restaurant> browseRestaurants() {
        return new ArrayList<>(restaurants.values());
    }

    public Optional<Order> placeOrder(long restaurantId, long customerId, List<Long> itemIds) {
        Restaurant r = restaurants.get(restaurantId);
        Customer c = customers.get(customerId);
        if (r == null || c == null || !r.isOpen) {
            System.out.println("Order failed — invalid restaurant or customer");
            return Optional.empty();
        }

        List<OrderItem> selectedItems = new ArrayList<>();
        for (long id : itemIds) {
            MenuItem m = r.menu.getItem(id);
            if (m == null || !m.available) {
                System.out.println("Item unavailable: " + id);
                return Optional.empty();
            }
            selectedItems.add(new OrderItem(m, 1));
        }

        Order order = new Order(orderIdGen.getAndIncrement(), restaurantId, customerId, selectedItems);
        order.status = OrderStatus.CONFIRMED;
        orders.put(order.id, order);

        notifyAsync("Customer " + c.name + " placed order " + order.id);
        notifyAsync("Restaurant " + r.name + " received order " + order.id);

        simulatePreparation(order);
        return Optional.of(order);
    }


    public void cancelOrder(long orderId) {
        Order o = orders.get(orderId);
        if (o != null && o.status != OrderStatus.DELIVERED) {
            o.status = OrderStatus.CANCELLED;
            notifyAsync("Order " + orderId + " cancelled.");
        } else {
            System.out.println("Order cannot be cancelled or not found.");
        }
    }

    public void assignDeliveryPartner(long orderId, String agentName) {
        Order o = orders.get(orderId);
        if (o == null) return;

        DeliveryAgent agent = agents.values().stream()
                .filter(a -> a.name.equals(agentName) && a.available)
                .findFirst().orElse(null);

        if (agent == null) {
            System.out.println("No available delivery agent found!");
            return;
        }

        synchronized (agent) {
            if (!agent.available) return; // double-check
            agent.available = false;
            o.deliveryAgentId = agent.id;
            o.status = OrderStatus.PICKED_UP;
        }

        notifyAsync("Agent " + agent.name + " picked up order " + o.id);
        deliverOrder(o, agent);
    }

    public void trackOrder(long orderId) {
        Order o = orders.get(orderId);
        if (o != null) System.out.println("Order " + o.id + " status: " + o.status);
    }

    private void simulatePreparation(Order o) {
        CompletableFuture.runAsync(() -> {
            try {
                o.status = OrderStatus.PREPARING;
                Thread.sleep(500);
                o.status = OrderStatus.PICKED_UP;
                notifyAsync("Order " + o.id + " is ready for pickup!");
            } catch (InterruptedException ignored) {}
        });
    }

    private void deliverOrder(Order o, DeliveryAgent agent) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000);
                o.status = OrderStatus.DELIVERED;
                agent.available = true;
                notifyAsync("Order " + o.id + " delivered successfully by " + agent.name);
            } catch (InterruptedException ignored) {}
        });
    }

    private void notifyAsync(String msg) {
        notificationPool.submit(() -> System.out.println("[NOTIFY] " + msg));
    }

    public void shutdown() {
        notificationPool.shutdown();
    }
}

class FoodDeliveryAppFacadeDemo {
    public static void main(String[] args) throws InterruptedException {
        FoodDeliveryService service = new FoodDeliveryService();

        // Setup data
        service.registerRestaurant(1, "Tasty Bites");
        service.addMenuItem(1, 101, "Paneer Butter Masala", 250);
        service.addMenuItem(1, 102, "Garlic Naan", 40);
        service.registerCustomer(201, "Alice");
        service.registerDeliveryAgent(301, "agent1");

        // Place order
        Optional<Order> orderOpt = service.placeOrder(1, 201, List.of(101L, 102L));
        Order order = orderOpt.orElseThrow();

        // Concurrently cancel and assign
        Callable<Void> cancelTask = () -> { service.cancelOrder(order.id); return null; };
        Callable<Void> assignTask = () -> { service.assignDeliveryPartner(order.id, "agent1"); return null; };

        ExecutorService exec = Executors.newFixedThreadPool(2);
        List<Future<Void>> futures = exec.invokeAll(List.of(cancelTask, assignTask));
        for (Future<Void> f : futures) {
            try { f.get(); }
            catch (ExecutionException e) { e.getCause().printStackTrace(); }
        }
        exec.shutdown();

        // Track
        service.trackOrder(order.id);

        Thread.sleep(2000); // wait for async notifications
        service.shutdown();
    }
}
