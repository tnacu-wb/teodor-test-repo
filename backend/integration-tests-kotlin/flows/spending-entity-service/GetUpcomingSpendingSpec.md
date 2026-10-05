# Spending Entity Service: getUpcomingSpending Flow

Returns expected spend for today, the current billing window, and the next billing period for a PIBA account.

```http
GET /v1/spending/upcomingSpending?accountId={pibaAccountId}[&tetheredUserGuid={guid}]
Host: spending-entity-service:9132
Accept: application/json
WB-Authorization: Bearer {jwt}
```

Requires an authenticated JWT (`@PreAuthorize("isAuthenticated()")`).

## Flow

1. Validate the JWT and require company, employee, and email claims.
2. Resolve the client's IP (request remote address, falling back to the configured Worldline default).
3. Prove account ownership and resolve tethered guid + scheme:
   - If `tetheredUserGuid` is present, match it against CDH Registration for the JWT company/employee.
   - If absent, call `piba-account-service-opera` `GET /piba/account/customers` and require `accountNumber == accountId`. PIBA loads tethered guids from CDH Registration and enriches each via Worldline SOAP.
4. Call Worldline REST `GET /PIRestAPI/api/v1/account/info` with scheme credentials and `TetheredUserGuid` to obtain account status, billing frequency, and currency. When Redis cache is enabled, this account-info call is cached for one hour by tethered guid.
5. Derive billing date windows from Worldline `billingFrequency` (`Weekly`, `Monthly`, or fortnightly default).
6. Aggregate spend totals from CDH `POST /IB/V1/Report/transactions` (page size 1) for:
   - spend today
   - next period
   - next billing window when that range differs from today
7. Return status, currency, dates, and the three expected spend totals.

## Mermaid Flow

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Spending as spending-entity-service
    participant Auth0 as Auth0 JWKS
    participant PIBA as piba-account-service-opera
    participant CDHOauth as CDH OAuth
    participant CDH as CDH API
    participant WorldlineSoap as Worldline SOAP
    participant WorldlineRest as Worldline REST

    Client->>Spending: GET /v1/spending/upcomingSpending?accountId={accountId}
    opt JWT verifier cache miss
        Spending->>Auth0: GET /.well-known/jwks.json
        Auth0-->>Spending: JWKS
    end
    Spending->>Spending: validate JWT claims; resolve client IP

    alt tetheredUserGuid present
        opt CDH OAuth token missing or expired
            Spending->>CDHOauth: POST /oauth2/v2.0/token
            CDHOauth-->>Spending: access_token
        end
        Spending->>CDH: GET /IBPay/V1/Registration?companyId={jwtCompanyId}&employeeId={jwtEmployeeId}
        CDH-->>Spending: tethered guids + schemes
        Spending->>Spending: require matching tetheredUserGuid
    else tetheredUserGuid absent
        Spending->>PIBA: GET /piba/account/customers
        opt CDH OAuth token missing or expired
            PIBA->>CDHOauth: POST /oauth2/v2.0/token
            CDHOauth-->>PIBA: access_token
        end
        PIBA->>CDH: GET /IBPay/V1/Registration?companyId={jwtCompanyId}&employeeId={jwtEmployeeId}
        CDH-->>PIBA: tethered guids + schemes
        loop each tethered guid
            PIBA->>WorldlineSoap: POST /b2b.pi.v1.1/b2bpiapi.svc
            WorldlineSoap-->>PIBA: TetheredUserDetails
        end
        PIBA-->>Spending: CustomerAccountsResponse
        Spending->>Spending: require accountNumber == accountId
    end

    alt AccountInfo cache miss
        Spending->>WorldlineRest: GET /PIRestAPI/api/v1/account/info
        WorldlineRest-->>Spending: status, billingFrequency, currency
    else AccountInfo cache hit
        Spending->>Spending: use cached account info
    end
    Spending->>Spending: compute billing date windows from billingFrequency

    opt CDH OAuth token missing or expired
        Spending->>CDHOauth: POST /oauth2/v2.0/token
        CDHOauth-->>Spending: access_token
    end
    par spend today total
        Spending->>CDH: POST /IB/V1/Report/transactions
        CDH-->>Spending: TotalBookingValue
    and next period total
        Spending->>CDH: POST /IB/V1/Report/transactions
        CDH-->>Spending: TotalBookingValue
    and next billing total when range differs from today
        Spending->>CDH: POST /IB/V1/Report/transactions
        CDH-->>Spending: TotalBookingValue
    end
    Spending-->>Client: 200 UpcomingSpendingResponseDto
```

## Features

- Upcoming spend estimates for today, current billing window, and next period.
- Account ownership via optional `tetheredUserGuid` or PIBA customer accounts.
- Worldline account info for status, billing frequency, and currency (scheme-aware credentials).
- Billing calendars:
  - `Weekly`: through next Monday
  - `Monthly`: through the 2nd of the month
  - default fortnightly: through the 2nd or 15th
- Parallel CDH transaction totals (`PageSize=1`) for the derived date ranges.
- Optional Redis cache for Worldline account info keyed by tethered guid (1 hour when cache type is Redis).

## Feature Flags

None. This endpoint does not gate behavior on feature flags.

## Request

| Parameter | In | Required | Effect |
| --- | --- | --- | --- |
| `WB-Authorization` | header | Yes | Bearer JWT for the logged-in user |
| `accountId` | query | Yes | PIBA account number to authorize and report on |
| `tetheredUserGuid` | query | No | Skips PIBA; validates guid via CDH Registration |

CDH transaction body shape (per date window):

```json
{
  "PIBAAccountNo": "{accountId}",
  "fromdate": "dd-MM-yyyy",
  "todate": "dd-MM-yyyy",
  "PageSize": 1,
  "PageNumber": 1
}
```

## Branches

- **Ownership path**: `tetheredUserGuid` present uses CDH Registration only; absent uses PIBA (CDH Registration + Worldline SOAP per guid).
- **AccountInfo cache hit/miss**: cache hit skips Worldline REST `account/info`.
- **Billing frequency**: Weekly / Monthly / fortnightly changes the date windows and whether a separate next-billing CDH call is needed.
- **Unknown account**: no matching tethered guid or account number fails ownership validation.
- **CDH Registration cache**: commons-cdh-lib may cache registration results when Redis cache is enabled.
