package LLD.social_media.linkedIn;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.stream.events.Comment;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// <--- Domain Layer --->
abstract class User{
    String name;
    String username;
    String password;
    Set<User> connections = new HashSet<>();
//    Set<Post> post = new HashSet<>();

    User(String name){
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User user = (User) obj;
        return name.equals(user.name);
    }

    @Override
    public String toString() {
        return name;
    }
}

class Employee extends User{
    Employee(String name){
        super(name);
    }
    String phoneNumber;
    String address;
}

class Employer extends User{
    Employer(String name){
        super(name);
    }
    String company;
}

class Profile{
    User user;
    String name;
    String bio;
    String profilePicture;
}

enum RequestStatus{
    SENT,
    ACCEPTED,
    DECLINED
}

@ToString
class ConnectionRequest{
    User from;
    User to;
    RequestStatus status;

    ConnectionRequest(User from, User to){
        this.from = from;
        this.to = to;
        this.status = RequestStatus.SENT;
    }
}

@ToString
@AllArgsConstructor
class Job{
    User poster;
    String title;
    String description;
    String location;
    Company company;
    List<User> applicants;

}

@Setter
@Getter
@AllArgsConstructor
class Message{
    String id;
    User sender;
    User receiver;
    String content;
}

@AllArgsConstructor
abstract class Notification{
    User user;
    String message;
}

class PopUpNotification extends Notification{
    PopUpNotification(User user, String message){
        super(user, message);
    }
}

class Company{
    String name;
    String location;
    String description;
    String website;
    List<Job> jobs =  new ArrayList<>();
    Set<User> followers =  new HashSet<>();

    Company(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}

class Post{
    User user;
    String content;
    List<Comment> comments;
    long likes;
}

// <--- Repository Layer --->
interface IConnectionRepository{
    void saveConnectionRequest(ConnectionRequest connectionRequest);
    void removeConnectionRequest(ConnectionRequest connectionRequest);
    List<ConnectionRequest> getConnectionRequests(User user);
    void acceptConnectionRequest(ConnectionRequest connectionRequest);
    void declineConnectionRequest(ConnectionRequest connectionRequest);
    List<User> getConnections(User user);
}

class ConnectionRepository implements IConnectionRepository{
    Map<User, List<ConnectionRequest>> connectionRequests;
    Map<User, List<User>> connectionsMap;

    ConnectionRepository(){
        connectionRequests = new HashMap<>();
        connectionsMap = new HashMap<>();
    }

    @Override
    public void saveConnectionRequest(ConnectionRequest connectionRequest) {
//        System.out.println(connectionRequests);
        connectionRequests.putIfAbsent(connectionRequest.from, new ArrayList<>());
        connectionRequests.get(connectionRequest.from).add(connectionRequest);
    }

    @Override
    public void removeConnectionRequest(ConnectionRequest connectionRequest) {
        connectionRequests.get(connectionRequest.from).remove(connectionRequest);
    }

    @Override
    public List<ConnectionRequest> getConnectionRequests(User user) {
        return connectionRequests.get(user);
    }

    @Override
    public void acceptConnectionRequest(ConnectionRequest connectionRequest) {

        connectionsMap.putIfAbsent(connectionRequest.from, new ArrayList<>());
        connectionsMap.get(connectionRequest.from).add(connectionRequest.to);

        connectionsMap.putIfAbsent(connectionRequest.to, new ArrayList<>());
        connectionsMap.get(connectionRequest.to).add(connectionRequest.from);

        // mark accepted and remove request from pending
        connectionRequest.status = RequestStatus.ACCEPTED;
        connectionRequests.get(connectionRequest.from).remove(connectionRequest);
    }

    @Override
    public void declineConnectionRequest(ConnectionRequest connectionRequest) {
        connectionRequests.get(connectionRequest.from).remove(connectionRequest);
        connectionRequest.status = RequestStatus.DECLINED;
    }

    @Override
    public List<User> getConnections(User user) {
        return connectionsMap.get(user);
    }
}

interface IUserRepository{
    void saveUser(User user);
    void removeUser(User user);
    User getUser(String username);
}

@Getter
class UserRepository implements IUserRepository{
    Map<String, User> users;

    UserRepository(){
        users = new HashMap<>();
    }

    @Override
    public void saveUser(User user) {
        users.put(user.username, user);
    }

    @Override
    public void removeUser(User user) {
        users.remove(user.username);
    }

    @Override
    public User getUser(String username) {
        return users.get(username);
    }
}

interface IProfileRepository{
    void saveProfile(Profile profile);
    void removeProfile(Profile profile);
    Profile getProfile(User user);
}

class ProfileRepository implements IProfileRepository{
    Map<User, Profile> profiles;

    ProfileRepository(){
        profiles = new HashMap<>();
    }

    @Override
    public void saveProfile(Profile profile) {
        profiles.put(profile.user, profile);
    }

    @Override
    public void removeProfile(Profile profile) {
        profiles.remove(profile.user);
    }

    @Override
    public Profile getProfile(User user) {
        return profiles.get(user);
    }
}

interface IPostRepository{
    void savePost(Post post);
    void removePost(Post post);
    List<Post> getPosts(User user);
}

class PostRepository implements IPostRepository{
    Map<User, List<Post>> posts;

    PostRepository(){
        posts = new HashMap<>();
    }

    @Override
    public void savePost(Post post) {
        posts.putIfAbsent(post.user, new ArrayList<>());
        posts.get(post.user).add(post);
    }

    @Override
    public void removePost(Post post) {
        posts.get(post.user).remove(post);
    }

    @Override
    public List<Post> getPosts(User user) {
        return posts.get(user);
    }
}

interface IJobRepository{
    void saveJob(Job job);
    void removeJob(Job job);
    List<Job> getJobs(Optional<User> user);
    List<User> getApplicants(Job job);
    void apply(User user, Job job);
}

class JobRepository implements IJobRepository{
    // implement single pattern
    private static JobRepository instance;

    public static JobRepository getInstance(){
        if(instance == null){
            instance = new JobRepository();
        }
        return instance;
    }


    Map<User, List<Job>> jobs;

    JobRepository(){
        jobs = new HashMap<>();
    }

    @Override
    public void saveJob(Job job) {
        jobs.putIfAbsent(job.poster, new ArrayList<>());
        jobs.get(job.poster).add(job);
    }

    @Override
    public void removeJob(Job job) {
        jobs.get(job.poster).remove(job);
    }

    @Override
    public List<Job> getJobs(Optional<User> user) {
        if(user.isPresent()){
            return jobs.get(user.get());
        }
        return jobs.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public List<User> getApplicants(Job job) {
        return job.applicants;
    }

    @Override
    public void apply(User user, Job job) {
        jobs.get(job.poster).stream()
                .filter(j -> j.equals(job))
                .findFirst()
                .ifPresent(j -> j.applicants.add(user));
    }
}

interface ISearchBuilder{
    List<User> searchUsers(String query);
    List<Job> searchJobs(Map<String, String> query);
}

interface IMessageRepository{
    void saveMessage(Message message);
    void deleteMessage(Message message);
    Map<User, List<Message>> getMessages(User user);
    List<Message> getMessages(User sender, User receiver);

}

class MessageRepository implements IMessageRepository{
    Map<User, Map<User, List<Message>>> messageMap;

    public MessageRepository() {
        this.messageMap = new HashMap<>();
    }

    @Override
    public void saveMessage(Message message){
        User sender = message.getSender();
        User receiver = message.getReceiver();
        messageMap.computeIfAbsent(sender, k -> new HashMap<>()).put(receiver, new ArrayList<>(){{add(message);}});
        messageMap.computeIfAbsent(receiver, k -> new HashMap<>()).put(sender, new ArrayList<>(){{add(message);}});
    }

    public void deleteMessage(Message message){
        User sender = message.getSender();
        User receiver = message.getReceiver();

        messageMap.get(sender).get(receiver).stream().filter(m -> m.equals(message)).findFirst()
                .ifPresent(m -> messageMap.get(sender).get(receiver).remove(m));

        messageMap.get(receiver).get(sender).stream().filter(m -> m.equals(message)).findFirst()
                .ifPresent(m -> messageMap.get(receiver).get(sender).remove(m));
    }

    public Map<User, List<Message>> getMessages(User user){
        // return unmodifiable list
        return messageMap.get(user);
    }

    public List<Message> getMessages(User sender, User receiver){
        // return unmodifiable list
        return messageMap.get(sender).get(receiver);
    }
}

interface INotificationRepository {
    void addNotification(User user, String message);
}

@Getter
class NotificationRepository implements INotificationRepository {
    private static NotificationRepository instance;

    private NotificationRepository() {
    }

    public static NotificationRepository getInstance() {
        if (instance == null) {
            instance = new NotificationRepository();
        }
        return instance;
    }


    Queue<Notification> notificationQueue = new LinkedList<>();

    public void addNotification(User user, String message){
        notificationQueue.add(new PopUpNotification(user, message));
    }
}

class SearchBuilder implements ISearchBuilder{
    UserRepository userRepository;
    JobRepository jobRepository;

    SearchBuilder(UserRepository userRepository, JobRepository jobRepository){
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public List<User> searchUsers(String query) {
        List<User> result = new ArrayList<>();
        for (User user : userRepository.getUsers().values()) {
            if (user.name.contains(query)) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public List<Job> searchJobs(Map<String, String> query) {
        Predicate<Job> predicate = j -> true; // lambda to implement predicate interface's test method
        for (Map.Entry<String, String> entry : query.entrySet()) {
//            System.out.println("key:"+entry.getKey());
            switch (entry.getKey()) {
                case "title":
                    predicate = predicate.and(j -> j.title.contains(entry.getValue()));
                    break;
                case "description":
                    predicate = predicate.and(j -> j.description.contains(entry.getValue()));
                    break;
                case "location":
                    predicate = predicate.and(j -> j.location.contains(entry.getValue()));
                    break;
                case "company":
                    predicate = predicate.and(j -> j.company.name.contains(entry.getValue()));
                    break;
            }
        }

        return jobRepository.getJobs(Optional.empty());
    }

    //    @Override
    public List<Job> searchJobs2(Map<String, String> query) {
        JobSearchBuilder jobSearchBuilder = new JobSearchBuilder();
        // set field like title, company etc
        jobSearchBuilder.title(query.get("title"))
                .company(query.get("company"))
                .location(query.get("location"))
                .build();


        return jobRepository.getJobs(Optional.empty()).stream()
                .filter(jobSearchBuilder)
                .collect(Collectors.toList());
    }

    static class JobSearchBuilder implements Predicate<Job>{

        Predicate<Job> predicate;

        JobSearchBuilder(){
            predicate =  Job -> true;
        }

        @Override
        public boolean test(Job job) {
            return predicate.test(job);
        }

        // search by title
        public JobSearchBuilder title(String title){
            if (title != null)
                predicate = predicate.and(job -> job.title.contains(title));
            return this;
        }

        // search by description
        public JobSearchBuilder description(String description){
            predicate = predicate.and(job -> job.description.contains(description));
            return this;
        }

        // search by location
        public JobSearchBuilder location(String location){
            predicate = predicate.and(job -> job.location.contains(location));
            return this;
        }

        // search by company
        public JobSearchBuilder company(String company){
            predicate = predicate.and(job -> job.company.name.contains(company));
            return this;
        }

        // build
        public Predicate<Job> build(){
            return predicate;
        }
    }
}

class NotificationHandler{
    INotificationRepository notificationRepository;
    ExecutorService executorService;

    NotificationHandler(INotificationRepository notificationRepository){
        this.notificationRepository =  notificationRepository;
        this.executorService = Executors.newFixedThreadPool(10);
        start();
    }

    public void handleNotification(Notification notification){
        System.out.println(notification.user + " :  " + notification.message);
    }

    // start handler
    public void start() {
        while(true){
            Notification notification = ((NotificationRepository) notificationRepository).notificationQueue.poll();
            if(notification != null){
                executorService.submit(() -> handleNotification(notification));
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

interface ILinkedIn{
    void register(User user);
    ConnectionRequest connect(User from, User to);
    void accept(ConnectionRequest connectionRequest);
    void decline(ConnectionRequest connectionRequest);
    List<User> searchUsers(String query);
    void postJob(Job job);
    List<Job> searchJobs(Map<String, String> query);
    void apply(User user, Job job);
}

// Facade
class LinkedIn implements ILinkedIn{
    IConnectionRepository connectionRepository;
    IUserRepository userRepository;
    IProfileRepository profileRepository;
    IPostRepository postRepository;
    IJobRepository jobRepository;
    ISearchBuilder searchBuilder;
    INotificationRepository notificationRepository;

    public LinkedIn(IConnectionRepository connectionRepository, IUserRepository userRepository,
                    IProfileRepository profileRepository, IPostRepository postRepository,
                    IJobRepository jobRepository, INotificationRepository notificationRepository,
                    ISearchBuilder searchBuilder) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.postRepository = postRepository;
        this.jobRepository = jobRepository;
        this.notificationRepository = notificationRepository;
        this.searchBuilder = searchBuilder;
    }

    @Override
    public void register(User user){
        userRepository.saveUser(user);
    }

    @Override
    public ConnectionRequest connect(User from, User to){
        ConnectionRequest request =  new ConnectionRequest(from, to);
        connectionRepository.saveConnectionRequest(request);
        notificationRepository.addNotification(to, "Connection Request from " + from);
        return request;
    }

    public void accept(ConnectionRequest connectionRequest){
        connectionRepository.acceptConnectionRequest(connectionRequest);
        notificationRepository.addNotification(connectionRequest.from, "Connection Request accepted by " + connectionRequest.to);
    }

    public void decline(ConnectionRequest connectionRequest){
        connectionRepository.removeConnectionRequest(connectionRequest);
        notificationRepository.addNotification(connectionRequest.from, "Connection Request declined by " + connectionRequest.to);
    }

    public List<User> searchUsers(String query){
        return searchBuilder.searchUsers(query);
    }

    public void postJob(Job job){
        jobRepository.saveJob(job);

        Set<User> notifier = new HashSet<>();
        // add all connections of the poster

        notifier.addAll(connectionRepository.getConnections(job.poster));
        // add all follower of company to notifier
        notifier.addAll(job.company.followers);

        notifier.forEach(user -> notificationRepository.addNotification(user, "New Job Posted by " + job.company));

    }

    public List<Job> searchJobs(Map<String, String> query){
        return searchBuilder.searchJobs(query);
    }

    public void apply(User user, Job job){
        jobRepository.apply(user, job);
    }
}

class Runner{
    public static void main(String[] args) {
        LinkedIn linkedIn = new LinkedIn(new ConnectionRepository(), new UserRepository(),
                new ProfileRepository(), new PostRepository(), JobRepository.getInstance(),
                NotificationRepository.getInstance(),
                new SearchBuilder(new UserRepository(), JobRepository.getInstance()));
        Executors.newSingleThreadExecutor().submit(() -> new NotificationHandler(linkedIn.notificationRepository));

        Employee employee1 = new Employee("Employee1");
        Employee employee2 = new Employee("Employee2");
        ConnectionRequest request = linkedIn.connect(employee1, employee2);

        System.out.println(linkedIn.connectionRepository.getConnectionRequests(employee1));

        linkedIn.accept(request);

        System.out.println(linkedIn.connectionRepository.getConnectionRequests(employee1));

        System.out.println(linkedIn.connectionRepository.getConnections(employee1));
        System.out.println(linkedIn.connectionRepository.getConnections(employee2));

        Employer employer = new Employer("Employer");
        ConnectionRequest request1 = linkedIn.connect(employee1, employer);
        linkedIn.accept(request1);

        // post a job
        Job job = new Job(employer, "Job", "Description", "Location", new Company("Company"), new ArrayList<>());
        linkedIn.postJob(job);
        // notify all followers

        System.out.println(linkedIn.jobRepository.getJobs(Optional.empty()));

        List<Job> jobs =  linkedIn.searchJobs(new HashMap<>(){{put("title", "Job");}});
        job = jobs.get(0);

        linkedIn.apply(employee1, job);

        System.out.println(linkedIn.jobRepository.getApplicants(job));
    }
}




