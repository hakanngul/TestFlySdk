package com.testfly.sdk.exceptions;

public class RetryExhaustedException extends FrameworkException {

    private final int attempts;

    public RetryExhaustedException(String message, int attempts, Throwable cause) {
        super(message, cause);
        this.attempts = attempts;
    }

    public int getAttempts() {
        return attempts;
    }
}
