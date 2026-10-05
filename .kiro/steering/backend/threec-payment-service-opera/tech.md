---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/threec-payment-service-opera/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.7 (with Spring Cloud 2025.1.1, Spring Framework 7.x, Spring Security 7.x)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`
- SpringDoc OpenAPI Maven Plugin (generates API docs at integration-test phase)

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Starter WebFlux | Reactive web stack (Netty-based, functional routing) |
| Spring Boot Starter Security | WebFlux security configuration |
| Spring Boot Starter Validation | Bean validation (Jakarta) |
| Spring Boot Starter Actuator | Health probes, metrics |
| AWS SDK v2 DynamoDB Enhanced | Payment transaction state persistence |
| AWS SDK v2 STS | IAM role assumption (IRSA) |
| Braintree Java | PayPal payment processing |
| Unleash Starter | Feature flag management |
| Thymeleaf (spring6) | XML template rendering for 3C request payloads |
| BouncyCastle (bcprov-jdk18on) | Cryptographic operations |
| Lombok | Boilerplate reduction |
| MapStruct | Object mapping between layers |
| SpringDoc OpenAPI (WebFlux) | API documentation |
| Guava | Utility functions |
| JAXB (Jakarta XML Bind) | XML serialisation/deserialisation for 3C API |
| Commons Entity Exceptions | Shared exception handling |
| Commons Logging | Shared structured logging |
| Spring Aspects | AOP support |

## Testing

| Tool | Purpose |
|------|---------|
| JUnit 5 | Unit and integration test framework |
| Reactor Test | Reactive stream testing utilities |
| MockWebServer (OkHttp3) | HTTP stub server for WebClient tests |
| Spring Security Test | Security context mocking |
| JaCoCo | Code coverage |
| Pitest | Mutation testing |

## Common Commands

```bash
# Build and run tests
cd backend && ./mvnw clean test -pl book-pay/services/threec-payment-service-opera -am

# Run locally (requires local DynamoDB on port 8042)
cd backend && ./mvnw spring-boot:run -pl book-pay/services/threec-payment-service-opera -Dspring.profiles.active=local

# Generate OpenAPI documentation (runs during integration-test phase)
cd backend && ./mvnw verify -pl book-pay/services/threec-payment-service-opera -am
```

## Application Configuration

- Port: `9001` (reactive/Netty)
- Profiles: `local`, `opera-dev`, `opera-qa`, `opera-uat`, `opera-dit`, `opera-perf`, `opera-prod`
- Web type: Reactive (`spring.main.web-application-type: reactive`)
- Persistence: DynamoDB (IRSA in cluster, local endpoint for dev)
- Feature Flags: Unleash
- Auth: Spring Security WebFlux
- Tracing: Micrometer + Brave (W3C propagation via log pattern)
- Actuator base path: `/threec-payment-service/actuator`
