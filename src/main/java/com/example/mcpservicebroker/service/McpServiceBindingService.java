package com.example.mcpservicebroker.service;

import com.example.mcpservicebroker.store.McpInstance;
import com.example.mcpservicebroker.store.McpInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class McpServiceBindingService implements ServiceInstanceBindingService {

    private final McpInstanceRepository instanceRepository;

    @Override
    public Mono<CreateServiceInstanceBindingResponse> createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
        String instanceId = request.getServiceInstanceId();
        String bindingId = request.getBindingId();
        String serviceId = request.getServiceDefinitionId();

        log.info("Received create service binding request: bindingId={}, instanceId={}, serviceId={}",
                bindingId, instanceId, serviceId);

        return instanceRepository.findById(instanceId)
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Service instance {} not found for binding {}", instanceId, bindingId);
                    return Mono.error(new ServiceInstanceDoesNotExistException(instanceId));
                }))
                .flatMap(instance -> {
                    String mcpUrl = instance.getMcpServerUrl();
                    Map<String, Object> credentials = new HashMap<>();
                    credentials.put("uri", mcpUrl);
                    credentials.put("url", mcpUrl);
                    credentials.put("protocol", "mcp");

                    log.info("Providing credentials for instance {} (binding {}): uri={}", instanceId, bindingId, mcpUrl);

                    return Mono.just(CreateServiceInstanceAppBindingResponse.builder()
                            .credentials(credentials)
                            .bindingExisted(false)
                            .build());
                });
    }

    @Override
    public Mono<DeleteServiceInstanceBindingResponse> deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
        String instanceId = request.getServiceInstanceId();
        String bindingId = request.getBindingId();
        log.info("Received delete service binding request: bindingId={}, instanceId={}", bindingId, instanceId);

        return instanceRepository.existsById(instanceId)
            .flatMap(instanceExists -> {
                if (!instanceExists) {
                    log.warn("Instance {} not found during unbind operation for binding {}", instanceId, bindingId);
                }
                log.info("Successfully processed delete request for binding: {}", bindingId);
                return Mono.just(DeleteServiceInstanceBindingResponse.builder().build());
            });
    }
}
