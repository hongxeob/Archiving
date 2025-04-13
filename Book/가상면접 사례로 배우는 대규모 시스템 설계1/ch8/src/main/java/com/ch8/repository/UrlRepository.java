package com.ch8.repository;

import com.ch8.model.UrlMapping;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class UrlRepository {

    /**
     * idToUrlMap: ID를 키로 사용하여 URL 매핑을 저장한다.
     * - Base62로 인코딩된 ID를 원래 ID로 복원할 때 필요
     * - ID 기반 조회 연산의 시간 복잡도를 O(1)로 유지
     */
    private final Map<Long, UrlMapping> idToUrlMap = new ConcurrentHashMap<>();
    /**
     * shortToUrlMap: 단축 URL을 키로 사용하여 URL 매핑을 저장
     * - 리다이렉트 요청 시 단축 URL로 원본 URL을 빠르게 조회하기 위함
     * - 가장 빈번한 조회 패턴인 "단축 URL → 원본 URL" 변환을 O(1) 시간에 수행
     */
    private final Map<String, UrlMapping> shortToUrlMap = new ConcurrentHashMap<>();
    /**
     * longToUrlMap: 원본 URL을 키로 사용하여 URL 매핑을 저장
     * - 중복 생성을 방지하기 위한 인덱스
     * - 이미 변환된 URL이 다시 요청되었을 때 기존 단축 URL을 재사용할 수 있다.
     * - 원본 URL 기반 조회를 O(1) 시간에 수행
     */
    private final Map<String, UrlMapping> longToUrlMap = new ConcurrentHashMap<>();

    public void save(UrlMapping urlMapping) {
        idToUrlMap.put(urlMapping.id(), urlMapping);
        shortToUrlMap.put(urlMapping.shortUrl(), urlMapping);
        longToUrlMap.put(urlMapping.originalUrl(), urlMapping);
    }
}
