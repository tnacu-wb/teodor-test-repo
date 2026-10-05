---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/opera-token-service/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.7 (with Spring Cloud 2025.1.1, Spring Framework 7.x, Spring Security 7.x)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`
- SpringDoc OpenAPI Maven Plugin (generates API docs at integration-test phase)
- Spring Cloud Contract Maven Plugin (contract tests)
- Gatling Maven Plugin 4.6.0 (performance tests)

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Starter Web | Servlet stack for REST controllers (Spring MVC) |
| Spring Boot Starter WebFlux / WebClient | WebClient.Builder for outbound HTTP calls |
| Spring Boot Starter Validation | Bean validation (Jakarta) |
| Spring Boot Starter Actuator | Health probes, metrics, Prometheus endpoint |
| Spring Boot Starter OAuth2 Client | OAuth2 client-credentials token acquisition |
| Spring Security OAuth2 Client | Token response handling for Opera auth |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| Micrometer Registry Prometheus | Prometheus metrics export |
| Lombok | Boilerplate reduction |
| MapStruct | Object mapping between layers |
| SpringDoc OpenAPI (WebMVC) | API documentation (UI + API + Common) |
| Common Auth0 | Shared Auth0 integration library |
| Commons Entity Exceptions | Shared exception handling |
| Commons Exceptions | Shared exception utilities |
| Commons Logging | Shared structured logging |
| Commons Validator | Input validation utilities |

## Testing

| Tool | Purpose |
|------|---------|
| JUnit 5 | Unit and integration test framework |
| Spring Cloud Contract | Contract verification |
| Gatling | Performance / load testing |
| Mockito (inline) | Mocking framework |
| JaCoCo | Code coverage |
| Checkstyle | Google code style enforcement |
| ArchUnit | Architecture rule enforcement via `coding-convention-rules` |

## Common Commands

```bash
# Build and run tests
cd backend && ./mvnw clean test -pl discover-search/services/opera-token-service -am

# Run locally
cd backend && ./mvnw spring-boot:run -pl discover-search/services/opera-token-service -Dspring.profiles.active=local

# Generate OpenAPI documentation (runs during integration-test phase)
cd backend && ./mvnw verify -pl discover-search/services/opera-token-service -am

# Run Gatling performance tests
cd backend && ./mvnw gatling:test -pl discover-search/services/opera-token-service
```

## Application Configuration

- Port: `9136`
- Profiles: `local`
- OAuth2 client: Spring Security client-credentials grant against Opera token endpoint
- Token refresh: Configurable refresh-ahead window (`app.token.refreshAheadMinutes`)
- Tracing: Micrometer + Brave (W3C propagation)
- Actuator base path: `/tokens/actuator`
- Metrics: Prometheus (p95, p99 percentiles, histograms)
