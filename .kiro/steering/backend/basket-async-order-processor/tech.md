---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/basket-async-order-processor/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.6 (with Spring Cloud, Spring Kafka, Spring WebFlux)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Kafka | Kafka consumer/producer |
| Spring WebFlux | Reactive HTTP client for REST calls |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`) |
| MapStruct | Object mapping between layers |
| Jackson | JSON serialization/deserialization |
| OpenAPI Generator | Model generation from OpenAPI specs |
| SpringDoc OpenAPI | API documentation |
| Brave (Zipkin) | Distributed tracing for Kafka clients |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Cloud Contract | Contract verification tests |
| Spring Kafka Test | Embedded Kafka for integration tests |
| JaCoCo | Code coverage |
| PIT (Pitest) | Mutation testing |
| ArchUnit | Architecture rule enforcement |
| Whitbread `coding-convention-rules` | Custom coding convention checks |

## Code Quality

- **Checkstyle**: Google style (`google-checkstyle.xml`), fails on warnings
- **SonarQube**: Coverage exclusions for models, config, mappers, exceptions, generated code

## Common Commands

```bash
# Build and run tests (from backend/ directory)
cd backend && ./mvnw clean install -pl book-pay/services/basket-async-order-processor -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw spring-boot:run -pl book-pay/services/basket-async-order-processor -Dspring.profiles.active=local

# Run checkstyle only
cd backend && ./mvnw checkstyle:check -pl book-pay/services/basket-async-order-processor

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl book-pay/services/basket-async-order-processor
```

## Application Configuration

- Port: `9110`
- Context path: `/aop/`
- Profiles: `local`, `opera-dev`, `opera-qa`, `opera-perf`
- Tracing: W3C propagation with baggage fields (`x-amzn-trace-id`, `flow-code`, `wb-session-id`)
