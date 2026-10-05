# OHIP Adapter Service: getChangeLog Flow

Returns the Opera activity/change log entries for one reservation.

```http
GET /ohip/v1/hotels/{hotelId}/reservations/changeLog?reservationId={reservationId}
Host: ohip-adapter-service:9100
Accept: application/json
```

The servlet context path is `/ohip/` and the change log controller is mapped under `/v1`, so the public path is `/ohip/v1/hotels/{hotelId}/reservations/changeLog`.

## Flow

ohip-adapter-service passes the hotel id, reservation id, and the optional `limit`/`offset` paging values straight through its change log in-port and out-port to a single Opera Reservations API call for the reservation's activity log. The Opera request carries the hotel id in the path and the `x-hotelid` header, and identifies the reservation via `parameterName=RESV_NAME_ID`/`parameterValue={reservationId}` query parameters.

If Opera returns no activity log (HTTP 204), the out-port raises an internal error instead of a 404. Any other Opera 4xx or 5xx status is likewise translated into an internal error. On success, the Opera `ActivityLog` payload is mapped to the service's change log response model and returned as-is.

## Mermaid Flow

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Ohip as ohip-adapter-service
    participant TokenService as opera-token-service
    participant OAuth as Opera OAuth
    participant OperaRsv as Opera Reservations API

    Client->>Ohip: GET /ohip/v1/hotels/{hotelId}/reservations/changeLog?reservationId={reservationId}
    opt no valid OAuth token
        alt release_ohip_use_token_service enabled
            Ohip->>TokenService: GET /v1/tokens/opera/access-token
            TokenService-->>Ohip: Opera access token
        else direct Opera OAuth
            Ohip->>OAuth: POST configured Opera auth endpoint
            OAuth-->>Ohip: access token
        end
    end
    Ohip->>OperaRsv: GET /rsv/v1/hotels/{hotelId}/reservations/activityLog?parameterName=RESV_NAME_ID&parameterValue={reservationId}&limit={limit}&offset={offset}
    Note over Ohip,OperaRsv: header x-hotelid={hotelId}
    alt Opera returns 204 No Content
        OperaRsv-->>Ohip: 204 empty body
        Ohip-->>Client: 500 DIGITAL_NO_ACTIVITY_LOG
    else Opera returns a 4xx error
        OperaRsv-->>Ohip: 4xx error response
        Ohip-->>Client: 500 OHIP_GET_LOG_ACTIVITY_OPERA_EXCEPTION
    else Opera returns a 5xx error
        OperaRsv-->>Ohip: 5xx error response
        Ohip-->>Client: 500 OHIP_GET_LOG_ACTIVITY_EXCEPTION
    else Opera returns the activity log
        OperaRsv-->>Ohip: ActivityLog
        Ohip->>Ohip: map ActivityLog to ChangeLogResponse
        Ohip-->>Client: 200 ChangeLogResponseDto
    end
```

## Features

- Single-reservation change log lookup by hotel id and Opera reservation id
- Optional `limit`/`offset` paging passed straight through to Opera
- One Opera Reservations API request with the required hotel header
- Reuses cached OAuth credentials while they remain valid
- Maps the Opera `ActivityLog` payload directly to the response model with no additional enrichment

## Feature Flags

| Flag | Effect when enabled |
| --- | --- |
| `release_ohip_use_token_service` | Acquires the Opera access token from opera-token-service instead of the direct Opera OAuth flow |
| `release_ohip_use_token_refresh_skew` | Refreshes direct Opera OAuth tokens using the configured clock-skew window before expiry |

## Request

| Parameter | Location | Required | Purpose |
| --- | --- | --- | --- |
| `hotelId` | path | Yes | Supplies the Opera hotel path value and `x-hotelid` header |
| `reservationId` | query | Yes | Supplies the Opera `parameterValue` for the `RESV_NAME_ID` lookup |
| `limit` | query | No | Passed through as the Opera `limit` query parameter |
| `offset` | query | No | Passed through as the Opera `offset` query parameter |

Example:

```http
GET /ohip/v1/hotels/HEAPTI/reservations/changeLog?reservationId=6004202&limit=20&offset=0
Accept: application/json
```

## Branches

| Trigger | Behavior |
| --- | --- |
| Valid OAuth token is cached | Skips token acquisition |
| `release_ohip_use_token_service` enabled and no valid token | Gets the token from opera-token-service |
| `release_ohip_use_token_service` disabled and no valid token | Gets the token directly from the configured Opera OAuth endpoint |
| Opera returns 204 for the activity log | Returns `DIGITAL_NO_ACTIVITY_LOG` |
| Opera returns a 4xx status | Returns `OHIP_GET_LOG_ACTIVITY_OPERA_EXCEPTION` |
| Opera returns a 5xx status | Returns `OHIP_GET_LOG_ACTIVITY_EXCEPTION` |
