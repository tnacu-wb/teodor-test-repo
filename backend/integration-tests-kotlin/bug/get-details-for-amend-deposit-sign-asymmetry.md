# getDetailsForAmend returns the deposit with the opposite sign to getReservationAmounts

**Service:** ohip-adapter-service
**Endpoint:** `GET /ohip/v1/reservations/amend/getDetailsForAmend`
**Flow doc:** `backend/integration-tests-kotlin/flows/ohip-adapter-service/GetDetailsForAmend.md`
**Scenarios (enabled):** `ohip.GetDetailsForAmendSpec` — the part-paid happy-path
scenarios assert the current as-is (negative) sign, with a comment linking this file.

## Status

**Unconfirmed inconsistency — awaiting a service-team ruling on the intended sign.**
Unlike the other `bug/` entries, the correct behavior is not determinable from the code,
so the scenarios stay **enabled** and assert the current pass-through behavior rather than
shipping disabled against an assumed correct value. The disabled-scenario policy applies only
once the intended behavior is known. If the team rules the amend sign wrong, flip the affected
assertions to the positive sign, disable them with the correct expectation preserved, and link
this file from the scenario.

## Chain

`AmendController.getReservationDetailsForAmend` → `AmendInPortImpl.getAmendSummary` →
`AmendOutPortImpl.getRateInfoSummary` → `OhipReservationClient.getRateInfo`
(`GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo?summaryInfo=true&type=Reservation&id={id}&detailDate={today}`).

## The asymmetry

Opera reports an already-paid amount as a **negative** `summary.deposit` (e.g. a £25.00
payment arrives as `-25.00`).

- `GET /ohip/v1/reservations/amounts` (`HotelReservationOutPortImpl.getReservationAmounts`)
  **negates** it: the API answers `deposit: 25.00`.
- `GET /ohip/v1/reservations/amend/getDetailsForAmend` (`AmendOutPortImpl`) stores it
  **as-is**: the API answers `deposit: {"<reservationId>": -25.00}`.

The same Opera fact therefore reaches consumers with opposite signs depending on which
OHIP endpoint they call. Either the amend endpoint should negate like its sibling, or
the difference is intentional (raw Opera passthrough) and should be documented in the
endpoint contract.

## Reproduction

`GetDetailsForAmendSpec`'s part-paid scenario: a Booking with
`BookingRoom(amountAlreadyPaid = 25.0)` installs the default
`booking.opera.reservation-amounts` stub, which emits Opera's negative `deposit: -25.00`
(matching real Opera behavior, see `OperaRateInfoStubs.reservationAmountsBody`). The
endpoint responds `deposit["<reservationId>"] == -25.00`; calling
`GET /ohip/v1/reservations/amounts` for the same Booking responds `deposit == 25.00`
(see `GetReservationAmountsSpec`).

## Fix hint

Decide the contract: if consumers expect the paid amount as a positive figure (as the
amounts endpoint provides), negate `summary.deposit` in `AmendOutPortImpl` before
keying it into the response map. If raw passthrough is intended, state it in the API
documentation so consumers do not reconcile the two endpoints incorrectly.
