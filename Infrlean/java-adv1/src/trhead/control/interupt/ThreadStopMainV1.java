package trhead.control.interupt;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class ThreadStopMainV1 {

	public static void main(String[] args) {
		MyTask task = new MyTask();
		Thread thread = new Thread(task, "work");
		thread.start();

		sleep(4000);
		log("작업 중단 지시 runFlag = false");
		task.runFlag = false;
	}

	static class MyTask implements Runnable {
		// 특정 스레드의 작업을 중단하는 가장 쉬운 방법은 변수를 사용하는 것이다.
		volatile boolean runFlag = true;

		@Override
		public void run() {
			while (runFlag) {
				log("작업 중");
				sleep(3000);
			}
			log("자원 정리");
			log("작업 종료");
		}
	}
}
