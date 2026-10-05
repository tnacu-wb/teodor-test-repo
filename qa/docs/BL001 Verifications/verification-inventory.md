# Verification Inventory — BL001

**Test:** `qa/tests/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts`
**Journey:** Guest UK Hotel 1-night 1-room Flex POA PIBA + Amendment + Cancellation
**Total assertions:** ~75 across 13 categories

Assertions are marked `hard` (fails the test) or `soft` (logs a warning and continues).

---

## 1. Inventory Preconditions

Confirms the hotel actually has sellable rooms for both the initial and post-amendment configurations before the journey starts.

| # | Assertion | Type |
|---|-----------|------|
| 1.1 | DOUBLE room (2 adults) availability exists for the target dates | hard |
| 1.2 | FAMILY room (2 adults + 1 child) availability exists, needed later by the amendment | hard |
| 1.3 | Dates auto-advance and are propagated to both criteria objects when inventory is missing | hard |

---

## 2. Page Load / Navigation

Each screen in the funnel renders its identifying element before the test interacts with it.

| # | Assertion | Type |
|---|-----------|------|
| 2.1 | DLP page wrapper visible (both Birmingham and Luton) | hard |
| 2.2 | HDP hotel title heading visible | hard |
| 2.3 | Ancillaries meals heading visible | hard |
| 2.4 | Guest details form visible | hard |
| 2.5 | Payment page card-details container visible | hard |
| 2.6 | Booking confirmation container visible within 120s | hard |
| 2.7 | Amendment page title visible | hard |
| 2.8 | Post-3DS redirect lands on the confirmation page | hard |

---

## 3. Content Integrity Across Three Sources

Verifies the same DLP content is consistent in AEM (authoring), GraphQL (delivery), and the rendered page.

| # | Assertion | Type |
|---|-----------|------|
| 3.1 | AEM ↔ GraphQL: title, description, picture | hard |
| 3.2 | AEM ↔ GraphQL: hotel list length and each hotel code | hard |
| 3.3 | AEM ↔ GraphQL: Why-section item count, FAQ section count and titles | hard |
| 3.4 | AEM ↔ GraphQL: map latitude, longitude, `hideHotelDistance` flag | hard |
| 3.5 | AEM ↔ GraphQL: popular-destinations item count | hard |
| 3.6 | UI ↔ AEM: Why-section item count and each item title | hard |
| 3.7 | UI ↔ AEM: FAQ tab presence per AEM section | hard |
| 3.8 | UI: "Explore" destinations section is populated | hard |

---

## 4. TripAdvisor Integration

Checks the third-party review data reaches the page correctly and the review link routes to the right hotel.

| # | Assertion | Type |
|---|-----------|------|
| 4.1 | Number of rating images equals the number of hotels returned by the API | hard |
| 4.2 | Each rating image `src` matches the half-star-rounded rating from the API | hard |
| 4.3 | Review link opens a new tab whose URL matches `/{country}/{language}/hotels{slug}.html` built from the API's `slug` | hard |

---

## 5. Hotel Listing and Map Presentation

Confirms the same hotel set is represented consistently in grid view and map view, including after pagination.

| # | Assertion | Type |
|---|-----------|------|
| 5.1 | Grid: hotel cards visible with thumbnail and "View hotel" button | hard |
| 5.2 | Grid: card count greater than zero after expanding all results | hard |
| 5.3 | Map: marker count equals hotels + 1 region marker | soft |
| 5.4 | Map: region marker present at the DLP's coordinates | soft |
| 5.5 | Map: clicking a marker opens a card with matching hotel name, thumbnail, and button | soft |
| 5.6 | Map pin count equals grid card count after "show more" | soft |

---

## 6. Configuration-Driven Display

Verifies the AEM `hideHotelDistance` flag actually controls whether distance appears, rather than asserting a fixed expectation.

| # | Assertion | Type |
|---|-----------|------|
| 6.1 | Primary DLP: distance visible on the first card and on every card | hard |
| 6.2 | Restricted DLP: reads the live AEM flag, then asserts hidden or visible accordingly | hard |
| 6.3 | Restricted DLP map view: distance hidden on map cards when the flag is set | soft |

---

## 7. Pricing and Cost Arithmetic

Tracks the price from rate selection through meals and donation to the final total, checking each transition.

| # | Assertion | Type |
|---|-----------|------|
| 7.1 | HDP price breakdown section visible and formatted as currency + digits | hard |
| 7.2 | HDP breakdown total captured and non-empty | hard |
| 7.3 | Ancillaries booking-summary total non-empty after meal selection | hard |
| 7.4 | Payment total changes after selecting the £3 donation | hard |
| 7.5 | Confirmation total contains the captured post-donation amount | hard |
| 7.6 | Confirmation total includes the expected currency | hard |
| 7.7 | Amendment total does not change after the occupancy change | soft |

---

## 8. Basket Lifecycle

Asserts the basket state machine advances correctly at the three points where its status is meaningful.

| # | Assertion | Type |
|---|-----------|------|
| 8.1 | `OPEN` when the ancillaries page is reached | hard |
| 8.2 | `COMPLETED` after payment and 3DS | hard |
| 8.3 | `CANCELLED` after cancellation | soft |
| 8.4 | Basket reference extractable from the ancillaries URL, and guest-facing booking reference present on the basket | hard |

---

## 9. Reservation Persistence (Opera Cross-Check)

The heaviest group — confirms what the guest saw in the UI is what actually persisted in the reservation system.

| # | Assertion | Type |
|---|-----------|------|
| 9.1 | Hotel ID and hotel name | soft |
| 9.2 | Arrival and departure dates | soft |
| 9.3 | Room count, adults per room, children per room | soft |
| 9.4 | Room type (accepts room name or type code; warns on upgrades such as `PPLDBL`) | soft |
| 9.5 | Rate plan code is `FLEXRATE` | soft |
| 9.6 | Rates per night match the captured breakdown | soft |
| 9.7 | Per-room total price and per-night prices | soft |
| 9.8 | Currency code is `GBP` | soft |
| 9.9 | `bookingFlowId` matches the value fetched for this hotel and rate | soft |
| 9.10 | Meal package code present once per adult in the room | soft |
| 9.11 | Deposit policy code is `D1A` on every room | soft |
| 9.12 | Total cost with no discounts and no amendment applied | soft |
| 9.13 | Payment card last four digits match the PIBA test card | soft |
| 9.14 | City tax present on at least one night for a taxed hotel | soft |
| 9.15 | Guest first name, last name, email, and title match billing | soft |

---

## 10. Guest Data Integrity

Guards against comparing typed input to app-normalised output — the app rewrites titles, phone prefixes, and address lines.

| # | Assertion | Type |
|---|-----------|------|
| 10.1 | Normalised title read back from the form is non-empty | hard |
| 10.2 | Normalised values (title, mobile prefix) are what get compared against the reservation, not the typed values | soft |
| 10.3 | Confirmation greeting contains the guest's first name | hard |

---

## 11. Payment Method and Policy

Verifies the Pay-on-Arrival + PIBA card path completes, including the 3-D Secure hop.

| # | Assertion | Type |
|---|-----------|------|
| 11.1 | Card details entered inside the Worldline iframe and submitted | hard |
| 11.2 | 3DS challenge handled (or correctly skipped when not presented) | hard |
| 11.3 | Confirmation payment label references payment on arrival | soft |
| 11.4 | Deposit policy on the reservation is the POA code `D1A` | soft |
| 11.5 | Masked card on the reservation matches the card used | soft |

---

## 12. Amendment Correctness

Confirms an occupancy change is applied, reflected in the UI, and priced as expected.

| # | Assertion | Type |
|---|-----------|------|
| 12.1 | Booking found by reference, surname, and arrival date | soft |
| 12.2 | Booking reference appears on the booking-details page | soft |
| 12.3 | Amendment page reachable via the Amend button | soft |
| 12.4 | Room info card shows "1 child" after the change | soft |
| 12.5 | Adults and children counts on the amended room | soft |
| 12.6 | Total cost unchanged, with correct amount and currency | soft |
| 12.7 | Amendment success notification displayed | soft |
| 12.8 | Booking history card: reference, lead guest name, "2 Adults, 1 Child", POA total | soft |

---

## 13. Cancellation Confirmation

Checks the guest-facing confirmation and the backend state agree that the booking is gone.

| # | Assertion | Type |
|---|-----------|------|
| 13.1 | Cancel confirmation modal reached and confirmed | soft |
| 13.2 | Success alert visible with checkmark and the booking reference in the message | soft |
| 13.3 | Modal explicitly closed | soft |
| 13.4 | Basket status is `CANCELLED` via API | soft |

---

## Note for Hardening

The `soft` markers in groups 9 and 12–13 are a deliberate POC decision, not a permanent state. They were introduced to surface every remaining mismatch in a single run rather than one per run. Before this goes into a regression pack, each one needs classifying:

- **Harden** — real checks that are now calibrated (basket `CANCELLED`, amendment occupancy, cancellation success message, most Opera fields)
- **Keep soft** — genuine environment variance (room upgrades, restaurant closures, net-vs-gross price differences, Google Maps availability)

Left as-is, group 9 is a suite of fifteen checks that can all fail silently.
