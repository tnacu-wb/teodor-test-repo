# OHIP Adapter Service: getHotelPackages Flow

Returns sellable hotel meal packages and restaurant context for a stay by combining Opera packages and hotel dining config.

```http
GET /ohip/hotels/{hotelId}/packages?startDate={arrival}&endDate={departure}&adults=1&children=0&nrNights={nights}
Host: ohip-adapter-service:9100
Accept: application/json
```

The servlet context path is `/ohip/`. Opera calls use the service OAuth client when no valid token is present.

## Flow

The controller maps the public packages request and loads packages and restaurant config in parallel. Restaurant config is cached by hotel id. The packages out-port forces downstream `adults=1` before calling Opera, regardless of the public adults value.

Opera packages are requested with start/end dates, children, include-group, and fetch instructions. When `mealInclusiveRate` is omitted or false, Opera also receives `sellSeparate=true`. Public `nrNights` is validated but not sent on the Opera package-list call.

The response combines restaurant presence flags with meal package entries (title, code, price, currency, inventory article when present).

## Mermaid Flow

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Ohip as ohip-adapter-service
    participant OAuth as Opera OAuth
    participant OperaEnt as Opera Hotel Config API
    participant OperaRtp as Opera Packages API

    Client->>Ohip: GET /ohip/hotels/{hotelId}/packages
    Ohip->>Ohip: map PackagesRequestDto; force downstream adults=1

    par restaurant config
        alt restaurant config cache hit
            Ohip->>Ohip: read OperaRestaurantsConfigCache by hotelId
        else restaurant config cache miss
            opt no valid OAuth token
                Ohip->>OAuth: POST /oauth/v1/tokens
                OAuth-->>Ohip: access_token
            end
            Ohip->>OperaEnt: GET /ent/config/v1/hotels/{hotelId}?hotelId={hotelId}&fetchInstructions=Dining
            Note over Ohip,OperaEnt: header x-hotelid={hotelId}
            OperaEnt-->>Ohip: RestaurantsResponseOhipDto
        end
    and packages list
        opt no valid OAuth token
            Ohip->>OAuth: POST /oauth/v1/tokens
            OAuth-->>Ohip: access_token
        end
        Ohip->>OperaRtp: GET /rtp/v1/packages?hotelId={hotelId}&startDate={arrival}&endDate={departure}&adults=1&children={children}&includeGroup=true&fetchInstructions=Header,CalculatedPrice,Items,PostingRules
        Note over Ohip,OperaRtp: sellSeparate=true unless mealInclusiveRate=true; header x-hotelid={hotelId}
        OperaRtp-->>Ohip: PackagesResponseOhipDto
    end

    Ohip->>Ohip: map restaurant flags, meals, inventory article numbers
    Ohip-->>Client: 200 PackagesResponseDto
```

## Features

- Parallel Opera packages list and dining/restaurant config
- Restaurant config Redis cache by hotel id (`OperaRestaurantsConfigCache`, 1-day manager)
- Downstream packages call always uses `adults=1`
- `mealInclusiveRate=true` omits `sellSeparate`; otherwise Opera receives `sellSeparate=true`
- `includeGroup=true` and package fetch instructions for header, calculated price, items, and posting rules
- Public packages response includes restaurant not-found / no-meals flags and meal package details

## Feature Flags

None. This endpoint does not gate behavior on feature flags in the current packages path.

`FeatureFlag.freeFnbExtras` exists on the model, but it is not configured under `feature-flags` in `application.yml` and is not used by the packages out-port/controller path traced for this endpoint.

Global OAuth may still use `release_ohip_use_token_service` for token acquisition on Opera calls.

## Request

| Parameter | Required | Notes |
| --- | --- | --- |
| `hotelId` | Yes (path) | Opera hotel id |
| `startDate` | Yes | Package search start, `yyyy-MM-dd` |
| `endDate` | Yes | Package search end, `yyyy-MM-dd` |
| `adults` | Yes | Must be positive on the public request; forced to `1` before Opera packages call |
| `children` | Yes | Positive or zero; forwarded to Opera |
| `nrNights` | Yes | Validated publicly; not sent on Opera package-list request |
| `ratePlanCode` | No | Present on the public/domain request; MapStruct packages OHIP DTO field is `ratePlan`, so this path does not currently forward a rate plan code on the normal package-list call |
| `mealInclusiveRate` | No | When true, omits `sellSeparate`; otherwise sends `sellSeparate=true` |

## Branches

| Trigger | Behavior |
| --- | --- |
| Restaurant config cache hit | Skips Opera dining config call |
| `mealInclusiveRate` true | Opera packages call omits `sellSeparate` |
| `mealInclusiveRate` false/omitted | Opera packages call includes `sellSeparate=true` |
| Empty Opera restaurants | Response marks restaurant not found |
