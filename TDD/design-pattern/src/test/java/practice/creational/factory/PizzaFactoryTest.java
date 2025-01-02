package practice.creational.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PizzaFactoryTest {

    private PizzaFactory pizzaFactory = new PizzaFactory();

    @Test
    @DisplayName("치즈 피자를 만들 수 있다.")
    void createCheesePizza() throws Exception {

        //given
        String type = "cheese";

        //when
        Pizza pizza = pizzaFactory.createPizza(type);

        //then
        assertThat(pizza instanceof CheesePizza).isEqualTo(true);
        assertThat(pizza.getPrice()).isEqualTo(10000);
        assertThat(pizza.getName()).isEqualTo("cheese");
    }

    @Test
    @DisplayName("페페로니 피자를 만들수 있다.")
    void createPeperoniPizza() throws Exception {

        //given
        String type = "peperoni";

        //when
        Pizza pizza = pizzaFactory.createPizza(type);

        //then
        assertThat(pizza.getPrice()).isEqualTo(20000);
        assertThat(pizza.getName()).isEqualTo("peperoni");
    }


    private interface Pizza {

        double getPrice();

        String getName();
    }

    private class CheesePizza implements Pizza {
        @Override
        public double getPrice() {
            return 10000;
        }

        @Override
        public String getName() {
            return "cheese";
        }
    }

    private class PizzaFactory {
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

    private class PeperoniPizza implements Pizza {
        @Override
        public double getPrice() {
            return 20000;
        }

        @Override
        public String getName() {
            return "peperoni";
        }
    }
}
