---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/hotel-card-service-opera/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.6 (with Spring Cloud, Spring Web, Spring Security)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Web | REST API controllers and web layer |
| Spring Boot Web Services | SOAP/web services support |
| Spring Cloud OpenFeign | Declarative HTTP clients for downstream services |
| Spring Cloud Circuit Breaker (Resilience4j) | Fault tolerance with circuit breakers and time limiters |
| Spring Boot Data Redis | Redis caching (Lettuce client, cluster mode, SSL) |
| Spring Boot Actuator | Health checks, metrics, and monitoring endpoints |
| Spring Boot Validation | Bean validation (Jakarta Validation) |
| Spring Cloud Bootstrap | Externalised configuration support |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`, `@Data`) |
| MapStruct | Object mapping between domain layers |
| Jackson | JSON serialization/deserialization (case-insensitive enums) |
| SpringDoc OpenAPI | API documentation (Swagger UI, webmvc-ui + webmvc-api) |
| OpenAPI Generator | DTO generation from OpenAPI specs (payment models) |
| Unleash | Feature flag management |
| Commons Validator | Input validation utilities |
| Gson | JSON processing (secondary to Jackson) |
| Angus Mail | Email support (Jakarta Mail implementation) |

## Whitbread Shared Libraries

| Library | Purpose |
|---------|---------|
| `worldline-ba-api-lib` | Worldline Business Account API integration |
| `commons-cdh-lib` | CDH (Customer Data Hub) client utilities |
| `commons-logging` | Structured logging |
| `common-auth0` | Auth0 JWT authentication and security |
| `commons-entity-validators` | Entity validation rules |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Boot Test | Test context and integration testing |
| Spring Cloud Contract Verifier | Consumer-driven contract tests |
| REST Assured | HTTP API testing |
| JavaFaker | Test data generation |
| JaCoCo | Code coverage reporting |
| PIT (Pitest) | Mutation testing with custom mutator set |

## Code Quality

- **PMD**: Custom ruleset in `codequality/pmd/`
- **SonarQube**: Coverage exclusions for models, config, mappers, properties, exceptions
- **PIT mutators**: CONDITIONALS_BOUNDARY, INCREMENTS, INVERT_NEGS, NEGATE_CONDITIONALS, EMPTY_RETURNS, FALSE_RETURNS, TRUE_RETURNS, NULL_RETURNS, PRIMITIVE_RETURNS, REMOVE_CONDITIONALS, REMOVE_INCREMENTS

## Common Commands

```bash
# Build and run tests (from monorepo root)
cd backend && ./mvnw clean install -pl identity/services/hotel-card-service-opera -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw spring-boot:run -pl identity/services/hotel-card-service-opera -Dspring.profiles.active=local

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl identity/services/hotel-card-service-opera
```

## Application Configuration

- Port: `9030`
- Actuator base path: `/hotel-card-service/actuator`
- Profiles: `local` (disables caching, uses test auth), `opera-perf`, `opera-dev`, `opera-dit`, `opera-uat`
- Tracing: Micrometer + Brave with W3C propagation (x-amzn-trace-id, flow-code baggage)
- Feature flags: Unleash integration (cdh-delay, company-name-validation, cdh-api-deprecation)
- Security: Auth0 JWT with multi-tenant validation (PI BB Tenant)
- Caching: Redis cluster with SSL, Lettuce client, adaptive refresh
- Circuit breakers: Resilience4j per downstream client (sliding window 10, failure rate 50%, 30s timeout)
