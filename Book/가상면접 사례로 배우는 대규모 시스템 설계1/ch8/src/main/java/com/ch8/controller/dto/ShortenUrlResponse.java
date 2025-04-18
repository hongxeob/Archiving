package com.ch8.controller.dto;

public record ShortenUrlResponse(
        String shortenUrl,
        String originUrl
) {
}
