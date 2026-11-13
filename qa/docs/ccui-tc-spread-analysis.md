# CCUI TC Spread Analysis

> How the 72 CCUI specs in the old WebdriverIO pack are distributed: E2E vs functional, what
> part of the product each E2E targets, and what kind of testing each one actually does.
> CCUI is the **Contact Centre UI** — used by receptionists, telephone agents and travel
> agencies to manage bookings. Companion to `regression-pack-migration-strategy.md`.

**Contents**

1. [The split — E2E vs functional](#1-the-split--e2e-vs-functional)
2. [E2E inventory by role](#2-e2e-inventory-by-role)
3. [Distribution summary](#3-distribution-summary)
4. [What the E2Es actually do](#4-what-the-e2es-actually-do)
5. [Functional areas vs E2E coverage](#5-functional-areas-vs-e2e-coverage)
6. [Conclusions](#6-conclusions)

---

## 1. The Split — E2E vs Functional

| Group            | Location                      | Specs  | Share |
|------------------|-------------------------------|-------:|------:|
| E2E — baselines  | `ccui/baselineTests`          |     26 |   36% |
| E2E — smokes     | `ccui/smokeTests`             |      7 |   10% |
| **E2E subtotal** |                               | **33** |**46%**|
| Functional       | `ccui/featureTests` (9 folders)|     39 |   54% |
| **Total**        |                               | **72** |       |

CCUI is **more E2E-weighted than PI** — 46% E2E versus PI's 36%. One baseline (369556, MLOS)
is a hard `describe.skip`, so effective E2E coverage is 32 of 33.

Only the **33 E2Es** are analysed below. Functional specs are used in §5 to measure gaps.

---

## 2. E2E Inventory by Role

Role is the primary axis in CCUI — an agent and a manager see different screens and have
different permissions. Split by role, guest-facing attributes second.

**Column codes**

*Journey* — `B` book · `B·A·C` full funnel · `A*` amend-only, booking seeded by API ·
`CPM` change-payment-method flow · `V` view/select only, never completes a booking

*Rate* — `Flex` · `Semi` semi-flex · `Std` standard · `Adv` advance · `Contract` negotiated

*Payment* — `PN` pay now · `POA` pay on arrival · `RWCC` reserve without card ·
`NonGuar` non-guaranteed

*Type* — `AMEND` amend-operation · `CONTRACT` contract/negotiated rate · `A2C` account-to-company ·
`OCC` occupancy permutation · `FEAT` feature-specific · `SKIP` permanently skipped

### 2.1 Standard Agent (14 specs)

| TC     | Locale | Journey | Rate     | Payment | Focus area                            | Type     |
|--------|:------:|:-------:|:--------:|:-------:|---------------------------------------|:--------:|
| 381188 |   DE   |  B·A·C  | Flex     |   PN    | Single room, amend add adult          | AMEND    |
| 381191 |   DE   |  B·A·C  | Flex     |   PN    | Amend remove adult                    | AMEND    |
| 381189 |   DE   |  B·A·C  | Flex     |   PN    | 2 rooms, amend remove room            | AMEND    |
| 381190 |   DE   |  B·A·C  | Flex     |   PN    | 2 rooms, amend add room               | AMEND    |
| 379921 |   DE   |  B·A·C  | Flex     |   PN    | Double room, non-BAC, amend           | AMEND    |
| 369930 |   DE   |  B·A·C  | Adv      |   PN    | 2 rooms, 3 adults 2 kids, breakfast   | OCC      |
| 380723 |   UK   |  B·A·C  | Flex     |   PN    | Amend change room type, add child     | AMEND    |
| 379925 |   UK   |  B·A·C  | Flex     |   POA   | Single room, BAC, amend add adult     | AMEND    |
| 379940 |   UK   |  B·A·C  | Flex     |   POA   | TWIN room, BAC, increase nights       | AMEND    |
| 380724 |   UK   |  B·A·C  | Flex     |   PN    | Accessible room, decrease nights      | AMEND    |
| 428938 |   UK   |    B    | Contract |   PN    | Contract rate by **Company ID**       | CONTRACT |
| 428939 |   UK   |    B    | Contract |   PN    | Contract rate by **Corp ID**, 3n, ACC | CONTRACT |
| 369555 |   UK   |    B    | Std      |   PN    | Thinnest spec; SRP steps commented out| FEAT     |
| 531538 |  HUB   |    V    | Flex     |    —    | Windowless room, continue to payment  | FEAT     |

### 2.2 Manager (12 specs)

| TC            | Locale | Journey | Rate     | Payment | Focus area                              | Type     |
|---------------|:------:|:-------:|:--------:|:-------:|-----------------------------------------|:--------:|
| 384400        |   DE   |   CPM   | Flex     | NonGuar | A2C: company search, guarantee code CO  | A2C      |
| 384401        |   DE   |   CPM   | Flex     | NonGuar | Change payment to card, guarantee CC    | A2C      |
| 368688        |   DE   |   A*    | Flex     |  RWCC   | RWCC amend: extend, remove, meals       | AMEND    |
| 369933+408182 |   DE   |  B·A·C  | Flex     |   POA   | A2C company ref, 4 rooms, 7 adults      | OCC      |
| 369962        |   DE   |  B·A·C  | Contract |   POA   | Family + Accessible, contract, A2C      | CONTRACT |
| 369950        |   DE   |    B    | Contract |    —    | Contract, child bkfst, non-guar, caller | CONTRACT |
| 369977        |  HUB   |  B·A·C  | Flex     |   PN    | 9 nights, 4 rooms, 8 adults, remove room| OCC      |
| 461417+464541 |   UK   |  B·A·C  | Adv      |   PN    | Advance refund path — richest CCUI spec | AMEND    |
| 369964        |   UK   |  B·A·C  | Semi     |   PN    | TripAdvisor no-award, free child bkfst  | AMEND    |
| 380725        |   UK   |  B·A·C  | Semi     |   PN    | TWIN + Family, amend remove child meal  | AMEND    |
| 409154        |   UK   |    B    | Flex     |   PN    | Accompanying guest details, multi-room  | OCC      |
| 369556        |   UK   |  B·A·C  | Flex     |   POA   | MLOS restriction — **`describe.skip`**  | SKIP     |

### 2.3 Smokes (7 specs)

| TC     | Role    | Locale | Journey | Purpose                                |
|--------|:-------:|:------:|:-------:|----------------------------------------|
| 470152 | Agent   |  Any   |    B    | Happy-path wiring check                |
| 470151 | Manager |  Any   |    B    | Happy-path wiring check (role twin)    |
| 474131 | Agent   | UK/DE  |   A*    | Amend add adult                        |
| 474130 | Manager | UK/DE  |   A*    | Amend add adult (role twin)            |
| 474133 | Agent   | UK/DE  |   C*    | Cancel booking                         |
| 474132 | Manager | UK/DE  |   C*    | Cancel booking (role twin)             |
| 115170 | Manager |   DE   |    B    | SRP map view, SRP-vs-HDP price, type-of-caller, non-guaranteed FMTRPL |

Six of the seven are **agent/manager twins** — three pairs running the same flow under two
roles. 115170 is misclassified: tagged `#sanity#` but it solely owns the CCUI SRP map view,
the SRP-vs-HDP price comparison and the type-of-caller flow, which is baseline-weight work.

---

## 3. Distribution Summary

> **How to read these counts.** Role, locale, journey shape and the channel-feature counts
> are grep-verified against the specs. Payment method and "type of testing" are
> **primary-intent classifications** — a spec is counted once under its distinguishing
> feature. Coverage claims in §5 are grep-verified against page-object usage.

### 3.1 By role

| Role           | Baselines | Smokes | Total | Share |
|----------------|----------:|-------:|------:|------:|
| Standard Agent |        14 |      3 |    17 |   52% |
| Manager        |        12 |      4 |    16 |   48% |

An even split by design. Manager specs skew toward A2C, contract rates and larger occupancy;
agent specs skew toward amend operations on simple bookings.

### 3.2 By locale

| Locale                | Baselines | Smokes | Total | Share |
|-----------------------|----------:|-------:|------:|------:|
| DE                    |        12 |      1 |    13 |   39% |
| UK                    |        12 |      — |    12 |   36% |
| HUB                   |         2 |      — |     2 |    6% |
| UK/DE or any (params) |         — |      6 |     6 |   18% |

### 3.3 By journey shape

| Journey                     | Count | Share | Note                              |
|-----------------------------|------:|------:|-----------------------------------|
| `B·A·C` full funnel         |    17 |   52% | Dominant pattern                  |
| `B` book only               |     5 |   15% | Contract rates, guest details     |
| `A*` amend-only             |     3 |    9% | 1 baseline + 2 smokes             |
| `B` smoke (no post-ops)     |     3 |    9% |                                   |
| `CPM` change-payment-method |     2 |    6% | A2C guarantee-code flows          |
| `C*` cancel-only            |     2 |    6% |                                   |
| `V` view/select only        |     1 |    3% | 531538 never completes a booking  |

**20 of 33 (61%) exercise an amend** and 19 (58%) cancel. Same amend-heavy profile as PI.

### 3.4 By rate

| Rate                | Count |
|---------------------|------:|
| Flex                |    17 |
| Contract/negotiated |     4 |
| Semi-Flex           |     2 |
| Advance             |     2 |
| Standard            |     1 |
| Not rate-specific   |     7 |

**Contract rate is CCUI-only** and has 4 owners — one of the few places CCUI carries coverage
PI does not have at all.

### 3.5 By payment method

| Payment method        | Count |
|-----------------------|------:|
| Pay Now               |    16 |
| Pay on Arrival        |     7 |
| Non-guaranteed        |     3 |
| RWCC                  |     2 |
| Not payment-specific  |     5 |

### 3.6 Channel-specific features

These are what make a CCUI spec worth keeping when PI already covers the booking backbone.

| Feature                          | Specs | Owners                                    |
|----------------------------------|------:|-------------------------------------------|
| Eckoh tokenisation               |    22 | Nearly all — the CCUI payment path        |
| BAC / non-BAC payment auth       |    11 | Agent and manager specs alike             |
| A2C (account-to-company)         |     7 | 384400, 384401, 428938/9, 369933, 369962, 369950 |
| Contract / negotiated rate       |     4 | 428938, 428939, 369962, 369950            |
| Non-guaranteed booking           |     5 | 384400/1, 369950, 368688 (RWCC), 115170   |
| Type-of-caller / accessible cust.|     2 | 369950, 115170                            |
| Guarantee codes (CO / CC)        |     2 | 384400 (CO), 384401 (CC)                  |

### 3.7 By type of testing

| Type                       | Count | What it means                                |
|----------------------------|------:|----------------------------------------------|
| `AMEND` Amend-operation    |    13 | The amend action is the subject              |
| `CONTRACT` Contract rate   |     4 | Negotiated rate by Company ID or Corp ID     |
| `OCC` Occupancy permutation|     4 | Multi-room, high-adult, accompanying guests  |
| `SMOKE` Smokes             |     7 | 6 agent/manager role twins + 115170          |
| `A2C` Account-to-company   |     2 | Guarantee codes, change payment method       |
| `FEAT` Feature-specific    |     2 | Windowless view, thin standard-rate spec     |
| `SKIP` Permanently skipped |     1 | 369556 MLOS                                  |

CCUI is **much more amend-dominated than PI** — 13 of 26 baselines exist mainly to cover one
amend operation. Feature-specific journeys, which are PI's largest group, are almost absent.

---

## 4. What the E2Es Actually Do

The skeleton is the PI booking flow plus a contact-centre wrapper:

```
agent or manager logs into CCUI
  → identify caller (ID&V / DPA, type-of-caller)
  → search hotel (SRP or direct)
  → select room + rate (incl. contract rate by Company/Corp ID)
  → ancillaries (meals, breakfast)
  → guest details (+ accompanying guests, A2C company reference)
  → payment via Eckoh (PN / POA / BAC / non-guaranteed / RWCC)
  → confirmation + Opera cross-check
  → [amend: add/remove room, adult, child, meal; change room type; change dates]
  → [cancel + verify status]
```

**Where coverage is concentrated**

| Area                          | Specs | Note                                    |
|-------------------------------|------:|-----------------------------------------|
| Amend operations              |    20 | Every amend op has an owner, most twice |
| Eckoh payment path            |    22 | The dominant CCUI-specific asset        |
| BAC / payment authorisation   |    11 | Well covered                            |
| Agent vs manager role parity  |    16 | Deliberate twinning                     |

**Thin areas inside the E2Es**

| Area                     | Specs | Note                                              |
|--------------------------|------:|---------------------------------------------------|
| Search / SRP             |     2 | 115170 (map view) and 369555 (steps commented out)|
| MLOS                     |     1 | 369556, and it is skipped — effectively zero      |
| Contract rate amends     |     1 | 369962 amends (reduce nights) + cancels; 428938/9, 369950 book only |
| hub brand                |     2 | 369977, 531538                                    |
| TripAdvisor              |     1 | 369964, no-award case only                        |

---

## 5. Functional Areas vs E2E Coverage

The 39 functional specs cluster into 9 areas. Sorted by gap severity.

| Functional area              | Specs | Status  | E2E coverage                    |
|------------------------------|------:|:-------:|---------------------------------|
| changeLogAgentId             |     3 | **GAP** | None                            |
| repeatBooking                |     2 | **GAP** | None                            |
| companyNameSpecialCharacters |     1 | **GAP** | None                            |
| manageBooking (3-criteria)   |     1 | **GAP** | None                            |
| eciLco                       |    16 | PARTIAL | 369930 (deep, single owner)     |
| amendA2C                     |     8 | PARTIAL | 384400, 384401 only             |
| negotiatedRates              |     3 | PARTIAL | 428938/9, 369962, 369950 book only |
| accompanyingGuestDetails     |     4 |   OK    | 409154                          |
| tripAdvisor                  |     1 |   OK    | 369964                          |

What the PARTIAL rows leave uncovered:

- **eciLco** — one baseline, 369930, covers ECI/LCO deeply: add/remove per room and
  all-rooms, per-room labels, cost in the total, ECI/LCO in the BIC on manage-booking, and
  ECI/LCO refund on amend. What the 16 functional specs add on top and no E2E covers: cancel
  with packages, multi-night confirmation, no-availability, hide/remove ECI and LCO, the
  9-rooms back-button path, and manage-booking BIC for a single night.
- **amendA2C** — cancellation, cancellation override, payment-page restrictions, update
  company reference, update email address, back-navigation from amend and from browser.
- **negotiatedRates** — cancel-company-check flow (both from search and from profile) and
  Corp ID field validation. Of the four contract E2Es only 369962 amends and cancels a
  contract-rate booking (428938/9 and 369950 book only), and none exercises the
  cancel-company-check flow or Corp ID validation.
- **manageBooking** — search with three criteria combined; 380724 only checks the
  cancelled-booking table.

### 5.1 Gaps ranked by risk

**High**

1. **amendA2C — 8 specs, 2 partially covering.** A2C bookings are a contact-centre-specific
   commercial flow. The E2Es cover creating one and changing its payment method, but not
   cancellation, cancellation override, payment-page restrictions, updating company reference
   or email, or back-navigation behaviour.
2. **ECI/LCO — 16 functional, 1 E2E (partial).** Not a full gap: 369930 exercises ECI/LCO
   deeply inside a book-and-amend journey (add/remove per room, BIC, amend refund). But it is
   a **single owner** carrying the whole channel's ECI/LCO E2E coverage, and the functional
   layer adds cancel-with-packages, multi-night confirmation, no-availability, and hide/remove
   paths that no E2E touches. Losing 369930 would drop CCUI ECI/LCO E2E coverage to zero.

**Medium**

3. **changeLogAgentId — 3 specs, zero E2E.** Audit trail showing which agent amended or
   cancelled a booking, including on RWCC bookings. Compliance-relevant and untested E2E.
4. **negotiatedRates — 3 specs, partial.** Contract-rate booking is covered; the
   cancel-company-check flow and Corp ID field validation are not.
5. **repeatBooking — 2 specs, zero E2E.** Repeat booking for accessible and twin rooms with
   booking-for-someone-else.

**Lower**

6. **manageBooking search with three criteria** (1 spec).
7. **companyNameSpecialCharacters on booker address** (1 spec).

### 5.2 Also uncovered — inherited from the skip

**MLOS has no working coverage at all.** 369556 is the only MLOS spec in CCUI and it is a
hard `describe.skip`. Nothing in the functional layer covers it either. This is a genuine
zero-coverage area disguised as a passing suite.

---

## 6. Conclusions

**Ratio.** 33 E2Es against 39 functional specs — E2Es are 46% of the 72 CCUI specs, a
noticeably higher share than PI's 36%. One E2E is permanently skipped, so 32 run.

**What kind of tests the E2Es are.** Overwhelmingly **amend-operation coverage**: 13 of 26
baselines exist mainly to exercise one amend action (add/remove adult, add/remove room,
change room type, increase/decrease nights, remove child meal). After that come contract
rate (4), occupancy permutations (4), and A2C payment flows (2). Six of the seven smokes are
agent/manager twins. Feature-specific journeys — PI's largest category — are nearly absent,
because in CCUI the *channel itself* is the feature.

**What they do.** The PI booking spine wrapped in a contact-centre shell: agent or manager
login, caller identification, then search → select → ancillaries → guest details → payment
via Eckoh → confirmation with Opera cross-check → amend → cancel. 61% amend, 58% cancel.

**Where coverage is concentrated.** Eckoh tokenisation (22 specs), amend operations (20),
BAC authorisation (11), and deliberate agent/manager role parity (16). The redundancy here is
what the migration strategy's channel-delta lever targets: most of these specs re-assert the
same Opera backbone PI already owns, and only the Eckoh/role/A2C/contract parts are unique.

**What is unique to CCUI and must not be cut.** Contract/negotiated rate (4 owners), A2C and
guarantee codes CO/CC (7), non-guaranteed bookings (5), type-of-caller and accessible-customer
handling (2), and the Eckoh payment path itself. None of these exist in PI.

**What is uncovered.** Four areas have zero E2E coverage:

1. **MLOS — no working coverage anywhere**, because the only spec (369556) is skipped.
2. **changeLogAgentId — audit trail of who amended or cancelled.** Compliance-relevant.
3. **repeatBooking** and **companyNameSpecialCharacters on booker address**.

Also partial: ECI/LCO (16 functional but only 1 E2E owner, 369930), amendA2C (8 functional
vs 2 E2E), negotiatedRates (cancel-company-check flow), manage-booking multi-criteria search.

**Implication for the migration.** CCUI is the channel where the strategy's channel-delta
thinning applies most aggressively, and the data supports it — the amend-heavy baselines
mostly duplicate PI's backbone. Three things are worth attention rather than accepting the
gaps:

- **Protect 369930.** It is the single E2E owner of ECI/LCO for CCUI. It reads as an
  occupancy spec but carries the whole channel's ECI/LCO coverage, so a budget cut would
  silently remove it. If anything, the functional cancel/hide/remove paths are worth folding
  in rather than dropping.
- **Resolve MLOS.** Either fix 369556 or formally retire the requirement. A permanently
  skipped spec is worse than no spec, because the suite looks green.
- **Consider a changeLogAgentId E2E.** The audit trail of which agent amended or cancelled a
  booking is compliance-relevant and has no E2E owner.
