---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/payment-orchestration-service/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.7 (with Spring Cloud 2025.1.1)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`

## Key Libraries

| Library | Purpose |
|---------|---------|
| Temporal Java SDK 1.35.0 | Workflow orchestration for payment flows |
| Spring Web | REST controllers |
| Spring Boot Actuator | Health checks and metrics |
| Micrometer + Brave | Observability, distributed tracing |
| SpringDoc OpenAPI | API documentation and Swagger UI |
| MapStruct | Object mapping between layers |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`) |

Use a Temporal Signal for an asynchronous write when the caller does not need a result, an Update for a write that must return a result or error, a Query for a read-only request, and Update-With-Start when the request must also start a missing workflow.

## Testing

| Tool | Purpose |
|------|---------|
| JaCoCo | Code coverage |
| PIT (Pitest) | Mutation testing |
| Spring Cloud Contract | Contract verification tests |
| Whitbread `coding-convention-rules` | Custom coding convention checks |

## Code Quality

- **Checkstyle**: Google style (`google-checkstyle.xml`), fails on warnings
- **SonarQube**: Coverage exclusions for models, config, mappers, exceptions, generated code

## Containerisation

- Base image: `eclipse-temurin:25-jre-alpine`
- Multi-stage Dockerfile for minimal runtime image

## Common Commands

```bash
# Build and run tests (from backend/ directory)
cd backend && ./mvnw clean install -pl book-pay/services/payment-orchestration-service -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw spring-boot:run -pl book-pay/services/payment-orchestration-service -Dspring.profiles.active=local

# Run checkstyle only
cd backend && ./mvnw checkstyle:check -pl book-pay/services/payment-orchestration-service

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl book-pay/services/payment-orchestration-service
```

## Application Configuration

- Port: `9200`
- Context path: none (API served at root, Actuator at `/payment-orchestrator/actuator`)
- Profiles: `local`, `integration`, `opera-dev`, `opera-qa`, `opera-perf`
- Tracing: W3C propagation with baggage fields
