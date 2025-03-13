package practice.ch6.replication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import practice.ch6.cluster.Node;
import practice.ch6.cluster.NodeManager;
import practice.ch6.config.ClusterConfig;
import practice.ch6.hash.ConsistentHash;
import practice.ch6.model.StorageResponse;
import practice.ch6.network.NodeClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ReplicationManager {
    private static final Logger logger = LoggerFactory.getLogger(ReplicationManager.class);

    private final NodeClient nodeClient;
    private final NodeManager nodeManager;
    private final ConsistentHash consistentHash;
    private final ClusterConfig clusterConfig;

    // 복제 큐: 나중에 재시도할 실패한 복제 작업 추적
    private final Map<String, ReplicationTask> pendingReplication = new ConcurrentHashMap<>();

    public ReplicationManager(NodeClient nodeClient, NodeManager nodeManager, ConsistentHash consistentHash, ClusterConfig clusterConfig) {
        this.nodeClient = nodeClient;
        this.nodeManager = nodeManager;
        this.consistentHash = consistentHash;
        this.clusterConfig = clusterConfig;
    }

    /**
     * 지정된 노드 목록에 데이터를 복제합니다. 쿼럼 작업에서 이미 업데이트된 노드는 제외합니다.
     *
     * @param key          복제할 키
     * @param value        복제할 값
     * @param ttlSeconds   TTL 값 (초)
     * @param updatedNodes 이미 업데이트된 노드 목록
     */
    @Async
    public void replicateAsync(String key, byte[] value, int ttlSeconds, List<String> updatedNodes) {
        List<String> targetNodes = consistentHash.getNodesForKey(key, clusterConfig.getReplicationFactor());

        // 이미 업데이트된 노드는 제외
        targetNodes = targetNodes.stream()
                .filter(nodeId -> !updatedNodes.contains(nodeId))
                .collect(Collectors.toList());

        if (targetNodes.isEmpty()) {
            logger.debug("No additional nodes to replicate key: {}", key);
            return;
        }

        logger.debug("Replicating key {} to nodes: {}", key, targetNodes);

        List<CompletableFuture<StorageResponse<Boolean>>> futures = new ArrayList<>();

        // 비동기 복제 요청 실행
        for (String nodeId : targetNodes) {
            futures.add(nodeClient.putAsync(nodeId, key, value, ttlSeconds));
        }

        // 모든 복제 작업 완료 대기
        try {
            List<String> finalTargetNodes = targetNodes;
            List<String> finalTargetNodes1 = targetNodes;
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .orTimeout(clusterConfig.getRequestTimeoutMs(), TimeUnit.MILLISECONDS)
                    .thenAccept(v -> {
                        // 결과 처리
                        List<String> failedNodes = new ArrayList<>();
                        for (int i = 0; i < futures.size(); i++) {
                            try {
                                StorageResponse<Boolean> response = futures.get(i).get();
                                if (!response.isSuccess() || !Boolean.TRUE.equals(response.getData())) {
                                    failedNodes.add(finalTargetNodes.get(i));
                                }
                            } catch (Exception e) {
                                failedNodes.add(finalTargetNodes.get(i));
                            }
                        }

                        // 실패한 노드가 있으면 재시도 큐에 추가
                        if (!failedNodes.isEmpty()) {
                            logger.warn("Replication failed for key {} to nodes: {}", key, failedNodes);
                            String taskId = key + "-" + UUID.randomUUID().toString();
                            pendingReplication.put(taskId, new ReplicationTask(
                                    taskId, key, value, ttlSeconds, failedNodes, System.currentTimeMillis()));
                        } else {
                            logger.debug("Replication completed successfully for key {}", key);
                        }
                    })
                    .exceptionally(e -> {
                        logger.error("Error during replication for key {}", key, e);
                        String taskId = key + "-" + UUID.randomUUID().toString();
                        pendingReplication.put(taskId, new ReplicationTask(
                                taskId, key, value, ttlSeconds, finalTargetNodes1, System.currentTimeMillis()));
                        return null;
                    });
        } catch (Exception e) {
            logger.error("Failed to schedule replication for key {}", key, e);
        }
    }

    /**
     * 노드가 클러스터에 복귀하거나 새로운 노드가 추가될 때 데이터 재조정을 수행합니다.
     *
     * @param nodeId 대상 노드 ID
     */
    public void rebalanceForNode(String nodeId) {
        // 노드 유효성 검사
        Optional<Node> nodeOpt = Optional.ofNullable(nodeManager.getNode(nodeId));
        if (nodeOpt.isEmpty() || !nodeOpt.get().isActive()) {
            logger.warn("Cannot rebalance for inactive node: {}", nodeId);
            return;
        }

        logger.info("Starting data rebalance for node: {}", nodeId);

        // 전체 키 세트를 가져와야 함 (실제 구현에서는 이 과정이 필요)
        // 여기서는 간략화를 위해 생략

        // 실제 구현 예시:
        // - 다른 노드에서 키 목록을 가져옴
        // - 각 키에 대해 새 노드가 담당해야 하는지 확인
        // - 담당해야 하는 키는 해당 노드로 복제
    }

    /**
     * 주기적으로 실패한 복제 작업을 재시도합니다.
     */
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void retryFailedReplications() {
        if (pendingReplication.isEmpty()) {
            return;
        }

        logger.info("Retrying {} failed replication tasks", pendingReplication.size());

        List<String> completedTasks = new ArrayList<>();

        for (ReplicationTask task : pendingReplication.values()) {
            // 오래된 작업은 폐기 (1시간 경과)
            if (System.currentTimeMillis() - task.getCreatedAt() > 3600000) {
                logger.warn("Discarding old replication task: {}", task.getTaskId());
                completedTasks.add(task.getTaskId());
                continue;
            }

            List<String> stillFailedNodes = new ArrayList<>();

            // 각 실패한 노드에 대해 재시도
            for (String nodeId : task.getTargetNodes()) {
                try {
                    // 노드가 활성 상태인지 확인
                    if (!nodeManager.isNodeActive(nodeId)) {
                        stillFailedNodes.add(nodeId);
                        continue;
                    }

                    // 복제 요청 실행
                    StorageResponse<Boolean> response =
                            nodeClient.put(nodeId, task.getKey(), task.getValue(), task.getTtlSeconds());

                    if (!response.isSuccess() || !Boolean.TRUE.equals(response.getData())) {
                        stillFailedNodes.add(nodeId);
                    }
                } catch (Exception e) {
                    logger.error("Error retrying replication to node {} for key {}",
                            nodeId, task.getKey(), e);
                    stillFailedNodes.add(nodeId);
                }
            }

            // 모든 노드에 성공했으면 작업 완료 표시
            if (stillFailedNodes.isEmpty()) {
                logger.info("Completed replication task: {}", task.getTaskId());
                completedTasks.add(task.getTaskId());
            } else {
                // 그렇지 않으면 실패한 노드 목록 업데이트
                task.setTargetNodes(stillFailedNodes);
                logger.debug("Replication task {} still pending for nodes: {}",
                        task.getTaskId(), stillFailedNodes);
            }
        }

        // 완료된 작업 제거
        completedTasks.forEach(pendingReplication::remove);
    }

    /**
     * 노드 제거 시 데이터를 다른 노드로 이동합니다.
     *
     * @param nodeId 제거될 노드 ID
     */
    public void migrateDataFromNode(String nodeId) {
        // 실제 구현에서는 제거될 노드에서 모든 데이터를 가져와서
        // 새로운 담당 노드로 이동하는 로직이 필요
        logger.info("Data migration started from node: {}", nodeId);
    }

    /**
     * 복제 작업을 추적하기 위한 내부 클래스
     */
    private static class ReplicationTask {
        private final String taskId;
        private final String key;
        private final byte[] value;
        private final int ttlSeconds;
        private List<String> targetNodes;
        private final long createdAt;

        public ReplicationTask(
                String taskId,
                String key,
                byte[] value,
                int ttlSeconds,
                List<String> targetNodes,
                long createdAt) {
            this.taskId = taskId;
            this.key = key;
            this.value = value;
            this.ttlSeconds = ttlSeconds;
            this.targetNodes = targetNodes;
            this.createdAt = createdAt;
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
            this.targetNodes = targetNodes;
        }

        public long getCreatedAt() {
            return createdAt;
        }
    }
}
