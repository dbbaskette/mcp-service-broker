package com.baskettecase.mcpservicebroker.store;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "mcp_instances")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class McpInstance {
    @Id
    @Column(nullable = false, unique = true)
    public String instanceId;
    public String serviceDefinitionId;
    public String planId;
    public String mcpServerUrl;

    public McpInstance(String instanceId, String serviceDefinitionId, String planId, String mcpServerUrl) {
        this.instanceId = instanceId;
        this.serviceDefinitionId = serviceDefinitionId;
        this.planId = planId;
        this.mcpServerUrl = mcpServerUrl;
    }
}
