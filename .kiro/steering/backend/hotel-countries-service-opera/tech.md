---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/hotel-countries-service-opera/**"
---

# Tech Stack

- Java 25, Spring Boot 4.0.7, Spring Cloud 2025.1.1
- Spring Cloud (commons, OpenFeign)
- Redis (Spring Data Redis, caching)
- JUnit 5, Mockito, Spring Boot Test

## Build

```bash
./mvnw clean install -pl backend/identity/services/hotel-countries-service-opera -am
```
