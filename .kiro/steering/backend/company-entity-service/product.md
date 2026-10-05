---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/company-entity-service/**"
---

# Product Overview

Manages company entity data with integrations to OHIP (Oracle Hospitality Integration Platform) and CDH (Customer Data Hub) adapters. Provides CRUD and lookup operations for company records used by business booker flows.

## Core Responsibilities

- Expose REST endpoints for company entity CRUD operations
- Integrate with OHIP adapter for hospitality-side company data
- Integrate with CDH adapter for customer data hub company profiles
- Generate API models via OpenAPI code generation
- Use WebFlux WebClient for non-blocking downstream calls

## Consumer Services / Integration Points

- Called by frontend business booker registration/management flows
- Depends on OHIP adapter and CDH adapter services
- Verified via Spring Cloud Contract tests
- Port: 9118
