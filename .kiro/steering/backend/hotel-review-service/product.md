---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/hotel-review-service/**"
---

# Product Overview

Aggregates hotel review data for Premier Inn properties. Uses Redis caching for performance and WebClient for non-blocking downstream calls. Secured via Spring Security.

## Core Responsibilities

- Serve aggregated hotel review data via REST endpoints
- Cache review data in Redis for low-latency reads
- Fetch review data from downstream sources using WebClient
- Secure endpoints via Spring Security

## Consumer Services / Integration Points

- Called by frontend/mobile for hotel detail pages
- Uses Redis for caching review aggregations
- Port: 9126
