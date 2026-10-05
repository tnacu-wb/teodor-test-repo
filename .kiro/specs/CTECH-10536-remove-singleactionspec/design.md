# Design Document: Remove SingleActionSpec and Migrate to JourneySpec

## Overview

The integration-test module exposes two author-facing base specs: `SingleActionSpec` (Kotest `BehaviorSpec`, `When`/`Then`) and `JourneySpec` (Kotest `FeatureSpec`, `scenario`). This work standardises on `JourneySpec` as the single base spec by migrating the 10 remaining `SingleActionSpec` tests, deleting `SingleActionSpec`, and updating the two framework comments and the `docs/README.md` guidance that reference it.

This is a **structural refactor only**. No `Booking` data, mock installation, client call, assertion, or expected status/error code changes. The transformation pattern is the one already proven by CTECH-10368 (`GetFooterBusinessBookerEnGbSpec`).

The implementation touches these files (all test-source paths relative to `backend/integration-tests-kotlin/src/test/kotlin/uk/co/whitbread/integrationtests/`):

| Action | File |
|--------|------|
| Migrate | `journeys/reservation/CreateOnHoldHappyPathSpec.kt` |
| Migrate | `journeys/reservation/SingleRoomWithPackagesSpec.kt` |
| Migrate | `journeys/reservation/SingleRoomSingleOccupancySpec.kt` |
| Migrate | `journeys/reservation/SingleRoomNonrefundNoPackagesSpec.kt` |
| Migrate | `journeys/reservation/MultiRoomReservationSpec.kt` |
| Migrate | `journeys/reservation/CreateOnHoldInvalidRateInfoSpec.kt` |
| Migrate | `journeys/reservation/CreateOnHoldSkipRateInfoSpec.kt` |
| Migrate | `journeys/reservation/ReservationErrorMissingRateInfoSpec.kt` |
| Migrate | `journeys/reservation/ReservationErrorOpera500Spec.kt` |
| Migrate | `journeys/reservation/ReservationErrorUnknownHotelSpec.kt` |
| Delete | `testkit/SingleActionSpec.kt` |
| Modify (comment) | `framework/config/IntegrationTestConfig.kt` |
| Modify (comment) | `framework/wiremock/StubLifecycleExtension.kt` |
| Modify (docs) | `docs/README.md` (relative to module root) |

---

## Architecture

The two base specs are author-equivalent except for their Kotest parent and the test-structure DSL. Confirmed by reading both classes:

| Aspect | `SingleActionSpec` | `JourneySpec` |
|--------|--------------------|---------------|
| Kotest parent | `BehaviorSpec()` | `FeatureSpec()` |
| Root container | `Given(description)` | `feature(description)` |
| Inner structure | `When(name) { Then(name) { ... } }` | `scenario(name) { ... }` |
| `testId` generation | description → slug + 6-char suffix | identical |
| `mocks`, `expect`, `attachEvidence` | identical | identical |
| `afterTest { mocks.cleanup() }` | identical | identical |

Because every author-facing member is identical, migration is a parent-class swap plus a DSL-shape change. No testkit, mock, stub, or client code changes.

### Transformation rule

```
SingleActionSpec                          JourneySpec
─────────────────                         ────────────
class X : SingleActionSpec(          →    class X : JourneySpec(
    "<description>", booking, {               "<description>", booking, {
        mocks.installFor(...)                     mocks.installFor(...)        // unchanged
        When("<when>") {                 →        scenario("<when>") {        // When → scenario
            <when-scope code>                         <when-scope code>        // unchanged, same order
            Then("<then>") {             →            // Then wrapper removed; body inlined
                result.attachEvidence()                   result.attachEvidence()
                expect("...") { ... }                     expect("...") { ... }
            }
        }
    })                                        })
```

Rules:
1. Replace `import ...testkit.SingleActionSpec` with `import ...testkit.JourneySpec`; change superclass `SingleActionSpec(` → `JourneySpec(`.
2. Each `When(name) { ... }` becomes `scenario(name) { ... }`. The scenario name is the existing `When` string.
3. The `Then(name) { ... }` wrapper is removed and its body inlined into the `scenario`. The `Then` descriptive name is dropped (its intent is already carried by the `When`/scenario name and the inner `expect(...)` labels), matching the CTECH-10368 precedent.
4. Code declared at `When` scope before the `Then` (e.g. `val result`, `val response`, `val body`) stays in place, in the same order, now at `scenario` scope.
5. Everything else — `Booking` literal, `mocks.installFor`/`installWith*`, request construction, `expect(...)` blocks, `attachEvidence(...)` — is byte-for-byte unchanged.

All 10 specs have exactly one `When` containing exactly one `Then`, so each migrates to exactly one `scenario`. Test-case count is therefore preserved.

---

## Components and Interfaces

### Migrated specs (no interface change)

The specs keep their file names, class names, package (`journeys.reservation`), and constructor arguments. Only the superclass and inner DSL change.

### `SingleActionSpec.kt` deletion

Deleted only after all 10 migrations compile. Post-deletion, `grep -r "SingleActionSpec" src` must return no source-type references (only the comment updates below, which remove the name entirely).

### Framework comment updates

Both are documentation-only comments naming the base classes:

- `IntegrationTestConfig.kt:23` — `// per-test stub cleanup in each spec base class (SingleActionSpec, JourneySpec), so`
- `StubLifecycleExtension.kt:12` — `* (SingleActionSpec, JourneySpec), which removes only the stubs tagged with the testIds`

Update each to reference only `JourneySpec` (e.g. `(JourneySpec)`), preserving surrounding wording.

### `docs/README.md` update

The "Base Spec Types" section currently documents two base classes ("There are two base classes for test authors") with a `SingleActionSpec` code example. Update to:
- State there is one base spec, `JourneySpec`.
- Remove the `SingleActionSpec` Kotlin example block.
- Keep the existing `JourneySpec` example and the shared-members description (`booking`, `testId`, `mocks`, `expect(name)`, `attachEvidence`).

---

## Data Models

No data-model changes. `Booking` and all `testkit/model` types are untouched.

---

## Worked Example

`ReservationErrorUnknownHotelSpec` before:

```kotlin
class ReservationErrorUnknownHotelSpec : SingleActionSpec(
    "a completely unknown hotel with no mocks at all",
    Booking( /* ...unchanged... */ ),
    {
        val reservationApi = ReservationApi()
        mocks.installWithoutHotelStubs(booking, testId)

        When("creating a reservation for an unknown hotel") {
            val result = reservationApi.createOnHoldReservation(booking, testId)

            Then("the request fails because no Opera stubs exist for the hotel") {
                result.attachEvidence()
                expect("returns error status") { result.isSuccess shouldBe false }
                expect("status is 500") { result.response.status.value shouldBe 500 }
            }
        }
    },
)
```

After:

```kotlin
class ReservationErrorUnknownHotelSpec : JourneySpec(
    "a completely unknown hotel with no mocks at all",
    Booking( /* ...unchanged... */ ),
    {
        val reservationApi = ReservationApi()
        mocks.installWithoutHotelStubs(booking, testId)

        scenario("creating a reservation for an unknown hotel") {
            val result = reservationApi.createOnHoldReservation(booking, testId)
            result.attachEvidence()
            expect("returns error status") { result.isSuccess shouldBe false }
            expect("status is 500") { result.response.status.value shouldBe 500 }
        }
    },
)
```

Only the import, the superclass, the `When` → `scenario` keyword, and the removal of the `Then` wrapper change.

### Per-spec notes

- **MultiRoomReservationSpec** — declares local helpers (`arrival`, `departure`, `familyBooking`, `reservationApi`) and a large `OnHoldRequest` at `When` scope, then a multi-room `installFor(listOf(...))`. All declarations move into the `scenario` body in the same order; the request literal is unchanged.
- **CreateOnHoldHappyPathSpec** — has a follow-up `BasketApi().getBasket(...)` call after the create assertions, still inside the original `Then`. It stays inline in the `scenario`, after the create `expect` blocks, unchanged.
- **CreateOnHoldInvalidRateInfoSpec / CreateOnHoldSkipRateInfoSpec / ReservationErrorMissingRateInfoSpec** — error specs asserting `errCode 944` and the price-breakdown debug message; assertions copied verbatim.
- **ReservationErrorOpera500Spec** — uses `installWithReservationFailure(...)`; installer call unchanged.
- The remaining happy-path specs (`SingleRoomWithPackagesSpec`, `SingleRoomSingleOccupancySpec`, `SingleRoomNonrefundNoPackagesSpec`) follow the plain worked example above.

---

## Error Handling

Not applicable as runtime behaviour — this is a test refactor. The relevant failure modes are build-time:

| Scenario | Detection | Mitigation |
|----------|-----------|------------|
| A `Then` body references a symbol that was scoped to `When` | Kotlin compile error | Step 4 preserves declaration order; all symbols stay in the single `scenario` scope |
| `SingleActionSpec` deleted while a spec still extends it | Unresolved reference at compile | Delete only after all 10 migrations compile (Requirement 2.3) |
| Accidental assertion/data change during edit | Diff review + identical test result | Keep edits limited to import, superclass, and DSL keywords |

---

## Testing Strategy

No new tests are added; the goal is behaviour-preserving migration.

- **Compilation** — `./gradlew compileTestKotlin` (or `test`) must succeed with no unresolved `SingleActionSpec` reference.
- **Behaviour parity** — run `./gradlew test` against the running integration environment and confirm each migrated spec yields the same result (same status codes, error codes, evidence) as before. The 10 specs map 1:1 to 10 scenarios, so the executable test count is unchanged.
- **Reference sweep** — `grep -r "SingleActionSpec" backend/integration-tests-kotlin/src backend/integration-tests-kotlin/docs` returns nothing after the work.
- **Isolation unchanged** — `JourneySpec.afterTest` calls the same `mocks.cleanup()` keyed by `testId`, so parallel-run isolation is identical to before.

---

## Open Questions

None. Spec naming is resolved (keep current names — see requirements). Whether to run the full suite against a live environment as part of this change, versus relying on compilation plus diff review, is an execution choice left to the implementer.
