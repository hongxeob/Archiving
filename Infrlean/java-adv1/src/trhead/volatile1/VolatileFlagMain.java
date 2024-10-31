package trhead.volatile1;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class VolatileFlagMain {
	public static void main(String[] args) {
		MyTask myTask = new MyTask();
		Thread thread = new Thread(myTask, "work");
		log("flag = " + myTask.flag);
		thread.start();

		sleep(3000);
		log("flag를 false로 변경 시도");
		myTask.flag = false;
		log("flag = " + myTask.flag);
		log("main 종료");
	}

	static class MyTask implements Runnable {
		// volatile은 값을 읽거나 쓸 때 캐시에 접근하는게 아니라 항상 메인 메모리에 접근한다 -> 그래서 성능이 느려진다.
		volatile boolean flag = true;

		@Override
		public void run() {
			log("task 시작");
			while (flag) {
				//flag가 false로 변하면 탈출
			}
			log("task 종료");
		}
	}
}
