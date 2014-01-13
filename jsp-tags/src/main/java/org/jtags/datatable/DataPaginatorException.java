
/**
 * The Class DataPaginatorException.
 */
public class DataPaginatorException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new data paginator exception.
	 */
	public DataPaginatorException() {
		super();
	}

	/**
	 * Instantiates a new data paginator exception.
	 * 
	 * @param message
	 *           the message
	 * @param cause
	 *           the cause
	 */
	public DataPaginatorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new data paginator exception.
	 * 
	 * @param message
	 *           the message
	 */
	public DataPaginatorException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new data paginator exception.
	 * 
	 * @param cause
	 *           the cause
	 */
	public DataPaginatorException(Throwable cause) {
		super(cause);
	}

}
