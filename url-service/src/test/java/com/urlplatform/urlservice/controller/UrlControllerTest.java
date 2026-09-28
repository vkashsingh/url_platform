package com.urlplatform.urlservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlplatform.urlservice.dto.CreateUrlRequest;
import com.urlplatform.urlservice.dto.UrlResponse;
import com.urlplatform.urlservice.exception.UrlNotFoundException;
import com.urlplatform.urlservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({UrlController.class, RedirectController.class})
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UrlService urlService;

    private UrlResponse sampleResponse() {
        return new UrlResponse(1L, "https://www.example.com/some/long/url",
                "aB12xY", 1L, "/aB12xY", OffsetDateTime.now());
    }

    @Test
    void createUrl_Returns201() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setOriginalUrl("https://www.example.com/some/long/url");
        request.setUserId(1L);

        when(urlService.createUrl(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("aB12xY"))
                .andExpect(jsonPath("$.shortUrl").value("/aB12xY"));
    }

    @Test
    void createUrl_InvalidUrl_Returns400() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setOriginalUrl("not-a-url");
        request.setUserId(1L);

        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createUrl_MissingUserId_Returns400() throws Exception {
        CreateUrlRequest request = new CreateUrlRequest();
        request.setOriginalUrl("https://example.com");
        // userId intentionally null

        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getAllUrls_Returns200() throws Exception {
        when(urlService.getAllUrls()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shortCode").value("aB12xY"));
    }

    @Test
    void getUrlById_Returns200() throws Exception {
        when(urlService.getUrlById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/urls/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUrlById_NotFound_Returns404() throws Exception {
        when(urlService.getUrlById(99L)).thenThrow(new UrlNotFoundException(99L));

        mockMvc.perform(get("/api/urls/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteUrl_Returns204() throws Exception {
        mockMvc.perform(delete("/api/urls/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void redirect_Returns302() throws Exception {
        when(urlService.getOriginalUrlByShortCode("aB12xY"))
                .thenReturn("https://www.example.com/some/long/url");

        mockMvc.perform(get("/aB12xY"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.example.com/some/long/url"));
    }

    @Test
    void redirect_NotFound_Returns404() throws Exception {
        when(urlService.getOriginalUrlByShortCode("ZZZZZZ"))
                .thenThrow(new UrlNotFoundException("ZZZZZZ"));

        mockMvc.perform(get("/ZZZZZZ"))
                .andExpect(status().isNotFound());
    }
}
