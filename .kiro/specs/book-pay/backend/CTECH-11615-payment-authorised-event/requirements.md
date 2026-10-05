---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-11615
---
# Requirements Document

## Introduction
Implement the **PaymentAuthorisedEvent** Kafka publication in the Payment Orchestration Service
and add a **webhook status validation** step to the Mobile SDK flow. This is the follow-up work
referenced in CTECH-12098 (Requirement 7) where the `publishAuthorisedPaymentEvent` activity
was left as a documented no-op seam.

Today, the Mobile SDK workflow transitions to `AUTHORIZED` and calls a logging placeholder.
The Secure Fields workflow transitions to `AUTHORIZED` but does not call the publication seam at
all. Neither flow publishes an event for the Basket Service to consume.

This work:
1. Defines the **PaymentAuthorisedEvent** schema and publishes it to the `payment-authorised`
   Kafka topic after successful authorisation in **both** flows (Mobile SDK and Secure Fields).
2. Adds a **status validation step** to the Mobile SDK webhook handler: after receiving the
   webhook signal, the workflow calls `GET /v2/transactions/{transactionId}` to confirm the
   transaction status before transitioning to `AUTHORIZED`. This closes a gap where a forged or
   replayed webhook (past HMAC validation) could authorise without a backend-to-backend
   confirmation.
3. Updates the Secure Fields workflow to call `publishAuthorisedPaymentEvent` after successful
   Datatrans authorisation, aligning it with the Mobile SDK flow.

The Basket Service consumes this event to set `paymentProvider=datatrans` and trigger the
downstream booking-confirmation choreography (`BasketOrderEvent` → BAOP → OPERA → ack →
completion).

## Glossary
- **PaymentAuthorisedEvent**: A Kafka event published by the Payment Orchestrator after a
  payment reaches `AUTHORIZED`. Consumed by the Basket Service to drive booking confirmation.
- **payment-authorised**: The Kafka topic for `PaymentAuthorisedEvent` messages.
- **Status validation (webhook)**: A backend-to-backend call to Datatrans
  `GET /v2/transactions/{transactionId}` after receiving a webhook, to confirm the transaction
  is genuinely authorised before transitioning the workflow.
- **DatatransCardInfo**: Card details returned by Datatrans (alias, masked PAN, expiry month/year).
- **cardAlias / token**: The Datatrans-issued tokenised card identifier, usable for future payments.

## Requirements

### Requirement 1: PaymentAuthorisedEvent Schema
**User Story:** As the Basket Service, I want to receive a well-defined event after payment
authorisation, so that I can set the payment provider and trigger booking confirmation.

#### Acceptance Criteria
1. THE event SHALL be a JSON message with the following top-level fields:
   - `basketId` (string, required) — the basket identifier
   - `transactionId` (string, required) — the Datatrans transaction identifier
   - `paymentProvider` (string, required) — always `"datatrans"` for this flow
   - `paymentMethod` (string, nullable) — Datatrans payment method code (e.g. `"VIS"`, `"ECA"`)
   - `cardAlias` (string, nullable) — the tokenised card alias from Datatrans
   - `last4Digits` (string, nullable) — last 4 digits of the card number (e.g. `"4242"`)
   - `expiry` (string, nullable) — card expiry in `"MM/YY"` format (e.g. `"12/30"`)
   - `authorizedAmount` (integer, nullable) — authorised amount in minor units (pence/cents)
   - `currency` (string, nullable) — ISO 4217 currency code (e.g. `"GBP"`)
2. THE Kafka message key SHALL be the `basketId`
3. THE event SHALL NOT include raw PAN, CVV, or any PCI-sensitive data
4. THE event schema SHALL be forward-compatible (consumers MUST tolerate unknown fields)

### Requirement 2: Kafka Topic and Producer Configuration
**User Story:** As an operator, I want the Payment Orchestrator to publish to a dedicated Kafka
topic with proper configuration, so that the event delivery is reliable and observable.

#### Acceptance Criteria
1. THE event SHALL be published to the `payment-authorised` Kafka topic
2. THE topic name SHALL be externally configurable via application properties
   (`payment.events.topics.payment-authorised`)
3. THE producer SHALL use `JsonSerializer` for values and `StringSerializer` for keys
4. THE producer SHALL be configured with `acks=all` for durability
5. THE producer SHALL include Spring Kafka's standard error handling (log on failure)
6. THE service SHALL add `spring-kafka` as a dependency
7. THE local docker-compose SHALL include a Kafka broker with the `payment-authorised` topic
   pre-created

### Requirement 3: Mobile SDK — Publish Event After Authorisation
**User Story:** As the booking-confirmation choreography, I want a `PaymentAuthorisedEvent`
published after a Mobile SDK payment is authorised, so that the Basket Service can confirm
the booking.

#### Acceptance Criteria
1. THE Mobile SDK workflow SHALL publish the event after the payment transitions to `AUTHORIZED`
2. THE event SHALL carry the card data available from Datatrans (alias, last 4 digits, expiry,
   payment method) obtained from the status validation response (Requirement 5)
3. THE publication SHALL occur via the existing `publishAuthorisedPaymentEvent` activity seam
4. IF publication fails, the activity SHALL retry (up to the configured Temporal retry policy)
   but SHALL NOT block the workflow indefinitely — after max retries the workflow transitions
   to `FAILED`
5. THE event SHALL be published after authorisation succeeds; it is the final activity in the
   authorisation path (there is no postDeposit or settlement chain following it within this
   workflow scope)

### Requirement 4: Secure Fields — Publish Event After Authorisation
**User Story:** As the booking-confirmation choreography, I want a `PaymentAuthorisedEvent`
published after a Secure Fields payment is authorised, so that both channels follow the same
downstream flow.

#### Acceptance Criteria
1. THE Secure Fields workflow SHALL publish the event after `authorizeTransaction` returns
   successfully (status = `AUTHORIZED`)
2. THE event SHALL carry card data from the `DatatransAuthorizeResponse` (alias, last 4 digits
   derived from masked PAN, expiry in `MM/YY` format)
3. THE publication SHALL use the same `publishAuthorisedPaymentEvent` activity as Mobile SDK
4. THE event SHALL be published after authorisation succeeds; it is the final activity in the
   authorisation path (there is no postDeposit or settlement chain following it within this
   workflow scope)
5. THE activity signature SHALL be extended to carry the additional card/payment fields needed
   for the event schema (payment method, last 4 digits, expiry)

### Requirement 5: Mobile SDK — Webhook Status Validation
**User Story:** As a security-hardened platform, I want the Mobile SDK workflow to validate the
transaction status with Datatrans after receiving a webhook, so that a forged or replayed
webhook cannot trigger authorisation without backend confirmation.

#### Acceptance Criteria
1. AFTER receiving a webhook signal with `status = "authorized"`, the workflow SHALL call
   `GET /v2/transactions/{transactionId}` (the existing `getTransactionStatus` activity) to
   confirm the Datatrans-side status
2. THE workflow SHALL only transition to `AUTHORIZED` if the status validation returns
   `authorized` or `settled`
3. IF the status validation returns `failed` or `canceled`, the workflow SHALL transition to
   `FAILED` or `CANCELLED` respectively, regardless of what the webhook claimed
4. IF the status validation call fails transiently (gateway error, timeout), the workflow
   SHALL retry per the existing `statusActivities` retry policy (max 2 attempts)
5. IF the status validation call throws `TransactionNotFoundException` (404), the workflow
   SHALL transition to `FAILED`
6. THE card data for the `PaymentAuthorisedEvent` SHALL be sourced from the status validation
   response (not the webhook payload), as it is the backend-confirmed source of truth
7. THE existing reconciliation poller path already calls `getTransactionStatus` and authorises
   on `authorized`/`settled` — it SHALL continue to work unchanged
8. THE `authorizationInProgress` guard SHALL prevent the reconciliation poller and the webhook
   status validation from racing

### Requirement 6: Activity Signature Extension
**User Story:** As a developer, I want the `publishAuthorisedPaymentEvent` activity to carry
all data needed for the full event schema, so that the event can be assembled without
additional service calls.

#### Acceptance Criteria
1. THE activity signature SHALL be extended to include: `paymentMethod`, `last4Digits`, `expiry`
2. ALL existing callers (Mobile SDK workflow) SHALL be updated to pass the new parameters
3. THE Secure Fields workflow SHALL call the activity with the same extended signature
4. THE activity parameters SHALL be sufficient to construct the full `PaymentAuthorisedEvent`
   without any additional service calls
5. Temporal workflow versioning is NOT required for this change because the service has no
   production deployment with in-flight workflows. Versioning markers may be added at a future
   date if a mid-flight deployment is needed

### Requirement 7: Observability and Logging
**User Story:** As an operator, I want visibility into event publication success/failure, so
that I can diagnose issues in the payment flow.

#### Acceptance Criteria
1. THE activity SHALL log at INFO on successful publication (basketId, transactionId)
2. THE activity SHALL log at ERROR on publication failure (basketId, transactionId, error)
3. THE activity SHALL NOT log cardAlias, last4Digits, or any card data
4. THE Kafka producer SHALL expose standard Micrometer metrics (send count, errors, latency)
5. THE webhook status validation SHALL log the confirmed status at INFO
   (basketId, transactionId, confirmedStatus)

### Requirement 8: Testing
**User Story:** As a developer, I want comprehensive tests for the event publication and
webhook validation, so that regressions are caught early.

#### Acceptance Criteria
1. UNIT tests SHALL verify the `PaymentAuthorisedEvent` model serialises to the expected JSON
2. UNIT tests SHALL verify the Kafka producer sends messages with the correct topic, key, and
   payload
3. WORKFLOW tests SHALL verify Mobile SDK publishes the event after status validation succeeds
4. WORKFLOW tests SHALL verify Secure Fields publishes the event after authorisation
5. WORKFLOW tests SHALL verify Mobile SDK webhook triggers status validation before authorising
6. WORKFLOW tests SHALL verify that a webhook with `status=authorized` but status validation
   returning `failed` results in `FAILED` (not `AUTHORIZED`)
7. WORKFLOW tests SHALL verify the reconciliation poller still works unchanged (no regression)

### Requirement 9: Docker Compose
**User Story:** As a developer running locally, I want Kafka available in the docker-compose
environment with the required topic pre-created, so that I can test event publication locally.

#### Acceptance Criteria
1. THE docker-compose SHALL include a Kafka broker (KRaft mode, no Zookeeper)
2. THE docker-compose SHALL include a `kafka-init` service that creates the `payment-authorised`
   topic on startup
3. THE Kafka broker SHALL be accessible at `localhost:9092` from the host
4. THE application configuration SHALL include a `local` profile with Kafka bootstrap server
   set to `localhost:9092`
