# getChangeLog discards ohip-adapter's errCode: every downstream failure surfaces as errCode 0

- **Status**: reproduced 2026-08-24 against the running integration stack by matrix row 19's
  journey `GetReservationChangeLogSpec` (all three negative scenarios).
- **Endpoint**: `GET /v1/reservations/changeLog` (hotel-reservation-entity-service)
- **Flow doc**: `backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/GetReservationChangeLog.md`
- **Chain**: `OhipAdapterClient.getChangeLog` -> `ChangeLogException` ->
  hotel-reservation-entity-service error envelope

## Expected

The caller can tell the three downstream outcomes apart, as they can one layer down: the
ohip-adapter-level `GetChangeLogSpec` asserts `errCode` 204 (Opera 204, `DIGITAL_NO_ACTIVITY_LOG`),
916 (Opera 4xx, `OHIP_GET_LOG_ACTIVITY_OPERA_EXCEPTION`) and 917 (Opera 5xx,
`OHIP_GET_LOG_ACTIVITY_EXCEPTION`). Passing the same failures through
hotel-reservation-entity-service should preserve that code, as the routing-instructions chain
does (`DeleteRoutingInstructionsSpec` asserts 937/939/958 end to end).

## Actual

All three produce the same body, HTTP 500 `{"errCode":0}`. "No activity log exists",
"Opera rejected the read" and "Opera is down" are indistinguishable to the caller.

`OhipAdapterClient.getChangeLog` maps every non-2xx **and** the 204 with
`response.bodyToMono(ChangeLogException.class)`:

```java
.onStatus(Predicate.isEqual(HttpStatusCode.valueOf(204)), response -> {
  WebClientUtils.logErrorResponse(log, response);
  return response.bodyToMono(ChangeLogException.class);
})
.onStatus(HttpStatusCode::isError, response -> {
  WebClientUtils.logErrorResponse(log, response);
  return response.bodyToMono(ChangeLogException.class);
})
```

`ChangeLogException` extends `AbstractInternalException` and offers no `@JsonCreator`; its only
single-argument constructor hard-codes the code:

```java
public ChangeLogException(String message) {
  super(message, message, 0);
}
```

so Jackson can only reach the `errCode = 0` path and the adapter's own code is thrown away.

Files:
- `backend/manage-modify/services/hotel-reservation-entity-service/src/main/java/uk/co/whitbread/reservation/infrastructure/rest/client/ohip/service/OhipAdapterClient.java` (~line 1163)
- `backend/manage-modify/services/hotel-reservation-entity-service/src/main/java/uk/co/whitbread/reservation/infrastructure/rest/client/changelog/exception/ChangeLogException.java`

## How a scenario reproduces it

Row 19's three negative scenarios exclude `booking.opera.activity-log` and install
`opera.activity-log.no-content` (Opera 204), `opera.activity-log.rejected` (Opera 4xx) and
`opera.activity-log.server-error` (Opera 502). Each returns HTTP 500 with `{"errCode":0}`.

## Effect on the journey

The three scenarios ship **enabled** and assert HTTP 500 plus the single-Opera-call shape,
which is correct and worth guarding. They deliberately do **not** assert `errCode`: there is no
code to assert that distinguishes them, and asserting `0` would freeze the defect into the
suite. Add the per-failure `errCode` assertions here once the exception carries the adapter's
code (the assertion is deferred, not bent).
