---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/rules-manager-entity-service/**"
---

# Project Structure

Standard Spring Boot service layout. Controller layer, service layer, JPA repository layer. Liquibase changelog files under `src/main/resources/db/changelog`. MapStruct mapper interfaces generate implementation at compile time.
