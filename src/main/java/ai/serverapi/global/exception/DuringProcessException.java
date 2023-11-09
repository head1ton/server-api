package ai.serverapi.global.exception;

public class DuringProcessException extends RuntimeException {

    public DuringProcessException(String message) {
        super(message);
    }

    public DuringProcessException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
