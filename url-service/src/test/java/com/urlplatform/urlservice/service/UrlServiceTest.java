package com.urlplatform.urlservice.service;

import com.urlplatform.urlservice.dto.CreateUrlRequest;
import com.urlplatform.urlservice.dto.UrlResponse;
import com.urlplatform.urlservice.entity.Url;
import com.urlplatform.urlservice.exception.UrlNotFoundException;
import com.urlplatform.urlservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @InjectMocks
    private UrlService urlService;

    private Url sampleUrl;

    @BeforeEach
    void setUp() {
        sampleUrl = new Url();
        sampleUrl.setId(1L);
        sampleUrl.setOriginalUrl("https://www.example.com/some/long/url");
        sampleUrl.setShortCode("aB12xY");
        sampleUrl.setUserId(1L);
        sampleUrl.setCreatedAt(OffsetDateTime.now());
    }

    // ---- Create URL ----

    @Test
    void createUrl_Success() {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setOriginalUrl("https://www.example.com/some/long/url");
        request.setUserId(1L);

        when(shortCodeGenerator.generate()).thenReturn("aB12xY");
        when(urlRepository.existsByShortCode("aB12xY")).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenReturn(sampleUrl);

        UrlResponse response = urlService.createUrl(request);

        assertThat(response).isNotNull();
        assertThat(response.getShortCode()).isEqualTo("aB12xY");
        assertThat(response.getShortUrl()).isEqualTo("/aB12xY");
        assertThat(response.getUserId()).isEqualTo(1L);
    }

    @Test
    void createUrl_GeneratesUniqueShortCode_HandlesCollision() {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setOriginalUrl("https://example.com");
        request.setUserId(1L);

        // First code collides, second is unique
        when(shortCodeGenerator.generate())
                .thenReturn("XXXXXX")
                .thenReturn("aB12xY");
        when(urlRepository.existsByShortCode("XXXXXX")).thenReturn(true);
        when(urlRepository.existsByShortCode("aB12xY")).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenReturn(sampleUrl);

        UrlResponse response = urlService.createUrl(request);

        assertThat(response.getShortCode()).isEqualTo("aB12xY");
        verify(shortCodeGenerator, times(2)).generate();
    }

    // ---- Get URL ----

    @Test
    void getUrlById_Success() {
        when(urlRepository.findById(1L)).thenReturn(Optional.of(sampleUrl));

        UrlResponse response = urlService.getUrlById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOriginalUrl()).isEqualTo("https://www.example.com/some/long/url");
    }

    @Test
    void getUrlById_NotFound_ThrowsException() {
        when(urlRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.getUrlById(99L))
                .isInstanceOf(UrlNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getAllUrls_ReturnsList() {
        when(urlRepository.findAll()).thenReturn(List.of(sampleUrl));

        List<UrlResponse> urls = urlService.getAllUrls();

        assertThat(urls).hasSize(1);
        assertThat(urls.get(0).getShortCode()).isEqualTo("aB12xY");
    }

    // ---- Redirect ----

    @Test
    void getOriginalUrlByShortCode_Success() {
        when(urlRepository.findByShortCode("aB12xY")).thenReturn(Optional.of(sampleUrl));

        String originalUrl = urlService.getOriginalUrlByShortCode("aB12xY");

        assertThat(originalUrl).isEqualTo("https://www.example.com/some/long/url");
    }

    @Test
    void getOriginalUrlByShortCode_NotFound_ThrowsException() {
        when(urlRepository.findByShortCode("ZZZZZZ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.getOriginalUrlByShortCode("ZZZZZZ"))
                .isInstanceOf(UrlNotFoundException.class)
                .hasMessageContaining("ZZZZZZ");
    }

    // ---- Delete URL ----

    @Test
    void deleteUrl_Success() {
        when(urlRepository.existsById(1L)).thenReturn(true);

        urlService.deleteUrl(1L);

        verify(urlRepository).deleteById(1L);
    }

    @Test
    void deleteUrl_NotFound_ThrowsException() {
        when(urlRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> urlService.deleteUrl(99L))
                .isInstanceOf(UrlNotFoundException.class);
    }
}
