package practice.ch6.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.ch6.client.KVStoreClient;
import practice.ch6.cluster.Node;
import practice.ch6.cluster.NodeManager;
import practice.ch6.hash.ConsistentHash;

import java.util.Base64;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class KVStoreController {
    private final KVStoreClient kvStoreClient;
    private final NodeManager nodeManager;
    private final ConsistentHash consistentHash;

    @GetMapping("/kv/{key}")
    public ResponseEntity<Map<String, String>> getValue(@PathVariable String key) {
        try {
            byte[] value = kvStoreClient.get(key);
            if (value == null) {
                return ResponseEntity.notFound().build();
            }

            String base64Value = Base64.getEncoder().encodeToString(value);
            return ResponseEntity.ok(Map.of("key", key, "value", base64Value));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/kv/{key}")
    public ResponseEntity<Map<String, String>> putValue(
            @PathVariable String key,
            @RequestBody Map<String, String> request) {
        try {
            String base64Value = request.get("value");
            if (base64Value == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Value is required"));
            }

            byte[] value = Base64.getDecoder().decode(base64Value);
            Integer ttl = null;
            if (request.containsKey("ttl")) {
                ttl = Integer.parseInt(request.get("ttl"));
            }

            boolean success;
            if (ttl != null) {
                success = kvStoreClient.put(key, value, ttl);
            } else {
                success = kvStoreClient.put(key, value);
            }

            if (success) {
                return ResponseEntity.ok(Map.of("message", "Value stored successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to store value"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/cluster/status")
    public ResponseEntity<Map<String, Object>> getClusterStatus() {
        Map<String, Object> status = Map.of(
                "nodes", nodeManager.getAllNodes(),
                "hashRing", consistentHash.getRingStatus(),
                "activeNodes", nodeManager.getAllNodes().stream()
                        .filter(node -> node.isActive())
                        .count()
        );

        return ResponseEntity.ok(status);
    }

    @PostMapping("/cluster/node/{nodeId}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateNode(@PathVariable String nodeId) {
        try {
            Node managerNode = nodeManager.getNode(nodeId);
            managerNode.setActive(false);
            managerNode.setStatus(Node.NodeStatus.MAINTENANCE);

            return ResponseEntity.ok(Map.of("message", "Node deactivated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/cluster/node/{nodeId}/activate")
    public ResponseEntity<Map<String, String>> activateNode(@PathVariable String nodeId) {
        try {
            Node managerNode = nodeManager.getNode(nodeId);
            managerNode.setActive(true);
            managerNode.setStatus(Node.NodeStatus.ACTIVE);

            return ResponseEntity.ok(Map.of("message", "Node activated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
