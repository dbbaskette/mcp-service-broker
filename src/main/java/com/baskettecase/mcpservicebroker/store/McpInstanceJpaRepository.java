package com.baskettecase.mcpservicebroker.store;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("cloudfoundry")
public interface McpInstanceJpaRepository extends JpaRepository<McpInstance, String> {
}
