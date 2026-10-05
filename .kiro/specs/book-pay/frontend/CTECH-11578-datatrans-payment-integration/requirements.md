---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-11578
---
# Requirements Document

## Introduction

This spec defines the requirements for integrating Datatrans payment services into the Premier Inn frontend payment page, specifically for the `frontend/pi-front-end-applications/apps/next-apps/premier-inn` application. The integration migrates from the legacy web2Pay system to Datatrans for New Card, PayPal, Apple Pay, and Google Pay payment methods, using Unleash feature toggle `release_datatrans_integration` for hotel-based rollout control. Jira ticket CTECH-11578 covers "Create Secured Fields, Paypal, Apple Pay and Google Pay integrations."

## Glossary

- **Datatrans**: New payment gateway provider replacing web2Pay for secure payment processing
- **Secure Fields**: Datatrans iframe-based secure card data collection system
- **web2Pay**: Legacy payment provider being migrated away from
- **Unleash**: Feature flag service controlling the rollout of Datatrans integration per hotel
- **Transaction ID**: Datatrans-generated identifier for payment session management
- **Basket ID**: Reservation identifier used across the payment flow
- **Return URL**: URL for post-3DS redirect after payment authentication

## Requirements

### Requirement 1: Feature Toggle Integration
**User Story:** As a system administrator, I want hotel-specific control over payment provider selection, so that I can gradually migrate hotels from web2Pay to Datatrans.

#### Acceptance Criteria
1. THE system SHALL check the Unleash feature flag `release_datatrans_integration` with hotel ID as the context
2. WHEN the feature flag returns `false` THE system SHALL continue using the existing web2Pay payment flow without any changes
3. WHEN the feature flag returns `true` THE system SHALL use the new Datatrans payment integration
4. THE system SHALL handle feature flag service failures gracefully by falling back to the legacy web2Pay flow

### Requirement 2: Datatrans Payment Method Support
**User Story:** As a guest, I want to pay using New Card, PayPal, Apple Pay, or Google Pay through Datatrans, so that I can complete my booking with my preferred payment method.

#### Acceptance Criteria
1. THE payment page SHALL support New Card payments via Datatrans Secure Fields integration
2. THE payment page SHALL support PayPal payments via Datatrans PayPal button integration
3. THE payment page SHALL support Apple Pay via Datatrans Payment Button integration
4. THE payment page SHALL support Google Pay via Datatrans Payment Button integration
5. THE system SHALL NOT migrate Saved Card payments to Datatrans (legacy flow remains unchanged)

### Requirement 3: Secure Fields Card Payment
**User Story:** As a guest, I want to securely enter my new card details, so that I can pay for my reservation without my sensitive data being handled by Premier Inn systems.

#### Acceptance Criteria
1. WHEN a guest selects New Card payment THE system SHALL display Datatrans Secure Fields for PAN and CVV entry
2. THE system SHALL collect expiry month and year through non-secured merchant-owned form fields
3. THE system SHALL call `POST http://localhost:9200/api/payments/secure-fields` with basketId and returnUrl to obtain transaction ID
4. THE basketId parameter SHALL be the reservation ID from the browser URL query parameter
5. THE returnUrl parameter SHALL be the current URL with additional query parameter `source=datatrans`
6. WHEN Datatrans secure fields emit a success event THE system SHALL call the backend authorize endpoint as defined in design-e2e.md
7. WHEN a guest selects a different payment method THE system SHALL hide the card form
8. WHEN a guest re-selects New Card THE system SHALL show the card form again

### Requirement 4: Continue Button Integration
**User Story:** As a guest, I want a single Continue button that handles Datatrans payment submission, so that I can complete my payment with a familiar interface.

#### Acceptance Criteria
1. THE existing Continue button SHALL be replaced with Datatrans submit functionality when New Card is selected
2. WHEN New Card is selected THE Continue button SHALL trigger `secureFields.submit()` with expiry month and year
3. WHEN other payment methods are selected THE Continue button SHALL trigger the appropriate Datatrans button functionality
4. THE button behavior SHALL only change when the Datatrans feature flag is enabled for the hotel

### Requirement 5: Alternative Payment Methods Integration
**User Story:** As a guest, I want to use PayPal, Apple Pay, or Google Pay through Datatrans, so that I can pay with my preferred digital wallet.

#### Acceptance Criteria
1. WHEN PayPal is selected THE existing PayPal button SHALL be replaced with the Datatrans PayPal button per https://docs.datatrans.ch/docs/paypal-button
2. WHEN Apple Pay is available THE existing Apple Pay button SHALL be replaced with the Datatrans Payment Button per https://docs.datatrans.ch/docs/payment-button
3. WHEN Google Pay is available THE existing Google Pay button SHALL be replaced with the Datatrans Payment Button per https://docs.datatrans.ch/docs/payment-button
4. THE buttons SHALL only be replaced when the Datatrans feature flag is enabled for the hotel

### Requirement 6: Post-Payment Redirect Handling
**User Story:** As a guest, I want to be automatically redirected to the confirmation page after successful Datatrans payment, so that I can see my booking confirmation.

#### Acceptance Criteria
1. WHEN the payment page URL contains query parameter `source=datatrans` THE system SHALL not make any server-side API calls
2. WHEN the payment page URL contains query parameter `source=datatrans` THE system SHALL not render any browser-side content including layout
3. WHEN the payment page URL contains query parameter `source=datatrans` THE system SHALL redirect to the confirmation page
4. THE confirmation page URL SHALL follow the pattern `/<country>/<language>/<booking-flow-id>/confirmation?reservationId=<reservationId>`
5. THE reservationId SHALL be extracted from the original URL query parameter

### Requirement 7: API Integration
**User Story:** As the frontend application, I want to communicate with the Datatrans backend service, so that I can initialize payment sessions and authorize transactions.

#### Acceptance Criteria
1. THE system SHALL call `POST http://localhost:9200/api/payments/secure-fields` to initialize Datatrans payment sessions
2. THE API call SHALL include basketId (reservation ID) and returnUrl (current URL + source=datatrans parameter)
3. THE system SHALL handle API response containing the transaction ID for Secure Fields initialization
4. THE system SHALL call the backend authorize endpoint as specified in design-e2e.md after successful Datatrans events
5. THE system SHALL handle API errors gracefully and provide appropriate user feedback

### Requirement 8: Error Handling and Fallback
**User Story:** As a guest, I want reliable payment processing with clear error messages, so that I can successfully complete my booking even if technical issues occur.

#### Acceptance Criteria
1. WHEN Unleash feature flag service is unavailable THE system SHALL default to the legacy web2Pay flow
2. WHEN Datatrans API calls fail THE system SHALL display appropriate error messages to the guest
3. WHEN Datatrans Secure Fields fail to load THE system SHALL provide fallback options or clear error guidance
4. THE system SHALL log integration errors for monitoring and debugging purposes
5. THE system SHALL maintain payment form state appropriately during error conditions