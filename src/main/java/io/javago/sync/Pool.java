package io.javago.sync;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

/**
 * The {@code Pool} class implements Go's {@code sync.Pool}.
 * A thread-safe object pool that manages a collection of reusable objects.
 *
 * @param <T> the type of objects managed by the pool
 */
public class Pool<T> {

	private final BlockingQueue<T> pool = new LinkedBlockingQueue<>();
	private final Supplier<T> creator;

	/**
	 * Constructs a new {@code Pool} with the given object creator.
	 *
	 * @param creator a {@code Supplier} that provides new instances of the objects managed by the pool
	 */
	public Pool(Supplier<T> creator) {
		this.creator = creator;
	}

	/**
	 * Retrieves an object from the pool. If the pool is empty, a new object is created using the {@code Supplier}.
	 *
	 * @return an object from the pool, or a newly created object if the pool is empty
	 */
	public synchronized T get() {
		if (pool.isEmpty()) {
			return creator.get();
		}
		return pool.poll();
	}

	/**
	 * Returns an object to the pool, making it available for future retrieval.
	 * If the number of objects in the pool is equal to {@link Integer#MAX_VALUE}, the object will not be returned to
	 * the pool.
	 *
	 * @param t the object to be returned to the pool
	 */
	public synchronized void put(T t) {
		pool.offer(t);
	}
}
