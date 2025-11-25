package LLD.machine_hd.ATM;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

record TransactionResult(boolean success, String message) {
    public static TransactionResult success(String msg) { return new TransactionResult(true, msg); }
    public static TransactionResult failure(String msg) { return new TransactionResult(false, msg); }
}

record AccountInfo(String accountId, long balanceCents) {}
/**
 * BankClient abstracts communication with bank backend.
 * Implementations can be REST clients or mocks.
 */
interface BankClient {
    boolean verifyPin(String accountId, String pin);
    AccountInfo getAccountInfo(String accountId);
    TransactionResult debit(String accountId, String txId, long amountCents);
    TransactionResult credit(String accountId, String txId, long amountCents);
}

/**
 * Thread-safe in-memory bank client for testing/demo.
 * Uses optimistic atomic operations on balances.
 */
class MockBankClient implements BankClient {
    private final Map<String, AtomicLong> balances = new ConcurrentHashMap<>();
    private final Map<String, String> pins = new ConcurrentHashMap<>();

    public MockBankClient() {
        // sample accounts
        balances.put("acct-1001", new AtomicLong(50_00)); // ₹50.00 => 5000 cents
        balances.put("acct-2002", new AtomicLong(100_00));
        pins.put("acct-1001", "1234");
        pins.put("acct-2002", "9999");
    }

    public void addAccount(String accountId, long balanceCents, String pin) {
        balances.put(accountId, new AtomicLong(balanceCents));
        pins.put(accountId, pin);
    }

    @Override
    public boolean verifyPin(String accountId, String pin) {
        return pin != null && pin.equals(pins.get(accountId));
    }

    @Override
    public AccountInfo getAccountInfo(String accountId) {
        AtomicLong bal = balances.get(accountId);
        if (bal == null) throw new IllegalArgumentException("Account not found");
        return new AccountInfo(accountId, bal.get());
    }

    @Override
    public TransactionResult debit(String accountId, String txId, long amountCents) {
        AtomicLong bal = balances.get(accountId);
        if (bal == null) return TransactionResult.failure("Account not found");
        while (true) {
            long current = bal.get();
            if (current < amountCents) return TransactionResult.failure("Insufficient funds");
            if (bal.compareAndSet(current, current - amountCents)) {
                return TransactionResult.success("Debited " + amountCents + " cents, tx=" + txId);
            }
        }
    }

    @Override
    public TransactionResult credit(String accountId, String txId, long amountCents) {
        AtomicLong bal = balances.get(accountId);
        if (bal == null) return TransactionResult.failure("Account not found");
        bal.addAndGet(amountCents);
        return TransactionResult.success("Credited " + amountCents + " cents, tx=" + txId);
    }
}

/** Single responsibility: authentication only. */
class AuthService {
    private final BankClient bankClient;

    public AuthService(BankClient bankClient) {
        this.bankClient = bankClient;
    }

    public boolean authenticate(String accountId, String pin) {
        return bankClient.verifyPin(accountId, pin);
    }
}

/**
 * Manages physical bill counts. Very small KISS policy greedy withdraw.
 * Denominations expressed in whole currency units (e.g., rupee) for simplicity.
 */
class CashInventory {
    private final Map<Integer, Integer> bills = new HashMap<>(); // denom -> count
    private final ReentrantLock lock = new ReentrantLock();

    public CashInventory(Map<Integer, Integer> initial) {
        if (initial != null) bills.putAll(initial);
    }

    /**
     * Try to withdraw amountUnits (e.g., 500 for ₹500) and produce map of dispensed bills.
     * Returns null if not possible.
     */
    public Map<Integer, Integer> tryWithdraw(int amountUnits) {
        lock.lock();
        try {
            Map<Integer, Integer> temp = new HashMap<>();
            int remaining = amountUnits;
            List<Integer> denoms = new ArrayList<>(bills.keySet());
            denoms.sort(Comparator.reverseOrder());
            for (int d : denoms) {
                int available = bills.getOrDefault(d, 0);
                if (available == 0) continue;
                int need = remaining / d;
                int take = Math.min(need, available);
                if (take > 0) {
                    temp.put(d, take);
                    remaining -= take * d;
                }
            }
            if (remaining != 0) return null;
            // commit
            temp.forEach((d, cnt) -> bills.put(d, bills.get(d) - cnt));
            return temp;
        } finally {
            lock.unlock();
        }
    }

    public void addCash(Map<Integer, Integer> toAdd) {
        lock.lock();
        try {
            toAdd.forEach((d, c) -> bills.merge(d, c, Integer::sum));
        } finally {
            lock.unlock();
        }
    }

    public Map<Integer, Integer> snapshot() {
        lock.lock();
        try {
            return new HashMap<>(bills);
        } finally {
            lock.unlock();
        }
    }
}

/** CashDispenser interacts with CashInventory; hardware abstraction. */
class CashDispenser {
    private final CashInventory inventory;

    public CashDispenser(CashInventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Dispense amountUnits (e.g., 500 => ₹500). Returns TransactionResult and, if success,
     * a map of denominations dispensed in the message.
     */
    public TransactionResult dispense(int amountUnits) {
        Map<Integer, Integer> dispensed = inventory.tryWithdraw(amountUnits);
        if (dispensed == null) {
            return TransactionResult.failure("Unable to dispense: denominations insufficient");
        }
        // In a real system we would talk to hardware and verify success.
        return TransactionResult.success("Dispensed: " + dispensed.toString());
    }
}

/**
 * Template Method: execute() provides logging / lifecycle; doExecute() implemented by subclasses.
 * Command pattern: a Transaction is an executable command.
 */
abstract class Transaction {
    protected final String txId;
    protected final String accountId;

    protected Transaction(String txId, String accountId) {
        this.txId = txId;
        this.accountId = accountId;
    }

    public final TransactionResult execute() {
        try {
            return doExecute();
        } catch (Exception e) {
            return TransactionResult.failure("Exception: " + e.getMessage());
        }
    }

    protected abstract TransactionResult doExecute();
}

/**
 * Withdrawal: debits bank first (preferred) and then dispenses.
 * If dispenser fails after debit, performs compensation (credit).
 */
class Withdrawal extends Transaction {
    private final long amountCents;
    private final BankClient bankClient;
    private final CashDispenser dispenser;

    public Withdrawal(String accountId, long amountCents, BankClient bankClient, CashDispenser dispenser) {
        super(UUID.randomUUID().toString(), accountId);
        this.amountCents = amountCents;
        this.bankClient = bankClient;
        this.dispenser = dispenser;
    }

    @Override
    protected TransactionResult doExecute() {
        // Step 1: Debit at bank
        TransactionResult debit = bankClient.debit(accountId, txId, amountCents);
        if (!debit.success()) return TransactionResult.failure("Bank debit failed: " + debit.message());

        // Step 2: Dispense cash (convert cents to whole units)
        int amountUnits = (int)(amountCents / 100); // e.g., 1500 cents -> 15 units
        TransactionResult dispRes = dispenser.dispense(amountUnits);
        if (!dispRes.success()) {
            // Compensation: refund
            bankClient.credit(accountId, txId + "-refund", amountCents);
            return TransactionResult.failure("Dispense failed, refunded: " + dispRes.message());
        }
        return TransactionResult.success("Withdrawal successful: " + dispRes.message());
    }
}

/** Deposit: For demo, we credit bank first then accept cash into inventory in real ATM. */
class Deposit extends Transaction {
    private final long amountCents;
    private final BankClient bankClient;

    public Deposit(String accountId, long amountCents, BankClient bankClient) {
        super(UUID.randomUUID().toString(), accountId);
        this.amountCents = amountCents;
        this.bankClient = bankClient;
    }

    @Override
    protected TransactionResult doExecute() {
        TransactionResult res = bankClient.credit(accountId, txId, amountCents);
        if (!res.success()) return TransactionResult.failure("Deposit failed: " + res.message());
        // In real ATM, would accept cash envelope and later refill inventory; omitted here.
        return TransactionResult.success("Deposit successful: " + amountCents + " cents credited.");
    }
}

/** Balance inquiry - lightweight read operation. */
class BalanceInquiry extends Transaction {
    private final BankClient bankClient;

    public BalanceInquiry(String accountId, BankClient bankClient) {
        super("BI-" + accountId, accountId);
        this.bankClient = bankClient;
    }

    @Override
    protected TransactionResult doExecute() {
        AccountInfo info = bankClient.getAccountInfo(accountId);
        return TransactionResult.success("Balance: " + info.balanceCents() + " cents");
    }
}

class TransactionFactory {
    public static Withdrawal createWithdrawal(String accountId, long amountCents, BankClient bankClient, CashDispenser dispenser) {
        return new Withdrawal(accountId, amountCents, bankClient, dispenser);
    }

    public static Deposit createDeposit(String accountId, long amountCents, BankClient bankClient) {
        return new Deposit(accountId, amountCents, bankClient);
    }

    public static BalanceInquiry createBalanceInquiry(String accountId, BankClient bankClient) {
        return new BalanceInquiry(accountId, bankClient);
    }
}

/**
 * TransactionManager accepts transactions and executes them concurrently.
 * Uses a bounded thread pool and returns Futures. Keeps design simple and testable.
 */
class TransactionManager {
    private final ExecutorService executor;

    public TransactionManager(int threads, int queueSize) {
        this.executor = new ThreadPoolExecutor(threads, threads,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueSize),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public Future<TransactionResult> submit(Transaction tx, long timeoutMillis) {
        Callable<TransactionResult> task = tx::execute;
        Future<TransactionResult> f = executor.submit(task);
        // For simplicity we return the Future; caller can get with timeout
        return f;
    }

    public void shutdown() {
        executor.shutdown();
    }
}

/** Simple ATM orchestration class used by UI/terminal. */
class ATM {
    private final AuthService authService;
    private final BankClient bankClient;
    private final CashDispenser dispenser;
    private final TransactionManager txManager;

    public ATM(AuthService authService, BankClient bankClient, CashDispenser dispenser, TransactionManager txManager) {
        this.authService = authService;
        this.bankClient = bankClient;
        this.dispenser = dispenser;
        this.txManager = txManager;
    }

    public boolean authenticate(String accountId, String pin) {
        return authService.authenticate(accountId, pin);
    }

    public TransactionResult balanceInquiry(String accountId) throws Exception {
        Transaction tx = TransactionFactory.createBalanceInquiry(accountId, bankClient);
        Future<TransactionResult> f = txManager.submit(tx, 5000);
        return f.get(3, TimeUnit.SECONDS);
    }

    public TransactionResult withdraw(String accountId, long amountCents) throws Exception {
        Transaction tx = TransactionFactory.createWithdrawal(accountId, amountCents, bankClient, dispenser);
        Future<TransactionResult> f = txManager.submit(tx, 5000);
        return f.get(5, TimeUnit.SECONDS);
    }

    public TransactionResult deposit(String accountId, long amountCents) throws Exception {
        Transaction tx = TransactionFactory.createDeposit(accountId, amountCents, bankClient);
        Future<TransactionResult> f = txManager.submit(tx, 5000);
        return f.get(5, TimeUnit.SECONDS);
    }
}


class Main {
    public static void main(String[] args) throws Exception {
        // Setup bank client
        MockBankClient bank = new MockBankClient();
        bank.addAccount("atm-user-1", 200_00, "4321"); // 200.00 => 20000 cents

        // Setup inventory (denominations in rupee units)
        CashInventory inv = new CashInventory(Map.of(500, 10, 200, 20, 100, 50)); // ₹500x10, ₹200x20, ₹100x50
        CashDispenser dispenser = new CashDispenser(inv);

        // Services
        AuthService auth = new AuthService(bank);
        TransactionManager txManager = new TransactionManager(4, 20);

        ATM atm = new ATM(auth, bank, dispenser, txManager);

        String acct = "atm-user-1";
        String pin = "4321";

        if (!atm.authenticate(acct, pin)) {
            System.out.println("Auth failed");
            return;
        }

        System.out.println("Balance: " + atm.balanceInquiry(acct));

        // Withdraw ₹1200 -> 1200*100 cents = 120000? careful unit mapping: our code expects cents
        long amountCents = 1200 * 100L; // ₹1200
        TransactionResult withdrawRes = atm.withdraw(acct, amountCents);
        System.out.println("Withdraw result: " + withdrawRes.message());

        System.out.println("Balance after withdraw: " + atm.balanceInquiry(acct));

        // Deposit ₹500
        long depositCents = 500 * 100L;
        TransactionResult dep = atm.deposit(acct, depositCents);
        System.out.println("Deposit result: " + dep.message());

        System.out.println("Final balance: " + atm.balanceInquiry(acct));

        txManager.shutdown();
    }
}