package practice.ch1.price;

import practice.ch1.Movie;

public class RegularPrice extends Price {
    @Override
    public double getCharge(int daysRented) {
        double thisAmount = 2;
        if (daysRented > 2) {
            thisAmount += (daysRented - 2) * 1.5;
        }
        return thisAmount;
    }

    @Override
    public int getPriceCode() {
        return Movie.REGULAR;
    }
}
