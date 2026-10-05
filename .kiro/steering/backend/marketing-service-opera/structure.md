---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/marketing-service-opera/**"
---

# Project Structure

Standard Spring Boot service layout. Controller layer for REST endpoints, service layer orchestrating BART calls via bart-shared-lib. Feign client interfaces, Resilience4j configuration, Redis cache config, and Auth0 security configuration.
