package mg.sprintlab.exception;

public class AuthException extends RuntimeException {
    private static final int DEFAULT_ERROR_CODE = 401;
    private final int errorCode;

    private static final String ERROR_MESSAGE = "not authorized to access this resource";

    public AuthException() {
        this(ERROR_MESSAGE, DEFAULT_ERROR_CODE);
    }

    public AuthException(String message) {
        this(message, DEFAULT_ERROR_CODE);
    }

    public AuthException(String message, int errorCode) {
        super(errorCode + ": " + message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
