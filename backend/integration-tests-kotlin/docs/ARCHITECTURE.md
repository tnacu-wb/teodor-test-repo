# Framework Architecture

High-level view of how the Kotlin/Kotest integration test framework is layered and what
happens at runtime. For authoring rules and detail, see
[TEST_FRAMEWORK_GUIDE.md](TEST_FRAMEWORK_GUIDE.md).

## Source boundary

`src/main` contains the reusable Booking-to-WireMock provisioning closure: Booking
models, the `defaultStubsFor` table and internal `MockInstaller`, upstream stub builders,
WireMock administration/model types, test-ID headers, strict
cleanup, endpoint configuration, preflight behavior, HTTP capture, scenario
lifecycle/evidence, and the local Ktor mock-provisioning entry point. Auth token and JWKS
support (`testkit.auth.AuthTokens`) also lives in `src/main`, but outside that closure: it
is called by journeys and hermetic tests only, never by provisioning. `src/test` contains
every Docker-free architecture,
framework, provisioning, server, serialization/encoding, and cleanup test.
`src/integrationTest` contains journeys, typed SUT clients, preset
`Hotels`/`Companies` catalogues, Kotest wiring, and the project preflight extension.

Provisioning reports optional installed-stub events through a small main-source sink
interface. The main-source scenario sink renders those events into evidence artifacts;
the local REST server omits journey evidence without changing provisioning behavior.
Malformed HTTP response decoding reports through an HTTP-owned recorder/context seam,
which the scenario sink implements. This keeps the dependency one-way from reporting to
HTTP while preserving automatic failure evidence.

Gradle's normal `build -> check -> test` lifecycle runs all hermetic tests and compiles
all `integrationTestClasses` without contacting the stack. The explicit
`integrationTest` task performs preflight and runs environment-backed journeys on every
invocation, with separate unit and integration reports.

## Architecture tests

Docker-free structural rules live under
`src/test/kotlin/uk/co/whitbread/integrationtests/architecture/` and run as part of
`./gradlew test`.

They enforce:

- inverted layer edges stay forbidden (`clients`/`stubs`/`framework`/`testkit` do not
  depend on `journeys`; `clients` do not depend on `stubs`; `framework` does not depend
  on `testkit`; HTTP and WireMock internals do not depend on reporting; and so on)
- source-set root packages stay intentional (`main` = `framework`/`provisioning`/
  `stubs`/`testkit`, `integrationTest` = `clients`/`framework`/`journeys`/`testkit`)
- service package segments under `clients` and `journeys` are all-lowercase and
  share keys where both layers exist
- journey primary classes are named `*Spec` and extend `JourneySpec`
- journey specs do not import `framework.*` or raw Ktor client request APIs. Journeys can import
  self-contained builders and ID constants from `stubs.*`
- main-source scenario models under `testkit.model` do not
  import WireMock admin or Ktor clients
- top-level typed-client classes outside model packages are named `*Api`, and
  `*Stubs*.kt` builder files stay under `stubs`
- public typed-client methods take `testId`
- client files import only from an explicit allowlist (the `parameter` and `header` Ktor
  request extensions, client models, `IntegrationTestConfig`, `ApiResult`,
  `ServiceApiClient`, feature flags, and testkit models), so every request goes through
  `ServiceApiClient`, which supplies the scenario baggage header and the `ApiResult`
  decoding — any new transport dependency fails the build by name
- only `testkit.featureflags` may import `BaggageFlag`, so the sealed `FeatureFlag`
  vocabulary stays the only flag implementation that can reach the wire
- main-source scenario model type names avoid endpoint-shaped suffixes such as `Request`
- `JourneySpec` owns the journey author interface and cleanup lifecycle

Package checks are path-based so the same package name can safely exist in more
than one source set.

Two limits are worth knowing before relying on a rule. The scanned roots are
`src/main/kotlin` and `src/integrationTest/kotlin` only, so nothing under `src/test/kotlin`
is checked. And most rules parse import statements as source text, so a fully qualified
reference written inline bypasses the import-based checks. Everything not in the list above
is a reviewer convention rather than an enforced invariant.

## Layers

Test authors work in `journeys`, `testkit`, `stubs`, and `clients`. The local `provisioning`
server is another entry point over the same testkit. `framework` contains runtime internals.

The arrows below show the principal control and planned-data flow. They are not an
exhaustive source-import graph; the architecture tests above define those boundaries.

```mermaid
flowchart TD
    subgraph journeys["journeys — Kotest specs"]
        SPEC["Journey specs (*Spec)<br/>scenario(...) blocks"]
    end

    subgraph testkit["testkit — author API + lifecycle internals"]
        JS["JourneySpec + ScenarioScope<br/>installFor · installStub · callCount · cleanup"]
        MODEL["model: Booking + nested data<br/>(BookingRoom, hotelPagePath, Aem, ...)"]
        INSTALLER["internal MockInstaller<br/>default collection · registration · journal counts · scoped removal"]
    end

    subgraph clients["clients — typed service clients"]
        CL["CompanyEntityApi · ContentEntityApi<br/>HotelReservationApi · OhipApi · SpendingApi"]
    end

    subgraph provisioning["provisioning — local Ktor API"]
        API["POST /mock-sessions<br/>health · OpenAPI · Swagger"]
    end

    subgraph stubs["stubs — self-contained stubs"]
        ST["PlannedStub<br/>ID · target · ordered mappings"]
        GATES["defaultStubsFor(booking)<br/>Booking data gates"]
    end

    subgraph framework["framework — runtime internals"]
        CFG["IntegrationConfig<br/>(resolved endpoint snapshot)"]
        PCFG["IntegrationTestConfig<br/>(composition root + Kotest lifecycle)"]
        WMA["WireMockInstances<br/>(enum-indexed Admin adapters + 1 shared transport)"]
        EVID["HTTP evidence models + recorder seam<br/>HttpEvidencePlugin · ApiResult"]
        REP["reporting<br/>scenario evidence sink + rendering"]
    end

    SPEC --> JS
    SPEC --> MODEL
    JS --> INSTALLER
    SPEC --> ST
    SPEC --> CL

    API --> MODEL
    API --> INSTALLER
    API --> WMA

    INSTALLER --> GATES
    MODEL --> GATES
    GATES --> ST
    INSTALLER --> ST
    INSTALLER --> WMA

    CL --> EVID
    CL --> PCFG
    JS --> PCFG
    PCFG --> CFG
    PCFG --> EVID
    PCFG --> WMA
    INSTALLER --> REP
    REP --> EVID

    classDef author fill:#e6f2ff,stroke:#1f6feb,color:#0b2545;
    classDef internal fill:#f3f3f3,stroke:#888,color:#222;
    class journeys,testkit,clients author;
    class provisioning,stubs,framework internal;
```

Happy-path journeys depend on `testkit` and `clients`. Journeys can import `stubs` constants and
builders for exclusions and direct installation. Journeys never import
`framework` directly.

Author-facing scenario control lives in `testkit` for the same reason. `testkit.featureflags`
owns the sealed `FeatureFlag` vocabulary, while `framework.http` owns the baggage grammar —
the test-ID member, the matcher that finds it, and the override encoding. The encoding is
keyed by the framework's own `BaggageFlag` abstraction, which `FeatureFlag` implements. That
keeps the flag vocabulary reachable from journeys without a `framework` -> `testkit` edge,
and an architecture rule enforces that the edge stays absent.

`IntegrationTestConfig` is intentionally the integration suite's composition root:
it resolves one lazy `IntegrationConfig` snapshot, constructs the shared SUT client and
one WireMock transport used by four Admin adapters, supplies them to `JourneySpec` and
typed-client defaults, and closes initialized resources after the Kotest project. Typed
clients retain explicit constructor injection for isolated tests. The local provisioning
process is a separate entry point; it owns its WireMock transport and closes it when Ktor
stops, with `main` cleanup retained for startup failures.

## Local provisioning-server flow

`JourneySpec` owns the journey author interface and cleanup lifecycle.
The Ktor application uses one `MockInstaller` for all requests.
Both adapters pass the test ID into the installer.
`callCount` reads the scenario request journal and does not require an install.
Independent scenarios still run concurrently because the installer keeps no scenario state.

```mermaid
sequenceDiagram
    autonumber
    participant Frontend as Frontend/test runner
    participant API as Ktor provisioning API
    participant Installer as MockInstaller
    participant WM as Four WireMock Admin APIs

    Frontend->>API: POST /mock-sessions (Booking JSON)
    API->>API: strict decode + Booking validation
    API->>API: generate UUID testId
    API->>Installer: installFor(booking, UUID)
    Installer->>Installer: collect and select Booking stubs
    Installer->>WM: install selected stubs for UUID
    alt installation succeeds
        API-->>Frontend: 201 {testId, baggage}
    else collection or installation fails
        API-->>Frontend: 503 with testId and the cause in the message
    end
    Frontend->>API: DELETE /mock-sessions/{testId}
    API->>Installer: removeFor(testId)
    Installer->>WM: remove scoped mappings and request events
    API-->>Frontend: 204
```

`GET /health` checks only that the local Ktor process is running. Generated
OpenAPI is served at `/openapi.json`, with Swagger UI at `/swagger`. Idempotent
`DELETE /mock-sessions/{testId}` removes the same scoped state as Kotlin journey
teardown; the disposable stack remains the recovery boundary for crashed clients
that never send DELETE. A failed POST is not undone either. Since a client that never
sends DELETE already leaks a whole session, undoing only the partial one bought a
mechanism the teardown boundary already covers.

The `503` therefore carries no retry or cleanup instruction, only the test ID and the
underlying failure message. Both failure kinds behind it — a Booking whose stubs cannot be
built, which throws before the first Admin call, and a WireMock Admin call that
failed — are told apart by reading that message rather than by a status code or a
flag, because the consumer is a developer rather than a retry loop.

## Per-scenario runtime flow

Each `scenario(...)` is one isolated test. Stubs are installed dynamically, matched by
the scenario `testId` (`baggage: wb-test-id=<testId>`), then cleaned up after the test.

```mermaid
    sequenceDiagram
    autonumber
    participant Spec as Scenario (JourneySpec)
    participant Installer as MockInstaller
    participant WM as WireMock Admin API
    participant Client as Typed client (OhipApi, ...)
    participant SUT as Service under test
    participant Down as Downstream WireMock stub

    Spec->>Installer: installFor(booking, testId, excluded)
    Installer->>Installer: collect and select Booking stubs
    Installer->>WM: register scoped StubMappings (matcher: baggage contains wb-test-id)
    Spec->>Client: call(..., testId)
    Client->>SUT: HTTP request (baggage: wb-test-id=<testId>)
    SUT->>Down: downstream call
    Down-->>SUT: stubbed response (matched on testId)
    SUT-->>Client: response
    Client-->>Spec: ApiResult
    Spec->>Spec: result.attachEvidence()
    Spec->>Spec: expect(...) assertions
    Spec->>Installer: callCount(Upstream) inside expect
    Installer->>WM: count journal events matching wb-test-id baggage
    WM-->>Installer: recorded call count
    Spec->>Installer: removeFor(testId) (after block)
    Installer->>WM: delete stubs and request events tagged with testId
```

## Test isolation model

Tests run concurrently with Kotest 6 limited spec and test concurrency of four. Kotlin
journeys use readable IDs with a random six-character suffix; REST sessions use
server-generated UUIDs. Both are isolated by `baggage` test-ID tagging rather than
by resetting WireMock between tests, so parallel scenarios never wipe each other's
stubs.

```mermaid
flowchart LR
    START["Externally create disposable Compose stack"]
    STOP["Externally destroy disposable Compose stack"]

    subgraph scen["Per scenario (concurrent)"]
        S1["scenario A<br/>testId = ...-a1b2c3"]
        S2["scenario B<br/>testId = ...-d4e5f6"]
    end

    subgraph wmocks["WireMock instances"]
        OP["Opera :8443"]
        CDH["CDH :8445"]
        WL["Worldline :8446"]
        AEM["AEM :18084"]
    end

    START --> scen
    S1 -- "install / remove state tagged a1b2c3" --> wmocks
    S2 -- "install / remove state tagged d4e5f6" --> wmocks
    scen --> STOP
```

The framework performs no project-wide mapping or request-journal reset. Stack creation
supplies clean initial state — including the authentication mappings loaded from
`integration-env/wiremock/`, which belong to the stack and which no test installs — strict
per-scenario cleanup removes mappings and request events carrying the exact test ID, and stack
destruction removes background or crashed-process state. Cooperative scenario cancellation cannot interrupt
the suspending cleanup work; the original cancellation resumes propagating after cleanup.
