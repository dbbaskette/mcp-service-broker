# MCP Service Broker

A Spring Cloud Open Service Broker for registering and binding Model Context Protocol (MCP) servers. Supports Cloud Foundry, local, and Kubernetes profiles.

## Features
- Register MCP servers via Cloud Foundry Service Broker API or REST API
- Bind MCP servers to apps (credentials provided at bind time)
- REST API for graphical management
- React UI for catalog and registration (see `frontend/`)
- Profile-based persistence: in-memory (local/k8s), Postgres (Cloud Foundry)
- Actuator endpoints for health/info

## Quick Start

### Local Development
```
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Cloud Foundry
Bind a Postgres service and push with `cloudfoundry` profile. Datasource auto-configured from VCAP_SERVICES.

### REST API
- `GET /api/mcp-servers` - List all MCP servers
- `POST /api/mcp-servers` - Register new MCP server (JSON body)
- `DELETE /api/mcp-servers/{instanceId}` - Delete MCP server

### UI
- Start the React UI in `frontend/` (see frontend README)

## Profiles
- `local`: In-memory repo
- `cloudfoundry`: Postgres via JPA (VCAP_SERVICES)
- `kubernetes`: In-memory repo

## Spring Boot Actuator Endpoints

The following actuator endpoints are enabled by default:

- **/actuator/health** — Returns application health status (useful for Cloud Foundry, Kubernetes, and monitoring tools).
- **/actuator/info** — Returns application info (can be customized in application properties).

You can access these endpoints at:

- Local: http://localhost:8081/actuator/health and http://localhost:8081/actuator/info
- Cloud Foundry: https://<your-app>.cfapps.io/actuator/health and /actuator/info
- Kubernetes: http://<pod-ip>:8080/actuator/health and /actuator/info

To expose more actuator endpoints, update `management.endpoints.web.exposure.include` in your properties files.

## License
MIT
