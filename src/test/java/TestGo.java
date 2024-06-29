import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.javago.Go.go;
import static org.junit.Assert.assertTrue;

public class TestGo {

	@Test
	public void Test_FunctionExecuted() throws InterruptedException {
		AtomicBoolean executed = new AtomicBoolean(false);
		go(() -> executed.set(true));
		Thread.sleep(500);
		assertTrue(executed.get());
	}
}
