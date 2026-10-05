# Implementation Plan: Get All Rate Plans for Hotel Integration Test

## Overview

Add integration tests for the `GET /ohip/ratePlans?hotelId={hotelId}` endpoint covering HEAPTI and FRAMTI hotels. The implementation adds or updates integration-test files in `backend/integration-tests-kotlin/src/test/kotlin/uk/co/whitbread/integrationtests/`.

## Tasks

- [ ] 1. Create `RatePlansResponse.kt` model classes
  - [x] 1.1 Create `clients/ohip/model/RatePlansResponse.kt` with five `@Serializable` data classes
    - `RatePlansResponse(ratePlans: List<RatePlan>)`, `RatePlan`, `RatePlanPrimaryDetails`, `RatePlanDescription`, `RatePlanClassifications` — all fields nullable with null defaults except `ratePlans` list
    - _Requirements: 2.1, 2.2, 2.3, 2.4_

- [ ] 2. Create `OperaRatePlansStubs.kt` and extend `OhipApi`
  - [x] 2.1 Create `stubs/opera/OperaRatePlansStubs.kt` with `ratePlans(hotelId, testId)` stub function
    - Match `urlPath = "/rtp/v1/ratePlans"`, `headers["x-hotelid"]`, and `headers[TEST_ID_HEADER]`; return HTTP 200 with static JSON body selected by `hotelId`
    - HEAPTI body: `ratePlanShortInfoList.hasMore=true`, `ratePlanShortInfoList.totalResults=746`, 20 `ratePlanShortInfoList.ratePlanShortInfo` objects (FLEXRATE, SEMIFLEX, ADVANCE, STANDARD, NONFLEX, NONFLEXD, HOUSEUSE, COMPTARY, ADVNCEBB, BADUTYBB, plus 10 more)
    - FRAMTI body: `ratePlanShortInfoList.hasMore=true`, `ratePlanShortInfoList.totalResults=1399`, 20 `ratePlanShortInfoList.ratePlanShortInfo` objects (FLEXRATE, MIFIXB03, MIFLXCOB, MIFXBCPO, MIFXBEDB, SEMIFLEX, ADVANCE, STANDARD, NONFLEX, NONFLEXD, plus 10 more)
    - Store each body as a `private const val` string in the same file
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_
  - [x] 2.2 Extend `clients/ohip/OhipApi.kt` — add `getRatePlans(hotelId, testId): ApiResult<RatePlansResponse>`
    - GET `$baseUrl/ohip/ratePlans`, query param `hotelId`, header `x-amzn-trace-id: testId`
    - _Requirements: 1.1, 1.2, 1.3_

- [ ] 3. Modify `Booking.kt` and `BookingMocks.kt`
  - [x] 3.1 Make `arrival`, `departure`, `roomType`, `adults` nullable with null defaults in `Booking.kt`; guard `init` block with null check
    - _Requirements: 4.1, 5.1_
  - [x] 3.2 Add `Opera.RatePlans` `MockDefinition` to `BookingMocks.kt` and append to `defaults` list; import `ratePlans` from `OperaRatePlansStubs`
    - _Requirements: 4.2, 5.2_

- [ ] 4. Create test journey classes
  - [x] 4.1 Create `journeys/ohipService/GetAllRatePlansForHotelHeapti.kt`
    - `JourneySpec` with `Booking(hotel = Hotels.HEAPTI)`, call `mocks.installFor(booking, testId)`, then call `OhipApi().getRatePlans(booking.hotel.hotelId, testId)` inside a `scenario(...)`
    - Assert: status 200, 20 rate plans, each has `hotelId == "HEAPTI"` and non-null `ratePlanCode`/`primaryDetails.description.defaultText`/`classifications.rateCategory`, codes contain FLEXRATE, SEMIFLEX, ADVANCE, STANDARD, NONFLEX, NONFLEXD, HOUSEUSE, COMPTARY, ADVNCEBB, BADUTYBB
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 6.1, 6.2, 6.3, 6.4_
  - [x] 4.2 Create `journeys/ohipService/GetAllRatePlansForHotelFramti.kt`
    - Mirror of HEAPTI test with `Hotels.FRAMTI` and FRAMTI expected codes: FLEXRATE, MIFIXB03, MIFLXCOB, MIFXBCPO, MIFXBEDB, SEMIFLEX, ADVANCE, STANDARD, NONFLEX, NONFLEXD
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 6.1, 6.2, 6.3, 6.4_

- [ ] 5. Checkpoint — Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP
- No property-based tests are applicable — these are deterministic integration tests against fixed WireMock stubs
- Task 3.1 (`Booking.kt` nullable fields) must land before 4.1/4.2 to allow `Booking(hotel = Hotels.HEAPTI)` construction
- Task 2.1 must land before 3.2 so `ratePlans` import resolves in `BookingMocks`
- The rate-plan journey tests use `JourneySpec`/`scenario(...)`, with the API call inside the scenario and the response verification inside `step(...)` blocks.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "2.1"] },
    { "id": 1, "tasks": ["2.2", "3.1"] },
    { "id": 2, "tasks": ["3.2"] },
    { "id": 3, "tasks": ["4.1", "4.2"] }
  ]
}
```
