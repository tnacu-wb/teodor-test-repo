---
parent_design: "none (derived from steering + industry standards)"
jira: "none"
---
# Implementation Plan: Baseline Test Migration from WebDriverIO to Playwright

## Overview
Complete migration of the comprehensive baseline test to achieve 100% functional parity using the Playwright + TypeScript framework with a proper Page Object Model, maintainable test structure, and clearly commented test steps. The GraphQL client and API validation helpers are provided by a separate spec (GraphQL API Client & Validation Helpers) and are consumed here as a prerequisite. The objective is functional parity, not a line-by-line clone — prefer idiomatic Playwright/TypeScript approaches and reuse (grouped in the POM) over replicating the original WebDriverIO patterns.

## Tasks

- [x] 1. Prerequisite: Shared GraphQL API Client Library
  - The GraphQL client, query wrappers, and API validation helpers are delivered by a separate spec — GraphQL API Client & Validation Helpers (`.kiro/specs/QA/SharedLibraries/GraphQLApiClient/`). They are NOT built in BL001.
  - [x] 1.1 Integrate and Consume the Shared GraphQL API Client Library
    - Take a dependency on the shared library from the GraphQL API Client spec (GraphQL client, basket helper, content helper, booking-confirmation helper, and hotel availability pre-check)
    - Wire it into `qa/src/api/` usage without adding new inline API calls or query wrappers in BL001
    - Confirm the library exposes the queries this journey needs (DLP content, TripAdvisor reviews, map-view hotels, hotel title, basket, hotel information, booking confirmation, meal packages, hotel availability)
    - _Requirements: 4.4, 7.1, 7.2, 7.3, 7.4, 9.2_

- [x] 2. New Page Objects Implementation
  - Extend existing page objects where possible; only create a new page object when the screen has no existing object. Export all new page objects from `qa/src/pages/pi/index.ts`.
  - [x] 2.1 Create Destination Landing Page Object
    - Create `qa/src/pages/pi/destinationLanding.page.ts`
    - Implement DLP navigation, page validation, element checks
    - Support TripAdvisor integration validation and review link testing
    - Implement map/grid view toggle and hotel card validation
    - Add hotel distance display toggle support based on AEM configuration
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 7.5_
  - [x] 2.2 Create Manage Booking Page Object
    - Create `qa/src/pages/pi/manageBooking.page.ts`
    - Implement modal opening, booking search, and booking information display
    - Support booking reference validation and action buttons (amend, cancel)
    - _Requirements: 5.1, 5.2, 6.1, 7.5_
  - [x] 2.3 Create Amendment Booking Page Object
    - Create `qa/src/pages/pi/amendBooking.page.ts`
    - Implement room and guest section editing, room type changes
    - Support availability checking and room update confirmation
    - Add booking summary cost validation during amendments
    - _Requirements: 5.4, 5.5, 5.6, 5.7, 5.8, 7.5_
  - [x] 2.4 Create Booking History Page Object
    - Create `qa/src/pages/pi/bookingHistory.page.ts`
    - Implement booking history card display and validation
    - Support guest name, room configuration, and payment total validation
    - Add cancelled booking status validation
    - _Requirements: 5.3, 5.9, 6.7, 7.6_

- [x] 3. Test Data and Constants Enhancement
  - [x] 3.1 Extend Hotel Constants
    - Update `qa/src/constants/hotels.ts` with DLP-specific data
    - Add map coordinates, distance settings, and availability configurations
    - Ensure hotels support both DOUBLE and FAMILY room types
    - _Requirements: 1.4, 2.1, 5.5_
  - [x] 3.2 Extend Guest Data Support
    - Update `qa/src/constants/guestData.ts` for multi-room scenarios
    - Support amendment guest configurations (adding children)
    - Maintain unique email generation for test isolation
    - _Requirements: 3.3, 5.6, 7.7_
  - [x] 3.3 Add Search Criteria Constants
    - Create search criteria types for room availability checks
    - Support DOUBLE (2 adults, 0 children) and FAMILY (2 adults, 1 child) configurations
    - Include date range and location parameters
    - _Requirements: 2.1, 5.5_

- [x] 4. Phase 1: DLP Validation Implementation
  - [x] 4.1 Implement Basic DLP Navigation and Validation
    - Navigate to DLP URL and validate page load
    - Validate core page elements (hotels list, map, filters)
    - Fetch DLP content from AEM dictionary endpoint (direct REST call, outside the GraphQL library) for page-element cross-validation
    - Fetch DLP content from GraphQL API and cross-validate against AEM dictionary response using `validateAemDlpContentServiceDictionaryAgainstDlpInformationAPIResponse` equivalent
    - _Requirements: 1.1, 1.6_
  - [x] 4.2 Implement TripAdvisor Integration Testing
    - Validate TripAdvisor section display and reviews against the API response
    - Test review link opens a new tab focused on the HDP reviews section; build the expected URL as `/{country}/{language}/hotels{slug}.html` using the `slug` field from the first TripAdvisor review (the library exposes `slug` on `TripAdvisorReview` for exactly this)
    - Pass `tripAdvisorDataRequired: true` in the query params
    - _Requirements: 1.2_
  - [x] 4.3 Implement Map/Grid View Toggle Testing
    - Test map view and grid view switching functionality
    - Validate hotel card pins and information in map view
    - Test hotel card details and navigation to HDP from map view
    - Validate that Back action from HDP returns to DLP in map view (not grid view)
    - _Requirements: 1.3_
  - [x] 4.4 Implement Hotel Distance and Show More Testing
    - Test hotel distance display toggle based on AEM configuration
    - Validate "show more" hotels functionality and extended results
    - Validate that after "show more" in grid view, the extended hotel list also appears as pins on the map view (pin count matches the loaded hotel count)
    - Test distance hiding on the restricted DLP page (second DLP URL), on BOTH grid and map views
    - _Requirements: 1.4, 1.5_

- [x] 5. Phase 2: Hotel Search and Booking Flow
  - [x] 5.1 Implement Hotel Search Console Integration
    - Navigate to HDP by slug FIRST (`openHotelDetailsBySlug(hotel.slug)`), then use the search console FROM that page
    - Clear pre-filled location and search with criteria using suggested hotel name
    - Support hotel search with location, dates, and room criteria
    - Validate navigation to correct HDP with search parameters
    - _Requirements: 2.1, 2.2_
  - [x] 5.2 Implement HDP Validation and Rate Selection
    - Validate HDP page load and hotel information display
    - Cross-validate hotel title and slug with API data
    - Implement Flex rate selection and validation
    - Validate booking summary panel population
    - _Requirements: 2.3, 2.4, 2.5_
  - [x] 5.3 Implement Price Breakdown and Book Now
    - Validate price breakdown display and calculations
    - Test Book Now button click (scroll to button on mobile viewports first)
    - Dismiss Premier Plus room upgrade modal if present (`closePremierPlusRoomUpgradeModalIfPresent()`)
    - Handle optional bathroom selection interstitial (`clickContinueIfChooseBathroomPageIsDisplayed()`)
    - Validate navigation to ancillaries page
    - _Requirements: 2.6, 2.7_

- [x] 6. Phase 3: Complete Booking Flow Enhancement
  - [x] 6.1 Enhance Ancillaries Flow with Meals
    - Extend existing ancillaries page to support meal selection
    - On arrival at the ancillaries page: extract `basketReferenceId` from the URL (`getReservationIdFromUrl()` equivalent) and validate basket status is `OPEN` — this happens HERE, not at confirmation
    - Handle responsive viewport: expand booking summary section on mobile (`expandSectionOnMobile()`) before interacting
    - Implement breakfast addition for 2 adults with cost validation (pass `exactMatch: false` for the meal-title match — breakfast titles vary by locale/hotel)
    - Validate booking summary updates with meal costs, and validate the added adult meals per room
    - _Requirements: 3.1, 3.2, 4.1_
  - [x] 6.2 Integrate Guest Details with Address Handling
    - Enhance existing guest details page for complete form filling
    - Handle responsive viewport: toggle the booking summary on mobile before interacting
    - Support manual address entry and leisure reason selection
    - **Read back the app-normalised values after filling the form** and use those (not the typed values) for later assertions: `title`, `mobilePrefix`, `landlinePrefix`, `addressLine1`, `addressLine2`, `addressLine3`, `postalCode`. The app normalises/auto-fills these, so comparing against what was typed would produce false failures in `validateGuestDetailsAgainstBookingConfirmation` and the confirmation-page intro section.
    - _Requirements: 3.3, 3.4_
  - [x] 6.3 Enhance Payment Flow with Donations
    - Extend payment page to support donation selection (£3)
    - Validate total cost updates with donations included
    - Ensure POA + PIBA payment option selection
    - _Requirements: 3.5, 3.6_
  - [x] 6.4 Maintain Payment Details and 3D Secure
    - Verify existing PIBA card entry in Worldline iframe
    - Ensure 3D Secure challenge handling works correctly via Worldline/SIX hosted page (`paymentSixCardSolution3dSecureHostPage.confirmPayment()`)
    - Validate navigation to booking confirmation
    - _Requirements: 3.7, 3.8, 3.9_

- [x] 7. Confirmation + API Cross-Validation (within the confirmation test.step)
  - These tasks are implemented INSIDE the 'Confirmation + API Validation' test.step — they are not a separate journey phase. All validations CONSUME the shared helpers from the GraphQL API Client library; BL001 only calls them and asserts on the results.
  - [x] 7.1 Implement Basket Status and Reference Validation
    - Validate basket status is `COMPLETED` after payment (the `OPEN` check already happened at the ancillaries page — see task 6.1)
    - Fetch the basket by reference to obtain `bookingReference` (the guest-facing booking reference used for manage-booking search and all later assertions)
    - Cross-check basket details with UI displayed information
    - _Requirements: 4.1_
  - [x] 7.2 Validate Booking Confirmation Page UI Sections
    - Validate all four confirmation-page sections against API data (this is UI validation, performed before/alongside the Opera cross-check):
      - **Booking details intro**: booker details, expected booking reference ID, hotel information, hotel type
      - **Room details**: room detail headers, room dates section, and meals for all rooms (needs `basketReferenceId`)
      - **Hotel directions**: `directions` field from `getHotelInformation`
      - **Total cost**: `bookingConfirmation.totalCost`, `currencyCode`, and the Pay On Arrival label
    - _Requirements: 3.9, 4.2, 4.6, 4.7_
  - [x] 7.3 Implement Booking Confirmation (Opera) Data Validation
    - Cross-validate the persisted reservation via the getBookingConfirmation GraphQL response (the source the baseline treats as "Opera data")
    - Retrieve `bookingFlowId` AFTER booking confirmation (not during preconditions): `getBookingFlowIdBasedOnHotelAndRate({ hotelId })`
    - Fetch meals packages using the real `bookingFlowId` and `basketReferenceId`, extract `adultMeals[0]` to build expected: `[{ description: name, totalQuantity: 2, unitPrice: price, packageCode: id }]`
    - Validate hotel, staying dates, room occupancy, and room types
    - Validate rate plan code, rates per night, per-room price, total room price, and currency
    - Validate booking flow ID, meal packages per room, and deposit policies for all rooms (policy code `D1A`)
    - Validate total cost (with donation, no amendment), payment card, city tax (`hasCityTax = true`), and guest details
    - _Requirements: 4.3, 4.5, 4.8, 4.9, 4.10, 4.11, 4.12, 4.13, 4.14_
  - [x] 7.4 Implement Hotel and Content API Validation
    - Cross-validate hotel information with content API
    - Verify meal packages pricing and availability
    - Ensure data consistency across UI and backend systems
    - _Requirements: 4.4, 4.5_

- [x] 8. Phase 5: Amendment Flow Implementation
  - [x] 8.1 Implement Manage Booking Modal Integration
    - Add manage booking modal access via header navigation
    - Support booking search by reference, surname, and arrival date
    - Validate booking information card display with correct details
    - _Requirements: 5.1, 5.2, 5.3_
  - [x] 8.2 Implement Room Type Amendment
    - Support amendment flow initiation via "Amend Booking" button
    - Implement room and guests section editing
    - Enable room type change from Double to Family by adding child, then confirm availability and update the room
    - **Indexing caution:** the original mixes conventions — `editSpecificRoom({ roomNumber: 1 })` is **1-indexed**, while `validateRoomInfoCardIncludesText({ roomIndex: 0 })` and `validateAdultsAndChildrenNumbersForRoomIndexes({ roomIndexesArray: [0] })` are **0-indexed**. Pick one convention for the new page objects and document it, to avoid an off-by-one bug.
    - _Requirements: 5.4, 5.5_
  - [x] 8.3 Implement Amendment Validation and Confirmation
    - Validate room availability checking for new configuration
    - Ensure cost remains unchanged per business rules: `validateTotalCostIsUpdated(totalCostWithDonations, false)` and `validateTotalCostAmountAndCurrency(totalCostWithDonations, UK_CURRENCY_CODE)`
    - Support amendment confirmation and navigation to confirmation page
    - Validate amendment success notification
    - Validate booking history card after amendment: booking reference ID, lead guest name, room guests ("2 Adults, 1 Child"), and Pay On Arrival total cost
    - _Requirements: 5.6, 5.7, 5.8, 5.9_

- [x] 9. Phase 6: Cancellation Flow Implementation
  - [x] 9.1 Implement Cancellation UI Flow
    - Support cancellation initiation via "Cancel Booking" button
    - Implement cancellation confirmation popup and processing
    - Display cancellation success message with booking reference
    - Explicitly close the cancel modal (`closeCancelModal()`) after the success message
    - _Requirements: 6.2, 6.3, 6.4, 6.5_
  - [x] 9.2 Implement Cancellation API Validation
    - Validate booking status as "Cancelled" via API after cancellation
    - Cross-check cancellation status in backend systems
    - _Requirements: 6.6_
  - [x] 9.3 Implement Cancellation UI Verification
    - Verify cancelled status display in UI after re-searching booking
    - Validate booking information shows cancelled state correctly
    - _Requirements: 6.1, 6.7_

- [x] 10. Test Integration and Structure
  - [x] 10.1 Create Main Baseline Test Spec
    - Create `qa/tests/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts`
    - Structure as a single `test()` block with `test.step()` for each logical phase
    - Implement proper test data flow and state management within the single test closure
    - _Requirements: 7.7_
  - [x] 10.2 Implement Test Step Structure
    - Use `test.step('Setup: Reset state and check availability', ...)` for preconditions
    - Use `test.step('DLP: Validate destination landing page', ...)` for DLP validation
    - Use `test.step('Search: Find and select hotel from HDP', ...)` for hotel search
    - Use `test.step('Booking: Complete with meals, guest details, and payment', ...)` for booking flow
    - Use `test.step('Confirmation + API Validation: Validate UI and cross-check booking data', ...)` for combined confirmation/API checks
    - Use `test.step('Amendment: Change room type Double→Family', ...)` for amendment
    - Use `test.step('Cancellation: Cancel and verify', ...)` for cancellation
    - Note: this is a single `test()` — NOT separate `describe` blocks — matching the old TC's single `it()` structure
    - _Requirements: All requirements_
  - [x] 10.3 Add Test Data Flow and State Management
    - Implement booking reference capture and sharing between phases
    - Support guest details and search criteria sharing across test phases
    - Add proper test isolation with unique identifiers
    - _Requirements: 3.3, 4.1, 5.2, 6.1_
  - [x] 10.4 Add Delimited Step Comments Throughout the Test
    - Precede each logical test step with a numbered step comment that describes the action and its expected outcome
    - Keep step numbering consistent with the journey order so the test maps back to the original baseline steps
    - Comment business intent rather than restating what well-named page object methods already convey
    - _Requirements: 8.1, 8.2, 8.3_
  - [x] 10.5 Implement Test Setup and Preconditions
    - Reset application state before the journey starts
    - Run the hotel availability pre-check (DOUBLE + FAMILY) before booking
    - Apply feature-toggle override: `release_pi_web_push_notifications: true`
    - Compute arrival dates dynamically (today + 3 days) and advance via availability helper until inventory is found
    - _Requirements: 9.1, 9.2, 9.3, 9.4_

- [x] 11. Verification and Quality Assurance
  - [x] 11.1 Update Page Object Index Exports
    - Update `qa/src/pages/pi/index.ts` to export new page objects
    - Ensure proper TypeScript imports and exports
    - _Requirements: 7.5_
  - [x] 11.2 Create Checkpoint Validation Tasks
    - Add intermediate validation points throughout the test flow
    - Implement proper error handling and meaningful error messages
    - Support test continuation from known good states
    - _Requirements: All phases_
  - [x] 11.3 Run Full Test Suite Validation
    - Execute complete baseline test in UAT environment
    - Validate 100% functional parity with the original WebDriverIO test
    - Verify all API cross-validations and UI interactions work correctly
    - Confirm each test step is delimited by a clear, descriptive comment
    - _Requirements: All requirements_

## Notes

### Key Decisions
- **Functional Parity, Not Cloning**: The new test reproduces the same actions and validations as the original WebDriverIO test but uses idiomatic Playwright/TypeScript approaches rather than mirroring the old implementation.
- **Reuse and POM Grouping**: Page interactions, API helpers, and utilities are written for reuse and grouped per the Page Object Model (page objects in `src/pages/pi/`, API helpers in `src/api/`, test data in `src/constants/`). Extend existing objects before creating new ones.
- **Single Spec File**: The complete baseline test remains in one spec file as a single `test()` block with `test.step()` sub-divisions to maintain its atomic nature while organizing logical phases. This matches the original test's single `it()` structure and avoids the need for state-sharing fixtures.
- **Delimited Step Comments**: Each test step is preceded by a clear, numbered comment (Requirement 8) so the journey is easy to follow and to map back to the original baseline.
- **Always Run the Full Journey**: The migrated test always executes booking → amendment → cancellation. The baseline's optional `validateOnlyBookingFlow` early-exit (which can stop after booking) is intentionally NOT ported.
- **API Layer Extracted**: The GraphQL client and API validation helpers are not built in BL001. They are delivered by the GraphQL API Client & Validation Helpers spec (`.kiro/specs/QA/SharedLibraries/GraphQLApiClient/`) and consumed here, so the API layer is built once and reused across tests.

### Testing Notes
- **UAT Environment Required (DIT-compatible)**: Test targets UAT for now but must not hardcode UAT-specific URLs or credentials. Environment switching via `ENV=uat|dit` in `playwright.config.ts` must work without code changes. Chromium headed mode is required due to Akamai WAF restrictions (applies to both environments).
- **Extended Timeouts**: Booking confirmation and payment processing require extended timeouts (up to 120s)
- **State Dependency**: Each phase depends on successful completion of previous phases - proper error handling and state validation required
- **API Rate Limiting**: GraphQL API calls should be throttled appropriately to avoid rate limiting during validation phases

### Property-Based Testing Considerations
- **Room Availability**: Property-based testing not suitable due to dependency on live hotel inventory
- **Date Ranges**: Dates are computed dynamically (arrival = today + 3 days) and advanced by the availability helper until inventory is found — see Req 9.4. They are not hardcoded, so property-based date generation would conflict with the availability-driven approach.
- **Payment Processing**: Live payment integration requires deterministic test card data
- **Amendment Logic**: Business rules for cost calculations require specific test scenarios rather than property-based generation

## Task Dependency Graph
```json
{
  "waves": [
    {
      "id": 0,
      "tasks": ["1.1"]
    },
    {
      "id": 1,
      "tasks": ["2.1", "2.2", "2.3", "2.4", "3.1", "3.2", "3.3"]
    },
    {
      "id": 2,
      "tasks": ["4.1", "4.2", "4.3", "4.4"]
    },
    {
      "id": 3,
      "tasks": ["5.1", "5.2", "5.3"]
    },
    {
      "id": 4,
      "tasks": ["6.1", "6.2", "6.3", "6.4"]
    },
    {
      "id": 5,
      "tasks": ["7.1", "7.2", "7.3", "7.4"]
    },
    {
      "id": 6,
      "tasks": ["8.1", "8.2", "8.3"]
    },
    {
      "id": 7,
      "tasks": ["9.1", "9.2", "9.3"]
    },
    {
      "id": 8,
      "tasks": ["10.1", "10.2", "10.3", "10.4", "10.5"]
    },
    {
      "id": 9,
      "tasks": ["11.1", "11.2", "11.3"]
    }
  ]
}
```
