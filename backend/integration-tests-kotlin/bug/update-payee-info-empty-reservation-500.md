# updateRoutingInstructionsWithPayeeInfo crashes on a reservation Opera holds nothing for

- **Endpoint**: `PUT /ohip/v1/reservations/instructions/payeeInfo` (ohip-adapter-service)
- **Chain**: per distinct reservation id — `GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}`
  (ten-instruction fetch set) → change-reservation `PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}`

## Expected

A reservation id Opera answers with its not-found success envelope (`{"reservations":{}}`,
no `reservation` member) should be skipped gracefully — contribute no write and let the
request return its normal 200 empty body, or at worst a mapped OHIP not-found error — never
an unhandled runtime exception.

## Actual

`HotelReservationOutPortImpl.updateRoutingInstructionsWithPayeeInfo` dereferences
`reservations.getReservation().get(0)` on the read result without a guard, so the whole
request fails with HTTP 500 and a generic envelope carrying the literal number 400 in
`errCode`, and no write is attempted for any id:

```
HTTP 500
{"errCode":400,"debugMessage":"Cannot invoke \"java.util.List.get(int)\" because the return
 value of \"...HotelReservationsType.getReservation()\" is null",
 "globalErrTextTemplate":"generic.server.exception"}
```

Observed 2026-09-01 by a coordinator live probe against the integration stack: with a
WireMock mapping answering the reservation GET for id `6008114` with `{"reservations":{}}`
and a permissive reservation PUT installed, `PUT
/ohip/v1/reservations/instructions/payeeInfo?hotelId=HEAPTI&reservationIds=6008114` returned
the 500 above and the PUT count stayed 0.

Same unguarded-dereference class as
`bug/delete-routing-instructions-empty-reservation-500.md` and the row 68
`getBookerProfileId` candidate (plan item 9).

## Reproduction

`UpdateReservationPayeeInfoSpec` — scenario "a reservation Opera holds nothing for is
skipped instead of crashing the request" (Wave 3 accepted design `81-S7`; ships disabled
with a `!` prefix; re-enable when fixed). One `BookingRoom` with
`reservationAbsentInOpera = true`, plain `installFor(booking)`; the scenario asserts the
correct behavior (200 empty body, exactly one reservation GET, `UPDATE_RESERVATION` count 0
with the default PUT installed as the absence mock) and currently fails against the 500.
