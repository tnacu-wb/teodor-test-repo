---
inclusion: fileMatch
fileMatchPattern: "backend/identity/libs/commons-validators/**"
---

# Tech Stack

- Java 25, Spring Boot 4.0.7, Spring Cloud 2025.1.1
- Jakarta Bean Validation API
- Passay (password policy engine)
- JUnit 5, Mockito for testing

## Build

```bash
./mvnw clean install -pl backend/identity/libs/commons-validators -am
```
