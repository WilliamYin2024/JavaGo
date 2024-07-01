package io.javago.examples;

import io.javago.Channel;
import io.javago.sync.WaitGroup;

import java.util.ArrayList;
import java.util.List;

import static io.javago.Go.go;

class DiningPhilosophersNoSelect {

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
					eat();
					System.out.println("Philosopher " + id + " has eaten " + (i+1) + " meals.");
				}
			}
			System.out.println("Philosopher " + id + " has finished eating.");
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
			Channel<Object> firstFork = (id < (id + 1) % NUM_PHILOSOPHERS) ? leftFork : rightFork;
			Channel<Object> secondFork = (firstFork == leftFork) ? rightFork : leftFork;

			firstFork.receive();
			secondFork.receive();

			sleep((int) (Math.random() * 100 + 1));

			secondFork.send(new Object());
			firstFork.send(new Object());
		}
	}
}
