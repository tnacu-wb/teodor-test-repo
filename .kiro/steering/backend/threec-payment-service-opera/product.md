---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/threec-payment-service-opera/**"
---

# Product Overview

The 3C Payment Service is a reactive microservice that manages payment processing through the 3C Web2Pay Payment Gateway. It handles the full lifecycle of card payment transactions — initialisation, authorisation, tokenisation, refunds, reconciliation, and webhook callbacks — for all Premier Inn booking channels.

## Core Responsibilities

- Process card payment journeys (pay-now, pay-on-arrival) via 3C Web2Pay gateway
- Handle 3D Secure (SCA) authentication flows
- Manage card tokenisation (create and update tokens for saved cards)
- Process refund requests against completed transactions
- Handle payment webhooks from 3C for transaction status updates
- Support PayPal payment processing (Braintree integration)
- Provide reconciliation endpoints for payment settlement
- Manage Eckoh-integrated payments for CCUI (PCI-compliant card capture)
- Route payments to correct merchant accounts by booking type, currency, and channel
- Persist payment transaction state in DynamoDB with TTL-based expiry

## Key Integrations

- **3C Web2Pay Gateway** — card payment initialisation, transaction processing, token management, refunds, reconciliation
- **Braintree** — PayPal payment processing (client token, nonce-based transactions)
- **Basket Service** — webhook callbacks to update basket payment status
- **Hotel Booking Service** — make-booking calls post-payment
- **Hotel Card Service** — save card details for returning customers
- **DynamoDB** — payment transaction state persistence (keyed by payment-id, GSI on request-id)
- **Unleash** — feature flag management (mock payments, PIBA CNP split)
- **Eckoh** — PCI-compliant card capture for contact centre (CCUI)

## Domain Context

Part of the Book & Pay squad. This service sits behind the Basket Service in the payment flow — Basket Service initiates payments by calling this service, and receives status updates back via webhooks. Supports all channels: PI web, PI business, mobile apps (iOS/Android), CCUI (contact centre), GDS, and front desk (MOTO). Multi-currency (GBP and EUR).
