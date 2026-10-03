package com.urlplatform.urlservice.exception;

public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException(Long id) {
        super("URL not found with id: " + id);
    }

    public UrlNotFoundException(String shortCode) {
        super("URL not found for short code: " + shortCode);
    }
}
