---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/feedback-service-opera/**"
---

# Tech Stack

- Java 25, Spring Boot 4.0.7, Spring Cloud 2025.1.1
- Spring Web MVC (REST)
- ADAL4J (Azure AD authentication for CRM access)
- Microsoft Dynamics CRM API
- JUnit 5, Mockito, Spring Boot Test

## Build

```bash
./mvnw clean install -pl backend/identity/services/feedback-service-opera -am
```
