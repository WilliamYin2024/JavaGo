package io.javago;

import java.util.*;

/**
 * The {@code BufferedQueueChannel} class is an implementation of the {@link Channel} interface, providing a Go
 * {@code channel} backed by a Queue for passing messages between threads.
 * It supports both sending and receiving messages with a specified capacity.
 *
 * @param <T> the type of messages handled by the channel
 */
public class BufferedQueueChannel<T> implements Channel<T> {

	private final Queue<T> channelQueue;
	private boolean closed = false;
	private final int capacity;

	/**
	 * Constructs a {@code BufferedQueueChannel} with a default capacity of 1.
	 */
	public BufferedQueueChannel() {
		this(1);
	}

	/**
	 * Constructs a {@code BufferedQueueChannel} with the specified capacity.
	 *
	 * @param capacity the capacity of the channel
	 * @throws IllegalArgumentException if capacity is less than or equal to 0
	 */
	public BufferedQueueChannel(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("capacity must be greater than 0");
		}
		channelQueue = new ArrayDeque<>(capacity);
		this.capacity = capacity;
	}

	/**
	 * Sends a message through the channel. If the channel is full, this method blocks until space becomes available.
	 * Returns immediately if it is interrupted while blocking.
	 *
	 * @param message the message to be sent
	 * @throws IllegalStateException if the channel is closed
	 */
	@Override
	public synchronized void send(T message) {
		while (true) {
			if (closed) {
				throw new IllegalStateException("Channel is closed");
			}
			if (channelQueue.size() >= capacity) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
			} else {
				break;
			}
		}
		channelQueue.add(message);
		this.notifyAll();
	}

	/**
	 * Receives a message from the channel. If the channel is empty, this method blocks until a message becomes
	 * available. Returns immediately if it is interrupted while blocking.
	 *
	 * @return the received message
	 * @throws NoSuchElementException if the channel is closed and empty
	 */
	@Override
	public synchronized T receive() {
		while (true) {
			if (closed) {
				if (channelQueue.isEmpty()) {
					throw new NoSuchElementException("Channel is closed and empty");
				} else {
					break;
				}
			} else if (channelQueue.isEmpty()) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return null;
				}
			} else {
				break;
			}
		}
		T message = channelQueue.poll();
		this.notifyAll();
		return message;
	}

	/**
	 * Checks if the channel is closed.
	 *
	 * @return {@code true} if the channel is closed, {@code false} otherwise
	 */
	@Override
	public synchronized boolean isClosed() {
		return closed;
	}

	/**
	 * Checks if the channel is empty.
	 *
	 * @return {@code true} if the channel is empty, {@code false} otherwise
	 */
	@Override
	public synchronized boolean isEmpty() {
		return channelQueue.isEmpty();
	}

	/**
	 * Checks if the channel is full.
	 *
	 * @return {@code true} if the channel is full, {@code false} otherwise
	 */
	@Override
	public synchronized boolean isFull() {
		return channelQueue.size() == capacity;
	}

	/**
	 * Closes the channel.
	 * Once closed, no more messages can be sent, but any remaining messages can still be received.
	 * Closing an already closed channel has no effect.
	 */
	@Override
	public synchronized void close() {
		if (!closed) {
			closed = true;
			this.notifyAll();
		}
	}

	/**
	 * Returns an iterator over the elements in this channel.
	 *
	 * @return an {@code Iterator} over the elements in this channel
	 */
	@Override
	public Iterator<T> iterator() {
		return new ChannelIterator();
	}

	/**
	 * Waits until the channel has space for another message or is closed. Returns immediately if it is interrupted
	 * while blocking.
	 *
	 * @return {@code true} if the channel has space, {@code false} if the channel is closed
	 */
	@Override
	public synchronized boolean hasSpace() {
		while (true) {
			if (closed) {
				return false;
			}
			if (channelQueue.size() < capacity) {
				return true;
			}
			try {
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
	}

	/**
	 * Waits until the channel has another message or is closed. Returns immediately if it is interrupted while
	 * blocking.
	 *
	 * @return {@code true} if there are more messages, {@code false} if the channel is empty and closed
	 */
	@Override
	public synchronized boolean hasNext() {
		while (true) {
			if (!channelQueue.isEmpty()) {
				return true;
			}
			if (closed) {
				return false;
			}
			try {
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
	}

	/**
	 * The {@code ChannelIterator} class provides an iterator over the elements in the {@code BufferedQueueChannel}. If
	 * there are no more messages, the iterator will block until a new message is received or the channel is closed.
	 */
	private class ChannelIterator implements Iterator<T> {

		/**
		 * Waits until the channel has another message or is closed. Returns immediately if it is interrupted while
		 * blocking.
		 *
		 * @return {@code true} if there are more messages, {@code false} if the channel is empty and closed
		 */
		@Override
		public boolean hasNext() {
			synchronized (BufferedQueueChannel.this) {
				return BufferedQueueChannel.this.hasNext();
			}
		}

		/**
		 * Receives a message from the channel. If the channel is empty, this method blocks until a message becomes
		 * available. Returns immediately if it is interrupted while blocking.
		 *
		 * @return the received message
		 * @throws NoSuchElementException if the channel is closed and empty
		 */
		@Override
		public T next() {
			synchronized (BufferedQueueChannel.this) {
				return receive();
			}
		}
	}
}
