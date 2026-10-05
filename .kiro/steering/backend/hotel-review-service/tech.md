---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/hotel-review-service/**"
---

# Tech Stack

- Java 25, Spring Boot 4.0.7, Spring Cloud 2025.1.1
- Spring WebFlux (WebClient for downstream calls)
- Redis (Spring Data Redis, caching)
- Spring Security
- JUnit 5, Mockito, Spring Boot Test

## Build

```bash
./mvnw clean install -pl backend/discover-search/services/hotel-review-service -am
```
