---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/cdh-adapter-service/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.6 (with Spring Cloud, Spring WebFlux, Spring Security)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring WebFlux | Reactive HTTP client (WebClient) for CDH API calls |
| Spring Web Services | Web services support |
| Spring Security Core | Security integration |
| Spring Cloud OpenFeign | Declarative HTTP client for OAuth token endpoint |
| Spring Cloud Bootstrap | Externalised configuration |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`) |
| MapStruct | Object mapping between domain/infrastructure layers |
| Jackson | JSON serialization/deserialization |
| SpringDoc OpenAPI | API documentation (Swagger UI) |
| Micrometer + Brave | Distributed tracing |
| Unleash | Feature flag management |
| Commons Validator | Input validation utilities |
| Feign HttpClient | HTTP client for Feign (response body extraction) |

## Whitbread Shared Libraries

| Library | Purpose |
|---------|---------|
| `commons-entity-exceptions` | Standardised exception handling |
| `commons-logging` | Structured logging |
| `coding-convention-rules` | Custom coding convention enforcement (test scope) |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Boot Test | Test context and integration testing |
| Spring Cloud Contract | Contract verification tests |
| OkHttp MockWebServer | WebClient integration testing |
| Reactor Test | Reactive stream testing |
| JaCoCo | Code coverage (merged unit + integration reports) |
| PIT (Pitest) | Mutation testing |

## Code Quality

- **Checkstyle**: Google style (`google-checkstyle.xml`), version 9.2
- **SonarQube**: Coverage exclusions for models, generated code, config, mappers, exceptions

## Common Commands

```bash
# Build and run tests (from monorepo root)
cd backend && ./mvnw clean install -pl identity/services/cdh-adapter-service -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw spring-boot:run -pl identity/services/cdh-adapter-service -Dspring.profiles.active=local

# Run checkstyle only
cd backend && ./mvnw checkstyle:check -pl identity/services/cdh-adapter-service

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl identity/services/cdh-adapter-service
```

## Application Configuration

- Port: `9119`
- Context path: `/v1/cdh`
- Actuator base path: `/v1/cdh/actuator`
- Profiles: `local` (disables Eureka/ConfigServer for isolated running)
- Tracing: Micrometer + Brave distributed tracing
- Feature flags: Unleash integration (environment-specific)
- OAuth: Azure AD client credentials flow for CDH API authentication
