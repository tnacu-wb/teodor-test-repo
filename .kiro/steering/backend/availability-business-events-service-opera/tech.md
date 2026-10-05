---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/availability-business-events-service-opera/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.6 (with Spring Cloud, Spring WebFlux, Spring Data JPA)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Starter GraphQL | GraphQL WebSocket subscription client for OHIP events |
| Spring WebFlux | Reactive HTTP client (WebClient) for Opera and downstream services |
| Spring Data JPA | PostgreSQL database access for availability cache |
| Spring Cloud OpenFeign | Declarative REST clients for internal services |
| Spring Cloud Bootstrap | Externalised configuration |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Data`) |
| Jackson / Gson | JSON serialization/deserialization |
| OpenAPI Generator 7.12.0 | Model generation from Content and OCD Adapter service specs |
| SpringDoc OpenAPI 2.8.3 | API documentation (Swagger UI for actuator endpoints) |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| Unleash | Feature flag management (city tax toggles) |
| PostgreSQL Driver | JDBC driver for availability cache database |
| Apache Commons Collections 4.4 | Collection utilities |
| Apache Commons Lang 3 | String and object utilities |
| Commons Validator | Input validation utilities |
| Reactor Core | Reactive stream processing for WebSocket subscriptions |

## Whitbread Shared Libraries

| Library | Purpose |
|---------|---------|
| `commons-logging` | Structured logging |
| `commons-exceptions` | Standardised exception handling |
| `commons-entity-exceptions` | Entity-level exception handling |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Boot Test | Test context and autoconfiguration |
| Reactor Test | Reactive stream testing |
| Spring GraphQL Test | GraphQL subscription testing |
| Testcontainers (PostgreSQL) | Integration testing with real database |
| H2 | In-memory database for unit tests |
| Mockito | Mocking for unit tests |
| JaCoCo | Code coverage reporting |
| PIT (Pitest) | Mutation testing |

## Code Quality

- **Checkstyle**: Google style (`google-checkstyle.xml`)
- **SonarQube**: Coverage exclusions for models, ports, exceptions, config, mappers, entities, enums, utilities

## Common Commands

```bash
# Build and run tests (from monorepo root)
cd backend && ./mvnw clean install -pl discover-search/services/availability-business-events-service-opera -am

# Run locally (requires 'local' profile + local PostgreSQL)
cd backend && ./mvnw spring-boot:run -pl discover-search/services/availability-business-events-service-opera -Dspring.profiles.active=local

# Start local PostgreSQL
docker-compose -f backend/discover-search/services/availability-business-events-service-opera/infrastructure/docker-local/docker-compose.yml up -d

# Run checkstyle only
cd backend && ./mvnw checkstyle:check -pl discover-search/services/availability-business-events-service-opera

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl discover-search/services/availability-business-events-service-opera
```

## Application Configuration

- Port: `${SPRING_APPLICATION_PORT}` (environment-configured)
- Actuator base path: `/availability-business-events-service/actuator`
- Profiles: `local` (uses local PostgreSQL on `localhost:5432`)
- Database: PostgreSQL (availability cache — hotels, rooms, rates, processed events)
- Tracing: W3C propagation with baggage fields (`x-amzn-trace-id`, `flow-code`)
- Feature flags: Unleash integration (`city-tax-uk`, `city-tax-uk-fallback`, `use-token-refresh-skew`)
- Opera chain code: `WHBOC001`
