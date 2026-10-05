---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/hotel-register-service-opera/**"
---

# Product Overview

Hotel Register Service Opera is a microservice within the Whitbread digital backend platform. It exposes REST APIs for customer registration across multiple channels — web (Premier Inn leisure), apps, InnBusiness (B2B), and Auth0 Universal Login. It handles account creation in CDH (Customer Data Hub), Auth0 user provisioning, reCAPTCHA verification, email encryption, password policy enforcement, and confirmation email sending.

## Core Responsibilities

- Register new leisure customers (web and apps channels) with captcha verification
- Register InnBusiness (B2B) customers via a two-step registration flow
- Register accounts via Auth0 Universal Login with API key protection
- Create user accounts in Auth0 (leisure, business, and InnBusiness connections)
- Create customer profiles in CDH (Customer Data Hub)
- Enforce configurable password policies (legacy and Auth0-compliant)
- Verify Google reCAPTCHA tokens before registration
- Encrypt/cypher email addresses (Base64 and plaintext endpoints)
- Send confirmation emails via Azure Email Service
- Validate business customer registrations
- Look up countries via hotel-countries service
- Interact with hotel-reservation-entity-service for reservation data
- Interact with marketing-service-opera for marketing preferences
- Cache data in Redis cluster for performance

## Key Integrations

- **Auth0** — User provisioning via Management API (B2C, B2B, and InnBusiness connections) with JWT encryption
- **CDH (Customer Data Hub)** — OAuth-secured API for customer account creation (via `commons-cdh-lib`)
- **Azure Email Service** — Transactional email sending for registration confirmations (PTI and generic channels)
- **Google reCAPTCHA** — Bot protection for web and app registration flows
- **Redis** — Clustered cache (Lettuce client) for performance
- **Hotel Countries Service** — OpenFeign client for country lookup
- **Hotel Reservation Entity Service** — OpenFeign client for reservation data
- **Marketing Service Opera** — OpenFeign client for marketing preferences
- **Unleash** — Feature flag evaluation for company name validation and CDH API deprecation toggles

## Domain Context

This service manages the "Customer Registration" flow for Premier Inn. It supports multiple registration channels: web (leisure customers with captcha), mobile apps, InnBusiness (corporate B2B with two-step flow), and Auth0 Universal Login (API-key-protected). Each registration involves creating a user in Auth0, creating a customer profile in CDH, enforcing password policies, and optionally sending confirmation emails. The service also provides email encryption endpoints used by other services for secure email handling.

## API Endpoints

### Hotel Register (`/customers/hotels`)
- `POST /customers/hotels` — Create a new customer (leisure or business, with captcha)

### Apps Register (`/v1/hotel-register/accounts/register`)
- `POST /v1/hotel-register/accounts/register` — Register an account via mobile app (with captcha)

### InnBusiness Register (`/v1/hotel-register/innbusiness/registration/`)
- `POST /v1/hotel-register/innbusiness/registration/step-one` — InnBusiness registration step one
- `POST /v1/hotel-register/innbusiness/registration/step-two` — InnBusiness registration step two (with password)

### Universal Login (`/v1/hotel-register/universal-login/leisure/`)
- `POST /v1/hotel-register/universal-login/leisure/accounts/{emailId}/confirm` — Send confirmation email (API key protected)
- `POST /v1/hotel-register/universal-login/leisure/accounts` — Register account via universal login (API key protected)

### Cypher
- `POST /va3PkOkJNTUf76oe3UDoIqrA` — Encrypt Base64-encoded email
- `POST /cypher` — Encrypt plaintext (deprecated)
