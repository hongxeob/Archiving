package practice.behavioral.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import practice.behavioral.strategy.details.CreditCardDetails;
import practice.behavioral.strategy.details.PayPalDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PayStrategyTest {
    private PayStrategy payPalStrategy;
    private PayStrategy cardStrategy;

    @BeforeEach
    public void setUp() {
        payPalStrategy = new PayByPayPal();
        cardStrategy = new PayByCreditCard();
    }

    @Test
    public void testPayWithoutSignIn() {
        // When: 로그인하지 않은 상태에서 결제 시도
        boolean result = payPalStrategy.pay(100);

        // Then: 결제 실패
        assertFalse(result);
    }

    @Test
    @DisplayName("Pay전략중 페이팔로 결제를 할 수 있다.")
    void payPayPalSuccessTest() throws Exception {
        // Given: 올바른 크레덴셜로 로그인
        PayPalDetails payPalDetails = new PayPalDetails("amanda@ya.com", "amanda1985");
        payPalStrategy.collectPaymentDetails(payPalDetails);

        // When: 결제 시도
        boolean result = payPalStrategy.pay(100);

        // Then: 결제 성공
        assertTrue(result);
    }

    @Test
    @DisplayName("페이팔로 결제시 계정 오류라면 결제가 되지 않는다.")
    void payPayPalFailTest_invalidCredential() throws Exception {
        //given
        PayPalDetails payPalDetails = new PayPalDetails("invalid@ya.com", "invalid_password");
        payPalStrategy.collectPaymentDetails(payPalDetails);

        //when
        boolean result = payPalStrategy.pay(100);

        //then
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("Pay전략중 카드 등록에 실패하면 카드 결제가 불가하다.")
    void payCardFailTest_notPresentCard() throws Exception {

        //given -> when -> then
        assertThat(cardStrategy.pay(100)).isEqualTo(false);
    }

    @Test
    @DisplayName("Pay전략중 카드가 유효할시 카드 결제에 성공한다.")
    void payCardSuccessTest() throws Exception {

        //given
        CreditCardDetails creditCardDetails = new CreditCardDetails("1234-1234-1234", "06-24", "123");
        cardStrategy.collectPaymentDetails(creditCardDetails);

        //when
        boolean paid = cardStrategy.pay(100);

        //then
        assertThat(paid).isEqualTo(true);
    }
}
