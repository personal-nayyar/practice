package LLD.CalenderService;

import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@Builder
class User{
    String id;
    String name;
    String email;
}

@AllArgsConstructor
class Slot{
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
}

@ToString
@Setter
@Getter
class Event {
    String id;
    String title;
    User owner;
    List<User> attendees;
    String description;
    String location;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    String status;


    private Event(Builder builder){
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.location = builder.location;
        this.startDateTime = builder.startDateTime;
        this.endDateTime = builder.endDateTime;
        this.status = builder.status;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private String id;
        private String title;
        private String description;
        private String location;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private String status;

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder description(String description){
            this.description = description;
            return this;
        }

        public Builder location(String location){
            this.location = location;
            return this;
        }

        public Builder startDate(LocalDateTime startDate){
            this.startDateTime = startDate;
            return this;
        }

        public Builder endDate(LocalDateTime endDate){
            this.endDateTime = endDate;
            return this;
        }

        public Builder status(String status){
            this.status = status;
            return this;
        }

        public Event build(){
            return new Event(this);
        }
    }
}

@Getter
@Builder
class QueryParam{
    User user;
    LocalDateTime startDate;
    LocalDateTime endDate;
}

interface IUserRepository{
    User findById(String id);
    User findByEmail(String email);
    User save(User user);
    void delete(User user);
    List<User> findAll();
}

// Repository pattern
interface ICalenderRepository{
    void save(Event event);
    Event findById(String id);
    List<Event> findAll();
    void delete(Event event);
    void update(Event event);
    List<Event> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<Event> findByUserEmailAndDateRange(String email, LocalDateTime startDate, LocalDateTime endDate);
    boolean isValidAvailableSlot(List<User> users, LocalDateTime startTime, LocalDateTime endTime);
    Optional<Slot> getAvailableSlots(List<User> users, LocalDateTime startTime, LocalDateTime endTime, int duration);
}

interface IQueryStrategy{
    List<Event> queryEvents(ICalenderRepository repository, QueryParam queryParam);
}

interface IEventValidator{
    boolean validate(Event event);
}

class InMemoryCalenderRepository implements ICalenderRepository{
    private Map<String, Event> events;
    private Map<String, List<Event>> userEvents; // Map<UserEmail, List<Event>>

    InMemoryCalenderRepository(){
        events = new HashMap<>();
    }

    @Override
    public void save(Event event){
        events.put(event.getId(), event);
        if (!userEvents.containsKey(event.owner.email)){
            userEvents.put(event.owner.email, new ArrayList<>());
        }
        userEvents.get(event.owner.email).add(event);
    }

    @Override
    public Event findById(String id){
        return events.get(id);
    }

    @Override
    public List<Event> findAll(){
        return new ArrayList<>(events.values());
    }

    @Override
    public void delete(Event event){
        events.remove(event.getId());
    }

    @Override
    public void update(Event event){
        events.put(event.getId(), event);
    }

    @Override
    public List<Event> findByDateRange(LocalDateTime startDate, LocalDateTime endDate){
        return events.values().stream()
                .filter(event -> event.getStartDateTime().isAfter(startDate) && event.getEndDateTime().isBefore(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByUserEmailAndDateRange(String email, LocalDateTime startDate, LocalDateTime endDate){
        return userEvents.get(email).stream().filter(event -> event.getStartDateTime().isAfter(startDate) && event.getEndDateTime().isBefore(endDate)).collect(Collectors.toList());
    }

    @Override
    public boolean isValidAvailableSlot(List<User> users, LocalDateTime startTime, LocalDateTime endTime){
        return users.stream().allMatch(user -> isSlotAvailable(user, startTime, endTime));
    }

    private boolean isSlotAvailable(User user, LocalDateTime startTime, LocalDateTime endTime){
        return findByUserEmailAndDateRange(user.email, startTime, endTime).isEmpty();
    }

    @Override
    public Optional<Slot> getAvailableSlots(List<User> users, LocalDateTime searchStartTime, LocalDateTime searchEnd, int minDuration){
        // Simple: Divide search range into 30-min intervals, check each
        LocalDateTime current = searchStartTime;
        while (current.plusMinutes(minDuration).isBefore(searchEnd)) {
            LocalDateTime slotEnd = current.plusMinutes(minDuration);
            if (isValidAvailableSlot(users, current, slotEnd)) {
                return Optional.of(new Slot(current, slotEnd));
            }
            current = current.plusMinutes(30); // Step by 30 min
        }
        return Optional.empty();
    }
}

class DateRangeQueryStrategy implements IQueryStrategy{
    @Override
    public List<Event> queryEvents(ICalenderRepository repository, QueryParam queryParam){
        return repository.findByDateRange(queryParam.getStartDate(), queryParam.getEndDate());
    }
}

class EventNameValidator implements IEventValidator{
    @Override
    public boolean validate(Event event){
        return event.getTitle().length() > 0;
    }
}

class EventDurationValidator implements IEventValidator{
    @Override
    public boolean validate(Event event){
        return event.getStartDateTime().isAfter(LocalDateTime.now().minusHours(1)) && event.getStartDateTime().isBefore(event.getEndDateTime());
    }
}

class EventCompositeValidator implements IEventValidator{
    private List<IEventValidator> validators;

    public EventCompositeValidator(List<IEventValidator> validators){
        this.validators = validators;
    }

    @Override
    public boolean validate(Event event){
        return validators.stream().allMatch(v -> v.validate(event));
    }
}

// factory design pattern
class EventValidatorFactory{
    public static IEventValidator getValidator(String type){
        switch (type){
            case "name":
                return new EventNameValidator();
            case "duration":
                return new EventDurationValidator();
            default:
                throw new RuntimeException("Invalid validator type");
        }
    }
}

interface CalenderService{
    Event createEvent(Event event);
    List<Event> queryEvents(QueryParam queryParam);
    void deleteEvent(Event event);
    Event updateEvent(Event event);
    Event getEvent(String id);
    List<Event> getAllEvents();
}

class CalenderServiceImpl implements CalenderService{
    private ICalenderRepository repository;
    private IQueryStrategy queryStrategy;
    private IEventValidator eventValidator;
    public CalenderServiceImpl(ICalenderRepository repository, IQueryStrategy queryStrategy, IEventValidator eventValidator){
        this.repository = repository;
        this.queryStrategy = queryStrategy;
        this.eventValidator = eventValidator;
    }

    @Override
    public Event createEvent(Event event){
        if (!eventValidator.validate(event)){
            throw new RuntimeException("Invalid event");
        }

        repository.save(event);
        return event;
    }

    @Override
    public List<Event> queryEvents(QueryParam queryParam){
        return queryStrategy.queryEvents(repository, queryParam);
    }

    @Override
    public void deleteEvent(Event event){
        if (!eventValidator.validate(event))
            throw new RuntimeException("Invalid Event");
        repository.delete(event);
    }

    @Override
    public Event updateEvent(Event event){
        if (!eventValidator.validate(event))
            throw new RuntimeException("Invalid Event");
        repository.update(event);
        return event;
    }

    @Override
    public Event getEvent(String id){
        return repository.findById(id);
    }

    @Override
    public List<Event> getAllEvents(){
        return repository.findAll();
    }
}

class Runner{
    public static void main(String[] args){
        CalenderServiceImpl calenderService = new CalenderServiceImpl(
                new InMemoryCalenderRepository(),
                new DateRangeQueryStrategy(),
                new EventCompositeValidator(
                        Arrays.asList(
                                EventValidatorFactory.getValidator("name"),
                                EventValidatorFactory.getValidator("duration"))
                )
        );

        calenderService.createEvent(new Event.Builder().id("1").title("Event 1").description("Event 1").location("Location 1").startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusHours(1)).status("Active").build());
        calenderService.createEvent(new Event.Builder().id("2").title("Event 2").description("Event 2").location("Location 2").startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusHours(1)).status("Active").build());
        calenderService.createEvent(new Event.Builder().id("3").title("Event 3").description("Event 3").location("Location 3").startDate(LocalDateTime.now()).endDate(LocalDateTime.now().plusHours(1)).status("Active").build());
        List<Event> events = calenderService.queryEvents(new QueryParam.QueryParamBuilder().startDate(LocalDateTime.now().minusHours(1)).endDate(LocalDateTime.now().plusHours(1)).build());
        System.out.println(events);
    }
}