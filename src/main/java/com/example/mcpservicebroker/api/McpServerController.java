package com.example.mcpservicebroker.api;

import com.example.mcpservicebroker.store.McpInstance;
import com.example.mcpservicebroker.store.McpInstanceRepository;
import com.example.mcpservicebroker.store.McpInstanceJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mcp-servers")
public class McpServerController {

    private final McpInstanceRepository inMemoryRepo;
    private final McpInstanceJpaRepository jpaRepo;
    private final String activeProfile;

    @Autowired
    public McpServerController(
            @Autowired(required = false) McpInstanceRepository inMemoryRepo,
            @Autowired(required = false) McpInstanceJpaRepository jpaRepo,
            @org.springframework.core.env.Environment orgEnv) {
        this.inMemoryRepo = inMemoryRepo;
        this.jpaRepo = jpaRepo;
        String[] profiles = orgEnv.getActiveProfiles();
        this.activeProfile = profiles.length > 0 ? profiles[0] : "default";
    }

    @GetMapping
    public List<McpInstance> listServers() {
        if (isJpa()) {
            return jpaRepo.findAll();
        } else {
            return inMemoryRepo.findAll().block().values().stream().collect(Collectors.toList());
        }
    }

    @PostMapping
    public ResponseEntity<McpInstance> addServer(@RequestBody McpInstance instance) {
        if (isJpa()) {
            McpInstance saved = jpaRepo.save(instance);
            return ResponseEntity.ok(saved);
        } else {
            inMemoryRepo.save(instance).block();
            return ResponseEntity.ok(instance);
        }
    }

    @DeleteMapping("/{instanceId}")
    public ResponseEntity<Void> deleteServer(@PathVariable String instanceId) {
        if (isJpa()) {
            jpaRepo.deleteById(instanceId);
        } else {
            inMemoryRepo.deleteById(instanceId).block();
        }
        return ResponseEntity.noContent().build();
    }

    private boolean isJpa() {
        return jpaRepo != null && ("cloudfoundry".equals(activeProfile));
    }
}
