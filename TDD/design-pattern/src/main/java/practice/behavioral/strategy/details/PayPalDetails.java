package practice.behavioral.strategy.details;

public class PayPalDetails extends PaymentDetails {
    private final String email;
    private final String password;

    public PayPalDetails(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
