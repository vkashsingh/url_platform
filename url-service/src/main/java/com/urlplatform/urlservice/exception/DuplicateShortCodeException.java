package com.urlplatform.urlservice.exception;

public class DuplicateShortCodeException extends RuntimeException {

    public DuplicateShortCodeException(String shortCode) {
        super("Short code '" + shortCode + "' already exists");
    }
}
