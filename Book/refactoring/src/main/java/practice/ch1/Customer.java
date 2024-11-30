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
        int frequentRenterPoints = 0;
        String result = getName() + " 고객님의 대여기록 \n";

        for (int i = 0; i < rentals.size(); i++) {
            double thisAmount = 0;
            Rental rental = rentals.get(i);

            //각 비디오별로 대여료 계산
            thisAmount = amountFor(rental);

            // 적립 포인트 1 증가
            frequentRenterPoints++;

            //최신물을 2일이상 대여하면 보너스 포인트 지금
            if ((rental.getMovie().getPriceCode() == Movie.NEW_RELEASE) && rental.getDaysRented() > 1) {
                frequentRenterPoints++;
            }

            // 대여하는 비디오 정보와 대여료 출력
            result += "\t" + rental.getMovie().getTitle() + "\t" + String.valueOf(thisAmount) + "\n";

            //현재까지 누적된 총 대여료
            totalAmount += thisAmount;

        }

        // 푸터 행 추가
        result += "누적 대여료: " + String.valueOf(totalAmount) + "\n";
        result += "적립 포인트: " + String.valueOf(frequentRenterPoints);
        return result;
    }

    private double amountFor(Rental rental) {
        double thisAmount = 0;
        switch (rental.getMovie().getPriceCode()) {
            case Movie.REGULAR -> {
                thisAmount += 2;
                if (rental.getDaysRented() > 2) {
                    thisAmount += (rental.getDaysRented() - 2) * 1.5;
                }

            }
            case Movie.CHILDREN -> {
                thisAmount += 1.5;
                if (rental.getDaysRented() > 3) {
                    thisAmount += (rental.getDaysRented() - 3) * 1.5;
                }
            }
            case Movie.NEW_RELEASE -> thisAmount += rental.getDaysRented() * 3;
        }
        return thisAmount;
    }
}
