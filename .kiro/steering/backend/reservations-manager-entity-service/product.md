---
inclusion: fileMatch
fileMatchPattern: "backend/manage-modify/services/reservations-manager-entity-service/**"
---

# Product Overview

Reservations Manager Entity Service is a microservice within the Whitbread digital backend platform. It provides hotel reservation management capabilities including viewing, amending, and cancelling bookings, online check-in, and invoice generation. It orchestrates interactions between multiple downstream services (OHIP, Basket, CDH Adapter, Content) and exposes a unified REST API consumed by Premier Inn web, mobile, and contact centre channels.

## Core Responsibilities

- Retrieve hotel reservation details by confirmation number or customer profile
- Amend reservations (dates, room types, guest details, packages)
- Cancel reservations with policy evaluation
- Online check-in workflow and eligibility assessment
- Invoice/confirmation PDF generation (Thymeleaf templates + OpenHTMLToPDF)
- Reservation search via CDH customer profile lookup
- Content enrichment from hotel and brand content services
- Payment information retrieval for reservation folios
- Feature flag evaluation via Unleash for progressive rollouts
- Redis caching for reservation and availability data
- AWS S3 storage for generated invoice PDFs

## Key Integrations

- **OHIP Adapter Service** — Opera OHIP PMS gateway for reservation CRUD, via WebClient
- **Basket Service** — basket lifecycle management (items, payments, allowances), via OpenAPI-generated models
- **CDH Adapter Service** — customer data hub for reservation search and profile lookup, via OpenAPI-generated models and Feign client
- **Content Entity Service** — hotel content and brand information
- **Hotel Info Service** — hotel details and configuration
- **AWS S3** — storage for generated invoice/confirmation PDFs
- **Redis Cluster** — distributed caching with Lettuce client
- **Unleash** — feature flag management for progressive rollouts
- **Auth0** — JWT validation via `common-auth0` library

## Domain Context

This service is part of the Manage & Modify squad's responsibility. It provides the post-booking management layer — allowing guests to view their upcoming reservations, make changes, check in online, and download confirmation documents. It sits alongside hotel-reservation-entity-service (which handles the initial booking creation) and focuses on the guest self-service management journey.
