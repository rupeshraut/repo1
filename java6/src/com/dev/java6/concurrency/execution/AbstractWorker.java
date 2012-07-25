package com.dev.java6.concurrency.execution;

import java.util.Queue;

/**
 * The Class AbstractWorker.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class AbstractWorker<T> extends Thread {

	/** The queue. */
	private final Queue<T> queue;

	/**
	 * Instantiates a new abstract worker.
	 * 
	 * @param queue
	 *            the queue
	 */
	public AbstractWorker(Queue<T> queue) {
		this.queue = queue;
		setDaemon(true);
	}// AbstractWorker

	/**
	 * sleep strategy.
	 *
	 * @throws InterruptedException the interrupted exception
	 */
	abstract void sleepStrategy() throws InterruptedException;

	/**
	 * Process.
	 * 
	 * @param queue
	 *            the queue
	 */
	abstract void perform(final Queue<T> queue);

	/**
	 * Run.
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				perform(queue);
				sleepStrategy();
			} catch (Exception e) {
				e.printStackTrace();
			}// try-catch
		}// while
	}// run()
}// class
