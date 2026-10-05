# Create reservation: how WireMock state returns a different ID per room

## The problem

The hotel reservation entity service sends a multi-room create request to OHIP. OHIP then sends
one Opera create request per room:

```http
POST /rsv/v1/hotels/HEAPTI/reservations
```

When two selected rooms are identical, these two Opera requests can have the same method, URL,
headers, and JSON body. A normal WireMock mapping would therefore return the same response for
both calls. OHIP would receive the same reservation ID twice and collapse it to one ID.

The test fixture already declares which IDs mocked Opera should allocate:

```kotlin
rooms =
    listOf(
        BookingRoom(reservationId = "6003002", ...),
        BookingRoom(reservationId = "6003003", ...),
    )
```

WireMock state lets the first identical POST return `6003002` and the second identical POST
return `6003003`.

## The most important distinction

There are two separate kinds of HTTP requests in this flow:

1. During test setup, the testkit sends mapping configuration requests to the **WireMock Admin
   API**.
2. During test execution, OHIP sends reservation creation requests to the **mocked Opera API**.

The different state parameter is sent in the first kind of request:

```http
POST /__admin/mappings
```

It is **not** added to OHIP's Opera reservation request.

For the two rooms, the testkit sends WireMock two mappings with different
`requiredScenarioState` values:

```text
First WireMock mapping:  requiredScenarioState = Started
Second WireMock mapping: requiredScenarioState = reservation-1-created
```

OHIP's two Opera POST requests can remain identical. WireMock distinguishes them using its
internal scenario state, not a parameter supplied by OHIP.

## Phase 1: installing the mappings

`installFor(booking)` builds one `StubMapping` per room and registers each mapping through
WireMock's `POST /__admin/mappings` endpoint.

The first Admin API request contains a mapping equivalent to the following abbreviated JSON.
Only the first response link is shown:

```json
{
  "scenarioName": "opera-create-reservation:<uuid>",
  "requiredScenarioState": "Started",
  "newScenarioState": "reservation-1-created",
  "request": {
    "method": "POST",
    "urlPath": "/rsv/v1/hotels/HEAPTI/reservations"
  },
  "response": {
    "status": 200,
    "jsonBody": {
      "links": [
        {
          "href": ".../reservations/6003002",
          "method": "GET",
          "operationId": "getReservation"
        }
      ]
    }
  }
}
```

The second Admin API request contains another mapping, again abbreviated to its first response
link:

```json
{
  "scenarioName": "opera-create-reservation:<uuid>",
  "requiredScenarioState": "reservation-1-created",
  "newScenarioState": "reservation-2-created",
  "request": {
    "method": "POST",
    "urlPath": "/rsv/v1/hotels/HEAPTI/reservations"
  },
  "response": {
    "status": 200,
    "jsonBody": {
      "links": [
        {
          "href": ".../reservations/6003003",
          "method": "GET",
          "operationId": "getReservation"
        }
      ]
    }
  }
}
```

The request matchers are allowed to be identical. The scenario parameters are different:

| Mapping | Required state | Response reservation ID | State after response |
| --- | --- | --- | --- |
| First room | `Started` | `6003002` | `reservation-1-created` |
| Second room | `reservation-1-created` | `6003003` | `reservation-2-created` |

Both mappings use the same `scenarioName`, so WireMock treats them as states in the same state
machine. `createReservation(booking)` generates a new UUID for each build. Concurrent builds and
repeated installs cannot share state.

The UUID is not an ownership token. Cleanup still matches the `wb-test-id` baggage on the request
pattern, so every chain a test created is reclaimed.

## Phase 2: OHIP calls mocked Opera

WireMock automatically initializes a new scenario to `Started`. The testkit does not need to
call a separate endpoint to set this initial state.

The runtime sequence is:

```text
WireMock state: Started

OHIP POST for room 1
  -> only the mapping requiring Started is eligible
  -> WireMock returns the response containing 6003002
  -> WireMock changes its state to reservation-1-created

OHIP POST for room 2
  -> the Started mapping is no longer eligible
  -> the mapping requiring reservation-1-created is eligible
  -> WireMock returns the response containing 6003003
  -> WireMock changes its state to reservation-2-created

OHIP schedules one fixed-rate PUT after each created ID is emitted:
  PUT /reservations/6003002
  PUT /reservations/6003003

The PUTs can overlap or interleave with later create POSTs. They do not read or change the
WireMock create scenario state. Only the relative order of the create POSTs is guaranteed.

After all create and fixed-rate operations complete:
OHIP GET /reservations/6003002
OHIP GET /reservations/6003003
  -> both created reservations are returned to the caller
```

The state belongs to WireMock and is changed by WireMock after it serves each configured
create response. The exact POST/PUT interleaving is irrelevant to this state chain.

## How the Kotlin loop builds the state chain

The create stub uses `rooms.mapIndexed` to construct all mappings before any OHIP request is
made. For each zero-based room `index`:

```text
required state:
  index 0 -> Started
  index N -> reservation-N-created

new state:
  every index N -> reservation-(N + 1)-created
```

For two rooms, this constructs:

```kotlin
val firstMapping =
    StubMapping(
        requiredScenarioState = "Started",
        newScenarioState = "reservation-1-created",
        response = responseContaining("6003002"),
    )

val secondMapping =
    StubMapping(
        requiredScenarioState = "reservation-1-created",
        newScenarioState = "reservation-2-created",
        response = responseContaining("6003003"),
    )

return listOf(firstMapping, secondMapping)
```

This code only constructs WireMock configuration. WireMock serves the configured responses
later, when OHIP calls the mocked Opera endpoint.

## Scaling to more rooms

The same loop scales without additional infrastructure changes. Four fixture rooms create this
chain:

| Mapping | Required state | Returned ID | New state |
| --- | --- | --- | --- |
| Room 1 | `Started` | first fixture ID | `reservation-1-created` |
| Room 2 | `reservation-1-created` | second fixture ID | `reservation-2-created` |
| Room 3 | `reservation-2-created` | third fixture ID | `reservation-3-created` |
| Room 4 | `reservation-3-created` | fourth fixture ID | `reservation-4-created` |

Every requestable room must have a unique explicit `reservationId`. The final state has no
matching create mapping, so an unexpected additional POST does not silently reuse the last ID.

## Single-room behavior

A single room does not need state. Its mapping has `null` scenario fields and behaves like an
ordinary WireMock mapping. Scenario fields are only added when `booking.rooms` contains more
than one requestable room.

## Concurrency and cleanup

This ordered state chain relies on OHIP's current reservation-create `maxConcurrency: 1`.
WireMock scenario selection and state transition must not be used here to coordinate concurrent
identical POSTs. If OHIP starts creating rooms concurrently, the mock strategy must be reviewed.

Mappings and scenario names are scoped with `testId`. Scenario cleanup happens when the testkit
deletes the test-owned mappings; no global WireMock state reset is performed because other tests
may be running concurrently.

## Relevant implementation

- [`OperaReservationStubs.kt`](../src/main/kotlin/uk/co/whitbread/integrationtests/stubs/opera/OperaReservationStubs.kt)
  constructs one create mapping and state transition per room.
- [`StubMapping.kt`](../src/main/kotlin/uk/co/whitbread/integrationtests/framework/wiremock/model/StubMapping.kt)
  serializes the scenario fields sent to WireMock.
- [`WireMockAdmin.kt`](../src/main/kotlin/uk/co/whitbread/integrationtests/framework/wiremock/WireMockAdmin.kt)
  sends each mapping to `POST /__admin/mappings`.
- [`OperaReservationStubsTest.kt`](../src/test/kotlin/uk/co/whitbread/integrationtests/stubs/opera/OperaReservationStubsTest.kt)
  verifies the state chain, response IDs, serialization, and test isolation.
- [`CreateReservationSpec.kt`](../src/integrationTest/kotlin/uk/co/whitbread/integrationtests/journeys/hotelreservation/CreateReservationSpec.kt)
  proves the behavior through the REST endpoint.
