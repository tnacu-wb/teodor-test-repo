# Company Entity Service: getCompaniesFromCdh Flow

Searches companies by name through CDH, then enriches each result with Opera company profile and negotiated-rate data via ohip-adapter-service.

```http
GET /v1/companies?companyName={companyName}&offset={offset}&limit={limit}[&negotiatedRateCompanies={true|false}]
Host: company-entity-service:9118
Accept: application/json
```

## Flow

company-entity-service validates the query, maps `limit` to CDH `pageSize` and `offset` to CDH `pageNumber`, caps page size at 20, and sets `accessContext=OPERA` and `accessedBy=company-entity-service`.

It posts the search to cdh-adapter-service, which authenticates to CDH OAuth when needed and calls the CDH company search API. Results without a non-empty `globalCompanyId` / `corpId` are dropped.

For each remaining company, company-entity-service calls ohip-adapter-service for the Opera company profile by corporate id, then for negotiated rates by profile/company id. Missing Opera profiles are skipped; a missing negotiated-rates response leaves `negotiatedRateEnabled` unset/false but keeps the company.

When `negotiatedRateCompanies=true`, the response is filtered to companies with negotiated rates. `totalResults` is recalculated after enrichment and filtering.

## Mermaid Flow

```mermaid
sequenceDiagram
    autonumber
    actor Client
    participant Company as company-entity-service
    participant CdhAdapter as cdh-adapter-service
    participant CdhOAuth as CDH OAuth
    participant CdhApi as CDH API
    participant Ohip as ohip-adapter-service
    participant OperaOAuth as Opera OAuth
    participant OperaCrm as Opera CRM
    participant OperaRate as Opera Rate API

    Client->>Company: GET /v1/companies?companyName&offset&limit[&negotiatedRateCompanies]
    Company->>Company: validate request, cap pageSize to 20, set accessContext/accessedBy
    Company->>CdhAdapter: POST /v1/cdh/account/companies

    opt CDH token missing or expired
        CdhAdapter->>CdhOAuth: POST /oauth2/v2.0/token
        CdhOAuth-->>CdhAdapter: access_token
    end

    CdhAdapter->>CdhApi: GET /AccountServices/V2/GetcompaniesV2?CompanyName&PageSize&PageNumber
    CdhApi-->>CdhAdapter: CompanySearch
    CdhAdapter-->>Company: company search results

    Company->>Company: keep only results with non-empty corpId

    loop each valid CDH company
        Company->>Ohip: GET /ohip/v1/profile/company/{corpId}

        opt Opera token missing or expired
            Ohip->>OperaOAuth: POST /oauth/v1/tokens
            OperaOAuth-->>Ohip: access_token
        end

        alt cache hit for corporate id company profile
            Ohip-->>Company: cached CompanyProfileDto
        else cache miss
            Ohip->>OperaCrm: GET /crm/v1/companies/{corpId}?fetchInstructions=ADDRESS&COMMUNICATION&SalesInfo&Keyword&Profile
            OperaCrm-->>Ohip: Company
            Ohip-->>Company: CompanyProfileDto
        end

        opt profile has companyId
            Company->>Ohip: GET /ohip/{companyId}/negotiatedRates
            Ohip->>OperaRate: GET /rtp/v1/profiles/{companyId}/negotiatedRates
            OperaRate-->>Ohip: NegotiatedRates
            Ohip-->>Company: NegotiatedRatesResponseDto
            Company->>Company: set negotiatedRateEnabled from rates
        end
    end

    opt negotiatedRateCompanies=true
        Company->>Company: keep only negotiated-rate enabled companies
    end

    Company-->>Client: 200 CompaniesResponseDto
```

## Features

- Company name search against CDH with paging (`offset` / `limit`)
- Hard page-size cap of 20 for the CDH search
- Filters out CDH rows without a corporate id before Opera enrichment
- Enriches each company with Opera profile fields (name, address, phone, company/profile ids, status)
- Marks `negotiatedRateEnabled` from Opera negotiated rates
- Optional filter to return only companies that have negotiated rates
- Recalculates `totalResults` after enrichment and optional filtering

## Feature Flags

None. This endpoint does not gate behavior on feature flags.

## Request

| Parameter | Location | Required | Notes |
| --- | --- | --- | --- |
| `companyName` | query | Yes | Non-empty company name search term |
| `offset` | query | Yes (`@Min(1)`) | Mapped to CDH `pageNumber`; omitted/zero fails validation |
| `limit` | query | Yes (`@Min(1)`) | Mapped to CDH `pageSize` and capped at 20 |
| `negotiatedRateCompanies` | query | No | Defaults to `false`; when `true`, response keeps only negotiated-rate companies |

Mapped CDH adapter body fields set by company-entity-service:

- `pageSize` = `min(limit, 20)`
- `pageNumber` = `offset`
- `accessContext` = `OPERA`
- `accessedBy` = `company-entity-service`

CDH API call uses `Authorization: Bearer`, `AccessContext`, `AccessedBy`, `Ocp-Apim-Subscription-Key`, and `X-Azure-FDID`. Opera CRM/rate calls use hub credentials via ohip-adapter-service (`x-hubid` on Opera).

## Branches

| Branch | Behavior |
| --- | --- |
| CDH result missing `corpId` | Dropped before OHIP calls |
| OHIP company profile 404 / not found | Company excluded from response |
| Negotiated rates 404 / empty | Company kept with `negotiatedRateEnabled=false` unless filtered out |
| `negotiatedRateCompanies=true` | Response filtered to negotiated-rate companies only |
| ohip-adapter cache enabled | Corporate-id company profile may be served from `OperaCompaniesCache` without calling Opera CRM |
