package io.github.williamyin2024.javago;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class DelayedCase implements Runnable {

	private final Duration delay;
	private final Runnable callback;
	private final OutputChannel<Integer> toSelector;
	private final int id;
	private final AtomicBoolean closed;

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
		}, getExecutionTime());
		try {
			Thread.sleep(delay.toMillis());
		} catch (InterruptedException e) {
			timer.cancel();
		}
	}

	private Date getExecutionTime() {
		Instant currentinstant = new Date().toInstant();
		Instant instantAfterDelay = currentinstant.plus(delay);
		return Date.from(instantAfterDelay);
	}
}
