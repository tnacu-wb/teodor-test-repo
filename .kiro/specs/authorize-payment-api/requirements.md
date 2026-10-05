# Requirements Document

## Introduction

This specification defines the Authorize Payment API (`POST /api/payments/authorize`) for the web Secure Fields flow. The endpoint is called by the Premier Inn web frontend after the user has entered card details via Datatrans Secure Fields and completed 3-D Secure authentication. Upon invocation, the Payment Orchestration Service validates the request, authorizes the payment with Datatrans, posts deposit folios to OPERA, confirms the booking, settles the payment, and updates the basket.

This phase implements the full orchestration flow with the following scoping:
- **Datatrans authorize and settle**: Real integration via existing `DatatransOutPort`.
- **OPERA deposit folio posting**: Placeholder (returns success, TODO for real integration).
- **Basket Async Order Processor / Basket Confirmation Processor**: Placeholder (skipped, returns success).
- **Basket update after settlement**: Placeholder (skipped, returns success).

Testing scope: Unit tests only, minimum 80% code coverage.

## Glossary

- **Payment_Orchestrator**: The backend microservice (`payment-orchestration-service`) that coordinates the payment flow between the web frontend, Datatrans, OPERA, Basket, and downstream services.
- **Datatrans_Gateway**: The Datatrans payment gateway accessed via `POST /v1/transactions/{transactionId}/authorize` to authorize a Secure Fields payment, and `POST /v1/transactions/{transactionId}/settle` for deferred settlement.
- **Web_Frontend**: The Premier Inn web application that invokes the Authorize API after the user completes card entry and 3-D Secure authentication.
- **Temporal_Workflow**: The Temporal-based workflow (`SecureFieldsPaymentWorkflow`) that orchestrates the authorize-settle sequence as Temporal activities.
- **OPERA**: The OHIP property management system that receives deposit/folio postings. (Stubbed in this phase.)
- **Basket_Service**: The service holding reservation/basket state. Used to verify the basket exists. (Update operation stubbed in this phase.)
- **Basket_Async_Order_Processor**: The service that processes booking confirmation events asynchronously. (Stubbed in this phase.)
- **Basket_Confirmation_Processor**: The service that finalises and confirms the booking after payment succeeds. (Stubbed in this phase.)
- **Transaction_ID**: The Datatrans transaction identifier (numeric string format, e.g. `"190410112056083383"`) returned during Secure Fields initialisation.
- **Basket_ID**: A string identifier for a reservation/basket (e.g. `bsk-a1b2c3d4-e5f6-7890`).
- **Payment_Status**: The lifecycle state of a payment: INITIALIZED, AUTHORIZED, SETTLED, or FAILED.

## Requirements

### Requirement 1: Accept Authorize Payment Request

**User Story:** As a web frontend developer, I want to send a transaction ID and basket ID to the Authorize API, so that the backend can finalize payment authorization after 3-D Secure completion.

#### Acceptance Criteria

1. THE Payment_Orchestrator SHALL expose a `POST /api/payments/authorize` endpoint that accepts an `application/json` request body containing `transactionId` (string, maximum 64 characters) and `basketId` (string, maximum 64 characters) fields.
2. WHEN a request is received with a well-formed JSON body containing non-blank `transactionId` and non-blank `basketId`, THE Payment_Orchestrator SHALL call the Datatrans gateway to finalize authorization for the given transaction and return an HTTP `204 No Content` response with no response body upon success.
3. IF the `transactionId` field is missing, null, empty, or contains only whitespace, THEN THE Payment_Orchestrator SHALL return an HTTP `400 Bad Request` response with error code `INVALID_REQUEST`.
4. IF the `basketId` field is missing, null, empty, or contains only whitespace, THEN THE Payment_Orchestrator SHALL return an HTTP `400 Bad Request` response with error code `INVALID_REQUEST`.
5. IF the request body is not valid JSON, THEN THE Payment_Orchestrator SHALL return an HTTP `400 Bad Request` response with error code `INVALID_REQUEST`.
6. IF the request `Content-Type` header is not `application/json`, THEN THE Payment_Orchestrator SHALL return an HTTP `415 Unsupported Media Type` response.
7. IF the Datatrans gateway indicates the card issuer declined the transaction, THEN THE Payment_Orchestrator SHALL return an HTTP `401 Unauthorized` response with error code `AUTHORIZATION_DECLINED`.
8. IF the Datatrans gateway indicates the transaction ID is not found or has expired, THEN THE Payment_Orchestrator SHALL return an HTTP `404 Not Found` response with error code `TRANSACTION_NOT_FOUND`.
9. IF the Datatrans gateway returns an HTTP 4xx or 5xx error not covered by a specific error code, THEN THE Payment_Orchestrator SHALL return an HTTP `502 Bad Gateway` response with error code `GATEWAY_ERROR`.

---

### Requirement 2: Validate Existing Workflow for Transaction

**User Story:** As the payment system, I want to verify that a workflow exists for the given transaction before attempting authorization, so that orphan or expired transactions are rejected early.

#### Acceptance Criteria

1. WHEN an authorize request is received, THE Payment_Orchestrator SHALL verify that an active Temporal_Workflow exists for the given Basket_ID, where "active" is defined as a workflow in RUNNING state whose last recorded Payment_Status is INITIALIZED.
2. IF no Temporal_Workflow is found for the given Basket_ID, THEN THE Payment_Orchestrator SHALL return an HTTP `404 Not Found` response with error code `TRANSACTION_NOT_FOUND`.
3. IF the Temporal_Workflow found for the Basket_ID has a Payment_Status of AUTHORIZED or SETTLED, THEN THE Payment_Orchestrator SHALL return an HTTP `409 Conflict` response with error code `TRANSACTION_ALREADY_AUTHORIZED`.
4. IF the Temporal_Workflow found for the Basket_ID has a Payment_Status of FAILED, THEN THE Payment_Orchestrator SHALL return an HTTP `404 Not Found` response with error code `TRANSACTION_NOT_FOUND`.

---

### Requirement 3: Authorize Payment via Datatrans

**User Story:** As the payment system, I want to call the Datatrans API to authorize the transaction, so that the card issuer approves the payment amount.

#### Acceptance Criteria

1. WHEN the workflow validation passes, THE Temporal_Workflow SHALL invoke the `authorizeTransaction` activity to call the Datatrans_Gateway at `POST /v1/transactions/{transactionId}/authorize` with a start-to-close timeout of 30 seconds and a maximum of 3 retry attempts for transient failures.
2. WHEN the Datatrans_Gateway returns an HTTP 200 response with a status of `authorized` and a non-null `acquirerAuthorizationCode`, THE Temporal_Workflow SHALL transition the Payment_Status to AUTHORIZED and SHALL extract the `acquirerAuthorizationCode` and `card.alias` token from the response.
3. IF the Datatrans_Gateway returns an error indicating the card issuer declined the transaction, THEN THE Payment_Orchestrator SHALL return an HTTP `401 Unauthorized` response with error code `AUTHORIZATION_DECLINED` and SHALL transition Payment_Status to FAILED.
4. IF the Datatrans_Gateway returns an error indicating 3-D Secure authentication failed, THEN THE Payment_Orchestrator SHALL return an HTTP `422 Unprocessable Entity` response with error code `3DS_AUTHENTICATION_FAILED` and SHALL transition Payment_Status to FAILED.
5. IF the Datatrans_Gateway returns an unexpected HTTP 4xx or 5xx error not covered by criteria 3 or 4, THEN THE Payment_Orchestrator SHALL return an HTTP `502 Bad Gateway` response with error code `GATEWAY_ERROR` and SHALL transition Payment_Status to FAILED.
6. IF the Datatrans_Gateway does not establish a connection within the configured timeout or the connection is refused, THEN THE Payment_Orchestrator SHALL return an HTTP `502 Bad Gateway` response with error code `GATEWAY_ERROR` and SHALL transition Payment_Status to FAILED.

---

### Requirement 4: Post Deposit Folios to OPERA (Placeholder)

**User Story:** As the payment system, I want to post deposit folios to OPERA after successful authorization, so that the property management system records the payment.

#### Acceptance Criteria

1. WHEN authorization is successful (Payment_Status is AUTHORIZED), THE Temporal_Workflow SHALL invoke the `postDeposit` activity, passing the reservation ID and Transaction_ID as arguments.
2. IN this phase, THE `postDeposit` activity SHALL be implemented as a placeholder that logs the invocation at INFO level including the reservation ID and Transaction_ID, and returns success without calling the real OPERA API.
3. THE placeholder implementation SHALL include a `// TODO: Replace with real OPERA deposit folio integration` comment at the entry point of the method body.
4. IF the `postDeposit` activity throws an unexpected error, THEN THE Temporal_Workflow SHALL propagate the failure and THE Payment_Orchestrator SHALL return an HTTP `502 Bad Gateway` response with error code `GATEWAY_ERROR`.

---

### Requirement 5: Confirm Booking via Basket Processors (Placeholder)

**User Story:** As the payment system, I want to trigger booking confirmation after OPERA deposit posting, so that the reservation is confirmed before settlement.

#### Acceptance Criteria

1. WHEN deposit folio posting completes successfully, THE Temporal_Workflow SHALL invoke a `confirmBooking` activity to put a booking confirmation event to the Basket_Async_Order_Processor and wait for a confirmation signal from the Basket_Confirmation_Processor.
2. IN this phase, THE `confirmBooking` activity SHALL be implemented as a placeholder that logs the invocation at INFO level including the Basket_ID and Transaction_ID, and returns success immediately without sending a real event or waiting for a real confirmation.
3. THE placeholder implementation SHALL include a `// TODO: Replace with real Basket Async Order Processor and Basket Confirmation Processor integration` comment at the entry point of the method body.
4. IF the `confirmBooking` activity throws an unexpected error, THEN THE Temporal_Workflow SHALL propagate the failure and THE Payment_Orchestrator SHALL return an HTTP `502 Bad Gateway` response with error code `GATEWAY_ERROR`.

---

### Requirement 6: Settle Payment via Datatrans

**User Story:** As the payment system, I want to settle the payment after booking confirmation, so that the authorized funds are captured from the cardholder's account.

#### Acceptance Criteria

1. WHEN booking confirmation completes successfully, THE Temporal_Workflow SHALL invoke a `settleTransaction` activity to call the Datatrans_Gateway at `POST /v1/transactions/{transactionId}/settle` for deferred settlement, configured with a start-to-close timeout of 30 seconds and a maximum of 3 retry attempts for transient failures.
2. WHEN the Datatrans_Gateway returns an HTTP 200 response indicating successful settlement, THE Temporal_Workflow SHALL transition the Payment_Status to SETTLED.
3. IF the Datatrans_Gateway returns an HTTP 4xx or 5xx error during settlement, THEN THE Temporal_Workflow SHALL transition Payment_Status to FAILED and THE Payment_Orchestrator SHALL return an HTTP `502 Bad Gateway` response with error code `GATEWAY_ERROR`.
4. IF the Datatrans_Gateway does not establish a connection within the configured timeout or the connection is refused during settlement, THEN THE Temporal_Workflow SHALL transition Payment_Status to FAILED and THE Payment_Orchestrator SHALL return an HTTP `502 Bad Gateway` response with error code `GATEWAY_ERROR`.

---

### Requirement 7: Update Basket After Settlement (Placeholder)

**User Story:** As the payment system, I want to update the basket with the payment result after settlement, so that the basket state reflects the completed payment.

#### Acceptance Criteria

1. WHEN settlement completes successfully (Payment_Status is SETTLED), THE Temporal_Workflow SHALL invoke a `updateBasket` activity, passing the Basket_ID, Transaction_ID, settled amount, and currency from the workflow context.
2. THE `updateBasket` activity SHALL be implemented as a placeholder that logs the invocation at INFO level including the Basket_ID and Transaction_ID, and returns a success result without calling the real Basket Service update API.
3. THE placeholder implementation SHALL include a `// TODO: Replace with real basket update integration` comment at the entry point of the method body.
4. IF the `updateBasket` activity throws an unexpected error, THEN THE Temporal_Workflow SHALL propagate the failure to the workflow caller, enabling Temporal's configured retry policy to re-attempt the activity.

---

### Requirement 8: Orchestrate via Temporal Workflow

**User Story:** As the payment system, I want the authorize-settle flow orchestrated through the existing Temporal workflow, so that the payment state is durable and the flow benefits from Temporal's retry and failure handling capabilities.

#### Acceptance Criteria

1. WHEN an authorize request is received, THE Payment_Orchestrator SHALL signal the existing Temporal_Workflow (identified by Basket_ID) to begin the authorization flow.
2. IF no Temporal_Workflow exists for the given Basket_ID when the authorize signal is sent, THEN THE Payment_Orchestrator SHALL return an HTTP `404 Not Found` response with error code `TRANSACTION_NOT_FOUND`.
3. THE Temporal_Workflow SHALL execute authorization, deposit posting, booking confirmation, settlement, and basket update as sequential Temporal activities, where each activity starts only after the preceding activity completes successfully.
4. THE Temporal activities SHALL be configured with a start-to-close timeout of 30 seconds and a maximum of 3 retry attempts for failures classified as transient (network timeouts, connection refused, or HTTP 5xx responses from downstream services). Failures classified as non-transient (HTTP 4xx responses such as authorization declined or validation errors) SHALL NOT be retried.
5. IF a non-transient failure occurs in any activity, THEN THE Temporal_Workflow SHALL terminate the sequence without executing subsequent activities and SHALL propagate the failure to the Payment_Orchestrator for translation into the appropriate HTTP error response.
6. IF all retry attempts are exhausted for a transient failure in any activity, THEN THE Temporal_Workflow SHALL terminate the sequence without executing subsequent activities and THE Payment_Orchestrator SHALL return an HTTP `502 Bad Gateway` response with error code `GATEWAY_ERROR`.
7. IF the Temporal service is unreachable or the workflow signal fails, THEN THE Payment_Orchestrator SHALL return an HTTP `502 Bad Gateway` response with error code `GATEWAY_ERROR`.

---

### Requirement 9: Error Response Format

**User Story:** As a web frontend developer, I want consistent error responses from the Authorize API, so that I can handle failures predictably in the application.

#### Acceptance Criteria

1. WHEN any error occurs, THE Payment_Orchestrator SHALL return a response with `Content-Type: application/json` containing a JSON body with a top-level `error` object that has exactly two fields: `code` (string) and `message` (string).
2. THE `error.code` field SHALL contain one of the defined error codes mapped to the following HTTP status codes: `INVALID_REQUEST` → HTTP 400, `TRANSACTION_NOT_FOUND` → HTTP 404, `BASKET_NOT_FOUND` → HTTP 404, `AUTHORIZATION_DECLINED` → HTTP 401, `TRANSACTION_ALREADY_AUTHORIZED` → HTTP 409, `3DS_AUTHENTICATION_FAILED` → HTTP 422, or `GATEWAY_ERROR` → HTTP 502.
3. THE `error.message` field SHALL contain a human-readable description of the error with a minimum length of 1 character and a maximum length of 500 characters.
4. IF an unexpected error occurs that does not map to one of the defined error codes, THEN THE Payment_Orchestrator SHALL return an HTTP `502 Bad Gateway` response with error code `GATEWAY_ERROR` and a message indicating a transient failure.
5. WHEN any error response is returned, THE Payment_Orchestrator SHALL NOT include any additional fields beyond `code` and `message` within the `error` object and SHALL NOT include any fields outside the `error` object in the response body.

---

### Requirement 10: Unit Test Coverage

**User Story:** As a developer, I want comprehensive unit tests for the authorize flow, so that the implementation is reliable and regressions are caught early.

#### Acceptance Criteria

1. THE Payment_Orchestrator authorize flow SHALL have unit test coverage of at least 80% as measured by JaCoCo line coverage, and THE Maven build SHALL fail if coverage drops below this threshold.
2. THE unit tests SHALL include at least one test per layer: the controller layer (verifying request validation returns HTTP 400 for invalid input and HTTP 204 for a successful authorization), the domain logic layer (verifying orchestration sequence and error-to-HTTP-status mapping), and the workflow activity layer (verifying Datatrans authorize call construction and placeholder behaviour for stubbed services).
3. THE unit tests SHALL use Mockito to mock external dependencies (Datatrans_Gateway, OPERA, Basket_Service, Temporal client) and SHALL NOT require running external services.
4. THE unit tests SHALL verify all defined error scenarios by asserting both the expected HTTP status code and the expected `error.code` value for each: invalid request (400, `INVALID_REQUEST`), transaction not found (404, `TRANSACTION_NOT_FOUND`), authorization declined (401, `AUTHORIZATION_DECLINED`), 3DS authentication failed (422, `3DS_AUTHENTICATION_FAILED`), transaction already authorized (409, `TRANSACTION_ALREADY_AUTHORIZED`), and gateway errors (502, `GATEWAY_ERROR`).
5. THE unit tests SHALL verify the happy path: given a valid `transactionId` and `basketId`, WHEN Datatrans returns a successful authorization response, THEN the controller returns HTTP 204 No Content.
