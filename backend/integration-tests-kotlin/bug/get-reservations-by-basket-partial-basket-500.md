# A basket Opera answers for only part of crashes the reservation-id re-stamp with HTTP 500

- **Status**: reproduced 2026-08-24 against the running integration stack by matrix row 12's
  journey `GetReservationsByBasketSpec` (scenario "a basket Opera answers for only one of its two
  reservations is reported as not found", which ships disabled on this file). Observed response:
  HTTP 500 `{"code":"999","details":["Index 1 out of bounds for length 1"]}`.
- **Endpoint**: `GET /v1/reservations/basket/{basketReference}` (hotel-reservation-entity-service)
- **Flow doc**: `backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/GetReservationsByBasket.md`
- **Chain**: basket-service basket items -> `HotelReservationOhipOutPortImpl.getReservationsByIds`
  -> ohip-adapter-service `GET /ohip/v1/reservations/basket` -> back in
  `HotelReservationInPortImpl.getReservationByBasketRefResponse`, the positional reservation-id
  re-stamp loop

## Expected

A basket whose reservations Opera can no longer fully answer is a not-found, the same outcome the
service already produces when Opera answers for *none* of them: HTTP 404 from ohip-adapter's
`DIGITAL_RESERVATION_NOT_FOUND`. Partial loss is strictly less complete than total loss and must
not be a harder failure.

## Actual

HTTP 500 with an unmapped `IndexOutOfBoundsException`, leaking the internal list arithmetic into
the error envelope (`code 999`, `details ["Index 1 out of bounds for length 1"]`).

`HotelReservationInPortImpl.getReservationByBasketRefResponse` walks the *basket item* indices
and indexes the *adapter response* list with them:

```java
var reservations = hotelReservationOhipOutPort.getReservationsByIds(basket.getHotelId(),
    reservationsIds, priceBreakdownNeeded, false, rateInfoNeeded);
...
for (int i = 0; i < reservationsIds.size(); i++) {
  reservations.getReservationByIdList().get(i).setReservationId(reservationsIds.get(i));
}
```

Nothing guarantees the two lists are the same length. ohip-adapter drops reservation ids Opera
answers nothing for (its `Flux` simply collects fewer elements) and does not fail as long as at
least one reservation came back, so any basket that is partially resolvable produces a shorter
list and the loop walks off the end. The same loop exists in the sibling overload used by the
authenticated/booking-reference path, so both are affected.

This is also the reachable form of the duplicate-`sourceId` defect the flow doc's Branches table
notes: it needs no duplicate ids at all, only one reservation Opera has lost.

Files:
- `backend/manage-modify/services/hotel-reservation-entity-service/src/main/java/uk/co/whitbread/reservation/domain/logic/HotelReservationInPortImpl.java`
  (`getReservationByBasketRefResponse`, both overloads)

## How a scenario reproduces it

Row 12's partial-basket scenario creates a **two**-room basket normally (both Opera reservations
exist, so the create succeeds), then installs the existing custom `opera.get-reservation.empty`
stub as a mid-journey override for **one** of the two reservation ids — WireMock serves the most
recently added match, so the other room keeps the default read. The basket still holds two items;
ohip-adapter returns one reservation; the re-stamp loop asks for index 1 of a one-element list.

## Effect on the journey

The scenario ships **disabled** (`!` prefix) and asserts the *correct* behaviour — HTTP 404, the
same outcome as a wholly missing basket — plus that both reservation reads were made. Re-enable it
when the loop iterates the response list (or fails with a mapped not-found on a length mismatch).
