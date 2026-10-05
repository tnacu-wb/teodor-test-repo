---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/hotel-account-service-opera/**"
---

# Product Overview

Hotel Account Service Opera is an Identity squad service for Premier Inn leisure guests and business bookers. It owns account-facing orchestration across customer profile, booking history, password recovery, Universal Login notifications, and InnBusiness account information.

## Core capabilities

- Retrieve, search, update, and delete customer accounts.
- Retrieve leisure and business booking history from CDH.
- Support forgotten-password, reset-password, and reset-key validation flows.
- Send account-registration, password, and blocked-account emails for Universal Login.
- Return InnBusiness notifications and Worldline account information.
- Provide an internal cached customer lookup.

## Domain and security rules

- Customer identity, bookings, account balances, and contact data are sensitive; never log credentials, tokens, or personal data.
- Forgotten-password responses must not disclose whether an email address belongs to an account.
- Auth0 remains the source of truth for identity operations.
- Leisure and business-booker behavior must both be preserved when changing account or stay flows.
- GB and DE behavior, including language and postcode rules, must remain explicit.
- External failures must be mapped to the established error model rather than silently treated as success, except for the deliberate anti-enumeration password behavior.