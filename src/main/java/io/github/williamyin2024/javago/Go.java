package io.github.williamyin2024.javago;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Go {

	private static final ExecutorService threadPool = Executors.newVirtualThreadPerTaskExecutor();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(threadPool::shutdown));
	}

	public static void go(Runnable r) {
		threadPool.execute(r);
	}
}
