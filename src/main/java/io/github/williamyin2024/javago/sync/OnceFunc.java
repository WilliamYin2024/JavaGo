package io.github.williamyin2024.javago.sync;

import java.util.concurrent.atomic.AtomicBoolean;

public class OnceFunc implements Runnable {

	private final AtomicBoolean called = new AtomicBoolean(false);
	private final Runnable func;

	public OnceFunc(Runnable func) {
		this.func = func;
	}

	@Override
	public void run() {
		if (called.compareAndSet(false, true)) {
			func.run();
		}
	}
}
