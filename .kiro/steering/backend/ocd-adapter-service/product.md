---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/ocd-adapter-service/**"
---

# Product Overview

OCD Adapter Service is a microservice that acts as a translation layer between Whitbread's internal hotel distribution platform and the Opera Cloud Distribution (OCD) external channel manager API. It adapts OCD's distribution shop API into Whitbread's internal domain model.

## Core Responsibilities

- Translate OCD distribution shop API responses into Whitbread hotel availability and rate models
- Provide hotel offer details (rates, room types, packages, policies) from OCD
- Handle tax information retrieval and calculation for hotel stays
- Manage OAuth2 client credentials for OCD API authentication
- Cache frequently accessed data via Redis to reduce OCD API load
- Generate PDF documents for booking confirmations and invoices

## Key Integrations

- **OCD (Opera Cloud Distribution)** — external channel manager API for hotel inventory and rates
- **Tax Service** — tax calculation and rate information
- **Redis** — caching layer for OCD responses
- **OAuth2** — client credentials flow for OCD API authentication
- **Common Auth0** — internal JWT validation for inbound requests

## Domain Context

Part of the Discover & Search squad. The service is consumed by other backend services (hotel-entity-service, availability-cache-service) that need hotel availability and rate information from OCD. It abstracts the complexity of the OCD API and provides a clean internal interface.
