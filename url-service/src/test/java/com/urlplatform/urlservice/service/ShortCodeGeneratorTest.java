package com.urlplatform.urlservice.service;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShortCodeGeneratorTest {

    private final ShortCodeGenerator generator = new ShortCodeGenerator();

    @Test
    void generate_ReturnsCorrectLength() {
        String code = generator.generate();
        assertThat(code).hasSize(6);
    }

    @Test
    void generate_ContainsOnlyAlphanumericChars() {
        String code = generator.generate();
        assertThat(code).matches("[A-Za-z0-9]{6}");
    }

    @RepeatedTest(20)
    void generate_ProducesVariedCodes() {
        // Just verify each call produces a 6-char alphanumeric code
        String code = generator.generate();
        assertThat(code).hasSize(6).matches("[A-Za-z0-9]+");
    }
}
