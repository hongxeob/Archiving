package practice.ch6.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

public class VersionedValue implements Serializable, Comparable<VersionedValue> {

    private static final long serialVersionUID = 1L;

    // 전역 버전 카운터 (클러스터 전체에서는 사용하지 않고, 단일 노드 테스트 시에만 사용)
    private static final AtomicLong VERSION_COUNTER = new AtomicLong(0);

    // 실제 값 데이터
    private byte[] value;

    // 값의 버전 번호 (동일 키에 대한 업데이트마다 증가)
    private long version;

    // 벡터 시계 (분산 시스템에서 인과성 추적용, 선택적 사용)
    private String vectorClock;

    // 생성 시간
    private Instant createdAt;

    // 마지막 수정 시간
    private Instant lastModifiedAt;

    // TTL (초), null인 경우 무기한
    private Integer ttlSeconds;

    // 마지막 수정 노드 ID
    private String lastModifiedBy;

    // CRC32 체크섬 (데이터 무결성 검증용)
    private Long checksum;

    /**
     * 기본 생성자
     */
    public VersionedValue() {
        this.createdAt = Instant.now();
        this.lastModifiedAt = Instant.now();
    }

    /**
     * 값과 버전을 지정하는 생성자
     *
     * @param value   값 데이터
     * @param version 버전 번호
     */
    public VersionedValue(byte[] value, long version) {
        this();
        this.value = value;
        this.version = version;
        calculateChecksum();
    }

    /**
     * 값, 버전, TTL을 지정하는 생성자
     *
     * @param value      값 데이터
     * @param version    버전 번호
     * @param ttlSeconds TTL(초), null인 경우 무기한
     */
    public VersionedValue(byte[] value, long version, Integer ttlSeconds) {
        this(value, version);
        this.ttlSeconds = ttlSeconds;
    }

    /**
     * 모든 필드를 지정하는 생성자
     *
     * @param value          값 데이터
     * @param version        버전 번호
     * @param vectorClock    벡터 시계
     * @param createdAt      생성 시간
     * @param lastModifiedAt 마지막 수정 시간
     * @param ttlSeconds     TTL(초)
     * @param lastModifiedBy 마지막 수정 노드 ID
     */
    public VersionedValue(
            byte[] value,
            long version,
            String vectorClock,
            Instant createdAt,
            Instant lastModifiedAt,
            Integer ttlSeconds,
            String lastModifiedBy) {
        this.value = value;
        this.version = version;
        this.vectorClock = vectorClock;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.ttlSeconds = ttlSeconds;
        this.lastModifiedBy = lastModifiedBy;
        calculateChecksum();
    }

    /**
     * 새 버전의 값 객체를 생성합니다.
     *
     * @param newValue 새 값 데이터
     * @return 새 버전의 값 객체
     */
    public VersionedValue createNextVersion(byte[] newValue) {
        return new VersionedValue(
                newValue,
                this.version + 1,
                updateVectorClock(),
                this.createdAt,
                Instant.now(),
                this.ttlSeconds,
                this.lastModifiedBy
        );
    }

    /**
     * 새 버전 및 TTL이 적용된 값 객체를 생성합니다.
     *
     * @param newValue      새 값 데이터
     * @param newTtlSeconds 새 TTL(초)
     * @return 새 버전의 값 객체
     */
    public VersionedValue createNextVersion(byte[] newValue, Integer newTtlSeconds) {
        return new VersionedValue(
                newValue,
                this.version + 1,
                updateVectorClock(),
                this.createdAt,
                Instant.now(),
                newTtlSeconds,
                this.lastModifiedBy
        );
    }

    /**
     * 전역 카운터로부터 새 버전 번호를 생성합니다.
     * 참고: 실제 분산 시스템에서는 사용하지 않고, 단일 노드 테스트용으로만 사용
     *
     * @return 새 버전 번호
     */
    public static long generateNewVersion() {
        return VERSION_COUNTER.incrementAndGet();
    }

    /**
     * 값이 만료되었는지 확인합니다.
     *
     * @return 만료 여부
     */
    public boolean isExpired() {
        if (ttlSeconds == null || ttlSeconds <= 0) {
            return false; // TTL이 없거나 0 이하이면 만료되지 않음
        }

        Instant expiryTime = lastModifiedAt.plusSeconds(ttlSeconds);
        return Instant.now().isAfter(expiryTime);
    }

    /**
     * 만료까지 남은 시간(초)을 계산합니다.
     *
     * @return 남은 시간(초), 이미 만료되었으면 0, TTL이 없으면 -1
     */
    public long getTimeToLive() {
        if (ttlSeconds == null || ttlSeconds <= 0) {
            return -1; // TTL이 없음
        }

        Instant expiryTime = lastModifiedAt.plusSeconds(ttlSeconds);
        long secondsLeft = Instant.now().until(expiryTime, java.time.temporal.ChronoUnit.SECONDS);
        return Math.max(0, secondsLeft);
    }

    /**
     * 체크섬을 계산하여 설정합니다.
     * 데이터 무결성 검증에 사용됩니다.
     */
    private void calculateChecksum() {
        if (value == null) {
            this.checksum = 0L;
            return;
        }

        // CRC32 체크섬 계산
        java.util.zip.CRC32 crc = new java.util.zip.CRC32();
        crc.update(value);
        this.checksum = crc.getValue();
    }

    /**
     * 데이터의 체크섬을 검증합니다.
     *
     * @return 체크섬 유효 여부
     */
    public boolean validateChecksum() {
        if (value == null || checksum == null) {
            return false;
        }

        java.util.zip.CRC32 crc = new java.util.zip.CRC32();
        crc.update(value);
        return crc.getValue() == checksum;
    }

    /**
     * 벡터 시계를 업데이트합니다.
     *
     * @return 업데이트된 벡터 시계
     */
    private String updateVectorClock() {
        // 실제 구현에서는 현재 노드의 카운터를 증가시키는 등의 로직이 필요함
        // 여기서는 간단히 타임스탬프로 대체
        return vectorClock != null ?
                vectorClock + ";" + System.currentTimeMillis() :
                String.valueOf(System.currentTimeMillis());
    }

    /**
     * 두 값 객체의 충돌 여부를 확인합니다.
     *
     * @param other 비교할 값 객체
     * @return 충돌 여부
     */
    public boolean conflictsWith(VersionedValue other) {
        // 버전이 같고 데이터가 다르면 충돌
        return this.version == other.version && !Arrays.equals(this.value, other.value);
    }

    /**
     * 최신 버전을 결정합니다.
     *
     * @param v1 첫 번째 값 객체
     * @param v2 두 번째 값 객체
     * @return 최신 버전의 값 객체
     */
    public static VersionedValue resolveConflict(VersionedValue v1, VersionedValue v2) {
        if (v1 == null) return v2;
        if (v2 == null) return v1;

        // 버전 번호가 높은 것을 선택
        if (v1.getVersion() > v2.getVersion()) {
            return v1;
        } else if (v2.getVersion() > v1.getVersion()) {
            return v2;
        }

        // 버전이 같으면 수정 시간이 최신인 것을 선택
        return v1.getLastModifiedAt().isAfter(v2.getLastModifiedAt()) ? v1 : v2;
    }

    // Getters and Setters

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
        this.lastModifiedAt = Instant.now();
        calculateChecksum();
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getVectorClock() {
        return vectorClock;
    }

    public void setVectorClock(String vectorClock) {
        this.vectorClock = vectorClock;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(Instant lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public Integer getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(Integer ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Long getChecksum() {
        return checksum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VersionedValue that = (VersionedValue) o;

        if (version != that.version) return false;
        if (!Arrays.equals(value, that.value)) return false;
        if (vectorClock != null ? !vectorClock.equals(that.vectorClock) : that.vectorClock != null) return false;
        if (createdAt != null ? !createdAt.equals(that.createdAt) : that.createdAt != null) return false;
        if (lastModifiedAt != null ? !lastModifiedAt.equals(that.lastModifiedAt) : that.lastModifiedAt != null)
            return false;
        if (ttlSeconds != null ? !ttlSeconds.equals(that.ttlSeconds) : that.ttlSeconds != null) return false;
        return lastModifiedBy != null ? lastModifiedBy.equals(that.lastModifiedBy) : that.lastModifiedBy == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(value);
        result = 31 * result + (int) (version ^ (version >>> 32));
        result = 31 * result + (vectorClock != null ? vectorClock.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (lastModifiedAt != null ? lastModifiedAt.hashCode() : 0);
        result = 31 * result + (ttlSeconds != null ? ttlSeconds.hashCode() : 0);
        result = 31 * result + (lastModifiedBy != null ? lastModifiedBy.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(VersionedValue other) {
        // 버전 번호로 비교
        return Long.compare(this.version, other.version);
    }

    @Override
    public String toString() {
        return "VersionedValue{" +
                "valueSize=" + (value != null ? value.length : 0) + " bytes" +
                ", version=" + version +
                ", vectorClock='" + vectorClock + '\'' +
                ", createdAt=" + createdAt +
                ", lastModifiedAt=" + lastModifiedAt +
                ", ttlSeconds=" + ttlSeconds +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", checksum=" + checksum +
                '}';
    }
}
