package practice.behavioral.strategy;

public interface PayStrategy {
    boolean pay(int paymentAmount);

    void collectPaymentDetails(String email, String password);
}
