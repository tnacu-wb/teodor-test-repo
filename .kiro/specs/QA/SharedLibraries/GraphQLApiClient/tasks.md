---
parent_design: "none (derived from steering + industry standards)"
jira: "none"
---
# Implementation Plan: GraphQL API Client & Validation Helpers Library

## Overview
Implementation of a shared, queries-only GraphQL API client library for the Playwright + TypeScript QA framework, providing reusable GraphQL communication, query wrappers, and validation helpers extracted from the baseline test migration (BL001). The client sends requests without API key headers (the endpoint does not require authentication), retries only server-side (5xx) failures, and is verified with Playwright Test in an integration-first manner.

## Tasks

- [x] 1. Core GraphQL Client Implementation
  - [x] 1.1 Set up project structure and TypeScript configuration
    - Create qa/src/api/ directory structure
    - Configure TypeScript interfaces and types
    - Set up the initial barrel export (index.ts) scaffold
    - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5, 10.6, 10.8, 10.11, 10.12_
  - [x] 1.2 Implement GraphQLClient core functionality
    - Build GraphQLClient class with APIRequestContext integration
    - Implement the typed query<T>() method only — do NOT add a mutation() method (queries-only)
    - Add HTTP and GraphQL error handling with descriptive messages
    - Implement retry logic limited to server-side failures (HTTP 5xx / GraphQL 5xx errorType) with backoff; never retry 4xx or schema errors
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 9.1, 9.2, 9.3, 9.4, 9.5_
  - [x] 1.3 Environment configuration and authentication
    - Integrate with playwright.config.ts apiBaseURL (the experience/GraphQL endpoint)
    - Configure Accept header and optional Bearer token support
    - Add ENV environment variable support (uat/dit switching)
    - Create client factory function for test consumption
    - Leave room for an optional Authorization: Bearer <Auth0 token> header for future logged-in flows (not required by the current guest consumer)
    - _Requirements: 1.6, 1.7, 8.1, 8.2, 8.4, 8.5, 8.6_
  - [x] 1.4 Error handling and resilience
    - Implement timeout configuration with 30s default
    - Add parameter validation for all public methods
    - Implement comprehensive logging for failed operations
    - Create custom error classes (GraphQLError, ValidationError)
    - Scope retries to server-side (5xx) failures only; never retry 4xx or GraphQL schema errors
    - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6, 9.7_

- [x] 2. Response Type Definitions
  - [x] 2.1 Core response interfaces
    - Define DlpContentResponse with hotels, map, and TripAdvisor data
    - Define BookingConfirmationResponse with booking, guest, and payment details
    - Define HotelInformationResponse with hotel details and facilities
    - Define BasketResponse with status, items, and payment information
    - Define AvailabilityResponse with room rates, availability flags, and pricing
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_
  - [x] 2.2 Input parameter interfaces
    - Define typed input interfaces for query parameters
    - Create helper types for common data structures (dates, occupancy, etc.)
    - Set up central barrel export for all types
    - _Requirements: 6.6, 6.7_

- [x] 3. Content API Module Implementation
  - [x] 3.1 DLP and content query wrappers
    - Implement getDlpContent() for destination landing page data
    - Implement getHotelInformation() for hotel details
    - Implement getHotelTitle() for hotel ID-based hotel name lookup
    - Add error handling and parameter validation for all methods
    - _Requirements: 2.1, 2.2, 2.6, 2.7_
  - [x] 3.2 Hotel information and reviews
    - Implement getHotelTripAdvisorReview() for review data
    - Implement getHotelsInformationForDLPMapView() for map view data
    - Add validateDlpContentConsistency() for cross-validation
    - Ensure all methods return typed responses
    - _Requirements: 2.3, 2.4, 2.5_

- [x] 4. Basket API Module Implementation  
  - [x] 4.1 Basket operations core functionality
    - Implement getBasketByReference() with BasketResponse typing
    - Implement validateBasketStatus() with clear assertion messages
    - Implement validateBasketTransition() for state change verification
    - Add support for all basket statuses (Open, Completed, Cancelled, Processing, PayPending)
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_
  - [x] 4.2 Async basket status monitoring
    - Implement waitForBasketStatus() with 30-second retry logic
    - Add exponential backoff for basket status polling
    - Provide detailed error messages for status validation failures
    - _Requirements: 3.6_

- [x] 5. Booking Confirmation Helpers
  - [x] 5.1 Core booking confirmation retrieval
    - Implement getBookingConfirmation() with typed response
    - Add comprehensive error handling for invalid basket references
    - Ensure response matches BookingConfirmationResponse interface
    - _Requirements: 4.1_
  - [x] 5.2 Hotel and dates validation helpers
    - Implement validateHotel() for hotel details verification
    - Implement validateStayingDates() for arrival/departure validation
    - Implement validateRoomsOccupancy() for guest count verification
    - Implement validateRoomTypes() for room configuration checks
    - _Requirements: 4.2, 4.3, 4.4, 4.5_
  - [x] 5.3 Pricing and rate validation helpers
    - Implement validateRatePlan() for rate code verification
    - Implement validateRatesPerNight() for nightly rate validation
    - Implement validateRoomPrice() for room pricing verification
    - Implement validateCurrency() for currency consistency checks
    - _Requirements: 4.6, 4.7, 4.8, 4.9_
  - [x] 5.4 Advanced booking validation helpers
    - Implement validateBookingFlowId() for flow tracking verification
    - Implement validateMealPackagesPerRoom() for meal selection validation
    - Implement validateDepositPoliciesForAllRooms() for deposit requirement checks
    - Implement validateTotalCostNoDiscountsNoAmendment() for pricing verification
    - _Requirements: 4.10, 4.11, 4.12, 4.13_
  - [x] 5.5 Payment and guest validation helpers
    - Implement validatePaymentCard() for payment details verification
    - Implement validateCityTax() for tax calculation validation
    - Implement validateGuestDetailsAgainstBookingConfirmation() for guest data verification
    - Ensure all validation methods provide detailed mismatch descriptions
    - _Requirements: 4.14, 4.15, 4.16, 4.17_

- [x] 6. Availability and Booking Flow Support
  - [x] 6.1 Availability checking functionality
    - Implement a fail-fast hotel availability helper (ensureHotelAvailability) that returns availability data on success and throws on failure — no boolean return — mirroring the existing business-booker getHotelAvailability
    - Support DOUBLE (2-adults) and FAMILY (2-adults-1-child) room types
    - Advance arrival/departure by a day and retry across a forward date range; retry transient 5xx service errors; log the hotel inventory before throwing
    - Design for test precondition validation in before-hooks
    - _Requirements: 5.1, 5.2, 5.5, 5.6, 5.7_
  - [x] 6.2 Meal packages and booking flow support
    - Implement getMealsPackages() for meal package options
    - Implement getBookingFlowIdBasedOnHotelAndRate() for flow identifier retrieval
    - Both methods use the GraphQL client built in task 1.2
    - _Requirements: 5.3, 5.4_

- [x] 7. Library Verification Tests (Playwright Test)
  - Use @playwright/test (no Jest). Tests live alongside the library and run via a dedicated script/project.
  - [x] 7.1 Client behaviour tests
    - Verify custom headers (Accept, optional auth token) are passed through to requests
    - Verify 5xx responses are retried and 4xx / GraphQL schema errors are not
    - Verify timeout handling and required-parameter validation errors
    - Use a stub APIRequestContext or a local mock server (no live calls)
    - _Requirements: 7.1, 7.4, 7.5, 7.6, 9.6, 9.7_
  - [x] 7.2 Read-only query integration tests (UAT)
    - Integration-test getDlpContent, getHotelInformation, getHotelTitle, getHotelTripAdvisorReview, getHotelsInformationForDLPMapView, the availability helper, getMealsPackages, and getBookingFlowIdBasedOnHotelAndRate against UAT with deterministic data
    - _Requirements: 7.1, 7.2, 7.7_
  - [x] 7.3 Fixture-based logic tests for booking-confirmation/basket helpers
    - Verify the comparison logic of BookingConfirmationHelpers and BasketAPI validators against captured JSON response fixtures (no live booking is created)
    - Note: end-to-end correctness of these helpers is exercised by BL001's full-journey run
    - _Requirements: 7.1, 7.3_

- [x] 8. Test Data and Independent Execution
  - [x] 8.1 Deterministic, read-only test data
    - Use known UAT hotel IDs (e.g. GATGAT, LONEUS) and fixed future dates; keep all library tests read-only so they never mutate UAT state or interfere with other suites
    - Commit captured JSON fixtures for the booking-confirmation/basket logic tests
    - _Requirements: 7.7_
  - [x] 8.2 Dedicated, independent test script/project
    - Add a `test:api` script (Playwright project or tag) so the library is verified independently and quickly, as the prerequisite gate before consumers build on it
    - _Requirements: 7.8_

- [x] 9. Packaging, Verification Run, and Documentation
  - [x] 9.1 Library packaging and exports
    - Complete the barrel export from qa/src/api/index.ts
    - Add comprehensive JSDoc to all public APIs
    - Use named exports for tree-shaking compatibility
    - _Requirements: 10.7, 10.8, 10.9, 10.10_
  - [x] 9.2 Environment integration check
    - Validate ENV switching between uat and dit
    - Test the client factory against the real playwright.config.ts wiring
    - Verify factory creates client without auth requirements
    - _Requirements: 8.1, 8.2, 8.4, 8.5, 8.6_
  - [x] 9.3 Run the verification suite
    - Run the Playwright `test:api` suite (client behaviour + read-only integration + fixture logic) against UAT
    - Confirm it runs independently of other QA specs and passes as the gate before consumer work (BL001)
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 7.7, 7.8_
  - [x] 9.4 Documentation and examples
    - Add usage examples and a short README (how to consume from a spec, how to run test:api)
    - Provide troubleshooting notes for common errors (missing config, 5xx retries, timeouts)
    - _Requirements: 10.13_

## Notes

**Testing Strategy**: Verified with Playwright Test (no Jest), integration-first. Read-only query wrappers are integration-tested against UAT with deterministic data; booking-confirmation/basket/meals helpers are checked against captured JSON fixtures (the library never creates bookings), with their true end-to-end correctness exercised by BL001's run.

**Error Handling Philosophy**: Clear, actionable errors over silent failures. Retries are limited to server-side (5xx) failures; because the client is queries-only, retries are always safe. 4xx and GraphQL schema errors fail fast with descriptive messages.

**Type Safety**: All API responses are strongly typed using TypeScript interfaces. This provides compile-time safety and IntelliSense support when consuming the library in test specifications. The library validates response shapes at runtime and throws typed errors when data doesn't match expected schemas.

**Environment Configuration**: The library integrates seamlessly with the existing playwright.config.ts environment setup, automatically selecting the correct GraphQL endpoint and authentication based on the ENV variable. This eliminates hardcoded URLs and manual environment management in test specifications.

**Reusability Design**: All components are designed for maximum reusability across different test specifications. The library follows single-responsibility principles with clear module boundaries, making it easy for test developers to import only the functionality they need.

**Authentication**: The UAT GraphQL endpoint does not require API key headers. The client sends `Accept: */*` and `Content-Type: application/json`. An optional Auth0 Bearer header is left open for future logged-in flows.

**Queries-only**: The client exposes only `query()`; there is no mutation method, matching current consumer needs and keeping retries safe.

## Task Dependency Graph
```json
{
  "waves": [
    {
      "id": 0,
      "tasks": ["1.1", "2.1"]
    },
    {
      "id": 1,
      "tasks": ["1.2", "2.2"]
    },
    {
      "id": 2,
      "tasks": ["1.3", "1.4", "3.1"]
    },
    {
      "id": 3,
      "tasks": ["3.2", "4.1"]
    },
    {
      "id": 4,
      "tasks": ["4.2", "5.1"]
    },
    {
      "id": 5,
      "tasks": ["5.2", "5.3", "6.1"]
    },
    {
      "id": 6,
      "tasks": ["5.4", "5.5", "6.2", "7.1"]
    },
    {
      "id": 7,
      "tasks": ["7.2", "7.3", "8.1"]
    },
    {
      "id": 8,
      "tasks": ["8.2", "9.1"]
    },
    {
      "id": 9,
      "tasks": ["9.2", "9.3"]
    },
    {
      "id": 10,
      "tasks": ["9.4"]
    }
  ]
}
```