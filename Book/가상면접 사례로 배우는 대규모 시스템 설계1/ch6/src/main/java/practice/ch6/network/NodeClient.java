package practice.ch6.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import practice.ch6.cluster.Node;
import practice.ch6.cluster.NodeManager;
import practice.ch6.config.ClusterConfig;
import practice.ch6.model.StorageRequest;
import practice.ch6.model.StorageResponse;
import practice.ch6.model.VersionedValue;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class NodeClient {
    private static final Logger logger = LoggerFactory.getLogger(NodeClient.class);

    private final RestTemplate restTemplate;
    private final NodeManager nodeManager;
    private final ClusterConfig clusterConfig;
    private final ObjectMapper objectMapper;

    public NodeClient(RestTemplate restTemplate, NodeManager nodeManager, ClusterConfig clusterConfig, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.nodeManager = nodeManager;
        this.clusterConfig = clusterConfig;
        this.objectMapper = objectMapper;
    }

    /**
     * 지정된 노드에서 키에 해당하는 값을 가져옵니다.
     *
     * @param nodeId 대상 노드 ID
     * @param key    조회할 키
     * @return 값 조회 결과 응답
     */
    @Retryable(value = {RestClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100, multiplier = 2))
    public StorageResponse<VersionedValue> get(String nodeId, String key) {
        Optional<Node> nodeOpt = Optional.ofNullable(nodeManager.getNode(nodeId));
        if (nodeOpt.isEmpty() || !nodeOpt.get().isActive()) {
            logger.warn("Node {} is not available for GET operation", nodeId);
            return StorageResponse.<VersionedValue>failure("Node not available");
        }

        Node node = nodeOpt.get();
        String url = buildUrl(node, "/internal/storage/" + key);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, buildHttpEntity(null), String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                StorageResponse<VersionedValue> response = parseResponse(responseEntity.getBody(), VersionedValue.class);
                logger.debug("GET from node {} for key {}: {}", nodeId, key, response);
                return response;
            } else {
                logger.warn("Failed to GET from node {} for key {}: {}", nodeId, key, responseEntity.getStatusCode());
                return StorageResponse.<VersionedValue>failure("Failed with status: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error during GET from node {} for key {}", nodeId, key, e);
            return StorageResponse.<VersionedValue>failure(e.getMessage());
        }
    }

    /**
     * 지정된 노드에 키-값 쌍을 저장합니다.
     *
     * @param nodeId     대상 노드 ID
     * @param key        저장할 키
     * @param value      저장할 값
     * @param ttlSeconds TTL(초), 0이면 무기한
     * @return 저장 결과 응답
     */
    @Retryable(value = {RestClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100, multiplier = 2))
    public StorageResponse<Boolean> put(String nodeId, String key, byte[] value, int ttlSeconds) {
        Optional<Node> nodeOpt = Optional.ofNullable(nodeManager.getNode(nodeId));
        if (nodeOpt.isEmpty() || !nodeOpt.get().isActive()) {
            logger.warn("Node {} is not available for PUT operation", nodeId);
            return StorageResponse.failure("Node not available");
        }

        Node node = nodeOpt.get();
        String url = buildUrl(node, "/internal/storage/" + key);

        StorageRequest request = new StorageRequest();
        request.setValue(value);
        request.setTtlSeconds(ttlSeconds);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, buildHttpEntity(request), String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                StorageResponse<Boolean> response = parseResponse(responseEntity.getBody(), Boolean.class);
                logger.debug("PUT to node {} for key {}: {}", nodeId, key, response);
                return response;
            } else {
                logger.warn("Failed to PUT to node {} for key {}: {}", nodeId, key, responseEntity.getStatusCode());
                return StorageResponse.failure("Failed with status: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error during PUT to node {} for key {}", nodeId, key, e);
            return StorageResponse.failure(e.getMessage());
        }
    }

    /**
     * 비동기적으로 지정된 노드에 키-값 쌍을 저장합니다.
     *
     * @param nodeId     대상 노드 ID
     * @param key        저장할 키
     * @param value      저장할 값
     * @param ttlSeconds TTL(초), 0이면 무기한
     * @return 비동기 작업 객체
     */
    public CompletableFuture<StorageResponse<Boolean>> putAsync(String nodeId, String key, byte[] value, int ttlSeconds) {
        return CompletableFuture.supplyAsync(() -> put(nodeId, key, value, ttlSeconds));
    }

    /**
     * 지정된 노드에서 키를 삭제합니다.
     *
     * @param nodeId 대상 노드 ID
     * @param key    삭제할 키
     * @return 삭제 결과 응답
     */
    @Retryable(value = {RestClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100, multiplier = 2))
    public StorageResponse<Boolean> delete(String nodeId, String key) {
        Optional<Node> nodeOpt = Optional.ofNullable(nodeManager.getNode(nodeId));
        if (nodeOpt.isEmpty() || !nodeOpt.get().isActive()) {
            logger.warn("Node {} is not available for DELETE operation", nodeId);
            return StorageResponse.failure("Node not available");
        }

        Node node = nodeOpt.get();
        String url = buildUrl(node, "/internal/storage/" + key);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, buildHttpEntity(null), String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                StorageResponse<Boolean> response = parseResponse(responseEntity.getBody(), Boolean.class);
                logger.debug("DELETE from node {} for key {}: {}", nodeId, key, response);
                return response;
            } else {
                logger.warn("Failed to DELETE from node {} for key {}: {}", nodeId, key, responseEntity.getStatusCode());
                return StorageResponse.failure("Failed with status: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error during DELETE from node {} for key {}", nodeId, key, e);
            return StorageResponse.failure(e.getMessage());
        }
    }

    /**
     * 노드의 상태를 확인합니다.
     *
     * @param nodeId 대상 노드 ID
     * @return 노드가 정상 작동 중인지 여부
     */
    public boolean checkNodeHealth(String nodeId) {
        Optional<Node> nodeOpt = Optional.ofNullable(nodeManager.getNode(nodeId));
        if (nodeOpt.isEmpty()) {
            return false;
        }

        Node node = nodeOpt.get();
        String url = buildUrl(node, "/actuator/health");

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, buildHttpEntity(null), String.class);

            boolean isHealthy = responseEntity.getStatusCode().is2xxSuccessful();
            if (isHealthy) {
                node.updateHeartbeat();
            }
            return isHealthy;
        } catch (Exception e) {
            logger.warn("Health check failed for node {}", nodeId, e);
            return false;
        }
    }

    /**
     * URL을 생성합니다.
     *
     * @param node 대상 노드
     * @param path API 경로
     * @return 완성된 URL
     */
    private String buildUrl(Node node, String path) {
        // 단일 노드 모드에서는 항상 localhost 사용
        String host = "localhost";
        int port = node.getAddress().getPort();

        // 로컬 테스트인지 Docker 환경인지 확인하는 로직은 유지
        // 나중에 다중 노드 환경으로 확장할 때 필요할 수 있음
        boolean isDockerEnvironment = System.getenv("DOCKER_ENV") != null;

        if (isDockerEnvironment) {
            // Docker 환경: 노드 ID에서 컨테이너 이름 추출 (node1:8081-xxx -> node1)
            String nodeId = node.getNodeId();
            if (nodeId.contains(":")) {
                host = nodeId.substring(0, nodeId.indexOf(":"));
            } else {
                host = nodeId;
            }
        }

        return String.format("http://%s:%d%s", host, port, path);
    }

    /**
     * HTTP 요청 엔티티를 생성합니다.
     *
     * @param body 요청 본문 객체
     * @return HTTP 엔티티
     */
    private HttpEntity<String> buildHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        if (body == null) {
            return new HttpEntity<>(headers);
        }

        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            return new HttpEntity<>(jsonBody, headers);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize request body", e);
            throw new RuntimeException("Failed to serialize request body", e);
        }
    }

    /**
     * JSON 응답을 파싱합니다.
     *
     * @param json      JSON 문자열
     * @param valueType 데이터 타입 클래스
     * @return 파싱된 응답 객체
     */
    private <T> StorageResponse<T> parseResponse(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructParametricType(StorageResponse.class, valueType));
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse response", e);
            return StorageResponse.failure("Failed to parse response: " + e.getMessage());
        }
    }
}
