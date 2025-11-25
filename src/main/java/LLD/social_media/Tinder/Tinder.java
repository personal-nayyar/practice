package LLD.social_media.Tinder;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;


class User {
    private final String id;
    private final String username;
    private final Profile profile;

    public User(String id, String username, Profile profile) {
        this.id = id; this.username = username; this.profile = profile;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public Profile getProfile() { return profile; }

    @Override
    public String toString() {
        return "User{" + username + ", id=" + id + "}";
    }
}


class Profile {
    private final String id;
    private final String userId;
    private final int age;
    private final String bio;
    private final double latitude;   // simple geo
    private final double longitude;
    private final List<String> photos;

    public Profile(String id, String userId, int age, String bio,
                   double latitude, double longitude, List<String> photos) {
        this.id = id; this.userId = userId; this.age = age;
        this.bio = bio; this.latitude = latitude; this.longitude = longitude;
        this.photos = photos != null ? new ArrayList<>(photos) : new ArrayList<>();
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public int getAge() { return age; }
    public String getBio() { return bio; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public List<String> getPhotos() { return Collections.unmodifiableList(photos); }

    @Override
    public String toString() {
        return "Profile{" + userId + ", age=" + age + ", bio='" + bio + "'}";
    }
}


enum SwipeType {
    LIKE, // left
    DISLIKE; // right
}

class Match {
    private final String id;
    private final String userA;
    private final String userB;
    private final Instant createdAt;

    public Match(String id, String userA, String userB) {
        this.id = id; this.userA = userA; this.userB = userB; this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getUserA() { return userA; }
    public String getUserB() { return userB; }
    public Instant getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Match{" + userA + "<->" + userB + "}";
    }
}

class Message {
    private final String id;
    private final String fromUser;
    private final String toUser;
    private final String text;
    private final Instant createdAt;

    public Message(String id, String fromUser, String toUser, String text) {
        this.id = id; this.fromUser = fromUser; this.toUser = toUser; this.text = text; this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getFromUser() { return fromUser; }
    public String getToUser() { return toUser; }
    public String getText() { return text; }
    public Instant getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "[" + createdAt + "] " + fromUser + " -> " + toUser + ": " + text;
    }
}

interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    List<User> findAll();
}

interface ProfileRepository {
    Profile save(Profile profile);
    Optional<Profile> findByUserId(String userId);
    List<Profile> findAll();
}

interface SwipeRepository {
    // record a swipe (one-direction)
    void recordSwipe(String fromUserId, String toUserId, SwipeType swipe);
    Optional<SwipeType> getSwipe(String fromUserId, String toUserId);
    Map<String, SwipeType> getSwipesByUser(String fromUserId);
}

interface MatchRepository {
    Match save(Match match);
    Optional<Match> findMatchBetween(String userA, String userB);
    List<Match> findMatchesForUser(String userId);
}

interface MessageRepository {
    Message save(Message message);
    List<Message> findMessagesBetween(String userA, String userB);
}

/* ------------------------------
   Repositories - In-memory impls (thread-safe)
   ------------------------------ */

class InMemoryUserRepository implements UserRepository {
    private final ConcurrentMap<String, User> store = new ConcurrentHashMap<>();
    @Override public User save(User user) { store.put(user.getId(), user); return user; }
    @Override public Optional<User> findById(String id) { return Optional.ofNullable(store.get(id)); }
    @Override public List<User> findAll() { return new ArrayList<>(store.values()); }
}

class InMemoryProfileRepository implements ProfileRepository {
    private final ConcurrentMap<String, Profile> byUser = new ConcurrentHashMap<>();
    @Override public Profile save(Profile profile) { byUser.put(profile.getUserId(), profile); return profile; }
    @Override public Optional<Profile> findByUserId(String userId) { return Optional.ofNullable(byUser.get(userId)); }
    @Override public List<Profile> findAll() { return new ArrayList<>(byUser.values()); }
}

class InMemorySwipeRepository implements SwipeRepository {
    // nested map: fromUser -> (toUser -> SwipeType)
    private final ConcurrentMap<String, ConcurrentMap<String, SwipeType>> store = new ConcurrentHashMap<>();

    @Override
    public void recordSwipe(String fromUserId, String toUserId, SwipeType swipe) {
        store.computeIfAbsent(fromUserId, k -> new ConcurrentHashMap<>()).put(toUserId, swipe);
    }

    @Override
    public Optional<SwipeType> getSwipe(String fromUserId, String toUserId) {
        ConcurrentMap<String, SwipeType> m = store.get(fromUserId);
        return m == null ? Optional.empty() : Optional.ofNullable(m.get(toUserId));
    }

    @Override
    public Map<String, SwipeType> getSwipesByUser(String fromUserId) {
        ConcurrentMap<String, SwipeType> m = store.get(fromUserId);
        return m == null ? Collections.emptyMap() : Collections.unmodifiableMap(m);
    }
}

class InMemoryMatchRepository implements MatchRepository {
    // store matches in both directions for fast lookup
    private final ConcurrentMap<String, Set<Match>> byUser = new ConcurrentHashMap<>();

    // To avoid duplicate matches a canonical key (minId#maxId) can be used
    private final ConcurrentMap<String, Match> canonical = new ConcurrentHashMap<>();

    private String canonicalKey(String a, String b) {
        if (a.compareTo(b) <= 0) return a + "#" + b;
        return b + "#" + a;
    }

    @Override
    public Match save(Match match) {
        String key = canonicalKey(match.getUserA(), match.getUserB());
        canonical.putIfAbsent(key, match); // idempotent
        byUser.computeIfAbsent(match.getUserA(), k -> ConcurrentHashMap.newKeySet()).add(match);
        byUser.computeIfAbsent(match.getUserB(), k -> ConcurrentHashMap.newKeySet()).add(match);
        return match;
    }

    @Override
    public Optional<Match> findMatchBetween(String userA, String userB) {
        return Optional.ofNullable(canonical.get(canonicalKey(userA, userB)));
    }

    @Override
    public List<Match> findMatchesForUser(String userId) {
        Set<Match> set = byUser.get(userId);
        return set == null ? Collections.emptyList() : new ArrayList<>(set);
    }
}

class InMemoryMessageRepository implements MessageRepository {
    // store messages by canonical conversation key: a#b (sorted)
    private final ConcurrentMap<String, List<Message>> conv = new ConcurrentHashMap<>();

    private String conversationKey(String a, String b) {
        return a.compareTo(b) <= 0 ? a + "#" + b : b + "#" + a;
    }

    @Override
    public Message save(Message message) {
        String key = conversationKey(message.getFromUser(), message.getToUser());
        conv.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>())).add(message);
        return message;
    }

    @Override
    public List<Message> findMessagesBetween(String userA, String userB) {
        return conv.getOrDefault(conversationKey(userA, userB), Collections.emptyList());
    }
}

// Strategy pattern
interface RecommendationStrategy {
    /**
     * Given a requester userId and candidate profiles, return ordered candidates to show.
     */
    List<Profile> recommend(String requesterUserId, List<Profile> candidates);
}

/**
 * Simple geography-and-age based strategy:
 * - prefer nearby profiles within a distance threshold
 * - prefer age within a desired range
 */
class SimpleGeoAgeRecommendation implements RecommendationStrategy {
    private final double maxDistanceKm;
    private final int minAge, maxAge;

    public SimpleGeoAgeRecommendation(double maxDistanceKm, int minAge, int maxAge) {
        this.maxDistanceKm = maxDistanceKm; this.minAge = minAge; this.maxAge = maxAge;
    }

    @Override
    public List<Profile> recommend(String requesterUserId, List<Profile> candidates) {
        // very small scoring function: distance penalty + age penalty
        List<Scored> scored = new ArrayList<>();
        // In real system, need requester's profile data; keep simple: no requester-specific filtering
        for (Profile p : candidates) {
            double dist = 0.0; // can't compute without requester location in this method
            int agePenalty = 0;
            if (p.getAge() < minAge) agePenalty += (minAge - p.getAge());
            if (p.getAge() > maxAge) agePenalty += (p.getAge() - maxAge);
            // Score: lower is better
            double score = agePenalty + dist / 100.0;
            scored.add(new Scored(p, score));
        }
        scored.sort(Comparator.comparingDouble(s -> s.score));
        List<Profile> out = new ArrayList<>();
        for (Scored s : scored) out.add(s.profile);
        return out;
    }

    private static class Scored {
        final Profile profile; final double score;
        Scored(Profile p, double score) { this.profile = p; this.score = score; }
    }
}

/* ------------------------------
   Services (business logic) - DIP: depends on repo interfaces
   ------------------------------ */

class SwipeService {
    private final SwipeRepository swipeRepo;
    private final MatchRepository matchRepo;
    private final ProfileRepository profileRepo;
    private final AtomicInteger matchIdSeq = new AtomicInteger(1);

    public SwipeService(SwipeRepository sr, MatchRepository mr, ProfileRepository pr) {
        this.swipeRepo = sr; this.matchRepo = mr; this.profileRepo = pr;
    }

    /**
     * User A swipes on user B.
     * If A likes B and B already liked A => create a match.
     */
    public Optional<Match> swipe(String fromUserId, String toUserId, SwipeType type) {
        if (fromUserId.equals(toUserId)) return Optional.empty(); // cannot swipe yourself
        swipeRepo.recordSwipe(fromUserId, toUserId, type);

        if (type == SwipeType.LIKE) {
            // check if reverse like exists
            Optional<SwipeType> reverse = swipeRepo.getSwipe(toUserId, fromUserId);
            if (reverse.isPresent() && reverse.get() == SwipeType.LIKE) {
                // create match if not exists
                Optional<Match> existing = matchRepo.findMatchBetween(fromUserId, toUserId);
                if (existing.isPresent()) return existing;
                String matchId = "m-" + matchIdSeq.getAndIncrement();
                Match match = new Match(matchId, fromUserId, toUserId);
                matchRepo.save(match);
                return Optional.of(match);
            }
        }
        return Optional.empty();
    }
}

class RecommendationService {
    private final ProfileRepository profileRepo;
    private final SwipeRepository swipeRepo;
    private final RecommendationStrategy strategy;

    public RecommendationService(ProfileRepository pr, SwipeRepository sr, RecommendationStrategy strat) {
        this.profileRepo = pr; this.swipeRepo = sr; this.strategy = strat;
    }

    /**
     * Get next N candidate profiles for a user (filters out already-swiped).
     */
    public List<Profile> getCandidates(String userId, int limit) {
        List<Profile> all = profileRepo.findAll();
        // filter out self and already-swiped
        Map<String, SwipeType> mySwipes = swipeRepo.getSwipesByUser(userId);
        List<Profile> candidates = new ArrayList<>();
        for (Profile p : all) {
            if (p.getUserId().equals(userId)) continue;
            if (mySwipes.containsKey(p.getUserId())) continue;
            candidates.add(p);
        }
        // rank via strategy
        List<Profile> ranked = strategy.recommend(userId, candidates);
        return ranked.size() > limit ? ranked.subList(0, limit) : ranked;
    }
}

class MessagingService {
    private final MatchRepository matchRepo;
    private final MessageRepository messageRepo;

    public MessagingService(MatchRepository mr, MessageRepository msgRepo) {
        this.matchRepo = mr; this.messageRepo = msgRepo;
    }

    /**
     * Send message from sender to recipient only if they have a match.
     */
    public Optional<Message> sendMessage(String fromUserId, String toUserId, String text) {
        // ensure match exists
        if (matchRepo.findMatchBetween(fromUserId, toUserId).isEmpty()) {
            return Optional.empty();
        }
        Message m = new Message(UUID.randomUUID().toString(), fromUserId, toUserId, text);
        messageRepo.save(m);
        return Optional.of(m);
    }

    public List<Message> getConversation(String userA, String userB) {
        return messageRepo.findMessagesBetween(userA, userB);
    }
}

/* ------------------------------
   Factories (creation helpers)
   ------------------------------ */

class ProfileFactory {
    public static Profile createProfile(String userId, int age, String bio, double lat, double lon, List<String> photos) {
        return new Profile(UUID.randomUUID().toString(), userId, age, bio, lat, lon, photos);
    }
}


public interface Tinder {
    void createUser(User user);
    void createProfile(User user);
    void swipe();
    void match();
    void sendMessage();
    List<Profile> recommend(String requesterUserId, List<Profile> candidates);
}

class TinderImpl implements Tinder {
    private final UserRepository userRepo;
    private final ProfileRepository profileRepo;
    private final SwipeRepository swipeRepo;
    private final MatchRepository matchRepo;
    private final RecommendationStrategy strategy;

    public TinderImpl(UserRepository userRepository, ProfileRepository profileRepo, SwipeRepository swipeRepo, MatchRepository matchRepo, RecommendationStrategy strategy) {
        this.userRepo = userRepository;
        this.profileRepo = profileRepo;
        this.swipeRepo = swipeRepo;
        this.matchRepo = matchRepo;
        this.strategy = strategy;
    }

    @Override
    public void createUser(User user) {
        userRepo.save(user);
        profileRepo.save(user.getProfile());
    }

    @Override
    public void createProfile(User user) {
        profileRepo.save(user.getProfile());
    }

    @Override
    public void swipe() {

    }

    @Override
    public void match() {

    }

    @Override
    public void sendMessage() {

    }

    @Override
    public List<Profile> recommend(String requesterUserId, List<Profile> candidates) {
        return List.of();
    }
}

class TinderCoreDemo {
    public static void main(String[] args) {
        // repositories
        UserRepository userRepo = new InMemoryUserRepository();
        ProfileRepository profileRepo = new InMemoryProfileRepository();
        SwipeRepository swipeRepo = new InMemorySwipeRepository();
        MatchRepository matchRepo = new InMemoryMatchRepository();
        MessageRepository messageRepo = new InMemoryMessageRepository();

        // services & strategy
        RecommendationStrategy strategy = new SimpleGeoAgeRecommendation(50.0, 18, 40);
        RecommendationService recSvc = new RecommendationService(profileRepo, swipeRepo, strategy);
        SwipeService swipeSvc = new SwipeService(swipeRepo, matchRepo, profileRepo);
        MessagingService msgSvc = new MessagingService(matchRepo, messageRepo);

        // create users + profiles
        Profile p1 = ProfileFactory.createProfile("u1", 25, "Hello, I love hiking", 12.97, 77.59, Arrays.asList("p1.jpg"));
        Profile p2 = ProfileFactory.createProfile("u2", 24, "Coffee & Java", 12.98, 77.60, Arrays.asList("p2.jpg"));
        Profile p3 = ProfileFactory.createProfile("u3", 30, "Foodie", 28.6, 77.2, Arrays.asList("p3.jpg"));

        User u1 = new User("u1", "alice", p1);
        User u2 = new User("u2", "bob", p2);
        User u3 = new User("u3", "carol", p3);

        // persist
        userRepo.save(u1); userRepo.save(u2); userRepo.save(u3);
        profileRepo.save(p1); profileRepo.save(p2); profileRepo.save(p3);

        // Get candidates for u1
        System.out.println("Candidates for u1: " + recSvc.getCandidates("u1", 10));

        // u1 likes u2
        System.out.println("u1 swipes RIGHT on u2: match? " + swipeSvc.swipe("u1", "u2", SwipeType.LIKE));
        // u2 likes u1 -> should create match
        System.out.println("u2 swipes RIGHT on u1: match? " + swipeSvc.swipe("u2", "u1", SwipeType.LIKE));

        // messaging only after match
        Optional<Message> m = msgSvc.sendMessage("u1", "u2", "Hi Bob! Nice to match :)");
        System.out.println("Message sent? " + m.isPresent());
        msgSvc.getConversation("u1", "u2").forEach(System.out::println);

        // u1 dislikes u3
        System.out.println("u1 swipes LEFT on u3: " + swipeSvc.swipe("u1", "u3", SwipeType.DISLIKE));

        // print matches
        System.out.println("Matches for u1: " + matchRepo.findMatchesForUser("u1"));
    }
}