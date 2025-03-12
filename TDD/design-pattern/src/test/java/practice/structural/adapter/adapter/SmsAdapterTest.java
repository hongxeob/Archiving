package practice.structural.adapter.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import practice.structural.adapter.adaptee.SMSService;

import static org.assertj.core.api.Assertions.assertThat;

class SmsAdapterTest {


    private SMSService smsService;
    private SmsAdapter smsAdapter;

    @BeforeEach
    void setUp() {
        smsService = new SMSService();
        smsAdapter = new SmsAdapter(smsService);
    }

    @Test
    @DisplayName("sms로 알림 전송이 가능하다.")
    void smsSenderSuccessTest() throws Exception {

        // given
        String message = "메시지";

        // when
        String result = smsAdapter.sendMessage(message);

        // then
        assertThat(result).isEqualTo("sms 전송메시지");

    }

}
