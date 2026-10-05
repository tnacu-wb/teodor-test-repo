---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/business-tether-service-opera/**"
---

# Product Overview

Business Tether Service Opera is a microservice within the Whitbread digital backend platform. It exposes REST APIs for employee tethering operations in the Premier Inn Business Booker (PI BB) ecosystem. The service allows employees to link their loyalty card or account number to the Worldline platform, and provides tethered login functionality for authenticated sessions.

## Core Responsibilities

- Tether employees by account number (16-digit, prefix `3089` or `63562902`) or card number (19-digit)
- Perform tethered login via Worldline SOAP API (new session or refresh existing session)
- Validate Auth0 JWT tokens and extract employee/company details
- Generate session tokens (hashed shared secrets with nonces and timestamps)
- Persist tethered GUID associations via PIBA Guid service (OpenFeign)
- Register tethered GUIDs in CDH (Customer Data Hub) for CDH-enabled flows
- Support multi-scheme operations (GB and DE markets)
- Validate link codes against configurable length constraints

## Key Integrations

- **Worldline (Atos B2B PI API)** — SOAP web service for tether-by-account, tether-by-card, login-tethered-user, and refresh-session operations
- **PIBA Guid Service** — OpenFeign client for saving tethered GUID records (fallback-enabled via Resilience4j)
- **CDH (Customer Data Hub)** — OAuth-secured API for registering tethered users (via `commons-cdh-lib`)
- **Auth0** — Multi-tenant JWT authorization with management API access for employee token verification
- **Redis** — Clustered cache (Lettuce client) for session/data caching
- **Worldline BA API Lib** — Shared library providing JAXB-generated Worldline request/response types and security callbacks

## Domain Context

This service manages the "Tethering" process in the PI BB (Premier Inn Business Booker) platform. Tethering links a business employee to a Worldline loyalty account using either their account number or card number. Once tethered, the employee receives a GUID that identifies them in the Worldline system. The tethered login flow creates or refreshes a session with Worldline, returning session tokens (session ID, shared secret, hash, nonce, timestamp) that the frontend uses for authenticated Worldline interactions.

## API Endpoints

### Business Tether (`/business/`)
- `POST /business/tether` — Tether an employee by account number or card number (returns tethered GUID)
- `POST /business/tether/login` — Perform a tethered login (new session or refresh existing session)
