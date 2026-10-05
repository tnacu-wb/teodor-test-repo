# updateCarRegistrationComment OpenAPI status drift

- **Status**: closed as a service bug on 2026-08-31. Implementation code is the behavioral
  source of truth; the HTTP 200 annotation/OpenAPI declaration is documentation drift.
- **Endpoint**: `POST /ohip/v1/kiosk/updateComments` (ohip-adapter-service)
- **Flow doc**:
  `backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateCarRegistrationComment.md`

## Code-defined behavior

HTTP 201 with an empty body. After the Opera reservation comment update completes,
`CheckInController.updateCarRegistrationComment` explicitly returns:

```java
return new ResponseEntity<>(HttpStatus.CREATED);
```

`CheckInControllerTest.updateCarRegistrationComment_ShouldReturnOk` does not assert the response
status, but the controller return value is explicit.

## Documentation drift

The checked-in OpenAPI operation and `@ApiResponse` annotation declare HTTP 200. They do not
control runtime behavior and are not authoritative for integration journey assertions.

## How a scenario reproduces it

Provision one reservation, then call the public OHIP endpoint with its hotel and reservation id
plus a valid car-registration comment. Allow the single Opera reservation PUT to succeed. A
future enabled journey should assert the code-defined HTTP 201 response.
