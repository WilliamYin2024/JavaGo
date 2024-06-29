package io.javago.examples;

import io.javago.Channel;
import io.javago.sync.WaitGroup;

import java.util.ArrayList;
import java.util.List;

import static io.javago.Go.go;

public class DiningPhilosophersNoSelect {

	private static final int NUM_PHILOSOPHERS = 500;
	private static final int NUM_MEALS = 500;
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
					eat();
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
			Channel<Object> firstFork = (id < (id + 1) % NUM_PHILOSOPHERS) ? leftFork : rightFork;
			Channel<Object> secondFork = (firstFork == leftFork) ? rightFork : leftFork;

			firstFork.receive();
			System.out.println("Philosopher " + id + " received first fork.");
			secondFork.receive();
			System.out.println("Philosopher " + id + " received second fork.");

			System.out.println("Philosopher " + id + " eating.");
			try {
				Thread.sleep((int) (Math.random() * 100 + 1));
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			secondFork.send(new Object());
			System.out.println("Philosopher " + id + " put down second fork.");
			firstFork.send(new Object());
			System.out.println("Philosopher " + id + " put down first fork.");
		}
	}
}
