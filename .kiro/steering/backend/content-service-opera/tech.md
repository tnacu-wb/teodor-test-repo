---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/content-service-opera/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.6 (with Spring Cloud, Spring Web MVC, Spring Data Redis)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Starter Web Services | REST API endpoint serving |
| Spring Boot Starter Data Redis | Redis cluster caching (Lettuce client, SSL) |
| Spring Boot Starter Actuator | Health checks, metrics, Prometheus endpoint |
| Spring Cloud OpenFeign | Declarative HTTP clients for AEM content APIs |
| Spring Cloud Circuit Breaker (Resilience4j) | Circuit breaker for AEM Feign clients |
| Spring Cloud Config Client | Externalised configuration |
| Spring Cloud Commons | Service discovery and load balancing utilities |
| SpringDoc OpenAPI (webmvc-ui + webmvc-api) | Swagger UI and API documentation generation |
| Lombok | Boilerplate reduction (`@Data`, `@Builder`, `@Slf4j`) |
| XStream 1.4.21 | XML serialization (legacy AEM response handling) |
| JAXB Runtime | XML binding for AEM content parsing |
| Commons Validator | Input validation utilities |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| Micrometer Prometheus | Metrics export for monitoring |
| Jackson 3.0.4 (BOM) | JSON serialization/deserialization |

## Whitbread Shared Libraries

| Library | Purpose |
|---------|---------|
| `commons-exceptions` | Standardised exception handling |
| `commons-logging` | Structured logging |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Boot Test | Test context and autoconfiguration |
| Spring Cloud Contract Verifier | Consumer-driven contract tests |
| Spring Cloud Contract WireMock | WireMock-based stub generation |
| Mockito | Mocking for unit tests |
| JaCoCo | Code coverage (70% instruction minimum, zero missed classes) |
| PIT (Pitest) | Mutation testing with JUnit 5 plugin |

## Code Quality

- **JaCoCo**: 70% instruction coverage minimum, no missed classes
- **PIT Mutation Testing**: Configured mutators include conditionals, negation, returns, increments
- **SonarQube**: Coverage exclusions for models, exceptions, services, controllers, Application class

## Common Commands

```bash
# Build and run tests (from monorepo root)
cd backend && ./mvnw clean install -pl discover-search/services/content-service-opera -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw spring-boot:run -pl discover-search/services/content-service-opera -Dspring.profiles.active=local

# Run with caching disabled (for local development without Redis)
cd backend && ./mvnw spring-boot:run -pl discover-search/services/content-service-opera -Dspring.profiles.active=local,disable-caching

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl discover-search/services/content-service-opera
```

## Application Configuration

- Port: `9058`
- Actuator base path: `/content-service/actuator`
- Profiles: `local` (uses dev AEM URL, disables compression), `disable-caching` (no Redis, no cache), `opera-dev`/`opera-qa`/`opera-uat`/`opera-perf`
- Cache: Redis cluster (SSL, Lettuce, 24h TTL default for all content caches)
- Tracing: W3C propagation with baggage fields (`x-amzn-trace-id`, `flow-code`)
- AEM Authentication: Basic auth (username/password from environment variables)
- Circuit Breaker: Resilience4j, ignores `FeignNonServerException`
- Feign timeouts: 30s connect, 30s read, max 200 connections per route
