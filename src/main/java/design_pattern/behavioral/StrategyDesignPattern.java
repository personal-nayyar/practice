package design_pattern.behavioral;

/**
 * In Strategy design pattern we define multiple strategy/algorithm to perform a single task
 client decides the actual implementation to be used at runtime.
 * */
public class StrategyDesignPattern {
}

interface PaymentStrategy{
    void makePayment(int amount);
}

class CreditCardStrategy implements PaymentStrategy{
    String name;
    String cardNumber;

    CreditCardStrategy(String name, String cardNumber) {
        this.name = name;
        this.cardNumber = cardNumber;
    }

    @Override
    public void makePayment(int amount) {
        System.out.println("Paid "+amount+" using credit card");
    }
}

class DebitCardStrategy implements PaymentStrategy{
    String name;
    String cardNumber;

    DebitCardStrategy(String name, String cardNumber) {
        this.name = name;
        this.cardNumber = cardNumber;
    }

    @Override
    public void makePayment(int amount) {
        System.out.println("Paid "+amount+" using debit card");
    }
}

class ShoppingCart{
    protected int amount;
    void pay(PaymentStrategy paymentStrategy){
        paymentStrategy.makePayment(amount);
    }
}

class Client{
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        cart.amount = 100;
        cart.pay(new CreditCardStrategy("abc","123"));
        cart.pay(new DebitCardStrategy("abc","123"));
    }
}