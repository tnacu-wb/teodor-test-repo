# OHIP Adapter Service: getReservationsByExternalId Flow

Finds Opera reservations by one external reference and returns reservation details enriched with booker billing and reservation amounts.

```http
GET /ohip/v1/reservations/external?externalReferenceId={externalReferenceId}
Host: ohip-adapter-service:9100
Accept: application/json
```

The servlet context path is `/ohip/` and the reservation controller is mapped under `/v1`, so the public path is `/ohip/v1/reservations/external`. Opera calls use the service OAuth client and include the configured Opera application key.

## Flow

ohip-adapter-service optionally converts the supplied external reference to an Opera wildcard search value, then searches the Opera Reservation API with `fetchInstructions=Reservation`. A reference whose fourth character is `R` has `-%` appended before the search; other references are passed unchanged.

When Opera returns no `reservationInfo`, the endpoint responds with 404. Otherwise, OHIP maps the returned reservations and derives the hotel id from the first reservation. It collects attached `ReservationContact` profile ids and loads each corresponding profile from the Opera Profile API. It also collects Opera reservation ids and loads the summary rate information for each reservation. These results provide billing details, amount paid, outstanding balance, total cost, and currency in the enhanced response.

Every Opera request passes through the OAuth client. With the token-service flag enabled, a missing or expired cached token is loaded from token-service. Otherwise, OHIP obtains a token directly from Opera using the configured client-credentials or password flow.

## Mermaid Flow

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Ohip as ohip-adapter-service
    participant Token as token-service
    participant OAuth as Opera OAuth
    participant OperaRsv as Opera Reservation API
    participant OperaCrm as Opera Profile API

    Client->>Ohip: GET /ohip/v1/reservations/external?externalReferenceId={externalReferenceId}
    opt fourth character of externalReferenceId is R
        Ohip->>Ohip: append -% to the Opera search value
    end

    opt no valid cached OAuth token
        alt release_ohip_use_token_service enabled
            Ohip->>Token: GET /v1/tokens/opera/access-token
            Token-->>Ohip: Opera access token
        else direct Opera OAuth
            Ohip->>OAuth: POST /oauth/v1/tokens
            OAuth-->>Ohip: access_token
        end
    end

    Ohip->>OperaRsv: GET /rsv/v1/reservations?externalReferenceIds={searchValue}&fetchInstructions=Reservation
    Note over Ohip,OperaRsv: header x-hubid={hubId}
    OperaRsv-->>Ohip: ReservationsDetails

    alt reservationInfo is null
        Ohip-->>Client: 404 Not Found
    else reservations found
        Ohip->>Ohip: map reservations and derive hotelId
        opt ReservationContact profile ids are present
            loop each distinct profileId
                Ohip->>OperaCrm: GET /crm/v1/profiles/{profileId}?fetchInstructions=Profile,Address,Communication,Correspondence,FutureReservation,HistoryReservation
                Note over Ohip,OperaCrm: header x-hubid={hubId}
                OperaCrm-->>Ohip: Profile
            end
        end
        loop each distinct Opera reservationId
            Ohip->>OperaRsv: GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo?idContext={context}&id={reservationId}&summaryInfo=true&type={type}
            Note over Ohip,OperaRsv: header x-hotelid={hotelId}
            OperaRsv-->>Ohip: reservation amount breakdown
        end
        Ohip->>Ohip: map billing, amounts, total cost, and currency
        Ohip-->>Client: 200 ReservationDetailsEnhancedDto
    end
```

## Features

- Single external-reference search across Opera reservations
- Automatic `-%` wildcard suffix for references whose fourth character is `R`
- Reservation-contact billing enrichment from Opera profiles
- Summary amount enrichment for every returned Opera reservation id
- Enhanced response containing reservation details, billing, paid and outstanding amounts, total cost, and currency
- 404 response when Opera returns no `reservationInfo`

## Feature Flags

| Flag | Effect when enabled |
| --- | --- |
| `release_ohip_use_token_service` | Acquires Opera access tokens from token-service instead of authenticating directly with Opera |
| `release_ohip_use_token_refresh_skew` | In direct Opera OAuth mode, refreshes cached tokens by the configured clock-skew interval before expiry |

## Request

| Query parameter | Required | Purpose |
| --- | --- | --- |
| `externalReferenceId` | Yes | External booking reference used to find matching Opera reservations |

Example:

```http
GET /ohip/v1/reservations/external?externalReferenceId=BART7421
Accept: application/json
```

## Branches

| Trigger | Behavior |
| --- | --- |
| Fourth character of `externalReferenceId` is `R` | Appends `-%` to the value sent to Opera |
| Opera `reservationInfo` is null | Returns 404 without profile or amount enrichment |
| `ReservationContact` profile ids are present | Loads each distinct profile from Opera CRM for billing enrichment |
| Opera reservation ids are present | Loads summary rate information for each distinct id |
| Opera search, profile, or amount request fails | Returns an internal service error using the corresponding OHIP error code |
