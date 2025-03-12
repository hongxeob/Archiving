package practice.structural.adapter;

import practice.structural.adapter.target.MessageSender;

public class NotificationService {
    private final MessageSender messageSender;

    public NotificationService(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public String notify(String message) {
        return messageSender.sendMessage(message);
    }
}
