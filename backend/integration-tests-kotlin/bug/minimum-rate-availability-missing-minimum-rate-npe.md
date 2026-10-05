# getMultiHotelAvailabilityV2 NPEs when an Opera room stay carries no minimumRate

- **Endpoint**: `POST /ohip/v2/hotels/availabilities` (ohip-adapter-service)
- **Chain**: `POST /parext/v1/hotels/minimumRateAvailability` (Opera minimum-rate search)

## Expected

An Opera room stay without a `minimumRate` block (a property with no sellable offer for
the window) should still be summarised — the hotel returned with its `availability`
status and a null/absent `minimumRate` — or at worst be skipped from
`hotelAvailabilityResults`.

## Actual

The result mapper dereferences `minimumRate` unconditionally and the whole request fails
with HTTP 500:

```
errCode: 400
Cannot invoke "...OfferTotalType.getAmountAfterTax()" because the return value of
"...SearchPropertyRoomStayType.getMinimumRate()" is null
```

## Reproduction

`GetMultiHotelAvailabilityV2Spec` — scenario
"an Opera room stay without a minimum rate is still summarised" (disabled; re-enable when
fixed). It installs `custom.opera.minimum-rate-availability-missing-rate`
(`minimumRateAvailabilityWithoutMinimumRate`), which keeps the generic minimum-rate
matchers but strips the `minimumRate` key from every room stay, then calls the endpoint
with a two-hotel booking.

Observed 2026-08-21; evidence example:
`build/test-evidence/minimum-rate-multi-hotel-availability-...-an-opera-ro-viz929/evidence.txt`.
