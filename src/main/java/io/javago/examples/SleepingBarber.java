package io.javago.examples;

import io.javago.Channel;
import io.javago.InputChannel;
import io.javago.OutputChannel;
import io.javago.sync.WaitGroup;

import static io.javago.Go.go;
import static io.javago.Selector.select;

class SleepingBarber {

	private static final int NUM_CUSTOMERS = 100;
	private static final int NUM_CHAIRS = 5;

	private static final WaitGroup wg = new WaitGroup();

	public static void main(String[] args) {
		wg.add(NUM_CUSTOMERS);

		try (
			Channel<Integer> customerArrived = Channel.make();
			Channel<Boolean> barberDone = Channel.make();
			Channel<Object> waitingRoom = Channel.make(NUM_CHAIRS)
		) {
			go(new Barber(customerArrived, barberDone));
			for (int i = 0; i < NUM_CUSTOMERS; i++) {
				go(new Customer(i, waitingRoom, customerArrived, barberDone));
				try {
					Thread.sleep((int) (Math.random() * 100 + 1));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
			wg.await();
		}
		System.out.println("All customers have been served or left.");
	}

	private record Barber(
		InputChannel<Integer> customerArrived,
		OutputChannel<Boolean> barberDone
	) implements Runnable {

		@Override
		public void run() {
			for (Integer customer : customerArrived) {
				System.out.println("Barber is cutting customer #" + customer + "'s hair.");
				sleep((int) (Math.random() * 100 + 1));
				System.out.println("Barber has finished cutting customer #" + customer + "'s hair.");
				barberDone.send(true);
			}
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
	}

	private record Customer(
		int id,
		Channel<Object> waitingRoom,
		OutputChannel<Integer> customerArrived,
		InputChannel<Boolean> barberDone
	) implements Runnable {

		@Override
		public void run() {
			try (wg) {
				System.out.println("Customer #" + id + " has arrived.");
				select()
					.addCase(waitingRoom, new Object(), () -> {
						System.out.println("Customer #" + id + " has taken a seat in the waiting room.");
						customerArrived.send(id);
						barberDone.receive();
						waitingRoom.receive();
						System.out.println("Customer #" + id + " received a haircut and is leaving.");
					})
					.addDefault(() -> System.out.println("Customer #" + id + " found no free chairs and is leaving."))
					.run();
			}
		}
	}
}
