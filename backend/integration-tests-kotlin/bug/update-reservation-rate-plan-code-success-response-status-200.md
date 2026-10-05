# updateReservationRatePlanCode OpenAPI status drift

- **Status**: closed as a service bug on 2026-08-31. Implementation code is the behavioral
  source of truth; the HTTP 201 annotation/OpenAPI declaration is documentation drift.
- **Endpoint**: `PUT /ohip/v1/reservations/rate-code` (ohip-adapter-service)
- **Flow doc**:
  `backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateReservationRatePlanCode.md`

## Code-defined behavior

HTTP 200 with an empty body. After the rate-plan update completes,
`HotelReservationController.updateReservationRatePlanCode` explicitly returns:

```java
return new ResponseEntity<>(HttpStatus.OK);
```

`HotelReservationControllerTest.updateReservationRatePlanCode_ShouldReturnReservation` also
asserts status 200, confirming that this behavior is intentional.

## Documentation drift

The checked-in OpenAPI operation and `@ApiResponse` annotation declare HTTP 201. They do not
control runtime behavior and are not authoritative for integration journey assertions.

## How a scenario reproduces it

Provision reservations with a target rate and matching availability, then call the public OHIP
endpoint with a valid `RatePlanChangeRequestDto`. Allow the reservation reads, availability
searches, and final reservation update to succeed. A future enabled journey should assert the
code-defined HTTP 200 response.
