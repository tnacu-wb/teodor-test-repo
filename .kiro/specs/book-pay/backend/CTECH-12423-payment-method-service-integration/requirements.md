---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-12423
---
# Requirements Document

## Introduction

The Payment Orchestration Service's payment flows currently use a stub `PaymentMethodStubAdapter` that hardcodes card payment availability (always returns `true` with `["VIS", "ECA", "AMX"]` brands). This work replaces the stub with a real integration to the **Payment Method Entity Service** (`http://localhost:9107`). The service will call the Payment Method Entity Service to validate card payment availability for hotels and extract the actual supported card types before proceeding with Datatrans initialization.

## Glossary

- **Payment Method Entity Service**: Upstream service that provides hotel-specific payment method configuration, including card payment availability and accepted card brands
- **PaymentMethodOutPort**: Secondary port interface for payment method validation
- **PaymentMethodValidationResult**: Domain model containing card availability flag and supported brands list
- **Card brand codes**: Datatrans payment method identifiers (e.g. "VIS", "ECA", "AMX", "DIN")

## Requirements

### Requirement 1: Replace stub with real REST integration
**User Story:** As a payment processor, I want the Payment Orchestration Service to validate payment methods with the actual Payment Method Entity Service, so that card payment availability reflects the real hotel configuration.

#### Acceptance Criteria
1.1. THE Payment Orchestration Service SHALL replace the `PaymentMethodStubAdapter` with a REST client calling the Payment Method Entity Service
1.2. THE service SHALL call `GET /v1/payment-methods` on the Payment Method Entity Service with required query parameters
1.3. THE service SHALL derive `country` and `language` parameters from reservation currency (GBP→GB/en, EUR→DE/de)
1.4. THE service SHALL pass `basketReference`, `country`, `language` as query parameters
1.5. THE service SHALL pass `bookingChannel` header ("WEB" for Secure Fields, "MOBILE" for Mobile SDK)
1.6. THE service SHALL default `userType` to "LEISURE"

### Requirement 2: Parse and validate payment method response
**User Story:** As a payment processor, I want to determine card payment availability by finding enabled Datatrans payment providers, so that I can gate transaction initialization correctly.

#### Acceptance Criteria
2.1. THE service SHALL search the response for `paymentProvider == "Datatrans"` entries
2.2. THE service SHALL check that the Datatrans entry has `enabled == true`
2.3. IF no Datatrans entry exists OR Datatrans is disabled THEN THE service SHALL return `PAYMENT_METHOD_NOT_AVAILABLE` error
2.4. THE service SHALL extract `acceptedCardTypes[].type` values from enabled Datatrans entries
2.5. THE service SHALL map payment method service card types to Datatrans codes
2.6. THE service SHALL return both availability flag AND card brand list in `PaymentMethodValidationResult`

### Requirement 3: Integrate with payment workflows
**User Story:** As a payment workflow, I want to call payment method validation before Datatrans initialization, so that I only proceed with valid payment configurations.

#### Acceptance Criteria
3.1. THE Secure Fields workflow SHALL call payment method validation after Hotel Reservation Entity Service and before Datatrans init
3.2. THE Mobile SDK workflow SHALL call payment method validation after Hotel Reservation Entity Service and before Datatrans init
3.3. THE service SHALL pass hotel ID from reservation response to payment method validation
3.4. THE service SHALL use card brands from validation result in Mobile SDK Datatrans init request
3.5. THE Secure Fields flow SHALL validate card availability but NOT pass card brands to Datatrans (endpoint doesn't accept them)

### Requirement 4: Consolidate payment method activities
**User Story:** As a workflow implementer, I want a single activity that provides both payment availability and card brands, so that I don't make duplicate calls to the Payment Method Entity Service.

#### Acceptance Criteria
4.1. THE service SHALL replace separate `validatePaymentMethods` and `getPaymentMethods` activities with a single consolidated call
4.2. THE consolidated activity SHALL return `PaymentMethodValidationResult` containing both availability flag and card brands
4.3. THE Mobile SDK workflow SHALL use the card brands from the validation result
4.4. THE workflows SHALL call the payment method service once per payment initialization

### Requirement 5: Handle errors gracefully
**User Story:** As a user attempting payment, I want clear error messages when payment methods are unavailable, so that I understand why my payment cannot proceed.

#### Acceptance Criteria
5.1. WHEN Payment Method Entity Service returns non-2xx response THEN THE service SHALL throw `ServiceUnavailableException`
5.2. WHEN Payment Method Entity Service is unreachable THEN THE service SHALL throw `ServiceUnavailableException`
5.3. WHEN no enabled Datatrans payment method is found THEN THE service SHALL return error code `PAYMENT_METHOD_NOT_AVAILABLE` with message "Card payment is not available for this hotel"
5.4. THE error SHALL prevent Datatrans transaction initialization
5.5. THE error SHALL be returned to the frontend with appropriate HTTP status code

### Requirement 6: Configuration support
**User Story:** As a system operator, I want the Payment Method Entity Service integration to be configurable, so that I can adjust timeouts and endpoints per environment.

#### Acceptance Criteria
6.1. THE service SHALL support configurable base URL for Payment Method Entity Service
6.2. THE service SHALL support configurable connect and read timeouts
6.3. THE configuration SHALL follow the same pattern as existing service integrations
6.4. THE default configuration SHALL point to `http://localhost:9107` for local development

### Requirement 7: Profile-based activation
**User Story:** As a developer, I want the real Payment Method Entity Service integration to be activated by profile, so that I can still use the stub during development when needed.

#### Acceptance Criteria
7.1. THE real adapter SHALL be active when `pmes-real` profile is enabled
7.2. THE stub adapter SHALL be active when `pmes-real` profile is NOT enabled
7.3. THE profile configuration SHALL allow local development flexibility
7.4. THE production deployment SHALL use the real adapter