package io.toolisticon.compiletesting;

/**
 * Exception to signal failing Assertion
 */
public class FailingAssertionException extends RuntimeException {

    public FailingAssertionException(String message) {
        super(message);
    }

    public FailingAssertionException(String message, Throwable e) {
        super(message,e);
    }

}
