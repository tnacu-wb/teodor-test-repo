---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/basket-confirmation-processor/**"
---

# Product Overview

Basket Confirmation Processor is a microservice within the Whitbread digital backend platform. It consumes basket acknowledgement events from Kafka and confirms individual basket item processing by calling the Basket Service REST API. This closes the loop on the order fulfilment workflow by reporting item-level outcomes back to the basket.

## Core Responsibilities

- Consume basket acknowledgement events from Kafka (`ack` topic)
- Map inbound Kafka event DTOs to domain models
- Call the Basket Service to confirm item processing status via REST
- Handle failures with retry logic (Spring Retry)

## Key Integrations

- **Kafka** — inbound acknowledgement events consumed from the `ack` topic
- **Basket Service** — REST calls to confirm item processing at `/v1/baskets/{basket-reference}/items/{itemId}/acks`
- **Micrometer / Brave** — distributed tracing with W3C propagation and Kafka client instrumentation

## Domain Context

This service is part of the basket/cart system that allows users to select multiple offerings (stay reservations, extras, etc.) and pay once. After an order action (commit, cancel, amend) is processed by the Basket Async Order Processor, this service receives the acknowledgement and updates the basket item status via the Basket Service.
