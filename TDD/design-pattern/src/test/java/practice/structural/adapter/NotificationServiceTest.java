package practice.structural.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import practice.structural.adapter.adaptee.EmailService;
import practice.structural.adapter.adaptee.SMSService;
import practice.structural.adapter.adapter.EmailAdapter;
import practice.structural.adapter.adapter.SmsAdapter;
import practice.structural.adapter.target.MessageSender;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationServiceTest {

    private NotificationService emailNotificationService;
    private NotificationService smsNotificationService;
    private MessageSender smsSender;
    private MessageSender emailSender;

    @BeforeEach
    void setUp() {
        EmailService emailService = new EmailService();
        emailSender = new EmailAdapter(emailService);

        SMSService smsService = new SMSService();
        smsSender = new SmsAdapter(smsService);

        emailNotificationService = new NotificationService(emailSender);
        smsNotificationService = new NotificationService(smsSender);
    }

    @Test
    @DisplayName("이메일 알림 서비스는 이메일 어댑터를 통해 메시지를 전송한다.")
    void emailNotificationServiceSendMessageSuccessTest() throws Exception {

        //given
        String message = "중요 알림";

        //when
        String response = emailSender.sendMessage(message);

        //then
        assertThat(response).isEqualTo("email 전송중요 알림");
    }

    @Test
    @DisplayName("이메일 알림 서비스는 이메일 어댑터를 통해 메시지를 전송한다.")
    void smsNotificationServiceSendMessageSuccessTest() throws Exception {

        //given
        String message = "중요 알림";

        //when
        String response = smsSender.sendMessage(message);

        //then
        assertThat(response).isEqualTo("sms 전송중요 알림");
    }
}
