# PIB TC Spread Analysis

> How the 235 PIB specs in the old WebdriverIO pack are distributed: E2E vs functional, what
> part of the product each E2E targets, and what kind of testing each one actually does.
> PIB is **InnBusiness** — the business-travel channel, covering both booking and a large
> company-administration surface. Companion to `regression-pack-migration-strategy.md`.

**Contents**

1. [The split — E2E vs functional](#1-the-split--e2e-vs-functional)
2. [E2E booking inventory](#2-e2e-booking-inventory)
3. [E2E non-booking inventory](#3-e2e-non-booking-inventory)
4. [Distribution summary](#4-distribution-summary)
5. [What the E2Es actually do](#5-what-the-e2es-actually-do)
6. [Functional areas vs E2E coverage](#6-functional-areas-vs-e2e-coverage)
7. [Conclusions](#7-conclusions)

---

## 1. The Split — E2E vs Functional

| Group                    | Location                          | Specs   | Share |
|--------------------------|-----------------------------------|--------:|------:|
| E2E — booking journeys   | `ib/end2endTests/bookingRebranding`|     34 |   14% |
| E2E — booking smokes     | `ib/end2endTests/smokeTests`      |      11 |    5% |
| E2E — non-booking journeys| `ib/end2endTests` (8 journeys)   |      76 |   32% |
| **E2E subtotal**         |                                   | **121** |**51%**|
| Functional               | `ib/featureTests` (11 folders)    |     114 |   49% |
| **Total**                |                                   | **235** |       |

PIB is the **largest and most E2E-balanced** of the three channels — 51% E2E. It is also the
only channel with a substantial non-booking surface: 76 of its 121 E2Es (63%) never touch a
booking flow at all.

**Structural note.** Unlike PI, PIB's E2E journeys and functional folders are named for the
same eleven areas. This makes coverage mapping in §6 far cleaner than for PI or CCUI, and it
means PIB was built journey-first rather than permutation-first.

---

## 2. E2E Booking Inventory (45 specs)

Role is the primary axis — Travel Manager, Booker and Self-Booker have different permissions,
spending limits and approval paths.

**Column codes**

*Journey* — `B` book · `B·A·C` full funnel · `B·A` book + amend · `A*` amend-only ·
`C*` cancel-only

*Rate* — `BFlex` business flex · `Semi` semi-flex · `Std` standard · `Adv` advance

*Payment* — `PN` pay now · `POA` pay on arrival · `RWCC` reserve without card

*Extras* — `3DS` Worldline/SIX 3-D Secure · `ALW` business allowances · `STC` stored card ·
`WND` windowless room · `WIFI` WiFi extras · `PLN` Planet payment

### 2.1 Travel Manager (19 specs)

| TC     | Locale | Journey | Rate  | Pay | Extras       | Focus area                             |
|--------|:------:|:-------:|:-----:|:---:|:------------:|----------------------------------------|
| 450422 |   UK   |   C*    | BFlex | POA | 3DS          | POA reservation, cancel                |
| 450423 |   DE   |    B    | BFlex | POA | 3DS          | DE POA reservation (rebranding header) |
| 450464 |   UK   |   A*    | BFlex | POA | —            | Amend IB booking add adult **via BB**  |
| 450465 |   UK   |   A*    | BFlex | POA | —            | Amend IB booking remove meal **via CCUI** |
| 343521 |   UK   |  B·A·C  | BFlex | POA | 3DS          | CNP POA, routing instructions + notes  |
| 485261 |   DE   |  B·A·C  | BFlex | PN  | 3DS          | 2 rooms, amend remove room             |
| 485262 |   DE   |  B·A·C  | BFlex | PN  | 3DS          | 1 room, amend add room                 |
| 493806 |   DE   |  B·A·C  | BFlex | PN  | 3DS          | 2 rooms, multiple amends               |
| 369610 |   DE   |  B·A·C  | BFlex | POA | 3DS ALW      | 3 nights, business flex, allowances    |
| 369609 |   DE   |  B·A·C  | BFlex | POA | 3DS          | 4 ACC rooms, 7 adults, meal prefs ON   |
| 369552 |   DE   |  B·A·C  | BFlex |RWCC | ALW          | 3 adults 1 child, reserve without card |
| 369553 |   UK   |  B·A·C  | BFlex | PN  | 3DS ALW      | Meal preferences turned **OFF**        |
| 380707 |   UK   |  B·A·C  | BFlex | POA | 3DS          | Display booking info, change room type |
| 369927 |   UK   |  B·A·C  | BFlex | PN  | —            | 5 nights, TWIN room, breakfast         |
| 369963 |   UK   |  B·A·C  | Adv   | PN  | 3DS          | Advance rate, family room, child meal  |
| 435202 |   UK   |  B·A·C  | BFlex | POA | 3DS WIFI     | WiFi extras, amend remove night        |
| 418505 |   UK   |   B·A   | BFlex | POA | 3DS          | Two sequential add-room amends         |
| 516551 |  HUB   |  B·A·C  | BFlex | POA | 3DS WND      | Windowless DBLNWD, amend add meal      |
| 453028 |   UK   |  B·A·C  | BFlex | PN  | PLN          | **Planet** payment/refund, Meal Deal   |

### 2.2 Booker (11 specs)

| TC     | Locale | Journey | Rate  | Pay | Extras       | Focus area                             |
|--------|:------:|:-------:|:-----:|:---:|:------------:|----------------------------------------|
| 381172 |   DE   |  B·A·C  | BFlex | PN  | 3DS ALW      | DE city tax, dinner allowance, add adult|
| 461367 |   UK   |  B·A·C  | BFlex | POA | 3DS ALW STC  | 2 rooms 2ad 2ch, allowances, amend     |
| 379896 |   UK   |  B·A·C  | BFlex | POA | ALW STC      | 2 nights, personal stored card, BAC POA|
| 369947 |   UK   |  B·A·C  | BFlex | POA | 3DS STC      | **14 nights**, personal stored card    |
| 369934 |   UK   |  B·A·C  | BFlex | POA | 3DS ALW STC  | Accessible LOWDBL, centrally stored card|
| 379917 |   UK   |  B·A·C  | BFlex | POA | 3DS          | Single room, new non-BAC card, add adult|
| 369935 |   UK   |  B·A·C  | BFlex | POA | ALW STC      | 2 nights, BAC, change room type        |
| 369936 |   UK   |  B·A·C  | BFlex | POA | 3DS          | **Premier Plus** double, 2 rooms 3 ad  |
| 369932 |  HUB   |  B·A·C  | BFlex | PN  | 3DS STC      | hub ACCWIN, add meal, personal card    |
| 369931 |  HUB   |  B·A·C  | Semi  | POA | 3DS          | hub BIGWIN, add meals, add night       |
| 516552 |  HUB   |   B·A   | Semi  | POA | 3DS WND      | Windowless BIGNWD, amend add adult     |
| 435202 |   UK   |  B·A·C  | BFlex | POA | 3DS WIFI     | *(TM spec — see §2.1; shown for context, not counted here)* |

### 2.3 Self-Booker (4 specs)

| TC     | Locale | Journey | Rate  | Pay | Extras       | Focus area                             |
|--------|:------:|:-------:|:-----:|:---:|:------------:|----------------------------------------|
| 493803 |   DE   |  B·A·C  | BFlex |RWCC | ALW          | FAM room, RWCC, amend add child + meal |
| 381183 |   DE   |  B·A·C  | BFlex | POA | 3DS ALW      | DE, amend remove adult                 |
| 369929 |   UK   |  B·A·C  | Semi  | POA | ALW STC      | Semi-flex, personal stored card        |
| 519597 |  HUB   |   B·A   | Std   | PN  | 3DS WND      | Windowless ACCNWD, change arrival date  |

### 2.4 Booking Smokes (11 specs)

| TC     | Role          | Journey | Purpose                                  |
|--------|:-------------:|:-------:|------------------------------------------|
| 469382 | Travel Manager|    B    | Happy path, POA PIBA                     |
| 469383 | Booker        |    B    | Happy path, BFlex PN                     |
| 469384 | Self-Booker   |    B    | Happy path, BFlex PN                     |
| 474035 | Booker        |   A*    | Amend add adult                          |
| 474036 | Self-Booker   |   A*    | Amend add adult (role twin)              |
| 469385 | Travel Manager|   A*    | Amend add adult (role twin)              |
| 474037 | Booker        |   C*    | Cancel booking                           |
| 474038 | Self-Booker   |   C*    | Cancel booking (role twin)               |
| 469386 | Travel Manager|   C*    | Cancel booking (role twin)               |
| 197342 | TM / Booker   |    B    | 2 nights, **centrally** stored card      |
| 197342 | TM / Booker   |    B    | 2 nights, **personally** stored card     |

Nine of eleven are **three-way role twins** — book, amend and cancel each run once per role.
Two specs share TC ID 197342, differing only in card storage location.

---

## 3. E2E Non-Booking Inventory (76 specs)

This is what makes PIB structurally different: a full company-administration product with no
PI or CCUI equivalent.

| Journey              | Specs | What it covers                                     |
|----------------------|------:|----------------------------------------------------|
| cardManagement       |    15 | Card CRUD, storage location, delivery, RBAC        |
| profileManagement    |    15 | Payment cards, passwords, preferences, profile edit |
| payApp               |    14 | Credit application by business type, share, submit  |
| companyManagement    |     9 | Booking alerts, company details, allowances, RBAC   |
| userManagement        |     7 | User lifecycle across all four roles                |
| spendingAndReporting |     6 | Spend over time, summaries, role-based visibility   |
| authJourney          |     5 | Register, login, reset, signup, account linking     |
| homeJourney          |     5 | Homepage widgets, notifications, role variants      |
| **Total**            |**76** |                                                     |

Expanded:

- **cardManagement** — add PIBA / Visa / Mastercard / regular cards, centrally stored vs
  personally stored, card delivery options, failure handling, role-based access, DE language.
- **profileManagement** — add payment cards by type, password change validations, meals and
  extras preferences, room preferences, edit profile on UK and DE sites, custom questions,
  contact-centre details.
- **payApp** — InnBusiness Pay application by business type (charity, government-funded
  school, limited company, solo trader, public company, other), share / resume / revoke
  between users, submission, and Whitbread approval.
- **companyManagement** — booking alerts (same-day, weekend), edit company details, main
  contact, allowances (Meal Deal, Ultimate WiFi), employee questions, role-based access.
- **userManagement** — add Booker / Guest / Self-Booker / Travel Manager, activate, resend
  activation, change role, change status, invite an employee to self-register.
- **spendingAndReporting** — spend over time, company spend, spending summary, and which
  roles can see which data.
- **authJourney** — register, login, forgotten password, reset, signup as a new DE company,
  signup into an existing company, link an existing account, marketing opt-in.
- **homeJourney** — homepage widgets, spending summary, notifications, upcoming bookings,
  role variants, suspended-account state.

**Role-based access control is a recurring theme.** At least 4 specs exist purely to assert
that Booker, Self-Booker and Guest *cannot* reach Company Management or Card Management. This
is negative-permission testing, a category absent from PI and CCUI.

---

## 4. Distribution Summary

> **How to read these counts.** Booking/non-booking split, role, locale, journey shape and
> the payment/extras counts are grep-verified against the specs. The two "type of testing"
> tables (§4.7, §4.8) are **primary-intent classifications** — a spec is counted once under
> its distinguishing feature. Coverage claims in §6 are grep-verified against page-object
> usage.

### 4.1 Booking vs non-booking

| Group        | Specs | Share of PIB E2Es |
|--------------|------:|------------------:|
| Non-booking  |    76 |               63% |
| Booking      |    45 |               37% |

### 4.2 Booking specs by role

| Role           | Baselines | Smokes | Total | Share |
|----------------|----------:|-------:|------:|------:|
| Travel Manager |        19 |      4 |    23 |   51% |
| Booker         |        11 |      4 |    15 |   33% |
| Self-Booker    |         4 |      3 |     7 |   16% |

Travel Manager dominates — it is the highest-privilege role and the one that can book on
behalf of others, approve requests and manage company settings. (All five role-less booking
specs — 450422/423, 450464/465, 453028 — log in as a Travel Manager, so TM is 19 baselines.
435202 is a TM spec counted once under TM, not as a Booker. The two shared `197342` smokes are
split one TM / one Booker, and the only Self-Booker smokes are the three role-op trios.)

### 4.3 Booking specs by locale

| Locale | Baselines | Share |
|--------|----------:|------:|
| UK     |        19 |   56% |
| DE     |        10 |   29% |
| HUB    |         5 |   15% |

hub has proportionally the strongest coverage of any channel here (15%), driven by the three
windowless-room specs.

### 4.4 Booking specs by journey shape

| Journey                     | Count | Share |
|-----------------------------|------:|------:|
| amend + cancel (full funnel)|    27 |   60% |
| amend, no cancel (incl. A*) |     8 |   18% |
| cancel, no amend (incl. C*) |     4 |    9% |
| book / view only            |     6 |   13% |

**35 of 45 booking specs (77%) exercise an amend** — the highest amend concentration of the
three channels. (Grep-verified: 35 call amend methods, 31 cancel, 27 do both; 6 book or view
only. An earlier draft split these into five rows that summed to 46.)

### 4.5 Booking specs by rate

| Rate                    | Count |
|-------------------------|------:|
| Business Flex (BFlex)   |    29 |
| Semi-Flex               |     3 |
| Standard                |     1 |
| Advance                 |     1 |
| Not rate-specific       |    11 |

Business Flex is the business-channel default and overwhelmingly dominant.

### 4.6 Booking specs by payment and extras

| Attribute                    | Specs |
|------------------------------|------:|
| Pay on Arrival               |    22 |
| Pay Now                      |    10 |
| RWCC (reserve without card)  |     2 |
| **3-D Secure (Worldline/SIX)**|    27 |
| Business allowances          |    13 |
| Stored card (personal/central)|    10 |
| Windowless rooms             |     3 |
| WiFi extras                  |     1 |
| Planet payment               |     1 |

**3DS is PIB-specific.** CCUI uses Eckoh instead and PI uses a different path, so these 27
specs carry payment coverage that exists nowhere else. (Payment/extras counts are
grep-verified against page-object usage.)

### 4.7 By type of testing — booking

| Type                        | Count | What it means                             |
|-----------------------------|------:|-------------------------------------------|
| `AMEND` Amend-operation     |    14 | The amend action is the subject           |
| `ROLE` Role permutation     |    11 | Same flow across TM / Booker / Self-Booker |
| `PAY` Payment permutation   |     8 | Stored cards, RWCC, Planet, 3DS variants  |
| `OCC` Occupancy permutation |     5 | Multi-room, 14-night, high-adult          |
| `FEAT` Feature-specific     |     5 | Windowless, WiFi, Premier Plus, meal prefs|
| `XCHAN` Cross-channel       |     2 | 450464 (IB→BB), 450465 (IB→CCUI)          |

### 4.8 By type of testing — non-booking

| Type                          | Count | What it means                            |
|-------------------------------|------:|------------------------------------------|
| `CRUD` Entity lifecycle       |    31 | Add, edit, activate, deactivate, delete — cards, users, employees, profiles |
| `WORKFLOW` Multi-step process |    19 | Pay App application, signup, approval, share/resume |
| `RBAC` Role-based access      |    12 | Who can see or do what; includes negative-permission checks |
| `DATA` Reporting / display    |    10 | Spending summaries, statements, homepage widgets |
| `VALIDATION` Form validation  |     4 | Password rules, field limits, DE language |

---

## 5. What the E2Es Actually Do

### 5.1 Booking journeys

The PI booking spine plus a business-travel wrapper:

```
log in as Travel Manager / Booker / Self-Booker
  → search hotel
  → select room + rate (Business Flex default)
  → ancillaries (meals, WiFi, meal preferences on/off)
  → guest details (+ stayer selection, employee questions, custom questions)
  → business allowances applied (dinner budget, spend limits)
  → payment: stored card / new card / RWCC → 3-D Secure via Worldline
  → confirmation + Opera cross-check
  → [amend: add/remove room, adult, child, meal, night; change room type or date]
  → [cancel + verify refund]
```

Two specs break the pattern deliberately: **450464** amends an IB booking through the BB
route and **450465** amends it through CCUI. These are the only cross-channel specs anywhere
in the pack and they test the project-switch path.

### 5.2 Non-booking journeys

These follow admin-product patterns rather than a booking spine:

```
log in with a specific role and tether state
  → navigate to an admin area (Manage, Cards, Profile, Spending, Pay App)
  → perform a CRUD or multi-step workflow
  → assert on state, permissions, notifications, or reported data
  → often: log in as a second role and verify what changed or what is blocked
```

The Pay App journey is the most complex: a multi-page credit application varying by business
type, with sharing between users, resumption, revocation, submission and an external
Whitbread approval step.

**Where coverage is concentrated**

| Area                      | Specs | Note                                       |
|---------------------------|------:|--------------------------------------------|
| Amend operations          |    35 | Highest of any channel (77% of booking)    |
| 3-D Secure                |    27 | PIB-only payment path                      |
| Card management           |    15 | Entirely PIB-specific                      |
| Profile management        |    15 | Entirely PIB-specific                      |
| Pay App workflow          |    14 | Entirely PIB-specific                      |
| Role permutation          |    11 | Deliberate three-way twinning              |

**Thin areas inside the E2Es**

| Area                   | Specs | Note                                                 |
|------------------------|------:|------------------------------------------------------|
| Search / SRP           |     1 | 453028 (map view) only                               |
| Non-BFlex rates        |   1–3 | Per rate                                             |
| Planet payment         |     1 | 453028 — sole owner of the Planet refund path        |
| WiFi extras            |     1 | 435202                                               |
| Cross-channel          |     2 | 450464, 450465 — sole owners of project switch       |
| Spending and reporting |     6 | Against 21 functional specs                          |

---

## 6. Functional Areas vs E2E Coverage

PIB's functional folders and E2E journeys share names, so this maps cleanly. 114 functional
specs across 11 areas.

| Functional area      | Func | E2E | Status  | Short assessment              |
|----------------------|-----:|----:|:-------:|-------------------------------|
| cardManagement       |   27 |  15 | PARTIAL | CRUD + RBAC covered, rest not |
| spendingAndReporting |   21 |   6 | PARTIAL | Summaries only; reports not   |
| auth                 |   12 |   5 | PARTIAL | Core flows only               |
| editEmployee         |    5 |   7 | PARTIAL | Validation uncovered          |
| home                 |    6 |   5 | PARTIAL | Two widgets uncovered         |
| bookingRebranding    |   12 |  34 |   OK    | Functional covers shell chrome|
| payApplication       |    9 |  14 |   OK    | E2E richer than functional    |
| myProfile            |    8 |  15 |   OK    | E2E richer than functional    |
| companyManagement    |    6 |   9 |   OK    | E2E richer than functional    |
| addEmployee          |    4 |   7 |   OK    | Covered by userManagement     |
| manageEmployees      |    4 |   7 |   OK    | Covered by userManagement     |

What the PARTIAL rows leave uncovered:

- **cardManagement** — cost centres and cost-centre assignment, pagination, people picker,
  payment-details iframe open/close behaviour, table headers, resend code, replace card.
- **spendingAndReporting** — MI report, emergency report, out-of-policy report, statements
  (history list, interim payments, totals, navigation, suspended state), transactions, and
  set/reset memorable word.
- **auth** — cookie consent, EAD modal for UK and DE users, security question page,
  set-a-password page.
- **editEmployee** — address validation, alternate-phone validation, personal-details
  validation.
- **home** — InnBusiness Pay widget, upcoming-bookings detail.

**No area has zero E2E coverage.** This is the sharpest contrast with PI (13 zero-coverage
areas) and CCUI (5). PIB was built journey-first, so every functional area has an E2E
counterpart.

### 6.1 Gaps ranked by risk

**High — large functional surface, thin E2E**

1. **spendingAndReporting — 21 functional vs 6 E2E.** The E2Es check spend-over-time and
   summaries for a few roles. Uncovered: MI report, emergency report, out-of-policy report,
   statements (history, interim payments, totals, navigation, suspended state), transactions,
   and set/reset memorable word. For a business-travel product, reporting *is* the product for
   finance users.
2. **cardManagement — 27 functional vs 15 E2E.** Uncovered: cost centres and cost-centre
   assignment, pagination, people picker, payment-details iframe open/close behaviour, table
   headers, resend code, replace card. Cost centres in particular are a business-billing
   concept with no E2E owner.

**Medium**

3. **auth — 12 functional vs 5 E2E.** Uncovered: cookie consent, EAD modal for both UK and DE
   users, security question page, set-a-password page.
4. **editEmployee validation — 5 functional, partial E2E.** Address, alternate phone and
   personal-details field validation.
5. **home — 6 functional vs 5 E2E.** InnBusiness Pay widget and upcoming-bookings detail.

**Booking-side gaps (not visible in the table above)**

6. **Payment declines.** As with PI and CCUI, no PIB E2E exercises a failed payment or a
   failed 3DS challenge. Given PIB is the only channel using Worldline 3DS, a decline or
   challenge-failure path here has no coverage anywhere.
7. **SRP / search.** One spec (453028) touches map view. Everything else navigates directly.
8. **Single-owner risks.** Planet payment (453028), WiFi extras (435202), and cross-channel
   project switch (450464, 450465) each have exactly one owner. Losing any of them removes the
   capability from the pack entirely.

### 6.2 Known defects affecting coverage

Two booking specs have skip guards that make them effectively dead on an English-website run:

| TC     | Guard                             | Hotel booked                 |
|--------|-----------------------------------|------------------------------|
| 435202 | skips when `isEnglishWebsite()`   | `LONDON_HEATHROW_AIRPORT`, GBP |
| 519597 | skips when `isEnglishWebsite()`   | `LONDON_KING_CROSS`          |

Both claim to be DE-only but book UK hotels. On a UK-primary run neither executes, which means
WiFi extras and windowless ACCNWD may currently have **zero** live coverage despite appearing
in the pack.

---

## 7. Conclusions

**Ratio.** 121 E2Es against 114 functional specs — E2Es are 51% of the 235 PIB specs, the
most balanced of the three channels. PIB is also the largest channel by a wide margin.

**The defining characteristic.** 63% of PIB's E2Es (76 of 121) are **non-booking** — company
administration, user lifecycle, card management, profile management, credit applications,
spending reports. This surface has no equivalent in PI or CCUI, which is why the migration
strategy treats PIB non-booking as unavoidable 1:1 work with no cross-app dedup available.

**What kind of tests the booking E2Es are.** Amend-dominated (14 primary, 35 touching an
amend — 77%, the highest of any channel), then role permutation (11 — three-way TM / Booker /
Self-Booker twinning), payment permutation (8 — stored cards, RWCC, Planet, 3DS), occupancy
(5), and feature-specific (5). Two are cross-channel and unique in the whole pack.

**What kind of tests the non-booking E2Es are.** Entity lifecycle CRUD (31), multi-step
workflows (19 — mostly Pay App and signup), role-based access control including
negative-permission checks (12), reporting and data display (10), and form validation (4).

**What they do.** Booking specs walk the PI spine wrapped in business-travel concerns —
role login, employee questions, business allowances, and 3-D Secure via Worldline. Non-booking
specs follow an admin pattern: log in with a specific role and tether state, navigate to an
admin area, perform a workflow, then often log in as a second role to verify what changed or
what is blocked.

**What is unique to PIB and must not be cut.** 3-D Secure via Worldline (27 owners), business
allowances and dinner budgets (13), stored-card billing (10), the entire non-booking admin
surface (76), cross-channel project switch (2), and Planet payment (1). None of this exists in
PI or CCUI.

**What is uncovered.** No functional area has zero E2E coverage — a much healthier position
than PI or CCUI. The real risks are thinness and single points of failure:

1. **spendingAndReporting** — 21 functional specs against 6 E2Es. Reports and statements are
   largely E2E-uncovered.
2. **cardManagement** — 27 functional against 15 E2Es. Cost centres have no E2E owner.
3. **Payment declines and 3DS failures** — zero coverage, and PIB is the only channel using
   Worldline, so the gap is absolute.
4. **Two specs may not run at all** — 435202 and 519597 have inverted skip guards, so WiFi
   extras and windowless ACCNWD may have no live coverage.

**Implication for the migration.** PIB splits cleanly into two very different problems. The
booking side (45 specs) is the richest consolidation pool in the entire pack — role × rate ×
payment permutation on one backbone, which is exactly what the strategy's budget targets. The
non-booking side (76 specs) is nearly all sole-owner work with no dedup lever, and it is the
largest single block of net-new page-object effort in the plan.

Three things are worth doing rather than accepting:

- **Verify 435202 and 519597 actually execute** before porting them. If they do not, they are
  cuts, not migrations.
- **Protect the single-owner capabilities** — Planet payment, WiFi extras, and both
  cross-channel specs. These are the specs where a budget cut would silently remove a
  capability.
- **Consider a 3DS-failure E2E.** PIB owns the only Worldline path in the pack and nothing
  tests what happens when the challenge fails.
