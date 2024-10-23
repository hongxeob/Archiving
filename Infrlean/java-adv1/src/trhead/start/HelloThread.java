package trhead.start;

public class HelloThread extends Thread {

	@Override
	public void run() {
		System.out.println("Thread Name => " + Thread.currentThread().getName() + " : run()");
	}
}
