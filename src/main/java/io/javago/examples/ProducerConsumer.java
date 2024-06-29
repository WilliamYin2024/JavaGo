package io.javago.examples;

import io.javago.Channel;
import io.javago.InputChannel;
import io.javago.OutputChannel;
import io.javago.sync.WaitGroup;

import java.util.Arrays;

import static io.javago.Go.go;

public class ProducerConsumer {

	private static final String[] sodas = {
		"Coca-Cola",
		"Sprite",
		"Pepsi",
		"7-Up",
		"Canada Dry",
		"Dr. Pepper",
		"Mountain Dew",
		"Fanta",
		"Crush",
		"Sierra Mist"
	};

	public static void main(String[] args) {
		try (Channel<String> channel = Channel.make()) {
			WaitGroup wg = new WaitGroup();
			wg.add(2);
			go(new Producer(channel, wg));
			go(new Consumer(channel, wg));
			wg.await();
		}
	}

	private record Producer(OutputChannel<String> channel, WaitGroup wg) implements Runnable {

		@Override
			public void run() {
				try (channel; wg) {
					Arrays.stream(sodas).forEach(channel::send);
				}
			}
		}

	private record Consumer(InputChannel<String> channel, WaitGroup wg) implements Runnable {

		@Override
			public void run() {
				try (wg) {
					for (String soda : channel) {
						System.out.printf("Consumer received %s.%n", soda);
					}
				}
			}
		}
}
