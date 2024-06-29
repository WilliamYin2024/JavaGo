import io.javago.Channel;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static io.javago.Go.go;
import static io.javago.Selector.select;
import static org.junit.Assert.assertEquals;

public class TestSelector {

	@Test
	public void Test_CaseRunNotDefault() {
		final AtomicInteger value = new AtomicInteger(0);
		Channel<Integer> ch = Channel.make();
		ch.send(1);
		select()
			.addCase(ch, value::set)
			.addDefault(() -> value.set(2))
			.run();
		assertEquals(1, value.get());
	}

	@Test
	public void Test_DefaultRun() {
		final AtomicInteger value = new AtomicInteger(0);
		Channel<Integer> ch = Channel.make();
		select()
			.addCase(ch, value::set)
			.addDefault(() -> value.set(2))
			.run();
		assertEquals(2, value.get());
	}

	@Test
	public void Test_OnlyOneCaseRunOneInputChannel() {
		final AtomicInteger value = new AtomicInteger(0);
		Channel<Integer> ch = Channel.make();
		ch.send(4);
		select()
			.addCase(ch, value::addAndGet)
			.addCase(ch, value::addAndGet)
			.run();
		assertEquals(4, value.get());
	}

	@Test
	public void Test_OnlyOneCaseRunTwoInputChannels() {
		final AtomicInteger value = new AtomicInteger(0);
		Channel<Integer> ch1 = Channel.make();
		Channel<Integer> ch2 = Channel.make();
		ch1.send(2);
		ch2.send(2);
		select()
			.addCase(ch1, value::addAndGet)
			.addCase(ch2, value::addAndGet)
			.run();
		assertEquals(2, value.get());
	}

	@Test
	public void Test_CaseInputClosedRunWithDefault() {
		final AtomicInteger value = new AtomicInteger(0);
		Channel<Integer> ch = Channel.make();
		ch.send(1);
		ch.close();
		select()
			.addCase(ch, value::set)
			.addDefault(() -> value.set(2))
			.run();
		assertEquals(1, value.get());
	}

	@Test
	public void Test_DelayedCaseCancelled() throws InterruptedException {
		final AtomicInteger value = new AtomicInteger(0);
		Channel<Integer> ch = Channel.make();
		ch.send(1);
		select()
			.addCase(ch, value::set)
			.addCase(Duration.ofMillis(500), () -> value.set(2))
			.run();
		Thread.sleep(1000);
		assertEquals(1, value.get());
	}

	@Test
	public void Test_DelayedCaseRun() throws InterruptedException {
		final AtomicInteger value = new AtomicInteger(0);
		Channel<Integer> ch = Channel.make();
		go(() -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return;
			}
			ch.send(1);
		});
		select()
			.addCase(ch, value::set)
			.addCase(Duration.ofMillis(500), () -> value.set(2))
			.run();
		assertEquals(2, value.get());
	}

	@Test
	public void Test_OnlyOneCaseRunOneOutputChannel() {
		final AtomicInteger value = new AtomicInteger(0);
		Channel<Integer> ch = Channel.make();
		select()
			.addCase(ch, 1, value::incrementAndGet)
			.addCase(ch, 1, value::incrementAndGet)
			.run();
		ch.close();
		int total = 0;
		for (int val : ch) {
			total += val;
		}
		assertEquals(1, value.get());
		assertEquals(1, total);
	}

	@Test
	public void Test_OnlyOneCaseRunTwoOutputChannels() {
		final AtomicInteger value = new AtomicInteger(0);
		Channel<Integer> ch1 = Channel.make();
		Channel<Integer> ch2 = Channel.make();
		select()
			.addCase(ch1, 1, value::incrementAndGet)
			.addCase(ch2, 1, value::incrementAndGet)
			.run();
		ch1.close();
		ch2.close();
		int total = 0;
		for (int val : ch1) {
			total += val;
		}
		for (int val : ch2) {
			total += val;
		}
		assertEquals(1, value.get());
		assertEquals(1, total);
	}
}
