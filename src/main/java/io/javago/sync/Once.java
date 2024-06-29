package io.javago.sync;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@code Once} class implements Go's {@code sync.Once}.
 * A utility class that ensures a given {@link Runnable} is executed only once.
 */
public class Once {

	private final AtomicBoolean called = new AtomicBoolean(false);

	/**
	 * Constructs a new {@code Once} instance.
	 */
	public Once() {}

	/**
	 * Executes the specified {@link Runnable} only once.
	 * If the {@code Runnable} has already been executed, subsequent calls will block until the first execution is
	 * complete.
	 *
	 * @param func the {@code Runnable} to be executed once
	 */
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
