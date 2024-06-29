package io.javago.sync;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * The {@code OnceValues} class implements Go's {@code sync.OnceValues}.
 * A {@link Supplier} wrapper that ensures the wrapped {@code Supplier} is executed only once and returns a pair of
 * values.
 *
 * @param <S> the type of the first value
 * @param <T> the type of the second value
 */
public class OnceValues<S, T> implements Supplier<OnceValues.Values<S, T>> {

	private final AtomicBoolean called = new AtomicBoolean(false);
	private final Supplier<Values<S, T>> supplier;

	/**
	 * Constructs a new {@code OnceValues} that will wrap the given {@code Supplier}.
	 *
	 * @param supplier the {@code Supplier} to be wrapped and executed only once
	 */
	public OnceValues(Supplier<Values<S, T>> supplier) {
		this.supplier = supplier;
	}

	/**
	 * Executes the wrapped {@code Supplier} only once. If this method is called multiple times,
	 * the wrapped {@code Supplier} will only be executed the first time and its result will be returned.
	 * Subsequent calls will return {@code null}.
	 *
	 * @return the result supplied by the wrapped {@code Supplier} on its first execution, or {@code null} on subsequent calls
	 */
	@Override
	public Values<S, T> get() {
		if (called.compareAndSet(false, true)) {
			return supplier.get();
		}
		return null;
	}

	/**
	 * A record that holds a pair of values.
	 *
	 * @param <S> the type of the first value
	 * @param <T> the type of the second value
	 * @param first the first value
	 * @param second the second value
	 */
	public record Values<S, T>(S first, T second) {}
}
