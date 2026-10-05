---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/availability-cache-service-opera/**"
---

# Product Overview

Caches hotel room availability data sourced from Opera PMS. Provides a fast read layer for availability queries using PostgreSQL for persistent storage and Redis for hot caching. Integrates with content and rules-agent services via Feign clients.

## Core Responsibilities

- Cache and serve hotel room availability from Opera PMS
- Maintain PostgreSQL persistence layer for availability data
- Provide Redis-based hot cache for low-latency reads
- Integrate with content-service and rules-agent-service via Feign clients
- Support feature flag toggling via Unleash

## Consumer Services / Integration Points

- Called by search/discovery frontend flows for availability checks
- Depends on content-service (hotel metadata) and rules-agent-service (pricing rules)
- Port: 9060
