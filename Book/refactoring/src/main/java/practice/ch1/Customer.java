package practice.ch1;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String name;
    private List<Rental> rentals = new ArrayList<>();

    public Customer(String name) {
        this.name = name;
    }

    public void addRental(Rental rental) {
        rentals.add(rental);
    }

    public String getName() {
        return name;
    }

    public String statement() {
        double totalAmount = 0;
        String result = getName() + " 고객님의 대여기록 \n";
        int frequentRenterPoints = 0;
        for (int i = 0; i < rentals.size(); i++) {
            Rental rental = rentals.get(i);

            // 적립 포인트 1 증가
            frequentRenterPoints += rental.getFrequentRenterPoints();

            // 대여하는 비디오 정보와 대여료 출력
            result += "\t" + rental.getMovie().getTitle() + "\t" + String.valueOf(rental.getCharge()) + "\n";

            //현재까지 누적된 총 대여료
            totalAmount += rental.getCharge();

        }

        // 푸터 행 추가
        result += "누적 대여료: " + String.valueOf(totalAmount) + "\n";
        result += "적립 포인트: " + String.valueOf(frequentRenterPoints);
        return result;
    }

}
