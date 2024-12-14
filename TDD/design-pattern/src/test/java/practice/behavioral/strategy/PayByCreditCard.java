package practice.behavioral.strategy;

public class PayByCreditCard implements PayStrategy {
    @Override
    public boolean pay(int paymentAmount) {
        return true;
    }

    @Override
    public void collectPaymentDetails(String email, String password) {

    }
}
