---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/marketing-service-opera/**"
---

# Tech Stack

- Java 25, Spring Boot 4.0.7, Spring Cloud 2025.1.1
- Spring Cloud OpenFeign
- Resilience4j (circuit breaker, retry)
- Redis (Spring Data Redis, caching)
- Auth0 (Spring Security OAuth2 resource server)
- bart-shared-lib (SOAP/BART integration)
- JUnit 5, Mockito, Spring Boot Test

## Build

```bash
./mvnw clean install -pl backend/identity/services/marketing-service-opera -am
```
