package practice.behavioral.strategy;

import practice.behavioral.strategy.details.PaymentDetails;

public class Order {
    private int totalCost = 0;
    private boolean isClosed = false;

    public void processOrder(PayStrategy strategy, PaymentDetails paymentDetails) {
        strategy.collectPaymentDetails(paymentDetails);
    }

    public void setTotalCost(int cost) {
        this.totalCost += cost;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed() {
        isClosed = true;
    }

}
