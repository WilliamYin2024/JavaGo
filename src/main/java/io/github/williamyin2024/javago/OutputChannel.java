package io.github.williamyin2024.javago;

public interface OutputChannel<T> extends AutoCloseable, Iterable<T> {
	void send(T message);
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
