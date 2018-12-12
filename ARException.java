

public class ARException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4537287165678989320L;

	/**
	 * 
	 */
	public ARException() {
	}

	/**
	 * @param message
	 */
	public ARException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ARException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ARException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ARException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
