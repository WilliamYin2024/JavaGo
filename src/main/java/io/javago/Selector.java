package io.javago;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * The {@code Selector} class implements Go's {@code select} statement.
 * It implements a select-like mechanism for handling multiple asynchronous tasks.
 * It manages cases involving input channels, output channels, delayed tasks, and a default case.
 * When a {@code Selector}'s {@link #run} method is called, it creates a virtual thread for each case.
 * In the event that one of the cases is a {@link DelayedCase}, the delayed case will also reserve a platform thread
 * until either another case in the {@code Selector} or the delayed case's method is completed.
 * This means that the amount of delayed cases in a {@code Selector} and their duration should be kept to a minimum.
 * When a case is executed, the virtual threads for all other tasks are interrupted and any pending tasks on them are
 * cancelled.
 */
public class Selector {

	private final List<Thread> threads = new ArrayList<>();
	private final List<Runnable> cases = new ArrayList<>();
	private final Channel<Integer> toCases = Channel.make();
	private final List<InputChannel<?>> inputChannels = new ArrayList<>();
	private final List<OutputChannel<?>> outputChannels = new ArrayList<>();
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private Runnable defaultCase;

	/**
	 * Private constructor for creating instances of {@code Selector}. Use {@link #select()} method to instantiate.
	 */
	private Selector() {}

	/**
	 * Static factory method to create a new instance of {@code Selector}.
	 *
	 * @return a new instance of {@code Selector}
	 */
	public static Selector select() {
		return new Selector();
	}

	/**
	 * Adds an {@link InputChannelCase} to the selector.
	 * The case will execute its {@link Consumer} after it receives a message from its associated {@link InputChannel}.
	 * The message received from the channel will be the input to the consumer.
	 *
	 * @param <T> the type of messages handled by the input channel
	 * @param ch the input channel to monitor
	 * @param c the consumer to execute when a message is received
	 * @return this {@code Selector} instance for method chaining
	 */
	public <T> Selector addCase(InputChannel<T> ch, Consumer<T> c) {
		cases.add(new InputChannelCase<>(ch, c, toCases, cases.size(), closed));
		inputChannels.add(ch);
		return this;
	}

	/**
	 * Adds an {@link OutputChannelCase} to the selector.
	 * The case will execute its {@link Runnable} after it sends a message to its associated {@link OutputChannel}.
	 *
	 * @param <T> the type of message to send through the output channel
	 * @param ch the output channel to send the message
	 * @param message the message to send through the output channel
	 * @param r the callback to execute after sending the message
	 * @return this {@code Selector} instance for method chaining
	 */
	public <T> Selector addCase(OutputChannel<T> ch, T message, Runnable r) {
		cases.add(new OutputChannelCase<>(ch, message, r, toCases, cases.size(), closed));
		outputChannels.add(ch);
		return this;
	}

	/**
	 * Adds a {@link DelayedCase} to the selector.
	 * The delayed case will reserve a platform thread until either another case in the {@code Selector} or the delayed
	 * case's method is completed.
	 * This means that the amount of delayed cases in a {@code Selector} and their duration should be kept to a minimum.
	 *
	 * @param d the duration to wait before executing the callback
	 * @param r the runnable to execute after the delay
	 * @return this {@code Selector} instance for method chaining
	 */
	public Selector addCase(Duration d, Runnable r) {
		cases.add(new DelayedCase(d, r, toCases, cases.size(), closed));
		return this;
	}

	/**
	 * Adds a default case to the selector.
	 * The default case will be executed if every {@link InputChannelCase} and {@link OutputChannelCase}'s channel is
	 * empty or full respectively.
	 *
	 * @param r the runnable to execute as the default case
	 * @return this {@code Selector} instance for method chaining
	 */
	public Selector addDefault(Runnable r) {
		defaultCase = r;
		return this;
	}

	/**
	 * Executes the selector logic.
	 * Creates a virtual thread for each case.
	 * In the event that one of the cases is a {@link DelayedCase}, the delayed case will also reserve a platform thread
	 * until either another case in the {@code Selector} or the delayed case's method is completed.
	 * This means that the amount of delayed cases in a {@code Selector} and their duration should be kept to a minimum.
	 * When a case is executed, the virtual threads for all other tasks are interrupted and any pending tasks on them
	 * are cancelled.
	 */
	public void run() {
		if (defaultCase != null) {
			boolean goToDefaultCase = true;
			for (InputChannel<?> ch : inputChannels) {
				if (!ch.isEmpty()) {
					goToDefaultCase = false;
				}
			}
			for (OutputChannel<?> ch : outputChannels) {
				if (!ch.isClosed() && !ch.isFull()) {
					goToDefaultCase = false;
				}
			}
			if (goToDefaultCase) {
				defaultCase.run();
				return;
			}
		}

		for (Runnable r : cases) {
			threads.add(Thread.ofVirtual().start(r));
		}
		int runningThreadId = toCases.receive();
		for (int i = 0; i < threads.size(); i++) {
			if (i != runningThreadId) {
				threads.get(i).interrupt();
			}
		}
		toCases.receive();
		toCases.close();
	}
}
