# Implementation Plan: Secure Fields v1 → v2 Migration (CTECH-12383)

## Overview

This plan migrates the Secure Fields (web) payment flow from Datatrans v1 APIs to v2 APIs. The
service is not in production, so v1 code is replaced directly — no Temporal versioning or
retention windows are needed.

The migration touches the REST adapter (URI and body changes), domain models (add `returnMethod`,
add `amount` to authorize), activity interface (signature update), workflow implementation (pass
amount, use v2 settle), and tests (MockWebServer adapter tests + workflow unit tests).

The external API (`POST /api/payments/secure-fields`, `POST /api/payments/authorize`) remains
unchanged. The `PaymentAuthorisedEvent` schema remains unchanged — `cardAlias`, `last4Digits`,
`expiry`, and `paymentMethod` continue to be populated from the v2 authorize response.

## Tasks

- [x] 1. Domain model and port updates
  - [x] 1.1 Add `returnMethod` field to `DatatransSecureFieldsRequest`
    - Add `String returnMethod` field to the record (default: `"POST"`)
    - Update all call sites creating `DatatransSecureFieldsRequest` to pass `"POST"`
    - _Requirements: 1.2, 1.9_

  - [x] 1.2 Update `DatatransOutPort.authorizeTransaction` signature
    - Change from `DatatransAuthorizeResponse authorizeTransaction(String transactionId, String refno)` to
      `DatatransAuthorizeResponse authorizeTransaction(String transactionId, String refno, long amount)`
    - _Requirements: 2.1, 2.3_

  - [x] 1.3 Update `PaymentActivities.authorizeTransaction` signature
    - Change from `DatatransAuthorizeResponse authorizeTransaction(String transactionId, String refno)` to
      `DatatransAuthorizeResponse authorizeTransaction(String transactionId, String refno, long amount)`
    - _Requirements: 2.1, 2.3_

  - [x] 1.4 Remove v1 `settleTransaction` from `DatatransOutPort` and `PaymentActivities`
    - Remove `settleTransaction(String transactionId, long amount, String currency, String refno)`
    - Rename `settleTransactionV2` to `settleTransaction` (or keep as-is and update callers)
    - _Requirements: 3.1, 7.3, 7.5_

- [x] 2. Adapter implementation — replace v1 with v2
  - [x] 2.1 Update `DatatransRestAdapter.initSecureFields` to use v2 path
    - Change URI from `/v1/transactions/secureFields` to `/v2/transactions/secure-fields`
    - Update request body to include `returnMethod` from the request record
    - Keep `amount`, `currency`, `returnUrl` in the body
    - Do NOT include `autoSettle` (v2 defaults to deferred settlement)
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8_

  - [x] 2.2 Update `DatatransRestAdapter.authorizeTransaction` to use v2 path
    - Change URI from `/v1/transactions/{transactionId}/authorize` to
      `/v2/transactions/{transactionId}/authorize`
    - Update request body from `Map.of("refno", refno)` to `Map.of("refno", refno, "amount", amount)`
    - Map HTTP 409 → `ThreeDsAuthenticationFailedException` (v2 returns 409 for invalid
      transaction status instead of v1's 422)
    - Keep 401/403 → `AuthorizationDeclinedException`, 404 → `TransactionNotFoundException`
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 2.10_

  - [x] 2.3 Remove v1 `settleTransaction` method from `DatatransRestAdapter`
    - Delete the method that calls `/v1/transactions/{transactionId}/settle`
    - Rename `settleTransactionV2` to `settleTransaction` (or update all callers)
    - _Requirements: 3.1, 7.3_

  - [x] 2.4 Update `PaymentActivitiesImpl` for new signatures
    - Update `authorizeTransaction` to pass `amount` to `DatatransOutPort.authorizeTransaction`
    - Update settlement calls to use the v2 method
    - _Requirements: 2.1, 3.1_

- [x] 3. Workflow implementation — use v2 activity calls
  - [x] 3.1 Update `SecureFieldsPaymentWorkflowImpl.initSecureFields`
    - Build `DatatransSecureFieldsRequest` with `returnMethod = "POST"`
    - No other change needed — the adapter method is updated in-place
    - _Requirements: 1.1, 1.2, 1.9_

  - [x] 3.2 Update `SecureFieldsPaymentWorkflowImpl.authorize()`
    - Pass `this.amount` as the third argument to `authorizeActivities.authorizeTransaction(transactionId, bookingReference, amount)`
    - Extract `cardAlias`, `last4Digits`, `expiry`, `paymentMethod` from v2 response (same extraction logic as before — field names are identical)
    - Confirm `publishAuthorisedPaymentEvent` receives all card data
    - _Requirements: 2.1, 2.5, 5.1, 5.2, 5.3, 5.4, 5.5, 5.6_

  - [x] 3.3 Switch settlement to v2 in the workflow
    - Replace calls to `settleTransaction` with the v2 method (whichever name was chosen in 1.4)
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6_

  - [x] 3.4 Confirm status check already uses v2
    - Verify `getTransactionStatus` in the codebase calls `GET /v2/transactions/{transactionId}`
    - No code change needed — document confirmation
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

- [x] 4. Testing — Adapter MockWebServer tests
  - [x] 4.1 Test: v2 Secure Fields init — success
    - Mock `POST /v2/transactions/secure-fields` returning `{transactionId: "019ff5ab-..."}`
    - Assert correct URI path, request body (amount, currency, returnUrl, returnMethod),
      and Basic Auth header
    - Assert returned transactionId matches the UUID from the response
    - _Requirements: 8.1_

  - [x] 4.2 Test: v2 Secure Fields init — error cases
    - 400 → assert `DatatransGatewayException`
    - 500 → assert `DatatransGatewayException`
    - Empty body → assert `DatatransGatewayException`
    - Server unreachable → assert `ServiceUnavailableException`
    - _Requirements: 8.1_

  - [x] 4.3 Test: v2 authorize — success with card data
    - Mock `POST /v2/transactions/{uuid}/authorize` returning full response with
      `acquirerAuthorizationCode`, `card` object (alias, masked, expiryMonth, expiryYear)
    - Assert correct URI path (UUID in path), request body (refno + amount),
      and Basic Auth header
    - Assert `DatatransAuthorizeResponse` fields are correctly extracted
    - Assert `card.alias` is present (cardAlias continuity confirmed)
    - _Requirements: 8.1, 8.2_

  - [x] 4.4 Test: v2 authorize — error cases
    - 401 → assert `AuthorizationDeclinedException`
    - 403 → assert `AuthorizationDeclinedException`
    - 404 → assert `TransactionNotFoundException`
    - 409 → assert `ThreeDsAuthenticationFailedException`
    - 500 → assert `DatatransGatewayException`
    - Server unreachable → assert `ServiceUnavailableException`
    - _Requirements: 8.1_

- [x] 5. Testing — Workflow unit tests
  - [x] 5.1 Test: authorize passes amount and extracts cardAlias
    - Trigger `authorize()` signal
    - Mock `authorizeTransaction` to return response with card object containing
      alias, masked, expiryMonth, expiryYear, paymentMethod
    - Assert `publishAuthorisedPaymentEvent` is called with:
      - `cardAlias` = response card.alias
      - `last4Digits` = last 4 of response card.masked
      - `expiry` = "MM/YY" from expiryMonth/expiryYear
      - `paymentMethod` = response paymentMethod
    - Assert `authorizeTransaction` was called with 3 args (transactionId, refno, amount)
    - _Requirements: 8.2, 5.2, 5.3, 5.4, 5.5_

  - [x] 5.2 Test: settlement uses v2 method
    - Assert the v2 settlement method is called (not the old v1 method)
    - _Requirements: 8.2_

  - [x] 5.3 Test: cardAlias null handling
    - Mock v2 authorize response with no `card` object (null)
    - Assert `publishAuthorisedPaymentEvent` is called with null cardAlias, null last4Digits,
      null expiry
    - Assert workflow still reaches SETTLED state
    - _Requirements: 5.6_

- [x] 6. Final verification
  - [x] 6.1 Run full build and tests
    - Run `cd backend && ./mvnw clean install -pl book-pay/services/payment-orchestration-service -am`
    - All tests pass
    - _Requirements: 8.3, 8.4_

  - [x] 6.2 Verify JaCoCo coverage ≥ 80%
    - Check coverage report for all new/modified non-excluded classes
    - _Requirements: 8.3_

  - [x] 6.3 Verify checkstyle — zero warnings
    - Run `cd backend && ./mvnw checkstyle:check -pl book-pay/services/payment-orchestration-service`
    - _Requirements: 8.4_

## Notes

- **No Temporal versioning needed.** The service is not in production. There are no in-flight
  workflows with v1 history. Code is replaced directly.
- The `returnMethod` field defaults to `"POST"` because v1 implicitly used POST for the 3DS
  redirect-back, and v2 makes this explicit. No functional change for the frontend.
- The `amount` parameter added to `authorizeTransaction` is the same value stored in workflow
  state during init. The v2 authorize endpoint requires it in the body (v1 did not).
- The `PaymentAuthorisedEvent` is confirmed unchanged. `cardAlias` comes from
  `DatatransCardInfo.alias()` which is populated identically from both v1 and v2 authorize
  responses.
- The `getTransactionStatus` method (from CTECH-12128) already uses `/v2/transactions/{id}`,
  so no change is needed for the status/reconciliation path.
- The v1 `settleTransaction` method is removed entirely — `settleTransactionV2` replaces it.
- No integration tests or property-based tests are required per ticket scope.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "1.3", "1.4"] },
    { "id": 1, "tasks": ["2.1", "2.2", "2.3", "2.4"] },
    { "id": 2, "tasks": ["3.1", "3.2", "3.3", "3.4"] },
    { "id": 3, "tasks": ["4.1", "4.2", "4.3", "4.4"] },
    { "id": 4, "tasks": ["5.1", "5.2", "5.3"] },
    { "id": 5, "tasks": ["6.1", "6.2", "6.3"] }
  ]
}
```
