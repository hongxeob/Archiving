package practice.ch6.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import practice.ch6.cluster.Node;
import practice.ch6.cluster.NodeManager;
import practice.ch6.hash.ConsistentHash;

import java.net.URL;
import java.util.List;

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

    @Value("${server.port}")
    private int serverPort;


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

        // 1. 현재 노드 정보 가져오기
        String currentNodeName = clusterConfig().getNodeName();
        int currentPort = serverPort; // @Value("${server.port}") 주입된 포트

        // 2. 현재 노드 추가
        Node currentNode = new Node(currentNodeName, currentPort); // 호스트 이름을 localhost가 아닌 컨테이너 이름으로
        currentNode.setStatus(Node.NodeStatus.ACTIVE);
        currentNode.setRole(Node.NodeRole.valueOf(clusterConfig().getNodeRole()));
        manager.addNode(currentNode);

        // 3. discoveryEndpoints를 사용하여 다른 노드 정보 추가
        List<String> endpoints = clusterConfig().getDiscoveryEndpoints();
        for (String endpoint : endpoints) {
            try {
                URL url = new URL(endpoint);
                String host = url.getHost(); // 호스트 이름
                int port = url.getPort() != -1 ? url.getPort() : 80;

                Node node = new Node(host, port);
                node.setStatus(Node.NodeStatus.ACTIVE);
                node.setRole(Node.NodeRole.PRIMARY); // 기본값으로
                manager.addNode(node);
            } catch (Exception e) {
                // URL 파싱 에러 처리
            }
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
