package LLD.Leetcode;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
// Abstract Problem class (Inheritance, Polymorphism)
abstract class Problem {
    private String id;
    private String title;
    private String description;
    private String difficulty; // e.g., "Easy", "Medium", "Hard"
    private List<String> tags;
    private List<TestCase> testCases;

    public Problem(String id, String title, String description, String difficulty, List<String> tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.tags = tags;
        this.testCases = new ArrayList<>();
    }

    // Abstract method for polymorphism
    public abstract int getTimeLimit(); // in seconds

    public void addTestCase(TestCase testCase) { testCases.add(testCase); }
    // Getters
}

// EasyProblem subclass
class EasyProblem extends Problem {
    public EasyProblem(String id, String title, String description, List<String> tags) {
        super(id, title, description, "Easy", tags);
    }

    @Override
    public int getTimeLimit() {
        return 30; // seconds
    }
}

// HardProblem subclass
class HardProblem extends Problem {
    public HardProblem(String id, String title, String description, List<String> tags) {
        super(id, title, description, "Hard", tags);
    }

    @Override
    public int getTimeLimit() {
        return 120; // seconds
    }
}

@Getter
// TestCase class
class TestCase {
    private String input;
    private String expectedOutput;

    public TestCase(String input, String expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }
    // Getters
}

@Getter
// Submission class (Encapsulation)
class Submission {
    private String id;
    private String userId;
    private String problemId;
    private String code; // Simulated as string
    private String result; // e.g., "Accepted", "Wrong Answer"
    private long timestamp;

    public Submission(String id, String userId, String problemId, String code) {
        this.id = id;
        this.userId = userId;
        this.problemId = problemId;
        this.code = code;
        this.timestamp = System.currentTimeMillis();
    }

    // Methods
    public void setResult(String result) { this.result = result; }
    // Getters
}

@Getter
// User class
class User {
    private String id;
    private String username;
    private String password;
    private int solvedCount;

    public User(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.solvedCount = 0;
    }

    public void incrementSolved() { solvedCount++; }
    public boolean authenticate(String pwd) { return password.equals(pwd); }
    // Getters
}

interface IUserService {
    User register(String username, String password);
    User login(String username, String password);
}

class UserService implements IUserService{
    Map<String, User> users = new HashMap<>();
    @Override
    public User register(String username, String password){
        if (users.containsKey(username)) throw new IllegalArgumentException("User exists");
        User user = new User(UUID.randomUUID().toString(), username, password);
        users.put(username, user);
        return user;
    }
    @Override
    public User login(String username, String password){
        User user = users.get(username);
        if (user != null && user.authenticate(password)) return user;
        throw new IllegalArgumentException("Invalid credentials");
    }
}


interface IProblemService {
    Problem createProblem(String title, String description, String difficulty, List<String> tags);
    List<Problem> searchProblems(String query);
    Problem findById(String id);
}

class ProblemService implements IProblemService{
    final private Map<String, Problem> problems = new HashMap<>();

    public ProblemService() {
        // Add sample problems
        Problem p1 = new EasyProblem("1", "Two Sum", "Find two numbers that add up to target", Arrays.asList("Array", "Hash Table"));
        p1.addTestCase(new TestCase("2 7 11 15\n9", "0 1"));
        problems.put("1", p1);
    }
    // Pre-populated
    @Override
    public Problem createProblem(String title, String description, String difficulty, List<String> tags) {
        String id = UUID.randomUUID().toString();
        Problem problem = difficulty.equals("Easy") ? new EasyProblem(id, title, description, tags) :
                new HardProblem(id, title, description, tags);
        problems.put(id, problem);
        return problem;
    }
    @Override
    public List<Problem> searchProblems(String query) {
        return problems.values().stream()
                .filter(p -> p.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        p.getTags().stream().anyMatch(tag -> tag.toLowerCase().contains(query.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public Problem findById(String id) {
        return problems.get(id);
    }
}
interface ISubmissionService {
    Submission submitCode(String userId, String problemId, String code);
    List<Submission> getUserSubmissions(String userId);
}

class SubmissionService implements ISubmissionService{
    final private Map<String, Submission> submissions = new HashMap<>();
    final private IJudgeService judgeService;
    final private IProblemService problemService;

    SubmissionService(IJudgeService judgeService, IProblemService problemService){
        this.judgeService = judgeService;
        this.problemService = problemService;
    }

    @Override
    public Submission submitCode(String userId, String problemId, String code) {
        Submission submission = new Submission(UUID.randomUUID().toString(), userId, problemId, code);
        submissions.put(submission.getId(), submission);

        // judge immediately
        String result = judgeService.judgeSubmission(submission, problemService.findById(problemId));
        submission.setResult(result);
        if ("Accepted".equals(result)) {
            // Update user stats (simplified)
        }
        return submission;
    }
    @Override
    public List<Submission> getUserSubmissions(String userId) {
        return submissions.values().stream()
                .filter(s -> s.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}

interface IJudgeService {
    String judgeSubmission(Submission submission, Problem problem);
}

class JudgeService implements IJudgeService{
    @Override
    public String judgeSubmission(Submission submission, Problem problem) {
        // Simulated judging: Check if code contains expected logic (KISS: string match)
        for(TestCase tc: problem.getTestCases()){
            String output = simulateExecution(submission.getCode(), tc.getInput());
            if (!output.equals(tc.getExpectedOutput())) return "Wrong Answer";
        }
        // Simulate judging
        return "Accepted";
    }

    private String simulateExecution(String code, String input) {
        // Very basic simulation: If code contains "return", assume correct for demo
        if (code.contains("return") && input.contains("2")) return "0 1"; // Hardcoded for Two Sum
        return "Wrong";
    }
}

// Singleton Pattern (Design Pattern)
class LeaderboardManager {
    private static LeaderboardManager instance;
    private Map<String, Integer> userScores = new HashMap<>(); // User ID to solved count

    private LeaderboardManager() {}

    public static LeaderboardManager getInstance() {
        if (instance == null) {
            synchronized (LeaderboardManager.class) {
                if (instance == null) instance = new LeaderboardManager();
            }
        }
        return instance;
    }

    public void updateScore(String userId) {
        userScores.put(userId, userScores.getOrDefault(userId, 0) + 1);
    }

    public List<String> getTopUsers(int n) {
        return userScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(n)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}

interface ICodingPlatformFacade {
    // User-related core ops
    String registerUser(String username, String password);
    String loginUser(String username, String password);

    // Problem-related core ops
    String createProblem(String title, String description, String difficulty, List<String> tags);
    List<String> searchProblems(String query); // Returns problem titles/IDs for simplicity

    // Submission-related core ops
    String submitCode(String userId, String problemId, String code); // Returns submission result
    List<String> getUserSubmissions(String userId); // Returns submission IDs/results

    // Leaderboard core op
    List<String> getTopUsers(int n); // Returns top user usernames
}

// Facade Implementation: Orchestrates core ops using injected services
class CodingPlatformFacade implements ICodingPlatformFacade {
    private final IUserService userService;
    private final IProblemService problemService;
    private final ISubmissionService submissionService;
    private final LeaderboardManager leaderboardManager; // Utility

    // Constructor: Inject services (Dependency Inversion)
    public CodingPlatformFacade(IUserService userService, IProblemService problemService,
                                ISubmissionService submissionService) {
        this.userService = userService;
        this.problemService = problemService;
        this.submissionService = submissionService;
        this.leaderboardManager = LeaderboardManager.getInstance();
    }

    @Override
    public String registerUser(String username, String password) {
        User user = userService.register(username, password);
        return "User registered: " + user.getUsername();
    }

    @Override
    public String loginUser(String username, String password) {
        User user = userService.login(username, password);
        return "Logged in as: " + user.getUsername();
    }

    @Override
    public String createProblem(String title, String description, String difficulty, List<String> tags) {
        Problem problem = problemService.createProblem(title, description, difficulty, tags);
        return problem.getId();
    }

    @Override
    public List<String> searchProblems(String query) {
        List<Problem> problems = problemService.searchProblems(query);
        return problems.stream().map(Problem::getId).collect(Collectors.toList());
    }

    @Override
    public String submitCode(String userId, String problemId, String code) {
        Submission submission = submissionService.submitCode(userId, problemId, code);
        if ("Accepted".equals(submission.getResult())) {
            leaderboardManager.updateScore(userId); // Update leaderboard on success
        }
        return "Submission result: " + submission.getResult();
    }

    @Override
    public List<String> getUserSubmissions(String userId) {
        List<Submission> submissions = submissionService.getUserSubmissions(userId);
        return submissions.stream()
                .map(s -> "Problem: " + s.getProblemId() + " | Result: " + s.getResult())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getTopUsers(int n) {
        List<String> topUserIds = leaderboardManager.getTopUsers(n);
        // Map IDs to usernames (simplified; in real code, fetch from UserService)
        return topUserIds.stream().map(id -> "User" + id).collect(Collectors.toList()); // Placeholder
    }
}

class CodingPlatform {
    public static void main(String[] args) {
        // Instantiate services (as before)
        IUserService userService = new UserService();
        IProblemService problemService = new ProblemService();
        IJudgeService judgeService = new JudgeService();
        ISubmissionService submissionService = new SubmissionService(judgeService, problemService);

        // Create Facade (inject services)
        ICodingPlatformFacade facade = new CodingPlatformFacade(userService, problemService, submissionService);

        // Use Facade for core ops (simplified, no direct service calls)
        System.out.println(facade.registerUser("coder123", "pass123"));
        System.out.println(facade.loginUser("coder123", "pass123"));

        String problemId = facade.createProblem("Two Sum", "Find two numbers...", "Easy", Arrays.asList("Array"));
        System.out.println("problem created:"+problemId);

        List<String> problems = facade.searchProblems("Array");
        System.out.println("Found problems: " + problems);

        System.out.println(facade.submitCode("user-id", problemId, "code here"));
        System.out.println("Top users: " + facade.getTopUsers(5));
    }
}
