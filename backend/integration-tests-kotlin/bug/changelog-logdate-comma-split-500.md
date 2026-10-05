# getChangeLog's unguarded logDate comma split (latent, NOT reproducible on the integration stack)

- **Status**: **not reproduced** — re-tested 2026-08-24 against the running integration stack
  by matrix row 19's journey `GetReservationChangeLogSpec`. The happy path returns **200**, so
  the scenario ships **enabled**. The unguarded code below is still a latent crash; this file
  is kept as the record of why it does not fire here.
- **Endpoint**: `GET /v1/reservations/changeLog` (hotel-reservation-entity-service)
- **Flow doc**: `backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/GetReservationChangeLog.md`
- **Chain**: `ChangeLogController` -> `ChangeLogInPortImpl` -> `ChangeLogOutPortImpl` ->
  ohip-adapter `GET /ohip/v1/hotels/{hotelId}/reservations/changeLog` -> Opera
  `GET /rsv/v1/hotels/{hotelId}/reservations/activityLog`

## The unguarded code

`ChangeLogOutPortImpl.getChangeLog` post-processes every entry with an unguarded split:

```java
response.getActivityLog().getActivityLog().forEach(log -> {
  var splitDateTime = log.getLogDate().split(",");
  log.setUser(log.getLogUserName());
  log.setDate(splitDateTime[0].trim());
  log.setTime(splitDateTime[1].trim());
});
```

`split(",")` on a value with no comma yields a one-element array, so `splitDateTime[1]` would
throw `ArrayIndexOutOfBoundsException` and the request would fail with HTTP 500. The same code
NPEs if `logDate` is absent, and NPEs on `response.getActivityLog()` if the adapter returns an
envelope without an activity log.

File: `backend/manage-modify/services/hotel-reservation-entity-service/src/main/java/uk/co/whitbread/reservation/infrastructure/rest/client/changelog/ChangeLogOutPortImpl.java`

## Why it does not fire against the default stub

The original trace assumed hotel-reservation-entity-service sees the frozen default stub's
literal `"logDate": "2026-01-15T10:00:00Z"` (no comma). It does not. Opera's
`operareservationsv0api.yaml` declares `logDate` as `type: string, format: date-time`, so
ohip-adapter's generated client parses it into a date-time and re-serializes it into its own
`ChangeLogTypeDto` (`logDate: type: string`) using the JVM's **localized short date-time**
form. The value hotel-reservation-entity-service actually receives is therefore
`1/15/26, 12:00 AM` — which **does** contain a comma, so the split succeeds.

Observed response on the integration stack (row 19 happy scenario, testId
`...change-log-is-returne-epk4c9`):

```json
{"activityLog":{"activityLog":[{"date":"1/15/26","time":"12:00 AM","actionType":"UPDATE RESERVATION",
"actionDescription":"Room type changed from LOWDBL to TWIN","user":"FRONTDESK1"}],
"totalPages":1,"offset":0,"limit":20,"hasMore":false,"totalResults":1,"count":1}}
```

## Residual risk (why this file is kept, not deleted)

The happy path is green only because of a locale coincidence in ohip-adapter's serialization:

1. The comma is supplied by `Locale`-dependent formatting. On a JVM locale whose short
   date-time form has no comma, every `getChangeLog` call 500s.
2. `date`/`time` are locale-formatted strings, not the ISO values Opera sent, so the endpoint's
   public contract silently depends on the adapter JVM's default locale and time zone (note
   `10:00:00Z` surfacing as `12:00 AM`).
3. A `logDate` that is null, or an adapter envelope with no `activityLog`, still NPEs.

Row 19's journey therefore asserts only that `date` is present, never its formatted value. A
locale change must not silently rewrite the assertion's meaning.
