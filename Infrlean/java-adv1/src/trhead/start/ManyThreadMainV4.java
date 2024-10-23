package trhead.start;

import static util.MyLogger.log;

public class ManyThreadMainV4 {
	public static void main(String[] args) {
		log("main() start");

		// 람다
		Runnable runnable = () -> log("run()");
		Thread thread = new Thread(runnable);
		thread.start();

		log("main() end");
	}
}
