package com.dev.codegen.exception;

/**
 * The type Codegen exception.
 */
public class CodegenException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new Codegen exception.
     */
    public CodegenException() {
        super();
    }

    /**
     * Instantiates a new Codegen exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public CodegenException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Codegen exception.
     *
     * @param message the message
     */
    public CodegenException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Codegen exception.
     *
     * @param cause the cause
     */
    public CodegenException(Throwable cause) {
        super(cause);
    }

}
