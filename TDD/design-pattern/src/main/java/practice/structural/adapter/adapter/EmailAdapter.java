package practice.structural.adapter.adapter;

import practice.structural.adapter.adaptee.EmailService;
import practice.structural.adapter.target.MessageSender;

public class EmailAdapter implements MessageSender {
    private final EmailService emailService;

    public EmailAdapter(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public String sendMessage(String message) {
        return emailService.sendMessage(message);
    }
}
