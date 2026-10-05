---
inclusion: fileMatch
fileMatchPattern: "backend/identity/libs/commons-cdh-lib/**"
---

# Product Overview

`commons-cdh-lib` is a shared Java library within the Whitbread digital backend platform. It encapsulates Customer Data Hub (CDH) integration concerns — Feign client wiring, Resilience4j circuit breakers, Redis-backed response caching, and JSON (de)serialisation — behind a stable API consumed by Whitbread microservices that need to call CDH.

The library was migrated from the standalone repository [`whitbread-eos/commons-cdh-lib`](https://github.com/whitbread-eos/commons-cdh-lib) into this monorepo at `backend/identity/libs/commons-cdh-lib/`. The upstream repository continues to publish the library to Nexus for external consumers; the monorepo build does **not** publish artifacts (no `<distributionManagement>`).

## Core Responsibilities

- Provide a reusable Spring Cloud OpenFeign client wrapper for the Customer Data Hub APIs
- Resolve tethered user GUIDs against CDH for downstream business operations
- Retrieve company-level and account-level spending data from CDH
- Register tethered users with CDH (Customer Data Hub onboarding)
- Apply Resilience4j circuit breaker fallbacks when CDH is unavailable
- Cache CDH responses in Redis (Spring Data Redis) to reduce upstream call volume
- Read full HTTP response bodies via `feign-httpclient` (required for non-trivial CDH responses)
- Provide Jackson-based JSON (de)serialisation for CDH request/response models

## Intended Consumer Services

The library is consumed in the monorepo by:

- **`piba-account-service-opera`** — depends on `commons-cdh-lib` with no exclusions; uses it for CDH-tethered user registration and tethered GUID resolution as part of PIBA account management flows.
- **`spending-entity-service`** — depends on `commons-cdh-lib` with two exclusions (`uk.co.whitbread.shared:commons-exceptions` and `commons-fileupload:commons-fileupload`) to avoid colliding with the service's own exception handling and file-upload stack; uses it for CDH-based spending data lookups (company spending, tethered GUID resolution).

External (non-monorepo) consumers continue to resolve the library from Nexus against the standalone repository's release cadence.

## Integration Points with Consumers

- **Feign clients** — the library exposes Feign-annotated interfaces for CDH endpoints; consumer services configure base URLs, OAuth credentials, and timeouts via Spring properties and let Spring Cloud OpenFeign auto-wire the clients.
- **Resilience4j fallbacks** — circuit-breaker fallback handlers are bundled in the library; consumers receive degraded-but-safe responses (rather than exceptions) when CDH is degraded or unreachable.
- **Redis caching** — the library contributes Spring Cache annotations and serialisers; consumers provide the Redis cluster connection (Lettuce client) via their own `RedisConfig` and inherit cache regions defined by the library.
- **Shared models** — CDH request/response DTOs are defined in the library so consumers do not duplicate Jackson model classes; mapping to service-internal domain models is the consumer's responsibility (typically via MapStruct in the consumer module).
- **Exception types** — consumers either inherit the library's exception hierarchy (PIBA Account Service) or exclude transitive `commons-exceptions` and translate at the boundary (Spending Entity Service).

## Domain Context

Customer Data Hub (CDH) is Whitbread's centralised customer data platform. Multiple business-line services (PIBA, InnBusiness spending, future onboarding flows) need to issue the same family of CDH requests — tethered user registration, GUID resolution, company spending lookup. `commons-cdh-lib` exists so each consumer does not re-implement this integration, ensuring consistent retry, caching, and circuit-breaker behaviour across the platform.
