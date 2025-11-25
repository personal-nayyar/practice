package A_interview_experiences.flipkart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class RideShareApplication {
}

/*
function req:
    user onboarding
        - add_user(user_detail)
    add Vehicle
        - add_vehicle(vehicle_detail)
    Driver offer rides
        - offer_ride(ride_detail)
        - end_ride(ride_details)
    Passenger select ride
        select_ride(source, destination, seats, selection_strategy)
        print_ride_stats()

non-functional req:
    user’s origin/destinations are not available directly but it’s possible via multiple rides
    Bangalore to Mumbai, the output can be Bangalore to Goa and Goa to Mumbai

Entity:
    User:
        - Driver
        - Passenger
    Vehicle
        - HATCHBACK
        - SEDAN
        - SUV
    - RIDE
        - SHARED
        - GO
        - PREMIUM

     - RideRequest
        -
* */
@AllArgsConstructor
class User{
    String name;
    String sex;
    int age;
    //.. username, password
}

class Driver extends User{
    String driverId;

    public Driver(String name, String sex, int age) {
        super(name, sex, age);
    }

    // .....
}

class Passenger extends User{
    String passengerId;

    public Passenger(String name, String sex, int age) {
        super(name, sex, age);
    }

    // .....
}

enum VehicleType{
    HATCHBACK,
    SEDAN,
    SUV,
    DEFAULT
}

@AllArgsConstructor
@Getter
class Vehicle{
    String owner;
    String make;
    String registrationNumber;
}

class HATCHBACK extends Vehicle{

    public HATCHBACK(String owner, String make, String registrationNumber) {
        super(owner, make, registrationNumber);
    }
}

class SEDAN extends Vehicle{
    public SEDAN(String owner, String make, String registrationNumber) {
        super(owner, make, registrationNumber);
    }
}

class SUV extends Vehicle{
    public SUV(String owner, String make, String registrationNumber) {
        super(owner, make, registrationNumber);
    }
}

class VehicleFactory{
    private static VehicleFactory instance;
    private VehicleFactory(){}
    public static VehicleFactory getInstance(){
        if (instance == null){
            synchronized (VehicleFactory.class){
                if (instance == null){
                    instance = new VehicleFactory();
                }
            }
        }
        return instance;
    }

    public Vehicle getVehicle(VehicleType vehicleType, String owner, String make, String registrationNumber){
        return switch (vehicleType){
            case SUV -> new SUV(owner, make, registrationNumber);
            case SEDAN -> new SEDAN(owner, make, registrationNumber);
            case HATCHBACK -> new HATCHBACK(owner, make, registrationNumber);
            case DEFAULT ->  new Vehicle(owner, make,registrationNumber);
            default -> throw new IllegalArgumentException("Vehicle type " + vehicleType + " does not exist");
        };
    }
}

enum RideStatus{
    OFFERED,
    BOOKED,
    ENDED;
}

@Getter
@Setter
@SuperBuilder
class Ride{
    String id;
    String owner;
    String vehicle;
    String source;
    String destination;
    RideStatus status;
    int seatAvailable;
    Ride(String source, String destination){
        this.source =  source;
        this.destination = destination;
    }
}

@Builder
class Booking{
    String rideId;
    String passengerId;
    int seats;
    boolean active;
    ISelectionStrategy selectionStrategy;

//    public Booking(String rideId, int seats, ISelectionStrategy selectionStrategy) {
//        this.rideId =  rideId;
//        this.seats =  seats;
//        this.selectionStrategy = selectionStrategy;
//    }
}

class RideService{
    Map<String, Ride> rideRepository;
    Map<String,Booking> bookingRepository;
    Map<String, Integer> endedOfferedCount;
    Map<String, Integer> endedTakenCount;
    RideService(){
        rideRepository = new HashMap<>();
        bookingRepository = new HashMap<>();
        endedOfferedCount = new HashMap<>();
        endedTakenCount = new HashMap<>();
    }

    public Ride offerRide(String owner, String source, String destination, int seat, String vehicle){
        // validate driver and vehicle
        Ride ride = Ride.builder()
                .id(UUID.randomUUID().toString())
                .source(source)
                .destination(destination)
                .owner(owner)
                .vehicle(vehicle)
                .seatAvailable(seat)
                .status(RideStatus.OFFERED)
                .build();
        rideRepository.putIfAbsent(ride.id, ride);
        return ride;
    }

    public Booking bookRide(String passengerId, String source, String dest, int seats, ISelectionStrategy selectionStrategy){
        List<Ride> rides = findAvailableRide(source, dest, seats);
        Optional<Ride> rideOptional = selectionStrategy.selectRide(rides, seats, "");
        if (rideOptional.isEmpty())
            throw new IllegalStateException("Not ride found in selection strategy");
        Ride ride = rideOptional.get();

        // update ride
        ride.setStatus(RideStatus.BOOKED);
        ride.setSeatAvailable(ride.seatAvailable - seats);

        // create booking
        return Booking.builder()
                .rideId(ride.getId())
                .passengerId(passengerId)
                .seats(seats)
                .selectionStrategy(selectionStrategy)
                .build();
    }

    private List<Ride> findAvailableRide(String source, String dest, int seats){
        return rideRepository.values().stream()
                .filter(ride -> ride.status == RideStatus.OFFERED
                        && ride.source.equals(source) && ride.destination.equals(dest)
                        && ride.seatAvailable >= seats
                ).collect(Collectors.toUnmodifiableList());
    }

    public void endRideByOfferId(int rideId) {
        Ride ride = rideRepository.get(rideId);
        if (ride == null) throw new IllegalStateException("Ride not found: " + rideId);
        if (ride.status == RideStatus.ENDED) throw new IllegalStateException("Ride already ended: " + rideId);
        ride.status = RideStatus.ENDED;
//        activeVehicleRegs.remove(ride.vehicle.registrationNumber);
        // Count this ride as an "offered" ended ride for owner
        endedOfferedCount.put(ride.owner, endedOfferedCount.getOrDefault(ride.owner, 0) + 1);

        // Count taken bookings for this ride that are ended: mark bookings as inactive and credit passenger endedTakenCount
        for (Booking b : bookingRepository.values()) {
            if (b.rideId.equals(rideId) && b.active) {
                b.active = false;
                endedTakenCount.put(b.passengerId, endedTakenCount.getOrDefault(b.passengerId, 0) + 1);
            }
        }
    }
}

interface ISelectionStrategy {
    Optional<Ride> selectRide(List<Ride> offerRides, int seats, String make);
}

class MostVacantStrategy implements ISelectionStrategy {
    public Optional<Ride> selectRide(List<Ride> candidates, int requiredSeats, String ignored) {
        return candidates.stream()
                .filter(r -> r.seatAvailable >= requiredSeats && r.status == RideStatus.OFFERED)
                .max(Comparator.comparingInt(r -> r.seatAvailable));
    }
}

class PreferredVehicleStrategy implements ISelectionStrategy {
    public Optional<Ride> selectRide(List<Ride> candidates, int requiredSeats, String preferredVehicleMake) {
        return candidates.stream()
                .filter(r -> r.seatAvailable >= requiredSeats && r.status == RideStatus.OFFERED)
//                .filter(r -> r.vehicle.make.equalsIgnoreCase(preferredVehicleMake))
                .findFirst();
    }
}


class UserService{
    Map<String, User> userRepo;

    UserService(){
        userRepo = new HashMap<>();
    }

    public void addUser(String name, String sex, int age){
        if (userRepo.containsKey(name)){
            throw new IllegalStateException("User already Exist");
        }
        userRepo.put(name, new User(name, sex, age));
    }

    public User getUser(String name){
        if (!userRepo.containsKey(name)){
            throw new IllegalStateException("User does not Exist");
        }
        return userRepo.get(name);
    }
}

class VehicleService{
    Map<String, Vehicle> vehicleRepo;
    UserService userService;
    VehicleFactory factory;


    VehicleService(UserService userService){
        vehicleRepo = new HashMap<>();
        this.userService = userService;
        this.factory = VehicleFactory.getInstance();
    }

    public void addVehicle(String owner, String make, String regNumber){
        userService.getUser(owner);
        if (vehicleRepo.containsKey(regNumber)){
            throw new IllegalStateException("Vehicle already registered with user "+vehicleRepo.get(regNumber).owner);
        }
        Vehicle vehicle =  factory.getVehicle(VehicleType.DEFAULT, owner, make, regNumber);
        vehicleRepo.put(regNumber, vehicle);
    }
}