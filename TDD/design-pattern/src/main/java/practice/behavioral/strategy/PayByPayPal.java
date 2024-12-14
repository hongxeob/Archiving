package practice.behavioral.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PayByPayPal implements PayStrategy {
    private static final Map<String, String> DATA_BASE = new HashMap<>();
    private boolean signedIn;
    private Logger logger = Logger.getLogger(PayByPayPal.class.getName());

    static {
        DATA_BASE.put("amanda1985", "amanda@ya.com");
        DATA_BASE.put("qwerty", "john@amazon.eu");
    }

    @Override
    public boolean pay(int paymentAmount) {
        if (signedIn) {
            logger.info("Paying " + paymentAmount + " using PayPal");
            return true;
        }
        return false;
    }

    @Override
    public void collectPaymentDetails(String email, String password) {
        if (verify(email, password)) {
            logger.info("정보 확인에 성공했습니다.");
        } else {
            logger.warning("잘못된 이메일 or 비밀번호 입니다!");
        }
    }

    private boolean verify(String email, String password) {
        setSignedIn(email.equals(DATA_BASE.get(password)));
        return signedIn;
    }

    private void setSignedIn(boolean signedIn) {
        this.signedIn = signedIn;
    }
}
