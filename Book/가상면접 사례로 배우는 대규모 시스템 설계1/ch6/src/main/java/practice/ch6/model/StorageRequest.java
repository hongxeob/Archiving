package practice.ch6.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StorageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    // 저장할 값
    private byte[] value;

    // TTL (Time To Live) - 초 단위
    private Integer ttlSeconds;

    // 작업 타입 (PUT, GET, DELETE 등)
    private OperationType operationType;

    // 요청에 대한 추가 옵션
    private Map<String, String> options;

    // 요청에 대한 메타데이터
    private Map<String, String> metadata;

    // 요청 트레이스 ID (요청 추적용)
    private String traceId;

    /**
     * 기본 생성자
     */
    public StorageRequest() {
        this.options = new HashMap<>();
        this.metadata = new HashMap<>();
    }

    /**
     * 값만 설정하는 생성자
     *
     * @param value 저장할 값
     */
    public StorageRequest(byte[] value) {
        this();
        this.value = value;
    }

    /**
     * 값과 TTL을 설정하는 생성자
     *
     * @param value      저장할 값
     * @param ttlSeconds TTL (초)
     */
    public StorageRequest(byte[] value, Integer ttlSeconds) {
        this(value);
        this.ttlSeconds = ttlSeconds;
    }

    /**
     * 모든 필드를 설정하는 생성자
     *
     * @param value         저장할 값
     * @param ttlSeconds    TTL (초)
     * @param operationType 작업 타입
     */
    public StorageRequest(byte[] value, Integer ttlSeconds, OperationType operationType) {
        this(value, ttlSeconds);
        this.operationType = operationType;
    }

    /**
     * 옵션을 추가하는 메서드
     *
     * @param key   옵션 키
     * @param value 옵션 값
     * @return 자기 자신 (메서드 체이닝 지원)
     */
    public StorageRequest addOption(String key, String value) {
        this.options.put(key, value);
        return this;
    }

    /**
     * 메타데이터를 추가하는 메서드
     *
     * @param key   메타데이터 키
     * @param value 메타데이터 값
     * @return 자기 자신 (메서드 체이닝 지원)
     */
    public StorageRequest addMetadata(String key, String value) {
        this.metadata.put(key, value);
        return this;
    }

    /**
     * 특정 옵션이 설정되었는지 확인하는 메서드
     *
     * @param key 옵션 키
     * @return 옵션 존재 여부
     */
    public boolean hasOption(String key) {
        return this.options.containsKey(key);
    }

    /**
     * 특정 옵션 값을 가져오는 메서드
     *
     * @param key 옵션 키
     * @return 옵션 값 또는 null
     */
    public String getOption(String key) {
        return this.options.get(key);
    }

    /**
     * 특정 메타데이터가 설정되었는지 확인하는 메서드
     *
     * @param key 메타데이터 키
     * @return 메타데이터 존재 여부
     */
    public boolean hasMetadata(String key) {
        return this.metadata.containsKey(key);
    }

    /**
     * 특정 메타데이터 값을 가져오는 메서드
     *
     * @param key 메타데이터 키
     * @return 메타데이터 값 또는 null
     */
    public String getMetadata(String key) {
        return this.metadata.get(key);
    }

    // PUT 요청 생성 팩토리 메서드
    public static StorageRequest createPutRequest(byte[] value) {
        return new StorageRequest(value, null, OperationType.PUT);
    }

    // PUT 요청 생성 팩토리 메서드 (TTL 포함)
    public static StorageRequest createPutRequest(byte[] value, Integer ttlSeconds) {
        return new StorageRequest(value, ttlSeconds, OperationType.PUT);
    }

    // GET 요청 생성 팩토리 메서드
    public static StorageRequest createGetRequest() {
        return new StorageRequest(null, null, OperationType.GET);
    }

    // DELETE 요청 생성 팩토리 메서드
    public static StorageRequest createDeleteRequest() {
        return new StorageRequest(null, null, OperationType.DELETE);
    }

    // Getters and Setters

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public Integer getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(Integer ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    /**
     * 스토리지 작업 유형 열거형
     */
    public enum OperationType {
        PUT,
        GET,
        DELETE,
        EXISTS,
        INCREMENT,
        DECREMENT,
        APPEND,
        EXPIRE
    }

    @Override
    public String toString() {
        return "StorageRequest{" +
                "valueSize=" + (value != null ? value.length : 0) + " bytes" +
                ", ttlSeconds=" + ttlSeconds +
                ", operationType=" + operationType +
                ", options=" + options +
                ", metadata=" + metadata +
                ", traceId='" + traceId + '\'' +
                '}';
    }
}
