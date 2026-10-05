---
parent_spec: CTECH-13417-node-upgrade-24.21.0
jira: CTECH-13417
requirement: "8.3 (SHOULD-level)"
status: research/documentation only — no code, build, or dependency changes
---

# Node.js 24 Modernisation Findings

> **Scope & disclaimer.** This is a research/documentation deliverable for task **8.3**
> (Requirement 8.3, SHOULD-level). It changes no build config, dependencies, or application
> code. Runtime is Node.js **24.20.0** (bundled **npm 11.19.0**), upgraded from Node 22.23.2
> under CTECH-13417. Every "Node 24 behaviour" claim below is taken from the Node.js / npm
> release documentation and has **not** been benchmarked or otherwise verified inside this
> repo. Treat all "potential future implementation" items as **OPTIONAL** and independent of
> the core upgrade.

## Already folded into the core upgrade (do not re-scope)

These modernisations shipped with the core upgrade (reference PR #232) and are listed only so
they are not mistaken for new opportunities:

- **npm alignment** — Docker global npm pinned to `11.19.0` to match Node 24.20.0's bundle.
- **`@types/node`** — bumped `22.10.2 → 24.13.3` across premier-inn, business-booker, ccui.
- **`v8` cleanup** — unused `v8` npm dep removed; `memory.js` / `memory.test.js` now use
  `import v8 from 'node:v8'` (confirmed in `apps/next-apps/premier-inn/src/pages/api/premierinn/memory.js`).
- **`runtimeEnvVars` refactor** — moved onto Node built-ins: `node:util` **`parseArgs`**
  (`scripts/runtimeEnvVars/index.mjs`) and **`styleText`** (`scripts/runtimeEnvVars/logger.mjs`),
  dropping the `glob` and `yargs` dependencies; marked `type: module`, `engines.node >=24.20.0`.
- **Docker HEALTHCHECK** — already uses global `fetch` + `AbortSignal.timeout(5000)`.

---

## Task 8.3 — Node.js 24 feature evaluation

### A. Node.js 24 runtime / API features relevant to this codebase

| Feature | Node 24 status (per docs) | Already used here? | Potential benefit |
|---|---|---|---|
| **`node:util` `styleText`** | Stable | ✅ Yes — `runtimeEnvVars/logger.mjs` | TTY-aware colour output with no colour dependency; honours `NO_COLOR`/`FORCE_COLOR`. Already realised. |
| **`node:util` `parseArgs`** | Stable | ✅ Yes — `runtimeEnvVars/index.mjs` | Zero-dependency CLI parsing (replaced `yargs`). Already realised. |
| **`node:` built-in prefix** | Convention | ⚠️ Partial — used in `memory.js`, `runtimeEnvVars`; not audited repo-wide | Makes built-in imports unambiguous vs. userland packages; a lightweight lint rule could enforce it consistently. |
| **Global `fetch` + `AbortSignal.timeout`** | Stable | ✅ Yes — Dockerfile HEALTHCHECK | Removes need for an HTTP client in small scripts/probes. Already realised. |
| **`fs.glob` / `fs.globSync` (`node:fs`)** | Stable in 24 | ❌ No (the old `glob` dep was already dropped) | Built-in globbing for any *new* repo scripts; avoids re-adding a `glob`-style dependency. |
| **Built-in test runner (`node:test`) + `node --test`** | Stable | ❌ No — repo standardises on Jest | Useful only for standalone `scripts/` utilities where spinning up Jest is overkill. Not a fit for app/component tests. |
| **Updated V8 (13.6 line) + Maglev/JIT improvements** | Shipped in 24 | n/a (transparent) | General execution/startup gains for build tooling and SSR at runtime — free, no code change. Magnitude unverified here. |
| **Newer JS built-ins** (`RegExp.escape`, `Float16Array`, `Error.isError`, `Array.fromAsync`, explicit resource management `using`/`await using`) | Available via V8 in 24 | ❌ No | Nice-to-have language ergonomics; `using` could simplify resource cleanup in scripts. TS `target: es5` in apps limits downlevel usefulness in app code. |
| **Permission model (`--permission`, stabilised name)** | Stable flag in 24 | ❌ No | Could sandbox untrusted build/codegen steps in CI. Non-trivial to adopt; treat as security hardening, not DX. |
| **`require(esm)` (sync ESM from CJS)** | Enabled by default in 24 | ❌ No (scripts already ESM `.mjs`) | Eases incremental ESM adoption if any CJS tooling needs to pull in ESM-only modules later. |
| **Stable `WebSocket` client global / Undici refresh** | Stable in 24 | ❌ No (app uses `graphql-ws`) | Only relevant for small scripts needing a WS client without a dependency. |

**Build-performance note.** The clearest build-time wins from Node 24 are the *transparent*
ones (V8/JIT refresh, faster startup) that require no code change and are already in effect
now that CI, Docker, and local `.nvmrc` are on 24.20.0. Any concrete build-time delta should
be measured against the Node 22 baseline mentioned in the design doc's "Performance Baseline"
section before claiming a benefit.

### B. npm 11.x features (bundled npm 11.19.0)

> **Applicability caveat.** This monorepo standardises on **Yarn v1 (classic) workspaces**;
> npm is not the package manager. npm 11 therefore matters mainly for the **Docker global npm
> pin** and any incidental `npx`/`npm exec` usage — not day-to-day dependency management. The
> DX upside is consequently limited.

Documented npm 11.x changes worth being aware of:

- **Higher engine floor** — npm 11 itself requires modern Node (Node 24 satisfies it); keeps the
  toolchain from silently running on unsupported Node.
- **Faster installs / reduced overhead** vs npm 10 (general performance work).
- **`npm audit signatures`** and supply-chain/SBOM (`npm sbom`) improvements — relevant if a
  future security task wants provenance checks, but would run alongside, not replace, Yarn.
- **Stricter default behaviours** (engine checks, some flags now error rather than warn) —
  worth noting so the Docker global-npm step doesn't surprise anyone.

No npm 11 feature is compelling enough to change the current Yarn-based workflow.

### C. Prioritised "potential future implementation" list (all OPTIONAL)

Each item is separable from the core upgrade and can be picked up independently.

1. **(Low effort / low risk) Enforce the `node:` prefix repo-wide.** Add an ESLint rule
   (e.g. `unicorn/prefer-node-protocol` or equivalent) so built-in imports are consistent with
   the already-modernised files. Purely a consistency/readability win.
2. **(Low effort / low risk) Prefer built-in `fs.glob` in new repo scripts.** Guidance only:
   avoid re-introducing a `glob` dependency now that the built-in is stable. No existing code
   change implied.
3. **(Low–medium effort) Capture a Node 24 vs 22 build-time baseline.** Feeds the design doc's
   Performance Baseline; turns the "transparent V8 gains" assumption into a measured fact.
   Pairs naturally with task **8.4** (CI/CD optimisation).
4. **(Medium effort) Use `node:test` for standalone `scripts/` utilities only.** Where a script
   doesn't warrant the Jest harness, a lightweight `node --test` file adds coverage cheaply.
   Explicitly **not** proposed for app or `@whitbread-eos/*` component tests, which stay on Jest.
5. **(Medium effort / opt-in) Adopt explicit resource management (`using`/`await using`) in
   ESM build scripts.** Ergonomic cleanup for file handles/temp resources in `scripts/`; keep
   out of `target: es5` app code.
6. **(Higher effort / security-oriented, deprioritised) Evaluate the Node permission model for
   CI codegen/build sandboxing.** Meaningful hardening but non-trivial; only worth it if a
   security workstream sponsors it.

**Not recommended:** switching package management to npm 11, or introducing the built-in test
runner for application/component test suites — both conflict with the established Yarn + Jest
tooling for no clear gain.

## Task 8.4 — CI/CD optimisation opportunities

_Requirement 8.6 (SHOULD / WHERE-level). Research and documentation only — no workflow or CI config files were modified. All observations are grounded in the current workflow files as they stand after the core upgrade (tasks 1–5)._

### Scope reviewed
- `.github/workflows/ci-fe.yaml` (PR + merge_group front-end CI)
- `.github/workflows/ci-fe-deploy.yaml` (push/deploy front-end CI)
- `.github/frontend/actions/build/action.yml` (composite build action both workflows call)
- `.github/frontend/actions/build/pre-build.sh`
- `frontend/pi-front-end-applications/.github/tests.yaml` (legacy workflow)
- `.github/workflows/build-graphql.yaml` (deliberately on Node 22 — out of scope, not a candidate for change)

---

### 1. Multi-version Node test matrix

**Observation.** The stack is now committed to a single pinned runtime: `.nvmrc` = `24.20.0`, `engines.node` = `>=24.20.0`, and `engine-strict=true` in `.npmrc`. Both front-end workflows inject a single `NODE_VERSION: "24.20.0"` env var and pass it to the composite build action.

**Recommendation: do NOT add a multi-version Node matrix.** Rationale:
- With `engine-strict=true` and `engines.node >=24.20.0`, any matrix leg on an older major (e.g. Node 22) would fail at `yarn install` by design, so the leg could not produce a meaningful signal without weakening the engine constraint — which would defeat the purpose of the upgrade.
- This is a one-way migration to a pinned runtime, not a library that must support a range of Node versions for downstream consumers. There is no external contract that needs matrix coverage.
- A matrix would multiply the build/lint/test/type-check/Docker cost (the `build` job already fans out per app and the pipeline runs SonarQube, Prisma, and dependency-review) for no added confidence.
- The transition is guarded instead by the rollback plan (task 7) — reverting `.nvmrc`, `package.json`, Dockerfile, and the CI `NODE_VERSION` — which is a cheaper and more honest safety net than a permanent matrix.

**If transition-period cross-check is ever wanted** (optional, not recommended for merge): a short-lived, manually-triggered `workflow_dispatch` job pinned to a second version is preferable to a matrix baked into the standard PR pipeline, and it should be deleted once the upgrade is confirmed stable.

---

### 2. Caching strategy observations

**Current state (grounded in `build/action.yml`).**
- The composite build calls `actions/setup-node@v7` **without** the `cache:` input, so setup-node's built-in Yarn cache is **not** enabled.
- There is **no** `actions/cache` step for the Yarn cache, `node_modules`, or the Nx cache (`.nx/cache`) in either workflow or the composite action.
- Each run does `npm install -g yarn` then a cold `yarn` install; Nx task caching therefore only helps **within a single run**, not across runs (the Nx cache directory is ephemeral per runner and is not persisted/restored).

**Node 24 relevance.** Caching here is largely Node-version-agnostic, with one real ABI caveat:
- Yarn v1's own cache stores package tarballs, which are not tied to a Node ABI — safe across a Node upgrade.
- However, **`sharp` and any other native module produce Node-ABI-specific binaries**. If a `node_modules` cache is ever introduced, its cache key MUST include the Node major (ABI) version, otherwise a Node 22-built `sharp` binary could be restored under Node 24 and fail at runtime. Keying on the lockfile hash alone is insufficient for native deps.
- **Nx cache invalidation does not automatically account for the Node version.** Nx hashes source, inputs, and configured `runtimeInputs`/env, but not the Node runtime by default. Because caching is not persisted across runs today this is currently moot, but if cross-run Nx caching is added later, the Node version (or at least a one-time cache-key bump on this upgrade) should be part of the cache key to avoid serving stale Node 22 build outputs.

**Optional improvement (separate from core upgrade).** Enabling `cache: 'yarn'` on `actions/setup-node` (with `cache-dependency-path` pointing at the workspace `yarn.lock`) would speed installs with negligible risk, since the Yarn tarball cache is ABI-independent. This is a pure CI-speed optimisation and is not required by the upgrade.

---

### 3. GitHub Action versions & Node runtime compatibility

**Important distinction.** The `NODE_VERSION` (24.20.0) is the runtime used to *run the build*. It is independent of the **Actions JS runtime** (`runs.using: node20`/`node24`) that GitHub uses to execute each JavaScript action. Bumping `NODE_VERSION` does **not** change which Node runtime executes `actions/checkout` etc. — that is governed by each action's own major version. So the action-version review below is about the actions runner, not the build runtime.

**Front-end workflows + composite (in scope) — all current, compatible:**

| Action | Version referenced | Notes |
|---|---|---|
| `actions/checkout` | `@v7` | Current major; modern Actions runtime. OK. |
| `actions/setup-node` | `@v7` (in `build/action.yml`) | Current major; modern runtime. OK. |
| `actions/upload-artifact` | `@v7` | Current major. OK. |
| `actions/dependency-review-action` | `@v5` | Recent major. OK. |
| `actions/create-github-app-token` | `@v3` | Recent major. OK. |
| `actions/github-script` | `@v9` | Recent major. OK. |
| `azure/setup-helm` | SHA-pinned `# v5.0.1` | SHA-pinned (good supply-chain practice). OK. |

**Legacy `tests.yaml` (in the front-end repo) — OUTDATED, flag:**

| Action | Version referenced | Problem |
|---|---|---|
| `actions/checkout` | `@v2` | Runs on a deprecated Node action runtime (Node 16-era); emits deprecation warnings and is at risk on current runners. |
| `actions/setup-node` | `@v1` | Runs on a very old Node action runtime (Node 12-era). Critically, **`node-version-file` support was only added in `setup-node@v3`**, so the `node-version-file: .nvmrc` input in this file is effectively ignored on `@v1` — the workflow does not actually pin to `.nvmrc` as intended. |

`tests.yaml` also uses `npm i` / `npm run bootstrap` even though the repo is a **Yarn v1 workspaces** project, and it triggers on every `push`/`pull_request`, overlapping the far more complete `ci-fe.yaml`. This looks like a stale/duplicate pipeline.

**`build-graphql.yaml` (out of scope, Node 22):** SHA-pinned `actions/checkout` (v7), `actions/setup-node` (v6), `actions/upload-artifact` (v7). Healthy and correctly left untouched. Noted only for completeness — do not change.

**Minor consistency note.** The front-end composite uses `setup-node@v7` while `build-graphql.yaml` uses `setup-node@v6`, and the main FE workflows use floating major tags (`@v7`) while `build-graphql.yaml` and `azure/setup-helm` are SHA-pinned. Standardising on SHA-pinning across all workflows would be a security/consistency improvement, but is out of scope here.

---

### 4. Prioritised OPTIONAL follow-up items (separate from the core upgrade)

These are explicitly **not** part of the Node 24.20.0 core upgrade and can be tracked as independent tickets:

1. **(High value, low risk) Modernise or retire `frontend/pi-front-end-applications/.github/tests.yaml`.** Either delete it (its coverage is subsumed by `ci-fe.yaml`) or, if kept, bump `actions/checkout@v2 → @v7` and `actions/setup-node@v1 → @v7` (so `node-version-file: .nvmrc` actually takes effect) and switch `npm` → `yarn` to match the project. Resolves both the outdated-action finding and the duplicate-pipeline finding.
2. **(Medium value, low risk) Enable dependency caching** via `cache: 'yarn'` on `actions/setup-node` in the composite build action to cut install time. ABI-safe because it caches Yarn tarballs, not built native binaries.
3. **(Guard-rail, do first if #2 or any `node_modules`/Nx cross-run cache is adopted) Include the Node major version in any future `node_modules`/Nx cache key**, so native modules like `sharp` and Nx build outputs cannot be served stale across a Node major change.
4. **(Low value, consistency) Standardise action pinning** — SHA-pin the floating `@v7`/`@v5`/`@v3`/`@v9` tags in the FE workflows and align `setup-node` versions across `build/action.yml` and `build-graphql.yaml`.

**Explicitly out of scope / do not do:** adding a permanent multi-version Node matrix to the standard pipeline; touching `build-graphql.yaml` (deliberately stays on Node `22.22.2`).
