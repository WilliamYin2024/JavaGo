package io.github.williamyin2024.javago;

public interface Channel<T> extends InputChannel<T>, OutputChannel<T> {
	@Override
	void send(T message);
	@Override
	T receive();
	@Override
	boolean isClosed();
	@Override
	void close();
	@Override
	boolean isEmpty();
	@Override
	boolean isFull();
	@Override
	boolean hasSpace();
	@Override
	boolean hasNext();

	static <T> Channel<T> make() {
		return new BufferedQueueChannel<>();
	}

	static <T> Channel<T> make(int capacity) {
		return new BufferedQueueChannel<>(capacity);
	}
}
