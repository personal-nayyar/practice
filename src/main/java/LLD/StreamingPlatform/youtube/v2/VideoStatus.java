package LLD.StreamingPlatform.youtube.v2;

import LLD.util.Notification.NotificationService;
import LLD.util.Notification.NotificationType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

enum VideoStatus { UPLOADED, ENCODING, READY, REMOVED }

class Video {
    final long id;
    final long uploaderId;
    final String title;
    final String description;
    final long uploadedAt;
    volatile VideoStatus status;
    final Set<Long> likes = ConcurrentHashMap.newKeySet();
    final Set<Long> dislikes = ConcurrentHashMap.newKeySet();
    final AtomicLong viewCount = new AtomicLong(0);
    final List<Comment> comments = Collections.synchronizedList(new ArrayList<>());
    volatile String storagePath; // returned by storage provider after upload

    Video(long id, long uploaderId, String title, String description) {
        this.id = id; this.uploaderId = uploaderId;
        this.title = title; this.description = description;
        this.uploadedAt = System.currentTimeMillis();
        this.status = VideoStatus.UPLOADED;
    }
}

class User {
    final long id;
    final String name;
    final Set<Long> subscriptions = ConcurrentHashMap.newKeySet(); // userIds this user subscribes to
    User(long id, String name) { this.id = id; this.name = name; }
}

class Comment {
    final long id;
    final long authorId;
    final String text;
    final long createdAt;
    Comment(long id, long authorId, String text) {
        this.id = id; this.authorId = authorId; this.text = text; this.createdAt = System.currentTimeMillis();
    }
}

// ---------- Storage & Encoding (Strategy + Factory) ----------
interface StorageProvider {
    // returns a storage path / id for the uploaded content
    String store(byte[] content, String filename);
}
class LocalStorageProvider implements StorageProvider {
    public String store(byte[] content, String filename){
        // stubbed: in real world, write to disk or object store and return path
        return "local://storage/" + UUID.randomUUID() + "/" + filename;
    }
}
class StorageFactory {
    public static StorageProvider getProvider(String type){
        return switch(type.toLowerCase()){
            case "local" -> new LocalStorageProvider();
            default -> new LocalStorageProvider();
        };
    }
}

interface Encoder {
    // simulate encoding and return encoded path or format
    String encode(String inputPath);
}
class SimpleEncoder implements Encoder {
    public String encode(String inputPath){
        // stub: real system would spit out multiple renditions and store them
        return inputPath + ":encoded";
    }
}

// ---------- Repositories (interfaces + in-memory impl) ----------
interface VideoRepo {
    Video save(Video v);
    Optional<Video> findById(long id);
    Collection<Video> findAll();
    List<Video> searchByTitleOrDescription(String q);
}
class InMemoryVideoRepo implements VideoRepo {
    private final Map<Long, Video> m = new ConcurrentHashMap<>();
    private static final InMemoryVideoRepo INSTANCE = new InMemoryVideoRepo();
    private InMemoryVideoRepo() {}
    public static InMemoryVideoRepo getInstance() { return INSTANCE; }
    public Video save(Video v){ m.put(v.id, v); return v; }
    public Optional<Video> findById(long id){ return Optional.ofNullable(m.get(id)); }
    public Collection<Video> findAll(){ return m.values(); }
    public List<Video> searchByTitleOrDescription(String q){
        String lower = q.toLowerCase();
        return m.values().stream()
                .filter(v -> v.title.toLowerCase().contains(lower) || v.description.toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}

interface UserRepo {
    User save(User u);
    Optional<User> findById(long id);
    Collection<User> findAll();
}
class InMemoryUserRepo implements UserRepo {
    private final Map<Long, User> m = new ConcurrentHashMap<>();
    private static final InMemoryUserRepo INSTANCE = new InMemoryUserRepo();
    private InMemoryUserRepo() {}
    public static InMemoryUserRepo getInstance(){ return INSTANCE; }
    public User save(User u){ m.put(u.id, u); return u; }
    public Optional<User> findById(long id){ return Optional.ofNullable(m.get(id)); }
    public Collection<User> findAll(){ return m.values(); }
}

interface IStreamingPlatform{
    void uploadVideo(long uploaderId, String title, String description, byte[] content);
    void likeVideo(long userId, long videoId);
    void dislikeVideo(long userId, long videoId);
    void subscribeUser(long userId, long subscriptionId);
    void unsubscribeUser(long userId, long subscriptionId);
    void commentOnVideo(long userId, long videoId, String text);
}

class StreamingPlatform{
    // repos
    private final VideoRepo videoRepo = InMemoryVideoRepo.getInstance();
    private final UserRepo userRepo = InMemoryUserRepo.getInstance();

    // helpers / strategies
    private final StorageProvider storage = StorageFactory.getProvider("local");
    private final Encoder encoder = new SimpleEncoder();

    // concurrency & ids
    private final AtomicLong videoIdGen = new AtomicLong(1);
    private final AtomicLong commentIdGen = new AtomicLong(1);
    private final AtomicLong userIdGen = new AtomicLong(1);

    // async executor for encoding and notifications
    private final ExecutorService backgroundPool = Executors.newCachedThreadPool();
    private final NotificationService notifier = NotificationService.getInstance();

    // User actions
    public User registerUser(String name) {
        long id = userIdGen.getAndIncrement();
        User u = new User(id, name);
        userRepo.save(u);
        // default listener: print notifications
//        notifier.subscribe(id, (uid, msg) -> System.out.printf("[Notify user %d] %s%n", uid, msg));
        return u;
    }

    public Video uploadVideo(long uploaderId, String title, String description, byte[] content, String filename){
        Optional<User> uploader = userRepo.findById(uploaderId);
        if(uploader.isEmpty()) throw new IllegalArgumentException("Uploader not found");

        long vid = videoIdGen.getAndIncrement();
        Video video = new Video(vid, uploaderId, title, description);
        video.status = VideoStatus.UPLOADED;
        videoRepo.save(video);

        // store raw content synchronously (could be async too)
        String path = storage.store(content, filename);
        video.storagePath = path;

        // async encoding job
        video.status = VideoStatus.ENCODING;
        backgroundPool.submit(() -> {
            try {
                // simulate encoding delay
                Thread.sleep(300);
                String encoded = encoder.encode(path);
                video.storagePath = encoded;
                video.status = VideoStatus.READY;
                // notify uploader and subscribers
                notifier.notifyAsync(NotificationType.IN_APP, "Your video is ready: " + video.title + " (id=" + video.id + ")", String.valueOf(uploaderId));
                // notify subscribers of uploader
                User u = uploader.get();
                // simple approach: notify all users who subscribed to uploader
                userRepo.findAll().forEach(user -> {
                    if(user.subscriptions.contains(uploaderId)) {
                        notifier.notifyAsync(NotificationType.IN_APP, "New video from " + uploader.get().name + ": " + video.title, String.valueOf(user.id));
                    }
                });
            } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        });
        return video;
    }


    public Optional<Video> getVideo(long videoId){
        return videoRepo.findById(videoId);
    }

    public List<Video> browseTopVideos(int limit){
        return videoRepo.findAll().stream()
                .filter(v -> v.status == VideoStatus.READY)
                .sorted(Comparator.comparingLong((Video v) -> v.viewCount.get()).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Video> search(String query){
        return videoRepo.searchByTitleOrDescription(query).stream()
                .filter(v -> v.status == VideoStatus.READY)
                .collect(Collectors.toList());
    }

    // Play/stream: increments views atomically and returns stream path (simplified)
    public Optional<String> playVideo(long videoId, long viewerId){
        Optional<Video> opt = videoRepo.findById(videoId);
        if(opt.isEmpty()) return Optional.empty();
        Video v = opt.get();
        if(v.status != VideoStatus.READY) return Optional.empty();
        long views = v.viewCount.incrementAndGet(); // atomic increment
        // note: metrics, CDN token generation, range-requests etc. are out of scope
        // notify uploader for milestones (example)
        if(views % 1000 == 0) {
            notifier.notifyAsync(NotificationType.IN_APP, "Video " + v.title + " reached " + views + " views!",   String.valueOf(v.uploaderId));
        }
        return Optional.of(v.storagePath);
    }


    // Likes / dislikes
    public void likeVideo(long videoId, long userId){
        videoRepo.findById(videoId).ifPresent(v -> {
            v.dislikes.remove(userId);
            v.likes.add(userId);
        });
    }
    public void dislikeVideo(long videoId, long userId){
        videoRepo.findById(videoId).ifPresent(v -> {
            v.likes.remove(userId);
            v.dislikes.add(userId);
        });
    }

    // Comments
    public Optional<Comment> commentOnVideo(long videoId, long userId, String text){
        Optional<Video> opt = videoRepo.findById(videoId);
        Optional<User> userOpt = userRepo.findById(userId);
        if(opt.isEmpty() || userOpt.isEmpty()) return Optional.empty();
        Video v = opt.get();
        Comment c = new Comment(commentIdGen.getAndIncrement(), userId, text);
        v.comments.add(c);
        // notify uploader
        notifier.notifyAsync(NotificationType.IN_APP,"New comment on " + v.title + " by user " + userId, String.valueOf( v.uploaderId));
        return Optional.of(c);
    }

    // Subscriptions
    public void subscribe(long subscriberId, long targetUserId){
        Optional<User> sub = userRepo.findById(subscriberId);
        Optional<User> target = userRepo.findById(targetUserId);
        if(sub.isPresent() && target.isPresent()){
            sub.get().subscriptions.add(targetUserId);
        }
    }
    public void unsubscribe(long subscriberId, long targetUserId){
        userRepo.findById(subscriberId).ifPresent(u -> u.subscriptions.remove(targetUserId));
    }

    // Recommendations (very simple):
    // 1) videos from subscribed creators (most recent)
    // 2) fallback: top videos globally
    public List<Video> recommend(long userId, int limit) {
        Optional<User> uOpt = userRepo.findById(userId);
        if(uOpt.isEmpty()) return browseTopVideos(limit);
        User u = uOpt.get();

        List<Video> fromSubscriptions = videoRepo.findAll().stream()
                .filter(v -> u.subscriptions.contains(v.uploaderId) && v.status == VideoStatus.READY)
                .sorted(Comparator.comparingLong(v -> -v.uploadedAt))
                .limit(limit)
                .collect(Collectors.toList());

        if(fromSubscriptions.size() >= limit) return fromSubscriptions;

        List<Video> top = browseTopVideos(limit);
        // merge while preserving uniqueness
        LinkedHashMap<Long, Video> map = new LinkedHashMap<>();
        fromSubscriptions.forEach(v -> map.put(v.id, v));
        top.forEach(v -> map.putIfAbsent(v.id, v));
        return new ArrayList<>(map.values()).subList(0, Math.min(limit, map.size()));
    }

    // shutdown
    public void shutdown(){
        backgroundPool.shutdown();
        notifier.shutdown();
    }
}

class Demo {
    public static void main(String[] args) throws Exception {
        StreamingPlatform platform = new StreamingPlatform();

        User alice = platform.registerUser("Alice");
        User bob = platform.registerUser("Bob");
        User creator = platform.registerUser("ChefChannel");

        platform.subscribe(alice.id, creator.id);
        platform.subscribe(bob.id, creator.id);

        byte[] dummy = "video-bytes".getBytes();
        Video v1 = platform.uploadVideo(creator.id, "How to make Pasta", "Delicious pasta recipe", dummy, "pasta.mp4");
        Video v2 = platform.uploadVideo(creator.id, "How to make Pizza", "Homemade pizza", dummy, "pizza.mp4");

        // wait for encoding to finish (demo)
        Thread.sleep(800);

        System.out.println("Search 'pasta': " + platform.search("pasta").stream().map(vid -> vid.title).collect(Collectors.toList()));

        // play some times
        for(int i=0;i<5;i++) platform.playVideo(v1.id, alice.id);

        platform.likeVideo(v1.id, alice.id);
        platform.commentOnVideo(v1.id, bob.id, "Great tutorial!");

        System.out.println("Recommendations for Alice: " + platform.recommend(alice.id, 5).stream().map(v -> v.title).collect(Collectors.toList()));

        platform.shutdown();
    }
}



