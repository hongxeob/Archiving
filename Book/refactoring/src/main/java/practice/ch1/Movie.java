package practice.ch1;

import practice.ch1.price.ChildrensPrice;
import practice.ch1.price.NewReleasePrice;
import practice.ch1.price.Price;
import practice.ch1.price.RegularPrice;

public class Movie {
    public static final int CHILDREN = 2;
    public static final int REGULAR = 0;
    public static final int NEW_RELEASE = 1;

    private String title;
    private int priceCode;
    private Price price;

    public Movie(String title, int priceCode) {
        this.title = title;
        setPriceCode(priceCode);
    }

    public String getTitle() {
        return title;
    }

    public int getPriceCode() {
        return price.getPriceCode();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPriceCode(int priceCode) {
        switch (priceCode) {
            case REGULAR -> price = new RegularPrice();
            case CHILDREN -> price = new ChildrensPrice();
            case NEW_RELEASE -> price = new NewReleasePrice();
            default -> throw new IllegalArgumentException("가격 코드가 잘못되었습니다.");
        }
    }

    public double getCharge(int daysRented) {
        return price.getCharge(daysRented);
    }

    public int getFrequentRenterPoints(int daysRented) {
        //최신물을 2일이상 대여하면 보너스 포인트 지급
        if ((getPriceCode() == Movie.NEW_RELEASE) && daysRented > 1) {
            return 2;
        } else {
            return 1;
        }
    }
}
