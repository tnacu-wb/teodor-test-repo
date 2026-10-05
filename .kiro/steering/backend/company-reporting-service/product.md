---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/company-reporting-service/**"
---

# Product Overview

Provides reporting capabilities for business booker accounts. Persists report data in DynamoDB and stores generated report files in AWS S3. Uses Auth0 for security and WebFlux WebClient for async downstream calls.

## Core Responsibilities

- Generate and serve business booker reports
- Persist report metadata in DynamoDB
- Store/retrieve report files from AWS S3
- Secure endpoints via Auth0 token validation
- Make async downstream calls via WebFlux WebClient

## Consumer Services / Integration Points

- Called by business booker frontend for report access
- Integrates with AWS DynamoDB (persistence) and S3 (file storage)
- Secured by Auth0
- Port: 9125
