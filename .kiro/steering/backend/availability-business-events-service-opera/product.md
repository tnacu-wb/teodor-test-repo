---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/availability-business-events-service-opera/**"
---

# Product Overview

The Availability Business Events Service (Opera) is a microservice within the Whitbread digital backend platform. It subscribes to Oracle Opera's OHIP Business Events via a GraphQL WebSocket connection and processes availability-related events in real time, persisting rate and inventory changes to a PostgreSQL availability cache database.

## Core Responsibilities

- Subscribe to Opera OHIP Business Events stream via WebSocket (GraphQL subscriptions)
- Process availability business events: Summary Totals, Apply Daily Rates, Rate Restrictions
- Persist rate and availability data changes to the PostgreSQL availability cache database
- Manage OAuth2 authentication with Opera OHIP APIs
- Track processed event offsets to support resumption after disconnection (cold start recovery)
- Apply city tax calculations by integrating with the OCD Adapter Service (controlled via feature flags)
- Handle WebSocket reconnection on authentication errors or connection failures

## Key Business Event Types

| Event | Purpose |
|-------|---------|
| Summary Total | Updates aggregate availability totals per hotel/room/rate combination |
| Apply Daily Rates | Updates daily rate amounts for hotel rooms and rate plans |
| Rate Restrictions | Updates booking restrictions (min stay, closed-to-arrival, etc.) |

## Key Integrations

- **Oracle OHIP GraphQL Subscriptions** — Real-time business event stream (WebSocket)
- **Oracle OHIP OAuth2** — Authentication for subscription handshake
- **Content Entity Service** — Retrieves global config and city tax hotel lists
- **OCD Adapter Service** — Calculates amounts after tax for rate plans
- **PostgreSQL** — Availability cache database (hotels, rooms, rates, processed events)
- **Unleash** — Feature flag management (city tax toggles, token refresh behaviour)

## Domain Context

This service acts as an event consumer in the availability data pipeline. Opera publishes business events whenever rates, restrictions, or inventory change in the PMS. This service subscribes to those events and updates the local availability cache, which downstream search and availability services query for fast lookups. The chain code `WHBOC001` identifies the Whitbread Opera Cloud environment.
