package practice.structural.adapter.adaptee;

public class EmailService {
    public String sendMessage(String message) {
        return "email 전송" + message;
    }
}
