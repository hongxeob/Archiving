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

        // 7개의 테스트 노드 생성
        for (int i = 1; i <= 7; i++) {
            Node node = new Node("localhost", 10000 + i);
            node.setStatus(Node.NodeStatus.ACTIVE);
            if (i <= 3) {
                node.setRole(Node.NodeRole.PRIMARY);
            } else {
                node.setRole(Node.NodeRole.SECONDARY);
            }
            manager.addNode(node);
        }

        return manager;
    }

    @Bean
    public ConsistentHash consistentHash(NodeManager nodeManager) throws Exception {
        return new ConsistentHash(nodeManager);
    }

//    @Bean
//    public List<LocalStorageNode> storageNodes(NodeManager nodeManager) {
//        List<LocalStorageNode> nodes = new ArrayList<>();
//
//        // 각 노드마다 로컬 스토리지 인스턴스 생성
//        for (Node node : nodeManager.getAllNodes()) {
//            LocalStorageNode storageNode = new LocalStorageNode(node.getNodeId());
//            nodes.add(storageNode);
//        }
//
//        return nodes;
//    }
}
