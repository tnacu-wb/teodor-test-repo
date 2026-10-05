---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-12098
---
# Implementation Plan: Mobile SDK Webhook API

## Overview

Implement the Datatrans Mobile SDK webhook (`POST /api/payments/webhooks/mobile-sdk`) and reshape
the Mobile SDK Temporal workflow so it stays open, receives the webhook as a signal, and moves
the payment from `INITIALIZED` to `AUTHORIZED`. Publishing `AuthorisedPaymentEvent` to Kafka and
backup reconciliation for lost webhooks are both out of scope — this plan only adds a documented
placeholder invocation point for the event. Work spans the REST layer, a new primary port +
signature validator, the Datatrans client (webhook URL), and the Temporal workflow/adapter, with
unit tests throughout.

All paths are relative to
`backend/book-pay/services/payment-orchestration-service/src/main/java/uk/co/whitbread/payment/orchestrator/`.

## Tasks

- [x] 1. Configuration and webhook payload model
  - [x] 1.1 Add `DatatransWebhookProperties`
    - New `infrastructure/config/DatatransWebhookProperties.java` with
      `@ConfigurationProperties(prefix = "integrations.datatrans.webhook")`
    - Fields: `callbackBaseUrl`, `hmacKey`, `validationEnabled` (default `true`)
    - Add the `integrations.datatrans.webhook` block to `src/main/resources/application.yml`
      (env vars `DATATRANS_WEBHOOK_BASE_URL`, `DATATRANS_WEBHOOK_HMAC_KEY`,
      `DATATRANS_WEBHOOK_VALIDATION_ENABLED`)
    - _Requirements: 3.4, 3.5, 4.2_

  - [x] 1.2 Extend `MobileSdkWebhookRequest`
    - Modify `domain/model/payment/in/MobileSdkWebhookRequest.java`
    - Add `refno`, `paymentMethod`, nested `card` (`alias`, `masked`, `expiryMonth`,
      `expiryYear`, `info` with brand/type/usage/country/issuer), and `attempts`
      (with `acquirerAuthorizationCode`)
    - Keep record semantics so unknown fields are ignored
    - _Requirements: 2.1, 2.2_

- [x] 2. Webhook signature validation
  - [x] 2.1 Implement `WebhookSignatureValidator`
    - New `infrastructure/rest/controller/payment/WebhookSignatureValidator.java` (`@Component`)
    - Parse `Datatrans-Signature` (`t=`, `s0=`); compute HMAC-SHA256 over `timestamp + rawBody`
      with the hex-decoded key; constant-time compare (`MessageDigest.isEqual`)
    - Return `true` when `validationEnabled == false` (documented non-prod escape hatch)
    - Return `false` when the header or key is missing/malformed
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_

- [x] 3. Primary port for webhook handling
  - [x] 3.1 Add `WebhookInPort` interface
    - New `domain/ports/primary/WebhookInPort.java` with
      `void handleMobileSdkWebhook(String basketId, MobileSdkWebhookRequest payload)`
    - _Requirements: 1.1, 4.1_

  - [x] 3.2 Implement `WebhookInPortImpl`
    - New `domain/logic/WebhookInPortImpl.java` (no Spring annotations)
    - Ignore (warn + return) when `basketId` is null/blank; otherwise delegate to
      `MobileSdkWorkflowPort.signalWebhookReceived(basketId, payload)`
    - Wire the bean in `infrastructure/config/InfrastructureBeanConfig.java`
    - _Requirements: 4.1, 4.3, 9.4_

- [x] 4. Secondary port + Datatrans client changes
  - [x] 4.1 Extend `MobileSdkWorkflowPort`
    - Modify `domain/ports/secondary/MobileSdkWorkflowPort.java`
    - Add `void signalWebhookReceived(String basketId, MobileSdkWebhookRequest payload)`
    - _Requirements: 4.1, 5.4_

  - [x] 4.2 Add `webhookUrl` to the Datatrans v2 init request
    - Modify `domain/model/DatatransMobileSdkRequest.java` to include `webhookUrl`
    - Set `webhook.url` in the v2 init call within `DatatransRestAdapter.initMobileSdk(...)`
    - _Requirements: 4.2_

- [x] 5. Reshape the Mobile SDK workflow to be long-lived
  - [x] 5.1 Update `MobileSdkPaymentWorkflow` interface
    - Modify `domain/workflow/MobileSdkPaymentWorkflow.java`
    - Replace `@WorkflowMethod String processPayment(String)` with
      `@WorkflowMethod void run(String basketId)`
    - Add `@UpdateMethod MobileSdkInitResult initMobileSdk(InitMobileSdkCommand command)`
    - Change the signal to `@SignalMethod void webhookReceived(MobileSdkWebhookRequest payload)`
    - Add `@QueryMethod PaymentStatus getPaymentStatus()` and
      `@QueryMethod MobileSdkInitResult getMobileSdkInitResult()`
    - Add `InitMobileSdkCommand` (in `domain/model`) carrying the callback webhook URL
    - _Requirements: 5.1, 5.3, 5.4, 5.5_

  - [x] 5.2 Rework `MobileSdkPaymentWorkflowImpl` state + init
    - Modify `domain/workflow/MobileSdkPaymentWorkflowImpl.java`
    - Add `@WorkflowInit` constructor storing `basketId`; add state fields
      (`paymentStatus`, `transactionId`, `bookingReference`, `amount`, `currency`,
      `reservationId`, `cardAlias`, `authorizedAmount`, `acquirerAuthorizationCode`,
      `initializationInProgress`)
    - Move the existing reservation → payment-method → amount → Datatrans-init logic into
      `initMobileSdk(...)`, storing state and building the webhook URL
      (`callbackBaseUrl` + `/mobile-sdk?basketId={basketId}`)
    - Implement `run(...)` to await a terminal state (`AUTHORIZED`/`FAILED`/`CANCELLED` for this
      ticket; no backup timer, no `EXPIRED`)
    - _Requirements: 5.1, 5.2, 5.6_

  - [x] 5.3 Implement `webhookReceived` transition logic
    - Guard: act only when `paymentStatus == INITIALIZED` (duplicate/late webhooks ignored)
    - Guard: `payload.transactionId()` must equal the stored `transactionId` (mismatch → ignore)
    - `authorized` → store `cardAlias`/`authorizedAmount`/`acquirerAuthorizationCode`, set
      `AUTHORIZED`, then call the publish placeholder (task 6.1)
    - `canceled` → `CANCELLED`; any other terminal status → `FAILED`
    - _Requirements: 4.4, 4.5, 6.1, 6.2, 6.3, 6.4, 6.5, 6.6_

- [x] 6. AuthorisedPaymentEvent seam + activities
  - [x] 6.1 Add the publish placeholder activity
    - Add `publishAuthorisedPaymentEvent(...)` to `domain/workflow/PaymentActivities.java`
    - Implement in `infrastructure/temporal/PaymentActivitiesImpl.java` as a logging no-op with a
      `TODO` referencing the follow-up Kafka ticket (document event name + available data)
    - _Requirements: 7.1, 7.2, 7.3, 7.4_

- [x] 7. Temporal adapter changes
  - [x] 7.1 Update `MobileSdkWorkflowAdapter`
    - Modify `infrastructure/temporal/MobileSdkWorkflowAdapter.java`
    - Change workflow ID to `payment-{basketId}`; remove the 30s execution timeout; use
      `WorkflowIdConflictPolicy.USE_EXISTING`
    - Implement init via `executeUpdateWithStart(initMobileSdk, command, run)` mirroring
      `TemporalWorkflowAdapter`
    - Implement `signalWebhookReceived(...)`: typed stub for `payment-{basketId}`, send the
      `webhookReceived` signal; catch `WorkflowNotFoundException`, log a warning, and swallow
    - _Requirements: 4.3, 5.3, 9.5_

  - [x] 7.2 Update `MobileSdkWorkflowPortStub`
    - Modify `infrastructure/temporal/MobileSdkWorkflowPortStub.java` to match the new port
      signature (add no-op `signalWebhookReceived`)
    - _Requirements: 4.1_

- [x] 8. Wire the controller
  - [x] 8.1 Replace the `501` placeholder in `WebhookController`
    - Modify `infrastructure/rest/controller/payment/WebhookController.java`
    - Inject `WebhookSignatureValidator`, `WebhookInPort`, `ObjectMapper`
    - Accept `@RequestBody byte[] rawBody`, `@RequestParam basketId`,
      `@RequestHeader Datatrans-Signature`
    - Validate signature → `401` on failure; deserialize; delegate to the port;
      return `200 {"status":"received"}`
    - Update OpenAPI annotations for `200`/`401`/`400`
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.6, 9.1, 9.2, 9.3_

- [x] 9. Unit tests
  - [x] 9.1 `WebhookSignatureValidatorTest`
    - Valid signature (precomputed HMAC), invalid, missing header, missing key, disabled flag
    - _Requirements: 10.2_

  - [x] 9.2 `WebhookControllerTest`
    - `200` valid; `401` invalid/missing signature; `400` malformed body; `200` unresolved
      workflow (blank basketId) with no progression
    - _Requirements: 10.1_

  - [x] 9.3 `WebhookInPortImplTest`
    - Delegates to the workflow port; blank/null basketId is ignored
    - _Requirements: 10.1_

  - [x] 9.4 `MobileSdkPaymentWorkflowImplTest`
    - Init keeps the workflow open; `authorized` → `AUTHORIZED`; duplicate/late webhook → no
      re-authorise; `failed` → `FAILED`; `canceled` → `CANCELLED`; `transactionId` mismatch →
      no authorisation; publish placeholder invoked exactly once on authorisation
    - Temporal `TestWorkflowExtension` with mocked activities
    - _Requirements: 10.3_

  - [x] 9.5 `MobileSdkWorkflowAdapterTest`
    - Init via Update-With-Start on `payment-{basketId}`; webhook signal sent;
      `WorkflowNotFoundException` swallowed (no throw)
    - _Requirements: 10.1, 10.3_

  - [x] 9.6 Verify build and coverage
    - Run `cd backend && ./mvnw test -pl book-pay/services/payment-orchestration-service`
    - Fix any failures; confirm JaCoCo >= 80% on new/modified non-excluded classes
    - _Requirements: 10.4, 10.5_

## Notes

- **Reference implementation:** `SecureFieldsPaymentWorkflowImpl` + `TemporalWorkflowAdapter` are
  the pattern to follow for a long-lived workflow (Update-With-Start, `@WorkflowInit`, `run`
  awaiting terminal state, status query). Mirror them for consistency.
- **Raw body for HMAC:** the controller must verify against the exact bytes Datatrans signed;
  deserialize only after validation (accept `byte[]`), since re-serialising the parsed model
  would not reproduce the signed bytes.
- **Datatrans does not retry** on non-2xx, so always return `200` for accepted/duplicate/
  unresolved cases and reserve non-2xx for genuine rejects (`401` bad signature, `400` malformed).
- **Kafka is out of scope.** Only the `publishAuthorisedPaymentEvent` placeholder is added; the
  follow-up ticket implements the real publication and the event schema/topic.
- **Backup reconciliation is out of scope.** If a webhook is lost, the workflow stays open in
  `INITIALIZED`; the future reconciliation process (mechanism TBD) handles it. Do not add a timer,
  Datatrans status polling, or an `EXPIRED` transition in this ticket.
- **Workflow ID = `payment-{basketId}`** (same as Secure Fields, one workflow per basket). No ID
  migration is planned; a basket is paid through a single channel.
- Confirm the remaining Open Questions in `design.md` (v2 webhook URL support and the exact
  `Datatrans-Signature` format) before/at implementation time.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "3.1", "4.1", "4.2", "5.1"] },
    { "id": 1, "tasks": ["2.1", "3.2", "6.1", "5.2"] },
    { "id": 2, "tasks": ["5.3", "7.1", "7.2"] },
    { "id": 3, "tasks": ["8.1"] },
    { "id": 4, "tasks": ["9.1", "9.2", "9.3", "9.4", "9.5"] },
    { "id": 5, "tasks": ["9.6"] }
  ]
}
```
