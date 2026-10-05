---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/company-service-opera/**"
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
| Spring Cloud OpenFeign | Declarative HTTP client (hotel-account service) |
| Spring Cloud Bootstrap | Externalized configuration bootstrap |
| Spring Cloud Commons | Service discovery and load balancing abstractions |
| Spring Boot Starter Actuator | Health checks, metrics, and management endpoints |
| Spring Data Redis (Lettuce) | Clustered Redis caching (24h company profile TTL) |
| Lombok | Boilerplate reduction (`@Slf4j`, `@Data`, `@Builder`) |
| MapStruct | Object mapping between CDH responses and API models |
| SpringDoc OpenAPI | Swagger UI and API documentation |
| Hibernate Validator | Jakarta Bean Validation (9.1.0.Final) |
| commons-validator | Email and input validation utilities |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| Unleash (springboot-unleash-starter) | Feature flag evaluation |
| Hystrix Core | Legacy circuit breaker (Netflix) |
| Whitbread `commons-logging` | Shared structured logging |
| Whitbread `commons-cdh-lib` | CDH integration library (company/employee data) |
| Whitbread `common-auth0` | Auth0 multi-tenant JWT authorization |
| Whitbread `commons-azure-email-service` | Azure email sending |
| Whitbread `commons-entity-validators` | Shared entity validation rules |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Boot Starter Test | JUnit 5, Mockito, AssertJ |
| Random Beans | Randomized test data generation |
| JaCoCo | Code coverage reporting |
| PIT (Pitest) | Mutation testing with comprehensive mutator set |

## Code Quality

- **Google Checkstyle**: Enforced via `google-checkstyle.xml` in project root
- **SonarQube**: Coverage exclusions for models, properties, mappers, config, exceptions, and application class
- **Pitest exclusions**: models, config, mappers, constants, exceptions, properties, generated code, and `ArchUnitTests`

## Common Commands

```bash
# Build and run tests (from monorepo root)
cd backend && ./mvnw clean install -pl identity/services/company-service-opera -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw clean install spring-boot:run -pl identity/services/company-service-opera -Dspring.profiles.active=local

# Run only this service's tests
cd backend && ./mvnw test -pl identity/services/company-service-opera

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl identity/services/company-service-opera
```

## Application Configuration

- Port: `9022`
- Base path: `/companies`, `/company/{companyId}`, `/company/{companyId}/questions`
- Actuator base path: `/company-service/actuator`
- Profiles: `local`, `opera-dev`, `opera-dit`, `opera-sit`, `opera-uat`, `opera-demo`, `opera-qa`, `opera-prod`, `opera-perf`
- Tracing: Brave with pattern `%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]`
- Cache: Redis cluster with Lettuce client, adaptive refresh, 5-minute period, 24h company profile TTL
- Feign: hotel-account client with 20s connect/read timeout
- Feature flags: `company-name-validation`, `cdh-api-deprecation` (Unleash)
