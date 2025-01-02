package practice.creational.factory;

class PizzaFactory {
    public Pizza createPizza(String type) {
        switch (type) {
            case "cheese":
                return new CheesePizza();
            case "peperoni":
                return new PeperoniPizza();
            default:
                throw new IllegalArgumentException("알 수 없는 피자 종류입니다.");
        }

    }
}
