package com.example.mcpservicebroker.store;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile({"local", "kubernetes"})
public class McpInstanceRepository {

    private final Map<String, McpInstance> instances = new ConcurrentHashMap<>();

    public Mono<Void> save(McpInstance instance) {
        return Mono.fromRunnable(() -> instances.put(instance.getInstanceId(), instance));
    }

    public Mono<McpInstance> findById(String instanceId) {
        return Mono.justOrEmpty(instances.get(instanceId));
    }

    public Mono<McpInstance> deleteById(String instanceId) {
        return Mono.justOrEmpty(instances.remove(instanceId));
    }

    public Mono<Boolean> existsById(String instanceId) {
        return Mono.just(instances.containsKey(instanceId));
    }

    public Mono<Map<String, McpInstance>> findAll() {
        return Mono.just(Map.copyOf(instances));
    }
}
