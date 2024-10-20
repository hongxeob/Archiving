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

	@GetMapping("/db/emails")
	public String getUsersByEmailsUseDB() {
		long startTime = System.nanoTime();

		userReader.getUsersByEmails(List.of("이메일"));

		long endTime = System.nanoTime();
		long duration = endTime - startTime;

		double durationInMs = duration / 1_000_000.0;

		return String.format("getUsers 실행 시간: %.3f ms", durationInMs);
	}

	@GetMapping("/db/name")
	public String getUsersByNameUseDB() {
		long startTime = System.nanoTime();

		userReader.getUsersByName("이메일");

		long endTime = System.nanoTime();
		long duration = endTime - startTime;

		double durationInMs = duration / 1_000_000.0;

		return String.format("=== 실행 시간: %.3f ms ===", durationInMs);
	}


	@GetMapping("/stream/emails")
	public String getUsersByEmailsUseFilter() {
		long startTime = System.nanoTime();

		userReader.getUsersByEmailsUseFilter(List.of("이메일"));

		long endTime = System.nanoTime();
		long duration = endTime - startTime;

		double durationInMs = duration / 1_000_000.0;

		return String.format("=== 실행 시간: %.3f ms ===", durationInMs);
	}


	@GetMapping("/stream/name")
	public String getUsersByNameUseFilter() {
		long startTime = System.nanoTime();

		userReader.getUsersByNameUseDB("이메일");

		long endTime = System.nanoTime();
		long duration = endTime - startTime;

		double durationInMs = duration / 1_000_000.0;

		return String.format("=== 실행 시간: %.3f ms ===", durationInMs);
	}
}
