package practice.structural.adapter.adapter;

import practice.structural.adapter.adaptee.SMSService;
import practice.structural.adapter.target.MessageSender;

public class SmsAdapter implements MessageSender {
    private final SMSService smsService;

    public SmsAdapter(SMSService smsService) {
        this.smsService = smsService;
    }

    @Override
    public String sendMessage(String message) {
        return smsService.sendMessage(message);
    }
}
