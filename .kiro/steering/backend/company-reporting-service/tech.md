---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/company-reporting-service/**"
---

# Tech Stack

- Java 25, Spring Boot 4.0.7, Spring Cloud 2025.1.1
- Spring WebFlux (WebClient for async calls)
- AWS SDK v2 (DynamoDB, S3)
- Auth0 (Spring Security OAuth2 resource server)
- JUnit 5, Mockito, Spring Boot Test

## Build

```bash
./mvnw clean install -pl backend/identity/services/company-reporting-service -am
```
