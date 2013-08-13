package com.dev.codegen.exception;

/**
 * The Class CodegenException.
 */
public class CodegenException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new codegen exception.
	 */
	public CodegenException() {
		super();
	}

	/**
	 * Instantiates a new codegen exception.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public CodegenException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new codegen exception.
	 * 
	 * @param message
	 *            the message
	 */
	public CodegenException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new codegen exception.
	 * 
	 * @param cause
	 *            the cause
	 */
	public CodegenException(Throwable cause) {
		super(cause);
	}

}
