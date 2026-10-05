# Implementation Plan: ddb-stream-basket-to-dt Monorepo Migration (SRE-338, phases 1–2)

## Overview

This plan imports `ddb-stream-basket-to-dt` from `whitbread-eos/ddb-stream-basket-to-dt` into `digital-monorepo` at `tools/python/ddb-stream-basket-to-dt/` with full Git history, drops the standalone repo's `.github/` cruft, adds a monorepo-root Dependabot config scoped to the tool, and stands up a path-triggered Python CI pipeline (uv test on PRs, ECR image build on merge) that leaves the Java estate untouched.

Scope is **phase 1 (land the code)** and **phase 2 (CI)**. Phase 3 (prod automation) and the SRE-319 ECR prerequisite are out of scope and captured only as follow-on notes.

The work is sequenced around three ordering constraints:

1. **The `git subtree add` import is a one-way gate** — the Dependabot file and CI workflow reference `tools/python/ddb-stream-basket-to-dt/`, so verification (though not authoring) depends on the tree existing.
2. **The image build is inline and self-contained** — it does not call the shared `ci-image-build-python.yaml` (whose hard-coded `./` context can't reach a subdirectory), so there is no cross-repo dependency; the `build` job mirrors `build-service.yaml`'s container build/push steps (the language-agnostic AWS/ECR/docker mechanics — **not** its Maven build; this tool has no Java).
3. **CI authoring is independent of the import** — the workflow and Dependabot files can be authored before or after the subtree add; they only need the tree present to be *verified*.

No application code is written in this plan. Implementation artifacts are YAML (workflows, Dependabot), the imported Python tree (imported essentially unmodified — the sole exception is a security fix folded in during review: the raw-DynamoDB-record WARNING log in `basket_stream_to_dynatrace.py` now emits only non-sensitive stream metadata), and Markdown.

## Tasks

- [x] 1. Pre-flight checks and capture the upstream SHA
  - Verify `tools/python/ddb-stream-basket-to-dt/` does not already exist with tracked content in the monorepo (abort if it does).
  - Confirm reachability with `git ls-remote https://github.com/whitbread-eos/ddb-stream-basket-to-dt.git`, then clone/fetch the target `<branch>` into a scratch directory.
  - Capture the scratch clone's tip as the **Upstream_SHA** (40-char hex) — this is the pre-filter, audit-only SHA. It will NOT be the SHA passed to `git subtree add`, because task 2.1 rewrites history; do not reuse it there. Reject placeholder URL/SHA values.
  - _Requirements: 1.7, 1.8_
  - _Design: §1 (pre-flight; fetch and capture the Upstream_SHA)_

- [x] 2. Import the tool via git subtree with cruft excluded
  - [x] 2.1 Produce a filtered source and capture the Imported_SHA
    - In the scratch clone run `git filter-repo --invert-paths --path .github --path .vscode --path .venv --path-glob '**/__pycache__/**' --path-glob '**/*.pyc' --path-glob 'build*.log'` so the standalone repo's `.github/` (CI, dependabot, copilot-instructions), editor config, and any local artifacts are dropped before import. (`__pycache__` needs a glob, not `--path`: `--path` matches only a top-level entry, but `__pycache__` dirs are nested; `.venv` is top-level so a plain `--path` prefix is fine.)
    - Note that `git filter-repo` **rewrites commit SHAs**; the Upstream_SHA from task 1 no longer exists in this filtered repo. Capture the filtered tip as the **Imported_SHA** — this is the SHA task 2.2 passes to `git subtree add`.
    - Confirm the rewritten tree still contains `Dockerfile`, `compose.yaml`, `test-data/`, `.pylintrc`, `pyproject.toml`, `uv.lock`, `.python-version`, `README.md`, both `.py` files, and the tool `.gitignore`.
    - _Requirements: 1.3, 1.5, 1.6, 2.1, 2.2, 2.4_
    - _Design: §1 (path exclusion; SHA rewriting)_
  - [x] 2.2 Run `git subtree add` using the Imported_SHA
    - Execute `git subtree add --prefix=tools/python/ddb-stream-basket-to-dt <rewritten-source> <Imported_SHA>` (no `--squash`). The `<Imported_SHA>` is the filtered tip from task 2.1 — NOT the Upstream_SHA from task 1, which does not exist in the filtered history.
    - Verify the merge commit has two parents and a `git-subtree-split: <Imported_SHA>` trailer; `git log <Imported_SHA>` walks the imported history with original authors/messages/timestamps (SHAs differ from upstream, by design).
    - Amend the merge commit body to record the upstream provenance: the `https://github.com/whitbread-eos/ddb-stream-basket-to-dt` URL and the **Upstream_SHA** from task 1 (neither is reachable from the imported history after filtering).
    - Confirm the imported build/config files (Dockerfile, compose.yaml, pyproject.toml, uv.lock, .python-version, .pylintrc) are byte-for-byte the upstream versions.
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 2.3_
    - _Design: §1 (subtree import; verifying history)_

- [x] 3. Add monorepo-root Dependabot config scoped to the tool
  - Create `.github/dependabot.yml` (new file — the monorepo has none) with `version: 2` and the `uv` and `docker` `updates` entries, each with `directory: "/tools/python/ddb-stream-basket-to-dt"` and `schedule.interval: "weekly"`.
  - Add the `docker-compose` entry **only if** the target GitHub instance's Dependabot supports that ecosystem (Requirement 3.5); omit it otherwise so the file stays schema-valid (Requirement 3.7). On GitHub.com it is supported, matching the Source_Repository.
  - Keep it scoped to the tool directory only; do not add root-level or Java-estate ecosystems.
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7_
  - _Design: §2 (Dependabot)_

- [x] 4. Create the path-triggered Tool CI workflow (test job + inline image build)
  - Create a **generic** workflow `.github/workflows/ci-python-tools.yaml` (serving every tool under `tools/python/<name>/`, not one workflow per tool) plus `.github/workflows/scripts/detect-changed-python-tools.sh`. Trigger on `on.pull_request.paths` / `on.push.paths` (branch `main`) scoped to `tools/python/**` (plus the workflow + detect script), and a `workflow_dispatch` trigger.
  - `detect` job: run `detect-changed-python-tools.sh` to emit a matrix of changed tools. `BASE_SHA` = PR merge base on PRs, `github.event.before` on pushes (only changed tools build), empty on dispatch (all tools). An unresolvable/all-zero base falls back to all tools.
  - `test` matrix job (per changed tool, runs on every event): derive details from the tool's own files — Python version from `.python-version` (fall back to `requires-python` in `pyproject.toml`), tests via `uv run --locked --no-build python -m unittest` (auto-discovery, no hard-coded test filename), then lint via `uv run --with pylint pylint --rcfile=.pylintrc` (ephemeral, so `pyproject.toml`/`uv.lock` stay byte-for-byte per Requirement 2.3). Steps: `actions/checkout` → `astral-sh/setup-uv` → `uv python install <version>` → `uv sync --locked --no-build` → run tests → lint. This job has **no `id-token` permission**. **Pin every action to a commit SHA** (CodeQL requires it).
  - `build` matrix job (**separate job**, `needs: [detect, test]`, only `if: github.event_name == 'push'` and the tool has a `Dockerfile`): keeping build in its own job — not steps appended to `test` — is what confines `id-token: write` to push-to-main runs, so PR and `workflow_dispatch` runs can never assume the ECR role. Image name `whitbreaddigital/<pyproject name>`. Steps: `aws-actions/configure-aws-credentials` (role `arn:aws:iam::327347623470:role/GitHubActionsECRPushRole`, `eu-west-1`) → `aws-actions/amazon-ecr-login` → tag via `.github/workflows/scripts/resolve-image-tag.sh` (pass `PR_NUMBER`) → `docker build --build-arg BUILD_TIMESTAMP=...` from the tool path → **Prisma scan** → `docker push` to ECR (scan runs before push so it actually gates). Workflow expressions reach `run:` via `env:` to avoid expression injection. Gated on `== 'push'` (not `!= 'pull_request'`, which would also fire on `workflow_dispatch`). ECR-only, no Docker Hub. Do **not** call the shared `ci-image-build-python.yaml` template (its `./` context can't reach a subdirectory).
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 5.1, 5.2, 5.3, 5.4, 5.5, 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 6.7, 6.8, 8.4_
  - _Design: §3 (generic Python-tools workflow)_

- [x] 5. Checkpoint — local verification of the imported tool
  - From `tools/python/ddb-stream-basket-to-dt/`: `uv sync --locked --no-build` (exit 0, lock unchanged); `uv run --locked --no-build python -m unittest` (auto-discovery; exit 0, 0 failures).
  - `docker build .` (from within `tools/python/ddb-stream-basket-to-dt/`; exit 0; image runs as uid/gid 9001). Note: the CI workflow, which runs from the repo root, uses `docker build tools/python/ddb-stream-basket-to-dt` instead.
  - `uv run --with pylint pylint --rcfile=.pylintrc --fail-on=E,F --fail-under=0 basket_stream_to_dynatrace.py` (no fatal/error from the move; pylint run ephemerally so pyproject.toml/uv.lock stay byte-for-byte).
  - Resolve any breakage before proceeding; ask the user if questions arise.
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 8.1_
  - _Design: §Verification Approach_

- [x] 6. Verify the Java build is untouched by tool changes
  - On a branch whose only diff is under `tools/python/ddb-stream-basket-to-dt/**`, run the backend detectors (`detect-all.sh`, `detect-changed-eph.sh`) and confirm `services` and `libraries` outputs are empty and no Maven module is selected.
  - Confirm no `pom.xml` was added or modified and the tool is not referenced by any `<module>`.
  - _Requirements: 4.3, 7.5, 8.2_
  - _Design: §4 (what this design does NOT do)_

- [x] 7. (Optional) Dry-run the Tool CI workflow before merge
  - Use `workflow_dispatch` (or a draft PR touching the tool) to confirm: the `test` job runs and passes; the `build` job is skipped on both `pull_request` and `workflow_dispatch` (it runs only on push to main); the workflow does not appear for a backend-only diff.
  - Confirm the Dependabot config shows three ecosystems on the tool directory with no schema error in the GitHub UI.
  - _Requirements: 4.1, 4.2, 5.5, 6.7, 3.7_
  - _Design: §Verification Approach_

- [x] 8. Final checkpoint — scope guard
  - Confirm no change was made to `ci-be.yaml`, the detect scripts, any gitops repo, any prod image reference, or the standalone repo.
  - Confirm the standalone repo remains active (not archived).
  - _Requirements: 9.1, 9.2, 9.3, 9.4_
  - _Design: §4, §Decisions and Open Questions items 5–6_

## Notes

- Task 7 is marked "(Optional)": a pre-merge dry-run. Skipping it ships the migration on the strength of local verification (tasks 5–6) and reviewer inspection.
- The image build is inline (task 4), matching `build-service.yaml`'s container build/push steps (not its Maven build); there is no dependency on `wbd-workflows-templates`, so the implementation is self-contained in the monorepo.
- The import (tasks 1–2) is the one-way gate. Dependabot (task 3) and the workflow (task 4) can be authored independently but are verified against the imported tree.
- This plan creates implementation artifacts only. The spec and the phases 1–2 implementation (tasks 1–8) are delivered together in this single PR. Review and merge follow the standard team process. (An earlier revision planned two separate PRs — spec first, implementation second — but they were combined.)
- The `tools.md` wording nuance (`detect-changed-modules.sh` is sourced by `detect-changed-eph.sh`, not called directly by `ci-be.yaml`) is noted in the design but is explicitly not fixed here.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1", "3", "4"] },
    { "id": 1, "tasks": ["2.1"] },
    { "id": 2, "tasks": ["2.2"] },
    { "id": 3, "tasks": ["5"] },
    { "id": 4, "tasks": ["6"] },
    { "id": 5, "tasks": ["7"] },
    { "id": 6, "tasks": ["8"] }
  ]
}
```
