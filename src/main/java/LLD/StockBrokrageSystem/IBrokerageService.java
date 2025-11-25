package LLD.StockBrokrageSystem;


import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

interface IBrokerageService {
    double getCurrentPrice(String stockName);
    Optional<Trade> placeOrder(Order order); // buy/sell
    Map<String, Integer> getPortfolio2(String userId);
    List<Trade> getTransactionHistory(String userId);
}

/*
  Simple Brokerage System (core)
  - Users create trading accounts
  - Place BUY/SELL orders (market orders)
  - MarketDataService simulates real-time prices
  - Orders are validated (balance/shares) and executed immediately at current price
  - Portfolio2 and transaction history maintained per account
  - Thread-safe using Concurrent collections and per-account locking
*/

// ---------- Domain ----------

enum Operation { BUY, SELL }

class Order {
    final String id;
    final String accountId;
    final String symbol;
    final Operation operation;
    final int quantity;          // shares
    final double limitPrice;     // if <=0 treat as market order
    final Instant createdAt;

    Order(String accountId, String symbol, Operation operation, int quantity, double limitPrice) {
        this.id = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.symbol = symbol;
        this.operation = operation;
        this.quantity = quantity;
        this.limitPrice = limitPrice;
        this.createdAt = Instant.now();
    }

    public String toString() {
        return String.format("Order[%s] %s %d %s @ %.2f (acct=%s)", id, operation, quantity, symbol, limitPrice, accountId);
    }
}

class Trade {
    final String tradeId;
    final String orderId;
    final String accountId;
    final String symbol;
    final Operation operation;
    final int quantity;
    final double price;
    final Instant executedAt;

    Trade(String orderId, String accountId, String symbol, Operation operation, int quantity, double price) {
        this.tradeId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.accountId = accountId;
        this.symbol = symbol;
        this.operation = operation;
        this.quantity = quantity;
        this.price = price;
        this.executedAt = Instant.now();
    }

    public String toString() {
        return String.format("Trade[%s] %s %d %s @ %.2f (acct=%s)", tradeId, operation, quantity, symbol, price, accountId);
    }
}

// Simple Portfolio2: map symbol -> shares
class Portfolio2 {
    private final ConcurrentHashMap<String, AtomicInteger> positions = new ConcurrentHashMap<>();

    void addShares(String symbol, int qty) {
        positions.computeIfAbsent(symbol, s -> new AtomicInteger(0)).addAndGet(qty);
    }

    int getShares(String symbol) {
        AtomicInteger a = positions.get(symbol);
        return a == null ? 0 : a.get();
    }

    Map<String,Integer> snapshot() {
        Map<String,Integer> copy = new HashMap<>();
        for (Map.Entry<String, AtomicInteger> e : positions.entrySet()) copy.put(e.getKey(), e.getValue().get());
        return copy;
    }
}

class Account {
    final String id;
    final String ownerName;
    private final AtomicReference<Double> cash = new AtomicReference<>(0.0);
    final Portfolio2 Portfolio2 = new Portfolio2();
    final List<Trade> trades = Collections.synchronizedList(new ArrayList<>());

    Account(String id, String ownerName, double initialCash) {
        this.id = id; this.ownerName = ownerName;
        this.cash.set(initialCash);
    }

    double getCash() { return cash.get(); }

    // deposit/withdraw atomic
    boolean debit(double amount) {
        while (true) {
            Double current = cash.get();
            if (current < amount) return false;
            if (cash.compareAndSet(current, current - amount)) return true;
        }
    }

    void credit(double amount) {
        while (true) {
            Double current = cash.get();
            if (cash.compareAndSet(current, current + amount)) return;
        }
    }
}

// ---------- Repositories (simple in-memory) ----------

interface AccountRepository {
    void save(Account account);
    Optional<Account> findById(String id);
}

class InMemoryAccountRepository implements AccountRepository {
    private final ConcurrentMap<String, Account> store = new ConcurrentHashMap<>();
    public void save(Account a) { store.put(a.id, a); }
    public Optional<Account> findById(String id) { return Optional.ofNullable(store.get(id)); }
}

// ---------- Market Data Service (simulated real-time quotes) ----------

class MarketDataService {
    private final ConcurrentMap<String, AtomicReference<Double>> priceMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    MarketDataService(Map<String, Double> initialPrices) {
        for (Map.Entry<String, Double> e : initialPrices.entrySet())
            priceMap.put(e.getKey(), new AtomicReference<>(e.getValue()));

        // Simulate price updates every second
        scheduler.scheduleAtFixedRate(this::simulatePriceMoves, 1, 1, TimeUnit.SECONDS);
    }

    double getPrice(String symbol) {
        AtomicReference<Double> ref = priceMap.get(symbol);
        if (ref == null) throw new IllegalArgumentException("Unknown symbol: " + symbol);
        return ref.get();
    }

    void updatePrice(String symbol, double price) {
        priceMap.computeIfAbsent(symbol, s -> new AtomicReference<>(price)).set(price);
    }

    private void simulatePriceMoves() {
        Random r = new Random();
        for (Map.Entry<String, AtomicReference<Double>> e : priceMap.entrySet()) {
            double oldp = e.getValue().get();
            double change = (r.nextDouble() - 0.5) * 0.02 * oldp; // +/-1%
            e.getValue().set(Math.max(0.01, oldp + change));
        }
    }

    void shutdown() { scheduler.shutdown(); }
}

// ---------- Brokerage Service (core order handling + validation) ----------

class BrokerageService {
    private final AccountRepository accountRepo;
    private final MarketDataService mdService;
    // per-account locking object to avoid race conditions on balances/positions
    private final ConcurrentMap<String, Object> accountLocks = new ConcurrentHashMap<>();

    BrokerageService(AccountRepository accountRepo, MarketDataService mdService) {
        this.accountRepo = accountRepo;
        this.mdService = mdService;
    }

    private Object lockForAccount(String accountId) {
        return accountLocks.computeIfAbsent(accountId, k -> new Object());
    }

    // Place an order: we accept market orders (limitPrice <= 0) or simple limit (not matched except if market price <= limit for buy or >= for sell)
    Optional<Trade> placeOrder(Order order) {
        Optional<Account> oa = accountRepo.findById(order.accountId);
        if (!oa.isPresent()) {
            System.out.println("Account not found: " + order.accountId);
            return Optional.empty();
        }
        Account account = oa.get();
        synchronized (lockForAccount(account.id)) {
            double marketPrice;
            try { marketPrice = mdService.getPrice(order.symbol); }
            catch (IllegalArgumentException e) { System.out.println("Unknown symbol: " + order.symbol); return Optional.empty(); }

            // determine execution price based on limit vs market
            double execPrice;
            if (order.limitPrice <= 0) {
                execPrice = marketPrice; // market order
            } else {
                // simple: buy limit executes only if market <= limit; sell limit executes only if market >= limit
                if ((order.operation == Operation.BUY && marketPrice <= order.limitPrice) ||
                        (order.operation == Operation.SELL && marketPrice >= order.limitPrice)) {
                    execPrice = marketPrice;
                } else {
                    System.out.println("Limit not executable now at market " + marketPrice + " for order " + order.id);
                    return Optional.empty();
                }
            }

            double totalCost = execPrice * order.quantity;
            if (order.operation == Operation.BUY) {
                // check cash
                if (account.getCash() < totalCost) {
                    System.out.println("Insufficient balance for account " + account.id);
                    return Optional.empty();
                }
                // debit cash, add shares
                boolean debited = account.debit(totalCost);
                if (!debited) { System.out.println("Concurrent insufficient funds: " + account.id); return Optional.empty(); }
                account.Portfolio2.addShares(order.symbol, order.quantity);
            } else {
                // SELL: check shares
                int shares = account.Portfolio2.getShares(order.symbol);
                if (shares < order.quantity) {
                    System.out.println("Insufficient shares to sell for account " + account.id);
                    return Optional.empty();
                }
                // reduce shares, credit cash
                account.Portfolio2.addShares(order.symbol, -order.quantity);
                account.credit(totalCost);
            }

            // create trade record
            Trade trade = new Trade(order.id, order.accountId, order.symbol, order.operation, order.quantity, execPrice);
            account.trades.add(trade);
            System.out.println("Executed: " + trade);
            return Optional.of(trade);
        }
    }

    // Query Portfolio2 and trades
    Map<String, Integer> getPortfolio2(String accountId) {
        Optional<Account> oa = accountRepo.findById(accountId);
        if (!oa.isPresent()) return Collections.emptyMap();
        return oa.get().Portfolio2.snapshot();
    }

    List<Trade> getTrades(String accountId) {
        Optional<Account> oa = accountRepo.findById(accountId);
        if (!oa.isPresent()) return Collections.emptyList();
        return new ArrayList<>(oa.get().trades);
    }
}

// ---------- Demo wiring and usage ----------

class BrokerageApp {
    public static void main(String[] args) throws Exception {
        // initial prices
        Map<String, Double> init = Map.of("AAPL", 150.0, "GOOG", 2800.0, "TSLA", 700.0);
        MarketDataService md = new MarketDataService(init);
        AccountRepository accountRepo = new InMemoryAccountRepository();
        BrokerageService broker = new BrokerageService(accountRepo, md);

        // create accounts
        Account alice = new Account("ACC1", "Alice", 10000.00);
        Account bob   = new Account("ACC2", "Bob",   2000.00);
        accountRepo.save(alice); accountRepo.save(bob);

        // place orders concurrently
        ExecutorService ex = Executors.newFixedThreadPool(4);

        // Market buy AAPL for Alice 10 shares
        ex.submit(() -> {
            Order o = new Order(alice.id, "AAPL", Operation.BUY, 10, 0); // market
            broker.placeOrder(o);
        });

        // Limit buy GOOG for Alice at 2700 (may not execute)
        ex.submit(() -> {
            Order o = new Order(alice.id, "GOOG", Operation.BUY, 1, 2700);
            broker.placeOrder(o);
        });

        // Bob tries to sell TSLA shares he doesn't own -> should fail
        ex.submit(() -> {
            Order o = new Order(bob.id, "TSLA", Operation.SELL, 1, 0);
            broker.placeOrder(o);
        });

        // Bob buys TSLA market small quantity
        ex.submit(() -> {
            Order o = new Order(bob.id, "TSLA", Operation.BUY, 2, 0);
            broker.placeOrder(o);
        });

        ex.shutdown(); ex.awaitTermination(5, TimeUnit.SECONDS);

        // view Portfolio2s/trades
        System.out.println("\n--- Portfolio2s ---");
        System.out.println("Alice: " + broker.getPortfolio2(alice.id) + " Cash: " + alice.getCash());
        System.out.println("Bob:   " + broker.getPortfolio2(bob.id)   + " Cash: " + bob.getCash());

        System.out.println("\n--- Trades ---");
        System.out.println("Alice trades: " + broker.getTrades(alice.id));
        System.out.println("Bob trades:   " + broker.getTrades(bob.id));

        // shutdown market data simulator
        md.shutdown();
    }
}