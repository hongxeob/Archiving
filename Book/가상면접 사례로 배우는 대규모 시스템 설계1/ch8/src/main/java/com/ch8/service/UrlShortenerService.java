package com.ch8.service;

import com.ch8.model.UrlMapping;
import com.ch8.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {
    private final UrlRepository urlRepository;

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
}
