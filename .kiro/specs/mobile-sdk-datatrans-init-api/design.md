# Design Document: Mobile SDK Datatrans Init API

## Overview

This design implements the `POST /api/payments/mobile-sdk` endpoint in the Payment Orchestration Service. The endpoint accepts a basket ID from the native iOS/Android app, orchestrates validation via a Temporal workflow, calls the Datatrans v2 API (`POST /v2/transactions`) to create a payment session, and returns the resulting transaction ID (UUID) to the mobile app.

The implementation follows the established hexagonal architecture pattern, extending existing ports and adapters with Mobile SDK–specific logic. Basket and Payment Method services are stubbed in this phase; only the Datatrans v2 integration is real.

### Key Design Decisions

1. **Fully reactive controller** — returns `Mono<ResponseEntity>` to avoid blocking the WebFlux event loop.
2. **Temporal + Reactive bridge** — uses `Mono.fromCallable(...).subscribeOn(Schedulers.boundedElastic())` to offload the blocking Temporal SDK call to an elastic thread pool.
3. **Datatrans v2 reactive WebClient call** — no `.block()`. The adapter returns `Mono<String>` (transaction ID).
4. **Minimal Datatrans request** — sends only `amount`, `currency`, `refno`, `paymentMethods`. No `autoSettle`, `createAlias`, or redirect URLs.
5. **New domain models** — `DatatransMobileSdkRequest` and `DatatransMobileSdkResponse` records for the v2 contract.
6. **Workflow activities as stubs** — Basket and PaymentMethod activities return hardcoded data.

---

## Architecture

```mermaid
flowchart TD
    subgraph "Mobile App"
        A[iOS/Android]
    end

    subgraph "Payment Orchestrator (WebFlux)"
        B[PaymentController]
        C[PaymentOrchestrationInPortImpl]
        D[MobileSdkWorkflowPort]
    end

    subgraph "Temporal"
        E[MobileSdkPaymentWorkflow]
        F[PaymentActivities]
    end

    subgraph "External"
        G[Datatrans v2 API]
    end

    subgraph "Stubs (this phase)"
        H[BasketOutPort stub]
        I[PaymentMethodOutPort stub]
    end

    A -->|POST /api/payments/mobile-sdk| B
    B -->|Mono.fromCallable + boundedElastic| C
    C --> D
    D -->|signal-with-start| E
    E -->|activity: getReservation| F
    F --> H
    E -->|activity: getPaymentMethods| F
    F --> I
    E -->|activity: initMobileSdk| F
    F -->|POST /v2/transactions (reactive)| G
    G -->|201 + transactionId| F
    F -->|result| E
    E -->|workflow result| D
    D -->|transactionId| C
    C -->|transactionId| B
    B -->|201 Created| A
```

### Request Flow

1. Mobile app sends `POST /api/payments/mobile-sdk` with `{"basketId": "..."}`.
2. `PaymentController` validates the request body, then wraps the domain call in `Mono.fromCallable` on `Schedulers.boundedElastic()`.
3. `PaymentOrchestrationInPortImpl.initMobileSdkPayment()` delegates to the `MobileSdkWorkflowPort`.
4. `MobileSdkWorkflowPort` starts (or signals) the `MobileSdkPaymentWorkflow` via Temporal.
5. The Temporal workflow executes activities sequentially:
   - **getReservation** — stub returns hardcoded reservation.
   - **validatePaymentMethods** — stub returns `["VIS", "ECA"]`.
   - **initMobileSdkTransaction** — calls Datatrans v2 via reactive WebClient.
6. Workflow returns the transaction ID (or error code).
7. Response propagates back through the reactive chain as `201 Created`.

---

## Components and Interfaces

### New Domain Models

```java
// Request body sent to Datatrans POST /v2/transactions
public record DatatransMobileSdkRequest(
    long amount,          // Amount in minor units (e.g. 8600 = £86.00)
    String currency,      // ISO 4217 (e.g. "GBP")
    String refno,         // Reference number (e.g. "PI-123456789")
    List<String> paymentMethods  // Card brand codes (e.g. ["VIS", "ECA"])
) {}

// Response parsed from Datatrans POST /v2/transactions
public record DatatransMobileSdkResponse(
    String transactionId  // UUID format transaction ID
) {}

// Result returned from the Temporal workflow
public record MobileSdkInitResult(
    boolean success,
    String transactionId,
    String errorCode,
    String errorMessage
) {}
```

### Modified Port: DatatransOutPort

Add a new method for Mobile SDK (v2) initialisation:

```java
public interface DatatransOutPort {
    // ... existing methods ...

    /**
     * Initialize a Mobile SDK transaction with Datatrans v2 API.
     *
     * @param request the v2 request containing amount, currency, refno, paymentMethods
     * @return Mono emitting the transaction ID (UUID string)
     * @throws DatatransGatewayException if Datatrans returns a non-2xx response
     * @throws ServiceUnavailableException if Datatrans is unreachable
     */
    Mono<String> initMobileSdk(DatatransMobileSdkRequest request);
}
```

### New Port: MobileSdkWorkflowPort

Extends `PaymentWorkflowPort` or stands as a new secondary port for Mobile SDK workflow interaction:

```java
public interface MobileSdkWorkflowPort {
    /**
     * Start the Mobile SDK payment workflow and wait for the init result.
     *
     * @param basketId the basket identifier (used as workflow ID)
     * @return the init result containing transactionId or error
     */
    MobileSdkInitResult initMobileSdkPayment(String basketId);
}
```

### Modified PaymentOrchestrationInPort

The existing `initMobileSdkPayment(String basketId)` method is already declared. The implementation changes from `throw new UnsupportedOperationException()` to delegating to `MobileSdkWorkflowPort`.

### Controller Changes

`PaymentController.initMobileSdk()` changes from returning `501 Not Implemented` to:

```java
@PostMapping(value = "/mobile-sdk", ...)
public Mono<ResponseEntity<MobileSdkInitResponse>> initMobileSdk(
    @Valid @RequestBody MobileSdkInitRequest request) {
    return Mono.fromCallable(() ->
            paymentOrchestrationInPort.initMobileSdkPayment(request.basketId()))
        .subscribeOn(Schedulers.boundedElastic())
        .map(transactionId -> ResponseEntity.status(HttpStatus.CREATED)
            .body(new MobileSdkInitResponse(transactionId)));
}
```

### New Response DTO

```java
public record MobileSdkInitResponse(String transactionId) {}
```

### DatatransRestAdapter Extension

New method in `DatatransRestAdapter`:

```java
@Override
public Mono<String> initMobileSdk(DatatransMobileSdkRequest request) {
    return datatransWebClient.post()
        .uri("/v2/transactions")
        .headers(h -> h.setBasicAuth(merchantId, properties.getMerchantPassword()))
        .bodyValue(Map.of(
            "amount", request.amount(),
            "currency", request.currency(),
            "refno", request.refno(),
            "paymentMethods", request.paymentMethods()
        ))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            Mono.error(new DatatransGatewayException(
                "Datatrans v2 returned " + response.statusCode())))
        .bodyToMono(DatatransMobileSdkResponse.class)
        .map(DatatransMobileSdkResponse::transactionId)
        .onErrorMap(WebClientRequestException.class, e ->
            new ServiceUnavailableException("Datatrans is unreachable: " + e.getMessage()));
}
```

### Temporal Workflow Implementation

`MobileSdkPaymentWorkflowImpl` orchestrates three activities:

```java
public class MobileSdkPaymentWorkflowImpl implements MobileSdkPaymentWorkflow {
    private final PaymentActivities activities = Workflow.newActivityStub(
        PaymentActivities.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .setRetryOptions(RetryOptions.newBuilder().setMaximumAttempts(3).build())
            .build());

    @Override
    public String processPayment(String basketId) {
        // 1. Get reservation (stub)
        Reservation reservation = activities.getReservation(basketId);

        // 2. Validate reservation state
        if (reservation.paymentCompleted()) {
            throw ApplicationFailure.newFailure("BOOKING_ALREADY_PAID", ...);
        }
        if (reservation.bookingReference() != null) {
            throw ApplicationFailure.newFailure("BOOKING_ALREADY_CONFIRMED", ...);
        }

        // 3. Get payment methods (stub)
        List<String> paymentMethods = activities.getPaymentMethods(reservation.hotelId());
        if (paymentMethods.isEmpty()) {
            throw ApplicationFailure.newFailure("PAYMENT_METHOD_NOT_AVAILABLE", ...);
        }

        // 4. Init Datatrans v2 transaction
        return activities.initMobileSdkTransaction(
            new DatatransMobileSdkRequest(
                reservation.baseAmount(),
                reservation.currencyCode(),
                "PI-" + basketId,
                paymentMethods
            ));
    }
}
```

### PaymentActivities Interface Extension

```java
public interface PaymentActivities {
    // ... existing activities ...

    @ActivityMethod
    Reservation getReservation(String basketId);

    @ActivityMethod
    List<String> getPaymentMethods(String hotelId);

    @ActivityMethod
    String initMobileSdkTransaction(DatatransMobileSdkRequest request);
}
```

### Temporal Workflow Adapter (MobileSdkWorkflowPort Implementation)

```java
@Component
@Profile("!integration")
public class MobileSdkWorkflowAdapter implements MobileSdkWorkflowPort {
    private final WorkflowClient workflowClient;
    private final String taskQueue;

    @Override
    public MobileSdkInitResult initMobileSdkPayment(String basketId) {
        String workflowId = "mobile-sdk-" + basketId;
        WorkflowOptions options = WorkflowOptions.newBuilder()
            .setWorkflowId(workflowId)
            .setTaskQueue(taskQueue)
            .setWorkflowExecutionTimeout(Duration.ofSeconds(30))
            .build();

        MobileSdkPaymentWorkflow workflow = workflowClient.newWorkflowStub(
            MobileSdkPaymentWorkflow.class, options);

        try {
            String transactionId = workflow.processPayment(basketId);
            return new MobileSdkInitResult(true, transactionId, null, null);
        } catch (WorkflowException e) {
            // Map Temporal ApplicationFailure to MobileSdkInitResult error
            return mapWorkflowError(e);
        }
    }
}
```

---

## Data Models

### Datatrans v2 Init Request (outbound)

| Field | Type | Description |
|-------|------|-------------|
| `amount` | `long` | Amount in minor currency units (e.g. 8600 for £86.00) |
| `currency` | `String` | ISO 4217 3-letter code (e.g. `"GBP"`) |
| `refno` | `String` | Reference number (format: `"PI-{basketId}"`) |
| `paymentMethods` | `List<String>` | Card brand codes (e.g. `["VIS", "ECA"]`) |

**Not included** (by design): `option.autoSettle`, `option.createAlias`, redirect URLs.

### Datatrans v2 Init Response (inbound)

| Field | Type | Description |
|-------|------|-------------|
| `transactionId` | `String` | UUID v4 transaction identifier |

The `transactionId` may also be extracted from the `Location` header of the 201 response.

### Reservation (from stub)

| Field | Type | Description |
|-------|------|-------------|
| `basketId` | `String` | Basket identifier |
| `hotelId` | `String` | Hotel identifier |
| `baseAmount` | `long` | Base amount in minor currency units |
| `currencyCode` | `String` | ISO 4217 currency code |
| `bookingReference` | `String` | Booking ref (null if not confirmed) |
| `paymentCompleted` | `boolean` | Whether payment is already done |
| `refno` | `String` | Reference number for Datatrans |

### Error Response

```json
{
  "error": {
    "code": "GATEWAY_ERROR",
    "message": "Payment gateway returned an unexpected error. Please try again."
  }
}
```

---

## Error Handling

| Scenario | Domain Exception | HTTP Status | Error Code |
|----------|-----------------|-------------|------------|
| Missing/blank basketId | Validation (Jakarta) | 400 | `INVALID_REQUEST` |
| Invalid JSON body | WebExchangeBindException | 400 | `INVALID_REQUEST` |
| Basket not found (future) | `BasketNotFoundException` | 404 | `BASKET_NOT_FOUND` |
| Payment already completed | `BookingAlreadyPaidException` | 409 | `BOOKING_ALREADY_PAID` |
| Booking already confirmed | `BookingAlreadyConfirmedException` | 409 | `BOOKING_ALREADY_CONFIRMED` |
| No card methods for hotel | `PaymentMethodNotAvailableException` | 422 | `PAYMENT_METHOD_NOT_AVAILABLE` |
| Datatrans 4xx/5xx response | `DatatransGatewayException` | 502 | `GATEWAY_ERROR` |
| Datatrans unreachable/timeout | `ServiceUnavailableException` | 503 | `SERVICE_UNAVAILABLE` |
| Temporal unreachable/timeout | `ServiceUnavailableException` | 503 | `SERVICE_UNAVAILABLE` |
| Unexpected error | `RuntimeException` | 503 | `SERVICE_UNAVAILABLE` |

### Error Propagation

Errors originating in Temporal activities are wrapped in `ApplicationFailure` with the error code as the failure type. The `MobileSdkWorkflowAdapter` maps these back to `MobileSdkInitResult` error codes. `PaymentOrchestrationInPortImpl` then throws the appropriate domain exception, which `GlobalExceptionHandler` maps to the HTTP response.

### Reactive Error Handling

The `Mono.fromCallable` in the controller naturally propagates exceptions. Domain exceptions thrown inside the callable are caught by Spring's reactive exception handling pipeline and routed to `GlobalExceptionHandler`.

---

## Testing Strategy

### Why Property-Based Testing Does NOT Apply

This feature consists of:
- REST controller wiring (request validation → delegation → response mapping)
- Temporal workflow orchestration (activity sequencing with hardcoded stub data)
- HTTP client call to Datatrans (single request/response)
- Error code mapping between layers

There are no pure functions with meaningful input variation, no parsers, no serializers, no algorithms. The "input space" is a single string (basketId). PBT would not find more bugs than example-based tests here.

### Unit Testing Strategy

| Layer | Tool | What's Tested |
|-------|------|---------------|
| Controller | WebTestClient | Request validation, HTTP status codes, response body shape, reactive chain |
| Domain logic | Mockito | Error mapping, delegation to workflow port, exception translation |
| Workflow | Temporal TestWorkflowEnvironment | Activity execution order, error handling, timeout behaviour |
| Datatrans adapter | MockWebServer (OkHttp) | Request body serialisation, auth header, error response handling |
| Exception handler | WebTestClient | All error scenarios produce correct HTTP status + error body |

### Test Scenarios

**Controller tests:**
- Valid request → 201 with transactionId
- Blank basketId → 400 INVALID_REQUEST
- Missing basketId field → 400 INVALID_REQUEST
- Invalid JSON → 400 INVALID_REQUEST
- Domain throws `DatatransGatewayException` → 502 GATEWAY_ERROR
- Domain throws `ServiceUnavailableException` → 503 SERVICE_UNAVAILABLE

**Domain logic tests:**
- Successful flow → returns transactionId
- Workflow returns error code `BOOKING_ALREADY_PAID` → throws `BookingAlreadyPaidException`
- Workflow returns error code `BOOKING_ALREADY_CONFIRMED` → throws `BookingAlreadyConfirmedException`
- Workflow returns error code `PAYMENT_METHOD_NOT_AVAILABLE` → throws `PaymentMethodNotAvailableException`
- Workflow returns error code `GATEWAY_ERROR` → throws `DatatransGatewayException`

**Temporal workflow tests (TestWorkflowEnvironment):**
- Happy path: stub activities return valid data → workflow returns transactionId
- Reservation has paymentCompleted=true → workflow fails with `BOOKING_ALREADY_PAID`
- Reservation has bookingReference set → workflow fails with `BOOKING_ALREADY_CONFIRMED`
- Payment methods empty → workflow fails with `PAYMENT_METHOD_NOT_AVAILABLE`
- Datatrans activity throws → workflow fails with `GATEWAY_ERROR`

**Datatrans adapter tests (MockWebServer):**
- Successful 201 response → returns Mono with transactionId
- 400 response → emits `DatatransGatewayException`
- 500 response → emits `DatatransGatewayException`
- Connection refused → emits `ServiceUnavailableException`
- Request body contains correct fields (amount, currency, refno, paymentMethods)
- Request has Basic Auth header with merchant credentials
