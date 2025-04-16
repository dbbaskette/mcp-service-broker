package com.example.mcpservicebroker.store;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@ToString
public class McpInstance {
    @Id
    @Column(nullable = false, unique = true)
    private String instanceId;
    private String serviceDefinitionId;
    private String planId;
    private String mcpServerUrl;
}
