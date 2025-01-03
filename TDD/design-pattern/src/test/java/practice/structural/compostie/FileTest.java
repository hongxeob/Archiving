package practice.structural.compostie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileTest {

    @Test
    @DisplayName("파일을 생성할 수 있다.")
    void createFileTest() throws Exception {

        File file = new File("name", 100);

        assertThat(file.name()).isEqualTo("name");
    }

    @ParameterizedTest
    @DisplayName("파일의 이름이 null이거나 비어있으면 예외 발생")
    @NullAndEmptySource
    void invalid_name_createFileTest_fail(String name) throws Exception {
        assertThatThrownBy(() -> new File("", 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must not be null or empty");
    }

    @Test
    @DisplayName("파일의 크기를 볼 수 있다.")
    void showDetailsTest() throws Exception {

        File file = new File("name", 100);

        assertThat(file.getSize()).isEqualTo(100);
    }
}
