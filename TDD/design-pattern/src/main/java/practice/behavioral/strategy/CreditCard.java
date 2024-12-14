package practice.behavioral.strategy;

public class CreditCard {
    private int amount;
    private String number;
    private String date;
    private String csv;

    public CreditCard(String number, String date, String csv) {
        this.amount = 100_000;
        this.number = number;
        this.date = date;
        this.csv = csv;
    }

    public int getAmount() {
        return amount;
    }

    public void updateAmount(int amount) {
        this.amount = amount;
    }
}
