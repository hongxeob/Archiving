package com.ch8.cache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UrlCacheService {
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public void put(String shortUrl, String originalUrl) {
        cache.put(shortUrl, originalUrl);
    }

    public String get(String shortUrl) {
        return cache.get(shortUrl);
    }

    public void clear() {
        cache.clear();
    }
}
