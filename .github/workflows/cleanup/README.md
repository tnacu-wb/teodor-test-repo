# Ephemeral environment cleanup

Manual teardown for the per-pull-request environments this repo deploys into the **dev**
Kubernetes cluster.

The workflow itself is [`../cleanup-ephemeral-envs.yaml`](../cleanup-ephemeral-envs.yaml).
It cannot live in this directory: GitHub Actions only reads workflow files directly under
`.github/workflows/`, and silently ignores subdirectories (this applies to reusable
`workflow_call` files too). So the workflow is a thin shell in the parent directory and all
the logic lives here, mirroring the existing [`../scripts/`](../scripts) convention.

## Who creates ephemeral environments

| Workflow | Trigger | Namespace | Release name |
| --- | --- | --- | --- |
| [`../ci-matrix-deploy-eph.yaml`](../ci-matrix-deploy-eph.yaml) (via [`../build-service.yaml`](../build-service.yaml)) | `pull_request` | `opera-be` | `<service>-pr-<pr-number>` |
| [`../ci-deploy-fe.yaml`](../ci-deploy-fe.yaml) (via [`../ci-pipeline-fe.yaml`](../ci-pipeline-fe.yaml)) | `pull_request` | `opera-fe` | `<app>-<sanitized-branch>` |

Everything else that lands in those namespaces — [`../ci-matrix-deploy.yaml`](../ci-matrix-deploy.yaml),
[`../helm-deploy.yml`](../helm-deploy.yml), and the Flux/GitOps releases — is permanent and
is named for the service or chart alone, with no PR or branch suffix.

## Naming convention

> **Target convention: `<service-or-app>-pr-<pr-number>`.**
> The delimiter is the anchored suffix `-pr-<digits>`, not an `opera-pr-*` prefix.

Nothing in the repo currently uses an `opera-pr-*` prefix — `opera-be` / `opera-fe` are
namespaces, not release-name prefixes. The suffix form is what the backend already emits and
what [`../ci-matrix-cleanup-eph.yaml`](../ci-matrix-cleanup-eph.yaml) already filters on
(`--filter "-pr-${PR_NUMBER}$"`), so it is the cheapest convention to standardise on.

The frontend is the outlier: `<app>-<sanitized-branch>` carries no PR marker, so a frontend
ephemeral release is not distinguishable from a permanent one by suffix alone. Aligning it
is a separate change — the release name is also the preview hostname
(`<release>.dev.premierinn.digital`), so renaming rotates every preview URL and orphans the
releases that are live today.

Until that happens the sweep recognises **both** forms:

| Form | Pattern | Notes |
| --- | --- | --- |
| Canonical | `^.+-pr-[0-9]+$` | Anchored, so PR 12 never matches PR 123. |
| Frontend legacy | `^(<app>\|<app>\|…)-.+$` | App list read from `frontend/pi-front-end-applications/apps/next-apps/`. |

Two guards keep permanent releases safe:

- The exact name of any frontend app or backend service in the repo is **protected** and
  never uninstalled — both patterns require a suffix, so a bare `premier-inn` or
  `basket-service` can never be selected.
- `dry-run` defaults to `true`. The first run prints the plan; you re-run with `dry-run:
  false` once the list looks right.

## Running it

Actions → **Cleanup Ephemeral Environments** → *Run workflow*.

| Input | Default | Purpose |
| --- | --- | --- |
| `namespaces` | `opera-be,opera-fe` | Namespaces to sweep. |
| `dry-run` | `true` | List what would be removed and stop. Pick `false` (or `no`) to actually uninstall. |
| `skip-open-prs` | `false` | Keep releases belonging to a still-open PR. Resolves open PRs and reconstructs both name forms, so it protects frontend branch-named releases too. |
| `release-filter` | *(empty)* | Extended regex; only releases matching it are considered. Useful for narrowing to one service, e.g. `^basket-service-`. |

`dry-run` and `skip-open-prs` are dropdowns rather than checkboxes, so turning the sweep live
is an explicit choice in the UI. Both accept `true`/`yes` and `false`/`no` interchangeably
(as do `1`/`0` and `on`/`off` when calling through the API or `gh workflow run --field`); an
unrecognised value fails the run instead of being read as `false`.

A typical sweep is two runs: leave the defaults to see the plan, then re-run with `dry-run:
false`. To reclaim only genuinely dead environments, set `skip-open-prs: true`.

## Files

- [`sweep-ephemeral-releases.sh`](sweep-ephemeral-releases.sh) — classifies and uninstalls
  the releases, and writes the job summary.
