---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-11215
---
# Implementation Plan: Secure Fields Datatrans Integration

## Overview

Replace the Phase 1 stub (501 Not Implemented) on `POST /api/payments/secure-fields` with a complete Secure Fields payment session initialization flow. This involves Temporal workflow orchestration (signal-with-start pattern), real Datatrans v1 API integration (`POST /v1/transactions/secureFields` with Basic Auth), stub adapters for Basket Service and Payment Method Entity Service, domain exception handling, and property-based testing with jqwik.

## Tasks

- [x] 1. Domain Models and Exceptions
  - [x] 1.1 Create domain model records
    - Create `Reservation` record in `domain/model/` with fields: basketId, hotelId, baseAmount, donationAmount, currencyCode, bookingReference, paymentCompleted
    - Create `PaymentMethodValidationResult` record with fields: cardPaymentAvailable, availableCardBrands
    - Create `DatatransSecureFieldsRequest` record with fields: amount (long), currency, returnUrl, autoSettle (boolean)
    - Create `DatatransSecureFieldsResponse` record with field: transactionId
    - Create `SecureFieldsInitResult` record with fields: transactionId, success, errorCode, errorMessage
    - Create `InitSecureFieldsSignal` record with field: basketId
    - _Requirements: 1.2, 1.5, 3.3, 6.1, 6.2, 8.1_

  - [x] 1.2 Create domain exceptions
    - Create `BasketNotFoundException` extending `RuntimeException` in `domain/exceptions/`
    - Create `BookingAlreadyPaidException` extending `RuntimeException`
    - Create `BookingAlreadyConfirmedException` extending `RuntimeException`
    - Create `PaymentMethodNotAvailableException` extending `RuntimeException`
    - Create `DatatransGatewayException` extending `RuntimeException`
    - Create `ServiceUnavailableException` extending `RuntimeException`
    - _Requirements: 4.2, 4.3, 5.1, 5.2, 5.3, 5.4, 5.5_

  - [x] 1.3 Create AmountCalculator utility
    - Create `AmountCalculator` class in `domain/logic/` with currency exponent map (GBP/EUR/USD/CHF → 2, JPY/KRW → 0)
    - Implement `toMinorUnits(BigDecimal amount, String currencyCode)` method using `movePointRight` and `longValueExact`
    - Implement `calculateTotal(BigDecimal baseAmount, BigDecimal donationAmount, String currencyCode)` that sums and converts
    - _Requirements: 8.2, 8.3_

- [x] 2. Port Interfaces and Stub Adapters
  - [x] 2.1 Create and update secondary port interfaces
    - Create `PaymentMethodOutPort` interface in `domain/ports/secondary/` with `validatePaymentMethods(String hotelId)` method
    - Update existing `BasketOutPort` to add `getReservation(String basketId)` method returning `Reservation`
    - Update existing `DatatransOutPort` to add `initSecureFields(DatatransSecureFieldsRequest request)` method returning String
    - Create `PaymentWorkflowPort` interface in `domain/ports/secondary/` with `initSecureFieldsPayment(String basketId)` returning `SecureFieldsInitResult`
    - _Requirements: 2.1, 2.2, 3.1_

  - [x] 2.2 Implement BasketStubAdapter
    - Create `BasketStubAdapter` in `infrastructure/rest/client/basket/` implementing `BasketOutPort`
    - Annotate with `@Component` and `@Profile("!basket-real")`
    - Return hardcoded `Reservation` (hotelId "HOTEL-001", baseAmount 86.00, donationAmount 0.00, currency "GBP", no booking ref, not paid)
    - Throw `BasketNotFoundException` for null or blank basketId
    - _Requirements: 1.3_

  - [x] 2.3 Implement PaymentMethodStubAdapter
    - Create `PaymentMethodStubAdapter` in `infrastructure/rest/client/` implementing `PaymentMethodOutPort`
    - Annotate with `@Component` and `@Profile("!pmes-real")`
    - Always return `PaymentMethodValidationResult(true, List.of("VIS", "ECA", "AMX"))`
    - _Requirements: 2.2, 2.3_

- [x] 3. Datatrans REST Adapter (Real Integration)
  - [x] 3.1 Create DatatransProperties configuration class
    - Create `DatatransProperties` in `infrastructure/config/` with `@ConfigurationProperties(prefix = "integrations.datatrans")`
    - Define fields: baseUrl, merchantId, merchantPassword, returnUrl, connectTimeout, readTimeout
    - _Requirements: 3.2_

  - [x] 3.2 Implement DatatransRestAdapter
    - Create `DatatransRestAdapter` in `infrastructure/rest/client/datatrans/` implementing `DatatransOutPort`
    - Use `WebClient` to call `POST /v1/transactions/secureFields`
    - Set HTTP Basic Auth header with `merchantId:merchantPassword`
    - Send request body with amount, currency, returnUrl, autoSettle=false
    - Parse response to extract `transactionId`
    - Map non-2xx responses to `DatatransGatewayException`
    - Map `WebClientRequestException` to `ServiceUnavailableException`
    - _Requirements: 3.1, 3.2, 3.4, 3.5, 3.6, 7.4_

  - [x] 3.3 Configure WebClient bean for Datatrans
    - Add WebClient bean in `InfrastructureBeanConfig` (or a new `DatatransWebClientConfig`)
    - Set base URL from `DatatransProperties`
    - Configure connect timeout and read timeout from properties
    - _Requirements: 3.2_

  - [x] 3.4 Add Datatrans configuration to application.yml
    - Add `integrations.datatrans` section with base-url, merchant-id, merchant-password (env vars), return-url, connect-timeout, read-timeout
    - Add local/test profile overrides with sandbox URL
    - _Requirements: 3.2, 7.4_

- [x] 4. Checkpoint - Domain and adapters compile
  - Ensure all tests pass, ask the user if questions arise.

- [x] 5. Temporal Workflow Implementation
  - [x] 5.1 Update PaymentWorkflow interface (or create PaymentWorkflow)
    - Update `SecureFieldsPaymentWorkflow` interface (or rename to `PaymentWorkflow`) with `@WorkflowInterface`
    - Add `@WorkflowMethod void run(String basketId)`
    - Add `@SignalMethod void initSecureFields(InitSecureFieldsSignal signal)`
    - Add `@QueryMethod SecureFieldsInitResult getLastInitResult()`
    - _Requirements: 1.1, 1.5_

  - [x] 5.2 Update PaymentActivities interface
    - Update existing `PaymentActivities` interface with `@ActivityInterface`
    - Add `@ActivityMethod Reservation getReservation(String basketId)`
    - Add `@ActivityMethod PaymentMethodValidationResult validatePaymentMethods(String hotelId)`
    - Add `@ActivityMethod String initDatatransSecureFields(DatatransSecureFieldsRequest request)`
    - _Requirements: 1.3, 2.2, 3.1_

  - [x] 5.3 Implement PaymentActivitiesImpl
    - Create `PaymentActivitiesImpl` in `infrastructure/temporal/` implementing `PaymentActivities`
    - Inject `BasketOutPort`, `PaymentMethodOutPort`, `DatatransOutPort`
    - Delegate each activity method to the corresponding port
    - _Requirements: 1.3, 2.2, 3.1_

  - [x] 5.4 Implement PaymentWorkflowImpl
    - Update `SecureFieldsPaymentWorkflowImpl` to implement the full workflow signal handler
    - Configure `ActivityOptions` with startToCloseTimeout=30s and retry max 3 attempts
    - In `initSecureFields` handler: get reservation → validate booking state → validate payment methods → calculate amount → call Datatrans
    - Set `lastInitResult` on success or failure with appropriate error codes
    - In `run()` method: block with `Workflow.await(() -> false)` to keep workflow open
    - _Requirements: 1.3, 1.4, 2.1, 3.3, 4.1, 4.2, 4.3, 8.2, 8.3_

  - [x] 5.5 Implement TemporalWorkflowAdapter
    - Create `TemporalWorkflowAdapter` in `infrastructure/temporal/` implementing `PaymentWorkflowPort`
    - Use Temporal client's `signalWithStart` to start or signal existing workflow (workflowId = `payment-{basketId}`)
    - After signaling, use query (`getLastInitResult`) with polling/await to get the result synchronously
    - Map workflow result error codes to domain exceptions
    - _Requirements: 1.1, 1.5, 1.6_

  - [x] 5.6 Register workflow and activities with Temporal worker
    - Update `TemporalConfig` to register `PaymentWorkflowImpl` and `PaymentActivitiesImpl` with the worker
    - Configure task queue name in `TemporalProperties` (or application.yml)
    - _Requirements: 1.1_

- [x] 6. Domain Service and Controller
  - [x] 6.1 Create SecureFieldsInitService
    - Create `SecureFieldsInitService` in `domain/logic/` implementing `PaymentOrchestrationInPort` (or a new `SecureFieldsInPort`)
    - Inject `PaymentWorkflowPort`
    - Implement `initSecureFieldsPayment(String basketId)` that delegates to the workflow port
    - Read `SecureFieldsInitResult`, throw domain exceptions based on errorCode if not successful
    - Return transactionId on success
    - _Requirements: 1.1, 1.5_

  - [x] 6.2 Update PaymentController for secure-fields
    - Update existing `PaymentController` to replace 501 stub with real implementation
    - Wire `SecureFieldsInitService` (or `PaymentOrchestrationInPort`)
    - Accept `@Valid @RequestBody SecureFieldsInitRequest` with basketId
    - Return `ResponseEntity.status(201).body(new SecureFieldsInitResponse(transactionId))`
    - _Requirements: 1.1, 1.2, 1.5, 6.1, 6.3, 6.4_

  - [x] 6.3 Implement GlobalExceptionHandler
    - Create `GlobalExceptionHandler` with `@RestControllerAdvice` in `infrastructure/rest/controller/`
    - Map `MethodArgumentNotValidException` → 400 `INVALID_REQUEST`
    - Map `BasketNotFoundException` → 404 `BASKET_NOT_FOUND`
    - Map `BookingAlreadyPaidException` → 409 `BOOKING_ALREADY_PAID`
    - Map `BookingAlreadyConfirmedException` → 409 `BOOKING_ALREADY_CONFIRMED`
    - Map `PaymentMethodNotAvailableException` → 422 `PAYMENT_METHOD_NOT_AVAILABLE`
    - Map `DatatransGatewayException` → 502 `GATEWAY_ERROR`
    - Map `ServiceUnavailableException` → 503 `SERVICE_UNAVAILABLE`
    - Return error body as `{"error": {"code": "...", "message": "..."}}`
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6_

- [x] 7. Checkpoint - End-to-end flow compiles and wires correctly
  - Ensure all tests pass, ask the user if questions arise.

- [x] 8. Unit Tests
  - [x] 8.1 Write unit tests for AmountCalculator
    - Test GBP conversion: 86.00 → 8600, 0.00 → 0
    - Test JPY conversion: 1000 → 1000 (no decimal shift)
    - Test combined total: baseAmount + donationAmount
    - Test edge cases: zero amounts, large values
    - _Requirements: 8.2, 8.3_

  - [x] 8.2 Write unit tests for SecureFieldsInitService
    - Test happy path: workflow returns success → return transactionId
    - Test each error code mapping: BOOKING_ALREADY_PAID → exception, BOOKING_ALREADY_CONFIRMED → exception, PAYMENT_METHOD_NOT_AVAILABLE → exception, GATEWAY_ERROR → exception
    - Mock `PaymentWorkflowPort` with Mockito
    - _Requirements: 1.1, 1.5, 4.1, 4.2, 4.3_

  - [x] 8.3 Write unit tests for PaymentWorkflowImpl
    - Test happy path with mocked activities returning valid data
    - Test booking already paid → sets error result, no Datatrans call
    - Test booking already confirmed → sets error result, no Datatrans call
    - Test payment method not available → sets error result, no Datatrans call
    - Test Datatrans exception → sets GATEWAY_ERROR result
    - Use Temporal's `TestWorkflowExtension` for workflow unit testing
    - _Requirements: 1.3, 1.4, 2.1, 4.1, 4.2, 4.3_

  - [x] 8.4 Write unit tests for DatatransRestAdapter
    - Test successful response parsing (transactionId extracted)
    - Test 4xx/5xx response → DatatransGatewayException thrown
    - Test connection error → ServiceUnavailableException thrown
    - Test Basic Auth header is correctly set
    - Use MockWebServer or WireMock for HTTP mocking
    - _Requirements: 3.1, 3.2, 3.5, 3.6_

  - [x] 8.5 Write unit tests for GlobalExceptionHandler
    - Test each exception type → correct HTTP status and error code
    - Verify error response body format: `{"error": {"code": "...", "message": "..."}}`
    - Use MockMvc to test controller advice
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6_

- [x] 9. Property-Based Tests (jqwik)
  - [x] 9.1 Write property test for input validation
    - **Property 1: Input validation rejects invalid basket IDs**
    - Generate blank, null, whitespace-only strings; verify 400 Bad Request with INVALID_REQUEST
    - Verify no workflow is started or signaled for invalid inputs
    - **Validates: Requirements 1.2, 5.1, 6.1, 6.4**

  - [x] 9.2 Write property test for booking state validation
    - **Property 2: Booking state validation prevents duplicate payments**
    - Generate `Reservation` records with random combinations of paymentCompleted=true and bookingReference!=null
    - Verify 409 Conflict returned and no Datatrans activity executed
    - **Validates: Requirements 1.4, 4.1, 4.2, 4.3**

  - [x] 9.3 Write property test for payment method validation
    - **Property 3: Payment method validation gates Datatrans calls**
    - Generate hotel configs where cardPaymentAvailable=false
    - Verify 422 Unprocessable Entity with PAYMENT_METHOD_NOT_AVAILABLE and no Datatrans call
    - **Validates: Requirements 2.1, 2.4, 5.3**

  - [x] 9.4 Write property test for amount calculation
    - **Property 4: Amount calculation correctness**
    - Generate random BigDecimal pairs (baseAmount, donationAmount) and currency codes
    - Verify output equals `(baseAmount + donationAmount) × 10^exponent`
    - Use `@ForAll BigDecimal` with constraints for non-negative values
    - **Validates: Requirements 3.3, 8.2, 8.3**

  - [x] 9.5 Write property test for transactionId passthrough
    - **Property 5: TransactionId passthrough integrity**
    - Generate random alphanumeric strings as transactionId
    - Verify the value returned to frontend is character-for-character identical to Datatrans response
    - **Validates: Requirements 3.5, 6.2, 7.5**

  - [x] 9.6 Write property test for error response format
    - **Property 6: Error response format consistency**
    - Generate all error conditions (each exception type)
    - Verify response body always contains `error.code` (non-null String) and `error.message` (non-null String)
    - **Validates: Requirements 5.6**

  - [x] 9.7 Write property test for downstream error mapping
    - **Property 7: Downstream error mapping**
    - Generate various non-2xx HTTP status codes from Datatrans; verify 502 GATEWAY_ERROR
    - Generate connection timeouts/refusals; verify 503 SERVICE_UNAVAILABLE
    - **Validates: Requirements 3.6, 5.4, 5.5**

  - [x] 9.8 Write property test for workflow idempotency
    - **Property 8: Temporal workflow idempotency**
    - Generate random basketIds, invoke initSecureFieldsPayment multiple times
    - Verify exactly one workflow instance exists per basketId (mock Temporal client)
    - **Validates: Requirements 1.5**



## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Property tests validate universal correctness properties from the design document (8 properties)
- Unit tests validate specific examples and edge cases
- The service already has Phase 1 scaffolding (501 stubs); this spec replaces the secure-fields stub with real logic
- Basket Service and Payment Method Entity Service remain stubs (profile-gated for future real integration)
- Datatrans integration is real — requires sandbox credentials for local/integration testing
- Temporal TestWorkflowEnvironment is used for workflow testing without a running Temporal server

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "1.3"] },
    { "id": 1, "tasks": ["2.1", "3.1"] },
    { "id": 2, "tasks": ["2.2", "2.3", "3.2", "3.3", "3.4"] },
    { "id": 3, "tasks": ["5.1", "5.2"] },
    { "id": 4, "tasks": ["5.3", "5.4", "5.5"] },
    { "id": 5, "tasks": ["5.6", "6.1"] },
    { "id": 6, "tasks": ["6.2", "6.3"] },
    { "id": 7, "tasks": ["8.1", "8.2", "8.3", "8.4", "8.5"] },
    { "id": 8, "tasks": ["9.1", "9.2", "9.3", "9.4", "9.5", "9.6", "9.7", "9.8"] }
  ]
}
```
