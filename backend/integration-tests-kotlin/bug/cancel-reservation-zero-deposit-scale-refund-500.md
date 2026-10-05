# Cancelling a nothing-paid basket fails with 500 after Opera and the basket are already cancelled

- **Service**: hotel-reservation-entity-service
- **Endpoint**: `POST /v1/reservations/cancellations`
- **Flow doc**: `backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/CreateReservationCancellations.md`
- **Reproduced by**: nothing in the suite today. Both
  `journeys/hotelreservation/CancelReservationSpec.kt` scenarios that used to reproduce it
  (`a pay-on-arrival basket is cancelled in Opera and the basket is cancelled` and
  `the same two-room basket is cancelled when OTA bookings are accepted`) are **enabled and
  green** since `booking.opera.reservation-amounts` began emitting a scale-0 `"deposit": 0`
  (2026-08-24, batch-2 change 1).

- **Status (2026-08-24)**: **open, unreproduced**. The stub fix only made the suite model the
  common Opera shape; it did not fix the service. `processRefund` / `updateChargesAfterRefund`
  still test a zero with `BigDecimal.ZERO.equals(...)`, so any producer that sends a scaled
  zero (`"deposit": 0.0`, `0.00`, …) still lands in the impossible branch and 500s after Opera
  and the basket have been cancelled. Whether real Opera can ever send a scaled zero on this
  field is the open question this file is waiting on; until it is answered the `equals`
  comparison stays a latent defect and this file stays open. Flipping the stub helper
  `OperaRateInfoStubs.operaAmount` back to `BigDecimal.valueOf(value)` reproduces the 500 on
  both scenarios (verified by mutation before their re-enablement).

## Expected

A pay-on-arrival cancellation with nothing paid returns `200` with the basket reference:
the Opera reservations are cancelled, the basket is cancelled, no refund is attempted and
the cancellation email is triggered.

## Actual

`500` with

```json
{"errCode":95,"debugMessage":"The refund request can't be triggered because the amountPaid is lower than 0 ","globalErrTextTemplate":"internal.server.exception"}
```

The failure lands **after** `processCancel` has already cancelled every Opera reservation
and called `basketOutPort.cancelBasket`. The caller sees a server error for a cancellation
that in fact succeeded, and the cancellation email is never triggered because
`processRefund` throws before `triggerEmailConfirmation`.

## Chain

1. `HotelReservationInPortImpl.cancelReservation` reads the basket, then reads its
   reservations through `ohip-adapter-service`
   (`GET /ohip/v1/reservations/basket`, `rateInfoNeeded=true`).
2. ohip-adapter reduces each reservation's rate-info summary deposit into
   `ReservationByBasketRefResponse.amountPaid` as `BigDecimal.ZERO.add(deposit.negate())`.
   `BigDecimal.add` keeps the larger scale, so a `0.0` deposit yields `amountPaid = 0.0`
   (scale 1), not `BigDecimal.ZERO` (scale 0).
3. `processCancel` cancels the reservations in Opera and cancels the basket.
4. `processRefund` skips the refund only on `BigDecimal.ZERO.equals(amountPaid)`, which is
   **scale-sensitive**: `BigDecimal.ZERO.equals(new BigDecimal("0.0"))` is `false`.
5. Falling through, `BigDecimal.ZERO.compareTo(amountPaid) < 0` is `false` for `0.0`, so
   the `else` branch throws
   `GenericReservationException(DIGITAL_REFUND_REQUEST_EXCEPTION)` — error code 95 — with
   the message "the amountPaid is lower than 0", which is not what happened.

## Root cause

`HotelReservationInPortImpl.processRefund` (and `updateChargesAfterRefund`, which has the
same `BigDecimal.ZERO.equals(...)` guard) compares `BigDecimal` values for a zero amount
with `equals` instead of `compareTo`. Any zero that arrives with a non-zero scale — which
is what Opera's `"deposit": 0.0` produces — is treated as neither "nothing paid" nor
"something paid", and the request dies in the impossible branch.

## Scope

- Reproduces on the plain pay-on-arrival path with `amountAlreadyPaid = 0`, i.e. the
  single most common cancellation shape.
- The ACI escape hatch does not help: the folios default emits `emptyFolio=true` at
  `amountAlreadyPaid = 0`, so the ACI deposit total is zero and the deposit is not
  overwritten with a scale-0 value.
- Rows 32 (`POST /v1/reservations/cancellations/rollback`) and 48
  (`PUT /v1/reservations/cancellations/on-hold`) are **not** affected: neither runs
  `processRefund`.
- The same scale-sensitive comparison appears a second time, in
  `ManageBookingUtils.getPaymentOption`
  (`!operaRes.getAmountPaid().equals(BigDecimal.ZERO) ? PAY_NOW : PAY_ON_ARRIVAL`). There it
  does not throw, it mis-classifies: a nothing-paid reservation whose deposit arrives with a
  non-zero scale is imported as `PAY_NOW` and the import reads
  `GET /csh/v1/hotels/{hotelId}/depositFolio` for a deposit that cannot exist. Found on
  2026-08-24 when the scale-0 zero removed that phantom read from five `FindBookingSpec`
  scenarios and both `FindBookingKioskSpec` import scenarios, whose Opera counts dropped from
  five to four; those expectations were updated to the correct behaviour. Fixing only
  `processRefund` and leaving this call site would keep the same latent defect.

## Fix

Compare with `compareTo`, e.g. `amountPaid.signum() == 0` (or
`BigDecimal.ZERO.compareTo(amountPaid) == 0`) for the "nothing paid" early return, in both
`processRefund` and `updateChargesAfterRefund`. Re-enable the two disabled scenarios
afterwards.
