package io.github.williamyin2024.javago.sync;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

public class Pool<T> {

	private final BlockingQueue<T> pool = new LinkedBlockingQueue<>();
	private final Supplier<T> creator;

	public Pool(Supplier<T> creator) {
		this.creator = creator;
	}

	public synchronized T get() {
		if (pool.isEmpty()) {
			return creator.get();
		}

		return pool.poll();
	}

	public synchronized void put(T t) {
		pool.offer(t);
	}
}
