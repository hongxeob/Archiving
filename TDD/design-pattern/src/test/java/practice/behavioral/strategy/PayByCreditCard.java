package practice.behavioral.strategy;

import practice.behavioral.strategy.details.CreditCardDetails;
import practice.behavioral.strategy.details.PaymentDetails;

import java.util.logging.Logger;

public class PayByCreditCard implements PayStrategy {
    private CreditCard card;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public boolean pay(int paymentAmount) {
        if (cardIsPresent()) {
            logger.info("결제액 => " + paymentAmount);
            card.updateAmount(card.getAmount() - paymentAmount);
            logger.info("잔여 금액 => " + card.getAmount());
            return true;
        }
        return false;
    }

    @Override
    public void collectPaymentDetails(PaymentDetails paymentDetails) {
        if (!(paymentDetails instanceof CreditCardDetails)) {
            throw new IllegalArgumentException("Payment details must be of type CreditCardDetails");
        }
        CreditCardDetails creditCardDetails = (CreditCardDetails) paymentDetails;
        card = new CreditCard(creditCardDetails.getNumber(), creditCardDetails.getDate(), creditCardDetails.getCsv());
        logger.info("카드 설정 성공");
        // 카드 유효성 검증등....
    }

    private boolean cardIsPresent() {
        return card != null;
    }
}
