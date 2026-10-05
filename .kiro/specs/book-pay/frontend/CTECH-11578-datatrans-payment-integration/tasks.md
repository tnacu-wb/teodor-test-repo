# Implementation Plan

## Overview

This plan implements the Datatrans payment integration for the Premier Inn Next.js frontend app, migrating from the legacy web2Pay system to Datatrans for New Card (Secure Fields), PayPal, Apple Pay, and Google Pay payment methods. The integration is controlled by the Unleash feature flag `release_datatrans_integration` with hotel-based rollout.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1"] },
    { "id": 1, "tasks": ["2", "4"] },
    { "id": 2, "tasks": ["3", "5", "6"] },
    { "id": 3, "tasks": ["7"] },
    { "id": 4, "tasks": ["8"] },
    { "id": 5, "tasks": ["9"] }
  ]
}
```

## Tasks

- [x] 1. Add Datatrans feature flag constant and environment variables
  - [x] 1.1 Export `FT_PI_DATATRANS_INTEGRATION = 'release_datatrans_integration'` from `pi-components-catalog/api/src/constants/featureToggle.ts`
  - [x] 1.2 Register the flag in `PAGE.PAYMENT.featureToggles.flagsWithFallback` with fallback `false` in `src/utils/pi-all-pages-constants.ts`
  - [x] 1.3 Add `NEXT_PUBLIC_PAYMENT_ORCHESTRATION_API` to `serverRuntimeConfig` and `publicRuntimeConfig` in `next.config.js`
  - [x] 1.4 Add `NEXT_PUBLIC_DATATRANS_SECURE_FIELDS_URL` to `publicRuntimeConfig` in `next.config.js`
  - [x] 1.5 Update `.env.local.sample` with both env vars including sandbox URL examples and explanatory comments

**Requirements:** 1.1, 1.2, 1.3, 1.4

---

- [x] 2. Skip server-side data loading when source=datatrans is present
  - [x] 2.1 Add early-return guard at the start of `createPaymentPiDataLoaderFn` in `src/page-helper/payment/data.pi.ts` — when `query.source === 'datatrans'`, return `{ dehydratedState: dehydrate(new QueryClient()), pcksQueryInput: null, hiQueryInput: null, basketReference, isDatatransReturn: true }` without executing any GraphQL or Redis calls
  - [x] 2.2 Pass `isDatatransReturn` through from `getServerSideProps` return value to the page component props in `src/pages/payment.tsx`
  - [x] 2.3 Update `data.pi.test.tsx` with a test asserting no GQL queries are made when `source=datatrans`

**Requirements:** 6.1

---

- [x] 3. Client-side redirect to confirmation page on source=datatrans mount
  - [x] 3.1 Update the `Props` interface in `src/page-helper/payment/page.pi.tsx` to include `isDatatransReturn?: boolean`
  - [x] 3.2 Add a `useEffect` that saves `bookingFlowId` to `sessionStorage` on initial (non-return) payment page load
  - [x] 3.3 Add a `useEffect` that reads `bookingFlowId` from `sessionStorage` and calls `router.replace` to `/<country>/<language>/<bookingFlowId>/confirmation?reservationId=<basketReference>` when `isDatatransReturn === true`
  - [x] 3.4 Add an early-return `if (isDatatransReturn) return null;` before any JSX render to prevent layout/data calls
  - [x] 3.5 Update `page.pi.test.tsx` with tests covering both the redirect path and the normal render path

**Requirements:** 6.1, 6.2, 6.3, 6.4, 6.5

---

- [x] 4. Implement useDatatransSecureFields hook
  - [x] 4.1 Create `src/hooks/use-datatrans-secure-fields.ts` with the custom hook that loads the Datatrans Secure Fields script from `publicRuntimeConfig.NEXT_PUBLIC_DATATRANS_SECURE_FIELDS_URL`
  - [x] 4.2 Manage `isScriptLoaded`, `isScriptLoading`, `scriptError` state for conditional script loading
  - [x] 4.3 Call `POST /api/payments/secure-fields` with `{ basketId }` on mount (when script is loaded) to obtain `transactionId`
  - [x] 4.4 Instantiate global `SecureFields` object with `transactionId`, attach event listeners (`ready`, `validate`, `change`, `success`, `error`)
  - [x] 4.5 Expose `isInitialized`, `isReady`, `isValid`, `errors`, `transactionId` state to consumers
  - [x] 4.6 Expose `submitPayment(expiryMonth, expiryYear)` method that calls `secureFields.submit()` with card expiry
  - [x] 4.7 Clean up SecureFields instance and remove event listeners on unmount
  - [x] 4.8 Create `src/hooks/use-datatrans-secure-fields.test.ts` with unit tests covering all state transitions using a mocked global `SecureFields` object

**Requirements:** 3.1, 3.2, 3.3, 3.5, 4.2

---

- [x] 5. Implement DatatransSecureFieldsForm component
  - [x] 5.1 Create `src/components/DatatransSecureFieldsForm/DatatransSecureFieldsForm.tsx` accepting props `basketId`, `isVisible`, `onSuccess`, `onError`
  - [x] 5.2 Use `useDatatransSecureFields` hook internally; render two iframe placeholder divs (`id="cardNumber"`, `id="cvv"`) for Datatrans injection
  - [x] 5.3 Render merchant-owned expiry month and year inputs using Chakra UI components consistent with existing payment form styling
  - [x] 5.4 Show Chakra UI inline error messages for each SecureFields field using the `errors` map from the hook
  - [x] 5.5 Conditionally render (return `null` or `display: none`) based on `isVisible` prop
  - [x] 5.6 Expose form submission capability so parent Continue button can trigger it
  - [x] 5.7 Create `src/components/DatatransSecureFieldsForm/DatatransSecureFieldsForm.test.tsx` with tests covering visibility toggling, error display, `onSuccess`/`onError` callbacks, and jest-axe accessibility check

**Requirements:** 3.1, 3.2, 3.7, 3.8, 4.1, 4.2

---

- [x] 6. Create Next.js API route handlers for Secure Fields and Authorize
  - [x] 6.1 Create `src/pages/api/payments/secure-fields.ts` — POST only, validates `basketId`, reads `NEXT_PUBLIC_PAYMENT_ORCHESTRATION_API` from `serverRuntimeConfig`, forwards to `{orchestratorBaseUrl}/api/payments/secure-fields` with `{ basketId, returnUrl }`, returns `201` with JSON body or `502` on failure
  - [x] 6.2 Create `src/pages/api/payments/authorize.ts` — POST only, validates `transactionId` and `basketId`, forwards to `{orchestratorBaseUrl}/api/payments/authorize`, returns `204` on success or `502` on failure
  - [x] 6.3 Construct `returnUrl` correctly — current page URL + `?source=datatrans` or `&source=datatrans` depending on existing query params
  - [x] 6.4 Return `405` for non-POST methods, `400` for missing required body params, `500` when env var not configured
  - [x] 6.5 Create `src/pages/api/payments/secure-fields.test.ts` and `src/pages/api/payments/authorize.test.ts` with unit tests covering all status codes using mocked `fetch` and `next/config`

**Requirements:** 7.1, 7.2, 7.3, 7.4, 7.5

---

- [x] 7. Integrate Datatrans Secure Fields into the payment page component
  - [x] 7.1 Import `FT_PI_DATATRANS_INTEGRATION` from `@whitbread-eos/api` and add to `useFeatureToggle()` destructure in `src/page-helper/payment/page.pi.tsx`
  - [x] 7.2 Add Datatrans-specific state: `datatransTransactionId`, `secureFieldsReady`, `secureFieldsError`, `isProcessingPayment`
  - [x] 7.3 Add `handleSecureFieldsSuccess` handler — if `data.redirect` is present, set `window.location.href` to 3DS URL; otherwise `POST /api/payments/authorize` then navigate to confirmation
  - [x] 7.4 Add `handleSecureFieldsError` handler that sets `secureFieldsError` state and renders Chakra UI `<Alert status="error">`
  - [x] 7.5 Wire Continue button — when `isDatatransEnabled && selectedPaymentType?.type === 'NEW_CARD'`, trigger `DatatransSecureFieldsForm` submission; otherwise use legacy handler
  - [x] 7.6 Render `<DatatransSecureFieldsForm>` conditionally when flag is `true` and `NEW_CARD` is selected; hide when different payment method selected
  - [x] 7.7 Ensure all legacy code paths remain unchanged when `isDatatransEnabled` is `false`

**Requirements:** 1.3, 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 4.1, 4.2, 4.3, 4.4, 8.1, 8.2, 8.3, 8.4, 8.5

---

- [x] 8. Replace PayPal, Apple Pay, and Google Pay buttons with Datatrans equivalents
  - [x] 8.1 When `isDatatransEnabled` is `true` and PAYPAL is selected, render Datatrans PayPal button (per https://docs.datatrans.ch/docs/paypal-button) instead of existing `PaypalWBButton`
  - [x] 8.2 When `isDatatransEnabled` is `true` and Apple Pay is available, render Datatrans Payment Button (per https://docs.datatrans.ch/docs/payment-button) instead of legacy Apple Pay button
  - [x] 8.3 When `isDatatransEnabled` is `true` and Google Pay is available, render Datatrans Payment Button instead of legacy Google Pay button
  - [x] 8.4 Wire `onSuccess` and `onError` callbacks to shared `handlePaymentSuccess` / `handlePaymentError` handlers
  - [x] 8.5 Ensure Saved Card payment type always uses legacy flow regardless of feature flag
  - [x] 8.6 When `isDatatransEnabled` is `false`, all existing payment buttons remain unchanged

**Requirements:** 2.2, 2.3, 2.4, 2.5, 5.1, 5.2, 5.3, 5.4

---

- [x] 9. Property-based tests, integration tests, and error handling refinement
  - [x] 9.1 Write `fast-check` property test asserting feature flag `false` always renders legacy web2Pay flow (no `DatatransSecureFieldsForm` mounted) for any `{ hotelId, basketId }` — **Validates: Requirements 1.2, 1.4**
  - [x] 9.2 Write `fast-check` property test asserting feature flag `true` with `NEW_CARD` selected always renders `DatatransSecureFieldsForm` for any `{ hotelId, basketId }` — **Validates: Requirements 1.3, 3.1**
  - [x] 9.3 Write `fast-check` property test asserting `source=datatrans` always triggers client-side redirect without UI for any `{ country, language, bookingFlowId, reservationId }` — **Validates: Requirements 6.1, 6.2, 6.3**
  - [x] 9.4 Write `fast-check` property test asserting SecureFields success event always leads to `POST /api/payments/authorize` call for any `{ transactionId, basketId }` — **Validates: Requirements 3.6, 7.4**
  - [x] 9.5 Extract pure `buildReturnUrl` helper and write `fast-check` property test asserting returnUrl always equals current URL + correct `source=datatrans` separator for any `{ path, existingParams }` — **Validates: Requirement 3.5**
  - [x] 9.6 Verify script load failure (missing env var or network error) causes graceful fallback to legacy flow with unit test
  - [x] 9.7 Verify API failure renders user-facing Chakra UI error alert with unit test

**Requirements:** 8.1, 8.2, 8.3, 8.4, 8.5

---

## Notes

- Tasks 1–3 can be completed and tested independently before the Secure Fields integration (Tasks 4–7).
- Task 4 (hook) is the core building block — Tasks 5, 6, and 7 all depend on it.
- Alternative payment methods (Task 8) are lower priority and can be deferred to a second iteration if needed.
- All property-based tests (Task 9) use `fast-check` which is already available in the monorepo's test dependencies.
- The `pages/api/payments/` directory already exists but is empty — Task 6 creates the first route handlers there.
