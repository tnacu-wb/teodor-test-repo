# Requirements Document

## Introduction

This feature implements the Datatrans Mobile SDK version 4 new-card payment flow in the Premier Inn iOS app. It enables guests to pay for a reservation using a new credit or debit card via the native Datatrans SDK, which handles card entry, 3-D Secure authentication, and tokenisation entirely within the app (reducing PCI scope). The app initiates a payment session through the Payment Orchestrator backend (`POST /api/payments/mobile-sdk`), receives a `transactionId`, starts the Datatrans SDK, and displays a placeholder native dialog indicating payment success or failure based on the SDK outcome.

This spec covers the iOS app integration only. The backend (Payment Orchestrator, webhook handling, OPERA folio posting, settlement) is a separate service with its own spec. Android is also a separate spec.

## Glossary

- **Datatrans_SDK**: The Datatrans Mobile SDK version 4 for iOS, integrated via Swift Package Manager, that renders native card entry UI, performs tokenisation, and runs 3-D Secure 2 authentication on-device.
- **Payment_Orchestrator**: The backend service that coordinates payments. Reached via `RequestsManager+Payment`. Initiates Datatrans transactions, validates webhooks, posts deposit folios to OPERA, and settles payments.
- **Transaction_ID**: A UUID-format identifier returned by the Payment_Orchestrator (`POST /api/payments/mobile-sdk`) that authorises the Datatrans_SDK to process a specific payment. Valid for 30 minutes.
- **Basket_ID**: The identifier for the guest's current booking basket, used to initiate a payment session.
- **Payment_Module**: The iOS app module (under `Modules/`) responsible for orchestrating the new-card payment flow, including session initiation, SDK lifecycle, and result handling.
- **RequestsManager**: The existing `SimpleNetwork` networking façade. Payment calls are made through its `+Payment` extension.
- **Payment_Result**: The domain outcome of a payment attempt as observed by the app: success, failed, or cancelled.
- **SCA**: Strong Customer Authentication, satisfied through 3-D Secure 2 challenge or frictionless flow run natively by the Datatrans_SDK.
- **Card_Alias**: A reusable token returned by Datatrans on successful authorisation, representing the tokenised card for future payments.
- **PILocalizedString**: The app's localization mechanism for user-facing copy (English base and German).
- **AnalyticsManager**: The app's shared analytics tracking service (`AnalyticsManager.shared`).
- **AccessibilityIdentifiers**: The app's structured accessibility identifier constants used by EarlGrey UI tests.

## Requirements

### Requirement 1: Payment Session Initiation

**User Story:** As a guest, I want the app to securely set up my payment session with the backend before I enter any card details, so that my payment is authorised against my specific booking and amount.

#### Acceptance Criteria

1. WHEN the guest taps Pay on the booking review screen, THE Payment_Module SHALL call `POST /api/payments/mobile-sdk` via the RequestsManager with the current Basket_ID to obtain a Transaction_ID.
2. WHEN the Payment_Orchestrator returns a 201 response containing a non-empty Transaction_ID, THE Payment_Module SHALL proceed to start the Datatrans_SDK using that Transaction_ID.
3. IF the Payment_Orchestrator returns a 400 INVALID_REQUEST error, THEN THE Payment_Module SHALL set the Payment_Result to failed and SHALL surface a localized error message indicating the request could not be processed.
4. IF the Payment_Orchestrator returns a 404 BASKET_NOT_FOUND error, THEN THE Payment_Module SHALL set the Payment_Result to failed and SHALL surface a localized error message indicating the booking could not be found.
5. IF the Payment_Orchestrator returns a 409 BOOKING_ALREADY_PAID error, THEN THE Payment_Module SHALL set the Payment_Result to failed, SHALL surface a localized message indicating the booking has already been paid, and SHALL navigate the guest to the booking confirmation screen.
6. IF the Payment_Orchestrator returns a 409 BOOKING_ALREADY_CONFIRMED error, THEN THE Payment_Module SHALL set the Payment_Result to failed, SHALL surface a localized message indicating the booking is already confirmed, and SHALL navigate the guest to the booking confirmation screen.
7. IF the Payment_Orchestrator returns a 422 PAYMENT_METHOD_NOT_AVAILABLE error, THEN THE Payment_Module SHALL set the Payment_Result to failed and SHALL surface a localized error message indicating card payment is not available for this hotel.
8. IF the Payment_Orchestrator returns a 502 GATEWAY_ERROR or 503 SERVICE_UNAVAILABLE error, THEN THE Payment_Module SHALL set the Payment_Result to failed and SHALL surface a localized error message indicating a temporary problem, suggesting the guest try again.
9. IF the payment session initiation request does not receive a response within 30 seconds, THEN THE Payment_Module SHALL set the Payment_Result to failed, SHALL NOT invoke the Datatrans_SDK, and SHALL surface a localized error message indicating that the request timed out.
10. WHILE a payment session initiation request is in progress, THE Payment_Module SHALL display a loading indicator and SHALL prevent the guest from submitting a second concurrent payment request for the same booking.

### Requirement 2: Datatrans SDK Lifecycle Management

**User Story:** As a guest, I want the native card entry experience to start promptly and handle all outcomes correctly, so that I can complete or abandon my payment without confusion.

#### Acceptance Criteria

1. WHEN a valid Transaction_ID is received from the Payment_Orchestrator, THE Payment_Module SHALL start the Datatrans_SDK with that Transaction_ID within 2 seconds.
2. THE Payment_Module SHALL rely on the Payment_Orchestrator backend to configure the Datatrans_SDK environment (sandbox for non-production, production for live). The SDK v4.0.0 does not expose a client-side environment toggle; the environment is determined by the backend when creating the transaction.
3. THE Payment_Module SHALL NOT pass a `returnUrl` or cardholder data to the Datatrans_SDK, as the SDK handles 3-D Secure natively in-app.
4. WHEN the Datatrans_SDK reports a successful transaction completion, THE Payment_Module SHALL present a native UIAlertController dialog with a localized title indicating payment was successful and an OK button that dismisses the dialog. This is a placeholder for the future booking confirmation flow.
5. IF the Datatrans_SDK reports a transaction error, THEN THE Payment_Module SHALL set the Payment_Result to failed, SHALL present a native UIAlertController dialog with a localized title indicating payment failed and a localized message describing the failure, with an OK button that dismisses the dialog and returns the guest to the booking review screen to retry.
6. IF the guest cancels the Datatrans_SDK payment interface, THEN THE Payment_Module SHALL set the Payment_Result to cancelled and SHALL return the guest to the booking review screen without charging the payment.
7. IF the Datatrans_SDK fails to initialise or present the payment interface, THEN THE Payment_Module SHALL set the Payment_Result to failed and SHALL surface a localized error message indicating the payment could not be started.
8. THE Payment_Module SHALL NOT access, store, or log any raw card data; card entry is handled exclusively within the Datatrans_SDK native UI.

### Requirement 3: 3-D Secure Authentication

**User Story:** As a guest, I want my bank's authentication step handled within the app, so that my payment is secure and compliant without leaving the booking flow.

#### Acceptance Criteria

1. WHERE the Datatrans_SDK requires SCA for the payment, THE Payment_Module SHALL allow the Datatrans_SDK to present its 3-D Secure 2 authentication interface natively within the app without navigating the guest to an external browser.
2. WHEN the guest completes 3-D Secure 2 authentication successfully within the Datatrans_SDK, THE Payment_Module SHALL treat the SDK completion as a successful transaction and present the success placeholder dialog.
3. IF 3-D Secure 2 authentication fails within the Datatrans_SDK, THEN THE Payment_Module SHALL set the Payment_Result to failed, SHALL surface a localized message indicating authentication failed, and SHALL allow the guest to retry the payment.
4. IF the guest cancels the 3-D Secure 2 authentication, THEN THE Payment_Module SHALL set the Payment_Result to cancelled, SHALL return the guest to the booking review screen, and SHALL NOT charge the payment.

### Requirement 4: Error and Interruption Handling

**User Story:** As a guest, I want the app to handle network problems and interruptions gracefully during payment, so that I am not left uncertain about whether I was charged.

#### Acceptance Criteria

1. IF network connectivity is lost while the Payment_Module is initiating a payment session, THEN THE Payment_Module SHALL set the Payment_Result to failed and SHALL surface a localized connectivity error message to the guest.
2. IF the app enters the background while the Datatrans_SDK is presenting the payment interface, THEN THE Payment_Module SHALL allow the SDK to continue its process and SHALL restore its state when the app returns to the foreground.
3. WHEN a payment attempt ends in a failed or cancelled Payment_Result, THE Payment_Module SHALL retain the guest's position on the booking review screen so they can retry without re-entering booking details.
4. IF the Transaction_ID expires (30-minute validity window) before the guest completes the SDK flow, THEN THE Payment_Module SHALL treat the SDK error as a failed Payment_Result and SHALL surface a localized message indicating the payment session has expired, suggesting the guest try again.
5. THE Payment_Module SHALL prevent the guest from submitting a second concurrent payment for the same booking at any stage of the payment flow.

### Requirement 5: Localization

**User Story:** As a guest using the app in English or German, I want all payment messages in my language, so that I understand each step of the payment flow.

#### Acceptance Criteria

1. WHEN the Payment_Module renders any guest-facing payment copy, THE Payment_Module SHALL resolve the copy through PILocalizedString rather than from a hardcoded string literal.
2. THE Payment_Module SHALL provide both English (Base.lproj) and German (de.lproj) translations for every guest-facing payment string introduced by this feature.
3. WHERE the Datatrans_SDK exposes a display language configuration, THE Payment_Module SHALL set the Datatrans_SDK display language to match the active app locale before starting the payment session. Note: In SDK v4.0.0, `TransactionOptions` does not expose a `language` property — the SDK follows the device locale automatically.
4. IF the active app locale is neither English nor German, THEN THE Payment_Module SHALL rely on the Datatrans_SDK to fall back to its default language (English) and SHALL render guest-facing payment copy using the English (Base.lproj) translation.


