---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/business-tether-service-opera/**"
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
| Spring Boot Starter Web Services | SOAP web service client (Worldline B2B PI API) |
| Spring Boot Starter Validation | Jakarta Bean Validation for request payloads |
| Spring Boot Starter Actuator | Health checks, metrics, and management endpoints |
| Spring Boot Micrometer Tracing Brave | Distributed tracing integration |
| Spring Cloud OpenFeign | Declarative HTTP client (PIBA Guid service) |
| Spring Cloud Circuit Breaker Resilience4j | Circuit breaker for Feign clients |
| Spring Cloud Bootstrap | Externalized configuration bootstrap |
| Spring Cloud Commons | Service discovery and load balancing abstractions |
| Spring Data Redis (Lettuce) | Clustered Redis caching |
| Lombok | Boilerplate reduction (`@Slf4j`, `@Data`, `@Builder`, `@AllArgsConstructor`) |
| MapStruct | Object mapping between domain models and API DTOs |
| SpringDoc OpenAPI | Swagger UI and API documentation |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| Google Guava | Utility collections and preconditions |
| Whitbread `commons-logging` | Shared structured logging |
| Whitbread `commons-cdh-lib` | CDH integration library (tethered user registration) |
| Whitbread `common-auth0` | Auth0 multi-tenant JWT authorization and token service |
| Whitbread `worldline-ba-api-lib` | JAXB-generated Worldline SOAP types and security callbacks |
| Whitbread `commons-exceptions` | Shared exception handling and error response models |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Boot Starter Test | JUnit 5, Mockito, AssertJ |
| Spring Cloud Contract Verifier | Consumer-driven contract testing (base class: `ContractVerifierBaseTest`) |
| WireMock Spring Boot | HTTP stub server for external service mocking |
| JaCoCo | Code coverage reporting |
| PIT (Pitest) | Mutation testing with comprehensive mutator set |

## Code Quality

- **PMD**: Custom rules in `codequality/pmd/`
- **SonarQube**: Coverage exclusions for models, properties, utils, and application class
- **Pitest exclusions**: models, config, mappers, constants, exceptions, properties, generated code, and `ArchUnitTests`

## Common Commands

```bash
# Build and run tests (from monorepo root)
cd backend && ./mvnw clean install -pl identity/services/business-tether-service-opera -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw clean install spring-boot:run -pl identity/services/business-tether-service-opera -Dspring.profiles.active=local

# Run only this service's tests
cd backend && ./mvnw test -pl identity/services/business-tether-service-opera

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl identity/services/business-tether-service-opera
```

## Application Configuration

- Port: `9047`
- Base path: `/business/tether`, `/business/tether/login`
- Actuator base path: `/business-tether-service/actuator`
- Profiles: `local`, `opera-dev`, `opera-dit`, `opera-sit`, `opera-uat`, `opera-demo`, `opera-qa`, `opera-prod`, `opera-perf`
- Tracing: Brave with pattern `%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]`
- Cache: Redis cluster with Lettuce client, adaptive refresh, 5-minute period
- Feign: PIBA Guid client with circuit breaker enabled
- Worldline: Multi-scheme SOAP endpoints (GB: en-GB, DE: de-DE) with WS-Security credentials
- Auth: Multi-tenant JWT with URL path regex `.*\/business\/tether\/.*`
