package practice.structural.compostie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class DirectoryTest {
    @DisplayName("디렉토리를 생성할 수 있다.")
    @Test
    void createDirectoryTest() throws Exception {
        Directory directory = new Directory("name");

        assertThat(directory.getName()).isEqualTo("name");
    }

    @Test
    @DisplayName("디렉토리의 이름이 null이거나 비어있으면 예외 발생")
    void createDirectoryFailTest() throws Exception {

        //given -> when -> then
        assertThatThrownBy(() -> new Directory(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must not be null or empty");
    }

    @Test
    @DisplayName("빈 디렉토리의 크기는 0이어야 한다.")
    void emptySizeTest() throws Exception {

        //given
        Directory directory = new Directory("empty");

        //when
        long size = directory.getSize();

        //then
        assertThat(size).isEqualTo(0);
    }

    @Test
    @DisplayName("파일 추가시 디렉토리에 정상적으로 추가되어야한다.")
    void addComponentTest() throws Exception {

        //given
        Directory directory = new Directory("name");
        File file = new File("file1", 100);

        //when
        directory.addComponent(file);

        //then
        assertThat(directory.getSize()).isEqualTo(100);
    }

    @Test
    @DisplayName("여러 파일 추가시 전체 크기가 올바르게 계산 되어야한다.")
    void multipleSizeTest() throws Exception {

        //given
        Directory parenDirectory = new Directory("parent");
        Directory childDirectory = new Directory("child");
        File file1 = new File("file1", 100);
        File file2 = new File("file2", 200);

        //when
        childDirectory.addComponent(file1);
        childDirectory.addComponent(file2);
        parenDirectory.addComponent(childDirectory);

        //then
        assertThat(parenDirectory.getSize()).isEqualTo(300);
    }

    @Test
    @DisplayName("중첩 디렉토리의 전체 크기가 계산된다.")
    void nestedDirectorySizeTest() throws Exception {

        //given
        Directory parenDirectory = new Directory("parent");
        Directory childDirectory = new Directory("child");
        File file1 = new File("file1", 100);
        File file2 = new File("file2", 100);

        //when
        childDirectory.addComponent(file1);
        childDirectory.addComponent(file2);
        parenDirectory.addComponent(childDirectory);

        //then
        assertThat(parenDirectory.getSize()).isEqualTo(200);
    }

    @Test
    @DisplayName("파일 삭제시 디렉토리에서 정상적으로 삭제 된다.")
    void removeComponentTest() throws Exception {

        //given
        Directory directory = new Directory("directory");
        File file = new File("file1", 100);
        directory.addComponent(file);

        //when
        directory.removeComponent(file);

        //then
        assertThat(directory.getSize()).isEqualTo(0);
    }

    @Test
    @DisplayName("디렉토리 이름이 null이거나 비어있으면 예외")
    void invalidDirectoryNameTest() throws Exception {

        //given -> when -> then
        assertAll(
                () -> assertThatThrownBy(() -> new Directory(null))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("Name must not be null or empty"),
                () -> assertThatThrownBy(() -> new Directory(""))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("Name must not be null or empty")
        );
    }
}
