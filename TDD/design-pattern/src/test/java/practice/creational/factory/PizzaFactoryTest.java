package practice.creational.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PizzaFactoryTest {

    private PizzaFactory pizzaFactory = new PizzaFactory(this);

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


}
