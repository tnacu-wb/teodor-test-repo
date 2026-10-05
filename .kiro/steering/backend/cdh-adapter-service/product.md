---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/cdh-adapter-service/**"
---

# Product Overview

The CDH Adapter Service is a microservice within the Whitbread digital backend platform. It encapsulates all integration logic for the Customer Data Hub (CDH) — Whitbread's centralised customer and company data platform hosted on Azure. The service translates Whitbread's internal data models to/from the CDH API data model and handles all communication with CDH endpoints via Azure API Management.

## Core Responsibilities

- Proxy and orchestrate calls to the CDH REST APIs for company and employee account management
- Translate Whitbread domain models to/from CDH API request/response structures
- Provide a unified internal API surface for upstream services to interact with CDH
- Handle OAuth2 token management for Azure AD authentication (client credentials flow)
- Manage feature toggles (Unleash) for gradual rollout of CDH-specific behaviours
- Support reservation search and invoice retrieval from CDH Booking Services
- Generate management information and emergency reports for companies
- Provide employee spend reporting via the Inn Business API

## Key API Domains

| Controller Domain | Purpose |
|-------------------|---------|
| Company | Retrieve company details, search companies, suppress rates |
| Employee | Retrieve and search employees within a company account |
| Reservation Search | Search reservations and retrieve reservation invoices from CDH |
| Reports | Management information and emergency reports for companies |
| Employee Spend | Employee spend reports via Inn Business API |

## Key Integrations

- **CDH Account Services API** — Company and employee account CRUD (Azure API Management)
- **CDH Booking Services API** — Reservation search and invoice retrieval
- **Inn Business API** — Employee spend reporting
- **Azure AD OAuth2** — Token management for CDH API authentication (client credentials)
- **Unleash** — Feature flag management for progressive rollout
- **OpenFeign** — Declarative HTTP client for OAuth token endpoint

## Domain Context

This service is designed as an adapter for the Customer Data Hub. It abstracts CDH-specific API details (subscription keys, Azure front-door headers, pagination) so that upstream services in the company profile management domain can interact with CDH without knowing its specifics.
