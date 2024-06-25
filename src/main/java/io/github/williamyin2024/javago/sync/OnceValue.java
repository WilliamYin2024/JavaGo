package io.github.williamyin2024.javago.sync;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class OnceValue<T> implements Supplier<T> {

	private final AtomicBoolean called = new AtomicBoolean(false);
	private final Supplier<T> func;

	public OnceValue(Supplier<T> func) {
		this.func = func;
	}


	@Override
	public T get() {
		if (called.compareAndSet(false, true)) {
			return func.get();
		}
		return null;
	}
}
