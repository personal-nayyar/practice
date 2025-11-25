package LLD.HotelMgmt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import LLD.util.address.Address;
import LLD.util.payment.PaymentStrategy;
import LLD.util.repository.IRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class HotelBookingService implements IHotelBookingService{

    enum RoomType{
        SINGLE(1000.0),
        DOUBLE(2000.0),
        SUITE(3000.0);
        private final double price;
        RoomType(double price){
            this.price = price;
        }
        public double getPrice(){
            return price;
        }
    }

    @ToString
    @Setter
    @Getter
    static class Room{
        String roomId;
        RoomType type;
        String hotelId;
        boolean occupied = false;
        private int version = 0; // for optimistic locking (if needed)
        ReentrantLock lock = new ReentrantLock();
        Set<LocalDate> reserveDate  = new HashSet<>();
        Room(String roomId, RoomType roomType, String hotelId){
            this.roomId = roomId;
            this.type = roomType;
            this.hotelId = hotelId;
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
    }

    @ToString
    @Setter
    @Getter
    static abstract class RoomDecorator extends Room {
        Room room;
        public RoomDecorator(Room room){
            super(room.getRoomId(), room.getType(), room.getHotelId());
            this.room = room;
        }

        abstract void additionalService();
    }

    static class PremiumServiceRoomDecorator extends RoomDecorator{
        public PremiumServiceRoomDecorator(Room room){
            super(room);
        }

        void additionalService(){
            System.out.println("Premium Service added");
        }
    }

    static class LoundryServiceRoomDecorator extends RoomDecorator{
        public LoundryServiceRoomDecorator(Room room){
            super(room);
        }

        void additionalService(){
            System.out.println("Loundry Service added");
        }
    }


    @AllArgsConstructor
    @ToString
    @Setter
    @Getter
    static class Hotel{
        String hotelId;
        String name;
        String city;
        Address address;
        int numberOfRooms;
        int numberOfSingleRooms;
        int numberOfDoubleRooms;
        int numberOfSuiteRooms;
    }

    @ToString
    @Setter
    @Getter
    static class Booking{
        enum Status{
            PENDING,
            CONFIRMED,
            CANCELLED,
            CHECKED_IN,
            CHECKED_OUT
        }
        String bookingId;
        String hotelId;
        RoomType roomType;
        List<String> roomId;
        String userId;
        LocalDate checkInDate;
        LocalDate checkOutDate;
        Status status;
        double totalAmount;
        int numberOfDays;
        int numberOfPersons;
        int numberOfRooms;

        Booking(String bookingId, String userId, String hotelId, RoomType type, int numberOfRooms, LocalDate checkInDate, LocalDate checkOutDate){
            this.bookingId = bookingId;
            this.userId = userId;
            this.hotelId = hotelId;
            this.roomType = type;
            this.numberOfRooms = numberOfRooms;
            this.checkInDate = checkInDate;
            this.checkOutDate = checkOutDate;
            this.status = Status.CONFIRMED;
            this.totalAmount = numberOfRooms * type.getPrice();
//            this.numberOfDays = checkOutDate.toEpochDay() - checkInDate.toEpochDay();
//            this.numberOfPersons = numberOfPersons;
        }

    }

    interface IBookingRepository extends IRepository<Booking> {
    }

    interface IRoomRepository extends IRepository<Room> {
        List<Room> findAvailableByType(RoomType type);
    }

    interface IHotelRepository extends IRepository<Hotel> {
    }

    static class BookingRepository implements IBookingRepository {
        Map<String, Booking> bookingMap;
        Map<String, List<LocalDateTime>> roomDateMap;
        public BookingRepository(){
            bookingMap = new HashMap<>();
            roomDateMap = new ConcurrentHashMap<>();
        }

        @Override
        public void save(Booking entity) {
            bookingMap.put(entity.getBookingId(), entity);
        }

        @Override
        public void update(Booking booking){
            bookingMap.put(booking.getBookingId(), booking);
        }
        @Override
        public void delete(Booking booking){
            bookingMap.remove(booking.getBookingId());
        }
        @Override
        public Booking findById(String id){
            return bookingMap.get(id);
        }

        @Override
        public List<Booking> findAll() {
            return new ArrayList<>(bookingMap.values());

        }
    }

    static class RoomRepository implements IRoomRepository {
        Map<String,Room> roomMap;
        public RoomRepository(){
            roomMap = new HashMap<>();
            roomMap.put("room1", new Room("room1", RoomType.SINGLE, "hotel1"));
            roomMap.put("room2", new Room("room2", RoomType.SINGLE,"hotel1"));
            roomMap.put("room3", new Room("room3", RoomType.DOUBLE,"hotel2"));
        }

        @Override
        public void save(Room entity) {
            roomMap.put(entity.roomId, entity);
        }

        @Override
        public void update(Room entity) {
            roomMap.put(entity.roomId, entity);
        }

        @Override
        public void delete(Room entity) {
            roomMap.remove(entity.roomId);
        }

        @Override
        public Room findById(String id) {
            return roomMap.get(id);
        }

        @Override
        public List<Room> findAll() {
            return roomMap.values().stream().toList();
        }

        @Override
        public List<Room> findAvailableByType(RoomType type) {
            return roomMap.values().stream()
                    .filter(r -> !r.isOccupied() && r.getType() == type)
                    .collect(Collectors.toList());
        }
    }
    static class HotelRepository implements IHotelRepository {
        Map<String, Hotel> hotelMap;
        public HotelRepository(){
            hotelMap = new HashMap<>();
            hotelMap.put("hotel1", new Hotel("hotel1", "hotel1", "city1", null, 100, 100, 100, 100));
            hotelMap.put("hotel2", new Hotel("hotel2", "hotel2", "city2", null, 100, 100, 100, 100));
            hotelMap.put("hotel3", new Hotel("hotel3", "hotel3", "city3", null, 100, 100, 100, 100));
        }
        @Override
        public void save(Hotel hotel){
            hotelMap.put(hotel.getHotelId(), hotel);
        }
        @Override
        public void update(Hotel hotel){
            hotelMap.put(hotel.getHotelId(), hotel);
        }
        @Override
        public void delete(Hotel hotel){
            hotelMap.remove(hotel.getHotelId());
        }
        @Override
        public Hotel findById(String id){
            return hotelMap.get(id);
        }
        @Override
        public List<Hotel> findAll(){
            return new ArrayList<>(hotelMap.values());
        }
    }

    interface AllotmentStrategy {
        List<String> allotRooms(Booking r);
    }

    static class DefaultAllotmentStrategy implements AllotmentStrategy {
        RoomRepository roomRepository;
        public DefaultAllotmentStrategy(RoomRepository roomRepository) {
            this.roomRepository = roomRepository;
        }

        @Override
        public List<String> allotRooms(Booking r) {
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
            System.out.println("Assigned rooms: " + assignedRooms + "for reservation :" + r.getBookingId());
            return assignedRooms;
        }
    }


    RoomRepository roomRepository = new RoomRepository();
    HotelRepository hotelRepository = new HotelRepository();
    BookingRepository bookingRepository = new BookingRepository();
    AllotmentStrategy allotmentStrategy = new DefaultAllotmentStrategy(roomRepository);
    AtomicLong reservationSeq = new AtomicLong(100);

    HotelBookingService(){}

    // Create reservation (no immediate room allocation)
    @Override
    public Booking reserve(String userId, String hotelId, RoomType roomType, int numberOfRooms, LocalDate from, LocalDate to) {
        // check availability for room type
        List<Room> rooms = roomRepository.findAll().stream()
                .filter(room -> room.getType() == roomType && hotelId.equals(room.getHotelId()))
                .filter(room -> room.reserveDate.stream().noneMatch(date -> date.isBefore(to) && date.isAfter(from)))
                .collect(Collectors.toList());
        if(rooms.size() < numberOfRooms){
            throw new RuntimeException("Not enough rooms available");
        }

        String id =  "RES-"+reservationSeq.getAndIncrement();
        Booking b = new Booking(id, userId, hotelId, roomType, numberOfRooms, from, to);
        bookingRepository.save(b);
        return b;
    }

    @Override
    public Booking cancel(String reservationId) {
        Booking b = bookingRepository.findById(reservationId);
        b.setStatus(Booking.Status.CANCELLED);
        bookingRepository.update(b);
        return b;
    }

    @Override
    public Booking findBooking(String reservationId) {
        return bookingRepository.findById(reservationId);
    }

    @Override
    public List<String> checkIn(String reservationId, PaymentStrategy paymentStrategy) {
        Booking b = bookingRepository.findById(reservationId);
        if(b == null || b.getStatus() != Booking.Status.CONFIRMED){
            throw new RuntimeException("Invalid reservation");
        }
        // allot rooms
        List<String> rooms = allotmentStrategy.allotRooms(b);
        b.setRoomId(rooms);

        paymentStrategy.pay(b.getTotalAmount());
        b.setStatus(Booking.Status.CHECKED_IN);
        bookingRepository.update(b);
        return List.of();
    }

    @Override
    public List<String> checkOut(String reservationId) {
        Booking r = bookingRepository.findById(reservationId);
        if (r == null || r.getStatus() != Booking.Status.CHECKED_IN) return null;

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
        r.setStatus(Booking.Status.CHECKED_OUT);
        bookingRepository.update(r);
        return r.getRoomId();
    }


}


interface IHotelBookingService {
    HotelBookingService.Booking reserve(String userId, String hotelId, HotelBookingService.RoomType type, int numberOfRooms, LocalDate from, LocalDate to);
    HotelBookingService.Booking cancel(String reservationId);
    HotelBookingService.Booking findBooking(String reservationId);
    List<String> checkIn(String reservationId , PaymentStrategy paymentStrategy);
    List<String> checkOut(String reservationId);
}






