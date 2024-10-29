package trhead.control.interupt;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class ThreadStopMainV4 {

	public static void main(String[] args) {
		MyTask task = new MyTask();
		Thread thread = new Thread(task, "work");
		thread.start();

		sleep(100);
		log("작업 중단 지시 thread.interrupt()");
		thread.interrupt();
		log("work 스레드 인터럽트 상태1 => " + thread.isInterrupted());
	}

	static class MyTask implements Runnable {
		@Override
		public void run() {

			// 스레드가 인터럽트 상태라면 true 반환 후 해당 스레드의 인터럽트 상태를 false로 변경.
			// 스레드가 인터럽트 상태가 아니라면 false 반환 후, 해당 스레드의 인터럽트 상태를 변경하지 않는다.
			while (!Thread.interrupted()) {
				log("작업 중");
			}
			log("work 스레드 인터럽트 상태2 => " + Thread.currentThread().isInterrupted());

			try {
				log("자원 정리 시도");
				Thread.sleep(1000);
				log("자원 정리 완료");
			} catch (InterruptedException e) {
				log("자원 정리 실패 - 자원 정리 중 인터럽트 발생");
				log("work 스레드 인터럽트 상태3 = " + Thread.currentThread().isInterrupted());
			}
			log("작업 종료");
		}
	}
}
