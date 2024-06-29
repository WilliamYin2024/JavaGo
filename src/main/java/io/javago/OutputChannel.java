package io.javago;

/**
 * The {@code OutputChannel} interface defines the operations for a Go {@code channel} in Java that can send messages of
 * a specified type.
 * It extends {@link AutoCloseable} to support resource management and {@link Iterable} to allow for iteration over the
 * messages in the channel.
 *
 * @param <T> the type of messages handled by the channel
 */
public interface OutputChannel<T> extends AutoCloseable, Iterable<T> {

	/**
	 * Sends a message through the channel, waiting if necessary for space to become available.
	 *
	 * @param message the message to be sent
	 * @throws IllegalStateException if the channel
	 */
	void send(T message);

	/**
	 * Checks if the channel is closed.
	 *
	 * @return {@code true} if the channel is closed, {@code false} otherwise
	 */
	boolean isClosed();

	/**
	 * Closes the channel.
	 * Once closed, no more messages can be sent, but any remaining messages can still be received.
	 * Closing an already closed channel has no effect.
	 */
	void close();

	/**
	 * Checks if the channel is empty.
	 *
	 * @return {@code true} if the channel is empty, {@code false} otherwise
	 */
	boolean isEmpty();

	/**
	 * Checks if the channel is full.
	 *
	 * @return {@code true} if the channel is full, {@code false} otherwise
	 */
	boolean isFull();

	/**
	 * Waits until the channel has space for another message or is closed.
	 *
	 * @return {@code true} if the channel has space, {@code false} if the channel is closed
	 */
	boolean hasSpace();

	/**
	 * Waits until the channel has another message.
	 *
	 * @return {@code true} if there are more messages, {@code false} if the channel is both closed and empty.
	 */
	boolean hasNext();

	/**
	 * Creates a new channel with a default capacity.
	 *
	 * @param <T> the type of messages handled by the channel
	 * @return a new {@code Channel} instance
	 */
	static <T> Channel<T> make() {
		return new BufferedQueueChannel<>();
	}

	/**
	 * Creates a new channel with the specified capacity.
	 *
	 * @param <T> the type of messages handled by the channel
	 * @param capacity the capacity of the channel
	 * @return a new {@code Channel} instance with the specified capacity
	 */
	static <T> Channel<T> make(int capacity) {
		return new BufferedQueueChannel<>(capacity);
	}
}
