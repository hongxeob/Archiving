package trhead.start;

import static util.MyLogger.log;

public class ManyThreadMainV1 {
	public static void main(String[] args) {
		log("main() start");

		MyRunnable runnable = new MyRunnable();
		Thread thread = new Thread(runnable);
		thread.start();

		log("main() end");
	}

	// 정적 중첩 클래스 사용
	static class MyRunnable implements Runnable {

		@Override
		public void run() {
			log("run()");
		}
	}
}
