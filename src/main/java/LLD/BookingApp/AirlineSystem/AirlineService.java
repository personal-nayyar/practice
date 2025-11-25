package LLD.BookingApp.AirlineSystem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 The airline management system should allow users to search for flights based on source, destination, and date.
 Users should be able to book flights, select seats, and make payments.
 The system should manage flight schedules, aircraft assignments, and crew assignments.
 The system should handle passenger information, including personal details and baggage information.
 The system should support different types of users, such as passengers, airline staff, and administrators.
 The system should be able to handle cancellations, refunds, and flight changes.
 The system should ensure data consistency and handle concurrent access to shared resources.
 The system should be scalable and extensible to accommodate future enhancements and new features.
 */

// <----- Models --------->
@Getter
@AllArgsConstructor
abstract class User{
    String userId;
    String name;
    String email;
    String phoneNumber;
}

class Passenger extends User{
    String passportNumber;
    String address;
    String gender;
    int age;

    public Passenger(String userId, String name, String email, String phoneNumber) {
        super(userId, name, email, phoneNumber);
    }
}

class CrewMember extends User{
    String crewMemberId;
    String crewMemberType;

    public CrewMember(String userId, String name, String email, String phoneNumber) {
        super(userId, name, email, phoneNumber);
    }
}

class Admin extends User{
    String adminId;
    String adminType;
    List<String> roles;

    public Admin(String userId, String name, String email, String phoneNumber) {
        super(userId, name, email, phoneNumber);
    }
}


@Getter
class Aircraft{
    enum Type{
        AIRPLANE,
        JET,
        HELICOPTER
    }
    String aircraftId;
    Type aircraftType;
    int numberOfSeats;

    Aircraft(Aircraft.Type type){
        this.aircraftType = type;
        this.aircraftId = UUID.randomUUID().toString();
        this.numberOfSeats = 30;
    }
}

@Setter
@Getter
class Seat{
    int id;
    enum Type {
        ECONOMY,
        BUSINESS,
        PREMIUM
    }
    Type type;
    boolean isAvailable;

    public Seat(int id, Type type) {
        this.id = id;
        this.type = type;
        this.isAvailable = true;
    }
}

@Setter
@Getter
class Flight{
    String flightId;
    String source;
    String destination;
    LocalDateTime departureTime;
    LocalDateTime arrivalTime;
    String aircraftId;
    List<String> crewMemberIds;
    List<String> passengerIds;
    Seat[] seats;
    ReentrantLock[] locks;

    Flight(String source, String destination, LocalDateTime departureTime, LocalDateTime arrivalTime){
        this.flightId = UUID.randomUUID().toString();
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.aircraftId = aircraftId;
        this.crewMemberIds = new ArrayList<>();
        this.passengerIds = new ArrayList<>();
    }
}

@ToString
@Setter
@Getter
class Booking{
    enum Status{
        PENDING,
        CONFIRMED,
        CANCELLED,
        REFUNDED,
        CHANGED
    }
    String bookingId;
    String flightId;
    String passengerId;
    int seatNumber;
    LocalDateTime bookingTime;
    double totalPrice;
    String paymentId;
    Status status;

    public Booking(String bookingId, String flightId, String passengerId, int seatNumber, LocalDateTime bookingTime) {
        this.bookingId = bookingId;
        this.flightId = flightId;
        this.passengerId = passengerId;
        this.seatNumber = seatNumber;
        this.bookingTime = bookingTime;
        this.status = Status.CONFIRMED;
    }
}

class Payment{
    String paymentId;
    double amount;
    PaymentStrategy paymentStrategy;
}

// <--------- Interface ->>>>>>
interface PaymentStrategy{
    void pay(double amount);
}

class UpiPayment implements PaymentStrategy{
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using UPI");
    }
}

class CreditCardPayment implements PaymentStrategy{
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using Credit Card");
    }
}

interface IUserRepository{
    User getUser(String userId);
    void addUser(User user);
    List<User> getAll();
}

class UserRepository implements IUserRepository{
    Map<String, User> userMap;
    // singleton class
    private static UserRepository instance;
    public static UserRepository getInstance(){
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }
    private UserRepository(){
        userMap = new HashMap<>();
    }
    @Override
    public User getUser(String userId) {
        return userMap.get(userId);
    }

    @Override
    public void addUser(User user) {
        userMap.put(user.userId, user);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }
}

interface IAircraftRepository{
    Aircraft addAircraft(Aircraft.Type aircraftType);
    Aircraft getAircraft(String airCraftId);
}

class AircraftRepository implements IAircraftRepository{
    Map<String, Aircraft> aircraftMap;
    // singleton class
    private static AircraftRepository instance;
    public static AircraftRepository getInstance(){
        if(instance == null){
            instance = new AircraftRepository();
        }
        return instance;
    }
    private AircraftRepository(){
        aircraftMap = new HashMap<>();
    }
    @Override
    public Aircraft getAircraft(String aircraftId) {
        return aircraftMap.get(aircraftId);
    }

    @Override
    public Aircraft addAircraft(Aircraft.Type aircraftType) {
        Aircraft aircraft = new Aircraft(aircraftType);
        aircraftMap.put(aircraft.getAircraftId(), aircraft);
        return aircraft;
    }
}

interface IFlightRepository{
    Flight getFlight(String flightId);
    void addFlight(Flight flight);
    List<Flight> searchFlight(String source, String destination, LocalDateTime startDate, LocalDateTime endDate);
}

class FlightRepository implements IFlightRepository{
    Map<String, Flight> flightMap;
    // singleton class
    private static FlightRepository instance;
    public static FlightRepository getInstance(){
        if(instance == null){
            instance = new FlightRepository();
        }
        return instance;
    }
    private FlightRepository(){
        flightMap = new HashMap<>();
    }
    @Override
    public Flight getFlight(String flightId) {
        return flightMap.get(flightId);
    }

    @Override
    public void addFlight(Flight flight) {
        flightMap.put(flight.getFlightId(), flight);
    }

    @Override
    public List<Flight> searchFlight(String source, String destination, LocalDateTime startDate, LocalDateTime endDate) {
        return flightMap.values().stream()
                .filter(flight -> flight.getSource().equals(source)
                        && flight.getDestination().equals(destination)
                        && flight.getDepartureTime().isAfter(startDate)
                        && flight.getDepartureTime().isBefore(endDate)
                ).collect(Collectors.toList());
    }
}

interface IBookingRepository{
    List<Booking> getAll(User user);
    Booking getBooking(String bookingId);
    Booking getBooking(String flightId, String userId);
    void addBooking(Booking booking);
    void updateBooking(Booking booking);
}

class BookingRepository implements IBookingRepository{
    Map<String, Booking> bookingMap;
    Map<String, List<Booking>> flightToBookingMap;
    // singleton class
    private static BookingRepository instance;
    public static BookingRepository getInstance(){
        if(instance == null){
            instance = new BookingRepository();
        }
        return instance;
    }
    private BookingRepository(){
        bookingMap = new HashMap<>();
        flightToBookingMap = new HashMap<>();
    }

    @Override
    public List<Booking> getAll(User user){
        return bookingMap.values().stream()
                .filter(booking -> booking.getPassengerId().equals(user.getUserId()))
                .collect(Collectors.toList());
    }
    @Override
    public Booking getBooking(String bookingId) {
        return bookingMap.get(bookingId);
    }

    @Override
    public Booking getBooking(String flightId, String userId) {
        return flightToBookingMap.computeIfAbsent(flightId, k -> new ArrayList<>()).stream()
                .filter(booking -> booking.passengerId.equals(userId))
                .findFirst().orElse(null);
    }

    @Override
    public void addBooking(Booking booking) {
        bookingMap.put(booking.bookingId, booking);
        flightToBookingMap.computeIfAbsent(booking.flightId, k -> new ArrayList<>()).add(booking);
    }

    @Override
    public void updateBooking(Booking booking) {
        bookingMap.put(booking.bookingId, booking);
        flightToBookingMap.get(booking.getFlightId()).removeIf(b -> b.getBookingId().equals(booking.getBookingId()));
        flightToBookingMap.get(booking.getFlightId()).add(booking);
    }
}

interface IPaymentRepository{
    void savePayment(Payment payment);
}

class PaymentRepository implements IPaymentRepository{
    Map<String, Payment> paymentMap;
    // singleton class
    private static PaymentRepository instance;
    public static PaymentRepository getInstance(){
        if(instance == null){
            instance = new PaymentRepository();
        }
        return instance;
    }
    private PaymentRepository(){
        paymentMap = new HashMap<>();
    }
    @Override
    public void savePayment(Payment payment) {

    }
}

interface IAirlineService {
    User register(User user);
    List<Flight> searchFlights(String source, String destination, LocalDateTime startDate, LocalDateTime endDate);
    Booking bookFlight(String flightId, String userId, int seatNumber);
    Booking cancelBooking(String bookingId);
    Booking changeBooking(String bookingId, String newDate, String newFlightId, int seatNumber);
    Aircraft addAircraft(Aircraft.Type aircraftType);
    Flight addFlight(String source, String destination, LocalDateTime departureTime, LocalDateTime arrivalTime);
    void addCrewMember(String crewMemberId, String crewMemberType);
    void assignCrewMember(int flightId, String crewMemberId);
    void assignAircraft(String flightId, String aircraftId);
}

class AirlineService implements IAirlineService{
    UserRepository userRepository;
    AircraftRepository aircraftRepository;
    FlightRepository flightRepository;
    BookingRepository bookingRepository;
    PaymentRepository paymentRepository;

    public AirlineService(UserRepository userRepository, AircraftRepository aircraftRepository, FlightRepository flightRepository, BookingRepository bookingRepository, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.aircraftRepository = aircraftRepository;
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public User register(User user) {
        userRepository.addUser(user);
        System.out.println("User registered successfully");
        return user;
    }

    @Override
    public List<Flight> searchFlights(String source, String destination, LocalDateTime startDate, LocalDateTime endDate) {
        return flightRepository.searchFlight(source, destination, startDate,  endDate);
    }

    @Override
    public Booking bookFlight(String flightId, String userId, int seatNumber) {
        Flight flight = flightRepository.getFlight(flightId);
        if(flight == null){
            throw new RuntimeException("Flight not found");
        }
        // check booking in booking repository
//        Booking booking = bookingRepository.getBooking(flightId, userId);
//        if(booking != null && booking.getSeatNumber() == seatNumber){
//            throw new RuntimeException("User already booked this flight");
//        }
        if(flight.seats[seatNumber].isAvailable() == false){
            throw new RuntimeException("Seat not available");
        }

        // acquire lock
        flight.locks[seatNumber].lock();
        try{
            // update seat status
            flight.seats[seatNumber].setAvailable(false);
        }catch (Exception e){
            throw new RuntimeException("Failed to book seat");
        }
        finally {
            flight.locks[seatNumber].unlock();
        }
        // create booking
        Booking booking = new Booking(UUID.randomUUID().toString(), flightId, userId, seatNumber, LocalDateTime.now());
        bookingRepository.addBooking(booking);
        return booking;
    }

    @Override
    public Booking cancelBooking(String bookingId) {
        // update the booking
        Booking booking = bookingRepository.getBooking(bookingId);
        if(booking == null){
            throw new RuntimeException("Booking not found");
        }
        booking.setStatus(Booking.Status.CANCELLED);
        bookingRepository.updateBooking(booking);

        // release seat [lock]
        Flight flight = flightRepository.getFlight(booking.getFlightId());
        flight.locks[booking.getSeatNumber()].lock();
        try{
            // update seat status
            flight.seats[booking.getSeatNumber()].setAvailable(true);
        }catch (Exception e){
            throw new RuntimeException("Failed to cancel seat");
        }
        finally {
            flight.locks[booking.getSeatNumber()].unlock();
        }
        return booking;
    }

    @Override
    public Booking changeBooking(String bookingId, String newDate, String newFlightId, int seatNumber) {
        // update previous booking
        cancelBooking(bookingId);

        // create a new booking
        return bookFlight(newFlightId, bookingRepository.getBooking(bookingId).getPassengerId(), seatNumber);
    }

    @Override
    public Aircraft addAircraft(Aircraft.Type aircraftType) {
        System.out.println("Aircraft added successfully");
        return aircraftRepository.addAircraft(aircraftType);
    }


    @Override
    public Flight addFlight(String source, String destination, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        Flight flight = new Flight(source, destination, departureTime, arrivalTime);
        flightRepository.addFlight(flight);
        System.out.println("Flight added successfully");
        return flight;
    }

    @Override
    public void addCrewMember(String crewMemberId, String crewMemberType) {
    }

    @Override
    public void assignCrewMember(int flightId, String crewMemberId) {

    }

    @Override
    public void assignAircraft(String flightId, String aircraftId) {
        Flight flight = flightRepository.getFlight(flightId);
        if(flight == null){
            throw new RuntimeException("Flight not found");
        }
        flight.setAircraftId(aircraftId);
        // fill flight seats
        flight.setSeats(new Seat[aircraftRepository.getAircraft(aircraftId).getNumberOfSeats()]);
        flight.setLocks(new ReentrantLock[aircraftRepository.getAircraft(aircraftId).getNumberOfSeats()]);
        for(int i = 0; i < aircraftRepository.getAircraft(aircraftId).getNumberOfSeats(); i++){
            flight.seats[i] = new Seat(i, Seat.Type.ECONOMY);
            flight.locks[i] = new ReentrantLock();
        }

        System.out.println("Aircraft assigned to flight successfully");
    }
}

class Runner{
    public static void main(String[] args) {
        AirlineService airlineService =  new AirlineService(
                UserRepository.getInstance(),
                AircraftRepository.getInstance(),
                FlightRepository.getInstance(),
                BookingRepository.getInstance(),
                PaymentRepository.getInstance()
        );

        // create users
        User user1 = new Passenger("user1", "user1@gmail.com", "user1", "1234");
        User user2 = new Passenger("user2", "user2@gmail.com", "user2", "1234");
        airlineService.register(user1);
        airlineService.register(user2);

        // validate user registration
        System.out.println(airlineService.userRepository.getAll().size());


        // create aircraft
        Aircraft aircraft = airlineService.addAircraft(Aircraft.Type.AIRPLANE);
        System.out.println(airlineService.aircraftRepository.aircraftMap.values());

        // create flights
        Flight flight = airlineService.addFlight("New York", "Los Angeles", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        System.out.println(airlineService.flightRepository.flightMap.values());

        // assign aircraft to flight
        airlineService.assignAircraft(flight.getFlightId(), aircraft.getAircraftId());

        // book flight
        Booking booking = airlineService.bookFlight(flight.getFlightId(), user1.getUserId(), 0);
        // validate booking
        System.out.println(airlineService.bookingRepository.getAll(user1));

        // book flight
        booking = airlineService.bookFlight(flight.getFlightId(), user1.getUserId(), 1);
        // validate booking
        System.out.println(airlineService.bookingRepository.getAll(user1));

        // concurrent Booking
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<Void> bookingCallable =  () -> {
            try{
                airlineService.bookFlight(flight.getFlightId(), user1.getUserId(), 2);
            }catch (Exception ex){
                System.out.println("Concurrent Booking Failed:"+ ex.getMessage());
            }
            return null;
        };
        Callable<Void> bookingCallable2 =  () -> {
            try{
                airlineService.bookFlight(flight.getFlightId(), user2.getUserId(), 2);
            }catch (Exception ex){
                System.out.println("Concurrent Booking Failed:"+ ex.getMessage());
            }
            return null;
        };
        try {
            executorService.invokeAll(List.of(bookingCallable, bookingCallable2));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
        System.out.println(airlineService.bookingRepository.getAll(user1));

        // cancel booking
        airlineService.cancelBooking(booking.getBookingId());
        System.out.println(airlineService.bookingRepository.getAll(user1));
    }

}










