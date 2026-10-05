---
parent_design: "#[[file:../../../../steering/designs/payments/design-e2e.md]]"
jira: CTECH-11518
---
# Implementation Plan: Hotel Reservation Entity Service Integration

## Overview

Replace the `BasketStubAdapter` and `BasketClient` with a real `ReservationServiceClient` that calls the Hotel Reservation Entity Service at `GET /v1/reservations/basket/{basketReference}`. Update the `Reservation` domain model (replace `baseAmount`+`donationAmount` with `totalCostOfStay`, set `refno` to `bookingReference`). Update the Temporal workflow to store `bookingReference` in workflow state and pass it as `refno` to all Datatrans calls. Remove all stub/profile-gated basket adapters.

## Tasks

- [x] 1. Update Domain Model and Port Interface
  - [x] 1.1 Update the `Reservation` domain record
    - Modify `domain/model/Reservation.java`
    - Replace fields `baseAmount` (BigDecimal) and `donationAmount` (BigDecimal) with `totalCostOfStay` (BigDecimal)
    - Remove `paymentCompleted` boolean field
    - Ensure `refno` field remains (will be set to `bookingReference` from reservation response)
    - Final record fields: `basketId`, `hotelId`, `totalCostOfStay`, `currencyCode`, `bookingReference`, `refno`
    - _Requirements: 1.6, 4.1, 7.4_

  - [x] 1.2 Update `BasketOutPort` interface
    - Remove the `getBasket(String basketId)` method (no longer needed)
    - Keep `getReservation(String basketId)` returning updated `Reservation`
    - Update Javadoc to reference Hotel Reservation Entity Service
    - _Requirements: 1.1, 7.3_

  - [x] 1.3 Update `AmountCalculator`
    - Remove `calculateTotal(BigDecimal baseAmount, BigDecimal donationAmount, String currencyCode)` method
    - Keep `toMinorUnits(BigDecimal amount, String currencyCode)` — this is now the only method needed
    - Update callers to use `toMinorUnits(reservation.totalCostOfStay(), reservation.currencyCode())`
    - _Requirements: 4.3_

- [x] 2. Response DTOs and Mapper
  - [x] 2.1 Create reservation response DTOs
    - Create `infrastructure/rest/client/reservation/dto/ReservationByBasketResponse.java` record with key fields: `reservationByIdList` (List), `bookingReference`, `basketReference`, `hotelId`, `currencyCode`, `totalCost`, `basketStatus`, `paymentOption`
    - Create `infrastructure/rest/client/reservation/dto/ReservationByIdDto.java` record with fields: `reservationId`, `rateInfo`, `reservationStatus`
    - Create `infrastructure/rest/client/reservation/dto/RateInfoDto.java` record with field: `summary`
    - Create `infrastructure/rest/client/reservation/dto/RateInfoSummaryDto.java` record with fields: `totalCostOfStay`, `currencyCode`, `gross`, `net`, `deposit`, `outStandingCostOfStay`, `guestPay`
    - Use `@JsonIgnoreProperties(ignoreUnknown = true)` on all DTOs to handle extra fields gracefully
    - _Requirements: 1.4, 1.5, 1.6_

  - [x] 2.2 Create `ReservationResponseMapper`
    - Create `infrastructure/rest/client/reservation/ReservationResponseMapper.java` as a MapStruct `@Mapper(componentModel = "spring")`
    - Implement `Reservation toDomain(String basketId, ReservationByBasketResponse response)` default method
    - Extract first item from `reservationByIdList`; throw `BasketNotFoundException` if list is null/empty
    - Extract `totalCostOfStay` from `firstReservation.rateInfo().summary().totalCostOfStay()`
    - Extract `currencyCode` from `firstReservation.rateInfo().summary().currencyCode()`
    - Set `hotelId` from `response.hotelId()`
    - Set `bookingReference` and `refno` both to `response.bookingReference()`
    - _Requirements: 1.3, 1.4, 1.5, 1.6, 3.1_

- [x] 3. Configuration
  - [x] 3.1 Create `ReservationProperties` configuration class
    - Create `infrastructure/config/ReservationProperties.java`
    - Annotate with `@Data`, `@Configuration`, `@ConfigurationProperties(prefix = "integrations.reservation")`
    - Fields: `host` (String, default `http://localhost:9103`), `reservationEndpoint` (String, default `/v1/reservations/basket/{basketReference}`), `connectTimeout` (Duration, default 5s), `readTimeout` (Duration, default 10s)
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

  - [x] 3.2 Create `ReservationWebClientConfig`
    - Create `infrastructure/config/ReservationWebClientConfig.java`
    - Annotate with `@Configuration` and `@RequiredArgsConstructor`
    - Inject `ReservationProperties`
    - Create `@Bean("reservationWebClient") WebClient` with base URL from `properties.getHost()`, connect timeout and response timeout from properties
    - Follow same pattern as existing Datatrans WebClient configuration
    - _Requirements: 5.1, 5.4, 5.5_

  - [x] 3.3 Update `application.yml`
    - Add `integrations.reservation` section with `host`, `reservationEndpoint`, `connect-timeout`, `read-timeout`
    - Remove `integrations.basket` section (no longer needed)
    - Default host: `http://${RESERVATION_HOST:localhost:9103}`
    - _Requirements: 5.1, 5.2, 5.3_

- [x] 4. REST Client Implementation
  - [x] 4.1 Create `ReservationServiceClient`
    - Create `infrastructure/rest/client/reservation/ReservationServiceClient.java`
    - Annotate with `@Slf4j`, `@Component`, `@RequiredArgsConstructor`
    - Inject `@Qualifier("reservationWebClient") WebClient`, `ReservationProperties`, `ReservationResponseMapper`
    - Implement `BasketOutPort.getReservation(String basketId)`:
      - Validate basketId is not null/blank → throw `BasketNotFoundException`
      - Call `GET {reservationEndpoint}` with basketId substituted for `{basketReference}`
      - Set `Accept: application/json` header
      - Map 404 → `BasketNotFoundException`
      - Map 5xx → `ServiceUnavailableException`
      - Map connection errors (`WebClientRequestException`) → `ServiceUnavailableException`
      - Parse response body to `ReservationByBasketResponse`
      - Call `mapper.toDomain(basketId, response)` to get `Reservation`
      - Validate `bookingReference` not null/blank → throw `DatatransGatewayException` if missing
      - Validate `totalCostOfStay` not null → throw `DatatransGatewayException` if missing
      - Validate `currencyCode` not null/blank → throw `DatatransGatewayException` if missing
      - Log basketId, bookingReference, hotelId, amount, currency (no PII)
      - Return `Reservation`
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 2.1, 2.2, 2.3, 3.3, 4.4, 6.1, 6.2, 6.3, 6.4_

- [x] 5. Remove Old Adapters
  - [x] 5.1 Delete `BasketStubAdapter`
    - Delete `infrastructure/rest/client/basket/BasketStubAdapter.java`
    - _Requirements: 7.1, 7.2_

  - [x] 5.2 Delete `BasketClient`
    - Delete `infrastructure/rest/client/basket/BasketClient.java`
    - _Requirements: 7.1, 7.2_

  - [x] 5.3 Remove the `basket` package directory
    - Delete `infrastructure/rest/client/basket/` directory if empty after removing the two files
    - _Requirements: 7.1_

- [x] 6. Update Temporal Workflow
  - [x] 6.1 Update `PaymentWorkflowImpl` state and signal handler
    - Add `private String bookingReference` field to workflow state
    - Add `private String transactionId` field to workflow state (if not already present)
    - In `initSecureFields` signal handler:
      - After `getReservation`: store `reservation.bookingReference()` in `this.bookingReference`
      - Replace amount calculation: `calc.toMinorUnits(reservation.totalCostOfStay(), reservation.currencyCode())`
      - Remove `reservation.paymentCompleted()` check (field no longer exists)
      - Remove `reservation.bookingReference() != null` rejection check (bookingReference is now always expected and used as refno)
      - Pass `reservation.refno()` (= bookingReference) to Datatrans request
      - Store returned `transactionId` in `this.transactionId`
    - In authorize handler (if exists): use `this.bookingReference` as refno
    - In settle handler (if exists): use `this.bookingReference` as refno
    - _Requirements: 3.1, 3.2, 4.1, 4.3, 7.4, 7.5_

  - [x] 6.2 Update `PaymentActivitiesImpl`
    - Ensure `getReservation` activity delegates to `BasketOutPort.getReservation(basketId)`
    - Remove any reference to `BasketOutPort.getBasket()` if present
    - No structural change needed — the port abstraction handles the switch
    - _Requirements: 1.1, 7.3_

  - [x] 6.3 Update Datatrans request construction
    - In `DatatransSecureFieldsRequest`: ensure `refno` field is available (add if not present) and set to `bookingReference`
    - In `DatatransMobileSdkRequest`: set `refno` to `bookingReference`
    - In `authorizeTransaction` call: pass `bookingReference` as `refno` parameter
    - In `settleTransaction` call: pass stored `bookingReference` from workflow state
    - _Requirements: 3.1, 3.2_

- [x] 7. Unit Tests
  - [x] 7.1 Write `ReservationServiceClientTest`
    - Use MockWebServer to mock the Hotel Reservation Entity Service
    - Test happy path: valid response → correct `Reservation` domain model with bookingReference as refno
    - Test 404 response → `BasketNotFoundException`
    - Test 500 response → `ServiceUnavailableException`
    - Test connection timeout → `ServiceUnavailableException`
    - Test empty `reservationByIdList` → `BasketNotFoundException`
    - Test missing `bookingReference` → `DatatransGatewayException`
    - Test missing `totalCostOfStay` → `DatatransGatewayException`
    - Test missing `currencyCode` → `DatatransGatewayException`
    - Test null/blank basketId input → `BasketNotFoundException`
    - Verify `Accept: application/json` header is sent
    - Verify path parameter substitution: `/v1/reservations/basket/{basketId}`
    - _Requirements: 1.1, 1.2, 2.1, 2.2, 2.3, 3.3, 4.4, 6.1, 6.2_

  - [x] 7.2 Write `ReservationResponseMapperTest`
    - Test successful mapping from full response → Reservation domain model
    - Test `hotelId` extracted from top-level response
    - Test `bookingReference` and `refno` both set to response's `bookingReference`
    - Test `totalCostOfStay` extracted from first reservation's `rateInfo.summary.totalCostOfStay`
    - Test `currencyCode` extracted from first reservation's `rateInfo.summary.currencyCode`
    - Test empty `reservationByIdList` → `BasketNotFoundException`
    - Test null `reservationByIdList` → `BasketNotFoundException`
    - _Requirements: 1.3, 1.4, 1.5, 1.6_

  - [x] 7.3 Update `PaymentWorkflowImplTest`
    - Update mock `Reservation` construction to use new record fields (`totalCostOfStay` instead of `baseAmount`+`donationAmount`, no `paymentCompleted`)
    - Verify workflow stores `bookingReference` in state
    - Verify `refno` = `bookingReference` is passed to Datatrans activity mocks
    - Verify `toMinorUnits(totalCostOfStay)` is used for amount calculation
    - Remove tests for `paymentCompleted` boolean (field removed)
    - Remove tests for "bookingReference != null means already confirmed" (no longer applicable)
    - Use Temporal `TestWorkflowExtension` with mocked activities
    - _Requirements: 3.1, 3.2, 4.1, 4.3_

  - [x] 7.4 Update `AmountCalculatorTest`
    - Remove tests for `calculateTotal` method (removed)
    - Verify `toMinorUnits` works correctly with `totalCostOfStay` values (e.g. 89.00 GBP → 8900)
    - Add edge cases: 0.00, large amounts, different currencies (EUR, JPY)
    - _Requirements: 4.3_

  - [x] 7.5 Delete `BasketStubAdapterTest`
    - Delete any test classes referencing `BasketStubAdapter`
    - _Requirements: 7.1_

  - [x] 7.6 Verify unit test coverage ≥ 80%
    - Run `./mvnw jacoco:report -pl book-pay/services/payment-orchestration-service`
    - Verify all new/modified classes have ≥ 80% line coverage: `ReservationServiceClient`, `ReservationResponseMapper`, `PaymentWorkflowImpl`, `AmountCalculator`
    - Add additional test cases if any class falls below 80%
    - _Requirements: all_

## Notes

- The `BasketStubAdapter` and `BasketClient` are deleted entirely — no profile gating, no stubs remain
- The `BasketOutPort` interface name is retained to minimise churn; the sole implementation is now `ReservationServiceClient`
- The `getBasket(String)` method is removed from `BasketOutPort` — only `getReservation(String)` remains
- `bookingReference` from the reservation response serves as `refno` for Datatrans — it is always expected to be present in the response. If missing, the service returns 502 GATEWAY_ERROR
- The `Reservation` record no longer has `paymentCompleted` — the previous "already paid" / "already confirmed" checks based on this field and bookingReference presence are removed from the workflow
- The Temporal workflow now stores `bookingReference` in its state so authorize and settle steps can use it as `refno` without re-fetching from the reservation service
- All environments (local, dev, QA, perf) will use the real reservation service — configure `RESERVATION_HOST` per environment
- Only unit tests are written for this ticket — no integration tests, contract tests, or property-based tests
- Unit test coverage must be at least **80%** (line coverage) for all new and modified classes — verified via JaCoCo

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "1.3"] },
    { "id": 1, "tasks": ["2.1", "3.1"] },
    { "id": 2, "tasks": ["2.2", "3.2", "3.3"] },
    { "id": 3, "tasks": ["4.1"] },
    { "id": 4, "tasks": ["5.1", "5.2", "5.3"] },
    { "id": 5, "tasks": ["6.1", "6.2", "6.3"] },
    { "id": 6, "tasks": ["7.1", "7.2", "7.3", "7.4", "7.5"] },
    { "id": 7, "tasks": ["7.6"] }
  ]
}
```
