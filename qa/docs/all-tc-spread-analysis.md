# All TC Spread Analysis (Macro)

> **Scope note.** The full old WebdriverIO pack is **473 spec files** (strategy §1.1). This
> roll-up covers the **438** specs in the three customer channels analysed by the spread docs. It
> excludes the two suites the strategy also leaves out — **BB (25, functional-only)** and
> **distribution routing (10, backend-only)** — which with the 438 make up the 473.

**Contents**

1. [The split — E2E vs functional, by application](#1-the-split--e2e-vs-functional-by-application)
2. [By journey shape](#2-by-journey-shape)
3. [By payment method](#3-by-payment-method)
4. [By type of testing](#4-by-type-of-testing)
5. [Coverage gaps — E2E vs functional, by application](#5-coverage-gaps--e2e-vs-functional-by-application)
6. [Estimated coverage gap if only E2Es are kept](#6-estimated-coverage-gap-if-only-e2es-are-kept)

> **How to read the counts.** Every figure here is lifted from the three source documents.
> Spec counts, brand/locale, role and journey shape are grep-verified in those docs. Payment
> method and "type of testing" are **primary-intent classifications** — a spec is normally
> counted once under its distinguishing feature. Two known quirks are carried forward and
> footnoted: PIB reports journey/payment only for its 45 booking specs (its 76 non-booking
> E2Es have no booking axis), and PI's type-of-testing table is non-exclusive (it sums to 54
> classifications across 47 specs).

---

## 1. The Split — E2E vs Functional, by Application

| Application           |     E2E | Functional | Harness† |   Total | E2E share |   E2E : Func |
| --------------------- | ------: | ---------: | -------: | ------: | --------: | -----------: |
| PIB — InnBusiness     |     121 |        114 |        0 |     235 |       51% |     1.06 : 1 |
| PI — Premier Inn      |      47 |         80 |        4 |     131 |       36% |      1 : 1.7 |
| CCUI — Contact Centre |      33 |         39 |        0 |      72 |       46% |     1 : 1.18 |
| **Total**             | **201** |    **233** |    **4** | **438** |   **46%** | **1 : 1.16** |

† PI's 4 harness files (`createReservations`, `confirmReservationTest`, `cancelReservations`,
`examples`) are data-setup utilities with no Zephyr ID — not tests. PIB and CCUI have none.

**Read-out.** PIB is the largest channel and the only E2E-majority one (51%), driven by a 76-spec
non-booking administration surface that has no PI or CCUI equivalent. PI is the most
functional-heavy (61% functional, ~1 E2E per 1.7 functional). CCUI sits between the two and is
more E2E-weighted than PI. Across all three, the functional layer is still the larger half of the
pack (233 vs 201).

### 1.1 Reconciliation with the migration strategy (source of truth)

Every count above maps onto the strategy's scope table (§1.1 of
`regression-pack-migration-strategy.md`):

| Strategy scope category                     |   Specs | Where it sits in this roll-up                |
| ------------------------------------------- | ------: | -------------------------------------------- |
| In-scope E2E regressions                    |     201 | The **E2E** column (PI 47, CCUI 33, PIB 121) |
| Functional-only (PI 80 + CCUI 39 + PIB 114) |     233 | The **Functional** column                    |
| PI harness scripts (data setup)             |       4 | The **Harness** column                       |
| **Covered by this roll-up**                 | **438** | The three customer channels                  |
| BB specs (functional-only)                  |      25 | *Excluded* — no spread analysis              |
| Distribution routing (backend-only)         |      10 | *Excluded* — no spread analysis              |
| **Full WebdriverIO pack (strategy §1.1)**   | **473** | 438 covered + 35 excluded                    |

**One correction from the source of truth.** The whole pack is 473, not 438 — the 438 figure is
the three analysed channels only. All per-application E2E/functional splits, the 201/233 totals,
and the PIB 45-booking / 76-non-booking split match the strategy exactly.

**What happens to the 201 E2Es.** The strategy migrates all 201 in-scope E2Es to Playwright and
consolidates them **losslessly to 165** (§1.3: PI 47→37, CCUI 33→24, PIB booking 45→34, PIB
non-booking 76→70). The consolidation drops no E2E *assertion* — it only removes duplicate files —
so the E2E coverage the gaps in §§5–6 are measured against is identical whether counted as 201
raw files or the 165 migrated specs.

---

## 2. By Journey Shape

Journey shape only applies to specs that walk a booking flow. PIB's 76 non-booking E2Es
(admin/CRUD/workflow) are shown as their own row so the table reconciles to all 201 E2Es. PIB's
booking journeys are reported in four grouped shapes in the source, mapped here to the canonical
categories used by PI and CCUI.

| Journey shape                           | PIB |  PI | CCUI |   Total | Share |
| --------------------------------------- | --: | --: | ---: | ------: | ----: |
| Non-booking / admin (no booking flow)   |  76 |   — |    — |      76 |   38% |
| `B·A·C` full funnel (book+amend+cancel) |  27 |  16 |   17 |      60 |   30% |
| `B` book-only / view-only               |   6 |  15 |    9 |      30 |   15% |
| `B·A` / `A*` book + amend (no cancel)   |   8 |  12 |    3 |      23 |   11% |
| `B·C` / `C*` book + cancel (no amend)   |   4 |   4 |    2 |      10 |    5% |
| `CPM` change-payment-method (A2C)       |   — |   — |    2 |       2 |    1% |
| **Total**                               | 121 |  47 |   33 | **201** |       |

**Read-out.** Ignoring PIB's admin block, the full funnel (book → amend → cancel) is the dominant
shape everywhere — 60 specs, 30% of all E2Es. Amend is the most-exercised booking operation
across the whole pack: **83 of 125 booking E2Es (66%) touch an amend** (PIB 35, PI 28, CCUI 20),
and 60 of those also cancel. This concentration is exactly where the migration strategy finds its
consolidation headroom.

---

## 3. By Payment Method

Primary (mutually exclusive) payment method per spec. PIB's non-booking specs have no payment and
are shown as a reconciling row. PI's Pay Now row folds in its explicit-brand variants (Visa, AMEX,
saved-card CVV). Channel-specific payment **technology** (Eckoh, Worldline 3DS, Planet) is a
separate cross-cutting axis, summarised beneath the table.

| Payment method                   | PIB |  PI | CCUI |   Total | Share |
| -------------------------------- | --: | --: | ---: | ------: | ----: |
| Non-booking / admin (no payment) |  76 |   — |    — |      76 |   38% |
| Pay Now (card at booking)‡       |  10 |  21 |   16 |      47 |   23% |
| Pay on Arrival                   |  22 |   8 |    7 |      37 |   18% |
| Not payment-specific (smokes)    |  11 |   6 |    5 |      22 |   11% |
| RWCC (reserve without card)      |   2 |   3 |    2 |       7 |    3% |
| PIBA (pay in advance)            |   — |   6 |    — |       6 |    3% |
| BAC (payment authorisation)      |   — |   3 |    — |       3 |    1% |
| Non-guaranteed booking           |   — |   — |    3 |       3 |    1% |
| **Total**                        | 121 |  47 |   33 | **201** |       |

‡ PI's Pay Now (21) = 17 generic + 2 explicit Visa + 1 AMEX + 1 saved-card CVV-only.

**Channel-specific payment technology (cross-cutting; overlaps the rows above).**

| Payment tech / path              | Owner channel | Specs | Note                                            |
| -------------------------------- | :-----------: | ----: | ----------------------------------------------- |
| Eckoh tokenisation               |     CCUI      |    22 | The CCUI payment path; exists nowhere else      |
| Worldline / SIX 3-D Secure       |      PIB      |    27 | PIB-only; CCUI uses Eckoh, PI a different path  |
| Stored card (personal / central) |      PIB      |    10 | Business stored-card billing                    |
| Planet payment / refund          |      PIB      |     1 | Single owner (453028)                           |
| BAC / non-BAC authorisation      |   PI + CCUI   |   ~14 | PI 3 (primary) + CCUI 11 (as a channel feature) |

**Read-out.** Pay Now and Pay on Arrival dominate the booking specs (84 of 125). The interesting
coverage is in the channel-unique payment paths — Eckoh (CCUI), Worldline 3DS (PIB) and Planet
(PIB) — each of which lives in exactly one channel and must survive any consolidation. **No
channel has a payment-decline or failed-authentication path in either layer** (see §5).

---

## 4. By Type of Testing

Unified test-type taxonomy across all three channels. Booking/journey types and PIB's non-booking
administration types (suffixed *admin*) are combined into one table and sorted by total. This is
the only breakdown that classifies all 201 E2Es.

| Test type                                 | PIB |  PI | CCUI |    Total |
| ----------------------------------------- | --: | --: | ---: | -------: |
| `AMEND` — amend operation is the subject  |  14 |  12 |   13 |       39 |
| `CRUD` — entity lifecycle *(admin)*       |  31 |   — |    — |       31 |
| `FEAT` — feature-specific journey         |   5 |  15 |    2 |       22 |
| `WORKFLOW` — multi-step process *(admin)* |  19 |   — |    — |       19 |
| `ROLE` — role permutation                 |  11 |   — |    6 |       17 |
| `PAY` — payment permutation               |   8 |   7 |    — |       15 |
| `OCC` — occupancy permutation             |   5 |   6 |    4 |       15 |
| `RBAC` — role-based access *(admin)*      |  12 |   — |    — |       12 |
| `DATA` — reporting / display *(admin)*    |  10 |   — |    — |       10 |
| `PIPE` — pipeline smokes                  |   — |   6 |    1 |        7 |
| `CONTRACT` — contract / negotiated rate   |   — |   — |    4 |        4 |
| `VALIDATION` — form validation *(admin)*  |   4 |   — |    — |        4 |
| `RATE` — rate permutation                 |   — |   3 |    — |        3 |
| `LOC` — locale / tax logic                |   — |   3 |    — |        3 |
| `USER` — user-type permutation            |   — |   2 |    — |        2 |
| `A2C` — account-to-company                |   — |   — |    2 |        2 |
| `XCHAN` — cross-channel                   |   2 |   — |    — |        2 |
| `SKIP` — permanently skipped              |   — |   — |    1 |        1 |
| **Total**                                 | 121 | 54* |   33 | **208*** |

\* PI's column sums to **54 classifications across its 47 E2Es** — the PI source classifies some
specs under more than one primary intent (e.g. a DE city-tax spec that also amends counts under
both `LOC` and `AMEND`). PIB (121) and CCUI (33) reconcile exactly to their spec counts. The 208
grand total is therefore 201 specs + 7 PI double-counts. Treat the PI column and the total as
*classification counts*, not spec counts. CCUI's 13 `AMEND` are its amend baselines; its 7 smokes
appear as 6 agent/manager role twins (`ROLE`) + the 115170 `#sanity#` SRP smoke (`PIPE`).

**Read-out.** Two very different populations sit in this table. The **booking/journey** E2Es are
amend-dominated (39), then feature-specific (22, almost all PI), role permutation (17) and the
payment/occupancy permutations (15 each). The **administration** E2Es (PIB only, 76 specs) are a
separate world — entity CRUD (31), multi-step workflows (19), role-based access control (12),
reporting (10) and validation (4). This split is why PIB non-booking is treated as unavoidable
1:1 migration work: nothing in PI or CCUI overlaps it.

---

## 5. Coverage Gaps — E2E vs Functional, by Application

What the **functional** layer tests that the **E2E** layer does not. Each source doc classifies
every functional area as **GAP** (zero E2E coverage), **PARTIAL** (E2E covers the core, functional
adds edges) or **OK** (E2E fully covers it). Rolled up:

| Application | Functional areas | OK (E2E covers) | PARTIAL | GAP (zero E2E) | Func specs in GAP areas |
| ----------- | ---------------: | --------------: | ------: | -------------: | ----------------------: |
| PI          |               21 |               4 |       5 |             12 |                      25 |
| CCUI        |                9 |               2 |       3 |              4 |                       7 |
| PIB         |               11 |               6 |       5 |              0 |                       0 |
| **Total**   |           **41** |          **12** |  **13** |         **16** |                  **32** |

PIB stands apart: **no functional area has zero E2E coverage**, versus 12 zero-coverage areas in
PI and 4 in CCUI. PIB was built journey-first, so every functional area has an E2E counterpart —
its gaps are *thinness*, not *absence*.

### 5.1 The concrete gaps

**PI — 12 areas with no E2E coverage, plus 3 thin PARTIALs**

| Functional area                             | Func specs | Risk | What the E2E layer misses                                         |
| ------------------------------------------- | ---------: | :--: | ----------------------------------------------------------------- |
| paymentDeclines                             |          3 | High | Failed payment: contact-bank, wrong details, retry                |
| groupWebForm                                |          4 | High | The >10-room group booking journey (a separate flow)              |
| 3DSIndicatorInPlanet                        |          1 | High | 3-D Secure authentication signalling                              |
| resetPassword / myProfile / accountSettings |          3 | Med  | Password reset, change, and strength validation                   |
| hotelAccount                                |          1 | Med  | Regular-guest / hotel account                                     |
| nineRooms                                   |          1 | Med  | 9-room boundary (largest E2E books 4 rooms)                       |
| companyNameSpecialCharacters                |          6 | Low  | Input validation across registration, booker/billing, settings    |
| countryDropdownFields                       |          2 | Low  | Country type-ahead on guest & payment details                     |
| webPushNotification                         |          3 | Low  | Allow / deny / cancel (E2E only dismisses the popup)              |
| hdpAnnouncement                             |          1 | Low  | Restaurant-closed banner                                          |
| eciLco *(PARTIAL)*                          |         17 | Med  | Cancel paths, low/no inventory, hide/remove ECI & LCO             |
| deOptIn *(PARTIAL)*                         |          9 | Low  | Residence × site × logged-in opt-in matrix                        |
| destinationLandingPage *(PARTIAL)*          |         17 | Low  | Content/filter surface: filters, FAQ, hero, travel guides, radius |

> Note on DLP: it is **not** a full gap. E2E 380779 covers the DLP end-to-end including map/grid
> view, with 90661 and 389731 adding lighter passes. Only the SEO/content and filter sections are
> functional-only.

**CCUI — 4 areas with no E2E coverage, plus 3 thin PARTIALs and 1 dead spec**

| Functional area                 | Func specs | Risk | What the E2E layer misses                                                              |
| ------------------------------- | ---------: | :--: | -------------------------------------------------------------------------------------- |
| amendA2C *(PARTIAL, 2 E2E)*     |          8 | High | Cancel, cancel-override, payment-page restrictions, update company ref/email, back-nav |
| eciLco *(PARTIAL, 1 E2E owner)* |         16 | High | Cancel-with-packages, multi-night, no-availability, hide/remove, 9-room back-button    |
| changeLogAgentId                |          3 | Med  | Audit trail of which agent amended / cancelled (compliance)                            |
| negotiatedRates *(PARTIAL)*     |          3 | Med  | Cancel-company-check flow, Corp ID field validation                                    |
| repeatBooking                   |          2 | Med  | Repeat booking for accessible/twin + book-for-someone-else                             |
| manageBooking (3-criteria)      |          1 | Low  | Search with three criteria combined                                                    |
| companyNameSpecialCharacters    |          1 | Low  | Booker-address special characters                                                      |
| MLOS *(no working coverage)*    |   0 / skip | High | Only spec (369556) is `describe.skip`; neither layer covers it                         |

**PIB — no zero-coverage areas; the gaps are thin PARTIALs and single-owner booking risks**

| Functional area                      | Func → E2E | Risk | What the E2E layer misses                                                                   |
| ------------------------------------ | :--------: | :--: | ------------------------------------------------------------------------------------------- |
| spendingAndReporting *(PARTIAL)*     |   21 → 6   | High | MI/emergency/out-of-policy reports, statements, transactions, memorable word                |
| cardManagement *(PARTIAL)*           |  27 → 15   | High | Cost centres & assignment, pagination, people picker, iframe behaviour, resend/replace card |
| auth *(PARTIAL)*                     |   12 → 5   | Med  | Cookie consent, EAD modal (UK+DE), security question, set-password page                     |
| editEmployee *(PARTIAL)*             |   5 → 7    | Med  | Address, alternate-phone, personal-details validation                                       |
| home *(PARTIAL)*                     |   6 → 5    | Low  | InnBusiness Pay widget, upcoming-bookings detail                                            |
| Booking: payment declines / 3DS fail |   0 / 0    | High | No failed-payment or failed-Worldline-3DS path anywhere                                     |

### 5.2 Gaps common to more than one channel

- **Payment declines / failed authentication** — *all three channels*. Every E2E uses a card that
  succeeds. PIB additionally has no failed-3DS path, and it owns the only Worldline path in the
  pack, so that gap is absolute.
- **Password / account management** — PI (reset, change, strength) and PIB (set-password, security
  question) are both functional-only.
- **companyNameSpecialCharacters input validation** — functional-only in both PI (6) and CCUI (1).
- **ECI/LCO edge paths** — PARTIAL in both PI and CCUI; the happy path is covered, the
  cancel/inventory/hide-remove paths are functional-only.

---

## 6. Estimated Coverage Gap if Only E2Es Are Kept

The migration strategy drops the functional layer by design — its Rule 1 (§1.1) migrates only the
E2E regressions and states that "if dropping the small tests leaves a gap somewhere, we'll address
that gap later as a separate task." This section estimates the size of that gap: how much *tested
behaviour* is lost if all **233 functional specs** are deleted and only the E2E layer remains (the
**201** in-scope E2Es, which the strategy consolidates losslessly to **165** — see §1.1).

**Method.** For each channel the functional specs are bucketed by the §5 classification, and a
coverage-loss factor is applied:

- **OK areas** — E2E already covers the behaviour → **~0% loss.**
- **GAP areas** — no E2E equivalent → **100% loss.**
- **PARTIAL areas** — E2E covers the core, functional adds edges → an estimated **~50–80%** of the
  functional specs test behaviour with no E2E equivalent (factor chosen per area from the source
  docs' descriptions: PI ~75%, CCUI ~70%, PIB ~54%).

These are deliberately approximate — the PARTIAL factors are judgement calls, not grep-verified.

### 6.1 Raw coverage loss

Each functional spec is bucketed **OK / PARTIAL / GAP** (per §5), then a loss factor is applied.

| Application | Functional |     OK | PARTIAL |    GAP | Est. lost† | % not in E2E‡ |
| ----------- | ---------: | -----: | ------: | -----: | ---------: | ------------: |
| PI          |         80 |     10 |      45 |     25 |        ~59 |          ~74% |
| CCUI        |         39 |      5 |      27 |      7 |        ~26 |          ~67% |
| PIB         |        114 |     43 |      71 |      0 |        ~38 |          ~33% |
| **Total**   |    **233** | **58** | **143** | **32** |   **~123** |      **~53%** |

† **Est. lost** = every GAP spec (100% unreplicated) + an estimated share of the PARTIAL specs
(PI ~75%, CCUI ~70%, PIB ~54%): PI 25 + ~34 = ~59 · CCUI 7 + ~19 = ~26 · PIB 0 + ~38 = ~38.
‡ **% not in E2E** = Est. lost ÷ Functional.

Read literally: dropping the functional layer leaves roughly **half of the scenarios the
functional specs exercise with no E2E equivalent** — heaviest in PI (~74%), lightest in PIB
(~33%, because PIB has no zero-coverage areas and thicker E2E overlap).

### 6.2 Risk-weighted coverage loss

Most of that raw ~53% is low-risk: input validation (special characters, country dropdowns), push
notifications, content/SEO surfaces, opt-in matrices and reporting variants. Filtering to the
genuinely **high-risk** uncovered behaviour from §5 — one gap per row so each stands on its own:

| Application       | High-risk uncovered behaviour    |  ~Specs |
| ----------------- | -------------------------------- | ------: |
| PI                | Payment declines                 |       3 |
| PI                | Group web form                   |       4 |
| PI                | Password / account management    |       3 |
| PI                | 3DS indicator in Planet          |       1 |
| PI                | Nine rooms                       |       1 |
| **PI subtotal**   |                                  | **~12** |
| CCUI              | amendA2C cancel / restrictions   |      ~6 |
| CCUI              | changeLogAgentId audit trail     |       3 |
| CCUI              | MLOS (skipped spec)              |       1 |
| CCUI              | Negotiated-rate cancel           |      ~2 |
| **CCUI subtotal** |                                  | **~12** |
| PIB               | Reports & statements             |     ~13 |
| PIB               | Card cost centres                |      ~5 |
| PIB               | Payment-decline / 3DS-fail path† |       — |
| **PIB subtotal**  |                                  | **~18** |
| **Total**         |                                  | **~42** |

† A gap in **both** the E2E and functional layers — PIB has no functional payment-decline or
failed-3DS spec either — so it is flagged as high-risk but carries no functional-spec count and is
not added to the subtotal.

Risk-weighted, the real exposure is roughly **40–45 specs (~17–19% of the functional layer)** —
the behaviour that would actually hurt if it regressed undetected.

### 6.3 Bottom line

- **Deleting all functionals and keeping only E2Es leaves an estimated ~53% of functional-tested
  scenarios unverified (raw), but only ~17–19% represents high-risk behaviour.** The other ~35% is
  low-risk edge/validation/content coverage the strategy consciously accepts.
- **By channel the picture is very uneven.** PIB is safest to thin (~33% raw loss, no zero-coverage
  areas). PI loses the most (~74% raw) and holds the highest-severity gaps (payment declines, group
  form, account management). CCUI sits between (~67% raw) with its own commercial-flow gaps
  (amendA2C, audit trail, MLOS).
- **The cheapest, highest-value net-new E2Es to close the worst gaps** are a shared
  **payment-decline / failed-3DS journey** (closes the single highest-risk gap in all three
  channels at once), plus a **CCUI changeLogAgentId** audit-trail spec and resolution of the
  **skipped MLOS** spec (369556). Everything else fits the strategy's stated position: accept the
  gap now, address it later as deliberate work.
