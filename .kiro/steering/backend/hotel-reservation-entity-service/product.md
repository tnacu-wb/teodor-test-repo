---
inclusion: fileMatch
fileMatchPattern: "backend/manage-modify/services/hotel-reservation-entity-service/**"
---

# Product Overview

Hotel Reservation Entity Service is a microservice within the Whitbread digital backend platform. It acts as the central reservation management layer, orchestrating create, retrieve, amend, and cancel operations for hotel reservations. It aggregates data from multiple downstream services (OHIP, Basket, Content, Rules, CDH) and exposes a unified REST API consumed by Premier Inn web, mobile, CCUI (contact centre), and distribution channels.

## Core Responsibilities

- Create hotel reservations by coordinating with OHIP Adapter for Opera PMS interactions
- Amend reservations (stay dates, room types, rate codes, guests, packages, distribution changes)
- Cancel reservations with policy evaluation and refund orchestration
- Manage Booking operations (retrieve, search, enrichment with content/hotel data)
- Online Check-In (CIOL) and Check-Out (COOL) workflows with eligibility rules
- Digital Key eligibility evaluation and QR code generation
- CDH-based booking search for reservation lookup by customer profile
- Change log retrieval for reservation modification history
- Redis caching for reservation and availability data
- Feature flag evaluation via Unleash for progressive rollouts
- Promotion and package management (charitable donations, meal deals, ancillaries)
- Universal Login API key validation for cross-channel authentication

## Key Integrations

- **OHIP Adapter Service** — Opera OHIP PMS gateway for all reservation CRUD operations, availability checks, rate plans, and package scheduling
- **Basket Service** — basket lifecycle management (create, add items, cancel, refund, payment initiation, allowances, deposit folios)
- **Content Entity Service** — hotel content, header data, business notes, payment info, rate information, and search rules
- **Rules Agent Entity Service** — amendment rules, max rooms/nights, room occupancy, source channel mapping, VAT codes, allowances, occupancy supplements
- **CDH Adapter Service** — customer data hub for reservation search and employee lookup
- **Hotel Entity Service** — hotel availability data (v2 distribution endpoint)
- **Hotel Account Service Opera** — customer profile updates
- **Promotion Service** — promotional package and promo-kind lookups
- **Redis Cluster** — distributed caching with Lettuce client and cluster topology refresh
- **Unleash** — feature flag management for progressive rollouts
- **Auth0** — multi-tenant JWT validation (CCUI, PI BB, Distribution)

## Domain Context

This service is part of the Manage & Modify squad's responsibility. It sits at the centre of the reservation lifecycle — from initial booking creation through amendment and cancellation. It serves as the entity layer that translates high-level business operations into coordinated calls across the Opera PMS (via OHIP), basket/payment systems, content services, and business rule engines.
