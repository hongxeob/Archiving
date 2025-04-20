package com.ch8.controller;

import com.ch8.controller.dto.ShortenUrlRequest;
import com.ch8.controller.dto.ShortenUrlResponse;
import com.ch8.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UrlShortenerController {
    private final UrlShortenerService urlShortenerService;

    @PostMapping("/shorten")
    public ResponseEntity<ShortenUrlResponse> shortenUrl(@RequestBody ShortenUrlRequest request) {
        final String shortenUrl = urlShortenerService.shortenUrl(request.originUrl());
        final ShortenUrlResponse response = new ShortenUrlResponse(shortenUrl, request.originUrl());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("{shortUrl}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortUrl) {
        final String originalUrl = urlShortenerService.getOriginalUrl(shortUrl);

        // 원본 URL이 없는 경우 404 Not Found 응답
        if (originalUrl == null) {
            return ResponseEntity.notFound().build();
        }

        // 302 Found 응답을 사용하여 리다이렉트
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
