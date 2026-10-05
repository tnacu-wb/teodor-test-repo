# `GET /ohip/v1/reservation/reservationId` answers 500 when Opera returns an empty reservation list

## Endpoint

`GET /ohip/v1/reservation/reservationId?hotelId={hotelId}&reservationId={reservationId}` —
ohip-adapter-service.

## Chain

`HotelReservationController.getReservationsByReservationId` →
`HotelReservationInPortImpl.getReservationsByReservationId` →
`HotelReservationOutPortImpl.getReservationsByReservationId` →
`OhipReservationClient.sendGetReservationsByReservationId`
(`GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}`).

## Expected

Opera's success envelope carrying no reservation is a not-found answer. When
`reservations.reservation` is `null` the out-port already returns `null` and the controller
answers `404` with an empty body. The present-but-empty array (`reservations.reservation: []`)
is the same world and must answer `404` with an empty body too.

## Actual

`500` with `{"errCode":400,"debugMessage":"Index 0 out of bounds for length 0",
"globalErrTextTemplate":"generic.server.exception"}`.

The null check passes because the list is non-null, so enrichment is skipped, but the billing
and company helpers index element `0` of the empty list unguarded:

- `HotelReservationOutPortImpl.getBookerProfileById(...)` — `getReservations().getReservation().get(0)`
- `HotelReservationOutPortImpl.getCompanyName(...)` — same unguarded `get(0)`

`IndexOutOfBoundsException` escapes and is mapped to the generic internal-server error.

Same family as `bug/delete-routing-instructions-empty-reservation-500.md`: an empty nested
reservation collection is not guarded on this service's reservation reads.

## Reproduction

`journeys/ohip/GetReservationByReservationIdSpec.kt`, scenario
`!answers 404 when Opera returns an empty reservation list` (ships disabled, asserting the
correct `404`). It installs the Booking's default stubs with `booking.opera.get-reservation`
excluded and overrides it with the custom stub `custom.opera.get-reservation-routing-empty`
(`getReservationRoutingEmpty`), which answers `200` with
`{"reservations":{"reservation":[]}}`. Re-enable the scenario when the helpers guard the list.
