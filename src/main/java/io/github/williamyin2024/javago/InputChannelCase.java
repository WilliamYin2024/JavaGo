package io.github.williamyin2024.javago;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class InputChannelCase<T> implements Runnable {

	private final InputChannel<T> inputChannel;
	private final Consumer<T> callback;
	private final OutputChannel<Integer> toSelector;
	private final int id;
	private final AtomicBoolean closed;

	public InputChannelCase(
		InputChannel<T> inputChannel,
		Consumer<T> callback,
		OutputChannel<Integer> toSelector,
		int id,
		AtomicBoolean closed
	) {
		this.inputChannel = inputChannel;
		this.callback = callback;
		this.toSelector = toSelector;
		this.id = id;
		this.closed = closed;
	}

	@Override
	public void run() {
		if (inputChannel.isClosed() && closed.compareAndSet(false, true)) {
			toSelector.send(id);
			T message = inputChannel.receive();
			callback.accept(message);
			toSelector.send(id);
		}
		boolean hasNext = inputChannel.hasNext();
		if (Thread.currentThread().isInterrupted() && !inputChannel.isClosed()) {
			return;
		}
		if ((hasNext || inputChannel.isClosed()) && closed.compareAndSet(false, true)) {
			toSelector.send(id);
			T message = inputChannel.receive();
			callback.accept(message);
			toSelector.send(id);
		}
	}
}
