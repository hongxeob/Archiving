package trhead.start;

public class HelloRunnableMain {
	public static void main(String[] args) {
		System.out.println(Thread.currentThread().getName() + ": main() start");

		HelloRunnable runnable = new HelloRunnable();
		Thread thread = new Thread(runnable);
		thread.start();

		System.out.println(Thread.currentThread().getName() + ": main() end");
	}
}
