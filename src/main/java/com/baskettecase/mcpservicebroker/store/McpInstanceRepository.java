package com.baskettecase.mcpservicebroker.store;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile({"local", "kubernetes"})
public class McpInstanceRepository {

    private final Map<String, McpInstance> instances = new ConcurrentHashMap<>();

    public Mono<Map<String, McpInstance>> findAll() {
        return Mono.just(instances);
    }

    public Mono<McpInstance> save(McpInstance instance) {
        instances.put(instance.getInstanceId(), instance);
        return Mono.just(instance);
    }

    public Mono<Void> deleteById(String instanceId) {
        instances.remove(instanceId);
        return Mono.empty();
    }
}
