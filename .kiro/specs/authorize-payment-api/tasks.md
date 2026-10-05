# Implementation Plan: Authorize Payment API

## Overview

Implement the `POST /api/payments/authorize` endpoint for the web Secure Fields flow in the Payment Orchestration Service. The endpoint accepts a `transactionId` and `basketId` from the web frontend after 3-D Secure completion, signals the existing Temporal workflow to execute the authorize → postDeposit → confirmBooking → settle → updateBasket activity chain, and returns HTTP 204 on success. The implementation extends the existing `SecureFieldsPaymentWorkflow` with an authorize signal, adds new activities, introduces authorize-specific domain exceptions, and updates the `GlobalExceptionHandler`. OPERA deposit posting, booking confirmation, and basket update are placeholder implementations.

## Tasks

- [x] 1. Create new domain models and exceptions
  - [x] 1.1 Create authorize-specific domain exception classes
    - Create `AuthorizationDeclinedException` extending `RuntimeException` with a message constructor
    - Create `TransactionNotFoundException` extending `RuntimeException` with a message constructor
    - Create `TransactionAlreadyAuthorizedException` extending `RuntimeException` with a message constructor
    - Create `ThreeDsAuthenticationFailedException` extending `RuntimeException` with a message constructor
    - Place in `uk.co.whitbread.payment.orchestrator.domain.exceptions`
    - Follow existing pattern (e.g., `BasketNotFoundException`)
    - _Requirements: 1.7, 1.8, 2.2, 2.3, 3.3, 3.4, 9.2_

  - [x] 1.2 Create `AuthorizeResult` record
    - Create `AuthorizeResult` record with fields: `success` (boolean), `errorCode` (String), `errorMessage` (String)
    - `AuthorizeSignal` removed — workflow signal uses plain `String transactionId`
    - Place in `uk.co.whitbread.payment.orchestrator.domain.model`
    - _Requirements: 8.1, 8.5_

  - [x] 1.3 Create Datatrans response records for authorize
    - Create `DatatransAuthorizeResponse` record with fields: `transactionId` (String), `status` (String), `acquirerAuthorizationCode` (String), `card` (DatatransCardInfo)
    - Create `DatatransCardInfo` record with fields: `alias` (String), `masked` (String), `expiryMonth` (String), `expiryYear` (String)
    - `DatatransSettleResponse` removed — settle uses `bodyToMono(Void.class)`
    - Place in `uk.co.whitbread.payment.orchestrator.domain.model`
    - _Requirements: 3.2_

- [x] 2. Extend workflow interface and ports
  - [x] 2.1 Extend `SecureFieldsPaymentWorkflow` interface with authorize signal and queries
    - Add `@SignalMethod void authorize(String transactionId)`
    - Add `@QueryMethod PaymentStatus getPaymentStatus()`
    - Add `@QueryMethod AuthorizeResult getAuthorizeResult()`
    - _Requirements: 8.1, 2.1, 2.3_

  - [x] 2.2 Extend `PaymentActivities` interface with new activity methods
    - Add `@ActivityMethod void confirmBooking(String basketId, String transactionId)`
    - Add `@ActivityMethod void settleTransaction(String transactionId)`
    - Add `@ActivityMethod void updateBasket(String basketId, String transactionId, long amount, String currency)`
    - _Requirements: 5.1, 6.1, 7.1_

  - [x] 2.3 Add `settleTransaction` method to `DatatransOutPort`
    - Add `void settleTransaction(String transactionId)` to the existing interface
    - Include Javadoc specifying it calls `POST /v1/transactions/{transactionId}/settle`
    - _Requirements: 6.1_

  - [x] 2.4 Add `signalAuthorize` method to `PaymentWorkflowPort`
    - Add `AuthorizeResult signalAuthorize(String basketId, String transactionId)` to the existing interface
    - Include Javadoc describing the signal-then-query pattern
    - _Requirements: 8.1, 8.2_

- [x] 3. Checkpoint - Ensure interfaces and models compile
  - Ensure all tests pass, ask the user if questions arise.

- [x] 4. Implement workflow and activity logic
  - [x] 4.1 Extend `SecureFieldsPaymentWorkflowImpl` with authorize signal handler
    - Add workflow state fields: `paymentStatus` (initialized to `INITIALIZED`), `authorizeResult`, `amount`, `currency`, `reservationId`
    - Store `amount`, `currency`, and `reservationId` during the existing `initSecureFields` signal handler
    - Implement `authorize(String transactionId)` signal handler:
      - Call `activities.authorizeTransaction(transactionId)` → set `paymentStatus = AUTHORIZED`
      - Call `activities.postDeposit(reservationId, transactionId)`
      - Call `activities.confirmBooking(basketId, transactionId)`
      - Call `activities.settleTransaction(transactionId)` → set `paymentStatus = SETTLED`
      - Call `activities.updateBasket(basketId, transactionId, amount, currency)`
      - Set `authorizeResult = new AuthorizeResult(true, null, null)`
    - On exception: set `paymentStatus = FAILED`, map exception to error code, set `authorizeResult`
    - Configure activity options with `startToCloseTimeout(30s)`, `maxAttempts(3)`, and `doNotRetry` for non-transient exceptions
    - Implement `getPaymentStatus()` and `getAuthorizeResult()` query methods
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 4.1, 4.4, 5.1, 5.4, 6.1, 6.3, 6.4, 7.1, 7.4, 8.3, 8.4, 8.5, 8.6_

  - [x] 4.2 Implement placeholder activities in `PaymentActivitiesImpl`
    - Implement `confirmBooking(basketId, transactionId)`: log at INFO level including basketId and transactionId, include `// TODO: Replace with real Basket Async Order Processor and Basket Confirmation Processor integration` comment, return void
    - Implement `settleTransaction(transactionId)`: delegate to `DatatransOutPort.settleTransaction(transactionId)`
    - Implement `updateBasket(basketId, transactionId, amount, currency)`: log at INFO level including basketId and transactionId, include `// TODO: Replace with real basket update integration` comment, return void
    - Update existing `postDeposit` implementation to log at INFO level and include `// TODO: Replace with real OPERA deposit folio integration` comment
    - _Requirements: 4.2, 4.3, 5.2, 5.3, 6.1, 7.2, 7.3_

  - [x] 4.3 Implement `DatatransRestAdapter.authorizeTransaction` (real integration)
    - Use `datatransWebClient.post()` to `/v1/transactions/{transactionId}/authorize`
    - Set HTTP Basic Auth with `properties.getMerchantId()` and `properties.getMerchantPassword()`
    - Map 401/403 response to `AuthorizationDeclinedException`
    - Map 404 response to `TransactionNotFoundException`
    - Map 422 response to `ThreeDsAuthenticationFailedException`
    - Map other 4xx/5xx to `DatatransGatewayException`
    - Map connection errors to `ServiceUnavailableException`
    - Parse successful response to `DatatransAuthorizeResponse`
    - Use `.block(properties.getReadTimeout())`
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6_

  - [x] 4.4 Implement `DatatransRestAdapter.settleTransaction` (real integration)
    - Use `datatransWebClient.post()` to `/v1/transactions/{transactionId}/settle`
    - Set HTTP Basic Auth with `properties.getMerchantId()` and `properties.getMerchantPassword()`
    - Send request body with `amount` and `currency` (from workflow context via method params or config)
    - Map 4xx/5xx responses to `DatatransGatewayException`
    - Map connection errors to `ServiceUnavailableException`
    - Use `.block(properties.getReadTimeout())`
    - _Requirements: 6.1, 6.2, 6.3, 6.4_

- [x] 5. Implement adapter and domain orchestration
  - [x] 5.1 Implement `TemporalWorkflowAdapter.signalAuthorize`
    - Derive workflow ID: `"payment-" + basketId`
    - Create an untyped workflow stub for the existing workflow
    - Query `getPaymentStatus()` to validate workflow state:
      - No workflow found → return `AuthorizeResult(false, "TRANSACTION_NOT_FOUND", "...")`
      - Status `AUTHORIZED` or `SETTLED` → return `AuthorizeResult(false, "TRANSACTION_ALREADY_AUTHORIZED", "...")`
      - Status `FAILED` → return `AuthorizeResult(false, "TRANSACTION_NOT_FOUND", "...")`
    - Signal `authorize(transactionId)` on the workflow
    - Poll `getAuthorizeResult()` query until non-null (same polling pattern as init flow)
    - Return the `AuthorizeResult`
    - Catch Temporal client exceptions and return `AuthorizeResult(false, "GATEWAY_ERROR", "...")`
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 8.1, 8.2, 8.7_

  - [x] 5.2 Update `PaymentOrchestrationInPortImpl.authorizePayment`
    - Replace `UnsupportedOperationException` with real implementation
    - Call `paymentWorkflowPort.signalAuthorize(basketId, transactionId)`
    - If result is not successful, map error code to domain exception:
      - `AUTHORIZATION_DECLINED` → `AuthorizationDeclinedException`
      - `TRANSACTION_NOT_FOUND` → `TransactionNotFoundException`
      - `TRANSACTION_ALREADY_AUTHORIZED` → `TransactionAlreadyAuthorizedException`
      - `3DS_AUTHENTICATION_FAILED` → `ThreeDsAuthenticationFailedException`
      - `GATEWAY_ERROR` → `DatatransGatewayException`
      - Unknown → `RuntimeException`
    - _Requirements: 1.2, 1.7, 1.8, 1.9, 2.1, 2.2, 2.3, 2.4, 9.2_

- [x] 6. Update controller and exception handler
  - [x] 6.1 Update `PaymentController.authorizePayment` endpoint
    - Change return type from `ResponseEntity<ErrorResponse>` to `ResponseEntity<Void>`
    - Remove the 501 stub and `NOT_IMPLEMENTED_RESPONSE` usage for this endpoint
    - Call `paymentOrchestrationInPort.authorizePayment(request.transactionId(), request.basketId())`
    - Return `ResponseEntity.noContent().build()` on success (HTTP 204)
    - Let exceptions propagate to `GlobalExceptionHandler`
    - Update OpenAPI annotations: change `@ApiResponse(responseCode = "501")` to `@ApiResponse(responseCode = "204")`
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6_

  - [x] 6.2 Extend `GlobalExceptionHandler` with authorize-specific exception handlers
    - Add handler for `AuthorizationDeclinedException` → HTTP 401, code `AUTHORIZATION_DECLINED`
    - Add handler for `TransactionNotFoundException` → HTTP 404, code `TRANSACTION_NOT_FOUND`
    - Add handler for `TransactionAlreadyAuthorizedException` → HTTP 409, code `TRANSACTION_ALREADY_AUTHORIZED`
    - Add handler for `ThreeDsAuthenticationFailedException` → HTTP 422, code `3DS_AUTHENTICATION_FAILED`
    - Ensure response body format: `{"error": {"code": "...", "message": "..."}}` with no additional fields
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5_

- [x] 7. Checkpoint - Ensure full flow compiles and existing tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 8. Unit tests
  - [x] 8.1 Write unit tests for `PaymentController` authorize endpoint (`PaymentControllerAuthorizeTest`)
    - Use `@WebFluxTest(PaymentController.class)` with `@MockitoBean` for `PaymentOrchestrationInPort`
    - Test valid request → HTTP 204, no body
    - Test missing `transactionId` → HTTP 400, code `INVALID_REQUEST`
    - Test missing `basketId` → HTTP 400, code `INVALID_REQUEST`
    - Test blank `transactionId` (whitespace only) → HTTP 400, code `INVALID_REQUEST`
    - Test empty request body → HTTP 400
    - Test `AuthorizationDeclinedException` thrown → HTTP 401, code `AUTHORIZATION_DECLINED`
    - Test `TransactionNotFoundException` thrown → HTTP 404, code `TRANSACTION_NOT_FOUND`
    - Test `TransactionAlreadyAuthorizedException` thrown → HTTP 409, code `TRANSACTION_ALREADY_AUTHORIZED`
    - Test `ThreeDsAuthenticationFailedException` thrown → HTTP 422, code `3DS_AUTHENTICATION_FAILED`
    - Test `DatatransGatewayException` thrown → HTTP 502, code `GATEWAY_ERROR`
    - _Requirements: 10.1, 10.2, 10.4, 10.5_

  - [x] 8.2 Write unit tests for `PaymentOrchestrationInPortImpl.authorizePayment` (`PaymentOrchestrationInPortImplAuthorizeTest`)
    - Use Mockito to mock `PaymentWorkflowPort`
    - Test happy path — `signalAuthorize` returns success → no exception thrown
    - Test error code `AUTHORIZATION_DECLINED` → throws `AuthorizationDeclinedException`
    - Test error code `TRANSACTION_NOT_FOUND` → throws `TransactionNotFoundException`
    - Test error code `TRANSACTION_ALREADY_AUTHORIZED` → throws `TransactionAlreadyAuthorizedException`
    - Test error code `3DS_AUTHENTICATION_FAILED` → throws `ThreeDsAuthenticationFailedException`
    - Test error code `GATEWAY_ERROR` → throws `DatatransGatewayException`
    - Test unknown error code → throws `RuntimeException`
    - _Requirements: 10.1, 10.2_

  - [x] 8.3 Write unit tests for `SecureFieldsPaymentWorkflowImpl` authorize signal (`SecureFieldsPaymentWorkflowAuthorizeTest`)
    - Use `temporal-testing` `TestWorkflowEnvironment` with mocked activities
    - Test happy path — all activities succeed → `paymentStatus` transitions to `SETTLED`, `authorizeResult.success()` is true
    - Test authorize activity fails (declined) → `paymentStatus` = `FAILED`, error code set
    - Test postDeposit activity fails → error propagated, status reflects failure
    - Test settle activity fails → `paymentStatus` = `FAILED`, error code set
    - Test sequential execution order: activities called in order authorize → postDeposit → confirmBooking → settle → updateBasket
    - _Requirements: 10.1, 10.2_

  - [x] 8.4 Write unit tests for `PaymentActivitiesImpl` authorize activities (`PaymentActivitiesImplAuthorizeTest`)
    - Use Mockito to mock `DatatransOutPort`, `OperaOutPort`, `BasketOutPort`
    - Test `authorizeTransaction` — delegates to `DatatransOutPort.authorizeTransaction` with correct transactionId
    - Test `settleTransaction` — delegates to `DatatransOutPort.settleTransaction` with correct transactionId
    - Test `postDeposit` — placeholder logs and returns without exception
    - Test `confirmBooking` — placeholder logs and returns without exception
    - Test `updateBasket` — placeholder logs and returns without exception
    - _Requirements: 10.1, 10.2_

  - [x] 8.5 Write unit tests for `DatatransRestAdapter` authorize/settle methods (`DatatransRestAdapterAuthorizeTest`)
    - Use MockWebServer to simulate Datatrans responses
    - Test `authorizeTransaction` — 200 OK → no exception, response parsed
    - Test `authorizeTransaction` — 401 (declined) → throws `AuthorizationDeclinedException`
    - Test `authorizeTransaction` — 404 (not found) → throws `TransactionNotFoundException`
    - Test `authorizeTransaction` — 500 (server error) → throws `DatatransGatewayException`
    - Test `authorizeTransaction` — connection timeout → throws `ServiceUnavailableException`
    - Test `settleTransaction` — 200 OK → no exception
    - Test `settleTransaction` — 4xx/5xx → throws `DatatransGatewayException`
    - _Requirements: 10.1, 10.2_

  - [x] 8.6 Write unit tests for `TemporalWorkflowAdapter.signalAuthorize` (`TemporalWorkflowAdapterAuthorizeTest`)
    - Use Mockito to mock `WorkflowClient`
    - Test workflow exists, status INITIALIZED → signal succeeds, returns successful `AuthorizeResult`
    - Test no workflow found → returns `AuthorizeResult` with `TRANSACTION_NOT_FOUND`
    - Test workflow status AUTHORIZED → returns `AuthorizeResult` with `TRANSACTION_ALREADY_AUTHORIZED`
    - Test workflow status FAILED → returns `AuthorizeResult` with `TRANSACTION_NOT_FOUND`
    - Test Temporal client unreachable → returns `AuthorizeResult` with `GATEWAY_ERROR`
    - _Requirements: 10.1, 10.2_

- [x] 9. Final checkpoint - Ensure all tests pass and coverage meets 80%
  - Ensure all tests pass, ask the user if questions arise.
  - Verify JaCoCo coverage is at or above 80% line coverage for the authorize flow.

## Notes

- No property-based tests are included — the design explicitly states PBT is not applicable for this orchestration feature.
- OPERA deposit posting, booking confirmation, and basket update are placeholder implementations that log and return success. Real integrations follow in future phases.
- The `authorizeTransaction` activity already exists in `PaymentActivities` interface; its implementation in `PaymentActivitiesImpl` delegates to `DatatransOutPort.authorizeTransaction()`.
- The `postDeposit` activity already exists; its implementation needs updating to add proper INFO logging and the TODO comment.
- `DatatransOutPort.authorizeTransaction()` method signature already exists but needs a real implementation in `DatatransRestAdapter`.
- Temporal activity retry is configured with `doNotRetry` for non-transient exceptions (4xx mapped to domain exceptions) ensuring only transient failures (5xx, timeouts) are retried.
- Each task references specific requirements for traceability.
- Checkpoints ensure incremental validation.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "1.3"] },
    { "id": 1, "tasks": ["2.1", "2.2", "2.3", "2.4"] },
    { "id": 2, "tasks": ["4.1", "4.2", "4.3", "4.4"] },
    { "id": 3, "tasks": ["5.1", "5.2"] },
    { "id": 4, "tasks": ["6.1", "6.2"] },
    { "id": 5, "tasks": ["8.1", "8.2", "8.3", "8.4", "8.5", "8.6"] }
  ]
}
```
