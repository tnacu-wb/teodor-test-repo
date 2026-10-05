---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/payment-orchestration-service/**"
---

# Product Overview

Payment Orchestration Service coordinates the new card payment journey for Whitbread's digital platform. It serves both web (Secure Fields) and mobile (Mobile SDK v4) channels, orchestrating payment transactions through Datatrans and managing workflows via Temporal.

## Core Responsibilities

- Coordinate card payment flows for web and mobile channels
- Orchestrate payment lifecycle through Datatrans gateway
- Manage payment workflows using Temporal for retry and failure handling
- Integrate with Basket Service for reservation context
- Post deposits/folios to OPERA on successful payment

## Key Integrations

- **Datatrans** — payment gateway hosting Secure Fields (web) and Mobile SDK v4 (native)
- **Temporal** — workflow orchestration for payment state management and retries
- **Basket Service** — reservation/basket state lookups
- **OPERA** — deposit and folio posting on payment completion

## Domain Context

This service sits between client applications (web/mobile) and payment infrastructure. It abstracts the complexity of multi-step payment flows (initialisation → 3-D Secure → authorisation → settlement) behind a simple API, handling channel-specific differences internally.

## Current Phase

Phase 1: infrastructure scaffold with stub endpoints returning 501 Not Implemented. Full payment orchestration logic follows in subsequent phases.
