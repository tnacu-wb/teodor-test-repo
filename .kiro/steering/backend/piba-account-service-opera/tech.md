---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/piba-account-service-opera/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.6 (with Spring Cloud 2025.1.0)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`
- Annotation processors: Lombok → MapStruct (with lombok-mapstruct-binding)

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Starter Web Services | REST API framework |
| Spring Boot Starter WebFlux | Reactive HTTP client (WebClient) for CDH calls |
| Spring Cloud OpenFeign | Declarative HTTP clients (Worldline REST, PIBA GUID) |
| Spring Cloud Circuit Breaker (Resilience4j) | Fallback handling for Feign clients |
| Spring Boot Starter Validation | Jakarta Bean Validation |
| Spring Data Redis (Lettuce) | Clustered Redis caching |
| Lombok | Boilerplate reduction (`@Slf4j`, `@AllArgsConstructor`, `@Data`, `@Builder`) |
| MapStruct | Object mapping between layers |
| SpringDoc OpenAPI | Swagger UI and API documentation |
| Apache POI | Excel (XLS) file generation for transaction downloads |
| OpenCSV | CSV file generation for transaction downloads |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| commons-validator | Input validation utilities |
| Whitbread `commons-logging` | Shared structured logging |
| Whitbread `commons-cdh-lib` | CDH integration library |
| Whitbread `common-auth0` | Auth0 JWT authorization (`@EnableAuthorization`) |
| Whitbread `worldline-ba-api-lib` | Worldline SOAP/REST API models and utilities |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Boot Starter Test | JUnit 5, Mockito, AssertJ |
| Spring Cloud Contract Verifier | Consumer-driven contract tests (Groovy DSL) |
| WireMock | HTTP stub server for external service mocking |
| JaCoCo | Code coverage reporting |
| PIT (Pitest) | Mutation testing with comprehensive mutator set |
| Random Beans | Randomized test data generation |

## Code Quality

- **PMD**: Custom rules for code size and naming (`codequality/pmd/`)
- **SonarQube**: Coverage exclusions for models, properties, utils, and application class
- **Pitest exclusions**: models, config, mappers, constants, exceptions, properties, generated code

## Common Commands

```bash
# Build and run tests (from backend/ directory)
cd backend && ./mvnw clean install -pl identity/services/piba-account-service-opera -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw clean install spring-boot:run -pl identity/services/piba-account-service-opera -Dspring.profiles.active=local

# Run only this service's tests
cd backend && ./mvnw test -pl identity/services/piba-account-service-opera

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl identity/services/piba-account-service-opera

# Start local Redis (required for local profile)
docker-compose -f backend/identity/services/piba-account-service-opera/infra/docker-compose.yml up -d
```

## Application Configuration

- Port: `9064`
- Base path: `/piba/account` (v1), `/v2/piba/account` (v2)
- Actuator base path: `/piba-account-service/actuator`
- Profiles: `local`, `opera-dev`, `opera-dit`, `opera-sit`, `opera-uat`, `opera-demo`, `opera-qa`, `opera-prod`, `opera-perf`
- Tracing: Brave with pattern `%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]`
- Cache: Redis cluster with Lettuce client, adaptive refresh, 5-minute period
- Feign circuit breaker: enabled globally
