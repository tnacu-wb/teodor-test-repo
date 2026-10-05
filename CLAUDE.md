# Monorepo Overview

This is Whitbread's digital monorepo. It contains multiple independent stacks, each with its
own toolchain. This file is **always loaded** — keep it tiny. Stack- and module-specific
detail lives in path-scoped steering under `.kiro/steering/` (see "Steering model" below).

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
├── .kiro/       # Steering and spec files (source of truth, shared with the Kiro harness)
└── .claude/     # Claude Code config: agents, hooks, commands
```

- **backend** is a Maven multi-module build organised as `backend/<squad>/{services,libs}/<module>`
  (squads: `meta`, `discover-search`, `book-pay`, `identity`, `arrive-stay-leave`, `manage-modify`).
- Each stack owns its own build tool, language, and conventions — do not assume one stack's
  tooling applies to another. See the matching `.kiro/steering/stacks/*.md` file.

## CI

Path-based change detection builds only the modules that changed; stacks build independently.
Everything an automated run needs must live in-repo (CLI and GitHub Actions runs have no
editor workspace and no user-level config).

## Steering Model

Steering lives under `.kiro/steering/` and is layered in three tiers. `.kiro/` remains the
single source of truth — it is shared with the Kiro harness, so **never fork or duplicate a
steering file into `.claude/`**. Read the `.kiro` file directly.

- **Tier 1 — this file**: always loaded. Universal facts only. Mirrors
  `.kiro/steering/monorepo.md`; keep the two in sync when either changes.
- **Tier 2 — `.kiro/steering/stacks/<stack>.md`**: stack-wide build/lint/test conventions.
- **Tier 3 — `.kiro/steering/<stack>/<module>/{product,structure,tech}.md`**: product,
  structure, and tech detail for one module.

### Which steering to read (Tier 2/3 routing)

Kiro loads Tiers 2/3 automatically via `fileMatch` frontmatter. Claude Code has no equivalent,
so **read them yourself before working on files under these paths**:

| Working on | Tier 2 (stack) | Tier 3 (module) |
|---|---|---|
| `backend/**` | `.kiro/steering/stacks/backend.md` | `.kiro/steering/backend/<module>/{product,structure,tech}.md` |
| `frontend/**` | `.kiro/steering/stacks/frontend.md` | `.kiro/steering/frontend/<module>/{product,structure,tech}.md` |
| `graphql/**` | `.kiro/steering/stacks/graphql.md` | `.kiro/steering/graphql/opera-apollo-subgraphs/{product,structure,tech}.md` |
| `qa/**` | `.kiro/steering/stacks/qa.md` | `.kiro/steering/qa/{product,structure,tech}.md` plus `appscontext.md`, `frontendcontext.md` |
| `mobile/*-android/**` | `.kiro/steering/stacks/mobile-android.md` | `.kiro/steering/mobile/premier-inn-android/{product,structure,tech}.md` |
| `mobile/*-ios/**` | `.kiro/steering/stacks/mobile-ios.md` | `.kiro/steering/mobile/premier-inn-ios/{product,structure,tech}.md` |
| `tools/**` | `.kiro/steering/stacks/tools.md` | `.kiro/steering/tools/<lang>/<module>/{product,structure,tech}.md` |
| `helm/**`, `**/infra/**`, `.github/workflows/**` | `.kiro/steering/stacks/infra.md` | `.kiro/steering/infra/*.md` |

**Tier 3 lookup rule for backend:** the steering folder is keyed by **module name only**, with
the squad segment dropped — `backend/identity/services/account-entity-service/**` is documented
at `.kiro/steering/backend/account-entity-service/`.

Read on demand, not up front: a task should pull in exactly the stacks and modules it touches.

### Steering loaded only when the task calls for it

- `.kiro/steering/designs/**` — design docs; the input to `spec-generator`. Read when asked.
- `.kiro/steering/opera-ohip/*.md` — Opera/OHIP domain and upgrade notes. Read when the task
  involves Opera/OHIP integration.

### Adding a new module (any stack)

1. Create `.kiro/steering/<stack>/<module>/` with `product.md`, `structure.md`, `tech.md`.
2. Give each file this frontmatter, scoped to the module's source path (Kiro needs it; Claude
   uses the routing table above):
   ```
   ---
   inclusion: fileMatch
   fileMatchPattern: "<stack>/.../<module>/**"
   ---
   ```
3. Add a row to the routing table above if you added a whole new **stack**.

New stack? Add one `.kiro/steering/stacks/<stack>.md` and a `.kiro/steering/<stack>/` folder.
Never leave `inclusion` blank on Tier 2/3 files — in Kiro a missing value defaults to always-on
and bloats every prompt.

## Specs

Feature specs live in `.kiro/specs/<team>/<layer>/<TICKET-description>/` as `requirements.md`,
`design.md`, `tasks.md` — team is one of `book-pay`, `arrive-stay-leave`, `discover-search`,
`identity`; layer is one of `frontend`, `backend`, `apps`. Cross-cutting specs go in
`.kiro/specs/<namespace>/<TICKET-description>/`, namespace one of `CORE`, `QA`, `monorepo`. Generate them with the
`spec-generator` agent (or `/spec`), never by hand.

Spec and design files use Kiro's `#[[file:...]]` navigable-reference syntax. Claude Code does
not resolve it, but the files are shared with Kiro — preserve the syntax when editing.

## Claude Code Setup

- **Agents** (`.claude/agents/`): `code-reviewer` (read-only review, both stacks),
  `spec-generator` (writes specs; needs the `atlassian` MCP server for Jira).
- **Commands** (`.claude/commands/`): `/spec` — explicit entry point to `spec-generator`.
- **Hooks** (`.claude/hooks/`): `route-spec-creation.sh` runs on every prompt and routes
  spec-creation requests to `spec-generator`. Ported from `.kiro/hooks/`.
- **MCP** (`.mcp.json`, repo root): `github`, `figma`, `figma-remote`, `atlassian`, `lucid`,
  `lambdatest`, `unleash`. The stdio servers need these env vars exported:
  `GITHUB_PERSONAL_ACCESS_TOKEN`, `FIGMA_ACCESS_TOKEN`, `UNLEASH_BASE_URL`, `UNLEASH_PAT`,
  `UNLEASH_DEFAULT_PROJECT`. The HTTP servers need `/mcp` auth on first use.
