---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-12128
depends_on: CTECH-12098
---
# Requirements Document

## Introduction

The Mobile SDK payment flow delivered under CTECH-12098 relies entirely on a Datatrans webhook to
learn that a payment has been authorised. `MobileSdkPaymentWorkflowImpl.run(...)` awaits a terminal
payment state indefinitely, with an explicit code comment deferring reconciliation to a future
ticket. This spec is that deferred reconciliation process.

This feature adds a **Reconciliation Poller** inside the Mobile SDK payment workflow: a
Temporal-timer-driven backup that, when no webhook has arrived within a configurable initial delay
after Mobile SDK initialisation, polls the Datatrans transaction status endpoint at a configurable
interval until either a terminal status is observed, the webhook arrives, or a configurable
reconciliation deadline is reached. When the poller observes an authorised transaction it drives the
exact same state transition and the same `publishAuthorisedPaymentEvent` seam as the webhook path,
with an exactly-once guarantee shared between the two paths. When the deadline passes without a
terminal answer, the payment moves to a new `EXPIRED` terminal state so the workflow completes
instead of leaking an open workflow execution per abandoned basket.

Scope is the **Mobile SDK channel only**; the Secure Fields channel is out of scope for this ticket
(see Open Question OQ-4).

## Glossary

- **Payment_Orchestrator**: The `payment-orchestration-service` Spring Boot application.
- **MobileSdk_Workflow**: The long-lived Temporal workflow `MobileSdkPaymentWorkflow` /
  `MobileSdkPaymentWorkflowImpl`, started per basket, exposing `run(basketId)` (workflow method),
  `initMobileSdk(...)` (update), `webhookReceived(...)` (signal) and query methods.
- **Reconciliation_Poller**: The backup polling logic added to `MobileSdk_Workflow.run(...)`. It is
  workflow code driven by Temporal timers, not a separate workflow or a Spring scheduled task.
- **Webhook_Path**: The existing `webhookReceived` signal handler that transitions payment state
  from a Datatrans webhook notification.
- **Datatrans_Status_Api**: The Datatrans "check transaction status" endpoint,
  `GET /v1/transactions/{transactionId}`, authenticated with HTTP Basic Auth using the configured
  merchant identifier and merchant password. See Open Question OQ-1.
- **Datatrans_Status_Adapter**: The `DatatransRestAdapter` implementation of the new
  `DatatransOutPort` method that calls Datatrans_Status_Api.
- **Status_Activity**: The new `PaymentActivities` activity method that invokes
  Datatrans_Status_Adapter and returns a channel-neutral transaction status result to the workflow.
- **Transaction_Status**: The `status` value returned by Datatrans_Status_Api for a transaction
  (for example `initialized`, `authorized`, `settled`, `canceled`, `failed`). See OQ-2.
- **Payment_Status**: The workflow-owned `PaymentStatus` enum. Existing values: `INITIALIZED`,
  `AUTHORIZED`, `SETTLED`, `FAILED`, `CANCELLED`. This feature adds `EXPIRED`.
- **Terminal_Payment_Status**: Any Payment_Status in {`AUTHORIZED`, `FAILED`, `CANCELLED`,
  `EXPIRED`}.
- **Authorisation_Publication**: The invocation of
  `PaymentActivities.publishAuthorisedPaymentEvent(basketId, transactionId, cardAlias, authorizedAmount, currency)`
  — the documented seam that a later ticket replaces with a Kafka publish.
- **Initial_Reconciliation_Delay**: Configurable duration measured from successful Mobile SDK
  initialisation, after which Reconciliation_Poller performs its first poll.
- **Reconciliation_Poll_Interval**: Configurable duration between consecutive polls.
- **Reconciliation_Deadline**: Configurable maximum total duration, measured from successful Mobile
  SDK initialisation, after which Reconciliation_Poller stops polling.
- **Sensitive_Payment_Data**: Primary account number, masked card number, card expiry, cardholder
  name, CVV, card fingerprint, and any guest personally identifiable information.

## Requirements

### Requirement 1: Reconciliation Activation

**User Story:** As a payment operations engineer, I want a backup process to start only when the
Datatrans webhook has failed to arrive, so that healthy webhook-driven payments incur no additional
Datatrans traffic.

#### Acceptance Criteria

1. WHEN `initMobileSdk` completes successfully and returns a non-blank transaction identifier, THE MobileSdk_Workflow SHALL start Reconciliation_Poller with its Initial_Reconciliation_Delay measured from that completion.
2. WHILE Payment_Status is `INITIALIZED` and the Initial_Reconciliation_Delay has not elapsed, THE Reconciliation_Poller SHALL perform zero calls to Datatrans_Status_Api.
3. WHEN Payment_Status reaches a Terminal_Payment_Status before the Initial_Reconciliation_Delay elapses, THE Reconciliation_Poller SHALL perform zero calls to Datatrans_Status_Api.
4. WHILE Payment_Status is `INITIALIZED` and the Initial_Reconciliation_Delay has elapsed and the Reconciliation_Deadline has not been reached, THE Reconciliation_Poller SHALL invoke Status_Activity once per Reconciliation_Poll_Interval.
5. WHEN Payment_Status reaches a Terminal_Payment_Status while Reconciliation_Poller is waiting for the next Reconciliation_Poll_Interval, THE Reconciliation_Poller SHALL stop polling before the next poll is issued.
6. WHILE `run(basketId)` has been started and `initMobileSdk` has not yet completed successfully, THE Reconciliation_Poller SHALL perform zero calls to Datatrans_Status_Api.

### Requirement 2: Datatrans Transaction Status Retrieval

**User Story:** As a payment orchestrator, I want a dedicated outbound port and activity for reading
Datatrans transaction status, so that reconciliation follows the existing hexagonal boundaries.

#### Acceptance Criteria

1. THE Payment_Orchestrator SHALL expose a transaction status retrieval operation on the secondary port `DatatransOutPort` that accepts a transaction identifier and returns a domain status result.
2. WHEN the transaction status operation is invoked, THE Datatrans_Status_Adapter SHALL send `GET /v1/transactions/{transactionId}` to the configured Datatrans base URL with HTTP Basic Authentication using the configured merchant identifier and merchant password.
3. THE Datatrans_Status_Adapter SHALL map the Datatrans response into a domain result carrying Transaction_Status, card alias, authorised amount, currency, and acquirer authorisation code.
4. THE Payment_Orchestrator SHALL expose the transaction status retrieval operation to workflows as a Status_Activity on the `PaymentActivities` activity interface.
5. IF Datatrans_Status_Api returns a 2xx response whose body is absent or whose Transaction_Status is absent or blank, THEN THE Datatrans_Status_Adapter SHALL throw `DatatransGatewayException`.
6. IF Datatrans_Status_Api returns HTTP 404, THEN THE Datatrans_Status_Adapter SHALL throw `TransactionNotFoundException`.
7. IF Datatrans_Status_Api returns any other error status, THEN THE Datatrans_Status_Adapter SHALL throw `DatatransGatewayException` carrying the returned HTTP status.
8. IF the Datatrans host is unreachable or the configured read timeout elapses, THEN THE Datatrans_Status_Adapter SHALL throw `ServiceUnavailableException`.
9. THE Payment_Orchestrator SHALL register the transaction status operation in the same Spring bean wiring used by the existing Datatrans activities, so that a deployed worker resolves Status_Activity without further configuration.

### Requirement 3: Authorised Reconciliation Outcome

**User Story:** As a guest whose payment succeeded but whose webhook was lost, I want the backup
process to complete my booking, so that my payment is not stranded.

#### Acceptance Criteria

1. WHEN Status_Activity returns Transaction_Status `authorized` AND Payment_Status is `INITIALIZED`, THE MobileSdk_Workflow SHALL set Payment_Status to `AUTHORIZED`.
2. WHEN Status_Activity returns Transaction_Status `authorized` AND Payment_Status is `INITIALIZED`, THE MobileSdk_Workflow SHALL store the card alias, authorised amount, and acquirer authorisation code from the status result in workflow state.
3. WHEN the MobileSdk_Workflow sets Payment_Status to `AUTHORIZED` from a reconciliation poll, THE MobileSdk_Workflow SHALL perform Authorisation_Publication with the same arguments the Webhook_Path uses.
4. FOR ANY single MobileSdk_Workflow execution, THE MobileSdk_Workflow SHALL perform Authorisation_Publication at most once, irrespective of how many webhook signals and reconciliation polls report an authorised transaction.
5. WHEN a webhook signal reporting `authorized` is processed before a reconciliation poll result is applied, THE Reconciliation_Poller SHALL leave Payment_Status and stored authorisation state unchanged and SHALL NOT perform Authorisation_Publication.
6. WHEN a reconciliation poll has already set Payment_Status to `AUTHORIZED` and a webhook signal reporting `authorized` for the same transaction identifier subsequently arrives, THE Webhook_Path SHALL leave Payment_Status and stored authorisation state unchanged and SHALL NOT perform Authorisation_Publication.
7. WHILE an authorisation transition initiated by either the Webhook_Path or the Reconciliation_Poller is in progress, THE MobileSdk_Workflow SHALL reject a concurrent authorisation transition from the other path using the existing `authorizationInProgress` guard.
8. IF Status_Activity returns a result whose transaction identifier differs from the transaction identifier held in workflow state, THEN THE MobileSdk_Workflow SHALL leave Payment_Status unchanged and SHALL log the mismatch.

### Requirement 4: Non-Authorised Terminal Reconciliation Outcomes

**User Story:** As a payment operations engineer, I want reconciliation to classify non-authorised
outcomes identically to the webhook path, so that payment state is consistent regardless of which
path observed the outcome.

#### Acceptance Criteria

1. WHEN Status_Activity returns Transaction_Status `canceled` AND Payment_Status is `INITIALIZED`, THE MobileSdk_Workflow SHALL set Payment_Status to `CANCELLED` and SHALL stop polling.
2. WHEN Status_Activity returns Transaction_Status `failed` AND Payment_Status is `INITIALIZED`, THE MobileSdk_Workflow SHALL set Payment_Status to `FAILED` and SHALL stop polling.
3. WHEN Status_Activity returns Transaction_Status `settled` AND Payment_Status is `INITIALIZED`, THE MobileSdk_Workflow SHALL apply the authorised outcome defined in Requirement 3, because settlement implies a completed authorisation.
4. WHEN Status_Activity returns a Transaction_Status that represents an in-flight transaction, THE MobileSdk_Workflow SHALL leave Payment_Status as `INITIALIZED` and SHALL continue polling until the Reconciliation_Deadline is reached.
5. WHEN Status_Activity returns a Transaction_Status that the Payment_Orchestrator does not recognise, THE MobileSdk_Workflow SHALL leave Payment_Status as `INITIALIZED`, SHALL log the unrecognised value, and SHALL continue polling until the Reconciliation_Deadline is reached.
6. IF Status_Activity fails with `TransactionNotFoundException`, THEN THE MobileSdk_Workflow SHALL set Payment_Status to `FAILED` and SHALL stop polling.

### Requirement 5: Reconciliation Deadline and Expiry

**User Story:** As a platform engineer, I want abandoned payments to reach a terminal state, so that
workflow executions do not accumulate indefinitely.

#### Acceptance Criteria

1. THE Payment_Orchestrator SHALL add the value `EXPIRED` to the Payment_Status enumeration.
2. WHEN the Reconciliation_Deadline is reached AND Payment_Status is `INITIALIZED`, THE MobileSdk_Workflow SHALL set Payment_Status to `EXPIRED` and SHALL stop polling.
3. WHEN Payment_Status is set to `EXPIRED`, THE MobileSdk_Workflow SHALL complete `run(basketId)` without throwing a workflow failure.
4. WHEN Payment_Status is set to `EXPIRED`, THE MobileSdk_Workflow SHALL perform zero Authorisation_Publication invocations.
5. WHILE Payment_Status is `EXPIRED`, THE MobileSdk_Workflow SHALL return `EXPIRED` from the `getPaymentStatus` query for as long as the execution history is retained.
6. WHEN a webhook signal arrives after Payment_Status is `EXPIRED`, THE Webhook_Path SHALL leave Payment_Status unchanged and SHALL log the late arrival, because the existing guard admits only `INITIALIZED`.

### Requirement 6: Externalised Timing Configuration

**User Story:** As a DevOps engineer, I want every reconciliation timing value configurable per
environment, so that I can tune reconciliation without a code change.

#### Acceptance Criteria

1. THE Payment_Orchestrator SHALL read Initial_Reconciliation_Delay from `integrations.datatrans.reconciliation.initial-delay` in `application.yml`, declared as `${DATATRANS_RECONCILIATION_INITIAL_DELAY:2m}`.
2. THE Payment_Orchestrator SHALL read Reconciliation_Poll_Interval from `integrations.datatrans.reconciliation.poll-interval` in `application.yml`, declared as `${DATATRANS_RECONCILIATION_POLL_INTERVAL:30s}`.
3. THE Payment_Orchestrator SHALL read Reconciliation_Deadline from `integrations.datatrans.reconciliation.max-duration` in `application.yml`, declared as `${DATATRANS_RECONCILIATION_MAX_DURATION:30m}`.
4. THE Payment_Orchestrator SHALL read a reconciliation enablement flag from `integrations.datatrans.reconciliation.enabled` in `application.yml`, declared as `${DATATRANS_RECONCILIATION_ENABLED:true}`.
5. WHERE `integrations.datatrans.reconciliation.enabled` is `false`, THE MobileSdk_Workflow SHALL await a webhook signal without starting Reconciliation_Poller and without applying the Reconciliation_Deadline.
6. THE Payment_Orchestrator SHALL bind the reconciliation configuration keys to a typed properties class in `infrastructure/config`, following the binding style of `DatatransWebhookProperties`.
7. THE Payment_Orchestrator SHALL express the three duration values as `java.time.Duration` values accepting Spring's duration notation, so that `30s`, `2m`, and `30m` are all valid inputs.
8. IF Initial_Reconciliation_Delay, Reconciliation_Poll_Interval, or Reconciliation_Deadline is configured as zero or negative, OR Reconciliation_Deadline is configured shorter than Initial_Reconciliation_Delay, THEN THE Payment_Orchestrator SHALL fail application startup with a message naming the offending configuration key.
9. THE MobileSdk_Workflow SHALL receive the reconciliation timing values as workflow input or activity-supplied data rather than reading Spring configuration from workflow code, so that the domain layer stays free of Spring dependencies.

### Requirement 7: Polling Resilience

**User Story:** As a guest, I want a Datatrans outage during reconciliation to leave my payment
recoverable, so that a gateway problem does not destroy my in-flight booking.

#### Acceptance Criteria

1. IF Status_Activity fails with `ServiceUnavailableException`, THEN THE MobileSdk_Workflow SHALL leave Payment_Status as `INITIALIZED` and SHALL schedule the next poll after Reconciliation_Poll_Interval.
2. IF Status_Activity fails with `DatatransGatewayException`, THEN THE MobileSdk_Workflow SHALL leave Payment_Status as `INITIALIZED` and SHALL schedule the next poll after Reconciliation_Poll_Interval.
3. IF every poll attempt fails until the Reconciliation_Deadline is reached, THEN THE MobileSdk_Workflow SHALL apply the expiry behaviour defined in Requirement 5 rather than failing the workflow execution.
4. THE Status_Activity invocation SHALL use a retry policy whose maximum attempts and start-to-close timeout together complete within one Reconciliation_Poll_Interval, so that consecutive polls do not overlap.
5. THE MobileSdk_Workflow SHALL treat a Status_Activity failure as a poll outcome inside the polling loop rather than propagating the failure out of `run(basketId)`.
6. WHEN a webhook signal arrives while a Status_Activity invocation is in flight, THE MobileSdk_Workflow SHALL apply the webhook outcome and SHALL discard the in-flight poll result on completion.

### Requirement 8: Observability Without Sensitive Data

**User Story:** As a support engineer, I want reconciliation activity traceable in logs, so that I
can diagnose missing webhooks without exposing guest or card data.

#### Acceptance Criteria

1. WHEN Reconciliation_Poller issues a poll, THE MobileSdk_Workflow SHALL log the basket identifier, the transaction identifier, and the poll attempt number at INFO level.
2. WHEN a poll returns a Transaction_Status, THE MobileSdk_Workflow SHALL log that Transaction_Status and the resulting Payment_Status at INFO level.
3. WHEN a poll attempt fails, THE MobileSdk_Workflow SHALL log the failure type and the transaction identifier at WARN level.
4. WHEN Payment_Status is set to `EXPIRED`, THE MobileSdk_Workflow SHALL log the basket identifier, the transaction identifier, and the total number of poll attempts at WARN level.
5. THE Payment_Orchestrator SHALL exclude Sensitive_Payment_Data from every log statement added by this feature.
6. THE MobileSdk_Workflow SHALL emit all reconciliation log statements through `Workflow.getLogger`, so that replayed history produces no duplicate log output.
7. THE Datatrans_Status_Adapter SHALL exclude the merchant password and the Datatrans response body's card object from every log statement.

### Requirement 9: Determinism and Replay Safety

**User Story:** As a platform engineer, I want the polling loop to be deterministic, so that workflow
replay after a worker restart or a code deployment reproduces the same history.

#### Acceptance Criteria

1. THE MobileSdk_Workflow SHALL derive all reconciliation waiting from Temporal workflow timers, using `Workflow.sleep` or `Workflow.await` with a timeout.
2. THE MobileSdk_Workflow SHALL exclude `System.currentTimeMillis`, `Instant.now`, `Thread.sleep`, and any other wall-clock or blocking construct from reconciliation code paths, reading workflow time through `Workflow.currentTimeMillis` where a time reading is required.
3. THE MobileSdk_Workflow SHALL perform every Datatrans status read through Status_Activity rather than by direct HTTP invocation from workflow code.
4. WHEN a worker restarts mid-reconciliation, THE MobileSdk_Workflow SHALL resume the polling loop from the replayed workflow state with the same remaining Reconciliation_Deadline.
5. THE MobileSdk_Workflow SHALL hold reconciliation loop state in workflow instance fields, so that Payment_Status, poll attempt count, and authorisation state survive replay.

### Requirement 10: Testing and Coverage

**User Story:** As a developer, I want reconciliation covered by automated tests including Spring
context verification, so that wiring defects are caught before deployment.

#### Acceptance Criteria

1. THE test suite SHALL verify with `TestWorkflowExtension` that no poll occurs when a webhook signal arrives before Initial_Reconciliation_Delay elapses.
2. THE test suite SHALL verify with `TestWorkflowExtension` that a poll returning `authorized` sets Payment_Status to `AUTHORIZED` and performs Authorisation_Publication exactly once.
3. THE test suite SHALL verify with `TestWorkflowExtension` that a webhook signal and a poll both reporting `authorized` for the same transaction perform Authorisation_Publication exactly once.
4. THE test suite SHALL verify with `TestWorkflowExtension` that repeated poll failures until the Reconciliation_Deadline produce Payment_Status `EXPIRED` and a completed workflow execution.
5. THE test suite SHALL verify that Datatrans_Status_Adapter maps a 404 response to `TransactionNotFoundException`, a 5xx response to `DatatransGatewayException`, and a connection failure to `ServiceUnavailableException`.
6. THE test suite SHALL verify through a Spring context test that the reconciliation properties bind from `application.yml` and that Status_Activity is registered on the Temporal worker, because unit tests alone missed a Spring wiring defect in CTECH-12098.
7. THE test suite SHALL verify that a configuration value violating the validation rule in Requirement 6 prevents Spring context startup.
8. THE build SHALL report JaCoCo line coverage of at least 80 percent for each new non-excluded class introduced by this feature.
9. THE build SHALL pass `checkstyle:check` with zero warnings for all files changed by this feature.

## Assumptions

- A1. CTECH-12098 is merged before this work starts, so `MobileSdkPaymentWorkflow` already exposes
  `run(basketId)`, `initMobileSdk(...)`, `webhookReceived(...)`, the query methods, the
  `authorizationInProgress` guard, and `publishAuthorisedPaymentEvent`. The branch under review
  still carries the pre-12098 `processPayment(String)` shape, so requirement wording targets the
  post-12098 interface.
- A2. Datatrans transaction identifiers remain valid for roughly 30 minutes after initialisation,
  which is why the default Reconciliation_Deadline is 30 minutes: polling beyond transaction
  validity yields no new information.
- A3. Downstream consumers of `PaymentStatus` tolerate a new `EXPIRED` enum value; no persisted
  representation of `PaymentStatus` exists outside Temporal workflow state today.

## Open Questions

- OQ-1. **Status endpoint version.** Datatrans documents the status read as
  `GET /v1/transactions/{transactionId}` with Basic Auth
  ([API endpoints](https://docs.datatrans.ch/docs/api-endpoints),
  [After the payment](https://docs.datatrans.ch/docs/after-the-payment)), and separately documents a
  v1 → v2 migration in which `/v1/transactions/{transactionId}/<operation>` becomes
  `/v2/transactions/{transactionId}/<operation>`
  ([migration guide](https://docs.datatrans.ch/docs/backend-migration-transactions-api-v1-v2)).
  Mobile SDK initialisation in this service already uses `/v2/transactions`. Whether the status read
  for a v2-initialised transaction must be issued against `/v1` or `/v2` needs confirmation against
  the sandbox before the design fixes the path. (Content was rephrased for compliance with licensing
  restrictions.)
- OQ-2. **Status value set and response field names.** The exact enumeration of `status` values, and
  the exact JSON paths for the authorised amount and the acquirer authorisation code in the status
  response, need confirming against a sandbox response capture. The documented example confirms only
  the `card.alias` location.
- OQ-3. **Expired-payment side effects.** Should `EXPIRED` trigger any downstream action — a
  Datatrans cancel call, a basket release, or an operations alert — or is a terminal state plus a
  WARN log sufficient for this ticket?
- OQ-4. **Secure Fields scope.** This spec covers the Mobile SDK channel only. Secure Fields
  currently completes authorisation synchronously and does not depend on a webhook, so it needs no
  reconciliation today. Confirm that assumption, or raise a follow-up ticket if Secure Fields is
  also moving to webhook-driven completion.
- OQ-5. **Poll cost ceiling.** With the proposed defaults an abandoned payment issues roughly 56
  status calls over 30 minutes. Confirm this is acceptable against any Datatrans rate limit or
  commercial per-call cost, or agree a backoff schedule instead of a fixed interval.
