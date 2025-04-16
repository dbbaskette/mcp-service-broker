package com.example.mcpservicebroker.config;

import org.springframework.cloud.servicebroker.model.catalog.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class CatalogConfig {

    private static final String MCP_SERVICE_ID = "mcp-service-a7s9d8f";
    private static final String MCP_PLAN_ID = "mcp-register-plan-j3k4l5";

    @Bean
    public Catalog catalog() {
        Plan plan = Plan.builder()
                .id(MCP_PLAN_ID)
                .name("register-existing")
                .description("Registers an existing MCP Server endpoint.")
                .free(true)
                .schemas(createSchemas())
                .build();

        ServiceDefinition serviceDefinition = ServiceDefinition.builder()
                .id(MCP_SERVICE_ID)
                .name("mcp-server")
                .description("Manages registrations for Model Context Protocol (MCP) Servers")
                .bindable(true)
                .tags("mcp", "model", "protocol")
                .plans(Collections.singletonList(plan))
                .metadata(createServiceMetadata())
                .build();

        return Catalog.builder()
                .serviceDefinitions(Collections.singletonList(serviceDefinition))
                .build();
    }

    private Map<String, Object> createServiceMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("displayName", "MCP Server Registration");
        metadata.put("longDescription", "A service broker to register and bind existing MCP server instances within Cloud Foundry.");
        metadata.put("providerDisplayName", "Your Company Name");
        return metadata;
    }

    private Schemas createSchemas() {
        Map<String, Object> createParams = new HashMap<>();
        createParams.put("$schema", "http://json-schema.org/draft-04/schema#");
        createParams.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        properties.put("mcpServerUrl", Map.of(
                "description", "The full URL of the existing MCP Server (e.g., http://host:port).",
                "type", "string",
                "format", "uri"
        ));
        createParams.put("properties", properties);
        createParams.put("required", List.of("mcpServerUrl"));

        ServiceInstanceSchema instanceSchema = ServiceInstanceSchema.builder()
                .createMethodSchema(MethodSchema.builder().parameters(createParams).build())
                .build();

        ServiceBindingSchema bindingSchema = ServiceBindingSchema.builder().build();

        return Schemas.builder()
                .serviceInstanceSchema(instanceSchema)
                .serviceBindingSchema(bindingSchema)
                .build();
    }
}
