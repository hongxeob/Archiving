package practice.query_and_stream_test.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	List<User> findByNameIn(List<String> names);

	List<User> findByEmail(String email);
}
