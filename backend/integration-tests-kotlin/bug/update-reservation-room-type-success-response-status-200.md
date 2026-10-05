# updateReservationRoomType OpenAPI status drift

- **Status**: closed as a service bug on 2026-08-31. Implementation code is the behavioral
  source of truth; the HTTP 201 annotation/OpenAPI declaration is documentation drift.
- **Endpoint**: `PUT /ohip/v1/reservations/roomTypeUpdate` (ohip-adapter-service)
- **Flow doc**:
  `backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateReservationRoomType.md`

## Code-defined behavior

HTTP 200 with a `RoomTypeChangeResponseDto` containing the request's `basketReferenceId`.
After the room-type change completes,
`HotelReservationController.updateReservationRoomType` explicitly returns:

```java
return new ResponseEntity<>(
    new RoomTypeChangeResponseDto(reservationRequest.getBasketReferenceId()), HttpStatus.OK);
```

`HotelReservationControllerTest.updateReservationRoomType_ShouldReturnReservation` also
asserts status 200, confirming that this behavior is intentional.

## Documentation drift

The checked-in OpenAPI operation and `@ApiResponse` annotation declare HTTP 201. They do not
control runtime behavior and are not authoritative for integration journey assertions.

## How a scenario reproduces it

Provision reservations with a target room type and matching availability, then call the public
OHIP endpoint with a valid `RoomTypeChangeRequestDto`. Allow the reservation reads,
availability searches, and final reservation update to succeed. A future enabled journey should
assert the code-defined HTTP 200 response and basket-reference body.
