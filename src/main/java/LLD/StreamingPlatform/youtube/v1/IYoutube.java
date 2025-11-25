package LLD.StreamingPlatform.youtube.v1;

import lombok.Getter;
import lombok.ToString;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

interface Observer{
    void update(String videoId);
}

@ToString
class User implements Observer{
    String id;
    List<String> channelList;
    List<String> subscriptions;

    User(String id){
        this.id = id;
        channelList = new ArrayList<>();
        subscriptions = new ArrayList<>();
    }

    @Override
    public void update(String channelName) {
        System.out.println("Notification for "+ this.id + ", New video uploaded by " + channelName);
    }
}

interface IVotable{
    void upVote(String userId);
    void downVote(String userId);
}

@Getter
class Video implements IVotable{
    String id;
    String title;
    String description;
    String userId;
    String channel;
    String playListId;
    AtomicInteger views = new AtomicInteger(0);
    List<Comment> comments = Collections.synchronizedList(new ArrayList<>());
    Set<String> likes = ConcurrentHashMap.newKeySet();
    Set<String> dislikes = ConcurrentHashMap.newKeySet();

    Video(String id, String title, String userId, String channel){
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.channel = channel;
    }

    @Override
    public void upVote(String userId){
        likes.add(userId);
    }
    @Override
    public void downVote(String userId){
        dislikes.add(userId);
    }
}

class Playlist{
    String id;
    String name;
    String channelId;
    List<String> videos;
    Playlist(String id, String name, String channelId){
        this.id = id;
        this.name = name;
        this.channelId = channelId;
        videos = new ArrayList<>();
    }
}

class Comment implements IVotable{
    String id;
    String userId;
    String videoId;
    String comment;
    List<String> likes;
    List<String> dislike;

    @Override
    public void upVote(String userId){
        likes.add(userId);
    }
    @Override
    public void downVote(String userId){
        dislike.add(userId);
    }
}

interface Subject{
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers();
}

@Getter
class Channel implements Subject{
    String id;
    String name;
    String description;
    String userId;
    List<String> videos;
    List<User> subscriber;

    Channel(String id, String name, String userId){
        this.id = id;
        this.name = name;
        this.userId = userId;
        videos = new ArrayList<>();
        subscriber = new ArrayList<>();
    }

    @Override
    public void attach(Observer observer) {
        subscriber.add((User) observer);
    }

    @Override
    public void detach(Observer observer) {
        subscriber.remove((User) observer);
    }

    @Override
    public void notifyObservers() {
        for(User user : subscriber){
            user.update(this.name);
        }
    }
}

interface IYoutube {
    void upload(String userId, Video video);
    void subscribe(String userId, String channelId);
    void unSubscribe(String userId, String channelId);
    List<Observer> getSubscriber(String channel);
}

class Youtube implements IYoutube{
    Map<String, User> userRepo = new ConcurrentHashMap<>(){{
        put("user1", new User("user1"));
        put("user2", new User("user2"));
    }};

    Map<String, Channel> channelRepo = new ConcurrentHashMap<>(){{
        put("ch1", new Channel("ch1", "ch1", "user1"));
        put("ch2", new Channel("ch2", "ch2", "user2"));
    }};

    Map<String, Playlist> playlistRepo = new ConcurrentHashMap<>(){{
        put("pl1", new Playlist("pl1", "pl1", "ch1"));
        put("pl2", new Playlist("pl2", "pl2", "ch2"));
    }};

    Map<String, Video> videoRepo = new ConcurrentHashMap<>(){{
        put("v1", new Video("v1", "v1", "user1", "ch1"));
        put("v2", new Video("v2", "v2", "user2", "ch2"));
    }};

    // Seed data
    {
        channelRepo.get("ch1").videos.add("v1");
        channelRepo.get("ch2").videos.add("v2");

        playlistRepo.get("pl1").videos.add("v1");
        playlistRepo.get("pl2").videos.add("v2");
    }

    @Override
    public void upload(String userId, Video video) {
        videoRepo.put(video.id, video);
        if (video.getChannel() != null)
            channelRepo.get(video.channel).videos.add(video.id);
        if (video.getPlayListId() != null)
            playlistRepo.get(video.channel).videos.add(video.id);

        // notify subscriber
        channelRepo.get(video.channel).notifyObservers();
    }

    @Override
    public void subscribe(String userId, String channelId) {
        channelRepo.get(channelId).attach(userRepo.get(userId));
        userRepo.get(userId).subscriptions.add(channelId);
        System.out.println("Subscribed to " + channelId + " by " + userId);
    }

    @Override
    public void unSubscribe(String userId, String channelId) {
        channelRepo.get(channelId).detach(userRepo.get(userId));
        userRepo.get(userId).subscriptions.remove(channelId);
        System.out.println("Unsubscribed from " + channelId + " by " + userId);
    }

    @Override
    public List<Observer> getSubscriber(String channelId) {
        return new ArrayList<>(channelRepo.get(channelId).getSubscriber());
    }
}

class Runner{
    public static void main(String[] args) {
        IYoutube youtube = new Youtube();
        youtube.subscribe("user1", "ch2");
        youtube.subscribe("user2", "ch1");
        youtube.upload("user1", new Video("v3", "v3", "user1", "ch1"));
        System.out.println(youtube.getSubscriber("ch1"));

        youtube.unSubscribe("user2", "ch1");
        System.out.println(youtube.getSubscriber("ch1"));
    }
}