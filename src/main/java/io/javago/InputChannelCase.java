package io.javago;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * The {@code InputChannelCase} class is used by the {@code Selector} class to create a case that is run when a message
 * is received from the case's channel.
 * It implements the {@link Runnable} interface and allows a callback to be run when a message is received.
 * Additionally, it sends an identifier to a specified output channel before and after executing the callback.
 *
 * @param <T> the type of messages handled by the input channel
 */
public class InputChannelCase<T> implements Runnable {

	private final InputChannel<T> inputChannel;
	private final Consumer<T> callback;
	private final OutputChannel<Integer> toSelector;
	private final int id;
	private final AtomicBoolean closed;

	/**
	 * Constructs an {@code InputChannelCase} with the specified input channel, callback, output channel, identifier,
	 * and closed state.
	 *
	 * @param inputChannel the input channel from which messages are received
	 * @param callback the consumer to be executed when a message is received
	 * @param toSelector the output channel to send the identifier
	 * @param id the identifier to be sent to the output channel
	 * @param closed the atomic boolean indicating the closed state
	 */
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

	/**
	 * Runs the input channel case. It receives a message from the input channel and executes the callback.
	 * If the input channel is closed or the thread is interrupted, the callback is executed immediately which will
	 * close its associated {@link Selector} object if it exists.
	 * The identifier is sent to the output channel before and after executing the callback.
	 *
	 * @throws NoSuchElementException if the channel is closed and empty
	 */
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
