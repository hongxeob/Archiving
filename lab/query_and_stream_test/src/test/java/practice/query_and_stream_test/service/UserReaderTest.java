package practice.query_and_stream_test.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;

import practice.query_and_stream_test.domain.User;
import practice.query_and_stream_test.domain.UserRepository;

@SpringBootTest
class UserReaderTest {

	@Autowired
	private UserReader userReader;

	@Autowired
	private UserRepository userRepository;

	private static final int COUNT = 10000;

	List<User> userList;
	List<String> emails;

	@BeforeEach
	void setUp() {
		FixtureMonkey sut = FixtureMonkey.builder()
			.objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
			.build();

		userList = sut.giveMe(User.class, COUNT);
		userList.forEach(user -> user.updateEmail("이메일"));

		emails = userList.stream()
			.map(User::getEmail)
			.toList();

		System.out.println("============ 데이터 insert ============");
		userList.forEach(user -> System.out.println(user.getEmail()));
		userRepository.saveAll(userList);
	}

	@AfterEach
	void tearDown() {
		userRepository.deleteAll();
	}

	@Test
	void findAllTest() throws Exception {
		//given
		System.out.println("============ findAll() ============");

		//when
		List<User> users = userReader.getUsers();

		//then
		assertThat(users.size()).isEqualTo(COUNT);
	}

	@Test
	void findAllByEmails() throws Exception {
		//given
		System.out.println("============ findAllByEmails() ============");

		//when
		List<User> foundByEmails = userReader.getUsersByEmails(emails);

		//then
		assertThat(foundByEmails.size()).isEqualTo(COUNT);
	}

	@Test
	void findAllByName() throws Exception {
		//given
		System.out.println("============ findAllByName() ============");

		//when
		userReader.getUsersByName("이메일");

		//then

	}
}
