# `POST /ohip/v1/reservations/memos` answers 500 when the post-write re-read returns no reservation

## Endpoint

`POST /ohip/v1/reservations/memos` — ohip-adapter-service.

## Chain

`HotelReservationController.createMemo` → `HotelReservationInPortImpl.createMemo` →
`HotelReservationOutPortImpl.createMemo` (phase 1: one
`PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}` per id; phase 2: one
`GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}?fetchInstructions=Reservation&fetchInstructions=RoutingInstructions&fetchInstructions=Comments`
per id) → `MemosOhipMapper.toModel`.

## Expected

Opera answering a success envelope whose `reservations.reservation` array is present but empty
means the property holds nothing for that id at re-read time. That reservation contributes no
comments, so it must contribute no memos: the endpoint answers `201 Created` with
`{"memos":[]}` — the same outcome it already produces for a reservation whose re-read reports no
comments at all.

## Actual

`500` with
`{"errCode":400,"debugMessage":"Index 0 out of bounds for length 0","globalErrTextTemplate":"generic.server.exception"}`.
The memo write itself succeeded — Opera call counts are `UPDATE_RESERVATION` 1 and
`GET_RESERVATION` 1 — so the caller is told the operation failed while the memo is in fact
stored in Opera.

`MemosOhipMapper.toModel` indexes `reservations.reservation.get(0)` with no null/emptiness
guard, so the `IndexOutOfBoundsException` (empty list) — or `NullPointerException` for a null
list — escapes and is mapped to the generic internal-server error.

Same family as `bug/update-special-requests-empty-reservation-500.md`,
`bug/get-reservation-by-reservation-id-empty-reservation-500.md`,
`bug/get-reservation-amounts-empty-folio-windows-500.md` and
`bug/delete-routing-instructions-empty-reservation-500.md`: empty nested Opera collections are
not guarded on this service's reservation reads.

## Reproduction

`journeys/ohip/AddReservationMemosSpec.kt`, scenario
`!a re-read reporting no reservations returns an empty memo list` (ships disabled, asserting the
correct `201` plus an empty memo list). It installs the Booking's default stubs with
`booking.opera.get-reservation` excluded and overrides it with the custom stub
`custom.opera.get-reservation-routing-empty` (`getReservationRoutingEmpty`), which answers `200`
with `{"reservations":{"reservation":[]}}`; the default `booking.opera.put-reservation` serves
the write. Verified on 2026-08-27 by temporarily enabling the scenario: HTTP 500 with the
debug message above. Re-enable the scenario when `MemosOhipMapper.toModel` guards the
reservation list.
