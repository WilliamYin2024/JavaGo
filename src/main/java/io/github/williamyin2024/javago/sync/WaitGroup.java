package io.github.williamyin2024.javago.sync;

public class WaitGroup {
	private int count;

	public synchronized void add(int amount) {
		count += amount;
	}

	public synchronized void done() {
		count--;
		if (count == 0) {
			this.notifyAll();
		}
	}

	public synchronized void await() {
		if (count > 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
