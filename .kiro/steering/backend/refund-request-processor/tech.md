---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/refund-request-processor/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.7 (with Spring Cloud 2025.1.1, Spring Framework 7.x)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`
- OpenAPI Generator Maven Plugin for model generation from payments service spec
- Maven Replacer Plugin for `javax` → `jakarta` namespace migration in generated sources

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Kafka | Kafka consumer and producer support |
| Spring Boot Starter Web Services | Minimal REST API (health/status) |
| Spring Boot Starter WebClient | HTTP client for 3C Payments API calls |
| Spring Boot Starter Validation | Bean validation (Jakarta) |
| Spring Boot Starter Actuator | Health probes, metrics, Prometheus endpoint |
| Spring Cloud Commons | Service discovery abstractions |
| Spring Retry | Retry logic for transient failures |
| Brave Kafka Clients | Distributed tracing propagation across Kafka messages |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`) |
| MapStruct | Object mapping between layers |
| SpringDoc OpenAPI | API documentation (generated at integration-test phase) |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| Micrometer Prometheus | Metrics export |
| Commons Entity Exceptions | Shared exception handling library |
| Commons Logging | Shared structured logging library |
| Commons Validator | Input validation utilities |
| Jackson Databind Nullable | OpenAPI nullable field support |
| OpenAPI Generator | Build-time model generation from payments OpenAPI spec |

## Testing

| Tool | Purpose |
|------|---------|
| JUnit 5 | Unit and integration test framework |
| Spring Kafka Test | Embedded Kafka broker for integration tests |
| Spring Cloud Contract | Contract verification (verifier) |
| JaCoCo | Code coverage |
| PIT (Pitest) | Mutation testing with extended mutator set |
| Checkstyle | Google code style enforcement |
| ArchUnit | Architecture rule enforcement via `coding-convention-rules` |

## Code Quality

- **Checkstyle**: Google style (`google-checkstyle.xml`), warnings reported (non-blocking)
- **SonarQube**: Coverage exclusions for models, config, mappers, exceptions, properties, generated code
- **JaCoCo**: Standard exclusions for boilerplate packages
- **PIT Mutators**: CONDITIONALS_BOUNDARY, INCREMENTS, INVERT_NEGS, NEGATE_CONDITIONALS, EMPTY_RETURNS, FALSE_RETURNS, TRUE_RETURNS, NULL_RETURNS, PRIMITIVE_RETURNS, REMOVE_CONDITIONALS, REMOVE_INCREMENTS

## Common Commands

```bash
# Build and run tests (from monorepo root)
cd backend && ./mvnw clean install -pl book-pay/services/refund-request-processor -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw spring-boot:run -pl book-pay/services/refund-request-processor -Dspring.profiles.active=local

# Run checkstyle only
cd backend && ./mvnw checkstyle:check -pl book-pay/services/refund-request-processor

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl book-pay/services/refund-request-processor

# Generate OpenAPI documentation (runs during integration-test phase)
cd backend && ./mvnw verify -pl book-pay/services/refund-request-processor -am
```

## Application Configuration

- Port: `9113`
- Profiles: `local` (isolates from Eureka/Config Server)
- Messaging: Kafka consumer (refund requests) + Kafka producer (acknowledgements)
- Tracing: Micrometer + Brave with Kafka instrumentation
- Auth: None (event-driven, no inbound HTTP auth required)
