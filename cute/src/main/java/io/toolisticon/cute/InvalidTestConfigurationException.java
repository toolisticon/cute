package io.toolisticon.cute;

/**
 * Exception thrown in case of an configuration error.
 */
public class InvalidTestConfigurationException extends RuntimeException {

    public InvalidTestConfigurationException(String message) {
        super(message);
    }

}
