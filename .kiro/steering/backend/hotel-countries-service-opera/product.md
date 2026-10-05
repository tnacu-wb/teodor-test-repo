---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/hotel-countries-service-opera/**"
---

# Product Overview

Provides hotel country data for downstream consumers. Caches country information in Redis for fast lookups and uses Feign clients for upstream data retrieval. Part of the identity squad's hotel reference data services.

## Core Responsibilities

- Serve hotel country reference data via REST endpoints
- Cache country data in Redis for low-latency reads
- Fetch upstream data using Feign clients
- Integrate with Spring Cloud commons for service discovery

## Consumer Services / Integration Points

- Called by other services needing hotel country data
- Uses Redis for caching
- Uses Feign clients for upstream service calls
- Port: 9031
