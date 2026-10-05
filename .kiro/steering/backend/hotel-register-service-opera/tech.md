---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/hotel-register-service-opera/**"
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
| Spring Boot Starter WebClient | Reactive HTTP client for outbound calls |
| Spring Boot Starter Validation | Jakarta Bean Validation |
| Spring Boot Starter Data Redis | Clustered Redis caching (Lettuce client) |
| Spring Cloud OpenFeign | Declarative HTTP clients (countries, reservation, marketing) |
| Spring Cloud Bootstrap | Externalized configuration bootstrap |
| Spring Cloud Commons | Service discovery and load balancing abstractions |
| Spring Cloud Contract | Consumer-driven contract testing (verifier + WireMock) |
| Spring Boot Starter Actuator | Health checks, metrics, and management endpoints |
| Lombok | Boilerplate reduction (`@Slf4j`, `@Data`, `@Builder`) |
| MapStruct | Object mapping between request/response models |
| SpringDoc OpenAPI | Swagger UI and API documentation |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| Micrometer Prometheus | Metrics export for monitoring |
| Unleash (springboot-unleash-starter) | Feature flag evaluation |
| Google reCAPTCHA (recaptchav2-java) | Bot protection for registration endpoints |
| Auth0 java-jwt | JWT token handling for Auth0 integration |
| Groovy | Scripting support (contract DSL and utilities) |
| Jackson BOM 3.0.4 | JSON serialization/deserialization |
| Whitbread `commons-cdh-lib` | CDH integration library (customer account creation) |
| Whitbread `common-auth0` | Auth0 multi-tenant JWT authorization |
| Whitbread `commons-azure-email-service` | Azure email sending (PTI and generic) |
| Whitbread `commons-validators` | Input validation utilities |
| Whitbread `commons-entity-validators` | Entity validation rules |
| Whitbread `commons-logging` | Shared structured logging |
| Whitbread `commons-exceptions` | Common exception handling |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Boot Starter Test | JUnit 5, Mockito, AssertJ |
| Spring Cloud Contract Verifier | Consumer-driven contract tests with Groovy DSL |
| Spring Cloud Contract WireMock | HTTP stub server for integration tests |
| REST Assured (spring-mock-mvc) | Controller-level HTTP testing |
| Random Beans | Randomized test data generation |
| JaCoCo | Code coverage reporting |
| PIT (Pitest) | Mutation testing with comprehensive mutator set |

## Code Quality

- **SonarQube**: Coverage exclusions for models, properties, and application class
- **Pitest exclusions**: models, config, mappers, constants, exceptions, properties, generated code, and `ArchUnitTests`
- **Pitest mutators**: CONDITIONALS_BOUNDARY, INCREMENTS, INVERT_NEGS, NEGATE_CONDITIONALS, EMPTY_RETURNS, FALSE_RETURNS, TRUE_RETURNS, NULL_RETURNS, PRIMITIVE_RETURNS, REMOVE_CONDITIONALS, REMOVE_INCREMENTS

## Common Commands

```bash
# Build and run tests (from monorepo root)
cd backend && ./mvnw clean install -pl identity/services/hotel-register-service-opera -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw clean install spring-boot:run -pl identity/services/hotel-register-service-opera -Dspring.profiles.active=local

# Run only this service's tests
cd backend && ./mvnw test -pl identity/services/hotel-register-service-opera

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl identity/services/hotel-register-service-opera
```

## Application Configuration

- Port: `9027`
- Base paths: `/customers/hotels`, `/v1/hotel-register/accounts/register`, `/v1/hotel-register/innbusiness/registration`, `/v1/hotel-register/universal-login/leisure`
- Actuator base path: `/v1/hotel-register/actuator`
- Profiles: `local`, `opera-dev`, `opera-dit`, `opera-sit`, `opera-uat`, `opera-demo`, `opera-qa`, `opera-prod`, `opera-perf`
- Tracing: Brave with pattern `%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]`
- Cache: Redis cluster with Lettuce client, adaptive refresh, 5-minute period
- Feign clients: hotel-countries, hotel-reservation-entity-service, marketing-service-opera
- Feature flags: `company-name-validation`, `cdh-api-deprecation` (Unleash)
- Encryption: AES with configurable secret key, salt, and static IV
- Password policies: legacy (min/max length + illegal chars) and Auth0-compliant (3-of-4 criteria)
