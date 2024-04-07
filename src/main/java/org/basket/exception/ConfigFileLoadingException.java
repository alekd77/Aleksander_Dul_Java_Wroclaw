package org.basket.exception;

public class ConfigFileLoadingException extends Exception {
    public ConfigFileLoadingException(String errorMessage) {
        super(errorMessage);
    }

    public ConfigFileLoadingException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
