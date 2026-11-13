# Regression Pack Rationalisation — PI + CCUI

Companion to `regression-pack-analysis.md`. Identifies redundant tests in the PI and CCUI
regression packs and specifies the consolidations that replace them.

PIB (121 tests) is covered separately in Appendix A. See also
`regression-pack-migration-strategy.md` for the full-pack plan.

---

## 1. Purpose

Reduce PI + CCUI regression runtime without losing verification depth. The pack is
80 spec files; the question was how much of it is genuinely duplicated.

**Answer: 21% is removable at zero loss of assertion coverage.** Considerably less than
the surface duplication in the test titles suggests.

---

## 2. Method

Source of truth is the spec code in `qa/reference/test/specs/{pi,ccui}/`, not the test
titles. Three overlap metrics were computed per pair:

| Metric            | Definition                                                                       | Answers                |
| ----------------- | -------------------------------------------------------------------------------- | ---------------------- |
| Step overlap      | Jaccard on the page-object call sequence                                         | wasted execution time  |
| Assertion overlap | Jaccard on the `BookingConfirmationHelpers` `OhipHelpers` `ApiHelpers` `validateX` set | wasted verification |
| Sole ownership    | assertions exercised by exactly one spec                                         | what is uncuttable     |

**Protected unit: assertion coverage.** A spec is only cuttable if every assertion it
makes has at least two other owners. Nothing was ring-fenced by policy.

Note for anyone repeating this: `qa/reference` is git-ignored (nested `.git`), so
`grep` / ripgrep returns nothing there. Files must be opened by explicit path.

---

## 3. Findings

### 3.1 There is one shared backbone

Nearly every PI baseline executes the same ~35-step sequence:

```text
resetApplicationState -> getSearchCriteriaAndHotelAvailability -> hotel details
-> select rate -> validate summary + price-per-night breakdown -> Book now
-> chooseYourBathroom (conditional) -> ancillaries + basket STATUS_OPEN
-> guest details (reason for stay, booker, billing) -> payment page + total cost
-> confirmCurrentBooking(payOption, card) -> BIC -> basket STATUS_COMPLETED
-> BookingConfirmationHelpers: hotel, stayingDates, roomsOccupancy, roomTypes,
   ratePlan, policyCode, ratesPerNight, bookingFlowId, roomPrice, currency, paymentCard
```

CCUI has its own ~45-step equivalent:

```text
login -> SRP -> HDP -> rate -> ancillaries -> GDP -> Eckoh tokenisation
-> T&C modal -> confirm booking -> ID&V / DPA override -> amend -> cancel
-> 17 fixed Opera assertions
```

Amend-focused specs use a different opening: seed via
`ApiReservationCalls.createReservationViaApi` + `confirmReservationViaApi`, then drive the
amend through the UI.

Because the backbone is shared, **step overlap is high everywhere and is not by itself
evidence of redundancy.** Assertion overlap is the discriminator.

### 3.2 PI baselines are mostly not redundant

Of 35 PI baselines examined, roughly 28 are the sole owner of a distinct feature journey
that merely happens to share the booking backbone. Cutting them on "another UK Flex
booking" grounds would silently drop coverage.

### 3.3 CCUI baselines are redundant, but narrowly

The CCUI duplication is real and mechanical — four specs are effectively copy-paste — but
it does not extend across the whole pack. Most CCUI specs still carry one distinct hook
(TripAdvisor no-award, accessible-room notification, A2C routing, type-of-caller).

### 3.4 Measured overlap

| Cluster | Step | Assertion | Verdict |
|---|---:|---:|---|
| 381188 / 381189 / 381190 / 381191 — CCUI agent DE Flex Leisure PN | 96-98% | **100%** (17/17) | 4 to 1 |
| 389724 / 389727 — PI someone-else RWCC | 92% | **100%** (11/11) | both cut |
| 530589 / 531538 — PI HUB windowless | 93% | **100%** | 2 to 1 |
| 428938 / 428939 — CCUI Company ID vs Corp ID | 93% | 97% | 2 to 1 |
| 266200 / 223456 — PI amend | 88% | 100% (223456 is a subset) | 223456 cut |
| 76425 / 460870 — PI Advance rate | 85% | 90% | merge |
| 385014 / 365944 / 385301 / 389728 — reg-card | 70-85% | 55-73% | **keep all** |
| 384400 / 384401 — change payment method | 78% | 65% | **keep both** |

Worked example: 381188 vs 381191 differ only in room count, adult count, one
`PmsRoomType` index, and whether the amend adds or removes an adult. Their 17 Opera
assertions and ~45 UI assertions are identical.

---

## 4. Sole-owner register — do not cut

Each of these is the only spec exercising the listed capability.

### 4.1 PI

| TC | Solely owns |
|---|---|
| 90661 | DLP filter journey: `validateFiltersVisible`, `validateMapViewChangeToMap`, `validateClearAllFiltersPanel`, `applyFilters`, `validateFiltersCollapse`, `validateHotelList`, `removeFilterPillByLabel`, `validateFilterPillsVisibleCount` |
| 380779 | DLP map view + TripAdvisor: `validateTripAdvisorSection`, `validateTripAdvisorOpenReviewsTab`, `validateMapViewCards`, `validateMapViewRedirectToHotelDetails`, `validateMapViewPinsLoadMore`, `validateMapViewHiddenHotelDistance` (AEM distance restriction) |
| 389731 | DLP content service: `validateAemDlpContentServiceDictionaryAgainstDlpInformationAPIResponse`, `getHotelsFromSnowdrop`, `validateAemHotelsWithMapAndRadius`, `validateTravelGuidesCards` |
| 376400 | Marketing opt-**in**: `validateEmailUpdatesAndOffers` (UK/DE/IT verbiage), `validateMarketingOptInAnalyticsValues`, CDH `AnonymousNewsletterPreferences`, yopmail double-opt-in check. Also `HUB_BIGGER_ROOM` (BIGWIN) |
| 374328 | Marketing opt-**out**: section hidden for previously-opted-out user, `optIn:false` in CDH, `validateBalanceOutstandingAmountAndCurrency` |
| 435201 | WiFi ancillary: `graphqlGetAncillariesWiFiExtras`, `validateWifiPackage`, `validateEciLcoWifiWithPrices`, `validateRoomIncludesExtra`, `clickExtrasDropdown` |
| 460870 + 464491 | Refund path: `validateDepositFoliosWithRefunds`, `validatePaymentStatus(REFUNDED)`, `validateBasketIsCancelledAndPaymentRefunded`, `validatePayNowTotalCost`. Also SINGLE room, `ACCESSIBLE_BARRIER_FREE` |
| 382591 | ECI/LCO packages + inventory decrement. **Only Ireland coverage** (loops UK/DE/IE) |
| 373793 | Employee website + EMPLOYEE rate plan |
| 76468 | Upgrade-to-Flex, full back-navigation regression, mid-flow hotel change, PI_STANDARD rate |
| 76469 | SRP filter + sort-by-distance, air-conditioning / chargeable-parking facility filters, MEAL_DEAL, 4-room occupancy |
| 76491 | SRP sort-by-price, multi-room lead-guest containers, `validateAddedChildrenMealsPerRoom` |
| 366971 | TripAdvisor on **HDP** including award popup; SRP sort order verified against API response |
| 385302 + 435918 | SRP map-view journey, `reviewRegCard` |
| 366969 | AMEX card, twin bed-type selection (`chooseYourRoomTypePage`), UK postcode address lookup |
| 76495 | Saved card (CVV-only), lowered-bath selection, amend change-room-type |
| 380774 | BAC payment-authorisation memorable-word flow, `validateRoomClassOrder` |
| 222161 | Non-zero donation (GBP 3), pre-selected continental breakfast from user profile |
| 380783 | **DE percentage city-tax invariance** — total unchanged when adding an adult |
| 385301 | `submitPrecheckinGerman` — German nationality skips SCA |
| 389728 | Multi-room reg-card (Room 2 lead-guest link) |
| 385014 | Reg-card + someone-else + different billing address + delete additional guest |
| 266203 | Amend refund maths (`validatePaymentsAmountCurrency`), checkout-before-original-checkin edge case, cancellation-policy-unchanged |
| 266200 | `validateAmendCancelPoliciesText` (semi-flex dictionary label) |
| 195251 | PIBA amend, `validateBasketIsConfirmed`, edit-lead-guest amend |
| 418503 | PAY_NOW + PIBA combination, two sequential amend cycles |
| 76467 | Richest DE guest booking (20 Opera assertions), RWCC, OHIP API cancel |

### 4.2 CCUI

| TC | Solely owns |
|---|---|
| 384400 | Account-to-Company: company search/verify, company reference, `validateBookingAllowancesNotesForA2C`, `validatePayeeInfo`, `validateFolioWindowNo`, `validateRoutingInstructions`, guarantee code **CO** |
| 384401 | Change payment method to card: guarantee code **CC**, `isPaymentMethodChange` Eckoh path |
| 380724 | `validateAccessibleRoomNotificationsIsDisplayed`, CCUI choose-room-type button, cancelled-booking search-table verification (`validateBookingsLength`, `bookingRow.validateStatus`) |
| 369964 | TripAdvisor **no-award** case, `getRoomIndexWithOccupancy`, CCUI free child breakfast |
| 115170 | CCUI SRP map view, SRP-vs-HDP price comparison, **type-of-caller / accessible customer** for DE non-guaranteed |
| 461417 + 464541 | CCUI refund path (most assertion-rich CCUI spec) — *inferred, not yet read* |
| 409154 | Accompanying guest details, multi-room combination — *inferred, not yet read* |
| 369933 + 408182 | A2C with company reference, 4 rooms / 7 adults — *inferred, not yet read* |

---

## 5. Cut list

Every entry removes only assertions with two or more other owners.
**Assertion coverage after these cuts: 100%.**

| TC | Suite | Reason |
|---|---|---|
| 389727 | PI baseline | 100% assertion overlap with 389724; delta is 3 login lines |
| 389724 | PI baseline | Core 11 assertions only. RWCC covered by 76467, 389728, 389731, 385302; booking-for-someone-else by 380786, 385014, 365944, 222161 |
| 376973 | PI baseline | Zero sole-owned assertions — all trace to 266203, 76491, 380779, 460870, 435201 |
| 223456 | PI baseline | Assertion set is a strict subset of 266200 |
| 76466 | PI baseline | Unique contribution is "9 nights"; otherwise inside 76467 union 76468 |
| 369555 | CCUI baseline | Thinnest CCUI spec: no amend, no cancel, SRP steps commented out (DNRQ-66591). Only PI_STANDARD-in-CCUI is distinct; meal-container assertions overlap 380724 / 428938 |
| 468894 | PI smoke | Strict subset — asserts only `validatePage` plus cancel message |
| 470151 *or* 470152 | CCUI smoke | Agent/manager twins, identical flows |
| 474130 *or* 474131 | CCUI smoke | Agent/manager amend twins |
| 474132 *or* 474133 | CCUI smoke | Agent/manager cancel twins |

`468893` is retained despite looking like a subset — it is the only owner of
`validateSuccessfullUpdatedBookingMessageDisplayed`.

---

## 6. Consolidations

### C1 — `CCUI-AMEND-DE` replaces 381188, 381189, 381190, 381191

Book as agent, DE hotel (FRANKFURT_MESSE), 1 night, **2 rooms** (1 adult + 2 adults),
PI_FLEX, Leisure, home address, PN via Eckoh with VISA. Then in a single amend session:

1. Add adult to room 1 — assert total increases by flat city tax.
2. Remove adult from room 2 — assert total decreases by half the city tax.
3. Add room 3 — assert `validatePaymentsAmountCurrency` additional-amount path.
4. Remove room 3 — assert refund path.

Cancel; assert `STATUS_CANCELLED`. Use `PID_STANDARD_ROOM` throughout (see section 8).

Covers ~96% of all four. **Saves 3 E2E runs.**

### C2 — `PI-WINDOWLESS` replaces 530589, 531538

Logged-in user, HUB London King's Cross, parameterised over `[PI_FLEX, PI_SEMI_FLEX]`.
Assert both Standard-No-Window and Bigger-No-Window are offered, book Standard-No-Window,
verify BIC room-type label and adult label, land on the Amend page.

Covers ~98% of both. **Saves 1 run.**

### C3 — `CCUI-CONTRACT-RATE` replaces 428938, 428939

Verify company by **Company ID**, clear, re-verify by **Corp ID** — exercises both
`selectCompanyByCompanyId` and `selectCompanyByCorpId`. Assert negotiated-rates-first
ordering. Book 1 accessible room with lowered bath (3 nights) plus 1 double room,
Premier Inn breakfast, contract rate, PN VISA.

Covers ~95% of both. **Saves 1 run.**

### C4 — `PI-ADVANCE-RATE` folds 76425 into 460870

460870 already covers PI_ADVANCE, POLICY_CODE_D1 and the refund path. Add 76425's two
distinct parameters — FMTRPL family room and `childMealsToAdd` — as a second room in
460870.

**Saves 1 run.**

---

## 7. Runtime optimisation — no coverage change

384400 and 384401 each spend ~25 UI steps building a non-guaranteed reservation before
reaching the change-payment-method flow that is the actual subject. Replace the prologue
with:

```js
await ApiHelpers.createAndConfirmReservationViaApi({
    hotelAvailabilityInput,
    guestDetails,
    hotel,
    paymentOption: Constants.PAYMENT_OPTION.nonGuaranteed
});
```

This is the pattern already used by 266200, 266203, 223456, 195251 and the smokes.
No assertion is lost; roughly 60% runtime is saved per spec. The same applies to most
CCUI book-then-amend specs.

---

## 8. Defects and hygiene found during analysis

Not part of the cut plan, but worth raising separately.

| Item | Detail |
|---|---|
| 381191 wrong constant | Uses `PmsRoomType.PI_STANDARD_ROOM` against a German hotel; siblings use `PID_STANDARD_ROOM`. Looks like copy-paste, not intent |
| 369556 permanently skipped | Hard `describe.skip`. Only MLOS coverage in the pack — fix or retire the TC; a skipped test gives false comfort |
| 369555 dead steps | SRP validation commented out under DNRQ-66591; the spec no longer does what its name claims |
| 115170 misclassified | Tagged `#sanity#` but owns the CCUI SRP map view, SRP-vs-HDP price comparison and type-of-caller flow. Should be a baseline |
| Assertions pinned to bugs | 389728 asserts `validateSuccessMessage({ isDisplayed: false })` (DNRQ-87379); 76425 asserts `PAYMENT_STATUS_COMPLETED` instead of `REFUNDED` (DNRQ-66957); 418503 asserts a PAY_ON_ARRIVAL label on a PAY_NOW booking (DNRQ-90056). Each will silently pass once the bug is fixed |
| `if (false)` blocks | 266200, 266203 and 223456 carry unreachable amend-PN blocks pending DNRQ-47440 |
| Environment-gated coverage | 389731's DLP/Snowdrop assertions only run on `dit`; 384400's email check only on `uat`. Coverage varies by environment |

---

## 9. Numbers

| | Current | After this doc | Strategy doc target |
|---|---:|---:|---:|
| PI baselines | 40 | 33 | 31 |
| CCUI baselines | 26 | 21 | 21 |
| Smokes (PI + CCUI) | 14 | 9 | 9 |
| PIB (end2end) | 121 | 104 | 70 |
| **Total** | **201** | **167** | **~165** |
| Assertion coverage | 100% | **100%** | **90–95%** |

The "after this doc" column reflects cuts and consolidations enumerated in this document
at 100% coverage. The "strategy doc target" column is the enumerated plan from
`regression-pack-migration-strategy.md` which additionally applies the 90–95% budget, the
cross-app dedup, and the PIB booking consolidations.

Plus the section 7 runtime optimisation on the retained CCUI specs, which does not change
the count but is likely the larger wall-clock win.

The 90–95% coverage budget is now **being spent** in the strategy doc. It covers rare
combinations (HUB plus amend, 9-night stays, FMTRPL plus child breakfast), role/locale
twins, and duplicate channel depth.

---

## 10. How to verify each cut

Before deleting any spec:

1. Take the spec's `validateX` set.
2. Confirm each assertion has at least two other owners in the register at section 4.
3. Run the proposed keep-set and diff the aggregate assertion inventory against a
   full-pack run. It must be identical.
4. Retire or merge the Zephyr TC — do not orphan the ID.

---

## 11. Confidence and gaps

Read in full: **49 of 80 spec files (61%)** — 38/40 PI baselines, 9/26 CCUI baselines,
5/14 smokes.

Firm and evidence-based: every entry in sections 5 and 6.

Not yet read, and therefore **not** assumed cuttable: 376362, 380777, 374857 (PI amend
variants) and 17 CCUI baselines.

An earlier draft of this analysis assumed such specs would fold and projected a 34%
reduction. Reading a sample disproved it — 380783, 374328, 366971, 369964 and 380724 each
turned out to own something distinct. **Treat unread files as owners until shown
otherwise.**

---

## 12. Follow-up: PIB

PIB's 94 tests span 9 journey areas: auth, booking rebranding, card management, company
management, home, pay app, profile management, spending and reporting, user management.

They share almost no backbone with the booking packs, so cross-app overlap is near zero.
Apply the same method as a separate exercise. The booking-rebranding journey (33 tests) is
the most likely place to find PI/CCUI-style parametric duplication.

---

# Appendix A — InnBusiness (PIB)

Same method and protected unit (assertion coverage) as the PI + CCUI analysis. Nothing
ring-fenced.

## A.1 Inventory correction

The summary in `regression-pack-analysis.md` records PIB as 94 tests. The reference tree
actually holds **121 spec files**:

| Journey | Specs |
|---|---:|
| bookingRebrandingJourney | 34 |
| cardManagementJourney | 15 |
| profileManagementJourney | 15 |
| payAppJourney | 14 |
| companyManagementJourney | 9 |
| userManagementJourney | 7 |
| authJourney | 5 |
| homeJourney | 5 |
| spendingAndReportingJourney | 6 |
| smokeTests | 11 |
| **Total** | **121** |

## A.2 Headline

PIB is **proportionally less redundant** than CCUI. Only bookingRebranding and the smokes
carry booking-style parametric duplication; the other 8 journeys are per-feature admin
flows where nearly every spec is a sole owner. Firm reduction **-14%** at 100% assertion
coverage; **~-20%** if the 90-95% budget is spent.

PIB booking specs run **3-D Secure** (`paymentSixCardSolution3dSecureHostPage.confirmPayment`),
unlike CCUI. The booking backbone otherwise matches PI/CCUI, on the `pages/bb` objects.

## A.3 Measured overlap

| Cluster | Assertion overlap | Verdict |
|---|---:|---|
| 485261 / 485262 / 493806 — Mgr DE 1n BFlex PN amend (add/remove/both room) | ~100% | 3 to 1 |
| 197342 centrally-stored vs personal-stored smoke (same TCID) | **100%** | 2 to 1 |
| Amend-add-adult smokes booker/self/TM | ~100% | 3 to 1 |
| Cancel smokes booker/self/TM | ~100% | 3 to 1 |
| Book-only smokes booker/self/TM | ~100% | 3 to 1 |
| 450422/424 UkHotelPoa vs 450423/425 DeHotelPoa | ~90% | 2 to 1 |
| home 446238 / 446140 vs 446237 | 100% (subsets of 446237) | cut 1 |
| PM5 vs PM15 (room prefs, both UK) | 100% | 2 to 1 |
| payApp AccessDeleted vs AccessRemoved | ~100% (delete vs unshare) | merge |

## A.4 Sole-owner register — do not cut

### bookingRebranding
| TC | Solely owns |
|---|---|
| 435202 | WiFi extras: `graphqlGetAncillariesWiFiExtras`, `validateAddedRemovedWifiLabelPerRoom`, `validateEciLcoWifiWithPrices`, `validateRoomIncludesExtra` + remove-night amend |
| 453028 | Planet payment path: `validatePaymentStatus(SUCCESS)`, `validateRefundStatus`, `validatePlanetRefundStatus`, `PAYMENT_STATUS_REFUNDED` + SRP map-view + Meal Deal |
| 450465 | **Cross-channel IB→CCUI** amend (switches project mid-test) |
| 450464 | Cross-channel IB→BB amend — *thin; see cut list* |
| 516551 | TripAdvisor on HDP (logo/rating/reviews/award popup) + windowless DBLNWD + privacy container |
| 369963 | TripAdvisor on HDP + PI_ADVANCE rate + child breakfast + remove-child-meal amend |
| 369609 | 4 rooms / 7 adults / 1 child + accessible + preselected meal from profile + print booking + Mastercard |
| 369934 | Choose-your-bathroom accessible flow (LOWDBL) + centrally-stored card + Meal Deal + extend-night |
| 369947 | 14-night long stay + personal-stored card (CVV-only) + SRP list view |
| 381172 | DE percentage city-tax invariance (add adult, total unchanged) + dinner allowance |
| 418505 | POA + two sequential add-room amends + `validatePaymentCard(POA)` |
| 516552 | Windowless BIGNWD + Semi-Flex + city-tax-invariance add-adult |
| 519597 | Windowless ACCNWD + Standard rate + change-arrival-date amend |
| 369609 / others | multi-room BB guest-details containers |

### Non-booking journeys (each spec a sole owner unless listed in A.5)
- **auth (5):** link-existing-account (WL), reset-password, register-finance-user, signup-booker-existing-company, signup-new-company-DE — zero cross-overlap.
- **company (9):** edit-company-details, edit-main-contact, review-changes, booking-alerts (same-day/weekly DE + weekend/price/recipients EN), custom-question, business-question, no-access, allowances-persistence (cross-project PI→IB→BB).
- **user-mgmt (7):** each owns a distinct account-lifecycle transition (role escalation, deactivate+purge, inactive-login-blocked, guest ME-denial, card=None, postcode edit, invite-to-add).
- **spending (6):** company-spending charts, IBPay report-card Worldline/MMA redirects, MI/emergency report + booking, DE no-spend state, memorable-word page + upcoming spending.
- **pay-app (14):** each business type gates a distinct flow — charity (DD errors), sole-trader (DD-by-post + mandate PDF + DOB), gov-funded (correspondence address), limited (registration lookup), 5-employee max-share, public (role card-permission matrix), register-now, IB-banner, entry-points.
- **card/profile:** 3 distinct card UI surfaces (centrally-stored company cards, InnBusiness-Pay/WL employee cards, personal-profile card + 3DS iframe) — the profile-card and company-card specs are disjoint, not duplicates.

## A.5 Cut list (100% assertion coverage retained)

| TC / spec | Journey | Reason |
|---|---|---|
| 485261, 485262 | bookingRebranding | Add/remove-room assertion sets ⊂ consolidated C-PIB-1 |
| 450464 | bookingRebranding | Thin amend-add-adult; assertions ⊂ every full amend spec; cross-channel IB→CCUI kept by 450465 |
| one of 446238 / 446140 | home | Identical assertion sets; both ⊂ 446237 |
| PM15 (or PM5) | profile | Room-preferences duplicate, both UK |
| CM6 | card | WL add-card ⊂ CM4 (both UK) |
| CM9 | card | ⊂ CM10 |
| 464510 | spending | Manage-account check ⊂ 464522; only booker-tab-hidden is unique → fold into 464522 |
| Amend-add-adult smokes: keep 1 of 3 | smokes | Role twins |
| Cancel smokes: keep 1 of 3 | smokes | Role twins |
| Book-only smokes: keep 1 of 3 | smokes | Role/card twins |
| Stored-card smokes: keep 1 of 2 | smokes | Same Zephyr TC 197342, identical assertions |

## A.6 Consolidations

**C-PIB-1 — Manager DE amend** replaces 485261 + 485262 + 493806.
Book as TM, DEFAULT_GERMAN_HOTEL, 1 night, 2 rooms, BUSINESS_FLEX, PN. Amend: add a room
(assert add notification + increased total) → remove a room (assert remove notification +
reduced total). Cancel; assert `STATUS_CANCELLED` + `validateCanceledBIC`. ~96%. Saves 2.

**C-PIB-2 — PIB POA rebranding header** replaces 450422/424 + 450423/425.
Not-tethered TM, parameterised over [DEFAULT_HOTEL(UK), FRANKFURT_MESSE(DE)], 1 room, POA,
Visa. Assert the full `validateInnBusinessHeader` / `validateGuestDetailsAndPaymentHeader`
rebranding sequence at each step; cancel on the UK pass, assert city tax on the DE pass.
Saves 1.

**C-PIB-3 — PIB stored-card smoke** replaces both 197342 specs.
Parameterise `storedCard` over [CENTRALLY_SAVED_CARD, PERSONAL_STORED_CARD]. Saves 1.

**C-PIB-4 — smoke role collapse.** Reduce the 3×3 role×operation smoke matrix to one spec
per operation (book / amend-add-adult / cancel), since all three roles seed via
`createAndConfirmReservationViaApi` and assert identically. 9 to 3.

**Merge — pay-app access revocation.** Fold AccessDeletedApplication + AccessRemovedFromShared
into one spec parameterised on revocation type (delete vs unshare); both terminate on
`accessRestrictedPage.validatePage`. Saves 1.

## A.7 Runtime optimisation — no coverage change

Most bookingRebranding amend specs build the reservation through the full UI before the
amend under test. The smokes already seed via `ApiHelpers.createAndConfirmReservationViaApi`.
Apply the same seeding to the pure-amend booking specs (e.g. the amend-family, WiFi
remove-night, child-meal remove) to cut ~50% runtime with no assertion loss. Keep full-UI
booking only where the booking flow itself is the subject (rebranding header, choose-your-
bathroom, preselected meal, stored-card selection).

## A.8 Defects and hygiene found during analysis

| Item | Detail |
|---|---|
| 435202 wrong skip guard | Comment/logic says "runs only on German website" (skips if English) but the hotel is LONDON_HEATHROW_AIRPORT (UK) and it asserts UK currency |
| 519597 wrong skip guard | Says "runs only on DE website" but hotel is LONDON_KING_CROSS and asserts `UK_CURRENCY_CODE` |
| 516551 / 516552 shared Zephyr URL | Both console.log `testcaseId=1655895` despite being TC 516551 vs 516552 (copy-paste) |
| Stored-card smokes | Both automate the same Zephyr TC 197342 — dedupe at source |
| Assertions/steps disabled by bugs | 464505 Out-of-policy report card commented out (DNRQ-88190); 465452 worldline back-nav workarounds (DNRQ-83275); 418505 add-room price + hasMeals workarounds (CTECH-2780 / DNRQ-90120 / DNRQ-57515); pay-app `validateNumberOfCards(3)` workaround (DNRQ-87941); 369934 bathroom (DNRQ-50426) |
| Environment-gated coverage | Most pay-app/spending specs run EN only; several booking specs DE-only or EN-only; some home employee-question steps DIT-only |

## A.9 Confidence and gaps

Read in full: ~54 of 121 specs — 18/34 bookingRebranding, all 14 pay-app, all 9 company,
all 6 spending, all 5 auth, all 5 home, all 7 user-mgmt, 6/11 smokes. Card (15) and profile
(15) classified via sub-agent extraction of their assertion sets, cross-checked against the
3-card-surface model.

Firm: every entry in A.5 and A.6. Not yet read individually and therefore not assumed
cuttable: 16 bookingRebranding specs (mostly distinct rate/room/card/channel variants) and
the card/profile locale-split pairs beyond the three named. Treat unread files as owners
until shown otherwise.

## A.10 Numbers

| Journey | Current | Firm | With budget |
|---|---:|---:|---:|
| bookingRebranding | 34 | 30 | ~27 |
| card + profile | 30 | 27 | ~24 |
| home | 5 | 4 | 4 |
| pay-app | 14 | 13 | ~12 |
| spending | 6 | 5 | 5 |
| auth / company / user-mgmt | 21 | 21 | 21 |
| smokes | 11 | 4 | 4 |
| **PIB total** | **121** | **104** (−14%) | **~97 (−20%)** |
| Assertion coverage | 100% | **100%** | ~95% |

**Whole pack (PI + CCUI + PIB): 201 → 165 (enumerated in the strategy doc)**, 90–95%
assertion coverage retained. See `regression-pack-migration-strategy.md` for the
authoritative breakdown and the budget allocation that closes the remaining gap to ~140.

