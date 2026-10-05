---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/marketing-service-opera/**"
---

# Product Overview

Manages newsletter subscription preferences for Premier Inn guests. Communicates with the legacy BART system via SOAP (using bart-shared-lib) to read and update subscription state. Employs resilience patterns and Redis caching for performance.

## Core Responsibilities

- Manage newsletter subscription opt-in/opt-out via REST endpoints
- Call BART SOAP services through bart-shared-lib for subscription state
- Cache subscription data in Redis
- Apply Resilience4j circuit breakers for downstream fault tolerance
- Secure endpoints via Auth0

## Consumer Services / Integration Points

- Called by frontend/mobile for newsletter preference management
- Depends on bart-shared-lib (SOAP client for BART)
- Uses Feign clients for inter-service communication
- Secured by Auth0
- Port: 9025
