---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/basket-service/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE 11 namespace)
- Spring Boot 4.0.7 (with Spring Cloud 2025.1.1, Spring Framework 7.x, Spring Security 7.x)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`
- OpenAPI Generator 7.12.0 (8 execution specs: payments, reservation, content, hotel-info, marketing, rules-agent, ohip-adapter, cdh-adapter)
- Spring Cloud Contract Maven Plugin (contract tests)

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Starter Web Services | Servlet stack for REST controllers (Spring MVC) |
| Spring Boot Starter WebClient | WebClient.Builder for outbound HTTP calls |
| Spring Boot Starter Validation | Bean validation (Jakarta) |
| Spring Boot Starter Data Redis | Redis cluster caching (Lettuce) |
| Spring Boot Starter Actuator | Health probes, metrics, Prometheus endpoint |
| Spring Kafka | Async event publishing (confirmation, refund) |
| AWS SDK v2 DynamoDB | Basket state persistence |
| Unleash Starter | Feature flag management |
| Lombok | Boilerplate reduction |
| MapStruct | Object mapping between layers |
| SpringDoc OpenAPI | API documentation (generated at integration-test phase) |
| Micrometer + Brave | Distributed tracing |
| Common Auth0 | Shared Auth0 integration library |
| Commons Entity Exceptions | Shared exception handling |
| Commons Entity Validators | Shared validation |
| Commons Logging | Shared structured logging |
| Jackson Databind Nullable | OpenAPI nullable field support |

## Testing

| Tool | Purpose |
|------|---------|
| JUnit 5 | Unit and integration test framework |
| Spring Cloud Contract | Contract verification (47 stubs) |
| WireMock | HTTP stub server for integration tests |
| JaCoCo | Code coverage (merged unit + integration reports) |
| Checkstyle | Google code style enforcement |
| ArchUnit | Architecture rule enforcement via `coding-convention-rules` |
| Pitest | Mutation testing |

## Common Commands

```bash
# Build and run tests
cd backend && ./mvnw clean test -pl book-pay/services/basket-service -am

# Run locally
cd backend && ./mvnw spring-boot:run -pl book-pay/services/basket-service -Dspring.profiles.active=local

# Generate OpenAPI documentation (runs during integration-test phase)
cd backend && ./mvnw verify -pl book-pay/services/basket-service -am
```

## Application Configuration

- Port: `9104` (JMX: `9105`)
- Profiles: `local` (isolates from config server)
- Cache: Redis cluster
- Persistence: DynamoDB
- Messaging: Kafka
- Tracing: Micrometer + Brave
- Feature Flags: Unleash
- Auth: Spring Security + Auth0
