# updateUdfc20 returns HTTP 200 although its contract declares HTTP 204

- **Status**: reproduced 2026-08-26 against the running integration stack by
  `UpdateUdfc20Spec` (scenario "a CIOL status is stored without a reservation read", which ships
  disabled on this bug).
- **Endpoint**: `PUT /v1/reservations/updateUdfc20` (hotel-reservation-entity-service)
- **Downstream endpoint**: `PUT /reservations/characterudfs` (ohip-adapter-service)
- **Flow doc**: `backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/UpdateUdfc20.md`

## Expected

HTTP 204 with an empty body. Both services' checked-in OpenAPI documents declare `204` as the
successful response, and neither declares a successful `200` response.

## Actual

HTTP 200 with an empty body. The isolated journey run captured:

```text
=== HTTP Response: Update Reservation UDFC20 ===
HTTP 200
content-length: 0
```

Both controller methods return Java `void` without setting `@ResponseStatus(HttpStatus.NO_CONTENT)`
or returning `ResponseEntity.noContent()`. Spring therefore uses its default successful response
status, HTTP 200:

- `ManageBookingController.updateUdfc20` in hotel-reservation-entity-service
- `UdfsController.updateCharacterUdfs` in ohip-adapter-service

The Swagger `@ApiResponse(responseCode = "204")` annotations describe the contract but do not set
the runtime response status.

## How the scenario reproduces it

`UpdateUdfc20Spec` provisions a normal one-room booking, calls the public HRE endpoint with a valid
`CIOL_STARTED` request, and allows the generic Opera reservation-update stub to succeed. The
scenario asserts the contracted HTTP 204 response and the expected one-write/no-read interaction.

The scenario ships **disabled** (`!` prefix) because the deployed response is HTTP 200. Re-enable
it after the public HRE endpoint returns HTTP 204. The downstream OHIP endpoint should also be
aligned with its own 204 contract so the internal API no longer has the same mismatch.
