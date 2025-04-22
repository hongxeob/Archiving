package com.ch8.service;

import com.ch8.cache.UrlCacheService;
import com.ch8.model.UrlMapping;
import com.ch8.repository.UrlRepository;
import com.ch8.util.Base62Encode;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {
    private final UrlRepository urlRepository;
    private final Base62Encode base62Encode;
    private final UrlCacheService urlCacheService;

    private final AtomicLong counter = new AtomicLong(1000);

    public String shortenUrl(String originUrl) {
        // URL이 이미 단축된 경우 기존 단축 URL 반환
        final UrlMapping existingMapping = urlRepository.findByLongUrl(originUrl);
        if (existingMapping != null) {
            return existingMapping.shortUrl();
        }

        // 새로운 ID 생성
        long id = counter.incrementAndGet();
        // Base62 인코딩
        String shortUrl = base62Encode.encode(id);

        // URL 매핑 저장
        urlRepository.save(new UrlMapping(id, shortUrl, originUrl));

        return shortUrl;
    }

    /**
     * 단축 URL로 원본 URL로 조회
     *
     * @param shortUrl
     * @return 원본 URL
     */
    public String getOriginalUrl(String shortUrl) {
        String cachedUrl = urlCacheService.get(shortUrl);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        UrlMapping urlMapping = urlRepository.findByShortUrl(shortUrl);
        if (urlMapping == null) {
            return null;
        }
        urlCacheService.put(shortUrl, urlMapping.originalUrl());

        return urlMapping.originalUrl();
    }
}
