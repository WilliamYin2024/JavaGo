import io.javago.Channel;
import io.javago.InputChannel;
import io.javago.OutputChannel;
import org.junit.Test;

import static io.javago.Go.go;
import static org.junit.Assert.assertEquals;

public class TestChannel {

	@Test
	public void Test_Channel() {
		try (Channel<Integer> ch = Channel.make()) {
			int a = 1;
			int b = 2;
			go(() -> {
				int c = a + b;
				ch.send(c);
			});
			int r = ch.receive();
			assertEquals(3, r);
		}
	}

	@Test
	public void Test_Iterator() {
		Channel<Integer> ch = Channel.make();
		go(() -> {
			for (int i = 1; i <= 10; i++) {
				ch.send(i);
			}
			ch.close();
		});
		int sum = 0;
		for (Integer i : ch) {
			sum += i;
		}
		assertEquals(55, sum);
	}

	@Test
	public void Test_IteratorDelayBeforeSend() {
		Channel<Integer> ch = Channel.make();
		go(() -> {
			for (int i = 1; i <= 10; i++) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				ch.send(i);
			}
			ch.close();
		});
		int sum = 0;
		for (Integer i : ch) {
			sum += i;
		}
		assertEquals(55, sum);
	}

	@Test
	public void Test_IteratorDelayBeforeClose() {
		Channel<Integer> ch = Channel.make();
		go(() -> {
			for (int i = 1; i <= 10; i++) {
				ch.send(i);
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			ch.close();
		});
		int sum = 0;
		for (Integer i : ch) {
			sum += i;
		}
		assertEquals(55, sum);
	}

	@Test
	public void Test_IteratorTwoThreadsSum() {
		Channel<Integer> inputChannel = Channel.make(10);
		Channel<Integer> outputChannel = Channel.make(2);
		go(() -> sumFromChannel(inputChannel, outputChannel));
		go(() -> sumFromChannel(inputChannel, outputChannel));
		for (int i = 1; i <= 10; i++) {
			inputChannel.send(i);
		}
		inputChannel.close();
		int sum1 = outputChannel.receive();
		int sum2 = outputChannel.receive();
		outputChannel.close();
		assertEquals(55, sum1 + sum2);
	}

	private void sumFromChannel(InputChannel<Integer> inputChannel, OutputChannel<Integer> outputChannel) {
		int sum = 0;
		for (int i = 0; i < 5; i++) {
			sum += inputChannel.receive();
		}
		outputChannel.send(sum);
	}
}
