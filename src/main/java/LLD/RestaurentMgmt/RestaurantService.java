package LLD.RestaurentMgmt;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// ----------------- Domain Models ----------------- //

class Ingredient {
    final String name;
    final String unit; // e.g. "kg", "pcs"
    final AtomicInteger quantity; // store smallest units (e.g. grams or pieces)

    Ingredient(String name, String unit, int initialQty) {
        this.name = name;
        this.unit = unit;
        this.quantity = new AtomicInteger(initialQty);
    }

    public int available() { return quantity.get(); }
    public String toString() { return name + ": " + quantity.get() + " " + unit; }
}

class MenuItem {
    final String id;
    final String name;
    final int priceCents; // store money as cents to avoid float precision
    final Map<String, Integer> recipe; // ingredientName -> quantity required

    MenuItem(String id, String name, int priceCents, Map<String, Integer> recipe) {
        this.id = id; this.name = name; this.priceCents = priceCents; this.recipe = recipe;
    }

    @Override public String toString() {
        return name + " (₹" + (priceCents / 100.0) + ")";
    }
}

class Customer {
    final String id;
    String name;
    String contact;

    Customer(String id, String name, String contact) { this.id = id; this.name = name; this.contact = contact; }
}

enum OrderStatus { PLACED, PREPARING, READY, COMPLETED, CANCELLED }

class OrderItem {
    final String menuItemId;
    final int quantity;
    OrderItem(String menuItemId, int quantity) { this.menuItemId = menuItemId; this.quantity = quantity; }
}

class Order {
    final String id;
    final String customerId;
    final List<OrderItem> items;
    final LocalDateTime placedAt;
    volatile OrderStatus status = OrderStatus.PLACED;
    int totalPriceCents = 0; // computed during processing
    Order(String id, String customerId, List<OrderItem> items) {
        this.id = id; this.customerId = customerId; this.items = items; this.placedAt = LocalDateTime.now();
    }
}

class Reservation {
    final String id;
    final String customerId;
    final LocalDateTime from;
    final int partySize;
    Reservation(String id, String customerId, LocalDateTime from, int partySize) {
        this.id = id; this.customerId = customerId; this.from = from; this.partySize = partySize;
    }
}

class Staff {
    final String id;
    final String name;
    final String role;
    Staff(String id, String name, String role) { this.id = id; this.name = name; this.role = role; }
}

class ScheduleEntry {
    final String staffId;
    final LocalDateTime start;
    final LocalDateTime end;
    ScheduleEntry(String staffId, LocalDateTime start, LocalDateTime end) { this.staffId = staffId; this.start = start; this.end = end; }
}

// ----------------- Payment Strategy ----------------- //

interface PaymentStrategy {
    boolean pay(int amountCents, Map<String, String> paymentDetails);
    String name();
}

class CashPayment implements PaymentStrategy {
    public boolean pay(int amountCents, Map<String, String> paymentDetails) {
        // cash handled at counter - assume success
        return true;
    }
    public String name() { return "Cash"; }
}

class CardPayment implements PaymentStrategy {
    public boolean pay(int amountCents, Map<String, String> paymentDetails) {
        // stub - in real system call payment gateway
        String card = paymentDetails.get("cardNumber");
        return card != null && card.length() >= 12;
    }
    public String name() { return "Card"; }
}

class MobilePayment implements PaymentStrategy {
    public boolean pay(int amountCents, Map<String, String> paymentDetails) {
        String wallet = paymentDetails.get("walletId");
        return wallet != null && !wallet.isEmpty();
    }
    public String name() { return "Mobile"; }
}

// ----------------- In-Memory Repositories (thread-safe) ----------------- //

class Inventory {
    // ingredientName -> Ingredient
    private final ConcurrentHashMap<String, Ingredient> store = new ConcurrentHashMap<>();
    // simple lock to coordinate multi-ingredient checks/updates in this example
    private final Object inventoryLock = new Object();

    void addIngredient(String name, String unit, int qty) {
        store.compute(name, (k, old) -> {
            if (old == null) return new Ingredient(name, unit, qty);
            old.quantity.addAndGet(qty);
            return old;
        });
    }

    Ingredient getIngredient(String name) { return store.get(name); }

    Map<String, Ingredient> snapshot() { return new HashMap<>(store); }

    /**
     * Try to reserve required ingredients for a menu item *batch*.
     * If all available -> deduct and return true.
     * If any shortage -> do nothing and return false.
     * Synchronized to keep consistency for this simple example.
     */
    boolean tryReserve(Map<String, Integer> required, int multiplier) {
        synchronized (inventoryLock) {
            // check
            for (Map.Entry<String, Integer> e : required.entrySet()) {
                Ingredient ing = store.get(e.getKey());
                int need = e.getValue() * multiplier;
                if (ing == null || ing.available() < need) return false;
            }
            // deduct
            for (Map.Entry<String, Integer> e : required.entrySet()) {
                Ingredient ing = store.get(e.getKey());
                int need = e.getValue() * multiplier;
                ing.quantity.addAndGet(-need);
            }
            return true;
        }
    }

    void release(Map<String, Integer> required, int multiplier) {
        synchronized (inventoryLock) {
            for (Map.Entry<String, Integer> e : required.entrySet()) {
                Ingredient ing = store.get(e.getKey());
                if (ing != null) ing.quantity.addAndGet(e.getValue() * multiplier);
            }
        }
    }

    String report() {
        StringBuilder sb = new StringBuilder();
        for (Ingredient i : store.values()) sb.append(i).append("\n");
        return sb.toString();
    }
}

class MenuRepository {
    final ConcurrentHashMap<String, MenuItem> store = new ConcurrentHashMap<>();
    void add(MenuItem m) { store.put(m.id, m); }
    Optional<MenuItem> find(String id) { return Optional.ofNullable(store.get(id)); }
    List<MenuItem> all() { return new ArrayList<>(store.values()); }
}

class CustomerRepository {
    final ConcurrentHashMap<String, Customer> store = new ConcurrentHashMap<>();
    void add(Customer c) { store.put(c.id, c); }
    Optional<Customer> find(String id) { return Optional.ofNullable(store.get(id)); }
}

class OrderRepository {
    final ConcurrentHashMap<String, Order> store = new ConcurrentHashMap<>();
    void add(Order o) { store.put(o.id, o); }
    Optional<Order> find(String id) { return Optional.ofNullable(store.get(id)); }
    List<Order> all() { return new ArrayList<>(store.values()); }
}

class ReservationRepository {
    final ConcurrentHashMap<String, Reservation> store = new ConcurrentHashMap<>();
    void add(Reservation r) { store.put(r.id, r); }
    List<Reservation> upcoming() {
        return store.values().stream().toList();
    }
}

class StaffRepository {
    final ConcurrentHashMap<String, Staff> store = new ConcurrentHashMap<>();
    void add(Staff s) { store.put(s.id, s); }
    Optional<Staff> find(String id) { return Optional.ofNullable(store.get(id)); }
}

// ----------------- Service Layer / Orchestrator ----------------- //

public class RestaurantService {
    final Inventory inventory = new Inventory();
    final MenuRepository menuRepo = new MenuRepository();
    final CustomerRepository customerRepo = new CustomerRepository();
    final OrderRepository orderRepo = new OrderRepository();
    final ReservationRepository reservationRepo = new ReservationRepository();
    final StaffRepository staffRepo = new StaffRepository();

    // For simple analytics
    final AtomicInteger totalSalesCents = new AtomicInteger(0);
    final List<String> auditLogs = Collections.synchronizedList(new ArrayList<>());

    // Place an order: check inventory, reserve ingredients, compute bill, accept payment, set status
    public Optional<Order> placeOrder(String customerId, List<OrderItem> items, PaymentStrategy payment, Map<String,String> paymentDetails) {
        // validate customer
        if (customerRepo.find(customerId).isEmpty()) {
            audit("Order failed: customer not found " + customerId);
            return Optional.empty();
        }
        // aggregate required ingredients and compute price
        Map<String, Integer> totalRequired = new HashMap<>();
        int totalPrice = 0;
        for (OrderItem oi : items) {
            Optional<MenuItem> maybe = menuRepo.find(oi.menuItemId);
            if (maybe.isEmpty()) { audit("Order failed: menu item not found " + oi.menuItemId); return Optional.empty(); }
            MenuItem mi = maybe.get();
            totalPrice += mi.priceCents * oi.quantity;
            for (Map.Entry<String,Integer> e : mi.recipe.entrySet()) {
                totalRequired.merge(e.getKey(), e.getValue() * oi.quantity, Integer::sum);
            }
        }

        // try reserve inventory
        boolean reserved = inventory.tryReserve(totalRequired, 1);
        if (!reserved) {
            audit("Order failed: insufficient ingredients for customer " + customerId);
            return Optional.empty();
        }

        // process payment
        boolean paid = payment.pay(totalPrice, paymentDetails);
        if (!paid) {
            // revert inventory
            inventory.release(totalRequired, 1);
            audit("Order failed: payment declined for customer " + customerId);
            return Optional.empty();
        }

        // create order
        Order order = new Order(UUID.randomUUID().toString(), customerId, items);
        order.totalPriceCents = totalPrice;
        order.status = OrderStatus.PREPARING;
        orderRepo.add(order);
        totalSalesCents.addAndGet(totalPrice);
        audit("Order placed: " + order.id + " customer:" + customerId + " amount:" + (totalPrice/100.0) + " paidBy:" + payment.name());

        // Simulate preparation asynchronously (simple)
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(500); // simulate cook time
            } catch (InterruptedException ignored) {}
            order.status = OrderStatus.READY;
            audit("Order ready: " + order.id);
            // In production: notify customer via push/WS
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {}
            order.status = OrderStatus.COMPLETED;
            audit("Order completed: " + order.id);
        });

        return Optional.of(order);
    }

    public Reservation makeReservation(String customerId, LocalDateTime from, int partySize) {
        Reservation r = new Reservation(UUID.randomUUID().toString(), customerId, from, partySize);
        reservationRepo.add(r);
        audit("Reservation made: " + r.id + " for customer " + customerId + " at " + from);
        return r;
    }

    public void addMenuItem(MenuItem m) { menuRepo.add(m); audit("Menu added: " + m.name); }
    public void addIngredient(String name, String unit, int qty) { inventory.addIngredient(name, unit, qty); audit("Ingredient added: "+name+" "+qty+unit); }
    public void addCustomer(Customer c) { customerRepo.add(c); audit("Customer added: " + c.id); }
    public void addStaff(Staff s) { staffRepo.add(s); audit("Staff added: " + s.name); }

    // Simple billing / report methods
    public String salesReport() {
        return "Total Sales: ₹" + (totalSalesCents.get() / 100.0) + "\nOrders: " + orderRepo.all().size();
    }

    public String inventoryReport() { return inventory.report(); }

    public List<Order> listOrders() { return orderRepo.all(); }

    private void audit(String s) { auditLogs.add(LocalDateTime.now() + " - " + s); }

    public List<String> getAuditLogs() { return new ArrayList<>(auditLogs); }
}

// ----------------- Demo Main ----------------- //

class RestaurantApp {
    public static void main(String[] args) throws Exception {
        RestaurantService rs = new RestaurantService();

        // Seed inventory & menu
        rs.addIngredient("Tomato", "pcs", 50);
        rs.addIngredient("Cheese", "g", 500);
        rs.addIngredient("Dough", "g", 2000);
        rs.addIngredient("Lettuce", "pcs", 20);

        Map<String,Integer> pizzaRecipe = new HashMap<>();
        pizzaRecipe.put("Dough", 250);
        pizzaRecipe.put("Tomato", 3);
        pizzaRecipe.put("Cheese", 100);

        Map<String,Integer> saladRecipe = new HashMap<>();
        saladRecipe.put("Lettuce", 1);
        saladRecipe.put("Tomato", 2);

        rs.addMenuItem(new MenuItem("m1","Margherita Pizza", 49900, pizzaRecipe)); // ₹499.00
        rs.addMenuItem(new MenuItem("m2","Green Salad", 19900, saladRecipe));       // ₹199.00

        // Add customers
        Customer alice = new Customer("c1","Alice","+91-999");
        Customer bob = new Customer("c2","Bob","+91-888");
        rs.addCustomer(alice); rs.addCustomer(bob);

        // Place order: Alice orders 1 pizza, pay by card
        List<OrderItem> items = List.of(new OrderItem("m1",1));
        PaymentStrategy card = new CardPayment();
        Map<String,String> cardDetails = Map.of("cardNumber", "424242424242");
        Optional<Order> maybeOrder = rs.placeOrder(alice.id, items, card, cardDetails);
        if (maybeOrder.isPresent()) {
            System.out.println("Order accepted: " + maybeOrder.get().id);
        } else System.out.println("Order failed");

        // Place order: Bob orders 2 salads, pay by mobile
        List<OrderItem> items2 = List.of(new OrderItem("m2",2));
        PaymentStrategy mobile = new MobilePayment();
        Map<String,String> mobileDetails = Map.of("walletId", "bob_wallet_1");
        rs.placeOrder(bob.id, items2, mobile, mobileDetails);

        // Make a reservation
        rs.makeReservation(alice.id, LocalDateTime.now().plusDays(1).withHour(19).withMinute(0), 4);

        // Wait a little for async order completion in demo
        Thread.sleep(1200);

        // Reports
        System.out.println("\n--- Sales Report ---");
        System.out.println(rs.salesReport());

        System.out.println("\n--- Inventory Report ---");
        System.out.println(rs.inventoryReport());

        System.out.println("\n--- Audit Logs ---");
        rs.getAuditLogs().forEach(System.out::println);
    }
}