# ADR-001: QA Test Folder Structure for Squad-Based Monorepo

## Status

Draft

## Date

2026-06-23

## Context

The QA automation suite (Playwright + TypeScript) currently keeps tests in a flat
layout under `qa/tests/pi/`. As the monorepo grows into a squad-based organization
(discover-search, book-pay, manage-modify, etc.), we need a test folder structure that:

1. Mirrors the squad-based organization used elsewhere in the repo (`backend/`, `.kiro/specs/`).
2. Maps tests directly to the features they cover, so a feature spec in
   `.kiro/specs/<squad>/<feature>/` has an obvious matching test location.
3. Distinguishes between focused functional tests and broad end-to-end coverage.
4. Preserves the existing body of regression tests during the transition.

### Top-level layout under `qa/tests/`

The first layer splits into one folder per squad, plus a single `general_regression`
folder that holds the existing (pre-restructure) regression tests.

```
qa/tests/
├── general_regression/                 ← legacy regression tests (first layer, not squad-scoped)
├── discover-search-squad/
├── book-pay-squad/
└── manage-modify-squad/
```

### Feature layer (within each squad)

Each squad folder contains a layer of feature folders. **These folder names match
exactly the feature folder names used in `.kiro/specs/<squad>/<feature>/`** so the
spec and its tests stay aligned.

For example, the spec `.kiro/specs/discover-search-squad/discover-search-filters/`
maps to `qa/tests/discover-search-squad/discover-search-filters/`.

### Test-type layer (within each feature)

Each feature folder contains two test-type folders:

| Folder | Purpose |
|--------|---------|
| `FunctionalTests` | Short, focused tests covering a specific part of the feature's functionality. |
| `E2E` | End-to-end tests covering all (or most) logical branches of the feature. These broadly overlap with what the functional tests cover, but exercise complete user journeys. |

### Full structure

```
qa/tests/
├── general_regression/
│   ├── homepage.spec.ts
│   ├── hotel-availability.spec.ts
│   └── ...                                  ← migrated legacy regression tests
│
├── discover-search-squad/
│   └── discover-search-filters/             ← matches .kiro/specs/discover-search-squad/discover-search-filters
│       ├── FunctionalTests/
│       │   ├── filter-by-price.spec.ts
│       │   └── filter-by-rating.spec.ts
│       └── E2E/
│           └── search-filters-journey.spec.ts
│
├── book-pay-squad/
│   └── payment-retry/                       ← matches .kiro/specs/book-pay-squad/payment-retry
│       ├── FunctionalTests/
│       │   └── retry-on-timeout.spec.ts
│       └── E2E/
│           └── payment-retry-journey.spec.ts
│
└── manage-modify-squad/
    └── cancel-booking/                      ← matches .kiro/specs/manage-modify-squad/cancel-booking
        ├── FunctionalTests/
        │   └── cancel-single-room.spec.ts
        └── E2E/
            └── cancel-booking-journey.spec.ts
```

### Relationship between FunctionalTests and E2E

- **FunctionalTests** are narrow: each verifies one specific behaviour or part of the
  feature in isolation. They are fast and pinpoint failures precisely.
- **E2E** tests are broad: each walks through complete logical branches of the feature
  as a user would. They provide confidence that the whole journey works, accepting
  significant overlap with the functional tests.

### Alignment with `.kiro/specs`

The squad → feature folder hierarchy intentionally matches `.kiro/specs/`:

| `.kiro/specs/` | `qa/tests/` |
|----------------|-------------|
| `<squad>/<feature>/<feature>-qa/` (spec workflow) | `<squad>/<feature>/` (actual tests) |

A QA engineer working from a spec under `.kiro/specs/discover-search-squad/discover-search-filters/discover-search-filters-qa/`
writes the resulting tests in `qa/tests/discover-search-squad/discover-search-filters/`.

## Consequences

### Positive

- **Spec-to-test traceability** — feature folder names match across `.kiro/specs/` and
  `qa/tests/`, so finding the tests for a feature is trivial.
- **Clear test intent** — the `FunctionalTests` vs `E2E` split makes the scope of each
  test obvious from its location.
- **Consistent with repo conventions** — the squad-based split aligns QA with `backend/`
  and `.kiro/specs/`.
- **Safe migration path** — `general_regression` preserves existing tests so nothing is
  lost during the move to the new structure.

### Negative

- **Deeper nesting** — tests are now 3+ levels deep (`squad/feature/test-type/`),
  which is more verbose than the current flat layout.
- **Naming discipline required** — feature folder names must be kept in sync with
  `.kiro/specs/`; drift between the two breaks the traceability benefit.
- **Overlap between FunctionalTests and E2E** — intentional, but risks duplicated
  maintenance effort if the relationship is not well understood.
- **Playwright config update needed** — `testDir` and any path-based test filtering may
  need adjustment to work with the new structure.

### Open questions (to resolve before accepting)

1. Should `general_regression` itself eventually be split by squad, or remain a permanent
   home for cross-cutting regression tests?
2. How are shared page objects and fixtures (`qa/src/pages`, `qa/src/fixtures`)
   organized relative to the squad structure — shared globally, or scoped per squad?
3. Should test labels/tags (regression, smoke, functional) be applied via Playwright
   annotations to allow cross-cutting test runs independent of folder location?
