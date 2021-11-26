package com.nikondsl.cache;

public class CompactingException extends Exception {
    public CompactingException() {
        super();
    }

    public CompactingException(String message) {
        super(message);
    }

    public CompactingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompactingException(Throwable cause) {
        super(cause);
    }

    protected CompactingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
