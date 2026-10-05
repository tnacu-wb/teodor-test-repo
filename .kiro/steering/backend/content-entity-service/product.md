---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/content-entity-service/**"
---

# Product Overview

Content Entity Service is a microservice within the Whitbread digital backend platform. It provides content sourced from Adobe Experience Manager (AEM) to the frontend applications — serving hotel information, booking flow content, page layouts, labels, SEO metadata, and configuration data for both leisure (Premier Inn) and business (Inn Business) channels.

## Core Responsibilities

- Fetch and cache content from AEM (Adobe Experience Manager)
- Serve hotel information including room types, meals, facilities, and opening status
- Provide localised labels and dictionary content for UI rendering
- Deliver page-level data: headers, footers, SEO, cookie policies, search results
- Serve global configuration per brand/site (leisure, business-booker, CCUI, distribution)
- Support DLP (dynamic landing pages) and apps homepage content
- Schedule periodic refresh of hotel search filters and opening-soon data

## Key Endpoints / Domains

| Domain | Description |
|--------|-------------|
| Hotel | Hotel details, search filters, opening-soon, upsell items |
| Booking | Booking flow labels and content |
| Room Type | Room type descriptions per brand |
| Meals | Meal/extras content |
| Header / Footer | Site navigation chrome |
| Labels | Localised i18n dictionary strings |
| SEO | SEO metadata for pages |
| Countries | Country list per site |
| Cookie Policies | Cookie consent content per brand |
| Global Config | Brand/site-level feature configuration |
| DLP | Dynamic landing page content |
| Apps | Mobile apps homepage content |
| Inn Business | Card management, user management, spending reports, home, pay application, auth, notifications, contact us |
| Business Notes | Allowances/business notes dictionary |
| Price Finder | Price finder content |
| Promo Config | Promotions configuration |
| Search Results | Search results page data |

## Key Integrations

- **AEM (Adobe Experience Manager)** — primary content source; REST calls to fetch pages, dictionaries, and hotel data
- **Snowdrop** — hotel search API for search filters
- **OHIP Adapter Service** — hotel info and rate plans from Opera Hospitality
- **Hotel Review Service** — TripAdvisor review data for hotels
- **Redis** — cluster-based caching layer for all content responses
- **Unleash** — feature flag management for toggling content features

## Domain Context

This service acts as the content aggregation layer between the CMS (AEM) and client applications. It normalises AEM's content structure into API-friendly responses, adds caching for performance, and supports multiple brands/sites (Premier Inn leisure, Inn Business, CCUI, distribution) with locale-aware content delivery.

