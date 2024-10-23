package trhead.start;

public class BadThreadMain {
	public static void main(String[] args) {
		System.out.println(Thread.currentThread().getName() + ": main() start");

		HelloThread helloThread = new HelloThread();

		System.out.println(Thread.currentThread().getName() + ": start() 호출 전");

		// 주의! `run()` 메서드가 아니라 반드시 `start()` 메서드를 호출해야 한다. 그래야 별도의 스레드에서 `run()` 코드가 실행된다.
		helloThread.run();

		System.out.println(Thread.currentThread().getName() + ": start() 호출 후");
		System.out.println(Thread.currentThread().getName() + ": main() end");

	}
}
