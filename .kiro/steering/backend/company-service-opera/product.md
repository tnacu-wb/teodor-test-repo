---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/company-service-opera/**"
---

# Product Overview

Company Service Opera is a microservice within the Whitbread digital backend platform. It exposes REST APIs for managing corporate/company accounts and their employees in the Premier Inn Business Booker (PI BB) ecosystem. It handles company profile CRUD, employee management, booking allowances/alerts configuration, and management information questions — all backed by the CDH (Customer Data Hub) data layer.

## Core Responsibilities

- List, search, and retrieve company profiles
- Update company details (address, contacts, status)
- Manage company booking allowances and booking alerts
- CRUD operations for management information questions (user-defined and business questions)
- Retrieve and update employee records with access level filtering
- Validate company names via feature-flagged rules (Unleash)
- Send emails via Azure Email Service (e.g., registration notifications)
- Cache company data in Redis cluster for performance
- Authenticate and authorize requests via Auth0 multi-tenant JWT

## Key Integrations

- **Hotel Account Service** — OpenFeign client for hotel account resolution (fallback-enabled)
- **CDH (Customer Data Hub)** — OAuth-secured API for company and employee data (via `commons-cdh-lib`)
- **Auth0** — Multi-tenant JWT authorization with management API access for user operations
- **Azure Email Service** — Transactional email sending for company/employee notifications
- **Redis** — Clustered cache (Lettuce client) for company profile data with 24-hour TTL
- **Unleash** — Feature flag evaluation for company name validation and CDH API deprecation toggles

## Domain Context

This service manages the "Company" entity in the PI BB (Premier Inn Business Booker) platform. Companies are corporate accounts that can have multiple employees. Each company has booking allowances (rate plans, upsell items, price caps), booking alerts (email notifications at configurable frequency), and management information questions that employees must answer during the booking flow. The service communicates with CDH as its primary data source and uses the hotel-account service for hotel-level lookups.

## API Endpoints

### Companies (`/companies/`)
- `GET /companies` — List all companies
- `POST /companies/search` — Search for companies by criteria
- `POST /companies/check` — Check if a company exists

### Company (`/company/`)
- `GET /company/{companyId}` — Retrieve company details
- `PUT /company/{companyId}` — Update company details
- `PUT /company/{companyId}/bookingallowances` — Update booking allowances
- `PUT /company/{companyId}/bookingalerts` — Update booking alerts
- `GET /company/{companyId}/employees` — Retrieve all employees for a company
- `PUT /company/{companyId}/employee/{employeeId}` — Update an employee

### Company Questions (`/company/{companyId}/questions/`)
- `GET /company/{companyId}/questions/user` — Retrieve all user-defined questions
- `POST /company/{companyId}/questions/user` — Create a user question
- `DELETE /company/{companyId}/questions/user/{questionId}` — Remove a user question
- `PUT /company/{companyId}/questions/user/{questionId}` — Update a user question
- `GET /company/{companyId}/questions/business` — Retrieve all business questions
- `PUT /company/{companyId}/questions/business/{questionId}` — Update a business question
