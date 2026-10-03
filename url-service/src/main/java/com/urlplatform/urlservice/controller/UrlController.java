package com.urlplatform.urlservice.controller;

import com.urlplatform.urlservice.dto.CreateUrlRequest;
import com.urlplatform.urlservice.dto.UrlResponse;
import com.urlplatform.urlservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/urls")
@Tag(name = "URLs", description = "URL shortening APIs")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    @Operation(summary = "Create a shortened URL")
    public ResponseEntity<UrlResponse> createUrl(@Valid @RequestBody CreateUrlRequest request) {
        UrlResponse response = urlService.createUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all shortened URLs")
    public ResponseEntity<List<UrlResponse>> getAllUrls() {
        return ResponseEntity.ok(urlService.getAllUrls());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a URL by ID")
    public ResponseEntity<UrlResponse> getUrlById(@PathVariable Long id) {
        return ResponseEntity.ok(urlService.getUrlById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a shortened URL")
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id) {
        urlService.deleteUrl(id);
        return ResponseEntity.noContent().build();
    }
}
