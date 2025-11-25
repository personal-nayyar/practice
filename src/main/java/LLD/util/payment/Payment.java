package LLD.util.payment;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Payment {
    String id;
    double amount;
    PaymentStatus status;
    PaymentStrategy strategy;
}
