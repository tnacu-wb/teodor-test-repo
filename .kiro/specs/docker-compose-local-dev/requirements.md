# Requirements Document

## Introduction

A self-contained Docker Compose setup for local development of the ohip-adapter-service that enables developers to run the service alongside all its infrastructure dependencies without connecting to any real external services. The environment includes:
- A WireMock instance (`wiremock-opera`) that simulates the Oracle Opera OHIP APIs
- A WireMock instance (`wiremock-unleash`) that serves a file-backed copy of the DIT Unleash feature flag API response
- A Redis instance for caching

This eliminates the need to run the separate `docker-local` compose file or connect to real Opera or Redis environments during development and testing. The build script fetches the DIT Unleash client feature response before startup so local feature flag evaluation matches DIT. The new compose file lives at `backend/integration-env/docker-compose.yml`.

The `backend/integration-env/` folder is designed for multi-service extensibility. Each service that needs a local integration environment adds its own Dockerfile in the `dockerfiles/` subfolder and its own `<service-name>.env` file. This convention allows future services to be added without conflicting with existing configurations.

## Glossary

- **Docker_Compose_File**: The `docker-compose.yml` file located at `backend/integration-env/docker-compose.yml` that defines the multi-container local development environment for the ohip-adapter-service, wiremock-opera, wiremock-unleash, and redis
- **Ohip_Adapter_Service**: The Spring Boot microservice container running the ohip-adapter-service application, built from the Service_Dockerfile located in the integration-env folder
- **Wiremock_Opera**: A standalone WireMock container that simulates the Oracle Opera OHIP API endpoints, allowing the ohip-adapter-service to operate without a real Opera connection
- **Wiremock_Unleash**: A standalone WireMock container that serves the DIT Unleash `/client/features` response from a generated file, allowing the ohip-adapter-service to evaluate feature toggles without running a real Unleash server locally
- **Unleash_Seed_Script**: A shell script located at `backend/integration-env/scripts/seed-unleash-wiremock.sh` that fetches the DIT Unleash `/client/features` response using `REAL_UNLEASH_TOKEN` and writes it to the Wiremock_Unleash mounted files directory
- **Redis_Service**: A Redis container using the `redis:7-alpine` image, providing the caching layer that the ohip-adapter-service requires
- **Env_File**: A per-service environment file committed to version control, following the naming convention `<service-name>.env` located at `backend/integration-env/`. For the ohip-adapter-service, this is `backend/integration-env/ohip-adapter-service.env`. The file contains non-sensitive mock-friendly values (WireMock URLs, Redis address, dummy placeholder strings for credentials) since all connections target local mocks. Each service in the integration environment has its own dedicated env file
- **Opera_Host_Variable**: The `OPERA_HOST` environment variable that determines where the Ohip_Adapter_Service sends its OHIP API requests
- **Build_Script**: A shell script located at `backend/integration-env/build.sh` that builds the ohip-adapter-service via Maven and then builds the Docker image using the Service_Dockerfile from the integration-env dockerfiles folder
- **Service_Dockerfile**: A per-service Dockerfile located in `backend/integration-env/dockerfiles/` following the naming convention `<service-name>.Dockerfile`. For the ohip-adapter-service, this is `backend/integration-env/dockerfiles/ohip-adapter-service.Dockerfile`. This convention allows multiple services to each have their own Dockerfile in the same folder
- **Mock_Framework**: A future system that will programmatically install WireMock stubs/mappings into the Wiremock_Opera and Wiremock_Unleash containers via the WireMock Admin API

## Requirements

### Requirement 1: Docker Compose File Structure

**User Story:** As a developer, I want a Docker Compose file at `backend/integration-env/docker-compose.yml` that defines the ohip-adapter-service and all its infrastructure dependencies as services, so that I can start the full local development environment through the build script with a single command.

#### Acceptance Criteria

1. THE Docker_Compose_File SHALL define exactly four services: Ohip_Adapter_Service, Wiremock_Opera, Wiremock_Unleash, and Redis_Service
2. WHEN the Build_Script executes Docker Compose startup, THE Docker_Compose_File SHALL start all four containers such that each reports a running status within 60 seconds
3. THE Docker_Compose_File SHALL place all services on a shared Docker network so that each service can resolve the others by their Docker Compose service names
4. THE Docker_Compose_File SHALL expose host-accessible port mappings for the Ohip_Adapter_Service, Wiremock_Opera, Wiremock_Unleash, and Redis_Service so that a developer can reach each service from the host machine
5. IF any infrastructure service (Wiremock_Opera, Wiremock_Unleash, or Redis_Service) is not yet healthy, THEN THE Docker_Compose_File SHALL define startup dependencies so that the Ohip_Adapter_Service container starts only after all three infrastructure containers are healthy
6. THE Docker_Compose_File SHALL set `mem_limit: 750m` on every service so no local container can consume more than 750 MB of RAM

### Requirement 2: Ohip Adapter Service Container Configuration

**User Story:** As a developer, I want the ohip-adapter-service to run as a container built from its dedicated Dockerfile in the integration-env dockerfiles folder, so that I can test my local changes against the mocked environment.

#### Acceptance Criteria

1. THE Docker_Compose_File SHALL reference the Service_Dockerfile at `backend/integration-env/dockerfiles/ohip-adapter-service.Dockerfile` using a relative path from the compose file location, with the build context set to the monorepo root so that the prebuilt service JAR is accessible, and SHALL tag the resulting service image as `ohip-adapter-service:local`
2. THE Ohip_Adapter_Service SHALL expose port 9100 on the host machine, mapped from container port 9100
3. THE Ohip_Adapter_Service SHALL expose JVM debug port 5005 on the host machine, mapped from container port 5005, so developers can attach an IntelliJ Remote JVM Debug configuration
4. THE Ohip_Adapter_Service SHALL activate the `opera-dit` Spring profile via the SPRING_PROFILES_ACTIVE environment variable
5. THE Ohip_Adapter_Service SHALL load environment variables from the Env_File located at `backend/integration-env/ohip-adapter-service.env` relative to the monorepo root
6. THE Ohip_Adapter_Service SHALL define a health check that verifies the Spring Boot actuator health endpoint is responding within 30 seconds of container start
7. IF the Ohip_Adapter_Service container fails to start within 60 seconds, THEN the container orchestrator SHALL report the service as unhealthy

### Requirement 3: Environment File

**User Story:** As a developer, I want the ohip-adapter-service to load its configuration from a per-service environment file committed to version control, so that I can start the local environment immediately without any manual setup, and future services can each have their own env file without conflicts.

#### Acceptance Criteria

1. THE Docker_Compose_File SHALL reference an Env_File named `ohip-adapter-service.env` located at `backend/integration-env/ohip-adapter-service.env` using the `env_file` directive for the Ohip_Adapter_Service container
2. THE Env_File SHALL be committed directly to version control, containing non-sensitive mock-friendly values suitable for the local Docker Compose environment
3. THE Env_File SHALL contain all required environment variables for the ohip-adapter-service to boot successfully, including: SPRING_PROFILES_ACTIVE, JAVA_OPTS, OPERA_HOST, OPERA_APP_KEY, OPERA_CLIENT_ID, OPERA_CLIENT_SECRET, OPERA_USERNAME, OPERA_PASSWORD, OPERA_AUTH_ENDPOINT, OPERA_SCOPE, OPERA_TOKEN_REFRESH_SKEW, ENABLE_CLIENT_CREDENTIALS, ENTERPRISE_ID, REDIS_ENDPOINT, CACHE_TYPE, UNLEASH_URL, UNLEASH_TOKEN, UNLEASH_ENVIRONMENT, RULES_AGENT_HOST, TOKEN_SERVICE_HOST, WIRE_TAP_ACCESS_TOKEN, OVERRIDE_INSUFFICIENT_CC, NON_DIGITAL_PAYMENT_HOTELS, PROMOTIONAL_RATE, PROMOTIONAL_PACKAGE, and RESTRICTIONS_PARALLEL_THREADS — each set to a non-empty value
4. THE Env_File SHALL set the OPERA_HOST variable value to the Wiremock_Opera service URL (`http://wiremock-opera:8080`)
5. THE Env_File SHALL set the UNLEASH_URL variable value to the Wiremock_Unleash service URL (`http://wiremock-unleash:8080`)
6. THE Env_File SHALL set the REDIS_ENDPOINT variable value to the Redis_Service address within the Docker network (`redis:6379`)
7. THE Env_File SHALL set secret-sourced variables — OPERA_APP_KEY, OPERA_CLIENT_ID, OPERA_CLIENT_SECRET, OPERA_USERNAME, OPERA_PASSWORD, and UNLEASH_TOKEN — to dummy mock values (e.g., `mock-app-key`, `mock-client-id`, `mock-unleash-token`) since these credentials are not validated by the local WireMock mocks
8. THE Env_File SHALL set config-sourced variables — ENTERPRISE_ID, OPERA_AUTH_ENDPOINT, OPERA_SCOPE, OPERA_TOKEN_REFRESH_SKEW, ENABLE_CLIENT_CREDENTIALS, CACHE_TYPE, UNLEASH_ENVIRONMENT, RULES_AGENT_HOST, TOKEN_SERVICE_HOST, WIRE_TAP_ACCESS_TOKEN, OVERRIDE_INSUFFICIENT_CC, NON_DIGITAL_PAYMENT_HOTELS, PROMOTIONAL_RATE, PROMOTIONAL_PACKAGE, and RESTRICTIONS_PARALLEL_THREADS — to their actual production configuration values since these are non-sensitive application settings
9. THE Env_File SHALL set the ENTERPRISE_ID variable value to `WHBPI` (the production configMap value)
10. THE Env_File SHALL set the RULES_AGENT_HOST and TOKEN_SERVICE_HOST variables to the Docker Compose service names intended for those dependencies (`rules-agent-entity-service` and `opera-token-service`) so that future services can be added to the same Docker network without changing the ohip-adapter-service configuration

### Requirement 4: WireMock Opera Container Configuration

**User Story:** As a developer, I want a standalone WireMock instance named wiremock-opera, so that the ohip-adapter-service can make OHIP API calls against a local mock instead of the real Opera system.

#### Acceptance Criteria

1. THE Wiremock_Opera SHALL use the `wiremock/wiremock` Docker image with a pinned version tag
2. THE Wiremock_Opera SHALL be named `wiremock-opera` as the Docker Compose service name
3. THE Wiremock_Opera SHALL map container port 8080 to a designated host port so that developers can access the WireMock admin API and stub management interface from the host machine
4. THE Wiremock_Opera SHALL start with the `--verbose` flag enabled so that all incoming request details are logged to the container's standard output
5. THE Wiremock_Opera SHALL be accessible to the Ohip_Adapter_Service via the hostname `wiremock-opera` on port 8080 within the Docker network

### Requirement 5: WireMock Unleash Container Configuration

**User Story:** As a developer, I want a standalone WireMock instance named wiremock-unleash that mocks the Unleash feature flag API, so that the ohip-adapter-service can evaluate feature toggles without running the real Unleash server and its Postgres dependency.

#### Acceptance Criteria

1. THE Wiremock_Unleash SHALL use the `wiremock/wiremock` Docker image with a pinned version tag
2. THE Wiremock_Unleash SHALL be named `wiremock-unleash` as the Docker Compose service name
3. THE Wiremock_Unleash SHALL map container port 8080 to a designated host port so that developers can access the WireMock admin API from the host machine for inspection
4. THE Wiremock_Unleash SHALL start with the `--verbose` flag enabled so that all incoming request details are logged to the container's standard output
5. THE Wiremock_Unleash SHALL be accessible to the Ohip_Adapter_Service via the hostname `wiremock-unleash` on port 8080 within the Docker network
6. THE Wiremock_Unleash SHALL define a health check that sends an HTTP GET to `/__admin/mappings` and considers the container healthy when it receives a 200 response within 5 seconds, with a check interval of 10 seconds and a maximum of 3 retries
7. THE Wiremock_Unleash SHALL mount `backend/integration-env/wiremock/unleash` to `/home/wiremock` so file-backed mappings and response bodies are loaded at container startup
8. THE Wiremock_Unleash SHALL serve `GET /client/features` from the generated `__files/client-features.json` response body
9. THE Wiremock_Unleash SHALL serve `POST /client/metrics` with a successful empty response so the Unleash client can report metrics without errors

### Requirement 6: Redis Container Configuration

**User Story:** As a developer, I want a Redis instance running within the compose environment, so that the ohip-adapter-service has a local caching layer without needing to run the separate docker-local compose file.

#### Acceptance Criteria

1. THE Redis_Service SHALL use the `redis:7-alpine` Docker image with a pinned version tag
2. THE Redis_Service SHALL be named `redis` as the Docker Compose service name
3. THE Redis_Service SHALL expose the Redis port (6379) on the host machine so that developers can connect to Redis from the host for inspection
4. THE Redis_Service SHALL be accessible to the Ohip_Adapter_Service via the hostname `redis` on port 6379 within the Docker network
5. THE Redis_Service SHALL define a health check so that dependent services can wait for Redis to be ready before starting

### Requirement 7: Service Connectivity and Dependencies

**User Story:** As a developer, I want the ohip-adapter-service to route all external calls to the local mocks and Redis instance within the compose environment, so that I can develop and test without any external dependencies.

#### Acceptance Criteria

1. THE Ohip_Adapter_Service SHALL set the Opera_Host_Variable to `http://wiremock-opera:8080`
2. WHEN the Ohip_Adapter_Service makes an OHIP API call, THE Ohip_Adapter_Service SHALL send the request to the Wiremock_Opera container at the base URL defined by the Opera_Host_Variable, verifiable by the request appearing in the Wiremock_Opera request log (GET `/__admin/requests`)
3. THE Docker_Compose_File SHALL declare the Ohip_Adapter_Service with `depends_on` entries for Wiremock_Opera, Wiremock_Unleash, and Redis_Service, each using `condition: service_healthy`, so that the Ohip_Adapter_Service container does not start until all three infrastructure services pass their health checks
4. THE Wiremock_Opera SHALL define a health check that sends an HTTP GET to `/__admin/mappings` and considers the container healthy when it receives a 200 response within 5 seconds, with a check interval of 10 seconds and a maximum of 3 retries

### Requirement 8: Extensibility for Future Mock Framework

**User Story:** As a developer, I want the WireMock containers to support future programmatic installation of stubs via the WireMock Admin API, so that a mock framework can manage stubs dynamically without manual configuration.

#### Acceptance Criteria

1. THE Wiremock_Opera SHALL expose the WireMock Admin API at the path `/__admin` on the mapped host port for programmatic stub management
2. WHEN an HTTP POST request is sent to `/__admin/mappings` on the Wiremock_Opera mapped host port, THE Wiremock_Opera SHALL accept the stub definition and return an HTTP 201 response within 5 seconds
3. THE Wiremock_Unleash SHALL expose the WireMock Admin API at the path `/__admin` on the mapped host port for programmatic stub management
4. WHEN an HTTP POST request is sent to `/__admin/mappings` on the Wiremock_Unleash mapped host port, THE Wiremock_Unleash SHALL accept the stub definition and return an HTTP 201 response within 5 seconds
5. WHEN either WireMock container starts, THE container SHALL respond to a GET request at `/__admin/mappings` on the mapped host port within 30 seconds of container creation

### Requirement 9: Dockerfile for Ohip Adapter Service

**User Story:** As a developer, I want a Dockerfile in the integration-env dockerfiles folder that packages the host-built ohip-adapter-service artifact, so that private Nexus access remains handled by the developer's local Maven/VPN/proxy setup.

#### Acceptance Criteria

1. THE Service_Dockerfile SHALL be located at `backend/integration-env/dockerfiles/ohip-adapter-service.Dockerfile`
2. THE Service_Dockerfile SHALL use a Java 25 JRE runtime image
3. THE Service_Dockerfile SHALL copy the prebuilt ohip-adapter-service fat JAR from `backend/discover-search/services/ohip-adapter-service/target/`
4. THE Service_Dockerfile SHALL produce a final image containing the compiled fat JAR, Java runtime, and a minimal HTTP client for the container health check, with no build tools, source code, or Maven cache present
5. THE Service_Dockerfile SHALL set the container entry point to execute the fat JAR using `java $JAVA_OPTS -jar /app/app.jar` so local JVM options from the Env_File are applied
6. THE Service_Dockerfile SHALL expose ports 9100 and 5005
7. THE Service_Dockerfile SHALL run the application as a non-root user
8. THE Service_Dockerfile SHALL be built with the repository root as the Docker build context so the prebuilt target JAR is available to copy into the runtime image

### Requirement 10: Build Script

**User Story:** As a developer, I want a build script that compiles the ohip-adapter-service, builds the Docker image, and starts the Docker Compose stack in one step, so that I can quickly rebuild and run the full local integration environment after making code changes.

#### Acceptance Criteria

1. THE Build_Script SHALL be located at `backend/integration-env/build.sh` and be executable (chmod +x)
2. WHEN the Build_Script is executed, THE Build_Script SHALL first run the Unleash_Seed_Script before Maven, Docker image build, or Docker Compose startup
3. THE Unleash_Seed_Script SHALL require `REAL_UNLEASH_TOKEN` to be set and SHALL exit non-zero if the token is missing
4. THE Unleash_Seed_Script SHALL fetch the DIT Unleash response from `${REAL_UNLEASH_URL:-https://eu.app.unleash-hosted.com/eukk0023/api}/client/features` and write it to `backend/integration-env/wiremock/unleash/__files/client-features.json`
5. IF the Unleash_Seed_Script cannot fetch the DIT Unleash response, THEN THE Build_Script SHALL exit non-zero and SHALL NOT run Maven, build the Docker image, or start the Docker Compose stack
6. WHEN the Unleash_Seed_Script completes successfully, THE Build_Script SHALL run the Maven wrapper (`./mvnw`) from the monorepo root to build the ohip-adapter-service module using `./mvnw clean package -pl backend/discover-search/services/ohip-adapter-service -am -DskipTests`
7. WHEN the Maven build completes successfully, THE Build_Script SHALL build the Docker image using an available Docker-compatible CLI (`docker` or `podman`) with the repository root as the build context and the Service_Dockerfile at `backend/integration-env/dockerfiles/ohip-adapter-service.Dockerfile` as the Dockerfile reference
8. THE Build_Script SHALL tag the resulting Docker image with a name that matches the image reference used in the Docker_Compose_File for the Ohip_Adapter_Service
9. WHEN the Docker image build completes successfully, THE Build_Script SHALL validate the Docker Compose configuration
10. WHEN Docker Compose validation completes successfully, THE Build_Script SHALL start the Docker Compose stack using `up -d --build`
11. THE Build_Script SHALL wait for the Ohip_Adapter_Service container to report healthy before printing success
12. IF the Maven build fails, THEN THE Build_Script SHALL exit with a non-zero exit code and print an error message without proceeding to the Docker image build step
13. IF the Docker image build fails, THEN THE Build_Script SHALL exit with a non-zero exit code and print an error message
14. IF Docker Compose validation, startup, or Ohip_Adapter_Service health verification fails, THEN THE Build_Script SHALL exit with a non-zero exit code and print an error message
15. THE Build_Script SHALL print a success message upon completion of the Unleash seed, Maven build, Docker image build, Docker Compose startup, and Ohip_Adapter_Service health verification
