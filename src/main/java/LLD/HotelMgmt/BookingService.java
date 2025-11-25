package LLD.HotelMgmt;

import LLD.util.payment.*;
import LLD.util.repository.IRepository;
import lombok.Getter;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

enum RoomType {
    SINGLE(100.0), DOUBLE(200.0), DELUXE(300.0), SUITE(500.0);
    private final double price; // fields after constants
    RoomType(double price){ this.price = price; } // constructor must be private or package-private
    public double getPrice(){ return price; } // getter
}

class Room {
    private final String roomId;
    private final RoomType type;
    private volatile boolean occupied;
    private int version = 0; // for optimistic locking (if needed)
    private final ReentrantLock lock = new ReentrantLock();
    private final Set<LocalDate> bookedDates = new HashSet<>(); // ✅ availability calendar


    public Room(String roomId, RoomType type) {
        this.roomId = roomId;
        this.type = type;
        this.occupied = false;
    }

    public String getRoomId() { return roomId; }
    public RoomType getType() { return type; }

    public boolean isOccupied() { return occupied; }

    /* Locking helpers used by BookingService */
    public void lock() { lock.lock(); }
    public void unlock() { lock.unlock(); }

    public boolean tryAssign() {
        if (occupied) return false;
        occupied = true;
        version++;
        return true;
    }

    public void release() {
        occupied = false;
        version++;
    }

    public int getVersion() { return version; }

    /** ✅ Check if all dates in [from, to) are free */
    public boolean isAvailable(LocalDate from, LocalDate to) {
        lock.lock();
        try {
            LocalDate date = from;
            while (!date.isAfter(to.minusDays(1))) {
                if (bookedDates.contains(date)) {
                    return false;
                }
                date = date.plusDays(1);
            }
            return true;
        } finally {
            lock.unlock();
        }
    }
}

class Guest {
    private final long guestId;
    private String name;
    private String email;
    private String phone;
    public Guest(long guestId, String name, String email, String phone) {
        this.guestId = guestId;
        this.name = name; this.email = email; this.phone = phone;
    }
    public long getGuestId() { return guestId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    // getters/setters...
}

enum ReservationStatus {
    RESERVED, CHECKED_IN, CHECKED_OUT, CANCELLED
}

@Getter
class Reservation {
    private final String reservationId;
    private final String guestId;
    private final RoomType roomType;
    private final LocalDate from;
    private final LocalDate to;
    private final int numberOfRooms;
    private List<String> roomIds; // assigned at check-in or when slot allocated
    private ReservationStatus status;
    private final double amount;
    public Reservation(String reservationId, String guestId, RoomType roomType, int numberOfRooms,
                       LocalDate from, LocalDate to, double amount) {
        this.reservationId = reservationId; this.guestId = guestId; this.roomType = roomType; this.numberOfRooms = numberOfRooms;
        this.from = from; this.to = to; this.amount = amount;
        this.status = ReservationStatus.RESERVED;
    }
    // getters & setters...
    public String getReservationId(){return reservationId;}
    public String getGuestId(){return guestId;}
    public RoomType getRoomType(){return roomType;}
    public LocalDate getFrom(){return from;}
    public LocalDate getTo(){return to;}
    public double getAmount(){return amount;}
    public ReservationStatus getStatus(){return status;}
    public void setStatus(ReservationStatus s){ this.status = s; }
    public List<String> getRoomId(){ return roomIds; }
    public void setRoomId(List<String> roomIds){ this.roomIds = roomIds; }
}

// <--------- Repository Interface --------->
interface IUserRepository extends IRepository<Guest> {
    Guest findByEmail(String email);
}


interface RoomRepository extends IRepository<Room> {
    List<Room> findAvailableByType(RoomType type);
}

/**
 * Simple in-memory repository. Uses ConcurrentHashMap for thread-safety.
 */
class InMemoryRoomRepository implements RoomRepository {
    private final Map<String, Room> store = new ConcurrentHashMap<>();
    public InMemoryRoomRepository(Collection<Room> initial) {
        for (Room r : initial) store.put(r.getRoomId(), r);
    }
    @Override public Room findById(String roomId) { return store.get(roomId); }
    @Override public void save(Room room) { store.put(room.getRoomId(), room); }

    @Override
    public void update(Room entity) {

    }

    @Override
    public void delete(Room entity) {

    }

    @Override public List<Room> findAll(){ return new ArrayList<>(store.values()); }
    @Override
    public List<Room> findAvailableByType(RoomType type) {
        return store.values().stream()
                .filter(r -> !r.isOccupied() && r.getType() == type)
                .collect(Collectors.toList());
    }
}

interface ReservationRepository extends IRepository<Reservation> {
}

class InMemoryReservationRepository implements ReservationRepository {
    private final Map<String, Reservation> store = new ConcurrentHashMap<>();
    @Override public void save(Reservation r){ store.put(r.getReservationId(), r); }

    @Override
    public void update(Reservation entity) {

    }

    @Override
    public void delete(Reservation entity) {

    }

    @Override public Reservation findById(String id){ return store.get(id); }

    @Override
    public List<Reservation> findAll() {
        return List.of();
    }
}

interface AllotmentStrategy {
    List<String> allotRooms(Reservation r);
}

class DefaultAllotmentStrategy implements AllotmentStrategy {
    RoomRepository roomRepository;
    public DefaultAllotmentStrategy(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<String> allotRooms(Reservation r) {
        List<Room> avail = roomRepository.findAvailableByType(r.getRoomType());
        if (avail.size() < r.getNumberOfRooms()) return null;

        AtomicInteger assignedCount = new AtomicInteger(0);
        List<String> assignedRooms =  new ArrayList<>();
        for (Room room : avail) {
            // lock per room to avoid races
            room.lock();
            try {
                if (room.isOccupied()) continue;
                boolean assigned = room.tryAssign();
                if (assigned) {
                    assignedCount.getAndIncrement();
                    assignedRooms.add(room.getRoomId());
                    if (assignedCount.get() == r.getNumberOfRooms())
                        break;
                }
            } finally {
                room.unlock();
            }
        }
        System.out.println("Assigned rooms: " + assignedRooms + "for reservation :" + r.getReservationId());
        return assignedRooms;
    }
}

interface IBookingService {
    Reservation createReservation(String guestId, RoomType roomType, int numberOfRooms, LocalDate startDate, LocalDate endTime);
    Reservation findReservation(String id);
    void cancelReservation(String reservationId);
    Optional<List<String>> checkIn(String reservationId, PaymentStrategy paymentStrategy);
    boolean checkOut(String reservationId);
}

public class BookingService implements IBookingService{
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
//    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final AllotmentStrategy allotmentStrategy;
    private final AtomicLong reservationSeq = new AtomicLong(1000);


    public BookingService(RoomRepository roomRepository, ReservationRepository reservationRepository, PaymentService paymentService, AllotmentStrategy allotmentStrategy) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
//        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.allotmentStrategy = allotmentStrategy;
    }

    // Create reservation (no immediate room allocation)
    @Override
    public Reservation createReservation(String guestId, RoomType roomType, int numberOfRooms, LocalDate startDate, LocalDate endTime) {
        String resId = "RES-" + reservationSeq.getAndIncrement();
        double amount = numberOfRooms * roomType.getPrice();
        Reservation r = new Reservation(resId, guestId, roomType, numberOfRooms, startDate, endTime, amount);
        reservationRepository.save(r);
        return r;
    }

    @Override
    public Reservation findReservation(String id) {
        return reservationRepository.findById(id);
    }

    @Override
    public void cancelReservation(String reservationId) {
        Reservation r = reservationRepository.findById(reservationId);
        r.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.update(r);
    }

    @Override
    public Optional<List<String>>  checkIn(String reservationId, PaymentStrategy paymentStrategy) {
        Reservation r = reservationRepository.findById(reservationId);
        if (r == null || r.getStatus() != ReservationStatus.RESERVED) return Optional.empty();

        // allocate rooms
        r.setRoomId(allotmentStrategy.allotRooms(r));
        // make payment
        paymentService.setPaymentStrategy(paymentStrategy);
        Payment receipt = paymentService.pay(r.getAmount());
        reservationRepository.update(r);
        // mark status as checked-in
        r.setStatus(ReservationStatus.CHECKED_IN);
        return Optional.of(r.getRoomId());
    }

    @Override
    public boolean checkOut(String reservationId) {
        Reservation r = reservationRepository.findById(reservationId);
        if (r == null || r.getStatus() != ReservationStatus.CHECKED_IN) return false;

        List<Room> rooms = r.getRoomId().stream()
                .map(roomRepository::findById)
                .collect(Collectors.toList());

        // release rooms
        for (Room room : rooms) {
            room.lock();
            try {
                room.release();
            } finally {
                room.unlock();
            }
        }
        r.setStatus(ReservationStatus.CHECKED_OUT);
        reservationRepository.update(r);
        return true;
    }
}

/** Simple reporting: occupancy and room breakdown */
class ReportingService {
    private final RoomRepository roomRepo;
    public ReportingService(RoomRepository rr) { this.roomRepo = rr; }

    public Map<String, Long> occupancySummary() {
        List<Room> all = roomRepo.findAll();
        long total = all.size();
        long occupied = all.stream().filter(Room::isOccupied).count();
        return Map.of("total", total, "occupied", occupied, "available", total - occupied);
    }

    public Map<String, Long> typeSummary() {
        return roomRepo.findAll().stream()
                .collect(Collectors.groupingBy(r -> r.getType().name(), Collectors.counting()));
    }
}

class Runner{
    public static void main(String[] args) throws Exception {
        // create rooms
        List<Room> initial = Arrays.asList(
                new Room("R101", RoomType.SINGLE),
                new Room("R102", RoomType.SINGLE),
                new Room("R201", RoomType.DOUBLE),
                new Room("R301", RoomType.DELUXE),
                new Room("R401", RoomType.SUITE)
        );

        RoomRepository roomRepo = new InMemoryRoomRepository(initial);
        ReservationRepository reservationRepo = new InMemoryReservationRepository();
        PaymentService paymentService = new PaymentService();
        AllotmentStrategy allotmentStrategy = new DefaultAllotmentStrategy(roomRepo);
        BookingService bookingService = new BookingService(roomRepo, reservationRepo, paymentService, allotmentStrategy);
        ReportingService reporting = new ReportingService(roomRepo);

        // create reservations
        Reservation a = bookingService.createReservation("user1", RoomType.SINGLE,1, LocalDate.now(), LocalDate.now().plusDays(2));
        Reservation b = bookingService.createReservation("user2", RoomType.SINGLE, 1, LocalDate.now(), LocalDate.now().plusDays(1));

        // simulate concurrent check-ins for the two reservations that target SINGLE rooms (only 2 single rooms available)
        ExecutorService es = Executors.newFixedThreadPool(4);
        Callable<List<String>> c1 = () -> bookingService.checkIn(a.getReservationId(), new CreditCardPayment("4111-1111" , "Nayyar")).orElse(null);
        Callable<List<String>> c2 = () -> bookingService.checkIn(b.getReservationId(), new CashPayment()).orElse(null);

        Future<List<String>> f1 = es.submit(c1);
        Future<List<String>> f2 = es.submit(c2);

        System.out.println("Res A assigned: " + f1.get());
        System.out.println("Res B assigned: " + f2.get());

        System.out.println("Occupancy summary: " + reporting.occupancySummary());
        System.out.println("Type summary: " + reporting.typeSummary());

        // check out A
        bookingService.checkOut(a.getReservationId());
        System.out.println("After checkout A occupancy: " + reporting.occupancySummary());
        es.shutdown();
    }
}



