package LLD.StreamingPlatform.Netflix;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

enum ContentType { MOVIE, SERIES }
enum ContentStatus { UPLOADED, ENCODING, READY, REMOVED }

abstract class Content{
    String id;
    String title;
    String description;
    byte[] content;
    final String genre;
    final int duration;
    volatile ContentStatus status = ContentStatus.UPLOADED;
    volatile Map<IContentEncoder, String> storagePath =  new ConcurrentHashMap<>(); // encoded path

    final AtomicLong totalViews = new AtomicLong(0);
    final Map<Long, Integer> ratings = new ConcurrentHashMap<>(); // userId -> rating 1..5

    public Content(String id, String title, byte[] content, String genre, int duration, String description) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.description = description;
    }

    // Abstract method for polymorphism
    public abstract void play();
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
}

class Movie extends Content{
    public Movie(String id, String title, byte[] content, String genre, int duration, String description) {
        super(id, title, content, genre, duration, description);
    }

    @Override
    public void play() {
        System.out.println("Streaming movie: " + getTitle());
    }
}

class Series extends Content{
    final int seasons;

    public Series(String id, String title,  byte[] content, String genre, int duration, String description, int seasons) {
        super(id, title, content, genre, duration, description);
        this.seasons = seasons;
    }
    @Override
    public void play() {
        System.out.println("Streaming TV show: " + getTitle() + " (Season 1)");
    }
    // For a movie, episodes == null. For a series, episodes contains episodes.
    final List<Episode> episodes = Collections.synchronizedList(new ArrayList<>());
}

class Episode extends Content{
    final int episode;
    public Episode(String id, String title,  byte[] content, String genre, int duration, String description, int episode) {
        super(id, title, content, genre, duration, description);
        this.episode = episode;
    }

    @Override
    public void play() {
        System.out.println("Streaming TV show: " + getTitle() + " (Season 1)");
    }
}

enum SubscriptionPlan {
    FREE(0, true), STANDARD(499, true), PREMIUM(799, true);

    final int monthlyFee;
    final boolean active;
    SubscriptionPlan(int fee, boolean active){
        this.monthlyFee = fee;
        this.active = active;
    }
}

@Getter
class User {
    final String id;
    final String email;
    final String password;
    private List<Profile> profiles = new ArrayList<>();
    volatile SubscriptionPlan plan = SubscriptionPlan.FREE;

    User(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    // Methods
    void addProfile(Profile profile) { profiles.add(profile); }
    boolean authenticate(String pwd) { return password.equals(pwd); }
}

@Getter
// Profile class
class Profile {
    private String name;
    private List<String> watchHistory; // List of content IDs
    public Profile(String name) {
        this.name = name;
        this.watchHistory = new ArrayList<>();
    }
    // Methods to add to history
}

// Playback session represents a user's active streaming session (simplified)
class PlaybackSession {
    final String token;
    final long contentId;
    final Long episodeId; // optional
    final long profileId;
    final long startedAt = System.currentTimeMillis();
    PlaybackSession(String token, long contentId, Long episodeId, long profileId) {
        this.token = token; this.contentId = contentId; this.episodeId = episodeId; this.profileId = profileId;
    }
}

// Strategy + Factory
interface IContentEncoder {
    String encode(Content content); // 240p, 480p, 720p, 1080p, 4k
}

class Encoder240p implements IContentEncoder {
    @Override
    public String encode(Content content) {
        return "encoded_240p_" + content.id;
    }
}

class Encoder480p implements IContentEncoder {
    @Override
    public String encode(Content content) {
        return "encoded_480p_" + content.id;
    }
}

class Encoder720p implements IContentEncoder {
    @Override
    public String encode(Content content) {
        return "encoded_720p_" + content.id;
    }
}

class Encoder1080p implements IContentEncoder {
    @Override
    public String encode(Content content) {
        return "encoded_1080p_" + content.id;
    }
}

class Encoder4K implements IContentEncoder {
    @Override
    public String encode(Content content) {
        return "encoded_4k_" + content.id;
    }
}

interface IContentStorage {
    String store(Content content);
}

class SimpleStorage implements IContentStorage {
    List<IContentEncoder> encoders = new ArrayList<>(){{
        add(new Encoder240p());
        add(new Encoder480p());
        add(new Encoder720p());
        add(new Encoder1080p());
        add(new Encoder4K());
    }};
    ExecutorService executorService = Executors.newFixedThreadPool(encoders.size());
    @Override
    public String store(Content content) {
        for (IContentEncoder encoder : encoders) {
            executorService.submit(() -> {
                content.storagePath.put(encoder, encoder.encode(content));
                System.out.println("Content uploaded in "+encoder.getClass());
            });
        }
        return "stored_" + content.id;
    }
}

// Interfaces (Abstraction, Interface Segregation)
interface IUserService {
    User register(String email, String password);
    User login(String email, String password);
}
interface IContentService {
    Content uploadContent(String title, String desc, String genre, byte[] contentBytes, String filename);
    List<Content> search(String query);
    List<Content> getRecommendations(User user);
}
interface IStreamingService {
    void stream(Content content, User user);
}
// Implementations (Single Responsibility)
class UserService implements IUserService {
    private Map<String, User> users = new HashMap<>(); // In-memory storage

    @Override
    public User register(String email, String password) {
        if (users.containsKey(email)) throw new IllegalArgumentException("User exists");
        User user = new User(UUID.randomUUID().toString(), email, password);
        users.put(email, user);
        return user;
    }

    @Override
    public User login(String email, String password) {
        User user = users.get(email);
        if (user != null && user.authenticate(password)) return user;
        throw new IllegalArgumentException("Invalid credentials");
    }
}

class ContentService implements IContentService {
    IContentStorage contentStorage;
    private List<Content> contentLibrary = new ArrayList<>(); // Pre-populated
    public ContentService() {
        contentStorage = new SimpleStorage();
        // Add sample content
        contentLibrary.add(new Movie("1", "Inception", new byte[2], "Sci-Fi", 148, "Mind-bending thriller"));
        contentLibrary.add(new Series("2", "Breaking Bad", new byte[2], "Drama", 45, "Chemistry teacher turns meth king", 5));
    }

    @Override
    public Content uploadContent(String title, String desc, String genre, byte[] contentBytes, String filename) {
        // upload using encoder and storage provide
        Content content = new Movie(UUID.randomUUID().toString(), title, contentBytes, genre, 120*60*60, desc);
        contentStorage.store(content);
        contentLibrary.add(content);
        return content;
    }

    @Override
    public List<Content> search(String query) {
        return contentLibrary.stream()
                .filter(c -> c.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        c.getGenre().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
    @Override
    public List<Content> getRecommendations(User user) {
        // Simple: Recommend based on first profile's watch history genre
        if (user.getProfiles().isEmpty()) return new ArrayList<>();
        String genre = user.getProfiles().get(0).getWatchHistory().isEmpty() ? "Drama" :
                contentLibrary.stream()
                        .filter(c -> user.getProfiles().get(0).getWatchHistory().contains(c.getId()))
                        .findFirst().orElse(new Movie("", "", new byte[2], "Drama", 0, "")).getGenre();
        return contentLibrary.stream()
                .filter(c -> c.getGenre().equals(genre))
                .collect(Collectors.toList());
    }
}

class StreamingService implements IStreamingService {
    @Override
    public void stream(Content content, User user) {
        if (!user.getPlan().active) throw new IllegalStateException("Subscription inactive");
        content.play(); // Polymorphism
        // Add to watch history (simplified)
        if (!user.getProfiles().isEmpty()) {
            user.getProfiles().get(0).getWatchHistory().add(content.getId());
        }
    }
}

interface IStreamingPlatform {
    Content uploadContent(String title, String desc, Set<String> genres, byte[] contentBytes, String filename);    void encodeContent(Content content);
    List<Content> search(String query);
    List<Content> getRecommendations(User user);
}

public class StreamingPlatform {
    public static void main(String[] args) {
        // Dependency Injection (SOLID)
        IUserService userService = new UserService();
        IContentService contentService = new ContentService();
        IStreamingService streamingService = new StreamingService();

        // Register and login
        User user = userService.register("user@example.com", "pass123");
        user = userService.login("user@example.com", "pass123");

        // Add profile
        user.addProfile(new Profile("Main"));

        // Search and stream
        List<Content> results = contentService.search("Sci-Fi");
        if (!results.isEmpty()) {
            streamingService.stream(results.get(0), user);
        }

        // Get recommendations
        List<Content> recs = contentService.getRecommendations(user);
        System.out.println("Recommendations: " + recs.stream().map(Content::getTitle).collect(Collectors.toList()));

        // upload content
        Content content = contentService.uploadContent("Inception", "Mind-bending thriller", "Sci-Fi", new byte[2], "inception.mp4");
    }
}



