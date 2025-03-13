package practice.ch6.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class NodeManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // 클러스터의 모든 노드를 관리하는 맵 (노드ID -> 노드 객체)
    private final Map<String, Node> nodes = new ConcurrentHashMap<>();

    // 장애 감지를 위한 스케줄러
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public NodeManager() {
        scheduler.scheduleAtFixedRate(this::checkNodeHealth, 0, 5, TimeUnit.SECONDS);
    }

    public void addNode(Node node) {
        nodes.put(node.getNodeId(), node);
        log.info("Node {} added to cluster", node.getNodeId());
    }

    public void removeNode(String nodeId) {
        nodes.remove(nodeId);
        log.info("Node {} removed from cluster", nodeId);
    }

    public boolean isNodeActive(String nodeId) {
        Node node = nodes.get(nodeId);
        return node != null && node.isActive();
    }

    /**
     * 클러스터의 모든 노드 목록을 반환
     */
    public Collection<Node> getAllNodes() {
        return nodes.values();
    }

    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    /**
     * 주기적으로 모든 노드의 상태를 확인
     * 실제 구현에서는 고스핑 프로토콜 등을 사용
     */
    private void checkNodeHealth() {
        nodes.values().forEach(node -> {
            try {
                boolean isHealthy = node.checkHealth();
                if (!isHealthy && node.isActive()) {
                    log.warn("Node {} appears to be unhealthy", node.getNodeId());
                    node.setActive(false);
                    // 장애 복구 절차 시작...
                } else if (isHealthy && !node.isActive()) {
                    log.info("Node {} has recovered", node.getNodeId());
                    node.setActive(true);
                }
            } catch (Exception e) {
                log.error("Error checking health of node {}", node.getNodeId(), e);
            }
        });
    }
}
