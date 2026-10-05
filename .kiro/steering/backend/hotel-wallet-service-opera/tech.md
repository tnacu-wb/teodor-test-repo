---
inclusion: fileMatch
fileMatchPattern: "backend/arrive-stay-leave/services/hotel-wallet-service-opera/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.3 (Spring Framework 7.x, Spring Security 7.x)

## Build System

- Maven (inherits from `digital-monorepo` parent POM)
- Parent artifact: `uk.co.whitbread:digital-monorepo:1.0.0`

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring WebFlux | Reactive HTTP client (WebClient) for downstream service calls |
| Spring Security OAuth2 Client | OAuth2 authentication |
| Spring Security OAuth2 Resource Server | JWT token validation |
| Thymeleaf (thymeleaf-spring6) | HTML template rendering for wallet pass content |
| jPassKit | Apple Wallet `.pkpass` file generation and signing |
| AWS SDK (S3, STS) | Certificate storage and retrieval |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`) |
| MapStruct | Object mapping between domain/infrastructure layers |
| Jackson | JSON serialization/deserialization |
| OpenAPI Generator 7.12.0 | Model generation from downstream service API specs |
| SpringDoc OpenAPI | API documentation (Swagger UI) |
| Micrometer + Prometheus | Metrics and monitoring |
| Jsoup | HTML parsing/manipulation |
| Nimbus JOSE JWT | JWT token handling |
| ModelMapper | Additional object mapping |
| Commons Validator | Input validation utilities |

## Whitbread Shared Libraries

| Library | Purpose |
|---------|---------|
| `commons-entity-exceptions` | Standardised exception handling |
| `common-auth0` | Auth0 authentication integration |
| `coding-convention-rules` | Custom coding convention enforcement (ArchUnit) |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Cloud Contract | Contract verification tests |
| Pact (Consumer & Provider) | Consumer-driven contract testing |
| WireMock | HTTP mocking for integration tests |
| OkHttp MockWebServer | WebClient integration testing |
| JaCoCo | Code coverage (merged unit + integration reports) |
| PIT (Pitest) | Mutation testing |
| ArchUnit | Architecture rule enforcement |

## Code Quality

- **Checkstyle**: Google style (`google-checkstyle.xml`), version 10.21.4
- **SonarQube**: Coverage exclusions for models, generated code, config, mappers, exceptions, properties

## Common Commands

```bash
# Build and run tests (from backend/ directory)
cd backend && ./mvnw clean install -pl arrive-stay-leave/services/hotel-wallet-service-opera -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw spring-boot:run -pl arrive-stay-leave/services/hotel-wallet-service-opera -Dspring.profiles.active=local

# Run checkstyle only
cd backend && ./mvnw checkstyle:check -pl arrive-stay-leave/services/hotel-wallet-service-opera

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl arrive-stay-leave/services/hotel-wallet-service-opera

# Generate OpenAPI docs
cd backend && ./mvnw verify -pl arrive-stay-leave/services/hotel-wallet-service-opera
```

## Application Configuration

- Port: `9131`
- Base package: `uk.co.whitbread.wallet`
- Profiles: `local` (disables Eureka/ConfigServer for isolated running)
- Metrics: Micrometer with Prometheus registry
- Security: OAuth2 Resource Server (JWT) + Auth0
