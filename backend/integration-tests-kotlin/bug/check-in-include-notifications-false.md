# Check-in sends `includeNotifications=false`

- **Endpoint**: `POST /ohip/v1/kiosk/checkIn` (ohip-adapter-service)
- **Chain**: `POST /fof/v1/hotels/{HotelId}/reservations/{ReservationId}/checkIns`

## Expected

The Opera check-in request should send `includeNotifications=true`. The
`CheckInRequest` model initializes this field to `true`.

## Actual

The controller mapper builds `CheckInRequest` through Lombok's generated builder. The
field has no `@Builder.Default` annotation. The builder therefore ignores the field
initializer and sends the Java default value, `false`.

The request still sends `ignoreWarnings=true`,
`overrideAdvancePaymentValidation=true`, and
`fetchReservationInstruction=["ReservationDetail"]`.

## Impact

Opera does not receive the requested notification setting. The current integration stub
does not match `includeNotifications`, so the journey cannot detect the defect.

## Required fix and validation

Preserve `true` when `CheckInRequest` is created through its builder. Then make the Opera
stub match all fixed request fields. Add a disabled journey that expects
`includeNotifications=true`, and enable it after the service fix.
