package practice.ch6.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "kvstore.cluster")
@Validated
public class ClusterConfig {
    /**
     * 현재 노드의 이름
     */
    @NotEmpty
    private String nodeName;

    /**
     * 현재 노드의 역할
     */
    @NotNull
    private String nodeRole = "SECONDARY";

    /**
     * 클러스터 발견을 위한 엔드포인트 목록
     */
    private List<String> discoveryEndpoints = new ArrayList<>();

    /**
     * 데이터 복제 인수 (복제본 수)
     */
    @Min(1)
    private int replicationFactor = 3;

    /**
     * 읽기 쿼럼 크기
     */
    @Min(1)
    private int readQuorum = 2;

    /**
     * 쓰기 쿼럼 크기
     */
    @Min(1)
    private int writeQuorum = 2;

    /**
     * 노드 간 요청 타임아웃 (밀리초)
     */
    @Min(100)
    private int requestTimeoutMs = 5000;

    /**
     * 하트비트 간격 (밀리초)
     */
    @Min(1000)
    private int heartbeatIntervalMs = 10000;

    /**
     * 노드 실패로 간주하기 전 누락된 하트비트 수
     */
    @Min(1)
    private int failureDetectionThreshold = 3;

    /**
     * 가상 노드 수 (일관된 해싱에서 사용)
     */
    @Min(10)
    private int virtualNodeCount = 256;

    /**
     * 데이터 재조정 배치 크기
     */
    @Min(1)
    private int rebalancingBatchSize = 1000;

    /**
     * 데이터 재조정 간 지연 시간 (밀리초)
     */
    @Min(0)
    private int rebalancingDelayMs = 50;

    /**
     * 자동 복제 동기화 활성화 여부
     */
    private boolean autoSyncEnabled = true;

    /**
     * 자동 장애 복구 활성화 여부
     */
    private boolean autoRecoveryEnabled = true;

    /**
     * 노드 참여 프로토콜 타임아웃 (밀리초)
     */
    @Min(1000)
    private int nodeJoinTimeoutMs = 30000;

    /**
     * 최대 동시 요청 수
     */
    @Min(1)
    private int maxConcurrentRequests = 5000;

    // 게터 및 세터 메서드

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeRole() {
        return nodeRole;
    }

    public void setNodeRole(String nodeRole) {
        this.nodeRole = nodeRole;
    }

    public List<String> getDiscoveryEndpoints() {
        return discoveryEndpoints;
    }

    public void setDiscoveryEndpoints(List<String> discoveryEndpoints) {
        this.discoveryEndpoints = discoveryEndpoints;
    }

    public int getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(int replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public int getReadQuorum() {
        return readQuorum;
    }

    public void setReadQuorum(int readQuorum) {
        this.readQuorum = readQuorum;
    }

    public int getWriteQuorum() {
        return writeQuorum;
    }

    public void setWriteQuorum(int writeQuorum) {
        this.writeQuorum = writeQuorum;
    }

    public int getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(int requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public int getHeartbeatIntervalMs() {
        return heartbeatIntervalMs;
    }

    public void setHeartbeatIntervalMs(int heartbeatIntervalMs) {
        this.heartbeatIntervalMs = heartbeatIntervalMs;
    }

    public int getFailureDetectionThreshold() {
        return failureDetectionThreshold;
    }

    public void setFailureDetectionThreshold(int failureDetectionThreshold) {
        this.failureDetectionThreshold = failureDetectionThreshold;
    }

    public int getVirtualNodeCount() {
        return virtualNodeCount;
    }

    public void setVirtualNodeCount(int virtualNodeCount) {
        this.virtualNodeCount = virtualNodeCount;
    }

    public int getRebalancingBatchSize() {
        return rebalancingBatchSize;
    }

    public void setRebalancingBatchSize(int rebalancingBatchSize) {
        this.rebalancingBatchSize = rebalancingBatchSize;
    }

    public int getRebalancingDelayMs() {
        return rebalancingDelayMs;
    }

    public void setRebalancingDelayMs(int rebalancingDelayMs) {
        this.rebalancingDelayMs = rebalancingDelayMs;
    }

    public boolean isAutoSyncEnabled() {
        return autoSyncEnabled;
    }

    public void setAutoSyncEnabled(boolean autoSyncEnabled) {
        this.autoSyncEnabled = autoSyncEnabled;
    }

    public boolean isAutoRecoveryEnabled() {
        return autoRecoveryEnabled;
    }

    public void setAutoRecoveryEnabled(boolean autoRecoveryEnabled) {
        this.autoRecoveryEnabled = autoRecoveryEnabled;
    }

    public int getNodeJoinTimeoutMs() {
        return nodeJoinTimeoutMs;
    }

    public void setNodeJoinTimeoutMs(int nodeJoinTimeoutMs) {
        this.nodeJoinTimeoutMs = nodeJoinTimeoutMs;
    }

    public int getMaxConcurrentRequests() {
        return maxConcurrentRequests;
    }

    public void setMaxConcurrentRequests(int maxConcurrentRequests) {
        this.maxConcurrentRequests = maxConcurrentRequests;
    }

    /**
     * 설정의 유효성을 검증합니다.
     * 특히 쿼럼 설정이 올바른지 확인합니다.
     */
    public void validateConfiguration() {
        // W + R > N 조건 확인 (강한 일관성 보장)
        if (writeQuorum + readQuorum <= replicationFactor) {
            throw new IllegalStateException(
                    "Invalid quorum configuration: writeQuorum(" + writeQuorum + ") + " +
                            "readQuorum(" + readQuorum + ") must be > replicationFactor(" + replicationFactor + ")");
        }

        // W <= N 조건 확인
        if (writeQuorum > replicationFactor) {
            throw new IllegalStateException(
                    "Invalid quorum configuration: writeQuorum(" + writeQuorum + ") " +
                            "must be <= replicationFactor(" + replicationFactor + ")");
        }

        // R <= N 조건 확인
        if (readQuorum > replicationFactor) {
            throw new IllegalStateException(
                    "Invalid quorum configuration: readQuorum(" + readQuorum + ") " +
                            "must be <= replicationFactor(" + replicationFactor + ")");
        }
    }
}
