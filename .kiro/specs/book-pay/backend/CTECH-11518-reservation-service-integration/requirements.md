---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-11518
---
# Requirements Document

## Introduction
Replace the Basket stub adapter in the Payment Orchestration Service with a real integration to the **Hotel Reservation Entity Service**. The frontend passes a `basketId` when initiating payment (both Secure Fields and Mobile SDK flows). The Payment Orchestrator calls `GET /v1/reservations/basket/{basketReference}` using that `basketId` to retrieve reservation details. From the response, the orchestrator extracts: `bookingReference` (used as `refno` for Datatrans and tracked in the Temporal workflow), `currencyCode`, and `totalCostOfStay` (the payment amount). This covers steps 3 and 4 in both payment flows as defined in the end-to-end design.

## Glossary
- **Hotel Reservation Entity Service**: Backend microservice holding reservation/basket state. Exposes reservation data by basket reference.
- **basketId / basketReference**: The identifier passed from the frontend when initiating payment. Used as the path parameter `{basketReference}` to look up reservation data.
- **bookingReference**: A reference returned from the reservation response. Used as `refno` when calling Datatrans APIs and tracked throughout the Temporal payment workflow.
- **totalCostOfStay**: The total amount the guest owes, sourced from `rateInfo.summary.totalCostOfStay` in the reservation response. Used as the payment amount.
- **currencyCode**: ISO 4217 currency code (e.g. GBP, EUR) from `rateInfo.summary.currencyCode`.
- **refno**: Datatrans reference number field. Set to the `bookingReference` retrieved from the reservation service.

## Requirements

### Requirement 1: Reservation Retrieval by Basket Reference
**User Story:** As a Payment Orchestrator, I want to retrieve reservation details from the Hotel Reservation Entity Service using the basketId, so that I can validate the basket, obtain the bookingReference for Datatrans, and determine the payment amount.

#### Acceptance Criteria
1. THE service SHALL call `GET /v1/reservations/basket/{basketReference}` on the Hotel Reservation Entity Service, substituting `{basketReference}` with the `basketId` received from the frontend
2. THE service SHALL send an `Accept: application/json` header with the request
3. THE service SHALL extract `bookingReference` from the top-level response field — this becomes the `refno` for Datatrans
4. THE service SHALL extract `currencyCode` from `rateInfo.summary.currencyCode` of the first reservation in `reservationByIdList`
5. THE service SHALL extract the payment amount from `rateInfo.summary.totalCostOfStay` of the first reservation in `reservationByIdList`
6. THE service SHALL map the response into the `Reservation` domain model for use by downstream payment logic

### Requirement 2: Basket ID Validation
**User Story:** As a Payment Orchestrator, I want to validate that the basketId corresponds to a valid reservation, so that invalid payment requests are rejected early.

#### Acceptance Criteria
1. THE service SHALL return `404 Not Found` with error code `BASKET_NOT_FOUND` when the Hotel Reservation Entity Service returns a 404 response
2. THE service SHALL return `404 Not Found` with error code `BASKET_NOT_FOUND` when the response contains an empty `reservationByIdList`
3. THE service SHALL return `400 Bad Request` with error code `INVALID_REQUEST` when the basketId is null, blank, or malformed

### Requirement 3: Booking Reference as Datatrans refno
**User Story:** As a Payment Orchestrator, I want to use the bookingReference from the reservation as the `refno` for Datatrans, so that the payment transaction is correlated with the correct booking.

#### Acceptance Criteria
1. THE service SHALL pass `bookingReference` as the `refno` field in all Datatrans API calls (Secure Fields init, Mobile SDK init, authorize, settle)
2. THE service SHALL store the `bookingReference` in the Temporal workflow state so it is available for subsequent payment lifecycle steps (authorize, settle, webhook processing)
3. THE service SHALL fail with `502 Bad Gateway` and error code `GATEWAY_ERROR` if `bookingReference` is null or empty in the reservation response

### Requirement 4: Amount and Currency Extraction
**User Story:** As a Payment Orchestrator, I want to extract the correct payment amount and currency from the reservation, so that the customer is charged the right amount via Datatrans.

#### Acceptance Criteria
1. THE service SHALL use `rateInfo.summary.totalCostOfStay` as the payment amount (in major currency units)
2. THE service SHALL use `rateInfo.summary.currencyCode` as the ISO 4217 currency code
3. THE service SHALL convert `totalCostOfStay` to minor currency units before sending to Datatrans (e.g. GBP 89.00 → 8900)
4. THE service SHALL fail with `502 Bad Gateway` and error code `GATEWAY_ERROR` if `totalCostOfStay` or `currencyCode` is null or missing from the response

### Requirement 5: Configuration and Connectivity
**User Story:** As a DevOps engineer, I want the Hotel Reservation Entity Service connection to be externally configurable, so that I can deploy across environments.

#### Acceptance Criteria
1. THE service SHALL configure the reservation service host via `integrations.reservation.host` in `application.yml` with default value `http://localhost:9103`
2. THE service SHALL configure the endpoint path via `integrations.reservation.reservationEndpoint` with value `/v1/reservations/basket/{basketReference}`
3. THE service SHALL support environment variable override via `${RESERVATION_HOST:localhost:9103}`
4. THE service SHALL configure connect and read timeouts independently
5. THE configuration SHALL follow the same pattern as existing `integrations.datatrans` configuration

### Requirement 6: Error Handling and Resilience
**User Story:** As a Payment Orchestrator, I want robust error handling for the reservation service call, so that transient failures are handled gracefully and the caller gets meaningful errors.

#### Acceptance Criteria
1. THE service SHALL return `503 Service Unavailable` with error code `SERVICE_UNAVAILABLE` when the Hotel Reservation Entity Service is unreachable (connection refused, timeout)
2. THE service SHALL return `502 Bad Gateway` with error code `GATEWAY_ERROR` when the Hotel Reservation Entity Service returns an unexpected error (5xx)
3. THE service SHALL log the error response from the Hotel Reservation Entity Service for debugging (without logging PII such as guest names, emails, or addresses)
4. THE service SHALL respect the configured read timeout and not block indefinitely

### Requirement 7: Replace Stub with Real Implementation
**User Story:** As a developer, I want the existing Basket stub adapter fully replaced with the real Hotel Reservation Entity Service client, so that all environments use live reservation data.

#### Acceptance Criteria
1. THE existing `BasketStubAdapter` SHALL be removed from the codebase
2. THE new `ReservationServiceClient` SHALL be the sole implementation of `BasketOutPort`
3. THE new implementation SHALL NOT use Spring profile gating — it is always active
4. THE existing `Reservation` domain model SHALL be updated: replace `baseAmount` + `donationAmount` with `totalCostOfStay` as the single amount field, and ensure `bookingReference` is always populated from the reservation response (used as `refno`)
5. THE `refno` field on the existing `Reservation` record SHALL be set to `bookingReference` (previously it was set to `basketId`)
