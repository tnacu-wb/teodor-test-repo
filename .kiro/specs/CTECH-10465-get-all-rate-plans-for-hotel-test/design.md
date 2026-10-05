# Design Document: Get All Rate Plans for Hotel Integration Test

## Overview

Add a Kotlin integration test covering the ohip-adapter-service `GET /ohip/ratePlans?hotelId={hotelId}` endpoint for HEAPTI and FRAMTI hotels. The test validates that the service correctly proxies Opera's `/rtp/v1/ratePlans` paginated response and returns well-formed rate plan data.

The implementation adds or updates these integration-test files:

| Action | File |
|--------|------|
| Extend | `clients/ohip/OhipApi.kt` |
| Modify | `testkit/model/Booking.kt` |
| Modify | `testkit/model/BookingMocks.kt` |
| Create | `clients/ohip/model/RatePlansResponse.kt` |
| Create | `stubs/opera/OperaRatePlansStubs.kt` |
| Create | `journeys/ohipService/GetAllRatePlansForHotelHeapti.kt` |
| Create | `journeys/ohipService/GetAllRatePlansForHotelFramti.kt` |

All paths are relative to `backend/integration-tests-kotlin/src/test/kotlin/uk/co/whitbread/integrationtests/`.

---

## Architecture

```
Test class (JourneySpec)
  │  uses Booking(hotel = Hotels.HEAPTI) and a single scenario(...)
  │  installFor(booking, testId) installs default mocks; the hotel-only booking
  │  skips guarded rate/package/profile/reservation-lookup/AEM stubs and installs
  │  OAuthToken, HotelConfig, CreateReservation, and RatePlans
  │  calls OhipApi.getRatePlans(hotelId, testId)
  ▼
OhipApi (Ktor client)
  │  GET http://localhost:9100/ohip/ratePlans?hotelId=HEAPTI
  │  header: x-amzn-trace-id: <testId>
  ▼
ohip-adapter-service (running in Docker / test env)
  │  calls Opera Cloud
  ▼
WireMock Opera (localhost:8443)
  │  stub: GET /rtp/v1/ratePlans
  │        header: x-hotelid: HEAPTI
  │        header: x-amzn-trace-id: <testId>
  └─ returns Opera rate plans JSON nested under ratePlanShortInfoList.ratePlanShortInfo
```

---

## Components and Interfaces

### Base test class decision

`JourneySpec` is used as the base class. It wraps Kotest `FeatureSpec`, creates a single feature from the supplied description, exposes `scenario(...)`, and drives the mock cleanup lifecycle. To avoid a full booking setup for a read-only rate-plan test:

1. `Booking` fields `arrival`, `departure`, `roomType`, and `adults` are made nullable with null defaults — see the Booking model change below.
2. The test constructs a hotel-only booking: `Booking(hotel = Hotels.HEAPTI)`.
3. `mocks.installFor(booking, testId)` is called with no overrides. The `enabled` guards in `BookingMocks` automatically skip mocks that require absent hotel-only data (`reservationId`, `guestProfile`, `availableRates`, `availablePackages`, `aem`). `OAuthToken`, `HotelConfig`, `CreateReservation`, and `RatePlans` install; `CreateReservation` is part of the default mock set but is not exercised by this read-only journey.

### `Booking` model change (`Booking.kt`)

Make `arrival`, `departure`, `roomType`, and `adults` nullable, and guard the `init` check:

```kotlin
data class Booking(
    val hotel: Hotel,
    val arrival: LocalDate? = null,
    val departure: LocalDate? = null,
    val roomType: String? = null,
    val adults: Int? = null,
    val children: Int = 0,
    val bookingReference: String? = null,
    val reservationId: String? = null,
    val status: ReservationStatus = ReservationStatus.ON_HOLD,
    val guestProfile: GuestProfile? = null,
    val aem: Aem? = null,
) {
    init {
        if (arrival != null && departure != null) {
            require(departure.isAfter(arrival)) { "booking.departure must be after booking.arrival" }
        }
    }
}
```

Existing call-sites that pass `arrival`, `departure`, `roomType`, and `adults` explicitly are unaffected by this change (named arguments still work the same way). Any site that reads `booking.arrival` or `booking.departure` non-null-safely will require a `!!` or null check — these are restricted to booking-specific stubs that are not called for the hotel-only booking.

### `BookingMocks` change (`BookingMocks.kt`)

Add a `RatePlans` `MockDefinition` to `BookingMocks.Opera` and append it to `defaults`:

```kotlin
val RatePlans = MockDefinition<Booking>("booking.opera.rate-plans") { booking ->
    wiremock.opera.stub(ratePlans(booking.hotel.hotelId, testId))
}
```

Add `Opera.RatePlans` to the end of the `defaults` list.

### OhipApi extension (`OhipApi.kt`)

Add one method. No changes to constructor or existing methods:

```kotlin
suspend fun getRatePlans(
    hotelId: String,
    testId: String,
): ApiResult<RatePlansResponse> {
    val response = client.get("$baseUrl/ohip/ratePlans") {
        accept(ContentType.Application.Json)
        parameter("hotelId", hotelId)
        headers.append("x-amzn-trace-id", testId)
    }
    return response.toApiResult(json)
}
```

### WireMock stub (`OperaRatePlansStubs.kt`)

One public top-level function:

```kotlin
fun ratePlans(hotelId: String, testId: String): StubMapping
```

Matches:
- `urlPath = "/rtp/v1/ratePlans"`
- `headers["x-hotelid"] = equalTo(hotelId)`
- `headers[TEST_ID_HEADER] = equalTo(testId)`

Returns HTTP 200 with a static Opera JSON body nested under `ratePlanShortInfoList.ratePlanShortInfo` (see Data Models section).

---

## Data Models

### `RatePlansResponse.kt`

```kotlin
@Serializable
data class RatePlansResponse(
    val ratePlans: List<RatePlan> = emptyList(),
)

@Serializable
data class RatePlan(
    val ratePlanCode: String? = null,
    val hotelId: String? = null,
    val primaryDetails: RatePlanPrimaryDetails? = null,
    val classifications: RatePlanClassifications? = null,
)

@Serializable
data class RatePlanPrimaryDetails(
    val description: RatePlanDescription? = null,
)

@Serializable
data class RatePlanDescription(
    val defaultText: String? = null,
)

@Serializable
data class RatePlanClassifications(
    val rateCategory: String? = null,
    val displaySet: String? = null,
    val marketCode: String? = null,
)
```

### Stub response body storage

Stub bodies are stored as `private const val` strings in `OperaRatePlansStubs.kt`, co-located with the function that uses them.

HEAPTI stub body: `ratePlanShortInfoList.hasMore=true`, `ratePlanShortInfoList.totalResults=746`, 20 `ratePlanShortInfoList.ratePlanShortInfo` objects including codes: FLEXRATE, SEMIFLEX, ADVANCE, STANDARD, NONFLEX, NONFLEXD, HOUSEUSE, COMPTARY, ADVNCEBB, BADUTYBB (plus 10 more to reach 20 items).

FRAMTI stub body: `ratePlanShortInfoList.hasMore=true`, `ratePlanShortInfoList.totalResults=1399`, 20 `ratePlanShortInfoList.ratePlanShortInfo` objects including codes: FLEXRATE, MIFIXB03, MIFLXCOB, MIFXBCPO, MIFXBEDB, SEMIFLEX, ADVANCE, STANDARD, NONFLEX, NONFLEXD (plus 10 more to reach 20 items).

Stub response wrapper shape:
```json
{
  "ratePlanShortInfoList": {
    "hasMore": true,
    "totalResults": 746,
    "offset": 20,
    "limit": 20,
    "totalPages": 38,
    "ratePlanShortInfo": [
      {
        "ratePlanCode": "FLEXRATE",
        "hotelId": "HEAPTI",
        "primaryDetails": {
          "description": { "defaultText": "Flex Rate" }
        },
        "classifications": {
          "rateCategory": "A",
          "displaySet": "PBF",
          "marketCode": "OTH"
        }
      }
    ]
  }
}
```

Each rate plan object inside `ratePlanShortInfo` has this shape:
```json
{
  "ratePlanCode": "FLEXRATE",
  "hotelId": "HEAPTI",
  "primaryDetails": {
    "description": { "defaultText": "Flex Rate" }
  },
  "classifications": {
    "rateCategory": "A",
    "displaySet": "PBF",
    "marketCode": "OTH"
  }
}
```

---

## Test Classes

Both classes follow the same structure in `journeys/ohipService/`. `JourneySpec` is used as the base class with a hotel-only `Booking`, one `scenario(...)`, and all assertions inside `step(...)` blocks.

```kotlin
class GetAllRatePlansForHotelHeapti : JourneySpec(
    "HEAPTI rate plans can be fetched",
    Booking(hotel = Hotels.HEAPTI),
    {
        mocks.installFor(booking, testId)

        scenario("GET /ohip/ratePlans?hotelId=HEAPTI") {
            val result = OhipApi().getRatePlans(booking.hotel.hotelId, testId)

            result.attachEvidence("Get All Rate Plans HEAPTI")

            step("returns HTTP 200 with 20 rate plans") {
                result.response.status.value shouldBe 200
                result.body.ratePlans shouldHaveSize 20
            }

            step("each rate plan belongs to HEAPTI and has required fields") {
                result.body.ratePlans.forEach { plan ->
                    plan.hotelId shouldBe "HEAPTI"
                    plan.ratePlanCode.shouldNotBeNull()
                    plan.primaryDetails?.description?.defaultText.shouldNotBeNull()
                    plan.classifications?.rateCategory.shouldNotBeNull()
                }
            }

            step("response includes expected rate plan codes") {
                val codes = result.body.ratePlans.map { it.ratePlanCode }
                codes shouldContainAll listOf(
                    "FLEXRATE", "SEMIFLEX", "ADVANCE", "STANDARD",
                    "NONFLEX", "NONFLEXD", "HOUSEUSE", "COMPTARY", "ADVNCEBB", "BADUTYBB",
                )
            }
        }
    },
)
```

`GetAllRatePlansForHotelFramti` mirrors this with `Hotels.FRAMTI` and its expected codes.

---

## Error Handling

These are integration tests, not production code. Error handling is limited to:

- If the WireMock stub is not registered, the ohip-adapter-service returns a non-200 response; `result.body` access will throw via `ApiResult.body`, failing the test with a clear message.
- `toApiResult` already handles deserialization errors by returning `successBody = null`.
- `JourneySpec.afterTest` calls `mocks.cleanup()`, removing stubs keyed by `testId` and preventing cross-test contamination even on failure.

---

## Testing Strategy

Property-based testing is **not applicable** for this feature. The tests are integration tests that exercise a fixed request/response cycle against a deterministic WireMock stub. Input variation (different `hotelId` values) is covered by two separate test classes.

The testing strategy is:

- **Example-based integration tests** — two `JourneySpec` classes, one per hotel, each making one live HTTP call through the service stack with a WireMock Opera stub in place.
- **Structural assertions** — verify response status, list size, per-item field presence, and known rate plan codes.
- **Stub isolation** — `TEST_ID_HEADER` matching ensures parallel test runs do not interfere.
- **Cleanup** — `JourneySpec.afterTest` removes stubs registered by each test via `mocks.cleanup()`.
