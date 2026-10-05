---
inclusion: fileMatch
fileMatchPattern: "backend/arrive-stay-leave/services/kiosk-checkin-service/**"
---

# Structure

The service uses hexagonal architecture under `uk.co.whitbread.kiosk`:

- `domain/model` — check-in, confirmation, room allocation, and profile domain models.
- `domain/ports/primary/KioskInPort` — inbound room and check-in operations.
- `domain/logic/KioskInPortImpl` — profile/comment sequencing, balance checks, card mapping, and check-in orchestration.
- `domain/ports/secondary/KioskOutPort` — downstream hotel and reservation capabilities.
- `infrastructure/rest/controller/kiosk` — REST controller, HTTP DTOs, and MapStruct mappers.
- `infrastructure/rest/client/ohip` — OHIP Adapter implementation for rooms, profiles, comments, amounts, and check-in.
- `infrastructure/rest/client/reservation` — Reservation Service payment-confirmation client.
- `infrastructure/config` — security, WebClient, mapper, and adapter wiring.

Keep WebClient and controller DTOs outside the domain ports. Add downstream operations to
`KioskOutPort` and its adapter rather than calling clients directly from the controller. The
controller deliberately sets allocation status after mapping; check behavior before changing the
existing MapStruct `status` warning.
