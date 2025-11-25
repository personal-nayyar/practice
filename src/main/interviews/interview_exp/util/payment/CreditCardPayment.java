package util.payment;

import LLD.util.payment.Payment;
import LLD.util.payment.PaymentStatus;

import java.util.UUID;

public class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cardHolderName;

    public CreditCardPayment(String cardNumber, String cardHolderName) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
    }

    @Override
    public Payment pay(double amount) {
        System.out.println("Paid ₹" + amount + " using Credit Card (" + cardHolderName + ")");
        return new Payment(UUID.randomUUID().toString(), amount, PaymentStatus.COMPLETED, this);
    }
}
