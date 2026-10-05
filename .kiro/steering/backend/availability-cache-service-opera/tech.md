---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/availability-cache-service-opera/**"
---

# Tech Stack

- Java 25, Spring Boot 4.0.7, Spring Cloud 2025.1.1, Spring Cloud
- PostgreSQL (Spring Data JPA)
- Redis (Spring Data Redis)
- OpenFeign (downstream service calls)
- OpenAPI Generator (model/API code generation)
- Unleash (feature flags)
- Spring Security
- JUnit 5, Mockito, Spring Boot Test

## Build

```bash
./mvnw clean install -pl backend/discover-search/services/availability-cache-service-opera -am
```
