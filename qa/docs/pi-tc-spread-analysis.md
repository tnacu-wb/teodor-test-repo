# PI TC Spread Analysis

> How the 131 PI specs in the old WebdriverIO pack are distributed: E2E vs functional, what
> part of the product each E2E targets, and what kind of testing each one actually does.
> Companion to `regression-pack-migration-strategy.md`.

**Contents**

1. [The split — E2E vs functional](#1-the-split--e2e-vs-functional)
2. [E2E inventory by brand](#2-e2e-inventory-by-brand)
3. [Distribution summary](#3-distribution-summary)
4. [What the E2Es actually do](#4-what-the-e2es-actually-do)
5. [Functional areas vs E2E coverage](#5-functional-areas-vs-e2e-coverage)
6. [Conclusions](#6-conclusions)

---

## 1. The Split — E2E vs Functional

| Group                   | Location                       | Specs   | Share |
|-------------------------|--------------------------------|--------:|------:|
| E2E — baselines         | `pi/baselineTests`             |      40 |   31% |
| E2E — smokes            | `pi/smokeTests`                |       7 |    5% |
| **E2E subtotal**        |                                |  **47** |**36%**|
| Functional              | `pi/featureTests` (21 folders) |      80 |   61% |
| Harness (not tests)     | `pi/*.spec.js`                 |       4 |    3% |
| **Total**               |                                | **131** |       |

Roughly **1 E2E for every 1.7 functional specs**.

The 4 harness files (`createReservations`, `confirmReservationTest`, `cancelReservations`,
`examples`) carry no Zephyr ID and no tags — they are data-setup utilities.

Only the **47 E2Es** are analysed below. The functional specs are used in §5 as the
reference for measuring E2E coverage gaps.

---

## 2. E2E Inventory by Brand

Split by brand so each table stays narrow. Within each, guest specs come first, then
logged-in, ordered by journey depth.

**Column codes**

*User* — `G` guest / anonymous · `L` logged-in

*Journey* — `B` book · `B·A` book + amend · `B·C` book + cancel · `B·A·C` full funnel ·
`A*` amend-only, booking seeded by API · `C*` cancel-only

*Rate* — `Flex` · `Semi` semi-flex · `Std` standard · `Adv` advance · `Emp` employee

*Payment* — `PN` pay now · `POA` pay on arrival · `PIBA` pay in advance ·
`BAC` payment authorisation · `RWCC` reserve without card · `Saved` stored card

*Type* — `FEAT` feature-specific · `AMEND` amend-operation · `PAY` payment permutation ·
`OCC` occupancy permutation · `RATE` rate permutation · `LOC` locale / tax logic ·
`USER` user-type permutation

### 2.1 UK — Premier Inn (17 specs)

| TC     | User | Journey | Rate | Payment | Focus area                                | Type  |
|--------|:----:|:-------:|:----:|:-------:|-------------------------------------------|:-----:|
| 373793 |  G   |    B    | Emp  |   PN    | Employee website + EMPLOYEE rate          | FEAT  |
| 90661  |  G   |  B·A·C  | Flex |   PN    | Remove-breakfast amend                    | AMEND |
| 76469  |  G   |   B·A   | Std  |   POA   | 9 nights, 4 rooms, SRP filters, MEAL_DEAL | OCC   |
| 460870 |  G   |  B·A·C  | Adv  |   PN    | Advance rate, refund path, remove room    | RATE  |
| 366969 |  G   |  B·A·C  | Flex |  AMEX   | AMEX card, TWIN room, extend dates        | PAY   |
| 435201 |  G   |  B·A·C  | Flex |   PN    | WiFi ancillary, remove room with WiFi     | FEAT  |
| 380779 |  G   |  B·A·C  | Flex |   BAC   | Donations, TripAdvisor, change room type  | FEAT  |
| 376973 |  G   |  B·A·C  | Flex |  PIBA   | Change arrival date, 2 rooms 4ad 2ch      | AMEND |
| 76425  |  G   |  B·A·C  | Adv  |   PN    | FMTRPL family room, child meals           | RATE  |
| 266200 |  L   |   A*    | Semi |   PN    | Amend cancel-policy text (dictionary)     | AMEND |
| 195251 |  L   |   A*    | Flex |  PIBA   | PIBA amend, extend checkout 2 days        | AMEND |
| 76495  |  L   |  B·A·C  | Flex |  Saved  | Saved card CVV-only, accessible LOWDBL    | PAY   |
| 380774 |  L   |   B·A   | Flex |   BAC   | BAC memorable word, room class order      | PAY   |
| 222161 |  L   |   B·C   | Semi |   PN    | Donations (GBP 3), preselected breakfast  | FEAT  |
| 376362 |  L   |   B·A   | Flex |  PIBA   | PIBA allowances, extend + add room/kid    | PAY   |
| 380777 |  L   |   B·A   | Flex |   BAC   | TWIN room, BAC, increase nights           | PAY   |
| 418503 |  L   |   B·A   | Flex |  PIBA   | Two sequential amend cycles               | AMEND |

### 2.2 DE — Premier Inn Deutschland (17 specs)

| TC     | User | Journey | Rate | Payment | Focus area                                 | Type  |
|--------|:----:|:-------:|:----:|:-------:|--------------------------------------------|:-----:|
| 385014 |  G   |    B    | Flex |   POA   | Reg-card, someone-else, billing address    | FEAT  |
| 365944 |  G   |    B    | Flex |   POA   | Different billing address, someone-else    | FEAT  |
| 389724 |  G   |    B    | Flex |    —    | Booking for someone else                   | USER  |
| 385301 |  G   |    B    | Flex |   POA   | Reg form, German nationality (skips SCA)   | FEAT  |
| 389728 |  G   |    B    | Flex |  RWCC   | Multi-room reg-card, reserve without card  | FEAT  |
| 76466  |  G   |   B·C   | Flex |   POA   | 9 nights, 1 room, meals                    | OCC   |
| 76468  |  G   |  B·A·C  | Std  |   POA   | Upgrade-to-Flex, back-nav, hotel change    | RATE  |
| 76467  |  G   |  B·A·C  | Flex |  RWCC   | Richest DE booking, RWCC, OHIP cancel      | FEAT  |
| 380786 |  G   |  B·A·C  | Flex |   PN    | City tax, amend remove-adult                | LOC   |
| 374328 |  G   |  B·A·C  | Flex |   PN    | Marketing opt-out, amend add-room          | FEAT  |
| 385302 |  L   |    B    | Flex |    —    | Reg-card review, single room                | FEAT  |
| 389727 |  L   |    B    | Flex |    —    | Booking for someone else (logged-in twin)  | USER  |
| 389731 |  L   |    B    | Flex |  RWCC   | 4 rooms, multi-night, no meals              | OCC   |
| 76491  |  L   |   B·C   | Flex |   POA   | Multi-room lead guest, child meals, SRP sort| OCC   |
| 266203 |  L   |   A*    | Flex |   PN    | Amend refund maths, early-checkout edge     | AMEND |
| 380783 |  L   |  B·A·C  | Flex |   PN    | City-tax invariance when adding an adult    | LOC   |
| 374857 |  L   |  B·A·C  | Flex |   PN    | 6 adults multi-room, no meals, remove room  | OCC   |

### 2.3 UK — hub (5 specs)

| TC     | User | Journey | Rate | Payment | Focus area                                | Type  |
|--------|:----:|:-------:|:----:|:-------:|-------------------------------------------|:-----:|
| 530589 |  G   |   B·A   | Flex |   PN    | Windowless room (Standard No Window)      | FEAT  |
| 376400 |  G   |  B·A·C  | Flex |   PN    | Marketing opt-in, CDH, HUB BIGWIN         | FEAT  |
| 366971 |  G   |  B·A·C  | Flex |   POA   | TripAdvisor on HDP, BIGWIN, reduce nights | FEAT  |
| 223456 |  L   |   A*    | Flex |   PN    | Amend shorten + add room/guest/meal       | AMEND |
| 531538 |  L   |   B·A   | Semi |   PN    | Windowless room (logged-in twin)          | FEAT  |

### 2.4 Multi-locale (1 spec)

| TC     | User | Journey | Rate | Payment | Focus area                                      | Type |
|--------|:----:|:-------:|:----:|:-------:|-------------------------------------------------|:----:|
| 382591 |  G   |    B    | Flex |   POA   | ECI/LCO packages, inventory decrement           | FEAT |

Loops UK → DE → **IE**. The only Ireland coverage anywhere in the PI pack.

### 2.5 Smokes (7 specs)

| TC     | Locale | User | Journey | Payment    | Purpose                                  |
|--------|:------:|:----:|:-------:|:----------:|------------------------------------------|
| 468889 |   UK   |  G   |    B    | POA (PIBA) | Happy-path wiring check                  |
| 468890 |   DE   |  G   |    B    | PN (Visa)  | Happy-path wiring check                  |
| 468891 |   UK   |  L   |    B    | POA (PIBA) | Happy-path wiring check                  |
| 468892 |   DE   |  L   |    B    | PN (Visa)  | Happy-path wiring check                  |
| 468893 | UK/DE  |  L   |   A*    | PN         | Amend add-adult; owns updated-booking msg |
| 115179 |   UK   |  G   |    B    | PN / POA   | 3 nights, meals, donations, FMQUAD       |
| 468894 | UK/DE  |  L   |   C*    | PN         | Cancel confirmation                      |

A deliberate 2×2 grid (UK/DE × guest/logged-in) plus one amend, one cancel, and one
meals-and-donations variant. These prove the pipeline works; they are not feature coverage.

---

## 3. Distribution Summary

> **How to read these counts.** Brand/locale, journey shape, and rate are grep-verified
> against the specs. Payment method and "type of testing" are **primary-intent
> classifications** — a spec that combines instruments (most load a PIBA card by default) or
> tests several things is counted once under its distinguishing feature. Coverage claims in
> §5 are grep-verified against page-object usage.

### 3.1 By brand and locale

| Brand / Locale            | Baselines | Smokes |   Total | Share |
|---------------------------|----------:|-------:|--------:|------:|
| UK — Premier Inn          |        17 |      3 |      20 |   43% |
| DE — Premier Inn Deutschl.|        17 |      2 |      19 |   40% |
| UK — hub                  |         5 |      — |       5 |   11% |
| UK/DE (parameterised)     |         — |      2 |       2 |    4% |
| Multi-locale incl. IE     |         1 |      — |       1 |    2% |
| **Total**                 |    **40** |  **7** |  **47** |       |

Germany is covered nearly as heavily as the UK, driven by city tax (a genuinely different
pricing path) plus German-specific reg-card and SCA behaviour.

### 3.2 By user type

| User type         | Baselines | Smokes | Total |
|-------------------|----------:|-------:|------:|
| Guest / anonymous |        23 |      3 |    26 |
| Logged-in         |        17 |      4 |    21 |

Near-even, and partly deliberate — several specs exist only as the logged-in twin of a guest
spec: 389724/389727, 530589/531538, 468889/468891, 468890/468892.

### 3.3 By journey shape

| Journey                 | Count | Share | Note                          |
|-------------------------|------:|------:|-------------------------------|
| `B·A·C` full funnel     |    16 |   34% | The dominant pattern          |
| `B` book only           |    10 |   21% | Feature lives in booking flow |
| `B·A` book + amend      |     7 |   15% |                               |
| `A*` amend-only         |     5 |   11% | 4 baselines + 1 smoke         |
| `B` smoke (no post-ops) |     5 |   11% |                               |
| `B·C` book + cancel     |     3 |    6% |                               |
| `C*` cancel-only        |     1 |    2% |                               |

**28 of 47 (60%) exercise an amend. 20 (43%) involve a cancellation.** Amend is by far the
most heavily tested area of PI.

### 3.4 By rate

| Rate                       | Count |
|----------------------------|------:|
| Flex                       |    32 |
| Semi-Flex                  |     3 |
| Standard                   |     2 |
| Advance                    |     2 |
| Employee                   |     1 |
| Not rate-specific (smokes) |     7 |

Flex dominates. Semi-Flex, Standard and Advance have only 2–3 owners each — thin, but each
owns something distinct (Advance → refund path, Semi-Flex → amend cancel-policy dictionary,
Standard → upgrade-to-Flex). Employee (373793) uses a rate plan rather than a `HotelRate`
constant.

### 3.5 By payment method

| Payment method             | Count |
|----------------------------|------:|
| Pay Now (generic card)     |    17 |
| Pay on Arrival (generic)   |     8 |
| PIBA (pay in advance)      |     6 |
| BAC (payment authorisation)|     3 |
| RWCC (reserve w/o card)    |     3 |
| Visa (explicit)            |     2 |
| Saved card (CVV-only)      |     1 |
| AMEX                       |     1 |
| Not payment-specific       |     6 |

### 3.6 By type of testing

The answer to "what kind of tests are these". Most specs do several things, so this is
**primary** intent.

| Type                         | Count | What it means                                   |
|------------------------------|------:|-------------------------------------------------|
| `FEAT` Feature-specific      |    15 | A named feature inside a real journey           |
| `AMEND` Amend-operation      |    12 | The amend action itself is the subject          |
| `PAY` Payment permutation    |     7 | Same journey, different payment instrument      |
| `OCC` Occupancy permutation  |     6 | Room counts, room types, adult/child mixes      |
| `PIPE` Pipeline smokes       |     6 | Prove the stack is up; not feature coverage     |
| `RATE` Rate permutation      |     3 | Flex vs Semi-Flex vs Standard vs Advance        |
| `LOC` Locale / tax logic     |     3 | DE city tax, including invariance checks        |
| `USER` User-type permutation |     2 | Guest vs logged-in doing the same thing         |

Expanding the two largest:

- **`FEAT`** — WiFi, windowless rooms, reg-card, TripAdvisor, donations, employee site,
  ECI/LCO, marketing opt-in/out, booking for someone else.
- **`AMEND`** — add or remove room, adult, child, meal; change room type; change arrival
  date; extend or reduce nights; two sequential amend cycles.

The pack is **not** primarily a permutation matrix. The largest group is feature-specific
journeys, then amend-operation coverage. Pure permutations (rate, payment, occupancy, user
type) account for 18 of 47 — a bit over a third.

---

## 4. What the E2Es Actually Do

Nearly every baseline follows one skeleton:

```
search (SRP or direct HDP)
  → select room + rate
  → add ancillaries (meals, WiFi, ECI/LCO)
  → guest details (+ reg-card / someone-else / billing address)
  → payment (one of ~8 instruments)
  → confirmation + cross-check against Opera/OHIP
  → [amend: add/remove room, guest, meal, dates, room type]
  → [cancel + verify refund / status]
```

What differs between specs is which **variant** of each step is exercised, and which extra
feature is layered on top. The Opera cross-check at confirmation is the common high-value
payload: hotel, dates, occupancy, rate, price, currency, deposit, city tax, meals, guest
details.

**Where coverage is concentrated**

| Area                | Specs | Note                                                |
|---------------------|------:|-----------------------------------------------------|
| Amend               |    28 | Every amend operation has at least one owner        |
| Booking + payment   |    47 | All specs go through payment                         |
| City tax (DE)       |    19 | Checked in every DE booking; 2 dedicated invariance checks (380783, 380786) |
| Meals / ancillaries |   ~18 | Add, remove, or preselect                           |
| DLP + map view      |     3 | 380779 (full), 90661, 389731                        |

**Thin areas inside the E2Es**

| Area             | Specs | Note                                              |
|------------------|------:|---------------------------------------------------|
| Search / SRP     |     3 | Only 76469, 76491, 366971 exercise SRP filters,   |
|                  |       | sort or price; the rest go straight to the HDP    |
| Non-Flex rates   |   2–3 | Per rate                                          |
| Ireland          |     1 | 382591 only, and only one iteration of a loop     |
| Accessible rooms |     3 | 460870, 76469, 76495; always incidental           |

(Counts above are grep-verified against page-object usage. DLP is listed here as covered,
correcting an earlier draft that placed it under uncovered areas.)

---

## 5. Functional Areas vs E2E Coverage

The 80 functional specs cluster into 21 areas. If we drop the functional layer (as
`regression-pack-migration-strategy.md` §1.1 does), what does the E2E layer still cover?

Sorted by gap severity.

| Functional area                | Specs | Status  | E2E coverage                                  |
|--------------------------------|------:|:-------:|-----------------------------------------------|
| companyNameSpecialCharacters   |     6 | **GAP** | None                                          |
| groupWebForm                   |     4 | **GAP** | None                                          |
| paymentDeclines                |     3 | **GAP** | None — all E2Es use happy-path cards          |
| webPushNotification            |     3 | **GAP** | None — E2Es only dismiss the popup            |
| countryDropdownFields          |     2 | **GAP** | None                                          |
| 3DSIndicatorInPlanet           |     1 | **GAP** | None                                          |
| accountSettingsPasswordChange  |     1 | **GAP** | None                                          |
| hdpAnnouncement                |     1 | **GAP** | None                                          |
| hotelAccount                   |     1 | **GAP** | None                                          |
| myProfile (password strength)  |     1 | **GAP** | None                                          |
| nineRooms                      |     1 | **GAP** | 76469 does 4 rooms — 9-room boundary untested |
| resetPassword                  |     1 | **GAP** | None                                          |
| destinationLandingPage         |    17 | PARTIAL | 380779 (full + map/grid), 90661, 389731       |
| eciLco                         |    17 | PARTIAL | 382591 + 435201 cover happy path only         |
| deOptIn                        |     9 | PARTIAL | 376400 / 374328 cover opt-in and opt-out only |
| guestDetails (persistence)     |     1 | PARTIAL | 76468 does back-navigation                    |
| Login (BART decomm)            |     1 | PARTIAL | Login is a precondition, never asserted       |
| marketingSuppression           |     5 |   OK    | 376400, 374328, 374857                        |
| tripAdvisor                    |     3 |   OK    | 380779, 366971                                |
| ancillaries (remove menu)      |     1 |   OK    | Covered by meal-amend specs                   |
| paymentSummary                 |     1 |   OK    | Implicit in every booking flow                |

**On destinationLandingPage.** Three E2Es exercise the DLP, not zero. 380779 covers it in
full — page elements against the AEM dictionary, TripAdvisor section, map/grid view toggle,
map cards, map-pin → HDP redirect, load-more, hotel list, and the distance-hidden variant.
90661 and 389731 cover a lighter subset (map view change, page elements, hotel list). What
the E2Es do **not** cover is the rich content and filtering surface: filters and
filters-applied, FAQ, hero, things-to-do, travel guides, hotel counter, hotel-list radius,
notifications, why-section and explore-other-destinations. So the core DLP journey plus map
is covered; the SEO/content sections are functional-only. Hence PARTIAL, not GAP.

### 5.1 Gaps ranked by risk

**High — revenue or access impact, zero E2E coverage**

1. **Payment declines** (3 specs). Every E2E uses a card that succeeds. Nothing exercises
   "contact bank", "incorrect card details", or "try again". A regression in decline handling
   would ship undetected.
2. **3DS indicator in Planet** (1 spec). Payment authentication signalling.

**Medium — account and identity**

3. **Password flows** (3 specs across `resetPassword`, `myProfile`,
   `accountSettingsPasswordChangeValidation`).
4. **Login as a subject** (1 spec). Logged-in E2Es sign in as a precondition but never assert
   on the login journey itself.
5. **Hotel account / regular guest** (1 spec).

**Medium — booking edge cases**

6. **Nine rooms** (1 spec). Largest E2E is 4 rooms; the 9-room boundary is untested.
7. **Group web form** (4 specs). The >10 rooms path, children and accessible checkboxes,
   school/youth group. A separate journey with no E2E equivalent.
8. **ECI/LCO** (17 functional vs ~2 E2E). Large functional suite covering cancel paths,
   dashboard BIC, low inventory, no availability, hide/remove ECI and LCO. E2Es cover only
   happy-path package selection.

**Medium — partially covered, rich surface left to functional layer**

9. **Destination Landing Page** (17 functional, 3 E2E). The core DLP journey and map/grid
   view *are* covered by E2Es — 380779 fully, 90661 and 389731 partially. The gap is the
   content and filtering surface: filters, FAQ, hero, things-to-do, travel guides, hotel
   counter, radius, notifications, why-section, explore-other-destinations. Not a full gap,
   but the majority of the 17 functional specs cover behaviour no E2E asserts.

**Lower — input validation and notifications**

10. **Company name special characters** (6 specs) — validation across registration, booker
    address, billing address, settings.
11. **Country dropdown type-ahead** (2 specs) — guest details and payment details.
12. **Web push notifications** (3 specs) — allow / deny / cancel. E2Es only dismiss the popup.
13. **HDP announcement** (1 spec) — restaurant-closed banner.
14. **DE opt-in matrix** (9 specs) — residence × site × logged-in permutations. E2Es cover
    basic opt-in and opt-out but not the matrix.

---

## 6. Conclusions

**Ratio.** 47 E2Es against 80 functional specs — E2Es are 36% of the 131 PI specs.

**What kind of tests the E2Es are.** Long, feature-layered user journeys, not a permutation
grid. The largest group (15) is feature-specific — a named feature exercised inside a
realistic booking flow. Second largest (12) is amend-operation coverage, where the amend
action itself is the subject. True permutations of rate, payment, occupancy and user type
together make up 18. Six smokes exist only to prove the pipeline works.

**What they do.** Every baseline walks the same spine — search, select, ancillaries, guest
details, payment, confirmation with an Opera cross-check — then optionally amends and
cancels. 60% include an amend, 43% also cancel. Variation across specs is in which room type,
rate, payment instrument, locale and extra feature gets pulled into that spine.

**Where coverage is concentrated.** Amend operations, payment instruments, meals, and German
city tax are all covered several times over. This is where the pack's redundancy lives, and
where the consolidations in the migration strategy come from.

**What is uncovered.** Twelve areas have zero E2E coverage. The three that matter most:

1. **Payment declines** — no E2E ever sees a failed payment.
2. **Password and account management** — reset, change, strength validation, all
   functional-only.
3. **Group web form** — the >10-room journey, a separate flow with no E2E equivalent.

**Partially covered, worth noting.** Destination Landing Page is *not* a full gap — 380779
covers the DLP end to end including map and grid view, with 90661 and 389731 adding lighter
passes. What is missing is the content/filter surface (filters, FAQ, hero, things-to-do,
travel guides, counter, radius, notifications). ECI/LCO is similar: happy-path package
selection is covered, the cancel and inventory paths are not.

**Implication for the migration.** Dropping the functional layer is defensible where E2Es
already cover the behaviour — marketing, TripAdvisor, ancillaries, payment summary, and the
core DLP + map journey. It is *not* free for the twelve full-gap areas. The one worth
re-adding as a new E2E rather than accepting the gap:

- **A payment-decline E2E** — cheap to build (one booking flow, a declining card, three
  assertions) and it closes the highest-risk gap in the pack.

For DLP, the gap is narrower than it first looks: the new Playwright BL001 already ports
380779's DLP validation including map/grid view, so the core journey carries over in the
migration. Only the content/filter sections would need net-new coverage, and those are
lower risk.

The remaining full-gap areas are lower risk and fit the strategy's stated position: accept
the gap now, address it later as deliberate new work.
