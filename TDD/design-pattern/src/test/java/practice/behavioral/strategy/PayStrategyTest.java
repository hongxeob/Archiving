package practice.behavioral.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import practice.behavioral.strategy.details.PayPalDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PayStrategyTest {
    private PayStrategy payStrategy;

    @BeforeEach
    public void setUp() {
        payStrategy = new PayByPayPal();
    }

    @Test
    public void testPayWithoutSignIn() {
        // When: 로그인하지 않은 상태에서 결제 시도
        boolean result = payStrategy.pay(100);

        // Then: 결제 실패
        assertFalse(result);
    }

    @Test
    @DisplayName("Pay전략중 페이팔로 결제를 할 수 있다.")
    void payPayPalSuccessTest() throws Exception {
        PayPalDetails payPalDetails = new PayPalDetails("amanda@ya.com", "amanda1985");
        // Given: 올바른 크레덴셜로 로그인
        payStrategy.collectPaymentDetails(payPalDetails);

        // When: 결제 시도
        boolean result = payStrategy.pay(100);

        // Then: 결제 성공
        assertTrue(result);
    }

    @Test
    @DisplayName("페이팔로 결제시 계정 오류시 결제가 되지 않는다.")
    void payCardSuccessTest() throws Exception {
        PayPalDetails payPalDetails = new PayPalDetails("invalid@ya.com", "invalid_password");
        //given
        payStrategy.collectPaymentDetails(payPalDetails);

        //when
        boolean result = payStrategy.pay(100);

        //then
        assertThat(result).isEqualTo(false);
    }
}
