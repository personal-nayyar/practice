package LLD.social_media.facebook;

import java.util.*;

interface IFacebook{

}
public class Facebook {

}

// --- Core Classes --- //

class User {
    String name, email, password, bio;
    List<User> friends = new ArrayList<>();
    List<Post> posts = new ArrayList<>();

    User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    void updateBio(String bio) { this.bio = bio; }
    void addFriend(User user) { friends.add(user); }
    void createPost(String content) { posts.add(new Post(this, content)); }
}

class Post {
    User author;
    String content;
    Date createdAt = new Date();
    List<String> likes = new ArrayList<>();
    List<Comment> comments = new ArrayList<>();

    Post(User author, String content) {
        this.author = author;
        this.content = content;
    }

    void like(String userName) { likes.add(userName); }
    void addComment(User user, String text) { comments.add(new Comment(user, text)); }

    public String toString() {
        return author.name + ": " + content + " ❤️ " + likes.size() + " likes";
    }
}

class Comment {
    User author;
    String text;

    Comment(User author, String text) {
        this.author = author;
        this.text = text;
    }
}

class FriendRequest {
    User sender, receiver;

    FriendRequest(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    void accept() {
        sender.addFriend(receiver);
        receiver.addFriend(sender);
    }
}

class SocialNetwork {
    Map<String, User> users = new HashMap<>();

    User register(String name, String email, String password) {
        User user = new User(name, email, password);
        users.put(email, user);
        return user;
    }

    List<Post> getNewsFeed(User user) {
        List<Post> feed = new ArrayList<>();
        for (User friend : user.friends)
            feed.addAll(friend.posts);
        feed.addAll(user.posts);
        feed.sort((a, b) -> b.createdAt.compareTo(a.createdAt));
        return feed;
    }
}

// --- Demo --- //

class SocialNetworkApp {
    public static void main(String[] args) {
        SocialNetwork network = new SocialNetwork();

        User alice = network.register("Alice", "alice@mail.com", "123");
        User bob = network.register("Bob", "bob@mail.com", "abc");

        FriendRequest req = new FriendRequest(alice, bob);
        req.accept();

        alice.createPost("Hello, world!");
        bob.createPost("Good morning!");

        bob.posts.get(0).like("Alice");
        alice.posts.get(0).addComment(bob, "Nice post!");

        System.out.println("\n--- Alice's News Feed ---");
        for (Post p : network.getNewsFeed(alice))
            System.out.println(p);
    }
}