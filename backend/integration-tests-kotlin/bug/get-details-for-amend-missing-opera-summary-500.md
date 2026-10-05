# getDetailsForAmend returns 500 when Opera reports no rate-info summary

**Service:** ohip-adapter-service
**Endpoint:** `GET /ohip/v1/reservations/amend/getDetailsForAmend`
**Flow doc:** `backend/integration-tests-kotlin/flows/ohip-adapter-service/GetDetailsForAmend.md`
**Scenario (disabled with the correct assertion preserved):** `ohip.GetDetailsForAmendSpec` →
`!aggregates a reservation whose Opera response carries no summary as nothing to add`

## Chain

`AmendController.getReservationDetailsForAmend` → `AmendInPortImpl.getAmendSummary` →
`AmendOutPortImpl.getRateInfoSummary` → `OhipReservationClient.getRateInfo`
(`GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo?summaryInfo=true&type=Reservation&id={id}&detailDate={today}`).

## Expected

`AmendOutPortImpl` seeds the accumulator at zero (`net`, `totalCostOfStay`,
`outStandingCostOfStay` all `BigDecimal(0)`). A reservation Opera answers `200` for but
reports no `summary` block for contributes no money, so the endpoint should answer `200`
with the scalars still at zero (and no entry, or a null entry, in `deposit`/`guestPay`).

## Actual

HTTP `500`. The reduction dereferences the summary unguarded:

```java
amendSummaryResponse.setNet(amendSummaryResponse.getNet().add(rateInfo.getSummary().getNet()));
```

`rateInfo.getSummary()` is `null`, so the very first accumulation throws
`NullPointerException`, which the controller advice maps to an internal server error. The
same crash occurs when `summary` is present but `net`, `totalCostOfStay` or
`outStandingCostOfStay` is null.

## Reproduction

The scenario installs the default stub world for a one-room Booking with
`excluded = setOf(OPERA_RESERVATION_AMOUNTS_STUB_ID)` and installs
`reservationAmountsWithoutSummary(booking)`
(`stubs/opera/custom/OperaReservationAmountsErrorStubs.kt`, stub id
`opera.reservation-amounts.no-summary`), which reuses the default reservation-amounts
matcher and answers `200` with a body carrying only the `links` envelope. The single
Opera `rateInfo` read is made (`callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1`) and
the response is `500` instead of the zeroed `200`.

## Family

Same family as the five existing bugs where unguarded access to an empty or absent nested
Opera collection/field returns HTTP 500:
`bug/delete-routing-instructions-empty-reservation-500.md`,
`bug/get-reservation-by-reservation-id-empty-reservation-500.md`,
`bug/get-reservation-amounts-empty-folio-windows-500.md`,
`bug/update-special-requests-empty-reservation-500.md`,
`bug/create-memo-empty-reservation-500.md`. One guard pattern would close all of them.

## Fix hint

Skip (or treat as zero) a `rateInfo` response with no `summary`, and null-guard the three
accumulated amounts, before reducing.
