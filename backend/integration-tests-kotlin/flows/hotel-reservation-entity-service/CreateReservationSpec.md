# Hotel Reservation Entity Service: createReservation Flow

This endpoint creates one or more on-hold hotel reservations, records them in a basket, and returns the basket reference and Opera reservation details.

```http
POST /v1/reservations
Host: hotel-reservation-entity-service:9103
Content-Type: application/json
Authorization: Bearer {token}   # required for BB; optional for other channels
```

## Flow

The controller validates and maps the request. The `BB` channel must be authenticated, and the user's access level must allow the requested room count. The service then creates an empty basket in `basket-service`, uses its booking reference as the external reference for every requested reservation, and enriches authenticated BB, PI, or CCUI requests with account identifiers.

Before creating the Opera reservations, the service removes unauthorized fixed prices, optionally resolves city-tax purpose-of-stay data through `content-entity-service`, and processes a promotion from the first room. A unique promotion is validated and translated through `promo-service`; every valid promotion is also written to the basket.

The request is sent to `ohip-adapter-service`. OHIP validates every requested room's rate with Opera, optionally substitutes Whitbread room types, resolves the channel source code through `rules-agent-entity-service`, checks cot inventory, expands soft-bundle package groups, and creates each Opera reservation. Depending on `getReservationsByIds`, it may read the newly created reservations back to return full details and validate their deposit policies.

After OHIP succeeds, hotel reservation stores a 30-minute reservation summary in Redis. When occupancy supplements are enabled, it also loads the hotel's supplement price from rules-agent. Finally, it adds the created reservation IDs to the basket using the latest ETag and returns `201 Created`.

## Mermaid Flow

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Reservation as hotel-reservation-entity-service
    participant Basket as basket-service
    participant Promo as promo-service
    participant Content as content-entity-service
    participant Ohip as ohip-adapter-service
    participant Rules as rules-agent-entity-service
    participant Redis as Redis
    participant Token as token-service / Opera OAuth
    participant OperaRate as Opera Rate Info API
    participant OperaInv as Opera Inventory API
    participant OperaPkg as Opera Package Groups API
    participant OperaRes as Opera Reservations API

    Client->>Reservation: POST /v1/reservations
    alt channel is BB
        Reservation->>Reservation: require authenticated user and access level for room count
    else channel is not BB
        Reservation->>Reservation: permit anonymous or authenticated request
    end
    Reservation->>Reservation: validate body and 8-character IATA number when supplied
    Reservation->>Basket: POST /v1/baskets
    Basket-->>Reservation: basket reference, booking reference, ETag
    Reservation->>Reservation: set booking reference as every reservation's externalReferenceId

    opt authenticated user
        Reservation->>Reservation: enrich BB, PI, or legacy CCUI account fields
    end
    Reservation->>Reservation: set bookingType and remove unauthorized fixed ratePrices

    opt release_pi_ccui_city_tax_uk enabled and channel PI, BB, or CCUI
        Reservation->>Content: GET /v1/content/hotels/{hotelId}/information?country=gb&language=en
        Content-->>Reservation: city-tax booking/effective dates
        Reservation->>Reservation: resolve Opera purpose of stay (LEI or NTLEI)
    end

    opt first room has promoKind and promotionCode
        alt promoKind is UNIQUE
            Reservation->>Promo: GET /v1/promo/batches/promo-kind?promoCode={clientCode}
            Promo-->>Reservation: status and optional operaPromoCode
            Reservation->>Reservation: reject invalid code or replace it for Opera
        end
        Reservation->>Basket: PUT /v1/baskets/{basketReference}/promotions (If-Match)
        Basket-->>Reservation: updated basket and ETag
    end

    Reservation->>Ohip: POST /ohip/v1/reservations
    Ohip->>Ohip: map request, including package consumption details

    opt an Opera access token is required
        alt release_ohip_use_token_service enabled
            Ohip->>Token: POST /v1/tokens/opera/access-token
        else direct Opera OAuth mode
            Ohip->>Token: POST /oauth/v1/tokens
        end
        Token-->>Ohip: access token
    end

    opt any supplied room type is a Whitbread room type
        loop each requested reservation
            Ohip->>Rules: GET /v1/rules/room-substitutions
            Rules-->>Ohip: candidate Opera room types
            Ohip->>OperaInv: GET /inv/v1/hotels/{hotelId}/hotelInventory
            OperaInv-->>Ohip: available room types
            Ohip->>Ohip: select an available substituted room type
        end
    end

    loop each requested room and date interval
        Ohip->>OperaRate: GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo
        OperaRate-->>Ohip: nightly prices and total
        Ohip->>Ohip: reject zero or missing nightly rates
    end

    Ohip->>Rules: GET /v1/rules/channel-info?channel={channel}&subchannel={subchannel}&language={language}&pms=OP
    Rules-->>Ohip: Opera sourceId

    opt one or more rooms request a cot
        Ohip->>OperaInv: GET /inv/v1/hotels/{hotelId}/itemInventory
        OperaInv-->>Ohip: cot item code and availability
        Ohip->>Ohip: reject insufficient stock or attach the cot item
    end

    opt release_create_reservation_with_soft_bundles enabled
        loop each selected package
            alt PackageGroupsCache hit
                Ohip->>Redis: read package group
                Redis-->>Ohip: cached package group
            else PackageGroupsCache miss
                Ohip->>OperaPkg: GET /rtp/v1/hotels/{hotelId}/packageGroups?code={packageCode}&limit=50
                OperaPkg-->>Ohip: package group members
                Ohip->>Redis: cache package group for one day
            end
        end
        Ohip->>Ohip: replace package groups with member packages
    end

    par Create reservations (POST calls use configured max concurrency 1)
        loop each requested reservation
            Ohip->>OperaRes: POST /rsv/v1/hotels/{hotelId}/reservations
            OperaRes-->>Ohip: created reservation ID
        end
    and Apply fixed rates (each PUT may interleave with later create POSTs)
        opt release_pi_bb_ccui_distr_fixed_rate enabled
            loop each emitted reservation ID
                Ohip->>OperaRes: PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}
                OperaRes-->>Ohip: changed reservation
            end
        end
    end

    alt getReservationsByIds is true
        loop each created reservation ID
            Ohip->>OperaRes: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}?fetchInstructions=...
            OperaRes-->>Ohip: full reservation and deposit policies
        end
        Ohip->>Ohip: reject multiple policy codes and compute total from amountDue
    else getReservationsByIds is false
        Ohip->>Ohip: build lightweight response from created IDs
    end
    Ohip-->>Reservation: created reservations

    Reservation->>Redis: cache reservation summary by basket reference (30 minutes)
    opt release_pi_ccui_distr_web3_occupancy_supplement enabled
        Reservation->>Rules: GET /v1/rules/occupancy-supplement?hotelId={hotelId}
        Rules-->>Reservation: supplement price
        Reservation->>Reservation: mark applicable basket items from OTA/adult occupancy
    end
    Reservation->>Basket: POST /v1/baskets/{basketReference}/items (If-Match)
    Basket-->>Reservation: updated basket
    Reservation-->>Client: 201 ReservationResponseDto
```

## Features

- Multi-room on-hold reservation creation under one basket
- BB room-count authorization and authenticated account enrichment for BB, PI, and CCUI
- Opera rate validation before reservation creation
- Client promotion storage plus unique-promotion validation and Opera-code resolution
- Optional city-tax purpose-of-stay conversion for PI, BB, and CCUI
- Optional cot inventory validation and attachment
- Optional Whitbread-to-Opera room substitution for amend-style room types
- Fixed-rate input protection for authenticated DISTR callers with `SCOPE_RATE_PRICE::WRITE`
- Redis reservation summary with a 30-minute TTL
- ETag-protected basket promotion and item updates

## Feature Flags

| Flag | Effect when enabled |
| --- | --- |
| `release_ccui_agent_id_log` | Stops adding the authenticated CCUI user's email to the legacy `ccuiUserEmailId` field during create-reservation enrichment |
| `release_pi_ccui_city_tax_uk` | For PI, BB, and CCUI, loads hotel city-tax dates from content and may convert leisure purpose of stay from `LEI` to Opera code `NTLEI` |
| `release_pi_ccui_distr_web3_occupancy_supplement` | Forces full reservation retrieval for multi-room requests, loads the hotel's occupancy-supplement rule, and marks basket items using OTA/adult-occupancy rules |
| `release_create_reservation_with_soft_bundles` | Resolves selected Opera package groups and replaces them with their member packages before creation |
| `release_set_default_payment_method_DS` | Owned by ohip-adapter-service on the downstream create call: switches the Opera reservation's default payment method from `CA` to `DS` |
| `release_distr_booking_fee` | For priced soft-bundle selections, carries the price in the intermediate package-group tuple; the current member mapping does not consume that value |
| `release_pi_bb_ccui_distr_fixed_rate` | Sends an Opera change-reservation request after each create to apply fixed rate details |
| `consumption_details_default_quantity` | Copies each package consumption `totalQuantity` into `defaultQuantity` while OHIP maps the request |
| `release_ohip_use_token_service` | Obtains credentials through token-service instead of direct Opera OAuth for Opera calls |
| `release_ohip_use_token_refresh_skew` | Refreshes Opera access tokens early using the configured clock skew |

## Request

Important JSON body fields:

| Field | Required | Effect |
| --- | --- | --- |
| `reservations` | Yes | Non-empty list of rooms to create; the first room's `hotelId` drives basket, promotion, city-tax, occupancy-rule, and response-level hotel data |
| `reservations[].hotelId` | Yes | Six alphabetic characters |
| `reservations[].arrival`, `departure` | Yes | Present or future stay dates |
| `reservations[].roomRates` | Yes | Includes `startDate`, `endDate`, `pmsRoomType`, and `ratePlanCode` |
| `reservations[].roomRates.ratePrices` | No | Retained only for an authenticated DISTR caller with fixed-rate write permission |
| `reservations[].roomRates.promoKind`, `promotionCode` | No | Only the first room determines basket-level promotion handling; a resolved Opera code is applied to all rooms |
| `reservations[].adultsNumber` | Yes | One or two adults; affects occupancy-supplement basket flags |
| `reservations[].childrenNumber` | No | Zero to three children |
| `reservations[].cotRequired` | No | Triggers Opera item-inventory validation when true |
| `reservations[].distributionIATANumber` | No | Must be exactly eight characters after trimming; written to Opera UDFC16 |
| `reservations[].reservationPackages` | No | Packages sent to Opera; may be expanded from soft-bundle groups |
| `bookingChannel.channel`, `subchannel` | Yes | Drives authorization, account enrichment, booking type, basket metadata, and rules-agent source mapping |
| `bookingChannel.language` | No | Upper-cased for rules-agent channel-info |
| `getReservationsByIds` | No | When true, OHIP fetches full created reservations and validates deposit-policy consistency; occupancy supplement forces it true for more than one room |
| `isOta` | No | When true and occupancy supplement applies, every created basket item is marked as having the supplement |
| `bookingFlowId` | No | Stored in the 30-minute reservation cache entry |

## Branches

| Trigger | Behavior |
| --- | --- |
| `channel=BB` | Requires authentication and an account access level whose maximum reservations covers the requested room count |
| Authenticated BB / PI / CCUI | Adds company/employee, customer, or legacy CCUI email identifiers respectively |
| Anonymous PI | Sets booking type to `ANON`; CCUI uses an empty booking type for negotiated rates and `ANON` otherwise |
| Fixed `ratePrices` without authorized DISTR scope | Clears the supplied prices before forwarding to OHIP |
| First room has `promoKind` and `promotionCode` | Adds the promotion to the basket; `UNIQUE` also calls promo-service and rejects missing, redeemed, or expired codes |
| PI / BB / CCUI with city-tax flag | Content hotel-information lookup may change purpose of stay to `NTLEI` based on booking, effective, and arrival dates |
| Cot requested | Opera item inventory must contain enough cots for all requested rooms |
| Any room uses a configured Whitbread room type | OHIP resolves substitutions through rules-agent and intersects them with Opera hotel inventory |
| `getReservationsByIds=true` | Reads every created reservation back from Opera, rejects multiple distinct deposit policy codes, and computes total cost from `amountDue` (zero when no policies exist) |
| Occupancy-supplement flag + multiple rooms | Forces `getReservationsByIds=true` so adult counts are available per created reservation |
| Package-groups cache hit | Skips the Opera package-groups call while expanding soft bundles |
