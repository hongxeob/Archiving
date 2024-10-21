package practice.query_and_stream_test.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import practice.query_and_stream_test.service.UserReader;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final UserReader userReader;

	@GetMapping
	public String getUsers() {
		long startTime = System.nanoTime();

		userReader.getUsers();

		long endTime = System.nanoTime();
		long duration = endTime - startTime;

		double durationInMs = duration / 1_000_000.0;

		return String.format("=== getUsers 실행 시간: %.3f ms ===", durationInMs);
	}

	@GetMapping("/db/names")
	public String getUsersByNamesUseDB() {
		long startTime = System.nanoTime();

		userReader.getUsersByNames(List.of("이름"));

		long endTime = System.nanoTime();
		long duration = endTime - startTime;

		double durationInMs = duration / 1_000_000.0;

		return String.format("getUsers 실행 시간: %.3f ms", durationInMs);
	}

	@GetMapping("/db/email")
	public String getUsersByEmailUseDB() {
		long startTime = System.nanoTime();

		userReader.getUsersByEmail("이메일");

		long endTime = System.nanoTime();
		long duration = endTime - startTime;

		double durationInMs = duration / 1_000_000.0;

		return String.format("=== 실행 시간: %.3f ms ===", durationInMs);
	}


	@GetMapping("/stream/names")
	public String getUsersByNamesUseFilter() {
		long startTime = System.nanoTime();

		userReader.getUsersByNamesUseFilter(List.of("이름"));

		long endTime = System.nanoTime();
		long duration = endTime - startTime;

		double durationInMs = duration / 1_000_000.0;

		return String.format("=== 실행 시간: %.3f ms ===", durationInMs);
	}


	@GetMapping("/stream/email")
	public String getUsersByEmailUseFilter() {
		long startTime = System.nanoTime();

		userReader.getUsersByEmailUseDB("이메일");

		long endTime = System.nanoTime();
		long duration = endTime - startTime;

		double durationInMs = duration / 1_000_000.0;

		return String.format("=== 실행 시간: %.3f ms ===", durationInMs);
	}
}
