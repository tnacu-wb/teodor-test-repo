---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: https://whitbreadis.atlassian.net/browse/CTECH-12090
---
# Implementation Plan: PI Payment Details — New Card Pay Now

## Overview

This retrospective plan aligns the existing Premier Inn Datatrans code with the supplied CTECH-12090 ticket. It verifies implemented behavior and adds the ticket-specific CVV tooltip, Save Card preference, rate-dependent payment timing, state-clearing, localization, responsive, and happy-path acceptance evidence. Error handling, system exceptions, and alternative failure flows are excluded.

## Tasks

- [ ] 1. Align the PI payment-page entry contract
  - [ ] 1.1 Verify PI Datatrans eligibility
    - Test all combinations of the Datatrans feature toggle and hotel `isDataTransEnabled` capability.
    - Prove only the eligible PI web page renders the scoped Datatrans experience.
    - _Requirements: 1.1, 1.2, 1.5_
  - [ ] 1.2 Verify Credit/Debit Card selection without a saved card
    - Assert that `NEW_CARD` is displayed from enabled payment-method data and can be selected independently of saved-card methods.
    - Keep other displayed methods outside CTECH-12090 acceptance evidence.
    - _Requirements: 1.3, 1.4, 13.5_
  - [ ] 1.3 Verify required payment-page data
    - Cover booking, basket, hotel, rate, methods, timing options, booking address, total, terms, content, locale, and booking-flow context.
    - _Requirements: 2.5, 7.1_

- [ ] 2. Implement and verify New Card Pay Now timing
  - [ ] 2.1 Enforce Flex-rate timing behavior
    - Default eligible Flex bookings to Pay on Arrival.
    - Allow the guest to switch to Pay Now and retain the exact enabled `PAY_NOW` option.
    - _Requirements: 2.1, 2.3_
  - [ ] 2.2 Enforce non-Flex timing behavior
    - Select Pay Now for all other scoped rates and omit the timing toggle when there is no choice.
    - Prevent Secure Fields startup when New Card Pay Now is unavailable.
    - _Requirements: 2.2, 2.4_
  - [ ] 2.3 Verify Pay Now page content and actions
    - Render the applicable description, booking total, currency, terms, Confirm Booking, and Back.
    - _Requirements: 2.5_

- [ ] 3. Complete the same-page New Card form
  - [ ] 3.1 Verify form composition and PCI boundary
    - Render Cardholder Name and Expiry Date as merchant-owned controls.
    - Render Card Number and CVV only as Datatrans-hosted fields within the payment page.
    - Verify accepted-card and secure-payment information.
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.6, 4.6_
  - [ ] 3.2 Verify Secure Fields loading and readiness
    - Initialize the latest transaction when New Card becomes visible.
    - Show the loading representation and disable confirmation until hosted fields report ready.
    - _Requirements: 3.5_
  - [ ] 3.3 Verify valid card entry
    - Cover a valid Cardholder Name, numeric hosted Card Number, non-expired numeric Expiry Date, and valid hosted CVV.
    - Assert that valid data shows no validation messages and enables confirmation.
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

- [ ] 4. Add the CVV information tooltip
  - [ ] 4.1 Build or reuse an accessible CVV help component
    - Add an information control associated with the hosted CVV field.
    - Source English and German explanatory content from localization.
    - _Requirements: 5.1, 5.2, 11.3_
  - [ ] 4.2 Implement tooltip dismissal and state preservation
    - Support close action, Escape, and outside pointer interaction.
    - Preserve card and billing state and remain on the payment page when opening or closing help.
    - _Requirements: 5.3, 5.4_
  - [ ] 4.3 Verify tooltip accessibility
    - Test keyboard activation, focus behavior, accessible name, expanded state, controlled relationship, and dismissal.
    - _Requirements: 5.5, 12.3, 12.4_

- [ ] 5. Add the Save Card preference
  - [ ] 5.1 Resolve and consume save-card eligibility
    - Identify the approved payment-options/allowed-to-save source for the PI guest and basket.
    - Show or enable Save Card only when reusable storage is permitted.
    - _Requirements: 6.1, 6.6_
  - [ ] 5.2 Add a default-off localized Save Card checkbox
    - Integrate the control into New Card form state with `false` as the initial value.
    - Ensure unticked means no request to persist a reusable card.
    - _Requirements: 6.2, 6.3, 11.3_
  - [ ] 5.3 Propagate explicit save consent through the approved contract
    - Extend or reuse the frontend/backend contract to carry opt-in without exposing card data.
    - Distinguish transaction tokenisation from persistence as a reusable saved card.
    - Confirm with product/payment ownership that this resolves the ticket wording “the card is stored.”
    - _Requirements: 6.4, 6.5_

- [ ] 6. Complete billing-address behavior
  - [ ] 6.1 Verify the booking address default
    - Display the previously entered booking address with the checkbox ticked.
    - Use that address without requiring extra fields while selected.
    - _Requirements: 7.1, 7.2, 7.3, 7.4_
  - [ ] 6.2 Verify alternative address entry
    - Show the form when the guest unticks the checkbox.
    - Support valid postcode/address selection and manual address entry through the shared component.
    - Validate mandatory transaction fields.
    - _Requirements: 8.1, 8.2, 8.3_
  - [ ] 6.3 Apply the active billing address to the transaction
    - Submit the valid alternative address when selected and restore the booking address when reticked.
    - Map the active address into 3DS cardholder context.
    - _Requirements: 8.4, 8.5_

- [ ] 7. Enforce Back navigation and payment-state clearing
  - [ ] 7.1 Preserve booking journey context on Back
    - Route to the previous booking step without invalidating the basket or booking session.
    - Verify the guest can return to payment without restarting the journey.
    - _Requirements: 9.1, 9.2, 9.3_
  - [ ] 7.2 Clear payment-page data when leaving
    - Clear cardholder, expiry, hosted PAN/CVV state, Save Card choice, and alternative billing address.
    - Prove none of these values are persisted or restored from browser storage.
    - _Requirements: 9.4, 9.5_

- [ ] 8. Verify the successful New Card Pay Now journey
  - [ ] 8.1 Lock submission and display progress
    - On valid Confirm Booking, prevent duplicate confirm/back actions and display the spinner through Secure Fields submission and 3DS handoff.
    - _Requirements: 10.1, 10.2_
  - [ ] 8.2 Verify successful direct and 3DS authorization paths
    - Use basket-only frontend authorization after direct Secure Fields success or a successful 3DS return.
    - Verify the selected save-card consent and tokenised card details continue through the approved booking process.
    - _Requirements: 10.3, 10.4, 10.5_
  - [ ] 8.3 Verify booking confirmation outcome
    - Confirm the booking reaches the confirmation page with a booking reference and the existing platform sends the booking-details email.
    - Record the ticket assumption that this confirms a valid OPERA reservation without adding frontend OPERA validation.
    - _Requirements: 10.6, 10.7, 10.8_

- [ ] 9. Complete localization, responsive, and accessibility compliance
  - [ ] 9.1 Externalize all scoped guest copy
    - Replace hardcoded headings, labels, placeholders, timing descriptions, validation text, tooltip copy, checkbox text, amount annotations, and actions.
    - Add and test English and German values.
    - _Requirements: 11.1, 11.2, 11.3, 11.4_
  - [ ] 9.2 Verify responsive layouts
    - Test supported mobile, tablet, and desktop breakpoints for reflow, overlap, clipping, scrolling, and action visibility.
    - Cover booking summary, methods, timing, card form, billing, total, Confirm Booking, and Back.
    - _Requirements: 12.1, 12.2_
  - [ ] 9.3 Verify end-to-end accessibility
    - Run automated checks and targeted keyboard/screen-reader verification for all scoped controls, tooltip, hosted-field support, loading, and dynamic billing content.
    - _Requirements: 5.5, 12.3, 12.4, 12.5_

- [ ] 10. Build ticket-aligned acceptance evidence
  - [ ] 10.1 Reconcile unit and component suites
    - Cover the gate, selection/timing, same-page form, valid entry, CVV tooltip, Save Card, billing, Back clearing, loading, localization, responsive styles, and accessibility.
    - Update tests to production components and current basket-only payment contracts.
    - _Requirements: 1.1, 2.1, 2.2, 3.1, 4.5, 5.4, 6.2, 7.2, 8.4, 9.4, 10.1, 11.3, 12.3_
  - [ ] 10.2 Add a PI page-object end-to-end happy path
    - Use shared fixtures and typed non-production hotel, guest, and card data.
    - Cover New Card → Pay Now → valid details → optional successful 3DS → booking creation → confirmation reference.
    - _Requirements: 10.1, 10.3, 10.5, 10.6_
  - [ ] 10.3 Add locale and viewport acceptance variants
    - Run the scoped happy path or focused visual/interaction checks in English and German at representative mobile and desktop viewports.
    - _Requirements: 11.1, 11.2, 12.1, 12.2_
  - [ ] 10.4 Run the CTECH-12090 checkpoint
    - Run targeted Jest suites, PI lint/type-check/build, and scoped Playwright tests.
    - Attach evidence for every in-scope scenario and explicitly exclude system exceptions, payment failures, alternative flows, POA processing, other payment methods, and non-PI channels.
    - _Requirements: 13.1, 13.2, 13.3, 13.4, 13.5_

## Notes

- The Jira content was supplied directly by the user and is now the primary ticket source.
- Current implementation review found no scoped CVV tooltip or Save Card control; both are implementation tasks.
- The ticket's “Save card unticked by default” and “the card is stored” statements require the consent interpretation recorded in `design.md`: transaction tokenisation may occur for processing, but reusable persistence is opt-in.
- Error handling, system exceptions, declines, failed 3DS, and alternative flow scenarios are excluded and must remain in their separately tracked story.
- Fraud-before-TRA policy, TRA eligibility, donations, POA operational handling, no-show, cancellation, refund, and OPERA internals are assumptions or external ownership boundaries.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "1.3", "2.1", "2.2", "2.3"] },
    { "id": 1, "tasks": ["3.1", "3.2", "4.1", "5.1", "6.1", "6.2"] },
    { "id": 2, "tasks": ["3.3", "4.2", "4.3", "5.2", "5.3", "6.3", "7.1", "7.2"] },
    { "id": 3, "tasks": ["8.1", "8.2", "9.1", "9.2", "9.3"] },
    { "id": 4, "tasks": ["8.3", "10.1", "10.2", "10.3"] },
    { "id": 5, "tasks": ["10.4"] }
  ]
}
```
