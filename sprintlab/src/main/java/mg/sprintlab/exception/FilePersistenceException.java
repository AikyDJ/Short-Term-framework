package mg.sprintlab.exception;

/**
 * FilePersistenceException
 */
public class FilePersistenceException extends RuntimeException {
    private static final int DEFAULT_ERROR_CODE = 500;
    private final int errorCode;

    private static final String ERROR_MESSAGE = "Error while persisting file";

    public FilePersistenceException() {
        this(ERROR_MESSAGE, DEFAULT_ERROR_CODE);
    }

    public FilePersistenceException(String message) {
        this(message, DEFAULT_ERROR_CODE);
    }

    public FilePersistenceException(String message, int errorCode) {
        super(errorCode + ": " + message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
