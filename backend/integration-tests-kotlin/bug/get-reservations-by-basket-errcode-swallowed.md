# getReservationsByIds discards ohip-adapter's 4xx errCode: a missing reservation surfaces as errCode 0

- **Status**: reproduced 2026-08-24 against the running integration stack by matrix row 12's
  journey `GetReservationsByBasketSpec` (scenario "a basket whose Opera reservation is gone maps
  to the adapter not-found").
- **Endpoint**: `GET /v1/reservations/basket/{basketReference}` (hotel-reservation-entity-service)
- **Flow doc**: `backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/GetReservationsByBasket.md`
- **Chain**: Opera returns no reservation -> ohip-adapter-service `HotelReservationNotFound`
  (`DIGITAL_RESERVATION_NOT_FOUND`, errCode `16`, HTTP 404) ->
  `OhipAdapterClient.sendGetReservationsByIds` 4xx handler -> `HotelReservationNotFoundException`
  -> hotel-reservation-entity-service error envelope
- **Same defect, different exception class**: `bug/cdh-search-errcode-swallowed.md` and
  `bug/changelog-adapter-errcode-swallowed.md`. This one is narrower: on the *same* client method
  the 5xx handler is fine, so a single request path shows both halves of the pattern.

## Expected

`GET /v1/reservations/basket/{basketReference}` answers HTTP 404 carrying ohip-adapter's
`DIGITAL_RESERVATION_NOT_FOUND` code `16`, the way the 5xx branch of the same method preserves
`OHIP_GET_RESERVATION_EXCEPTION` (`960`) and `DIGITAL_POLICY_CODE_NOT_UNIQUE_EXCEPTION` (`19`).

## Actual

HTTP 404 with body `{"errCode":0}`. Nothing distinguishes "Opera holds no such reservation" from
any other not-found on this endpoint.

`OhipAdapterClient.sendGetReservationsByIds` maps the two status families into two different
exception classes:

```java
.onStatus(HttpStatusCode::is4xxClientError, response -> {
  WebClientUtils.logErrorResponse(log, response);
  return response.bodyToMono(HotelReservationNotFoundException.class);
})
.onStatus(HttpStatusCode::is5xxServerError, response -> {
  WebClientUtils.logErrorResponse(log, response);
  return response.bodyToMono(HotelReservationOhipException.class);
})
```

`HotelReservationOhipException` carries a `@JsonCreator` constructor binding
`globalErrTextTemplate` / `debugMessage` / `errCode`, so the 5xx code survives. Its not-found
sibling has no `@JsonCreator` at all:

```java
public class HotelReservationNotFoundException extends AbstractNotFoundException {
  public HotelReservationNotFoundException(String message) {
    super(message, message, 0);
  }
  ...
}
```

Jackson's `ThrowableDeserializer` therefore falls back to the single-`String` constructor, which
hard-codes `0`, and the adapter's `16` is thrown away.

Files:
- `backend/manage-modify/services/hotel-reservation-entity-service/src/main/java/uk/co/whitbread/reservation/infrastructure/rest/client/ohip/service/OhipAdapterClient.java`
- `backend/manage-modify/services/hotel-reservation-entity-service/src/main/java/uk/co/whitbread/reservation/domain/exceptions/HotelReservationNotFoundException.java`
- `backend/manage-modify/services/hotel-reservation-entity-service/src/main/java/uk/co/whitbread/reservation/domain/exceptions/HotelReservationOhipException.java`

## How a scenario reproduces it

Row 12's not-found scenario creates a one-room basket, then excludes
`booking.opera.get-reservation` and installs the custom `opera.get-reservation.empty` stub, so
Opera answers `200` with an empty body for the basket's only reservation id. ohip-adapter raises
`DIGITAL_RESERVATION_NOT_FOUND` (16) as HTTP 404; the endpoint answers HTTP 404
`{"errCode":0}`.

## Effect on the journey

The scenario ships **enabled** and asserts HTTP 404 plus the call shape (the reservation read is
made, and the whole enrichment fan-out downstream of it is proved absent), which is correct and
worth guarding. It deliberately does **not** assert `errCode` — asserting `0` would freeze the
defect into the suite. The sibling scenario "an Opera rejection of the reservation read surfaces
the adapter failure code" *does* assert `errCode == 960`, so the suite records that the same hop
preserves a code when the target class carries a `@JsonCreator`. Add the `errCode` assertion here
once `HotelReservationNotFoundException` carries one (the assertion is deferred, not
bent).
