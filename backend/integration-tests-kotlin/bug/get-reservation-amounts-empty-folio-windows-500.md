# `GET /ohip/v1/reservations/amounts` answers 500 when a reservation has no cashiering folio window

## Endpoint

`GET /ohip/v1/reservations/amounts?hotelId={hotelId}&reservationIds={id}` on
ohip-adapter-service.

## Chain

`HotelReservationController.getReservationAmounts` ->
`HotelReservationInPortImpl.getReservationAmounts` ->
`HotelReservationOutPortImpl.getReservationAmounts(hotelId, reservationIds)` (public overload,
around line 1547) -> `OhipReservationClient.getFoliosAciAmount` ->
`HotelReservationOutPortImpl.extractAmountFromFolioWindows` (around line 1436).

`OhipReservationClient.getFoliosAciAmount` filters the Opera folio response away when
`reservationFolioInformation` is absent **or** its `folioWindows` list is empty, so the reactive
chain completes empty and the value reaching the caller is `null`. The caller then does
`extractAmountFromFolioWindows(...).abs()` (around line 1551) with no null guard, so a
`NullPointerException` propagates and the request fails.

## Expected

A reservation Opera has opened no cashiering folio window for has nothing posted against it. The
ACI deposit total is zero, so the reservation's own rate-info summary figures should be returned
unchanged: `200` with `deposit` and `outStandingCostOfStay` exactly as the summary reports them.
This is the same outcome the service already produces for a folio window that reports
`emptyFolio = true`.

## Actual

`500` (internal server error) from the NullPointerException. No money summary is returned at all,
even though the rate-info leg succeeded.

## Reproduction

`journeys/ohip/GetReservationAmountsSpec.kt`, scenario
`!treats a reservation with no folio windows as nothing posted` (shipped disabled with its correct assertion preserved,
asserting the correct behavior).

- Booking: one `Hotels.HEAPTI` hotel with one `availableRates` entry matching the room, arrival
  `LocalDate.now().plusDays(30)`, departure arrival + 2 days, one `BookingRoom` with
  `reservationId = "6005608"`, `roomType = "LOWDBL"`, `adults = 2`, `status = RESERVED`,
  `amountAlreadyPaid = 50.0`.
- `installFor(booking, excluded = setOf(OPERA_RESERVATION_FOLIOS_STUB_ID))` then
  `installStub(reservationFoliosWithoutWindows(booking))` - stub id
  `opera.reservation-folios.no-windows`, which serves the same folio URL with
  `reservationFolioInformation.folioWindows = []`.
- Call `GET /ohip/v1/reservations/amounts` for that hotel and reservation id.

Both Opera legs are still called once each (`GET_RATE_INFO` 1, `GET_FOLIOS` 1); the failure is in
the adapter's own handling of the folio answer.

## Note

The same missing null guard covers two neighbouring shapes: a folio response with no
`reservationFolioInformation` member at all (identical filter, identical NPE), and a folio window
that omits `emptyFolio`, or a non-empty window that omits `payment` - both dereferenced without a
null check while summing. Fixing the deposit reconciliation should treat "no folio data" as a zero
ACI total.
