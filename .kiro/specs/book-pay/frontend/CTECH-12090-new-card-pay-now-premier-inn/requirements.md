---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: https://whitbreadis.atlassian.net/browse/CTECH-12090
---
# Requirements Document

## Introduction

CTECH-12090, **[FE][PI] Payment details page - PN - new card**, enables a Premier Inn guest to select a new Credit/Debit Card and complete Datatrans Secure Fields on the payment details page without using a saved payment method. This specification reconciles the supplied Jira description and scenarios with the existing `premier-inn` implementation and the parent end-to-end payment design.

The implementation scope is Premier Inn web users completing the **New Card — Pay Now** journey. Other payment methods may appear as contextual choices, but their implementation is not part of this specification. Pay on arrival is described only where it controls whether Pay Now is selectable or defaulted. Per the ticket, system exceptions, payment failures, and alternative-flow error handling are tracked separately and are not acceptance criteria here.

## Glossary

- **PI**: The public Premier Inn web application and the only channel in scope.
- **New Card**: The Credit/Debit Card payment method used without selecting a previously saved card.
- **Pay Now (PN)**: The payment timing option that takes payment as part of booking completion.
- **Pay on Arrival (POA)**: A payment timing option used only to define Flex-rate defaults and the guest's ability to switch to Pay Now.
- **Datatrans**: The payment provider used for hosted card fields, tokenisation, and 3-D Secure.
- **Secure Fields**: Datatrans-hosted card-number and CVV iframes displayed within the Premier Inn payment page.
- **3DS**: 3-D Secure cardholder authentication, including Transaction Risk Analysis (TRA) decisions controlled by the payment platform.
- **Booking Address**: The address already supplied in the guest-details step.
- **Billing Address**: The address used for the card transaction; initially the booking address unless the guest chooses another.
- **Reusable Card Token**: A Datatrans alias that may be retained as a saved card only when the guest opts in and is eligible.
- **Basket Reference**: The reservation identifier passed through the payment and confirmation journey.

## Requirements

### Requirement 1: Premier Inn New Card Journey Availability
**User Story:** As a Premier Inn guest, I want New Card to be available on the payment page, so that I can book without using a saved payment method.

#### Acceptance Criteria
1.1 WHEN an eligible PI guest reaches the payment details page THEN THE application SHALL display Credit/Debit Card as an enabled payment method.
1.2 THE application SHALL select the Datatrans payment experience only when the Datatrans feature toggle and hotel capability are enabled.
1.3 THE Credit/Debit Card option SHALL represent the `NEW_CARD` method and SHALL NOT require a previously saved card.
1.4 THE application MAY display other enabled payment methods returned for the basket, but their payment flows SHALL remain outside CTECH-12090.
1.5 THE CTECH-12090 implementation SHALL apply only to PI web users and SHALL NOT apply to Business Booker, CCUI, or native apps.

### Requirement 2: Payment Timing for New Card
**User Story:** As a guest, I want the correct payment timing for my rate, so that I can choose Pay Now when the booking permits it.

#### Acceptance Criteria
2.1 WHEN the selected rate is Flex and both timing options are enabled THEN THE application SHALL default to Pay on Arrival and SHALL allow the guest to change to Pay Now.
2.2 WHEN the selected rate is not Flex THEN THE application SHALL select Pay Now and SHALL NOT require the guest to choose a timing option.
2.3 WHEN the guest selects Pay Now THEN THE application SHALL retain the enabled `PAY_NOW` payment option for the active New Card transaction.
2.4 IF New Card Pay Now is not enabled for the basket THEN THE application SHALL NOT start the scoped Secure Fields journey.
2.5 THE application SHALL display the Pay Now description, booking total, currency, terms and conditions, Confirm Booking action, and Back action applicable to the booking.

### Requirement 3: Same-Page New Card Form
**User Story:** As a guest, I want to enter new card details on the payment details page, so that I can complete checkout without navigating to a separate Premier Inn card page.

#### Acceptance Criteria
3.1 WHEN the guest selects Credit/Debit Card THEN THE application SHALL display the New Card form on the current payment details page.
3.2 THE form SHALL display Cardholder Name, Card Number, Expiry Date, and CVV controls.
3.3 THE Card Number and CVV controls SHALL be hosted by Datatrans Secure Fields.
3.4 THE Cardholder Name and Expiry Date controls SHALL be owned by the Premier Inn form and SHALL be submitted to Datatrans with the hosted-field data.
3.5 WHILE the Secure Fields session is loading THE form SHALL show a loading representation and SHALL prevent confirmation until the fields are ready.
3.6 THE page SHALL display accepted-card and secure-payment information without exposing card data to Premier Inn code.

### Requirement 4: Valid Card Detail Entry
**User Story:** As a guest, I want valid card details to be accepted, so that I can continue to card authentication and booking completion.

#### Acceptance Criteria
4.1 WHEN the guest enters a non-empty Cardholder Name THEN THE application SHALL accept it as the cardholder value.
4.2 WHEN the guest enters a valid Card Number THEN Datatrans Secure Fields SHALL accept numeric card input without displaying a validation error.
4.3 WHEN the guest enters a valid numeric Expiry Date in `MM / YY` form THEN THE application SHALL accept it when the month is valid and the card is not expired.
4.4 WHEN the guest enters a valid CVV THEN Datatrans Secure Fields SHALL accept it without displaying a validation error.
4.5 WHEN all card controls contain valid values THEN THE application SHALL permit Confirm Booking for the selected Pay Now option.
4.6 RAW PAN and CVV SHALL remain within Datatrans-hosted fields and SHALL NOT be read, stored, logged, or transmitted by Whitbread-owned frontend or BFF code.

### Requirement 5: CVV Information Tooltip
**User Story:** As a guest, I want an explanation of the CVV, so that I know which security code to enter.

#### Acceptance Criteria
5.1 WHEN the CVV field is displayed THEN THE application SHALL display an accessible information control associated with that field.
5.2 WHEN the guest clicks, taps, or keyboard-activates the information control THEN THE application SHALL display localized content explaining the CVV/security code.
5.3 WHEN the tooltip is open and the guest selects its close action or interacts outside it THEN THE application SHALL dismiss the tooltip.
5.4 WHEN the tooltip opens or closes THEN THE application SHALL preserve all entered card and billing values and SHALL keep the guest on the payment page.
5.5 THE tooltip SHALL expose correct focus, accessible-name, expanded-state, and dismissal semantics for keyboard and screen-reader users.

### Requirement 6: Save Card Preference
**User Story:** As an eligible guest, I want to control whether my new card is saved, so that reusable card storage occurs only with my choice.

#### Acceptance Criteria
6.1 WHEN saving a card is permitted for the guest and booking THEN THE New Card form SHALL display a Save Card checkbox.
6.2 WHEN the Save Card checkbox first appears THEN it SHALL be unticked by default.
6.3 WHEN the checkbox remains unticked THEN THE application SHALL NOT request persistence of the card as a reusable saved payment method.
6.4 WHEN the eligible guest ticks Save Card and the payment succeeds THEN THE application SHALL pass the opt-in through the approved payment contract so the Datatrans alias can be stored for future use.
6.5 THE application SHALL distinguish Datatrans tokenisation required for processing from guest consent to persist a reusable saved card.
6.6 IF the guest is not eligible to save a card THEN THE application SHALL omit or disable the control according to the payment-options response.

### Requirement 7: Existing Booking Address as Billing Address
**User Story:** As a guest, I want to use the address already supplied for my booking, so that I do not need to enter it again.

#### Acceptance Criteria
7.1 WHEN a booking address is available THEN THE payment page SHALL display it in the Account Billing Address section.
7.2 WHEN that section first appears THEN THE Billing Address checkbox SHALL be ticked by default.
7.3 WHILE the checkbox remains ticked THE booking address SHALL be used as the transaction billing address.
7.4 WHILE the checkbox remains ticked THE application SHALL NOT require additional billing-address fields.

### Requirement 8: Alternative Billing Address Entry
**User Story:** As a guest, I want to enter a different billing address, so that the card transaction uses the correct address.

#### Acceptance Criteria
8.1 WHEN the guest unticks the Billing Address checkbox THEN THE application SHALL display the billing-address form on the same payment page.
8.2 THE billing-address form SHALL allow the guest to enter a valid postcode and select an address or enter the address manually.
8.3 THE form SHALL collect and validate all mandatory billing-address fields required by the transaction.
8.4 WHEN all mandatory values are valid THEN THE application SHALL use the new address for the card transaction and 3DS cardholder context.
8.5 WHEN the guest reticks the Billing Address checkbox THEN THE application SHALL use the booking address instead of the alternative address.

### Requirement 9: Back Navigation and Data Clearing
**User Story:** As a guest, I want to go back without losing my booking session, so that I can change earlier booking details and continue later.

#### Acceptance Criteria
9.1 WHEN the guest selects Back from the payment details page THEN THE application SHALL return the guest to the previous booking step.
9.2 WHEN the guest navigates back THEN THE booking session and basket SHALL remain active.
9.3 WHEN the guest returns to the payment page THEN THE application SHALL allow the booking journey to continue without restarting.
9.4 WHEN the guest leaves the payment page via Back THEN THE application SHALL NOT retain cardholder name, expiry, PAN, CVV, Save Card selection, or an alternative billing address entered on that page.
9.5 THE application SHALL NOT persist payment-form values in browser storage for restoration after Back navigation.

### Requirement 10: New Card Pay Now Confirmation
**User Story:** As a guest, I want to confirm a Pay Now booking with my new card, so that the reservation is created and I receive confirmation.

#### Acceptance Criteria
10.1 GIVEN valid New Card details and the Pay Now option WHEN the guest selects Confirm Booking THEN THE application SHALL disable repeat actions and replace the action state with a loading spinner.
10.2 THE loading state SHALL remain visible while Secure Fields submits and until the 3DS handoff or direct authorization outcome begins.
10.3 WHEN Datatrans requires 3DS THEN THE browser SHALL present the 3DS journey and SHALL resume the PI booking flow after successful authentication.
10.4 WHEN Secure Fields succeeds without a redirect THEN THE frontend SHALL request authorization using the basket reference held by the Payment Orchestrator workflow.
10.5 WHEN authorization succeeds THEN THE booking-confirmation process SHALL continue using the tokenised card details and the selected save-card preference.
10.6 WHEN the booking is successfully created THEN THE application SHALL display the confirmation page with a booking reference.
10.7 WHEN the booking is successfully created THEN THE platform SHALL send the booking-details email through the existing confirmation process.
10.8 THE PI frontend SHALL treat successful confirmation as evidence of a valid OPERA reservation in accordance with the ticket assumption; OPERA validation implementation is outside this frontend spec.

### Requirement 11: English and German Content
**User Story:** As an English- or German-language guest, I want payment content in my selected language, so that I can understand the form and actions.

#### Acceptance Criteria
11.1 WHEN the selected language is English THEN THE application SHALL display all scoped payment content in English.
11.2 WHEN the selected language is German THEN THE application SHALL display all scoped payment content in German.
11.3 ALL headings, labels, placeholders, descriptions, tooltip content, checkbox text, validation text, loading text, amount annotations, and action text SHALL use the Premier Inn localization mechanism.
11.4 THE application SHALL NOT hardcode guest-facing English copy in the scoped components.

### Requirement 12: Responsive and Accessible Presentation
**User Story:** As a guest using any supported device, I want the payment page to adapt to my screen and input method, so that I can complete the booking.

#### Acceptance Criteria
12.1 WHEN the viewport changes across supported mobile, tablet, and desktop resolutions THEN THE page SHALL reflow without hiding, clipping, or overlapping scoped controls.
12.2 THE booking summary, payment methods, card form, billing address, timing selection, total, Confirm Booking action, and Back action SHALL remain usable at each supported breakpoint.
12.3 ALL scoped interactive controls SHALL be keyboard operable and expose accessible names, roles, states, focus order, and visible focus indication.
12.4 Loading and dynamically displayed content SHALL be announced appropriately without causing unexpected focus loss.
12.5 Hosted Secure Fields SHALL meet the accessibility capabilities provided by Datatrans and SHALL be surrounded by accessible Premier Inn labels and supporting content.

### Requirement 13: Ticket Scope Boundaries and Assumptions
**User Story:** As a delivery team, I want ticket boundaries recorded explicitly, so that CTECH-12090 acceptance remains focused.

#### Acceptance Criteria
13.1 CTECH-12090 SHALL NOT define acceptance behavior for system exceptions, payment failures, or alternative error flows.
13.2 Fraud-check ordering and TRA eligibility SHALL be controlled by the Datatrans/payment-platform rules and SHALL NOT be reimplemented in the PI frontend.
13.3 Donation selection SHALL remain in the preceding booking step.
13.4 POA front-desk payment, no-show, cancellation, and refund journeys SHALL remain outside this specification.
13.5 Other payment methods SHALL be treated as payment-page context only and SHALL NOT be implemented or verified as part of CTECH-12090.
