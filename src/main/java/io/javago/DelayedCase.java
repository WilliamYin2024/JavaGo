package io.javago;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@code DelayedCase} class is used to create the timeout case found in Go's {@code select} statement.
 * It represents a task that is executed after a specified delay.
 * It implements the {@link Runnable} interface and allows a callback to be run after the delay.
 * {@code DelayedCase} reserves a platform thread until its callback has completed execution so the delay and amount of
 * instances of this class currently running should be kept to a minimum.
 * Additionally, it sends an identifier to a specified output channel before and after executing the callback.
 */
public class DelayedCase implements Runnable {

	private final Duration delay;
	private final Runnable callback;
	private final OutputChannel<Integer> toSelector;
	private final int id;
	private final AtomicBoolean closed;

	/**
	 * Constructs a {@code DelayedCase} with the specified delay, callback, output channel, identifier, and closed
	 * state.
	 * {@code DelayedCase} reserves a platform thread until its callback has completed execution so the delay and amount
	 * of instances of this class currently running should be kept to a minimum.
	 *
	 * @param delay the duration to wait before executing the callback
	 * @param callback the runnable to be executed after the delay
	 * @param toSelector the output channel to send the identifier
	 * @param id the identifier to be sent to the output channel
	 * @param closed the atomic boolean indicating the closed state
	 */
	public DelayedCase(
		Duration delay,
		Runnable callback,
		OutputChannel<Integer> toSelector,
		int id,
		AtomicBoolean closed
	) {
		this.delay = delay;
		this.callback = callback;
		this.toSelector = toSelector;
		this.id = id;
		this.closed = closed;
	}

	/**
	 * Runs the delayed task. After the specified delay, it sends the identifier to the output channel,
	 * runs the callback, and sends the identifier again. If interrupted, it cancels the timer.
	 * Reserves a platform thread until its callback has completed execution so the delay and amount of instances of
	 * this class currently running should be kept to a minimum.
	 */
	@Override
	public void run() {
		Timer timer = new Timer();
		final Thread thread = Thread.currentThread();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (closed.compareAndSet(false, true)) {
					toSelector.send(id);
					callback.run();
					toSelector.send(id);
					thread.interrupt();
				}
			}
		}, calculateExecutionTime());
		try {
			Thread.sleep(delay.toMillis());
		} catch (InterruptedException e) {
			timer.cancel();
		}
	}

	/**
	 * Calculates the execution time by adding the delay to the current time.
	 *
	 * @return the date representing the execution time
	 */
	private Date calculateExecutionTime() {
		Instant currentInstant = new Date().toInstant();
		Instant instantAfterDelay = currentInstant.plus(delay);
		return Date.from(instantAfterDelay);
	}
}
