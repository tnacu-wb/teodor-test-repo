---
inclusion: fileMatch
fileMatchPattern: "backend/arrive-stay-leave/services/kiosk-checkin-service/**"
---

# Product

`kiosk-checkin-service` is the Arrive Stay Leave orchestration service for Premier Inn hotel kiosk
arrival. It allocates a suitable vacant room or reports a requested room's housekeeping state, then
coordinates profile updates, reservation comments, payment confirmation, and OHIP check-in.

## Capability

- Exposes `POST /v1/kiosk/allocate` and authenticated `POST /v1/kiosk/checkIn`.
- Uses OHIP Adapter for hotel operations and Reservation Service for non-zero payment confirmation.
- Maps configured room preferences, card schemes, and reservation comment types.
- Rejects unavailable rooms, unpaid balances, missing card details, and partially paid check-ins with explicit error codes.
- Returns two keys and the OHIP `UDFC07` value as the Wi-Fi access code after check-in.

Guest identities, reservation details, payment tokens, and room assignments are sensitive. Never log
or commit real values; retain field masking and use placeholders in profiles, examples, and tests.
