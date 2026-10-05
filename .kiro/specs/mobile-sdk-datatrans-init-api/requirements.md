# Requirements Document

## Introduction

This specification defines the Init API for Mobile SDK payments using Datatrans v2. The API accepts a basket ID from the mobile application and returns a Datatrans transaction ID that the native iOS/Android SDK uses to start the payment journey. The implementation follows the existing hexagonal architecture and Temporal workflow orchestration pattern established in the Payment Orchestration Service.

This phase focuses on the **Datatrans v2 integration only**. Basket Service, OPERA, and Payment Method Entity Service integrations are stubbed as placeholders to be implemented in future phases.

## Glossary

- **Payment_Orchestrator**: The backend microservice (`payment-orchestration-service`) that coordinates the payment flow between the mobile app, Basket, Datatrans, and downstream services.
- **Datatrans_V2_Gateway**: The Datatrans payment gateway accessed via `POST /v2/transactions` to initialise a Mobile SDK payment session.
- **Mobile_App**: The native iOS or Android Premier Inn application that invokes the Init API and starts the Datatrans Mobile SDK v4.
- **Basket_Service**: The service holding reservation/basket state for the booking being paid. (Stubbed in this phase.)
- **Payment_Method_Entity_Service**: The service that provides and validates available card payment methods for a specific hotel. (Stubbed in this phase.)
- **Temporal_Workflow**: The Temporal-based workflow (`MobileSdkPaymentWorkflow`) that orchestrates the init sequence (validation, payment method check, Datatrans call).
- **Transaction_ID**: A UUID-format identifier returned by Datatrans v2 upon successful session initialisation. Used by the mobile SDK to begin the native payment UI.
- **Basket_ID**: A string identifier for a reservation/basket (e.g. `bsk-a1b2c3d4-e5f6-7890`).

## Requirements

### Requirement 1: Accept Mobile SDK Init Request

**User Story:** As a mobile app developer, I want to send a basket ID to the Init API, so that the backend can initialise a Datatrans payment session for the native SDK.

#### Acceptance Criteria

1. THE Payment_Orchestrator SHALL expose a `POST /api/payments/mobile-sdk` endpoint that accepts an `application/json` request body containing a `basketId` string field.
2. WHEN a request is received with a well-formed JSON body containing a non-blank `basketId`, THE Payment_Orchestrator SHALL return an HTTP `201 Created` response with an `application/json` body containing a `transactionId` string field in UUID format.
3. WHEN the `basketId` field is missing, null, empty, or contains only whitespace in the request body, THE Payment_Orchestrator SHALL return an HTTP `400 Bad Request` response with error code `INVALID_REQUEST`.
4. WHEN the request body is not valid JSON, THE Payment_Orchestrator SHALL return an HTTP `400 Bad Request` response with error code `INVALID_REQUEST`.
5. WHEN the request `Content-Type` header is not `application/json`, THE Payment_Orchestrator SHALL return an HTTP `415 Unsupported Media Type` response.

---

### Requirement 2: Validate Basket Exists (Stub)

**User Story:** As the payment system, I want to verify the basket exists before processing payment, so that invalid basket references are rejected early.

#### Acceptance Criteria

1. WHEN a mobile SDK init request is received, THE Temporal_Workflow SHALL invoke a Temporal activity to retrieve the reservation from the Basket_Service using the provided Basket_ID.
2. IN this phase, THE Basket_Service activity SHALL be implemented as a stub that returns a valid hardcoded reservation for any provided Basket_ID, enabling end-to-end flow testing without a live Basket Service dependency.
3. THE stub SHALL return a Reservation object containing a hardcoded hotel ID, amount (integer in minor currency units), currency code (ISO 4217), and reference number.

---

### Requirement 3: Prevent Duplicate Payment (Stub)

**User Story:** As the payment system, I want to reject payment initialisation for reservations that already have a booking reference or completed payment, so that duplicate charges are prevented.

#### Acceptance Criteria

1. IF the reservation retrieved from the Basket_Service has `paymentCompleted` equal to `true`, THEN THE Payment_Orchestrator SHALL return an HTTP `409 Conflict` response with error code `BOOKING_ALREADY_PAID` and SHALL NOT proceed with Datatrans transaction initialisation.
2. IF the reservation retrieved from the Basket_Service has a non-null `bookingReference`, THEN THE Payment_Orchestrator SHALL return an HTTP `409 Conflict` response with error code `BOOKING_ALREADY_CONFIRMED` and SHALL NOT proceed with Datatrans transaction initialisation.
3. IF the reservation has both `paymentCompleted` equal to `true` and a non-null `bookingReference`, THEN THE Payment_Orchestrator SHALL return the `BOOKING_ALREADY_PAID` error code.
4. IN this phase, THE stub Basket_Service activity SHALL always return a reservation with `paymentCompleted` equal to `false` and `bookingReference` equal to `null`, so the happy path proceeds to Datatrans.

---

### Requirement 4: Validate Payment Methods for Hotel (Stub)

**User Story:** As the payment system, I want to validate that card payment methods are available for the hotel associated with the reservation, so that the payment flow only proceeds when card payment is supported.

#### Acceptance Criteria

1. WHEN the basket reservation is valid, THE Temporal_Workflow SHALL request available card payment methods from the Payment_Method_Entity_Service using the hotel ID extracted from the reservation.
2. WHEN the Payment_Method_Entity_Service returns at least one card payment method, THE Temporal_Workflow SHALL forward the list of card brand codes for use in the Datatrans transaction initialisation.
3. IF the Payment_Method_Entity_Service returns an empty list of card payment methods for the hotel, THEN THE Payment_Orchestrator SHALL return an HTTP `422 Unprocessable Entity` response with error code `PAYMENT_METHOD_NOT_AVAILABLE`.
4. IN this phase, THE Payment_Method_Entity_Service activity SHALL be implemented as a stub that returns a hardcoded list of card brand codes (e.g. `["VIS", "ECA"]`), enabling the Datatrans call without a live dependency.

---

### Requirement 5: Initialise Datatrans v2 Transaction

**User Story:** As the payment system, I want to call the Datatrans v2 API to create a transaction, so that the mobile app receives a transaction ID to start the native SDK payment flow.

#### Acceptance Criteria

1. WHEN all validations pass, THE Temporal_Workflow SHALL call the Datatrans_V2_Gateway at `POST /v2/transactions` with the reservation amount (integer in minor currency units), currency (ISO 4217 3-letter code), reference number, and the list of allowed payment method codes.
2. THE Datatrans_V2_Gateway request SHALL NOT include `option.autoSettle` because it is optional and the default Datatrans behaviour (deferred settlement) is the desired behaviour.
3. THE Datatrans_V2_Gateway request SHALL NOT include `option.createAlias` because tokenisation is not required in this phase.
4. THE Datatrans_V2_Gateway request SHALL NOT include redirect URLs because the Mobile SDK handles 3DS natively in-app.
5. WHEN the Datatrans_V2_Gateway returns an HTTP 201 response, THE Temporal_Workflow SHALL extract the Transaction_ID (UUID string) from the `Location` header or response body.
6. IF the Datatrans_V2_Gateway returns an HTTP 4xx or 5xx error response, THEN THE Payment_Orchestrator SHALL return an HTTP `502 Bad Gateway` response with error code `GATEWAY_ERROR`.
7. IF the Datatrans_V2_Gateway does not establish a connection within the configured timeout or the connection is refused, THEN THE Payment_Orchestrator SHALL return an HTTP `503 Service Unavailable` response with error code `SERVICE_UNAVAILABLE`.

---

### Requirement 6: Return Transaction ID to Mobile App

**User Story:** As a mobile app developer, I want to receive the Datatrans transaction ID after successful initialisation, so that I can start the native payment SDK with it.

#### Acceptance Criteria

1. WHEN the Datatrans_V2_Gateway returns a valid Transaction_ID, THE Payment_Orchestrator SHALL return an HTTP `201 Created` response with Content-Type `application/json` and a JSON body containing the `transactionId` field.
2. THE returned `transactionId` field SHALL contain the Transaction_ID value received from the Datatrans_V2_Gateway without modification, in UUID v4 format (e.g., `"2d49fde3-3f03-4b45-8b3e-a5c1e2f7d8e9"`).
3. IF the Datatrans_V2_Gateway returns an error response or a response that does not contain a valid UUID Transaction_ID, THEN THE Payment_Orchestrator SHALL return an error HTTP response with error code `GATEWAY_ERROR`.

---

### Requirement 7: Orchestrate via Temporal Workflow

**User Story:** As the payment system, I want the init flow orchestrated through a Temporal workflow, so that the payment state is durable and the flow benefits from Temporal's retry and failure handling capabilities.

#### Acceptance Criteria

1. WHEN a mobile SDK init request is received, THE Payment_Orchestrator SHALL delegate processing to the Temporal_Workflow using signal-with-start semantics and SHALL wait synchronously for the workflow result for a maximum of 30 seconds before returning a response to the caller.
2. THE Temporal_Workflow SHALL use a workflow ID derived from the Basket_ID to ensure exactly-once semantics per basket.
3. THE Temporal_Workflow SHALL execute validation and Datatrans calls as Temporal activities configured with a start-to-close timeout of 30 seconds and a maximum of 3 retry attempts for transient failures.
4. IF the Temporal service is unreachable or the synchronous result wait exceeds 30 seconds, THEN THE Payment_Orchestrator SHALL return an HTTP `503 Service Unavailable` response with error code `SERVICE_UNAVAILABLE`.

---

### Requirement 8: Error Response Format

**User Story:** As a mobile app developer, I want consistent error responses from the Init API, so that I can handle failures predictably in the app.

#### Acceptance Criteria

1. WHEN any error occurs, THE Payment_Orchestrator SHALL return a response with `Content-Type: application/json` containing a JSON body with a top-level `error` object that has exactly two fields: `code` (string) and `message` (string).
2. THE `error.code` field SHALL contain one of the defined error codes: `INVALID_REQUEST`, `BASKET_NOT_FOUND`, `BOOKING_ALREADY_PAID`, `BOOKING_ALREADY_CONFIRMED`, `PAYMENT_METHOD_NOT_AVAILABLE`, `GATEWAY_ERROR`, or `SERVICE_UNAVAILABLE`.
3. THE `error.message` field SHALL contain a non-empty, human-readable description of the error with a maximum length of 500 characters.
4. IF an unexpected error occurs that does not map to one of the defined error codes, THEN THE Payment_Orchestrator SHALL return an HTTP `503 Service Unavailable` response with error code `SERVICE_UNAVAILABLE` and a message indicating a transient failure.

---

### Requirement 9: Datatrans v2 Authentication

**User Story:** As the payment system, I want the Datatrans v2 API call to use HTTP Basic Authentication, so that requests are properly authenticated with the merchant credentials.

#### Acceptance Criteria

1. WHEN calling the Datatrans_V2_Gateway, THE Payment_Orchestrator SHALL authenticate using HTTP Basic Authentication with the configured merchant ID as the username and merchant password as the password.
2. THE merchant credentials SHALL be loaded from Spring configuration properties (`DatatransProperties`) and SHALL NOT be hardcoded in application source code.

---

### Requirement 10: Reactive Non-Blocking Implementation

**User Story:** As an operations engineer, I want the Mobile SDK Init API to be fully reactive and non-blocking, so that the service can handle high request volumes efficiently across multiple pods in production.

#### Acceptance Criteria

1. THE Datatrans v2 HTTP client call SHALL use Spring WebClient in a fully non-blocking reactive manner, returning `Mono<T>` throughout the call chain without using `.block()` or any blocking synchronisation.
2. THE REST controller endpoint SHALL return `Mono<ResponseEntity<T>>` to enable the Spring WebFlux event loop to handle requests without thread blocking.
3. THE implementation SHALL NOT use `Thread.sleep()`, synchronised blocks, or any blocking I/O operations on the WebFlux event loop threads.
4. THE service SHALL be horizontally scalable across multiple pods with no shared mutable state between requests, ensuring thread safety for concurrent request handling.
5. WHEN the Temporal workflow port awaits a result, THE implementation SHALL use a non-blocking mechanism (e.g. `Mono.fromCallable` on a bounded elastic scheduler or `Mono.fromFuture`) so that the event loop thread is not blocked while waiting for the Temporal workflow result.
