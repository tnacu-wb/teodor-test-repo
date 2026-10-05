---
inclusion: manual
---

# Opera & OHIP — Technical Reference

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 25 (OpenJDK) |
| Framework | Spring Boot 4.0.6 |
| Build | Maven (via wrapper `./mvnw`) |
| HTTP Client | Spring WebClient (reactive) |
| Mapping | MapStruct |
| API Generation | OpenAPI Generator (spring server) |
| Testing | JUnit 5, Mockito, WireMock |
| Architecture Tests | ArchUnit |
| Real-time Events | WebSocket + GraphQL (Spring GraphQL) |
| Database | PostgreSQL (for availability cache) |
| Feature Flags | Unleash |

## Authentication with Opera

- **Auth type:** OAuth2 Client Credentials
- **Token endpoint:** `{opera-host}/oauth/v1/tokens`
- **Credentials:** `opera.clientId` / `opera.clientSecret` (app properties)
- **App key:** Passed as `x-app-key` header on all OHIP requests
- **Hotel ID:** Passed as `x-hotelid` header

## Key Configuration Properties

```yaml
config.service.ohip:
  reservationEndpoint: /rsv/v1/hotels/{hotelId}/reservations
  reservationIdEndpoint: /rsv/v1/hotels/{hotelId}/reservations/{reservationId}
  foliosEndPoint: /csh/v1/hotels/{hotelId}/reservations/{reservationId}/folios
  depositFoliosEndpoint: /csh/v1/hotels/{hotelId}/reservations/{reservationId}/depositFolios
  ratePlansEndpoint: /rtp/v1/ratePlans
  cancellationPoliciesEndpoint: /rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellationPolicies
  hotelConfigEndpoint: /ent/config/v1/hotels/{hotelId}
  routingInstructions: /csh/v1/hotels/{HotelId}/reservations/{ReservationId}/routingInstructions
```

## Request Patterns

### Standard OHIP Request Headers
```
x-hotelid: {hotelId}
x-app-key: {appKey}
Authorization: Bearer {oauthToken}
Content-Type: application/json
```

### Timezone Handling
- Hotel timezone retrieved from: `hotelConfigInfo.propertyControls.dateTimeFormatting.timeZoneRegion`
- Mapped via `HotelInfoMapper` to `hotelTimeZone` field
- Used for cancellation deadline calculations (ZoneId conversion)
- Folio/deposit operations do NOT currently use timezone conversion

### Deposit Folio Flow (PAY_NOW)
1. Retrieve ON HOLD reservation
2. Retrieve reservation cost (rateInfo)
3. Calculate total cost of stay
4. If totalCostOfStay == outstandingCostOfStay → create deposit folio
5. POST to `/csh/v1/hotels/{hotelId}/reservations/{reservationId}/depositFolios`

### Folio History
- Constant: `INCLUDE_FOLIO_HISTORY = "includeFolioHistory"`
- Always set to `false` — folio history is never retrieved
- Endpoint: getFoliosAciAmount uses `/csh/v1/.../folios` with `includeFolioHistory=false`

## Error Handling

- Custom exception: `HotelReservationException` with `ErrorCode` enum
- Key error codes:
  - `OHIP_GET_FOLIOS_ACI_AMOUNT_EXCEPTION` (945)
  - `OHIP_SEND_DEPOSIT_FOLIOS_EXCEPTION` (951)
  - `OHIP_CANCEL_RESERVATION_EXCEPTION` (950)
- Error logging via `ExceptionLogger.log()`
- HTTP status checked via `onStatus(HttpStatusCode::isError, ...)`

## Code Generation

OpenAPI specs generate:
- DTOs (Data Transfer Objects) in `target/generated-sources/openapi/`
- Package: `uk.co.whitbread.hotel.ohip.adapter.generated.models`
- Generator: `openapi-generator-maven-plugin` with `spring` generator

## Testing Approach

- **Unit tests:** Mockito-based, mock WebClient responses
- **WireMock tests:** Full HTTP integration tests with stubbed Opera responses
  - Stubs in `src/test/resources/stubs/` (JSON files)
  - Example: `ohip_get_folios_success.json`
- **ArchUnit tests:** Coding convention enforcement (no field injection, Java 16+ collectors)
- **Integration tests:** Spring Boot test context with H2/PostgreSQL

## Feature Flags (Unleash)

- `getDepositFolioPostAfterDisableOnHold` — controls deposit folio posting flow
- Used in `HotelReservationOutPortImpl` to switch between legacy and new deposit folio logic

## Key Constants (OhipConstants.java)

```java
INCLUDE_FOLIO_HISTORY = "includeFolioHistory"
BOOLEAN_FALSE = "false"
HOTEL_ID_ERROR = "The hotelId should be provided"
RESV_NAME_ID = "RESV_NAME_ID"
STANDARD_RATE = "STANDARD"
```

## Upgrade Compatibility Notes

- The codebase uses **tolerant parsing** (`@JsonIgnoreProperties(ignoreUnknown = true)`) on response DTOs
- This means additive field changes from Opera upgrades won't break deserialization
- However, strict enum handling or field-presence assertions may still be affected
- Generated DTOs from OpenAPI specs should be regenerated when spec versions change


---

## opera-ohip-app (Test Data Tool)

### Location
`tools/opera-ohip-app/`

### Purpose
Next.js application for directly updating Opera Cloud test data via OHIP APIs. Used by QA and developers to configure hotel availability, rates, and restrictions in lower environments.

### Tech Stack
- Next.js 15 (App Router)
- TypeScript
- Axios (HTTP client)
- Multi-environment support (UAT, SIT, PERF)

### Features

| Feature | Endpoint | Description |
|---------|----------|-------------|
| Update Daily Rates | PUT /rtp/v1/hotels/{hotelId}/ratePlanSchedules | Auto-batches large room type lists |
| Clear Restrictions | DELETE /inv/v0/hotels/{hotelId}/restrictions | Date range support |
| Update Sell Limits | PUT /inv/v0/hotels/{hotelId}/sellLimitsByDateRange | Room Type or Room Class level |
| Update Package Code | PUT /rtp/v0/hotels/{hotelId}/packages/{packageCode} | Auto-fills config from stored package data |

### Sell Limits — Code Category Support

The Sell Limits form supports two code categories:

| Code Category | API Value | Code Values | UI Input |
|--------------|-----------|-------------|----------|
| Room Type | `RoomType` | Any room type code | Free text |
| Room Class | `roomClass` | ST, PP, BG, SV, PV, BV, SE | Dropdown |

### Room Class Values

| Code | Description |
|------|-------------|
| ST | Standard |
| PP | Premier Plus |
| BG | Bigger |
| SV | Superior |
| PV | Premier View |
| BV | Bigger View |
| SE | Suite/Executive |

### API Payload Format (Sell Limits)

```json
{
  "sellLimitsByDateRange": [{
    "sellLimitDateRanges": [{
      "actionType": "SET_AVAILABLE",
      "startDate": "2026-08-19",
      "endDate": "2026-08-22",
      "sunday": true, "monday": true, "tuesday": true,
      "wednesday": true, "thursday": true, "friday": true, "saturday": true,
      "amount": "2",
      "flatOrPercentage": "F"
    }],
    "hotelId": "{hotelId}",
    "codeCategory": "roomClass",
    "codeValue": "ST"
  }]
}
```

### Configuration
Environment credentials in `.env.local`:
```
UAT_OHIP_BASE_URL=https://...
UAT_OHIP_CLIENT_ID=...
UAT_OHIP_CLIENT_SECRET=...
UAT_OHIP_APP_KEY=...
UAT_OHIP_SCOPE=...
UAT_OHIP_ENTERPRISE_ID=...
```

### Running
```bash
cd tools/opera-ohip-app
npm install
npm run dev
# Opens at http://localhost:3000
```


### Update Package Code Module

**Simplified UX:** Users only input Hotel ID, Package Code (dropdown), and Unit Price. All other fields are auto-populated from `lib/ohip/packageData.ts`.

**User inputs:**
- Hotel ID
- Package Code (dropdown of 30+ packages)
- Unit Price (GBP)
- Schedule Start/End dates (defaults: 2022-12-01 to 2045-12-21)

**Auto-filled from package config:**
- Description, short description
- Posting rhythm (EveryNight / ArrivalNight / LastNight)
- Price calculation rule (FlatRate / PerRoom / PerAdult)
- Web bookable, add to rate, print separate line, sell separate
- Inventory items (if applicable)
- Forecast group, catering flag

**Key files:**
- `lib/ohip/packageData.ts` — All package code configurations with full metadata
- `lib/ohip/packages.ts` — API client for PUT /rtp/v0/hotels/{hotelId}/packages/{packageCode}
- `app/api/packages/route.ts` — Next.js API route handler
- `app/components/PackagesForm.tsx` — Simplified form with dropdown + auto-fill

**API:** `PUT /rtp/v0/hotels/{hotelId}/packages/{packageCode}`
