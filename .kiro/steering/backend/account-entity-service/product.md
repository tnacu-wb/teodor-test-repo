---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/account-entity-service/**"
---

# Product Overview

Account Entity Service is a microservice within the Whitbread digital backend platform. It acts as a proxy service exposing account creation and management endpoints from `hotel-register-microservice` to the GraphQL experience layer. It handles customer account registration and marketing newsletter preference management.

## Core Responsibilities

- Expose REST APIs for customer account registration (`POST /v1/account`)
- Proxy account creation requests to the hotel-register-microservice (customers endpoint)
- Manage marketing newsletter preferences (`PUT /v1/account/preferences`)
- Validate incoming registration requests (postcode, phone, company name)
- Support feature flag–driven validation via Unleash (e.g., company name validation)
- Map between external DTOs and internal domain models using MapStruct

## Key Integrations

- **Hotel Register Microservice** — WebClient-based HTTP calls for customer registration (`/customers/hotels`)
- **Marketing Service** — REST calls to manage newsletter email preferences (Azure Front Door–protected)
- **Unleash** — Feature flag evaluation for conditional validation logic
- **Spring Cloud Config / Bootstrap** — Externalised configuration via config server

## Domain Context

The service sits in the Company / Profile Management Squad domain. It provides a simplified entity-style interface for account operations, bridging the experience layer (GraphQL) with the underlying hotel-register and marketing services. Registration includes customer details, addresses, contacts, passport info, and source channel metadata. Marketing preferences handle newsletter opt-in/opt-out flows.

## API Endpoints

### V1 (`/v1/account`)
- `POST /` — Register a new customer account (optionally updates marketing preferences in the same call)
- `PUT /preferences` — Update marketing newsletter preferences for a customer
