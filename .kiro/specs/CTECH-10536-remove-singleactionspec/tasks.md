# Implementation Plan: Remove SingleActionSpec and Migrate to JourneySpec

## Overview

Migrate the 10 remaining `SingleActionSpec` reservation tests to `JourneySpec`, delete the `SingleActionSpec` base class, and update the two framework comments and `docs/README.md` that reference it. This is a behaviour-preserving structural refactor. All test-source paths are relative to `backend/integration-tests-kotlin/src/test/kotlin/uk/co/whitbread/integrationtests/`.

Per-spec transformation (applies to every migration task in Task 1):
- Replace `import ...testkit.SingleActionSpec` with `import ...testkit.JourneySpec`; change superclass `SingleActionSpec(` → `JourneySpec(`.
- Convert the `When(name) { ... }` block to `scenario(name) { ... }` (keep the name).
- Remove the inner `Then(name) { ... }` wrapper and inline its body into the `scenario`, preserving statement order.
- Leave `Booking` data, `mocks.installFor`/`installWith*`, request construction, every `expect(...)`, and every `attachEvidence(...)` unchanged.

## Tasks

- [x] 1. Migrate the 10 reservation specs to `JourneySpec`
  - [x] 1.1 Migrate `journeys/reservation/ReservationErrorUnknownHotelSpec.kt`
    - Simplest case (no follow-up calls); use as the reference migration. Keeps `installWithoutHotelStubs`, asserts `isSuccess == false` and status 500
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6_
  - [x] 1.2 Migrate `journeys/reservation/SingleRoomSingleOccupancySpec.kt`
    - Happy path, 1 adult; `installFor`, asserts 201 + basket reference + one reservation
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6_
  - [x] 1.3 Migrate `journeys/reservation/SingleRoomNonrefundNoPackagesSpec.kt`
    - Happy path, NONREFUND rate, no packages; asserts 201 + basket reference + one reservation
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6_
  - [x] 1.4 Migrate `journeys/reservation/SingleRoomWithPackagesSpec.kt`
    - Happy path with packages; includes a follow-up `basketApi.getBasket(...)` call and assertions that stay inline in the scenario
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6_
  - [x] 1.5 Migrate `journeys/reservation/CreateOnHoldHappyPathSpec.kt`
    - Happy path; `When`-scope `val result/response/body` declarations and the trailing `BasketApi().getBasket(...)` move into the scenario in the same order
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6_
  - [x] 1.6 Migrate `journeys/reservation/MultiRoomReservationSpec.kt`
    - Most complex: local `arrival`/`departure`/`familyBooking`/`reservationApi` declarations and a large `OnHoldRequest` literal at `When` scope move into the scenario unchanged; keeps `installFor(listOf(booking, familyBooking), testId)`
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, (edge cases)_
  - [x] 1.7 Migrate `journeys/reservation/CreateOnHoldInvalidRateInfoSpec.kt`
    - Error spec; keeps `installWithInvalidRateInfo`, asserts 500 + errCode 944 + price-breakdown debug message + `internal.server.exception`
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, (edge cases)_
  - [x] 1.8 Migrate `journeys/reservation/CreateOnHoldSkipRateInfoSpec.kt`
    - Error spec; `installFor` with empty `availableRates`, asserts 500 + errCode 944 + debug message + `internal.server.exception`
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6_
  - [x] 1.9 Migrate `journeys/reservation/ReservationErrorMissingRateInfoSpec.kt`
    - Error spec; `installFor` with empty `availableRates`, asserts 500 + errCode 944 + debug message
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6_
  - [x] 1.10 Migrate `journeys/reservation/ReservationErrorOpera500Spec.kt`
    - Error spec; keeps `installWithReservationFailure(...)`, asserts 500 + non-null error body
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, (edge cases)_

- [x] 2. Verify migration compiles before deleting the base class
  - [x] 2.1 Run `./gradlew compileTestKotlin` from `backend/integration-tests-kotlin`
    - Confirm no compile errors and no unresolved-symbol errors (catches any `When`-scope symbol that failed to move into the `scenario`)
    - _Requirements: 2.3, 4.1_

- [x] 3. Delete the `SingleActionSpec` base class
  - [x] 3.1 Delete `testkit/SingleActionSpec.kt`
    - Only after Task 2.1 passes. Then re-run `./gradlew compileTestKotlin` to confirm nothing else referenced the type
    - _Requirements: 2.1, 2.2_

- [x] 4. Update framework comments and documentation
  - [x] 4.1 Update the `SingleActionSpec` comment in `framework/config/IntegrationTestConfig.kt` (line ~23) to reference only `JourneySpec`
    - _Requirements: 3.1_
  - [x] 4.2 Update the `SingleActionSpec` comment in `framework/wiremock/StubLifecycleExtension.kt` (line ~12) to reference only `JourneySpec`
    - _Requirements: 3.2_
  - [x] 4.3 Update `docs/README.md` "Base Spec Types" section: state there is one base spec (`JourneySpec`), remove the `SingleActionSpec` example, keep the `JourneySpec` example and shared-members description
    - _Requirements: 3.3, 3.4_

- [x] 5. Final verification sweep
  - [x] 5.1 Run `grep -r "SingleActionSpec" backend/integration-tests-kotlin/src backend/integration-tests-kotlin/docs` and confirm zero matches
    - _Requirements: 2.2, 4.1_
  - [x] 5.2 Run `./gradlew test` against the running integration environment; confirm 10 migrated scenarios pass with the same status/error codes as before
    - _Requirements: 4.2, 4.3_

- [x] 6. Checkpoint — Ensure all tests pass and the suite is green; ask the user if questions arise.

## Notes

- Tasks 1.1–1.10 are independent of each other and can be done in any order or in parallel; 1.1 is listed first as the simplest reference case, escalating to the more involved specs (1.5, 1.6).
- Task 3 (delete) MUST follow Task 2 (compile check) so the class is never removed while a spec still extends it (Requirement 2.3).
- Tasks 4.1–4.3 are documentation/comment-only and have no compile dependency on Tasks 1–3, but are sequenced after deletion so the docs match the final state.
- No new tests are added and no assertions change — Task 5.2 verifies parity, it does not add coverage.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8", "1.9", "1.10"] },
    { "id": 1, "tasks": ["2.1"] },
    { "id": 2, "tasks": ["3.1"] },
    { "id": 3, "tasks": ["4.1", "4.2", "4.3"] },
    { "id": 4, "tasks": ["5.1", "5.2"] }
  ]
}
```
