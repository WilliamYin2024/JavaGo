package io.javago.examples;

import io.javago.Channel;
import io.javago.sync.WaitGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.javago.Go.go;
import static io.javago.Selector.select;

class DiningPhilosophersSelect {

	private static final int NUM_PHILOSOPHERS = 10;
	private static final int NUM_MEALS = 3;
	private static final WaitGroup wg = new WaitGroup();

	public static void main(String[] args) {
		wg.add(NUM_PHILOSOPHERS);

		List<Channel<Object>> forks = new ArrayList<>(NUM_PHILOSOPHERS);
		for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
			Channel<Object> fork = Channel.make();
			forks.add(fork);
			fork.send(new Object());
		}

		Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
		for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
			philosophers[i] = new Philosopher(i, forks.get(i), forks.get((i + 1) % NUM_PHILOSOPHERS));
			go(philosophers[i]);
		}

		wg.await();
		for (Channel<Object> fork : forks) {
			fork.close();
		}
		System.out.println("All philosophers have finished dining.");
	}

	private record Philosopher(int id, Channel<Object> leftFork, Channel<Object> rightFork) implements Runnable {

		@Override
		public void run() {
			try (wg) {
				for (int i = 0; i < NUM_MEALS; i++) {
					think();
					acquireForks();
					eat();
					System.out.println("Philosopher " + id + " has eaten " + (i+1) + " meals.");
					releaseForks();
				}
			}
			System.out.println("Philosopher " + id + " has finished.");
		}

		private void sleep(long millis) {
			if (millis > 0) {
				synchronized (this) {
					try {
						this.wait(millis);
					} catch (InterruptedException ignored) {}
				}
			}
		}

		private void think() {
			sleep((int) (Math.random() * 100 + 1));
		}

		private void eat() {
			sleep((int) (Math.random() * 100 + 1));
		}

		private void acquireForks() {
			AtomicBoolean acquired = new AtomicBoolean(false);
			while (!acquired.get()) {
				if (id == 0) {
					select()
						.addCase(rightFork, r ->
							select()
								.addCase(leftFork, l -> acquired.set(true))
								.addDefault(() -> rightFork.send(new Object()))
								.run()
						)
						.addDefault(() -> {})
						.run();
				} else {
					select()
						.addCase(leftFork, l ->
							select()
								.addCase(rightFork, r -> acquired.set(true))
								.addDefault(() -> leftFork.send(new Object()))
								.run()
						)
						.addDefault(() -> {})
						.run();
				}
			}
		}

		private void releaseForks() {
			if (id == 0) {
				leftFork.send(new Object());
				rightFork.send(new Object());
			} else {
				rightFork.send(new Object());
				leftFork.send(new Object());
			}
		}
	}
}
