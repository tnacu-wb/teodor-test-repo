# deleteRoutingInstructions crashes on a reservation Opera holds nothing for

- **Endpoint**: `DELETE /ohip/v1/reservations/routingInstructions` (ohip-adapter-service)
- **Chain**: per reservation id — `GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}`
  (fetchInstructions `Reservation,RoutingInstructions,Comments`) → per-instruction
  cashiering DELETE → per-Business-Notes change-reservation PUT

## Expected

A reservation id Opera answers with a success envelope whose
`reservations.reservation` array is empty (Opera's not-found shape on this GET) should be
skipped gracefully — a no-op 204 like the instruction-less branch, or at worst a mapped
OHIP not-found error — never an unhandled runtime exception.

## Actual

`HotelReservationOutPortImpl.deleteRoutingInstruction` guards the routing-instruction
block with `!reservation.getReservations().getReservation().isEmpty()`, but then calls
`deleteBusinessNotes(...)` unconditionally, and that method dereferences
`reservation.getReservations().getReservation().get(0)` without a guard. With the empty
array the whole request fails with HTTP 500 and a generic envelope:

```
HTTP 500
{"errCode":400,"debugMessage":"Index 0 out of bounds for length 0",
 "globalErrTextTemplate":"generic.server.exception"}
```

Observed 2026-08-21 against the integration stack (evidence:
`build/test-evidence/ohip-adapter-deletes-reservation-routing-instructions-a-reservation-opera-holds-nothing-fo-0v5m1l/evidence.txt`).

## Reproduction

`DeleteRoutingInstructionsSpec` — scenario "a reservation Opera holds nothing for is
skipped without crashing" (disabled with a `!` prefix; re-enable when fixed). It excludes
the default `booking.opera.get-reservation` stub and installs
`custom.opera.get-reservation-routing-empty`, which answers the routing GET with
`{"reservations":{"reservation":[]}}` for a reservation id the booking never created,
then calls the endpoint for that id. The scenario asserts the correct behavior (204,
exactly one Opera call) and currently fails against the 500.

The same bug is reproduced one layer up by
`journeys/hotelreservation/DeleteRoutingInstructionsSpec` — scenario "a reservation Opera
holds nothing for is skipped without crashing" (also `!`-disabled) — proving
hotel-reservation-entity-service surfaces the adapter's 500 unchanged on
`DELETE /v1/reservations/routingInstructions`.
