---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/on-demand-refresh-service-opera/**"
---

# Structure

The service uses hexagonal boundaries under `uk.co.whitbread.ondemandrefreshservice`:

- `domain/logic` — request validation orchestration, Opera filtering, date-window scheduling, and startup jobs.
- `domain/model` — scheduler and availability domain models.
- `domain/ports/primary` — refresh entry points.
- `domain/ports/secondary` — Opera, Content Service, OCD Adapter, migration-status, and persistence capabilities.
- `infrastructure/rest/controller` — `/refresh/hotels` HTTP endpoint.
- `infrastructure/rest/client` — reactive Content, Opera, and OCD Adapter clients.
- `infrastructure/adapters` — availability orchestration, migration refresh, and database batching.
- `infrastructure/quartz` — on-demand, nightly, and migration-status jobs and schedulers.
- `infrastructure/repository` and `infrastructure/entity` — reader/writer JPA plus JDBC batch persistence.
- `infrastructure/config` — dual data sources, OAuth2 WebClient, Unleash, Jackson, and scheduler configuration.

Keep HTTP and persistence details behind secondary ports. Add Opera calls through the configured OAuth2 `operaWebClient`, retain reader/writer transaction boundaries, and schedule work through `HotelAvailabilityOnDemandJobScheduler` rather than invoking jobs directly.