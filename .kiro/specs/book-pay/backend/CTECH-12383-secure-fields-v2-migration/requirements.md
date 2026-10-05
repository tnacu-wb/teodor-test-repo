---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-12383
depends_on: CTECH-11615
---
# Requirements Document

## Introduction

The Payment Orchestration Service's Secure Fields (web) flow currently uses Datatrans **v1 APIs**
for transaction initialisation (`POST /v1/transactions/secureFields`), authorisation
(`POST /v1/transactions/{transactionId}/authorize`), and settlement
(`POST /v1/transactions/{transactionId}/settle`). Datatrans has released a **v2 Transactions API**
that supports the Secure Fields flow with improved semantics, UUID-format transaction identifiers,
and an `attempts`-based status model.

This work migrates the Secure Fields web channel from v1 to v2 by:

1. Replacing the init endpoint from `POST /v1/transactions/secureFields` to
   `POST /v2/transactions/secure-fields`.
2. Replacing the authorize endpoint from `POST /v1/transactions/{transactionId}/authorize` (int64
   path param) to `POST /v2/transactions/{transactionId}/authorize` (UUID path param).
3. Replacing the settlement endpoint from `POST /v1/transactions/{transactionId}/settle` to
   `POST /v2/transactions/{transactionId}/settle` (UUID path param).
4. Updating the Secure Fields workflow's reconciliation/status-check path from
   `GET /v1/transactions/{transactionId}` to `GET /v2/transactions/{transactionId}`.
5. Ensuring that the `PaymentAuthorisedEvent` continues to carry `cardAlias`, `last4Digits`,
   `expiry`, and `paymentMethod` from the v2 authorize response — confirming backward
   compatibility of the event schema across the API version change.

After this work, both payment channels (Mobile SDK and Secure Fields) use Datatrans v2 exclusively,
eliminating the split-version maintenance burden and aligning with the Datatrans v2 migration path.

The external contract (`POST /api/payments/secure-fields` and `POST /api/payments/authorize`) is
**unchanged** — the frontend sees no difference. The migration is entirely backend-internal.

## Glossary

- **Secure Fields (v1)**: The current Datatrans hosted-iframe integration using
  `/v1/transactions/secureFields` for init and `/v1/transactions/{transactionId}/authorize` for
  authorization with numeric (int64) transaction identifiers.
- **Secure Fields (v2)**: The new Datatrans hosted-iframe integration using
  `/v2/transactions/secure-fields` for init and `/v2/transactions/{transactionId}/authorize` for
  authorization with UUID-format transaction identifiers.
- **DatatransSecureFieldsRequest**: The domain model representing the outbound request to
  initialise a Secure Fields transaction with Datatrans.
- **DatatransAuthorizeResponse**: The domain model representing the authorisation response from
  Datatrans, carrying `acquirerAuthorizationCode`, card details, and payment method.
- **DatatransCardInfo**: Card details record carrying `alias`, `masked`, `expiryMonth`,
  `expiryYear`.
- **PaymentAuthorisedEvent**: The Kafka event published after successful authorisation.
- **cardAlias**: The Datatrans-issued tokenised card identifier returned in the authorize
  response, published as part of `PaymentAuthorisedEvent`.
- **UUID transaction ID**: The v2 format for Datatrans transaction identifiers (e.g.
  `019ff5ab-577c-7c90-8b46-1b818a4f37a2`), replacing the v1 numeric format (e.g.
  `260812131115215413`).

## Requirements

### Requirement 1: Secure Fields Initialisation — Migrate to v2

**User Story:** As the Payment Orchestrator, I want to initialise Secure Fields transactions via
the Datatrans v2 API, so that both channels use a consistent API version and I benefit from v2
improvements (UUID identifiers, forward-compatible response shape).

#### Acceptance Criteria

1. THE `DatatransOutPort.initSecureFields` operation SHALL call
   `POST /v2/transactions/secure-fields` instead of `POST /v1/transactions/secureFields`.
2. THE request body SHALL include: `amount` (int64, minor units), `currency` (3-char ISO 4217),
   `returnUrl` (string, 3DS redirect URL), and `returnMethod` (string, default `"POST"`).
3. THE request body SHALL NOT include `autoSettle` — the v2 Secure Fields init does not accept
   it (deferred settlement is the default behaviour).
4. THE response SHALL be parsed to extract `transactionId` (UUID string format).
5. THE adapter SHALL authenticate with HTTP Basic Auth using the configured merchant identifier
   and merchant password (same credential mechanism as v1).
6. IF Datatrans returns a non-2xx status, THE adapter SHALL throw `DatatransGatewayException`
   with the HTTP status code.
7. IF Datatrans returns an empty body or a null/blank `transactionId`, THE adapter SHALL throw
   `DatatransGatewayException`.
8. IF Datatrans is unreachable or the read timeout elapses, THE adapter SHALL throw
   `ServiceUnavailableException`.
9. THE `DatatransSecureFieldsRequest` domain model SHALL be updated to carry all fields required
   by the v2 request body.

### Requirement 2: Secure Fields Authorisation — Migrate to v2

**User Story:** As the Payment Orchestrator, I want to authorise authenticated Secure Fields
transactions via the Datatrans v2 API, so that the authorize call matches the v2 transaction
lifecycle.

#### Acceptance Criteria

1. THE `DatatransOutPort.authorizeTransaction` operation SHALL call
   `POST /v2/transactions/{transactionId}/authorize` instead of
   `POST /v1/transactions/{transactionId}/authorize` when invoked from the Secure Fields flow.
2. THE `transactionId` path parameter SHALL be a UUID string (matching the v2 init response).
3. THE request body SHALL include `refno` (string) and `amount` (int64, minor units).
4. THE response SHALL be parsed to extract: `acquirerAuthorizationCode` (string),
   `transactionId` (UUID string — returned at top level in v2 authorize response).
5. THE `DatatransAuthorizeResponse` SHALL continue to carry `card.alias`, `card.masked`,
   `card.expiryMonth`, `card.expiryYear`, and `paymentMethod` — the v2 authorize response
   is consistent with v1 on these fields for the "authorize an authenticated transaction"
   endpoint.
6. IF Datatrans returns HTTP 401 or 403, THE adapter SHALL throw
   `AuthorizationDeclinedException`.
7. IF Datatrans returns HTTP 404, THE adapter SHALL throw `TransactionNotFoundException`.
8. IF Datatrans returns HTTP 409 (invalid transaction status), THE adapter SHALL throw
   `ThreeDsAuthenticationFailedException` (the transaction was not properly authenticated).
9. IF Datatrans returns any other error status, THE adapter SHALL throw
   `DatatransGatewayException`.
10. IF Datatrans is unreachable, THE adapter SHALL throw `ServiceUnavailableException`.

### Requirement 3: Secure Fields Settlement — Migrate to v2

**User Story:** As the downstream booking-confirmation choreography, I want settlement to use the
v2 API, so that the full transaction lifecycle operates on the same API version.

#### Acceptance Criteria

1. THE Secure Fields settlement path SHALL call
   `POST /v2/transactions/{transactionId}/settle` instead of
   `POST /v1/transactions/{transactionId}/settle`.
2. THE `transactionId` path parameter SHALL be a UUID string.
3. THE request body SHALL include `amount` (int64), `currency` (3-char ISO 4217), and
   `refno` (string).
4. THE response for a successful settlement SHALL be HTTP 204 (no body), consistent with v2.
5. IF Datatrans returns any error status, THE adapter SHALL throw
   `DatatransGatewayException`.
6. IF Datatrans is unreachable, THE adapter SHALL throw `ServiceUnavailableException`.

### Requirement 4: Status Check — Use v2 for Secure Fields

**User Story:** As the Payment Orchestrator, I want the Secure Fields transaction status check to
use the v2 API, so that the reconciliation/backup path is consistent with the init and authorize
calls.

#### Acceptance Criteria

1. WHEN retrieving status for a Secure Fields transaction, THE adapter SHALL call
   `GET /v2/transactions/{transactionId}` (UUID path parameter).
2. THE response SHALL be parsed to extract: `transactionId`, `status`, `currency`,
   `authorizedAmount`, `paymentMethod`, and card details from the `card` object (if present)
   or the first `attempts` entry.
3. IF Datatrans returns HTTP 404, THE adapter SHALL throw `TransactionNotFoundException`.
4. IF the response body is absent or `status` is blank, THE adapter SHALL throw
   `DatatransGatewayException`.
5. THE existing `DatatransTransactionStatus` domain record SHALL continue to be the return type
   — the v2 status response maps to the same domain fields.

### Requirement 5: PaymentAuthorisedEvent — Confirm cardAlias Continuity

**User Story:** As the Basket Service consumer, I want the `PaymentAuthorisedEvent` to continue
carrying `cardAlias`, `last4Digits`, `expiry`, and `paymentMethod` after the v2 migration, so
that downstream booking confirmation is unaffected.

#### Acceptance Criteria

1. THE `PaymentAuthorisedEvent` schema SHALL remain unchanged — no fields added or removed.
2. THE `cardAlias` field SHALL be populated from the v2 authorize response's `card.alias` value
   (present when `option.createAlias` was set during init, or when the card was tokenised).
3. THE `last4Digits` field SHALL be derived from the v2 authorize response's `card.masked`
   value (last 4 characters of the masked string).
4. THE `expiry` field SHALL be formatted as `"MM/YY"` from `card.expiryMonth` and
   `card.expiryYear` in the v2 authorize response.
5. THE `paymentMethod` field SHALL be populated from the top-level `paymentMethod` field in
   the v2 authorize response (or from the first `attempts` entry if not at top level — confirm
   v2 response shape).
6. IF `card` is absent in the v2 authorize response (edge case — wallet-based auth without
   card details), THE nullable fields `cardAlias`, `last4Digits`, and `expiry` SHALL be null.

### Requirement 6: Backward Compatibility — External API Unchanged

**User Story:** As the frontend team, I want the `POST /api/payments/secure-fields` and
`POST /api/payments/authorize` contracts to remain identical, so that no frontend changes are
needed for this backend migration.

#### Acceptance Criteria

1. THE `POST /api/payments/secure-fields` request and response contracts SHALL remain unchanged.
2. THE `POST /api/payments/authorize` request and response contracts SHALL remain unchanged.
3. THE `transactionId` returned by `POST /api/payments/secure-fields` SHALL now be a UUID string
   (v2 format) instead of a numeric string (v1 format). The frontend passes this to the
   Secure Fields JS library `init()` call — Datatrans JS library accepts both formats.
4. THE HTTP status codes and error response structure SHALL remain unchanged.
5. NO new request parameters or headers SHALL be required from the frontend.

### Requirement 7: Remove v1 Secure Fields Code Paths

**User Story:** As a developer, I want v1 code paths removed and replaced with v2, so that the
codebase does not carry dead code.

#### Acceptance Criteria

1. THE v1 `initSecureFields` implementation in `DatatransRestAdapter` (calling
   `POST /v1/transactions/secureFields`) SHALL be replaced in-place with the v2 implementation
   (calling `POST /v2/transactions/secure-fields`).
2. THE v1 `authorizeTransaction` implementation (calling
   `POST /v1/transactions/{transactionId}/authorize`) SHALL be replaced in-place with the v2
   implementation (calling `POST /v2/transactions/{transactionId}/authorize`).
3. THE v1 `settleTransaction` method SHALL be removed — the existing `settleTransactionV2`
   method SHALL be used directly (or renamed to `settleTransaction`).
4. NO Temporal `Workflow.getVersion` marker is required — the service is not in production and
   there are no in-flight workflow histories to protect.
5. THE `DatatransOutPort` interface methods SHALL be updated in-place to reflect v2 semantics.
   No versioned method pairs (v1 + v2 side-by-side) are needed.

### Requirement 8: Testing and Coverage

**User Story:** As a developer, I want the v2 migration covered by automated tests at ≥80%
coverage, following the same test patterns used in the existing codebase (unit tests, workflow
tests, adapter MockWebServer tests).

#### Acceptance Criteria

1. THE adapter tests SHALL verify the correct v2 URI paths, request bodies, Basic Auth, and
   error mapping using MockWebServer.
2. THE workflow tests SHALL verify that executions call v2 activities and that card data
   (cardAlias, last4Digits, expiry, paymentMethod) flows through to the
   `PaymentAuthorisedEvent`.
3. THE build SHALL report JaCoCo line coverage ≥ 80% for each new or modified non-excluded
   class.
4. THE build SHALL pass `checkstyle:check` with zero warnings.
5. Integration tests and property-based tests are NOT required for this ticket.

## Assumptions

- A1. CTECH-11615 is merged — the `PaymentAuthorisedEvent` publication, webhook status validation,
  and the extended activity signature are already in place.
- A2. The Datatrans Secure Fields JS library (loaded by the frontend) is compatible with UUID-format
  transaction IDs returned by v2 init — the library's `init(transactionId, {...})` call works with
  both v1 numeric and v2 UUID formats.
- A3. The v2 "authorize an authenticated transaction" endpoint returns `card` object with the same
  structure as v1 (`alias`, `masked`, `expiryMonth`, `expiryYear`).
- A4. The `refno` field is required in the v2 authorize request body (same as v1).
- A5. The existing `settleTransactionV2` method in `DatatransRestAdapter` already calls
  `POST /v2/transactions/{transactionId}/settle` — this work wires the Secure Fields flow to use
  it instead of `settleTransaction` (v1).
- A6. The service is NOT in production — there are no in-flight workflow histories. Temporal
  versioning markers are not required; v1 code can be replaced directly.

## Open Questions

- OQ-1. **v2 authorize response — card object presence.** Confirm that the v2 authorize response
  for an authenticated Secure Fields transaction returns the `card` object at the top level (same
  as v1). The v2 docs show `card.masked` in the authorize response example — confirm `card.alias`,
  `card.expiryMonth`, `card.expiryYear` are also present.
