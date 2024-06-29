package io.javago.sync;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * The {@code OnceValue} class implements Go's {@code sync.OnceValue}.
 * A {@link Supplier} wrapper that ensures the wrapped {@code Supplier} is executed only once.
 *
 * @param <T> the type of results supplied by this supplier
 */
public class OnceValue<T> implements Supplier<T> {

	private final AtomicBoolean called = new AtomicBoolean(false);
	private final Supplier<T> func;

	/**
	 * Constructs a new {@code OnceValue} that will wrap the given {@code Supplier}.
	 *
	 * @param func the {@code Supplier} to be wrapped and executed only once
	 */
	public OnceValue(Supplier<T> func) {
		this.func = func;
	}

	/**
	 * Executes the wrapped {@code Supplier} only once. If this method is called multiple times,
	 * the wrapped {@code Supplier} will only be executed the first time and its result will be returned.
	 * Subsequent calls will return {@code null}.
	 *
	 * @return the result supplied by the wrapped {@code Supplier} on its first execution, or {@code null} on subsequent calls
	 */
	@Override
	public T get() {
		if (called.compareAndSet(false, true)) {
			return func.get();
		}
		return null;
	}
}
