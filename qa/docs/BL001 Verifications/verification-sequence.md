# Verification Sequence — BL001

**Test:** `qa/tests/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts`
**Journey:** Guest UK Hotel 1-night 1-room Flex POA PIBA + Amendment + Cancellation

Assertions listed in exact execution order, grouped by `test.step()`. Marked `hard` (fails the test) or `soft` (logs warning, continues).

---

## Step 1: Setup — Reset state and check availability

| # | Verification | Type |
|---|-------------|------|
| 1 | Homepage loads and basic auth session is established with Akamai WAF | hard |
| 2 | DOUBLE room (2 adults, 0 children) availability confirmed for the hotel | hard |
| 3 | FAMILY room (2 adults, 1 child) availability confirmed for the hotel | hard |

---

## Step 2: DLP — Validate destination landing page

| # | Verification | Type |
|---|-------------|------|
| 4 | Birmingham DLP page wrapper visible | hard |
| 5 | AEM dictionary response fetched successfully | hard |
| 6 | GraphQL DLP content response fetched successfully | hard |
| 7 | AEM ↔ GraphQL: title matches | hard |
| 8 | AEM ↔ GraphQL: description matches | hard |
| 9 | AEM ↔ GraphQL: picture matches (null-safe) | hard |
| 10 | AEM ↔ GraphQL: hotel codes and count match | hard |
| 11 | AEM ↔ GraphQL: Why-section item count matches | hard |
| 12 | AEM ↔ GraphQL: FAQ section count and titles match | hard |
| 13 | AEM ↔ GraphQL: map coordinates and hideHotelDistance match | hard |
| 14 | AEM ↔ GraphQL: popular-destinations item count matches | hard |
| 15 | UI: hotel cards visible on the page | hard |
| 16 | UI ↔ AEM: Why-section items count and each title matches | hard |
| 17 | UI: FAQ tabs present and each AEM FAQ title appears | hard |
| 18 | UI: "Explore" destinations section is populated | hard |
| 19 | TripAdvisor rating image count equals API hotel count | hard |
| 20 | Each TripAdvisor rating image `src` matches the API half-star-rounded value | hard |
| 21 | TripAdvisor review link opens new tab with correct HDP URL | hard |
| 22 | Map view toggle activates map (or soft-skips if unavailable) | soft |
| 23 | Map: marker count equals hotels + 1 region marker | soft |
| 24 | Map: region marker at DLP coordinates | soft |
| 25 | Map: hotel card opens with correct name, thumbnail, button | soft |
| 26 | Grid view: hotel cards have thumbnail and "View hotel" button | hard |
| 27 | Primary DLP: distance visible on first card | hard |
| 28 | Primary DLP: distance visible on ALL cards | hard |
| 29 | Show more: all hotels loaded, card count > 0 | hard |
| 30 | Map pins match grid card count after show more | soft |
| 31 | Luton DLP page wrapper visible | hard |
| 32 | Luton DLP: distance hidden or visible based on live AEM `hideHotelDistance` flag | hard |

---

## Step 3: Search — Find and select hotel from HDP

| # | Verification | Type |
|---|-------------|------|
| 33 | HDP loaded with hotel title visible | hard |
| 34 | Hotel title exists in API response (non-empty) | hard |
| 35 | Flex rate selected successfully | hard |
| 36 | Price breakdown section visible with currency + digits | hard |
| 37 | Price breakdown total captured and non-empty | hard |

---

## Step 4: Booking — Complete with meals, guest details, and payment

### Ancillaries

| # | Verification | Type |
|---|-------------|------|
| 38 | Ancillaries page loaded | hard |
| 39 | Basket reference ID extracted from URL (non-empty) | hard |
| 40 | Basket status is `OPEN` | hard |
| 41 | Meals heading visible (or skip if restaurant closed) | conditional |
| 42 | Breakfast added for 2 adults | conditional |
| 43 | Booking summary total non-empty after meal selection | conditional |
| 44 | Adult meals per room contains "2" | conditional |

### Guest Details

| # | Verification | Type |
|---|-------------|------|
| 45 | Guest details form loaded | hard |
| 46 | Normalised guest title read back is non-empty | hard |

### Payment

| # | Verification | Type |
|---|-------------|------|
| 47 | Payment page loaded | hard |
| 48 | Total cost captured before donation (non-empty) | hard |
| 49 | Total cost changes after selecting £3 donation | hard |
| 50 | Total cost with donation captured (non-empty) | hard |

### Payment Details (Worldline iframe)

| # | Verification | Type |
|---|-------------|------|
| 51 | Payment iframe visible and card details entered | hard |
| 52 | Payment submitted within iframe | hard |

### 3D Secure

| # | Verification | Type |
|---|-------------|------|
| 53 | 3DS challenge handled (confirmed or skipped) | hard |
| 54 | Redirect to booking confirmation page within 120s | hard |

---

## Step 5: Confirmation + API Validation

### UI Validation

| # | Verification | Type |
|---|-------------|------|
| 55 | Confirmation page loaded | hard |
| 56 | Booking reference present on confirmation page (non-empty) | hard |
| 57 | Basket status is `COMPLETED` | hard |
| 58 | Basket contains guest-facing booking reference | hard |
| 59 | `bookingFlowId` retrieved from API (non-empty) | hard |
| 60 | Confirmation greeting contains guest first name | hard |
| 61 | Confirmation shows hotel name | hard |
| 62 | Room dates displayed in "DD MMM" format matching search dates | hard |
| 63 | Meals section present with "Adult" text (or skipped) | soft |
| 64 | Hotel directions section matches API (partial) | soft |
| 65 | Total cost amount matches captured value | hard |
| 66 | Payment label references "arrival" | soft |

### Opera API Cross-Validation

| # | Verification | Type |
|---|-------------|------|
| 67 | Booking confirmation fetched from GraphQL | soft |
| 68 | Hotel ID and name match | soft |
| 69 | Arrival and departure dates match search criteria | soft |
| 70 | Room count, adults, children match | soft |
| 71 | Room type contains expected code (warns on upgrades) | soft |
| 72 | Rate plan code is `FLEXRATE` | soft |
| 73 | Rates per night match UI price breakdown | soft |
| 74 | Per-room total and per-night prices match | soft |
| 75 | Currency is `GBP` | soft |
| 76 | Booking flow ID matches | soft |
| 77 | Meal package code present once per adult | soft |
| 78 | Deposit policy code is `D1A` on all rooms | soft |
| 79 | Total cost (no discounts, no amendment) matches | soft |
| 80 | Payment card last 4 digits match PIBA test card | soft |
| 81 | City tax present on at least one night | soft |
| 82 | Guest first name, last name, email, title match billing | soft |

---

## Step 6: Amendment — Change room type Double → Family

| # | Verification | Type |
|---|-------------|------|
| 83 | Booking reference available from confirmation step | hard |
| 84 | Manage Booking modal opens from header | soft |
| 85 | Booking found by reference, surname, arrival date | soft |
| 86 | Booking reference appears on booking-details page | soft |
| 87 | Amend button clicked, amendment page reached | soft |
| 88 | Amendment page title visible ("Amend your booking") | soft |
| 89 | Room and Guests section expanded | soft |
| 90 | Edit room modal opened for Room 1 | soft |
| 91 | Children set to 1 via dropdown | soft |
| 92 | Check availability triggered | soft |
| 93 | Room updated successfully | soft |
| 94 | Room info card contains "1 child" | soft |
| 95 | Room info card shows correct adults and children counts | soft |
| 96 | Total cost unchanged after amendment | soft |
| 97 | Total cost amount and currency (£) correct | soft |
| 98 | Amendment confirmed ("Confirm changes" clicked) | soft |
| 99 | Amendment success notification displayed | soft |
| 100 | Booking history: reference matches | soft |
| 101 | Booking history: lead guest name matches | soft |
| 102 | Booking history: room guests show "2 Adults, 1 Child" | soft |
| 103 | Booking history: POA total cost matches | soft |

---

## Step 7: Cancellation — Cancel and verify

| # | Verification | Type |
|---|-------------|------|
| 104 | Booking reference and basket reference available | hard |
| 105 | Manage Booking modal opens from HDP header | soft |
| 106 | Booking found by reference, surname, arrival date | soft |
| 107 | Cancel Booking button clicked | soft |
| 108 | Cancellation confirmed in modal | soft |
| 109 | Success alert visible with checkmark | soft |
| 110 | Success alert contains booking reference | soft |
| 111 | Cancel modal closed | soft |
| 112 | Basket status is `CANCELLED` via API | soft |

---

## Summary

| Step | Hard | Soft/Conditional | Total |
|------|------|------------------|-------|
| 1. Setup | 3 | 0 | 3 |
| 2. DLP | 22 | 5 | 27 |
| 3. Search | 5 | 0 | 5 |
| 4. Booking | 14 | 4 | 18 |
| 5. Confirmation UI | 9 | 3 | 12 |
| 5. Confirmation API | 0 | 16 | 16 |
| 6. Amendment | 1 | 20 | 21 |
| 7. Cancellation | 1 | 8 | 9 |
| **Total** | **55** | **56** | **~112** |
