---
inclusion: fileMatch
fileMatchPattern: "graphql/**"
---

# Opera Apollo Subgraphs — Structure

## Source Directory Layout

```
graphql/
├── src/
│   ├── index.ts                                # Express + Apollo Server bootstrap
│   ├── tests/                                  # Jest test suites
│   │   └── <subgraph-name>.test.ts
│   └── apollo/
│       ├── client/                             # Axios HTTP client factory and helpers
│       ├── config/                             # Apollo Server configuration (plugins, context)
│       ├── exception/                          # GraphQL error formatting and mapping
│       ├── log/                                # Pino logger initialisation
│       ├── middleware/                         # Express middleware (auth headers, correlation ID)
│       ├── pipeline/                           # Request pipeline utilities (batching, composition)
│       ├── subgraphs/                          # Federated subgraphs (38 total)
│       │   └── <subgraph-name>/
│       │       ├── resolvers.ts                # Query/Mutation resolvers
│       │       ├── dataSources.ts              # Axios-based REST client for backend APIs
│       │       └── schema/
│       │           └── schema.graphql          # Subgraph SDL (federation directives)
│       ├── telemetry/                          # OpenTelemetry SDK setup (traces + metrics)
│       └── utils/                              # Shared utilities (date formatting, string helpers)
├── package.json                                # Dependencies and npm scripts
├── package-lock.json                           # Lockfile
├── tsconfig.json                               # TypeScript compiler config
├── Dockerfile                                  # Production container image
├── .prettierrc                                 # Prettier config
├── .prettierignore                             # Prettier ignore patterns
├── .dockerignore                               # Docker build context exclusions
├── .gitignore                                  # Git ignore patterns
├── .env.local                                  # Local dev environment variables (not committed secrets)
└── README.md                                   # Source repo README
```

## Subgraph Folder Convention

Each subgraph follows the same three-file pattern:

| File | Role |
|------|------|
| `resolvers.ts` | Defines Query and Mutation resolvers; delegates data fetching to `dataSources` |
| `dataSources.ts` | Axios-based client wrapping one or more backend REST endpoints |
| `schema/schema.graphql` | Subgraph schema using Apollo Federation directives (`@key`, `@external`, `@requires`) |

## Subgraph List (38)

account-entity-service, address-lookup-entity-service, availability-cache-service-opera,
basket-service, booking-confirmation-pipeline, booking-information-pipeline,
business-tether-service-opera, company-employee-service-opera, company-entity-service,
company-reporting-service, company-service-opera, content-entity-service,
digital-key-service, donations-pipeline, feedback-service-opera,
hotel-account-service-opera, hotel-availabilities-pipeline, hotel-card-service-opera,
hotel-dashboard-service-opera, hotel-entity-service, hotel-info-service-opera,
hotel-login-service-opera, hotel-payment-service-opera, hotel-register-service-opera,
hotel-reservation-entity-service, hotel-review-service, hotel-wallet-service-opera,
marketing-service-opera, pay-app-entity-service, piba-account-service-opera,
piba-registration-service-opera, reservations-manager-entity-service,
rules-manager-entity-service, spending-entity-service, table-reservation-service,
and additional pipeline/utility subgraphs.
