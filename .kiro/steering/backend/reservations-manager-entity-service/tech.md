---
inclusion: fileMatch
fileMatchPattern: "backend/manage-modify/services/reservations-manager-entity-service/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.7 (with Spring Cloud 2025.1.1, Spring Framework 7.x, Spring Security 7.x)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`
- OpenAPI Generator Maven Plugin for model generation from 2 OpenAPI specs (Basket, CDH Adapter)
- Maven Replacer Plugin for `javax` → `jakarta` namespace migration in generated sources
- Spring Cloud Contract Maven Plugin for contract tests

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Starter Web Services | REST API framework |
| Spring Boot Starter WebFlux | Reactive support (WebClient for downstream calls) |
| Spring Boot Starter Validation | Bean validation (Jakarta) |
| Spring Boot Starter Cache | Caching abstraction |
| Spring Boot Starter Data Redis | Redis caching (Lettuce cluster client) |
| Spring Boot Starter Actuator | Health probes, metrics, Prometheus endpoint |
| Spring Cloud Starter OpenFeign | Declarative HTTP clients |
| Thymeleaf | HTML template engine for invoice generation |
| OpenHTMLToPDF (pdfbox) | HTML-to-PDF rendering for confirmations/invoices |
| AWS SDK v2 (S3, STS) | S3 storage for generated PDFs, STS for role assumption |
| Unleash (springboot-unleash-starter) | Feature flag management |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`) |
| MapStruct | Object mapping between layers |
| SpringDoc OpenAPI | API documentation (generated at integration-test phase) |
| Micrometer + Brave | Distributed tracing |
| Micrometer Prometheus | Metrics export |
| Commons CDH Lib | Shared CDH client library |
| Common Auth0 | Shared Auth0 integration library |
| Commons Entity Exceptions | Shared exception handling library |
| Commons Logging | Shared structured logging library |
| Commons Validator | Input validation utilities |
| Commons IO | I/O utility functions |
| Jackson Databind Nullable | OpenAPI nullable field support |

## Testing

| Tool | Purpose |
|------|---------|
| JUnit 5 | Unit and integration test framework |
| Spring Cloud Contract | Contract verification (verifier + stub runner + WireMock) |
| WireMock Standalone | HTTP stub server for integration tests |
| JaCoCo | Code coverage |
| PIT (Pitest) | Mutation testing with extended mutator set |
| Checkstyle | Google code style enforcement (fails on errors) |
| ArchUnit | Architecture rule enforcement via `coding-convention-rules` |

## Code Quality

- **Checkstyle**: Google style (`google-checkstyle.xml`), fails on errors
- **SonarQube**: Coverage exclusions for models, config, mappers, exceptions, properties, generated code
- **JaCoCo**: Standard exclusions for boilerplate packages
- **PIT Mutators**: CONDITIONALS_BOUNDARY, INCREMENTS, INVERT_NEGS, NEGATE_CONDITIONALS, EMPTY_RETURNS, FALSE_RETURNS, TRUE_RETURNS, NULL_RETURNS, PRIMITIVE_RETURNS, REMOVE_CONDITIONALS, REMOVE_INCREMENTS

## Common Commands

```bash
# Build and run tests (from monorepo root)
cd backend && ./mvnw clean install -pl manage-modify/services/reservations-manager-entity-service -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw spring-boot:run -pl manage-modify/services/reservations-manager-entity-service -Dspring.profiles.active=local

# Run checkstyle only
cd backend && ./mvnw checkstyle:check -pl manage-modify/services/reservations-manager-entity-service

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl manage-modify/services/reservations-manager-entity-service

# Generate OpenAPI documentation (runs during integration-test phase)
cd backend && ./mvnw verify -pl manage-modify/services/reservations-manager-entity-service -am
```

## Application Configuration

- Port: `9116`
- Profiles: `local`, `integration`
- Cache: Redis cluster with Lettuce client
- Storage: AWS S3 for generated invoice PDFs
- Tracing: Micrometer + Brave
- Auth: Auth0 JWT via `common-auth0`
- Feature flags: Unleash
