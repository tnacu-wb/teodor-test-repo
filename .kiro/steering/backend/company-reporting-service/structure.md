---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/company-reporting-service/**"
---

# Project Structure

Standard Spring Boot service layout. Controller layer for REST endpoints, service layer with WebClient for async calls. DynamoDB repository classes for persistence, S3 client integration for file storage. Auth0 security configuration.
