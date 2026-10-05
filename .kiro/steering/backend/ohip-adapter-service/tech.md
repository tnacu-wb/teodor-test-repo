---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/ohip-adapter-service/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.6 (with Spring Cloud, Spring WebFlux, Spring Security OAuth2)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring WebFlux | Reactive HTTP client (WebClient) for OHIP API calls |
| Spring Security OAuth2 Client | OAuth2 token management for OHIP authentication |
| Spring Data Redis | Caching layer (Lettuce client, Redis cluster) |
| Spring Cloud Bootstrap | Externalised configuration |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`) |
| MapStruct 1.6.2 | Object mapping between domain/infrastructure layers |
| Jackson | JSON serialization/deserialization |
| OpenAPI Generator 7.12.0 | Model generation from Opera OHIP API specs |
| SpringDoc OpenAPI 2.8.3 | API documentation (Swagger UI) |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| Unleash 1.2.3 | Feature flag management |
| ModelMapper 3.2.1 | Additional object mapping |
| Apache PDFBox 3.0.2 | PDF generation |
| Spring WS Core | Web services support |
| Nimbus JOSE JWT 9.37.2 | JWT token handling |
| Commons Validator 1.7 | Input validation utilities |

## Whitbread Shared Libraries

| Library | Version | Purpose |
|---------|---------|---------|
| `commons-entity-exceptions` | 2.0.4 | Standardised exception handling |
| `commons-logging` | 4.0.4 | Structured logging |
| `coding-convention-rules` | 2.0.1 | Custom coding convention enforcement |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Cloud Contract | Contract verification tests |
| WireMock | HTTP mocking for integration tests |
| OkHttp MockWebServer | WebClient integration testing |
| Reactor Test | Reactive stream testing |
| JaCoCo | Code coverage (merged unit + integration reports) |
| PIT (Pitest) | Mutation testing |
| ArchUnit 1.4.2 | Architecture rule enforcement |

## Code Quality

- **Checkstyle**: Google style (`google-checkstyle.xml`), version 10.21.4
- **SonarQube**: Coverage exclusions for models, generated code, config, mappers, exceptions

## Common Commands

```bash
# Build and run tests (from backend/ directory)
cd backend && ./mvnw clean install -pl discover-search/services/ohip-adapter-service -am

# Run locally (requires 'local' profile + local Redis)
cd backend && ./mvnw spring-boot:run -pl discover-search/services/ohip-adapter-service -Dspring.profiles.active=local

# Start local Redis
docker-compose -f backend/discover-search/services/ohip-adapter-service/infrastructure/docker-local/docker-compose.yml up -d

# Run checkstyle only
cd backend && ./mvnw checkstyle:check -pl discover-search/services/ohip-adapter-service

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl discover-search/services/ohip-adapter-service
```

## Application Configuration

- Port: `9100`
- Context path: `/ohip/`
- Profiles: `local` (disables Eureka/ConfigServer for isolated running)
- Tracing: W3C propagation with baggage fields (`x-amzn-trace-id`, `flow-code`, `wb-session-id`)
- Feature flags: Unleash integration (environment-specific)

