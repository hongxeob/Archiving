package practice.query_and_stream_test.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import practice.query_and_stream_test.aop.Timer;
import practice.query_and_stream_test.domain.User;
import practice.query_and_stream_test.domain.UserRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserReader {
	private final UserRepository userRepository;

	@Timer
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Timer
	public List<User> getUsersByNames(List<String> names) {
		return userRepository.findByNameIn(names);
	}

	@Timer
	public List<User> getUsersByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Timer
	public List<User> getUsersByNamesUseFilter(List<String> names) {
		return userRepository.findAll()
			.stream()
			.filter(user -> names.contains(user.getName()))
			.toList();
	}

	@Timer
	public List<User> getUsersByEmailUseDB(String email) {
		return userRepository.findAll()
			.stream()
			.filter(user -> user.getEmail().equals(email))
			.toList();
	}
}
