package practice.creational.factory;

class CheesePizza implements Pizza {
    @Override
    public double getPrice() {
        return 10000;
    }

    @Override
    public String getName() {
        return "cheese";
    }
}
