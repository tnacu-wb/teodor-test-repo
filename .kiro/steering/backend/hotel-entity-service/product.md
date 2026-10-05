---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/hotel-entity-service/**"
---

# Product Overview

Hotel Entity Service is a microservice within the Whitbread digital backend platform. It provides hotel availability search, room/rate information, and pricing across multiple channels (PI, BB, CCUI, distribution). The service aggregates data from Opera (via OHIP Adapter), content services, rules engine, and caching layers to deliver a unified hotel search and availability API.

## Core Responsibilities

- Search hotel availability by criteria (dates, location, guests, rate plans)
- Return single-hotel and multi-hotel availability results with pricing
- Apply business rules (room substitution, max nights/rooms, rate suppression, occupancy supplements)
- Provide hotel information, room types, packages, and extras
- Calculate distance-from-search for hotel results
- Support group booking enquiries (via Dynamics 365)
- Serve list-of-values lookups (cancellation reasons, rate plans, preferences)
- Cache availability results in Redis for performance
- Support feature-flagged behaviour via Unleash

## Key API Endpoints (Controllers)

- **Availability** — hotel availability search (single and multi-hotel), distribution availability
- **Distance** — distance-from-search calculations for SRP results
- **Group Booking** — group booking enquiry submission
- **Hotel** — hotel information and room types
- **LOV (List of Values)** — cancellation reasons, rate plans, preferences
- **Packages** — meal packages, extras, donations
- **SRP** — search results page aggregation
- **Validation** — input validation endpoints

## Key Integrations

- **OHIP Adapter Service** — Opera availability, inventory, room types, rate plans, restrictions
- **Availability Cache Service** — cached availability lookups for faster responses
- **Content Entity Service** — hotel content, facilities, search rules, labels, global config
- **Rules Agent Entity Service** — room substitution, max nights/rooms, occupancy supplements, rate suppression
- **Basket Service** — basket reference lookups for pricing context
- **Promotion Service** — promotional rate/promo-kind lookups
- **Dynamics 365** — group booking ticket creation
- **Snowdrop** — hotel search/geolocation
- **Redis** — availability and on-sale flag caching
- **Unleash** — feature flag evaluation

## Domain Context

This service sits at the heart of the search and booking funnel. It is consumed by the SRP (Search Results Page) and HDP (Hotel Details Page) on both Premier Inn (PI) and hub by Premier Inn (BB) websites, as well as by the Contact Centre UI (CCUI) and distribution/affiliate channels.

