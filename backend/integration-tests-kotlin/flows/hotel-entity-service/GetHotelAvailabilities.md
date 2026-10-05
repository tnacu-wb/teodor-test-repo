# Hotel Entity Service: getHotelAvailabilities Flow

Returns room and rate availability for a single hotel, including occupancy validation, on-sale checks, OHIP availability, and post-processing (suppression, city tax, soft bundles, MLoS).

```http
GET /v1/hotels/{hotelId}/availabilities?arrivalDate={yyyy-MM-dd}&departureDate={yyyy-MM-dd}&adultsNumber={n}&channel={channel}&subchannel={subchannel}&language={language}
Host: hotel-entity-service
Accept: application/json
Authorization: Bearer {token}   # required when channel=BB
```

## Flow

Hotel entity maps the public query into a domain request, then enforces occupancy rules via `rules-agent-entity-service` (`GET /v1/rules/max-room-occupancy`). When `roomTypes` is omitted or blank, accepted room-type variants are derived from those rules; otherwise the supplied room types are validated.

If `promoKind=UNIQUE` and `promotionCode` is present, hotel entity resolves the Opera promo code through `promo-service` before availability lookup.

It then reads hotel on-sale status from Redis (cache miss falls through to `ohip-adapter-service` `GET /ohip/hotels/status`). Only on-sale hotels that pass HUB brand restrictions continue; otherwise an empty unavailable response is returned.

For on-sale hotels, hotel entity loads brand via `content-entity-service` and calls `ohip-adapter-service` `GET /ohip/hotels/{hotelId}/availabilities` once per room-type variant. OHIP resolves room substitutions and channel rate-plan sets via rules-agent, then calls Opera room types, inventory, availability, and rate-info APIs (OAuth as needed).

After OHIP returns, hotel entity applies company-rate suppression (BB/CCUI when flagged), global rate-suppression rules, BUSIFLEX/FLEXRATE exclusion (non-CCUI), configured promotional excluded rates, city-tax price adjustment when flagged, optional twin-room and soft-bundle enrichment, and optional CCUI MLoS restriction labeling. Dates on the response are reset to the request arrival/departure, and `available` is forced false when no room rates remain.

## Mermaid Flow

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Hotel as hotel-entity-service
    participant Rules as rules-agent-entity-service
    participant Promo as promo-service
    participant Redis as Redis
    participant Ohip as ohip-adapter-service
    participant Content as content-entity-service
    participant CDH as cdh-adapter-service
    participant Company as company-entity-service
    participant Basket as basket-service
    participant Opera as Opera APIs

    Client->>Hotel: GET /v1/hotels/{hotelId}/availabilities
    alt channel is BB
        Hotel->>Hotel: require authenticated user and access level for room count
    else other channels
        Hotel->>Hotel: allow without BB user auth
    end

    Hotel->>Rules: GET /v1/rules/max-room-occupancy?channelId={channel}
    Rules-->>Hotel: MaxRoomOccupancyResponse
    alt roomTypes present and non-blank
        Hotel->>Hotel: validate occupancy against accepted room types
    else roomTypes absent or blank
        Hotel->>Hotel: derive one or more room-type variants
    end

    opt promoKind=UNIQUE and promotionCode present
        Hotel->>Promo: GET /v1/promo/batches/promo-kind?promoCode={promotionCode}
        Promo-->>Hotel: operaPromoCode (or reject redeemed/expired)
        Hotel->>Hotel: replace promotionCode for OHIP; restore client code on response
    end

    Hotel->>Redis: read HotelStatusDto by hotelId
    alt on-sale status cache miss
        Hotel->>Ohip: GET /ohip/hotels/status?hotelIds={hotelId}
        Ohip->>Opera: hotel details / status
        Opera-->>Ohip: hotel status
        Ohip-->>Hotel: HotelStatusDto list
        Hotel->>Redis: cache HotelStatusDto
    else cache hit
        Redis-->>Hotel: HotelStatusDto
    end

    alt hotel not on sale
        Hotel-->>Client: 200 unavailable HotelAvailabilityResponseDto
    else hotel on sale
        Hotel->>Content: GET /v1/content/hotels/{hotelId}/information?country=gb&language=en
        Content-->>Hotel: brand (HUB rules)
        alt HUB brand with TWIN/FAM/children/cots
            Hotel-->>Client: 200 unavailable empty rates
        else HUB rules pass
            loop once per room-type variant
                Hotel->>Ohip: GET /ohip/hotels/{hotelId}/availabilities
                Ohip->>Rules: GET /v1/rules/room-substitutions
                Rules-->>Ohip: substitutions
                opt no promotionCode on OHIP request
                    Ohip->>Rules: GET /v1/rules/channel-info
                    Rules-->>Ohip: ratePlanSets
                end
                Ohip->>Opera: room types, inventory, availability, rateInfo
                Opera-->>Ohip: availability and prices
                Ohip-->>Hotel: AvailabilityResponseDto
            end

            opt originalBasketReference present and city-tax flag on
                Hotel->>Basket: GET /v1/baskets/{basket-reference}
                Basket-->>Hotel: reservation sourceId
                Hotel->>Ohip: GET /ohip/v1/reservations/ids?hotelId={hotelId}&reservationIds={id}
                Ohip-->>Hotel: purpose of stay
            else city-tax flag on without amend
                Hotel->>Content: GET /v1/content/global-config
                Hotel->>Content: hotel information (or Redis content cache)
            end

            opt roomTypes contains TWIN
                Hotel->>Ohip: GET /ohip/hotels/{hotelId}/packages
                Ohip-->>Hotel: packages for twin filtering
            end

            opt softBundle set, single adult room, all children=0
                Hotel->>Ohip: GET /ohip/hotels/{hotelId}/packages
                Hotel->>Content: GET /v1/content/meals
                Hotel->>Content: GET /v1/content/labels/extras
            end

            opt companyId present, channel BB or CCUI, company-rate-suppression on
                opt numeric companyId
                    Hotel->>Company: GET /v1/companies/opera-id/{id}
                    Hotel->>CDH: POST /v1/cdh/account/companies
                end
                Hotel->>CDH: GET /v1/cdh/account/company/{accountId}/suppress-rates
                CDH-->>Hotel: suppressed rate codes
            end

            Hotel->>Rules: GET /v1/rules/rate-suppressions
            Note over Hotel,Rules: in-memory cache until expiry
            Rules-->>Hotel: rateSuppressionList
            Hotel->>Hotel: apply suppressions, flex exclusion, excluded rates

            opt channel CCUI, show-mlos flag on, available with empty roomRates
                Hotel->>Ohip: GET /ohip/hotels/{hotelId}/restrictions?startDate={arrival}&endDate={departure}
                Ohip-->>Hotel: MinimumLengthOfStay restrictions
            end

            Hotel-->>Client: 200 HotelAvailabilityResponseDto
        end
    end
```

## Features

- Single-hotel availability for stay dates, occupancy, channel, and subchannel
- Optional room-type list or automatic variants from max-room-occupancy rules
- BB channel authorization: authenticated user whose access level allows the requested room count
- On-sale gate via Redis-backed hotel status (OHIP fallback)
- HUB brand restriction: TWIN, FAM, children, or cots requests return unavailable
- UNIQUE promo resolution to Opera promo code via promo-service
- OHIP availability with optional companyId and promotionCode
- Rate filtering: company suppressions, global rate suppressions, BUSIFLEX/FLEXRATE handling, promotional excluded rates
- City-tax net/effective price adjustment (standard and amend via basket + lightweight reservation)
- Twin room package filtering and soft-bundle enrichment
- CCUI MLoS label when inventory looks available but no rates remain under a minimum-length restriction
- Employee rate plan code handling maps privilege company id and folds employee rates into FLEXRATE

## Feature Flags

| Flag | Effect when enabled |
| --- | --- |
| `release_pi_ccui_city_tax_uk` | Runs city-tax eligibility and price adjustment (global config + hotel information; amend path uses basket and OHIP lightweight reservations) |
| `release_bb_ccui_company_rate_suppression` | For BB/CCUI with `companyId`, loads CDH company suppress-rates and removes matching rate plans |
| `release_ccui_show_mlos_label` | For CCUI only, when hotel is available but `roomRates` is empty, calls OHIP restrictions and may set `mlos=true` |

## Request

Path and query parameters on the public API:

| Parameter | Required | Notes |
| --- | --- | --- |
| `hotelId` | Yes | Path parameter; Opera hotel id |
| `arrivalDate` | Yes | `yyyy-MM-dd`; validated with `departureDate` |
| `departureDate` | Yes | `yyyy-MM-dd`; must be after arrival |
| `roomTypes` | No | When omitted or blank, variants come from max-room-occupancy rules |
| `adultsNumber` | Yes | Per-room adult counts; sent to OHIP as `adults` |
| `childrenNumber` | No | Per-room children; sent to OHIP as `children` (defaults to 0) |
| `cotsRequired` | No | Per-room flags; defaults to false downstream |
| `companyId` | No | Forwarded to OHIP; may drive company-rate suppression |
| `channel` | Yes | BB requires auth; used for rules and OHIP |
| `subchannel` | Yes | Forwarded to OHIP / channel-info |
| `language` | Yes | Forwarded to OHIP / channel-info |
| `country` | No | Soft bundles and content locale |
| `ratePlanCodes` | No | Employee rate handling only on this endpoint; not sent to OHIP single-hotel availability |
| `promotionCode` | No | Forwarded to OHIP; UNIQUE promo resolves via promo-service first |
| `originalBasketReference` | No | Amend city-tax path |
| `softBundle` | No | Soft-bundle enrichment for eligible single-room requests |
| `promoKind` | No | `UNIQUE` triggers promo-service resolution |

Downstream OHIP call shape (from hotel entity):

```http
GET /ohip/hotels/{hotelId}/availabilities?arrivalDate={arrival}&departureDate={departure}&roomTypes={types}&adults={adults}&children={children}&cotsRequired={cots}&channel={channel}&subchannel={subchannel}&language={language}[&companyId=...][&promotionCode=...]
```

## Branches

| Trigger | Behavior |
| --- | --- |
| `channel=BB` | Spring `@PreAuthorize` requires authenticated user whose access level covers the requested room count |
| Empty / blank `roomTypes` | Derive variants from max-room-occupancy; call OHIP once per variant and aggregate |
| Hotel not on sale or HUB rule fail | Return 200 with `available=false` and empty `roomRates` |
| `promoKind=UNIQUE` + `promotionCode` | Resolve Opera promo via promo-service; restore client promo code on matching rates |
| On-sale status Redis hit | Skip OHIP `/hotels/status` |
| `originalBasketReference` + city-tax flag | Basket stay item then OHIP lightweight reservation for purpose-of-stay |
| City-tax flag without amend | Content global-config and hotel information (content Redis when present) |
| `roomTypes` includes `TWIN` | OHIP packages for twin special-request filtering |
| `softBundle` + one adult room + all children 0 | Packages + content meals + extras labels |
| `companyId` + BB/CCUI + company-rate-suppression flag | Resolve CDH account id if needed, then suppress rates |
| Rate-suppression in-memory miss/expiry | Refresh from rules-agent `/v1/rules/rate-suppressions` |
| CCUI + MLoS flag + available with no rates | OHIP restrictions for MinimumLengthOfStay |
| Employee `ratePlanCodes` | Inject privilege `companyId` and fold employee rate into FLEXRATE |
