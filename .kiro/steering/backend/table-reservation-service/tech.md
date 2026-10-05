---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/table-reservation-service/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE 11 namespace)
- Spring Boot 4.0.7 (with Spring Cloud 2025.1.1, Spring Framework 7.x, Spring Security 7.x)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`
- Spring Cloud Contract Maven Plugin (contract tests, currently skipped)

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Starter Web Services | Servlet stack for REST controllers (Spring MVC) |
| Spring Web RestClient | Outbound HTTP client (LiveRes/Zonal, AEM) |
| Spring Boot Starter Validation | Bean validation (Jakarta) |
| Spring Boot Starter Cache | Caching abstraction |
| Spring Boot Starter Actuator | Health probes, metrics, Prometheus endpoint |
| Spring Security Config | Auth0 JWT validation |
| Caffeine | Local in-memory cache for restaurant content |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`) |
| MapStruct | Object mapping between layers |
| SpringDoc OpenAPI | API documentation (generated at integration-test phase) |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| Logstash Logback Encoder | Structured JSON logging |
| Common Auth0 | Shared Auth0 integration library |
| Commons Exceptions | Shared exception handling library |
| Commons Logging | Shared structured logging library |
| Commons Validator | Input validation utilities |
| Jackson Databind | JSON serialization/deserialization |

## Testing

| Tool | Purpose |
|------|---------|
| JUnit 5 | Unit and integration test framework |
| Spring Cloud Contract | Contract verification (verifier + WireMock) |
| JaCoCo | Code coverage |
| Checkstyle | Google code style enforcement (fails on warnings) |
| ArchUnit | Architecture rule enforcement via `coding-convention-rules` |

## Common Commands

```bash
# Build and run tests (from monorepo root)
cd backend && ./mvnw clean install -pl book-pay/services/table-reservation-service -am

# Run locally (requires profile)
cd backend && ./mvnw spring-boot:run -pl book-pay/services/table-reservation-service -Dspring.profiles.active=local

# Run checkstyle only
cd backend && ./mvnw checkstyle:check -pl book-pay/services/table-reservation-service

# Generate OpenAPI documentation (runs during integration-test phase)
cd backend && ./mvnw verify -pl book-pay/services/table-reservation-service -am
```

## Application Configuration

- Port: `9121`
- Profiles: `local` (isolates from Eureka/Config Server)
- Cache: Caffeine in-memory (AEM content)
- Tracing: Micrometer + Brave (W3C propagation)
- Auth: Auth0 JWT via `common-auth0`
