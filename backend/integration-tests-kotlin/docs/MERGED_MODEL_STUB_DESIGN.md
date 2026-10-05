# Merged model/stub design — config-reads batch (phase 3 barrier)

Consolidates 14 per-group design reports for the unstubbed-Opera-endpoints plan
(`backend/integration-tests-kotlin/docs/UNSTUBBED_OPERA_ENDPOINTS_PLAN.md`). One concept =
one Booking fact, one stub ID, one gate. All 14 group reports were present and usable.

## Global conventions (resolved conflicts)

1. **Custom/error stub ID prefix**: every phase-two custom or error stub uses
   `custom.opera.<name>` (own unique ID, installed via `excluded` + `installStub`; the
   `replacements` mechanism has been removed). This supersedes the per-group variants `test.opera.*`
   (reports 1, 11), `journey.opera.*` (report 3), `opera.*.bad-request` (report 2), and
   `booking.opera.*.error` (report 8). Existing custom stubs (`getReservationEmpty`,
   `opera.get-reservation.bad-request`, `opera.put-reservation.bad-request`,
   `creditCardInfoFailure`) keep their current IDs and are reused as-is.
2. **Physical-room inventory fact (dedup of reports 10 + 13)**: ONE fact —
   `Hotel.physicalRooms: List<HotelPhysicalRoom>`. Report 13's separate
   `Hotel.housekeepingRooms` / `HotelHousekeepingRoom` is **dropped**; the
   housekeeping-overview stub reads `HotelPhysicalRoom.housekeepingStatus` instead.
   One gate source feeds two stubs (vacant-rooms, housekeeping-overview).
3. **Pre-check-in fact family (reports 3, 4, 12)**: three distinct world states, three
   facts, all on `BookingRoom`, all requiring `reservationId`:
   - `preCheckInAvailable: Boolean` — Opera *can* record mobile pre-check-in (gates the
     preCheckIn POST stub; endpoints 1.4).
   - `preRegistration: PreRegistration?` — pre-registration *has completed* (gates the
     attachment-delete stub and enriches the reservation GET; endpoints 1.5/1.6).
   - `assignedRoomId: String?` — a physical room is assigned (gates the check-in stub;
     endpoint 3.3). Report 11 (allocate) deliberately uses no fact — the room number is
     endpoint input; the room-assignment stub gates on reservation rooms alone.
4. **Shared get-reservation response enrichment (reports 4 + 5)**: ONE coordinated
   widening of `getReservationResponse` in `OperaReservationByIdStubs.kt` (same stub ID
   `booking.opera.get-reservation`), covering both the pre-registration block and the
   routing-instructions/comments block. Land it once, in batch 1, with a single
   characterization-test update and one full-suite rerun.
5. **Flag posture everywhere**: opera-token-service flags (`release_ohip_use_token_service`,
   `release_ohip_use_token_refresh_skew`) are environment-pinned OFF — never
   overridden, one spec comment per journey. Request-scoped flags covered in both states
   in every reachable state: `mobile_preRegistered_repurpose` (1.4),
   `release_availability_from_different_room_classes` (2.1 /distr),
   `release_set_cnp_booking_alerts` (4 movePaymentDetails),
   `release_distr_booking_fee` (4 confirmAmendSingleCall).
6. **Known baseline failures tolerated**: `ConfirmReservationSpec` retry-timing
   flake, `InitSecureFieldsSpec` 502. All negative scenarios that override a stub on the
   shared retry-spec PUT path inherit the same ~19-30s retry-vs-30s-timeout risk; verify
   retry budget or pick a non-retried status before implementing each.
7. **rules-agent-entity-service is a real compose service** (not a WireMock target): no
   stubbing, no `callCount`. Scenarios depending on seeded rules data (BI-1/BI-3, V2-4,
   MHA-HP3-min, B-series substitutions) are flagged conditional; on missing seed data,
   record and skip per the failure policy.
8. **`callCount` is per-upstream** — exact Opera totals are pinned at implementation time
   after empirically confirming whether the Opera OAuth token call counts (reports 5, 6,
   8, 13, 14 all raise this once; resolve it once, in the first batch-1 implementation,
   and reuse the answer).

---

## Consolidated Booking-model changes (7 facts, all backward-compatible)

| # | Fact | Owner | Serves | Source reports |
| - | --- | --- | --- | --- |
| M1 | `preCheckInAvailable: Boolean = false` — "Opera can record mobile pre-check-in for this reservation; gates the Opera preCheckIn stub" | `BookingRoom` | 1.4 pre-checkin/pre-register | 3 |
| M2 | `preRegistration: PreRegistration?` with `PreRegistration(regCardAttachmentId: String? , regCardFileName: String?, preCheckInAlertId: String?)` — completed pre-registration state; enriches reservation GET (preRegistered/attachments/alerts) and gates the attachment-delete stub | `BookingRoom` | 1.5/1.6 attachments | 4 |
| M3 | Extend `RoutingInstruction`: `payeeProfileId: String = "500001"`, `instructions: List<RoutingFolioInstruction> = emptyList()`; new `RoutingFolioInstruction(daily=true, creditLimit?, routingLinkId?, transactionCodes=[], billingCodes=[])` | `BookingRoom.routingInstructions` | 1.7 routing delete; also read by section-4 movePaymentDetails scenarios (folioWindowNumber only) | 5 |
| M4 | `reservationComments: List<ReservationComment> = emptyList()`; `ReservationComment(title, text="Integration test note", commentId="1001", type="RESERVATION")` — "Business Notes" drives the 1.7 cleanup PUT; reusable for future memo endpoints | `BookingRoom` | 1.7 | 5 |
| M5 | `Rate.displaySet: String? = null` — rate-plan classification display set Opera reports | `Rate` (`Rates.kt`) | 2.1 negotiated display-set filter (B3) | 7 |
| M6 | `physicalRooms: List<HotelPhysicalRoom> = emptyList()`; `HotelPhysicalRoom(roomId, roomType, housekeepingStatus="Clean", frontOfficeStatus="Vacant", floor: String? = null)` — the hotel's Opera front-office physical-room inventory with housekeeping state | `Hotel` | 3.1 vacant rooms + 3.4 housekeeping (single fact, dedup of reports 10/13) | 10, 13 |
| M7 | `assignedRoomId: String? = null` — "Opera room number assigned to this reservation; gates the front-office check-in stub" | `BookingRoom` | 3.3 kiosk check-in | 12 |

No model change for: 1.1, 1.2/1.3, 1.8, 2.2, 2.3, 3.2, and all of section 4 (endpoint
inputs stay in typed-client requests, never in `Booking`).

Fixture additions (not model changes): a second multi-hotel-capable preset in
`testkit/presets/Hotels.kt` (report 7); a checked-in minimal valid-PDF base64 constant
for attachments (report 4).

Flag-enum addition: `OhipFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE("mobile_preRegistered_repurpose")`.

---

## Consolidated default-stub changes

### New default stubs (13)

| # | Stub ID | File | Matcher | Gate (`DefaultStubs.kt`) | Reports |
| - | --- | --- | --- | --- | --- |
| S1 | `booking.opera.delete-reservation` | `OperaReservationDeletionStubs.kt` | `DELETE /rsv/v1/hotels/{h}/reservations/{r}` (+ `Content-Length: 0` if reliable) → 204 | existing `hotel != null && roomsWithReservationIds.isNotEmpty()` block | 1 |
| S2 | `booking.opera.pre-check-in` | `OperaPreCheckInStubs.kt` | `POST /rsv/v1/.../preCheckIn`, real caller body shape → 200 with non-empty `links` | rooms with `preCheckInAvailable` (M1) | 3 |
| S3 | `booking.opera.add-file-attachment` | `OperaFileAttachmentStubs.kt` | `POST /med/config/v1/fileAttachments`, `x-hotelid`, body `$.linkType=="RESERVATION"`, `$.linkId=={reservationId}` → 200 minimal body | reservation rooms (widens install set suite-wide — characterization update) | 4 |
| S4 | `booking.opera.delete-reservation-attachment` | `OperaFileAttachmentStubs.kt` | `DELETE /rsv/v1/.../attachments/{regCardAttachmentId}` → 204 | rooms with `preRegistration?.regCardAttachmentId != null` (M2) | 4 |
| S5 | `booking.opera.delete-routing-instruction` | `OperaRoutingInstructionDeleteStubs.kt` | `DELETE /csh/v1/.../routingInstructions/folio` + required query params (`payeeId`, `folioWindowNo`, `daily`, 7 weekday flags, `retrievePostingsForRoomRouting=false`, optional params when facts carry them) → 200 empty | reservation rooms with a routingInstruction having non-empty `instructions` (M3) | 5 |
| S6 | `booking.opera.multi-hotel-availability` | `OperaMultiHotelAvailabilityStubs.kt` | `GET /par/v1/availability`, `x-hubid`, mutually exclusive mapping families: ratePlanSet-shaped (+ empty-fallback for unconfigured sets — mandatory, service always queries PBN and PBF) and ratePlanCode-shaped, using `absent`-param discriminators | `hasAvailabilityData` without the `hotels.size == 1` clause | 7 |
| S7 | `booking.opera.multi-hotel-negotiated-availability` | same file | same URL, `reservationProfileType=Company` + `attachedProfileId` | `companies.isNotEmpty()` && any rate `ratePlanSet == "NEGOTIATED"` | 7 |
| S8 | `booking.opera.minimum-rate-availability` | `OperaMinimumRateAvailabilityStubs.kt` | `POST /parext/v1/hotels/minimumRateAvailability`, `x-hotelid` = first hotel, body dates+hotelIds, tolerant on `rooms[].roomTypes`; one mapping per `splitDateRange` interval (reuse `OperaDateInterval.kt`); response always emits `minimumRate` + `availability` (mapper NPEs otherwise) | multi-hotel-relaxed `hasAvailabilityData` | 8 |
| S9 | `booking.opera.multi-room-rate-availability` | `OperaMultiRoomRateAvailabilityStubs.kt` | `POST /parext/v1/hotels/multiRoomRateAvailability`, `hotelHeaders`, JSONPath on hotelIds/dates/rooms; tag-echo convention (`tag` = PMS room type, KDoc'd) | existing `hotel != null && hasAvailabilityData` block | 9 |
| S10 | `booking.opera.vacant-rooms` | `OperaVacantRoomsStubs.kt` | `GET /fof/v1/hotels/{h}/rooms` with full query (`roomType`, `hotelRoomStatus=Clean`, `hotelFORoomStatus=Vacant`, `includeAllRoomConditions=true`, `limit=60`), `x-hotelid`; one mapping per distinct roomType; response filters Clean+Vacant physicalRooms of that type | hotels with `physicalRooms.isNotEmpty()` (M6) | 10 |
| S11 | `booking.opera.room-assignment` | `OperaRoomAssignmentStubs.kt` | `POST /fof/v1/.../roomAssignments`, `x-hotelid`, body `$.criteria.roomId` + `$.criteria.reservationIdList[0].id` → 2xx `links` body | reservation rooms (like `activityLog`) | 11 |
| S12 | `booking.opera.check-in` | `OperaCheckInStubs.kt` | `POST /fof/v1/.../checkIns`, `x-hotelid`, body `reservation.roomId == assignedRoomId` + `ignoreWarnings==true` → in-house reservation snapshot from facts (`reservationStatus="InHouse"`, `currentRoomInfo.roomId=assignedRoomId`) | reservation rooms with `assignedRoomId != null` (M7) | 12 |
| S13 | `booking.opera.housekeeping-overview` | `OperaHousekeepingStubs.kt` | `GET /hsk/v1/hotels/{h}/housekeepingOverview` with required `roomIdText`; catch-all (any roomIdText → wrapper-shaped empty overview, added first) + per-room mappings (`roomIdText equalTo roomId` → status from `HotelPhysicalRoom.housekeepingStatus`) | hotels with `physicalRooms.isNotEmpty()` (M6 — **same gate source as S10**, two stubs) | 13 (fact merged with 10) |

Every new builder: purpose-focused KDoc, no `testId` param, Docker-free unit test,
and mapping-doc row filled on delivery (rows 193-207 + corrections below).

### Widened existing stubs (5 definite + 3 verify-first)

| # | Stub | Widening | Reports |
| - | --- | --- | --- |
| W1 | `booking.opera.get-reservation` (response only, `OperaReservationByIdStubs.kt`) | ONE coordinated enrichment: (a) when `preRegistration != null`: `preRegistered: true`, `attachments[]`, `alerts[]` (alert code/description per service constants); (b) `routingInstructions[].folio` gains `payeeInfo.payeeId.id` + `instructions[]` (weekday flags all-true, `timeSpan` from stay when daily); (c) **always emit `comments`** (from `reservationComments`, `[]` when none — absent comments NPEs `deleteBusinessNotes`). Additive, fact-gated; update characterization tests once | 4, 5 |
| W2 | `booking.opera.hotel-inventory` | drop `hotels.size == 1` gate clause; per-hotel mappings | 7 |
| W3 | `booking.opera.rate-info` | per-hotel × per-rate mappings; gate widened; verify `/distr` sends `summaryInfo=true` (Known Matcher Gaps) | 7 |
| W4 | `booking.opera.item-inventory` | drop `hotels.size == 1`; per-hotel mappings for hotels with `itemInventory` | 7 |
| W5 | `booking.opera.rate-plans` | per-hotel builder + `displaySet` in response when `Rate.displaySet` (M5) present | 7 |
| V1 | `booking.opera.credit-card-info` | verify all six section-4 callers send `cardIdContext=OPERA` + `cardIdType=CreditCard`; widen matcher only if a real caller differs (same stub ID) | 14 |
| V2 | `booking.opera.reservation-amounts` | verify updateBusinessItems Prepaid rate-info query carries `idContext`/`id`/`summaryInfo=true`/`type`; widen only if genuinely omitted | 14 |
| V3 | `booking.opera.packages-list` | verify confirmAmendSingleCall RTP query vs pinned `adults=1`/dates/fetchInstructions regex; widen or add caller-shape mappings under same ID if not | 14 |

Confirmed reuse, NO change needed after request-shape overlap checks: `booking.opera.put-reservation`
(serves 1.2 add-profile PUT, 1.4 alert PUT, 1.5 alert cleanup, 1.7 Business Notes, section-4
PUTs — urlPath-only permissive matcher), `booking.opera.get-reservation` matcher (query-permissive,
all `fetchInstructions` variants), `booking.opera.create-profile`, `booking.opera.company-profile`,
`booking.opera.room-types`, `booking.opera.deposit-folios` (exists — report 14 corrects the trace
claim), `booking.opera.delete-cancellation-policy`/`create-cancellation-policy` +
get-reservation for 1.8 (shared `updateCancellationPolicy` path).

### Reserved phase-two custom stub IDs (normalized to `custom.opera.*`)

`custom.opera.delete-reservation-error`, `custom.opera.create-profile-bad-request`,
`custom.opera.pre-check-in-no-links`, `custom.opera.pre-check-in-error`,
`custom.opera.put-reservation-check-in-alert-error`, `custom.opera.add-file-attachment-failure`,
`custom.opera.delete-reservation-attachment` (permissive absence-proof install),
`custom.opera.delete-reservation-attachment-failure`, `custom.opera.put-reservation-alert-failure`,
`custom.opera.get-reservation-routing-error`, `custom.opera.delete-routing-instruction-error`,
`custom.opera.get-reservation-cancel-policies-error`, `custom.opera.multi-hotel-availability-error`,
`custom.opera.multi-hotel-negotiated-availability-error`, `custom.opera.company-profile-no-profile-id`,
`custom.opera.minimum-rate-availability-error`, `custom.opera.multi-room-rate-availability-error`,
`custom.opera.hotel-inventory-error`, `custom.opera.vacant-rooms-error`,
`custom.opera.room-assignment-error`, `custom.opera.check-in-rejected`,
`custom.opera.housekeeping-overview-failure`, `custom.opera.deposit-folios-error`,
`custom.opera.put-reservation-error`.

---

## Consolidated typed-client changes (`clients/ohip/OhipApi.kt`, via `ServiceApiClient`, all with `testId`; `featureFlagOverrides` where flags exist) — 22 methods

Batch 1: `deleteReservation(hotelId, reservationId, testId)`;
`createProfile(hotelId, reservationId, CreateProfileRequest, testId)`;
`preCheckInReservation(PreCheckInRequest, testId, flags)`;
`preRegisterReservation(PreCheckInRequest, testId, flags)`;
`addAttachmentToReservation(ReservationFileAttachmentRequest, testId, flags)`;
`deleteRegCardAttachment(hotelId, reservationId, testId, flags)`;
`deleteRoutingInstructions(hotelId, reservationIds, testId)`.
(1.8 `updateCancellationPolicies` already exists.)

Batch 2: `getMultiHotelAvailability(MultiHotelAvailabilityRequest, testId, flags)`;
`getHotelAvailabilityByIds(AvailabilityByIdsRequest, testId, flags)`;
`getMultiHotelAvailabilityV2(request, testId, flags)` (POST /ohip/v2/hotels/availabilities);
`getHotelAvailabilitiesByIdsV2(...)`; `getHotelAvailabilitiesByIdsV3(...)`.

Batch 3: `getVacantRooms(hotelId, roomType, testId)` (POST, query params, no body — verify
bodyless-POST ergonomics); `allocateRooms(RoomAllocationRequest, testId)`;
`kioskCheckIn(KioskCheckInRequest, testId)`;
`fetchHouseKeepingStatus(hotelId, roomId, testId, flags)`.

Batch 4: `saveDepositFolios`; `updateBusinessItems`; `movePaymentDetails(hotelId,
reservationIds, testId, flags)`; `updateReservation`; `confirmAmend` (reuses
`ReservationByBasketRefResponse` DTO); `confirmAmendSingleCall(..., flags)`.

Plus minimal request/response models under `clients/ohip/model/` per group reports
(`PreCheckInRequest/Response` shared by pre-checkin, pre-register, and the attachment-add
response; kiosk/vacant/housekeeping/allocation minimal DTOs; section-4 slim `tempReservations`
DTOs built by spec-local fixtures).

---

## Batch 1 — Reservation lifecycle (plan §§1.1-1.8)

Model: M1-M4. New stubs: S1-S5. Widening: W1 (land once here). Clients: 7 methods.

### Happy-path scenarios (14; 13 new + 1 existing)

| ID | Endpoint | Scenario | Merged facts/stubs used |
| --- | --- | --- | --- |
| DR-H1 | DELETE /ohip/v1/reservations | delete returns 204; Opera DELETE pass-through, `callCount(OPERA)==1` | reservation room; S1 |
| CPK-HP-1 | POST /ohip/v1/profile/createProfile | one staying guest: GET(GuestLastStay) → CRM create → PUT, HTTP 200 empty | reservation room + `guestProfile`; existing get/create-profile/put stubs |
| CPK-HP-2 | same | empty `guestDetails`: no CRM POST (count delta), PUT still runs | same |
| PC-HP1 | POST /ohip/v1/reservations/pre-checkin | preCheckIn POST + alert PUT → 200 `status=Success`; flag `mobile_preRegistered_repurpose=false` | M1; S2 + existing put-reservation |
| PC-HP2 | same | flag ON skips Opera preCheckIn POST (absence via `callCount(OPERA)==1`), alert still added | M1; S2 installed, unhit |
| PR-HP1 | POST /ohip/v1/reservations/pre-register | same Opera preCheckIn op, no alert PUT (`callCount(OPERA)==1`) → 200 Success | M1; S2 (shared stub after overlap check) |
| ADD-H1 | POST /ohip/v1/reservations/attachments | valid PDF reg card uploaded → 200 `success=true`; checked-in base64 PDF fixture | reservation room; S3 |
| DEL-H1 | DELETE /ohip/v1/reservations/attachments | pre-registered with attachment + alert → 204; GET/DELETE/PUT counts 1/1/1 | M2 (full); S4 + W1(a) + existing get/put |
| DEL-H2 | same | not pre-registered → silent no-op 204; DELETE 0 / PUT 0 (absence via direct-installed permissive `custom.opera.delete-reservation-attachment`) | no M2; W1 baseline GET |
| DEL-H3 | same | pre-registered, no reg card, alert only → 204; DELETE 0 / PUT 1 | M2 (`preCheckInAlertId` only); S4 gate-off |
| RI-HP1 | DELETE /ohip/v1/reservations/routingInstructions | 1 folio + 1 daily instruction + Business Notes → 204; GET/DELETE/PUT counts | M3+M4; S5 + W1(b)(c) + existing get/put |
| RI-HP2 | same | batch of 2 reservations, mixed shapes (multi-instruction w/ optional params; notes-only sibling) → 204, exact `callCount(OPERA)` proves fan-out + skips | M3+M4; S5 |
| CP-1 | PUT /ohip/v1/reservations/cancel-policies | **exists** (`UpdateCancellationPoliciesSpec`) — sync 200 + zero Opera calls at response time | existing stubs only |
| CP-2 | same | deferred rewrite reaches Opera: `eventually` until `callCount(OPERA) >= 3×N` — **conditional on blocker B1** (baggage propagation across the background `CompletableFuture`); verify first, else record skipped | existing cancellation-policy stubs |

### Phase-two negatives (~20 designed)

DR-N1 (938 mapping); CPK-NEG-1/2/3 (GET fail → `OHIP_GET_PROFILEID_EXCEPTION`; CRM fail →
`OHIP_CREATE_PROFILE_EXCEPTION`; PUT fail → retry-timing risk); PC-N1/N2/N3 + PR-N1/N2
(link-less 2xx → `status=Error`; Opera error mappings; alert-PUT failure — retry risk);
ADD-N1 (958), ADD-N2 (timeout — **recommend skip**); DEL-N1 (reuse `getReservationEmpty`),
DEL-N2 (938), DEL-N3 (952 — retry risk); RI-N1..N4 (mapped exceptions + batch abort) +
RI-N5 (empty-reservation-list 500 — service-bug candidate,
`bugs/delete-routing-instructions-empty-reservation-500.md`); CP-N1 (conditional on B1,
else record as service-test concern). All via `excluded` + `custom.opera.*` IDs.

### Batch-1 risks

- W1 touches every journey installing `booking.opera.get-reservation` → full-suite rerun.
- S1 Content-Length matcher and S3 gate widening: validate on first green run.
- DELETE-path retry policy unknown (DR-N1); duplicate profile id in CPK PUT body;
  createProfile returns runtime 200 not documented 204 (assert 200).
- B1: cancel-policies background baggage propagation (blocks CP-2/CP-N1).
- `mobile_preRegistered_repurpose` request-scope reachability (blocks PC-HP2 if env-scoped).

---

## Batch 2 — Availability (plan §§2.1-2.3)

Model: M5 (+ second multi-hotel preset fixture). New stubs: S6-S9. Widenings: W2-W5.
Clients: 5 methods. Collection rule enforced: every rate declares `promotionCode` or
`ratePlanSet`.

### Happy-path scenarios (19; 2 conditional)

| ID | Endpoint | Scenario | Merged facts/stubs |
| --- | --- | --- | --- |
| A1 | GET /ohip/hotels/availabilities | two hotels each return a PBF rate; PBN empty-fallback leg mandatory | S6 (ratePlanSet family) |
| A2 | same | mixed: rate-less hotel → `available:false` | S6 |
| A3 | same | PBN + PBF rates merged; availability callCount proves doubled fan-out | S6 |
| B1 | GET /ohip/hotels/availabilities/distr | public search by `ratePlanCodes`, two hotels, per-night breakdown | S6 (ratePlanCode family) + W2/W3 + room-types |
| B2 | same | negotiated by `globalCompanyId` | S7 + existing company-profile |
| B3 | same | `negotiatedRateDisplaySets` filter | M5 + W5 + S7 |
| B4 | same | `pmsRoomTypes` bypasses rules-agent | S6 |
| B5 | same | cot stock → `cotAvailable:true` | W4 |
| B6 | same | non-DISTR channel, `release_availability_from_different_room_classes=true`: cross-room-class rooms kept | S6, flag ON |
| B7 | same | same, flag OFF: filtered out | S6, flag OFF |
| MRV2-HP1 | POST /ohip/v2/hotels/availabilities | multi-hotel minimum-rate summary, 1 Opera call | S8 |
| MRV2-HP2 | same | sold-out hotel → `available=false` with minimumRate | S8 (numberOfRooms=0 fact) |
| MRV2-HP3 | same | roomTypes via rules-agent — **conditional on real rules-agent data** | S8 tolerant matcher |
| MRV2-HP4 | same | >90-day stay → 2 interval calls, summed | S8 per-interval mappings |
| V2-1 | POST /ohip/v2/hotels/availabilities/distr | DISTR 2-room fan-out + house inventory | S9 + existing hotel-inventory |
| V2-2 | same | non-DISTR groups rooms, skips inventory (`callCount==0` absence) | S9 |
| V2-3 | same | corporate object stamps `globalCompanyId` (request data, not a fact) | S9 |
| V2-4 | same | rules-agent substitution + specialRequests — **conditional on seed data** | S9 |
| V3-1 | POST /ohip/v3/hotels/availabilities/distr | multi-corporate fan-out ×2, merged stamped rates | S9 |

### Phase-two negatives (~13 designed)

N-A1 (split failure → `OHIP_MULTIHOTELS_AVAILABILITY_EXCEPTION`), N-A2 (companyId dropped —
absence proof / possible bug), N-B1 (no Profile-type id → `DIGITAL_NO_PROFILE`), N-B2
(negotiated 500), N-B3 (no availability → rateInfo/itemInventory absence); MR-NEG-1
(`OHIP_MULTIHOTEL_AVAILABILITY_EXCEPTION`), MR-NEG-2 (interval intersection drop), MR-NEG-3
(missing minimumRate NPE — service-bug candidate); V2-N1..N4 + N5 (stretch, conflict-retry).

### Batch-2 risks

Repeated-query-param matching; `absent`-matcher availability in the stub model (raise, don't
patch framework, if missing); DISTR/BB rules-agent seed data; tag-echo convention limits;
interval-split mirroring; gate widenings W2-W5 change other Bookings' install sets →
full-suite rerun; x-hotelid pinned to first hotel (send hotelIds in booking order).

---

## Batch 3 — Front office / kiosk (plan §§3.1-3.4)

Model: M6, M7. New stubs: S10-S13. No widenings. Clients: 4 methods.

### Happy-path scenarios (6)

| ID | Endpoint | Scenario | Merged facts/stubs |
| --- | --- | --- | --- |
| VAC-H1 | POST /ohip/v1/rooms/getVacant | clean vacant rooms of requested type returned (101/102, not 201) | M6; S10 |
| VAC-H2 | same | roomType with no clean-vacant rooms → 200 empty (Dirty room in M6 data) | M6; S10 (data-driven) |
| AR-HP1 | POST /ohip/v1/rooms/allocate | allocate room to reservation → 200 `links` | reservation room; S11 |
| KCI-1 | POST /ohip/v1/kiosk/checkIn | kiosk check-in → 200 InHouse snapshot, `currentRoomInfo.roomId == assignedRoomId` | M7; S12 |
| HSK-1 | GET /ohip/v1/rooms/fetchHouseKeepingStatus | known room "101" → 200 status "Clean" (from `HotelPhysicalRoom.housekeepingStatus`) | M6; S13 per-room mapping |
| HSK-2 | same | unknown room "999" → 200 "RoomId Not Available" via catch-all empty overview | M6; S13 catch-all |

No flags anywhere in batch 3 (token flags env-OFF; spec comments only).

### Phase-two negatives (4 designed)

VAC-N1 (930 `OHIP_GET_VACANT_ROOMS_EXCEPTION`), AR-N1 (931 `OHIP_ALLOCATE_ROOMS_EXCEPTION` —
verify retry policy first), KCI-N1 (918, `callCount==1` no-retry proof), HSK-N1 (932). All
`custom.opera.*` overrides.

### Batch-3 risks

Bodyless-POST ergonomics for getVacant; unmatched-roomType near-miss 404 masking (mutation
validation must break responses, not just matchers); catch-all vs per-room mapping order
(catch-all first; cover in unit test); housekeeping mapper latent NPE (stub keeps wrappers);
`callCount` upstream key derivation for new stubs; HSK status values come from M6's
`housekeepingStatus`, statuses beyond Clean/Dirty allowed by the free-text field.

---

## Batch 4 — Credit-card-info caller variants (plan §4)

Model: none. New stubs: none. Verify-first widenings: V1-V3. Clients: 6 methods.
Corrections: `booking.opera.deposit-folios` already exists (reuse); mapping row 188's
credit-card-info attribution is wrong for PUT /ohip/v1/reservations (fix in the mapping doc).

### Happy-path scenarios (14 core + 1 optional)

| ID | Endpoint | Scenario | Flags |
| --- | --- | --- | --- |
| DF-1 | POST deposit-folios | carded reservation → 201, counts GET+ccInfo+cshPOST (card mandatory — cardless NPE is a phase-two bug candidate) | none |
| BI-1 | PUT business | businessItems branch (rules-agent allowances — **conditional R1**) | none |
| BI-2 | PUT business | Distribution channel company + folio window | none |
| BI-3 | PUT business | Prepaid → folio window 3 (**pending V2**) | none |
| MP-1 | PUT movePaymentDetails | move card to window 2; `callCount==3` proves no alert PUT | `release_set_cnp_booking_alerts=false` |
| MP-2 | same | CNP alert added; `callCount==4` | flag `=true` |
| UR-1 | PUT /ohip/v1/reservations | amend-stay from caller temp state; `callCount==1` (no GET/ccInfo — trace surprise) | none |
| UR-2 | same | edit-room variant — optional, fold to service tests because it does not add a materially different cross-boundary path unless requested | none |
| CA-1 | PUT confirmAmend | full amend orchestration, ~9 Opera calls, refreshed basket | none |
| CA-2 | same | `markAsPayOnArrival=true` adds GET+PUT round | none |
| CS-1 | PUT confirmAmendSingleCall | stay-date change, no booking-fee round | `release_distr_booking_fee=false` |
| CS-2 | same | package removal + RTP pricing (**pending V3**) | flag OFF |
| CS-3 | same | booker details, CRM PUT ×2 (deployed behavior, comment not bug) | flag OFF |
| CS-4 | same | booking-fee round (**pending V3/R4** — omit the unreachable flag state if the package is unavailable) | flag ON |

### Phase-two negatives (12 designed)

Per report 14's table: deposit-folios empty-GET / csh-500 / cardless-NPE bug file; business
missing-reservation / unrecognized-channel absence proof; movePaymentDetails missing-reservation /
cardless-skip absence; updateReservation PUT failure (retry caveat); confirmAmend
temp-not-found / unlinked-original absence; confirmAmendSingleCall merged-PUT failure.

### Batch-4 risks

R1 rules-agent allowances seed data (BI-1/BI-3); V1-V3 matcher verifications; R4 booking-fee
package validity; R6 exact callCounts pinned at implementation + mutation-validated; R7
double CRM PUT tolerated with comment; mapping-doc corrections (rows 185-190, 188, 44).

---

## Cross-batch sequencing notes

1. Land W1 (get-reservation enrichment) once, early in batch 1 — both 1.5 and 1.7 depend
   on it; single characterization update + suite rerun.
2. M6/S10/S13 land together in batch 3 (one fact, one gate source, two stubs, one suite
   rerun).
3. Resolve the OAuth-token-in-`callCount` question empirically during the first batch-1
   implementation; every later exact-count assertion reuses the answer.
4. Full journey-suite rerun after each batch, tolerating only the two known
   baseline failures.
