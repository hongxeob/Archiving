package trhead.sync.test;


public class SyncTest1BadMain {
	public static void main(String[] args) throws InterruptedException {
		Counter counter = new Counter();
		Runnable task = new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 10000; i++) {
					counter.increment();
				}
			}
		};
		Thread thread1 = new Thread(task);
		Thread thread2 = new Thread(task);

		thread1.start();
		thread2.start();

		thread1.join();
		thread2.join();

		System.out.println("결과: " + counter.getCount());
	}

	static class Counter {
		private int count = 0;

		public void increment() {
			count = count + 1;
		}

		public int getCount() {
			return count;
		}
	}
}
