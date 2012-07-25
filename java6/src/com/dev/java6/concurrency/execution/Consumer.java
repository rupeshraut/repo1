package com.dev.java6.concurrency.execution;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Class Consumer.
 */
public class Consumer extends AbstractWorker<Integer> {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(Consumer.class.getName());

	/**
	 * Instantiates a new consumer.
	 * 
	 * @param queue
	 *            the queue
	 */
	public Consumer(Queue<Integer> queue) {
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
		TimeUnit.MILLISECONDS.sleep(1000);
	}

	/**
	 * 
	 * @param queue
	 *            the queue
	 * @see com.dev.java6.concurrency.execution.AbstractWorker#perform(java.util.
	 *      Queue)
	 */
	@Override
	void perform(final Queue<Integer> queue) {
		if (queue.size() > 0) {
			Integer integer = queue.poll();
			if (integer != null) {
				LOGGER.log(Level.INFO, new Date().getTime() + ",  " + this.getName() + " >> " + integer);
			}// if
		}// if
	}// process()

}// Worker