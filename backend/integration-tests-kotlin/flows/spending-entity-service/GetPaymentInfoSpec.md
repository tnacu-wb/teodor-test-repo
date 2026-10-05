# Spending Entity Service: getPaymentInfo Flow

Returns paginated Worldline payment history for a PIBA account after confirming the caller owns the account.

```http
GET /v1/spending/paymentInfo?accountId={pibaAccountId}&page={page}&size={size}&nonInvoiceOnly={true|false}[&tetheredUserGuid={guid}]
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
4. Call Worldline REST `GET /PIRestAPI/api/v1/account/paymentInfo` with scheme-specific partner headers (`CompanyNumber`, `TrustedPartnerCredentials`, `CultureCode`, `IPAddress`, `TetheredUserGuid`) and query params `page`, `maxDisplayRows` (from `size`), and `nonInvoicedOnly` (from `nonInvoiceOnly`).
5. Map payment rows to the public DTO (including currency symbol and a derived `paymentFailed` flag).

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

    Client->>Spending: GET /v1/spending/paymentInfo
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

    Spending->>WorldlineRest: GET /PIRestAPI/api/v1/account/paymentInfo?page={page}&maxDisplayRows={size}&nonInvoicedOnly={flag}
    Note over Spending,WorldlineRest: headers include TetheredUserGuid and scheme credentials
    WorldlineRest-->>Spending: PaymentInfoResponse
    Spending-->>Client: 200 PaymentInfoResponseDto
```

## Features

- Paginated payment history for a PIBA account (`page`, `size`).
- Optional filter for non-invoiced payments only (`nonInvoiceOnly` -> Worldline `nonInvoicedOnly`).
- Account ownership via optional `tetheredUserGuid` or PIBA customer accounts.
- Scheme-aware Worldline credentials (GB vs DE location properties).
- Response mapping adds currency symbol and `paymentFailed` from `failureReason`.

## Feature Flags

None. This endpoint does not gate behavior on feature flags.

## Request

| Parameter | In | Required | Effect |
| --- | --- | --- | --- |
| `WB-Authorization` | header | Yes | Bearer JWT for the logged-in user |
| `accountId` | query | Yes | PIBA account number to authorize and query |
| `page` | query | Yes | Worldline page number (min 1) |
| `size` | query | Yes | Mapped to Worldline `maxDisplayRows` (min 1) |
| `nonInvoiceOnly` | query | Yes | Mapped to Worldline `nonInvoicedOnly` |
| `tetheredUserGuid` | query | No | Skips PIBA; validates guid via CDH Registration |

## Branches

- **Ownership path**: `tetheredUserGuid` present uses CDH Registration only; absent uses PIBA (CDH Registration + Worldline SOAP per guid).
- **Scheme selection**: scheme from the matched tethered registration chooses Worldline location credentials/culture.
- **Unknown account**: no matching tethered guid or account number fails ownership validation.
- **CDH Registration cache**: commons-cdh-lib may cache registration results when Redis cache is enabled.
