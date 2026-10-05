---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-12098
---
# Requirements Document

## Introduction
Implement the **Mobile SDK webhook API** in the Payment Orchestration Service. This is the
`POST /api/payments/webhooks/mobile-sdk` endpoint that **Datatrans** calls server-to-server
after a customer completes a card payment through the Datatrans **Mobile SDK Version 4**
(https://docs.datatrans.ch/docs/mobile-sdk) in the native iOS/Android apps. The Mobile SDK
handles the card entry and 3-D Secure challenge natively in-app; unlike the web Secure Fields
flow (where the frontend calls `POST /api/payments/authorize`), the mobile flow has **no
client-driven authorize call** — Datatrans notifies the backend asynchronously via this webhook.

Today the endpoint is a Phase 1 placeholder that returns `501 Not Implemented`, and the
`MobileSdkPaymentWorkflow` is a short-lived workflow that completes as soon as it returns a
`transactionId`. This work replaces the placeholder with a real implementation and updates the
Mobile SDK Temporal workflow so it stays open after initialisation, receives the webhook as a
signal, validates it, and transitions the payment from `INITIALIZED` to `AUTHORIZED`.

On reaching `AUTHORIZED`, the downstream choreography publishes an **AuthorisedPaymentEvent** to
Kafka for the Basket Service to pick up and drive booking confirmation. **The Kafka publication
itself is out of scope for this ticket and will be delivered as a later piece of work.** This
spec delivers the webhook endpoint, webhook validation, workflow correlation, and the workflow
state transition to `AUTHORIZED`, leaving a clearly-defined seam where the event publication is
added next.

## Glossary
- **Mobile SDK (Version 4)**: Datatrans native SDK embedded in the iOS/Android apps. Collects
  card data and performs the 3-D Secure challenge in-app, posting card data directly to Datatrans.
- **Webhook**: A server-to-server HTTP callback from Datatrans to the Payment Orchestrator after
  a Mobile SDK payment event. Delivered to `POST /api/payments/webhooks/mobile-sdk`.
- **transactionId**: The Datatrans transaction identifier. Created during Mobile SDK init and
  echoed back in the webhook payload; stored in the Temporal workflow state.
- **basketId**: The basket/reservation identifier passed by the app when initiating payment.
  Used to derive the Temporal workflow ID (`payment-{basketId}`).
- **refno**: Datatrans reference number, set to the `bookingReference` at init time.
- **MobileSdkPaymentWorkflow**: The Temporal workflow that orchestrates the mobile payment
  lifecycle. Started/attached at init and kept open to receive the webhook signal.
- **AuthorisedPaymentEvent**: The Kafka event published after authorisation for the Basket
  Service to consume. **Publication is out of scope for this ticket (future work).**
- **Datatrans-Signature**: The HMAC signature header Datatrans sends with webhook requests,
  used to verify the request is genuinely from Datatrans.
- **Backup reconciliation**: A separate, future process that reconciles payments left in
  `INITIALIZED` when a webhook is lost (Datatrans does not retry). **Out of scope for this
  ticket** — see Requirement 8.

## Requirements

### Requirement 1: Mobile SDK Webhook Endpoint
**User Story:** As Datatrans, I want to notify the Payment Orchestrator when a Mobile SDK
payment has been authorised, so that the backend can progress the payment without the app
having to call an authorize API.

#### Acceptance Criteria
1. THE service SHALL expose `POST /api/payments/webhooks/mobile-sdk` accepting the Datatrans
   Mobile SDK webhook JSON payload
2. THE endpoint SHALL replace the current Phase 1 `501 Not Implemented` placeholder response
3. THE endpoint SHALL return `200 OK` with body `{"status":"received"}` once a valid webhook has
   been accepted for processing
4. THE endpoint SHALL respond promptly and hand off workflow progression asynchronously (via a
   Temporal signal), because Datatrans does NOT retry on a non-2xx response
5. THE endpoint SHALL NOT be invoked by the mobile app directly — it is a server-to-server
   callback from Datatrans only
6. THE OpenAPI documentation SHALL describe the endpoint, its request payload, and its `200`,
   `401`, and `400` responses

### Requirement 2: Webhook Payload Model
**User Story:** As a Payment Orchestrator, I want to deserialize the Datatrans webhook payload,
so that I can extract the fields needed to correlate and authorise the payment.

#### Acceptance Criteria
1. THE service SHALL deserialize the webhook body into a request model capturing at least:
   `transactionId`, `merchantId`, `type`, `status`, `currency`, `refno`, `paymentMethod`,
   `authorizedAmount`, and the `card` object (including `alias`, `masked`, `expiryMonth`,
   `expiryYear`, and card `info`)
2. THE service SHALL ignore unknown fields in the payload (forward-compatible deserialization)
3. THE service SHALL treat the `status` field as the authoritative payment outcome
   (`authorized`, `failed`, `canceled`, etc.)
4. THE service SHALL NOT log full card data (PAN, alias, CVV) or other PII from the payload;
   only non-sensitive identifiers (`transactionId`, `refno`, `status`) MAY be logged

### Requirement 3: Webhook Authenticity Validation
**User Story:** As a security-conscious platform, I want every webhook verified as genuinely
originating from Datatrans, so that a forged request cannot authorise a payment.

#### Acceptance Criteria
1. THE service SHALL validate the authenticity of each webhook before processing it
2. THE service SHALL verify the `Datatrans-Signature` HMAC (SHA-256 over the signing timestamp
   concatenated with the raw request body, keyed with the configured Datatrans HMAC/sign key)
3. THE service SHALL reject a webhook whose signature is missing or invalid with `401 Unauthorized`
   and SHALL NOT signal the workflow
4. THE HMAC signing key SHALL be externally configurable (environment variable) and SHALL NOT be
   hard-coded or logged
5. WHEN signature validation cannot be performed because no key is configured in a non-production
   profile, THE service MAY skip validation only under an explicit, documented configuration flag
   (default: validation enabled)

### Requirement 4: Correlating the Webhook to the Payment Workflow
**User Story:** As a Payment Orchestrator, I want to map an incoming webhook to the correct
payment workflow, so that the right basket's payment is authorised.

#### Acceptance Criteria
1. THE service SHALL determine the target Temporal workflow (`payment-{basketId}`) from the
   incoming webhook
2. THE Mobile SDK init flow SHALL register a Datatrans webhook URL that carries the correlation
   key (`basketId`) so it is returned to the service on callback
3. WHEN no workflow can be resolved for the webhook, THE service SHALL return `200 OK` (to avoid
   Datatrans treating it as a delivery failure) and SHALL log a warning without progressing any
   payment
4. THE service SHALL verify that the `transactionId` in the webhook matches the `transactionId`
   stored in the resolved workflow before authorising; a mismatch SHALL be logged and SHALL NOT
   authorise the payment
5. WHEN a webhook arrives with no corresponding payment (no `basketId`, no matching workflow, or
   a `transactionId` that does not match the workflow's stored transaction), THE service SHALL
   acknowledge it with `200 OK`, log a warning with the available identifiers, and take no further
   action — it SHALL NOT create a workflow, authorise a payment, or return a non-2xx status

### Requirement 5: Mobile SDK Workflow Stays Open for the Webhook
**User Story:** As a Payment Orchestrator, I want the Mobile SDK workflow to remain running after
initialisation, so that it can receive the asynchronous webhook signal and progress the payment.

#### Acceptance Criteria
1. THE `MobileSdkPaymentWorkflow` SHALL remain open after returning the `transactionId` (it SHALL
   NOT complete at end of initialisation as it does today)
2. THE workflow SHALL store the `transactionId`, `bookingReference`/`refno`, `amount`, `currency`,
   and `reservationId` in its state during initialisation, mirroring the Secure Fields workflow
3. THE workflow SHALL be identified by `payment-{basketId}` so both channels use one workflow per
   basket and the webhook can address it deterministically
4. THE workflow SHALL expose a signal method that accepts the webhook payload
5. THE workflow SHALL expose a query for the current `PaymentStatus`
6. THE workflow SHALL remain open until it reaches a terminal state (`SETTLED`, `FAILED`, or
   `CANCELLED`). The `EXPIRED` state (for a webhook that never arrives) is produced by the future
   backup reconciliation process (Requirement 8) and is NOT implemented in this ticket; until
   then a workflow with no webhook simply remains open awaiting the signal

### Requirement 6: Authorisation on Webhook Receipt
**User Story:** As a Payment Orchestrator, I want a valid `authorized` webhook to move the payment
to `AUTHORIZED`, so that booking confirmation can proceed.

#### Acceptance Criteria
1. WHEN the workflow receives a webhook with `status = authorized` AND the current
   `PaymentStatus` is `INITIALIZED`, THE workflow SHALL transition to `AUTHORIZED`
2. THE workflow SHALL persist the authorisation outcome details available on the payload
   (e.g. `card.alias` token, `authorizedAmount`, acquirer authorization code) in its state for
   the downstream event
3. WHEN the workflow receives a webhook while the `PaymentStatus` is already `AUTHORIZED` or
   `SETTLED`, THE workflow SHALL treat the webhook as a duplicate and SHALL NOT re-authorise
   (idempotent handling)
4. WHEN the workflow receives a webhook with a non-authorised terminal `status` (e.g. `failed`),
   THE workflow SHALL transition to `FAILED`
5. WHEN the workflow receives a webhook with `status = canceled`, THE workflow SHALL transition to
   `CANCELLED`
6. THE state transition SHALL guard on the current status so that a duplicate webhook — or, in
   future, the backup reconciliation process — cannot double-authorise

### Requirement 7: AuthorisedPaymentEvent Publication (Out of Scope — Seam Only)
**User Story:** As a developer, I want a clear, isolated seam where the AuthorisedPaymentEvent is
published, so that the follow-up ticket can wire Kafka without reworking this change.

#### Acceptance Criteria
1. THE workflow SHALL, upon reaching `AUTHORIZED`, invoke a single well-defined extension point
   responsible for publishing the `AuthorisedPaymentEvent`
2. THE actual Kafka publication SHALL NOT be implemented in this ticket; the extension point MAY
   be a no-op/logging placeholder with a `TODO` referencing the follow-up work
3. THE placeholder SHALL be documented so the follow-up ticket knows the event name, the data
   available at that point (basketId, transactionId, card token/alias, amount, currency), and
   where publication is invoked
4. THE absence of the real publication SHALL NOT prevent the workflow from reaching `AUTHORIZED`

### Requirement 8: Backup Reconciliation for Stuck Transactions (Out of Scope — Future Work)
**User Story:** As a Payment Orchestrator, I want a backup path for payments left in `INITIALIZED`
when a webhook is lost, so that a completed Mobile SDK payment is not stranded because Datatrans
does not retry.

**This requirement is explicitly deferred and SHALL NOT be implemented in this ticket.** It is
recorded here so the follow-up work has a clear starting point. The webhook path (Requirements
1–6) is the primary mechanism; the reconciliation below is the safety net added later.

#### Future intent (not implemented here)
1. A separate backup/reconciliation process SHOULD detect transactions still in `INITIALIZED`
   beyond the init→authorise window (~30 minutes, tied to Datatrans transaction validity and the
   room-hold TTL)
2. For such a transaction it SHOULD query Datatrans (`GET /v1/transactions/{transactionId}`) and
   self-authorise if the transaction is authenticated/authorised, otherwise move it to `EXPIRED`
3. Any reconciliation path MUST reuse the same status guard (`PaymentStatus == INITIALIZED`) so it
   cannot race the webhook path into a double-authorise
4. The mechanism (in-workflow Temporal timer vs. external scanner) is a decision for the follow-up
   ticket

> Consequence for this ticket: a workflow whose webhook never arrives simply stays open awaiting
> the signal. No timer, polling, or `EXPIRED` transition is built here.

### Requirement 9: Error Handling and Resilience
**User Story:** As a Payment Orchestrator, I want robust webhook error handling, so that failures
are contained and observable.

#### Acceptance Criteria
1. THE endpoint SHALL return `400 Bad Request` when the payload is missing required fields
   (e.g. `transactionId` or `status`) and cannot be processed
2. THE endpoint SHALL return `401 Unauthorized` when signature validation fails (Requirement 3)
3. THE endpoint SHALL NOT expose internal error detail or stack traces in the response body
4. THE service SHALL log webhook receipt and outcome (accepted, rejected, duplicate, unresolved)
   with correlation identifiers (`transactionId`, `refno`, workflow ID) but without PII
5. WHEN signalling the workflow fails because the workflow is not found, THE service SHALL log a
   warning and return `200 OK` (Requirement 4.3)

### Requirement 10: Test Coverage
**User Story:** As a developer, I want the webhook path covered by unit tests, so that the
behaviour is verified and regressions are caught.

#### Acceptance Criteria
1. THE webhook controller SHALL have unit tests covering: valid webhook → `200`, invalid/missing
   signature → `401`, malformed payload → `400`, and unresolved workflow → `200` with no
   progression
2. THE signature validator SHALL have unit tests for valid, invalid, and missing signatures
3. THE Mobile SDK workflow SHALL have unit tests (Temporal test framework) covering: init keeps
   the workflow open, `authorized` webhook → `AUTHORIZED`, duplicate/late webhook → no
   re-authorise, `failed` webhook → `FAILED`, `canceled` webhook → `CANCELLED`, and a
   `transactionId` mismatch → no authorisation
4. ALL new and modified classes SHALL meet the project's JaCoCo coverage threshold (>= 80% line
   coverage), excluding models/config/mappers per the existing SonarQube/JaCoCo exclusions
5. ALL tests SHALL pass via `cd backend && ./mvnw test -pl book-pay/services/payment-orchestration-service`
