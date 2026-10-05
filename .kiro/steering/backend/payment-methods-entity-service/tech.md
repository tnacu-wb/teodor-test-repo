---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/payment-methods-entity-service/**"
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
- Checkstyle (Google style, fail on warning)

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Starter Web | Servlet stack for REST controllers (Spring MVC) |
| Spring Boot Starter WebFlux / WebClient | WebClient.Builder for outbound HTTP calls |
| Spring Boot Starter Validation | Bean validation (Jakarta) |
| Spring Boot Starter Security | Security filter chain |
| Spring Boot Starter OAuth2 Resource Server | Multi-tenant JWT validation |
| Spring Boot Starter Data Redis | Redis cluster caching (Lettuce) |
| Spring Boot Starter Actuator | Health probes, metrics, Prometheus endpoint |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| Micrometer Registry Prometheus | Prometheus metrics export |
| Unleash Starter | Feature flag management |
| Lombok | Boilerplate reduction |
| MapStruct | Object mapping between layers |
| SpringDoc OpenAPI (WebMVC) | API documentation |
| Common Auth0 | Shared Auth0 integration library |
| Commons Entity Exceptions | Shared exception handling |
| Commons Logging | Shared structured logging |
| Commons Validator | Input validation utilities |
| Commons IO | File/IO utilities |

## Testing

| Tool | Purpose |
|------|---------|
| JUnit 5 | Unit and integration test framework |
| Spring Cloud Contract | Contract verification (WireMock-based) |
| Mockito (inline) | Mocking framework |
| JaCoCo | Code coverage (merged unit + integration reports) |
| Checkstyle | Google code style enforcement |
| Pitest | Mutation testing |
| ArchUnit | Architecture rule enforcement via `coding-convention-rules` |

## Common Commands

```bash
# Build and run tests
cd backend && ./mvnw clean test -pl book-pay/services/payment-methods-entity-service -am

# Run locally
cd backend && ./mvnw spring-boot:run -pl book-pay/services/payment-methods-entity-service -Dspring.profiles.active=local

# Generate OpenAPI documentation (runs during integration-test phase)
cd backend && ./mvnw verify -pl book-pay/services/payment-methods-entity-service -am
```

## Application Configuration

- Port: `9107`
- Profiles: `local`, `opera-dev`, `opera-qa`, `opera-perf`
- Cache: Redis cluster (SSL, Lettuce client, key prefix `Payment-Methods-Entity-Service`)
- Auth: Multi-tenant OAuth2 resource server (CCUI tenant + PI/BB tenant)
- Tracing: Micrometer + Brave (W3C propagation, baggage: x-amzn-trace-id, flow-code, wb-session-id)
- Feature Flags: Unleash (kill switches, hub hotels, 72h payments)
- Actuator base path: `/v1/payment-methods/actuator`
- Metrics: Prometheus (p95, p99 percentiles, request histograms, 2000ms max expected)
