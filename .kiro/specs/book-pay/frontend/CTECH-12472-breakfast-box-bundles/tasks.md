---
parent_design: none (derived from steering + industry standards)
jira: CTECH-12472
---

# Implementation Plan: Hot & Kids' Breakfast Box Bundles on the Extras Page

## Overview

Implements the Hot Breakfast Box Bundle (`BFGBBA`) and the conditionally-free Kids' Breakfast
Box Bundle (`BFGBBK`) on the Extras page ("Add meals for entire stay") for PI, PIB, and CCUI,
by extending the existing `MealSelection`/`MealItem` meal-bundle pattern already used for the
Breakfast Roll Bundle, Lighter Bundle, and Continental Bundle. No new GraphQL fields and no
new feature flags are required — the two bundles arrive as new `meals[]`/`mealsKids[]` rows,
gated purely by whether the hotel has both codes configured in the Opera pre-payment table.

## Tasks

- [ ] 1. Confirm open items before finishing implementation
  - [ ] 1.1 Confirm CCUI agent-facing interaction (scenario 4.1)
    - Confirm with design/product the exact CCUI "Add"/"Remove" control for the Kids'
      Breakfast Box Bundle before finalising that control's implementation.
    - _Requirements: 4.3_
  - [ ] 1.2 Confirm the multi-adult free-kids-breakfast interaction with product/business
    - Get an explicit decision on whether 2 adults each purchasing a qualifying bundle should
      override, add to, or be capped by the per-room children-count cap, before shipping any
      behaviour for that specific scenario.
    - _Requirements: 4.2, 4.4_
  - [ ] 1.3 Verify PIB integration point
    - Verify that `MealSelection` is already rendered at
      `apps/next-apps/business-booker/src/page-helper/guest-details/page.bb.tsx` and that
      the bundles flow through the existing component and submission path with no additional
      integration work.
    - _Requirements: 1.1, 2.1_
  - [ ] 1.4 Track pending content
    - Confirm delivery timeline for allergy/nutrition content and product imagery from the
      F&B/design team, and for German translations from the localisation team; land English
      copy behind i18next keys now regardless.
    - _Requirements: 1.5, 2.3, 2.7, 9.2_

- [ ] 2. Confirm backend data availability (verification, not implementation)
  - [ ] 2.1 Verify `GET_PACKAGES` returns the new rows for TONHIL/CROCOO
    - Confirm against a participating-hotel environment that `meals[]` includes a row with
      `id: 'BFGBBA'`, `freeBreakfastOption: true`, `freeBreakfastCode: 'BFGBBK'`,
      `upsellType: 'breakfast'` (required for children availability logic), and that
      `mealsKids[]` includes a row with `id: 'BFGBBK'`, per OCB-2582's pre-payment table
      configuration. No query/schema change is expected; this is a verification step.
    - _Requirements: 1.1, 2.1, 7.1_
  - [ ] 2.2 Regenerate GraphQL types if any field is missing
    - If verification in 2.1 finds a genuinely new field, run
      `yarn type-generate --targetEnv=<env>`; never hand-edit `graphql.ts`.
    - _Requirements: 1.1, 2.1_

- [ ] 3. Hot Breakfast Box Bundle card
  - [ ] 3.1 Wire the `BFGBBA` row through `MealSelection`/`MealItem`
    - Confirm the existing adult-meal rendering path in `MealSelection` picks up the new
      `meals[]` row and renders it via `MealItem` with title, description, `renderPrice`
      formatting, allergy link, and the `upsell.label.promoText.freeKidsMeal` condition note
      (already driven by `freeBreakfastOption`) with no code change expected.
    - _Requirements: 1.1, 1.2, 1.3, 1.5, 1.6_
  - [ ] 3.2 Adult stepper bounds
    - Add the bundle-specific cap `min(adultsInRoom, 2)` for the `BFGBBA` stepper (distinct
      from the generic per-room adult meal bounds used by other bundles), and confirm the
      stepper label reads "adults" and defaults to 0.
    - _Requirements: 1.4, 5.1, 5.2_
  - [ ] 3.3 Unit tests
    - Extend `MealSelection.test.tsx`: card renders with correct title/description/price
      format/condition note when `BFGBBA` is present; adult stepper min 0, max
      `min(adultsInRoom, 2)`; not rendered when `BFGBBA` absent.
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 5.1, 5.2, 7.1_

- [ ] 4. Kids' Breakfast Box Bundle card and eligibility logic
  - [ ] 4.1 Wire the `BFGBBK` row through the children's-meals block
    - Confirm the existing `childrenMeals` block in `MealSelection` renders the new
      `mealsKids[]` row with product image, title "Kids' Breakfast Box Bundle", "kids"-labelled
      stepper defaulting to 0, and allergy link, gated on `childrenMeals.length > 0`; the
      price rendering matches other free breakfast meals (no special "FREE\*" text required).
      Ensure the booking summary correctly handles BFGBBK.price = 0 (normalize if needed).
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7_
  - [ ] 4.2 Kids stepper disabled/enabled state
    - Disable the kids stepper (`isSubtractDisable`/`isPlusDisable`) whenever the room's
      `BFGBBA` quantity is 0; enable it once `BFGBBA` quantity is >= 1, using the existing
      `freeBreakfastCode` linkage and requiring a truthy `upsellType` on the adult meal
      (per `availableChildrenMealSelections` logic in `MealSelection.component.tsx`).
    - _Requirements: 3.1, 4.1_
  - [ ] 4.3 Kids stepper cap
    - Cap the kids stepper at the number of children allocated to the room (not the generic
      `freeBreakfastMaxPerMeal` value used elsewhere), pending resolution of the multi-adult
      edge case in task 1.2.
    - _Requirements: 4.2_
  - [ ] 4.4 Cascading removal on eligibility loss
    - When the room's `BFGBBA` quantity returns to 0, remove the room's `BFGBBK` selections
      before the guest can continue, mirroring `MealSelection`'s existing cascading removal
      for other free-linked children's meals.
    - _Requirements: 3.1_
  - [ ] 4.5 CCUI agent-facing control
    - Implement the CCUI-specific "Add"/"Remove" control for the Kids' Breakfast Box Bundle
      once task 1.1 confirms the exact interaction; until then, implement the safest
      interpretation (explicit add/remove for all children in the room, enabled once adult
      quantity >= 1) and flag for review.
    - _Requirements: 4.3_
  - [ ] 4.6 Unit tests
    - Extend `MealSelection.test.tsx`: Kids' card hidden when children = 0; kids stepper
      disabled while adults = 0; kids stepper enabled and capped at children count once
      adults >= 1; cascading removal when adult quantity returns to 0; `jest-axe`
      accessibility checks for both the disabled and enabled kids-card states.
    - Add CCUI-specific test cases once task 4.5's interaction is implemented.
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 3.1, 4.1, 4.2, 4.3_

- [ ] 5. Booking summary integration
  - [ ] 5.1 Real-time summary updates
    - Wire Hot/Kids Breakfast Box Bundle quantity changes into the existing booking-summary
      update path so the panel shows each bundle's name, quantity, line cost (£8.00 × adult
      quantity; £0.00 for kids), and an updated running total, without a page reload.
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_
  - [ ] 5.2 Unit/page-level tests
    - Assert booking summary content and running total across adult/kids quantity changes.
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_

- [ ] 6. Submission payload
  - [ ] 6.1 Map selections to Opera package codes
    - Extend the `roomsSelections`/`packagesSelection` builder feeding `saveReservation` so a
      `BFGBBA` quantity > 0 produces a `{ id: 'BFGBBA', noOfSelections }` entry and a
      `BFGBBK` quantity > 0 produces a `{ id: 'BFGBBK', noOfSelections }` entry (price
      calculation is handled by the backend), with neither entry present when its quantity is 0.
    - _Requirements: 8.1, 8.2, 8.3_
  - [ ] 6.2 Page-level tests (PI, CCUI, PIB per task 1.3's confirmed integration point)
    - Assert the `saveReservation` payload shape for: adult-only selection, adult+kids
      selection, and no selection.
    - _Requirements: 8.1, 8.2, 8.3_

- [ ] 7. Non-participating hotel regression
  - [ ] 7.1 Verify absence behaviour
    - Add/extend a test fixture without `BFGBBA`/`BFGBBK` in the `GET_PACKAGES` response and
      assert neither card renders and existing bundles/extras (Breakfast Roll Bundle, Lighter
      Bundle, Continental Bundle, early check-in, late check-out, Wi-Fi) are unaffected.
    - _Requirements: 7.1, 7.2_

- [ ] 8. Copy, localisation, and accessibility
  - [ ] 8.1 Add new i18next keys
    - Add keys for the two bundle titles, the Hot bundle's description, the "adults"/"kids"
      stepper labels, and the "Allergy & Nutrition info" link text (reusing
      `upsell.label.promoText.freeKidsMeal` for the condition note, which already exists).
      Populate English values now; leave German values pending per Open Question 2 in
      `design.md`.
    - _Requirements: 9.1, 9.2_
  - [ ] 8.2 Data-testid audit
    - Confirm all new/extended elements use `formatDataTestId` consistent with the existing
      `MealItem`/`MealSelection` naming convention.
    - _Requirements: 1.1, 2.1_
  - [ ] 8.3 Accessibility pass
    - Run `jest-axe` against the new card states (adult card; kids card disabled; kids card
      enabled; CCUI variant).
    - _Requirements: 2.1, 4.1_

- [ ] 9. Checkpoint: full regression pass
  - [ ] 9.1 Run `yarn check-integrity:all` (type-check + lint:fix + test + build) across
        affected packages (`api`, `atoms`, `molecules`, `organisms`, `utils`, `premier-inn`,
        `ccui`, `business-booker`)
    - _Requirements: 7.2_
  - [ ] 9.2 Manually verify non-participating-hotel behaviour is pixel-identical to current
        production Extras page
    - _Requirements: 7.1, 7.2_
  - [ ] 9.3 Manually verify responsive layout across mobile, tablet, and desktop breakpoints
        for both new cards
    - _Requirements: 10.1_

## Notes

- No backend/GraphQL implementation is included in this plan — the pre-payment table entries
  already exist (OCB-2582) and `meals[]`/`mealsKids[]` already carry the fields this feature
  needs; task 2 is verification only, not new backend work from this spec's frontend layer.
- No Unleash feature flags are introduced for this feature — visibility is entirely data-driven
  via the hotel's pre-payment-table configuration flowing through `GET_PACKAGES` (see
  design.md "Key changes versus the previous draft").
- Tasks 1.1–1.3 gate the CCUI-specific control (4.5), the multi-adult edge case (4.3's final
  cap behaviour), and the PIB integration point (6.2's PIB test target) respectively; the rest
  of the plan (PI card rendering, stepper bounds, booking summary, submission mapping,
  non-participating-hotel regression) does not depend on them and can proceed in parallel.
- No new property-based tests are warranted; the quantity-cap and eligibility logic is covered
  by targeted unit tests (tasks 3.3, 4.6) mirroring `MealSelection`'s existing test style,
  given the small, deterministic input space (0–2 adults, 0–N children per room).
- E2E (Playwright, `qa/`) coverage is a follow-up recommendation, not included in this plan's
  scope — raise as a separate `QA` spec once the CCUI interaction and German copy are
  confirmed.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "1.3", "1.4", "2.1"] },
    { "id": 1, "tasks": ["2.2", "3.1", "4.1"] },
    { "id": 2, "tasks": ["3.2", "4.2"] },
    { "id": 3, "tasks": ["4.3", "4.4", "4.5"] },
    { "id": 4, "tasks": ["3.3", "4.6", "5.1", "6.1", "8.1", "8.2"] },
    { "id": 5, "tasks": ["5.2", "6.2", "7.1", "8.3"] },
    { "id": 6, "tasks": ["9.1", "9.2", "9.3"] }
  ]
}
```
