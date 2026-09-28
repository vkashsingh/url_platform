package com.urlplatform.urlservice.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Generates unique 6-character URL-safe short codes.
 * Characters: A-Z, a-z, 0-9 (62 chars → ~56 billion combinations).
 */
@Service
public class ShortCodeGenerator {

    private static final String ALPHABET =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
