package practice.structural.adapter.adaptee;

public class SMSService {
    public String sendMessage(String message) {
        return "sms 전송" + message;
    }
}
