---
parent_design: .kiro/specs/monorepo/main-branch-versioning/design.md
parent_requirements: .kiro/specs/monorepo/main-branch-versioning/requirements.md
jira: CTECH-13822
---
# Implementation Plan: Main Branch Versioning

## Tag-Only Versioning (Current Implementation)

Following PR review, the implementation moved from a two-phase, marker-driven model (root
`VERSION` file + `CHANGELOG.md`, a bot commit back to `main`, a commit-message loop guard) to a
**tag-only** model: the version lives only in git tags, the change log lives only in the annotated
tag message and the body on the tag's GitHub Release page, and nothing is ever committed back to
`main`. `ci-main-versioning.yaml` tags `github.sha` directly and then calls `ci-be.yaml` /
`ci-fe-deploy.yaml` as reusable workflows (`workflow_call`, `secrets: inherit`) within the same
run — there is no second push and therefore no loop guard to implement. A second PR review round
then pinned the change log's commit range explicitly (`PREVIOUS_TAG`/`TO_REF` plus an ancestor
check) and renamed the gate/steps away from "release" wording. This plan reflects that final,
activated design; the tasks below are the ones that actually shipped.

## Overview

This plan delivers automatic repo-wide semantic versioning, change log generation, annotated
git tagging, change log publication on GitHub, and reusable-workflow-driven production builds for
every versioning run on `main`:

- **`ci-main-versioning.yaml`** runs a single `version` job (displayed as "Compute version, tag
  and publish change log") that: validates the target ref, mints a GitHub App token, checks out
  `github.sha` with full history and tags, determines the version (reusing an existing tag on
  `github.sha` if present — re-run safety — otherwise computing the next minor bump from the
  highest existing tag, or `1.3.0` if none exist), renders the change log over
  `(PREVIOUS_TAG, github.sha]`, tags `github.sha` directly with an annotated tag, pushes only that
  tag, and publishes (or re-confirms) the change log on the tag's GitHub Release page.
- Two further jobs, `backend` and `frontend` (`needs: version`), call `ci-be.yaml` and
  `ci-fe-deploy.yaml` as reusable workflows with the computed version.
- **`ci-be.yaml`** and **`ci-fe-deploy.yaml`** no longer trigger their production path from their
  own `push`-to-`main` trigger (removed); instead each implements a `main-deployment-gate` job
  (its validation step id is `version-tag`) that validates the `semantic-version` input and
  confirms its tag points at `github.sha` before any downstream production job runs.
- **`build-service.yaml`** validates `semantic-version`/`checkout-ref` as a second line of defence.

Implementation order followed the design's compute-first, tag-directly, idempotent-by-construction,
no-cross-boundary-trust, reuse, pin-the-range, coalescing principles: build and test the pure
shell helpers first, then wire the `version` job around them, then wire the reusable-workflow
calls and the receiving `main-deployment-gate` jobs, then remove the superseded two-phase
artifacts.

Test sub-tasks marked with `*` are optional and, other than the checkpoints, remain **unimplemented**
— see Notes. Property-based tests use bats-core with a generator harness (≥100 iterations) and are
tagged `Feature: main-branch-versioning, Property {number}: {property_text}`.

## Tasks

- [x] 1. Implement version computation helper
  - [x] 1.1 Implement `compute-next-version.sh`
    - Read `CURRENT_VERSION`, `EXISTING_TAGS`, `INITIAL_VERSION` from env
    - Empty `CURRENT_VERSION` ⇒ candidate = `INITIAL_VERSION`; otherwise validate against
      `^[0-9]+\.[0-9]+\.[0-9]+$` (hard failure with actionable message if malformed — no silent
      reset) and minor-bump with patch reset (`X.(Y+1).0`), parsing the minor with `10#` so a
      leading zero is never read as octal
    - Collision-avoidance loop: while candidate is in `EXISTING_TAGS`, bump minor again
    - Emit `version=<X.Y.Z>` on `$GITHUB_OUTPUT`
    - _Requirements: 2.3, 3.1, 3.2, 3.3, 3.4, 3.6, 3.7, 3.8_

  - [ ]* 1.2 Write property test for minor-bump invariant — `compute-next-version.bats`
    - **Property 1: Minor-bump invariant**
    - **Validates: Requirements 3.1, 3.2, 3.3, 3.4, 3.6**
    - Generator: random `X.Y.Z` incl. large components; empty input as edge case; ≥100 iterations;
      tag `Feature: main-branch-versioning, Property 1: Minor-bump invariant`
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.6_

  - [ ]* 1.3 Write property test for next-available version avoiding existing tags — `compute-next-version.bats`
    - **Property 2: Next-available version avoids existing tags**
    - **Validates: Requirements 3.8**
    - Generator: random current version + random existing-tag sets incl. runs of consecutive
      minors; ≥100 iterations; tag `Feature: main-branch-versioning, Property 2: Next-available version avoids existing tags`
    - _Requirements: 3.8_

  - [ ]* 1.4 Write example/unit tests for version computation — `compute-next-version.bats`
    - `compute-next-version("1.3.0") == "1.4.0"` (3.5); empty `CURRENT_VERSION` ⇒
      `INITIAL_VERSION` (2.3); malformed `CURRENT_VERSION` ⇒ error (3.7)
    - _Requirements: 2.3, 3.5, 3.7_

- [x] 2. Implement retry helper for the tag push
  - [x] 2.1 Implement `retry-push.sh`
    - Generic wrapper for an arbitrary command: up to `RETRY_MAX_ATTEMPTS` (default 3) attempts
      with exponential backoff (`RETRY_BASE_DELAY * 2^(attempt-1)`, default base 2s), then hard
      failure with the last non-zero exit status
    - _Requirements: 13.3_

  - [ ]* 2.2 Write unit tests for the retry helper — `retry-push.bats`
    - Assert at most 3 attempts with increasing backoff, then failure with the last exit status
    - _Requirements: 13.3_

- [x] 3. Extend the image-tag resolver for the main-line semantic-version path
  - [x] 3.1 Extend `resolve-image-tag.sh` main-line branch
    - When a well-formed `SEMANTIC_VERSION` is supplied, emit it verbatim as the image tag (no
      ref/sha/timestamp appended); leave the PR/ephemeral branch and the non-semantic main-line
      fallback unchanged
    - _Requirements: 2.5, 10.1, 10.2, 10.3_

  - [ ]* 3.2 Write property test for the main-line image tag — `resolve-image-tag.bats`
    - **Property 3: Main-line image tag equals the semantic version**
    - **Validates: Requirements 2.5, 10.1**
    - Generator: random valid semantic versions; ≥100 iterations; tag
      `Feature: main-branch-versioning, Property 3: Main-line image tag equals the semantic version`
    - _Requirements: 2.5, 10.1_

- [x] 4. Checkpoint - mandatory helper scripts implemented
  - `compute-next-version.sh`, `retry-push.sh`, and the `resolve-image-tag.sh` main-line branch are
    implemented and manually verified through production activation runs; the optional bats suites
    (tasks 1.2–1.4, 2.2, 3.2) remain open and are tracked separately (see Notes)

- [x] 5. Implement change log generation
  - [x] 5.1 Implement `generate-changelog.sh`
    - Thin wrapper around `conventional-changelog-cli` pinned to an **exact** version (`5.0.0`,
      no range), `conventionalcommits` preset, invoked via `npx --yes
      --package=conventional-changelog-cli@<exact>` (no root `package.json` required)
    - Require `VERSION` (or `CHANGELOG_VERSION`) to already be computed; supply it via a
      `--context` file so the heading is that version, not one derived from any package.json;
      `--release-count 1` restricts output to the single new entry
    - Write output to `NOTES_FILE` (a runner-temp path, not a repository file)
    - First version (`PREVIOUS_TAG` empty): write a short "First versioned change log of the
      monorepo." entry instead of invoking the tool
    - Empty tool output despite a previous tag: fall back to a minimal dated heading
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7_

  - [ ]* 5.2 Write example tests for the changelog wrapper
    - Given a version and sample conventional commits: heading contains the version and a date and
      groups by type; first-version and empty-output fallback text match the documented format
    - Ancestor check: a `PREVIOUS_TAG` that is not an ancestor of `TO_REF` exits `2` without
      invoking the CLI
    - Pinned range: the rendered entry only includes commits from `(PREVIOUS_TAG, TO_REF]`,
      matching the `CHANGELOG_FROM`/`CHANGELOG_TO` values passed via the generated `--config` file
      rather than whatever the tool would pick as its own "latest tag"
    - _Requirements: 5.1, 5.3, 5.5, 5.6, 5.9, 5.10, 5.11_

  - [x] 5.3 Pin the change log commit range and rename "release" wording (second PR review round)
    - Added `TO_REF` to `generate-changelog.sh` (the workflow passes `github.sha`); added a
      `git merge-base --is-ancestor "$PREVIOUS_TAG" "$TO_REF"` safety check that exits `2` before
      running the tool if `PREVIOUS_TAG` is set but is not an ancestor of `TO_REF`
    - Pinned the rendered range explicitly via a generated `--config` file (`config.cjs`) whose
      `gitRawCommitsOpts.from`/`.to` read from the `CHANGELOG_FROM`/`CHANGELOG_TO` env vars (never
      interpolated into the generated JavaScript), so the tool never falls back to its own
      "latest tag" as the range start; the first-version fallback (empty `PREVIOUS_TAG`) is
      unaffected and still skips both the config file and the CLI invocation
    - In the `version` job, computed `PREVIOUS_TAG` as the highest semver tag in `github.sha`'s own
      history, excluding tags on `github.sha` itself (`git tag --merged "$GITHUB_SHA" --no-contains
      "$GITHUB_SHA" | sort -V | tail -n1`) — distinct from `CURRENT_VERSION` (the highest semver tag
      in the whole repo, task 7), which is what the next version is bumped from. The two normally
      coincide; `PREVIOUS_TAG` only differs when a higher tag sits on a commit outside `main`'s
      history
    - [x] Fixed review comment 4098436723 (Copilot): moved the `PREVIOUS_TAG` derivation above the
      re-run check (task 7) and added `--no-contains`, so `previous-tag` is written to
      `$GITHUB_OUTPUT` on the already-tagged (re-run) path too — previously it was only written on
      the fresh-version path, so the "Report versioning summary" step (task 10) always reported
      `<none>` on a re-run
    - Renamed "release" terminology to avoid confusion with our own concepts: the job
      `main-release-gate` → `main-deployment-gate` (its validation step id `release` →
      `version-tag`) in `ci-be.yaml`/`ci-fe-deploy.yaml`; the `version` job's display name →
      "Compute version, tag and publish change log"; step names → "Generate change log",
      "Publish change log on GitHub", "Report versioning summary"; the notes file →
      `${{ runner.temp }}/changelog.md`; `build-service.yaml`'s validation step →
      "Validate main production deployment inputs". GitHub's own API/product names
      (GitHub Releases REST API, the `/releases` endpoint, `already_exists`, `--release-count`,
      `release/*`/`hotfix/release*` branch names, Helm release) were left unchanged
    - Documented the coalescing contract as a comment alongside the workflow's `concurrency:`
      block: the concurrency group keeps at most one pending run, so a burst of pushes to `main`
      versions only the newest pending run; no commit is lost because that run's change log covers
      every commit since the previous tag (including those whose own run was superseded) and the
      backend/frontend pipelines deploy that newest commit
    - _Requirements: 4.4, 5.8, 5.9, 5.10, 5.11, 5.12, 9.1, 12.4_

- [x] 6. Implement `ci-main-versioning.yaml` trigger, concurrency, and target validation
  - `on: push: branches: [main]` only — no PR, other-branch, tag, or `workflow_dispatch` trigger
  - Workflow-level `concurrency: group: production-main-versioning, cancel-in-progress: false`,
    with a comment documenting the coalescing contract (task 5.3): at most one pending run per
    group, so a burst of pushes versions only the newest pending run and no commit is lost
  - "Validate fixed production target" step: fail immediately if `github.ref !=
    refs/heads/main`
  - Workflow-level `permissions: contents: read`; job-level `permissions: contents: read` (write
    access comes only from the minted App token)
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 11.3, 12.1, 12.2, 12.4_

- [x] 7. Implement version determination in the `version` job (incl. re-run safety)
  - Mint a GitHub App token via `actions/create-github-app-token@v3`
    (`vars.PIPELINE_APP_ID`/`secrets.PIPELINE_APP_PRIVATE_KEY`, `permission-contents: write`)
  - Checkout `github.sha` with `fetch-depth: 0`, `fetch-tags: true`, using the App token
  - Derive `PREVIOUS_TAG` (task 5.3) as the highest existing semver tag in `github.sha`'s own
    history, excluding tags on `github.sha` itself (`git tag --merged "$GITHUB_SHA" --no-contains
    "$GITHUB_SHA"`), and write it as the `previous-tag` output **before** the re-run check, so it
    is set on both the fresh and already-tagged paths
  - Re-run check: if `github.sha` already carries a tag matching `^[0-9]+\.[0-9]+\.[0-9]+$` (via
    `git tag --points-at`, highest by `sort -V`), reuse it as `version` and set
    `already-tagged=true`, skipping change log/tag creation
  - Otherwise: derive `CURRENT_VERSION` as the highest existing semver tag in the whole repo via
    `sort -V` (NOT `git describe`), gather `EXISTING_TAGS`, and run `compute-next-version.sh`
    (task 1.1) with `INITIAL_VERSION=1.3.0`
  - Set `timeout-minutes: 10` on the job
  - [x] Fixed review comment 4098436723 (Copilot): `previous-tag` was previously only written on
    the fresh-version path, so the already-tagged (re-run) path left it unset and the "Report
    versioning summary" step (task 10) always reported `<none>`; moving the `PREVIOUS_TAG`
    derivation above the re-run check and adding `--no-contains` fixes both the missing output and
    the correctness of the re-run value
  - _Requirements: 2.2, 2.3, 3.1, 4.1, 4.4, 5.8, 11.1, 11.2, 11.4, 13.1_

- [x] 8. Wire change log generation into the `version` job
  - Invoke `generate-changelog.sh` (task 5.1) with `VERSION`, `PREVIOUS_TAG`,
    `TO_REF: ${{ github.sha }}` (task 5.3), and `NOTES_FILE=${{ runner.temp }}/changelog.md`,
    gated `if: steps.compute.outputs.already-tagged != 'true'`
  - _Requirements: 4.2, 5.1, 5.9_

- [x] 9. Implement tag creation and push
  - Gated the same as task 8 (skip on an already-tagged commit)
  - Derive the tagger identity from the App token's `app-slug` output (`<app-slug>[bot]`); fail
    fast with an actionable error if `app-slug` is empty
  - `git config --local user.name/user.email` to that identity; `git tag -a "$VERSION"
    --cleanup=verbatim -F "$NOTES_FILE" "$GITHUB_SHA"` (tag points at `github.sha` directly, no
    marker commit)
  - Push **only** the tag ref via `retry-push.sh git push origin "refs/tags/${VERSION}"` (task 2.1)
    — no branch push
  - _Requirements: 4.2, 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 6.7, 6.8_

- [x] 10. Implement change log publication on GitHub and step summary
  - Always runs (both the fresh-tag and already-tagged branches), step "Publish change log on
    GitHub": if `NOTES_FILE` is missing/empty, reconstitute the change log from
    `git tag --list --format='%(contents)' "$VERSION"`
  - `POST {GITHUB_API_URL}/repos/{GITHUB_REPOSITORY}/releases` with `tag_name`/`name`/`body` using
    the App token; treat `201` and `422 already_exists` as success; any other status fails the job
    and logs the response body
  - Step "Report versioning summary": write a step summary (`$GITHUB_STEP_SUMMARY`) with version,
    previous tag, tagged commit SHA, and the tag's GitHub Release page URL
  - _Requirements: 4.3, 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 13.2, 14.1, 14.2, 14.3_

- [x] 11. Checkpoint - versioning job validated end-to-end
  - The `version` job's full path (target validation → token → checkout → version determination →
    change log → tag → publish → summary) has been manually verified through production activation
    runs, including the re-run/idempotency path; optional automated test suites (tasks 1.2–1.4,
    2.2, 3.2, 5.2) remain open (see Notes)

- [x] 12. Wire the `backend` / `frontend` reusable-workflow calls
  - `needs: version`; `backend` job: `uses: ./.github/workflows/ci-be.yaml` with
    `semantic-version: ${{ needs.version.outputs.version }}` and `secrets: inherit`; `frontend`
    job: `uses: ./.github/workflows/ci-fe-deploy.yaml`, same pattern
  - Each calling job declares the union of permissions its called workflow's own jobs require
    (backend: `actions: read`, `contents: read`, `security-events: write`, `id-token: write`,
    `pull-requests: write`, `issues: write`; frontend: `actions: read`, `checks: read`,
    `contents: read`, `id-token: write`)
  - _Requirements: 8.1, 8.2, 8.3_

- [x] 13. Add `main-deployment-gate` job to `ci-be.yaml` and `ci-fe-deploy.yaml`
  - `workflow_call` trigger with a required `semantic-version` string input on both workflows
  - `main-deployment-gate`: `if: github.event_name == 'push' && github.ref == 'refs/heads/main'`;
    checkout `github.sha` with full history + tags; step "Validate semantic version and tag"
    (`id: version-tag`) validates `semantic-version` non-empty and `^[0-9]+\.[0-9]+\.[0-9]+$`,
    `github.sha` matches `^[0-9a-f]{40}$`, and `git rev-parse -q --verify
    "refs/tags/${SEMANTIC_VERSION}^{commit}"` equals `github.sha`; outputs `semantic-version` and
    `checkout-sha` from that step
  - Downstream production jobs `needs: main-deployment-gate` with an `always() &&
    (github.event_name != 'push' || github.ref != 'refs/heads/main' ||
    needs.main-deployment-gate.result == 'success')` gate, so non-main-push triggers bypass the
    gate unaffected; downstream jobs thread `checkout-sha` (fallback `github.sha`) as their
    checkout ref and `semantic-version` through to image tagging
  - _Requirements: 8.4, 9.1, 9.2, 9.3, 9.4, 9.5, 9.6, 9.7, 14.4, 14.5_

- [x] 14. Remove `main` from `ci-be.yaml` / `ci-fe-deploy.yaml` push triggers; literal concurrency groups
  - Removed `main` from the `push` trigger `branches` list of both workflows (backend keeps
    `release/*`, `hotfix/*`; frontend keeps `release/*`, `hotfix/release*`, `perf/*`)
  - Concurrency groups changed to literal names — `BE CI Orchestrator-${{ github.ref }}` and
    `CI FE Deploy-${{ github.ref }}` — instead of `${{ github.workflow }}`-based names, because
    `github.workflow` resolves to the caller's name inside a called workflow
  - PR / release / hotfix / perf triggers and behaviour left unchanged
  - _Requirements: 1.5, 1.6, 8.5, 8.6_

- [x] 15. Add production-deployment input validation to `build-service.yaml`
  - `semantic-version` / `checkout-ref` optional `workflow_call` inputs (default `''`)
  - "Validate main production deployment inputs" (`if: inputs.semantic-version != ''`): fail unless
    `semantic-version` matches `^[0-9]+\.[0-9]+\.[0-9]+$`, unless `github.event_name == 'push' &&
    github.ref == 'refs/heads/main'`, and unless `checkout-ref` matches `^[0-9a-f]{40}$`
  - Separate unconditional `checkout-ref` format validation for any caller that supplies it
  - Threaded `SEMANTIC_VERSION` into `resolve-image-tag.sh` (task 3.1) as the image tag; JAR
    filename and Docker `APP_VERSION` build-arg remain hardcoded `1.0.0` (documented Known
    Limitation, not addressed by this task)
  - _Requirements: 9.2, 9.3, 9.4, 10.1_

- [x] 16. Redesign cleanup — remove the superseded two-phase artifacts
  - Removed the root `VERSION` file and `CHANGELOG.md` from the repository; no step reads or
    writes either
  - Removed `version-should-run.sh`, its loop-guard property test, and the commit-message-marker
    gate (`bumped minor version on main <version>`) from every workflow — there is no
    automation-authored commit back to `main` to guard against
  - Removed the `main-test` canary trigger and its duplicate/parallel dev-GitOps sync path used
    during development of this feature; production activation targets `main` only
  - Confirmed `ci-main-versioning.yaml` never pushes a branch commit — only the annotated tag
  - _Requirements: 2.1, 1.4_

- [ ]* 17. Write remaining integration and smoke tests
  - [ ]* 17.1 Re-run / idempotency integration tests
    - Re-running the workflow on an already-tagged `github.sha` reuses the version, creates no new
      tag, and still succeeds at change log publication and both reusable-workflow calls; a
      repeated change-log-publish attempt for the same version returns `422 already_exists` and is
      treated as success, reading the change log body back from the tag's own contents
    - The "Report versioning summary" step on a re-run reports the actual highest earlier tag as
      "Previous tag", NOT `<none>` (regression test for review comment 4098436723)
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 7.3, 7.4, 7.6_

  - [ ]* 17.2 `main-deployment-gate` and reusable-workflow-wiring integration tests
    - `main-deployment-gate` fails closed on an empty/malformed `semantic-version`, a malformed
      SHA, or a tag that does not point at `github.sha`; succeeds and outputs both values when
      consistent; downstream jobs correctly skip/gate in both cases; a raw push to `main` alone
      does not trigger `ci-be.yaml` / `ci-fe-deploy.yaml`'s production path without the
      `workflow_call`
    - _Requirements: 1.5, 8.4, 8.5, 9.2, 9.3, 9.4, 9.5, 9.6, 9.7_

  - [ ]* 17.3 Concurrency, coalescing, and smoke checks
    - Two near-simultaneous pushes to `main` serialise via `production-main-versioning` and
      produce two distinct, non-colliding versions; a third push landing while a run is in
      progress supersedes any run still queued behind it, so only the newest is versioned, and its
      change log still covers every commit since the previous tag (coalescing contract); no root
      `VERSION`/`CHANGELOG.md` file exists or is consulted; per-stack versioning is not consulted;
      the `version` job's `timeout-minutes: 10` reports a timeout on overrun; the tag and its
      change log are discoverable via git/GitHub UI
    - _Requirements: 2.1, 2.4, 3.8, 12.1, 12.2, 12.4, 13.1, 14.3_

- [x] 18. Final checkpoint - tag-only versioning activated on production `main`
  - All mandatory implementation tasks (1.1, 2.1, 3.1, 5.1, 5.3, 6, 7, 8, 9, 10, 12, 13, 14, 15,
    16) are complete and running in production on `main`; optional automated test suites
    (`compute-next-version.bats`, `resolve-image-tag.bats`, `retry-push.bats`, changelog example
    tests, and the integration/smoke suite in task 17) remain to be written

## Notes

- Tasks marked with `*` are optional (property tests, unit/example tests, integration and smoke
  checks). **As of this revision they are all still unimplemented** — no `.bats` files exist yet
  under `.github/workflows/scripts/tests/` beyond the scaffolded `README.md` and
  `helpers/generators.bash` stub. Core workflow and helper implementation tasks are all complete.
- `.github/workflows/scripts/tests/README.md` and `helpers/generators.bash` have been cleaned of
  the removed loop-guard generators (`BOT_ACTOR`, `RELEASE_MARKER`,
  `gen_commit_author`/`gen_commit_metadata` are gone) and now reference tasks 1.2–1.4, 2.2, 3.2
  and properties P1–P3, ready for whoever picks up the still-unimplemented bats suites.
- Property tests use bats-core with a ≥100-iteration generator harness and are tagged
  `Feature: main-branch-versioning, Property {number}: {property_text}`; Python + `hypothesis`
  driving the scripts via subprocess is an acceptable substitute.
- The changelog tool (`conventional-changelog-cli@5.0.0`, pinned exactly) and the GitHub App token
  action are reused dependencies, not reimplemented.
- Checkpoints (tasks 4, 11, 18) mark natural validation breaks; they certify the mandatory
  implementation, not the optional test suites.
- No commit is ever pushed back to `main`, and there is no loop guard: the only push this feature
  makes is the annotated tag itself (`refs/tags/<version>`).
- Properties are renumbered 1–3 (previously 1, 2, 4) now that the loop-guard property (previously
  Property 3) has been removed along with `version-should-run.sh`; there is no gap in the sequence.
- Known limitations (see requirements.md / design.md): the backend JAR filename and the Docker
  `APP_VERSION` build-arg remain hardcoded `1.0.0` — only the image tag carries the real semantic
  version; and `compute-next-version.sh`'s bash arithmetic does not guard against version
  components beyond 64-bit range. The very first push to `main` after activation tags itself
  `1.3.0` automatically — no manual seed tag is required.
- **Second PR review round (task 5.3):** a human reviewer asked that "release" be avoided for our
  own concepts, reserved only for GitHub's own API/product naming (the GitHub Releases REST API,
  the `/releases` endpoint, `already_exists`, `--release-count`, `release/*`/`hotfix/release*`
  branch names, Helm release); everywhere else the spec and the workflows now say "change log" /
  "deployment" / "version". The same round pinned the change log's commit range explicitly
  (`PREVIOUS_TAG`/`TO_REF` plus the `git merge-base --is-ancestor` check) instead of letting
  `conventional-changelog-cli` pick its own "latest tag", and added the workflow comment
  documenting the coalescing contract at the `concurrency:` block.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "2.1", "3.1", "5.1"] },
    { "id": 1, "tasks": ["1.2", "1.3", "1.4", "2.2", "3.2", "5.2", "5.3"] },
    { "id": 2, "tasks": ["4"] },
    { "id": 3, "tasks": ["6"] },
    { "id": 4, "tasks": ["7"] },
    { "id": 5, "tasks": ["8"] },
    { "id": 6, "tasks": ["9"] },
    { "id": 7, "tasks": ["10"] },
    { "id": 8, "tasks": ["11"] },
    { "id": 9, "tasks": ["12", "13", "14", "15"] },
    { "id": 10, "tasks": ["16"] },
    { "id": 11, "tasks": ["17.1", "17.2", "17.3"] },
    { "id": 12, "tasks": ["18"] }
  ]
}
```
