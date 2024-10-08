package hello.delivery.Exception;

public class TokenNotFoundException extends RuntimeException{
    public TokenNotFoundException() {
        super();
    }

    public TokenNotFoundException(String message) {
        super(message);
    }

    public TokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenNotFoundException(Throwable cause) {
        super(cause);
    }

    protected TokenNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
