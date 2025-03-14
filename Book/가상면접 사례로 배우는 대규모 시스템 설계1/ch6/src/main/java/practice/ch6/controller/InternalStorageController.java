package practice.ch6.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.ch6.model.StorageRequest;
import practice.ch6.model.StorageResponse;
import practice.ch6.model.VersionedValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/internal/storage")
@RequiredArgsConstructor
public class InternalStorageController {
    private final Map<String, VersionedValue> storage = new ConcurrentHashMap<>();
    private final AtomicLong versionCounter = new AtomicLong(1);

    @GetMapping("/{key}")
    public ResponseEntity<StorageResponse<VersionedValue>> getValue(@PathVariable String key) {
        VersionedValue value = storage.get(key);
        if (value == null) {
            return ResponseEntity.ok(StorageResponse.<VersionedValue>failure("Key not found"));
        }
        return ResponseEntity.ok(StorageResponse.success(value));
    }

    @PutMapping("/{key}")
    public ResponseEntity<StorageResponse<Boolean>> putValue(
            @PathVariable String key,
            @RequestBody StorageRequest request) {

        byte[] value = request.getValue();
        Integer ttl = request.getTtlSeconds();

        long version = versionCounter.getAndIncrement();
        VersionedValue versionedValue = new VersionedValue(value, version, ttl);

        storage.put(key, versionedValue);

        return ResponseEntity.ok(StorageResponse.success(true));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<StorageResponse<Boolean>> deleteValue(@PathVariable String key) {
        VersionedValue removed = storage.remove(key);
        return ResponseEntity.ok(StorageResponse.success(removed != null));
    }
}
