# Content Entity Service: getRoomTypeInformation Flow

Returns brand room-type content from AEM, optionally enriching images and descriptions from a hotel's room configuration when `hotelId` is supplied.

```http
GET /v1/content/room-type?country={country}&language={language}&brand={brand}[&hotelId={hotelId}]
Host: content-entity-service
Accept: application/json
```

## Flow

`RoomTypeController` requires `country`, `language`, and `brand`. `brand` is lowercased before the AEM room-types call. content-entity-service loads AEM room-types detail for the brand (via `RoomTypeCache` when caching is enabled).

If `hotelId` is present and non-blank, it also loads AEM hotel complete-data and, for matching room type codes, overrides `roomImage` from tab item `fileReference` and `roomDescription` from `rateGridRoomDescription` when those values are non-blank. Comma-delimited room codes are matched by any overlapping code after stripping spaces.

## Mermaid Flow

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Content as content-entity-service
    participant Redis as Redis
    participant AEM as AEM

    Client->>Content: GET /v1/content/room-type?country&language&brand[&hotelId]
    Content->>Content: validate request and lowercase brand
    alt RoomTypeCache hit
        Content->>Redis: read RoomTypeCache
        Redis-->>Content: room types
    else cache miss or cache disabled
        Content->>AEM: GET /{country}/{language}/content-service.room-types.detail/brand/{brand}.json
        AEM-->>Content: room types
        opt cache enabled
            Content->>Redis: store RoomTypeCache
        end
    end
    opt hotelId present and nonblank
        alt HotelInformationCache hit
            Content->>Redis: read HotelInformationCache
            Redis-->>Content: hotel detail
        else cache miss or cache disabled
            Content->>AEM: GET /{country}/{language}/hoteldirectory/{firstHotelIdChar}/{hotelId}.complete.data
            AEM-->>Content: hotel detail
            opt cache enabled
                Content->>Redis: store HotelInformationCache
            end
        end
        Content->>Content: override matching roomImage and roomDescription from hotel tab items
    end
    Content-->>Client: 200 RoomTypeDto
```

## Features

- Brand-level room type catalog from AEM
- Optional hotel-level image and description overrides via hotel complete-data
- Room type codes converted from comma-delimited strings to lists in the public response
- Optional Redis caches: `RoomTypeCache`, `HotelInformationCache`
- Runtime path is `/v1/content/room-type` (not the OpenAPI-documented `/roomType` spelling)

## Feature Flags

None. This endpoint does not gate behavior on feature flags.

## Request

| Parameter | Required | Notes |
| --- | --- | --- |
| `country` | Yes | Used in AEM paths as supplied |
| `language` | Yes | Used in AEM paths as supplied |
| `brand` | Yes | Lowercased before the room-types AEM path |
| `hotelId` | No | Enables hotel-detail enrichment when present and non-blank |

## Branches

| Trigger | Behavior |
| --- | --- |
| `hotelId` missing or blank | Room-types AEM only |
| `hotelId` present with matching room codes | Overrides image/description from hotel tab items when non-blank |
| `hotelId` present without matching room codes | Keeps brand room-type image and description |
| Missing `country`, `language`, or `brand` | Validation rejects the request before AEM |
| Cache hits | Corresponding AEM calls are skipped |
