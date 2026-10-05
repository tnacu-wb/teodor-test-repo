# Test Framework Guide

This module is a Kotlin/Kotest integration test framework for service journeys. It
runs tests against an externally managed integration environment and installs dynamic
WireMock stubs through the WireMock Admin API.

The short version:

```text
scenario data
  -> installFor(booking)
  -> client call with baggage: wb-test-id=<testId>
  -> attach evidence, then assert
  -> JourneySpec cleanup
```

The main rule for authors is: keep scenario facts explicit and let the testkit handle mock choreography.

## Project Layout

```text
integration-tests-kotlin/
  build.gradle.kts
  flows/             # Public endpoint flow documents; independent of journey coverage
  docs/
    page-tests/      # Cross-service page coverage documents
  src/main/kotlin/uk/co/whitbread/integrationtests/
    testkit/        # Reusable models, Booking installation, feature flags, scenario lifecycle
    stubs/          # Self-contained stub builders and default upstream collectors
    framework/      # WireMock, endpoints, preflight, HTTP, and evidence internals
    provisioning/   # Local Ktor mock-provisioning API
  src/main/resources/
    keys/           # Checked-in test-only RSA signing key used by AuthTokens
    wiremock/       # Reusable provisioning templates
  src/test/kotlin/uk/co/whitbread/integrationtests/
    architecture/   # Docker-free package and layer architecture rules
    framework/      # Header, WireMock, preflight, HTTP, and evidence contracts
    provisioning/   # Ktor route, OpenAPI, and config tests
    support/        # Shared hermetic test fixtures and the WireMock Admin fake
    testkit/        # Docker-free provisioning, cleanup, and lifecycle tests
  src/integrationTest/kotlin/uk/co/whitbread/integrationtests/
    journeys/       # Environment-backed Kotest specs (service keys below)
    testkit/        # JourneySpec and preset fixtures
    clients/        # Typed clients for services under test (same service keys)
    framework/      # Kotest runtime config and project preflight extension
  src/integrationTest/resources/
    kotest.properties
```

Service package keys are all-lowercase and shared across `clients.<key>` and
`journeys.<key>` where both exist:

```text
companyentity, contententity, hotelreservation, ohip, payment, spendingentity
basket  # clients only (deployed setup dependency with no journeys of its own)
pages   # journeys only (cross-service page flows)
```

Flow documents describe public endpoint behavior and are maintained independently
of Kotlin journey coverage. Their presence does not imply that a matching typed
client or journey exists. Journey coverage is represented by `journeys.<key>`.

Author-facing helpers such as `hotelPagePath` live in `testkit.model`, not under
`stubs`.

`test` runs every hermetic framework and architecture test (including
Konsist/path-based layer rules). `integrationTest` runs only environment-backed
journeys and is deliberately never treated as up to date. Normal `build` and `check`
run the Docker-free suite and compile all integration sources without contacting the
environment.

Use `journeys`, `testkit`, `stubs`, and `clients` in tests. Journey specs must not import
`framework`. Use `excluded` to drop Booking defaults. Call `installStub(stub)` to install
a self-contained stub outside those defaults, including any override for an excluded
default.

## Runtime Wiring

`IntegrationConfig` resolves one base-URL snapshot for services and WireMock Admin
ports. The environment-variable names and host-local defaults are:

```text
OHIP_INTERFACE_BASE_URL          = http://localhost:9100
HOTEL_RESERVATION_BASE_URL       = http://localhost:9103
RATE_MANAGEMENT_GATEWAY_BASE_URL = http://localhost:9105
CONTENT_BASE_URL                 = http://localhost:9106
SPENDING_ENTITY_BASE_URL         = http://localhost:9132
COMPANY_ENTITY_BASE_URL          = http://localhost:9118
CDH_ADAPTER_BASE_URL             = http://localhost:9119
PIBA_ACCOUNT_BASE_URL            = http://localhost:9064
PAYMENT_ORCHESTRATION_BASE_URL   = http://localhost:9200
BASKET_BASE_URL                  = http://localhost:9104

WIREMOCK_OPERA_URL               = http://localhost:8443
WIREMOCK_CDH_URL                 = http://localhost:8445
WIREMOCK_AEM_URL                 = http://localhost:18084
WIREMOCK_WORLDLINE_URL           = http://localhost:8446
```

Every value can be overridden independently through the environment variable shown
above. Host-run Gradle tests and the host-local provisioning server need no overrides.
A future containerized test process can use Compose service origins such as
`http://ohip-adapter-service:9100` and `http://wiremock-opera:8080`.

Endpoint values are loaded once on first suite use and validated before preflight as
HTTP(S) origins: a host is required, an optional port must be valid, one trailing slash
is normalized, and user info, paths, queries, and fragments are rejected. Invalid
settings surface during Kotest's before-project phase with an `IllegalArgumentException`
cause naming the setting; endpoint loading is deferred so the cause is not replaced by
`ExceptionInInitializerError`. The same resolved snapshot drives typed-client defaults,
readiness checks, and four WireMock Admin adapters sharing one HTTP transport.

`IntegrationTestConfig` is both Kotest's project config and the suite's intentional
composition root. It wires:

- spec and test execution each use Kotest 6 limited concurrency of four.
- one lazily resolved `IntegrationConfig` endpoint snapshot.
- shared `WireMockInstances`, which owns one transport used by all four Admin adapters.
- shared Ktor `httpClient`.
- `EnvironmentPreflightExtension` before project test execution.
- project shutdown that closes every initialized shared HTTP client.

Typed clients normally take their HTTP client and service base URL from this composition
root, while retaining constructor parameters for tests that need isolated dependencies.
No separate runtime-container abstraction is used.

Environment startup is external to this framework. The preflight polls ten
application actuator endpoints—OHIP adapter, Hotel Reservation entity, Content entity, Rules Manager, CDH
adapter, Company entity, PIBA account, Spending entity, Payment Orchestration, and Basket—plus all four WireMock
Admin endpoints concurrently. Each configured service base URL contributes one
actuator check, and each WireMock URL contributes one `/__admin/mappings` check.
Application readiness requires HTTP 2xx with a JSON `status` of `UP`; WireMock readiness
requires HTTP 2xx with a valid `mappings` array containing every startup mapping the stack
bakes into that container — `auth.jwks` and `cdh.oauth-token` on CDH, `opera.oauth-token` on
Opera. AEM and Worldline mount no mappings directory, so any array satisfies them. Without
that name check an unmounted or empty mappings directory is silent: Docker creates a missing
bind-mount source as an empty directory, and WireMock then serves nothing while answering both
the Compose healthcheck and `/__admin/mappings` successfully, so the first symptom would be
every authenticated journey failing at once with a 401. Integration tests execute only after
every dependency is ready in the same polling round. If readiness times out, the project
fails once with an aggregated list of unavailable dependencies and no integration test
executes.

Preflight defaults to a 120-second overall timeout, a two-second polling interval, and a
three-second per-request timeout. Environment variables are the normal controls for a
Gradle invocation. `PreflightSettings` checks test-worker JVM properties first, but Gradle
does not forward `./gradlew -D...` properties to its forked test worker automatically. If
needed, pass a JVM property to both processes with `JAVA_TOOL_OPTIONS`; it then takes
precedence over the corresponding environment variable.

| Setting | Test-worker JVM property | Environment variable |
| --- | --- | --- |
| Overall timeout in seconds | `integration.preflight.timeoutSeconds` | `INTEGRATION_PREFLIGHT_TIMEOUT_SECONDS` |
| Poll interval in milliseconds | `integration.preflight.pollIntervalMillis` | `INTEGRATION_PREFLIGHT_POLL_INTERVAL_MILLIS` |
| Request timeout in milliseconds | `integration.preflight.requestTimeoutMillis` | `INTEGRATION_PREFLIGHT_REQUEST_TIMEOUT_MILLIS` |

For example:

```bash
INTEGRATION_PREFLIGHT_TIMEOUT_SECONDS=180 ./gradlew integrationTest
JAVA_TOOL_OPTIONS='-Dintegration.preflight.timeoutSeconds=180' ./gradlew integrationTest
```

All three values must be positive. The poll interval must be shorter than the overall
timeout, and the per-request timeout must not exceed the overall timeout.

The local Docker Compose stack is disposable and externally owned. Stack creation
provides the initial WireMock state, and stack destruction removes abandoned state
after a crashed test process. The Kotlin framework never resets mappings or request
journals globally. Per-scenario cleanup is always strict: it attempts every reachable
WireMock operation, then throws one aggregated `CleanupException` if anything remains
incomplete. If a dependency disappears after preflight, the journey fails with the
structured cleanup details alongside any underlying test failure.

## Local Mock-Provisioning Server

With the disposable integration stack running, start the local API with:

```bash
./gradlew run
```

It binds to `127.0.0.1:9190` by default. Override the bind address with
`MOCK_PROVISIONING_HOST` and the port with `MOCK_PROVISIONING_PORT`. The server
loads the same `WIREMOCK_OPERA_URL`, `WIREMOCK_CDH_URL`, `WIREMOCK_AEM_URL`, and
`WIREMOCK_WORLDLINE_URL` settings used by Kotlin provisioning. The server process owns
one HTTP transport shared by its four WireMock Admin adapters, and `main` closes it on
the way out — whether the server returns normally or never bound in the first place.

| Method and path | Purpose |
| --- | --- |
| `GET /health` | Local process health check; it does not probe WireMock. |
| `POST /mock-sessions` | Strictly decode a `Booking`, generate a UUID test ID, and install its mocks. |
| `DELETE /mock-sessions/{testId}` | Idempotently remove the session's scoped mappings and request events. |
| `GET /openapi.json` | OpenAPI generated from the Ktor routes and serializers. |
| `GET /swagger` | Interactive Swagger UI. |

A successful POST returns `201` with `testId` and the complete `baggage` value the
frontend must propagate. The mocks install or they do not: **every** failure — an
undecodable body, a `Booking` whose stubs cannot be built, a rejected WireMock Admin call —
returns `503` with the same fixed `{"message": ...}` body. DELETE returns `204`, or
`503` if any cleanup operation was incomplete.

The response deliberately does not classify the failure. Nothing automated branches on
it, and the server logs the exception with its stack trace, so that log is where you
look — not the response body. Nothing cleans up after a failed POST either: every
mapping is scoped by `wb-test-id` and the Compose stack is disposable, so orphaned
state costs nothing. Delete the test ID if you want to be tidy. The `Booking` **request**
schema at `/swagger` is generated from the model itself and is the reference for what
the endpoint accepts. Running the provisioning server inside Compose is not currently
supported; run it as a host-local process with `./gradlew run`.

## Base Spec Types

There is one base class for test authors: `JourneySpec`. Use it for everything, from a single-action test to a multi-step business journey. Each `scenario(...)` is one test; a spec can have one scenario for a single action or several scenarios for a longer journey.

`JourneySpec` exposes `installFor`, `installStub`, and `callCount` directly in each scenario.
Declare `Booking` data inside the scenario that uses it, then call `installFor(...)`.
The framework does not expose a generic provisioning-model interface.

Simplified `JourneySpec` example:

```kotlin
class GetLightweightReservationsByIdsSpec : JourneySpec(
    "HEAPTI lightweight reservations can be fetched by id",
    {
        val ohipApi = OhipApi()

        scenario("GET /ohip/v1/reservations/ids returns one lightweight reservation") {
            val booking = Booking(
                hotels = listOf(
                    Hotels.HEAPTI.copy(
                        availableRates = listOf(
                            Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2),
                        ),
                    ),
                ),
                arrival = LocalDate.now().plusDays(14),
                departure = LocalDate.now().plusDays(16),
                rooms = listOf(
                    BookingRoom(
                        reservationId = "6001001",
                        roomType = "LOWDBL",
                        adults = 2,
                        status = ReservationStatus.RESERVED,
                    ),
                ),
            )
            val room = booking.room

            installFor(booking)

            val result = ohipApi.getLightweightReservationsByIds(
                reservationId = requireNotNull(room.reservationId),
                hotelId = booking.hotel.hotelId,
                testId = testId,
            )

            result.attachEvidence("Get Reservations By Ids")

            expect("returns the requested reservation") {
                result.response.status.value shouldBe 200
                result.body.reservationByIdList.first().reservationId shouldBe room.reservationId
            }
        }
    },
)
```

`JourneySpec` creates a fresh scenario scope for each `scenario(...)`. That scope derives a `testId` from the feature description, scenario name, and a random six-character suffix, and exposes:

- `testId`: used in service calls and WireMock matchers.
- `installFor`, `installStub`, and `callCount`: scenario-owned mock operations.
- `expect(name) { ... }`: named assertion blocks for readable test code.
- `result.attachEvidence(prefix?)`: adds a bounded request/response exchange to the scenario artifact.

`JourneySpec` removes the scenario-owned WireMock state after each scenario and writes the cleanup
result into the same artifact. This makes the scenario, not the spec class, the mock and evidence
isolation unit. A scenario can install as often as it needs. Cleanup is not part of the author
interface, so a journey cannot purge the request journal before it calls `callCount`.

A second install does not replace the first. Both sets of mappings stay registered, and WireMock
serves the most recently added match, so a focused install must come *after* whatever it means to
override — normally next to the call it serves rather than grouped with the setup at the top of the
scenario. Cleanup is unaffected: it reclaims everything owned by the test ID regardless of how many
installs produced it.

## Scenario Data

`Booking` is the current primary scenario model. It is scenario data with lightweight validation:

```kotlin
data class Booking(
    val hotels: List<Hotel> = emptyList(),
    val companies: List<Company> = emptyList(),
    val arrival: LocalDate? = null,
    val departure: LocalDate? = null,
    val rooms: List<BookingRoom> = emptyList(),
    val aem: Aem? = null,
    val loggedUser: LoggedUser? = null,
    val bookingReference: String? = null,
)

data class BookingRoom(
    val reservationId: String? = null,
    val roomType: String? = null,
    val ratePlan: String? = null,
    val roomTypeAfterUpdate: String? = null,
    val adults: Int? = null,
    val children: Int = 0,
    val status: ReservationStatus = ReservationStatus.ON_HOLD,
    val guestProfile: GuestProfile? = null,
    val selectedPackages: List<SelectedPackage> = emptyList(),
    val depositPolicyCode: String = "DEP",
    val depositPolicyCodeAfterUpdate: String? = null,
    val routingInstructions: List<RoutingInstruction> = emptyList(),
    val amountAlreadyPaid: Double = 0.0,
)
```

Keep data models focused on scenario facts. Lightweight validation is fine, but they should not know how to call services or install mocks.

`Booking` and its complete model graph are also the direct JSON contract for the
POC mock-provisioning API. Use the shared `bookingJson` format: it rejects unknown
properties, preserves Kotlin defaults for omitted values, and encodes `LocalDate`
as ISO-8601 `YYYY-MM-DD` strings. The POC has no parallel transport DTO, mapping
layer, endpoint version, request-size limit, or collection-size limit. OpenAPI is
generated from the implemented Ktor route and serializers at `/openapi.json`.

The same `Booking` should drive:

- WireMock stubs through `installFor(booking)`.
- service requests through typed clients such as `OhipApi`.
- assertions through expected values such as `booking.hotels` or `booking.hotel.hotelId`.

That keeps tests coherent. If a test changes dates, rate plan, room type, packages, guest profile, reservation ID, or AEM content, the stubs and request should move with that data.

`booking.hotel` is a convenience accessor for the first hotel in `booking.hotels`; use it for single-hotel flows such as reservation creation. It fails only when a flow actually uses the accessor without providing hotel data.

`booking.room` is a convenience accessor for single-room flows. It fails unless `booking.rooms` contains exactly one room. Leave `rooms` empty for scenarios that do not need Opera reservation mocks. When a room represents an Opera reservation, specify `reservationId`; the framework does not invent a default reservation ID.

For reservation creation, the explicit `reservationId` is the deterministic ID that mocked
Opera will allocate, not a field sent by the client. Each requestable room receives its own
create response. When multiple create requests have identical matchable data, the mappings use
a `testId`-scoped WireMock Scenario to return the room IDs in `Booking.rooms` order. This relies
on OHIP's checked-in reservation-create `maxConcurrency: 1`; concurrent identical creates need
a thread-safe allocation mechanism instead. See
[Create-reservation WireMock state](CREATE_RESERVATION_WIREMOCK_STATE.md).

## Authenticated Service Calls

For services protected by `common-auth0`, use one `LoggedUser` as both the JWT
claims source and the authenticated scenario fixture:

```kotlin
val loggedUser = LoggedUser(
    accessLevel = "SUPER",
    companyAccountId = "company-account-1",
    companyId = "company-1",
    employeeId = "employee-1",
    email = "integration.user@example.test",
)
val booking = Booking(loggedUser = loggedUser)

installFor(booking)

val result = SpendingApi().getCompanySpending(
    fromMonthYear = "2026-01",
    toMonthYear = "2026-02",
    testId = testId,
    wbAuthorization = AuthTokens.bearer(loggedUser),
)
```

Setting `Booking.loggedUser` is essential for the downstream fixtures, but note that no test
installs the JWKS: the stack serves it. The typed client's `wbAuthorization` parameter sends the
complete `Bearer ...` value as `WB-Authorization`. Add the endpoint-specific CDH or
Worldline fixtures to the same `LoggedUser` when the successful response needs them.

`AuthTokens` signs with a fixed, test-only RSA key checked in at
`src/main/resources/keys/integration-test-signing-key.json`, and the matching public key is a
startup mapping owned by the Compose stack
(`backend/integration-env/wiremock/cdh-auth0/mappings/auth-jwks.json`). It has to be, rather than
something a scenario installs: the services' JWT decoders do not propagate the scenario baggage
header, so that mapping can carry no ownership matcher and no cleanup could ever reclaim it. The
key is fixed so the two can agree; `BakedAuthMappingsTest` fails if they drift. Keep the token's
user
and `Booking.loggedUser` aligned as well so JWT claims, downstream fixtures, and
assertions describe one coherent scenario; this user alignment is a data-consistency
rule, not a cryptographic requirement. Omit `wbAuthorization` only when the
scenario deliberately exercises missing authentication or authorization. For a
client that uses standard `Authorization` instead, pass the same value returned by
`AuthTokens.bearer(...)` through that client's header API.

## Feature Flag Overrides

A scenario must state its complete feature-flag state explicitly instead of depending on the
environment's deployed default. Put the pins in one named map and pass that map as
`featureFlagOverrides` on every service-under-test call in the scenario:

```kotlin
val result = ohipApi.getHotelAvailability(
    request = availabilityRequest(booking, channel = "BB"),
    testId = testId,
    featureFlagOverrides = mapOf(OhipFeatureFlag.BB_FLEX_RATE_STRIKETHROUGH to false),
)
```

Which flags to pin: every flag the endpoint's flow doc lists, including flags permanently fixed
to one state. When an OHIP flow lists `release_ohip_use_token_service`, include
`OhipFeatureFlag.USE_TOKEN_SERVICE to false`; this is an explicit invariant, not an ON/OFF test
axis. Treat any other flow-listed Opera token-service flag the same way. When an endpoint has
other request-scoped flags, cover every reachable ON/OFF permutation and put the complete
combination in each scenario's map. A fixed-state flag remains in every map at its fixed value
and gets no impossible opposite-state scenario. If a path-changing state is neither fixed nor
reachable through baggage override, report it as a blocker rather than relying on the deployed
default. When the verified flow lists no flags, there is nothing to pin and the call needs no
`featureFlagOverrides`.

Typed-client methods gain a `featureFlagOverrides` parameter when their first flow-listed flag
needs pinning; add it alongside the scenario rather than speculatively.

An override can only control a flag the service evaluates inside the scenario's request: it
travels in the request's `baggage` header and is resolved per request. Infrastructure flags
evaluated outside that context — such as OHIP's OAuth token-acquisition flags — cannot be
changed this way. When the verified flow lists one, still include its `false` invariant in the
OHIP flag map while never building an ON-state scenario for it; the environment pin remains the
enforcement point.

The override travels as a second W3C baggage member alongside the test ID, so one header
carries both scenario isolation and scenario flag state:

```text
baggage: wb-test-id=<testId>,wb-feature-overrides=release_bb_flex_rate_strikethrough:off
```

Flags live in `testkit.featureflags` as one enum per owning service, all implementing the
sealed `FeatureFlag` interface:

```kotlin
sealed interface FeatureFlag { val key: String }

enum class OhipFeatureFlag(override val key: String) : FeatureFlag {
    BB_FLEX_RATE_STRIKETHROUGH("release_bb_flex_rate_strikethrough"),
}
```

`key` is the Unleash flag name exactly as the service resolves it. Add a new constant to the
enum of the service that owns the flag, and add a new enum when a service gets its first one.
Because every enum shares the `FeatureFlag` supertype, one call can override flags belonging
to more than one service. Name the enum after the service that owns the flag, not the client
that sends it: a journey calling one service may need to override a flag owned by a service
further downstream.

## Mock Installation

Mock installation uses self-contained stubs, one default-stub table, and one installer over four
WireMock Admin adapters.

Important types:

- `JourneySpec.ScenarioScope`: the journey author interface for installation and call counting.
- `MockInstaller`: internal implementation for default collection, registration, evidence, and scoped removal.
- `WireMockTarget`: one enum shared by registration, cleanup, and target diagnostics.
  Each constant carries its display name, environment variable, default URL, and required startup
  mappings, so endpoint resolution and readiness derive from it rather than repeating the list.
- `PlannedStub`: immutable ID, target, and ordered mappings produced by each public stub builder.
- `defaultStubsFor(booking)` (`stubs/DefaultStubs.kt`): the entry point that owns every
  Booking gate. It sums one private function per upstream (`cdhStubs`, ...,
  `operaStubs`) in the same file; together they are the complete Booking-to-stubs table.

The `JourneySpec` scenario methods delegate to the internal installer with the scenario test ID.
The Ktor routes use one installer directly.
The installer calls `selectedStubs`, which calls `defaultStubsFor` and applies exclusions.
It then installs each selected stub through the same scoped operation as direct installation.

Each stub file owns its ID constant and target. Exclusions use the constants. Direct
installs use the same `PlannedStub` values as default collection.

Default collection finishes before the first WireMock Admin request, but it is not strictly pure:
mapping construction can read a classpath template, initialize process-scoped auth key
material, or resolve time-derived response fields.

This complete provisioning closure is compiled from `src/main`: Booking and upstream
models, the default-stub table, low-level stubs, WireMock administration, and strict cleanup are
reused directly by the local REST server. JWT and JWKS generation also lives in `src/main`
(`testkit.auth.AuthTokens`) but is *not* part of that closure — no provisioning source
references it, because the stack serves the JWKS and the REST server never mints a token. Evidence contracts,
the scenario sink, and rendering also live in `src/main`. `JourneySpec` injects a
`ScenarioEvidenceSink` through `StubInstallEvidenceSink`; callers such as the local REST
server omit the optional sink.

For the Opera gates below, a *requestable room* has Booking `arrival` and `departure`
plus non-null room `roomType` and `adults`. A *reservation room* is requestable and also
has a non-null `reservationId`. A Booking has *reservation data* when it has at least one
requestable room and every one of them is a reservation room; it has *availability data*
when it has exactly one hotel, at least one requestable room, and none of them carries a
`reservationId`. Those two conditions are mutually exclusive, and several Opera gates below
are phrased in terms of them.

```text
booking.cdh.company-search             # only when booking.companies is not empty
booking.cdh.company-spend              # only when loggedUser.companySpend is not null
booking.cdh.employee-spend             # only when loggedUser.employeeSpend is not null
booking.cdh.registration-details       # only when loggedUser.tetheredAccount is not null
booking.cdh.account-spend              # only when loggedUser.tetheredAccount.accountSpend is not null
booking.cdh.transaction-details        # only when loggedUser.tetheredAccount.accountActivity.transactionAggregates is not empty

booking.worldline.tethered-user-details # only when loggedUser.tetheredAccount is not null
booking.worldline.account-info          # only when loggedUser.tetheredAccount.accountActivity.worldlineAccount is not null
booking.worldline.payment-info          # only when loggedUser.tetheredAccount.accountActivity.paymentHistory is not null

booking.aem.footer                      # only when aem.footer is not null
booking.aem.index-header-data           # only when aem.indexHeaderData is not null
booking.aem.search-results-data         # only when aem.searchResultsData is not null
booking.aem.global-config               # only when aem.globalConfig is not null
booking.aem.room-type                   # only when aem.roomType is not null
booking.aem.cookie-policies             # only when aem.cookiePolicies is not null
booking.aem.hotel-directory             # only when booking.hotels is not empty
booking.aem.hotel-detail                # for each entry in booking.hotels, only when non-empty

booking.opera.hotel-config             # for each entry in booking.hotels, only when non-empty
booking.opera.cancellation-reasons     # for each hotel whose cancellationReasons is not null
booking.opera.room-types               # for each entry in booking.hotels, returning hotel.availableRoomTypes
booking.opera.hotel-inventory          # only when the Booking has availability data
booking.opera.availability             # only when the Booking has availability data
booking.opera.item-inventory           # only when the Booking has availability data and hotel.itemInventory is not null
booking.opera.rate-info                # one per available rate, when first hotel has availableRates and the Booking has reservation data or availability data
booking.opera.rate-plans               # only when first hotel has availableRates
booking.opera.rate-plan-info           # one per available rate carrying a promotionCode
booking.opera.create-reservation       # one create response per requestable room, when all have unique reservationId
booking.opera.hotel-restaurants        # when first hotel has packageCatalogue and a requestable room exists
booking.opera.package-group            # only when first hotel has packageCatalogue packages, or any requestable room has selectedPackages
booking.opera.packages-list            # one per requestable room when first hotel has packageCatalogue
booking.opera.donation-packages        # only when first hotel has packageCatalogue donationPackages
booking.opera.get-reservation          # one per reservation room; a second state follows PUT when depositPolicyCodeAfterUpdate is set
#                                        carries booking.bookingReference as the reservation's external reference when set
#                                        depositPolicies.amountPaid follows BookingRoom.amountAlreadyPaid; paymentCard when operaPaymentCard is set
#                                        ratePlan disambiguates hotel.availableRates entries sharing room type and occupancy
booking.opera.put-reservation          # one per reservation room; advances that room's GET state when depositPolicyCodeAfterUpdate is set
#                                        a room with selectedPackagesAfterUpdate swaps its permissive PUT for package-pinned
#                                        mappings adding/removing exactly the packages that differ from selectedPackages
booking.opera.cancel-reservation       # one per reservation room when at least one hotel exists
booking.opera.delete-cancellation-policy # one per reservation room; pins the first policyId when the room carries cancellationPolicies
booking.opera.create-cancellation-policy # one per reservation room when at least one hotel exists
booking.opera.reservation-amounts      # one per reservation room when at least one hotel exists
booking.opera.city-tax-rate-info       # one per reservation room whose selectedPackages include CITYTAX
booking.opera.credit-card-info         # one per reservation room whose operaPaymentCard is not null
booking.opera.reservation-folios       # one per reservation room when at least one hotel exists
booking.opera.deposit-folios           # one per reservation room when at least one hotel exists
booking.opera.reservation-deposits     # one read per room; empty unless depositPaymentReference supplies a posted deposit
booking.opera.reservations-by-external-reference # when bookingReference is present; an empty room list returns no matches
booking.opera.get-profile              # one per room whose guestProfile is not null
booking.opera.create-profile           # one per room whose guestProfile is not null
booking.opera.update-profile           # one per room whose guestProfile is not null
booking.opera.company-profile          # one per company in booking.companies; also answers the raw companyId read
#                                        with an empty profile (skipped when companyId == corpId) so corporate-id-first
#                                        consumers can exercise their profile-by-company-id fallback
booking.opera.company-profile-search   # one profile-summary search stub when booking.companies is not empty
booking.opera.company-negotiated-rates # one per company in booking.companies
```

The availability gates carry one extra collection rule: when a Booking has availability data,
every rate on the hotel must declare either a `promotionCode` or a `ratePlanSet`. Collection
fails naming the offending rate plans rather than installing a mapping Opera would never match.

### Default Install

Most tests should do this:

```kotlin
scenario("fetching a reservation by id") {
    val booking =
        Booking(
            hotels = listOf(Hotels.HEAPTI),
            arrival = LocalDate.now().plusDays(14),
            departure = LocalDate.now().plusDays(16),
            rooms =
                listOf(
                    BookingRoom(
                        reservationId = "6001001",
                        roomType = "LOWDBL",
                        adults = 2,
                    ),
                ),
        )

    installFor(booking)

    val result = OhipApi().getLightweightReservationsByIds(
        reservationId = requireNotNull(booking.room.reservationId),
        hotelId = booking.hotel.hotelId,
        testId = testId,
    )
    result.attachEvidence()
}
```

This:

1. builds every default mapping enabled by the `Booking` fields before any WireMock Admin request;
2. applies the plain `excluded` input;
3. stamps test-ID ownership on each selected stub;
4. registers each resulting `StubMapping` through WireMock Admin;
5. records each successfully installed mapping in the scenario-owned evidence artifact.

### Plan Changes

Exclusions and direct installs are exceptions, not an alternative authoring style. A normal
world state belongs in reusable Booking facts and generic default stubs so both Kotlin
journeys and REST provisioning can use it. Use an exclusion plus direct install only for
behavior the generic default cannot represent, normally a downstream failure or a temporal
mid-journey transition, and document that reason at the journey installation site. Do not use
one only to assert a mapper-local request body; cover that in a service, mapper, or contract test.

To override a default, pass the default's ID in `excluded` and install the override with
`installStub`. Every custom/error stub declares its own unique stub ID (never the default's),
so evidence and cleanup identify it unambiguously.
Journey specs use constants and builders from `stubs.*`.
They must not import `framework.*`.

```kotlin
// Exceptional: the deposit-read failure cannot coexist with the normal deposits default.
installFor(booking, excluded = setOf(OPERA_RESERVATION_DEPOSITS_STUB_ID))
installStub(reservationDepositsOperaFailure(booking, booking.room))

// Exceptional: this override must take effect after an earlier call in the journey.
installStub(allHotels(hotels))
```

`excluded` removes a complete logical stub group. Selection input is the test developer's
responsibility: an unknown excluded ID throws, naming the IDs this Booking selected.

`installStub(stub)` adds a self-contained stub at any point in the journey: right after
`installFor` when it overrides an excluded default, or later when the change belongs to a
later moment in the journey.

The builder supplies the evidence ID and WireMock target. `Upstream` is not part of direct
installation. It remains the author-facing value for `callCount`.

For justified exceptional mappings outside a model graph, put the builder under
`stubs/<upstream>/custom`. Make it return `PlannedStub`. Never stamp ownership yourself:
`scopedTo` adds the `wb-test-id` baggage matcher to every mapping. See
[SCENARIO_OWNERSHIP.md](SCENARIO_OWNERSHIP.md).

Example of an error-path install:

```kotlin
installStub(getReservationBadRequest(hotelId = "HEAPTI", reservationId = "asd123"))
```

Do not throw while building an override stub to simulate a downstream error. That fails the
test during mock setup before the service call is made. Instead, plan a stub that returns
the downstream error response and then assert how the service handles it.

## Verifying Downstream Calls

Installing a mock does not mean the endpoint under test used it. `Booking` describes the world
rather than one endpoint, so a scenario normally installs more mappings than the call it is
exercising needs. The request journal is therefore the only evidence of what the service
actually did, and `JourneySpec.ScenarioScope` exposes it as one count per mocked upstream:

```kotlin
callCount(Upstream.AEM) shouldBe 0
```

It counts every request carrying this scenario's `wb-test-id` baggage to that WireMock,
regardless of method, path, or body. That breadth is deliberate: a regressed service often sends
a *malformed* request that no installed stub matches, and a matcher-derived query would report
zero for exactly the call the assertion exists to catch.

`callCount` is a read. It does not require an install to have happened, and it does not disturb
one. Read it before cleanup: cleanup deletes the journal events it reports on, so a count taken
afterwards is zero for everything. `JourneySpec` owns cleanup, so a journey cannot trigger it early.

A scenario proving rejection before any downstream call should install the upstream normally and
then assert zero calls:

```kotlin
val booking = Booking(aem = Aem(cookiePolicies = cookiePoliciesAem))

installFor(booking)

val result = contentEntityApi.getCookiePolicies(country = null, /* ... */ testId = testId)
result.attachEvidence("Get Cookie Policies Missing Country")

expect("returns a validation error without calling AEM") {
    result.response.status.value shouldBe 422
    callCount(Upstream.AEM) shouldBe 0
}
```

Installing the working stub is what makes the assertion meaningful: the happy path was available
and the service still did not reach for it. Omitting the stub instead proves nothing, because the
service may have called the upstream and mapped the resulting `404` to the same response.

Know what the count does and does not prove:

- **It sees only requests carrying the scenario's baggage.** That is what keeps it correct under
  concurrency, but it also means a downstream client that does not propagate the header is
  invisible. Some clients in this estate already behave that way, which is why the OAuth and JWKS
  mappings are served by the stack rather than installed per scenario. Treat a zero as "no
  scenario-owned request reached this WireMock", not as an absolute proof of no call.
- **It names a WireMock instance, not an upstream system.** A WireMock serving both an upstream's
  data stubs and its OAuth plumbing counts both, so a token fetch can show up in an
  `Upstream.CDH` or `Upstream.OPERA` count.
- **It is read at one instant, with no synchronisation against the service.** A downstream call
  the service makes *after* returning its response — an async audit or cache warm — may not be
  recorded yet.
- **Per-mock counting and request-shape assertions do not exist.** Counting a single logical mock
  needs a matcher-derived query, which is ambiguous when one mock installs overlapping matchers. Add it against a real scenario that
  needs it, rather than speculatively.

## Stub Builders

Stubs live under `stubs/<upstream>`. Direct custom stubs live under
`stubs/<upstream>/custom`. Journey specs can import their public builders and ID constants.

Examples:

- `stubs/opera/OperaHotelConfigStubs.kt`
- `stubs/opera/OperaRateInfoStubs.kt`
- `stubs/opera/OperaReservationStubs.kt`
- `stubs/opera/custom/OperaReservationErrorStubs.kt`
- `stubs/aem/*`
- `stubs/cdh/*`
- `stubs/worldline/*`

Public stub builders return `PlannedStub`. Each stub file declares its ID constant and target.
One logical stub can contain several ordered mappings. Private raw mapping helpers can support
variants in the same file.

Good pattern:

```kotlin
const val OPERA_HOTEL_CONFIG_STUB_ID = "booking.opera.hotel-config"

fun hotelConfig(hotel: Hotel): PlannedStub =
    PlannedStub(
        id = OPERA_HOTEL_CONFIG_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = listOf(hotelConfigMapping(hotel)),
    )

private fun hotelConfigMapping(hotel: Hotel): StubMapping = ...
```

Do not take a `testId` parameter. Do not add an ownership header. The installer stamps every
mapping.

`createReservation(booking)` creates its own UUID-based WireMock Scenario name for multi-room
builds. It needs no caller-supplied chain key.

The authentication plumbing that could not carry ownership — the JWKS and the two OAuth token
endpoints — is served by the stack's WireMock startup mappings instead of being installed here,
so there is no unscoped case to permit. See `backend/integration-env/wiremock/README.md` and
[SCENARIO_OWNERSHIP.md](SCENARIO_OWNERSHIP.md).

## Test Isolation And Cleanup

Tests run concurrently, so isolation is not based on resetting WireMock before every test. Data-bearing stubs match the current test id as a W3C baggage member:

```text
service request header: baggage: wb-test-id=<testId>
downstream WireMock stub header matcher: BAGGAGE_HEADER to testIdHeaderMatcher(testId)
```

Typed clients must pass the `testId` header on service-under-test calls. `CompanyEntityApi`,
`ContentEntityApi`, `HotelReservationApi`, `OhipApi`, `PaymentOrchestrationApi`, and
`SpendingApi` all do by issuing every request through `ServiceApiClient`, whose request
verbs require `testId` and send it as the baggage header. Two architecture rules keep this
closed: one fails the build for a public client method that does not take `testId`, and one
fails the build for a client file that imports raw Ktor request APIs instead of using
`ServiceApiClient`.

Cleanup happens after each scenario. `JourneySpec` calls `MockInstaller.removeFor(testId)`.
The installer lists stubs on every configured WireMock, removes only mappings whose `baggage`
matcher contains the scenario's `wb-test-id`, then removes request-journal events matching the
same exact baggage member.

Cleanup is deterministic in every environment; there is no CI/local policy switch.
`MockInstaller.removeFor` returns a `CleanupResult` after complete cleanup and throws one
`CleanupException` containing every mapping-list, mapping-delete, and request-journal removal
failure after incomplete cleanup. It runs in a `NonCancellable` context so a cooperative timeout
or cancellation cannot interrupt its suspending WireMock calls; the original cancellation
continues propagating afterward. A failed `installFor` is not rolled back: removal matches the
`wb-test-id` baggage rather than a record of what was installed, so the partial attempt's mappings
are reclaimed by normal scenario cleanup. A scenario that otherwise passed fails on incomplete
cleanup. If the scenario already failed or was cancelled, its original exception remains primary
and the cleanup exception is attached as suppressed context. Hard JVM, Gradle, or container
termination remains outside this guarantee and is recovered by destroying the disposable stack.

No matching mappings or request events, and DELETE responses indicating that a mapping is already absent, are idempotent success. Cleanup still matches the exact `testId`: it removes every scoped mapping and request event owned by that scenario, including installed mappings the scenario did not call, while leaving other test IDs and the stack's OAuth/JWKS startup mappings unchanged.

`WireMockAdmin` currently supports:

- `stub(mapping)`
- `removeStub(id)`
- `removeRequests(criteria)`
- `countRequests(pattern)`
- `listStubs()`

`countRequests` rejects a response reporting `requestJournalDisabled`, because WireMock answers
a disabled journal with `count: 0` rather than an error and a silent zero would satisfy every
absence assertion in the suite.

Scenario request events remain in WireMock until teardown, then are removed by exact
baggage criteria. Unscoped/background events remain until the disposable Compose stack
is destroyed. The framework does not expose a global reset operation.

## HTTP Clients And Evidence

Clients live under `clients`. Each client takes a single `ServiceApiClient` constructor
parameter whose defaults use `IntegrationTestConfig.httpClient` and the matching URL from
the resolved `IntegrationConfig`. `ServiceApiClient` is the sole request entry point: its
`get`/`getText`/`post`/`put` verbs require the scenario `testId`, send the baggage header,
set the JSON negotiation headers, and decode the response into `ApiResult`. Tests can
still inject an isolated HTTP client and base URL through the `ServiceApiClient`
constructor. Response decoding always uses `ServiceJson`. The composition root also uses
`ServiceJson` for request encoding, so the two directions use the same configuration.

Client methods return `ApiResult<T>`:

- `response`: raw Ktor response.
- `bodyText`: raw response text.
- `body`: decoded success body, or fails if the response was not successful.
- `errorBody`: decoded common error model for non-2xx responses when possible.

For successful responses:

```kotlin
val result = OhipApi().getLightweightReservationsByIds(
    reservationId = requireNotNull(booking.room.reservationId),
    hotelId = booking.hotel.hotelId,
    testId = testId,
)
result.attachEvidence("Get reservations by ids")

expect("returns the requested reservation") {
    result.response.status.value shouldBe 200
    result.body.reservationByIdList.first().reservationId shouldBe booking.room.reservationId
}
```

For error responses:

```kotlin
val result = OhipApi().getLightweightReservationsByIds(
    reservationId = requireNotNull(booking.room.reservationId),
    hotelId = booking.hotel.hotelId,
    testId = testId,
)
result.attachEvidence("Get reservations by ids")

expect("returns server error") {
    result.response.status.value shouldBe 500
    result.errorBody.shouldNotBeNull()
}
```

Call `attachEvidence()` after the client returns. A malformed successful JSON response is recorded automatically before decoding throws, even though no `ApiResult` is returned.

`HttpEvidencePlugin` captures method, URL, all synthetic headers, supported textual or byte-array request bodies, response status, response headers, and response body. Request and response bodies are each limited to 64 KiB of UTF-8 data and carry an explicit byte-count marker when truncated. The environment uses mocked integrations and synthetic test data, so authorization and data fields are intentionally retained without redaction. If Ktor sends a request body as another content type, request-body evidence may be absent.

## Reports And Stub Logs

`build.gradle.kts` uses Gradle's built-in test reports. The `integrationTest` task:

- runs with JUnit Platform;
- shows standard streams;
- captures concise evidence-path lines in JUnit XML standard output;
- clears stale `build/test-evidence` content before an executed test run.

The HTML report is generated at:

```text
build/reports/tests/integrationTest/index.html
```

The Docker-free unit-test HTML report is separate:

```text
build/reports/tests/test/index.html
```

JUnit XML results are at:

```text
build/test-results/integrationTest/
```

Every journey scenario writes one ordered artifact at:

```text
build/test-evidence/<testId>/evidence.txt
```

The artifact contains installed-stub entries, explicitly attached HTTP exchanges, the final scenario outcome, and the complete strict cleanup result. Partial stub installation and malformed success responses are retained on failure. Stdout and JUnit XML contain only `Scenario evidence: <path>` rather than full payloads.

Each artifact is isolated correctly, but standard output is process-wide. With concurrent
journeys, Gradle/Kotest can place a path line under a different test case in HTML or JUnit
XML. Locate evidence by its `testId` under `build/test-evidence`, or search report output
for the exact artifact path; do not rely on one-to-one stdout attribution.

If artifact writing fails, an otherwise successful scenario fails. When the journey or cleanup already failed, the write failure is attached as suppressed so it cannot hide the primary failure.

Gradle 9.4 can render JUnit Platform file entries as an `Attachments` tab, but
Kotest 6.2.2 does not expose file-entry publication from a running Kotest scenario.
Printing JUnit's `[[ATTACHMENT|...]]` XML convention remains plain stdout in the
Gradle HTML report, so this framework keeps the explicit evidence path until
Kotest provides a supported publication API or the runner is deliberately adapted.

## Kotlin Style

The module uses ktlint with the official Kotlin style. Unused-import detection is
enabled explicitly in `.editorconfig` and all three source sets are covered.

```bash
./gradlew ktlintCheck   # validate without changing files
./gradlew ktlintFormat  # auto-format and remove imports ktlint considers unused
```

`ktlintCheck` is part of the normal `check` and `build` lifecycle. Formatting is
explicit because it changes source files. Run `build` after formatting: ktlint's
unused-import analysis has known extension-import edge cases, while compilation is
the authoritative verification.

## Running Tests

The code targets JVM 25, and the build requires a discoverable Java 25 toolchain. The
Gradle wrapper itself must also run on a supported JVM. From this module:

```bash
cd backend/integration-tests-kotlin

# Compile everything and run Docker-free tests
./gradlew build

# Run every Docker-free framework test
./gradlew test

# Run environment-backed journeys after project preflight
./gradlew integrationTest

# Run both suites; integrationTest always executes against current external state
./gradlew test integrationTest

# Force the hermetic suite too
./gradlew test integrationTest --rerun-tasks
```

The normal `build -> check -> test` lifecycle is restored. `check` additionally
depends on `integrationTestClasses`, so journey compilation failures break a normal
build without executing journeys or environment preflight. The preflight, evidence,
and scenario-lifecycle behavior is exercised under `src/test`; only the live project
extension and journeys execute under `integrationTest`.

Run one spec:

```bash
./gradlew integrationTest --tests "uk.co.whitbread.integrationtests.journeys.ohip.GetLightweightReservationsByIdsSpec"
```

Every explicit `integrationTest` invocation executes against current external state;
`--rerun-tasks` is not required, including when a `--tests` filter is used.

Open the report (`open` is macOS; use `xdg-open` on Linux):

```bash
open build/reports/tests/integrationTest/index.html
```

The integration environment must be started separately through the externally managed workflow appropriate to the runtime. Gradle does not invoke Docker, Podman, Compose, or an environment startup script. The project preflight waits for every configured application and WireMock dependency before allowing journeys to execute.

## Common Failures

### `Environment preflight failed`

The externally managed environment did not become completely ready before the configured timeout. The failure lists every application or WireMock endpoint that was unavailable in the final polling round, including its last HTTP, response-validation, or connection failure. Start or repair those dependencies and rerun the suite; no journey ran during the failed attempt.

### `Connection refused` During `installFor(...)`

The framework passed preflight, but could no longer reach a WireMock Admin API while installing a stub. Open the scenario artifact and compare the latest successfully installed mock with the resolved endpoint environment settings. The Admin exception identifies the target base URL, but a logical mock name is recorded only after that mapping succeeds. For a failure before the first registration, use the stack trace to identify the failing stub builder.

Examples:

- `booking.opera.hotel-config` uses `WIREMOCK_OPERA_URL`.
- a CDH mock would use `WIREMOCK_CDH_URL`.
- an AEM mock uses `WIREMOCK_AEM_URL`.
- a Worldline mock uses `WIREMOCK_WORLDLINE_URL`.

### `Connection refused` After Stub Installation

If the artifact contains successful stub entries and the failure points at a typed client such as `OhipApi`, the WireMock setup worked and the missing dependency is likely a service-under-test port.

### Missing Scenario Evidence

The artifact is finalized after scenario cleanup. A JVM termination or failure during spec construction before a scenario starts cannot produce one. For an executed scenario, inspect the test-ID directories directly:

```text
build/test-evidence/<testId>/evidence.txt
```

The same path is printed in Gradle/HTML/JUnit output, but concurrent standard-output
capture can associate it with a different test case.

### No HTTP Evidence

Normal HTTP evidence requires an `ApiResult` and an `attachEvidence(...)` call. If the test fails before a response exists, no complete exchange can be attached. Malformed successful JSON is the exception: the raw exchange is captured automatically before decoding fails. For downstream error scenarios, install an error-returning stub and let the service call happen.

### Stale Reports

`integrationTest` clears prior scenario evidence before each invocation and is never
up to date. Its HTML and JUnit reports therefore describe the latest explicit run.

## Evolving Booking And Provisioning

The Kotlin journeys and local REST server deliberately share the same `Booking` type and
`MockInstaller` implementation. `JourneySpec` supplies its scenario test ID. The REST adapter
generates a UUID for POST and accepts the same ID on DELETE:

- adding a serializable field with a default or nullable value requires no Ktor
  route change; strict JSON decoding and generated OpenAPI pick it up automatically;
- adding or changing a Booking-gated stub under the existing four WireMocks requires
  no server change; REST and Kotlin callers use the updated installer automatically;
- changing a field to be required changes the POC JSON contract and can break older
  request fixtures, so update the checked-in contract examples and tests together;
- a field type unsupported by kotlinx.serialization needs a serializer;
- a new WireMock target, a different public request model, or new HTTP behavior does
  require server/configuration work.

Restart `./gradlew run` after code changes; the running JVM does not hot-reload this
application.

## Adding A New Booking Mock

When the booking flow calls a new downstream endpoint:

1. Add a focused stub builder under `stubs/<upstream>`.
2. Make it take the scenario data its matcher or response depends on — `Booking`, `Hotel`,
   `BookingRoom`, and so on. Do not give it a `testId` parameter or an ownership header.
3. Declare the string ID constant and WireMock target in the same stub file.
4. Return one `PlannedStub` with at least one ordered mapping.
5. Add the Booking gate in `stubs/DefaultStubs.kt`, inside the function for its
   upstream (`cdhStubs`, `worldlineStubs`, `datatransStubs`, `aemStubs`, or
   `operaStubs`).
6. Add or update a journey/error spec that proves the dependency behavior.
7. Add or update a Docker-free installer/server characterization when the new gate
   changes the representative mapping set.

Keep the journey spec focused on business data. The spec should not manually install every low-level stub.

## Adding A Different Scenario Model

The framework intentionally has one production provisioning model: `Booking`. Extend it when
new scenario facts belong to the same provisioning contract. A genuinely different public
model requires an explicit architecture and REST-contract decision; do not reintroduce a
generic planner, registry, reflection scanner, or executable receiver DSL speculatively.

## Authoring Checklist

- Exclude routine missing/null/blank/malformed-field and local DTO/domain validation cases;
  keep them in the owning service's tests.
- Model the scenario with explicit data first.
- Install mocks from that data with `installFor(booking)`.
- Keep normal stubs generic and Booking-driven; justify every exclusion, direct install, or
  custom stub as behavior a generic default cannot represent.
- Pass `testId` into every service-under-test client call.
- Pin every flag the endpoint's flow doc lists with `featureFlagOverrides`, including fixed
  flags at their fixed value; cover every reachable permutation of non-fixed request-scoped
  flags, and pin nothing when the flow lists none.
- Use `expect(...)` for meaningful assertions.
- Call `attachEvidence(...)` after each important client result.
- Prove "no downstream call" with an installed mock and `callCount(Upstream.X) shouldBe 0`,
  never by leaving the upstream unstubbed.
- Prefer an error-returning override stub over throwing during mock setup.
- Keep low-level WireMock builders out of normal journey specs.
- Override runtime hosts and ports through the documented endpoint environment variables.
- Open `build/test-evidence/<testId>/evidence.txt` from the path printed for the scenario.
