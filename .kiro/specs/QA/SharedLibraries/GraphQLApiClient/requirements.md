---
parent_design: "none (derived from steering + industry standards)"
jira: "none"
---
# Requirements Document

## Introduction
This document specifies requirements for a shared GraphQL API Client library to be consumed by Playwright end-to-end tests in the QA framework. The library extracts reusable GraphQL client functionality and API validation helpers from the baseline test migration (BL001), providing a centralized, tested API layer for the new Playwright + TypeScript QA framework. This library enables consistent GraphQL communication patterns and eliminates duplication across multiple test specifications.

## Glossary
- **GraphQL Client**: A reusable, queries-only client class that executes GraphQL queries via Playwright's APIRequestContext
- **Query Wrappers**: High-level API call methods that encapsulate specific GraphQL operations (e.g., getDlpContent, getBasketByReference)
- **Validation Helpers**: Functions that cross-validate GraphQL response data against UI inputs or business rules
- **Response Models**: TypeScript interfaces that define the shape of GraphQL response data
- **API Base URL**: The GraphQL endpoint URL injected from playwright.config.ts environment configuration
- **Baseline Test (BL001)**: The comprehensive end-to-end booking journey test that will consume this library

## Requirements

### Requirement 1: GraphQL Client Foundation
**User Story:** As a test developer, I want a reusable GraphQL client built on Playwright's APIRequestContext, so that I can make typed GraphQL calls with consistent error handling across all tests.

#### Acceptance Criteria
1. THE GraphQLClient class SHALL accept an APIRequestContext and GraphQL base URL in its constructor
2. THE GraphQLClient class SHALL provide a typed query<T>() method that accepts a query string and optional variables
3. THE GraphQLClient class SHALL be queries-only and SHALL NOT expose a mutation method (no consumer needs writes, and auto-retrying non-idempotent mutations would be unsafe)
4. THE GraphQLClient class SHALL handle HTTP-level errors (network failures, 4xx/5xx responses) and GraphQL-level errors (errors field in response)
5. THE GraphQLClient class SHALL surface clear error messages that distinguish between HTTP and GraphQL failures
6. THE GraphQLClient SHALL send requests to the GraphQL endpoint without API key headers (the endpoint does not require authentication); the design SHALL leave room for an optional `Authorization: Bearer <Auth0 token>` header for future logged-in flows
7. THE GraphQLClient class SHALL make the GraphQL base URL accessible from playwright.config.ts environment configuration

### Requirement 2: Content and DLP Query Wrappers
**User Story:** As a test developer, I want pre-built methods for content and DLP operations, so that I can validate page content without writing raw GraphQL queries.

#### Acceptance Criteria
1. THE ContentAPI module SHALL provide getDlpContent(dlpPath: string) to fetch destination landing page data
2. THE ContentAPI module SHALL provide getHotelInformation(hotelId: string) to fetch hotel details
3. THE ContentAPI module SHALL provide getHotelTripAdvisorReview(params) to fetch TripAdvisor reviews
4. THE ContentAPI module SHALL provide getHotelsInformationForDLPMapView(params) to fetch map view hotel data
5. THE ContentAPI module SHALL provide validateDlpContentConsistency(expected, apiData) to compare a caller-supplied expected-content object against the getDlpContent API response; sourcing the expected content (e.g. an AEM content dictionary) is the caller's responsibility and is out of scope for this library
6. THE ContentAPI module SHALL provide getHotelTitle(hotelId: string) to fetch hotel name by hotel ID
7. WHEN called with invalid parameters THE query wrapper methods SHALL throw errors with descriptive messages

### Requirement 3: Basket Operations
**User Story:** As a test developer, I want basket-specific API operations, so that I can validate booking state transitions and basket contents during test flows.

#### Acceptance Criteria
1. THE BasketAPI module SHALL provide getBasketByReference(reference: string) to retrieve basket details
2. THE BasketAPI module SHALL provide validateBasketStatus(expectedStatus, basketId) to verify basket state
3. THE BasketAPI module SHALL provide validateBasketTransition(fromStatus, toStatus, basketId) to verify state changes
4. THE BasketAPI module SHALL support basket statuses: Open, Completed, Cancelled, Processing, PayPending
5. WHEN a basket status validation fails THE BasketAPI module SHALL provide clear assertion messages indicating expected vs actual status
6. THE BasketAPI module SHALL retry basket status checks for up to 30 seconds to account for async processing

### Requirement 4: Booking Confirmation Validation
**User Story:** As a test developer, I want comprehensive booking confirmation validation helpers, so that I can verify persisted reservation data matches the booking inputs without duplicating validation logic.

#### Acceptance Criteria
1. THE BookingConfirmationHelpers module SHALL provide getBookingConfirmation(basketReference) to fetch confirmation data
2. THE BookingConfirmationHelpers module SHALL provide validateHotel(booking, expectedHotel) to verify hotel details
3. THE BookingConfirmationHelpers module SHALL provide validateStayingDates(booking, criteria) to verify arrival/departure dates
4. THE BookingConfirmationHelpers module SHALL provide validateRoomsOccupancy(booking, criteria) to verify adult/child counts
5. THE BookingConfirmationHelpers module SHALL provide validateRoomTypes(booking, selectedRoomName, hotelType) to verify room configurations
6. THE BookingConfirmationHelpers module SHALL provide validateRatePlan(booking, ratePlanCode) to verify rate codes
7. THE BookingConfirmationHelpers module SHALL provide validateRatesPerNight(booking, expectedPrices) to verify nightly rates
8. THE BookingConfirmationHelpers module SHALL provide validateRoomPrice(booking, roomPrices) to verify room pricing
9. THE BookingConfirmationHelpers module SHALL provide validateCurrency(booking, currencyCode) to verify currency consistency
10. THE BookingConfirmationHelpers module SHALL provide validateBookingFlowId(booking, bookingFlowId) to verify flow tracking
11. THE BookingConfirmationHelpers module SHALL provide validateMealPackagesPerRoom(booking, mealsPackage, criteria) to verify meal selections
12. THE BookingConfirmationHelpers module SHALL provide validateDepositPoliciesForAllRooms(booking, policyCode) to verify deposit requirements
13. THE BookingConfirmationHelpers module SHALL provide validateTotalCostNoDiscountsNoAmendment(booking, totalCost, paymentType) to verify pricing
14. THE BookingConfirmationHelpers module SHALL provide validatePaymentCard(booking, paymentType, cardNumber) to verify payment details
15. THE BookingConfirmationHelpers module SHALL provide validateCityTax(booking, hasCityTax) to verify tax calculations
16. THE BookingConfirmationHelpers module SHALL provide validateGuestDetailsAgainstBookingConfirmation(booking, guestDetails, roomDetails) to verify guest information
17. WHEN validation fails THE BookingConfirmationHelpers module SHALL provide detailed mismatch descriptions

### Requirement 5: Availability and Booking Flow Support
**User Story:** As a test developer, I want availability checking and booking flow helpers, so that I can verify test preconditions and retrieve booking flow identifiers.

#### Acceptance Criteria
1. THE library SHALL provide a hotel availability helper that returns availability data when the required room configurations are available for the given dates, and throws a descriptive error (fail-fast) when they are not — mirroring the existing framework's getHotelAvailability (no boolean return)
2. THE library SHALL support availability checks for DOUBLE (2-adults) and FAMILY (2-adults-1-child) room types
3. THE library SHALL provide getMealsPackages(params) to retrieve meal package options
4. THE library SHALL provide getBookingFlowIdBasedOnHotelAndRate(params) to retrieve booking flow identifiers
5. WHEN required room types are unavailable THE availability helper SHALL fail fast with a clear message and SHALL log the hotel inventory before throwing
6. THE availability helper SHALL be suitable for test precondition validation in before-hooks
7. THE availability helper SHALL retry across a forward date range (advancing arrival/departure by a day per attempt) and SHALL retry transient 5xx service errors before failing, to avoid spurious no-availability results

### Requirement 6: Typed Response Models
**User Story:** As a test developer, I want TypeScript interfaces for API responses, so that I have compile-time type safety and IntelliSense support when working with API data.

#### Acceptance Criteria
1. THE library SHALL define DlpContentResponse interface with hotels, map coordinates, and TripAdvisor flags
2. THE library SHALL define BookingConfirmationResponse interface with booking reference, cost, currency, rooms, guest details, and payment info
3. THE library SHALL define HotelInformationResponse interface with hotel details, facilities, and contact information
4. THE library SHALL define BasketResponse interface with status, items, payment status, and booking reference
5. THE library SHALL define AvailabilityResponse interface with room rates, availability flags, and pricing
6. THE library SHALL define typed input interfaces for common query parameters
7. THE library SHALL export all response models from a central barrel file for easy consumption

### Requirement 7: Library Verification (Playwright, integration-first)
**User Story:** As a QA engineer, I want the library verified with the framework's own runner and against the live API, so that I can trust it before other tests depend on it — without over-investing in mock-heavy unit tests.

#### Acceptance Criteria
1. THE library tests SHALL use Playwright Test (`@playwright/test`), consistent with the QA framework runner; no Jest or other test runner SHALL be introduced
2. THE read-only query wrappers (getDlpContent, getHotelInformation, getHotelTitle, getHotelTripAdvisorReview, getHotelsInformationForDLPMapView, the availability helper, getMealsPackages, getBookingFlowIdBasedOnHotelAndRate) SHALL be integration-tested against UAT using deterministic inputs (known hotel IDs, fixed future dates)
3. THE booking-confirmation, basket, and meals-per-booking validation helpers SHALL be verified against captured JSON response fixtures rather than live calls, because no booking exists in isolation; their true end-to-end correctness is exercised when BL001 runs the full journey and calls them with a live booking reference (the library does not create bookings itself)
4. THE client behaviour tests SHALL cover error handling: 5xx responses are retried, 4xx and GraphQL schema errors are not retried, timeouts are honoured, and required-parameter validation throws clear errors
5. THE tests SHALL verify custom headers (Accept, optional Bearer token) are passed through to requests
6. THE tests SHALL verify query wrappers reject invalid or missing required parameters with descriptive messages
7. THE integration tests SHALL be read-only and use deterministic data that does not mutate UAT state or interfere with other suites
8. THE library test suite SHALL run independently via a dedicated script/Playwright project and complete quickly

### Requirement 8: Environment Configuration Integration
**User Story:** As a test developer, I want the GraphQL client to automatically use the correct API endpoint for the current test environment, so that I don't need to hardcode URLs or manage environment switching.

#### Acceptance Criteria
1. THE GraphQLClient SHALL read the GraphQL base URL from playwright.config.ts configuration
2. THE GraphQLClient SHALL support both UAT and DIT environment endpoints as configured in playwright.config.ts
4. THE GraphQLClient SHALL be configurable via a factory function that accepts a test's APIRequestContext
5. WHEN the ENV value is invalid THE GraphQLClient SHALL fail with clear error messages
6. THE GraphQLClient SHALL support the ENV environment variable switching between uat and dit

### Requirement 9: Error Handling and Resilience
**User Story:** As a test developer, I want robust error handling and retry logic, so that my tests are resilient to temporary network issues and provide clear failure diagnostics.

#### Acceptance Criteria
1. THE GraphQLClient SHALL retry only server-side failures (HTTP 5xx, or GraphQL errors whose extensions.errorType is 5xx) with backoff; because the client is queries-only, all retried operations are idempotent
2. THE GraphQLClient SHALL distinguish between retryable errors (network timeouts, 5xx responses) and non-retryable errors (4xx responses, GraphQL schema errors)
3. THE GraphQLClient SHALL capture and log full request/response details for failed operations
4. THE GraphQLClient SHALL provide timeout configuration with a default of 30 seconds per request
5. WHEN all retries are exhausted THE GraphQLClient SHALL throw descriptive errors that include the operation attempted and failure reason
6. THE query wrapper methods SHALL validate required parameters before making GraphQL calls
7. THE validation helper methods SHALL provide detailed assertion messages that clearly indicate the expected vs actual values

### Requirement 10: Clean Module Organization
**User Story:** As a test developer, I want a well-organized library with clear module boundaries, so that I can easily find and import the functionality I need.

#### Acceptance Criteria
1. THE library SHALL organize code in qa/src/api/ with separate files per concern
2. THE GraphQLClient SHALL be in qa/src/api/graphqlClient.ts
3. THE BasketAPI helpers SHALL be in qa/src/api/basketApi.ts  
4. THE ContentAPI helpers SHALL be in qa/src/api/contentApi.ts
5. THE BookingConfirmationHelpers SHALL be in qa/src/api/bookingConfirmationHelpers.ts
6. THE typed response models SHALL be in qa/src/api/types/
7. THE library SHALL provide a barrel export from qa/src/api/index.ts for convenient importing
8. THE library SHALL follow the existing QA framework conventions for file naming and module structure
9. THE library SHALL export classes and functions using named exports for tree-shaking compatibility
10. THE library SHALL include comprehensive JSDoc documentation for all public APIs
11. THE AvailabilityHelpers SHALL be in qa/src/api/availabilityHelpers.ts
12. THE client factory SHALL be in qa/src/api/clientFactory.ts
13. THE library SHALL include a short README with usage examples and troubleshooting notes for consumers