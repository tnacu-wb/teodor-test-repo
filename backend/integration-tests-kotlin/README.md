# integration-tests-kotlin

Kotlin/Kotest integration tests for service journeys.

Framework documents:

- [Test framework guide](docs/TEST_FRAMEWORK_GUIDE.md) — the maintained guide for writing
  journeys: base specs, scenario data, mock installation, cleanup, and evidence.
- [Mock install path](docs/MOCK_INSTALL_PATH.md) — the mock installation path from
  `JourneySpec.installFor(booking)` to the WireMock Admin API.
- [Scenario ownership](docs/SCENARIO_OWNERSHIP.md) — how the `wb-test-id` baggage matcher is
  stamped onto every mapping, and why stub builders never mention `testId`.
- [Create-reservation WireMock state](docs/CREATE_RESERVATION_WIREMOCK_STATE.md) — how a
  `testId`-scoped WireMock Scenario returns a different reservation ID per identical room.
- [Architecture](docs/ARCHITECTURE.md) — source boundaries, layers, and runtime flows.

Flow documents:

- Authoring guidelines: the `integration-testing-trace-endpoint` skill owns the flow doc
  format and the tracing workflow (`.claude/skills/integration-testing-trace-endpoint/`).
- [Company entity service flows](flows/company-entity-service/)
- [Content entity service flows](flows/content-entity-service/)
- [Hotel entity service flows](flows/hotel-entity-service/)
- [Hotel reservation entity service flows](flows/hotel-reservation-entity-service/)
- [OHIP adapter service flows](flows/ohip-adapter-service/)
- [Payment orchestration service flows](flows/payment-orchestration-service/)
- [Spending entity service flows](flows/spending-entity-service/)
- [Page tests](docs/page-tests/)

Flow documents describe public endpoint behavior and are maintained independently
of Kotlin journey coverage. A flow document does not by itself indicate that a
matching journey exists.

API matrices:

- [Backend services API matrix](docs/api-matrix/BACKEND_SERVICES_API.md)
- [Spending entity service API matrix](docs/api-matrix/SPENDING_ENTITY_SERVICE_API.md)

API matrices map service APIs to external APIs, default stubs, journey tests, and flow documents.

## Commands

The build compiles and runs on a discoverable Java 25 toolchain and emits Java 25-only
bytecode. The Gradle wrapper itself must also run on a supported JVM. Run commands from
this module:

```bash
cd backend/integration-tests-kotlin
```

### Run the local provisioning server

```bash
./gradlew run
```

Starts the Ktor mock-provisioning API at `http://127.0.0.1:9190`. Keep the
disposable integration stack running before creating a mock session, because the
server installs mappings through the four WireMock Admin APIs. Stop the server
with `Ctrl+C`.

The bind address and port can be overridden when necessary:

```bash
MOCK_PROVISIONING_HOST=127.0.0.1 MOCK_PROVISIONING_PORT=9290 ./gradlew run
```

The server uses the same WireMock endpoint configuration as the Kotlin suite.
Override `WIREMOCK_OPERA_URL`, `WIREMOCK_CDH_URL`, `WIREMOCK_AEM_URL`, or
`WIREMOCK_WORLDLINE_URL` when the Admin APIs are not on their localhost defaults.
The server owns one HTTP transport shared by its four WireMock Admin adapters. Ktor's
stop event closes that transport, with process-level cleanup retained for startup failures.

### Run tests and checks

The integration suite uses the published localhost ports by default. Override any SUT
origin independently with `OHIP_INTERFACE_BASE_URL`,
`HOTEL_RESERVATION_BASE_URL`,
`RATE_MANAGEMENT_GATEWAY_BASE_URL`, `CONTENT_BASE_URL`,
`SPENDING_ENTITY_BASE_URL`, `COMPANY_ENTITY_BASE_URL`, `CDH_ADAPTER_BASE_URL`,
`PIBA_ACCOUNT_BASE_URL`, or `PAYMENT_ORCHESTRATION_BASE_URL`. This supports future Compose-network values such as
`http://ohip-adapter-service:9100` without changing the current host workflow.
All SUT and WireMock values must be HTTP(S) origins without paths, queries, fragments,
or user information.

```bash
# Run the hermetic framework, contract, architecture, and server tests.
# These tests do not require Docker or the integration stack.
./gradlew test

# Run environment-backed service journeys. The disposable stack must be running,
# and the task always performs project preflight and executes against current state.
./gradlew integrationTest

# Run the hermetic suite followed by the environment-backed journey suite.
# Gradle may reuse an up-to-date hermetic result; integrationTest executes every time.
./gradlew test integrationTest

# Force every hermetic test to execute even when Gradle considers it up to date.
./gradlew test --rerun-tasks

# Force the hermetic suite and then execute the complete environment-backed suite.
./gradlew test integrationTest --rerun-tasks

# Compile main, test, and integration-test code; run hermetic tests and checks;
# and build the application distributions. It does not run integrationTest.
./gradlew build

# Check Kotlin formatting and imports without changing source files.
./gradlew ktlintCheck

# Apply Kotlin formatting and remove imports identified as unused.
./gradlew ktlintFormat

# Open the generated HTML reports on macOS.
open build/reports/tests/test/index.html
open build/reports/tests/integrationTest/index.html
```

## Local provisioning API

| Method and endpoint | Description |
| --- | --- |
| `GET http://127.0.0.1:9190/health` | Confirms that the local Ktor server is running. It does not check the WireMock instances. |
| `POST http://127.0.0.1:9190/mock-sessions` | Accepts strict Booking JSON, generates a UUID test ID, and installs its mocks. |
| `DELETE http://127.0.0.1:9190/mock-sessions/{testId}` | Idempotently removes the session's scoped mappings and request-journal events. |
| `GET http://127.0.0.1:9190/openapi.json` | Returns OpenAPI generated from the Ktor routes and Kotlin serializers. |
| `GET http://127.0.0.1:9190/swagger` | Opens Swagger UI for browsing and trying the API. |

Example health check:

```bash
curl --fail http://127.0.0.1:9190/health
```

Example mock installation:

```bash
curl --fail \
  --header 'Content-Type: application/json' \
  --data '{
    "hotels": [{
      "hotelId": "HEAPTI",
      "shortId": "LONHEA",
      "name": "London Heathrow Airport Terminal 4",
      "addressLine": "Sheffield Road",
      "city": "London",
      "postcode": "TW6 3AF",
      "phone": "+44 20 0000 0000"
    }],
    "arrival": "2026-08-01",
    "departure": "2026-08-03",
    "rooms": [{ "reservationId": "6001001", "roomType": "LOWDBL", "adults": 2 }]
  }' \
  http://127.0.0.1:9190/mock-sessions
```

Every field is optional — what you supply is what determines which mocks get installed,
and this is a small corner of what a `Booking` accepts. For the complete field surface,
open [`/swagger`](http://127.0.0.1:9190/swagger) and expand the **request** schema on
`POST /mock-sessions`: it is generated from the `Booking` model itself, so it cannot
drift from what the server accepts. The decoder is strict, so an unknown or misspelled
key is rejected — the response only reports that provisioning failed, and the server
log names the key.

A successful request returns `201 Created` with a server-generated UUID `testId` and the
complete `baggage` header value to send with it:

```json
{
  "testId": "3f9c1e84-5b0a-4d21-9f77-2c6ab0e1d5f3",
  "baggage": "wb-test-id=3f9c1e84-5b0a-4d21-9f77-2c6ab0e1d5f3"
}
```

Frontend requests must propagate that baggage value so the services match the installed
WireMock stubs.
Delete the session with `DELETE /mock-sessions/{testId}` after use. Kotlin journeys
clean their scenario-owned WireMock state automatically. Cleanup removes both scoped mappings
and request-journal events while preserving other concurrent test IDs.

Each executed scenario writes one artifact to `build/test-evidence/<testId>/evidence.txt`,
containing its installed stubs, its outcome, and its complete cleanup result. HTTP exchanges
appear only where the scenario called `attachEvidence(...)`, plus any malformed successful
response captured automatically. Request and response bodies are bounded at 64 KiB each. Test
output carries one concise path to the artifact instead of full payloads.

The framework does not start or manage the environment. `integrationTest` contacts
the environment, and the local provisioning server contacts WireMock when a session
is created. Before any integration test executes, `integrationTest` waits for all required
application health and WireMock Admin endpoints to become ready and reports every
unavailable dependency together if readiness times out. Because external state is
not tracked, every explicit `integrationTest` invocation executes without
`--rerun-tasks`.

Reusable Booking models, installers, stub builders, WireMock administration, strict
cleanup, endpoint configuration, preflight behavior, HTTP capture, scenario
lifecycle/evidence, and the local Ktor server live in `src/main`. Journeys, typed
service clients, preset fixtures, Kotest project wiring, and the project preflight
extension live in `src/integrationTest`. All Docker-free architecture, framework,
provisioning, cleanup, JSON-contract, and server tests live in `src/test`.
