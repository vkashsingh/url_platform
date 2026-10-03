package com.urlplatform.urlservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public class CreateUrlRequest {

    @NotBlank(message = "Original URL is required")
    @URL(message = "Original URL must be a valid URL")
    private String originalUrl;

    @NotNull(message = "User ID is required")
    private Long userId;

    public CreateUrlRequest() {}

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
