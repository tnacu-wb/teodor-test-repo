# searchBookingFromCdh discards cdh-adapter's errCode: a CDH failure surfaces as errCode 0

- **Status**: reproduced 2026-08-24 against the running integration stack by matrix row 26's
  journey `SearchBookingCdhSpec` (scenario "a CDH server error surfaces as HTTP 500").
- **Endpoint**: `GET /v1/reservations/search/booking/cdh` (hotel-reservation-entity-service)
- **Flow doc**: `backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/SearchBookingCdh.md`
- **Chain**: CDH 5xx -> cdh-adapter-service `CDHException` -> `CdhAdapterClient.searchReservation`
  -> `CdhReservationException` -> hotel-reservation-entity-service error envelope
- **Same defect, different exception class**: `bug/changelog-adapter-errcode-swallowed.md`
  records the identical shape on `GET /v1/reservations/changeLog` via `ChangeLogException`. Both
  are instances of one pattern: a `WebClient` `onStatus` handler that deserializes the adapter's
  error body into an `AbstractInternalException` subclass whose only single-argument constructor
  hard-codes `errorCode` 0.

## Expected

The caller can tell a CDH outage apart from other failures of this endpoint, the way the
routing-instructions chain preserves 937/939/958 end to end (`DeleteRoutingInstructionsSpec`).
cdh-adapter-service raises `CDHException` with its own `ErrorCode`, and that code should reach
the caller.

## Actual

HTTP 500 with body `{"errCode":0}`. Nothing distinguishes "CDH is down" from any other internal
failure of the search.

`CdhAdapterClient.searchReservation` maps every error status by deserializing the adapter's body
into the exception type:

```java
.onStatus(HttpStatusCode::isError, response -> {
  WebClientUtils.logErrorResponse(log, response);
  return response.bodyToMono(CdhReservationException.class);
})
```

`CdhReservationException` extends `AbstractInternalException` and offers no `@JsonCreator`; its
only single-argument constructor hard-codes the code:

```java
public CdhReservationException(String message) {
  super(message, message, 0);
}
```

so Jackson can only reach the `errCode = 0` path and the adapter's own code is thrown away.

Files:
- `backend/manage-modify/services/hotel-reservation-entity-service/src/main/java/uk/co/whitbread/reservation/infrastructure/rest/client/cdh/service/CdhAdapterClient.java`
- `backend/manage-modify/services/hotel-reservation-entity-service/src/main/java/uk/co/whitbread/reservation/infrastructure/rest/client/ohip/service/exceptions/CdhReservationException.java`

## How a scenario reproduces it

Row 26's negative scenario excludes `booking.cdh.reservation-search` and installs
`booking.cdh.reservation-search.server-error` (CDH 500 on both the V2 and the V3 route). The
endpoint answers HTTP 500 with `{"errCode":0}`.

## Effect on the journey

The scenario ships **enabled** and asserts HTTP 500 plus the single-CDH-call shape, which is
correct and worth guarding. It deliberately does **not** assert `errCode`: there is no code to
assert that identifies the failure, and asserting `0` would freeze the defect into the suite.
Add the `errCode` assertion here once the exception carries the adapter's code (the
assertion is deferred, not bent).
