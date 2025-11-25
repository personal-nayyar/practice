package LLD.CarRental;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// < --- Model --->

@ToString
@AllArgsConstructor
abstract class Vehicle{ // extendalibity
    final String id; // UUID
    final String make;
    final String model;
    final int year;
    final String licensePlate;
    final double pricePerDay;
    final ReentrantLock lock = new ReentrantLock();
}


class Car extends Vehicle{
    enum Type { SEDAN, SUV, HATCHBACK, VAN, TRUCK }

    Type type;

    Car(String id, String make, String model, int year, String licencePlate, double pricePerDay, Type type){
        super(id, make, model, year, licencePlate, pricePerDay);
        this.type =  type;
        this.transmissionType = "Automatic";
    }
    String transmissionType;
}


class Bike extends Vehicle{
    Bike(String id, String make, String model, int year, String licencePlate){
        super(id, make, model, year, licencePlate, 0);
        this.fuelType = "Petrol";
    }
    String fuelType;
}

@AllArgsConstructor
abstract class User{
    String id;
    String name;
    String email;
    String phoneNumber;
}


class Customer extends User{
    String address;
    String drivingLicense;

    public Customer(String id, String name, String email, String phoneNumber) {
        super(id, name, email, phoneNumber);
    }
}

class Admin extends User{
    String role;

    public Admin(String id, String name, String email, String phoneNumber) {
        super(id, name, email, phoneNumber);
    }
}

@ToString
@NoArgsConstructor
@AllArgsConstructor
class Booking{
    enum Status { CREATED, CONFIRMED, CANCELLED }
    String id;
    Vehicle vehicle;
    Customer customer;
    LocalDateTime startTime;
    LocalDateTime endTime;
    double totalPrice;
    Status status;
}

enum PaymentStatus{
    PENDING,
    COMPLETED,
    FAILED
}
class Payment {
    enum Method {CARD, UPI, WALLET}

    enum Status {INITIATED, SUCCESS, FAILED}

    String id;
    double amount;
    Method mode;
    Status status;
}

interface PaymentStrategy{
    PaymentStatus pay(double amount);
}

class CreditCardPaymentStrategy implements PaymentStrategy{
    @Override
    public PaymentStatus pay(double amount){
        // implement payment logic
        return PaymentStatus.COMPLETED;
    }
}

// <---- interface/Repository-------->
interface IVehicleRepository{
    void save(Vehicle vehicle);
    void delete(Vehicle vehicle);
    Vehicle get(String id);
    List<Vehicle> getAll();
}

interface IUserRepository{
    void save(User user);
    void delete(User user);
    User get(String id);
    List<User> getAll();
}

interface IBookingRepository{
    Booking save(Booking booking);
    void delete(Booking booking);
    Booking get(String id);
    List<Booking> getAll();
}

interface IPaymentRepository{
    void save(Payment payment);
    void delete(Payment payment);
    Payment get(String id);
    List<Payment> getAll();
}

interface ISearchRepository{
    List<Vehicle> search(Map<String, String> filterQuery);
    List<Vehicle> getAvailableVehicle(Map<String, String> filterQuery, LocalDateTime startTime, LocalDateTime endTime);
    boolean isAvailable(Vehicle vehicle, LocalDateTime startTime, LocalDateTime endTime);
    double calculateTotalPrice(Vehicle vehicle, LocalDateTime startTime, LocalDateTime endTime);
}

//<-----InterfaceImpl-------->
class VehicleRepositoryImpl implements IVehicleRepository{
    // singleton instance
    private static VehicleRepositoryImpl instance;
    private VehicleRepositoryImpl(){
        vehicleMap = new HashMap<>();
    }
    public static VehicleRepositoryImpl getInstance(){
        if(instance == null){
            instance = new VehicleRepositoryImpl();
        }
        return instance;
    }


    Map<String, Vehicle> vehicleMap; // <id, vehicle>
    @Override
    public void save(Vehicle vehicle){
        vehicleMap.put(vehicle.id, vehicle);
    }
    @Override
    public void delete(Vehicle vehicle){
        vehicleMap.remove(vehicle.id);
    }
    @Override
    public Vehicle get(String id){
        return vehicleMap.get(id);
    }
    @Override
    public List<Vehicle> getAll(){
        return new ArrayList<>(vehicleMap.values());
    }
}

class UserRepositoryImpl implements IUserRepository{
    Map<String, User> userMap; // <id, user>
    UserRepositoryImpl(){
        userMap = new HashMap<>();
    }
    @Override
    public void save(User user){
        userMap.put(user.id, user);
    }
    @Override
    public void delete(User user){
        userMap.remove(user.id);
    }
    @Override
    public User get(String id){
        return userMap.get(id);
    }
    @Override
    public List<User> getAll(){
        return new ArrayList<>(userMap.values());
    }
}

class BookingRepositoryImpl implements IBookingRepository{
    Map<String, Booking> bookingMap; // <VehicleId, booking>

    // singleton instance
    private static BookingRepositoryImpl instance;
    private BookingRepositoryImpl(){
        bookingMap = new HashMap<>();
    }
    public static BookingRepositoryImpl getInstance(){
        if(instance == null){
            instance = new BookingRepositoryImpl();
        }
        return instance;
    }

    @Override
    public Booking save(Booking booking){
         return bookingMap.put(booking.vehicle.id, booking);
    }
    @Override
    public void delete(Booking booking){
        bookingMap.remove(booking.vehicle.id);
    }
    @Override
    public Booking get(String id){
        return bookingMap.get(id);
    }
    @Override
    public List<Booking> getAll(){
        return new ArrayList<>(bookingMap.values());
    }
}

class PaymentRepositoryImpl implements IPaymentRepository{
    Map<String, Payment> paymentMap; // <id, payment>
    //singleton class
    private static PaymentRepositoryImpl instance;
    private PaymentRepositoryImpl(){
        paymentMap = new HashMap<>();
    }
    public static PaymentRepositoryImpl getInstance(){
        if(instance == null){
            instance = new PaymentRepositoryImpl();
        }
        return instance;
    }

    @Override
    public void save(Payment payment){
        paymentMap.put(payment.id, payment);
    }
    @Override
    public void delete(Payment payment){
        paymentMap.remove(payment.id);
    }
    @Override
    public Payment get(String id){
        return paymentMap.get(id);
    }
    @Override
    public List<Payment> getAll(){
        return new ArrayList<>(paymentMap.values());
    }
}

class SearchRepositoryImpl implements ISearchRepository{
    IVehicleRepository vehicleRepository;
    IBookingRepository bookingRepository;

    // singleton class
    private static SearchRepositoryImpl instance;
    private SearchRepositoryImpl(IVehicleRepository vehicleRepository, IBookingRepository bookingRepository){
        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
    }
    public static SearchRepositoryImpl getInstance(){
        if(instance == null){
            instance = new SearchRepositoryImpl(VehicleRepositoryImpl.getInstance(), BookingRepositoryImpl.getInstance());
        }
        return instance;
    }

    @Override
    public List<Vehicle> search(Map<String, String> filterQuery){
        List<Vehicle> allVehicle = vehicleRepository.getAll();
        Predicate<Vehicle> predicate = Vehicle -> true;
        for (Map.Entry<String, String> entry: filterQuery.entrySet()){
            switch (entry.getKey()){
                case "make":
                    predicate = predicate.and(vehicle -> vehicle.make.contains(entry.getValue()));
                    break;
                case "model":
                    predicate = predicate.and(vehicle -> vehicle.model.contains(entry.getValue()));
                    break;
                case "year":
                    predicate = predicate.and(vehicle -> vehicle.year == Integer.parseInt(entry.getValue()));
                    break;
                case "licencePlate":
                    predicate = predicate.and(vehicle -> vehicle.licensePlate.contains(entry.getValue()));
                    break;
                case "type":
                    predicate = predicate.and(vehicle -> ((Car)vehicle).type.toString() == entry.getValue());
                    break;
                case "pricePerDay":
                    predicate = predicate.and(vehicle -> vehicle.pricePerDay == Double.parseDouble(entry.getValue()));
                    break;
            }
        }
        return allVehicle.stream().filter(predicate).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Vehicle> getAvailableVehicle(Map<String, String> filterQuery, LocalDateTime startTime, LocalDateTime endTime){
        List<Vehicle> allVehicle = new ArrayList<>(search(filterQuery));
        List<Vehicle> bookedVehicle = bookingRepository.getAll().stream()
                .filter(booking -> booking.startTime.isBefore(endTime) && booking.endTime.isAfter(startTime))
                .map(booking -> booking.vehicle)
                .toList();
        allVehicle.removeAll(bookedVehicle);
        return allVehicle;
    }

    @Override
    public boolean isAvailable(Vehicle vehicle, LocalDateTime startTime, LocalDateTime endTime){
        return bookingRepository.getAll().stream()
                .noneMatch(booking -> booking.vehicle.id.equals(vehicle.id) && booking.startTime.isBefore(endTime) && booking.endTime.isAfter(startTime));
    }

    @Override
    public double calculateTotalPrice(Vehicle vehicle, LocalDateTime startTime, LocalDateTime endTime){
        return vehicle.pricePerDay * (endTime.toLocalDate().toEpochDay() - startTime.toLocalDate().toEpochDay());
    }
}


// <--- Interface/Service --->
public interface CarRental {
    void addVehicle(Vehicle vehicle);
    List<Vehicle> search(Map<String, String> filterQuery);
    List<Vehicle> getAvailableVehicle(Map<String, String> filterQuery, LocalDateTime startTime, LocalDateTime endTime);
    Booking reserve(Vehicle vehicle, Customer customer, LocalDateTime startTime, LocalDateTime endTime);
    void cancel(Booking booking);
    Booking update(Booking booking);
}

// FACADE
@AllArgsConstructor
class CarRentalImpl implements CarRental{
    IVehicleRepository vehicleRepository;
    IBookingRepository bookingRepository;
    ISearchRepository searchRepository;
    IPaymentRepository paymentRepository;

    CarRentalImpl(){
        vehicleRepository = VehicleRepositoryImpl.getInstance();
        bookingRepository = BookingRepositoryImpl.getInstance();
        searchRepository = SearchRepositoryImpl.getInstance();
        paymentRepository = PaymentRepositoryImpl.getInstance();
    }

    @Override
    public void addVehicle(Vehicle vehicle){
        vehicleRepository.save(vehicle);
    }

    @Override
    public List<Vehicle> search(Map<String, String> filterQuery){
        return searchRepository.search(filterQuery);
    }

    @Override
    public List<Vehicle> getAvailableVehicle(Map<String, String> filterQuery, LocalDateTime startTime, LocalDateTime endTime){
        return searchRepository.getAvailableVehicle(filterQuery, startTime, endTime);
    }

    @Override
    public Booking reserve(Vehicle vehicle, Customer customer, LocalDateTime startTime, LocalDateTime endTime){
        boolean locked = vehicle.lock.tryLock();
        if (!locked){
            throw new IllegalStateException("Car is being reserved by another request. Try again.");
        }
        try{
            if(!searchRepository.isAvailable(vehicle, startTime, endTime)){
                throw new RuntimeException("Vehicle is not available");
            }
            double totalPrice = searchRepository.calculateTotalPrice(vehicle, startTime, endTime);
            // make payment

            // create booking
            Booking booking = new Booking(UUID.randomUUID().toString(), vehicle, customer, startTime, endTime, totalPrice, Booking.Status.CONFIRMED);
            bookingRepository.save(booking);
            return booking;
        } finally {
            vehicle.lock.unlock();
        }
    }

    @Override
    public void cancel(Booking booking){
        bookingRepository.delete(booking);
    }

    @Override
    public Booking update(Booking booking){
        return bookingRepository.save(booking);
    }

}

class Runner{
    public static void main(String[] args) {
        CarRentalImpl carRental = new CarRentalImpl();
        carRental.addVehicle(new Car("1", "Toyota", "Camry", 2022, "ABC123", 100, Car.Type.SUV));
        carRental.addVehicle(new Car("2", "Honda", "Civic", 2022, "DEF456", 150, Car.Type.SEDAN));
        carRental.addVehicle(new Car("3", "Ford", "Mustang", 2022, "GHI789", 200, Car.Type.SUV));

        System.out.println("All vehicles:");
        System.out.println(carRental.search(Map.of()));

        // search for all SUV
        System.out.println("All SUVs:");
        System.out.println(carRental.search(Map.of("type", "SUV")));

        // search for all available car between now and 2 days from now
        System.out.println("All available car between now and 2 days from now:");
        System.out.println(carRental.getAvailableVehicle(Map.of(), LocalDateTime.now(), LocalDateTime.now().plusDays(2)));

        // reserve a car
        System.out.println("Reserve a car:");
        Booking booking = carRental.reserve(carRental.getAvailableVehicle(Map.of(), LocalDateTime.now(), LocalDateTime.now().plusDays(2)).get(0),
                new Customer("1", "John", "Doe", "9045"),
                LocalDateTime.now(), LocalDateTime.now().plusDays(2));
        System.out.println(booking);

        // search for all available car between now and 2 days from now
        System.out.println("All available car between now and 2 days from now:");
        System.out.println(carRental.getAvailableVehicle(Map.of(), LocalDateTime.now(), LocalDateTime.now().plusDays(2)));

        // try concurrent reservations for same car to demonstrate locking
        ExecutorService exec = Executors.newFixedThreadPool(2);
        Callable<Void> task = () -> {
            try {
                carRental.reserve(carRental.getAvailableVehicle(Map.of(), LocalDateTime.now(), LocalDateTime.now().plusDays(2)).get(1),
                        new Customer("1", "John", "Doe", "9045"),
                        LocalDateTime.now(), LocalDateTime.now().plusDays(2));
            } catch (Exception e) {
                System.out.println("Concurrent attempt failed: " + e.getMessage());
            }
            return null;
        };
        try {
            exec.invokeAll(Arrays.asList(task, task));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        exec.shutdown();

        // search for all available car between now and 2 days from now
        System.out.println("All available car between now and 2 days from now:");
        System.out.println(carRental.getAvailableVehicle(Map.of(), LocalDateTime.now(), LocalDateTime.now().plusDays(2)));


        // update a booking
        System.out.println("Update a booking:");
        booking.endTime = booking.endTime.plusDays(1);
        System.out.println(carRental.update(booking));

        // cancel a booking
        System.out.println("Cancel a booking:");
        carRental.cancel(booking);

        // search for all available car between now and 2 days from now
        System.out.println("All available car between now and 2 days from now:");
        System.out.println(carRental.getAvailableVehicle(Map.of(), LocalDateTime.now(), LocalDateTime.now().plusDays(2)));

    }
}



