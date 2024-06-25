package io.github.williamyin2024.javago;

import java.util.concurrent.atomic.AtomicBoolean;

public class OutputChannelCase<T> implements Runnable {

	private final OutputChannel<T> outputChannel;
	private final T message;
	private final Runnable callback;
	private final OutputChannel<Integer> toSelector;
	private final int id;
	private final AtomicBoolean closed;

	public OutputChannelCase(
		OutputChannel<T> outputChannel,
		T message,
		Runnable callback,
		OutputChannel<Integer> toSelector,
		int id,
		AtomicBoolean closed
	) {
		this.outputChannel = outputChannel;
		this.message = message;
		this.callback = callback;
		this.toSelector = toSelector;
		this.id = id;
		this.closed = closed;
	}

	@Override
	public void run() {
		boolean hasSpace = outputChannel.hasSpace();
		if (Thread.currentThread().isInterrupted()) {
			return;
		}
		if (hasSpace && closed.compareAndSet(false, true)) {
			toSelector.send(id);
			outputChannel.send(message);
			callback.run();
			toSelector.send(id);
		}
	}
}
