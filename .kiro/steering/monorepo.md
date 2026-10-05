# Monorepo Overview

This is Whitbread's digital monorepo. It contains multiple independent stacks, each with its
own toolchain. This file is **always included** — keep it tiny. Stack- and module-specific
detail lives in path-scoped steering (see "Steering model" below).

## Top-Level Layout

```
digital-monorepo/
├── backend/     # Java / Spring Boot microservices + libs, grouped by squad
├── frontend/    # TypeScript / Next.js web applications
├── graphql/     # TypeScript / Apollo GraphQL subgraph services (federated)
├── mobile/      # Native mobile apps (Android + iOS)
├── qa/          # Playwright end-to-end test automation
├── tools/       # Internal operational tools (own toolchains; not in the Maven reactor)
├── .github/     # CI (path-based change detection + matrix builds)
└── .kiro/       # Steering and spec files
```

- **backend** is a Maven multi-module build organised as `backend/<squad>/{services,libs}/<module>`
  (squads: `meta`, `discover-search`, `book-pay`, `identity`, `arrive-stay-leave`, `manage-modify`).
- Each stack owns its own build tool, language, and conventions — do not assume one stack's
  tooling applies to another. See the matching `stacks/*.md` file.

## CI

Path-based change detection builds only the modules that changed; stacks build independently.
Everything an automated run needs must live in-repo (CLI and GitHub Actions runs have no
editor workspace and no user-level config).

## Steering Model

Steering is centralised under `.kiro/steering/` and layered in three tiers:

- **Tier 1 — this file (`monorepo.md`)**: always included. Universal facts only.
- **Tier 2 — `stacks/<stack>.md`**: loaded via `fileMatch` when a file in that stack is in
  context (e.g. `backend/**`). Holds stack-wide build/lint/test conventions.
- **Tier 3 — `<stack>/<module>/{product,structure,tech}.md`**: loaded via `fileMatch` scoped
  to that module's source path. Holds product, structure, and tech detail for one module.

Only Tier 1 is always on; Tiers 2 and 3 load lazily by path, so a task pulls in exactly the
stacks and modules it touches.

### Adding a new module (any stack)

1. Create `.kiro/steering/<stack>/<module>/` with `product.md`, `structure.md`, `tech.md`.
2. Give each file this frontmatter, scoped to the module's source path:
   ```
   ---
   inclusion: fileMatch
   fileMatchPattern: "<stack>/.../<module>/**"
   ---
   ```
3. Nothing else is required — Tier 1 and the stack's Tier 2 file apply automatically.

New stack? Add one `stacks/<stack>.md` and a `<stack>/` folder. Never leave `inclusion`
blank on Tier 2/3 files — a missing value defaults to always-on and bloats every prompt.
