# updateDiscount discards ohip-adapter's 4xx errCode and status: invalid amount surfaces as errCode 0, not-found as 400/0 instead of 404/23

- **Status**: reproduced 2026-08-28 against the running integration stack by the direct adapter
  journey `journeys/ohip/UpdateDiscountSpec` (which observes the adapter's real 400/22 and
  404/23) together with `journeys/hotelreservation/UpdateReservationDiscountSpec`, whose
  HRE-boundary scenarios observe 400 `{"errCode":0}` for both cases.
- **Endpoint**: `PUT /v1/reservations/discount` (hotel-reservation-entity-service)
- **Flow doc**: `backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateDiscount.md`
  (adapter side; the HRE hop is a pure delegate around `OhipAdapterClient.sendUpdateDiscountRequest`)
- **Chain (invalid amount)**: adapter guard rejects the aggregate discount →
  `DIGITAL_UPDATE_DISCOUNT_EXCEPTION` errCode `22`, HTTP 400 →
  `OhipAdapterClient.sendUpdateDiscountRequest` 4xx handler → `DiscountInvalidAmountException`
  → HRE answers HTTP 400 `{"errCode":0}`.
- **Chain (missing reservation)**: adapter completeness guard finds no collected reservation →
  `DIGITAL_OPERA_DISCOUNT_EXCEPTION` errCode `23`, HTTP 404 → the same 4xx handler and the same
  exception class → HRE answers HTTP **400** `{"errCode":0}`, although HRE's own controller
  declares a 404 response for this operation.
- **Same defect family**: `bug/get-reservations-by-basket-errcode-swallowed.md`,
  `bug/cdh-search-errcode-swallowed.md`, `bug/changelog-adapter-errcode-swallowed.md`. This
  instance is wider than the basket one: a single 4xx handler swallows two distinct adapter
  outcomes, losing the status distinction as well as both codes.

## Expected

`PUT /v1/reservations/discount` propagates the adapter's error contract the way the 5xx branch
of the same method preserves codes through `HotelReservationOhipException`'s `@JsonCreator`:

- discount above the aggregate nightly-rate total → HTTP 400 carrying errCode `22`;
- reservation missing from the Opera read → HTTP 404 carrying errCode `23` (the controller's
  own `@ApiResponse(responseCode = "404")` declares this shape).

## Actual

Both cases answer HTTP 400 with body `{"errCode":0}`. Nothing distinguishes "your discount is
too large" from "that reservation does not exist" at the HRE boundary.

`OhipAdapterClient.sendUpdateDiscountRequest` maps every 4xx to one exception class:

```java
.onStatus(HttpStatusCode::is4xxClientError,
    response -> {
      WebClientUtils.logErrorResponse(log, response);
      return response.bodyToMono(DiscountInvalidAmountException.class);
    })
```

`DiscountInvalidAmountException` has no `@JsonCreator`, so Jackson's `ThrowableDeserializer`
falls back to the single-`String` constructor, which hard-codes errCode `0`:

```java
public class DiscountInvalidAmountException extends AbstractBadRequestException {
  public DiscountInvalidAmountException(String message) {
    super(message, message, 0);
  }
  ...
}
```

And because the class extends `AbstractBadRequestException`, the adapter's 404 is re-emitted as
400, contradicting the controller's declared 404 response.

Files:
- `backend/manage-modify/services/hotel-reservation-entity-service/src/main/java/uk/co/whitbread/reservation/infrastructure/rest/client/ohip/service/OhipAdapterClient.java`
  (`sendUpdateDiscountRequest`)
- `backend/manage-modify/services/hotel-reservation-entity-service/src/main/java/uk/co/whitbread/reservation/infrastructure/rest/client/ohip/exceptions/DiscountInvalidAmountException.java`
- `backend/manage-modify/services/hotel-reservation-entity-service/src/main/java/uk/co/whitbread/reservation/infrastructure/rest/controller/reservation/HotelReservationController.java`
  (`updateDiscount` `@ApiResponse` 404)

## How a scenario reproduces it

- Invalid amount: a one-room Booking with a deterministic nightly rate whose stay total is below
  the requested discount; the adapter answers 400/22 and HRE surfaces 400/0.
- Missing reservation: exclude `booking.opera.get-reservation` and install
  `opera.get-reservation.empty`, so Opera answers 200 with an empty body; the adapter answers
  404/23 and HRE surfaces 400/0.

## Effect on the journeys

`UpdateReservationDiscountSpec`'s invalid-amount scenario ships **enabled** asserting HTTP 400
plus the exact call shape; it deliberately does **not** assert `errCode` — asserting `0` would
freeze the defect into the suite. Its missing-reservation scenario is **disabled with the correct assertion preserved**
and asserts the correct contract (HTTP 404, errCode `23`); it is re-enabled when the client
propagates the adapter's status and code. The adapter's own contract is guarded enabled by
`journeys/ohip/UpdateDiscountSpec` (400/22 and 404/23 at the adapter boundary), so the correct
behaviour stays continuously proven one hop below.
