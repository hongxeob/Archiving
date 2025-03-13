package practice.ch6.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import practice.ch6.client.KVStoreClient;
import practice.ch6.config.ClusterConfig;
import practice.ch6.consistency.QuorumManager;
import practice.ch6.hash.ConsistentHash;
import practice.ch6.replication.ReplicationManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@Service
public class CoordinatorService implements KVStoreClient {
    private static final Logger logger = LoggerFactory.getLogger(CoordinatorService.class);

    private final ConsistentHash consistentHash;
    private final ReplicationManager replicationManager;
    private final QuorumManager quorumManager;
    private final ClusterConfig clusterConfig;

    public CoordinatorService(
            ConsistentHash consistentHash,
            ReplicationManager replicationManager,
            QuorumManager quorumManager,
            ClusterConfig clusterConfig
    ) {
        this.consistentHash = consistentHash;
        this.replicationManager = replicationManager;
        this.quorumManager = quorumManager;
        this.clusterConfig = clusterConfig;
    }

    @Override
    public byte[] get(String key) {
        long startTime = System.nanoTime();
        try {
            // 키를 저장할 노드 찾기
            List<String> targetNodes = consistentHash.getNodesForKey(key, clusterConfig.getReplicationFactor() );

            // 쿼럼 읽기 수행
            return quorumManager.readWithQuorum(key, targetNodes);
        } catch (Exception e) {
            logger.error("Error during GET operation for key: {}", key, e);
            throw new RuntimeException("Failed to get value for key: " + key, e);
        } finally {
            long duration = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTime);
            logger.debug("GET operation for key {} completed in {} μs", key, duration);
        }
    }

    @Override
    public boolean put(String key, byte[] value) {
        return put(key, value, 0);
    }

    @Override
    public boolean put(String key, byte[] value, int ttlSeconds) {
        long startTime = System.nanoTime();
        try {
            // 키를 저장할 노드 찾기 (복제본 포함)
            List<String> targetNodes = consistentHash.getNodesForKey(key, clusterConfig.getReplicationFactor());

            // 쿼럼 쓰기 수행
            boolean success = quorumManager.writeWithQuorum(key, value, ttlSeconds, targetNodes);

            // 비동기 복제 수행 (쿼럼에 포함되지 않은 노드에 대해)
            if (success) {
                CompletableFuture.runAsync(() ->
                        replicationManager.replicateAsync(key, value, ttlSeconds, targetNodes));
            }

            return success;
        } catch (Exception e) {
            logger.error("Error during PUT operation for key: {}", key, e);
            throw new RuntimeException("Failed to put value for key: " + key, e);
        } finally {
            long duration = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTime);
            logger.debug("PUT operation for key {} completed in {} μs", key, duration);
        }
    }
}
