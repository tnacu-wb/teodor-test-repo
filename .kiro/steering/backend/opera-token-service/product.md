---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/opera-token-service/**"
---

# Product Overview

The Opera Token Service is a microservice responsible for generating and managing OAuth2 tokens for the Oracle OHIP (Opera Hospitality Integration Platform) API. It acts as a centralised token broker so that downstream services can authenticate against Opera without managing credentials or token lifecycle directly.

## Core Responsibilities

- Obtain OAuth2 client-credentials tokens from the Opera identity provider
- Provide a REST endpoint for internal services to fetch valid Opera tokens
- Handle token refresh ahead of expiry (configurable refresh-ahead window)
- Validate provider identifiers on incoming requests
- Abstract Opera authentication complexity from consuming services

## Key Integrations

- **Opera OHIP Identity Provider** — OAuth2 client-credentials grant for token acquisition
- **Consuming services** — OHIP Adapter Service, Hotel Entity Service, and other Opera-integrated services call this token service to obtain bearer tokens before calling OHIP APIs
- **Auth0 (common-auth0)** — shared authentication library for service-to-service auth

## Domain Context

Part of the Discover & Search squad. This service is a shared infrastructure utility consumed by any service that needs to call Opera/OHIP APIs. It minimises the number of token requests to Opera by centralising credential management and providing pre-refreshed tokens to callers.
