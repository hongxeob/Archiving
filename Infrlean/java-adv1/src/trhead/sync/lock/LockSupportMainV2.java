package trhead.sync.lock;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

import java.util.concurrent.locks.LockSupport;

public class LockSupportMainV2 {
	public static void main(String[] args) {
		Thread thread1 = new Thread(new ParkTest(), "Thread-1");
		thread1.start();

		sleep(100);
		log("Thread-1 state: " + thread1.getState());

	}

	static class ParkTest implements Runnable {

		@Override
		public void run() {
			log("park 시작");
			LockSupport.parkNanos(2000_000000); //parkNanos() 사용
			log("park 종료, state: " + Thread.currentThread().getState());
			log("인터럽트 상태 : " + Thread.currentThread().isInterrupted());
		}
	}
}
