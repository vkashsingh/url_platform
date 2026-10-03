package com.urlplatform.urlservice.service;

import com.urlplatform.urlservice.dto.CreateUrlRequest;
import com.urlplatform.urlservice.dto.UrlResponse;
import com.urlplatform.urlservice.entity.Url;
import com.urlplatform.urlservice.exception.UrlNotFoundException;
import com.urlplatform.urlservice.repository.UrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UrlService {

    private static final int MAX_COLLISION_RETRIES = 10;

    private final UrlRepository urlRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    public UrlService(UrlRepository urlRepository, ShortCodeGenerator shortCodeGenerator) {
        this.urlRepository    = urlRepository;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    public UrlResponse createUrl(CreateUrlRequest request) {
        String shortCode = generateUniqueShortCode();

        Url url = new Url();
        url.setOriginalUrl(request.getOriginalUrl());
        url.setShortCode(shortCode);
        url.setUserId(request.getUserId());

        Url saved = urlRepository.save(url);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UrlResponse> getAllUrls() {
        return urlRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UrlResponse getUrlById(Long id) {
        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new UrlNotFoundException(id));
        return toResponse(url);
    }

    @Transactional(readOnly = true)
    public String getOriginalUrlByShortCode(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));
        return url.getOriginalUrl();
    }

    public void deleteUrl(Long id) {
        if (!urlRepository.existsById(id)) {
            throw new UrlNotFoundException(id);
        }
        urlRepository.deleteById(id);
    }

    // ---- Helpers ----

    private String generateUniqueShortCode() {
        for (int attempt = 0; attempt < MAX_COLLISION_RETRIES; attempt++) {
            String code = shortCodeGenerator.generate();
            if (!urlRepository.existsByShortCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException(
                "Could not generate a unique short code after " + MAX_COLLISION_RETRIES + " attempts");
    }

    private UrlResponse toResponse(Url url) {
        return new UrlResponse(
                url.getId(),
                url.getOriginalUrl(),
                url.getShortCode(),
                url.getUserId(),
                "/" + url.getShortCode(),
                url.getCreatedAt()
        );
    }
}
