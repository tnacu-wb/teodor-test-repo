---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/rules-manager-entity-service/**"
---

# Tech Stack

- Java 25, Spring Boot 4.0.7, Spring Cloud 2025.1.1
- Spring Data JPA (PostgreSQL)
- Liquibase (database migrations)
- MapStruct (entity-DTO mapping)
- JUnit 5, Mockito, Spring Boot Test

## Build

```bash
./mvnw clean install -pl backend/discover-search/services/rules-manager-entity-service -am
```
