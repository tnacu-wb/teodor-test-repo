# A booker-surname CDH search fails with 500 when a result carries no booker

- **Service**: hotel-reservation-entity-service
- **Endpoint**: `GET /v1/reservations/search/booking/cdh`
- **Flow doc**: `backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/SearchBookingCdh.md`
- **Reproduced by**: `journeys/hotelreservation/SearchBookingCdhSpec.kt`, scenario
  `a booker surname search over results CDH returns without a booker empties the page`
  (shipped disabled with a `!` prefix).

## Expected

`bookerLastName` is a filter. A CDH result that carries no `Booker` block cannot match the
requested surname, so it is dropped and the caller gets `200` with an empty page
(`searchResults = 0`, `responseLimitExceeded = false`) — the same answer as a result whose
booker carries a different surname.

## Actual

`500` with

```json
{"code":"999","details":["Cannot invoke \"uk.co.whitbread.reservation.domain.model.out.CdhBooker.getLastName()\" because the return value of \"uk.co.whitbread.reservation.domain.model.out.CdhResults.getBooker()\" is null"]}
```

The CDH call itself succeeded and returned a page; the failure is entirely in-process, after
the downstream hop.

## Chain

1. `CdhSearchBookingOutPortImpl.searchBookingsFromCdh` POSTs the criteria to
   cdh-adapter-service and hands the page to `CdhSearchBookingsResponseMapper.toModel`.
2. `toCdhResultsModel` copies a `Booker` into `CdhResults.booker` **only** when
   `cdhResultsDto.getBooker() != null`, so a result CDH returns without a booker keeps
   `booker == null`. That is a supported shape: the DTO field is optional and CDH omits it
   for bookings whose booker it does not hold.
3. `toFilterByBookerOrGuestLastnameModel` then runs

   ```java
   .filter(results -> bookerLastName.equalsIgnoreCase(results.getBooker().getLastName()))
   ```

   and dereferences that null.

Note the sibling branch one line below is written defensively — the `guestLastName` filter
streams `cdhResult.getGuests()` and matches with `anyMatch`, so an empty guest list simply
fails to match. Only the booker branch dereferences.

## Root cause

`CdhSearchBookingsResponseMapper.toFilterByBookerOrGuestLastnameModel` dereferences
`CdhResults.getBooker()` without a null check, although the mapper immediately above it
treats the booker as optional.

## Scope

- Any `bookerLastName` search that reaches a result without a booker fails, whatever the
  requested surname — the filter cannot even reject the result.
- One bookerless result in an otherwise fine page is enough: the filter runs over the whole
  list, so a single missing booker fails the entire request.
- The guest-surname filter and every unfiltered search are unaffected.

## Fix

Null-guard the booker before comparing, e.g.

```java
.filter(results -> results.getBooker() != null
    && bookerLastName.equalsIgnoreCase(results.getBooker().getLastName()))
```

Re-enable the disabled scenario afterwards.
