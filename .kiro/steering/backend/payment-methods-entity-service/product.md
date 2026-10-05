---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/payment-methods-entity-service/**"
---

# Product Overview

The Payment Methods Entity Service determines which payment methods are available to a customer during the booking journey. It aggregates data from multiple sources — reservations, hotel information, customer accounts, rules engine, and feature flags — to build a contextual list of payment options for a given basket.

## Core Responsibilities

- Calculate available payment methods (card, PayPal, pay-on-arrival, PIBA) based on booking context
- Apply business rules from the Rules Agent Service to determine PayPal eligibility
- Look up hotel payment information (accepted cards, currencies, hub hotel status)
- Retrieve customer saved cards and company card details
- Fetch basket and reservation context to inform payment option availability
- Support CCUI-specific payment method flows (contact centre)
- Manage feature flags for kill-switches and gradual rollouts of payment features
- Cache responses via Redis for performance

## Key Integrations

- **Basket Service** — fetch basket context (booking details, hotel, guest info)
- **Reservation Service** — retrieve reservation details by basket reference
- **Hotel Entity Service** — hotel information (payment configuration, hub hotel status)
- **Content Service** — hotel payment information (accepted card types)
- **Hotel Account Service** — customer hotel account lookups
- **Hotel Card Service** — company saved cards
- **Rules Agent Service** — PayPal eligibility rules
- **3C Payment Service (token endpoint)** — PayPal client token generation
- **Redis** — response caching (Lettuce client, cluster mode)
- **Unleash** — feature flags (kill switches for payments, hub hotels POA, DE/UK 72h)
- **Auth0** — multi-tenant JWT validation (CCUI tenant, PI/BB tenant)

## Domain Context

Part of the Book & Pay squad. This service is called by the GraphQL subgraph layer and directly by CCUI during the payment step of the booking funnel. It does not process payments itself — it determines what payment options to present to the user before they proceed to the 3C Payment Service.
