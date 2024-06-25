package io.github.williamyin2024.javago.sync;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class OnceValues<S, T> implements Supplier<OnceValues.Values<S, T>> {

	private final AtomicBoolean called = new AtomicBoolean(false);
	private final Supplier<Values<S, T>> supplier;

	public OnceValues(Supplier<Values<S, T>> supplier) {
		this.supplier = supplier;
	}

	@Override
	public Values<S, T> get() {
		if (called.compareAndSet(false, true)) {
			return supplier.get();
		}
		return null;
	}

	public record Values<S, T>(S first, T second) {


	}
}
