package io.github.williamyin2024.javago;

public interface InputChannel<T> extends AutoCloseable, Iterable<T> {
	T receive();
	boolean isClosed();
	boolean isEmpty();
	boolean isFull();
	boolean hasSpace();
	boolean hasNext();

	@Override
	void close();

	static <T> Channel<T> make() {
		return new BufferedQueueChannel<>();
	}

	static <T> Channel<T> make(int capacity) {
		return new BufferedQueueChannel<>(capacity);
	}
}
