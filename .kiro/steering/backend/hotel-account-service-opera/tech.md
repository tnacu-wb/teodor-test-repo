---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/hotel-account-service-opera/**"
---

# Tech Stack

## Runtime and build

- Java 25, Jakarta EE namespace
- Spring Boot 4.0.7 and Spring Cloud 2025.1.1
- Maven wrapper with `digital-monorepo-service-parent` and `${revision}` versioning
- Application port `9020`; Actuator base path `/hotel-account-service/actuator`

## Key libraries

- Spring MVC, WebClient/WebFlux, Web Services, OpenFeign, Redis/Lettuce
- `common-auth0`, `commons-cdh-lib`, `commons-azure-email-service`, `commons-validators`, and `commons-entity-validators`
- MapStruct, Jackson, Hibernate Validator, Unleash, Micrometer/Brave
- SpringDoc OpenAPI and OpenAPI Generator

Do not restore the removed global `WebMvcConfigurer.configureContentNegotiation` default. It forces JSON/text for requests without `Accept`, breaking `/v3/api-docs.yaml`; Prometheus already declares its own media types.

## Build commands

```bash
cd backend && ./mvnw clean verify -pl identity/services/hotel-account-service-opera -am
cd backend && ./mvnw test -pl identity/services/hotel-account-service-opera -am
cd backend && ./mvnw spring-boot:run -pl identity/services/hotel-account-service-opera \
  -Dspring-boot.run.profiles=local
```

Use the wrapper only. The `verify` lifecycle generates contract tests/stubs and OpenAPI YAML, runs tests, creates JaCoCo reports, starts/stops the app, and builds an executable Boot JAR. PIT is configured but runs only when its mutation goal is invoked explicitly.

## Configuration and profiles

- Profiles: `local`, `integration`, `opera-dev`, `opera-dit`, `opera-uat`, `opera-perf`, `opera-prod`.
- Local caching is disabled; optional Redis and Unleash containers are under `infrastructure/docker/`.
- Supply Auth0, CDH, Azure email, Worldline, Redis, Unleash, and downstream URL settings through environment variables. Never add real credentials to profile files.

## Testing

Tests cover controllers/contracts, service orchestration, mappers, validators, client fallbacks, and shared library behavior. WireMock supplies external responses. Generated output belongs under `target/`; committed API documentation belongs under `docs/openApi/`.