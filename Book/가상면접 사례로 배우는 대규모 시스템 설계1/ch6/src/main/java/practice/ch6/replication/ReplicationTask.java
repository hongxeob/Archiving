package practice.ch6.replication;


import java.util.ArrayList;
import java.util.List;

/**
 * 복제 작업을 추적하기 위한 클래스
 * 복제가 실패한 경우 나중에 재시도하기 위한 정보를 저장
 */
public class ReplicationTask {
    // 복제 작업 고유 식별자
    private final String taskId;

    // 복제할 키
    private final String key;

    // 복제할 값
    private final byte[] value;

    // TTL 값 (초)
    private final int ttlSeconds;

    // 복제 대상 노드 목록
    private List<String> targetNodes;

    // 작업 생성 시간
    private final long createdAt;

    // 마지막 시도 시간
    private long lastAttemptedAt;

    // 시도 횟수
    private int attemptCount;

    // 최대 재시도 횟수
    private final int maxRetries;

    /**
     * 복제 작업 생성자
     *
     * @param taskId      작업 식별자
     * @param key         복제할 키
     * @param value       복제할 값
     * @param ttlSeconds  TTL 값 (초)
     * @param targetNodes 복제 대상 노드 목록
     * @param createdAt   작업 생성 시간 (밀리초 타임스탬프)
     */
    public ReplicationTask(
            String taskId,
            String key,
            byte[] value,
            int ttlSeconds,
            List<String> targetNodes,
            long createdAt) {
        this(taskId, key, value, ttlSeconds, targetNodes, createdAt, 3);
    }

    /**
     * 복제 작업 생성자
     *
     * @param taskId      작업 식별자
     * @param key         복제할 키
     * @param value       복제할 값
     * @param ttlSeconds  TTL 값 (초)
     * @param targetNodes 복제 대상 노드 목록
     * @param createdAt   작업 생성 시간 (밀리초 타임스탬프)
     * @param maxRetries  최대 재시도 횟수
     */
    public ReplicationTask(
            String taskId,
            String key,
            byte[] value,
            int ttlSeconds,
            List<String> targetNodes,
            long createdAt,
            int maxRetries) {
        this.taskId = taskId;
        this.key = key;
        this.value = value;
        this.ttlSeconds = ttlSeconds;
        this.targetNodes = new ArrayList<>(targetNodes);
        this.createdAt = createdAt;
        this.lastAttemptedAt = createdAt;
        this.attemptCount = 0;
        this.maxRetries = maxRetries;
    }

    /**
     * 복제 작업 시도 횟수를 증가시키고 마지막 시도 시간을 업데이트합니다.
     */
    public void markAttempted() {
        this.attemptCount++;
        this.lastAttemptedAt = System.currentTimeMillis();
    }

    /**
     * 최대 재시도 횟수에 도달했는지 확인합니다.
     *
     * @return 최대 재시도 횟수 초과 여부
     */
    public boolean hasExceededMaxRetries() {
        return attemptCount >= maxRetries;
    }

    /**
     * 특정 시간(밀리초) 이후에 생성된 작업인지 확인합니다.
     *
     * @param timestamp 비교할 시간(밀리초)
     * @return 지정된 시간 이후에 생성되었는지 여부
     */
    public boolean isCreatedAfter(long timestamp) {
        return createdAt > timestamp;
    }

    /**
     * 작업이 특정 기간(밀리초) 이상 경과했는지 확인합니다.
     *
     * @param duration 기간(밀리초)
     * @return 지정된 기간보다 오래되었는지 여부
     */
    public boolean isOlderThan(long duration) {
        return System.currentTimeMillis() - createdAt > duration;
    }

    /**
     * 마지막 시도 이후 특정 기간(밀리초) 이상 경과했는지 확인합니다.
     *
     * @param duration 기간(밀리초)
     * @return 마지막 시도 후 지정된 기간이 경과했는지 여부
     */
    public boolean canRetryAfter(long duration) {
        return System.currentTimeMillis() - lastAttemptedAt > duration;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getKey() {
        return key;
    }

    public byte[] getValue() {
        return value;
    }

    public int getTtlSeconds() {
        return ttlSeconds;
    }

    public List<String> getTargetNodes() {
        return targetNodes;
    }

    public void setTargetNodes(List<String> targetNodes) {
        this.targetNodes = new ArrayList<>(targetNodes);
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getLastAttemptedAt() {
        return lastAttemptedAt;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    @Override
    public String toString() {
        return "ReplicationTask{" +
                "taskId='" + taskId + '\'' +
                ", key='" + key + '\'' +
                ", ttlSeconds=" + ttlSeconds +
                ", targetNodes=" + targetNodes +
                ", attemptCount=" + attemptCount +
                ", createdAt=" + createdAt +
                '}';
    }
}
