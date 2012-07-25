package com.dev.java6.concurrency.execution;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Class ConcurrentExecution.
 */
public class ConcurrentExecution {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(ConcurrentExecution.class.getName());

	/** The queue. */
	private static final Queue<Integer> QUEUE = new ConcurrentLinkedQueue<Integer>();

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public static void main(String[] args) throws InterruptedException {
		new Producer(QUEUE).start();
		startConsumer();
	}// main()

	/**
	 * Run.
	 * 
	 * @throws InterruptedException
	 */
	private static void startConsumer() throws InterruptedException {
		final int processors = 10;//Runtime.getRuntime().availableProcessors();
		final ExecutorService executorService = Executors.newFixedThreadPool(processors);

		LOGGER.log(Level.INFO, "available processors " + processors);

		try {
			while (true) {
				if (!QUEUE.isEmpty()) {
					execute(executorService, processors, new Consumer(QUEUE));
				}// if
			}// while

		} finally {
			if (executorService != null && !executorService.isShutdown()) {
				executorService.shutdown();
			}// if
		}// finally
	}

	/**
	 * Time.
	 * 
	 * @param executor
	 *            the executor
	 * @param concurrency
	 *            the concurrency
	 * @param action
	 *            the action
	 * @return the long
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	public static long execute(final Executor executor, final int concurrency, final Runnable action) throws InterruptedException {

		final CountDownLatch ready = new CountDownLatch(concurrency);
		final CountDownLatch start = new CountDownLatch(1);
		final CountDownLatch done = new CountDownLatch(concurrency);

		for (int ctr = 0; ctr < concurrency; ctr++) {
			executor.execute(new Runnable() {

				public void run() {
					ready.countDown(); // Tell timer we're ready
					try {
						start.await(); // Wait till peers are ready
						action.run();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					} finally {
						done.countDown(); // Tell timer we're done
					}// finally
				}// run()

			});
		}// for
		ready.await(); // Wait for all workers to be ready
		long startNanos = System.nanoTime();
		start.countDown(); // And they're off!
		done.await(); // Wait for all workers to finish
		return System.nanoTime() - startNanos;
	}// time()

}// class
