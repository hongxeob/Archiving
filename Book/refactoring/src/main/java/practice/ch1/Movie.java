package practice.ch1;

public class Movie {
    public static final int CHILDREN = 2;
    public static final int REGULAR = 0;
    public static final int NEW_RELEASE = 1;

    private String title;
    private int priceCode;

    public Movie(String title, int priceCode) {
        this.title = title;
        this.priceCode = priceCode;
    }

    public String getTitle() {
        return title;
    }

    public int getPriceCode() {
        return priceCode;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPriceCode(int priceCode) {
        this.priceCode = priceCode;
    }

    public double getCharge(int daysRented) {
        double result = 0;
        switch (getPriceCode()) {
            case REGULAR -> {
                result += 2;
                if (daysRented > 2) {
                    result += (daysRented - 2) * 1.5;
                }

            }
            case CHILDREN -> {
                result += 1.5;
                if (daysRented > 3) {
                    result += (daysRented - 3) * 1.5;
                }
            }
            case NEW_RELEASE -> result += daysRented * 3;
        }
        return result;
    }

    public int getFrequentRenterPoints(int daysRented) {
        //최신물을 2일이상 대여하면 보너스 포인트 지급
        if ((getPriceCode() == Movie.NEW_RELEASE) && daysRented > 1) {
            return 2;
        }
        return 1;
    }
}
