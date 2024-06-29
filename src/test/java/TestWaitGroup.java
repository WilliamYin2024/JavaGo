import io.javago.sync.WaitGroup;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.javago.Go.go;
import static org.junit.Assert.assertTrue;

public class TestWaitGroup {

	@Test
	public void Test_WaitGroup() {
		WaitGroup wg = new WaitGroup();
		AtomicBoolean executed = new AtomicBoolean(false);
		wg.add(1);
		go(() -> {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			executed.set(true);
			wg.done();
		});
		wg.await();
		assertTrue(executed.get());
	}
}
