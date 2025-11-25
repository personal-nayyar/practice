package LLD.DigitalWallet;


import lombok.Getter;
import lombok.ToString;
import utils.ThreadUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/*
The digital wallet should allow users to create an account and manage their personal information.
Users should be able to add and remove payment methods, such as credit cards or bank accounts.
The digital wallet should support fund transfers between users and to external accounts.
The system should handle transaction history and provide a statement of transactions.
The digital wallet should support multiple currencies and perform currency conversions.
The system should ensure the security of user information and transactions.
The digital wallet should handle concurrent transactions and ensure data consistency.
The system should be scalable to handle a large number of users and transactions.
* */
public interface IDigitalWallet {
    void createAccount(User user);
    void addPaymentMethod(User user, PaymentMethod paymentMethod);
    void removePaymentMethod(User user, PaymentMethod paymentMethod);
    void transferFunds(User user, User recipient, double amount);
    void getTransactionHistory(User user);
    void getStatement(User user);
    void convertCurrency(User user, double amount, Currency currency);
}

@ToString
// <---- Models-------->
class User{
    String id;
    String name;
    String email;
    String phone;
    String address;
    Currency currency;
    Wallet wallet;

    User(String id, String name, Currency currency){
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.wallet = new Wallet(this.id);
    }
}

@ToString
class Wallet {
    String id;
    String userId;
    double balance;

    public Wallet(String userId) {
        this.id = UUID.randomUUID().toString();
        this.userId  = userId;
        this.balance = 100;
    }
}

enum Currency{
    INR, USD, EUR;
    double getConversionRate(){
        return 1;
    }
}




interface PaymentMethod{
    boolean pay(double amount);
}

class WalletPaymentMethod implements PaymentMethod{
    @Override
    public boolean pay(double amount) {
        return true;
    }
}

class CreditCardPaymentMethod implements PaymentMethod{
    @Override
    public boolean pay(double amount) {
        return true;
    }
}

class BankAccountPaymentMethod implements PaymentMethod{
    @Override
    public boolean pay(double amount) {
        return true;
    }
}


@Getter
@ToString
abstract class Transactionn {
    enum Status {
        PENDING, COMPLETED, FAILED
    }
    String id;
    User from;
    User to;
    PaymentMethod paymentMethod;
    double amount;
    Currency currency;
    Status status;
    ReentrantLock lock = new ReentrantLock();

    Transactionn(User from, User to, PaymentMethod paymentMethod, double amount){
        this.id = UUID.randomUUID().toString();
        this.from = from;
        this.to = to;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.currency = from.currency;
        this.status = Status.COMPLETED;
    }
}

class TransferTransactionn extends Transactionn {
    TransferTransactionn(User from, User to, PaymentMethod paymentMethod, double amount) {
        super(from, to, paymentMethod, amount);
    }
    // transfer specific fields
}

class DebitTransactionn extends Transactionn {
    DebitTransactionn(User from, User to, PaymentMethod paymentMethod, double amount) {
        super(from, to, paymentMethod, amount);
    }
    // debit specific fields
}

class CreditTransactionn extends Transactionn {
    CreditTransactionn(User from, User to, PaymentMethod paymentMethod, double amount) {
        super(from, to, paymentMethod, amount);
    }
    // credit specific fields
}

class AddFund extends Transactionn {
    AddFund(User from, User to, PaymentMethod paymentMethod, double amount) {
        super(from, to, paymentMethod, amount);
    }
    // add fund specific fields
}

// <---- interface/Repository-------->
interface IUserRepository{
    User getUserById(String id);
    void saveUser(User user);
    List<User> getAll();
}


interface IWalletRepository{
    Wallet getWalletById(String id);
    void saveWallet(Wallet wallet);
}

interface ITransactionRepository{
    Transactionn getTransaction(String transactionId);
    List<Transactionn> getTransactionById(String userId);
    void saveTransaction(Transactionn transactionn);
}

interface IPaymentMethodRepository{
    PaymentMethod getPaymentMethodById(String id);
    void savePaymentMethod(PaymentMethod paymentMethod);
}

class PaymentMethodRepositoryImpl implements IPaymentMethodRepository{
    Map<String, PaymentMethod> paymentMethodMap = new HashMap<>();
    @Override
    public PaymentMethod getPaymentMethodById(String id) {
        return paymentMethodMap.get(id);
    }
    @Override
    public void savePaymentMethod(PaymentMethod paymentMethod) {
        paymentMethodMap.put(paymentMethod.toString(), paymentMethod);
    }
}

// <------ interface Implementations------->
class UserRepositoryImpl implements IUserRepository{
    Map<String, User> userMap = new HashMap<>();
    @Override
    public User getUserById(String id) {
        return userMap.get(id);
    }
    @Override
    public void saveUser(User user) {
        userMap.put(user.id, user);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }
}


class WalletRepositoryImpl implements IWalletRepository{
    Map<String, Wallet> walletMap = new HashMap<>(); // <---- UserId -> Wallet
    @Override
    public Wallet getWalletById(String id) {
        return walletMap.get(id);
    }
    @Override
    public void saveWallet(Wallet wallet) {
        walletMap.put(wallet.userId, wallet);
    }
}

class TransactionRepositoryImpl implements ITransactionRepository{
    Map<String, List<Transactionn>> transactionMap = new HashMap<>(); // <---- UserId -> List<Transactionn>
    @Override
    public Transactionn getTransaction(String transactionId) {
        return transactionMap.values().stream().flatMap(List::stream)
                .filter(transactionn -> transactionn.id.equals(transactionId)).findFirst().orElse(null);
    }

    @Override
    public List<Transactionn> getTransactionById(String userId) {
        return transactionMap.get(userId);
    }

    @Override
    public void saveTransaction(Transactionn transactionn) {
        // save transactionn in both user wallet
        transactionMap.computeIfAbsent(transactionn.from.id, k-> new ArrayList<>()).add(transactionn);
        transactionMap.computeIfAbsent(transactionn.to.id, k-> new ArrayList<>()).add(transactionn);
    }
}

// <---- interface/Service-------->
interface IDigitalWalletService{
    void createAccount(User user);
    void addPaymentMethod(PaymentMethod paymentMethod);
    void removePaymentMethod(PaymentMethod paymentMethod);
    void transferFunds(User user, User recipient, double amount);
    List<Transactionn> getTransactionHistory(User user);
    void getStatement(User user);
    void addFund(User user, double amount, Currency currency);
}

// <---- interface/Service-------->
class DigitalWalletService implements IDigitalWalletService{
    IUserRepository userRepository;
    IWalletRepository walletRepository;
    ITransactionRepository transactionRepository;
    IPaymentMethodRepository paymentMethodRepository;

    public DigitalWalletService(IUserRepository userRepository, IWalletRepository walletRepository, ITransactionRepository transactionRepository, IPaymentMethodRepository paymentMethodRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public void createAccount(User user) {
        userRepository.saveUser(user);
        walletRepository.saveWallet(user.wallet);
    }

    @Override
    public void addPaymentMethod(PaymentMethod paymentMethod) {
        paymentMethodRepository.savePaymentMethod(paymentMethod);
    }

    @Override
    public void removePaymentMethod(PaymentMethod paymentMethod) {
        paymentMethodRepository.savePaymentMethod(paymentMethod);
    }

    @Override
    public void transferFunds(User user, User recipient, double amount) {
        // currency conversion if different currency
        if (!user.currency.equals(recipient.currency)){
            double convertedAmount = amount * recipient.currency.getConversionRate() / user.currency.getConversionRate();
            amount = convertedAmount;
        }
        // update wallet balance
        Transactionn transactionn = new TransferTransactionn(user, recipient, new WalletPaymentMethod(), amount);
        // save transactionn in both user wallet
        boolean locked = transactionn.lock.tryLock();
        if (!locked)
            throw new RuntimeException("Transactionn already in progress");
        try{
            ThreadUtils.sleepSeconds(2);
            walletRepository.getWalletById(user.id).balance -= amount;
            walletRepository.getWalletById(recipient.id).balance += amount;
            transactionRepository.saveTransaction(transactionn);
        } finally {
            transactionn.lock.unlock();
        }
    }

    @Override
    public List<Transactionn> getTransactionHistory(User user) {
        return transactionRepository.getTransactionById(user.id);
    }

    @Override
    public void getStatement(User user) {
        transactionRepository.getTransactionById(user.id);
    }

    @Override
    public void addFund(User user, double amount, Currency currency) {
        // convert current currency to INR
        double convertedAmount = amount * user.currency.getConversionRate() / currency.getConversionRate();
        userRepository.getUserById(user.id).wallet.balance += amount;
        transactionRepository.saveTransaction(new AddFund(user, user, new WalletPaymentMethod(), convertedAmount));
    }
}

class Runner{
    public static void main(String[] args) {
        DigitalWalletService digitalWallet = new DigitalWalletService(new UserRepositoryImpl(), new WalletRepositoryImpl(), new TransactionRepositoryImpl(), new PaymentMethodRepositoryImpl());

        User user1 = new User("1", "Nayyar1", Currency.INR);
        digitalWallet.createAccount(user1);
        User user2 = new User("2", "Nayyar2", Currency.INR);
        digitalWallet.createAccount(user2);
        System.out.println(digitalWallet.userRepository.getAll());

//        digitalWallet.addPaymentMethod(new PaymentMethod("1", "Nayyar", Currency.INR));


//        digitalWallet.transferFunds(user1,user2,100);
//        System.out.println(digitalWallet.userRepository.getAll());

        // concurrent Transactionn
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<Void> task = () -> {
            Transactionn transactionn = null;
            try{
                 digitalWallet.transferFunds(user1,user2,100);
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

        System.out.println(digitalWallet.userRepository.getAll());


        System.out.println(digitalWallet.getTransactionHistory(user1));
        System.out.println(digitalWallet.getTransactionHistory(user2));

        digitalWallet.addFund(user1, 100, Currency.INR);

        System.out.println(digitalWallet.userRepository.getAll());
    }
}





