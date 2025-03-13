package practice.ch6.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.ch6.client.KVStoreClient;
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
}
