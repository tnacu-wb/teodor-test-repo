# Regression Pack Migration Strategy — WebdriverIO to Playwright

> Companion to `regression-pack-analysis.md` and `regression-pack-rationalisation.md`.
> Defines what migrates (§1.1 — e2e regressions only), the coverage rule (§1.2 — no loss),
> and how to port it to Playwright while shrinking the pack **without dropping any
> coverage**: overlap/subset dedup, deferring the shared booking backbone to PI
> (channel-delta), composite folding, and parameterising role/locale twins.

---

## 1. Scope and Target Outcome

### 1.1 What we migrate, and what we don't

The old WebdriverIO pack has **473 spec files**. They fall into two groups:

- **End-to-end regressions** — full user journeys (search → book → confirm → amend →
  cancel) that test many features together and check the results against Opera/OHIP.
- **Functional tests** — small specs that each test one narrow feature in isolation.

The two groups were maintained in parallel, so the same feature often appears in both a
functional test *and* one or more e2e regressions. This was done inconsistently — sometimes
a new feature was added to an existing regression, sometimes it only got its own small test.

Two rules decide what's in and what's out.

**Rule 1 — only e2e regressions.** We don't migrate the functional-only specs. The value
of our regression pack is in testing features *together*, in real user flows. That's what
catches regressions. If dropping the small tests leaves a gap somewhere, we'll address that
gap later as a separate task — it doesn't block this migration.

**Rule 2 — only tests that drive the UI.** Every migrated spec must act like a real user:
open a browser, navigate, click, fill forms. API calls are fine as **helpers**, but they
can't be the test itself.

What API calls are allowed for:

- **Reading data** to set up or drive a journey — hotel info, availability, rates, room
  types, content.
- **Setting up preconditions** — creating a booking via API so an amend test has something
  to work with, or (planned, see §4) setting availability, packages and rates on a hotel
  so the journey is predictable.
- **Checking the backend after a UI journey** — confirming that what the user saw and did
  actually landed correctly in Opera/OHIP.
- **Cleaning up** — cancelling bookings or resetting state after a run.

What's NOT allowed:

- A spec that *only* calls APIs and checks responses, with no browser interaction at all.
  That's a backend test and it belongs to the team that owns the API.

The key question: is there a UI journey being tested? If yes, any API assertions hanging
off that journey are fine — in fact they're the most valuable checks we have, and PI carries
the deepest set (§10). If there's no UI journey, it's out of scope.

**In scope — front-end e2e regressions**

| Suite                        | Location            | Specs   |
|------------------------------|---------------------|--------:|
| PI baselines                 | pi/baselineTests    |      40 |
| PI smokes                    | pi/smokeTests       |       7 |
| CCUI baselines               | ccui/baselineTests  |      26 |
| CCUI smokes                  | ccui/smokeTests     |       7 |
| PIB journeys (incl. smokes)  | ib/end2endTests     |     121 |
| **Total in scope**           |                     | **201** |

**Out of scope**

| Excluded             | Location                         | Specs   | Why               |
|----------------------|----------------------------------|--------:|-------------------|
| PI feature tests     | pi/featureTests                  |      80 | functional-only   |
| CCUI feature tests   | ccui/featureTests                |      39 | functional-only   |
| PIB feature tests    | ib/featureTests                  |     114 | functional-only   |
| BB specs             | bb/                              |      25 | functional-only   |
| Distribution routing | distr/end2endRoutingInstructions |      10 | backend-only      |
| PI harness scripts   | pi/*.spec.js                     |       4 | data setup        |
| **Total excluded**   |                                  | **272** |                   |

201 + 272 = 473, which matches the full reference tree.

Two boundary notes. The `distr` suite sounds like it should be in scope from its name —
ten specs covering routing instructions — but it has **no page objects at all**: it just
calls basket and reservation APIs directly and asserts the responses. No browser, no UI.
That makes it a backend test (Rule 2 excludes it). If we need that routing-instruction
coverage, it should live with the team that owns that API. The four `pi/*.spec.js` files
at the root (`createReservations`, `confirmReservationTest`, `cancelReservations`,
`examples`) have no Zephyr ID and no tags — they're just data-setup utilities, not tests.

### 1.2 How much coverage loss we accept — none

We accept **no loss of assertion coverage**. Every assertion the in-scope e2e pack makes
today must still have an owner after migration. There is no coverage budget to spend and no
"long tail" we are willing to drop: the target is 100% of the e2e regression assertions
carried forward.

That does **not** mean we keep every file. Most of the duplication in the old pack is
genuinely redundant — the same assertion made twice, not two different assertions — so we
can still shrink the pack substantially. The rule is only that every reduction has to be
**lossless**: the assertions of any file we remove must still run somewhere else. We reduce
count and runtime through these levers, and only these:

- **Overlap and subset dedup.** Where one spec's assertions are a strict subset of another's,
  or two specs assert exactly the same thing, we keep one. The dropped spec's assertions
  still run — in the spec that kept them.
- **Defer the shared backbone to PI (channel-delta).** PI, CCUI and PIB drive the same
  booking UI underneath, so the same Opera checks (hotel, dates, price, deposit, city tax,
  meals, …) repeat across all three. PI owns the full depth of those checks; CCUI and PIB
  keep only what is unique to their channel and trust PI for the rest. Nothing is lost — the
  backbone assertion still runs, once, in PI (§10).
- **Consolidation and folding.** Where journeys chain naturally (search → book → amend →
  cancel), we merge them into one composite that carries every assertion. Where a removed
  spec has a unique assertion, we fold that assertion into the spec that replaces it.
- **Parameterise role/locale twins.** Specs that differ only in who logs in or which
  language site loads become one parameterised spec run across both values — not one spec
  with the other deleted. Both variants still execute.
- **Runtime-opt.** API-seeding a booking prologue instead of clicking through it saves
  runtime, not assertions.

What we will **never** do:

- **Cut the last owner of any assertion.** If only one spec in the whole pack asserts a
  behaviour, it stays — there is no budget that can buy it.
- **Drop a rare combination just because it is rare.** 9-night stays, HUB + amend, FMTRPL +
  child breakfast and similar edge cases are not redundant — rarity is not overlap. If the
  combination is the sole owner of its assertions, it stays. It can only go if a diff proves
  another spec already makes the same checks.
- **Thin a spec nobody has read.** Every reduction is gated on a read-and-diff that proves
  the assertion has another owner (§9.4, §14.1). Deferring to PI, consolidating, or
  parameterising an unread spec is exactly the move this plan must not make.

### 1.3 Target numbers

| Phase | Scope              | Source | Target | How we reduce the count   | Depth savings      |
|-------|--------------------|-------:|-------:|---------------------------|--------------------|
| 0     | Shared API library |      — |      — | —                         | —                  |
| 1     | PI (reference)     |     47 |     37 | 3 composites, 5 cuts,     | —                  |
|       |                    |        |        | 1 smoke cut               |                    |
| 2     | CCUI               |     33 |     24 | 2 consolidations, 2 cuts, | channel-delta ×14  |
|       |                    |        |        | 3 smoke twins             |                    |
| 3a    | PIB booking        |     45 |     34 | 4 consolidations, 1 cut   | channel-delta ×12  |
| 3b    | PIB non-booking    |     76 |     70 | 3 folds, 3 cuts           | —                  |
|       | **Total**          |**201** |**165** |                           |                    |

> **"How we reduce the count"** = techniques that delete spec files without dropping a check:
> merging several into one (consolidation), removing genuinely redundant ones (overlap cuts),
> parameterising role/locale twins so both still run from one file.
> This drives the Source → Target arithmetic.
>
> **"Depth savings"** = making a kept file shorter and faster by removing assertions that
> PI already covers (channel-delta). This doesn't remove any files — it saves runtime and
> maintenance.

The Source numbers are verified against the actual files in the reference tree. The Target
column matches what §§5–8 assign file by file — every source spec is accounted for as
either merged, kept, or cut.

Two types of reductions work differently:

- **Consolidation / overlap cuts** — remove files from the plan, but only where the file's
  assertions provably run elsewhere (a merge target, or a spec that already makes the same
  checks). These are what change the number, and every one is lossless.
- **Channel-delta** — makes a kept file shorter by dropping checks that PI already does.
  Doesn't delete files, doesn't change the count. Saves runtime and maintenance cost, and
  loses no coverage because the check still runs in PI.

We split them in the table so the numbers stay honest: you can trace every removed file to
a specific merge or an overlap that another spec covers. Channel-delta never makes the count
go down.

165 is the count reached by the evidence-backed, **lossless** techniques enumerated in
§§5–8: for every file removed there, the assertions provably run somewhere else. There is
no lower target beyond it — we are not chasing ~140 or any other number. §14 lists further
reductions that *might* be possible (permutation clusters and long-tail overlaps), but each
is only a hypothesis until a read-and-diff proves the assertions have another owner. If the
proof holds, the count drops below 165 with no coverage lost; if it doesn't, 165 stands. We
keep §§5–8 and §14 separate on purpose so we never confuse "dedup backed by a diff" with
"dedup we still have to prove".

---

## 2. Design Decisions

These are the main choices we made before starting. Each one shapes how the migration
works across all phases.

**How we port each test — hybrid approach.**
Some old tests naturally chain together (search → book → amend → cancel on the same
booking). Where that happens, we merge them into one composite spec. Where a test stands
alone and covers a unique feature, we port it 1:1. This gives us the best of both: fewer
files where merging makes sense, but clear failure isolation where it doesn't.

**Cross-app dedup — PI is the reference channel.**
PI, CCUI and PIB all share the same booking UI underneath. That means many of the same
Opera checks (hotel, dates, price, deposit, etc.) are repeated across all three apps. We
don't repeat that work: PI owns the full depth of those checks. CCUI and PIB specs only
keep what's unique to their channel (agent login, Eckoh payment, business rules, etc.) and
trust PI for the rest.

**Order — PI and CCUI first, then PIB.**
Channel priority. PI is the main customer-facing website, CCUI is the Contact Centre UI
(used by receptionists, telephone agents, and travel agencies to manage bookings). PIB (InnBusiness) comes
after because it depends on patterns established by PI and its admin page objects are
entirely new work.

**Shared API library — deliver it first (Phase 0).**
Every spec needs API helpers for setting up data, checking Opera, and cleaning up. We build
that library before writing any new specs. BL001 already uses it; the rest will too.

**Coverage target — no loss (100% carried forward).**
We keep every assertion the in-scope e2e pack makes today (see §1.2). The pack still
shrinks, but only through lossless levers — overlap dedup, deferring the shared backbone to
PI, consolidation, folding, parameterisation, runtime-opt — never by dropping a check. If
removing a spec would leave an assertion with zero owners, it stays.

**Test layer — e2e regressions only.**
We only migrate end-to-end user journeys. Functional-only specs and backend-only tests
are out of scope (see §1.1).

---

## 3. ID and Naming Scheme

### Kiro spec IDs

Format: `REG-<APP>-<N>`, where `<APP>` is one of `PI`, `CCUI`, `PIB` and `<N>` is a
sequential number per app starting from 1.

Examples: `REG-PI-1`, `REG-PI-12`, `REG-CCUI-3`, `REG-PIB-7`.

Each app owns its own counter, so numbers never collide across apps. PI and CCUI are
numbered sequentially and the order carries no priority. **PIB numbers are grouped into
blocks by phase and journey** so a spec's number signals where it belongs: booking specs
occupy `REG-PIB-1`–`REG-PIB-34` (§7) and non-booking specs `REG-PIB-40`–`REG-PIB-143`, each
journey in its own decade-aligned block (§8, e.g. auth in the 40s, company in the 50s). Journeys with
more than ten specs spill into the following decade, but the blocks are spaced so they never collide,
and the booking and non-booking ranges do not overlap.

The existing BL001 (the PI flagship already migrated) becomes **REG-PI-1** under this
scheme. New spec folders follow the pattern `.kiro/specs/QA/RegressionMigrations/REG-PI-2/`,
etc.

### Other paths

- **Playwright files**: `qa/tests/regressions/{pi,ccui,pib}/baseline-e2e-<attrs>.spec.ts`.
- **Page objects**: `qa/src/pages/{pi,ccui,pib}/`, barrel-exported from `index.ts`.
- **Test data**: `qa/src/test-data/{pi,ccui,pib}/`.

Note the app folder is `pib`, matching the directories already present in `qa/src/`. The
old framework called it `ib`; that name is retained only when pointing at reference specs.

> **Legacy reference: BL001.** The PI flagship was migrated first as a proof of concept and
> is already **Done**. It is counted as REG-PI-1 in the Phase 1 totals, but it keeps its
> original `BL001` ID and existing folder `.kiro/specs/QA/BaselineMigrations/BL001/` rather
> than being re-created under the new path. So REG-PI-1 needs no further migration work — it
> stands as the reference pattern the other specs follow.

### Location shorthands

- `PIbase` = `qa/reference/test/specs/pi/baselineTests/`
- `PIsmoke` = `qa/reference/test/specs/pi/smokeTests/`
- `CCUIbase` = `qa/reference/test/specs/ccui/baselineTests/`
- `CCUIsmoke` = `qa/reference/test/specs/ccui/smokeTests/`
- `IB/<journey>` = `qa/reference/test/specs/ib/end2endTests/<journey>/`

### Migration-approach vocabulary

| Label           | Meaning                                              |
|-----------------|------------------------------------------------------|
| **1:1**         | Port to functional parity                            |
| **N:1**         | Consolidate multiple old TCs into one new spec       |
| **Enhance**     | Port + fold a cut spec's unique assertion            |
| **Cut**         | Do not migrate (redundant)                           |
| **Runtime-opt** | Port but API-seed the booking prologue instead of UI |
| **Channel-delta** | Port channel-specific assertions only; drop backbone covered by PI |
| **Done**        | Already migrated (BL001)                             |

---

## 4. Phase 0 — Shared GraphQL API Client and Validation Helpers

Delivered as a separate spec (`.kiro/specs/QA/SharedLibraries/GraphQLApiClient/`).
BL001 already consumes it; Phase 0 completes it for the whole pack.

| Deliverable      | Content                                     | Consumed by |
|------------------|---------------------------------------------|-------------|
| GraphQL core     | GraphQLClient, BasketAPI, ContentAPI,       | All phases  |
|                  | BookingConfirmationHelpers,                 |             |
|                  | AvailabilityHelpers                         |             |
| OHIP / Opera     | CancellationApi; Opera-side reservation and | All phases  |
|                  | folio reads for UI cross-validation         |             |
| OHIP seeding     | **Planned** — set availability, packages,   | All phases  |
|                  | rates per hotel / room type                 |             |
| AEM              | DLP content dictionary fetch + validation   | Phase 1     |
| CCUI additions   | Eckoh tokenisation, ID&V/DPA helpers,       | Phase 2     |
|                  | agent/manager auth                          |             |
| PIB additions    | InnBusiness auth/company/employee/pay-app/  | Phase 3     |
|                  | spending queries, Planet payment status,    |             |
|                  | cross-channel project switch                |             |

Current shape in `qa/src/api/`: `graphql/` (client, basket, content, availability,
confirmation helpers), `ohip/` (cancellation), `aem/` (DLP dictionary + validation), all
barrel-exported from `index.ts`.

**OHIP seeding is the one net-new capability worth calling out.** Today OHIP is read-mostly
plus cancellation. The intent is to extend it into a precondition-setting tool: force
availability to a known state, pin a specific package or rate onto a hotel and room type,
and generally make a journey deterministic before the UI is touched. That removes the main
source of flake in the old pack, where specs depended on whatever inventory happened to
exist. It is permitted under Rule 2 (§1.1) because it sets up a UI journey rather than
being the subject of one. Specs that would benefit most: the windowless-room set (REG-PI-2,
REG-CCUI-20, REG-PIB-10, REG-PIB-17, REG-PIB-18), the package/allowance journeys, and anything asserting rate order.

**Exit criteria:** BL001 green using only the library; no inline API calls in any spec.

---

## 5. Phase 1 — PI (Reference Channel)

PI establishes canonical booking + Opera coverage. Full `BookingConfirmationHelpers`
depth is maintained here; secondary channels defer to it.

### 5.1 Composites and Consolidations

**BL001** — `guest-uk-flex-poa-piba-amendment`

- Composed of: 380779 + 90661 + 389731 @PIbase (3 files)
- Map: 3:1 | Approach: **Done**
- Coverage: ~100% of DLP + change-room cluster
- Notes: Flagship pattern. Its TripAdvisor coverage is on the **DLP**
  (`validateTripAdvisorSection`, review-link-to-HDP). It does **not** cover 366971's HDP
  award popup or SRP-sort-vs-API, so 366971 is not folded here — it stands as REG-PI-30.

**REG-PI-2** — `pi-windowless-hub`

- Composed of: 530589 + 531538 @PIbase (2 files)
- Map: 2:1 | Approach: **N:1** param `[PI_FLEX, PI_SEMI_FLEX]`
- Coverage: ~98%
- Notes: Rationalisation C2. **Blocked on the 531538 ID collision** — see §9.2.

**REG-PI-3** — `pi-advance-refund-uk`

- Composed of: 460870/464491 + 76425 @PIbase (2 files — 460870 and 464491 are the same
  spec file, which declares both IDs)
- Map: 2:1 | Approach: **Enhance** (add FMTRPL+childMeal room)
- Coverage: 100%
- Notes: Rationalisation C4

### 5.2 Sole-Owner Baselines — 1:1 (REG-PI-4–REG-PI-31)

Each owns a distinct feature. Amend-focused specs use **Runtime-opt** (API-seed prologue).

| BL-ID | Old TC        | Feature Solely Owned                              | Approach    |
|-------|---------------|---------------------------------------------------|-------------|
| REG-PI-4 | 373793       | Employee website + EMPLOYEE rate                  | 1:1         |
| REG-PI-5 | 76468        | Upgrade-to-Flex + back-nav + hotel change         | 1:1         |
| REG-PI-6 | 385014       | Reg-card + someone-else + diff billing            | 1:1         |
| REG-PI-7 | 385301       | submitPrecheckinGerman (skips SCA)                | 1:1         |
| REG-PI-8 | 385302/435918| SRP map-view + reviewRegCard (one file, two IDs)  | 1:1         |
| REG-PI-9 | 389728       | Multi-room reg-card Room 2 link                   | 1:1         |
| REG-PI-10 | 76469        | SRP filter + sort-by-distance + MEAL_DEAL         | 1:1         |
| REG-PI-11 | 76491        | SRP sort-by-price + multi-room lead-guest         | 1:1         |
| REG-PI-12 | 435201       | WiFi ancillary (graphqlGetAncillariesWiFi)        | 1:1         |
| REG-PI-13 | 376400       | Marketing opt-in + CDH + HUB BIGWIN              | 1:1         |
| REG-PI-14 | 374328       | Marketing opt-out + validateBalance               | 1:1         |
| REG-PI-15 | 366969       | AMEX card + twin bed-type + UK postcode           | 1:1         |
| REG-PI-16 | 76495        | Saved card (CVV-only) + amend room-type           | 1:1         |
| REG-PI-17 | 380774       | BAC payment-auth + validateRoomClassOrder         | 1:1         |
| REG-PI-18 | 222161       | Non-zero donation + continental from profile      | 1:1         |
| REG-PI-19 | 382591       | ECI/LCO packages + Ireland coverage               | 1:1         |
| REG-PI-20 | 76467        | Richest DE guest (20 asserts) + RWCC + cancel     | 1:1         |
| REG-PI-21 | 380786       | DE city-tax + amend-remove-adult + cancel         | 1:1         |
| REG-PI-22 | 380783       | DE city-tax invariance (add adult)                | 1:1         |
| REG-PI-23 | 266203       | Amend refund maths + early-checkout edge          | 1:1 + R-opt |
| REG-PI-24 | 266200       | validateAmendCancelPoliciesText                   | 1:1 + R-opt |
| REG-PI-25 | 195251       | PIBA amend + validateBasketIsConfirmed            | 1:1 + R-opt |
| REG-PI-26 | 418503       | PAY_NOW + PIBA + two amend cycles                 | 1:1 + R-opt |
| REG-PI-27 | 374857       | DE 2n multi-rooms no-meal PN remove-room          | 1:1 + R-opt |
| REG-PI-28 | 376362       | UK Flex PIBA POA extend-checkout + kid            | 1:1 + R-opt |
| REG-PI-29 | 380777       | UK Flex TWIN BAC POA increase-nights              | 1:1 + R-opt |
| REG-PI-30 | 366971       | TripAdvisor on HDP (award popup) + SRP sort vs API | 1:1         |
| REG-PI-31 | 365944       | DE someone-else + different billing address + POA | 1:1         |

> **R-opt** = Runtime-opt (API-seed prologue)

REG-PI-31 (365944) was unassigned in earlier drafts. It is cited in the rationalisation cut
list as a co-owner of booking-for-someone-else coverage — part of why 389724 is cut — so it
cannot itself be dropped without re-examining that cut. It is the closest overlap in the PI
set to REG-PI-6 (385014) and is therefore a **lossless-dedup candidate** (§14): it merges
into REG-PI-6 only if a read-and-diff proves their someone-else + different-billing
assertions fully overlap. Absent that proof it ports 1:1 and stays.

### 5.3 Cuts (Do Not Migrate)

Each cut names the kept spec(s) that own its assertions, so the removal is traceable (§9.4).

| Old TC         | Why it is redundant, and where its assertions still run |
|----------------|---------------------------------------------------------|
| 389724         | DE someone-else + RWCC booking; 11 core Opera asserts. Someone-else owned by REG-PI-6 (385014) + REG-PI-31 (365944); RWCC by REG-PI-20 (76467) + REG-PI-9 (389728). |
| 389727         | Logged-in twin of 389724 with an identical assertion set; same owners (REG-PI-6/-31 for someone-else, REG-PI-20/-9 for RWCC). |
| 376973         | No sole-owned assertion: change-arrival amend owned by REG-PI-23 (266203); free-child-breakfast-at-booking by REG-PI-11 (76491); BAC card by REG-PI-10/-17/-28/-29 + BL001. |
| 223456         | **Not** a strict subset of 266200 — 223456 is HUB / FLEX / policy-D1A and changes the arrival date; 266200 is non-hub / SEMI-FLEX / D1 and does not. Its asserts are owned across REG-PI-24 (266200: shorten + add) + REG-PI-23 (266203: change-arrival), and the HUB+amend context by REG-PI-2 + REG-PI-30. |
| 76466          | DE POA / Mastercard / meals / cancel / city-tax / DAX-policy all owned by REG-PI-5 (76468) + REG-PI-20 (76467); the 9-night boundary by REG-PI-10 (76469). No sole-owned assertion. |
| 468894 (smoke) | Book + cancel smoke; every assertion (incl. the cancel-confirmation message) is a subset of the full-funnel baselines that cancel (e.g. REG-PI-3, REG-PI-20). |

### 5.4 PI Smokes — Keep 6 (1:1)

| Old TC | Name                                            |
|--------|-------------------------------------------------|
| 468889 | Guest UK 1n 1r POA PIBA — **Done**              |
| 468890 | Guest DE 1n 1r PN Visa                          |
| 468891 | Logged-in UK 1n 1r POA PIBA                     |
| 468892 | Logged-in DE 1n 1r PN Visa                      |
| 468893 | Amend add-adult (owns updated-booking msg)      |
| 115179 | UK 3n meals donations FMQUAD                    |

468889 already ships as `qa/tests/regressions/pi/tc-468889-guest-piba-poa.spec.ts`. Two
specs are therefore migrated, not one: BL001 and this smoke.

**Phase 1 net: 47 → 37** (31 baselines BL001 + REG-PI-2 to REG-PI-31, 6 smokes).

Reconciliation of the 40 source baselines: 5 cut (§5.3), 7 consumed by the three
consolidations in §5.1 (3 + 2 + 2), and 28 ported 1:1 as REG-PI-4–REG-PI-31.

---

## 6. Phase 2 — CCUI (Cross-App Thinning Applied)

PI owns the channel-neutral Opera backbone. Each CCUI spec keeps **channel-delta
assertions only**: agent/manager auth, Eckoh tokenisation, ID&V/DPA override, A2C,
contract rate, business booking rules. Amend prologues are API-seeded.

### 6.1 Consolidations

| BL-ID | New Spec Name         | Composed Of                  | Map | Approach |
|-------|-----------------------|------------------------------|-----|----------|
| REG-CCUI-1 | ccui-amend-de         | 381188+89+90+91 @CCUIbase    | 4:1 | **N:1**  |
| REG-CCUI-2 | ccui-contract-rate-uk | 428938+428939 @CCUIbase      | 2:1 | **N:1**  |

Coverage: ~96% (REG-CCUI-1), ~95% (REG-CCUI-2).

### 6.2 Sole-Owner Baselines — Channel-Delta (REG-CCUI-3–REG-CCUI-21)

| BL-ID | Old TC        | Channel-Specific Feature                   | Approach      |
|-------|---------------|--------------------------------------------|---------------|
| REG-CCUI-3 | 384400       | A2C: company search, guarantee code CO     | 1:1 + R-opt   |
| REG-CCUI-4 | 384401       | Change-payment-method, Eckoh path          | 1:1 + R-opt   |
| REG-CCUI-5 | 379921       | Agent DE 2n Flex PN non-BAC + amend        | Channel-delta |
| REG-CCUI-6 | 380723       | Agent UK 2-room amend-room-type + child    | Channel-delta |
| REG-CCUI-7 | 379925       | Agent UK Single Flex POA BAC add-adult     | Channel-delta |
| REG-CCUI-8 | 380724       | Accessible-room + cancelled-booking table  | Channel-delta |
| REG-CCUI-9 | 369930       | Agent DE 2-room kids Advanced PN amend     | Channel-delta |
| REG-CCUI-10 | 379940       | Agent UK TWIN Flex POA BAC increase-nights | Channel-delta |
| REG-CCUI-11 | 368688       | Manager DE RWCC extend+remove+meals        | Ch-delta+R-opt|
| REG-CCUI-12 | 369933/408182| Manager DE A2C 4-room 7-adult              | 1:1 — unread  |
| REG-CCUI-13 | 369977       | Manager Hub 9n 4-room remove-room          | Ch-delta+R-opt|
| REG-CCUI-14 | 461417/464541| Manager UK Advance PN refund path          | 1:1 — unread  |
| REG-CCUI-15 | 369962       | Manager DE Family Accessible contract A2C  | Channel-delta |
| REG-CCUI-16 | 369964       | Manager UK TA no-award + free-child-bkfst  | Channel-delta |
| REG-CCUI-17 | 369950       | Manager DE contract child-bkfst non-guar   | Channel-delta |
| REG-CCUI-18 | 380725       | Manager UK Semi-Flex remove-child-meal     | Channel-delta |
| REG-CCUI-19 | 409154       | Accompanying guest details multi-room      | 1:1 — unread  |
| REG-CCUI-20 | 531538       | Agent Hub windowless continue-to-payment   | Channel-delta |
| REG-CCUI-21 | 115170       | CCUI SRP map view + SRP-vs-HDP price + type-of-caller | Channel-delta |

REG-CCUI-20 is **blocked on the 531538 ID collision** — see §9.2. REG-CCUI-21 is 115170 promoted from
the smoke pack: it is tagged `#sanity#` but solely owns the CCUI SRP map view, the
SRP-vs-HDP price comparison and the type-of-caller / accessible-customer flow for DE
non-guaranteed, which is baseline-weight coverage.

**"1:1 — unread" on REG-CCUI-12, REG-CCUI-14 and REG-CCUI-19** is deliberate. The rationalisation doc marks
all three as inferred rather than read, so their assertion sets are unknown. Channel-delta
drops backbone assertions, which cannot be done safely against a spec nobody has diffed
(§14.1). They port at full depth until read; promote them to channel-delta only after the
diff. This costs no specs — channel-delta never changed the count — only runtime.

### 6.3 Cuts

| Old TC        | Reason                                            |
|---------------|---------------------------------------------------|
| 369555        | Thinnest spec; SRP steps dead (DNRQ-66591)        |
| 369556 (MLOS) | `describe.skip` — fix MLOS or retire; don't port |

### 6.4 CCUI Smokes — Keep 3 + 1 Promoted

| Action                    | Keep   | Collapsed from              |
|---------------------------|--------|-----------------------------|
| Agent/Manager book        | 470152 | 470152+470151 (role twin)   |
| Agent/Manager amend       | 474131 | 474131+474130 (role twin)   |
| Agent/Manager cancel      | 474133 | 474133+474132 (role twin)   |
| Map-view + non-guaranteed | 115170 | promoted to baseline REG-CCUI-21  |

Seven smoke files reduce to three smokes plus one promotion: three role twins collapse
(470151/470152, 474130/474131, 474132/474133) and 115170 moves to the baseline set.

This is lossless under §1.2 — subset dedup, not twin deletion. The agent-vs-manager depth
these smokes re-check is already owned by the §6.2 baselines (which carry both agent and
manager specs), so the kept smoke is a shallow sanity pass and the dropped twin asserts
nothing a baseline does not. If a read ever shows a smoke twin owning a unique assertion,
parameterise it across both roles rather than dropping one.

**Phase 2 net: 33 → 24** (20 baselines REG-CCUI-1–REG-CCUI-20, REG-CCUI-21 promoted from smoke, 3 smokes).

Reconciliation of the 26 source baselines: 2 cut (§6.3), 6 consumed by the two
consolidations in §6.1 (4 + 2), and 18 ported as REG-CCUI-3–REG-CCUI-20.

---

## 7. Phase 3a — PIB Booking (Cross-App Thinning Applied)

Same approach as CCUI: channel-delta only for the booking backbone; PI holds the
canonical Opera depth. Keep cross-channel (IB→CCUI) in full since it tests the
project-switch path.

### 7.1 Consolidations

| BL-ID    | New Spec Name              | Composed Of                     | Approach        |
|----------|----------------------------|---------------------------------|-----------------|
| REG-PIB-1    | pib-mgr-de-amend           | 485261+485262+493806 @IB/bkReb  | N:1 (3:1)       |
| REG-PIB-2    | pib-poa-rebranding-header  | 450422/424+450423/425 @IB/bkReb | N:1 param[UK,DE]|
| REG-PIB-3     | pib-smoke-stored-card      | both 197342 @IB/smokeTests      | N:1 (2:1)       |
| REG-PIB-4–REG-PIB-6 | pib-smoke-book/-amend/-cancel | 9 role-op smokes @IB/smokeTests | N:1 (9:3) |

The payApp share-revocation fold is a 2→1 consolidation like the ones above, but its source
specs live in `@IB/payAppJourney`, so it is numbered with its journey in Phase 3b as
**REG-PIB-82** (§8), not here — keeping source and output in the same phase.

The 9:3 smoke collapse (REG-PIB-4–REG-PIB-6) is lossless under §1.2: the three kept specs
parameterise the role-operation smokes across their roles, so every role × operation still
runs — nine files fold into three, six are not dropped. Any role-op a diff shows is already
owned at depth by a §7.2–§7.3 booking baseline can instead be removed as subset dedup.

### 7.2 Sole-Owner Booking Baselines — Channel-Delta (REG-PIB-7–REG-PIB-18)

| BL-ID | Old TC | Channel-Specific Feature                       |
|-------|--------|------------------------------------------------|
| REG-PIB-7 | 435202 | WiFi extras + remove-night amend               |
| REG-PIB-8 | 453028 | Planet payment/refund + SRP map + Meal Deal    |
| REG-PIB-9 | 450465 | Cross-channel IB→CCUI amend (project switch)   |
| REG-PIB-10 | 516551 | TripAdvisor HDP + windowless DBLNWD + privacy  |
| REG-PIB-11 | 369963 | TA HDP + PI_ADVANCE + child-bkfst + remove     |
| REG-PIB-12 | 369609 | 4-room accessible + preselected-meal + print   |
| REG-PIB-13 | 369934 | LOWDBL + centrally-stored card + extend-night  |
| REG-PIB-14 | 369947 | 14-night + personal-stored card + SRP list     |
| REG-PIB-15 | 381172 | DE city-tax invariance + dinner allowance       |
| REG-PIB-16 | 418505 | POA + two sequential add-room amends           |
| REG-PIB-17 | 516552 | Windowless BIGNWD + Semi-Flex + city-tax       |
| REG-PIB-18 | 519597 | Windowless ACCNWD + Standard + change-arrival  |

### 7.3 Unassigned Booking Baselines — Primary Lossless-Dedup Pool (REG-PIB-19–REG-PIB-34)

Sixteen `bookingRebranding` specs are not covered by §7.1 or §7.2. Earlier drafts recorded
this as "~8 TCs" under a single REG-PIB-19, which both understated the count and broke the
one-ID-per-spec rule. They are enumerated here so nothing is silently dropped:

| BL-ID | Old TC | Journey                                                    |
|-------|--------|------------------------------------------------------------|
| REG-PIB-19 | 343521 | Manager CNP POA amend change-guest-details                 |
| REG-PIB-20 | 369552 | Manager DE 2n RWC                                          |
| REG-PIB-21 | 369553 | Manager UK 1n 1r no-preselected-meals                      |
| REG-PIB-22 | 369610 | Manager DE 3n 1r 2ad BusinessFlex POA                      |
| REG-PIB-23 | 369927 | Manager UK 5n twin meal PN                                 |
| REG-PIB-24 | 369929 | Self-booker UK SemiFlex personal-stored-card BAC           |
| REG-PIB-25 | 369931 | Booker Hub SemiFlex meals BAC add-night                    |
| REG-PIB-26 | 369932 | Booker Hub BusinessFlex personal-stored non-BAC card       |
| REG-PIB-27 | 369935 | Booker UK 2n BFlex meals BAC change-room-type              |
| REG-PIB-28 | 369936 | Booker UK BFlex Premier Plus                               |
| REG-PIB-29 | 379896 | Booker UK 2n double 2ad BFlex BAC POA                      |
| REG-PIB-30 | 379917 | Booker UK 2n BFlex new non-BAC card POA amend-add-adult     |
| REG-PIB-31 | 380707 | Manager UK 1n POA BAC change-room-type                     |
| REG-PIB-32 | 381183 | Self-booker DE BFlex POA remove-adult                      |
| REG-PIB-33 | 461367 | Booker 2r multi-night meal BusiFlex POA allowances amend    |
| REG-PIB-34 | 493803 | Self-booker DE RWCC amend add-child-adult + child-meal      |

This is the richest consolidation pool in the pack: the cluster is almost entirely
role (booker / self-booker / travel-manager) × rate (BFlex / SemiFlex / Standard) ×
payment (POA / PN / BAC / non-BAC / stored-card) permutation on the same booking backbone.
Several obvious pairings stand out — REG-PIB-27/REG-PIB-31 (change-room-type), REG-PIB-25/REG-PIB-26 (Hub 1n 1r
2ad), REG-PIB-30/REG-PIB-32 (add/remove adult), REG-PIB-20/REG-PIB-34 (DE RWC/RWCC).

**They are listed as 1:1 and counted as 16 until each is read.** The rationalisation doc's
standing rule applies: treat an unread spec as a sole owner until a diff proves otherwise.
Collapsing them — by parameterising the role × rate × payment permutations onto one backbone
spec — is the first and largest lossless reduction available in §14, allowed only where a
read-and-diff proves the permutations share all their assertions. A permutation that turns
out to own a unique assertion stays.

This pool closes the booking range at REG-PIB-34. Phase 3b non-booking starts at REG-PIB-40
(§8), so booking (≤34) and non-booking (≥40) never collide — the earlier REG-PIB-30s overlap
with `authJourney` has been resolved by the block scheme in §3.

### 7.4 Cuts

| Old TC                | Reason                                         |
|-----------------------|------------------------------------------------|
| 450464                | Thin amend-add-adult; subset of full amends    |

Only 450464 is cut in Phase 3a. The home twin cut (one of 446238/446140, both ⊂ 446237) is a
`homeJourney` spec and belongs to Phase 3b — it is handled in §8, not counted here.

485261 and 485262 are **not** listed as cuts. They are absorbed into REG-PIB-1, and counting
them in both places double-counted the saving.

**Phase 3a net: 45 → 34.**

Reconciliation of the 45 source specs. bookingRebranding (34): 3 consumed by REG-PIB-1, 2 by
REG-PIB-2, 12 ported as REG-PIB-7–REG-PIB-18, 16 ported as REG-PIB-19–REG-PIB-34, 1 cut (450464) — output 30.
Smokes (11): 2 consumed by REG-PIB-3, 9 by REG-PIB-4–REG-PIB-6 — output 4.

---

## 8. Phase 3b — PIB Non-Booking (1:1, Full Depth)

No cross-app lever applies. These are admin/account flows with no PI equivalent.
Biggest net-new POM effort — new page objects for the InnBusiness shell (Tailwind/shadcn).

| Journey (`@IB/…`)           |  Src | Fold | Cut |  Keep | BL-ID Range              |
|-----------------------------|-----:|-----:|----:|------:|--------------------------|
| authJourney                 |    5 |    — |   — |     5 | REG-PIB-40–REG-PIB-44    |
| companyManagementJourney    |    9 |    — |   — |     9 | REG-PIB-50–REG-PIB-58    |
| userManagementJourney       |    7 |    — |   — |     7 | REG-PIB-60–REG-PIB-66    |
| payAppJourney               |   14 |    1 |   — |    13 | REG-PIB-70–REG-PIB-82    |
| spendingAndReportingJourney |    6 |    1 |   — |     5 | REG-PIB-90–REG-PIB-94    |
| cardManagementJourney       |   15 |    — |   2 |    13 | REG-PIB-100–REG-PIB-112  |
| profileManagementJourney    |   15 |    1 |   — |    14 | REG-PIB-120–REG-PIB-133  |
| homeJourney                 |    5 |    — |   1 |     4 | REG-PIB-140–REG-PIB-143  |
| **Total**                   |**76**| **3**|**3**|**70** |                          |

Six source specs drop out: **3 folds** (payApp, spending, profile — a parameterise/merge that
keeps every assertion) and **3 cuts** (card ×2, home ×1 — each a proven subset of a kept spec,
§14.1). An earlier draft lumped all six under "Fold", a later one swung to 2 folds + 4 cuts; the
honest split is **3 folds + 3 cuts** (the profile room-preferences pair is a role twin, so it
parameterises rather than being cut).

#### Per-spec mapping (source TC → BL-ID)

Every source spec is listed. Ordered by source TC within each journey. `Appr.` is the
migration approach from §3 (all **1:1** except the two folds and four cuts).

**authJourney** — 5 → 5 (all 1:1)

| BL-ID     | Src TC | Feature                                                        | Appr. |
|-----------|--------|----------------------------------------------------------------|-------|
| REG-PIB-40 | 467769 | Sign-up new company (DE): marketing opt-in, confirm email, create account | 1:1 |
| REG-PIB-41 | 467845 | Sign-up booker, existing company (UK): TM approves, confirm, autologin    | 1:1 |
| REG-PIB-42 | 467846 | Login + forgotten password → resend → reset → re-login (DE)               | 1:1 |
| REG-PIB-43 | 467953 | Register finance user (UK + FU)                                           | 1:1 |
| REG-PIB-44 | 469744 | Link existing account via WL login → link → InnBusiness Pay               | 1:1 |

**companyManagementJourney** — 9 → 9 (all 1:1)

| BL-ID     | Src TC | Feature                                                        | Appr. |
|-----------|--------|----------------------------------------------------------------|-------|
| REG-PIB-50 | 464275 | Edit company details (valid + invalid values)                  | 1:1 |
| REG-PIB-51 | 464276 | Edit main contact (invalid search, then valid update)          | 1:1 |
| REG-PIB-52 | 464277 | Review changes (company details + main contact)                | 1:1 |
| REG-PIB-53 | 464278 | No access for Booker / Self-Booker / Guest                     | 1:1 |
| REG-PIB-54 | 464279 | Allowances persist for non-tethered TM                         | 1:1 |
| REG-PIB-55 | 464280 | Booking alerts: same-day, London/Berlin hotels, weekly frequency | 1:1 |
| REG-PIB-56 | 464281 | Booking alerts: weekend arrival, recipients, employees         | 1:1 |
| REG-PIB-57 | 464283 | Custom question: add, check network request, delete            | 1:1 |
| REG-PIB-58 | 464286 | Edit business-account question + save                          | 1:1 |

**userManagementJourney** — 7 → 7 (all 1:1)

| BL-ID     | Src TC | Feature                                                        | Appr. |
|-----------|--------|----------------------------------------------------------------|-------|
| REG-PIB-60 | 435229 | Add Self-Booker → activate → Inactive → login blocked → purge  | 1:1 |
| REG-PIB-61 | 435230 | Add Guest → activate → guest cannot access ME                  | 1:1 |
| REG-PIB-62 | 435231 | Add Booker → escalate to TM → verify ME access                 | 1:1 |
| REG-PIB-63 | 435232 | Add TM → activate → edit card = None                           | 1:1 |
| REG-PIB-64 | 435233 | Add TM → activate → edit company address (valid postcode)      | 1:1 |
| REG-PIB-65 | 435235 | Invite employee to add themselves → verify status              | 1:1 |
| REG-PIB-66 | 436060 | Add Booker → resend activation → deactivate + purge            | 1:1 |

**payAppJourney** — 14 → 13 (1 fold)

| BL-ID     | Src TC        | Feature                                                 | Appr. |
|-----------|---------------|---------------------------------------------------------|-------|
| REG-PIB-70 | 468986        | Charity business type: DD iframe, submit, status        | 1:1 |
| REG-PIB-71 | 469333        | Gov-funded school: edit correspondence address, save & resume | 1:1 |
| REG-PIB-72 | 469334        | Limited company: registration-number validation, share  | 1:1 |
| REG-PIB-73 | 469335        | Add "Me" card on a booker-shared application (UK)        | 1:1 |
| REG-PIB-74 | 469337        | Add card for existing employee on booker-shared app (UK) | 1:1 |
| REG-PIB-75 | 469342        | Other business type: share with 5 employees (picker disabled) | 1:1 |
| REG-PIB-76 | 469362        | Entry points: TM sees all, booker sees none (UK)         | 1:1 |
| REG-PIB-77 | 469381        | Sole trader: DD by post, summary page                    | 1:1 |
| REG-PIB-78 | 470210        | Public company: share, booker resumes application        | 1:1 |
| REG-PIB-79 | 470211        | Card-permission matrix: only TM + booker add cards       | 1:1 |
| REG-PIB-80 | 470340        | IB banner hidden when application shared (UK)            | 1:1 |
| REG-PIB-81 | 470630        | "Register now" points to register IBPay account          | 1:1 |
| REG-PIB-82 | 469343+471038 | Pay-app share revocation, param `[delete, unshare]`      | Fold |

**spendingAndReportingJourney** — 6 → 5 (1 fold)

| BL-ID     | Src TC | Feature                                                        | Appr. |
|-----------|--------|----------------------------------------------------------------|-------|
| REG-PIB-90 | 464505 | TM AC+CH (EN): spend-over-time + spent-this-month + report redirects | 1:1 |
| REG-PIB-91 | 464522 | TM+CH (DE): spend-over-time + summary + manage-account redirects | 1:1 |
| REG-PIB-92 | 464571 | TM tether FU: spending summary, no upcoming, memorable-word page | 1:1 |
| REG-PIB-93 | 464714 | TM AC+CH (EN): booking → MI / emergency / out-of-policy reports | 1:1 |
| REG-PIB-94 | 465452 | TM AC+CH IBPay (EN): download report + report-card redirects   | 1:1 |
| —          | 464510 | Booker manage-account (DE); only booker-tab-hidden unique → **fold into REG-PIB-91** | Fold |

**cardManagementJourney** — 15 → 13 (2 cuts)

| BL-ID      | Src TC | Feature                                                       | Appr. |
|------------|--------|---------------------------------------------------------------|-------|
| REG-PIB-100 | 464282 | Non-tethered TM: Visa → Amex → delete (EN) *(CM10)*           | 1:1 |
| REG-PIB-101 | 464285 | TM AH: PIBA card add / edit / delete (DE)                    | 1:1 |
| REG-PIB-102 | 464288 | TM: centrally-stored Mastercard add + delete                 | 1:1 |
| REG-PIB-103 | 464294 | "No cards" empty-state string (DE)                           | 1:1 |
| REG-PIB-104 | 464295 | No access for Booker / Self-Booker / Guest                   | 1:1 |
| REG-PIB-105 | 464296 | TM CH: regular card add / edit / delete (DE)                 | 1:1 |
| REG-PIB-106 | 464299 | TM: regular card + declined payment (3cp modal)              | 1:1 |
| REG-PIB-107 | 464301 | TM AH: add new employee + WL card → company correspondence   | 1:1 |
| REG-PIB-108 | 464320 | Booker AH: WL card for existing employee, limit + restrict usage *(CM4)* | 1:1 |
| REG-PIB-109 | 464322 | Card-holder booker cannot add WL card for himself            | 1:1 |
| REG-PIB-110 | 464323 | Booker AH: WL card for existing employee → company correspondence | 1:1 |
| REG-PIB-111 | 465576 | TM AH: add card for me, cardholder-address option unavailable | 1:1 |
| REG-PIB-112 | 465577 | Booker AH: WL card for existing employee → cardholder alt address | 1:1 |
| —           | 464321 | WL add-card-for-me *(CM6)* — ⊂ REG-PIB-108 / 464320 (both UK)  | Cut |
| —           | 464297 | Non-tethered TM PIBA → Mastercard *(CM9)* — ⊂ REG-PIB-100 / 464282 | Cut |

**profileManagementJourney** — 15 → 14 (1 fold)

| BL-ID      | Src TC | Feature                                                       | Appr. |
|------------|--------|---------------------------------------------------------------|-------|
| REG-PIB-120 | 445844 | TM: edit My Profile details (DE)                             | 1:1 |
| REG-PIB-121 | 445845 | Booker: edit profile details + address search (UK)          | 1:1 |
| REG-PIB-122 | 445846 | Booker: change password + current-password validation       | 1:1 |
| REG-PIB-123 | 445847 | Guest: change password + wrong-current validation            | 1:1 |
| REG-PIB-124 | 445848 | TM creates custom question; Guest triggers validation + saves | 1:1 |
| REG-PIB-125 | 445849 | TM: add Visa card, check details after save                  | 1:1 |
| REG-PIB-126 | 445850 | Self-Booker: add Mastercard + personal address + delete      | 1:1 |
| REG-PIB-127 | 445851 | Booker: add PIBA EU card + personal address + delete         | 1:1 |
| REG-PIB-128 | 445852 | TM: add PIBA card + memorable-word validation + delete       | 1:1 |
| REG-PIB-129 | 445853 | Self-Booker: edit meals & extras (breakfast + wifi) (UK)     | 1:1 |
| REG-PIB-130 | 445854 | Booker: edit meals & extras (breakfast, no wifi) (DE)        | 1:1 |
| REG-PIB-131 | 445856 | Booker: edit room preferences (UK) *(PM5)*                   | 1:1 |
| REG-PIB-132 | 445857 | Receive promotions: contact-centre preferences               | 1:1 |
| REG-PIB-133 | 445964 | TM: change password + same-password validation               | 1:1 |
| —           | 445855 | TM room preferences (UK) *(PM15)*; role twin of REG-PIB-131 / 445856 — uniquely owns the 2-adult/2-child auto-Family path → **fold into REG-PIB-131**, param `[Booker, TM]` | Fold |

**homeJourney** — 5 → 4 (1 cut)

| BL-ID      | Src TC | Feature                                                       | Appr. |
|------------|--------|---------------------------------------------------------------|-------|
| REG-PIB-140 | 446237 | TM books for another Booker (as stayer) + upcoming bookings *(superset)* | 1:1 |
| REG-PIB-141 | 446238 | Booker books for self (as stayer) + upcoming bookings        | 1:1 |
| REG-PIB-142 | 446239 | Self-Booker books for self; no spending-summary + upcoming   | 1:1 |
| REG-PIB-143 | 446242 | Suspended account: notifications, spending summary, upcoming | 1:1 |
| —           | 446140 | TM books for another TM — ⊂ REG-PIB-140 / 446237 (either 446238/446140 twin qualifies, A.5) | Cut |

**Fold and cut evidence.** payApp `AccessDeleted` (469343) + `AccessRemoved` (471038) become
one parameterised spec **REG-PIB-82** (`pib-payapp-share-revocation`, param `[delete, unshare]`) —
2 files to 1, so it is a fold, not a cut, and no coverage is lost. spending 464510 folds into
REG-PIB-91 (464522): only its booker-tab-hidden check is unique, and that assertion moves into
REG-PIB-91. profile 445855 (TM) + 445856 (Booker) are role twins on the same Room Preferences
screen; 445855 uniquely exercises the 2-adult/2-child auto-Family path and 445856 the cot path, so
they parameterise into **REG-PIB-131** across `[Booker, TM]` — a fold (both variants run), not a cut.
The three cuts (464321, 464297, and one home twin) are each a proven subset of a kept spec per
rationalisation A.3/A.5, so their assertions already have an owner (§14.1). Earlier drafts counted
the payApp pair as 2 cuts and placed the payApp fold in Phase 3a, which lost a spec across the phase
boundary.

> **Governance note (§9.3).** Several source specs declare more than one Zephyr ID (e.g. 467953
> bundles the finance-user registration sub-cases; 464279, 445847 and others carry extras). The
> table lists each spec's lead ID; the retire/relink step must carry the spec's full Zephyr set
> so none is orphaned.

All retained specs port at **full depth** (no cross-app lever): 1:1 except REG-PIB-82 (fold),
REG-PIB-91 (absorbs the 464510 fold), and REG-PIB-131 (absorbs the 445855 role twin).

**Phase 3b net: 76 → 70.**

---

## 9. Cross-Cutting Workstreams

### 9.1 Page Object Model (runs alongside each phase)

| Phase              | What exists                     | What is net-new                     |
|--------------------|---------------------------------|-------------------------------------|
| 1 (PI)             | 13 POMs in `pages/pi/`          | Minor extensions                    |
| 2 (CCUI)           | 5 `[mock]` components, no POMs  | `pages/ccui/*` + Eckoh + agent login|
| 3a (PIB booking)   | 6 `[mock]` components, no POMs  | `pages/pib/*` booking screens       |
| 3b (PIB non-book.) | as above                        | `pages/pib/*` admin (bulk effort)   |

`pages/ccui/` and `pages/pib/` exist but are empty. What is present is component
scaffolding — `header`, `footer`, `searchConsole`, `notificationPopup`, `cookieConsent` —
all suffixed `[mock]`, so treat them as placeholders rather than working page objects.
There is no `pages/bb/`; the shared booking screens for PIB go under `pages/pib/`.

### 9.2 Defect Pre-Clearance

Do not port a bug. Resolve or flag before migrating the affected TC:

| Item                       | Detail                                        | Blocks           |
|----------------------------|-----------------------------------------------|------------------|
| 531538 ID collision        | Two different specs print the same TC ID      | REG-PI-2, REG-CCUI-20     |
| 381191 wrong constant      | `PI_STANDARD_ROOM` vs `PID_STANDARD_ROOM`     | REG-CCUI-1            |
| DNRQ-66591 dead SRP steps  | 5 specs, not 1 (see below)                    | REG-PI-10, REG-PI-11, REG-PI-30, REG-CCUI-7 |
| 369556 permanently skipped | Hard `describe.skip`; only MLOS coverage      | Cut/fix          |
| Assertions pinned to bugs  | 389728 (87379), 76425 (66957), 418503 (90056) | REG-PI-9, REG-PI-3, REG-PI-26 |
| `if (false)` blocks        | 266200, 266203, 223456 unreachable (47440)    | REG-PI-23, REG-PI-24     |
| Environment-gated coverage | 389731 DLP dit-only; 384400 email uat-only    | BL001, REG-CCUI-3     |
| PIB zero-coverage guards   | 435202, 519597 skip on EN but book UK hotels  | REG-PIB-7, REG-PIB-18     |

#### OPEN ACTION — 531538 is claimed by two different specs

This one needs a Zephyr decision by a person; it cannot be resolved from the code. Both of
these print `TestCase ID: 531538` in their `it()` title:

- **PI** (folds into REG-PI-2) — `end2endBaselineBookingAsLoggedUserHubHotelWindowlessRoom`
  - Linked Zephyr record: **531538**
- **CCUI** (ports as REG-CCUI-20) — `end2endBaselineViewAndSelectAsAgentHubHotelWindowlessRoomContinueToPayment`
  - Linked Zephyr record: **internal id 1702933**

They are two genuinely different tests — a logged-in PI Hub windowless booking versus an
agent viewing and selecting a windowless room in CCUI — so one of the two printed IDs is a
copy-paste error.

Why it blocks work: the strategy folds 531538 into **REG-PI-2** (PI, §5.1) *and* ports it as
**REG-CCUI-20** (CCUI, §6.2). Under §9.3 an old ID must be either retired or carried forward,
never both. As it stands the same ID would be retired by one phase and retained by another.

Required before REG-PI-2 or REG-CCUI-20 is picked up:

1. Confirm in Zephyr which test case 531538 actually is.
2. Issue a correct ID for the other spec, or confirm it already has one and the printed
   title is simply wrong.
3. Record the mapping in both REG-PI-2 and REG-CCUI-20 so the Zephyr governance step in §9.3 has
   something unambiguous to act on.

Until that happens, treat the two specs as independent and do **not** assume folding one
covers the other.

**DNRQ-66591 blast radius.** The commented-out SRP validation is not confined to the spec
being cut. Five specs carry the workaround:

| TC     | Spec                                                     | Plan       |
|--------|----------------------------------------------------------|------------|
| 76469  | pi BookingAsGuestUKHotelNineNightsFourRoomsPOARemoveMeal | REG-PI-10      |
| 76491  | pi BookingAsLoggedUserDEHotelTwoNightsTwoRoomsPOA        | REG-PI-11      |
| 366971 | pi GuestUKHubHotelMultiNightsOneRoomPOAReduceNumberOfNights | REG-PI-30   |
| 379925 | ccui BookAsAgentOneRoomOneAdultFleRatePoaBac             | REG-CCUI-7      |
| 369555 | ccui BookAsAgentOneRoomsTwoAdultsMealsStandardRatePayNow | Cut        |

Porting any of the first four as-is carries the dead steps forward. Either restore the SRP
assertions in the new spec or record the gap explicitly.

**PIB zero-coverage guards.** Both 435202 and 519597 guard with
`if (Locale.isEnglishWebsite()) this.skip()` — so they run only on the German site — yet
they book `LONDON_HEATHROW_AIRPORT` and `LONDON_KING_CROSS` respectively, 435202 with
`UK_CURRENCY_CODE`. On an English-website run neither executes at all. Confirm they
currently produce results before porting; if they do not, they are cuts, not migrations.

### 9.3 Zephyr/Lambdatest Governance

For every N:1 / Cut:
1. Retire or merge the old Zephyr TC IDs.
2. Create a new Zephyr/Lambdatest TC linking to the new BL-ID.
3. Never orphan an ID.

### 9.4 Verification (BL001 Verifications Pattern)

Per migrated spec:
1. Diff the new spec's `validateX` assertion set against the union of the old TC(s) it replaces.
2. For channel-delta specs: confirm the channel-specific assertions are present; confirm the
   backbone assertions are present in the PI reference spec.
3. Record every assertion the new spec does **not** carry, with the reason it is still
   owned: covered by another spec (the PI reference backbone, a consolidation, or a
   parameterised twin), or defect-blocked (§9.2). "Dropped to save budget" is not a valid
   reason — there is no budget.

Per phase:
4. Aggregate the per-spec records into an assertion-inventory diff against the old pack.
   The gate is **zero loss**: every assertion must still have an owner (§1.2). A spec may
   omit an assertion only because another spec provably carries it — the diff exists to
   prove that, not to license a drop. No validator may fall to zero owners pack-wide.

---

## 10. Cross-App Coverage Matrix (Cut Safety Artifact)

| Assertion Category       | Owned By | Example Specs      |
|--------------------------|----------|--------------------|
| Channel-neutral Opera    | **PI**   | BL001, REG-PI-20, REG-PI-21|
| (hotel, dates, occupancy, rate, price, currency, deposit, flowId, cityTax, meals, guestDetails) |||
| PI-specific              | **PI**   | BL001, REG-PI-2–31     |
| (DLP, TripAdvisor, marketing, employee site, ECI/LCO, reg-card, donations, saved-card)         |||
| CCUI-specific            | **CCUI** | REG-CCUI-1–REG-CCUI-21        |
| (Eckoh tokenisation, agent DPA/IDV, A2C codes, contract rate, manager RWCC, accessible notif.) |||
| PIB-specific             | **PIB**  | REG-PIB-1–REG-PIB-34        |
| (3DS Worldline, allowances, dinner-budget, stored-card billing, cross-channel, Planet refund)   |||
| PIB non-booking          | **PIB**  | REG-PIB-40–REG-PIB-143        |
| (company admin, user lifecycle, pay-app, spending, card mgmt, profile, auth, home)             |||

**Rule:** cross-app dedup (§14) and channel-delta thinning are only allowed against rows PI
already owns — that is what keeps them lossless. If a PI reference spec is quarantined,
temporarily re-enable backbone depth in the affected secondary spec.

This matrix is the standing check for every cut, in any phase. There is no separate "Phase
4"; earlier drafts referred to one that was never defined.

---

## 11. Sequencing Summary

```text
Phase 0: Shared API client library ───────────────────────────────────────────────────
    │
    ▼
Phase 1: PI baselines + smokes (37 specs) ────────────────────────────────────────────
    │
    ▼
Phase 2: CCUI baselines + smokes (24 specs) ──────────────────────────────────────────
    │
    ▼
Phase 3a: PIB booking + smokes (34 specs) ────────────────────────────────────────────
    │
    ▼
Phase 3b: PIB non-booking (70 specs) ─────────────────────────────────────────────────
```

The §14 lossless-dedup pass runs against each phase as it is reached, not as a phase of its own.

---

## 12. Effort Estimates (Relative)

Effort here is POM cost plus per-spec cost times spec count. The two move independently: a
phase can be small in specs but expensive in POMs (Phase 2) or the reverse (Phase 1).

| Phase | Specs | POM cost | Per-spec | Size | Driver                                |
|-------|------:|----------|----------|------|---------------------------------------|
| 0     |     — | —        | —        | L    | Extend existing library for CCUI/PIB + OHIP seeding |
| 1     |    37 | ~none    | low      | M–L  | POMs exist, BL001 proved the pattern; volume only |
| 2     |    24 | high     | high     | L    | Net-new CCUI POMs + Eckoh iframe — highest per-spec risk |
| 3a    |    34 | med-high | medium   | L    | Net-new PIB booking POMs, but backbone proven by PI |
| 3b    |    70 | very high| medium   | XL   | Net-new admin POMs, 8 journeys, unfamiliar stack |

Revised from an earlier draft, where Phase 1 was sized M against a spec count of ~32 and
Phase 3a M against ~24. The corrected counts are 37 and 34, so both moved up a notch —
Phase 3a materially, since 34 specs on net-new booking POMs is no longer the cheap phase it
looked like at 24.

Phases 2 and 3a now sit at the same size for different reasons: Phase 2 is fewer specs but
carries the Eckoh iframe, the single hardest integration in the migration; Phase 3a is more
specs on a backbone PI has already validated. If capacity is tight, Phase 3a is the more
predictable of the two.

---

## 13. Risks

| Risk                          | Impact                         | Mitigation                          |
|-------------------------------|--------------------------------|-------------------------------------|
| PI reference spec flaky       | Secondary channels lose        | Keep one full-funnel smoke per      |
|                               | backbone coverage              | channel; re-enable backbone temp.   |
| PIB 3b net-new POMs           | Largest effort; unfamiliar     | Sequence last; split by journey     |
|                               | Tailwind/shadcn stack          | into sub-phases                     |
| Payment divergence            | Cannot dedup payment tests     | Keep payment paths 1:1 per channel  |
| (PI/PIB 3DS vs CCUI Eckoh)   | across channels                |                                     |
| Environment-gated assertions  | Coverage varies by env         | Document per-spec; run uat+dit      |
|                               |                                | monthly                             |
| Worldline/SIX iframe flaky    | 3DS tests brittle              | 120s timeouts; retry on first fail  |
| Unread specs thinned          | Assertions dropped from specs   | Channel-delta and cross-app dedup  |
|                               | nobody has diffed              | gated on a read-and-diff first     |

---

## 14. Lossless-Dedup Backlog — Reducing Below 165 Without Losing Coverage

§§5–8 enumerate 165 specs at full coverage. That is the floor the evidence-backed lossless
techniques already reach; there is no coverage budget and no lower target to hit (§1.2).
This section is a **backlog of further reductions that might be possible** — places where
the pack looks duplicative enough that a single spec could carry what several do today. None
of them is a cut yet. Each is a hypothesis that becomes a reduction only once a read-and-diff
proves the assertions have another owner, so that removing the file loses nothing. Everything
above is backed by a file-by-file read of the reference tree; everything below still has to
earn it.

### 14.1 The gate

No spec leaves the plan until:

1. Its `validateX` set has been read from the source file — not inferred from its name.
2. Every assertion in that set has another owner. There is no "accepted loss" escape hatch —
   an assertion with no other owner keeps its spec.
3. The §10 matrix confirms no validator drops to zero owners pack-wide.

The rationalisation doc read 49 of 80 PI + CCUI files and left **17 CCUI baselines and 3 PI
amend variants unread**, with the standing instruction to treat unread files as owners.
Several §6.2 rows (REG-CCUI-12, REG-CCUI-14, REG-CCUI-19) are marked "inferred, not yet read", so §6.2
ports them 1:1 at full depth rather than channel-delta. Thinning an unread spec is the one move this
plan should not make.

### 14.2 Where lossless reductions may come from

Ranked by confidence, highest first. Every figure is a **ceiling that only materialises if
the diff proves overlap** — not a saving we are committed to taking.

| Pool                                    | Candidate specs | Possible lossless reduction | Basis |
|-----------------------------------------|----------------:|----------------------------:|-------|
| PIB booking permutations (§7.3)         |              16 |                        8–10 | Role × rate × payment matrix on one backbone; parameterise the ones that share every assertion |
| PIB non-booking long tail (§8)          |              70 |                         6–8 | Rationalisation A.3 measured ~100% overlap in several clusters — genuine duplicates |
| CCUI role twins (§6.2)                   |              18 |                         3–4 | Agent vs manager on near-identical journeys — parameterise so both roles still run |
| PI someone-else overlap (REG-PI-31 vs REG-PI-6)|         2 |                           1 | 365944 and 385014 may both own someone-else + different billing |
| Defect-blocked zero-coverage specs      |               2 |                           2 | 435202, 519597 may not execute at all — cutting them drops no live coverage (§9.2) |
|                                         |                 |                   **20–25** |       |

The two largest pools are also the cheapest to verify, because both are permutation clusters
where one parameterised spec can replace a group without dropping a check.

Note what is **not** in this table: the old "PI rare combinations" pool (9-night stays,
HUB + amend, FMTRPL + child-breakfast). Rarity is not overlap — those specs are sole owners
of their assertions, so under the no-loss rule they are off the table unless a diff proves
another spec already makes the same checks.

### 14.3 What must never be cut

- The last owner of any assertion, in any channel.
- A rare-combination spec that solely owns its assertions — rare is not redundant (§1.2).
- Payment-path divergence: PI/PIB 3DS and CCUI Eckoh stay 1:1 per channel (§13).
- Cross-channel switching (450465 / REG-PIB-9) — sole owner of the project-switch path.
- Anything already pinned to an open defect, where the assertion is the only record of the bug.

### 14.4 Recording

Every reduction gets a line in the phase's verification record (§9.4): spec removed,
assertions it carried, and the spec(s) that now own each one. A removal with no such record —
no proof of another owner — is a coverage regression, not a saving, and must be reverted.
There is no target to reach: the count lands wherever proven-lossless dedup takes it. If a
pool turns out not to overlap, its specs stay and the count stays higher. Coverage is the
constraint; the spec count is just the result.
