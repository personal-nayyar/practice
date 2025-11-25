package util.payment;

import LLD.util.payment.Payment;

public interface PaymentStrategy {
    Payment pay(double amount);
}
