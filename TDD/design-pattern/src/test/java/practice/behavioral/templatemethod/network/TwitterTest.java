package practice.behavioral.templatemethod.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TwitterTest {

    private Twitter twitter;
    private String id;
    private String password;
    private String message;

    @BeforeEach
    void setUp() {
        id = "id";
        password = "password";
        message = "message";
        twitter = new Twitter(id, password);
    }

    @Test
    @DisplayName("포스트를 할 수 있다.")
    void postSuccessTest() throws Exception {
        //when
        boolean result = twitter.post(message);

        //then
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("로그인을 할 수 있다.")
    void logInSuccessTest() throws Exception {
        //when
        boolean result = twitter.logIn(id, password);

        //then
        assertThat(result).isEqualTo(true);
    }

}
