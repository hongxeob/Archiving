package trhead.start;

import static util.MyLogger.log;

public class ManyThreadMainV2 {
	public static void main(String[] args) {
		log("main() start");

		// 익명 클래스 사용
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				log("run()");
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();

		log("main() end");
	}
}
