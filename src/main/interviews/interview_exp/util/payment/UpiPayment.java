package util.payment;

import java.util.UUID;

class UpiPayment implements PaymentStrategy {
    private String upiId;

    public UpiPayment(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public Payment pay(double amount) {
        System.out.println("Paid ₹" + amount + " using UPI ID: " + upiId);
        return new Payment(UUID.randomUUID().toString(), amount, PaymentStatus.COMPLETED, this);
    }
}
