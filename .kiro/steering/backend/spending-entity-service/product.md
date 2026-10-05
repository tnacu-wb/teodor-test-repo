---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/spending-entity-service/**"
---

# Product Overview

Spending Entity Service is a microservice within the Whitbread digital backend platform. It provides InnBusiness spending and reporting functionality, allowing business customers to view company spending, account spending, employee spend reports, upcoming spending, and payment information.

## Core Responsibilities

- Expose REST APIs for spending data retrieval and reporting (`/v1/spending/`)
- Retrieve company-level and account-level spending from Worldline
- Fetch employee spend reports via CDH Adapter Service
- Provide upcoming spending information for accounts
- Retrieve payment info from Worldline
- Generate CSV reports for account spending downloads
- Cache responses using Redis cluster

## Key Integrations

- **Worldline** — REST/Feign client for account info, payment data, and spending transactions
- **PIBA Account Service** — Feign client to resolve customer accounts and tethered user details
- **CDH (Customer Data Hub)** — REST client for tethered GUID resolution and company spending data
- **CDH Adapter Service** — Feign client for employee spend reports
- **Auth0** — JWT-based authorization with `@PreAuthorize` security
- **Redis** — Clustered cache for response caching

## Domain Context

This service is part of the InnBusiness B2B platform, providing spending visibility for corporate PIBA account holders. It aggregates data from multiple sources (Worldline, CDH, PIBA Account Service) to present unified spending views at company, account, and employee levels.

## API Endpoints (`/v1/spending/`)

- `GET /companySpending` — Retrieve company spending (requires SUPER access level)
- `GET /accountSpending` — Retrieve account spending (supports JSON and CSV download via Accept header)
- `GET /employeeSpend` — Retrieve employee spend reports
- `GET /upcomingSpending` — Retrieve upcoming spending for an account
- `GET /paymentInfo` — Retrieve payment information for an account
