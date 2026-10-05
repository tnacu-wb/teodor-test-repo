---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/hotel-card-service-opera/**"
---

# Product Overview

The Hotel Card Service Opera is a microservice within the Whitbread digital backend platform. It manages payment card operations for both company (Business Booker) and customer (Premier Inn) accounts. The service provides full CRUD functionality for payment cards, integrating with multiple payment providers including Worldline and 3C Payment, and supports both centralised (BB Central) and personal card types across GB and DE markets.

## Core Responsibilities

- Manage customer payment card lifecycle (create, read, update, delete)
- Manage company payment card lifecycle (create, read, update, delete)
- Manage Inn Business payment cards for company employees
- Integrate with Worldline REST API for card holder and account card operations
- Integrate with 3C Payment service for payment tokenisation
- Integrate with CDH (Customer Data Hub) for authorization and company card management
- Integrate with PIBA account service for account lookups
- Integrate with Hotel Account service for account details
- Provide card masking, tokenisation, and validation utilities
- Support feature toggles (Unleash) for progressive rollout of card behaviours
- Implement circuit breaker patterns (Resilience4j) for fault tolerance on downstream calls
- Cache customer card data in Redis for performance

## Key API Domains

| Controller Domain | Purpose |
|-------------------|---------|
| Customer Cards | CRUD operations for customer (PI personal) payment cards |
| Company Cards | CRUD operations for company (BB central) payment cards |
| Hotel Cards | General hotel-level card operations |
| Inn Business Cards | Card operations for Inn Business employee accounts |

## Key Integrations

- **Worldline REST API** — Card holder management, account card CRUD (GB and DE markets)
- **3C Payment Service** — Payment card tokenisation and SCA authorisation
- **CDH (Customer Data Hub)** — Company card management and employee authorisation via Azure API Management
- **PIBA Account Service** — Account lookup and validation
- **Hotel Account Service** — Account details retrieval via OpenFeign
- **Redis Cluster** — Customer card data caching (Lettuce client, SSL enabled)
- **Unleash** — Feature flag management (CDH delay, company name validation, CDH API deprecation)
- **Auth0** — JWT token validation for PI BB tenant authentication

## Domain Context

This service sits at the intersection of payment card management and company profile management. It uses a Strategy pattern to handle different card types (BB Central, BB Personal, PI Personal) and communicates with multiple downstream services through Feign clients protected by Resilience4j circuit breakers. The Worldline integration supports multi-market operations (GB and DE) with market-specific credentials and culture codes.
