# Whitbread Digital Monorepo

A multi-stack monorepo containing Whitbread's digital services: Java/Spring Boot microservices, TypeScript/Apollo GraphQL subgraphs, and supporting infrastructure. Each stack has its own toolchain; path-based CI builds only what changed.

## Directory Structure

```
digital-monorepo/
├── .github/workflows/               # CI (change detection + matrix builds)
│   ├── ci.yaml                      # CI orchestrator (change detection + matrix)
│   ├── build-service.yaml           # Reusable per-service build workflow
│   ├── build-library.yaml           # Reusable per-library build workflow
│   └── build-graphql.yaml           # Reusable GraphQL stack build workflow
├── backend/
│   ├── pom.xml                      # Aggregator POM (module list only)
│   ├── mvnw / mvnw.cmd / .mvn/     # Maven Wrapper
│   ├── parents/
│   │   ├── service-parent/          # Parent for SB4 services
│   │   └── library-parent/          # Parent for shared libraries
│   ├── discover-search/
│   │   └── services/
│   │       ├── availability-business-events-service-opera/
│   │       ├── availability-cache-service-opera/
│   │       ├── content-entity-service/
│   │       ├── content-service-opera/
│   │       ├── hotel-entity-service/
│   │       ├── hotel-info-service-opera/
│   │       ├── hotel-review-service/
│   │       ├── ohip-adapter-service/
│   │       ├── ocd-adapter-service/
│   │       ├── on-demand-refresh-service-opera/
│   │       ├── opera-token-service/
│   │       ├── rules-agent-entity-service/  ⚠️ Spring Boot 3 (Java 17)
│   │       └── rules-manager-entity-service/
│   ├── book-pay/
│   │   ├── libs/
│   │   │   ├── coding-convention-rules/
│   │   │   ├── commons-entity-exceptions/
│   │   │   ├── commons-exceptions/
│   │   │   ├── commons-logging/
│   │   │   └── qas-address-lookup-lib/
│   │   └── services/
│   │       ├── address-lookup-entity-service/
│   │       ├── basket-async-order-processor/
│   │       ├── basket-confirmation-processor/
│   │       ├── basket-service/
│   │       ├── hotel-payment-service-opera/
│   │       ├── payment-methods-entity-service/
│   │       ├── payment-orchestration-service/
│   │       ├── refund-request-processor/
│   │       ├── table-reservation-service/
│   │       └── threec-payment-service-opera/
│   ├── identity/
│   │   ├── libs/
│   │   │   ├── bart-shared-lib/
│   │   │   ├── common-auth0/
│   │   │   ├── commons-auth0/
│   │   │   ├── commons-azure-email-service/
│   │   │   ├── commons-cdh-lib/
│   │   │   ├── commons-entity-validators/
│   │   │   ├── commons-validators/
│   │   │   └── worldline-ba-api-lib/
│   │   └── services/
│   │       ├── account-entity-service/
│   │       ├── bulk-employee-upload-service-opera/
│   │       ├── business-tether-service-opera/
│   │       ├── cdh-adapter-service/
│   │       ├── company-employee-service-opera/
│   │       ├── company-entity-service/
│   │       ├── company-reporting-service/
│   │       ├── company-service-opera/
│   │       ├── feedback-service-opera/
│   │       ├── hotel-card-service-opera/
│   │       ├── hotel-account-service-opera/
│   │       ├── hotel-countries-service-opera/
│   │       ├── hotel-login-service-opera/
│   │       ├── hotel-register-service-opera/
│   │       ├── marketing-service-opera/
│   │       ├── pay-app-entity-service/
│   │       ├── piba-account-service-opera/
│   │       ├── piba-registration-service-opera/
│   │       └── spending-entity-service/
│   ├── arrive-stay-leave/
│   │   └── services/
│   │       ├── hotel-wallet-service-opera/
│   │       └── kiosk-checkin-service/
│   └── manage-modify/
│       └── services/
│           ├── hotel-dashboard-service-opera/
│           ├── hotel-reservation-entity-service/
│           └── reservations-manager-entity-service/
├── .kiro/                           # Steering and spec files
├── graphql/                         # TypeScript / Apollo GraphQL subgraph services
│   ├── src/
│   │   └── apollo/subgraphs/       # 38 federated subgraphs
│   ├── package.json
│   ├── tsconfig.json
│   └── Dockerfile
└── .gitignore
```

## Squads and Modules

### Discover & Search

| Module | Type | Description |
|--------|------|-------------|
| `availability-business-events-service-opera` | Service | Event-driven service subscribing to Opera OHIP Business Events via GraphQL WebSocket, processing availability changes into a PostgreSQL cache |
| `content-entity-service` | Service | Content entity management for search and discovery |
| `content-service-opera` | Service | Content aggregation layer fetching room types, rate classifications, cookie policies, and booking notifications from Adobe Experience Manager with Redis caching |
| `hotel-entity-service` | Service | Hotel entity management and availability |
| `hotel-info-service-opera` | Service | Hotel information microservice providing hotel details via Feign clients with Redis caching |
| `ohip-adapter-service` | Service | OHIP (Oracle Hospitality Integration Platform) adapter |
| `availability-cache-service-opera` | Service | Caches hotel room availability from Opera PMS with PostgreSQL and Redis layers |
| `rules-manager-entity-service` | Service | Manages business rules for availability and pricing with PostgreSQL/Liquibase persistence |
| `rules-agent-entity-service` | Service | ⚠️ **Spring Boot 3** — Reads business rules from PostgreSQL into memory and serves them via REST for rule evaluation (amendment, rates, channels, RBAC, VAT, etc.) |
| `hotel-review-service` | Service | Hotel review aggregation with Redis caching and WebClient downstream calls |
| `ocd-adapter-service` | Service | OCD (Opera Cloud Distribution) adapter translating external channel manager API into internal hotel availability and rate models |
| `on-demand-refresh-service-opera` | Service | Schedules targeted and nightly Opera availability refreshes into the PostgreSQL cache, with Opera/BART hotel filtering and migration-status synchronization |
| `opera-token-service` | Service | Centralised OAuth2 token broker for Opera/OHIP API authentication, providing pre-refreshed client-credentials tokens to consuming services |

### Book & Pay

| Module | Type | Description |
|--------|------|-------------|
| `coding-convention-rules` | Library | ArchUnit coding convention rules shared across services |
| `commons-entity-exceptions` | Library | Common entity exception classes for REST error handling |
| `commons-exceptions` | Library | Whitbread commons exceptions library for standardized error responses |
| `commons-logging` | Library | Whitbread commons logging library with structured JSON logging and tracing |
| `qas-address-lookup-lib` | Library | QAS Pro On Demand SOAP client for address lookup via WSDL |
| `basket-async-order-processor` | Service | Async order processing for the basket/cart feature, handling multi-item purchases and Opera reservation coordination |
| `basket-confirmation-processor` | Service | Kafka event processor that consumes basket acknowledgement events and confirms item processing status via the Basket Service REST API |
| `hotel-payment-service-opera` | Service | Hotel payment microservice handling payment processing with Feign clients and circuit breakers |
| `payment-methods-entity-service` | Service | Determines available payment methods (card, PayPal, POA, PIBA) for a booking by aggregating hotel info, rules engine, customer accounts, and feature flags |
| `payment-orchestration-service` | Service | Payment orchestration for the new card payment journey (Datatrans Secure Fields + Mobile SDK V4) |
| `address-lookup-entity-service` | Service | UK address lookup via QAS (Quick Address Search) using qas-address-lookup-lib |
| `refund-request-processor` | Service | Kafka consumer that processes refund requests from the Basket Service, executing card refunds via the 3C Payments API and publishing acknowledgement events |
| `table-reservation-service` | Service | Restaurant table booking for Beefeater, Brewers Fayre, Cookhouse & Pub brands, integrating with LiveRes (Zonal) API and AEM content |
| `threec-payment-service-opera` | Service | Reactive payment processing service managing the full lifecycle of card transactions (initialise, authorise, tokenise, refund, reconcile) through the 3C Web2Pay gateway, plus PayPal via Braintree |
| `basket-service` | Service | Core booking basket orchestration managing the lifecycle from creation through payment to confirmation, coordinating OHIP, payments, CDH, and Kafka events |

### Identity

| Module | Type | Description |
|--------|------|-------------|
| `common-auth0` | Library | Common security library enabling OAuth2 resource server and JWT validation |
| `commons-auth0` | Library | Auth0 integration library for token management, JWT validation, and user operations |
| `commons-azure-email-service` | Library | SDK for Azure email service integration via SOAP/WSDL |
| `commons-cdh-lib` | Library | Shared CDH (Customer Data Hub) client library used by account and spending services |
| `commons-entity-validators` | Library | Common entity validators with OWASP encoding and observability |
| `commons-validators` | Library | Custom Jakarta Bean Validation annotations (password, postcode, date format, conditional null checks) |
| `bart-shared-lib` | Library | Legacy BART system SOAP/WSDL shared library with CXF-generated Java stubs from 25+ WSDLs |
| `worldline-ba-api-lib` | Library | Worldline Business Account SOAP API client library |
| `account-entity-service` | Service | Proxy service exposing account registration and marketing preference endpoints, bridging the experience layer with hotel-register and marketing services |
| `bulk-employee-upload-service-opera` | Service | Bulk employee upload via CSV/Excel with CDH integration and Azure email notifications |
| `business-tether-service-opera` | Service | Handles employee tethering operations for PI Business Booker, linking loyalty cards to Worldline via SOAP and managing tethered login sessions |
| `cdh-adapter-service` | Service | Adapter for the Customer Data Hub (CDH), handling company/employee account management, reservation search, and report generation via Azure API Management |
| `company-employee-service-opera` | Service | Manages corporate company employees with CDH integration, email service, and Feign-based microservice communication |
| `company-service-opera` | Service | Manages corporate company accounts and employees in the PI Business Booker ecosystem, providing CRUD for profiles, booking allowances/alerts, and management questions |
| `hotel-account-service-opera` | Service | Orchestrates leisure and business customer profiles, booking history, password recovery, Universal Login emails, and InnBusiness account information across Auth0, CDH, Azure Email, PIBA, and Worldline |
| `hotel-card-service-opera` | Service | Manages payment card CRUD for company and customer accounts, integrating with Worldline, 3C Payment, and CDH across GB and DE markets |
| `hotel-login-service-opera` | Service | Hotel login microservice handling user authentication with Auth0 and session management |
| `hotel-register-service-opera` | Service | Customer registration service supporting web, apps, InnBusiness, and Auth0 Universal Login channels with CDH account creation and email confirmation |
| `pay-app-entity-service` | Service | Pay app entity microservice managing mobile payment wallet features with Kafka integration and WebFlux |
| `piba-account-service-opera` | Service | PIBA account service exposing customer balance and transaction endpoints with Worldline integration |
| `piba-registration-service-opera` | Service | PIBA user registration microservice with Worldline BA API and CDH integration |
| `spending-entity-service` | Service | Spending entity management for InnBusiness spending and reporting |
| `company-entity-service` | Service | Company entity management with OHIP and CDH adapter integrations |
| `company-reporting-service` | Service | Business booker reporting with DynamoDB persistence and AWS S3 file storage |
| `feedback-service-opera` | Service | Guest feedback submissions via Microsoft Dynamics CRM |
| `hotel-countries-service-opera` | Service | Hotel country reference data with Redis caching and Feign clients |
| `marketing-service-opera` | Service | Newsletter subscription management using BART SOAP services with Auth0 security |

### Arrive Stay Leave

| Module | Type | Description |
|--------|------|-------------|
| `hotel-wallet-service-opera` | Service | Hotel digital wallet service generating Apple Wallet passes for bookings, integrating with AWS S3, OAuth2, and Pact contract testing |
| `kiosk-checkin-service` | Service | Orchestrates kiosk room allocation and authenticated guest check-in across OHIP Adapter and Reservation Service |

### Manage & Modify

| Module | Type | Description |
|--------|------|-------------|
| `hotel-dashboard-service-opera` | Service | Home screen dashboard content aggregation for Premier Inn apps using WebFlux and OpenAPI codegen |
| `hotel-reservation-entity-service` | Service | Central reservation management service orchestrating create, amend, and cancel operations across OHIP, Basket, Content, and Rules services |
| `reservations-manager-entity-service` | Service | Hotel reservation management (view, amend, cancel, online check-in, invoice generation) orchestrating OHIP, Basket, CDH Adapter, and Content services |

## Prerequisites

- **JDK 25** — required by Spring Boot 4.0.7 parent (backend stack, default)
- **JDK 17** — required for Spring Boot 3 services (e.g., `rules-agent-entity-service`)
- **Maven** — provided via the Maven Wrapper (`./mvnw` in `backend/`), no local installation needed
- **Node.js 22** — required for the GraphQL stack (`graphql/`)
- **npm 8+** — ships with Node.js 22; used for GraphQL dependency management

## Build Instructions

All builds use the Maven Wrapper in the `backend/` directory.

### Full build (all modules)

```bash
cd backend && ./mvnw clean install
```

### Single service build

```bash
cd backend && ./mvnw clean install -pl <squad>/services/<service-name> -am
```

For example:

```bash
cd backend && ./mvnw clean install -pl book-pay/services/basket-async-order-processor -am
```

The `-am` flag (also-make) ensures any dependent modules are built first.

### Single library build

```bash
cd backend && ./mvnw clean install -pl <squad>/libs/<library-name> -am
```

### Skip tests

Append `-DskipTests` to any build command:

```bash
cd backend && ./mvnw clean install -DskipTests
```

### GraphQL stack

The GraphQL stack is a TypeScript/Apollo Server application providing a federated subgraph
layer between the Apollo Router and backend REST APIs.

```bash
cd graphql && npm install
npm run build
```

#### Run tests (local development only — not run in CI)

```bash
cd graphql && npm test
```

#### Lint and format check

```bash
cd graphql && npm run lint
cd graphql && npm run format:check
```

## Versioning Strategy

This monorepo uses **lock-step versioning**. All modules inherit their version from their respective parent POM (`1.0.0`) via the `${revision}` property. Individual services do not declare their own `<version>` element.

This means:
- A single version bump in the root POM updates all services
- Released artifacts are named consistently (e.g. `basket-async-order-processor-1.0.0.jar`)
- There is no risk of version drift between services
- Override at build time with `-Drevision=1.1.0`

## CI Pipeline

The CI pipeline runs via GitHub Actions and is self-contained within this repository.

### How it works

1. **Change detection** — The orchestrator workflow (`ci.yaml`) determines which modules have changed by inspecting modified file paths under `backend/` and `graphql/`. If `backend/pom.xml` changes, all backend modules are rebuilt. If any file under `graphql/` changes, the GraphQL build is triggered.
2. **Dynamic path resolution** — Build workflows resolve service/library paths dynamically using glob patterns (`backend/*/services/$NAME`), so adding a new squad or moving a service requires zero CI workflow changes.
3. **Parallel matrix builds** — Changed modules are built in parallel using a matrix strategy (`fail-fast: false`), so one failure does not cancel other builds.
4. **Reverse dependency detection** — When a library changes, dependent services across all squads are automatically identified and rebuilt.
5. **Per-service build steps** — Each service build (`build-service.yaml`) runs:
   - `mvn verify -pl <squad>/services/<service-name> -am` (from `backend/` working directory)
   - SonarQube analysis (project key: `whitbread-eos_digital-monorepo_<service-name>`)
   - JAR artifact upload
6. **GraphQL build** — The GraphQL workflow (`build-graphql.yaml`) installs the Rover CLI and runs a schema compatibility check (only when subgraph schema files at `src/apollo/subgraphs/**/schema/schema.graphql` changed in the diff), then runs `npm install`, `npm run build`, and uploads a tarball artifact. Tests are not run in CI.

### Triggers

- Push to `main`, `release/*`, or `hotfix/*` branches
- Pull requests targeting `main`

## Adding a New Service

1. Create directory under `backend/<squad>/services/`
2. Add `<module><squad>/services/<service-name></module>` entry in `backend/pom.xml`
3. Service POM inherits from the service parent: `<relativePath>../../../parents/service-parent/pom.xml</relativePath>`
4. Create steering files in `.kiro/steering/backend/<service-name>/` with `fileMatchPattern: "backend/<squad>/services/<service-name>/**"`
5. CI automatically picks up new services via path-based change detection

## Adding a New Library

1. Create directory under `backend/<squad>/libs/`
2. Add `<module><squad>/libs/<library-name></module>` entry in `backend/pom.xml`
3. Library POM inherits from the library parent: `<relativePath>../../../parents/library-parent/pom.xml</relativePath>`
4. Create steering files in `.kiro/steering/backend/<library-name>/` with `fileMatchPattern: "backend/<squad>/libs/<library-name>/**"`
5. CI automatically picks up new libraries via path-based change detection
 