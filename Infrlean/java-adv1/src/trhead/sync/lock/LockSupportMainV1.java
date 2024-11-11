package trhead.sync.lock;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

import java.util.concurrent.locks.LockSupport;

public class LockSupportMainV1 {
	public static void main(String[] args) {
		Thread thread1 = new Thread(new ParkTest(), "Thread-1");
		thread1.start();

		sleep(100);
		log("Thread-1 state: " + thread1.getState());

		log("main => unPark(Thread-1)");
//		LockSupport.unpark(thread1);
		thread1.interrupt();
	}

	static class ParkTest implements Runnable {

		@Override
		public void run() {
			log("park 시작");
			LockSupport.park();
			log("park 종료, state: " + Thread.currentThread().getState());
			log("인터럽트 상태 : " + Thread.currentThread().isInterrupted());
		}
	}
}
