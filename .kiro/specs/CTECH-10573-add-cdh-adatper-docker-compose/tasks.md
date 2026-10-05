# Implementation Plan: Add CDH Adapter Service to Docker Compose Integration Environment

## Overview

Add the CDH adapter service and its WireMock CDH dependency to the existing Docker Compose integration environment, following patterns established by ohip-adapter-service and content-entity-service. All artifacts are infrastructure configuration files (Dockerfile, env file, docker-compose YAML, shell script, diagram).

## Tasks

- [ ] 1. Add WireMock CDH service and CDH Adapter Service Dockerfile
  - [ ] 1.1 Add `wiremock-cdh` service definition to `docker-compose.yml`
    - Add service using `wiremock/wiremock:3.12.1` image
    - Map port 8080 internally to host port 8445
    - Add healthcheck querying `http://localhost:8080/__admin/mappings`
    - Connect to `integration-net` network
    - Set memory limit to 750m
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_

  - [ ] 1.2 Create `cdh-adapter-service.Dockerfile` in `backend/integration-env/dockerfiles/`
    - Use `eclipse-temurin:25-jre` base image
    - Install `curl` for healthcheck
    - Create non-root `appuser` user/group
    - Copy JAR from `backend/identity/services/cdh-adapter-service/target/cdh-adapter-service-1.0.0.jar` to `/app/app.jar`
    - Expose ports 9119 and 5007
    - Set entrypoint passing `$JAVA_OPTS` to JVM
    - Run as `appuser`
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7_

- [ ] 2. Add CDH Adapter Service environment and compose definition
  - [ ] 2.1 Create `cdh-adapter-service.env` in `backend/integration-env/`
    - Set `SPRING_PROFILES_ACTIVE=opera-dit`
    - Set `JAVA_OPTS` with RAM percentages and remote debug agent on port 5007
    - Set `CDH_API_HOST` to `http://wiremock-cdh:8080`
    - Set `AZURE_OAUTH_CLIENT_TOKEN_URL` to `http://wiremock-cdh:8080/oauth2/token`
    - Set mock placeholder values for OAuth credentials (`CDH_OAUTH_SCOPE`, `AZURE_FDID`, `ACCOUNT_HUB_OAUTH_SUBSCRIPTION_KEY`, `BOOKING_HUB_OAUTH_SUBSCRIPTION_KEY`, `INN_BUSINESS_OAUTH_SUBSCRIPTION_KEY`, `AZURE_OAUTH_CLIENT_CLIENT_SECRET`, `AZURE_OAUTH_CLIENT_CLIENT_ID`)
    - Set Unleash variables pointing to `wiremock-unleash` container
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6_

  - [ ] 2.2 Add `cdh-adapter-service` service definition to `docker-compose.yml`
    - Use image `cdh-adapter-service:local`
    - Set build context to `../../` with Dockerfile path `backend/integration-env/dockerfiles/cdh-adapter-service.Dockerfile`
    - Map host ports 9119 and 5007
    - Reference `cdh-adapter-service.env`
    - Add `depends_on` for `wiremock-cdh` and `wiremock-unleash` with `condition: service_healthy`
    - Add healthcheck querying `http://localhost:9119/v1/cdh/actuator/health`
    - Connect to `integration-net` network
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7_

- [ ] 3. Checkpoint - Verify Docker Compose configuration
  - Ensure `docker compose config` validates successfully, ask the user if questions arise.

- [ ] 4. Update build script and services diagram
  - [ ] 4.1 Update `build.sh` to include CDH adapter service
    - Ensure build script assumes all JARs are pre-built (no Maven build step for CDH)
    - Add `docker build` command for `cdh-adapter-service:local` image
    - Add `wait_for_healthy "cdh-adapter-service"` call after compose up
    - Add CDH health endpoint URL to the final success summary
    - _Requirements: 5.1, 5.2, 5.3, 5.4_

  - [ ] 4.2 Update `services.md` diagram to include CDH services
    - Add `cdh-adapter-service` node
    - Add `wiremock-cdh` node
    - Show connection from `cdh-adapter-service` to `wiremock-cdh` for CDH API calls
    - Show connection from `cdh-adapter-service` to `wiremock-unleash` for feature flags
    - _Requirements: 6.1, 6.2, 6.3, 6.4_

- [ ] 5. Final checkpoint - Verify complete integration
  - Ensure all configuration is consistent and `docker compose config` passes, ask the user if questions arise.

## Notes

- No property-based tests — this feature is purely infrastructure configuration
- All artifacts follow existing patterns from `ohip-adapter-service` and `content-entity-service`
- The build script does NOT include Maven builds — it assumes ALL service JARs are pre-built
- `wiremock-cdh` serves dual duty: mocks both CDH API and Azure OAuth token endpoint
- Checkpoints verify docker-compose validity rather than running tests

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "2.1"] },
    { "id": 1, "tasks": ["2.2"] },
    { "id": 2, "tasks": ["4.1", "4.2"] }
  ]
}
```
