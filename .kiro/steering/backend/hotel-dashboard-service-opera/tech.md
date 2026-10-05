---
inclusion: fileMatch
fileMatchPattern: "backend/manage-modify/services/hotel-dashboard-service-opera/**"
---

# Tech Stack

- Java 25, Spring Boot 4.0.7, Spring Cloud 2025.1.1
- Spring WebFlux (WebClient for downstream calls)
- OpenAPI Generator (model/API code generation)
- JUnit 5, Mockito, Spring Boot Test

## Build

```bash
./mvnw clean install -pl backend/manage-modify/services/hotel-dashboard-service-opera -am
```
