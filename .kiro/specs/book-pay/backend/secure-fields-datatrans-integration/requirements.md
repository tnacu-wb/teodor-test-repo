---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-11215
---
# Requirements Document

## Introduction
Implementing the `POST /api/payments/secure-fields` endpoint in the Payment Orchestration Service to enable Secure Fields payment integration with Planet Datatrans using their v1 APIs. This feature provides the backend API that the web frontend calls to initialize a Secure Fields payment session, enabling PCI-compliant card data collection through Datatrans-hosted iframes. Referenced in Jira ticket CTECH-11215: "[BE] - Create new API for new card payment for WEB".

## Glossary
- **Secure Fields**: Datatrans-hosted iframe solution for PCI-compliant card data collection on web
- **Payment Orchestrator**: Backend service coordinating payment flows between frontend and Datatrans
- **Datatrans**: Payment gateway (Planet Datatrans) providing Secure Fields hosting and transaction processing
- **Basket Service**: Service holding reservation/basket state for bookings being paid
- **Payment Method Entity Service**: Service providing and validating available payment methods per hotel
- **Transaction ID**: Datatrans identifier for payment sessions, valid for 30 minutes
- **OPERA**: Property/hospitality platform receiving deposit/folio postings after successful payment

## Requirements

### Requirement 1: Secure Fields Payment Session Initialization
**User Story:** As a web frontend, I want to initialize a Secure Fields payment session, so that I can render card input iframes for the customer.

#### Acceptance Criteria
1. THE service SHALL expose `POST /api/payments/secure-fields` endpoint
2. THE endpoint SHALL accept a request body containing `basketId` (string, required)
3. THE service SHALL retrieve reservation details from Basket Service using the provided `basketId`
4. THE service SHALL check if the reservation already has a booking reference
5. THE service SHALL return `201 Created` with `transactionId` on successful session initialization
6. THE returned `transactionId` SHALL be valid for 30 minutes as per Datatrans specifications

### Requirement 2: Payment Method Validation
**User Story:** As a payment orchestrator, I want to validate available payment methods for the hotel, so that I only process payments for supported card brands.

#### Acceptance Criteria
1. THE service SHALL validate payment methods for the specific hotel before proceeding with Datatrans
2. THE service SHALL call Payment Method Entity Service to get and validate payment methods
3. THE service SHALL retrieve payment method configuration via Content Entity Service → AEM chain
4. IF card payment is not available for the hotel THEN the service SHALL return `422 Unprocessable Entity` with error code `PAYMENT_METHOD_NOT_AVAILABLE`

### Requirement 3: Datatrans v1 API Integration
**User Story:** As a payment orchestrator, I want to integrate with Datatrans v1 secureFields API, so that I can initialize payment sessions for web Secure Fields.

#### Acceptance Criteria
1. THE service SHALL call Datatrans `POST /v1/transactions/secureFields` endpoint
2. THE service SHALL use HTTP Basic Authentication with merchantId:merchantPwd credentials
3. THE service SHALL send request body with `amount` (baseAmount + donationAmount in minor currency units), `currency`, `returnUrl`, and cardholder data
4. THE service SHALL set `autoSettle = false` for deferred settlement
5. THE service SHALL receive `transactionId` from Datatrans and return it to the frontend
6. THE service SHALL handle Datatrans API errors and map them to appropriate HTTP status codes

### Requirement 4: Booking Reference Validation
**User Story:** As a payment orchestrator, I want to verify booking status before payment, so that I prevent duplicate payments for already confirmed reservations.

#### Acceptance Criteria
1. THE service SHALL check if the reservation already has a booking reference at payment initiation
2. IF the reservation already has a completed payment THEN the service SHALL return `409 Conflict` with error code `BOOKING_ALREADY_PAID`
3. IF the reservation already has a booking confirmation THEN the service SHALL return `409 Conflict` with error code `BOOKING_ALREADY_CONFIRMED`

### Requirement 5: Error Handling and Response Codes
**User Story:** As a frontend developer, I want consistent error responses, so that I can handle different failure scenarios appropriately.

#### Acceptance Criteria
1. THE service SHALL return `400 Bad Request` with error code `INVALID_REQUEST` for missing or malformed basketId
2. THE service SHALL return `404 Not Found` with error code `BASKET_NOT_FOUND` when no basket exists for the given basketId
3. THE service SHALL return `422 Unprocessable Entity` with error code `PAYMENT_METHOD_NOT_AVAILABLE` when card payment is not supported for the hotel
4. THE service SHALL return `502 Bad Gateway` with error code `GATEWAY_ERROR` when Datatrans returns an error
5. THE service SHALL return `503 Service Unavailable` with error code `SERVICE_UNAVAILABLE` when downstream services are unavailable
6. THE error response body SHALL follow the standard format: `{"error": {"code": "ERROR_CODE", "message": "Human readable message"}}`

### Requirement 6: Request/Response Contract Compliance
**User Story:** As a frontend developer, I want well-defined API contracts, so that I can integrate reliably with the payment service.

#### Acceptance Criteria
1. THE request body SHALL contain `basketId` as a required string field
2. THE successful response SHALL contain `transactionId` as a string field
3. THE response SHALL include proper Content-Type headers (application/json)
4. THE service SHALL validate request body structure and return 400 for invalid JSON
5. THE service SHALL include OpenAPI documentation for the endpoint

### Requirement 7: Security and PCI Compliance
**User Story:** As a security officer, I want payment initialization to maintain PCI compliance, so that card data never touches our systems.

#### Acceptance Criteria
1. THE service SHALL NOT collect, process, or store any card data (PAN, CVV, expiry)
2. THE service SHALL only handle non-sensitive data (amounts, currency, cardholder name/address)
3. THE service SHALL validate and sanitize all input parameters before processing
4. THE service SHALL use HTTPS for all external API communications
5. THE returned transactionId SHALL be the only payment-related data passed back to frontend

### Requirement 8: Amount and Currency Handling
**User Story:** As a payment orchestrator, I want to correctly calculate and send payment amounts to Datatrans, so that the customer is charged the right amount.

#### Acceptance Criteria
1. THE service SHALL retrieve baseAmount and donationAmount from the reservation
2. THE service SHALL convert amounts to minor currency units (pence for GBP, cents for EUR/USD)
3. THE service SHALL send combined total amount (baseAmount + donationAmount) to Datatrans
4. THE service SHALL retrieve currency code from the reservation and pass to Datatrans
5. THE service SHALL include returnUrl for 3DS redirect handling in the Datatrans request