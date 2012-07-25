package com.dev.java6.concurrency.execution;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * The Class Producer.
 */
public class Producer extends AbstractWorker<Integer> {

	/** The random. */
	private static final Random RANDOM = new Random();

	/**
	 * Instantiates a new producer.
	 * 
	 * @param queue
	 *            the queue
	 */
	public Producer(Queue<Integer> queue) {
		super(queue);
	}

	/**
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @see com.dev.java6.concurrency.execution.AbstractWorker#sleepStrategy()
	 */
	@Override
	void sleepStrategy() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(100);
	}// threadSleepStrategy()

	/**
	 * 
	 * @param queue
	 *            the queue
	 * @see com.dev.java6.concurrency.execution.AbstractWorker#perform(java.util.
	 *      Queue)
	 */
	@Override
	void perform(Queue<Integer> queue) {
		if (queue != null) {
			queue.add(RANDOM.nextInt());
			System.out.println(queue);
		}// if
	}// process()
}// class
