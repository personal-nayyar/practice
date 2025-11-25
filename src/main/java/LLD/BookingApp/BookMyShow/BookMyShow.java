package LLD.BookingApp.BookMyShow;

import LLD.util.payment.Payment;
import LLD.util.payment.PaymentStrategy;
import LLD.util.repository.IRepository;
import lombok.Builder;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

// <-----Entity ------->
class User{
    String id;
    String name;
    String address;
    List<Booking> bookings;

    User(String id){
        this.id =  id;
        this.bookings = new ArrayList<>();
    }
}

enum Genre{
    ACTION, THRILLER, ROMANCE;
}

class Movie{
    String id;
    String name;
    Genre genre;
    LocalDate releaseDate;
    Movie(String id, Genre genre){
        this.id = id;
        this.genre = genre;
    }
}

enum SeatType{
    SILVER, PREMIUM, LOUNGE
}

enum SeatStatus{
    AVAILABLE,
    REVERSED,
    BOOKED
}

class Seat{
    int seatNumber;
    SeatType type;
    SeatStatus status;
    Seat(int seatNumber){
        this.seatNumber = seatNumber;
        this.type = SeatType.PREMIUM;
        this.status = SeatStatus.AVAILABLE;
    }
}

class Show{
    String id;
    String movieId;
    List<Seat> seats;
    int totalSeats = 100;
    Show(String id, String movieId){
        this.id = id;
        this.movieId =id;
        seats = new CopyOnWriteArrayList<>();
        for (int i = 0; i < totalSeats; i++) {
            seats.add(new Seat(i));
        }
    }
}

@Builder
class Booking{
    String id;
    String userId;
    String showId;
    List<Integer> seats;
    Payment payment;
}

// <------ interface/Repository ------->
interface IUserRepository extends IRepository<User>{ }
interface IMovieRepository extends IRepository<Movie> {}
interface IShowRepository extends IRepository<Show> {}
interface IBookingRepository extends IRepository<Booking> {}

// <-------- Services --------->
interface IBookingService{
    List<Show> search(Map<String, String> queryFilter);
    Show getShowDetails(String showId);
    List<Seat> reverse(String showId, List<Seat> seatList);
    Booking confirmBooking(List<Seat> seats, PaymentStrategy paymentStrategy);
}

class CatalogService{
    Map<String, Movie> movieRepo = new HashMap<>(){{
        put("movie1", new Movie("movie1", Genre.ROMANCE));
        put("movie2", new Movie("movie2", Genre.ACTION));
        put("movie3", new Movie("movie3", Genre.THRILLER));
    }};

    Map<String, Show> showRepo = new HashMap<>(){{
        put("show1", new Show("show1","movie1"));
        put("show2", new Show("show2","movie2"));
        put("show3", new Show("show3","movie3"));
    }};

    List<Show> search(Map<String, String> queryFilter){
        return showRepo.values().stream().toList();
    }

    Show getShow(String showId){
        return showRepo.get(showId);
    }
}

class CacheService{
    Map<String, List<Seat>> reserveCache = new ConcurrentHashMap<>();
}

class BookingService{
    CacheService cacheService;
    CatalogService catalogService;
    Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();
    Map<String, Booking> bookingRepo = new ConcurrentHashMap<>();

    public ReentrantLock getLockFor(String showId){
        return locks.computeIfAbsent(showId, key -> new ReentrantLock());
    }

    BookingService(CacheService cacheService){
        this.cacheService = cacheService;
    }

    Booking reserve(String showId, List<Integer> seats, String userId){
        // check all seats availability
        Set<Integer> available= cacheService.reserveCache.get(showId).stream()
                .filter(seat -> seat.status.equals(SeatStatus.AVAILABLE))
                .map(seat -> seat.seatNumber)
                .collect(Collectors.toSet());
        for (Integer seatNumber: seats){
            if (!available.contains(seatNumber))
                throw new ConcurrentModificationException("Seat not available to reverse");
        }

        // reverse seat with locking
        ReentrantLock lock =  getLockFor(showId);
        if (lock.tryLock()){
            // reserver each seat
            try{
                for (Integer seatNumber: seats){
                    // reserve this seat
                    cacheService.reserveCache.get(showId).get(seatNumber).status = SeatStatus.REVERSED;
                }
            } catch (RuntimeException exception){
                lock.unlock();
                throw exception;
            }
        }else{
            throw new ConcurrentModificationException("Seat not available to reverse");
        }

        return Booking.builder()
                .id(UUID.randomUUID().toString())
                .showId(showId)
                .seats(seats)
                .userId(userId)
                .build();
    }

    Booking confirmBooking(String bookingId, PaymentStrategy paymentStrategy){
        Booking booking = bookingRepo.get(bookingId);

        // make payment
        paymentStrategy.pay(0.0);

        // confirm booking
        // book seat with locking
        ReentrantLock lock =  getLockFor(booking.showId);
        if (lock.tryLock()){
            // reserver each seat
            try{
                for (Integer seatNumber: booking.seats){
                    // reserve this seat
                    cacheService.reserveCache.get(booking.showId).get(seatNumber).status = SeatStatus.BOOKED;
                    catalogService.getShow(booking.showId).seats.get(seatNumber).status = SeatStatus.BOOKED;
                }
            } catch (RuntimeException exception){
                lock.unlock();
                throw exception;
            }
        }else{
            throw new ConcurrentModificationException("Seat not available to reverse");
        }
        return booking;
    }
}

