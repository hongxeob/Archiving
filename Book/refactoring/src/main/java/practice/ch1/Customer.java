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
        String result = getName() + " 고객님의 대여기록 \n";
        for (int i = 0; i < rentals.size(); i++) {
            Rental rental = rentals.get(i);

            // 대여하는 비디오 정보와 대여료 출력
            result += "\t" + rental.getMovie().getTitle() + "\t" + String.valueOf(rental.getCharge()) + "\n";
        }

        // 푸터 행 추가
        result += "누적 대여료: " + String.valueOf(getTotalCharge()) + "\n";
        result += "적립 포인트: " + String.valueOf(getTotalFrequentRenterPoints());
        return result;
    }

    private double getTotalCharge() {
        double totalAmount = 0;
        for (int i = 0; i < rentals.size(); i++) {
            Rental rental = rentals.get(i);
            totalAmount += rental.getCharge();
        }
        return totalAmount;
    }

    private int getTotalFrequentRenterPoints() {
        int frequentRenterPoints = 0;
        for (int i = 0; i < rentals.size(); i++) {
            Rental rental = rentals.get(i);
            frequentRenterPoints += rental.getFrequentRenterPoints();
        }
        return frequentRenterPoints;
    }
}
