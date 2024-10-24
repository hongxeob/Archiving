package trhead.control;

import static util.MyLogger.log;

import trhead.start.HelloRunnable;

public class ThreadInfoMain {
	public static void main(String[] args) {
		// main 스레드
		Thread mainThread = Thread.currentThread();
		log("mainThread => " + mainThread);
		log("mainThread.threadId() => " + mainThread.threadId());
		log("mainThread.getName() => " + mainThread.getName());
		log("mainThread.getPriority() => " + mainThread.getPriority());
		log("mainThread.getThreadGroup() => " + mainThread.getThreadGroup());
		log("mainThread.getState() => " + mainThread.getState());

		// myThread 스레드
		Thread myThread = new Thread(new HelloRunnable(), "myThread");
		log("myThread => " + myThread);
		log("myThread.threadId() => " + myThread.threadId());
		log("myThread.getName() => " + myThread.getName());
		log("myThread.getPriority() => " + myThread.getPriority());
		log("myThread.getThreadGroup() => " + myThread.getThreadGroup());
		log("myThread.getState() => " + myThread.getState());
	}
}
