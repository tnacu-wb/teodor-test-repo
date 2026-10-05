---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/refund-request-processor/**"
---

# Product Overview

Refund Request Processor is a Kafka consumer microservice within the Whitbread digital backend platform. It processes refund requests originating from the Basket Service, executing card refunds via the 3C Payments API and acknowledging completion back to the basket event stream.

## Core Responsibilities

- Consume refund request messages from Kafka topics published by the Basket Service
- Validate and deserialise refund request payloads
- Execute card refund operations via the 3C Payments REST API
- Publish acknowledgement events back to Kafka upon successful/failed refund processing
- Provide health and metrics endpoints for operational monitoring
- Expose a minimal REST API for operational support (health, status)

## Key Integrations

- **Kafka (consumer)** — consumes refund request events from the Basket Service topic
- **Kafka (producer)** — publishes basket acknowledgement events confirming refund processing outcome
- **3C Payments API** — external payment gateway for executing card refunds via REST
- **Basket Service** — upstream event producer; acknowledgement target

## Domain Context

This service is part of the Book & Pay squad's responsibility. It handles the asynchronous refund leg of the booking lifecycle — when a reservation is cancelled or amended and a refund is due, the Basket Service publishes a refund request event which this processor picks up, executes against the 3C Payments gateway, and confirms back. It decouples the synchronous booking flow from the potentially slow/retryable payment refund operation.
