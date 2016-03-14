package at.ac.imp.exceptions;

public class LineMalformedException extends Exception {

	private static final long serialVersionUID = 1L;

	public LineMalformedException() {
		super();
	}

	public LineMalformedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LineMalformedException(String message, Throwable cause) {
		super(message, cause);
	}

	public LineMalformedException(String message) {
		super(message);
	}

	public LineMalformedException(Throwable cause) {
		super(cause);
	}

}
