package LLD.DigitalWallet;

import lombok.Getter;
import org.springframework.transaction.annotation.Transactional;
import utils.ThreadUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

interface IWallet {
}

class Userr{
    String id;
    String name;
    Currency currency;
    IWallet wallet;
}

@Getter
class Account{
    private final String accountId;
    private BigDecimal balance;
    private final ReentrantLock lock = new ReentrantLock();

    public Account(String accountId) {
        this.accountId = accountId;
        this.balance = BigDecimal.ZERO;
    }

    public String getAccountId() { return accountId; }

    // Thread-safe credit
    public void credit(BigDecimal amount) {
        lock.lock();
        try {
            balance = balance.add(amount);
        } finally {
            lock.unlock();
        }
    }

    // Thread-safe debit, throws if insufficient
    public void debit(BigDecimal amount) {
        lock.lock();
        try {
            if (balance.compareTo(amount) < 0) {
                throw new IllegalStateException("Insufficient balance");
            }
            balance = balance.subtract(amount);
        } finally {
            lock.unlock();
        }
    }

    public BigDecimal getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }
}

class Transaction {
    public enum Type { CREDIT, DEBIT, TRANSFER }

    private final String txId;
    private final String fromAccount; // nullable for external credit
    private final String toAccount;   // nullable for external debit
    private final BigDecimal amount;
    private final Type type;
    private final Instant when;
    private final String note;

    public Transaction(String fromAccount, String toAccount, BigDecimal amount, Type type, String note) {
        this.txId = UUID.randomUUID().toString();
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.type = type;
        this.when = Instant.now();
        this.note = note;
    }

    // getters
    public String getTxId() { return txId; }
    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public BigDecimal getAmount() { return amount; }
    public Type getType() { return type; }
    public Instant getWhen() { return when; }
    public String getNote() { return note; }
}


interface TransactionRepository {
    void save(Transaction tx);
    List<Transaction> findByAccountId(String accountId, int limit);
}

class InMemoryTransactionRepository implements TransactionRepository {
    // accountId -> list of tx
    private final Map<String, Deque<Transaction>> store = new ConcurrentHashMap<>();

    @Override
    public void save(Transaction tx) {
        // save for both from and to accounts if present
        if (tx.getFromAccount() != null) {
            store.computeIfAbsent(tx.getFromAccount(), k -> new ArrayDeque<>()).addFirst(tx);
        }
        if (tx.getToAccount() != null) {
            store.computeIfAbsent(tx.getToAccount(), k -> new ArrayDeque<>()).addFirst(tx);
        }
    }

    @Override
    public List<Transaction> findByAccountId(String accountId, int limit) {
        Deque<Transaction> deque = store.getOrDefault(accountId, new ArrayDeque<>());
        List<Transaction> result = new ArrayList<>();
        int i = 0;
        for (Transaction t : deque) {
            result.add(t);
            i++;
            if (i >= limit) break;
        }
        return result;
    }
}

class PaymentException extends Exception {
    public PaymentException(String message) { super(message); }
    public PaymentException(String message, Throwable t) { super(message, t); }
}

interface PaymentProcessor {
    /**
     * Process external payment for crediting wallet (e.g., UPI top-up).
     * Returns transactionId for the external provider or throws exception.
     */
    String processPayment(String UserrId, BigDecimal amount) throws PaymentException;

    /**
     * Refund / reverse if needed.
     */
    void refund(String providerTxId) throws PaymentException;
}

/**
 * Simple stub implementation. In real world it would call payment gateway.
 */
class UPIPaymentProcessor implements PaymentProcessor {
    @Override
    public String processPayment(String UserrId, BigDecimal amount) throws PaymentException {
        // KISS: simulate success
        return "UPI-" + UUID.randomUUID();
    }

    @Override
    public void refund(String providerTxId) throws PaymentException {
        // simulate refund
    }
}

interface NotificationService {
    void notify(String UserrId, String message);
}

class ConsoleNotificationService implements NotificationService {
    @Override
    public void notify(String UserrId, String message) {
        System.out.printf("Notify[%s]: %s%n", UserrId, message);
    }
}

interface IWalletService{
    Account createAccount(String accountId);
    BigDecimal getBalance(String accountId);
    void addMoney(String UserrId, String accountId, BigDecimal amount, String note) throws PaymentException;
    void withdraw(String UserrId, String accountId, BigDecimal amount, String note);
    void transfer(String fromAccountId, String toAccountId, BigDecimal amount, String note);
    List<Transaction> getRecentTransactions(String accountId, int limit);
}

@Getter
class WalletService implements IWalletService{
    // simple in-memory account store; for production use persistent storage
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final TransactionRepository txRepo;
    private final PaymentProcessor paymentProcessor;
    private final NotificationService notificationService;

    public WalletService(TransactionRepository txRepo,
                         PaymentProcessor paymentProcessor,
                         NotificationService notificationService) {
        this.txRepo = txRepo;
        this.paymentProcessor = paymentProcessor;
        this.notificationService = notificationService;
    }

    // create account (idempotent)
    public Account createAccount(String accountId) {
        return accounts.computeIfAbsent(accountId, Account::new);
    }

    public BigDecimal getBalance(String accountId) {
        Account a = accounts.get(accountId);
        if (a == null) return BigDecimal.ZERO;
        return a.getBalance();
    }

    // add money via external payment
    public void addMoney(String UserrId, String accountId, BigDecimal amount, String note) throws PaymentException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive");

        // process external payment via strategy
        String providerTxId = paymentProcessor.processPayment(UserrId, amount);

        // credit wallet
        Account a = createAccount(accountId);
        a.credit(amount);

        Transaction tx = new Transaction(null, accountId, amount, Transaction.Type.CREDIT, "Top-up: " + note + " (providerTx:" + providerTxId + ")");
        txRepo.save(tx);

        notificationService.notify(UserrId, "Wallet credited: " + amount + ". New balance: " + a.getBalance());
    }

    // withdraw to external (simplified: only wallet->external allowed)
    public void withdraw(String UserrId, String accountId, BigDecimal amount, String note) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive");
        Account a = accounts.get(accountId);
        if (a == null) throw new IllegalStateException("Account not found");

        a.debit(amount);
        Transaction tx = new Transaction(accountId, null, amount, Transaction.Type.DEBIT, "Withdraw: " + note);
        txRepo.save(tx);
        notificationService.notify(UserrId, "Wallet debited: " + amount + ". New balance: " + a.getBalance());
    }

    @Transactional
    // transfer between two accounts (internal)
    public void transfer(String fromAccountId, String toAccountId, BigDecimal amount, String note) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive");
        Account from = accounts.get(fromAccountId);
        Account to = createAccount(toAccountId);

        if (from == null) throw new IllegalStateException("Source account not found");

        ThreadUtils.sleepSeconds(5);
        // Debit then credit to avoid money creation
        from.debit(amount);
        to.credit(amount);

        Transaction tx = new Transaction(fromAccountId, toAccountId, amount, Transaction.Type.TRANSFER, note);
        txRepo.save(tx);

        // notifications (in real system we'd look up Userr ids)
        notificationService.notify(fromAccountId, "Transferred " + amount + " to " + toAccountId);
        notificationService.notify(toAccountId, "Received " + amount + " from " + fromAccountId);
    }

    public List<Transaction> getRecentTransactions(String accountId, int limit) {
        return txRepo.findByAccountId(accountId, limit);
    }
}

class App {
    public static void main(String[] args) throws Exception {
        TransactionRepository txRepo = new InMemoryTransactionRepository();
        PaymentProcessor upi = new UPIPaymentProcessor();
        NotificationService notifier = new ConsoleNotificationService();

        WalletService walletService = new WalletService(txRepo, upi, notifier);

        String aliceAcc = "alice_acc";
        String bobAcc = "bob_acc";

        // Create accounts
        walletService.createAccount(aliceAcc);
        walletService.createAccount(bobAcc);

        // Add money to Alice via UPI
        walletService.addMoney("alice", aliceAcc, new BigDecimal("1000"), "salary top-up");

        // Transfer to Bob
        walletService.transfer(aliceAcc, bobAcc, new BigDecimal("200"), "pay back");

        // Withdraw from Bob
        walletService.withdraw("bob", bobAcc, new BigDecimal("50"), null);

        // Show transactions for alice
        List<Transaction> txs = walletService.getRecentTransactions(aliceAcc, 10);
        txs.forEach(t -> System.out.println(t.getTxId() + " " + t.getType() + " " + t.getAmount() + " " + t.getNote()));

        System.out.println(walletService.getAccounts().get(aliceAcc).getBalance());
        System.out.println(walletService.getAccounts().get(bobAcc).getBalance());

        // validate concurrent transaction
        // validate concurrent transaction
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<Void> task = () -> {
            try {
                walletService.transfer(aliceAcc, bobAcc, new BigDecimal("100"), "concurrent transfer");
            } catch (Exception e) {
                System.out.println("Concurrent attempt failed: " + e.getMessage());
            }
            return null;
        };
        try {
            executorService.invokeAll(Arrays.asList(task, task));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();

        System.out.println(walletService.getAccounts().get(aliceAcc).getBalance());
        System.out.println(walletService.getAccounts().get(bobAcc).getBalance());
    }
}