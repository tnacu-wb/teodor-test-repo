# Integration Environment

Local Docker Compose environment for the integration services, WireMock dependencies,
Unleash, Jaeger, and the OpenTelemetry Collector. Application framework caches are
disabled. Redis is not part of the stack. Rules Agent keeps its required in-memory rules map.

## Local URLs

Available once the stack is up (`scripts/build.sh` or `scripts/start-compose.sh`).

| Target | URL |
| --- | --- |
| Jaeger UI | <http://localhost:16686> |
| Unleash UI | <http://localhost:4242> |
| OHIP health | <http://localhost:9100/ohip/actuator/health> |
| Content health | <http://localhost:9106/v1/content/actuator/health> |
| Rules Manager health | <http://localhost:9105/rmg/actuator/health> |
| Rules Agent health | <http://localhost:9108/v1/rules/actuator/health> |
| CDH health | <http://localhost:9119/v1/cdh/actuator/health> |
| Company Entity health | <http://localhost:9118/v1/companies/actuator/health> |
| Hotel Entity health | <http://localhost:9102/v1/hotels/actuator/health> |
| PIBA health | <http://localhost:9064/piba-account-service/actuator/health> |
| Spending health | <http://localhost:9132/v1/spending/actuator/health> |
| Basket health | <http://localhost:9104/v1/baskets/actuator/health> |
| Hotel Reservation health | <http://localhost:9103/v1/reservations/actuator/health> |
| Hotel Countries health | <http://localhost:9031/hotel-countries-service/actuator/health> |
| ThreeC Payment health | <http://localhost:9001/threec-payment-service/actuator/health> |
| Hotel Account health | <http://localhost:9020/hotel-account-service/actuator/health> |
| Company Service health | <http://localhost:9022/company-service/actuator/health> |
| Payment Methods health | <http://localhost:9107/v1/payment-methods/actuator/health> |
| Payment Orchestration health | <http://localhost:9200/payment-orchestrator/actuator/health> |
| WireMock Opera | <http://localhost:8443/__admin/mappings> |
| WireMock AEM | <http://localhost:18084/__admin/mappings> |
| WireMock CDH/Auth0 | <http://localhost:8445/__admin/mappings> |
| WireMock Worldline/Datatrans | <http://localhost:8446/__admin/mappings> |

### Basket infrastructure

Basket persistence and event publishing run locally in Compose:

| Dependency | Host endpoint | Provisioned resources |
| --- | --- | --- |
| DynamoDB Local | `http://localhost:8000` | `newBasket`, `PrepaidBookingCharges`, `threec-payment`, and their indexes |
| Kafka | `localhost:9092` | `orders`, `refunds`, `notifications`, `payment-authorised`, and `booking-completed` |

`dynamodb-init` creates the DynamoDB resources before `basket-service` and
`threec-payment-service-opera` start. `kafka-init` creates the Kafka topics before
`basket-service` and `payment-orchestration-service` start. Both jobs are idempotent.
DynamoDB data is retained in the `dynamodb-data` named volume.

### Postgres

The shared `unleash-postgres` instance is published on host port `5438`.

| Setting | Value |
| --- | --- |
| Host / Port | `localhost` (or `127.0.0.1`) / `5438` |
| User / Password | `unleash` / `unleash` |
| Databases | `rulesmanager` (rules-manager migrates and rules-agent reads the `rules_engine` schema), `unleash` |

```text
postgresql://unleash:unleash@localhost:5438/rulesmanager
jdbc:postgresql://localhost:5438/rulesmanager
```

Data is ephemeral — recreating `unleash-postgres` re-runs the init script and Liquibase from
scratch.

### Payment orchestration

`payment-orchestration-service` (port `9200`, health at
`/payment-orchestrator/actuator/health`) coordinates card-payment workflows and uses
Temporal and the shared WireMock Worldline/Datatrans service:

| Dependency | Host endpoint | Notes |
| --- | --- | --- |
| Temporal | gRPC `localhost:7233` | `temporalio/auto-setup` image; creates its own `temporal` and `temporal_visibility` databases on the shared `unleash-postgres`. The service's worker connects at startup, so it waits for Temporal to become healthy. |
| WireMock Worldline/Datatrans | <http://localhost:8446/__admin/mappings> | The shared `wiremock-worldline-datatrans` instance (also used by PIBA/spending for Worldline) mocks the Datatrans payment gateway (`DATATRANS_BASE_URL`). Stub mappings are installed at runtime by the integration-testing framework. |

The service connects to Basket, Payment Methods, Hotel Reservation, Kafka, Temporal, and the
Datatrans endpoint. The OPERA adapter is still a local stub.

Because Temporal is backed by the volume-less `unleash-postgres`, `start-compose.sh` stops
Temporal and `payment-orchestration-service` before force-recreating Postgres. Temporal's
schema is rebuilt cleanly on each startup.

### Payment methods

`payment-methods-entity-service` runs on port `9107`. It connects to Reservation, Content,
Basket, Hotel Entity, Rules Agent, Hotel Account, Company Service, ThreeC Payment, and
Unleash. Its Spring and Redis cache is disabled.

`hotel-account-service-opera` connects to Hotel Countries, ThreeC Payment, CDH Adapter,
PIBA Account, Unleash, WireMock CDH/Auth0, and WireMock Worldline/Datatrans. Its Spring and
Redis cache is disabled. `hotel-countries-service-opera` connects to WireMock AEM.
`threec-payment-service-opera` connects to Basket, DynamoDB Local, Unleash, and WireMock
Worldline/Datatrans.

`company-service-opera` connects to Hotel Account and the existing external-system endpoints.
Its Spring and Redis cache is disabled.

## Layout

- `env/` — per-service environment variable files.
- `scripts/` — helper scripts (seed, build, start).
- `dockerfiles/` — per-service Dockerfiles that package the built jars into `:local` images.
- `otel/` — the OpenTelemetry Java agent, downloaded by the scripts and mounted read-only into
  the service containers (git-ignored).
- `unleash/import/` — the generated Unleash startup import; the snapshot and import files are
  git-ignored.
- `postgres/init/` — Postgres init scripts run on first container start; provisions the
  dedicated `rulesmanager` database on the shared `unleash-postgres` instance.
- `dynamodb/init/` — idempotent local Basket and ThreeC table initialization.

## Scripts

### `scripts/seed-unleash.sh`

Fetches DIT `/client/features` and generates the local Unleash startup import under
`unleash/import/`. The generated snapshot and import files are ignored by git.

```bash
REAL_UNLEASH_TOKEN='...' ./backend/integration-env/scripts/seed-unleash.sh
```

### `scripts/prepare-otel-agent.sh`

Downloads the OpenTelemetry Java agent into `otel/` (skipped if it already exists). It is
invoked automatically by `build-images.sh` and `start-compose.sh`, so you rarely need to run
it directly.

```bash
./backend/integration-env/scripts/prepare-otel-agent.sh
```

### `scripts/build-images.sh`

Prepares a local Unleash import from the DIT feature-flag snapshot, then builds
the local service images:

- `ohip-adapter-service:local`
- `content-entity-service:local`
- `hotel-entity-service:local`
- `rules-manager-entity-service:local`
- `rules-agent-entity-service:local`
- `cdh-adapter-service:local`
- `company-entity-service:local`
- `piba-account-service-opera:local`
- `spending-entity-service:local`
- `basket-service:local`
- `hotel-reservation-entity-service:local`
- `hotel-countries-service-opera:local`
- `threec-payment-service-opera:local`
- `hotel-account-service-opera:local`
- `company-service-opera:local`
- `payment-methods-entity-service:local`
- `payment-orchestration-service:local`

Run it from the monorepo root:

```bash
REAL_UNLEASH_TOKEN='...' ./backend/integration-env/scripts/build-images.sh
```

### `scripts/start-compose.sh`

Validates `docker-compose.yml`, starts the Compose stack without rebuilding service
images, recreates local Unleash/Postgres so the generated import is applied, and waits
for the app services to become healthy.

```bash
./backend/integration-env/scripts/start-compose.sh
```

### `scripts/build.sh`

Runs `scripts/build-images.sh`, then `scripts/start-compose.sh`. This is the full
local setup flow.

```bash
REAL_UNLEASH_TOKEN='...' ./backend/integration-env/scripts/build.sh
```

## Observability

Jaeger UI is available at:

```text
http://localhost:16686
```

The Java services export traces to the OpenTelemetry Collector. The collector filters
out `/actuator` spans before forwarding traces to Jaeger.

The OpenTelemetry Java agent is kept outside the app images for faster local rebuilds.

WireMock is not instrumented. Jaeger shows outbound service calls to WireMock, while
request payloads can be inspected through WireMock admin endpoints.

Unleash runs as a real local server at `http://localhost:4242`. The seed script fetches
DIT `/client/features`, converts it to an Unleash import file, and imports that snapshot
into the local `development` environment. The Java services that use Unleash run with
`UNLEASH_ENVIRONMENT=dit`, so DIT environment constraints in the snapshot are evaluated
with the same SDK context as DIT.
