package io.github.williamyin2024.javago;

import java.util.*;

public class BufferedQueueChannel<T> implements Channel<T> {

	private final Queue<T> channelQueue;
	private boolean closed = false;
	private final int capacity;

	public BufferedQueueChannel() {
		this(1);
	}

	public BufferedQueueChannel(int capacity) {
		channelQueue = new ArrayDeque<>(capacity);
		this.capacity = capacity;
	}

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
				}
			} else {
				break;
			}
		}
		channelQueue.add(message);
		this.notifyAll();
	}

	@Override
	public synchronized T receive() {
		while (true) {
			if (closed) {
				if (channelQueue.isEmpty()) {
					throw new NoSuchElementException();
				} else {
					break;
				}
			} else if (channelQueue.isEmpty()) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			} else {
				break;
			}
		}
		T message = channelQueue.poll();
		this.notifyAll();
		return message;
	}

	@Override
	public synchronized boolean isClosed() {
		return closed;
	}

	@Override
	public synchronized boolean isEmpty() {
		return channelQueue.isEmpty();
	}

	@Override
	public synchronized boolean isFull() {
		return channelQueue.size() == capacity;
	}

	@Override
	public synchronized void close() {
		if (!closed) {
			closed = true;
			this.notifyAll();
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new ChannelIterator();
	}

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
			}
		}
	}

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
			}
		}
	}

	private class ChannelIterator implements Iterator<T> {

		@Override
		public boolean hasNext() {
			return BufferedQueueChannel.this.hasNext();
		}

		@Override
		public T next() {
			return receive();
		}
	}
}
