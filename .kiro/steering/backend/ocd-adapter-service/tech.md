---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/ocd-adapter-service/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE 11 namespace)
- Spring Boot 4.0.7 (with Spring Cloud 2025.1.1, Spring Framework 7.x, Spring Security 7.x)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`
- OpenAPI Generator 7.12.0 (1 execution: OCD distribution shop API)
- Spring Cloud Contract Maven Plugin (contract tests)

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Starter Web Services | Servlet stack for REST controllers (Spring MVC) |
| Spring Boot Starter WebClient | WebClient.Builder for outbound HTTP calls to OCD |
| Spring Boot Starter Validation | Bean validation (Jakarta) |
| Spring Boot Starter Data Redis | Redis caching |
| Spring Boot Starter Security OAuth2 Client | OAuth2 client credentials for OCD API |
| Spring Boot Starter Actuator | Health probes, metrics, Prometheus endpoint |
| Micrometer Registry Prometheus | Prometheus metrics export |
| Micrometer + Brave | Distributed tracing |
| Lombok | Boilerplate reduction |
| MapStruct | Object mapping between layers |
| ModelMapper | Additional object mapping |
| PDFBox | PDF generation for booking documents |
| SpringDoc OpenAPI | API documentation |
| Common Auth0 | Shared Auth0 JWT validation |
| Commons Entity Exceptions | Shared exception handling |
| Commons Logging | Shared structured logging |
| Jackson Databind Nullable | OpenAPI nullable field support |
| NimbusDS JOSE JWT | JWT token handling |

## Testing

| Tool | Purpose |
|------|---------|
| JUnit 5 | Unit and integration test framework |
| Spring Cloud Contract | Contract verification |
| OkHttp MockWebServer | Mock HTTP server for OCD client tests |
| JaCoCo | Code coverage (merged unit + integration reports) |
| Checkstyle | Google code style enforcement |
| ArchUnit | Architecture rule enforcement via `coding-convention-rules` |
| Pitest | Mutation testing |

## Common Commands

```bash
# Build and run tests
cd backend && ./mvnw clean test -pl discover-search/services/ocd-adapter-service -am

# Run locally
cd backend && ./mvnw spring-boot:run -pl discover-search/services/ocd-adapter-service -Dspring.profiles.active=local

# Generate OpenAPI documentation (runs during integration-test phase)
cd backend && ./mvnw verify -pl discover-search/services/ocd-adapter-service -am
```

## Application Configuration

- Port: `9134`
- Context path: `/ocd`
- Profiles: `local` (isolates from config server)
- Cache: Redis
- Auth inbound: Spring Security + Auth0
- Auth outbound: OAuth2 client credentials (OCD API)
- Tracing: Micrometer + Brave
