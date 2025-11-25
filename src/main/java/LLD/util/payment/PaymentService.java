package LLD.util.payment;

public class PaymentService {
    private PaymentStrategy paymentStrategy;

    // allows changing strategy dynamically
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public Payment pay(double amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment strategy not set.");
        }
        return paymentStrategy.pay(amount);
    }
}

