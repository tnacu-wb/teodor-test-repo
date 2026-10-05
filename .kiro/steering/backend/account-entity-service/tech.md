---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/account-entity-service/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.6 (with Spring Cloud 2025.1.0)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`
- Annotation processors: Lombok → MapStruct (with lombok-mapstruct-binding)

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Starter Web Services | REST API framework (Spring MVC) |
| Spring Boot Starter WebFlux | Reactive HTTP client (WebClient) for outbound calls |
| Spring Boot Starter Validation | Jakarta Bean Validation for request validation |
| Spring Cloud Commons | Service discovery abstractions |
| Spring Cloud Starter Bootstrap | Externalised configuration via config server |
| Spring Cloud Starter OpenFeign | Declarative HTTP client support |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Data`, `@Builder`) |
| MapStruct | Object mapping between DTOs and domain models |
| SpringDoc OpenAPI | Swagger UI and API documentation |
| Unleash (springboot-unleash-starter) | Feature flag evaluation |
| commons-validator | Input validation utilities (email, postcode) |
| Micrometer + Brave | Distributed tracing and Prometheus metrics |
| Whitbread `commons-exceptions` | Shared exception handling framework |
| Whitbread `commons-logging` | Shared structured logging with masking |
| Whitbread `commons-entity-validators` | Shared entity validation utilities |
| Whitbread `commons-entity-exceptions` | Entity-level exception types |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Boot Starter Test | JUnit 5, Mockito, AssertJ |
| JaCoCo | Code coverage reporting (unit + integration merged) |
| PIT (Pitest) | Mutation testing with comprehensive mutator set |
| ArchUnit (`coding-convention-rules`) | Architectural constraint verification |
| Checkstyle (Google style) | Code style enforcement |

## Code Quality

- **Checkstyle**: Google style rules (`google-checkstyle.xml`)
- **SonarQube**: Coverage exclusions for models, properties, config, mappers, exceptions, generated code
- **Pitest exclusions**: models, config, mappers, constants, exceptions, properties, generated code
- **JaCoCo**: Merged unit + integration coverage with controller-specific reporting

## Common Commands

```bash
# Build and run tests (from backend/ directory)
cd backend && ./mvnw clean install -pl identity/services/account-entity-service -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw clean install spring-boot:run -pl identity/services/account-entity-service -Dspring.profiles.active=local

# Run only this service's tests
cd backend && ./mvnw test -pl identity/services/account-entity-service

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl identity/services/account-entity-service
```

## Application Configuration

- Port: `9120`
- Base path: `/v1/account`
- Actuator base path: `/v1/account/actuator`
- Profiles: `local`, `opera-dev`, `opera-dit`, `opera-sit`, `opera-uat`, `opera-qa`, `opera-perf`, `opera-hulk`, `opera-wanda`, `opera-prod`
- Tracing: Brave with AWS propagation type
- Metrics: Prometheus with 95th/99th percentile histograms for HTTP requests
