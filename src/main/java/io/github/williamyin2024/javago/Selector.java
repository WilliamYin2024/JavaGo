package io.github.williamyin2024.javago;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Selector {

	private final List<Thread> threads = new ArrayList<>();
	private final List<Runnable> cases = new ArrayList<>();
	private final Channel<Integer> toCases = Channel.make();
	private final List<InputChannel<?>> inputChannels = new ArrayList<>();
	private final List<OutputChannel<?>> outputChannels = new ArrayList<>();
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private Runnable defaultCase;

	private Selector() {}

	public static Selector select() {
		return new Selector();
	}

	public <T> Selector addCase(InputChannel<T> ch, Consumer<T> c) {
		cases.add(new InputChannelCase<>(ch, c, toCases, cases.size(), closed));
		inputChannels.add(ch);
		return this;
	}

	public <T> Selector addCase(OutputChannel<T> ch, T message, Runnable r) {
		cases.add(new OutputChannelCase<>(ch, message, r, toCases, cases.size(), closed));
		outputChannels.add(ch);
		return this;
	}

	public Selector addCase(Duration d, Runnable r) {
		cases.add(new DelayedCase(d, r, toCases, cases.size(), closed));
		return this;
	}

	public Selector addDefault(Runnable r) {
		defaultCase = r;
		return this;
	}

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
