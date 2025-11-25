package LLD.LogisticSystem;

import LLD.util.Notification.NotificationService;
import LLD.util.Notification.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

abstract class Vehicle {
    private String id;
    private String type; // e.g., "Truck", "Bike"
    private boolean available;
    public Vehicle(String id, String type) {
        this.id = id;
        this.type = type;
        this.available = true;
    }
    // Abstract method for polymorphism
    public abstract double getCapacity(); // e.g., weight limit
    // Getters/Setters
    public String getId() { return id; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}

class Truck extends Vehicle {
    public Truck(String id) {
        super(id, "Truck");
    }
    @Override
    public double getCapacity() {
        return 1000.0; // kg
    }
}
// Bike subclass
class Bike extends Vehicle {
    public Bike(String id) {
        super(id, "Bike");
    }
    @Override
    public double getCapacity() {
        return 50.0; // kg
    }
}

enum ShipmentStatus {
    CREATED, PICKUP_SCHEDULED, PICKED_UP, IN_TRANSIT, AT_WAREHOUSE, OUT_FOR_DELIVERY, DELIVERED, RETURNED, CANCELLED, NOT_FOUND
}
@Setter
@Getter
// Shipment class (Encapsulation)
class Shipment {
    private String id;
    private String senderId;
    private String receiverId;
    private String origin;
    private String destination;
    private double weight;
    volatile ShipmentStatus status;
    private String warehouseId;
    private String vehicleId;
    public Shipment(String id, String senderId, String receiverId, String origin, String destination, double weight) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
        this.status = ShipmentStatus.CREATED;
    }
    // Methods
    public void updateStatus(ShipmentStatus status) { this.status = status; }
    // Getters/Setters
}

@Getter
// Warehouse class
class Warehouse {
    private String id;
    private String location;
    private List<String> shipmentIds; // Stored shipments
    public Warehouse(String id, String location) {
        this.id = id;
        this.location = location;
        this.shipmentIds = new ArrayList<>();
    }
    public void addShipment(String shipmentId) { shipmentIds.add(shipmentId); }
    // Getters
}

@Getter
// User class
class User {
    private String id;
    private String name;
    private String address;
    public User(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
    // Getters
}

// ===== Pricing Strategy (Strategy Pattern) =====
interface PricingStrategy {
    double calculatePrice(double weightKg, double l, double b, double h);
}

// Implementations (Single Responsibility)
class FlatRateStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(double weightKg, double l, double b, double h) {
        return 100.0; // Flat rate
    }
}

class WeightBasedStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(double weightKg, double l, double b, double h) {
        return weightKg * 10.0; // $10 per kg
    }
}

// Interfaces (Abstraction, Interface Segregation)
interface IShipmentService {
    Shipment createShipment(String senderId, String receiverId, String origin, String destination, double weight);
    void assignToWarehouse(String shipmentId, String warehouseId);
    ShipmentStatus trackShipment(String shipmentId);
}

interface IDeliveryService {
    void assignDelivery(String shipmentId, Vehicle vehicle);
    void completeDelivery(String shipmentId);
}

interface IWarehouseService {
    Warehouse getWarehouse(String location);
}

@Getter
// Implementations (Single Responsibility)
class ShipmentService implements IShipmentService {
    private Map<String, Shipment> shipments = new HashMap<>(); // In-memory

    @Override
    public Shipment createShipment(String senderId, String receiverId, String origin, String destination, double weight) {
        String id = UUID.randomUUID().toString();
        Shipment shipment = new Shipment(id, senderId, receiverId, origin, destination, weight);
        shipments.put(id, shipment);
        return shipment;
    }

    @Override
    public void assignToWarehouse(String shipmentId, String warehouseId) {
        Shipment shipment = shipments.get(shipmentId);
        if (shipment != null) {
            shipment.setWarehouseId(warehouseId);
            shipment.setStatus(ShipmentStatus.AT_WAREHOUSE);
        }
    }

    @Override
    public ShipmentStatus trackShipment(String shipmentId) {
        Shipment shipment = shipments.get(shipmentId);
        return shipment != null ? shipment.getStatus() : ShipmentStatus.NOT_FOUND;
    }
}

@Getter
class DeliveryService implements IDeliveryService {
    private Map<String, Shipment> shipments; // Injected

    public DeliveryService(Map<String, Shipment> shipments) {
        this.shipments = shipments;
    }

    @Override
    public void assignDelivery(String shipmentId, Vehicle vehicle) {
        Shipment shipment = shipments.get(shipmentId);
        if (shipment != null && vehicle.isAvailable() && vehicle.getCapacity() >= shipment.getWeight()) {
            shipment.setVehicleId(vehicle.getId());
            vehicle.setAvailable(false);
            shipment.updateStatus(ShipmentStatus.IN_TRANSIT);
            NotificationService.getInstance().notifyAsync(NotificationType.SMS, "Shipment " + shipmentId + " assigned to vehicle " + vehicle.getId(), shipment.getSenderId());
        }
    }

    @Override
    public void completeDelivery(String shipmentId) {
        Shipment shipment = shipments.get(shipmentId);
        if (shipment != null) {
            shipment.updateStatus(ShipmentStatus.DELIVERED);
            // Free vehicle (simplified)
            NotificationService.getInstance().notifyAsync(NotificationType.SMS,"Shipment " + shipmentId + " delivered!", shipment.getSenderId());
        }
    }
}

class WarehouseService implements IWarehouseService {
    private Map<String, Warehouse> warehouses = new HashMap<>(); // Pre-populated

    public WarehouseService() {
        warehouses.put("Delhi", new Warehouse("WH1", "Delhi"));
        warehouses.put("Mumbai", new Warehouse("WH2", "Mumbai"));
    }

    @Override
    public Warehouse getWarehouse(String location) {
        return warehouses.get(location);
    }
}

public class LogisticsService {
    public static void main(String[] args) {
        // Dependency Injection (SOLID)
        IShipmentService shipmentService = new ShipmentService();
        IWarehouseService warehouseService = new WarehouseService();
        IDeliveryService deliveryService = new DeliveryService(((ShipmentService) shipmentService).getShipments()); // Hack for demo; use proper injection

        // Create users
        User sender = new User("U1", "Alice", "Delhi");
        User receiver = new User("U2", "Bob", "Mumbai");

        // Create shipment
        Shipment shipment = shipmentService.createShipment(sender.getId(), receiver.getId(), "Delhi", "Mumbai", 20.0);

        // Assign to warehouse
        Warehouse warehouse = warehouseService.getWarehouse("Delhi");
        shipmentService.assignToWarehouse(shipment.getId(), warehouse.getId());

        // Track
        System.out.println("Status: " + shipmentService.trackShipment(shipment.getId()));

        // Assign delivery
        Vehicle truck = new Truck("V1");
        deliveryService.assignDelivery(shipment.getId(), truck);

        // Complete
        deliveryService.completeDelivery(shipment.getId());
    }
}


