package com.ch8.controller;

import com.ch8.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UrlShortenerController {
    private final UrlShortenerService urlShortenerService;


}
