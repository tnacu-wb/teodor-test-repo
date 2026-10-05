# Implementation Plan: Mobile SDK Reconciliation Poller (CTECH-12128)

## Overview

This plan implements a Temporal-timer-driven reconciliation poller inside `MobileSdkPaymentWorkflowImpl.run(...)` that backs up the Datatrans webhook by polling `GET /v1/transactions/{transactionId}` on a fixed cadence, with exactly-once authorisation semantics and a configurable expiry deadline. The implementation follows the existing hexagonal architecture, activity/adapter patterns, and Temporal versioning conventions already in the payment-orchestration-service.

## Tasks

- [x] 1. Domain records, enum, and port extension
  - [x] 1.1 Add `EXPIRED` to `PaymentStatus` enum and create `MobileSdkReconciliationSettings` record
    - Add the `EXPIRED` value to the existing `PaymentStatus` enum
    - Create `MobileSdkReconciliationSettings` record with fields: `boolean enabled`, `long initialDelayMillis`, `long pollIntervalMillis`, `long maxDurationMillis`
    - Update any terminal-status utility/set to include `EXPIRED`
    - _Requirements: 5.1, 6.9_

  - [x] 1.2 Create `DatatransTransactionStatus` domain result record
    - Create record with fields: `String transactionId`, `String status`, `String currency`, `Integer authorizedAmount`, `String acquirerAuthorizationCode`, `DatatransCardInfo card`
    - Place in the domain layer alongside existing payment result types
    - _Requirements: 2.1, 2.3_

  - [x] 1.3 Extend `InitMobileSdkCommand` with reconciliation settings
    - Add nullable `MobileSdkReconciliationSettings reconciliation` field to `InitMobileSdkCommand`
    - Add a source-compatible one-argument constructor `InitMobileSdkCommand(String webhookUrl)` that passes `null` for settings
    - _Requirements: 6.9, 9.5_

  - [x] 1.4 Add `getTransactionStatus` to `DatatransOutPort`
    - Add `DatatransTransactionStatus getTransactionStatus(String transactionId)` to the `DatatransOutPort` secondary port interface
    - _Requirements: 2.1_

  - [x] 1.5 Add `getTransactionStatus` to `PaymentActivities` interface
    - Add the activity method `DatatransTransactionStatus getTransactionStatus(String transactionId)` to the existing `PaymentActivities` Temporal activity interface
    - _Requirements: 2.4_

- [x] 2. Infrastructure: Configuration and adapter
  - [x] 2.1 Create `DatatransReconciliationProperties` configuration class
    - Create in `infrastructure/config` following the `DatatransWebhookProperties` binding style
    - Bind `integrations.datatrans.reconciliation.initial-delay`, `poll-interval`, `max-duration` as `Duration` fields and `enabled` as `boolean`
    - Add Bean Validation annotations for positive duration and a cross-field validator ensuring `maxDuration >= initialDelay`
    - Fail startup with a message naming the offending configuration key when validation fails
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.6, 6.7, 6.8_

  - [x] 2.2 Add reconciliation configuration to `application.yml`
    - Add the four properties under `integrations.datatrans.reconciliation` with environment variable placeholders and defaults: `initial-delay: ${DATATRANS_RECONCILIATION_INITIAL_DELAY:2m}`, `poll-interval: ${DATATRANS_RECONCILIATION_POLL_INTERVAL:30s}`, `max-duration: ${DATATRANS_RECONCILIATION_MAX_DURATION:30m}`, `enabled: ${DATATRANS_RECONCILIATION_ENABLED:true}`
    - _Requirements: 6.1, 6.2, 6.3, 6.4_

  - [x] 2.3 Implement `getTransactionStatus` in `DatatransRestAdapter`
    - Issue `GET /v1/transactions/{transactionId}` with HTTP Basic Auth using the configured merchant ID and password
    - Map 2xx response into `DatatransTransactionStatus` carrying transactionId, status, currency, authorizedAmount, acquirerAuthorizationCode, and card alias
    - Throw `DatatransGatewayException` on absent body or blank status
    - Throw `TransactionNotFoundException` on HTTP 404
    - Throw `DatatransGatewayException` carrying the HTTP status on other errors
    - Throw `ServiceUnavailableException` on host unreachable or read timeout
    - Never log the merchant password, raw response body, or card object
    - _Requirements: 2.2, 2.3, 2.5, 2.6, 2.7, 2.8, 8.7_

  - [x] 2.4 Implement `getTransactionStatus` in `PaymentActivitiesImpl`
    - Delegate to `DatatransOutPort.getTransactionStatus`
    - Register in the same Spring bean wiring used by existing Datatrans activities
    - Do not log the response body or card object
    - _Requirements: 2.4, 2.9, 8.7_

  - [x] 2.5 Write adapter MockWebServer tests for `DatatransRestAdapter.getTransactionStatus`
    - Assert correct `GET /v1/transactions/{transactionId}` path and Basic Auth header
    - Assert mapping from complete authorized/settled response to domain result fields
    - Assert absent body and blank status map to `DatatransGatewayException`
    - Assert HTTP 404 maps to `TransactionNotFoundException`
    - Assert 4xx/5xx maps to `DatatransGatewayException` with HTTP status
    - Assert connection/read failure maps to `ServiceUnavailableException`
    - Assert merchant password and card object are never emitted in logs
    - _Requirements: 10.5_

- [ ] 3. Checkpoint - Ensure adapter and domain compile
  - Ensure all tests pass, ask the user if questions arise.

- [x] 4. Infrastructure: Workflow adapter and settings mapping
  - [x] 4.1 Update `MobileSdkWorkflowAdapter` to pass reconciliation settings
    - Inject `DatatransReconciliationProperties`
    - Convert `Duration` values to milliseconds and create `MobileSdkReconciliationSettings`
    - Include settings in `InitMobileSdkCommand` for Update-With-Start
    - _Requirements: 6.9_

  - [x] 4.2 Write Spring context and startup validation tests
    - Verify binding of `2m`, `30s`, `30m`, and `true` from `application.yml`
    - Verify environment/property overrides work
    - Parameterized tests: zero/negative initial-delay, poll-interval, max-duration each fail startup
    - Parameterized test: max-duration shorter than initial-delay fails startup
    - Assert exception messages contain the full offending property key
    - Verify `getTransactionStatus` is exposed through `PaymentActivitiesImpl` registration on `payment-workflows`
    - _Requirements: 10.6, 10.7_

- [x] 5. Workflow implementation: Reconciliation loop
  - [x] 5.1 Add workflow state fields and version marker
    - Add `initCompletedAtMillis`, `reconciliationSettings`, `pollAttemptCount` workflow instance fields
    - Add `Workflow.getVersion("CTECH-12128-mobile-sdk-reconciliation", Workflow.DEFAULT_VERSION, 1)` call
    - Create a dedicated activity stub/options for the status activity: start-to-close 5s, max attempts 2, retry interval 1s, backoffCoefficient 1.0, non-retryable `TransactionNotFoundException`
    - Use `Workflow.getLogger` for all reconciliation logging
    - _Requirements: 9.1, 9.2, 9.5_

  - [x] 5.2 Implement the reconciliation polling loop in `run(...)`
    - After init completes with a non-blank transactionId, record `initCompletedAtMillis`
    - If settings are null or `enabled=false`, await webhook-only (legacy/disabled branch)
    - Calculate deadline, firstPollAt, and nextPollAt from initCompletedAtMillis
    - Use `Workflow.await(Duration, condition)` for initial delay and poll intervals
    - Check `paymentStatus` is still `INITIALIZED` before and after each timer/activity yield
    - Stop polling if a terminal status is observed at any check point
    - Call `statusActivities.getTransactionStatus(transactionId)` once per poll interval
    - Log basket ID, transaction ID, and poll attempt number at INFO on each poll
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 6.5, 8.1, 9.1, 9.3, 9.4_

  - [x] 5.3 Implement status classification and outcome handling
    - Normalize status (trim + lowercase)
    - `authorized`/`settled` → shared `authorize(...)` path
    - `canceled` → `CANCELLED`; stop polling
    - `failed` → `FAILED`; stop polling
    - `TransactionNotFoundException` → `FAILED`; stop polling
    - In-flight (`initialized`, `pending`, `processing`) → keep `INITIALIZED`; schedule next poll
    - Unknown non-blank value → log at WARN; keep `INITIALIZED`; schedule next poll
    - Discard stale result if `paymentStatus != INITIALIZED` after activity returns
    - Discard result if `result.transactionId != stored transactionId`; log mismatch at WARN
    - `ServiceUnavailableException`/`DatatransGatewayException` → keep `INITIALIZED`; schedule next poll; log WARN
    - _Requirements: 3.1, 3.2, 3.8, 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 7.1, 7.2, 7.5, 8.2, 8.3_

  - [x] 5.4 Implement shared `authorize(...)` path for exactly-once publication
    - Generalize the existing `authorize(...)` method to accept a common authorization outcome
    - Check `paymentStatus == INITIALIZED` and `!authorizationInProgress` guard
    - Set `authorizationInProgress = true`, store cardAlias/authorizedAmount/acquirerAuthorizationCode/currency
    - Call `publishAuthorisedPaymentEvent` before setting `AUTHORIZED`
    - Release `authorizationInProgress` in finally block
    - Ensure both webhook and poll paths use this single method
    - _Requirements: 3.3, 3.4, 3.5, 3.6, 3.7_

  - [x] 5.5 Implement expiry logic
    - When deadline is reached and `paymentStatus == INITIALIZED`, set `EXPIRED`
    - Log basket ID, transaction ID, and total poll attempts at WARN
    - Complete `run(...)` without throwing a workflow failure
    - Do not invoke `publishAuthorisedPaymentEvent`
    - Late webhook signals after EXPIRED are ignored (existing guard admits only `INITIALIZED`)
    - _Requirements: 5.2, 5.3, 5.4, 5.5, 5.6, 7.3, 8.4_

  - [x] 5.6 Ensure determinism: no wall-clock, no direct HTTP, temporal timers only
    - Verify no `System.currentTimeMillis`, `Instant.now`, `Thread.sleep` in reconciliation paths
    - Use `Workflow.currentTimeMillis()` for time readings
    - Use `Workflow.sleep` / `Workflow.await` for all waiting
    - All Datatrans reads go through the status activity
    - _Requirements: 9.1, 9.2, 9.3_

- [x] 6. Checkpoint - Ensure workflow compiles and basic tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 7. Temporal workflow tests
  - [x] 7.1 Write TestWorkflowExtension test: no poll before initial delay
    - Initialise successfully, advance virtual time to just before two minutes, send authorized webhook, assert zero `getTransactionStatus` calls
    - Also test no-init case: verify no blank transactionId reaches the activity
    - _Requirements: 10.1_

  - [x] 7.2 Write TestWorkflowExtension test: authorized poll outcome
    - Return matching `authorized` status after initial delay
    - Assert `AUTHORIZED`, stored outcome fields, one `publishAuthorisedPaymentEvent` call, and completed `run(...)`
    - Also test `settled` uses the same path
    - _Requirements: 10.2_

  - [x] 7.3 Write TestWorkflowExtension test: webhook/poll race - exactly once publication
    - Send authorized webhook and return authorized poll result for the same transaction
    - Assert `publishAuthorisedPaymentEvent` called exactly once regardless of ordering
    - Test both orderings: webhook-first and poll-first
    - _Requirements: 10.3_

  - [x] 7.4 Write TestWorkflowExtension test: repeated failures until deadline → EXPIRED
    - Return `ServiceUnavailableException`/`DatatransGatewayException` for every poll until the deadline
    - Assert `EXPIRED`, successful workflow completion, zero publication, no external side effect
    - _Requirements: 10.4_

  - [x] 7.5 Write TestWorkflowExtension tests: non-authorised outcomes and edge cases
    - `canceled` → `CANCELLED`, stop polling
    - `failed` → `FAILED`, stop polling
    - `TransactionNotFoundException` → `FAILED`, stop polling
    - In-flight statuses continue polling at fixed interval
    - Transaction ID mismatch → status unchanged, poll continues
    - Late webhook after `EXPIRED` → status remains `EXPIRED`
    - Disabled configuration: no poll, open workflow until webhook
    - _Requirements: 4.1, 4.2, 4.6, 5.6, 6.5_

- [x] 8. Property-based tests (jqwik)
  - [x] 8.1 Write property test for Property 1: Polling never precedes a valid initialisation window
    - **Property 1: Polling never precedes a valid initialisation window**
    - Generate valid reconciliation settings and workflow states (no init, pre-delay, terminal-before-delay)
    - Assert zero status-activity calls in all pre-condition scenarios
    - **Validates: Requirements 1.1, 1.2, 1.3, 1.5, 1.6, 9.1**

  - [x] 8.2 Write property test for Property 2: Polling follows a fixed cadence and bounded deadline
    - **Property 2: Polling follows a fixed cadence and bounded deadline**
    - Generate valid positive initial delay, poll interval, and max duration tuples
    - Assert polls occur only at the configured cadence and no poll at or after the deadline
    - Assert `EXPIRED` when still `INITIALIZED` at deadline
    - **Validates: Requirements 1.4, 5.2, 7.3, 9.4**

  - [x] 8.3 Write property test for Property 3: Authorisation is path-independent and idempotent
    - **Property 3: Authorisation is path-independent and idempotent**
    - Generate sequences of equivalent authorized/settled webhook and poll outcomes
    - Assert same stored authorisation state and at-most-once publication regardless of path
    - **Validates: Requirements 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 4.3**

  - [x] 8.4 Write property test for Property 4: Stale or mismatched poll results cannot mutate state
    - **Property 4: Stale or mismatched poll results cannot mutate workflow state**
    - Generate status results with non-matching transaction IDs or results arriving after terminal
    - Assert PaymentStatus, stored authorisation data, and publication count are unchanged
    - **Validates: Requirements 3.8, 7.6**

  - [x] 8.5 Write property test for Property 5: Status classification preserves terminal semantics
    - **Property 5: Status classification preserves the intended terminal semantics**
    - Generate all recognized and unrecognized status strings
    - Assert correct mapping: authorized/settled → authorize path, canceled → CANCELLED, failed → FAILED, in-flight/unknown → INITIALIZED
    - **Validates: Requirements 4.1, 4.2, 4.3, 4.4, 4.5, 4.6**

  - [x] 8.6 Write property test for Property 6: Expiry is terminal with no authorisation side effect
    - **Property 6: Expiry is terminal and has no authorisation side effect**
    - Generate sequences of non-terminal/retryable outcomes reaching the deadline
    - Assert `EXPIRED`, completed run without failure, zero publication, and later webhooks ignored
    - **Validates: Requirements 5.2, 5.3, 5.4, 5.6, 7.1, 7.2, 7.5**

  - [x] 8.7 Write property test for Property 7: Poll failures are contained within reconciliation
    - **Property 7: Poll failures are contained within reconciliation**
    - Generate sequences of `ServiceUnavailableException`, `DatatransGatewayException`, and unexpected failures
    - Assert `INITIALIZED` preserved, next poll scheduled, and failure never propagates out of `run(...)`
    - **Validates: Requirements 7.1, 7.2, 7.3, 7.5**

- [ ] 9. Final checkpoint - Ensure all tests pass and quality gates met
  - Ensure all tests pass, ask the user if questions arise.
  - Run `checkstyle:check` with zero warnings for all changed files
  - Verify JaCoCo line coverage ≥ 80% for each new non-excluded class

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Property tests validate universal correctness properties from the design document using jqwik (≥100 tries)
- Temporal workflow tests use `TestWorkflowExtension` with virtual time — no real 30-minute waits
- Adapter tests use `MockWebServer` with the service's real `RestClient` construction
- The implementation must remain Mobile SDK-only; no Secure Fields changes
- Sandbox confirmation of the exact Datatrans status response fields is a pre-merge gate for the adapter fixtures

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2"] },
    { "id": 1, "tasks": ["1.3", "1.4", "1.5"] },
    { "id": 2, "tasks": ["2.1", "2.2"] },
    { "id": 3, "tasks": ["2.3", "2.4"] },
    { "id": 4, "tasks": ["2.5", "4.1"] },
    { "id": 5, "tasks": ["4.2", "5.1"] },
    { "id": 6, "tasks": ["5.2"] },
    { "id": 7, "tasks": ["5.3", "5.4"] },
    { "id": 8, "tasks": ["5.5", "5.6"] },
    { "id": 9, "tasks": ["7.1", "7.2", "7.3", "7.4", "7.5"] },
    { "id": 10, "tasks": ["8.1", "8.2", "8.3", "8.4", "8.5", "8.6", "8.7"] }
  ]
}
```
