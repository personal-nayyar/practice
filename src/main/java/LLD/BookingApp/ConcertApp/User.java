package LLD.BookingApp.ConcertApp;

import java.util.*;

// --- Core Classes --- //

class User {
    String name, email;

    User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}

class Seat {
    int seatNumber;
    boolean booked;

    Seat(int seatNumber) {
        this.seatNumber = seatNumber;
    }
}

class Concert {
    String concertId, artist, venue;
    Date date;
    List<Seat> seats;

    Concert(String concertId, String artist, String venue, Date date, int totalSeats) {
        this.concertId = concertId;
        this.artist = artist;
        this.venue = venue;
        this.date = date;
        this.seats = new ArrayList<>();
        for (int i = 1; i <= totalSeats; i++)
            seats.add(new Seat(i));
    }

    List<Seat> getAvailableSeats() {
        List<Seat> available = new ArrayList<>();
        for (Seat s : seats)
            if (!s.booked) available.add(s);
        return available;
    }
}

class Booking {
    User user;
    Concert concert;
    Seat seat;
    Date bookingTime;

    Booking(User user, Concert concert, Seat seat) {
        this.user = user;
        this.concert = concert;
        this.seat = seat;
        this.bookingTime = new Date();
    }

    public String toString() {
        return "Booking Confirmed for " + user.name +
               " | Concert: " + concert.artist +
               " | Seat: " + seat.seatNumber +
               " | Date: " + concert.date;
    }
}

class Payment {
    boolean process(User user, double amount) {
        System.out.println("Processing payment of ₹" + amount + " for " + user.name);
        return true;
    }
}

// --- Singleton Service --- //

class ConcertService {
    private static ConcertService instance;
    private List<Concert> concerts = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private Map<String, Queue<User>> waitingList = new HashMap<>();

    private ConcertService() {}

    public static ConcertService getInstance() {
        if (instance == null) instance = new ConcertService();
        return instance;
    }

    void addConcert(Concert concert) {
        concerts.add(concert);
        waitingList.put(concert.concertId, new LinkedList<>());
    }

    List<Concert> search(String artist) {
        List<Concert> result = new ArrayList<>();
        for (Concert c : concerts)
            if (c.artist.equalsIgnoreCase(artist)) result.add(c);
        return result;
    }

    synchronized Booking bookTicket(User user, Concert concert, int seatNumber) {
        Seat seat = concert.seats.get(seatNumber - 1);
        if (seat.booked) {
            System.out.println("Seat " + seatNumber + " already booked. Adding " + user.name + " to waiting list.");
            waitingList.get(concert.concertId).add(user);
            return null;
        }

        seat.booked = true;
        new Payment().process(user, 1500.00);
        Booking booking = new Booking(user, concert, seat);
        bookings.add(booking);
        sendConfirmation(user, booking);
        return booking;
    }

    synchronized void cancelBooking(User user, Concert concert, int seatNumber) {
        Seat seat = concert.seats.get(seatNumber - 1);
        Optional<Booking> booking = bookings.stream()
            .filter(b -> b.user == user && b.concert == concert && b.seat == seat)
            .findFirst();

        if (booking.isPresent()) {
            bookings.remove(booking.get());
            seat.booked = false;
            System.out.println(user.name + " canceled booking for seat " + seatNumber);
            allocateFromWaitingList(concert, seat);
        } else {
            System.out.println("No booking found for " + user.name + " on seat " + seatNumber);
        }
    }

    private void allocateFromWaitingList(Concert concert, Seat seat) {
        Queue<User> queue = waitingList.get(concert.concertId);
        if (!queue.isEmpty()) {
            User nextUser = queue.poll();
            System.out.println("Allocating freed seat " + seat.seatNumber + " to " + nextUser.name + " from waiting list.");
            bookTicket(nextUser, concert, seat.seatNumber);
        }
    }

    void sendConfirmation(User user, Booking booking) {
        System.out.println("Email sent to " + user.email + ": " + booking);
    }
}

// --- Demo --- //

class ConcertApp {
    public static void main(String[] args) {
        ConcertService service = ConcertService.getInstance();

        Concert concert1 = new Concert("C1", "Arijit Singh", "Mumbai Stadium", new Date(), 2);
        service.addConcert(concert1);

        User alice = new User("Alice", "alice@mail.com");
        User bob = new User("Bob", "bob@mail.com");
        User charlie = new User("Charlie", "charlie@mail.com");

        // Booking and waiting list
        service.bookTicket(alice, concert1, 1);
        service.bookTicket(bob, concert1, 2);
        service.bookTicket(charlie, concert1, 1); // goes to waiting list

        // Cancel and auto-allocate
        System.out.println("\n--- Alice cancels her booking ---");
        service.cancelBooking(alice, concert1, 1);

        System.out.println("\nAvailable Seats: " + concert1.getAvailableSeats().size());
    }
}