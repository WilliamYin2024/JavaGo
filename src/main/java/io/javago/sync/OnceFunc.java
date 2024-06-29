package io.javago.sync;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@code OnceFunc} class implements Go's {@code sync.OnceFunc}.
 * A {@link Runnable} wrapper that ensures the wrapped {@code Runnable} is executed only once.
 */
public class OnceFunc implements Runnable {

	private final AtomicBoolean called = new AtomicBoolean(false);
	private final Runnable func;

	/**
	 * Constructs a new {@code OnceFunc} that will wrap the given {@code Runnable}.
	 *
	 * @param func the {@code Runnable} to be wrapped and executed only once
	 */
	public OnceFunc(Runnable func) {
		this.func = func;
	}

	/**
	 * Executes the wrapped {@code Runnable} only once.
	 * If this method is called multiple times, the wrapped {@code Runnable} will only be executed the first time.
	 */
	@Override
	public void run() {
		if (called.compareAndSet(false, true)) {
			func.run();
		}
	}
}
