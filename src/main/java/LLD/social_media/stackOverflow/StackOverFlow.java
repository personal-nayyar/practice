package LLD.social_media.stackOverflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.*;

import java.util.*;

@ToString
@Getter
@Setter
@Builder
class User{
    String name;
    String username;
    int reputation;
}

// abstraction
interface Votable{
    void upVote();
    void downVote();
    int getScore();
}

@ToString
@Setter
@Getter
@Builder
class Question implements Votable{
    String id;
    String title;
    String description;
    User user;
    List<Answer> answers = new ArrayList<>();
    List<Tag> tags = new ArrayList<>();
    List<Comment> comments = new ArrayList<>();
    int upVotes;
    int downVotes;
    int score;

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
@Builder
@Getter
@Setter
class Answer implements Votable{
    String title;
    User user;
    String questionId;
    String description;
    List<Comment> comments;
    int upVotes;
    int downVotes;
    int score;

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
@Builder
@Setter
@Getter
class Comment implements Votable{
    User user;
    Question question;
    Answer answer;
    String comment;
    int upVotes;
    int downVotes;
    int score;

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
@Builder
@Setter
@Getter
class Tag {
    String name;
    List<Question> questions;
}

interface IQuestionRepository{
    void save(Question question);
    Question findById(String id);
    List<Question> findAll();
    List<Question> findByTag(Tag tag);
    List<Question> findByUser(User user);
    List<Question> findByTitle(String title);
    List<Question> findByDescription(String description);
}

// Repository pattern
class InMemoryQuestionRepository implements IQuestionRepository{
    Map<String, Question> questions;// title -> question
    Map<Tag, List<Question>> tagQuestions; // tag -> questions
    Map<User, List<Question>> userQuestions; // user -> questions
    Map<Question, List<Comment>> questionComments; // question -> comments
    Map<Question, List<Answer>> questionAnswers; // question -> answers


    // singleton pattern
    private static InMemoryQuestionRepository instance = new InMemoryQuestionRepository();
    public static InMemoryQuestionRepository getInstance(){
        return instance;
    }

    private InMemoryQuestionRepository() {
        questions = new HashMap<>();
        tagQuestions = new HashMap<>();
        userQuestions = new HashMap<>();
        questionComments = new HashMap<>();
        questionAnswers = new HashMap<>();
    }

    @Override
    public void save(Question question) {
        questions.put(question.getTitle(), question);
        System.out.println("Question saved successfully");
        userQuestions.computeIfAbsent(question.getUser(), k -> new ArrayList<>()).add(question);
        System.out.println("Question added to user");
        for(Tag tag : question.getTags()){
            tagQuestions.computeIfAbsent(tag, k -> new ArrayList<>()).add(question);
            System.out.println("Tag saved successfully to question");
        }
//        for(Comment comment: question.getComments()){
//            questionComments.computeIfAbsent(question, k -> new ArrayList<>()).add(comment);
//        }
//
//        for(Answer answer: question.getAnswers()){
//            questionAnswers.computeIfAbsent(question, k -> new ArrayList<>()).add(answer);
//        }
    }

    @Override
    public Question findById(String id) {
        System.out.println("Finding question by id");
        return questions.values().stream().filter(question -> question.id.equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    @Override
    public List<Question> findAll() {
        return questions.values().stream().toList();
    }

    @Override
    public List<Question> findByTag(Tag tag) {
        return tagQuestions.get(tag);
    }

    @Override
    public List<Question> findByUser(User user) {
        return userQuestions.get(user);
    }

    @Override
    public List<Question> findByTitle(String title) {
        return questions.values().stream().filter(question -> question.getTitle().contains(title)).toList();
    }

    @Override
    public List<Question> findByDescription(String description) {
        return questions.values().stream().filter(question -> question.getDescription().contains(description)).toList();
    }
}

// similarly create AnswerRepository and Comment Repository
interface IAnswerRepository{
    void save(Answer answer);
    Answer findById(String id);
    List<Answer> findAll();
    List<Answer> findByQuestion(Question question);
    List<Answer> findByUser(User user);
}

class InMemoryAnswerRepository implements IAnswerRepository{
    Map<String, Answer> answers; // title -> description
    Map<User, List<Answer>> userAnswers; // user -> answers

    InMemoryAnswerRepository(){
        answers = new HashMap<>();
        userAnswers = new HashMap<>();
//        questionAnswers = new HashMap<>();
    }

    @Override
    public void save(Answer answer) {
        answers.put(answer.getTitle(), answer);
        System.out.println("Answer saved successfully");
        userAnswers.computeIfAbsent(answer.getUser(), k -> new ArrayList<>()).add(answer);
        System.out.println("Answer added to user");
    }

    @Override
    public Answer findById(String id) {
        return null;
    }

    @Override
    public List<Answer> findAll() {
        return List.of();
    }

    @Override
    public List<Answer> findByQuestion(Question question) {
//        return questionAnswers.get(question);
        return List.of();
    }

    @Override
    public List<Answer> findByUser(User user) {
        return List.of();
    }
}

interface ICommentRepository{
    void save(Comment comment);
    Comment findById(String id);
    List<Comment> findAll();
    List<Comment> findByQuestion(Question question);
    List<Comment> findByUser(User user);
    List<Comment> findByAnswer(Answer answer);
    List<Comment> findByTag(Tag tag);
}

class InMemoryCommentRepository implements ICommentRepository{

    @Override
    public void save(Comment comment) {

    }

    @Override
    public Comment findById(String id) {
        return null;
    }

    @Override
    public List<Comment> findAll() {
        return List.of();
    }

    @Override
    public List<Comment> findByQuestion(Question question) {
        return List.of();
    }

    @Override
    public List<Comment> findByUser(User user) {
        return List.of();
    }

    @Override
    public List<Comment> findByAnswer(Answer answer) {
        return List.of();
    }

    @Override
    public List<Comment> findByTag(Tag tag) {
        return List.of();
    }
}

interface IUserRepository{
    void save(User user);
    User findById(String id);
    List<User> findAll();
    List<User> findByUsername(String username);
    List<User> findByEmail(String email);
}

class InMemoryUserRepository implements IUserRepository{
    Map<String, User> userMap;
    Map<User, List<Question>> userQuestions; // user -> questions
    Map<User, List<Answer>> userAnswers; // user -> questions

    InMemoryUserRepository(){
        userMap = new HashMap<>();
    }

    @Override
    public void save(User user) {
        userMap.put(user.getUsername(), user);
    }

    @Override
    public User findById(String id) {
        return userMap.get(id);
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public List<User> findByUsername(String username) {
        return List.of();
    }

    @Override
    public List<User> findByEmail(String email) {
        return List.of();
    }
}

public interface StackOverFlow {
    void addUser(User user);
    void addQuestion(Question question);
    void addAnswer(Answer answer);
    void addComment(Comment comment);
    void addTag(Tag tag);
    void upVote(Votable votable);
    void downVote(Votable votable);
    Set<Question>  search(String Title);
}

class StackOverFlowImpl implements StackOverFlow{
    private IQuestionRepository questionRepository;
    private IAnswerRepository answerRepository;
    private ICommentRepository commentRepository;
    private IUserRepository userRepository;

    public StackOverFlowImpl(IQuestionRepository questionRepository, IAnswerRepository answerRepository, ICommentRepository commentRepository, IUserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addUser(User user) {
        // validation checks
        if(user.getUsername() == null || user.getUsername().isEmpty()){
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        userRepository.save(user);
        System.out.println("User added successfully");
    }

    @Override
    public void addQuestion(Question question) {
        // validation checks
        if(question.getTitle() == null || question.getTitle().isEmpty()){
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if(question.getDescription() == null || question.getDescription().isEmpty()){
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if(question.getUser() == null){
            throw new IllegalArgumentException("User cannot be null");
        }
        questionRepository.save(question);
    }

    @Override
    public void addAnswer(Answer answer) {
        // validation checks
        if(answer.getQuestionId() == null){
            throw new IllegalArgumentException("Question cannot be null");
        }
        if(answer.getUser() == null){
            throw new IllegalArgumentException("User cannot be null");
        }
        answerRepository.save(answer);
        // add answer to question
        questionRepository.findById(answer.getQuestionId()).getAnswers().add(answer);
        System.out.println("Answer added successfully to question");

    }

    @Override
    public void addComment(Comment comment) {
        // validation checks
        if(comment.getUser() == null){
            throw new IllegalArgumentException("User cannot be null");
        }
        if(comment.getQuestion() == null && comment.getAnswer() == null){
            throw new IllegalArgumentException("Question and Answer cannot be null");
        }
        commentRepository.save(comment);
    }

    @Override
    public void addTag(Tag tag){
//        tagRepository.save(tag);
    }

    @Override
    public void upVote(Votable votable) {
        votable.upVote();
    }

    @Override
    public void downVote(Votable votable) {
        votable.downVote();
    }

    @Override
    public Set<Question> search(String keyword) {
        Set<Question> questions = new HashSet<>();
        questions.addAll(questionRepository.findByTitle(keyword));
        questions.addAll(questionRepository.findByDescription(keyword));

        // add answers
//        for (Question question: questions){
//            question.getAnswers().addAll(answerRepository.findByQuestion(question));
//            question.getComments().addAll(commentRepository.findByQuestion(question));
//        }
        return questions;
    }
}

class Runner{
    static ObjectMapper mapper;
    static {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static void main(String[] args) throws JsonProcessingException {
        StackOverFlow stackOverFlow = new StackOverFlowImpl(InMemoryQuestionRepository.getInstance(), new InMemoryAnswerRepository(), new InMemoryCommentRepository(), new InMemoryUserRepository());
        User user = User.builder().name("User1").username("User1").build();
        stackOverFlow.addUser(user);


        Question question = Question.builder().id(UUID.randomUUID().toString()).title("Question1").description("Description1")
                .tags(List.of(Tag.builder().name("Tag1").build()))
                .answers(new ArrayList<>())
                .comments(new ArrayList<>())
                .user(user)
                .build();
        stackOverFlow.addQuestion(question);

        System.out.println("Search results: " + mapper.writeValueAsString(stackOverFlow.search("Question1")));
        System.out.println("=========================================");

        User user2 = User.builder().name("User2").username("User2").build();
        Answer answer = Answer.builder().description("Description2").questionId(question.getId()).user(user2).build();
        stackOverFlow.addAnswer(answer);

        System.out.println("Search results: " + mapper.writeValueAsString(stackOverFlow.search("Question1")));
        System.out.println("=========================================");

        Comment comment = Comment.builder().comment("Comment1").question(question).user(user).build();
        stackOverFlow.addComment(comment);

        stackOverFlow.upVote(question);

        System.out.println("Search results: " + mapper.writeValueAsString(stackOverFlow.search("Question1")));
        System.out.println("=========================================");

    }
}


