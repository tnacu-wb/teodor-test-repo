---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/table-reservation-service/**"
---

# Product Overview

Table Reservation Service is a microservice within the Whitbread digital backend platform. It handles restaurant table bookings for Whitbread's food & beverage brands (Beefeater, Brewers Fayre, Cookhouse & Pub, Bar + Block, and Table Table). The service acts as an intermediary between Premier Inn's digital channels and the LiveRes (Zonal) reservation platform.

## Core Responsibilities

- Create, retrieve, and cancel restaurant table reservations via the LiveRes/Zonal API
- Fetch restaurant configuration and availability from AEM (Adobe Experience Manager) content
- Validate booking parameters (party size, date/time, brand, restaurant)
- Map internal domain models to LiveRes API request/response formats
- Serve restaurant content (menus, opening hours, brand info) sourced from AEM
- Provide OpenAPI-documented REST endpoints for frontend/mobile consumption

## Key Integrations

- **LiveRes (Zonal) API** — external restaurant reservation platform for creating, retrieving, and cancelling table bookings via `RestClient`
- **AEM (Adobe Experience Manager)** — content management system providing restaurant configuration, menus, and brand-specific content via `RestClient`
- **Auth0** — JWT-based authentication and authorization via `common-auth0` library
- **Caffeine Cache** — local in-memory caching for restaurant content and configuration

## Domain Context

This service is part of the Book & Pay squad's responsibility. It supports the ancillary booking journey where Premier Inn guests can add restaurant reservations alongside their hotel stay. The service exposes endpoints consumed by the Premier Inn web and mobile apps during the booking funnel and post-booking management screens.
