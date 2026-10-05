# updateReservationAlerts rejects the documented Opera wire value for area with an unmapped 500

- **Status**: reproduced 2026-08-28 against the running integration stack (coordinator live
  probe: valid hotel and reservation id, one alert with `area` = `"CheckIn"`).
- **Endpoint**: `PUT /ohip/v1/reservations/alerts` (ohip-adapter-service)
- **Flow doc**: `backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateReservationAlerts.md`
- **Chain**: `AlertDto.area` carries no validation constraint → the generated
  `ReservationAlertMapperImpl` resolves it with `Enum.valueOf(AlertAreaType.class, area)`, which
  accepts only the constant names (`CHECKIN`, `CHECKOUT`, `RESERVATION`, `BILLING`, `INHOUSE`)
  → any other value, including Opera's own documented wire values (`CheckIn`, ...), throws
  `IllegalArgumentException` inside mapping, before that reservation's PUT → no exception
  handler → generic envelope.

## Expected

HTTP 400 with the declared validation error shape for an unsupported `area` value. Note the
asymmetry that makes this easy to hit: the enum's `@JsonCreator fromValue` accepts exactly the
wire values (`CheckIn`), while this mapper accepts exactly the constant names (`CHECKIN`), so a
caller copying the value Opera itself uses is rejected with a crash.

## Actual

HTTP 500 with body
`{"errCode":400,"debugMessage":"No enum constant uk.co.whitbread.hotel.ohip.adapter.generated.models.AlertAreaType.CheckIn","globalErrTextTemplate":"generic.server.exception"}`.

Files:
- `backend/discover-search/services/ohip-adapter-service/src/main/java/uk/co/whitbread/ohip/infrastructure/rest/controller/reservation/model/in/AlertDto.java`
- generated `ReservationAlertMapperImpl.toAlertTypeDto` (`Enum.valueOf` on the raw string)

## How a scenario reproduces it

Send a valid alerts request whose alert `area` is `"CheckIn"` (the Opera wire value) instead of
`"CHECKIN"` (the constant name).

## Effect on the journey

None enabled or disabled: the trigger is input-shaped and stays in the owning service's tests
as routine request validation, so it is recorded here only. Row 85's accepted design lists it as an excluded suspected
defect, now confirmed. The enabled journey `UpdateReservationAlertsOhipSpec` sends the constant
name `CHECKIN` and proves the mapper's constant→wire translation on the Opera body.
