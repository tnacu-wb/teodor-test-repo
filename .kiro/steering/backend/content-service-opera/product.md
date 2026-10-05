---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/content-service-opera/**"
---

# Product Overview

The Content Service (Opera) is a microservice within the Whitbread digital backend platform. It acts as a content aggregation layer that fetches content from Adobe Experience Manager (AEM) and returns normalised JSON responses for room types, rate classifications, cookie policies, and booking notifications across multiple brands and locales.

## Core Responsibilities

- Fetch and serve room type content (descriptions, images, facilities) from AEM by country, language, and brand
- Retrieve rate classification details (rate names, descriptions, ordering) for specific hotels
- Serve cookie policy content filtered by language, brand, and sub-brand
- Deliver booking notification messages from AEM content fragments
- Cache all AEM responses in Redis cluster with configurable TTL (default 24 hours) to reduce upstream load
- Apply circuit breaker patterns (Resilience4j) to AEM Feign client calls with fallback factories
- Provide OpenAPI documentation via SpringDoc Swagger UI

## Key Integrations

- **Adobe Experience Manager (AEM)** — Upstream content source for room types, rates, cookie policies, and booking notifications (accessed via Feign clients with basic authentication)
- **Redis Cluster** — Distributed caching layer (Lettuce client, SSL-enabled) for all content responses
- **Spring Cloud Config** — Externalised configuration management
- **Resilience4j** — Circuit breaker for AEM client resilience with `FeignNonServerException` ignored
- **Micrometer + Brave** — Distributed tracing with W3C propagation

## Domain Context

This service sits in the Discover & Search squad's domain. It provides content data that downstream services (e.g., search, availability, booking flows) consume to display room information, rate descriptions, and policy content to end users. Content is sourced from AEM content fragments and cached aggressively since it changes infrequently. The service supports multiple brands (Premier Inn = "pi" default) and locales.
