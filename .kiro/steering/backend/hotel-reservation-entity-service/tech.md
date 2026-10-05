---
inclusion: fileMatch
fileMatchPattern: "backend/manage-modify/services/hotel-reservation-entity-service/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.6 (with Spring Cloud 2025.x, Spring Framework 7.0, Spring Security 7.0)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`
- OpenAPI Generator Maven Plugin for model generation from 9 OpenAPI specs
- Maven Replacer Plugin for `javax` → `jakarta` namespace migration in generated sources

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Starter Web Services | REST API framework |
| Spring Boot Starter Validation | Bean validation (Jakarta) |
| Spring Boot Starter Data Redis | Redis caching (Lettuce cluster client with SSL) |
| Spring Boot Starter Actuator | Health probes, metrics, Prometheus endpoint |
| Spring WebClient | Reactive HTTP client for downstream service calls |
| Spring Retry | Retry logic for transient failures |
| Spring Cloud Commons | Service discovery abstractions |
| Spring Security | Multi-tenant Auth0 JWT validation |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`) |
| MapStruct | Object mapping between layers |
| SpringDoc OpenAPI | API documentation (generated at integration-test phase) |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| Micrometer Prometheus | Metrics export |
| Unleash (springboot-unleash-starter) | Feature flag management |
| Nimbus JOSE JWT | JWT token parsing and validation |
| Commons Validator | Input validation utilities |
| Commons Entity Exceptions | Shared exception handling library |
| Commons Entity Validators | Shared validation library |
| Common Auth0 | Shared Auth0 integration library |
| Commons Logging | Shared structured logging library |
| Jackson Databind Nullable | OpenAPI nullable field support |
| Apache PDFBox | PDF generation |
| QRGen (javase + core) | QR code generation |
| ZXing | Barcode/QR code image processing |
| OpenAPI Generator | Build-time model generation from OpenAPI specs |

## Testing

| Tool | Purpose |
|------|---------|
| JUnit 5 | Unit and integration test framework |
| Spring Cloud Contract | Contract verification (verifier + stub runner + WireMock) |
| Spring Security Test | Security context test support |
| JaCoCo | Code coverage (merged unit + integration reports) |
| PIT (Pitest) | Mutation testing with extended mutator set |
| ArchUnit | Architecture rule enforcement via `coding-convention-rules` |
| Checkstyle | Google code style enforcement |

## Code Quality

- **Checkstyle**: Google style (`google-checkstyle.xml`), fails on warnings
- **SonarQube**: Coverage exclusions for models, config, mappers, exceptions, properties, generated code
- **JaCoCo**: Merges unit test + controller integration test coverage into combined report
- **PIT Mutators**: CONDITIONALS_BOUNDARY, INCREMENTS, INVERT_NEGS, NEGATE_CONDITIONALS, EMPTY_RETURNS, FALSE_RETURNS, TRUE_RETURNS, NULL_RETURNS, PRIMITIVE_RETURNS, REMOVE_CONDITIONALS, REMOVE_INCREMENTS

## Common Commands

```bash
# Build and run tests (from monorepo root)
cd backend && ./mvnw clean install -pl manage-modify/services/hotel-reservation-entity-service -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw spring-boot:run -pl manage-modify/services/hotel-reservation-entity-service -Dspring.profiles.active=local

# Run checkstyle only
cd backend && ./mvnw checkstyle:check -pl manage-modify/services/hotel-reservation-entity-service

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl manage-modify/services/hotel-reservation-entity-service

# Generate OpenAPI documentation (runs during integration-test phase)
cd backend && ./mvnw verify -pl manage-modify/services/hotel-reservation-entity-service -am
```

## Application Configuration

- Port: `9103`
- Actuator base path: `/v1/reservations/actuator`
- Profiles: `local`, `opera-dev`, `opera-qa`, `opera-perf`
- Cache: Redis cluster with SSL, key prefix `Hotel-Reservation-Entity-Service`
- Tracing: W3C propagation with baggage fields (`x-amzn-trace-id`, `flow-code`, `wb-session-id`)
- Auth: Multi-tenant JWT (CCUI, PI BB, Distribution) via Auth0
