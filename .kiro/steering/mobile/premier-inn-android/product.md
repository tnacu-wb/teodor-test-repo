---
inclusion: always
---

# Product Context

Premier Inn is the Android mobile app for Whitbread's Premier Inn hotel chain (UK's largest hotel brand). The app enables guests to search and book hotels, manage reservations, check in online, handle payments, and access their stay details.

Target market: UK hotel guests. Distributed via Google Play with staged rollouts.

## Package Identifiers

| Variant | Application ID |
|---------|---------------|
| Production | `com.whitbread.premierinn` |
| Stage (dev/test) | `com.whitbread.premierinn.stage` |

## Core User Journeys

| Journey | Packages | Summary |
|---------|----------|---------|
| Hotel search | `search/`, `searchresults/`, `calendar/`, `roomcriteria/` | Location, postcode, or map-based search |
| Booking | `hoteldetails/`, `reviewbooking/`, `payment/`, `paymentdetails/` | Availability → review → pay |
| My Bookings | `mybookings/`, `bookingdetails/` | View, amend, cancel reservations |
| Check-in Online (CIOL) | `ciol/` | Pre-arrival check-in, room allocation |
| Authentication | `login/`, `accountlogin/` | Auth0-based sign-in/sign-up |
| Account & Profile | `account/`, `personaldetails/` | Guest preferences, saved details |
| Payments | `payment/`, `paymentmethods/` | PayPal (Braintree), Datatrans |
| Push Notifications | `notifications/`, `push/` | Firebase Cloud Messaging |
| QR Kiosk Check-in | `qrkiosk/` | On-property kiosk scanning |

## Product Rules

These rules apply to all new code touching user-facing features.

### Localisation

- Supported locales: English (`en-GB`, primary) and German (`de`).
- All user-facing strings MUST be externalised to `strings.xml` with locale variants. Never hardcode display text in Kotlin or Compose.

### Feature Toggles

- Use `BuildConfig.STAGING` for compile-time stage/production differences.
- Use Firebase Remote Config for runtime feature flags.
- Guard risky or partially-complete features behind a Remote Config flag before merging to `develop`.

### Accessibility

- Every interactive UI element MUST have a content description (Compose: `contentDescription` or `semantics {}`).
- New Compose screens MUST pass TalkBack verification before merge.
- Follow Material 3 accessibility guidelines (minimum touch targets, contrast ratios).

### Analytics

- Every new screen MUST fire an Adobe Analytics page-view event on appearance.
- Key user interactions (button taps, form submissions, errors) MUST fire Adobe track-action events.
- Analytics event constants live in the feature's `analytics/` sub-package.

### Deep Links

- The app handles `premierinn.com` deep links for bookings, hotels, and check-in flows.
- New features that have a web-accessible equivalent MUST register appropriate intent filters in the manifest.

### Staged Rollouts

- Production releases use Google Play staged rollouts (percentage-based).
- Monitor crash-free rate and ANR rate before increasing rollout percentage.
