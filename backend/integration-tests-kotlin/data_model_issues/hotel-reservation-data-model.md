# Hotel Reservation Entity - Booking model and default-stub gaps

This is a live queue of unresolved gaps that work inside
`backend/integration-tests-kotlin` can fix: Booking-model facts, default-stub shapes, and
testkit capabilities. Remove an entry when its blocker is resolved. Service-only bugs and
historical implementation notes belong in `bug/`, flow docs, or plans rather than here.
When no unresolved entries remain, leave this file empty.

## `GET /v1/reservations/changeLog`: `limit`/`offset` pass-through is unprovable today

- **Endpoint / scenario**: a paging scenario for `GET /v1/reservations/changeLog` (designed, not
  implemented).
- **Missing fact / stub shape**: the default `booking.opera.activity-log` stub deliberately does
  not match `limit`/`offset`, and journeys read WireMock state only through `callCount`.
- **Why the existing model/stubs cannot express it**: proving pass-through needs either a
  request-journal assertion capability in the testkit or a paging-matcher custom stub for a
  *happy* request shape; plan section 4 item 4 allows custom **error** stubs only. Both are
  batch-2 decisions, so the plan row's "limit/offset gives a second assertion" claim does not
  hold and no paging scenario was written.

## `GET /v1/reservations/search/booking/cdh`: no `CdhEndpoint` constant family

- **Endpoint / scenario**: the `expect("calls ...")` block of every scenario for this endpoint.
- **Missing fact / stub shape**: `testkit/mocks` has `OperaEndpoint` but no CDH equivalent, so CDH
  assertions can only be made per upstream: `callCount(Upstream.CDH) shouldBe 1`.
- **Why the existing model/stubs cannot express it**: plan section 4 item 5 permits adding
  `OperaEndpoint` constants only; a CDH endpoint enum is outside this batch's change surface.
  Recorded rather than fixed. The per-upstream count is still exact for this endpoint because the
  path makes exactly one CDH call.

## `POST /v1/reservations/cancellations`: `PAY_NOW` and the CCUI agent-stamping leg have no reachable journey

- **Status**: scope gap; nothing designed.
- **Endpoint / scenario**: none - recorded only.
- **Missing fact / stub shape**: (a) a way to mint a basket carrying `paymentOption = PAY_NOW`,
  and (b) a way to present a `WB-Authorization` CCUI JWT.
- **Why the existing model/stubs cannot express it**: `POST /v1/reservations` always mints a
  basket with a null `paymentOption`, and `BasketApi` (itself outside the plan's allowed change
  surface) exposes only `getBasket` and `changeStatus`. So the per-reservation
  `GET /v1/baskets/deposit-folios/{id}` reads, the content-service payment-information lookup,
  `chargesByReservationIds` on the OHIP call and the whole charge-reconciliation branch are
  unreachable. Separately, `release_ccui_agent_id_log` is evaluated only for an authenticated
  caller, and the journey suite has no way to present a CCUI `bookingFlow` claim, so the
  `PUT /ohip/v1/reservations/ccAgentId` leg - and the Opera reservation PUT the matrix row
  attributes to it - are untestable in either flag state.

## `POST /v1/reservations/cancellations/rollback`: basket non-interaction cannot be proved directly

- **Status**: **not blocking**. All five designed scenarios ship enabled in
  `journeys/hotelreservation/RollbackReservationCancellationsSpec.kt`; this records the one
  claim the suite proves only indirectly.
- **Endpoint / scenario**: every scenario of the rollback endpoint.
- **Missing fact / stub shape**: a way to assert that basket-service received **no** call - the
  flow doc's strongest claim is that rollback performs no basket read and no basket state
  transition.
- **Why the existing model/stubs cannot express it**: basket-service is a real deployed service,
  not one of the four WireMock upstreams (`OPERA`, `CDH`, `AEM`, `WORLDLINE`), so
  `callCount(Upstream.X) shouldBe 0` cannot reach it because an absence proof requires an
  installed mock. The journey's one-call/two-expect shape also excludes a follow-up
  `BasketApi.getBasket` assertion inside the scenario as a proxy.
- **Consequence for the plan**: the spec proves basket non-interaction indirectly instead. The
  no-cancellation-id scenario sends a basket reference that was demonstrably never looked up -
  the reference is echoed or nulled purely from the ohip response shape - and the two negative
  scenarios pass a literal, non-existent basket reference and still reach Opera. Proving it
  directly would need a testkit capability (a basket-service call journal), which is frozen for
  this batch.

## `POST /v1/reservations/save-deposit-folios`: the absent basket hop cannot be counted

The endpoint makes no basket-service call - `hotelId` and `reservationId` come straight from the
request body. basket-service is a real deployed collaborator rather than one of the four WireMock
upstreams (`Upstream` = OPERA/CDH/AEM/WORLDLINE, frozen), so its absence cannot be proved with
`callCount(...) shouldBe 0`. The spec's exact Opera counts plus the trace are what carry that
claim. No model or stub change is warranted.

## `POST /v1/reservations/rooms/delete`: unreachable on a create-reservation basket (found at trace stage, 2026-08-24)

Wave 4's plan assumes this endpoint acts on the multi-room OPEN basket built by the shared
`createReservation` setup. The trace
(`backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/DeleteReservationRooms.md`)
contradicts that:

- `removeRoom` validates the `token` query parameter against **`basket.originalBasketId`**, not
  against `tempBookingRef` (`HotelReservationInPortImpl:3973`).
- `originalBasketId` is populated only by the copy-booking (amend) flow, which creates the temp
  basket with the original basket's reference (`HotelReservationInPortImpl:3253`). Every other
  create path passes `null` (`BasketOutPort.createBasket(hotelId, channel, subChannel)` defaults
  `originalBasketId` to `null`), so a basket produced by `POST /v1/reservations` has none.
- With `originalBasketId = null`, `TokenUtils.isValid(token, null)` NPEs inside its own
  `try`/`catch` and returns false, so an unauthenticated caller always gets
  `DIGITAL_INVALID_TOKEN2` (errCode 120, 400) - no Opera call is ever made.
- Authenticating does not help: the controller passes `isNonRefundable = null`, so the
  non-refundable guard always runs and calls `getManageBookingInformation(..., basketReference =
  basket.getOriginalBasketId(), ...)`, whose first act is `basketOutPort.getBasketById(null)`.

Making the endpoint viable needs a temp basket created by `POST /v1/reservations/copy`,
which this plan lists as a hard blocker ("second set of Opera reservation identities not
expressible by the create-reservation default"), plus a valid `CipherUtils` token minted for the
*original* basket - obtainable only from `GET /v1/reservations/find`, i.e. the row's setup would be
create -> complete -> find -> copy -> delete-room. No Booking-model or default-stub change alone
unblocks it. Recommend dropping this endpoint from Wave 4 and moving it to the batch that unblocks
`POST /v1/reservations/copy`.

## `PUT /v1/reservations/amend/editRoom` needs an amend basket too

Traced 2026-08-24 (Wave 4 trace stage). Same root cause as
`POST /v1/reservations/rooms/delete`, with one extra escape hatch.

- `editRoom` validates the body's `token` against **`basket.originalBasketId`**
  (`HotelReservationInPortImpl.validateRequest`, line ~3577), and the non-refundable guard calls
  `isBookingNonRefundable(hotelId, release_amend_distribution_single_call ? tempBasketRef :
  basket.getOriginalBasketId(), ...)` (line ~3585).
- A basket minted by the wave-3/wave-4 `createReservation` setup has `originalBasketId = null`, so
  an unauthenticated call always returns `DIGITAL_INVALID_TOKEN2` (errCode 120, HTTP 400) with no
  Opera call, and an authenticated call instead dies in `basketOutPort.getBasketById(null)` inside
  the guard.
- **Unlike `POST /v1/reservations/rooms/delete`, one combination is still reachable**: pin
  `release_amend_distribution_single_call` ON and send `bookingChannel.channel = "DISTR"`. The
  flag-and-DISTR pair skips the token check, and the same flag points the non-refundable guard at
  `tempBookingRef`. The basket itself can stay a `PI`-channel basket (the deployed rules service
  rejects `DISTR` *creation*, but `bookingChannel` here is a request field, not the basket's).
- Consequence: `release_amend_distribution_single_call` cannot get an ON/OFF pair on this
  row - its OFF state has no reachable happy path without a copy-minted basket. Only a pinned-ON
  scenario plus this note is possible.
- No Booking-model or default-stub change unblocks the OFF state; it needs
  `POST /v1/reservations/copy` plus a `CipherUtils` token minted for the original basket.

## `GET /v1/reservations/basket/{basketReference}`: the basket hop cannot be counted

Found 2026-08-24 (Wave 4 design stage). Same shape as the rollback and
save-deposit-folios blockers above.

- basket-service is real (`GET /v1/baskets/{reference}`), so the flow doc's basket-not-found and
  basket-5xx branches have no negative journey and the single basket read cannot be asserted.
