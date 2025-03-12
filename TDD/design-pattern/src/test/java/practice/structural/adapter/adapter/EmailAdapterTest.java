package practice.structural.adapter.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import practice.structural.adapter.adaptee.EmailService;

import static org.assertj.core.api.Assertions.assertThat;

class EmailAdapterTest {

    private EmailService emailService;
    private EmailAdapter emailAdapter;

    @BeforeEach
    void setUp() {
        emailService = new EmailService();
        emailAdapter = new EmailAdapter(emailService);
    }

    @Test
    @DisplayName("이메일 어댑터로 메일을 발송할 수 있다.")
    void sendEmailSuccessTest() {
        // given
        String message = "메시지";

        // when
        String result = emailAdapter.sendMessage(message);

        // then
        assertThat(result).isEqualTo("email 전송메시지");
    }

}
