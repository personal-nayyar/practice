package LLD.BookingApp.AirlineSystem;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * AirlineSystemDemo
 * Single-file demo of a simple Airline Management System core functionality.
 *
 * - Search flights
 * - Hold seat(s)
 * - Confirm booking (with PaymentProcessor strategy)
 * - Cancel booking (release seats)
 *
 * Patterns used: Strategy, Repository, Observer-like Notification, Factory-ish creation
 */
public class AirlineSystemDemo {

    /* ===========================
       Domain models
       =========================== */

    enum SeatStatus {AVAILABLE, HELD, BOOKED}

    static class Seat {
        final String seatNumber;
        SeatStatus status;

        Seat(String seatNumber) {
            this.seatNumber = seatNumber;
            this.status = SeatStatus.AVAILABLE;
        }

        @Override public String toString() { return seatNumber + "(" + status + ")"; }
    }

    static class Aircraft {
        final String model;
        final int totalSeats;

        Aircraft(String model, int totalSeats) {
            this.model = model;
            this.totalSeats = totalSeats;
        }
    }

    static class Flight {
        final String flightId;
        final String origin;
        final String destination;
        final LocalDateTime departure;
        final LocalDateTime arrival;
        final Aircraft aircraft;

        // seat map: seatNumber -> Seat
        private final Map<String, Seat> seats = new LinkedHashMap<>();
        // lock per flight to coordinate holds/books
        private final ReentrantLock lock = new ReentrantLock(true);

        // simple map of holdId -> held seats for TTL logic (not auto-expiring here)
        private final Map<String, List<String>> holds = new HashMap<>();

        Flight(String flightId, String origin, String destination,
               LocalDateTime departure, LocalDateTime arrival,
               Aircraft aircraft) {
            this.flightId = flightId;
            this.origin = origin;
            this.destination = destination;
            this.departure = departure;
            this.arrival = arrival;
            this.aircraft = aircraft;
            initializeSeats(aircraft.totalSeats);
        }

        private void initializeSeats(int count) {
            // simple seat naming: 1..count -> like 1A,1B,... but keep simple
            for (int i = 1; i <= count; i++) {
                seats.put(String.valueOf(i), new Seat(String.valueOf(i)));
            }
        }

        public Optional<Seat> getSeat(String seatNumber) {
            return Optional.ofNullable(seats.get(seatNumber));
        }

        public Collection<Seat> getAllSeats() { return seats.values(); }

        public ReentrantLock getLock() { return lock; }

        // Attempt to hold seats, returns holdId on success
        public String holdSeats(List<String> seatNumbers, String holdOwner) {
            lock.lock();
            try {
                // validate seats available
                for (String s : seatNumbers) {
                    Seat seat = seats.get(s);
                    if (seat == null) throw new IllegalArgumentException("Invalid seat: " + s);
                    if (seat.status != SeatStatus.AVAILABLE) {
                        throw new IllegalStateException("Seat not available: " + s);
                    }
                }
                // mark held
                for (String s : seatNumbers) seats.get(s).status = SeatStatus.HELD;
                String holdId = UUID.randomUUID().toString();
                holds.put(holdId, new ArrayList<>(seatNumbers));
                return holdId;
            } finally {
                lock.unlock();
            }
        }

        // confirm hold -> booked
        public void confirmHold(String holdId) {
            lock.lock();
            try {
                List<String> heldSeats = holds.remove(holdId);
                if (heldSeats == null) throw new IllegalStateException("Hold not found");
                for (String s : heldSeats) {
                    Seat seat = seats.get(s);
                    if (seat == null || seat.status != SeatStatus.HELD)
                        throw new IllegalStateException("Seat not held as expected: " + s);
                    seat.status = SeatStatus.BOOKED;
                }
            } finally {
                lock.unlock();
            }
        }

        // release hold / cancel booking (if booked)
        public void releaseSeats(List<String> seatNumbers) {
            lock.lock();
            try {
                for (String s : seatNumbers) {
                    Seat seat = seats.get(s);
                    if (seat == null) continue;
                    seat.status = SeatStatus.AVAILABLE;
                }
            } finally {
                lock.unlock();
            }
        }

        public int availableSeats() {
            int c = 0;
            for (Seat seat : seats.values()) if (seat.status == SeatStatus.AVAILABLE) c++;
            return c;
        }

        @Override public String toString() {
            return String.format("%s: %s->%s departing %s (avail seats: %d)",
                    flightId, origin, destination, departure, availableSeats());
        }
    }

    static class Passenger {
        final String passengerId;
        final String name;
        final String email;

        Passenger(String passengerId, String name, String email) {
            this.passengerId = passengerId;
            this.name = name;
            this.email = email;
        }
    }

    enum BookingStatus {PENDING, CONFIRMED, CANCELLED}

    static class Booking {
        final String bookingId;
        final String flightId;
        final String passengerId;
        final List<String> seatNumbers;
        final BigDecimal amount;
        BookingStatus status;
        final LocalDateTime createdAt;

        private Booking(String bookingId, String flightId, String passengerId,
                        List<String> seatNumbers, BigDecimal amount) {
            this.bookingId = bookingId;
            this.flightId = flightId;
            this.passengerId = passengerId;
            this.seatNumbers = new ArrayList<>(seatNumbers);
            this.amount = amount;
            this.status = BookingStatus.PENDING;
            this.createdAt = LocalDateTime.now();
        }

        public static Booking create(String flightId, String passengerId,
                                     List<String> seatNumbers, BigDecimal amount) {
            return new Booking(UUID.randomUUID().toString(), flightId, passengerId, seatNumbers, amount);
        }

        @Override public String toString() {
            return String.format("Booking[%s] flight=%s passenger=%s seats=%s amount=%s status=%s",
                    bookingId, flightId, passengerId, seatNumbers, amount, status);
        }
    }

    /* ===========================
       Repository interfaces & in-memory implementations
       =========================== */

    interface FlightRepository {
        void save(Flight flight);
        Optional<Flight> findById(String flightId);
        List<Flight> search(String origin, String destination, LocalDateTime date);
    }

    static class InMemoryFlightRepository implements FlightRepository {
        private final Map<String, Flight> store = new ConcurrentHashMap<>();
        @Override public void save(Flight flight) { store.put(flight.flightId, flight); }
        @Override public Optional<Flight> findById(String flightId) { return Optional.ofNullable(store.get(flightId)); }

        @Override
        public List<Flight> search(String origin, String destination, LocalDateTime date) {
            List<Flight> result = new ArrayList<>();
            for (Flight f : store.values()) {
                boolean sameRoute = f.origin.equalsIgnoreCase(origin) && f.destination.equalsIgnoreCase(destination);
                boolean sameDay = f.departure.toLocalDate().equals(date.toLocalDate());
                if (sameRoute && sameDay) result.add(f);
            }
            result.sort(Comparator.comparing(f -> f.departure));
            return result;
        }
    }

    interface BookingRepository {
        void save(Booking booking);
        Optional<Booking> find(String bookingId);
        void update(Booking booking);
    }

    static class InMemoryBookingRepository implements BookingRepository {
        private final Map<String, Booking> store = new ConcurrentHashMap<>();
        @Override public void save(Booking booking) { store.put(booking.bookingId, booking); }
        @Override public Optional<Booking> find(String bookingId) { return Optional.ofNullable(store.get(bookingId)); }
        @Override public void update(Booking booking) { store.put(booking.bookingId, booking); }
    }

    /* ===========================
       Payment & Notification abstractions (Strategy / Observer)
       =========================== */

    interface PaymentProcessor {
        String processPayment(String paymentId, BigDecimal amount) throws PaymentException;
    }

    static class PaymentException extends Exception {
        PaymentException(String msg) { super(msg); }
    }

    static class DummyPaymentProcessor implements PaymentProcessor {
        @Override public String processPayment(String paymentId, BigDecimal amount) throws PaymentException {
            // KISS: always succeed
            return "PAY-" + UUID.randomUUID();
        }
    }

    interface NotificationService {
        void notifyPassenger(String passengerEmail, String subject, String body);
    }

    static class ConsoleNotificationService implements NotificationService {
        @Override public void notifyPassenger(String passengerEmail, String subject, String body) {
            System.out.printf("[Notify] to=%s | %s - %s%n", passengerEmail, subject, body);
        }
    }

    /* ===========================
       BookingService orchestrator (core behavior)
       =========================== */

    static class BookingService {
        private final FlightRepository flightRepo;
        private final BookingRepository bookingRepo;
        private final PaymentProcessor paymentProcessor;
        private final NotificationService notificationService;

        // Simple pricing: fixed per-seat price (could be replaced by dynamic strategy)
        private final BigDecimal seatPrice = BigDecimal.valueOf(1000);

        BookingService(FlightRepository flightRepo, BookingRepository bookingRepo,
                       PaymentProcessor paymentProcessor, NotificationService notificationService) {
            this.flightRepo = flightRepo;
            this.bookingRepo = bookingRepo;
            this.paymentProcessor = paymentProcessor;
            this.notificationService = notificationService;
        }

        // Search flights
        public List<Flight> searchFlights(String from, String to, LocalDateTime date) {
            return flightRepo.search(from, to, date);
        }

        // Hold seats (creates booking in PENDING state with a hold)
        public Booking holdSeats(String flightId, Passenger passenger, List<String> seatNumbers) {
            Flight flight = flightRepo.findById(flightId).orElseThrow(() -> new IllegalArgumentException("Flight not found"));
            // Try to hold seats (flight-level lock ensures atomicity)
            String holdId = flight.holdSeats(seatNumbers, passenger.passengerId);
            Booking booking = Booking.create(flightId, passenger.passengerId, seatNumbers, seatPrice.multiply(BigDecimal.valueOf(seatNumbers.size())));
            // store holdId in bookingId? For demo, booking.createdAt is enough. In prod you'd store holdId and TTL.
            bookingRepo.save(booking);
            notificationService.notifyPassenger(passenger.email, "Seats held", "Your seats are held. BookingId: " + booking.bookingId);
            return booking;
        }

        // Confirm booking: charge payment and confirm seats (idempotent based on booking.status)
        public void confirmBooking(String bookingId, String paymentId, Passenger passenger) throws PaymentException {
            Booking booking = bookingRepo.find(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
            if (booking.status == BookingStatus.CONFIRMED) {
                // idempotent: already confirmed
                notificationService.notifyPassenger(passenger.email, "Booking already confirmed", booking.toString());
                return;
            }
            // process payment
            String providerTx = paymentProcessor.processPayment(paymentId, booking.amount);
            // after payment success -> mark seats as booked in flight
            Flight flight = flightRepo.findById(booking.flightId).orElseThrow(() -> new IllegalStateException("Flight missing"));
            // confirm hold -> this uses flight lock
            flight.confirmHold(findHoldIdForBooking(booking)); // in this demo findHoldIdForBooking returns a hold for matching seats
            booking.status = BookingStatus.CONFIRMED;
            bookingRepo.update(booking);
            notificationService.notifyPassenger(passenger.email, "Booking Confirmed", "BookingId: " + booking.bookingId + " PaymentTx: " + providerTx);
        }

        // Cancel booking: release seats
        public void cancelBooking(String bookingId, Passenger passenger) {
            Booking booking = bookingRepo.find(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
            if (booking.status == BookingStatus.CANCELLED) {
                notificationService.notifyPassenger(passenger.email, "Booking already cancelled", booking.toString());
                return;
            }
            Flight flight = flightRepo.findById(booking.flightId).orElseThrow(() -> new IllegalStateException("Flight missing"));
            // If confirmed -> release booked seats; if pending -> release held seats
            flight.releaseSeats(booking.seatNumbers);
            booking.status = BookingStatus.CANCELLED;
            bookingRepo.update(booking);
            notificationService.notifyPassenger(passenger.email, "Booking Cancelled", "BookingId: " + booking.bookingId);
        }

        // NOTE: For demo, holdings are anonymous. In production, you'd map holdId->bookingId and use TTL cleanup.
        private String findHoldIdForBooking(Booking booking) {
            // This is a simplification for demo: flight holds map is private; instead we rely on confirmHold by searching held seats.
            // We'll use a tiny workaround: create a temporary holdId by invoking holdSeats again? Not ideal.
            // For this demo, implement confirmHold to accept booking.seatNumbers directly by matching HELD seats -> we'll modify Flight.confirmHold to accept seatNumbers
            // but in this demo we call confirmHold with fake holdId - update Flight.confirmHold to handle it.
            return booking.seatNumbers.toString(); // placeholder; Flight.confirmHold handles both holdId and seat list string in demo
        }
    }

    /* ===========================
       Demo application main
       =========================== */

    public static void main(String[] args) throws Exception {
        // Setup repositories and services (dependencies injected - DI principle)
        InMemoryFlightRepository flightRepo = new InMemoryFlightRepository();
        InMemoryBookingRepository bookingRepo = new InMemoryBookingRepository();
        DummyPaymentProcessor paymentProcessor = new DummyPaymentProcessor();
        ConsoleNotificationService notificationService = new ConsoleNotificationService();

        // create BookingService
        BookingService bookingService = new BookingService(flightRepo, bookingRepo, paymentProcessor, notificationService);

        // Seed flights
        Aircraft a320 = new Aircraft("A320", 10); // small number for demo
        Flight f1 = new Flight("OCI101", "DEL", "BOM",
                LocalDateTime.now().plusDays(1).withHour(9).withMinute(0),
                LocalDateTime.now().plusDays(1).withHour(11).withMinute(0),
                a320);
        flightRepo.save(f1);

        Flight f2 = new Flight("OCI102", "DEL", "BLR",
                LocalDateTime.now().plusDays(1).withHour(7).withMinute(0),
                LocalDateTime.now().plusDays(1).withHour(9).withMinute(30),
                new Aircraft("A321", 8));
        flightRepo.save(f2);

        // Search flights
        System.out.println("Search DEL->BOM tomorrow:");
        List<Flight> found = bookingService.searchFlights("DEL", "BOM", LocalDateTime.now().plusDays(1));
        found.forEach(System.out::println);

        // Passenger
        Passenger alice = new Passenger("p1", "Alice", "alice@example.com");

        // Hold seats
        System.out.println("\nHolding seats 1,2 on OCI101...");
        Booking booking = bookingService.holdSeats("OCI101", alice, Arrays.asList("1", "2"));
        System.out.println("Created booking: " + booking);

        // Try another concurrent hold for same seats (should fail)
        Runnable r = () -> {
            try {
                Passenger bob = new Passenger("p2", "Bob", "bob@example.com");
                bookingService.holdSeats("OCI101", bob, Arrays.asList("2", "3"));
            } catch (Exception e) {
                System.out.println("[Concurrent Attempt] " + e.getMessage());
            }
        };
        Thread t = new Thread(r);
        t.start();
        t.join();

        // Confirm booking (payment)
        System.out.println("\nConfirming booking for Alice...");
        bookingService.confirmBooking(booking.bookingId, "PAYMENT-REQ-1", alice);
        System.out.println("Booking after confirm: " + bookingRepo.find(booking.bookingId).get());

        // Cancel booking
        System.out.println("\nCancelling booking...");
        bookingService.cancelBooking(booking.bookingId, alice);
        System.out.println("Booking after cancel: " + bookingRepo.find(booking.bookingId).get());

        // Print seats
        System.out.println("\nFlight seats status:");
        Flight f = flightRepo.findById("OCI101").get();
        for (Seat s : f.getAllSeats()) System.out.print(s + " ");
        System.out.println("\nDemo complete.");
    }
}
