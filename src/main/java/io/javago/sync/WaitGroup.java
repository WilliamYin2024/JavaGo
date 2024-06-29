package io.javago.sync;

/**
 * The {@code WaitGroup} class implements Go's {@code sync.WaitGroup}.
 * A synchronization aid that allows one or more threads to wait until a set of operations being performed in other
 * threads completes.
 * It implements the {@link AutoCloseable} interface and allows a try-with-resources statement to automatically decrease
 * its count by one.
 */
public class WaitGroup implements AutoCloseable {
	private int count;

	/**
	 * Constructs a new {@code WaitGroup} with an initial count of zero.
	 */
	public WaitGroup() {
		this.count = 0;
	}

	/**
	 * Increments the count of this wait group by the specified amount.
	 *
	 * @param amount the amount by which to increment the count
	 */
	public synchronized void add(int amount) {
		count += amount;
	}

	/**
	 * Decrements the count of this wait group by one.
	 * If the count reaches zero, all waiting threads are notified.
	 * Equivalent to {@link #close()}.
	 *
	 * @throws IllegalStateException if the wait group has already reached zero
	 */
	public synchronized void done() {
		if (count == 0) {
			throw new IllegalStateException("WaitGroup has already reached zero");
		}

		count--;
		if (count == 0) {
			this.notifyAll();
		}
	}

	/**
	 * Causes the current thread to wait until the count of this wait group reaches zero.
	 * If the current count is zero, this method returns immediately.
	 */
	public synchronized void await() {
		if (count > 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Decrements the count of this wait group by one.
	 * If the count reaches zero, all waiting threads are notified.
	 * Equivalent to {@link #done()}.
	 *
	 * @throws IllegalStateException if the wait group has already reached zero
	 */
	@Override
	public synchronized void close() {
		done();
	}
}
