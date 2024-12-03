package practice.ch1.price;

import practice.ch1.Movie;

public class ChildrensPrice extends Price {
    @Override
    public int getPriceCode() {
        return Movie.CHILDREN;
    }

    @Override
    public double getCharge(int daysRented) {
        double thisAmount = 1.5;
        if (daysRented > 3) thisAmount += (daysRented - 3) * 1.5;
        return thisAmount;
    }
}
