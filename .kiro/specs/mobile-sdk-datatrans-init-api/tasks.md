# Implementation Plan: Mobile SDK Datatrans Init API

## Overview

Implement the `POST /api/payments/mobile-sdk` endpoint in the Payment Orchestration Service. The endpoint accepts a basket ID from the native mobile app, orchestrates validation via a Temporal workflow (with stubbed basket and payment method services), calls Datatrans v2 (`POST /v2/transactions`) to create a payment session, and returns the transaction ID to the mobile app. All code follows the existing hexagonal architecture, uses Spring WebFlux (fully reactive), and leverages Temporal SDK 1.35.0 for workflow orchestration.

## Tasks

- [x] 1. Create domain models and port interfaces
  - [x] 1.1 Create `DatatransMobileSdkRequest` and `DatatransMobileSdkResponse` records
    - Create `DatatransMobileSdkRequest` record with fields: `amount` (long), `currency` (String), `refno` (String), `paymentMethods` (List<String>)
    - Create `DatatransMobileSdkResponse` record with field: `transactionId` (String)
    - Place in `uk.co.whitbread.payment.orchestrator.domain.model`
    - _Requirements: 5.1, 5.2, 5.3, 5.4_

  - [x] 1.2 Create `MobileSdkInitResult` record and `Reservation` record
    - Create `MobileSdkInitResult` record with fields: `success` (boolean), `transactionId` (String), `errorCode` (String), `errorMessage` (String)
    - Create `Reservation` record with fields: `basketId`, `hotelId`, `baseAmount` (long), `currencyCode`, `bookingReference`, `paymentCompleted` (boolean), `refno`
    - Place in `uk.co.whitbread.payment.orchestrator.domain.model`
    - _Requirements: 2.1, 2.2, 2.3, 3.1, 3.2_

  - [x] 1.3 Add `initMobileSdk` method to `DatatransOutPort`
    - Add `Mono<String> initMobileSdk(DatatransMobileSdkRequest request)` to the existing `DatatransOutPort` interface
    - Include Javadoc specifying reactive contract (no `.block()`)
    - _Requirements: 5.1, 10.1_

  - [x] 1.4 Create `MobileSdkWorkflowPort` interface
    - Create a new secondary port interface in `uk.co.whitbread.payment.orchestrator.domain.ports.secondary`
    - Define method: `MobileSdkInitResult initMobileSdkPayment(String basketId)`
    - _Requirements: 7.1, 7.2_

  - [x] 1.5 Add `merchantId` field to `DatatransProperties`
    - Add `private String merchantId;` field to the existing `DatatransProperties` configuration class
    - _Requirements: 9.1, 9.2_

- [x] 2. Implement Temporal workflow activities
  - [x] 2.1 Extend `PaymentActivities` interface with new activity methods
    - Add `@ActivityMethod Reservation getReservation(String basketId)`
    - Add `@ActivityMethod List<String> getPaymentMethods(String hotelId)`
    - Add `@ActivityMethod String initMobileSdkTransaction(DatatransMobileSdkRequest request)`
    - _Requirements: 2.1, 4.1, 5.1, 7.3_

  - [x] 2.2 Implement `getReservation` stub in `PaymentActivitiesImpl`
    - Return a hardcoded `Reservation` with valid data: hotelId, baseAmount (8600), currencyCode ("GBP"), paymentCompleted=false, bookingReference=null
    - _Requirements: 2.2, 2.3, 3.4_

  - [x] 2.3 Implement `getPaymentMethods` stub in `PaymentActivitiesImpl`
    - Return a hardcoded list: `List.of("VIS", "ECA")`
    - _Requirements: 4.4_

  - [x] 2.4 Implement `initMobileSdkTransaction` activity in `PaymentActivitiesImpl`
    - Inject `DatatransOutPort` and call `initMobileSdk(request).block()` (blocking is allowed inside Temporal activity threads)
    - Map exceptions: `DatatransGatewayException` → rethrow, `ServiceUnavailableException` → rethrow
    - _Requirements: 5.1, 5.5, 5.6, 5.7_

- [x] 3. Implement Datatrans v2 REST adapter
  - [x] 3.1 Implement `initMobileSdk` in `DatatransRestAdapter`
    - Use `datatransWebClient.post()` to `/v2/transactions`
    - Set HTTP Basic Auth header with `properties.getMerchantId()` and `properties.getMerchantPassword()`
    - Send request body with `amount`, `currency`, `refno`, `paymentMethods` — no `autoSettle`, no `createAlias`, no redirect URLs
    - Parse response body to `DatatransMobileSdkResponse` and extract `transactionId`
    - Map error status codes to `DatatransGatewayException`
    - Map `WebClientRequestException` to `ServiceUnavailableException`
    - Return `Mono<String>` — no `.block()` call
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7, 9.1, 10.1_

- [x] 4. Checkpoint - Ensure domain models, ports, and adapter compile
  - Ensure all tests pass, ask the user if questions arise.

- [x] 5. Implement Temporal workflow logic
  - [x] 5.1 Implement `MobileSdkPaymentWorkflowImpl.processPayment`
    - Configure activity stub with `startToCloseTimeout(30s)` and `maxAttempts(3)`
    - Call `activities.getReservation(basketId)` to retrieve reservation
    - Validate: if `reservation.paymentCompleted()` is true, throw `ApplicationFailure` with type `BOOKING_ALREADY_PAID`
    - Validate: if `reservation.bookingReference()` is non-null, throw `ApplicationFailure` with type `BOOKING_ALREADY_CONFIRMED`
    - Call `activities.getPaymentMethods(reservation.hotelId())`
    - Validate: if payment methods list is empty, throw `ApplicationFailure` with type `PAYMENT_METHOD_NOT_AVAILABLE`
    - Build `DatatransMobileSdkRequest` with reservation data and call `activities.initMobileSdkTransaction(request)`
    - Return the transaction ID
    - _Requirements: 2.1, 3.1, 3.2, 3.3, 4.1, 4.2, 4.3, 5.1, 7.3_

  - [x] 5.2 Implement `MobileSdkWorkflowAdapter`
    - Create class implementing `MobileSdkWorkflowPort`, annotated with `@Component`
    - Inject `WorkflowClient` and task queue name
    - Build `WorkflowOptions` with workflow ID `"mobile-sdk-" + basketId`, task queue, and 30s execution timeout
    - Create workflow stub and call `processPayment(basketId)`
    - On success: return `MobileSdkInitResult(true, transactionId, null, null)`
    - On `WorkflowException`: extract `ApplicationFailure` type and message, return `MobileSdkInitResult(false, null, errorCode, errorMessage)`
    - _Requirements: 7.1, 7.2, 7.3, 7.4_

- [x] 6. Wire domain logic and controller
  - [x] 6.1 Implement `initMobileSdkPayment` in `PaymentOrchestrationInPortImpl`
    - Inject `MobileSdkWorkflowPort` (add to constructor)
    - Call `mobileSdkWorkflowPort.initMobileSdkPayment(basketId)`
    - If result is not successful, call existing `mapErrorToException` to throw the appropriate domain exception
    - Return `result.transactionId()` on success
    - _Requirements: 1.2, 3.1, 3.2, 4.3, 5.6, 5.7, 6.1, 8.1, 8.2_

  - [x] 6.2 Create `MobileSdkInitRequest` and `MobileSdkInitResponse` DTOs
    - Create `MobileSdkInitRequest` record with `@NotBlank String basketId`
    - Create `MobileSdkInitResponse` record with `String transactionId`
    - Place in the application DTO package
    - _Requirements: 1.1, 1.3, 6.1, 6.2_

  - [x] 6.3 Update `PaymentController` endpoint for Mobile SDK
    - Change `POST /api/payments/mobile-sdk` to return `Mono<ResponseEntity<MobileSdkInitResponse>>`
    - Accept `@Valid @RequestBody MobileSdkInitRequest request`
    - Use `Mono.fromCallable(() -> paymentOrchestrationInPort.initMobileSdkPayment(request.basketId())).subscribeOn(Schedulers.boundedElastic())`
    - Map successful result to `ResponseEntity.status(201).body(new MobileSdkInitResponse(transactionId))`
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 6.1, 6.2, 10.2, 10.5_

  - [x] 6.4 Update `GlobalExceptionHandler` for Mobile SDK error cases
    - Verify all domain exceptions (`BasketNotFoundException`, `BookingAlreadyPaidException`, `BookingAlreadyConfirmedException`, `PaymentMethodNotAvailableException`, `DatatransGatewayException`, `ServiceUnavailableException`) are mapped to correct HTTP status codes and error response format
    - Add any missing mappings if not already present
    - _Requirements: 8.1, 8.2, 8.3, 8.4_

  - [x] 6.5 Register `MobileSdkWorkflowPort` bean in `InfrastructureBeanConfig`
    - Wire `MobileSdkWorkflowPort` into `PaymentOrchestrationInPortImpl` constructor
    - Ensure Temporal worker registers `MobileSdkPaymentWorkflowImpl` and the updated `PaymentActivitiesImpl`
    - _Requirements: 7.1_

- [x] 7. Checkpoint - Ensure full flow compiles and existing tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 8. Unit tests
  - [x] 8.1 Write unit tests for `PaymentController` Mobile SDK endpoint
    - Use `WebTestClient` to test:
    - Valid request → 201 Created with transactionId
    - Blank basketId → 400 Bad Request with INVALID_REQUEST
    - Missing basketId field → 400 Bad Request with INVALID_REQUEST
    - Invalid JSON → 400 Bad Request
    - Domain throws `DatatransGatewayException` → 502 with GATEWAY_ERROR
    - Domain throws `ServiceUnavailableException` → 503 with SERVICE_UNAVAILABLE
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 5.6, 5.7, 8.1, 8.2, 10.2_

  - [x] 8.2 Write unit tests for `PaymentOrchestrationInPortImpl.initMobileSdkPayment`
    - Use Mockito to mock `MobileSdkWorkflowPort`
    - Test successful flow returns transactionId
    - Test error code `BOOKING_ALREADY_PAID` throws `BookingAlreadyPaidException`
    - Test error code `BOOKING_ALREADY_CONFIRMED` throws `BookingAlreadyConfirmedException`
    - Test error code `PAYMENT_METHOD_NOT_AVAILABLE` throws `PaymentMethodNotAvailableException`
    - Test error code `GATEWAY_ERROR` throws `DatatransGatewayException`
    - Test error code `SERVICE_UNAVAILABLE` throws `ServiceUnavailableException`
    - _Requirements: 3.1, 3.2, 4.3, 5.6, 5.7, 6.1, 8.2_

  - [x] 8.3 Write unit tests for `MobileSdkPaymentWorkflowImpl` using Temporal TestWorkflowEnvironment
    - Test happy path: stub activities return valid data → workflow returns transactionId
    - Test reservation with paymentCompleted=true → workflow fails with BOOKING_ALREADY_PAID
    - Test reservation with non-null bookingReference → workflow fails with BOOKING_ALREADY_CONFIRMED
    - Test empty payment methods list → workflow fails with PAYMENT_METHOD_NOT_AVAILABLE
    - Test initMobileSdkTransaction activity throws exception → workflow fails with appropriate error
    - _Requirements: 2.1, 3.1, 3.2, 4.2, 4.3, 5.1, 7.3_

  - [x] 8.4 Write unit tests for `DatatransRestAdapter.initMobileSdk` using MockWebServer
    - Test successful 201 response → returns Mono with transactionId
    - Test 400 error response → emits DatatransGatewayException
    - Test 500 error response → emits DatatransGatewayException
    - Test connection refused → emits ServiceUnavailableException
    - Verify request body contains correct fields (amount, currency, refno, paymentMethods)
    - Verify request has Basic Auth header with merchant credentials
    - _Requirements: 5.1, 5.5, 5.6, 5.7, 9.1, 10.1_

- [x] 9. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- No property-based tests are included as the design has no Correctness Properties section — this feature is primarily REST wiring, workflow orchestration, and HTTP client integration with no pure algorithmic logic warranting PBT.
- Basket Service and Payment Method Entity Service are stubbed with hardcoded data; real integrations follow in future phases.
- The Datatrans v2 request deliberately excludes `autoSettle`, `createAlias`, and redirect URLs per design.
- `Mono.fromCallable` + `Schedulers.boundedElastic()` bridges the blocking Temporal SDK call without blocking the WebFlux event loop.
- Each task references specific requirements for traceability.
- Checkpoints ensure incremental validation.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "1.4", "1.5"] },
    { "id": 1, "tasks": ["1.3", "2.1"] },
    { "id": 2, "tasks": ["2.2", "2.3", "2.4", "3.1"] },
    { "id": 3, "tasks": ["5.1", "5.2"] },
    { "id": 4, "tasks": ["6.1", "6.2"] },
    { "id": 5, "tasks": ["6.3", "6.4", "6.5"] },
    { "id": 6, "tasks": ["8.1", "8.2", "8.3", "8.4"] }
  ]
}
```
