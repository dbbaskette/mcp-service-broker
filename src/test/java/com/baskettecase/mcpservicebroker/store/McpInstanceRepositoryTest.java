package com.baskettecase.mcpservicebroker.store;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class McpInstanceRepositoryTest {
    private McpInstanceRepository repository;

    @BeforeEach
    void setUp() {
        repository = new McpInstanceRepository();
    }

    @Test
    void testSaveAndFindAll() {
        McpInstance instance = new McpInstance("id1", "svc", "plan", "http://localhost:9000");
        repository.save(instance).block();
        Map<String, McpInstance> all = repository.findAll().block();
        assertNotNull(all);
        assertTrue(all.containsKey("id1"));
        assertEquals("http://localhost:9000", all.get("id1").mcpServerUrl);
    }

    @Test
    void testDeleteById() {
        McpInstance instance = new McpInstance("id2", "svc", "plan", "http://localhost:9001");
        repository.save(instance).block();
        repository.deleteById("id2").block();
        Map<String, McpInstance> all = repository.findAll().block();
        assertFalse(all.containsKey("id2"));
    }
}
