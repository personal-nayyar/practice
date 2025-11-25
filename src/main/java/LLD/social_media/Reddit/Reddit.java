package LLD.social_media.Reddit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@ToString
@Setter
@Getter
@Builder
class User{
    String id;
    String username;
    String email;
//    List<String> forumIds;
}

@ToString
@Setter
@Getter
@Builder
class Forum{
    String id;
    String title;
    List<Post> posts;
    List<User> members;
}

interface IVotable {
    void upVote();
    void downVote();
    int getScore();
}

@ToString
@Setter
@Getter
@Builder
class Post implements IVotable{
    String id;
    String title;
    String content;
    Forum forum;
    User author;
    int upVotes;
    int downVotes;
    int score;
    List<Comment> comments;

    @Override
    public void upVote() {
        upVotes++;
        score++;
    }

    @Override
    public void downVote() {
        downVotes++;
        score--;
    }

    @Override
    public int getScore() {
        return score;
    }
}

@ToString
@Setter
@Getter
@Builder
class Comment implements IVotable{
    String id;
    String content;
    User author;
    int upVotes;
    int downVotes;
    int score;
    String parentPostId;

    @Override
    public void upVote() {
        upVotes++;
        score++;
    }

    @Override
    public void downVote() {
        downVotes++;
        score--;
    }

    @Override
    public int getScore() {
        return score;
    }
}

interface IForumRepository{
    void createForum(String forumName);
    void joinForum(String forumName, String userId);
}

interface IPostRepository{
    void createPost(String forumName, String postTitle, String postContent);
    void upVotePost(String forumName, String postTitle);
    void downVotePost(String forumName, String postTitle);
    void getPost(String forumName, String postTitle);
    void getTopPosts(String forumName, int limit);
    void getPostsByUser(String forumName, String userId);
    void getPostsByForum(String forumName);
}

interface ICommentRepository{
    void createComment(String forumName, String postTitle, String commentContent);
    void upVoteComment(String forumName, String postTitle, String commentContent);
    void downVoteComment(String forumName, String postTitle, String commentContent);
    void getComment(String forumName, String postTitle, String commentContent);
    void getCommentsByPost(String forumName, String postTitle);
    void getCommentsByUser(String forumName, String userId);
}

interface IUserRepository{
    void createUser(String username, String email);
    void joinForum(String forumName, String userId);
    void leaveForum(String forumName, String userId);
}

// Strategy Pattern
interface IScoreComparator extends Comparator<Post>{
    int compare(Post post1, Post post2);
}

class ScoreComparator implements IScoreComparator{
    @Override
    public int compare(Post post1, Post post2) {
        return Integer.compare(post2.getScore(), post1.getScore());
    }
}

interface Reddit {
    void createForum(String forumName);
    void joinForum(String forumName, User user);
    void createPost(String forumName, String postTitle, String postContent);
    void createComment(String postTitle, String commentContent);
    void upVote(IVotable votable);
    void downVote(IVotable votable);
    void displayForum(String forumName);
    List<Post> getTopPosts(String forumName, int limit);
}

class RedditImpl implements Reddit{
    IForumRepository forumRepository;
    IPostRepository postRepository;
    ICommentRepository commentRepository;
    IUserRepository userRepository;

    public RedditImpl(IForumRepository forumRepository, IPostRepository postRepository, ICommentRepository commentRepository, IUserRepository userRepository) {
        this.forumRepository = forumRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    RedditImpl(){}

    // In-Memory Data Store
    Map<String, Forum> forumMap = new HashMap<>(); // forumName -> Forum
    Map<String, Post> postMap = new HashMap<>(); // postId -> Post
    Map<String, Comment> commentMap = new HashMap<>(); // commentId -> Comment
    Map<String, User> userMap = new HashMap<>(); // userId -> User

    Map<User, List<Post>> userPostMap = new HashMap<>(); // userId -> List<Post>
    Map<User, List<Comment>> userCommentMap = new HashMap<>(); // userId -> List<Comment>

    Map<Forum, List<Post>> forumPostMap = new HashMap<>(); // forumName -> List<Post>
    Map<Forum, List<User>> forumUserMap = new HashMap<>(); // forumName -> List<User>

//    {
//        Map<String, Forum> forumMap = new HashMap<>(); // forumName -> Forum
//        Map<String, Post> postMap = new HashMap<>(); // postId -> Post
//        Map<String, Comment> commentMap = new HashMap<>(); // commentId -> Comment
//        Map<String, User> userMap = new HashMap<>(); // userId -> User
//
//        Map<User, List<Post>> userPostMap = new HashMap<>(); // userId -> List<Post>
//        Map<User, List<Comment>> userCommentMap = new HashMap<>(); // userId -> List<Comment>
//
//        Map<Forum, List<Post>> forumPostMap = new HashMap<>(); // forumName -> List<Post>
//        Map<Forum, List<User>> forumUserMap = new HashMap<>(); // forumName -> List<User>
//    }

    @Override
    public void createForum(String forumName) {
        Forum forum = Forum.builder()
                .id(UUID.randomUUID().toString())
                .title(forumName)
                .members(new ArrayList<>())
                .posts(new ArrayList<>())
                .build();
        forumMap.put(forumName, forum);
    }

    @Override
    public void joinForum(String forumName, User user) {
        Forum forum = forumMap.get(forumName);
        forum.getMembers().add(user);
    }

    @Override
    public void createPost(String forumName, String postTitle, String postContent) {
        Forum forum = forumMap.get(forumName);
        Post post = Post.builder().id(UUID.randomUUID().toString()).title(postTitle).content(postContent).forum(forum)
                .comments(new ArrayList<>())
                .build();
        postMap.put(postTitle, post);

        forumPostMap.computeIfAbsent(forum, k -> new ArrayList<>()).add(post);
        userPostMap.computeIfAbsent(post.getAuthor(), k -> new ArrayList<>()).add(post);
    }

    @Override
    public void createComment(String postTitle, String commentContent) {
        Post post = postMap.get(postTitle);
        Comment comment = Comment.builder().id(UUID.randomUUID().toString())
                .content(commentContent).parentPostId(post.getTitle()).build();
        commentMap.put(commentContent, comment);

        post.getComments().add(comment);
        postMap.put(post.getTitle(), post);

        userCommentMap.computeIfAbsent(post.getAuthor(), k -> new ArrayList<>()).add(comment);
    }

    @Override
    public void upVote(IVotable votable) {
        // validation checks
        if(votable instanceof Post){
            Post post = (Post) votable;
            post.upVote();
            postMap.put(post.getTitle(), post);
        }

        if (votable instanceof Comment){
            Comment comment = (Comment) votable;
            comment.upVote();
            commentMap.put(comment.getContent(), comment);
        }
    }

    @Override
    public void downVote(IVotable votable) {
        // validation checks
        // validation checks
        if(votable instanceof Post){
            Post post = (Post) votable;
            post.downVote();
            postMap.put(post.getTitle(), post);
        }

        if (votable instanceof Comment){
            Comment comment = (Comment) votable;
            comment.downVote();
            commentMap.put(comment.getContent(), comment);
        }
    }

    @Override
    public void displayForum(String forumName) {
        Forum forum = forumMap.get(forumName);
        if(forum != null){
            for(Post post : forumPostMap.get(forum)){
                System.out.println(post.toString());
            }
        }
    }

    @Override
    public List<Post> getTopPosts(String forumName, int limit) {
        Forum forum = forumMap.get(forumName);
        if(forum != null){
            List<Post> posts = forumPostMap.get(forum);
            posts.sort(new ScoreComparator());

            // return top limit posts
            return posts.subList(0, limit);
        }
        return null;
    }
}

class Runner{
    static ObjectMapper objectMapper;
    static {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    public static void main(String[] args) throws JsonProcessingException {
        RedditImpl red = new RedditImpl();
        User u1 = new User("u1", "u1", "u1");
        User u2 = new User("u2", "u2", "u2");
        User u3 = new User("u3", "u3", "u3");

        red.createForum("Java");
        red.joinForum("Java", u1);
        red.joinForum("Java", u2);
        red.joinForum("Java", u3);

        red.createPost("Java", "Hello", "Hi");
        red.createPost("Java", "World", "Hello again");

        red.createComment("Hello", "Nice");
        red.createComment("Hello", "Good");
        red.createComment("World", "Good");

        red.upVote(red.postMap.get("Hello"));
        red.upVote(red.commentMap.get("Nice"));

        red.displayForum("Java");
        System.out.println(objectMapper.writeValueAsString(red.getTopPosts("Java", 2)));
    }
}