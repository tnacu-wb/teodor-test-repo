---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-12423
---
# Implementation Plan: Payment Method Entity Service Integration (CTECH-12423)

## Overview

This plan replaces the stub `PaymentMethodStubAdapter` with a real REST client integration to the Payment Method Entity Service. The implementation consolidates separate payment method validation and retrieval activities into a single call, adds configuration support, and updates both Secure Fields and Mobile SDK workflows to use the real service for payment method validation and card brand extraction.

## Tasks

- [x] 1. Configuration and properties setup
  - [x] 1.1 Create `PaymentMethodProperties` configuration class
    - Create `@ConfigurationProperties(prefix = "integrations.payment-method-service")` class
    - Add fields: `baseUrl`, `connectTimeout`, `readTimeout` with defaults
    - Add `@PositiveDuration` validation annotations on timeout fields
    - _Requirements: 6.1, 6.2, 6.3, 6.4_

  - [x] 1.2 Add configuration to `application.yml`
    - Add `integrations.payment-method-service` section with base-url, timeouts
    - Set default base-url to `http://localhost:9107`
    - _Requirements: 6.1, 6.2, 6.4_

  - [x] 1.3 Create RestClient bean for Payment Method Entity Service
    - Add `paymentMethodRestClient` bean to `InfrastructureBeanConfig`
    - Use same pattern as `datatransRestClient` and `reservationRestClient`
    - Wire `PaymentMethodProperties` for base URL and timeouts
    - _Requirements: 6.1, 6.2, 6.3_

- [x] 2. External API models for Payment Method Entity Service
  - [x] 2.1 Create `PaymentMethodDto` record
    - Add fields: `paymentProvider` (String), `enabled` (boolean), `acceptedCardTypes` (List<AcceptedCardTypeDto>)
    - Place in `infrastructure/rest/client/paymentmethod` package
    - _Requirements: 2.1, 2.2, 2.4_

  - [x] 2.2 Create `AcceptedCardTypeDto` record
    - Add field: `type` (String)
    - Place in same package as `PaymentMethodDto`
    - _Requirements: 2.4, 2.5_

- [x] 3. Update domain port interface
  - [x] 3.1 Update `PaymentMethodOutPort.validatePaymentMethods` signature
    - Change from `validatePaymentMethods(String hotelId)` 
    - To `validatePaymentMethods(String basketReference, String country, String language, String bookingChannel)`
    - Update JavaDoc to reflect new parameters
    - _Requirements: 1.2, 1.3, 1.4, 1.5, 4.1, 4.2_

- [x] 4. Implement real REST adapter
  - [x] 4.1 Create `PaymentMethodRestAdapter` class
    - Add `@Component @Profile("pmes-real")` annotations
    - Inject `paymentMethodRestClient` via `@Qualifier`
    - Implement `PaymentMethodOutPort` interface
    - _Requirements: 1.1, 7.1_

  - [x] 4.2 Implement `validatePaymentMethods` REST call
    - Build GET request to `/v1/payment-methods` with query parameters
    - Add `basketReference`, `country`, `language`, `userType=LEISURE` as query params
    - Add `bookingChannel` header ("WEB" or "MOBILE")
    - Handle HTTP errors with `onStatus` → `ServiceUnavailableException`
    - Parse response as `PaymentMethodDto[]`
    - _Requirements: 1.2, 1.3, 1.4, 1.5, 1.6, 5.1, 5.2_

  - [x] 4.3 Implement payment method filtering and mapping
    - Filter response for `paymentProvider == "Datatrans"` AND `enabled == true`
    - Extract `acceptedCardTypes[].type` values
    - Map payment method service codes to Datatrans codes (ECA→ECA, VIS→VIS, AMX→AMX, DIN→DIN)
    - Return `PaymentMethodValidationResult(cardPaymentAvailable, mappedCardBrands)`
    - If no enabled Datatrans method found, return `PaymentMethodValidationResult(false, [])`
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6_

  - [x] 4.4 Add error handling and logging
    - Catch `ResourceAccessException` → throw `ServiceUnavailableException`
    - Log successful calls and errors appropriately
    - Never log sensitive data (basket references should be safe for logging)
    - _Requirements: 5.1, 5.2_

- [x] 5. Update stub adapter for new interface
  - [x] 5.1 Update `PaymentMethodStubAdapter.validatePaymentMethods` signature
    - Change signature to match updated port interface
    - Keep `@Profile("!pmes-real")` annotation
    - Continue returning hardcoded `PaymentMethodValidationResult(true, ["VIS", "ECA", "AMX"])`
    - Update log message to include new parameters
    - _Requirements: 7.2, 7.3_

- [x] 6. Update activity interface and implementation
  - [x] 6.1 Replace separate activities with consolidated one
    - Remove `PaymentMethodValidationResult validatePaymentMethods(String hotelId)`
    - Remove `List<String> getPaymentMethods(String hotelId)`
    - Add `PaymentMethodValidationResult validatePaymentMethods(String basketReference, String hotelId, String currency, String bookingChannel)`
    - Update JavaDoc with new parameter descriptions
    - _Requirements: 4.1, 4.2_

  - [x] 6.2 Update `PaymentActivitiesImpl` for consolidated activity
    - Implement new `validatePaymentMethods` activity
    - Derive `country` and `language` from `currency` parameter (GBP→GB/en, EUR→DE/de)
    - Call `PaymentMethodOutPort.validatePaymentMethods` with derived parameters
    - Remove old separate activity implementations
    - _Requirements: 1.3, 4.1, 4.2_

- [x] 7. Update Secure Fields workflow
  - [x] 7.1 Integrate payment method validation in Secure Fields init
    - After `getReservation(basketId)` and before `initDatatransSecureFields`
    - Extract `hotelId` from reservation response
    - Call `activities.validatePaymentMethods(basketId, hotelId, reservation.currency(), "WEB")`
    - Check `result.cardPaymentAvailable()` — fail with `PAYMENT_METHOD_NOT_AVAILABLE` if false
    - Do NOT pass card brands to Datatrans (Secure Fields endpoint doesn't accept them)
    - _Requirements: 3.1, 3.3, 3.5, 4.1, 5.3, 5.4_

  - [x] 7.2 Remove old activity calls from Secure Fields workflow
    - Remove calls to separate `validatePaymentMethods` and `getPaymentMethods` activities
    - Replace with single consolidated call as implemented in 7.1
    - _Requirements: 4.1, 4.3_

- [x] 8. Update Mobile SDK workflow  
  - [x] 8.1 Integrate payment method validation in Mobile SDK init
    - After `getReservation(basketId)` and before `initMobileSdkTransaction`
    - Extract `hotelId` from reservation response
    - Call `activities.validatePaymentMethods(basketId, hotelId, reservation.currency(), "MOBILE")`
    - Check `result.cardPaymentAvailable()` — fail with `PAYMENT_METHOD_NOT_AVAILABLE` if false
    - Pass `result.availableCardBrands()` to Datatrans Mobile SDK init request
    - _Requirements: 3.2, 3.3, 3.4, 4.1, 5.3, 5.4_

  - [x] 8.2 Remove old activity calls from Mobile SDK workflow
    - Remove calls to separate `validatePaymentMethods` and `getPaymentMethods` activities
    - Replace with single consolidated call as implemented in 8.1
    - _Requirements: 4.1, 4.4_

- [x] 9. Testing — Adapter MockWebServer tests
  - [x] 9.1 Test: Payment Method Entity Service success
    - Mock `GET /v1/payment-methods` returning array with enabled Datatrans method
    - Assert correct URI, query parameters (basketReference, country, language, userType), headers (bookingChannel)
    - Assert returned `PaymentMethodValidationResult` has `cardPaymentAvailable=true` and correct card brands
    - Verify card type mapping (ECA→ECA, VIS→VIS, AMX→AMX, DIN→DIN)
    - _Requirements: 1.2, 1.3, 1.4, 1.5, 1.6, 2.1, 2.2, 2.4, 2.5, 2.6_

  - [x] 9.2 Test: No Datatrans payment method available
    - Mock response with no Datatrans entries or all Datatrans entries have `enabled=false`
    - Assert returned `PaymentMethodValidationResult` has `cardPaymentAvailable=false` and empty brands list
    - _Requirements: 2.1, 2.2, 2.3, 2.6_

  - [x] 9.3 Test: Payment Method Entity Service errors
    - Mock 500 error response → assert `ServiceUnavailableException`
    - Mock 400 error response → assert `ServiceUnavailableException`  
    - Mock timeout/unreachable → assert `ServiceUnavailableException`
    - _Requirements: 5.1, 5.2_

  - [x] 9.4 Test: Currency to country/language mapping
    - Test GBP currency → assert country="GB", language="en" in request
    - Test EUR currency → assert country="DE", language="de" in request
    - _Requirements: 1.3_

- [x] 10. Testing — Workflow unit tests
  - [x] 10.1 Test: Payment method validation gates transaction init
    - Mock workflow execution through init phase
    - Assert `validatePaymentMethods` activity called after `getReservation` and before Datatrans init
    - Verify correct parameters passed (basketId, hotelId, currency, bookingChannel)
    - _Requirements: 3.1, 3.2, 3.3, 4.1_

  - [x] 10.2 Test: Unavailable payment method fails workflows
    - Mock `validatePaymentMethods` returning `PaymentMethodValidationResult(false, [])`
    - Assert both Secure Fields and Mobile SDK workflows return `PAYMENT_METHOD_NOT_AVAILABLE` error
    - Assert Datatrans init is NOT called when payment method unavailable
    - _Requirements: 2.3, 5.3, 5.4_

  - [x] 10.3 Test: Mobile SDK uses card brands from validation
    - Mock `validatePaymentMethods` returning available card brands
    - Assert card brands passed to `DatatransMobileSdkRequest.paymentMethods`
    - _Requirements: 3.4, 4.4_

  - [x] 10.4 Test: Secure Fields doesn't pass card brands to Datatrans
    - Mock successful payment method validation
    - Assert Secure Fields Datatrans init doesn't include `paymentMethods` parameter
    - Assert validation still gates the initialization (availability check)
    - _Requirements: 3.5_

- [x] 11. Final verification
  - [x] 11.1 Run full build with tests
    - Run `cd backend && ./mvnw clean install -pl book-pay/services/payment-orchestration-service -am`
    - All tests pass, including new MockWebServer and workflow tests
    - _Requirements: All_

  - [x] 11.2 Verify JaCoCo coverage ≥ 80%
    - Check coverage report for all new classes (PaymentMethodRestAdapter, PaymentMethodDto models)
    - Check coverage for modified classes (workflow implementations, activity implementations)
    - _Requirements: Testing coverage goals_

  - [x] 11.3 Verify checkstyle — zero warnings
    - Run `cd backend && ./mvnw checkstyle:check -pl book-pay/services/payment-orchestration-service`
    - _Requirements: Code quality standards_

  - [x] 11.4 Manual verification with profile switching
    - Test with `pmes-real` profile active → assert real adapter used
    - Test with `pmes-real` profile inactive → assert stub adapter used
    - _Requirements: 7.1, 7.2, 7.3, 7.4_

## Notes

- **Profile-based adapter selection.** The `@Profile("pmes-real")` and `@Profile("!pmes-real")` annotations ensure only one adapter is active at a time. Local development can use the stub when the Payment Method Entity Service isn't available.
- **Parameter derivation.** The country and language parameters are derived from the reservation currency (GBP→GB/en, EUR→DE/de) rather than being passed from the frontend, simplifying the API contract.
- **Workflow consolidation.** The separate `validatePaymentMethods` and `getPaymentMethods` activities are merged into a single call to reduce network overhead and eliminate duplicate calls to the Payment Method Entity Service.
- **Card brand usage difference.** Mobile SDK passes the card brands to Datatrans in the `paymentMethods` field, while Secure Fields does the availability check but doesn't pass the brands (the Secure Fields endpoint doesn't accept them).
- **Error handling consistency.** Payment Method Entity Service integration follows the same error handling patterns as existing integrations (4xx/5xx/timeout → `ServiceUnavailableException`), and unavailable payment methods return a domain error that workflows can handle gracefully.
- **No integration tests.** Following the existing pattern, only MockWebServer adapter tests and workflow unit tests are required — no integration tests or property-based tests.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "2.1", "2.2"] },
    { "id": 1, "tasks": ["1.3", "3.1"] },
    { "id": 2, "tasks": ["4.1", "4.2", "5.1"] },
    { "id": 3, "tasks": ["4.3", "4.4", "6.1", "6.2"] },
    { "id": 4, "tasks": ["7.1", "7.2", "8.1", "8.2"] },
    { "id": 5, "tasks": ["9.1", "9.2", "9.3", "9.4"] },
    { "id": 6, "tasks": ["10.1", "10.2", "10.3", "10.4"] },
    { "id": 7, "tasks": ["11.1", "11.2", "11.3", "11.4"] }
  ]
}
```