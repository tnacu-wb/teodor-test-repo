---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/basket-async-order-processor/**"
---

# Product Overview

Basket Async Order Processor is a microservice within the Whitbread digital backend platform. It asynchronously processes basket/cart order events — confirming reservations, cancelling reservations, and confirming amendments — by consuming messages from Kafka and delegating to the Reservation Entity Service via REST.

## Core Responsibilities

- Consume basket order events from Kafka topics
- Route events by action type: COMMIT, CHANGE_PAY, CANCEL, AMEND
- Call the Reservation Entity Service to execute the order action
- Publish acknowledgement messages back to Kafka with the outcome status
- Handle failures with retry (5 attempts, exponential backoff) and dead-letter queue processing

## Key Integrations

- **Kafka** — inbound order events, outbound acknowledgement messages
- **Reservation Entity Service** — REST calls to confirm, cancel, or amend reservations
- **Basket Service** — basket status lookups

## Domain Context

This service is part of a basket/cart system that allows users to select multiple offerings (stay reservations, extras, etc.) and pay once. Each basket item maps to an Opera reservation managed by the Reservation Entity Service.
