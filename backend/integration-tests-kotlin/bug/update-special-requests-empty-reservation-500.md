# `PUT /ohip/v1/reservations/special-requests` answers 500 when Opera returns a reservation envelope with no reservation

## Endpoint

`PUT /ohip/v1/reservations/special-requests` — ohip-adapter-service.

## Chain

`HotelReservationController.updateReservationsSpecialRequests` →
`HotelReservationInPortImpl.updateSpecialRequests` →
`HotelReservationOutPortImpl.updateSpecialRequests` (phase 1: read + comment removal) →
`OhipReservationClient.getReservations`
(`GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}`).

## Expected

Opera answering a success envelope that carries no reservation
(`{"reservations":{}}`, or a present-but-empty `reservation` array) means the property holds
nothing for that id. The comment-removal phase has nothing to remove, so the reservation must
be skipped and the flow must continue to phase 2: the final change-reservation
`PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}` is still sent and the endpoint
answers `200 OK` with an empty body — exactly as it does for the wholly empty HTTP body case,
where the reactive stream emits nothing and the removal phase is skipped.

## Actual

`500` with
`{"errCode":400,"debugMessage":"Cannot invoke \"java.util.List.get(int)\" because the return
value of \"uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationsType.getReservation()\"
is null","globalErrTextTemplate":"generic.server.exception"}`, and **no** final PUT is sent
(observed Opera counts: `GET_RESERVATION` 1, `UPDATE_RESERVATION` 0).

`HotelReservationOutPortImpl.updateReservationComments` dereferences
`reservation.getReservations().getReservation().get(0)` with no null/emptiness guard, so the
`NullPointerException` (null list) or `IndexOutOfBoundsException` (empty list) escapes and is
mapped to the generic internal-server error.

Same family as `bug/get-reservation-by-reservation-id-empty-reservation-500.md` and
`bug/delete-routing-instructions-empty-reservation-500.md`: empty nested reservation
collections are not guarded on this service's reservation reads.

## Reproduction

`journeys/ohip/UpdateSpecialRequestsSpec.kt`, scenario
`!an empty reservation collection skips the reservation instead of failing` (ships disabled,
asserting the correct `200` plus one final update). The room carries the
`reservationAbsentInOpera` fact, so the `booking.opera.get-reservation` default answers `200`
with `{"reservations":{}}` for it. Verified on 2026-08-27 by temporarily enabling the scenario:
HTTP 500 with the NPE debug message above. Re-enable the scenario when
`updateReservationComments` guards the reservation list.
