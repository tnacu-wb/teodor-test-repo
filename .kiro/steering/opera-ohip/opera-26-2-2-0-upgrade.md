---
inclusion: manual
---

# Opera Cloud 26.2.2.0 Upgrade — Impact Assessment & Code Analysis

## Overview

This steering file consolidates the full impact assessment for the Oracle Hospitality OPERA Cloud 26.2.2.0 upgrade affecting Whitbread's digital platform. It combines the official Oracle patch notes, the Confluence impact assessment, and the local code analysis performed against the `digital-monorepo`.

**Next available minor release after 26.2.2.0:** 26.3.0.0

**Overall Assessment:** Low to Medium impact — no confirmed breaking changes to Whitbread's documented OHIP booking journey APIs.

---

## Breaking API Changes in 26.2.2.0

### 1. Guest Message Timezone Change

- **What changed:** `postGuestMessages`, `putGuestMessages`, and `putResvGuestMessages` will now interpret `messageDate`/`deliveryDate` in the hotel's timezone rather than the database timezone.
- **Affected module:** `rsv`
- **Code impact:** NONE — No Java code in the repo calls these endpoints. The OpenAPI spec (`operareservationsv0api.yaml`) defines them, but they are not consumed.
- **Action:** No code changes required. Confirm no downstream service outside this repo uses these APIs.

### 2. Deprecated GET CC Authorization Instruction Endpoints

- **What changed:** `getCCAuthorizationInstructions` and `getCCAuthorizationInstructionsByProfile` GET endpoints retired and replaced by POST equivalents.
- **Code impact:** NONE — Zero matches for these endpoints in any Java code.
- **Action:** No code changes required.

### 3. getFolioHistory Pagination (50 Row Limit)

- **What changed:** `GET /hotels/{hotelId}/folioHistory` now returns max 50 rows per page; clients must use `limit` and `offset` parameters.
- **Code impact:** NONE — The `OhipReservationClient.getFoliosAciAmount()` explicitly passes `includeFolioHistory=false`. No code calls the dedicated `getFolioHistory` endpoint.
- **Action:** No code changes required. Monitor future usage if folio history is ever needed.

### 4. RTP packageTransactionCode Validation

- **What changed:** `postRatePlan`, `postRatePlanPackages`, `postRatePlanSchedules`, `putRatePlanSchedules`, and `putRatePlan` will return validation errors if `packageTransactionCode` is missing when packages are attached.
- **Code impact:** NONE — Rate plan integration is read-only. The code only calls `GET /rtp/v1/ratePlans` to retrieve rate plan data. No write operations to rate plan APIs exist.
- **Action:** No code changes required.

---

## Bug Fixes with Potential Behavioural Impact

### Business Events (HOPCS-93086)

- **Fix:** An UPDATE RESERVATION business event now generates whenever a reservation is updated, including updates containing reservation notes without a note ID.
- **Impact on code:** The `availability-business-events-service-opera` subscribes to Opera business events via WebSocket/GraphQL. More events may be triggered post-upgrade.
- **Action:** Monitor for increased event volume and ensure no processing bottlenecks.

### Deposit Folio Calculation (HOPCS-91867)

- **Fix:** The internal `postRateDeposit` API now calculates required prepayment by stay date using the daily rate amount from Rate Info.
- **Impact on code:** Both `ohip-adapter-service` and `hotel-reservation-entity-service` heavily use deposit folio APIs (`sendDepositFoliosRequest`, `getGeneratedDepositFolios`, `createDepositFolios`).
- **Action:** Regression test deposit folio creation flows to ensure amounts are correct post-upgrade, especially for multi-night reservations with varying rates.

### Credit Card Retention on OWS Updates (HOPCS-93057)

- **Fix:** Credit card details now remain on the reservation when OWS reservation updates are received for guest requests, special requests, or comment updates.
- **Impact on code:** The system manages reservation payment methods.
- **Action:** Positive fix — test reservation retrieval flows with payment methods to confirm improvement.

### putBlock Delta Update (HOPCS-93427)

- **Fix:** When an external system updates blockDetails through `putBlock`, the tax type will not be changed unless included in the REST API call.
- **Action:** Validate block-related flows if applicable.

### ratePlanSet Now Mandatory in CRO Response (HOPCS-93694)

- **Fix:** `ratePlanSet` is now mandatory in `getHotelAvailabilityCRO` response for LTB screen to display rate/room combination. Correct availability is displayed when availability search is done without display set.
- **Impact on code:** The availability services retrieve hotel availability; ensure response parsing handles `ratePlanSet` presence.
- **Action:** Validate availability search flows.

### Profile Merge Business Events (HOPCS-93300)

- **Fix:** When a profile is merged, the source profile ID is now retrieved in the `getBusinessEvents` API operation.
- **Action:** Update event consumers if they need the source profile ID for reconciliation.

### Transaction Status in Business Events (HOPCS-93260)

- **Fix:** When charges are routed after posting, the UPDATE POSTING Business Event now reflects the appropriate transaction status (WINDOW TRANSFER vs TRANSFER with ROUTEDYN=Y).
- **Action:** Verify event-driven logic doesn't assume a single status value.

---

## OHIP API Impact Summary Table

| Category | API / Area | Change Type | Summary | Impact | Action |
|----------|-----------|-------------|---------|--------|--------|
| Rate & Inventory | `getHotelAvailabilityCRO` | Contract Change | `ratePlanSet` now mandatory in response | High | Update response handling; validate availability flows |
| Rate & Inventory | `setRatePlanSchedules` | Bug Fix | Supports overlapping date ranges without errors | High | Re-test pricing updates with overlapping schedules |
| Block Management | `putBlock` | Bug Fix | TAXTYPE/SERVICE CHARGE preserved unless explicitly changed | Medium | Validate delta update payload handling |
| Financial / Deposits | `postRateDeposit` | Calculation Fix | Accurate deposit calculation using daily rates | High | Re-test deposits for multi-night bookings |
| Financial / Payments | Credit Card Validation | Bug Fix | Works correctly with Internal Token Service enabled | High | Validate payment flows for external reservations |
| Business Events | Reservation Events | Bug Fix | Update events triggered for note updates without note ID | High | Validate event streaming and downstream consumption |
| Business Events | Profile Merge Events | Enhancement | Source profile ID now included in events | High | Update consumers for reconciliation |
| OHIP Future Changes | `getFolioHistory` | Enhancement | Pagination introduced (max 50 rows) | Strategic | Implement pagination handling if needed |
| OHIP Future Changes | Multiple APIs | Architectural Change | Migration from GET → POST (search endpoints) | Strategic | Plan refactor for endpoint consumption |
| OHIP Future Changes | Guest Messaging APIs | Behavioral Change | Time zone aligned to hotel instead of DB | Strategic | Update time handling logic if used |

---

## Affected Services in digital-monorepo

### ohip-adapter-service (discover-search)

- **Role:** Central OHIP proxy — makes all Opera REST API calls.
- **Key files:**
  - `OhipReservationClient.java` — reservation, folio, deposit calls
  - `OhipConstants.java` — `INCLUDE_FOLIO_HISTORY` constant
  - `HotelReservationOutPortImpl.java` — deposit folio orchestration, cancellation policies, timezone handling
  - `HotelInfoMapper.java` — maps `hotelTimeZone` from `propertyControls.dateTimeFormatting.timeZoneRegion`
- **Upgrade concerns:** Deposit folio calculation changes, business event volume, timezone handling for cancellation deadlines.

### availability-business-events-service-opera (discover-search)

- **Role:** Subscribes to Opera business events via WebSocket/GraphQL for rate restriction updates.
- **Key files:**
  - `EventSubscriptionWebSocketClient.java` — WebSocket subscription
  - `RateRestrictionsMapper.java` — maps rate plan events to DB entities
  - `SubscribeBusinessEventsColdStartSvc.java` — cold-start subscription
- **Upgrade concerns:** Increased event volume from HOPCS-93086 fix; new profile merge event data (HOPCS-93300).

### hotel-reservation-entity-service (manage-modify)

- **Role:** Reservation management, amendments, cancellations, deposit folios.
- **Key files:**
  - `OhipAdapterClient.java` — calls ohip-adapter-service for rate plans, deposit folios
  - `HotelReservationOhipOutPortImpl.java` — orchestrates availability, rate plans, deposit folios
  - `OhipAdapterProperties.java` — endpoint configuration including `ratePlansEndpoint`, `generatedDepositFoliosEndpoint`
- **Upgrade concerns:** Deposit folio amounts may change; validate confirm/amend flows.

### availability-cache-service-opera (discover-search)

- **Role:** Caches hotel availability data from Opera.
- **Upgrade concerns:** Ensure `ratePlanSet` mandatory field doesn't break availability response parsing.

### content-entity-service (discover-search)

- **Role:** Hotel content and configuration.
- **Key file:** `HotelInformationExtendedMapper.java` — maps `hotelTimeZone`
- **Upgrade concerns:** Minimal — timezone mapping already correct.

---

## Full List of Bug Fixes in 26.2.2.0 Patch

| Bug ID | Reference | Summary |
|--------|-----------|---------|
| 39574287 | HOPCS-94079 | Channel Rate Code field allows >8 characters |
| 39574267 | HOPCS-94078 | Channel Rate Code supports hyphens |
| 39568180 | HOPCS-94019 | Rate Info no longer shows negative rates for posting rhythm |
| 39567261 | HOPCS-93984 | Membership details added from Rooming List |
| 39566808 | HOPCS-93976 | Hub Control deactivation reflects at property |
| 39560775 | HOPCS-93914 | Comp Posting Journal shows routed charges |
| 39560543 | HOPCS-93911 | ADS supports text/xml media type |
| 39553227 | HOPCS-93831 | Rooms Availability Summary tile fixed |
| 39552453 | HOPCS-93815 | Transfer posting only moves selected tax postings |
| 39547802 | HOPCS-93777 | Rooms Sold Persons count corrected |
| 39547764 | HOPCS-93775 | Commissions report displays correct values |
| 39539862 | HOPCS-93696 | Max sharing guests configurable up to 50 |
| 39539849 | HOPCS-93694 | ratePlanSet mandatory in CRO response |
| 39523624 | HOPCS-93531 | Commission transmittal letter fixed |
| 39511131 | HOPCS-93455 | Credit Bill fiscal communication fixed |
| 39507513 | HOPCS-93427 | putBlock preserves TAXTYPE/SERVICE CHARGE |
| 39488942 | HOPCS-93324 | OWS fetch booking fixed for cash payment |
| 39486557 | HOPCS-93300 | Source profile ID in merge business events |
| 39478109 | HOPCS-93260 | Transaction status reflects routing type correctly |

---

## QA Testing Timeline (Sprint 4)

| Task | Duration | Start | Finish | Resource |
|------|----------|-------|--------|----------|
| Digital QA: Sanity Testing - PIBA & CCUI | 2 days | Thu 16-07-26 | Fri 17-07-26 | Sujith Mohan |
| Digital QA: Regression Testing - PIBA & CCUI | 8 days | Mon 20-07-26 | Wed 29-07-26 | Sujith Mohan |
| Digital QA: Sanity Testing - PI & Restaurants | 2 days | Thu 16-07-26 | Fri 17-07-26 | Ankit Sharma |
| Digital QA: Regression Testing - PI & Restaurants | 8 days | Mon 20-07-26 | Wed 29-07-26 | Ankit Sharma |
| Digital QA: Sanity Testing - Mobile Apps | 2 days | Thu 16-07-26 | Fri 17-07-26 | Priyadarisini Ravichandran |
| Digital QA: Regression Testing - Mobile Apps | 8 days | Mon 20-07-26 | Wed 29-07-26 | Priyadarisini Ravichandran |
| Digital QA: Oracle Distribution - Booking.com | 3 days | Thu 16-07-26 | Mon 20-07-26 | Abdi Mahamud |
| Digital QA: Sign-off | 1 day | Thu 30-07-26 | Thu 30-07-26 | Preeti Bhutani |

---

## Recommended Validation Checklist

- [ ] Run end-to-end regression for full booking journey: availability, on-hold creation, ancillaries, guest details, payment, confirmation, abandonment
- [ ] Run servicing regression for cancel and amend flows
- [ ] Validate CRM profile create and retrieve integrations for tolerance to additive fields
- [ ] Review timezone handling in cancellation deadline logic
- [ ] Confirm deposit folio creation amounts are accurate for multi-night varied-rate reservations
- [ ] Monitor business events subscription service for increased event volume
- [ ] Validate package, package group, and hotel configuration retrieval
- [ ] Confirm `ratePlanSet` in availability responses doesn't break parsing
- [ ] Update support/operations documentation where Opera UI workflows changed

---

## References

- [Digital Opera Upgrade 26.2.2.0 Impact Assessment (Confluence)](https://whitbreadis.atlassian.net/wiki/x/kICUSQE)
- [Digital Impact Assessment: Opera Upgrade to 26.2.2.0 (Confluence)](https://whitbreadis.atlassian.net/wiki/x/CYCzSAE)
- [Opera OHIP API Calls](https://whitbreadis.atlassian.net/wiki/x/bIDH9)
- [Oracle OPERA Cloud 26.2 Release Hub](https://docs.oracle.com/en/industries/hospitality/opera-cloud/26.2/)
- [Oracle Hospitality API Docs - property_26.2.0.0](https://github.com/oracle/hospitality-api-docs/tree/property_26.2.0.0/rest-api-specs)


---

## Whitbread Opera OHIP API Call Inventory

Source: [Opera OHIP API Calls (Confluence)](https://whitbreadis.atlassian.net/wiki/x/bIDH9)

This section documents all Opera API calls made across the Whitbread booking, cancel, and amend journeys.

### Booking Journey

| Step | Journey Stage | API Call | Endpoint |
|------|--------------|----------|----------|
| 1.1 | HDP - Availability Check | Retrieve Room Types | GET /rm/config/v1/hotels/{hotelId}/roomTypes |
| 1.1 | HDP - Availability Check | Retrieve Hotel Inventory | GET /inv/v1/hotels/{hotelId}/hotelInventory |
| 1.1 | HDP - Availability Check | Retrieve Hotel Availability | GET /par/v1/hotels/{hotelId}/availability |
| 1.1 | HDP - Availability Check | Retrieve Rate Information | GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo |
| 1.1 | HDP - Availability Check | Retrieve Item Inventory | GET /inv/v0/hotels/{hotelId}/itemInventory |
| 1.2 | HDP - Create On-Hold Booking | Retrieve Item Inventory | GET /inv/v0/hotels/{hotelId}/itemInventory |
| 1.2 | HDP - Create On-Hold Booking | Create ON HOLD Reservation | POST /rsv/v1/hotels/{hotelId}/reservations |
| 1.2 | HDP - Create On-Hold Booking | Create Item Inventory Hold (cots) | POST /inv/v0/hotels/{hotelId}/itemInventoryHold |
| 1.2 | HDP - Create On-Hold Booking | Retrieve Reservation (optional) | GET /rsv/v1/hotels/{hotelId}/reservations |
| 2.1.1 | Ancillaries - Booking Summary | Retrieve ON HOLD Reservation | GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 2.1.1 | Ancillaries - Booking Summary | Retrieve Reservation Costs | GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo |
| 2.1.1 | Ancillaries - Booking Summary | Retrieve Hotel Config | GET /ent/config/v1/hotels/{hotelId} |
| 2.1.1 | Ancillaries - Booking Summary | Retrieve Rate Pricing for Flex (optional) | GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo |
| 2.1.2 | Ancillaries - Populate Page | Retrieve ON HOLD Reservation | GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 2.1.2 | Ancillaries - Populate Page | Retrieve Hotel Restaurants | GET /ent/config/v1/hotels/{hotelId} |
| 2.1.2 | Ancillaries - Populate Page | Retrieve Packages | GET /rtp/v1/packages |
| 2.2 | Ancillaries - Save | Retrieve Packages | GET /rtp/v1/packages |
| 2.2 | Ancillaries - Save | Retrieve Package Groups (remove) | GET /rtp/v0/hotels/{hotelId}/packageGroups |
| 2.2 | Ancillaries - Save | Update ON HOLD Reservation (remove packages) | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 2.2 | Ancillaries - Save | Retrieve Package Groups (add) | GET /rtp/v0/hotels/{hotelId}/packageGroups |
| 2.2 | Ancillaries - Save | Update ON HOLD Reservation (add packages) | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 2.3 | Ancillaries - Upgrade to Flex | Update Rate Plan Code | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 3.1 | Guest Details - Booking Summary | Retrieve ON HOLD Reservation | GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 3.1 | Guest Details - Booking Summary | Retrieve Reservation Costs | GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo |
| 3.1 | Guest Details - Booking Summary | Retrieve Hotel Config | GET /ent/config/v1/hotels/{hotelId} |
| 3.2 | Guest Details - Update | Create Guest Profile | POST /crm/v1/profiles |
| 3.2 | Guest Details - Update | Create Booker Profile | POST /crm/v1/profiles |
| 3.2 | Guest Details - Update | Update ON HOLD Reservation (profiles) | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 3.2 | Guest Details - Update | Set Business or Leisure | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 4.1.1 | Payment - Booking Summary | Retrieve ON HOLD Reservation | GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 4.1.1 | Payment - Booking Summary | Retrieve Reservation Costs | GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo |
| 4.1.1 | Payment - Booking Summary | Retrieve Hotel Config | GET /ent/config/v1/hotels/{hotelId} |
| 4.1.2 | Payment - Donations | Retrieve Donations Packages | GET /rtp/v1/packages |
| 4.2 | Payment - Take Payment | Retrieve ON HOLD Reservation | GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 4.2 | Payment - Take Payment | Retrieve Reservation Costs | GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo |
| 4.2 | Payment - Take Payment | Retrieve Profiles | GET /crm/v1/profiles |
| 4.2 | Payment - Take Payment | Retrieve Hotel Config | GET /ent/config/v1/hotels/{hotelId} |
| 4.2 | Payment - Take Payment | Retrieve Packages (donations) | GET /rtp/v1/packages |
| 4.2 | Payment - Take Payment | Update Reservation (donations) | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 5.1 | Confirm Booking (PAY_NOW) | Retrieve ON HOLD Reservation | GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 5.1 | Confirm Booking (PAY_NOW) | Retrieve Reservation Cost | GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo |
| 5.1 | Confirm Booking (PAY_NOW) | Create Deposit Folios | POST /csh/v1/hotels/{hotelId}/reservations/{reservationId}/depositFolios |
| 5.1 | Confirm Booking (PAY_NOW) | Retrieve Confirmed Reservation | GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 5.1 | Confirm Booking (PAY_ON_ARRIVAL) | Confirm Reservation | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 5.2 | Confirmation - Populate Page | Retrieve Reservation | GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 5.2 | Confirmation - Populate Page | Retrieve Reservation Costs | GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo |
| 5.2 | Confirmation - Populate Page | Retrieve Profiles | GET /crm/v1/profiles |
| 5.2 | Confirmation - Populate Page | Retrieve Hotel Config | GET /ent/config/v1/hotels/{hotelId} |
| 6 | Abandon Booking | Delete ON HOLD Reservation | DELETE /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |

### Cancel Journey

| Step | Journey Stage | API Call | Endpoint |
|------|--------------|----------|----------|
| 1 | Cancel Booking | Retrieve Reservation Details | GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 1 | Cancel Booking | Retrieve Reservation Costs | GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo |
| 1 | Cancel Booking | Retrieve Hotel Config | GET /ent/config/v1/hotels/{hotelId} |
| 1 | Cancel Booking | Post Refunds | POST /csh/v1/hotels/{hotelId}/reservations/{reservationId}/depositFolios |
| 1 | Cancel Booking | Cancel Reservation | POST /rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellations |

### Amend Journey

| Step | Journey Stage | API Call | Endpoint |
|------|--------------|----------|----------|
| 1.1 | Copy Booking | Retrieve Original Reservation | GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 1.1 | Copy Booking | Create Copy Reservation | POST /rsv/v1/hotels/{hotelId}/reservations ON HOLD |
| 1.1 | Copy Booking | Retrieve Temporary Reservation | GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 1.1 | Copy Booking | Retrieve Original Reservation | GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 1.1 | Copy Booking | Retrieve Reservation Costs | GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo |
| 1.1 | Copy Booking | Retrieve Hotel Config | GET /ent/config/v1/hotels/{hotelId} |
| 2.1 | Amend Stay Dates | Retrieve Temp Reservation | GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 2.1 | Amend Stay Dates | Retrieve Rate Plans (amendable info) | GET /rtp/v0/ratePlans |
| 2.1 | Amend Stay Dates | Create ON HOLD for Extra Period | POST /rsv/v1/hotels/{hotelId}/reservations ON HOLD |
| 2.1 | Amend Stay Dates | Update Reservation (new dates) | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 2.1 | Amend Stay Dates | Clean Up On-Hold | DELETE /rsv/v0/reservations/{reservationId} |
| 2.1 | Amend Stay Dates | Remove Routing Instructions | DELETE /csh/v1/hotels/{HotelId}/reservations/{ReservationId}/routingInstructions/folio |
| 2.1 | Amend Stay Dates | Update Reservation (routing) | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 3.1 | Add Room | Retrieve Item Inventory | GET /inv/v0/hotels/{hotelId}/itemInventory |
| 3.1 | Add Room | Create ON HOLD Reservation | POST /rsv/v1/hotels/{hotelId}/reservations ON HOLD |
| 3.1 | Add Room | Create Item Inventory Hold | POST /inv/v0/hotels/{hotelId}/itemInventoryHold |
| 3.1 | Add Room | Update Reason for Stay | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 4.1 | Remove Room | Retrieve Rate Plans (amendable) | GET /rtp/v0/ratePlans |
| 4.1 | Remove Room | Remove Room | DELETE /rsv/v0/reservations/{reservationId} |
| 5.1 | Edit Room | Retrieve Packages | GET /rtp/v1/packages |
| 5.1 | Edit Room | Update Packages | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 5.1 | Edit Room | Update Reservation (amended details) | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 6.1 | Update Ancillaries | Retrieve Packages | GET /rtp/v1/packages |
| 6.1 | Update Ancillaries | Retrieve Package Groups | GET /rtp/v0/hotels/{hotelId}/packageGroups |
| 6.1 | Update Ancillaries | Update Reservation (packages) | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 7 | Confirm Amend | Post Refunds | POST /csh/v1/hotels/{hotelId}/reservations/{reservationId}/depositFolios |
| 7 | Confirm Amend | Cancel Original | POST /rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellations |
| 7 | Confirm Amend | Create Deposit Folios (new rooms) | POST /csh/v1/hotels/{hotelId}/reservations/{reservationId}/depositFolios |
| 7 | Confirm Amend | Confirm Reservation | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 7 | Confirm Amend | Remove Cancellation Policy | DELETE /rsv/v0/hotels/{hotelId}/reservations/{reservationId}/cancellationPolicies |
| 7 | Confirm Amend | Add Cancellation Policy | POST /rsv/v0/hotels/{hotelId}/reservations/{reservationId}/cancellationPolicies |
| 7 | Confirm Amend | Remove Routing Instructions | DELETE /csh/v1/hotels/{HotelId}/reservations/{ReservationId}/routingInstructions/folio |
| 7 | Confirm Amend | Move Payment to Window 2 | PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} |
| 7 | Confirm Amend | Retrieve Credit Card Info | GET /fof/config/v1/creditCardInfo |
| 7 | Confirm Amend | Save Deposit Folios | POST /csh/v1/hotels/{hotelId}/reservations/{reservationId}/depositFolios |

### Complete Opera API Domain Coverage

| Domain | Module | Endpoints Used |
|--------|--------|----------------|
| Reservations | RSV v1 | GET, POST, PUT, DELETE reservations; rateInfo; cancellations |
| Reservations | RSV v0 | DELETE reservations; cancellationPolicies |
| Inventory | INV v1 | GET hotelInventory |
| Inventory | INV v0 | GET itemInventory; POST itemInventoryHold |
| Configuration | ENT config v1 | GET hotels/{hotelId} |
| Profiles | CRM v1 | GET profiles; POST profiles |
| Rate & Packages | RTP v1 | GET packages |
| Rate & Packages | RTP v0 | GET ratePlans; GET packageGroups |
| Cashiering | CSH v1 | POST depositFolios; DELETE routingInstructions |
| Room Config | RM config v1 | GET roomTypes |
| Property Availability | PAR v1 | GET availability |
| Front Office | FOF config v1 | GET creditCardInfo |

### Service Architecture (Flow)

```
User → Frontend → hotel-entity-service → ohip-adapter-service → OPERA
User → Frontend → hotel-reservation-entity-service → ohip-adapter-service → OPERA
User → Frontend → basket-service → ohip-adapter-service → OPERA
```

Key services in the integration chain:
- **hotel-entity-service** — availability, hotel info, packages
- **hotel-reservation-entity-service** — reservation CRUD, amendments, cancellation
- **ohip-adapter-service** — central proxy making all direct Opera API calls
- **basket-service** — payment orchestration
