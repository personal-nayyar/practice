package LLD.splitwise;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

public class SplitWise {
}

@Data
class User{
    String id;
    String name;
    String email;
    String phoneNumber;
    String photoUrl;
    String password;

    public User(String name){
        this.name = name;
    }
}

@Data
class Expense{
    String id;
    User payer;
    List<Split> splits;
    SplitStrategy splitStrategy;
    Group group; // optional

    boolean isSettled;
    String title;
    String description;
    String date;
    double amount;

    public Expense(long id, String description, double amount, User payer, SplitStrategy splitStrategy) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.isSettled = false;
        this.splitStrategy = splitStrategy;
    }

    // Single Responsibility: Compute splits using strategy (Polymorphism)
    public void computeSplits() {
        splits = splitStrategy.calculateSplits(this);
    }
    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}

enum ExpenseType{
    EQUAL,
    EXACT,
    PERCENTAGE;
}

class EqualExpense extends Expense{
    public EqualExpense(long id, String description, double amount, User payer, SplitStrategy splitStrategy) {
        super(id, description, amount, payer, splitStrategy);
    }
}

class ExactExpense extends Expense{
    public ExactExpense(long id, String description, double amount, User payer, SplitStrategy splitStrategy) {
        super(id, description, amount, payer, splitStrategy);
    }
}

class PercentageExpense extends Expense{
    public PercentageExpense(long id, String description, double amount, User payer, SplitStrategy splitStrategy) {
        super(id, description, amount, payer, splitStrategy);
    }

}

// factory pattern for expense creation
class ExpenseFactory {
    public static Expense createExpense(String type, long id, String description, double amount,
                                        User payer, Group group, Object... params) {
        SplitStrategy strategy;
        switch (type.toLowerCase()) {
            case "equal":
                strategy = new EqualSplitStrategy();
                break;
            case "exact":
                @SuppressWarnings("unchecked")
                List<Double> shares = (List<Double>) params[0];
                strategy = new ExactSplitStrategy(shares);
                break;
            case "percentage":
                @SuppressWarnings("unchecked")
                List<Double> percentages = (List<Double>) params[0];
                strategy = new PercentageSplitStrategy(percentages);
                break;
            default:
                throw new IllegalArgumentException("Unknown split type: " + type);
        }
        Expense expense = new Expense(id, description, amount, payer, strategy);
        if (group != null) expense.setGroup(group);
        expense.computeSplits();  // SRP: Compute immediately
        return expense;
    }
}

// Strategy pattern
interface SplitStrategy{
    List<Split> calculateSplits(Expense expense);
}

class EqualSplitStrategy implements SplitStrategy{
    @Override
    public List<Split> calculateSplits(Expense expense) {
        List<Split> splits = new ArrayList<>();
        int numUsers = expense.getGroup() != null ? expense.getGroup().getUsers().size() : 2;  // Default 2 if no group
        double share = expense.getAmount() / numUsers;
        for (User  user : (expense.getGroup() != null ? expense.getGroup().getUsers() : List.of(expense.getPayer()))) {
            if (!user.equals(expense.getPayer())) {  // Payer doesn't owe themselves
                splits.add(new Split(user, share));
            }
        }
        return splits;
    }
}

class ExactSplitStrategy implements SplitStrategy{
    private List<Double> shares; // injected via constructor
    public ExactSplitStrategy(List<Double> shares) {
        this.shares = shares;
    }

    @Override
    public List<Split> calculateSplits(Expense expense) {
        List<Split> splits = new ArrayList<>();
        double totalShare = shares.stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(totalShare - expense.getAmount()) > 0.01) {
            throw new InvalidSplitException("Shares must sum to expense amount");
        }
        // Assume shares correspond to users in group; logic simplified
        List<User> users = expense.getGroup().getUsers();
        for (int i = 0; i < shares.size(); i++) {
            User user = users.get(i % users.size());  // Simplified mapping
            if (!user.equals(expense.getPayer())) {
                splits.add(new Split(user, shares.get(i)));
            }
        }
        return splits;
    }
}

class PercentageSplitStrategy implements SplitStrategy{
    private List<Double> percentages;  // e.g., [30.0, 70.0]

    public PercentageSplitStrategy(List<Double> percentages) {
        this.percentages = percentages;
    }

    @Override
    public List<Split> calculateSplits(Expense expense) {
        List<Split> splits = new ArrayList<>();
        double totalPct = percentages.stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(totalPct - 100.0) > 0.01) {
            throw new InvalidSplitException("Percentages must sum to 100");
        }
        // Similar logic as Exact, but amount = (pct / 100) * expense.amount
        List<User> users = expense.getGroup().getUsers();
        for (int i = 0; i < percentages.size(); i++) {
            User user = users.get(i % users.size());
            if (!user.equals(expense.getPayer())) {
                double share = (percentages.get(i) / 100.0) * expense.getAmount();
                splits.add(new Split(user, share));
            }
        }
        return splits;
    }
}

@AllArgsConstructor
@Data
class Split{
    User user;
    double amount;
}

@AllArgsConstructor
@Data
class Balance{
    private User fromUser ;  // Who owes
    private User toUser ;    // Who is owed
    private double amount;
}

@Data
class Group{
    String id;
    String name;
    List<User> users;

    public Group(String name, List<User> users){
        this.name = name;
        this.users = users;
    }
}

// Custom Exception
class InvalidSplitException extends RuntimeException {
    public InvalidSplitException(String message) {
        super(message);
    }
}

// Custom Exception
class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}


class ExpenseRepository{
    List<Expense> expenseList = new ArrayList<>();
    public void save(Expense expense){}
    public void delete(Expense expense){}
    public void get(Expense expense){}
}

class BalanceRepository{
    List<Balance> balanceList = new ArrayList<>();
    public void save(Balance balance){
        balanceList.add(balance);
    }
    Balance findBetweenUsers(User from, User to){
        for (Balance balance : balanceList) {
            if (balance.getFromUser().equals(from) && balance.getToUser().equals(to)) {
                return balance;
            }
        }
        return null;
    }
}

// Singleton pattern
class DatabaseConfig{
    private static DatabaseConfig instance;
    private DatabaseConfig(){
    }
    public static DatabaseConfig getInstance(){
        if (instance == null){
            synchronized (DatabaseConfig.class){
                if (instance == null){
                    instance = new DatabaseConfig();
                }
            }
        }
        return instance;
    }
}


interface ExpenseSharingService {
    void addExpense(String type, long id, String description, double amount,
                    User payer, Group group, Object... params);
    Map<User, Map<User, Double>> computeBalances(); // view Balance sheet
    void viewBalanceSheet();
    void settleDebt(User from, User to, double amount);
}


class ExpenseSharingServiceImpl implements ExpenseSharingService {
    private ExpenseRepository expenseRepository;
    private BalanceRepository balanceRepository;
    private DatabaseConfig dbConfig = DatabaseConfig.getInstance();  // Singleton

    public ExpenseSharingServiceImpl(ExpenseRepository expenseRepository, BalanceRepository balanceRepository) {
        this.expenseRepository = expenseRepository;
        this.balanceRepository = balanceRepository;
    }

    // Add expense using Factory (SRP: Service orchestrates)
    @Override
    public void addExpense(String type, long id, String description, double amount,
                           User payer, Group group, Object... params) {
        Expense expense = ExpenseFactory.createExpense(type, id, description, amount, payer, group, params);
        expenseRepository.save(expense);

        // Update balances (net owed)
        for (Split split : expense.getSplits()) {
            updateBalance(payer, split.getUser (), -split.getAmount());  // Payer is credited
            updateBalance(split.getUser (), payer, split.getAmount());   // Debtor is debited
        }

        // Notify users (Observer Pattern)
//        payer.notifyObservers("New expense added: " + description);
//        if (group != null) {
//            for (User  user : group.getUsers()) {
//                user.notifyObservers("Group expense: " + description);
//            }
//        }
    }

    // Compute all balances (Polymorphism via repositories)
    public Map<User, Map<User, Double>> computeBalances() {
        List<Balance> allBalances = balanceRepository.balanceList;

        // Group balances by fromUser  for initial population
        Map<User, Map<User, Double>> userBalances = new HashMap<>();

        // Step 1: Initialize maps for all unique users involved
        Set<User> allUsers = new HashSet<>();
        for (Balance b : allBalances) {
            allUsers.add(b.getFromUser ());
            allUsers.add(b.getToUser ());
        }
        for (User  user : allUsers) {
            userBalances.put(user, new HashMap<>());
        }

        // Step 2: Aggregate directional owes (positive: owes to)
        for (Balance balance : allBalances) {
            User from = balance.getFromUser ();
            User to = balance.getToUser ();
            double amount = balance.getAmount();

            // Normalize to 2 decimal places for precision
            amount = Math.round(amount * 100.0) / 100.0;

            // Add to from -> to (owes)
            userBalances.get(from).merge(to, amount, Double::sum);

            // Add symmetric to to -> from (owed, negative)
            userBalances.get(to).merge(from, -amount, Double::sum);
        }

        // Step 3: Clean up - remove self-balances and zeros
        for (User  user : userBalances.keySet()) {
            Map<User, Double> inner = userBalances.get(user);
            inner.entrySet().removeIf(entry ->
                    entry.getKey().equals(user) ||  // Self
                            Math.abs(entry.getValue()) < 0.01  // Zero (floating-point tolerance)
            );
        }

        // Step 4: Return unmodifiable view (immutability, encapsulation)
        Map<User, Map<User, Double>> result = new HashMap<>();
        for (Map.Entry<User, Map<User, Double>> entry : userBalances.entrySet()) {
            result.put(entry.getKey(), Collections.unmodifiableMap(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    // Settle debt (SRP)
    @Override
    public void settleDebt(User from, User to, double amount) {
        Balance balance = balanceRepository.findBetweenUsers(from, to);
        if (balance != null && balance.getAmount() >= amount) {
            balance.setAmount(balance.getAmount() - amount);
            balanceRepository.save(balance);
//            from.notifyObservers("Settled " + amount + " with " + to.getName());
        } else {
            throw new BusinessException("Insufficient balance to settle");
        }
    }

    private void updateBalance(User from, User to, double amount) {
        Balance balance = balanceRepository.findBetweenUsers(from, to);
        if (balance == null) {
            balance = new Balance(from, to, amount);
        } else {
            balance.setAmount(balance.getAmount() + amount);
        }
        balanceRepository.save(balance);
    }
    @Override
    public void viewBalanceSheet(){
        Map<User, Map<User, Double>> computationSheet = computeBalances();
        System.out.println("Compute Sheet:");
        for (Map.Entry<User, Map<User, Double>> entry : computationSheet.entrySet()) {
            System.out.println(entry.getKey().getName() + ":");
            for (Map.Entry<User, Double> subEntry : entry.getValue().entrySet()) {
                System.out.println("\t" + subEntry.getKey().getName() + ": " + subEntry.getValue());
            }
        }
    }
}

class SplitwiseApp {
    public static void main(String[] args) {
        // Setup dependencies (DIP)
        ExpenseRepository expenseRepo = new ExpenseRepository();
        BalanceRepository balanceRepo = new BalanceRepository();

        ExpenseSharingService expenseService = new ExpenseSharingServiceImpl(expenseRepo, balanceRepo);

        // create users for group
        User john = new User("John");
        User alice = new User("Alice");
        User bob = new User("Bob");

        // create group
        Group friends = new Group("Friends", List.of(john, alice, bob));

        expenseService.addExpense("equal", 1, "Lunch", 300, john, friends);
        expenseService.viewBalanceSheet();
        expenseService.settleDebt(john, new User("Alice"), 50);
        expenseService.settleDebt(alice, new User("Bob"), 50);
        expenseService.settleDebt(bob, new User("John"), 50);
        expenseService.viewBalanceSheet();
    }
}




