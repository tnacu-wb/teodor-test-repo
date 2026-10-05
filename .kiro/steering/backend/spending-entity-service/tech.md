---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/spending-entity-service/**"
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
| Spring Boot Starter Web | REST API framework |
| Spring Cloud OpenFeign | Declarative HTTP clients (Worldline, PIBA Account Service, CDH Adapter) |
| Spring Cloud Circuit Breaker (Resilience4j) | Fallback handling for Feign clients |
| Spring Boot Starter Validation | Jakarta Bean Validation |
| Spring Data Redis (Lettuce) | Clustered Redis caching |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Data`, `@Builder`) |
| MapStruct | Object mapping between layers |
| SpringDoc OpenAPI | Swagger UI and API documentation |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| commons-validator | Input validation utilities |
| Whitbread `commons-exceptions` | Shared exception handling |
| Whitbread `commons-entity-exceptions` | Entity-level exception handling |
| Whitbread `commons-cdh-lib` | CDH integration library |
| Whitbread `coding-convention-rules` | ArchUnit coding convention checks |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Boot Starter Test | JUnit 5, Mockito, AssertJ |
| JaCoCo | Code coverage reporting |
| PIT (Pitest) | Mutation testing |
| ArchUnit | Architecture rule enforcement (via coding-convention-rules) |

## Code Quality

- **Checkstyle**: Google style (`google-checkstyle.xml`), fails on warnings
- **SonarQube**: Coverage exclusions for models, properties, config, mappers, exceptions, generated code

## Common Commands

```bash
# Build and run tests (from backend/ directory)
cd backend && ./mvnw clean install -pl identity/services/spending-entity-service -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw spring-boot:run -pl identity/services/spending-entity-service -Dspring.profiles.active=local

# Run checkstyle only
cd backend && ./mvnw checkstyle:check -pl identity/services/spending-entity-service

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl identity/services/spending-entity-service

# Start local Redis (required for local profile)
docker-compose -f backend/identity/services/spending-entity-service/infrastructure/docker-local/docker-compose.yml up -d
```

## Application Configuration

- Port: `9132`
- Base path: `/v1/spending/`
- Profiles: `local`, `opera-dev`, `opera-qa`, `opera-perf`
- Cache: Redis cluster with Lettuce client, adaptive refresh, 5-minute period
- Feign circuit breaker: enabled globally
