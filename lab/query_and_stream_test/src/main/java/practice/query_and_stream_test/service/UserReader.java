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
	public List<User> getUsersByEmails(List<String> emails) {
		return userRepository.findByEmailIn(emails);
	}

	@Timer
	public List<User> getUsersByName(String name) {
		return userRepository.findByName(name);
	}

	@Timer
	public List<User> getUsersByEmailsUseFilter(List<String> names) {
		return userRepository.findAll()
			.stream()
			.filter(user -> user.getEmail().equals("이메일"))
			.toList();
	}

	@Timer
	public List<User> getUsersByNameUseDB(String name) {
		return userRepository.findAll()
			.stream()
			.filter(user -> user.getName().equals("이름"))
			.toList();
	}
}
