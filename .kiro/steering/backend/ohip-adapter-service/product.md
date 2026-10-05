---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/ohip-adapter-service/**"
---

# Product Overview

The OHIP Adapter Service is a microservice within the Whitbread digital backend platform. It encapsulates all integration logic specific to the Oracle Opera Hospitality Integration Platform (OHIP). The service translates Whitbread's internal data models to/from the Opera PMS data model and handles all communication with Opera's OHIP APIs.

## Core Responsibilities

- Translate Whitbread domain models onto Oracle Opera data models (and vice versa)
- Proxy and orchestrate calls to Opera OHIP REST APIs
- Provide a unified internal API surface for upstream services (e.g. Reservation Entity Service) to interact with Opera
- Handle OAuth2 token management for OHIP API authentication
- Manage feature toggles (Unleash) for gradual rollout of Opera-specific behaviours
- Cache frequently accessed data via Redis (e.g. hotel config, rate plans)

## Key API Domains

| Controller Domain | Purpose |
|-------------------|---------|
| Reservation | Create, retrieve, modify, cancel reservations in Opera |
| Availability | Query hotel room availability and inventory |
| Rate Plans | Retrieve rate plans and pricing |
| Check-in | Digital check-in and room allocation |
| Amend | Modify existing reservations (dates, guests, rooms) |
| Hotel Info | Retrieve hotel configuration and details |
| Packages | Query available packages and add-ons |
| Preferences | Guest preferences management |
| Profile | Guest profile CRUD operations in Opera |
| Deposit/Folios | Deposit and folio management for reservations |
| Eckoh | Payment card tokenisation integration (PCI compliance) |
| Change Log | Reservation change history |
| List of Values | Opera reference data lookups |
| Room Allocation | Room assignment operations |
| UDFs | User-defined fields management |

## Key Integrations

- **Oracle OHIP APIs** — Reservations, Rates, Cashiering, Inventory, CRM, Enterprise, Hotel Config, List of Values
- **Rules Agent Service** — Room substitution rules and business rule evaluation
- **Redis** — Caching layer for hotel config and rate data
- **Unleash** — Feature flag management for progressive rollout

## Domain Context

This service is designed as a PMS adapter — when Whitbread integrates a new Property Management System, a parallel adapter service would be created following the same contract. Upstream services call the OHIP Adapter without needing to know Opera-specific details.

