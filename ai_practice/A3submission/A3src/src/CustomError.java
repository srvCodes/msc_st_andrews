public class CustomError extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CustomError() {
		super();
	}
	
	public CustomError(String message) {
		super(message);
	}

    public CustomError(Throwable cause) {
        super(cause);
    }

    public CustomError(String message, Throwable cause) {
        super(message, cause);
    }

}
