package io.javago.examples;

import io.javago.Channel;
import io.javago.sync.WaitGroup;

import java.util.ArrayList;
import java.util.List;

import static io.javago.Go.go;
import static io.javago.Selector.select;

public class DiningPhilosophersSelect {

	private static final int NUM_PHILOSOPHERS = 2000;
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
		System.out.println("All philosophers have finished dining.");
	}

	private record Philosopher(int id, Channel<Object> leftFork, Channel<Object> rightFork) implements Runnable {

		@Override
		public void run() {
			try (wg) {
				for (int i = 0; i < NUM_MEALS; i++) {
					think();
					getForks();
					eat();
					releaseForks();
				}
			}
			System.out.println("Philosopher " + id + " has finished.");
		}

		private void think() {
			System.out.println("Philosopher " + id + " thinking.");
			try {
				Thread.sleep((int) (Math.random() * 100 + 1));
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		private void eat() {
			System.out.println("Philosopher " + id + " eating.");
			try {
				Thread.sleep((int) (Math.random() * 100 + 1));
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		private void getForks() {
			think();
			getLeftFork();
			getRightFork();
		}

		private void getLeftFork() {
			select()
				.addCase(leftFork, (o) -> System.out.println("Philosopher " + id + " received left fork."))
				.addDefault(() -> {
					System.out.println("Philosopher " + id + " can't get left fork.");
					think();
					getLeftFork();
				})
				.run();
		}

		private void getRightFork() {
			select()
				.addCase(rightFork, (o) -> System.out.println("Philosopher " + id + " received right fork."))
				.addDefault(() -> {
					System.out.println("Philosopher " + id + " can't get right fork.");
					leftFork.send(new Object());
					System.out.println("Philosopher " + id + " put down left fork.");
					getForks();
				})
				.run();
		}

		private void releaseForks() {
			leftFork.send(new Object());
			System.out.println("Philosopher " + id + " put down left fork.");
			rightFork.send(new Object());
			System.out.println("Philosopher " + id + " put down right fork.");
		}
	}
}
