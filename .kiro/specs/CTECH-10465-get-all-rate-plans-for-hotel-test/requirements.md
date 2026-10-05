# Requirements Document

## Introduction

Add a Kotlin integration test for the ohip-adapter-service "Get All Rate Plans for Hotel" endpoint (`GET /ohip/ratePlans?hotelId={hotelId}`). The test verifies that the service correctly fetches and returns rate plans from Opera's paginated `/rtp/v1/ratePlans` endpoint for HEAPTI and FRAMTI hotels.

## Glossary

- **Integration_Test**: A Kotest `JourneySpec` test class that validates end-to-end behavior of the ohip-adapter-service using WireMock stubs for Opera dependencies.
- **OhipApi_Client**: The `OhipApi` class that provides HTTP methods for calling ohip-adapter-service endpoints.
- **WireMock_Stub**: A stub mapping registered with the Opera WireMock instance to simulate Opera API responses.
- **Rate_Plan**: A JSON object containing `ratePlanCode`, `hotelId`, `primaryDetails`, and `classifications` fields returned by the ohip-adapter-service.
- **Opera_Rate_Plans_Response**: The Opera Cloud response shape consumed by the ohip-adapter-service, where rate plans are nested under `ratePlanShortInfoList.ratePlanShortInfo`.
- **Opera_Rate_Plans_Endpoint**: The Opera Cloud API endpoint at `/rtp/v1/ratePlans` that returns paginated rate plan data.
- **Test_ID**: A unique trace correlation identifier passed via the `x-amzn-trace-id` header to isolate test traffic.

## Requirements

### Requirement 1: OhipApi Client Method

**User Story:** As a test author, I want a `getRatePlans` method on the `OhipApi` client, so that integration tests can call the ohip-adapter-service rate plans endpoint.

#### Acceptance Criteria

1. THE OhipApi_Client SHALL expose a `getRatePlans(hotelId: String, testId: String)` method that returns an `ApiResult<RatePlansResponse>`.
2. WHEN `getRatePlans` is called, THE OhipApi_Client SHALL send a GET request to `{baseUrl}/ohip/ratePlans` with `hotelId` as a query parameter.
3. WHEN `getRatePlans` is called, THE OhipApi_Client SHALL include the Test_ID value in the `x-amzn-trace-id` request header.

### Requirement 2: Rate Plans Response Model

**User Story:** As a test author, I want Kotlin model classes for the rate plans response, so that I can deserialize and assert on the ohip-adapter-service response body.

#### Acceptance Criteria

1. THE Integration_Test project SHALL contain a `RatePlansResponse` data class with a `ratePlans` list field.
2. THE Integration_Test project SHALL contain a `RatePlan` data class with fields: `ratePlanCode` (String), `hotelId` (String), `primaryDetails` (object with nested `description.defaultText`), and `classifications` (object with `rateCategory`, `displaySet`, `marketCode`).
3. THE RatePlansResponse and RatePlan model classes SHALL use `kotlinx.serialization.Serializable` annotation for JSON deserialization.
4. THE RatePlan model classes SHALL use nullable types for optional fields (`displaySet`, `marketCode`).

### Requirement 3: Opera Rate Plans WireMock Stub

**User Story:** As a test author, I want a WireMock stub function for the Opera rate plans endpoint, so that the ohip-adapter-service receives realistic paginated responses during tests.

#### Acceptance Criteria

1. THE Integration_Test project SHALL contain a stub function that creates a WireMock_Stub for the Opera_Rate_Plans_Endpoint at path `/rtp/v1/ratePlans`.
2. WHEN the stub is created for HEAPTI, THE WireMock_Stub SHALL return an Opera_Rate_Plans_Response with 20 `ratePlanShortInfoList.ratePlanShortInfo` items, `hasMore=true`, and `totalResults=746`.
3. WHEN the stub is created for FRAMTI, THE WireMock_Stub SHALL return an Opera_Rate_Plans_Response with 20 `ratePlanShortInfoList.ratePlanShortInfo` items, `hasMore=true`, and `totalResults=1399`.
4. THE WireMock_Stub SHALL match requests containing the Test_ID in the `x-amzn-trace-id` header.
5. THE WireMock_Stub SHALL match requests containing an `x-hotelid` header matching the target hotel identifier.

### Requirement 4: HEAPTI Rate Plans Integration Test

**User Story:** As a developer, I want an integration test that verifies rate plans retrieval for HEAPTI hotel, so that I can confirm the ohip-adapter-service correctly proxies Opera rate plan data.

#### Acceptance Criteria

1. THE Integration_Test SHALL extend `JourneySpec` following the existing journey test pattern in the `journeys/ohipService/` package.
2. WHEN the test executes, THE Integration_Test SHALL register a WireMock_Stub for the Opera_Rate_Plans_Endpoint for HEAPTI.
3. WHEN the test calls `getRatePlans` with hotelId `HEAPTI`, THE Integration_Test SHALL verify the HTTP response status is 200.
4. WHEN the test calls `getRatePlans` with hotelId `HEAPTI`, THE Integration_Test SHALL verify the response contains 20 rate plans.
5. WHEN the test calls `getRatePlans` with hotelId `HEAPTI`, THE Integration_Test SHALL verify each Rate_Plan in the response has `hotelId` equal to `HEAPTI`.
6. WHEN the test calls `getRatePlans` with hotelId `HEAPTI`, THE Integration_Test SHALL verify the response contains expected rate plan codes including FLEXRATE, SEMIFLEX, ADVANCE, STANDARD, NONFLEX, NONFLEXD, HOUSEUSE, COMPTARY, ADVNCEBB, and BADUTYBB.

### Requirement 5: FRAMTI Rate Plans Integration Test

**User Story:** As a developer, I want an integration test that verifies rate plans retrieval for FRAMTI hotel, so that I can confirm multi-hotel support works correctly.

#### Acceptance Criteria

1. THE Integration_Test SHALL extend `JourneySpec` following the existing journey test pattern in the `journeys/ohipService/` package.
2. WHEN the test executes, THE Integration_Test SHALL register a WireMock_Stub for the Opera_Rate_Plans_Endpoint for FRAMTI.
3. WHEN the test calls `getRatePlans` with hotelId `FRAMTI`, THE Integration_Test SHALL verify the HTTP response status is 200.
4. WHEN the test calls `getRatePlans` with hotelId `FRAMTI`, THE Integration_Test SHALL verify the response contains 20 rate plans.
5. WHEN the test calls `getRatePlans` with hotelId `FRAMTI`, THE Integration_Test SHALL verify each Rate_Plan in the response has `hotelId` equal to `FRAMTI`.
6. WHEN the test calls `getRatePlans` with hotelId `FRAMTI`, THE Integration_Test SHALL verify the response contains expected rate plan codes including FLEXRATE, MIFIXB03, MIFLXCOB, MIFXBCPO, MIFXBEDB, SEMIFLEX, ADVANCE, STANDARD, NONFLEX, and NONFLEXD.

### Requirement 6: Rate Plan Structure Validation

**User Story:** As a developer, I want the test to validate rate plan object structure, so that I can detect breaking changes in the response schema.

#### Acceptance Criteria

1. WHEN a successful response is returned, THE Integration_Test SHALL verify each Rate_Plan contains a non-null `ratePlanCode` field.
2. WHEN a successful response is returned, THE Integration_Test SHALL verify each Rate_Plan contains a non-null `hotelId` field.
3. WHEN a successful response is returned, THE Integration_Test SHALL verify each Rate_Plan contains a `primaryDetails.description.defaultText` field.
4. WHEN a successful response is returned, THE Integration_Test SHALL verify each Rate_Plan contains a `classifications.rateCategory` field.
