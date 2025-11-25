package LLD.StockBrokrageSystem;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import utils.ThreadUtils;

import java.util.*;
import java.util.concurrent.*;


// <--------Entity -------->
@Setter
@Getter
class User{
    String id;
    String name;
    double balance;
    Portfolio portfolio;
    List<Transaction> transactions;
    User(String name){
        this.name = name;
        this.balance =  100;
        this.portfolio = new Portfolio(name);
        this.transactions = new ArrayList<>();
    }
}

@ToString
@Getter
class Stock{
    String id;
    String name;
    double price;
    Stock(String name, double price){
        this.name = name;
        this.price = price;
    }

    public int hashCode(){
        return name.hashCode();
    }

    public boolean equals(Object obj){
        return this.getName().equals(((Stock)obj).getName());
    }
}


class Portfolio{
    String userId;
    Map<Stock, Integer> stocks = new ConcurrentHashMap<>();
    Portfolio(String userId){
        this.userId = userId;
    }

    public String toString(){
        return "Portfolio{" +
                "userId='" + userId + '\'' +
                ", stocks=" + stocks +
                '}';
    }
}

@Builder
@Setter
@Getter
class Transaction{
    enum TransactionType {BUY, SELL}
    String id;
    String userId;
    String stockId;
    int qty;
    double txnAmount;
    TransactionType type;
}

 interface IStockBrokerageService {
    double getPrice(String stockName);
    Transaction buyStock(String userId, String stockName, int quantity);
    Transaction sellStock(String userId, String stockName, int quantity);
    Portfolio getPortfolio(String userId);
    List<Transaction> getTransactionHistory(String userId);
}

 public class StockBrokerageService implements IStockBrokerageService{
    Map<String, User> userRepo = new HashMap<>(){{
        put("user1", new User("user1"));
        put("user2", new User("user1"));
    }};
    Map<String, Stock> stockRepo = new HashMap<>(){{
        put("st1", new Stock("st1", 10.0));
        put("st2", new Stock("st2", 20.0));
    }};

    Map<String, Object> userLocks = new ConcurrentHashMap<>();

    public Object getLockFor(String userId){
        return userLocks.computeIfAbsent(userId, k -> new Object());
    }

     @Override
     public double getPrice(String stockName) {
         return stockRepo.get(stockName).getPrice();
     }

     @Override
     public Transaction buyStock(String userId, String stockName, int quantity) {
         User user = userRepo.get(userId);
         // calculate txn amount
         Stock stock = stockRepo.get(stockName);
         double txnAmount = stock.getPrice() * quantity;
         Transaction transaction = Transaction.builder()
                 .id(UUID.randomUUID().toString())
                 .type(Transaction.TransactionType.BUY)
                 .userId(userId)
                 .stockId(stockName)
                 .qty(quantity)
                 .txnAmount(txnAmount)
                 .build();
         synchronized (getLockFor(userId)){
             System.out.println("[" + Thread.currentThread().getName() + "] " + System.currentTimeMillis() + " - Buying for " + userId);
             //  check balance
             if (txnAmount > user.getBalance()){
                 throw new UnsupportedOperationException("Insufficient Fund");
             }
             user.setBalance(user.getBalance() -  txnAmount);
             user.getPortfolio().stocks.put(stock, user.getPortfolio().stocks.getOrDefault(stock, 0)+quantity);
             user.getTransactions().add(transaction);
             userRepo.put(userId, user);
             ThreadUtils.sleepSeconds(2);
             System.out.println(this.getPortfolio(userId));
         }
         return transaction;

     }

     @Override
     public Transaction sellStock(String userId, String stockName, int quantity) {
        User user = userRepo.get(userId);
         // calculate txn amount
         Stock stock = stockRepo.get(stockName);
         double txnAmount = stock.getPrice() * quantity;
         Transaction transaction = Transaction.builder()
                 .id(UUID.randomUUID().toString())
                 .type(Transaction.TransactionType.SELL)
                 .userId(userId)
                 .stockId(stockName)
                 .qty(quantity)
                 .txnAmount(txnAmount)
                 .build();
         synchronized (getLockFor(userId)){
             System.out.println("[" + Thread.currentThread().getName() + "] " + System.currentTimeMillis() + " - selling for " + userId);
             // check stock availability
             if (user.getPortfolio().stocks.getOrDefault(stock, 0) < quantity){
                 throw new UnsupportedOperationException("Insufficient Stock");
             }
             user.setBalance(user.getBalance() +  txnAmount);
             user.getPortfolio().stocks.put(stock, user.getPortfolio().stocks.getOrDefault(stock, 0)-quantity);
             user.getTransactions().add(transaction);
             userRepo.put(userId, user);
             System.out.println(this.getPortfolio(userId));
             ThreadUtils.sleepSeconds(2);
         }
         return transaction;
     }

     @Override
     public Portfolio getPortfolio(String userId) {
        return userRepo.get(userId).getPortfolio();
     }

     @Override
     public List<Transaction> getTransactionHistory(String userId) {
         return userRepo.get(userId).getTransactions();
     }
 }

 class Runner{
     public static void main(String[] args) {
         StockBrokerageService brokerageService = new StockBrokerageService();
         System.out.println(brokerageService.getPrice("st1"));

         brokerageService.buyStock("user1" , "st1", 5);
//         System.out.println(brokerageService.getPortfolio("user1"));

         // concurrent buy
         Callable<Transaction> callable  = () -> {
             return brokerageService.buyStock("user1", "st1", 5);
         };
         ExecutorService executorService = Executors.newFixedThreadPool(2);
         Future<Transaction> f1 = executorService.submit(callable);
         Future<Transaction> f2 = executorService.submit(callable);
         try {
             f1.get();
             f2.get();
         } catch (ExecutionException | InterruptedException e) {
             System.out.println(e.getMessage());
         }
//         executorService.shutdown();

         brokerageService.sellStock("user1" , "st1", 5);
//         System.out.println(brokerageService.getPortfolio("user1"));


         CompletableFuture<Void> f3 = CompletableFuture.runAsync(() -> brokerageService.sellStock("user1", "st1", 5), executorService);
         CompletableFuture<Void> f4 = CompletableFuture.runAsync(() -> brokerageService.sellStock("user1", "st1", 5), executorService);

         try{
             CompletableFuture.allOf(f3,f4).join();
         }catch (Exception e){
             System.out.println(e.getMessage());
         }
         executorService.shutdown();

//         System.out.println(brokerageService.getPortfolio("user1"));

     }
 }
