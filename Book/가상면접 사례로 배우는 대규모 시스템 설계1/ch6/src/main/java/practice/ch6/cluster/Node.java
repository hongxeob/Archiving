package practice.ch6.cluster;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 분산 키-값 저장소 클러스터의 개별 노드를 표현
 */
public class Node {
    // 노드 식별자
    private final String nodeId;

    // 호스트 주소
    private final InetSocketAddress address;

    // 노드의 역할 (PRIMARY, SECONDARY, ARBITER 등)
    private NodeRole role;

    // 노드 활성 상태
    private final AtomicBoolean active = new AtomicBoolean(true);

    // 노드 상태 정보
    private NodeStatus status = NodeStatus.JOINING;

    // 마지막 하트비트 시간
    private Instant lastHeartbeatTime = Instant.now();

    // 노드의 메타데이터
    private final Map<String, String> metadata = new ConcurrentHashMap<>();

    // 저장된 키의 개수
    private final AtomicLong keyCount = new AtomicLong(0);

    // 사용 중인 메모리 (바이트)
    private final AtomicLong usedMemory = new AtomicLong(0);

    // 초당 요청 수
    private final AtomicLong requestsPerSecond = new AtomicLong(0);

    /**
     * 노드 생성자
     *
     * @param host 노드 호스트
     * @param port 노드 포트
     */
    public Node(String host, int port) {
        this.nodeId = generateNodeId(host, port);
        this.address = new InetSocketAddress(host, port);
        this.role = NodeRole.SECONDARY; // 기본값은 SECONDARY
    }

    /**
     * 노드 식별자를 생성합니다.
     */
    private String generateNodeId(String host, int port) {
        return host + ":" + port + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 노드의 건강 상태를 확인합니다.
     * 실제 구현에서는 네트워크 통신을 통해 노드가 응답하는지 확인합니다.
     *
     * @return 노드가 정상적으로 작동하는지 여부
     */
    public boolean checkHealth() {
        // 실제 구현에서는 노드에 헬스체크 요청을 보내고 응답을 확인합니다.
        // 여기서는 간단히 항상 정상이라고 가정합니다.
        return true;
    }

    /**
     * 노드의 메트릭 정보를 수집합니다.
     *
     * @return 노드 메트릭 정보
     */
    public Map<String, Object> collectMetrics() {
        Map<String, Object> metrics = new ConcurrentHashMap<>();
        metrics.put("nodeId", nodeId);
        metrics.put("host", address.getHostString());
        metrics.put("port", address.getPort());
        metrics.put("role", role);
        metrics.put("status", status);
        metrics.put("active", active.get());
        metrics.put("lastHeartbeat", lastHeartbeatTime);
        metrics.put("keyCount", keyCount.get());
        metrics.put("usedMemory", usedMemory.get());
        metrics.put("requestsPerSecond", requestsPerSecond.get());

        return metrics;
    }

    /**
     * 노드의 하트비트를 업데이트합니다.
     */
    public void updateHeartbeat() {
        this.lastHeartbeatTime = Instant.now();
    }

    /**
     * 노드에 메타데이터를 추가합니다.
     *
     * @param key   메타데이터 키
     * @param value 메타데이터 값
     */
    public void addMetadata(String key, String value) {
        metadata.put(key, value);
    }

    public String getNodeId() {
        return nodeId;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public NodeRole getRole() {
        return role;
    }

    public void setRole(NodeRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active.get();
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    public Instant getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public long getKeyCount() {
        return keyCount.get();
    }

    public void incrementKeyCount() {
        keyCount.incrementAndGet();
    }

    public void decrementKeyCount() {
        keyCount.decrementAndGet();
    }

    public long getUsedMemory() {
        return usedMemory.get();
    }

    public void updateUsedMemory(long bytesUsed) {
        usedMemory.set(bytesUsed);
    }

    public long getRequestsPerSecond() {
        return requestsPerSecond.get();
    }

    public void updateRequestsPerSecond(long rps) {
        requestsPerSecond.set(rps);
    }

    @Override
    public String toString() {
        return "Node{" + "nodeId='" + nodeId + '\'' + ", address=" + address + ", role=" + role + ", active=" + active + ", status=" + status + '}';
    }

    /**
     * 노드의 역할을 정의하는 열거형
     */
    public enum NodeRole {
        PRIMARY,    // 주 노드 (쓰기 작업 처리)
        SECONDARY,  // 보조 노드 (읽기 작업 처리, 복제)
        ARBITER     // 중재자 (투표에만 참여, 데이터 저장 안 함)
    }

    /**
     * 노드의 상태를 정의하는 열거형
     */
    public enum NodeStatus {
        JOINING,    // 클러스터에 참여 중
        ACTIVE,     // 정상 작동 중
        LEAVING,    // 클러스터 이탈 중
        FAILED,     // 장애 상태
        MAINTENANCE // 유지보수 모드
    }

}
