package trhead.sync;

public class WithDrawTask implements Runnable {
	private BankAccount account;
	private int amount;

	public WithDrawTask(BankAccount account, int amount) {
		this.account = account;
		this.amount = amount;
	}

	@Override
	public void run() {
		account.withdraw(amount);
	}
}
