# Payment Orchestration Service

Coordinates card payment flows for the new payment journey, serving both web (Secure Fields) and
mobile (Mobile SDK v4) channels through a unified polymorphic API. Manages transactions through
Datatrans, orchestrates payment workflows using Temporal, and integrates with Basket and OPERA.

## API Endpoints

| Method | Path                                | Description                          |
|--------|-------------------------------------|--------------------------------------|
| POST   | `/api/payments/init`                | Initialize payment (polymorphic)     |
| POST   | `/api/payments/authorize`           | Authorize payment (synchronous)      |
| POST   | `/api/payments/webhooks/datatrans`  | Datatrans webhook callback           |
| GET    | `/payment-orchestrator/actuator/health` | Health check                     |

OpenAPI documentation is available at `/swagger-ui.html` when the service is running.

## Client Integration Guide

### Unified Payment Initialization

All payment methods are initialized through a single endpoint using Jackson polymorphic
discrimination on the `paymentMethod` field:

**Web (Secure Fields):**

```bash
curl -X POST http://localhost:9200/api/payments/init \
  -H "Content-Type: application/json" \
  -d '{
    "paymentMethod": "NEW_CARD_WEB",
    "basketId": "AQN-147756bb-bb71-4842-959a-2efe87e378ed",
    "returnUrl": "https://www.premierinn.com/payments/3ds-return",
    "country": "gb",
    "language": "en",
    "userType": "LEISURE",
    "clientChannel": "PI"
  }'
```

**Mobile (Mobile SDK):**

```bash
curl -X POST http://localhost:9200/api/payments/init \
  -H "Content-Type: application/json" \
  -d '{
    "paymentMethod": "NEW_CARD_MOBILE",
    "basketId": "AQN-147756bb-bb71-4842-959a-2efe87e378ed",
    "country": "gb",
    "language": "en",
    "userType": "LEISURE",
    "clientChannel": "APPS_IOS"
  }'
```

**Response (201 Created):**

```json
{
  "paymentMethod": "NEW_CARD_WEB",
  "transactionId": "190410112056083383",
  "methodConfig": null
}
```

### returnUrl Security Requirements

For `NEW_CARD_WEB` requests, the `returnUrl` is validated against security rules:

1. **HTTPS only** — HTTP URLs are rejected with a 400 error.
2. **Host allowlist** — The URL host must appear in the configured allowlist.

Allowed hosts are configured via `payment.security.allowed-return-url-hosts`:

```yaml
payment:
  security:
    allowed-return-url-hosts:
      - "premierinn.com"
      - "www.premierinn.com"
      - "premierinn.digital"
```

Requests with an invalid returnUrl receive a `400 Bad Request` with error code
`INVALID_REQUEST` and the message `returnUrl must use HTTPS and belong to an allowed host`.

### Synchronous Authorization

Authorization uses Temporal's `@UpdateMethod` for immediate synchronous responses.
No polling is required — the result is returned directly from the workflow:

```bash
curl -X POST http://localhost:9200/api/payments/authorize \
  -H "Content-Type: application/json" \
  -d '{"basketId": "AQN-147756bb-bb71-4842-959a-2efe87e378ed"}'
```

**Success response (200 OK):**

```json
{
  "success": true,
  "errorCode": null,
  "errorMessage": null
}
```

**Pre-condition failure (thrown as PaymentInitializationException):**

The workflow validates three pre-conditions before proceeding:
- `transactionId` must be set (payment must be initialized)
- `authorizationInProgress` must be false
- `paymentStatus` must be `INITIALIZED`

`NEW_CARD_MOBILE` payments do **not** call this endpoint. Their authorization is driven by the
Datatrans webhook, with a reconciliation polling loop
(`integrations.datatrans.reconciliation.*`) as the fallback when no webhook arrives.

See [payment-init-update-with-start-flow.md](payment-init-update-with-start-flow.md) for the
full Temporal Update-With-Start flow, the re-initialization policy matrix, and workflow expiry.

### Webhook Endpoint

The Datatrans webhook endpoint is gateway-scoped at `/api/payments/webhooks/datatrans`
(not method-scoped). It serves both web and mobile payment callbacks:

- HMAC signature validation via the `Datatrans-Signature` header
- Transaction correlation via `transactionId` in the payload (not basketId alone)
- Invalid/mismatched payloads are logged and dropped at the workflow signal level
- Always returns 200 OK (Datatrans does not retry on non-2xx)

### Error Response Format

All errors use a consistent structure with typed `PaymentErrorCode` values:

```json
{
  "error": {
    "code": "BASKET_NOT_FOUND",
    "message": "No basket found for the given basketId"
  }
}
```

Request-shape failures caught by Bean Validation before the workflow is reached (a missing
field, a `returnUrl` that is not HTTPS or whose host is not allowlisted, an unreadable body, or
an unknown `paymentMethod` discriminator) return `400` with code `INVALID_REQUEST`. The codes
below are the typed `PaymentErrorCode` values the workflow itself produces.

**Error codes and their HTTP status mappings:**

| PaymentErrorCode               | HTTP Status | Description                                      |
|-------------------------------|-------------|--------------------------------------------------|
| `BASKET_NOT_FOUND`            | 404         | Basket does not exist                            |
| `PAYMENT_METHOD_NOT_AVAILABLE`| 422         | Card payment not available for this hotel        |
| `TRANSACTION_ALREADY_AUTHORIZED` | 409      | Transaction already authorized                   |
| `BOOKING_ALREADY_PAID`        | 409         | Booking already paid                             |
| `GATEWAY_ERROR`               | 502         | Datatrans gateway error                          |
| `GATEWAY_AUTHENTICATION_FAILED` | 502       | Datatrans rejected **our** merchant credentials or permissions (401/403). An operational fault on our side — never a card decline, and never surfaced to clients as 401/403 |
| `VALIDATION_FAILED`           | 400         | Request validation failed                        |
| `AUTHORIZATION_IN_PROGRESS`   | 409         | Authorization already in flight                  |
| `TRANSACTION_EXPIRED`         | 410         | Transaction has expired                          |
| `INVALID_TRANSACTION_STATE`   | 409         | Payment not in valid state for operation         |
| `TRANSACTION_MISMATCH`        | 409         | Gateway transaction identifiers or authorized amount do not match this payment |
| `EXPIRED`                     | 410         | Payment session has expired                      |

### Payment statuses

The workflow's `paymentStatus` is not part of the HTTP contract — clients see typed error codes,
not statuses — but it is what queries and operational dashboards read, and three of these states
exist purely so a human can tell one kind of stuck payment from another.

| PaymentStatus             | Terminal | Re-init | Meaning                                                                                       |
|---------------------------|----------|---------|-----------------------------------------------------------------------------------------------|
| `INITIALIZED`             | no       | allowed | A Datatrans transaction exists; nothing has been charged                                        |
| `AUTHORIZED`              | yes      | rejected| Funds held; awaiting the booking outcome                                                         |
| `SETTLEMENT_PENDING`      | no       | rejected| Booking confirmed; the capture is being retried (up to 72h)                                     |
| `SETTLED`                 | yes      | rejected| Money captured                                                                                  |
| `SETTLEMENT_FAILED`       | yes      | rejected| Booking exists, capture exhausted its horizon — **settle manually in Datatrans**; auth NOT voided |
| `BOOKING_PENDING_TIMEOUT` | yes      | rejected| Authorized, booking outcome never resolved — **reconcile manually**; auth NOT voided             |
| `FAILED`                  | yes      | allowed | The attempt failed and nothing is held; the customer can pay again                              |
| `CANCELLED`               | yes      | rejected| The authorization was voided (booking failed, or basket cancelled)                              |
| `EXPIRED`                 | yes      | rejected| The basket was abandoned before authorization and the transaction was cancelled                 |

Both manual-intervention states log at `ERROR` with the transaction id, booking reference,
amount, and merchant id — grep for `MANUAL SETTLEMENT REQUIRED` and
`MANUAL RECONCILIATION REQUIRED`.

### What happens after authorization

Cancelling an authorization is safe only while no booking exists. Because the
`PaymentAuthorisedEvent` is what triggers the booking, that single fact decides the whole
post-authorization design:

- **Before the event is published** — nothing downstream exists. If the publish cannot be
  delivered within its horizon (default 3 minutes; the customer is watching a pending screen),
  the workflow cancels the authorization and sets `FAILED`, which the customer can retry. The
  event is delivered **at least once**: a publish that reached Kafka but lost its
  acknowledgement is retried, so consumers must dedupe on `basketId` / `transactionId`.
- **After the event is published** — a booking may exist, so nothing in the workflow cancels the
  authorization again, and the expiry timer no longer applies. The workflow waits for the
  `bookingCompleted` Kafka signal, and polls `GET /v1/baskets/{basket-reference}` on each
  interval that passes without one (default every 60s, for 45 minutes). A basket that reads
  `COMPLETED` settles; one that reads `FAILED`/`CANCELLED` cancels; one still pending at the end
  of the horizon parks as `BOOKING_PENDING_TIMEOUT`, because cancelling a booking that may still
  complete gives away a free stay and settling one that may never happen charges for nothing.
- **Settlement itself** retries for up to 72 hours (exponential backoff from 5s, capped at
  15 minutes, 30s per attempt). A gateway outage of several hours is survived without a human.
  Exhaustion or a non-retryable failure lands in `SETTLEMENT_FAILED` with the authorization
  intact. If ops settles the transaction by hand, no work is lost: the settle converges, so any
  later attempt reports success rather than capturing twice.

### Workflow configuration

| Property (`integrations.payment.workflow.*`) | Default | What it bounds                                    |
|----------------------------------------------|---------|---------------------------------------------------|
| `timeout`                                    | `30m`   | Pre-authorization expiry (abandoned basket)        |
| `settlement.retry-horizon`                   | `72h`   | Total settlement retry window                      |
| `settlement.backoff-cap`                     | `15m`   | Maximum gap between settlement attempts            |
| `settlement.attempt-timeout`                 | `30s`   | One settle call                                    |
| `publish.retry-horizon`                      | `3m`    | Total authorised-event publish window              |
| `booking-poll.interval`                      | `60s`   | Wait before asking the Basket Service              |
| `booking-poll.horizon`                       | `45m`   | Total booking-completion reconciliation window     |

All of these reach the workflow as start memos rather than being read inside it — a value that
could change between a run and its replay would break determinism. Workflows started before a
knob existed read `null` and fall back to the defaults above.

### Retry and idempotency for money-moving calls

Temporal retries `authorizeTransaction` and `settleTransaction`, because the alternative —
giving up on the first ambiguous failure — leaves a guest either uncharged for a booking or
holding money nobody captures. Authorize gets three attempts (a customer is waiting on the
response); settle gets a 72-hour horizon (the booking already exists, so there is nobody to
disappoint by taking longer and everything to lose by giving up). Retrying is safe because both
calls converge rather than repeat:

- Datatrans answers a repeated authorize with `409`, the same status it uses for a genuine
  3-D Secure failure. The adapter therefore never reads `409` as a verdict: it fetches the
  transaction status and reports success only when Datatrans confirms the transaction is
  authorized (or settled) for this exact `transactionId`, `refno`, and amount, rebuilding the
  result a first-time success would have returned. If the status says nothing was authorized,
  the `409` really was a 3-D Secure failure. If the status call itself fails, the failure stays
  retryable — an ambiguous outcome must never be turned into a terminal payment failure.
- A repeated settle is resolved the same way against a confirmed `settled` status.
- Failures that are decisions rather than accidents — a decline, a 3-D Secure failure, an
  unknown transaction, or a confirmed transaction that does not match this payment — are on the
  activity `doNotRetry` list and fail on the first attempt.
- Rejected merchant credentials (`401`/`403` from Datatrans) are on that list too, for a
  different reason. Retrying is harmless — a `401` moves no money — but futile, since all three
  attempts carry the same wrong password. Failing fast gets the error to the customer sooner
  and leaves one `ERROR` log per attempt rather than three.

### Datatrans 401/403 is a merchant-credentials fault, not a decline

Every Datatrans call authenticates with the merchant id and the configured merchant password,
so a `401` (bad or expired credentials) or `403` (merchant not permitted) is a fault in **our**
configuration. No card was presented to an issuer and no money moved. All calls — authorize,
settle, cancel, both inits, and the status endpoint — map it to
`DatatransAuthenticationException`, log it at `ERROR` ("Datatrans rejected merchant
credentials", with the status, merchant id, and the gateway's error body; never the password),
and answer clients with `502 GATEWAY_AUTHENTICATION_FAILED`. It is deliberately not a `401` to
our clients — that would claim *their* authentication failed and send them refreshing tokens —
and the customer-facing message mentions neither the card nor the issuer.

### Migration from Old Endpoints

The following endpoints have been removed and replaced by the unified API:

| Old Endpoint                            | Replacement                     |
|-----------------------------------------|---------------------------------|
| `POST /api/payments/secure-fields`      | `POST /api/payments/init` with `paymentMethod: "NEW_CARD_WEB"` |
| `POST /api/payments/mobile-sdk`         | `POST /api/payments/init` with `paymentMethod: "NEW_CARD_MOBILE"` |
| `POST /api/payments/webhooks/mobile-sdk` | `POST /api/payments/webhooks/datatrans` |

The authorize endpoint (`POST /api/payments/authorize`) is unchanged but now returns
synchronous results directly instead of requiring signal+polling.

## Local Development

### Prerequisites

- Java 25
- Maven Wrapper (included — do not use a locally installed Maven)
- Docker & Docker Compose (for Temporal)

### Build

```bash
cd backend
./mvnw clean install -pl book-pay/services/payment-orchestration-service -am
```

### Run

1. Start Temporal via Docker Compose:

```bash
cd backend/book-pay/services/payment-orchestration-service/infrastructure/docker-local
docker compose up -d
```

2. Start the service:

```bash
cd backend
./mvnw spring-boot:run -pl book-pay/services/payment-orchestration-service \
  -Dspring.profiles.active=local
```

The service starts on **port 9200**.

### Temporal UI

Available at http://localhost:8080 when docker-compose is running.

### Skip Tests

```bash
cd backend
./mvnw clean install -pl book-pay/services/payment-orchestration-service -am -DskipTests
```

## Testing

```bash
cd backend
./mvnw test -pl book-pay/services/payment-orchestration-service
```

Test structure:

```
src/test/java/
└── uk/co/whitbread/payment/orchestrator/
    ├── domain/logic/            # Unit tests (strategies, error mapping, validators)
    ├── domain/workflow/         # Temporal workflow tests (TestWorkflowEnvironment)
    └── infrastructure/rest/    # Controller + adapter tests
```

Coverage is measured with **JaCoCo**; mutation testing uses **PIT**.

## Architecture
The service follows **hexagonal architecture** (ports & adapters):

```
uk.co.whitbread.payment.orchestrator
├── domain/                  # Core business logic
│   ├── exceptions/          # Domain exceptions
│   ├── logic/               # Service implementations (AmountCalculator, strategies)
│   ├── model/               # Domain models, DTOs, PaymentErrorCode
│   ├── ports/
│   │   ├── primary/         # Inbound ports (PaymentOrchestrationInPort, WebhookInPort)
│   │   └── secondary/       # Outbound ports (DatatransOutPort, BasketOutPort, etc.)
│   └── workflow/            # Temporal workflow + activity interfaces
│       └── PaymentWorkflow  # Unified workflow (strategies: NewCardWeb, NewCardMobile)
└── infrastructure/          # Driven side (outbound)
    ├── config/              # Spring configuration (Temporal, WebClient, properties)
    ├── kafka/               # Kafka producer/consumer configuration
    ├── rest/
    │   ├── client/          # REST adapters (Datatrans, Basket, PaymentMethod)
    │   └── controller/      # REST controllers (PaymentController, WebhookController)
    └── temporal/            # Temporal workflow adapter (TemporalWorkflowAdapter)
```

- **Domain layer** — Business logic and port interfaces. `domain/logic`, `domain/exceptions`,
  and the core models are framework-free and wired via `InfrastructureBeanConfig`.
- **Infrastructure layer** — REST controllers, external service clients, Temporal adapter.

### Architecture stance — deliberate deviations from strict hexagonal

Two framework dependencies inside `domain/` are **by design**, not oversights:

- **`domain/workflow` is Temporal-native.** Workflow and strategy classes import
  `io.temporal.*` directly. Orchestration code *is* Temporal code — `Workflow.await`,
  activity stubs, and `ApplicationFailure` handling are the mechanism, not incidental
  framework leakage. Hiding the SDK behind an abstraction would add indirection without
  making the orchestration portable (swapping Temporal would mean rewriting this layer
  regardless).
- **Jackson annotations on workflow-crossing types are load-bearing.** Types that travel
  through Temporal payloads (`PaymentInitCommand` hierarchy, `WebhookPayload`,
  `BookingCompletedEvent`, `AuthorizeResult`) carry `@JsonTypeInfo`/Jackson annotations
  because Temporal's data converter is Jackson; the polymorphic type info is required for
  update-with-start to deserialize the correct command subtype. Removing them breaks the
  workflow.

Known deviation that is *not* deliberate: the REST request/response DTOs under
`domain/model/payment` (`PaymentInitRequest` subtypes, `PaymentInitResponse`, and the
bean-validation machinery) are web-layer artifacts that belong in `infrastructure/rest`
and are pending relocation.
- **Strategy pattern** — `NewCardWebStrategy` and `NewCardMobileStrategy` encapsulate
  method-specific initialization and authorization logic.
- **Event-inbox pattern** — Signal handlers deposit validated events into workflow state;
  strategies consume via `Workflow.await(predicate)`.

## Integration Points

| System    | Purpose                                      | Adapter                    |
|-----------|----------------------------------------------|----------------------------|
| Datatrans | Payment gateway (Secure Fields & Mobile SDK) | `DatatransRestAdapter`     |
| Basket    | Reservation/basket state for bookings        | `ReservationServiceClient` |
| OPERA     | Deposit and folio posting                    | Not yet implemented        |
| Temporal  | Workflow orchestration and retry management  | `TemporalWorkflowAdapter`  |
| Kafka     | Event publishing (PaymentAuthorisedEvent)    | `KafkaProducerConfig`      |

## Configuration 

Every host and base URL is overridable by environment variable; the table gives the
checked-in default that applies when the variable is unset.

| Property                                        | Env var                     | Default                              | Description                           |
|-------------------------------------------------|-----------------------------|--------------------------------------|---------------------------------------|
| `server.port`                                   | —                           | `9200`                               | HTTP listen port                      |
| `temporal.server.host`                          | `TEMPORAL_HOST`             | `localhost`                          | Temporal gRPC host                    |
| `temporal.server.port`                          | `TEMPORAL_PORT`             | `7233`                               | Temporal gRPC port                    |
| `temporal.namespace`                            | `TEMPORAL_NAMESSPACE`       | `default`                            | Temporal namespace the client and worker use |
| `temporal.task-queue`                           | —                           | `payment-workflows`                  | Temporal task queue name              |
| `integrations.payment.workflow.timeout`         | `PAYMENT_WORKFLOW_TIMEOUT`  | `30m`                                | Workflow expiry timeout               |
| `integrations.datatrans.base-url`               | `DATATRANS_BASE_URL`        | `https://api.sandbox.datatrans.com`  | Datatrans API base URL (**required, non-blank**) |
| `integrations.datatrans.merchant-password`      | `DATATRANS_MERCHANT_PASSWORD` | (none)                             | Datatrans HTTP Basic password (**required, non-blank**) |
| `integrations.datatrans.webhook.callback-base-url` | `DATATRANS_WEBHOOK_BASE_URL` | (none)                           | Public base URL for webhook callbacks |
| `integrations.datatrans.webhook.hmac-key`       | `DATATRANS_WEBHOOK_HMAC_KEY` | (none)                              | HMAC signing key for signature verify |
| `integrations.datatrans.reconciliation.initial-delay` | `DATATRANS_RECONCILIATION_INITIAL_DELAY` | `2m`          | Mobile SDK reconciliation start delay |
| `integrations.datatrans.reconciliation.poll-interval` | `DATATRANS_RECONCILIATION_POLL_INTERVAL` | `30s`         | Reconciliation polling interval       |
| `integrations.datatrans.reconciliation.max-duration`  | `DATATRANS_RECONCILIATION_MAX_DURATION` | `30m`          | Maximum reconciliation duration       |
| `integrations.datatrans.reconciliation.enabled` | `DATATRANS_RECONCILIATION_ENABLED` | `true`                        | Enables Mobile SDK reconciliation polling |
| `integrations.reservation.host`                 | `RESERVATION_HOST`          | `http://localhost:9103`              | Hotel Reservation Entity Service base URL (**required, non-blank**) |
| `integrations.payment-method-service.host`      | `PAYMENT_METHOD_SERVICE_HOST` | `http://localhost:9107`            | Payment Method Entity Service base URL (**required, non-blank**) |
| `integrations.basket.host`                      | `BASKET_SERVICE_HOST`       | `http://localhost:9104`              | Basket Service base URL (**required, non-blank**) |
| `payment.security.allowed-return-url-hosts`     | —                           | premierinn.com, www.premierinn.com, premierinn.digital | Allowed returnUrl hosts |

Properties marked **required, non-blank** are validated at startup with `@Validated` /
`@NotBlank`. The checked-in default for `merchant-password` is the empty string, so a deployed
environment that forgets `DATATRANS_MERCHANT_PASSWORD` now fails to start rather than booting
and failing on the first live payment. The `local`, `test`, and `integration` profiles supply
placeholder values.

### CORS

Browser clients are matched against `*.premierinn.digital`, `*.premierinn.com`,
`http://localhost:[*]` and `https://localhost:[*]`. The localhost patterns are anchored on
purpose — a substring pattern such as `*localhost*` also matches
`https://evil-localhost.attacker.com`.

### Spring Profiles

| Profile       | Purpose                                          |
|---------------|--------------------------------------------------|
| `local`       | Local development (Temporal + Datatrans sandbox) |
| `integration` | OpenAPI generation (no Temporal required)         |
| `opera-dev`   | Development environment endpoints                |
| `opera-qa`    | QA environment endpoints                         |
| `opera-perf`  | Performance testing environment                  |

## Project Folder Structure

```
payment-orchestration-service/
├── docs/
│   └── openApi/                # Static OpenAPI specification
├── infrastructure/
│   └── docker-local/           # Docker Compose (Temporal + PostgreSQL + UI)
├── src/
│   ├── main/
│   │   ├── java/               # Application source
│   │   └── resources/          # Configuration (application*.yml)
│   └── test/
│       ├── java/               # Unit, property-based, and workflow tests
│       └── resources/          # Test configuration + replay histories
├── google-checkstyle.xml       # Checkstyle rules
├── pom.xml                     # Maven project descriptor
└── README.md
```

## Technologies

- Java 25
- Spring Boot 4.0.7 / Spring Cloud 2025.1.1
- Temporal Java SDK 1.35.0
- Maven Wrapper
- Docker
- Micrometer + Prometheus (metrics)
- Brave (distributed tracing)
- SpringDoc OpenAPI
- Lombok / MapStruct
- Jackson 3 (polymorphic type handling)
