package practice.structural.adapter;

import practice.structural.adapter.adaptee.EmailService;
import practice.structural.adapter.adaptee.SMSService;
import practice.structural.adapter.adapter.EmailAdapter;
import practice.structural.adapter.adapter.SmsAdapter;

public class NotificationRunner {
    public static void main(String[] args) {
        //email
        EmailService emailService = new EmailService();
        EmailAdapter emailAdapter = new EmailAdapter(emailService);

        NotificationService emailNotification = new NotificationService(emailAdapter);
        String emailNotificationMessage = emailNotification.notify("중요 알림 발생!");
        System.out.println("emailNotificationMessage = " + emailNotificationMessage);

        //sms
        SMSService smsService = new SMSService();
        SmsAdapter smsAdapter = new SmsAdapter(smsService);

        NotificationService smsNotification = new NotificationService(smsAdapter);
        String smsNotificationMessage = smsNotification.notify("중요 알림 발생!!");
        System.out.println("smsNotificationMessage = " + smsNotificationMessage);
    }
}
