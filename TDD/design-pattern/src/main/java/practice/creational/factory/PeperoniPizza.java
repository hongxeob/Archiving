package practice.creational.factory;

class PeperoniPizza implements Pizza {
    @Override
    public double getPrice() {
        return 20000;
    }

    @Override
    public String getName() {
        return "peperoni";
    }
}
