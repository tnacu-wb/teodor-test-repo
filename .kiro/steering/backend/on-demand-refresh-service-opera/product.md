---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/on-demand-refresh-service-opera/**"
---

# Product

`on-demand-refresh-service-opera` is the Discover & Search service that synchronizes Opera hotel availability into the PostgreSQL availability cache. Migration tooling and developers can request targeted refreshes, while Quartz jobs maintain nightly availability and hotel Opera/BART migration status.

## Capability

- Exposes `GET /refresh/hotels` for required hotel IDs and optional date boundaries.
- Filters requests to Opera-enabled hotels before scheduling work.
- Returns `202` for all-Opera input, `206` for mixed Opera/BART input, and `200` when no Opera hotel is eligible.
- Splits long date ranges into delayed Quartz jobs and reschedules failed on-demand work.
- Retrieves rates, inventory, and restrictions from Opera and OCD Adapter data before persisting the cache.
- Uses Content Service global configuration and Unleash feature flags.

Hotel identifiers and operational availability are internal data. Credentials, OAuth tokens, database connection details, and customer data must never be logged, documented with real values, or committed.