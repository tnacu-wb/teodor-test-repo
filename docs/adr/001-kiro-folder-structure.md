# ADR-001: Kiro Folder Structure for Squad-Based Monorepo

## Date

2026-06-23

## Context

Our monorepo hosts multiple squads (discover-search, book-pay, manage-modify, etc.), each owning backend services, frontend applications, and QA automation. We need a consistent structure for the `.kiro/` folder so that:

1. Kiro loads only relevant context when working within a specific squad, layer, or service.
2. Specs (feature implementation workflows) are easy to locate and grouped logically.
3. The structure scales as squads and services grow.
4. Different team members (backend devs, frontend devs, QA engineers) get appropriate guidance without noise from unrelated areas.

## Decision

### Steering (`.kiro/steering/`)

We adopt a layered steering model:

```
.kiro/steering/
├── global.md                              ← inclusion: always
├── qa-conventions.md                      ← inclusion: fileMatch, pattern: "**/qa/**"
├── backend-conventions.md                 ← inclusion: fileMatch, pattern: "**/backend/**"
├── frontend-conventions.md                ← inclusion: fileMatch, pattern: "**/frontend/**"
│
├── discover-search-squad/
│   ├── squad.md                           ← inclusion: fileMatch, pattern: "discover-search-squad/**"
│   ├── backend.md                         ← inclusion: fileMatch, pattern: "discover-search-squad/backend/**"
│   ├── frontend.md                        ← inclusion: fileMatch, pattern: "discover-search-squad/frontend/**"
│   └── qa.md                              ← inclusion: fileMatch, pattern: "discover-search-squad/qa/**"
│
├── book-pay-squad/
│   ├── squad.md                           ← inclusion: fileMatch, pattern: "book-pay-squad/**"
│   ├── backend.md                         ← inclusion: fileMatch, pattern: "book-pay-squad/backend/**"
│   ├── frontend.md                        ← inclusion: fileMatch, pattern: "book-pay-squad/frontend/**"
│   └── qa.md                              ← inclusion: fileMatch, pattern: "book-pay-squad/qa/**"
│
└── manage-modify-squad/
    ├── squad.md                           ← inclusion: fileMatch, pattern: "manage-modify-squad/**"
    ├── backend.md                         ← inclusion: fileMatch, pattern: "manage-modify-squad/backend/**"
    ├── frontend.md                        ← inclusion: fileMatch, pattern: "manage-modify-squad/frontend/**"
    └── qa.md                              ← inclusion: fileMatch, pattern: "manage-modify-squad/qa/**"
```

**Inclusion behaviour:**

| Layer | Loads when... |
|-------|---------------|
| `global.md` | Every session (always) |
| `qa-conventions.md` | Any file in a `qa/` folder is opened |
| `backend-conventions.md` | Any file in a `backend/` folder is opened |
| `frontend-conventions.md` | Any file in a `frontend/` folder is opened |
| `<squad>/squad.md` | Any file within that squad's folder is opened |
| `<squad>/backend.md` | A file in that squad's `backend/` subfolder is opened |
| `<squad>/frontend.md` | A file in that squad's `frontend/` subfolder is opened |
| `<squad>/qa.md` | A file in that squad's `qa/` subfolder is opened |

**Steering file content guidelines:**

| File | Contains |
|------|----------|
| `global.md` | Language versions, git workflow, CI commands, PR conventions, shared tooling |
| `qa-conventions.md` | Test labeling (@regression, @smoke, @functional), naming patterns, shared test utilities |
| `backend-conventions.md` | API patterns, error handling, logging, shared libraries |
| `frontend-conventions.md` | Component patterns, state management, styling, accessibility |
| `<squad>/squad.md` | Domain terms, owned services/APIs, integrations, business context |
| `<squad>/backend.md` | Squad-specific backend frameworks, DB schemas, service patterns |
| `<squad>/frontend.md` | Squad-specific UI libraries, routing, component conventions |
| `<squad>/qa.md` | Squad-specific test environments, mock data, runner config |

### Specs (`.kiro/specs/`)

Separate specs per layer, grouped by squad and feature.

```
.kiro/specs/
├── discover-search-squad/
│   └── hotel-search-filters/
│       ├── hotel-search-filters-backend/
│       │   ├── requirements.md
│       │   ├── design.md
│       │   └── tasks.md
│       ├── hotel-search-filters-frontend/
│       │   ├── requirements.md
│       │   ├── design.md
│       │   └── tasks.md
│       └── hotel-search-filters-qa/
│           ├── requirements.md
│           ├── design.md
│           └── tasks.md
│
├── book-pay-squad/
│   └── payment-retry/
│       ├── payment-retry-backend/
│       │   ├── requirements.md
│       │   ├── design.md
│       │   └── tasks.md
│       └── payment-retry-qa/
│           ├── requirements.md
│           ├── design.md
│           └── tasks.md
│
└── manage-modify-squad/
    └── cancel-booking/
        └── cancel-booking-frontend/
            ├── requirements.md
            ├── design.md
            └── tasks.md
```

**Naming convention:** `<feature-name>-<layer>` where layer is `backend`, `frontend`, or `qa`.

**Hierarchy:** squad → feature → layer-spec. Intermediate folders (squad, feature) are purely organizational. Kiro identifies a spec by the leaf folder containing the three standard files.

**Not every feature needs all three layer-specs.** A backend-only API change only needs one spec. Create specs for the layers that are actually involved.

## Consequences

### Positive

- **Scoped context loading** — developers only get steering relevant to their current work, reducing noise and improving Kiro's response quality.
- **Scales horizontally** — adding a new squad or service follows the same pattern with no restructuring needed.
- **Clear ownership** — spec and steering locations map directly to squad/layer ownership, making it obvious who maintains what.
- **Independent work streams** — separate specs per layer allow backend, frontend, and QA to work in parallel without blocking each other.

### Negative

- **More files to maintain** — each squad needs 4 steering files (squad + 3 layers). This is manageable but requires discipline when conventions change.
- **Spec naming requires consistency** — teams must follow the `<feature>-<layer>` suffix convention; otherwise the grouping benefit is lost.
- **Intermediate folders add depth** — the specs path is 4 levels deep (specs/squad/feature/layer-spec). This is acceptable for organization but can feel verbose.

### Mitigations

- Document this ADR in the repo so new team members understand the structure.
- Add a brief note in `global.md` referencing this ADR for discoverability.
- Review steering files quarterly to prune stale content.
