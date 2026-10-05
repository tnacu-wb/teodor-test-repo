---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/basket-service/**"
---

# Product Overview

Basket Service is the core orchestration microservice for the Premier Inn hotel booking journey. It manages the lifecycle of a booking basket — from creation through payment to confirmation — coordinating multiple downstream services.

## Core Responsibilities

- Create, retrieve, update, and cancel booking baskets (hotel room reservations)
- Orchestrate payment flows (pay-now, pay-on-arrival, PayPal, CCUI/Eckoh)
- Manage basket items: room selections, packages, allowances, occupancy updates
- Coordinate with OHIP adapter for reservation creation and deposit folios
- Handle booking confirmation and email notifications via Kafka events
- Support amend/cancel flows for existing reservations
- Integrate with CDH adapter for company profile lookups
- Feature flag management via Unleash for gradual rollouts

## Key Integrations

- **OHIP Adapter Service** — hotel reservation system (create/modify/cancel reservations, deposit folios)
- **Payments Service (3C)** — card payment initiation, webhooks, refunds
- **CDH Adapter Service** — company profile and contact data
- **Content Service** — hotel information and content
- **Marketing Service** — marketing preferences
- **Rules Agent Service** — business rules and promotions
- **Kafka** — async event publishing (confirmation, refund processing)
- **DynamoDB** — basket state persistence
- **Redis** — caching layer
- **Unleash** — feature flag management

## Domain Context

Part of the Book & Pay squad. The service is consumed by Premier Inn web (premierinn.com, ccui.premierinn.com, premierinnbusiness.com) and mobile apps during the booking funnel. It exposes REST APIs consumed by the GraphQL subgraph layer and directly by CCUI.
