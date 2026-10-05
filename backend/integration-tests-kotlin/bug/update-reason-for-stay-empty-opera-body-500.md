# updateReasonForStay crashes on an empty Opera success body: unguarded mapper surfaces 500 with errCode 400

- **Status**: reproduced 2026-08-28 against the running integration stack (coordinator live
  probe: one reserved room, `booking.opera.put-reservation` excluded,
  `custom.opera.put-reservation-empty-body` installed, one-id reasonForStay request).
- **Endpoint**: `PUT /ohip/v1/reservations/reasonForStay` (ohip-adapter-service)
- **Flow doc**: `backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateReasonForStay.md`
- **Chain**: Opera answers the change-reservation PUT `200` with an empty body → the collected
  `ChangeReservationDetails` list is empty → `UpdateReasonForStayResponseOhipMapper` calls
  `changeReservationDetails.get(0)` with no guard → `IndexOutOfBoundsException` → no
  `@ControllerAdvice`/`@ExceptionHandler` in the service → generic envelope.

## Expected

A deterministic mapped response. The write succeeded in Opera, so either a graceful success with
the ids derived from the request, or a specific mapped error — not an unhandled crash. At
minimum, not a 5xx whose body claims `errCode 400`.

## Actual

HTTP 500 with body
`{"errCode":400,"debugMessage":"Index 0 out of bounds for length 0","globalErrTextTemplate":"generic.server.exception"}`.
The errCode field carries the number 400 (not a registered `ErrorCode`), so the failure is both
unmapped and mislabelled.

Files:
- `backend/discover-search/services/ohip-adapter-service/src/main/java/uk/co/whitbread/ohip/infrastructure/rest/controller/reservation/mapper/UpdateReasonForStayResponseOhipMapper.java`
  (unguarded `get(0)` and `getReservations().getReservation().get(0)`)

## How a scenario reproduces it

Install a Booking with one `RESERVED` room, exclude `booking.opera.put-reservation`, install
`custom.opera.put-reservation-empty-body`, and send a one-id reasonForStay request.

## Effect on the journey

Row 77's accepted design records this branch as an evidence blocker and excludes it from the
enabled scenario set. A disabled correct-behaviour scenario is deferred until the expected
contract is agreed (the current behaviour offers nothing stable to assert); this file records
the confirmed reproduction. Contrast: the sibling alerts endpoint tolerates the same Opera state
into its normal 204 (proved enabled by `UpdateReservationAlertsOhipSpec`).
