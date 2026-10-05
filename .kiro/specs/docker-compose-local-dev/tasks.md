# Implementation Plan: Docker Compose Local Dev Environment

## Overview

Create a Docker Compose local development environment for the ohip-adapter-service at `backend/integration-env/`. The implementation creates the compose file, runtime Dockerfile, build script, environment file, Unleash WireMock mappings, and Unleash seeding script needed to run the service with local dependencies via one build command.

## Tasks

- [x] 1. Create committed environment file and gitignore
  - [x] 1.1 Create the env file at `backend/integration-env/ohip-adapter-service.env`
    - Define all required environment variables: OPERA_HOST, OPERA_APP_KEY, OPERA_CLIENT_ID, OPERA_CLIENT_SECRET, OPERA_USERNAME, OPERA_PASSWORD, ENTERPRISE_ID, REDIS_ENDPOINT, UNLEASH_URL, UNLEASH_TOKEN, UNLEASH_ENVIRONMENT, SPRING_PROFILES_ACTIVE, JAVA_OPTS, and service-specific config values
    - Set OPERA_HOST to `http://wiremock-opera:8080`
    - Set UNLEASH_URL to `http://wiremock-unleash:8080`
    - Set REDIS_ENDPOINT to `redis:6379`
    - Set SPRING_PROFILES_ACTIVE to `opera-dit`
    - Set UNLEASH_ENVIRONMENT to `dit`
    - Use mock placeholder strings for credential variables
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.7_

  - [x] 1.2 Update root `.gitignore` to exclude generated Unleash feature responses
    - Add pattern `backend/integration-env/wiremock/unleash/__files/client-features.json` to prevent committing large DIT-derived feature flag payloads
    - _Requirements: 3.6_

- [x] 2. Create the Dockerfile for ohip-adapter-service
  - [x] 2.1 Create the runtime Dockerfile at `backend/integration-env/dockerfiles/ohip-adapter-service.Dockerfile`
    - Use `eclipse-temurin:25-jre` as the runtime base image
    - Install `curl` for the Compose actuator health check
    - Copy the prebuilt fat JAR from `backend/discover-search/services/ohip-adapter-service/target/`
    - Create a non-root user (`appuser`) in the runtime stage
    - Expose ports 9100 and 5005
    - Set entrypoint to run `java $JAVA_OPTS -jar /app/app.jar`
    - Ensure no build tools, source code, or Maven cache in the final image
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6, 9.7, 9.8_

- [x] 3. Create the Docker Compose file
  - [x] 3.1 Create `backend/integration-env/docker-compose.yml` with all four services
    - Define the `wiremock-opera` service using `wiremock/wiremock:3.12.1`, mapping host port 8443 to container port 8080, with `--verbose` command flag, and health check (`curl -fsS /__admin/mappings`, interval 10s, timeout 5s, 3 retries)
    - Define the `wiremock-unleash` service using `wiremock/wiremock:3.12.1`, mapping host port 8444 to container port 8080, mounting `./wiremock/unleash:/home/wiremock`, with `--verbose` command flag, and health check (`curl -fsS /__admin/mappings`, interval 10s, timeout 5s, 3 retries)
    - Define the `redis` service using `redis:7-alpine`, mapping host port 6379 to container port 6379, with health check (`redis-cli ping`, interval 10s, timeout 5s, 3 retries)
    - Define the `ohip-adapter-service` with image tag `ohip-adapter-service:local`, build context `../../` (monorepo root), dockerfile path `backend/integration-env/dockerfiles/ohip-adapter-service.Dockerfile`, port mappings 9100:9100 and 5005:5005, env_file `./ohip-adapter-service.env`, depends_on all three infra services with `condition: service_healthy`, and health check (`curl -fsS http://localhost:9100/ohip/actuator/health`, interval 10s, timeout 30s, 6 retries, start_period 30s)
    - Set `mem_limit: 750m` on all four services
    - Define a shared custom bridge network `integration-net` and attach all services to it
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 4.1, 4.2, 4.3, 4.4, 4.5, 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 6.1, 6.2, 6.3, 6.4, 6.5, 7.1, 7.3, 7.4, 8.1, 8.3_

- [x] 4. Checkpoint - Validate compose file structure
  - Ensure `docker compose -f backend/integration-env/docker-compose.yml config` validates without errors, ask the user if questions arise.

- [x] 5. Create the build script
  - [x] 5.1 Create `backend/integration-env/build.sh` as an executable shell script
    - Add shebang line (`#!/usr/bin/env bash`) and `set -e` for fail-fast behaviour
    - Determine the monorepo root directory relative to the script location
    - Run `backend/integration-env/scripts/seed-unleash-wiremock.sh` before Maven to fetch DIT Unleash `/client/features` into the WireMock mounted `__files` directory
    - Fail before Maven, image build, or compose startup when `REAL_UNLEASH_TOKEN` is missing or the DIT Unleash fetch fails
    - Run Maven build: `./mvnw clean package -pl backend/discover-search/services/ohip-adapter-service -am -DskipTests`
    - On Maven failure, print error message and exit with non-zero code
    - On Maven success, run Docker-compatible image build using `docker` or `podman`: `<container-cli> build -f backend/integration-env/dockerfiles/ohip-adapter-service.Dockerfile -t ohip-adapter-service:local .`
    - On Docker build failure, print error message and exit with non-zero code
    - Validate Docker Compose configuration
    - Start the stack using Docker Compose: `<compose-cli> -f backend/integration-env/docker-compose.yml up -d --build`
    - Wait for `ohip-adapter-service` to report healthy
    - On success, print completion message indicating Maven build, Docker image build, Docker Compose startup, and health verification succeeded
    - Ensure file has executable permission (chmod +x)
    - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5, 10.6, 10.7, 10.8, 10.9, 10.10, 10.11_

- [x] 6. Final checkpoint - Verify all files are in place
  - Ensure all created files exist with correct content: `docker-compose.yml`, `ohip-adapter-service.Dockerfile`, `build.sh`, `seed-unleash-wiremock.sh`, `ohip-adapter-service.env`, WireMock Unleash mappings, and the `.gitignore` update. Ask the user if questions arise.

## Notes

- This feature consists entirely of infrastructure configuration files (Docker Compose YAML, Dockerfile, Bash, environment files). No application code changes are needed.
- Property-based tests are not applicable since these are declarative configuration artifacts, not functions with inputs/outputs.
- The env file (`ohip-adapter-service.env`) is committed with mock-friendly values. The generated DIT Unleash response body is git-ignored.
- The Docker image tag `ohip-adapter-service:local` in `build.sh` must match the image reference expected by the compose file (which uses a build directive instead of an image reference).
- WireMock Unleash stubs are file-backed because the DIT `/client/features` response is large. The generated response body is git-ignored.
- The build context for the Dockerfile is the monorepo root (`../../` relative to the compose file) so that the prebuilt service JAR is accessible during the Docker build.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "2.1"] },
    { "id": 1, "tasks": ["1.3", "3.1"] },
    { "id": 2, "tasks": ["5.1"] }
  ]
}
```
