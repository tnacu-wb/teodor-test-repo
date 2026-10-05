# updatePreferences crashes when preferencesCollections is omitted: NPE surfaces 500 with errCode 400

- **Status**: reproduced 2026-08-28 against the running integration stack (coordinator live
  probe: valid hotel and reservation id, request body without `preferencesCollections`).
- **Endpoint**: `PUT /ohip/v1/reservations/preferences` (ohip-adapter-service)
- **Flow doc**: `backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdatePreferences.md`
- **Chain**: `ReservationPreferencesRequestDto.preferencesCollections` carries only `@Valid`, no
  `@NotNull`, so the request passes Bean Validation → `UpdatePreferencesRequestOhipMapper`
  calls `.getPreferencesCollections().stream()` unguarded → `NullPointerException` before any
  Opera call → no exception handler → generic envelope.

## Expected

HTTP 400 with the service's declared validation error shape: a required collection missing from
the request is an input error the contract's 400 response exists for (add `@NotNull`, or define
an explicit empty-collection behaviour).

## Actual

HTTP 500 with body
`{"errCode":400,"debugMessage":"Cannot invoke \"java.util.List.stream()\" because the return value of \"...ReservationPreferencesRequest.getPreferencesCollections()\" is null","globalErrTextTemplate":"generic.server.exception"}`.
Zero Opera calls (the crash is before the fan-out).

Files:
- `backend/discover-search/services/ohip-adapter-service/src/main/java/uk/co/whitbread/ohip/infrastructure/rest/controller/reservation/model/in/ReservationPreferencesRequestDto.java`
- `backend/discover-search/services/ohip-adapter-service/src/main/java/uk/co/whitbread/ohip/infrastructure/rest/client/reservation/mapper/UpdatePreferencesRequestOhipMapper.java`

## How a scenario reproduces it

Send `{"hotelId": "<hotel>", "reservationsIds": ["<id>"]}` with no `preferencesCollections`
member to the endpoint.

## Effect on the journey

None enabled or disabled: the trigger is malformed input, which stays in the owning service's
tests as routine request validation — the journey suite records the defect in this file only. Row 84's accepted
design lists it as an excluded evidence note, now confirmed.
