---
parent_design: none (derived from steering + industry standards)
jira: CTECH-12472
---

# Requirements Document

## Introduction

CTECH-12472 introduces two new purchasable food bundles — a **Hot Breakfast Box Bundle**
(adult) and a **Kids' Breakfast Box Bundle** (child) — on the booking-flow **Extras page**
(Step 1, "Add meals for entire stay" section) for **PI**, **PIB**, and **CCUI** users, at
hotels where Opera package codes `BFGBBA` (adult) and `BFGBBK` (kids) are configured in the
pre-payment table. Guests can pre-purchase breakfast boxes instead of buying them at the
front desk. The pre-payment table entries already exist, created by the Opera Team under
**OCB-2582**; currently only **TONHIL** and **CROCOO** have both codes configured, so the
feature is scoped to those two hotels until other hotels are configured. Hotels without both
codes configured are explicitly out of scope — the new cards must not appear there at all,
and existing extras/meals must be unaffected.

This spec is grounded directly in the full verbatim CTECH-12472 ticket text supplied for this
generation (description, in-scope/out-of-scope, assumptions, and 10 acceptance-criteria
scenarios), and in the existing meal-bundle card pattern already shipped in
`frontend/pi-front-end-applications` — the `MealSelection` organism and `MealItem` molecule
used today for the Breakfast Roll Bundle, Lighter Bundle, and Continental Bundle, including
their `freeBreakfastOption`/`freeBreakfastCode`/`freeBreakfastMaxPerMeal` free-kids-meal
mechanism and the `AddSubtract` quantity-stepper atom. This document supersedes an earlier
draft of this spec that was generated against a near-empty ticket; every requirement below is
sourced from the ticket text, not inferred.

Several details cannot be finalised from the ticket alone and are carried as open questions
into `design.md`: pending German translations, an unresolved interaction between the "up to 4
free kids breakfasts" edge case and the per-room children cap, and pending allergy/nutrition
content and product imagery from the F&B/design team.

## Glossary

- **Hot Breakfast Box Bundle**: new adult-facing purchasable bundle, Opera package code
  `BFGBBA`, priced per adult per day.
- **Kids' Breakfast Box Bundle**: new child-facing bundle, Opera package code `BFGBBK`,
  conditionally free — gated on at least one adult in the room purchasing a Hot Breakfast Box
  Bundle.
- **Pre-payment table**: the Opera-side configuration (per hotel) that determines which
  package codes are purchasable ahead of arrival; entries for `BFGBBA`/`BFGBBK` were created
  by the Opera Team under **OCB-2582**. A hotel not configured in this table for both codes is
  out of scope for this feature.
- **TONHIL / CROCOO**: the two hotels currently configured with both `BFGBBA` and `BFGBBK` in
  the pre-payment table.
- **Extras page (Step 1, "Add meals for entire stay")**: the booking-flow step where guests
  add optional meal bundles to their stay, rendered today via the `MealSelection` organism.
- **PI**: the Premier Inn consumer Next.js app (`apps/next-apps/premier-inn`).
- **PIB**: the Premier Inn Business booker Next.js app (`apps/next-apps/business-booker`).
- **CCUI**: the Contact Centre User Interface (`apps/next-apps/ccui`), used by agents booking
  or amending stays on a guest's behalf. In scope for this ticket, with its own
  acceptance-criteria scenario (4.1) for the agent-facing interaction.
- **`MealSelection` / `MealItem`**: existing organism/molecule pair
  (`@whitbread-eos/organisms` / `@whitbread-eos/molecules`,
  `pi-components-catalog/organisms/src/ancillaries/MealSelection`,
  `pi-components-catalog/molecules/src/ancillaries/MealItem`) that renders adult meal-bundle
  cards (e.g. Breakfast Roll Bundle) and a separate children's meals block, including the
  existing free-kids-meal eligibility mechanism.
- **`AddSubtract`**: existing quantity-stepper atom (`@whitbread-eos/atoms`) used as the
  adult/kids quantity control on meal-bundle cards.
- **`freeBreakfastOption` / `freeBreakfastCode` / `freeBreakfastMaxPerMeal`**: existing fields
  already returned on `meals[]` by the `GET_PACKAGES` GraphQL query and already consumed by
  `MealSelection`, which link an adult meal to a free/capped children's meal.
- **`GET_PACKAGES`**: existing GraphQL query
  (`pi-components-catalog/api/src/queries/ancillaries/getPackages.ts`) returning
  `meals`, `mealsKids`, `extrasItems`, and `roomSelection` for the Extras page. This ticket
  assumes no new query is needed — the two bundles arrive as new rows sourced from the
  pre-payment table.
- **Booking summary panel**: the running-total summary shown alongside the Extras page that
  updates as extras/meals selections change.

## Requirements

### Requirement 1: Hot Breakfast Box Bundle card content

**User Story:** As a guest booking a stay at a participating hotel, I want to see a Hot
Breakfast Box Bundle card on the Extras page, so that I can pre-purchase a hot breakfast for
my stay.

#### Acceptance Criteria

1. WHEN the Extras page loads for a reservation at a hotel with `BFGBBA` configured THE
   Extras page SHALL display a Hot Breakfast Box Bundle card with the title "Hot Breakfast Box
   Bundle".
2. THE Hot Breakfast Box Bundle card SHALL display the description "Your choice of any hot
   breakfast box and a drink".
3. THE Hot Breakfast Box Bundle card SHALL display its price formatted as "£8.00 per
   adult/day (£8.00 for X night(s))", where the per-night amount and stay total reflect the
   bundle's actual price and the reservation's number of nights.
4. THE Hot Breakfast Box Bundle card SHALL display a quantity stepper labelled "adults",
   defaulting to 0.
5. THE Hot Breakfast Box Bundle card SHALL display an "Allergy & Nutrition info" link.
6. THE Hot Breakfast Box Bundle card SHALL display a condition note "Kids eat breakfast free
   with this meal", sourced from the i18n key `upsell.label.promoText.freeKidsMeal`.

### Requirement 2: Kids' Breakfast Box Bundle card visibility and content

**User Story:** As a guest booking a stay with children, I want to see a Kids' Breakfast Box
Bundle card when my room has children, so that I understand my children's breakfast option.

#### Acceptance Criteria

1. WHEN the room has one or more children allocated THE Extras page SHALL display a Kids'
   Breakfast Box Bundle card.
2. IF the room has zero children allocated THEN THE Extras page SHALL NOT display the Kids'
   Breakfast Box Bundle card for that room.
3. THE Kids' Breakfast Box Bundle card SHALL display a product image.
4. THE Kids' Breakfast Box Bundle card SHALL display the title "Kids' Breakfast Box Bundle".
5. THE Kids' Breakfast Box Bundle card SHALL display the price as free (matching the price rendering of other free breakfast meals).
6. THE Kids' Breakfast Box Bundle card SHALL display a quantity stepper labelled "kids",
   defaulting to 0.
7. THE Kids' Breakfast Box Bundle card SHALL display an "Allergy & Nutrition info" link.

### Requirement 3: Kids' stepper is inactive until an adult bundle is selected

**User Story:** As a guest, I want the Kids' Breakfast Box Bundle stepper to make clear it
isn't available yet, so that I understand I need to add an adult bundle first.

#### Acceptance Criteria

1. WHILE the Hot Breakfast Box Bundle quantity for the room is 0 THE Kids' Breakfast Box
   Bundle stepper SHALL be disabled/inactive.

### Requirement 4: Kids' stepper unlocks and is capped once an adult bundle is selected

**User Story:** As a guest who has purchased a Hot Breakfast Box Bundle, I want to select
free breakfast boxes for my children up to the number in my room, so that the whole family is
covered.

#### Acceptance Criteria

1. WHEN the Hot Breakfast Box Bundle quantity for the room is >= 1 THE Kids' Breakfast Box
   Bundle stepper SHALL become enabled.
2. THE Kids' Breakfast Box Bundle stepper's maximum value SHALL be capped at the number of
   children allocated to the room.
3. (CCUI, scenario 4.1) IN the agent-facing CCUI view, WHEN the adult bundle quantity is >= 1
   THE Kids' Breakfast Box Bundle "Add" control SHALL be enabled, allowing the agent to add
   the kids' bundle for all children in the room, or remove it if already added.
4. **Open item — not resolved by this spec:** if 2 adults in a room each purchase a different
   bundle that offers a free kids breakfast, this can yield up to 4 free kids breakfasts total
   (2 per qualifying adult purchase), which may exceed the per-room children-count cap defined
   in Acceptance Criterion 4.2. Whether the aggregate free-kids-breakfast entitlement should
   override, add to, or be capped by the per-room children count requires product/business
   clarification — see Open Questions in `design.md`. This requirement intentionally does not
   pick an interpretation.

### Requirement 5: Adult stepper bounds

**User Story:** As a guest, I want the Hot Breakfast Box Bundle stepper to reflect a sensible
maximum, so that I don't try to buy more bundles than makes sense for my room.

#### Acceptance Criteria

1. THE Hot Breakfast Box Bundle stepper's minimum value SHALL be 0.
2. THE Hot Breakfast Box Bundle stepper's maximum value SHALL be `min(adultsInRoom, 2)`, i.e.
   the number of adults in the room, capped at 2.

### Requirement 6: Booking summary reflects selections in real time

**User Story:** As a guest, I want the booking summary to update as I change my breakfast box
bundle quantities, so that I always see an accurate running total.

#### Acceptance Criteria

1. WHEN the Hot Breakfast Box Bundle or Kids' Breakfast Box Bundle quantity changes THE
   booking summary panel SHALL update in real time.
2. THE booking summary panel SHALL show each selected bundle's name and quantity.
3. THE booking summary panel SHALL show the Hot Breakfast Box Bundle line cost as £8.00 ×
   quantity.
4. THE booking summary panel SHALL show the Kids' Breakfast Box Bundle line cost as £0.00.
5. THE booking summary panel SHALL show an updated running total reflecting both bundles'
   costs.

### Requirement 7: Non-participating hotels are unaffected

**User Story:** As a guest booking at a hotel that does not offer the breakfast box bundles,
I want the Extras page to look and behave exactly as it does today, so that I am not shown
options I cannot buy.

#### Acceptance Criteria

1. IF the selected hotel does not have both `BFGBBA` and `BFGBBK` configured in the
   pre-payment table THEN THE Extras page SHALL NOT display the Hot Breakfast Box Bundle card
   or the Kids' Breakfast Box Bundle card.
2. ON a non-participating hotel, THE Extras page's existing bundles and extras (e.g.
   Breakfast Roll Bundle, Lighter Bundle, Continental Bundle, early check-in, late check-out,
   Wi-Fi) SHALL be unaffected by this feature.

### Requirement 8: Submission maps selections to Opera package codes

**User Story:** As a guest, I want my breakfast box bundle selections to be correctly recorded
against my booking, so that they are honoured at the hotel.

#### Acceptance Criteria

1. WHEN the guest proceeds from the Extras page to the next step (Your Details/Payment) WITH
   a Hot Breakfast Box Bundle quantity > 0, THE booking payload SHALL include a selection
   mapped to Opera package code `BFGBBA` with the selected quantity (price is calculated by
   the backend).
2. WHEN the guest proceeds from the Extras page to the next step WITH a Kids' Breakfast Box
   Bundle quantity > 0, THE booking payload SHALL include a selection mapped to Opera package
   code `BFGBBK` with the selected quantity (price is zero and calculated by the backend).
3. IF neither bundle has a quantity > 0 THEN THE booking payload SHALL NOT include a selection
   for `BFGBBA` or `BFGBBK`.

### Requirement 9: Localisation

**User Story:** As a guest using the PI site in English or German, I want the breakfast box
bundle content correctly translated, so that I can understand what I'm buying.

#### Acceptance Criteria

1. WHEN the PI site locale is English THE new bundle cards, stepper labels, condition note,
   and "Allergy & Nutrition info" link SHALL display the correct English copy as specified in
   Requirements 1 and 2.
2. WHEN the PI site locale is German THE new bundle cards, stepper labels, condition note, and
   "Allergy & Nutrition info" link SHALL display the correct German copy.
   **Open item:** exact German translations for the new strings are pending from the
   localisation/content team — see Open Questions in `design.md`. All new guest-facing copy
   MUST be sourced from i18next translation keys (not hard-coded strings) so translations can
   be supplied without further code changes once available.

### Requirement 10: Responsive layout

**User Story:** As a guest on any device, I want the new bundle cards to display correctly, so
that I have a good experience regardless of screen size.

#### Acceptance Criteria

1. THE Hot Breakfast Box Bundle and Kids' Breakfast Box Bundle cards SHALL adapt/reflow across
   mobile, tablet, and other resolution changes consistently with the existing meal-bundle
   card pattern (Breakfast Roll Bundle, Lighter Bundle, Continental Bundle).
