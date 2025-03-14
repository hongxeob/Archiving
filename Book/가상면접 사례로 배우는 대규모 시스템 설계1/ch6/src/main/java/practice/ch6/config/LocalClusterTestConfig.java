package practice.ch6.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import practice.ch6.cluster.Node;
import practice.ch6.cluster.NodeManager;
import practice.ch6.hash.ConsistentHash;

@Configuration
public class LocalClusterTestConfig {
    @Value("${kvstore.cluster.nodeName}")
    private String nodeName;

    @Value("${kvstore.cluster.nodeRole}")
    private String nodeRole;

    @Value("${kvstore.cluster.replicationFactor}")
    private int replicationFactor;

    @Value("${kvstore.cluster.readQuorum}")
    private int readQuorum;

    @Value("${kvstore.cluster.writeQuorum}")
    private int writeQuorum;

    @Value("${kvstore.cluster.requestTimeoutMs}")
    private int requestTimeoutMs;

    @Bean
    public ClusterConfig clusterConfig() {
        ClusterConfig config = new ClusterConfig();
        config.setNodeName(nodeName);
        config.setNodeRole(nodeRole);
        config.setReplicationFactor(replicationFactor);
        config.setReadQuorum(readQuorum);
        config.setWriteQuorum(writeQuorum);
        config.setRequestTimeoutMs(requestTimeoutMs);
        return config;
    }

    @Bean
    public NodeManager nodeManager() {
        NodeManager manager = new NodeManager();

        // 현재 서버만 등록 (다른 노드 없음)
        Node currentNode = new Node("localhost", 8089);
        currentNode.setStatus(Node.NodeStatus.ACTIVE);
        currentNode.setRole(Node.NodeRole.PRIMARY);
        manager.addNode(currentNode);

        return manager;
    }

    @Bean
    public ConsistentHash consistentHash(NodeManager nodeManager) throws Exception {
        return new ConsistentHash(nodeManager);
    }
}
