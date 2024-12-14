package practice.behavioral.strategy;

import practice.behavioral.strategy.details.PaymentDetails;

public interface PayStrategy {
    boolean pay(int paymentAmount);

    void collectPaymentDetails(PaymentDetails paymentDetails);
}
