package practice.ch6.hash;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import practice.ch6.cluster.Node;
import practice.ch6.cluster.NodeManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 일관된 해싱 구현
 * 각 노드는 해시 링 위에 배치되며, 가상 노드를 사용하여 데이터를 균등하게 분산시킴
 */
@Component
public class ConsistentHash {
    private static final Logger logger = LoggerFactory.getLogger(ConsistentHash.class);

    // 가상 노드 수 (각 물리 노드당 생성할 가상 노드 수)
    private static final int VIRTUAL_NODES = 256;

    // 해시 함수로 SHA-256 사용
    private final MessageDigest md;

    // 해시 링 : 키는 해시 값, 값은 노드 식별자
    private final SortedMap<Long, String> hashRing = new ConcurrentSkipListMap<>();

    private final NodeManager nodeManager;

    // 동시성 처리를 위한 읽기/쓰기 락
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ConsistentHash(NodeManager nodeManager) throws NoSuchAlgorithmException {
        this.md = MessageDigest.getInstance("SHA-256");
        this.nodeManager = nodeManager;
    }

    @PostConstruct
    public void init() {
        for (Node node : nodeManager.getAllNodes()) {
            addNode(node.getNodeId());
        }
        logger.info("Consistent hash ring initialized with {} nodes", nodeManager.getAllNodes().size());

    }

    /**
     * 물리적 노드를 해시 링에 추가
     * 각 물리 노드상 가상 노드를 여러개 생성하여 데이터를 분산
     *
     * @param nodeId 노드 식별자
     */
    public void addNode(String nodeId) {
        lock.writeLock().lock();
        try {
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                String virtualNodeId = nodeId + "#" + i;
                long hash = hash(virtualNodeId);
                hashRing.put(hash, nodeId);
                logger.debug("Added virtual node {} with hash {] for physical node {}", virtualNodeId, hash, nodeId);
            }
            logger.info("Node {] added to hash ring with {} virtual nodes", nodeId, VIRTUAL_NODES);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 물리적 노드를 해시 링에서 제거합니다.
     *
     * @param nodeId 노드 식별자
     */
    public void removeNode(String nodeId) {
        lock.writeLock().lock();
        try {
            Iterator<Map.Entry<Long, String>> it = hashRing.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Long, String> entry = it.next();
                if (entry.getValue().equals(nodeId)) {
                    it.remove();
                }
            }
            logger.info("Node {} removed from hash ring", nodeId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 지정된 키에 대한 담당 노드를 찾습니다.
     *
     * @param key 키
     * @return 담당 노드 식별자
     */
    public String getNodeForKey(String key) {
        lock.readLock().lock();
        try {
            if (hashRing.isEmpty()) {
                throw new IllegalStateException("Hash ring is Empty, no nodes available");
            }
            long hash = hash(key);

            // 해시 키 이상인 모든 엔트리를 가져옴
            SortedMap<Long, String> tailMap = hashRing.tailMap(hash);

            // 해시 링 순환 - 만약 tailMap이 비어있으면 첫 번째 노드를 선택
            Long nodeHash = tailMap.isEmpty() ? hashRing.firstKey() : tailMap.firstKey();

            String nodeId = hashRing.get(nodeHash);
            logger.debug("Key {} with hash {} is mapped to node {}", key, hash, nodeId);

            return nodeId;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 키의 복제본을 저장할 노드 목록을 가져옵니다.
     *
     * @param key               키
     * @param replicationFactor 복제 인수 (몇 개의 노드에 복제할지)
     * @return 노드 식별자 목록
     */
    public List<String> getNodesForKey(String key, int replicationFactor) {
        lock.readLock().lock();
        try {
            if (hashRing.isEmpty()) {
                throw new IllegalStateException("Hash ring is empty, no nodes available");
            }
            Set<String> uniqueNodes = new LinkedHashSet<>();
            long hash = hash(key);

            // 해시 키 이상인 모든 엔트리를 가져옴
            SortedMap<Long, String> tailMap = new TreeMap<>(hashRing.tailMap(hash));
            tailMap.putAll(hashRing.headMap(hash)); // 순환 링 처리를 위해 나머지 부분 추가

            // 필요한 수의 고유 노드를 가져옴
            for (String nodeId : tailMap.values()) {
                if (!uniqueNodes.contains(nodeId)) {
                    uniqueNodes.add(nodeId);
                    if (uniqueNodes.size() >= replicationFactor) {
                        break;
                    }
                }
            }

            // 유효한 물리 노드만 필터링
            List<String> result = uniqueNodes.stream()
                    .filter(nodeManager::isNodeActive)
                    .limit(replicationFactor)
                    .toList();

            // 충분한 노드를 찾지 못한 경우
            if (result.size() < replicationFactor) {
                logger.warn("Could not find {} nodes for key {}, found only {}",
                        replicationFactor, key, result.size());
            }

            return result;
        } finally {
            lock.readLock().unlock();

        }
    }

    /**
     * 키 또는 노드 ID에 대한 해시 값을 계산합니다.
     *
     * @param key 해시할 키
     * @return 계산된 해시 값
     */
    public long hash(String key) {
        md.reset();
        md.update(key.getBytes());
        byte[] digest = md.digest();

        // 첫 8바이트를 long 값으로 변환
        long hash = 0;
        for (int i = 0; i < 8; i++) {
            hash <<= 8;
            hash |= ((int) digest[i] & 0xFF);
        }
        return hash;
    }

    /**
     * 현재 해시 링의 상태 정보를 가져옵니다.
     *
     * @return 해시 링 상태 정보
     */
    public Map<String, Object> getRingStatus() {
        lock.readLock().lock();
        try {
            HashMap<String, Object> status = new HashMap<>();
            status.put("physicalNodes", nodeManager.getAllNodes().size());
            status.put("virtualNodes", hashRing.size());

            // 노드별 가상 노드 수 계산
            HashMap<String, Integer> nodesDistribution = new HashMap<>();
            for (String nodeId : hashRing.values()) {
                nodesDistribution.put(nodeId, nodesDistribution.getOrDefault(nodeId, 0) + 1);
            }
            status.put("nodesDistribution", nodesDistribution);

            return status;
        } finally {
            lock.readLock().unlock();
        }
    }
}
