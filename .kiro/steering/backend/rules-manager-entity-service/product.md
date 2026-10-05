---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/rules-manager-entity-service/**"
---

# Product Overview

Manages business rules used for availability and pricing decisions. Provides CRUD operations for rules stored in PostgreSQL with schema managed by Liquibase. Uses MapStruct for mapping between entities and DTOs.

## Core Responsibilities

- Expose REST endpoints for business rule CRUD operations
- Persist rules in PostgreSQL with Liquibase-managed schema
- Map between JPA entities and API DTOs using MapStruct
- Serve rules to availability-cache-service and other consumers

## Consumer Services / Integration Points

- Called by availability-cache-service-opera (rules evaluation)
- Called by internal admin tooling for rules management
- Port: 9105
