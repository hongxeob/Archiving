package practice.behavioral.strategy.details;

public class CreditCardDetails extends PaymentDetails {
    private final String number;
    private final String date;
    private final String csv;

    public CreditCardDetails(String number, String date, String csv) {
        this.number = number;
        this.date = date;
        this.csv = csv;
    }

    public String getNumber() {
        return number;
    }

    public String getDate() {
        return date;
    }

    public String getCsv() {
        return csv;
    }
}
