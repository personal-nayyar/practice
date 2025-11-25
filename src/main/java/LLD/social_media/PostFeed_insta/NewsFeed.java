package LLD.social_media.PostFeed_insta;


import java.util.*;

class User {
    private final String id;
    private final String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "User{id='" + id + "', name='" + name + "'}";
    }
}

class Post {
    private final String id;
    private final String userId;
    private final String content;
    private final long timestamp;

    public Post(String id, String userId, String content) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUserId() { return userId; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "Post{id='" + id + "', user='" + userId + "', content='" + content + "'}";
    }
}


// ------------------ Repository Layer ------------------
interface UserRepository {
    void save(User user);
    Optional<User> findById(String id);
}

class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new HashMap<>();

    public void save(User user) {
        users.put(user.getId(), user);
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }
}

interface PostRepository {
    void save(Post post);
    List<Post> findByUserIds(Set<String> userIds);
}

class InMemoryPostRepository implements PostRepository {
    private final List<Post> posts = new ArrayList<>();

    public void save(Post post) {
        posts.add(post);
    }

    public List<Post> findByUserIds(Set<String> userIds) {
        List<Post> result = new ArrayList<>();
        for (Post p : posts) {
            if (userIds.contains(p.getUserId())) {
                result.add(p);
            }
        }
        return result;
    }
}

interface FollowRepository {
    void follow(String followerId, String followeeId);
    Set<String> getFollowees(String followerId);
    Set<String> getFollowers(String followeeId); // new method
}

class InMemoryFollowRepository implements FollowRepository {
    private final Map<String, Set<String>> followees = new HashMap<>();
    private final Map<String, Set<String>> followers = new HashMap<>();

    @Override
    public void follow(String followerId, String followeeId) {
        followees.computeIfAbsent(followerId, k -> new HashSet<>()).add(followeeId);
        followers.computeIfAbsent(followeeId, k -> new HashSet<>()).add(followerId);
        System.out.println(followerId + " started following " + followeeId);
    }

    @Override
    public Set<String> getFollowees(String followerId) {
        return followees.getOrDefault(followerId, new HashSet<>());
    }

    @Override
    public Set<String> getFollowers(String followeeId) {
        return followers.getOrDefault(followeeId, new HashSet<>());
    }
}

interface FeedRepository {
    void addPostToFeed(String userId, Post post);
    List<Post> getFeed(String userId);
}

class InMemoryFeedRepository implements FeedRepository {
    private final Map<String, List<Post>> feeds = new HashMap<>();

    @Override
    public void addPostToFeed(String userId, Post post) {
        feeds.computeIfAbsent(userId, k -> new ArrayList<>()).add(post);
    }

    @Override
    public List<Post> getFeed(String userId) {
        return feeds.getOrDefault(userId, new ArrayList<>());
    }
}

// ------------------ Strategy Pattern ------------------
interface FeedStrategy {
    List<Post> generateFeed(List<Post> posts);
}

class RecentPostsStrategy implements FeedStrategy {
    @Override
    public List<Post> generateFeed(List<Post> posts) {
        posts.sort((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        return posts;
    }
}

class RandomPostsStrategy implements FeedStrategy {
    @Override
    public List<Post> generateFeed(List<Post> posts) {
        Collections.shuffle(posts);
        return posts;
    }
}

// ------------------ Main NewsFeed Interface ------------------
interface NewsFeed {
    void registerUser(User user);
    void followUser(String userId, String targetUserId);
    void createPost(String userId, String content);
    List<Post> getNewsFeed(String userId);
}


class NewsFeedApp implements NewsFeed {
    private final UserRepository userRepo;
    private final PostRepository postRepo;
    private final FollowRepository followRepo;
    private final FeedRepository feedRepo;

    public NewsFeedApp(UserRepository userRepo, PostRepository postRepo,
                       FollowRepository followRepo, FeedRepository feedRepo) {
        this.userRepo = userRepo;
        this.postRepo = postRepo;
        this.followRepo = followRepo;
        this.feedRepo = feedRepo;
    }

    @Override
    public void registerUser(User user) {
        userRepo.save(user);
        System.out.println("Registered: " + user);
    }

    @Override
    public void followUser(String userId, String targetUserId) {
        if (userRepo.findById(userId).isPresent() && userRepo.findById(targetUserId).isPresent()) {
            followRepo.follow(userId, targetUserId);
        } else {
            System.out.println("Invalid follow request: user(s) not found");
        }
    }

    @Override
    public void createPost(String userId, String content) {
        if (userRepo.findById(userId).isEmpty()) {
            System.out.println("User not found: " + userId);
            return;
        }
        Post post = new Post(UUID.randomUUID().toString(), userId, content);
        postRepo.save(post);

        // Push post into own feed
        feedRepo.addPostToFeed(userId, post);

        // Push post into followers’ feeds
        Set<String> followers = followRepo.getFollowers(userId); // we’ll add this
        for (String followerId : followers) {
            feedRepo.addPostToFeed(followerId, post);
        }

        System.out.println("Post created and pushed: " + post);
    }

    @Override
    public List<Post> getNewsFeed(String userId) {
        return feedRepo.getFeed(userId);
    }
}

class Runner {
    public static void main(String[] args) {
        UserRepository userRepo = new InMemoryUserRepository();
        PostRepository postRepo = new InMemoryPostRepository();
        FollowRepository followRepo = new InMemoryFollowRepository();
        FeedRepository feedRepo = new InMemoryFeedRepository();

        NewsFeed newsFeed = new NewsFeedApp(userRepo, postRepo, followRepo, feedRepo);

        User u1 = new User("u1", "Alice");
        User u2 = new User("u2", "Bob");
        User u3 = new User("u3", "Charlie");

        newsFeed.registerUser(u1);
        newsFeed.registerUser(u2);
        newsFeed.registerUser(u3);

        newsFeed.followUser("u1", "u2"); // Alice follows Bob
        newsFeed.followUser("u1", "u3"); // Alice follows Charlie

        newsFeed.createPost("u2", "Bob’s first post!");
        newsFeed.createPost("u3", "Charlie shares something.");
        newsFeed.createPost("u1", "Alice writes a note.");

        System.out.println("\nAlice's Precomputed News Feed:");
        newsFeed.getNewsFeed("u1").forEach(System.out::println);
    }
}
