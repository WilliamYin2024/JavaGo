package io.javago;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@code OutputChannelCase} class is used by the {@code Selector} class to create a case that is run when a message
 * is sent to the case's channel.
 * It implements the {@link Runnable} interface and allows a callback to be run after the message is sent.
 * Additionally, it sends an identifier to a specified output channel before and after executing the callback.
 *
 * @param <T> the type of message to be sent to the output channel
 */
public class OutputChannelCase<T> implements Runnable {

	private final OutputChannel<T> outputChannel;
	private final T message;
	private final Runnable callback;
	private final OutputChannel<Integer> toSelector;
	private final int id;
	private final AtomicBoolean closed;

	/**
	 * Constructs an {@code OutputChannelCase} with the specified output channel, message, callback, output channel for selector, identifier, and closed state.
	 *
	 * @param outputChannel the output channel to which the message will be sent
	 * @param message the message to be sent to the output channel
	 * @param callback the runnable to be executed after the message is sent
	 * @param toSelector the output channel to send the identifier
	 * @param id the identifier to be sent to the output channel
	 * @param closed the atomic boolean indicating the closed state
	 */
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

	/**
	 * Runs the output channel case. It sends a message to the output channel and executes the callback.
	 * If the output channel is closed or the thread is interrupted, the case returns immediately without executing the
	 * callback.
	 * This will not close its associated {@link Selector} object if it exists meaning it will still wait for a case to
	 * be completed.
	 * The identifier is sent to the output channel before and after executing the callback.
	 */
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
