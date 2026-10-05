# A multi-room cancellation sends the first reservation id in every Opera cancellation body

- **Service**: ohip-adapter-service (reached from hotel-reservation-entity-service)
- **Endpoints**: `POST /ohip/v1/reservations/cancellations`, and therefore
  `POST /v1/reservations/cancellations` (row 31) and
  `POST /v1/reservations/cancellations/rollback` (row 32)
- **Flow doc**: `backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/RollbackReservationCancellations.md`
- **Reproduced by**: nothing. Found by tracing row 32; see *Why no scenario carries this*
  below. `journeys/hotelreservation/RollbackReservationCancellationsSpec.kt` deliberately
  ships no multi-room scenario rather than one that would pass regardless.

## Expected

Cancelling `reservationIds = ["A", "B"]` posts two Opera cancellations, and each one names
the reservation it is cancelling in its own body:

```
POST /rsv/v1/hotels/{hotelId}/reservations/A/cancellations
  { "reservations": [ { "reservationIdList": [ { "id": "A", "type": "Reservation" } ], ... } ] }
POST /rsv/v1/hotels/{hotelId}/reservations/B/cancellations
  { "reservations": [ { "reservationIdList": [ { "id": "B", "type": "Reservation" } ], ... } ] }
```

## Actual

Both bodies carry `"id": "A"`. Only the URL path varies:

```
POST /rsv/v1/hotels/{hotelId}/reservations/B/cancellations
  { "reservations": [ { "reservationIdList": [ { "id": "A", "type": "Reservation" } ], ... } ] }
```

## Chain

1. The cancellation loop calls
   `CancelReservationRequestOhipMapper.toCancelReservationModel(reservationId, request)`
   once per id, passing the id it is currently cancelling as the first argument. That
   argument is what builds the URL path.
2. The body is built by `mapCancelReservationDetails(cancelReservationRequest)`, which
   ignores the per-call `reservationId` argument entirely and does
   `reservationUniqueIdType.setId(cancelReservationRequest.getReservationIds().get(0))`
   — always element 0 of the whole request's list.

`backend/discover-search/services/ohip-adapter-service/src/main/java/uk/co/whitbread/ohip/infrastructure/rest/client/reservation/mapper/CancelReservationRequestOhipMapper.java:31`

## Root cause

`mapCancelReservationDetails` takes only the request, not the reservation id being
cancelled, so it has no way to name the right one and hardcodes `get(0)`. The method is
correct for the single-reservation case, which is why it has gone unnoticed.

## Why no scenario carries this

The defect is invisible to a journey. WireMock's request journal is the only evidence a
journey may read, and journeys inspect WireMock only through `callCount`. Both cancellations still go
to their own distinct URLs, so every `callCount(Upstream.OPERA)` and
`callCount(OperaEndpoint.CANCEL_RESERVATION)` value is identical whether the bug is
present or not; only the request *body* differs, and reading bodies out of the journal is
outside the allowed journey shape. A multi-room scenario would therefore pass with the bug
in place and prove nothing, so row 32 does not add one.

Proving it needs either an ohip-adapter unit test on the mapper (the natural home — a
two-id request should produce two different bodies) or a testkit capability to assert
recorded request bodies, which is frozen for this batch.

## Impact

Whether real Opera rejects or silently mis-handles the second cancellation depends on
Opera's own precedence between the path id and the body id; the WireMock defaults match on
the path only, so the integration environment cannot answer that. Either way the request
ohip-adapter sends is self-contradictory for every reservation after the first.

## Fix

Pass the per-call reservation id into the body mapper —
`mapCancelReservationDetails(String reservationId, CancelReservationRequest request)` — and
set `reservationUniqueIdType.setId(reservationId)`, keeping `getReservationIds().get(0)`
nowhere. Then add a two-id mapper unit test in ohip-adapter-service.
