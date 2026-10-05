# Requirements Document

## Introduction

Add the CDH adapter service to the existing Docker Compose integration environment, following the same patterns established by ohip-adapter-service and content-entity-service. This includes a WireMock container to simulate the CDH API (including OAuth token endpoint), a Dockerfile, an environment file, docker-compose service definitions, and updates to the build script and services diagram.

## Glossary

- **CDH_Adapter_Service**: The Spring Boot microservice that adapts requests to the CDH (Company Data Hub) API, located at `backend/identity/services/cdh-adapter-service`
- **WireMock_CDH**: A WireMock container that simulates both the CDH API endpoints and the Azure OAuth token endpoint used by CDH_Adapter_Service
- **Integration_Environment**: The Docker Compose-based local development environment defined in `backend/integration-env/`
- **Build_Script**: The shell script `backend/integration-env/build.sh` that builds Docker images, starts the compose stack, and verifies health
- **Services_Diagram**: The Mermaid diagram in `backend/integration-env/services.md` documenting service topology

## Requirements

### Requirement 1: WireMock CDH Service Definition

**User Story:** As a developer, I want a WireMock container that mocks the CDH API and OAuth token endpoint, so that CDH_Adapter_Service can run locally without real Azure/CDH credentials.

#### Acceptance Criteria

1. THE Integration_Environment SHALL define a `wiremock-cdh` service using the `wiremock/wiremock:3.12.1` image
2. THE `wiremock-cdh` service SHALL expose port 8080 internally and map it to host port 8445
3. THE `wiremock-cdh` service SHALL include a healthcheck that queries `http://localhost:8080/__admin/mappings`
4. THE `wiremock-cdh` service SHALL be connected to the `integration-net` network
5. THE `wiremock-cdh` service SHALL have a memory limit of 750m

### Requirement 2: CDH Adapter Service Dockerfile

**User Story:** As a developer, I want a Dockerfile for CDH_Adapter_Service, so that it can be built as a local Docker image for the integration environment.

#### Acceptance Criteria

1. THE Dockerfile SHALL use `eclipse-temurin:25-jre` as the base image
2. THE Dockerfile SHALL install `curl` for healthcheck support
3. THE Dockerfile SHALL create a non-root `appuser` user and group
4. THE Dockerfile SHALL copy the pre-built JAR from `backend/identity/services/cdh-adapter-service/target/cdh-adapter-service-1.0.0.jar` to `/app/app.jar`
5. THE Dockerfile SHALL run the application as the non-root `appuser`
6. THE Dockerfile SHALL expose ports 9119 and 5007
7. THE Dockerfile SHALL use an entrypoint that passes `$JAVA_OPTS` to the JVM

### Requirement 3: CDH Adapter Service Environment File

**User Story:** As a developer, I want an environment file for CDH_Adapter_Service, so that it is configured to use WireMock containers instead of real external services.

#### Acceptance Criteria

1. THE environment file SHALL set `SPRING_PROFILES_ACTIVE` to `opera-dit`
2. THE environment file SHALL set `JAVA_OPTS` to include initial/max RAM percentages and a remote debug agent on port 5007
3. THE environment file SHALL set `CDH_API_HOST` to point to the `wiremock-cdh` container URL
4. THE environment file SHALL set `AZURE_OAUTH_CLIENT_TOKEN_URL` to point to the `wiremock-cdh` container URL
5. THE environment file SHALL set `CDH_OAUTH_SCOPE`, `AZURE_FDID`, `ACCOUNT_HUB_OAUTH_SUBSCRIPTION_KEY`, `BOOKING_HUB_OAUTH_SUBSCRIPTION_KEY`, `INN_BUSINESS_OAUTH_SUBSCRIPTION_KEY`, `AZURE_OAUTH_CLIENT_CLIENT_SECRET`, and `AZURE_OAUTH_CLIENT_CLIENT_ID` to mock placeholder values
6. THE environment file SHALL set Unleash variables (`UNLEASH_URL`, `UNLEASH_TOKEN`, `UNLEASH_ENVIRONMENT`) to point to the `wiremock-unleash` container

### Requirement 4: CDH Adapter Service Docker Compose Definition

**User Story:** As a developer, I want the CDH_Adapter_Service defined in docker-compose.yml, so that it starts alongside other integration services with proper dependency ordering.

#### Acceptance Criteria

1. THE Integration_Environment SHALL define a `cdh-adapter-service` service using image `cdh-adapter-service:local`
2. THE `cdh-adapter-service` service SHALL map host port 9119 to container port 9119 and host port 5007 to container port 5007
3. THE `cdh-adapter-service` service SHALL reference the `cdh-adapter-service.env` environment file
4. THE `cdh-adapter-service` service SHALL depend on `wiremock-cdh` and `wiremock-unleash` with `condition: service_healthy`
5. THE `cdh-adapter-service` service SHALL include a healthcheck that queries `http://localhost:9119/v1/cdh/actuator/health`
6. THE `cdh-adapter-service` service SHALL be connected to the `integration-net` network
7. THE `cdh-adapter-service` service SHALL specify a build context at `../../` with the Dockerfile path `backend/integration-env/dockerfiles/cdh-adapter-service.Dockerfile`

### Requirement 5: Build Script Updates

**User Story:** As a developer, I want the build script to build Docker images and start the complete integration environment including CDH, so that running `build.sh` brings up the full stack.

#### Acceptance Criteria

1. THE Build_Script SHALL assume that all service JARs are pre-built and available in their respective target directories
2. THE Build_Script SHALL build the Docker image `cdh-adapter-service:local` using the CDH Dockerfile
3. THE Build_Script SHALL call `wait_for_healthy "cdh-adapter-service"` after starting the compose stack
4. THE Build_Script SHALL print the CDH health endpoint URL in the final success summary

### Requirement 6: Services Diagram Update

**User Story:** As a developer, I want the services diagram to include CDH_Adapter_Service, so that the integration environment topology is accurately documented.

#### Acceptance Criteria

1. THE Services_Diagram SHALL include a `cdh-adapter-service` node
2. THE Services_Diagram SHALL include a `wiremock-cdh` node
3. THE Services_Diagram SHALL show `cdh-adapter-service` connecting to `wiremock-cdh` for CDH API calls
4. THE Services_Diagram SHALL show `cdh-adapter-service` connecting to `wiremock-unleash` for feature flags
