# An unknown basket returns HTTP 502 GATEWAY_ERROR instead of 404 BASKET_NOT_FOUND

- **Status**: reproduced 2026-08-25 against the running integration stack by
  `InitSecureFieldsSpec` (scenario "an unknown basket returns basket not found without
  initializing payment", which ships disabled on this file). Observed response: HTTP 502
  `{"error":{"code":"GATEWAY_ERROR","message":"Activity with activityType='GetReservation'
  failed: 'Activity task failed'. ... retryState=RETRY_STATE_MAXIMUM_ATTEMPTS_REACHED"}}`.
- **Endpoint**: `POST /api/payments/secure-fields` (payment-orchestration-service)
- **Chain**: `PaymentController.initSecureFields` -> `PaymentOrchestrationInPortImpl` ->
  Temporal `SecureFieldsPaymentWorkflowImpl.initSecureFields` -> `activities.getReservation`
  -> hotel-reservation-entity-service `GET /v1/reservations/basket/{basketReference}`

## Expected

A basket the reservation service does not know is a not-found: HTTP 404 with
`{"error":{"code":"BASKET_NOT_FOUND"}}`. The service already has the complete plumbing for
this outcome and nothing else is missing — `PaymentOrchestrationInPortImpl` maps the workflow
error code `BASKET_NOT_FOUND` to `BasketNotFoundException`, and `GlobalExceptionHandler`
renders that as 404. The workflow simply never emits that code.

## Actual

HTTP 502 `GATEWAY_ERROR`, with a raw Temporal activity-failure string as the public message —
it leaks the activity type, scheduled and started event ids, the worker identity, and the
retry state into the error envelope.

`SecureFieldsPaymentWorkflowImpl.initSecureFields` wraps its whole body in one blanket handler
that rewrites every failure, whatever its cause, as a gateway error:

```java
} catch (Exception e) {
  return new SecureFieldsInitResult(null, false,
      "GATEWAY_ERROR", e.getMessage());
}
```

Step 1 of that body is `activities.getReservation(basketId)`. An unknown basket makes it throw,
the catch discards the distinction between "the basket does not exist" and "the payment gateway
failed", and the caller cannot tell a client mistake from an upstream outage. The retry state in
the message also shows the missing basket was retried to the activity's maximum attempts before
failing, so a plainly non-retryable condition is treated as a transient one.

Files:
- `backend/book-pay/services/payment-orchestration-service/src/main/java/uk/co/whitbread/payment/orchestrator/domain/workflow/SecureFieldsPaymentWorkflowImpl.java`
  (`initSecureFields`, the trailing `catch (Exception e)`)

## Related: the same detail is lost on the Mobile SDK channel

`POST /api/payments/mobile-sdk` loses the same distinction by a different route. That workflow
rethrows the activity failure rather than catching it, and the resulting Temporal failure type
is the exception class name, which the service's error-code mapping does not recognise — so it
surfaces as 503 `SERVICE_UNAVAILABLE`. `InitMobileSdkSpec` documents that in its own scenario
and asserts the 503, so this file covers only the Secure Fields channel.

## How a scenario reproduces it

The scenario installs the working downstream mappings with `installFor(booking)` and then calls
the endpoint with a well-formed but unknown basket id (`AQN-${UUID.randomUUID()}`). Installing
the happy path is what makes the assertion meaningful: the mappings were available and the
service still stopped before reaching them.

## Effect on the journey

The scenario ships **disabled** (`!` prefix) and asserts the *correct* behaviour — HTTP 404
`BASKET_NOT_FOUND`, plus `callCount(Upstream.OPERA) shouldBe 0` and
`callCount(Upstream.WORLDLINE) shouldBe 0` proving the missing basket stops the flow before
reservation enrichment and payment. Re-enable it when the workflow maps a missing basket to
`BASKET_NOT_FOUND` instead of folding it into the blanket gateway-error handler.
