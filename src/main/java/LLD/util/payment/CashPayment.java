package LLD.util.payment;

import java.util.UUID;

public class CashPayment implements PaymentStrategy{
    @Override
    public Payment pay(double amount) {
        System.out.println("Paid ₹" + amount + " using Cash");
        return new Payment(UUID.randomUUID().toString(), amount, PaymentStatus.COMPLETED, this);
    }
}
