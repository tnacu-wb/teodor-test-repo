---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/company-entity-service/**"
---

# Tech Stack

- Java 25, Spring Boot 4.0.7, Spring Cloud 2025.1.1
- Spring WebFlux (WebClient for downstream calls)
- OpenAPI Generator (model/API code generation)
- Spring Cloud Contract (contract testing)
- JUnit 5, Mockito, Spring Boot Test

## Build

```bash
./mvnw clean install -pl backend/identity/services/company-entity-service -am
```
