package mg.sprintlab.exception;

public class AuthException extends RuntimeException {
    private static final int ERROR_CODE = 401;
    private static final String ERROR_MESSAGE = "not authorized to access this resource";

    public AuthException() {
        super(ERROR_CODE + ": " + ERROR_MESSAGE);
    }

    public AuthException(String message) {
        super(ERROR_CODE + ": " + message);
    }
    public AuthException(String message, int errorCode) {
        super(errorCode + ": " + message);
    }
    
}
