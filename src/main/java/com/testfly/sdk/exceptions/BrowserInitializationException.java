package com.testfly.sdk.exceptions;

public class BrowserInitializationException extends FrameworkException {

    public BrowserInitializationException(String message) {
        super(message);
    }

    public BrowserInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
