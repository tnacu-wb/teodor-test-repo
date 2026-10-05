---
parent_design: "none (derived from steering + industry standards)"
jira: "none"
---
# Requirements Document

## Introduction
This spec defines the complete migration of the baseline test `end2endBaselineGuestUKHotelOneNightOneRoomFlexPOAPIBAChangeRoomType.spec.js` from the old WebDriverIO + JavaScript framework (located in `qa/reference/`) to the new Playwright + TypeScript framework (located in `qa/`). The migration must achieve 100% functional parity with the original test while leveraging the new framework's improved maintainability and reliability patterns.

The implementation objective is to run against **UAT only** for now, but the test must be written to be **compatible with DIT (dev) in the future** — meaning no UAT-specific hardcoding of URLs, credentials, or environment assumptions. Environment switching is handled by the existing `ENV` variable in `playwright.config.ts` (uat/dit), and the test must use that abstraction rather than baking in UAT-specific values.

The main objective is that the new test performs the exact same actions and validations as the old one. It is not a goal to reproduce the old test step-by-step or replicate its implementation patterns. Where Playwright or TypeScript offer a better, more idiomatic, or more reliable way to achieve the same action, those capabilities should be used instead of cloning the old approach verbatim.

## Dependencies
This spec consumes a shared GraphQL API client and its query/validation helpers that are delivered by a separate spec: **GraphQL API Client & Validation Helpers** (`.kiro/specs/QA/SharedLibraries/GraphQLApiClient/`). BL001 does not build the GraphQL client, the query wrappers, or the API validation helpers itself — it treats them as an external prerequisite and consumes their published, tested interfaces. All API cross-validation (Requirement 4), the availability pre-check (Requirement 9.2), and DLP content cross-checks (Requirement 1.6) depend on that library being available.

## Glossary
- **DLP**: Destination Landing Page - page displaying hotels in a region with filters and maps
- **HDP**: Hotel Details Page - individual hotel page with rooms, rates, and booking
- **PIBA**: Premier Inn Business Account Card - specific payment method for business bookers
- **POA**: Pay On Arrival - payment option where guest pays at hotel, not online
- **TripAdvisor Integration**: Third-party reviews displayed on hotel pages
- **GraphQL API**: Backend API used for validation and cross-reference checks
- **Amendment Flow**: Process to modify existing booking (room type, dates, guests)
- **Opera**: Backend reservation system; in this test, "Opera data" refers to the `getBookingConfirmation` GraphQL response (which reflects what was persisted), not a direct Opera API call

## Requirements

### Requirement 1: Destination Landing Page Validation
**User Story:** As a QA engineer, I want to validate all DLP functionality so that I can ensure the page displays correctly with all expected elements and integrations.

#### Acceptance Criteria
1. THE system SHALL validate DLP page loads with all core elements (hotels list, map, filters)
2. THE system SHALL validate TripAdvisor integration displays reviews and links correctly
3. THE system SHALL validate map view/grid view toggle functionality works
4. THE system SHALL validate hotel distance display toggle based on AEM configuration
5. THE system SHALL validate "show more" hotels functionality expands results
6. THE system SHALL cross-validate DLP content against GraphQL API responses
7. WHEN the custom notification popup is displayed THE test SHALL dismiss it in order to proceed with the journey
8. THE test SHALL keep explicit assertion of the notification popup's close-count/last-closed behaviour out of scope while it remains disabled in the baseline (blocked by CTECH-1604), matching the original test

### Requirement 2: Hotel Search and Selection Flow
**User Story:** As a QA engineer, I want to validate the hotel search and booking initiation so that guests can successfully find and select hotels.

#### Acceptance Criteria
1. THE system SHALL search for hotels using search console with location and dates
2. THE system SHALL navigate to Hotel Details Page for selected hotel
3. THE system SHALL validate hotel information matches API data (title, slug presence)
4. THE system SHALL select Flex rate option and validate selection
5. THE system SHALL validate booking summary panel populates correctly
6. THE system SHALL validate price breakdown display and calculation
7. THE system SHALL initiate booking flow via Book Now button

### Requirement 3: Complete Booking Flow with Ancillaries and Payment
**User Story:** As a guest, I want to complete a full booking with meals and payment so that I can secure my reservation.

#### Acceptance Criteria
1. THE system SHALL dismiss the Premier Plus room upgrade modal if it appears after clicking Book Now, then handle the optional bathroom selection interstitial
2. THE system SHALL add breakfast meals for 2 adults and validate cost calculation
3. THE system SHALL capture all guest details (personal, address, contact)
4. THE system SHALL select leisure reason for stay
5. THE system SHALL add £3 donation and validate total cost update
6. THE system SHALL select Pay On Arrival + PIBA card payment options
7. THE system SHALL complete payment via Worldline iframe with PIBA card details
8. THE system SHALL handle 3D Secure challenge if presented
9. THE system SHALL validate the booking confirmation page across all four sections: booking details intro (booker details, booking reference, hotel information, hotel type), room details (headers, dates, meals for all rooms), hotel directions, and total cost (amount, currency, Pay On Arrival label)

### Requirement 4: Comprehensive API Cross-Validation
**User Story:** As a QA engineer, I want to validate all booking data against backend APIs so that I can ensure data integrity across systems.

#### Acceptance Criteria
1. THE system SHALL validate basket status transitions: `OPEN` on arrival at the ancillaries page (with `basketReferenceId` extracted from the URL at that point), and `COMPLETED` after payment succeeds
2. THE system SHALL validate booking confirmation data matches GraphQL API
3. THE system SHALL validate Opera reservation data matches booking details
4. THE system SHALL validate hotel information via content API
5. THE system SHALL validate meal packages pricing and details
6. THE system SHALL validate payment method and amount in backend systems
7. THE system SHALL validate guest details storage and retrieval
8. THE test SHALL validate rate plan code and room types against the booking confirmation data
9. THE test SHALL validate rates per night, per-room price, and total room price
10. THE test SHALL validate the currency code
11. THE test SHALL validate the booking flow ID for the hotel and rate
12. THE test SHALL validate deposit policies for all rooms
13. THE test SHALL validate city tax applicability for the hotel
14. THE test SHALL validate the total cost (including the donation, before any amendment) against the selected Pay On Arrival payment method

### Requirement 5: Booking Amendment Functionality
**User Story:** As a guest, I want to modify my booking room type so that I can accommodate changed travel needs.

#### Acceptance Criteria
1. THE system SHALL open manage booking modal via header navigation
2. THE system SHALL search for booking using reference and guest surname
3. THE system SHALL display booking information card with correct details
4. THE system SHALL initiate amendment flow via "Amend Booking" button
5. THE system SHALL change room type from Double to Family by adding child
6. THE system SHALL validate room availability for new configuration
7. THE system SHALL confirm price remains unchanged (per business rules)
8. THE system SHALL save changes and redirect to confirmation page
9. THE system SHALL validate amendment success notification display

### Requirement 6: Booking Cancellation Flow
**User Story:** As a guest, I want to cancel my booking so that I can avoid charges when plans change.

#### Acceptance Criteria
1. THE system SHALL access booking via manage booking modal
2. THE system SHALL initiate cancellation via "Cancel Booking" button
3. THE system SHALL display cancellation confirmation popup
4. THE system SHALL process cancellation confirmation
5. THE system SHALL display cancellation success message with booking reference
6. THE system SHALL validate the reservation is cancelled via the entity API (reservation-level check), matching the original test's `validateReservationIsCancelled(hotelId, basketDetails)` — this is a deeper check than just basket status
7. THE system SHALL verify cancelled status display in UI after re-searching booking

### Requirement 7: New Framework Infrastructure Components
**User Story:** As a developer, I want the test to reuse shared infrastructure so that the API layer is built once (in the GraphQL API Client spec) and consumed by many tests.

#### Acceptance Criteria
1. THE test SHALL consume the shared GraphQL client (delivered by the GraphQL API Client spec) rather than defining its own
2. THE test SHALL consume the shared basket API validation helpers from that library
3. THE test SHALL consume the shared content API validation helpers from that library
4. THE test SHALL consume the shared booking-confirmation ("Opera") validation helpers from that library
5. THE system SHALL provide new page objects for DLP, manage booking, and amendment flows
6. THE system SHALL provide booking history validation components
7. THE system SHALL maintain existing test data constants and patterns


### Requirement 8: Test Step Readability
**User Story:** As a QA engineer, I want clearly commented test steps so that I can quickly identify which part of the journey each block of code corresponds to.

#### Acceptance Criteria
1. THE test SHALL include comments that clearly show and delimit each test step
2. EACH test step comment SHALL describe the action being performed and its expected outcome
3. THE comments SHALL make it easy to map the Playwright test back to the original baseline test steps

### Requirement 9: Test Setup and Preconditions
**User Story:** As a QA engineer, I want deterministic setup and precondition checks so that the journey (including the Double-to-Family amendment) runs reliably.

> **Implementation reference:** The basic auth (Akamai WAF) and initial page load pattern (consent cookies pre-set, notification popup dismissal) has already been solved in the POC test at `qa/tests/pi/homepage.spec.ts` and its page object `qa/src/pages/pi/home.page.ts`. BL001 MUST reuse that approach first — specifically: `httpCredentials` from `playwright.config.ts` for WAF auth, pre-setting consent cookies via `page.context().addCookies()` before navigation, and dismissing the notification popup after page load. Do not reinvent these patterns.

#### Acceptance Criteria
1. THE test SHALL reset application state before starting the journey (reusing the cookie-preset and popup-dismissal pattern from `qa/tests/pi/homepage.spec.ts`)
2. THE test SHALL confirm via API that the selected hotel has availability for both a DOUBLE room (2 adults) and a FAMILY room (2 adults, 1 child) for the chosen dates before beginning the booking; however, only the DOUBLE room is booked initially — the FAMILY availability is a precondition solely for the later Double-to-Family amendment to succeed
3. THE test SHALL apply the feature-toggle override `release_pi_web_push_notifications: true` (which enables the custom notification prompt validated in Req 1.7) to reproduce the baseline's runtime configuration
4. THE test SHALL compute arrival dates dynamically using a relative offset (e.g. today + N days) and advance them via the availability helper until inventory is found, matching the original test's approach of deriving dates from live availability rather than hardcoding them
5. THE test SHALL use the following test data values: hotel has city tax (`hasCityTax = true`), the expected deposit policy code for POA is `D1A`, primary DLP URL is `hotels/england/west-midlands/birmingham.html`, distance-hidden DLP URL is `hotels/england/bedfordshire/luton.html`, and the hotel is `DEFAULT_HOTEL`
