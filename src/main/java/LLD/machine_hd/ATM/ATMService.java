package LLD.machine_hd.ATM;

import lombok.*;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

/*
The ATM system should support basic operations such as balance inquiry, cash withdrawal, and cash deposit.
Users should be able to authenticate themselves using a card and a PIN (Personal Identification Number).
The system should interact with a bank's backend system to validate user accounts and perform transactions.
The ATM should have a cash dispenser to dispense cash to users.
The system should handle concurrent access and ensure data consistency.
* */


enum Operation{
    ENQUIRY,
    WITHDRAW,
    DEPOSIT
}

enum AccountStatus{
    ACTIVE,
    INACTIVE,
    BLOCKED
}

enum AccountType{
    SAVINGS,
    CURRENT
}

@Setter
@Getter
@ToString
abstract class Account{
    private String accountNumber;
    private String cardNumber;
    private String pin;
    private double balance = 0;
    private AccountStatus status = AccountStatus.ACTIVE;

    public Account(String accountNumber){
        this.accountNumber = accountNumber;
    }
}

@Setter
@Getter
class SavingsAccount extends Account{
    private double interestRate;
    SavingsAccount(String accountNumber){
        super(accountNumber);
    }

    static SavingsAccountBuilder builder(){
        return new SavingsAccountBuilder();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SavingsAccountBuilder {
        private String accountNumber;
        private String cardNumber;
        private String pin;
        private double balance;
        private AccountStatus status;
        private double interestRate;

        public SavingsAccountBuilder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public SavingsAccountBuilder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public SavingsAccountBuilder pin(String pin) {
            this.pin = pin;
            return this;
        }

        public SavingsAccountBuilder balance(double balance) {
            this.balance = balance;
            return this;
        }

        public SavingsAccountBuilder status(AccountStatus status) {
            this.status = status;
            return this;
        }

        public SavingsAccountBuilder interestRate(double interestRate) {
            this.interestRate = interestRate;
            return this;
        }

        public SavingsAccount build() {
            SavingsAccount savingsAccount = new SavingsAccount(accountNumber);
            savingsAccount.setCardNumber(cardNumber);
            savingsAccount.setPin(pin);
            savingsAccount.setBalance(balance);
            savingsAccount.setStatus(status);
            savingsAccount.setInterestRate(interestRate);
            return savingsAccount;
        }
    }
}

@Getter
@Setter
class CurrentAccount extends Account{
    private double overdraftLimit;
    CurrentAccount(String accountNumber){
        super(accountNumber);
    }

    static CurrentAccountBuilder builder(){
        return new CurrentAccountBuilder();
    }

    @NoArgsConstructor
    public static class CurrentAccountBuilder {
        private String accountNumber;
        private String cardNumber;
        private String pin;
        private double balance;
        private AccountStatus status;
        private double overdraftLimit;

        public CurrentAccountBuilder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public CurrentAccountBuilder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public CurrentAccountBuilder pin(String pin) {
            this.pin = pin;
            return this;
        }

        public CurrentAccountBuilder balance(double balance) {
            this.balance = balance;
            return this;
        }

        public CurrentAccountBuilder status(AccountStatus status) {
            this.status = status;
            return this;
        }

        public CurrentAccountBuilder overdraftLimit(double overdraftLimit) {
            this.overdraftLimit = overdraftLimit;
            return this;
        }

        public CurrentAccount build() {
            CurrentAccount currentAccount = new CurrentAccount(accountNumber);
            currentAccount.setCardNumber(cardNumber);
            currentAccount.setPin(pin);
            currentAccount.setBalance(balance);
            currentAccount.setStatus(status);
            currentAccount.setOverdraftLimit(overdraftLimit);
            return currentAccount;
        }
    }
}

class AccountFactory{
    public static SavingsAccount createSavingsAccount(String accountNumber, String cardNumber, String pin, double balance, AccountStatus status, double interestRate){
        return SavingsAccount.builder()
                .accountNumber(accountNumber)
                .cardNumber(cardNumber)
                .pin(pin)
                .balance(balance)
                .status(status)
                .interestRate(interestRate)
                .build();
    }

    public static CurrentAccount createCurrentAccount(String accountNumber, String cardNumber, String pin, double balance, AccountStatus status, double overdraftLimit){
        return CurrentAccount.builder()
                .accountNumber(accountNumber)
                .cardNumber(cardNumber)
                .pin(pin)
                .balance(balance)
                .status(status)
                .overdraftLimit(overdraftLimit)
                .build();
    }
}

interface IAccountRepository{
    boolean withdraw(String accountNumber, int amount);
    boolean deposit(String accountNumber, int amount);
    int checkBalance(String accountNumber);;
    // cash dispenser
    // card reader
    // pin reader
    // bank backend
    // display
    // printer
}

class InMemoryAccountRepository implements IAccountRepository{
    Map<String, Integer> accountMap; // accountNumber -> accountBalance
    public InMemoryAccountRepository(){
        accountMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean withdraw(String accountNumber, int amount){
        if(accountMap.get(accountNumber) < amount){
            throw new RuntimeException("Insufficient balance");
        }
        try {
            accountMap.put(accountNumber, accountMap.get(accountNumber) - amount);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deposit(String accountNumber, int amount){
        try {
            accountMap.put(accountNumber, accountMap.get(accountNumber) + amount);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int checkBalance(String accountNumber){
        return accountMap.get(accountNumber);
    }
}

public interface ATMService {
    boolean withdraw(String accountNumber, int amount);
    boolean deposit(String accountNumber, int amount);
    double checkBalance(String accountNumber);
    boolean authenticate(String accountNumber, String pin);
}

class ATMServiceImpl implements ATMService{
    Map<String, Account> accountMap;

    ATMServiceImpl(){
        this.accountMap =  new ConcurrentHashMap<>();
    }


    @Override
    public boolean withdraw(String accountNumber, int amount) {
        validateAccount(accountNumber);
        ensureSufficientBalance(accountNumber, amount);
        accountMap.get(accountNumber).setBalance(accountMap.get(accountNumber).getBalance() - amount);
        return true;
    }

    @Override
    public boolean deposit(String accountNumber, int amount) {
        validateAccount(accountNumber);
        accountMap.get(accountNumber).setBalance(accountMap.get(accountNumber).getBalance() + amount);
        return true;
    }

    @Override
    public double checkBalance(String accountNumber) {
        // validate account
        validateAccount(accountNumber);
        return accountMap.get(accountNumber).getBalance();
    }

    @Override
    public boolean authenticate(String accountNumber, String pin) {
        validateAccount(accountNumber);
        return accountMap.get(accountNumber).getPin().equals(pin);
    }

    private void validateAccount(String accountNumber){
        Account account = accountMap.get(accountNumber);
        if(account == null){
            throw new RuntimeException("Account not found");
        }
        if(account.getStatus() != AccountStatus.ACTIVE){
            throw new RuntimeException("Account is not active");
        }
    }

    private void ensureSufficientBalance(String accountNumber, int amount){
        Account account = accountMap.get(accountNumber);
        if(account.getBalance() < amount){
            throw new RuntimeException("Insufficient balance");
        }
    }
}

class Runner{
    public static void main(String[] args) {

    }

    @Test
    public void test(){
        ATMServiceImpl atmService = new ATMServiceImpl();
        Account savingAccount = SavingsAccount.builder()
                .accountNumber("123456")
                .cardNumber("123456")
                .pin("1234")
                .balance(1000)
                .status(AccountStatus.ACTIVE)
                .interestRate(0.05)
                .build();
        Account currentAccount = CurrentAccount.builder()
                .accountNumber("123457")
                .cardNumber("123457")
                .pin("1234")
                .balance(1000)
                .status(AccountStatus.ACTIVE)
                .overdraftLimit(1000)
                .build();

        atmService.accountMap.put("123456", savingAccount);
        atmService.accountMap.put("123457", currentAccount);


        // test withdraw
        assertTrue(atmService.withdraw("123456", 500));
        assertEquals(500, atmService.checkBalance("123456"));

        // test deposit
        assertTrue(atmService.deposit("123456", 200));
        assertEquals(700, atmService.checkBalance("123456"));

        // test authentication
        assertTrue(atmService.authenticate("123456", "1234"));
        assertFalse(atmService.authenticate("123456", "12345"));

        // test insufficient balance
        try {
            atmService.withdraw("123456", 1000);
            fail("Should throw exception for insufficient balance");
        } catch (RuntimeException e) {
            assertEquals("Insufficient balance", e.getMessage());
        }

        // test inactive account
        atmService.accountMap.get("123456").setStatus(AccountStatus.INACTIVE);
        try {
            atmService.withdraw("123456", 500);
            fail("Should throw exception for inactive account");
        } catch (RuntimeException e) {
            assertEquals("Account is not active", e.getMessage());
        }
    }
}






