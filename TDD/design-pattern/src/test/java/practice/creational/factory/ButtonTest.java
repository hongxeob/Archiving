package practice.creational.factory;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.logging.Logger;


class ButtonTest {
    @Mock
    private Logger log;

    @Test
    void testButton() {

        //given
        Button htmlButton = new HTMLButton();

        //when
        htmlButton.render();

        //then
    }

}
