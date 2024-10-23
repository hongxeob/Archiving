package trhead.start;

public class DaemonThreadMain {
	public static void main(String[] args) {
		System.out.println(Thread.currentThread().getName() + ": main() start");

		/**
		 * setDaemon(true)
		 * -> 유일한 user 스레드인 `main` 스레드가 종료되면서 자바 프로그램도 바로 종료된다.
		 * -> 따라서 `run() end` 가 출력되기 전에 프로그램이 종료된다.
		 *
		 * setDaemon(false)
		 * -> main 스레드가 종료되어도, user 스레드인 Thread-0이 종료될 때까지 (10초) 자바 프로그램을 종료하지 않는다.
		 * -> 따라서 Thread-0: run() end` 가 출력된다.
		 * -> user 스레드인 `main` 스레드와 `Thread-0` 스레드가 모두 종료되면서 자바 프로그램도 종료된다.
		 * */
		DaemonThread daemonThread = new DaemonThread();
		daemonThread.setDaemon(true); // 데몬 스레드 여부
		daemonThread.start();

		System.out.println(Thread.currentThread().getName() + ": main() end");
	}

	static class DaemonThread extends Thread {
		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName() + ": run() start");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			System.out.println(Thread.currentThread().getName() + ": run() end");
		}
	}
}
