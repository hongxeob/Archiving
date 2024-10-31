package trhead.control.printer;

import static util.MyLogger.log;

import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MyPrinter4 {
	public static void main(String[] args) {
		Printer printer = new Printer();
		Thread thread = new Thread(printer);
		thread.start();

		Scanner userInput = new Scanner(System.in);
		while (true) {
			log("프린터할 문서를 입력하세요. 종로 (q): ");
			String input = userInput.nextLine();
			if (input.equals("q")) {
				thread.interrupt();
				break;
			}
			printer.addJob(input);
		}
	}

	static class Printer implements Runnable {
		Queue<String> jobQueue = new ConcurrentLinkedQueue<>();

		@Override
		public void run() {
			while (!Thread.interrupted()) {
				// CPU가 계속 이 로직을 확인한다.
				if (jobQueue.isEmpty()) {
					Thread.yield();
					continue;
				}
				try {
					String job = jobQueue.poll();
					log("출력 시작: " + job + ", 대기 문서 : " + jobQueue);

					Thread.sleep(3000);
					log("출력 완료: " + job);
				} catch (InterruptedException e) {
					log("인터럽트 발생!!");
					break;
				}
			}
			log("프린터 종료");
		}

		public void addJob(String input) {
			jobQueue.offer(input);
		}
	}
}
