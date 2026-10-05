---
inclusion: always
---

# Product

The **Premier Inn iOS** app is the native mobile application for the Premier Inn hotel chain (Whitbread). It lets guests manage the full stay lifecycle from their phone, from finding a hotel through to checking out. It is an internal Whitbread product (Copyright © Whitbread); access to dependencies requires SSO-authorised GitHub SSH keys.

## Audience

Premier Inn guests, both leisure and business travellers. Business booker flows are a **first-class concern, not an edge case** — account for them when designing any booking, payment, or account feature. The app ships to multiple locales (English base, German), so treat all guest-facing copy as localizable from the start.

## Core capabilities

New work normally extends one of these capabilities. Identify the capability first, then place the work in its matching feature module rather than spreading logic across the app.

- **Search & booking**: find hotels, select rooms and rates, manage rooms/guests, review and book, apply upsells.
- **Booking management**: view reservations, find a reservation, amend dates/bookings, amend and pay.
- **Account**: register, login (Auth0), reset/change password, manage user details, preferences, newsletter, GDPR, delete account.
- **Stay experience**: online check-in, digital/door keys (Zaplox), kiosk pass, hotel details, maps, important announcements, order and pay.
- **Payments**: card details, add new card, payment methods (incl. PayPal), business booker flows.

## Domain vocabulary

Reuse this terminology consistently across UI copy, code identifiers, and specs. Do not invent synonyms. These concepts are modelled in `SimpleNetwork/Sources/Model/` (e.g. `Reservation`, `Rate`, `RoomType`, `Hotel`, `UpsellItem`, `User`, `PaymentCard`) — reuse those types rather than redefining them.

- **Reservation / booking** — a guest's stay record.
- **Rate** — the price/conditions option for a room.
- **Room type** — the category of room (e.g. double, family, accessible).
- **Upsell** — an optional add-on offered during booking.
- **Business booker** — a guest booking under a business account/flow.
- **Digital key / door key** — Zaplox-issued credential for room access.
- **Kiosk pass** — in-hotel self-service credential.
- **CIOL** — check-in online.

## Domain rules & conventions

- **Map work to a capability.** Extend the matching feature module; do not scatter capability logic across the app.
- **Guest data is sensitive.** Bookings, payment details, and account information are personal data. Respect GDPR/consent flows, avoid logging PII, and never hardcode or expose real guest credentials, card data, or reservation identifiers — use placeholders in examples and tests.
- **Auth0 is the source of truth for identity.** Route registration, login, and password flows through the existing Auth0 integration; do not introduce parallel auth.
- **Digital keys are safety-critical.** Treat Zaplox door-key and check-in flows with extra care; failures here directly block a guest's stay. Prefer fail-safe handling and clear error states over silent failure.
- **Payments demand correctness.** Card, PayPal, and business-booker payment flows must handle failure and confirmation states explicitly; never assume success.
- **Localize all user-facing strings.** Use `PILocalizedString` and add German translations alongside English; never ship hardcoded display copy.
- **Session is centralised.** The signed-in guest, tokens, and booking state live behind shared managers (`UserSessionManager`, `BookingDetails`, `SettingsManager`); read and update guest state through them rather than passing it around ad hoc.
