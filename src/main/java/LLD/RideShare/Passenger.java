package LLD.RideShare;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

// --- Entities --- //

class Passenger {
    String name;
    String contact;
    List<Ride> ongoingRides = new ArrayList<>();
    List<Ride> completedRides = new ArrayList<>();

    Passenger(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    // Passenger requests a ride
    Ride requestRide(RideService service, String pickup, String destination, RideType type) {
        RideRequest request = new RideRequest(this, pickup, destination, type);
        return service.addRideRequest(request);
    }
}

class Driver {
    String name;
    String vehicle;
    boolean available = true;
    List<Ride> ongoingRides = new ArrayList<>();
    List<Ride> completedRides = new ArrayList<>();

    Driver(String name, String vehicle) {
        this.name = name;
        this.vehicle = vehicle;
    }

    // Accept ride from RideService
    Ride acceptRide(RideService service, RideRequest request) {
        if (!available) {
            System.out.println("Driver " + name + " is not available.");
            return null;
        }
        Ride ride = service.assignDriverToRide(request, this);
        if (ride != null) {
            available = false;
            ongoingRides.add(ride);
            System.out.println("Driver " + name + " accepted ride: " + ride);
        }
        return ride;
    }

    void startRide(Ride ride) {
        ride.startRide();
        System.out.println("Driver " + name + " started ride: " + ride);
    }

    void completeRide(Ride ride, RideService service) {
        ride.completeRide();
        ongoingRides.remove(ride);
        completedRides.add(ride);
        available = true;
        service.processPayment(ride);
        System.out.println("Driver " + name + " completed ride: " + ride);
    }
}

// --- Ride Request & Ride --- //

enum RideType { REGULAR, PREMIUM }
enum RideStatus { REQUESTED, ACCEPTED, ONGOING, COMPLETED }

class RideRequest {
    static AtomicInteger counter = new AtomicInteger(1);
    int requestId;
    Passenger passenger;
    String pickup;
    String destination;
    RideType rideType;

    RideRequest(Passenger p, String pickup, String dest, RideType type) {
        this.requestId = counter.getAndIncrement();
        this.passenger = p;
        this.pickup = pickup;
        this.destination = dest;
        this.rideType = type;
    }

    public String toString() {
        return "RideRequest{" + requestId + ", " + passenger.name + ", " + pickup + "->" + destination + ", " + rideType + "}";
    }
}

class Ride {
    RideRequest request;
    Driver driver;
    RideStatus status;
    double fare;

    Ride(RideRequest request, Driver driver, double fare) {
        this.request = request;
        this.driver = driver;
        this.fare = fare;
        this.status = RideStatus.ACCEPTED;
    }

    void startRide() { status = RideStatus.ONGOING; }
    void completeRide() { status = RideStatus.COMPLETED; }
    public String toString() {
        return "Ride{" + request.passenger.name + ", Driver=" + driver.name + ", Fare=" + fare + ", Status=" + status + "}";
    }
}

// --- Service Layer --- //

class RideService {
    private List<RideRequest> rideRequests = new CopyOnWriteArrayList<>();
    private List<Ride> ongoingRides = new CopyOnWriteArrayList<>();
    private PaymentService paymentService = new PaymentService();

    Ride addRideRequest(RideRequest request) {
        rideRequests.add(request);
        System.out.println("New ride requested: " + request);
        // broadCast to near by drivers (all)
        return null;
    }

    Ride assignDriverToRide(RideRequest request, Driver driver) {
        if (!rideRequests.contains(request)) return null;
        rideRequests.remove(request);
        double fare = calculateFare(request);
        Ride ride = new Ride(request, driver, fare);
        ongoingRides.add(ride);
        return ride;
    }

    private double calculateFare(RideRequest request) {
        double baseFare = request.rideType == RideType.REGULAR ? 10 : 20;
        Random rand = new Random();
        double distanceFare = rand.nextInt(20) + 5;
        return baseFare + distanceFare;
    }

    void processPayment(Ride ride) {
        paymentService.processPayment(ride.request.passenger, ride.driver, ride.fare);
        ongoingRides.remove(ride);
    }
}

// --- Payment Service --- //

class PaymentService {
    void processPayment(Passenger passenger, Driver driver, double amount) {
        System.out.println("Payment of $" + amount + " from " + passenger.name + " to " + driver.name + " processed.");
    }
}

// --- Demo --- //

class RideSharingApp {
    public static void main(String[] args) {
        RideService service = new RideService();

        // Create drivers
        Driver d1 = new Driver("Alice", "Car1");
        Driver d2 = new Driver("Bob", "Car2");

        // Create passengers
        Passenger p1 = new Passenger("John", "12345");
        Passenger p2 = new Passenger("Emma", "67890");

        // Passengers request rides
        RideRequest r1 = new RideRequest(p1, "Point A", "Point B", RideType.REGULAR);
        RideRequest r2 = new RideRequest(p2, "Point C", "Point D", RideType.PREMIUM);

        service.addRideRequest(r1);
        service.addRideRequest(r2);

        // Drivers accept rides
        Ride ride1 = d1.acceptRide(service, r1);
        Ride ride2 = d2.acceptRide(service, r2);

        // Start rides
        if (ride1 != null) d1.startRide(ride1);
        if (ride2 != null) d2.startRide(ride2);

        // Complete rides
        if (ride1 != null) d1.completeRide(ride1, service);
        if (ride2 != null) d2.completeRide(ride2, service);

        // Print completed rides for each passenger
        System.out.println("\nPassenger " + p1.name + " completed rides: " + p1.completedRides.size());
        System.out.println("Passenger " + p2.name + " completed rides: " + p2.completedRides.size());
    }
}