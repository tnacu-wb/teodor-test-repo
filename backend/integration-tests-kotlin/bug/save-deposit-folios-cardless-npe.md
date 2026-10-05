# saveDepositFolios NPEs when the reservation has no saved payment card

- **Endpoint**: `POST /ohip/v1/reservations/deposit-folios` (ohip-adapter-service)
- **Chain**: `GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}` (Opera reservation
  read) → card enrichment → `POST /csh/v1/.../depositFolios` (Opera cashiering)

## Expected

A reservation whose Opera payload carries no payment method with a card (a cardless or
pay-on-arrival world) should still have its deposit folio posted — the folio's own
`defaultPaymentMethod` (or no card enrichment at all) driving the cashiering POST — or at
worst a mapped OHIP error, never a raw NullPointerException.

## Actual

The card-enrichment mapper takes the first payment method and dereferences it without a
null guard, so the whole request fails with HTTP 500:

```
errCode: 400
Cannot invoke "...ReservationPaymentMethodType.getPaymentCard()" because
"resPaymentMethod" is null
```

No cashiering POST is attempted.

## Reproduction

`journeys/ohip/SaveDepositFoliosSpec` — scenario "a cardless reservation's deposit folio is
still posted to cashiering" (disabled; re-enable when fixed). It installs a normal cardless
Booking (no `operaPaymentCard` on the room, so the default `booking.opera.get-reservation`
payload has no payment card) and posts a deposit folio for that reservation.

The same bug reproduces one layer up through hotel-reservation-entity-service, which adds no
guard of its own: `journeys/hotelreservation/SaveDepositFoliosSpec` — scenario "a cardless
reservation's deposit folio is still posted to cashiering" (disabled, same reason). Re-enable
both when the adapter's card-enrichment mapper null-guards the payment method.

Observed 2026-08-21 during batch-4 negative-path work (also flagged from the
happy-path phase as a bug candidate; `PUT /ohip/v1/reservations/confirmAmend` with
`markAsPayOnArrival=true` on a cardless reservation NPEs in
`PayOnArrivalReservationRequestOhipMapper` for the same missing-card reason).
