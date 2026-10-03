package com.urlplatform.urlservice.dto;

import java.time.OffsetDateTime;

public class UrlResponse {

    private Long id;
    private String originalUrl;
    private String shortCode;
    private Long userId;
    private String shortUrl;
    private OffsetDateTime createdAt;

    public UrlResponse() {}

    public UrlResponse(Long id, String originalUrl, String shortCode,
                       Long userId, String shortUrl, OffsetDateTime createdAt) {
        this.id          = id;
        this.originalUrl = originalUrl;
        this.shortCode   = shortCode;
        this.userId      = userId;
        this.shortUrl    = shortUrl;
        this.createdAt   = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getShortUrl() { return shortUrl; }
    public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
