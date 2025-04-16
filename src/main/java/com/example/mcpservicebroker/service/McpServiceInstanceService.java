package com.example.mcpservicebroker.service;

import com.example.mcpservicebroker.store.McpInstance;
import com.example.mcpservicebroker.store.McpInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceExistsException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.instance.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class McpServiceInstanceService implements ServiceInstanceService {

    private final McpInstanceRepository instanceRepository;

    @Override
    public Mono<CreateServiceInstanceResponse> createServiceInstance(CreateServiceInstanceRequest request) {
        String instanceId = request.getServiceInstanceId();
        String serviceId = request.getServiceDefinitionId();
        String planId = request.getPlanId();

        log.info("Received create service instance request: instanceId={}, serviceId={}, planId={}",
                instanceId, serviceId, planId);

        return instanceRepository.existsById(instanceId)
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Service instance already exists: {}", instanceId);
                        return Mono.error(new ServiceInstanceExistsException(instanceId, serviceId));
                    } else {
                        Map<String, Object> params = request.getParameters();
                        if (params == null || !params.containsKey("mcpServerUrl") || !(params.get("mcpServerUrl") instanceof String)) {
                            log.error("Missing or invalid required parameter 'mcpServerUrl' for instance {}", instanceId);
                            return Mono.error(new IllegalArgumentException("Missing or invalid required parameter: mcpServerUrl"));
                        }
                        String mcpUrl = ((String) params.get("mcpServerUrl")).trim();
                        if (mcpUrl.isEmpty()) {
                            return Mono.error(new IllegalArgumentException("Required parameter 'mcpServerUrl' cannot be empty"));
                        }
                        log.info("Registering MCP server URL: {} for instance {}", mcpUrl, instanceId);

                        McpInstance instance = new McpInstance(instanceId, serviceId, planId, mcpUrl);

                        return instanceRepository.save(instance)
                                .doOnSuccess(v -> log.info("Successfully registered MCP instance: {}", instanceId))
                                .thenReturn(CreateServiceInstanceResponse.builder()
                                        .instanceExisted(false)
                                        .build());
                    }
                });
    }

    @Override
    public Mono<DeleteServiceInstanceResponse> deleteServiceInstance(DeleteServiceInstanceRequest request) {
        String instanceId = request.getServiceInstanceId();
        log.info("Received delete service instance request: instanceId={}", instanceId);

        return instanceRepository.deleteById(instanceId)
                .map(deletedInstance -> {
                    log.info("Successfully deleted MCP instance: {}", instanceId);
                    return DeleteServiceInstanceResponse.builder().build();
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Attempted to delete non-existent instance: {}", instanceId);
                    return Mono.error(new ServiceInstanceDoesNotExistException(instanceId));
                }));
    }

    @Override
    public Mono<GetServiceInstanceResponse> getServiceInstance(GetServiceInstanceRequest request) {
        String instanceId = request.getServiceInstanceId();
        log.debug("Received get service instance request: instanceId={}", instanceId);

        return instanceRepository.findById(instanceId)
                .map(instance -> GetServiceInstanceResponse.builder()
                        .serviceDefinitionId(instance.getServiceDefinitionId())
                        .planId(instance.getPlanId())
                        .build())
                .switchIfEmpty(Mono.error(new ServiceInstanceDoesNotExistException(instanceId)));
    }
}
