package io.javago;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The {@code Go} class implements Go's {@code go} statement.
 * It provides a simple way to execute tasks asynchronously using a virtual thread per task executor.
 * It initializes a thread pool and ensures that it is properly shut down when the JVM exits.
 *
 * <p>This class is intended to be used for running {@link Runnable} tasks in a multi-threaded environment.</p>
 *
 * @see java.util.concurrent.ExecutorService
 * @see java.util.concurrent.Executors
 */
public class Go {

	/**
	 * The thread pool used for executing tasks.
	 * It is initialized as a virtual thread per task executor.
	 */
	private static final ExecutorService threadPool = Executors.newVirtualThreadPerTaskExecutor();

	// Static block to add a shutdown hook to ensure the thread pool is properly shut down when the JVM exits.
	static {
		Runtime.getRuntime().addShutdownHook(new Thread(threadPool::shutdown));
	}

	private Go() {}

	/**
	 * Executes the given task asynchronously using the thread pool.
	 * Used to recreate Go's {@code go} keyword in Java.
	 *
	 * @param r the task to be executed
	 * @throws NullPointerException if the task is null
	 */
	public static void go(Runnable r) {
		threadPool.execute(r);
	}
}
