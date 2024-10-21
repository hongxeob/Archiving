package practice.query_and_stream_test.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import practice.query_and_stream_test.domain.User;
import practice.query_and_stream_test.service.UserReader;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final UserReader userReader;

	@GetMapping
	public String getUsers() {
		long startTime = System.nanoTime();

		List<Long> userIds = userReader.getUsers()
			.stream()
			.map(User::getId)
			.collect(Collectors.toList());

		double durationInMs = getDurationInMs(startTime);

		String idsList = getIdsList(userIds);

		return String.format("getUsers 실행 시간: %.3f ms\n사용자 ID 목록: [%s]", durationInMs, idsList);
	}

	@GetMapping("/db/names")
	public String getUsersByNamesUseDB() {
		long startTime = System.nanoTime();

		List<Long> userIds = userReader.getUsersByNames(List.of("이름1", "이름2", "이름3"))
			.stream()
			.map(User::getId)
			.toList();

		double durationInMs = getDurationInMs(startTime);

		String idsList = getIdsList(userIds);

		return String.format("getUsers 실행 시간: %.3f ms\n사용자 ID 목록: [%s]", durationInMs, idsList);
	}

	@GetMapping("/db/email")
	public String getUsersByEmailUseDB() {
		long startTime = System.nanoTime();

		List<Long> userIds = userReader.getUsersByEmail("이메일")
			.stream()
			.map(User::getId)
			.collect(Collectors.toList());

		double durationInMs = getDurationInMs(startTime);

		String idsList = getIdsList(userIds);

		return String.format("getUsers 실행 시간: %.3f ms\n사용자 ID 목록: [%s]", durationInMs, idsList);
	}

	@GetMapping("/stream/names")
	public String getUsersByNamesUseFilter() {
		long startTime = System.nanoTime();

		List<Long> userIds = userReader.getUsersByNamesUseFilter(List.of("이름1", "이름2", "이름3"))
			.stream()
			.map(User::getId)
			.collect(Collectors.toList());

		double durationInMs = getDurationInMs(startTime);

		String idsList = getIdsList(userIds);

		return String.format("getUsers 실행 시간: %.3f ms\n사용자 ID 목록: [%s]", durationInMs, idsList);
	}

	@GetMapping("/stream/email")
	public String getUsersByEmailUseFilter() {
		long startTime = System.nanoTime();

		List<Long> userIds = userReader.getUsersByEmailUseDB("이메일")
			.stream()
			.map(User::getId)
			.collect(Collectors.toList());

		double durationInMs = getDurationInMs(startTime);

		String idsList = getIdsList(userIds);

		return String.format("getUsers 실행 시간: %.3f ms\n사용자 ID 목록: [%s]", durationInMs, idsList);
	}

	private String getIdsList(List<Long> userIds) {
		String idsList = userIds.stream()
			.map(Object::toString)
			.collect(Collectors.joining(", "));
		return idsList;
	}

	private static double getDurationInMs(long startTime) {
		long endTime = System.nanoTime();
		long duration = endTime - startTime;

		double durationInMs = duration / 1_000_000.0;
		return durationInMs;
	}
}
