---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/content-entity-service/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.6 (with Spring Cloud, Spring WebFlux, Spring Data Redis)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Starter Web Services | REST API (servlet-based) |
| Spring Boot Starter WebFlux | Reactive HTTP client (WebClient) for outbound calls |
| Spring Boot Starter Data Redis | Redis caching (Lettuce client, cluster mode) |
| Spring Boot Starter Cache | Cache abstraction |
| Spring Boot Starter Validation | Bean validation (Jakarta) |
| Spring Cloud Commons | Service discovery abstractions |
| Spring Cloud Bootstrap | External config bootstrap |
| Spring Security | Security filters (web + core) |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`) |
| MapStruct | Object mapping between layers |
| Jackson | JSON serialization/deserialization |
| SpringDoc OpenAPI | API documentation (Swagger UI) |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| Micrometer Prometheus | Metrics export |
| Unleash | Feature flag management |
| Logback | Logging |
| Commons Validator | URL/input validation |
| Whitbread `commons-entity-exceptions` | Shared exception handling |
| Whitbread `commons-logging` | Shared logging configuration |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Cloud Contract | Contract verification tests |
| WireMock Spring Boot | HTTP stub server for integration tests |
| Mockito | Unit test mocking |
| JaCoCo | Code coverage (unit + integration merged) |
| PIT (Pitest) | Mutation testing |
| ArchUnit | Architecture rule enforcement |
| Whitbread `coding-convention-rules` | Custom coding convention checks |

## Code Quality

- **Checkstyle**: Google style (`google-checkstyle.xml`), fails on warnings
- **SonarQube**: Coverage exclusions for models, config, mappers, exceptions, properties, generated code
- **SpringDoc OpenAPI Maven Plugin**: Auto-generates OpenAPI YAML during integration-test phase

## Common Commands

```bash
# Build and run tests (from backend/ directory)
cd backend && ./mvnw clean install -pl discover-search/services/content-entity-service -am

# Run locally (requires 'local' profile + local Redis)
cd backend && ./mvnw spring-boot:run -pl discover-search/services/content-entity-service -Dspring.profiles.active=local

# Start local Redis dependency
docker-compose -f backend/discover-search/services/content-entity-service/infrastructure/docker-local/docker-compose.yml up -d

# Run checkstyle only
cd backend && ./mvnw checkstyle:check -pl discover-search/services/content-entity-service

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl discover-search/services/content-entity-service
```

## Application Configuration

- Port: `9106`
- Actuator base path: `/v1/content/actuator`
- Profiles: `local`, `integration`, `opera-dev`, `opera-qa`, `opera-perf`, `opera-prod`
- Tracing: W3C propagation with baggage fields (`x-amzn-trace-id`, `flow-code`, `wb-session-id`)
- Cache: Redis cluster with Lettuce client, key prefix `Content-Entity-Service`
- Feature flags: Unleash with environment-based configuration
- Schedulers: Hotel search filters (weekly, Monday 6am), opening-soon (daily, 5am)

