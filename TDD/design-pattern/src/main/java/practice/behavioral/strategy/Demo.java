package practice.behavioral.strategy;

import practice.behavioral.strategy.details.CreditCardDetails;
import practice.behavioral.strategy.details.PayPalDetails;
import practice.behavioral.strategy.details.PaymentDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Demo {
    private static Map<Integer, Integer> priceOnProducts = new HashMap<>();
    private static Order order = new Order();
    private static PayStrategy strategy;
    private static PaymentDetails paymentDetails;

    static {
        priceOnProducts.put(1, 2200);
        priceOnProducts.put(2, 1850);
        priceOnProducts.put(3, 1100);
        priceOnProducts.put(4, 890);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (!order.isClosed()) {
            int cost;

            String continueChoice;
            do {
                System.out.print("장바구니에 추가할 상품을 선택하세요:" + "\n" +
                        "1 - 마더보드 (2200원)" + "\n" +
                        "2 - CPU (1850원)" + "\n" +
                        "3 - HDD (1100원)" + "\n" +
                        "4 - 메모리 (890원)" + "\n");

                int choice = Integer.parseInt(reader.readLine());
                cost = priceOnProducts.get(choice);
                order.setTotalCost(cost);

                System.out.print("계속 쇼핑하시겠습니까? (Y/N): ");
                continueChoice = reader.readLine();
            } while (continueChoice.equalsIgnoreCase("Y"));

            if (strategy == null) {
                System.out.println("\n결제 방법을 선택하세요:");
                System.out.println("1 - PayPal");
                System.out.println("2 - 신용카드");

                String paymentMethod = reader.readLine();

                // 결제 전략 생성
                if (paymentMethod.equals("1")) {
                    strategy = new PayByPayPal();
                    System.out.println("이메일을 입력하세요: ");
                    String email = reader.readLine();
                    System.out.println("비밀번호를 입력하세요: ");
                    String password = reader.readLine();
                    paymentDetails = new PayPalDetails(email, password);
                } else {
                    strategy = new PayByCreditCard();
                    System.out.println("카드번호를 입력하세요: ");
                    String cardNumber = reader.readLine();
                    System.out.println("카드 만료일을 입력하세요 (MM/YY): ");
                    String date = reader.readLine();
                    System.out.println("CVV를 입력하세요: ");
                    String cvv = reader.readLine();
                    paymentDetails = new CreditCardDetails(cardNumber, date, cvv);
                }
            }

            // 주문 처리
            order.processOrder(strategy, paymentDetails);
            boolean paymentResult = strategy.pay(order.getTotalCost());

            if (paymentResult) {
                System.out.println("결제가 완료되었습니다. 금액: " + order.getTotalCost() + "원");
            } else {
                System.out.println("결제 실패!");
            }

            order.setClosed();
        }
    }
}
