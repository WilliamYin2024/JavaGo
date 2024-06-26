package io.github.williamyin2024.javago.sync;

import java.util.concurrent.atomic.AtomicBoolean;

public class Once {

	private final AtomicBoolean called = new AtomicBoolean(false);

	public synchronized void doOnce(Runnable func) {
		if (called.compareAndSet(false, true)) {
			func.run();
			this.notifyAll();
			return;
		}
		try {
			this.wait();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
