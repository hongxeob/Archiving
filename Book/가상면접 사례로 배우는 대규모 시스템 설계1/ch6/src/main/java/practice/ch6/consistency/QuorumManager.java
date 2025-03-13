package practice.ch6.consistency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import practice.ch6.cluster.NodeManager;
import practice.ch6.config.ClusterConfig;
import practice.ch6.model.StorageResponse;
import practice.ch6.model.VersionedValue;
import practice.ch6.network.NodeClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * 쿼럼 기반 읽기/쓰기 일관성을 관리하는 클래스
 */
@Component
public class QuorumManager {
    private static final Logger logger = LoggerFactory.getLogger(QuorumManager.class);

    private final NodeClient nodeClient;
    private final ClusterConfig clusterConfig;
    private final NodeManager nodeManager;

    @Autowired
    public QuorumManager(NodeClient nodeClient, ClusterConfig clusterConfig, NodeManager nodeManager) {
        this.nodeClient = nodeClient;
        this.clusterConfig = clusterConfig;
        this.nodeManager = nodeManager;
    }

    /**
     * 읽기 쿼럼 크기를 계산합니다.
     *
     * @return 읽기 쿼럼 크기
     */
    public int calculateReadQuorum() {
        return clusterConfig.getReadQuorum();
    }

    /**
     * 쓰기 쿼럼 크기를 계산합니다.
     *
     * @return 쓰기 쿼럼 크기
     */
    public int calculateWriteQuorum() {
        return clusterConfig.getWriteQuorum();
    }

    /**
     * 쿼럼 기반 읽기를 수행합니다.
     *
     * @param key         읽을 키
     * @param targetNodes 대상 노드 목록
     * @return 읽은 값 또는 null (키가 존재하지 않는 경우)
     */
    public byte[] readWithQuorum(String key, List<String> targetNodes) {
        int readQuorum = calculateReadQuorum();

        logger.debug("Performing quorum read for key {} with quorum size {} on nodes {}",
                key, readQuorum, targetNodes);

        // 병렬로 모든 노드에 읽기 요청 전송
        List<CompletableFuture<StorageResponse<VersionedValue>>> futures = targetNodes.stream()
        .map(nodeId -> CompletableFuture.<StorageResponse<VersionedValue>>supplyAsync(
            () -> nodeClient.get(nodeId, key)))
        .toList();



        try {
            // 쿼럼에 도달할 때까지 대기
            CompletableFuture<Void> quorum = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0]));

            // 요청 타임아웃 설정
            quorum.get(clusterConfig.getRequestTimeoutMs(), TimeUnit.MILLISECONDS);

            // 성공한 응답 수집
            List<StorageResponse<VersionedValue>> responses = futures.stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (Exception e) {
                            logger.warn("Failed to get response for key {}", key, e);
                            return null;
                        }
                    })
                    .filter(r -> r != null && r.isSuccess() && r.getData() != null)
                    .collect(Collectors.toList());

            // 쿼럼에 도달했는지 확인
            if (responses.size() < readQuorum) {
                throw new IllegalStateException(
                        "Failed to reach read quorum for key " + key +
                                ". Required: " + readQuorum + ", Received: " + responses.size());
            }

            // 버전이 가장 높은 값 선택
            Optional<StorageResponse<VersionedValue>> latestResponse = responses.stream()
                    .max(Comparator.comparingLong(r -> r.getData().getVersion()));

            if (latestResponse.isPresent()) {
                VersionedValue latest = latestResponse.get().getData();

                // 읽기 수리 (Read Repair) - 오래된 버전을 가진 노드 업데이트
                if (responses.size() > 1) {
                    performReadRepair(key, latest, responses, targetNodes);
                }

                return latest.getValue();
            } else {
                // 모든 응답이 유효하지 않은 경우
                return null;
            }

        } catch (TimeoutException e) {
            logger.error("Timeout during quorum read for key {}", key, e);
            throw new RuntimeException("Timeout during quorum read for key " + key, e);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error during quorum read for key {}", key, e);
            throw new RuntimeException("Error during quorum read for key " + key, e);
        }
    }

    /**
     * 쿼럼 기반 쓰기를 수행합니다.
     *
     * @param key         저장할 키
     * @param value       저장할 값
     * @param ttlSeconds  TTL 값 (초)
     * @param targetNodes 대상 노드 목록
     * @return 성공 여부
     */
    public boolean writeWithQuorum(String key, byte[] value, int ttlSeconds, List<String> targetNodes) {
        int writeQuorum = calculateWriteQuorum();

        logger.debug("Performing quorum write for key {} with quorum size {} on nodes {}",
                key, writeQuorum, targetNodes);

        // 먼저 최신 버전을 읽어서 새 버전 번호 결정
        long newVersion = determineNewVersionNumber(key, targetNodes);

        // 새 버전의 값 생성
        VersionedValue versionedValue = new VersionedValue(value, newVersion, ttlSeconds > 0 ? ttlSeconds : null);

        // 병렬로 모든 노드에 쓰기 요청 전송
        List<CompletableFuture<StorageResponse<Boolean>>> futures = new ArrayList<>();
        List<String> successfulNodes = Collections.synchronizedList(new ArrayList<>());

        for (String nodeId : targetNodes) {
            CompletableFuture<StorageResponse<Boolean>> future = CompletableFuture.supplyAsync(() -> {
                StorageResponse<Boolean> response = nodeClient.put(nodeId, key, versionedValue.getValue(), ttlSeconds);
                if (response.isSuccess() && Boolean.TRUE.equals(response.getData())) {
                    successfulNodes.add(nodeId);
                }
                return response;
            });
            futures.add(future);
        }

        try {
            // 모든 요청 완료 대기
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0]));

            // 타임아웃 설정
            allFutures.get(clusterConfig.getRequestTimeoutMs(), TimeUnit.MILLISECONDS);

            // 쿼럼 확인
            boolean quorumReached = successfulNodes.size() >= writeQuorum;

            if (!quorumReached) {
                logger.error("Failed to reach write quorum for key {}. Required: {}, Successful: {}",
                        key, writeQuorum, successfulNodes.size());

                // 롤백 필요 없음 - 버전 기반 충돌 해결로 처리
                return false;
            }

            logger.debug("Quorum write successful for key {} on nodes: {}", key, successfulNodes);
            return true;

        } catch (TimeoutException e) {
            logger.error("Timeout during quorum write for key {}", key, e);
            return false;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error during quorum write for key {}", key, e);
            return false;
        }
    }

    /**
     * 새 버전 번호를 결정합니다.
     *
     * @param key         키
     * @param targetNodes 대상 노드 목록
     * @return 새 버전 번호
     */
    private long determineNewVersionNumber(String key, List<String> targetNodes) {
        // 모든 노드에서 현재 버전 조회
        List<CompletableFuture<StorageResponse<VersionedValue>>> futures = targetNodes.stream()
                .map(nodeId -> CompletableFuture.supplyAsync(() -> nodeClient.get(nodeId, key)))
                .collect(Collectors.toList());

        long maxVersion = 0;

        try {
            // 일부 노드만 응답해도 진행 (쿼럼까지는 기다리지 않음)
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .orTimeout(clusterConfig.getRequestTimeoutMs() / 2, TimeUnit.MILLISECONDS)
                    .exceptionally(e -> null) // 타임아웃 무시하고 계속 진행
                    .get();

            // 응답받은 최대 버전 확인
            for (CompletableFuture<StorageResponse<VersionedValue>> future : futures) {
                try {
                    StorageResponse<VersionedValue> response = future.getNow(null);
                    if (response != null && response.isSuccess() && response.getData() != null) {
                        maxVersion = Math.max(maxVersion, response.getData().getVersion());
                    }
                } catch (Exception e) {
                    // 개별 오류 무시
                }
            }
        } catch (Exception e) {
            logger.warn("Error determining new version for key {}", key, e);
        }

        // 최대 버전 + 1 반환 (새 값이면 1부터 시작)
        return maxVersion + 1;
    }

    /**
     * 읽기 수리(Read Repair)를 수행합니다.
     * 최신 버전보다 오래된 데이터를 가진 노드를 업데이트합니다.
     *
     * @param key         키
     * @param latestValue 최신 값
     * @param responses   모든 노드의 응답
     * @param targetNodes 대상 노드 목록
     */
    private void performReadRepair(
            String key,
            VersionedValue latestValue,
            List<StorageResponse<VersionedValue>> responses,
            List<String> targetNodes) {

        if (!clusterConfig.isAutoSyncEnabled()) {
            return; // 자동 동기화가 비활성화된 경우 수행하지 않음
        }

        // 최신 버전보다 오래된 값을 가진 노드 식별
        Map<String, VersionedValue> nodeValues = new HashMap<>();
        // 응답과 targetNodes의 인덱스 매핑
        for (int i = 0; i < responses.size(); i++) {
            StorageResponse<VersionedValue> response = responses.get(i);
            if (response.getNodeId() != null) {
                // nodeId가 응답에 포함된 경우
                nodeValues.put(response.getNodeId(), response.getData());
            } else if (i < targetNodes.size()) {
                // 위치로 매핑
                nodeValues.put(targetNodes.get(i), response.getData());
            }
        }

        List<String> nodesToRepair = new ArrayList<>();

        // 각 노드마다 버전 확인
        for (String nodeId : targetNodes) {
            VersionedValue nodeValue = nodeValues.get(nodeId);
            if (nodeValue != null && nodeValue.getVersion() < latestValue.getVersion()) {
                nodesToRepair.add(nodeId);
            }
        }

        if (nodesToRepair.isEmpty()) {
            return; // 수리할 노드가 없음
        }

        logger.debug("Performing read repair for key {} on nodes: {}", key, nodesToRepair);

        // 비동기로 오래된 노드 업데이트
        for (String nodeId : nodesToRepair) {
            CompletableFuture.runAsync(() -> {
                try {
                    Integer ttl = latestValue.getTtlSeconds();
                    nodeClient.put(nodeId, key, latestValue.getValue(), ttl != null ? ttl : 0);
                } catch (Exception e) {
                    logger.warn("Failed to perform read repair for key {} on node {}", key, nodeId, e);
                }
            });
        }
    }
}
