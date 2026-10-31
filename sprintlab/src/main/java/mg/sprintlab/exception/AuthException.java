package mg.sprintlab.exception;

public class AuthException extends RuntimeException {
    private static final int ERROR_CODE = 401;
    private static final String ERROR_MESSAGE = "Unauthorized";

    public AuthException() {
        super("ERROR " + ERROR_CODE + ": " + ERROR_MESSAGE);
    }

    public AuthException(String message) {
        super("ERROR " + ERROR_CODE + ": " + message);
    }
    
}
