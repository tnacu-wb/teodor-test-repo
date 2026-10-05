---
inclusion: manual
---

# Opera & OHIP — Service Structure

## Architecture Overview

```
┌─────────────┐    ┌──────────────────────────────┐    ┌──────────────────┐    ┌───────┐
│  Frontend   │───▶│  hotel-entity-service         │───▶│                  │    │       │
│  (React)    │    │  hotel-reservation-entity-svc │    │  ohip-adapter-   │───▶│ OPERA │
│             │───▶│  basket-service               │───▶│  service         │    │ Cloud │
└─────────────┘    └──────────────────────────────┘    └──────────────────┘    └───────┘
                              │                                │
                              ▼                                ▼
                   ┌──────────────────────┐         ┌──────────────────┐
                   │ availability-cache-  │         │ availability-    │
                   │ service-opera        │         │ business-events- │
                   └──────────────────────┘         │ service-opera    │
                                                    └──────────────────┘
```

## Services & Responsibilities

### ohip-adapter-service
- **Location:** `backend/discover-search/services/ohip-adapter-service/`
- **Role:** Central OHIP proxy — the ONLY service that makes direct REST calls to Opera Cloud
- **Owns:** OpenAPI specs for Opera APIs, request/response mapping, error handling
- **Key packages:**
  - `infrastructure.rest.client.reservation.ohip` — OhipReservationClient
  - `infrastructure.rest.client.hotel` — hotel config/info
  - `infrastructure.rest.client.rates` — rate plans
  - `infrastructure.rest.client.packages` — packages & package groups
  - `infrastructure.rest.client.availability` — availability
- **Config:** OHIP endpoint URLs, auth credentials, timeout settings

### hotel-reservation-entity-service
- **Location:** `backend/manage-modify/services/hotel-reservation-entity-service/`
- **Role:** Reservation orchestration — booking, amendments, cancellations
- **Calls:** ohip-adapter-service (via REST client)
- **Key flows:** create booking, amend dates/rooms/occupancy, cancel, confirm, deposit folios

### hotel-entity-service
- **Location:** `backend/discover-search/services/hotel-entity-service/`
- **Role:** Hotel information, availability, packages for the search/booking frontend
- **Calls:** ohip-adapter-service, availability-cache-service

### availability-cache-service-opera
- **Location:** `backend/discover-search/services/availability-cache-service-opera/`
- **Role:** Caches Opera availability data in a local DB for fast retrieval
- **Data:** Room rates, restrictions, hotel availability

### availability-business-events-service-opera
- **Location:** `backend/discover-search/services/availability-business-events-service-opera/`
- **Role:** Subscribes to Opera business events via WebSocket/GraphQL
- **Processes:** Rate restriction changes, inventory updates
- **Connection:** WebSocket subscription to Opera's GraphQL business events API

### content-entity-service
- **Location:** `backend/discover-search/services/content-entity-service/`
- **Role:** Hotel content, configuration, timezone mapping

### hotel-wallet-service-opera
- **Location:** `backend/arrive-stay-leave/services/hotel-wallet-service-opera/`
- **Role:** Hotel wallet/key generation for guest stays

## Opera API Domains Used

| Domain | Module | Purpose |
|--------|--------|---------|
| RSV v1 | Reservations | Reservation CRUD, rate info, cancellations |
| RSV v0 | Reservations (legacy) | Delete reservations, cancellation policies |
| INV v1 | Inventory | Hotel room inventory |
| INV v0 | Inventory (legacy) | Item inventory, inventory holds |
| ENT config v1 | Enterprise Config | Hotel configuration |
| CRM v1 | Profiles | Guest/booker profile management |
| RTP v1 | Rate & Packages | Packages |
| RTP v0 | Rate & Packages (legacy) | Rate plans, package groups |
| CSH v1 | Cashiering | Deposit folios, routing instructions |
| RM config v1 | Room Config | Room types |
| PAR v1 | Property Availability | Hotel availability |
| FOF config v1 | Front Office | Credit card info |

## OpenAPI Specs in Repo

Located in `ohip-adapter-service/src/main/resources/openapi/`:
- `operareservationsv0api.yaml` — Reservations (v0)
- `operarateapi.yaml` — Rate plans
- `operaratev0api.yaml` — Rate plans (v0)
- `operaparv0api.yaml` — Property availability

## Business Events Integration

The `availability-business-events-service-opera` connects via WebSocket to Opera's GraphQL subscription API to receive real-time rate restriction and inventory change events. These events update the local availability cache.

**Connection details:**
- Protocol: WebSocket (wss://)
- API: GraphQL subscription
- Auth: OAuth2 token from Opera
- Events processed: Rate restrictions (min/max LOS, rate code changes)


---

## opera-ohip-app (Test Data Tool)

**Location:** `tools/opera-ohip-app/`

**Role:** Next.js app for directly manipulating Opera Cloud test data via OHIP REST APIs. Used by QA and developers in lower environments (UAT, SIT, PERF).

### App Structure

```
tools/opera-ohip-app/
├── app/
│   ├── api/
│   │   ├── health/route.ts       # Health check endpoint
│   │   ├── ping/route.ts         # Ping endpoint
│   │   ├── rate-plan/route.ts    # Daily rates API handler
│   │   ├── restrictions/route.ts # Restrictions API handler
│   │   ├── sell-limits/route.ts  # Sell limits API handler
│   │   └── packages/route.ts     # Package update API handler
│   ├── components/
│   │   ├── EnvironmentSelector.tsx
│   │   ├── RatePlanForm.tsx
│   │   ├── ResponsePanel.tsx
│   │   ├── RestrictionsForm.tsx
│   │   ├── SellLimitsForm.tsx    # Supports Room Type + Room Class
│   │   └── PackagesForm.tsx      # Simplified: dropdown + unit price only
│   ├── globals.css
│   ├── layout.tsx
│   └── page.tsx
├── lib/ohip/
│   ├── auth.ts                   # OAuth2 token management
│   ├── environments.ts           # Multi-env config resolver
│   ├── packageData.ts            # All package code configs (auto-fill data)
│   ├── packages.ts               # Package update API client
│   ├── ratePlan.ts               # Rate plan update logic
│   ├── restrictions.ts           # Restriction clearing logic
│   ├── sellLimits.ts             # Sell limits update (Room Type/Class)
│   └── validation.ts             # Input validation / SSRF protection
├── .env.example
├── .env.local                    # Actual credentials (gitignored)
├── Dockerfile
├── package.json
└── README.md
```

### Key Endpoints Used

| Opera API | Method | Purpose |
|-----------|--------|---------|
| `/rtp/v1/hotels/{hotelId}/ratePlanSchedules` | PUT | Update daily rates |
| `/inv/v0/hotels/{hotelId}/restrictions` | DELETE | Clear restrictions |
| `/inv/v0/hotels/{hotelId}/sellLimitsByDateRange` | PUT | Set sell limits (Room Type or Room Class) |
| `/rtp/v0/hotels/{hotelId}/packages/{packageCode}` | PUT | Update package code (price, schedule) |
