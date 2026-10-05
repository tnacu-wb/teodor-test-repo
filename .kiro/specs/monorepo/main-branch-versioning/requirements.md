---
parent_design: none (derived from steering + industry standards)
jira: CTECH-13822
---
# Requirements Document

## Tag-Only Versioning (Current Implementation)

Following PR review, this feature was redesigned from a two-phase, marker-driven,
commit-back-to-main model to a **tag-only** model. There is no root `VERSION` file and no
`CHANGELOG.md` in the repository, and the versioning workflow never commits back to `main`.
Instead:

1. `ci-main-versioning.yaml` runs once per push to `main` (`concurrency: production-main-versioning`,
   `cancel-in-progress: false`) and checks out `github.sha` with full history and tags.
2. The current version is the **highest existing `X.Y.Z` git tag**, found with `sort -V` (not
   `git describe`, which only sees tags reachable from `HEAD`). With no existing tags, the
   `INITIAL_VERSION` is `1.3.0` — the very first push to `main` after this feature ships tags
   itself `1.3.0` automatically; no manual seed tag is required.
3. The next version is a minor bump with the patch reset to `0` (`X.Y.Z` → `X.(Y+1).0`), with a
   collision-avoidance loop against existing tags.
4. **Re-run safety:** if `github.sha` already carries a semantic-version tag (e.g. a re-run of a
   previously successful workflow run), that version is reused and change log generation and
   tag creation are both skipped.
5. The **change log range is pinned explicitly**, not left to the changelog tool's own "latest
   tag" heuristic. Two tags are computed: `CURRENT_VERSION` (the highest `X.Y.Z` tag in the whole
   repo, used for the *next version*) and `PREVIOUS_TAG` (the highest `X.Y.Z` tag in `github.sha`'s
   own history, excluding tags on `github.sha` itself (`--no-contains`), via `git tag --merged
   "$GITHUB_SHA" --no-contains "$GITHUB_SHA"`, used as the *change log's lower bound*).
   `PREVIOUS_TAG` is computed before the re-run check and emitted on both the fresh and
   already-tagged paths, so the step summary (Requirement 7.6) is accurate on a re-run too.
   These normally coincide and differ only when a higher tag sits on a commit outside `main`'s
   history. `generate-changelog.sh` receives `PREVIOUS_TAG` and `TO_REF` (`github.sha`), refuses
   (exit 2) if `PREVIOUS_TAG` is not an ancestor of `TO_REF`, and pins the range to exactly
   `(PREVIOUS_TAG, TO_REF]` by generating a `--config` file whose `gitRawCommitsOpts.from`/`.to`
   read from the `CHANGELOG_FROM`/`CHANGELOG_TO` env vars (never interpolated into the JavaScript).
   The change log is rendered by `generate-changelog.sh` (wrapping `conventional-changelog-cli`
   pinned to exact version `5.0.0`) into a runner-temp file (`${{ runner.temp }}/changelog.md`),
   and used as **both** the annotated tag's message **and** the body on the tag's GitHub Release
   page — there is no changelog file committed to the repository.
6. The tagged commit is `github.sha` itself: an **annotated, unprefixed** tag (e.g. `1.3.0`) is
   created with `git tag -a <version> --cleanup=verbatim -F <notes-file> <sha>` and only the
   **tag** is pushed (via the `retry-push.sh` retry helper) — no branch push, no marker commit.
7. The change log is published on the tag's GitHub Release page via the REST API (the API object
   is called a "release", but it adds nothing beyond the tag and its change log); a `422
   already_exists` response is treated as success, making the whole run idempotent on re-run.
8. `ci-main-versioning.yaml` then calls `ci-be.yaml` and `ci-fe-deploy.yaml` as **reusable
   workflows** (`workflow_call`, `secrets: inherit`) with the computed version as a required
   `semantic-version` input. `main` has been removed from both workflows' own `push` triggers, so
   they no longer run their main-line path directly on a push to `main` — only via this call.
9. Each of `ci-be.yaml` / `ci-fe-deploy.yaml` gates its production work behind a
   `main-deployment-gate` job that validates the supplied `semantic-version` and the checkout SHA,
   and confirms the corresponding git tag actually points at `github.sha` before any downstream
   production job runs.

There is no second push, no commit-message marker, no loop guard, and nothing is written back to
`main`. Everything the versioning workflow produces lives in git tags and the tags' GitHub Release
pages.

**Coalescing contract.** The workflow-level concurrency group keeps at most one pending run per
group, so when several pushes land while a run is in progress, only the newest pending run is
versioned and the intermediate runs are replaced. No commit is lost: the newest commit's tag
carries a change log covering every commit since the previous tag (including those whose own run
was superseded), and the backend/frontend pipelines deploy that newest commit. Versions therefore
map to deployed states of `main`, not to individual pushes — see Requirement 12.4.

## Introduction

This specification defines automatic repo-wide semantic versioning, git tagging, change log
generation, and reusable-workflow-driven production builds, triggered for every versioning run on
the `main` branch of the Whitbread digital-monorepo (JIRA: CTECH-13822). The feature implements a
single, authoritative version for the entire monorepo, derived purely from git tags, that
increments with every such run — see the "Coalescing contract" note above for how a burst of
pushes maps to versioning runs.

The ordering contract is preserved from the original proposal: the next version **must be
computed before** the change log is rendered, because the change log/tag message is headed by
that version number.

1. **Compute** the next version from existing tags (minor bump, e.g. 1.3.0 → 1.4.0), or reuse the
   version already tagged on this commit (re-run safety).
2. **Render** a change log headed by that version, over the range `(PREVIOUS_TAG, github.sha]`.
3. **Tag** `github.sha` with an annotated, unprefixed tag carrying that change log as its message.
4. **Publish** the change log on the tag's GitHub Release page.
5. **Build** backend and frontend production deployment artifacts from that same commit, tagging
   images with the semantic version, by calling `ci-be.yaml` / `ci-fe-deploy.yaml` as reusable
   workflows.

This replaces both the pre-feature behaviour (merge to main builds an image tagged with a commit
SHA + timestamp) and an earlier, superseded two-phase design that wrote a `VERSION` file and
`CHANGELOG.md` in a bot commit back to `main` (see "Tag-Only Versioning" above for why that was
dropped).

## Glossary

- **Repo-wide version**: A single semantic version (X.Y.Z) that applies to all services and
  applications in the monorepo, derived solely from git tags (no version file).
- **Minor bump**: Incrementing the middle digit of a semantic version and resetting the patch to 0
  (e.g., 1.3.0 → 1.4.0).
- **Annotated tag**: A git tag object (not a lightweight ref) carrying a tagger identity and a
  message; used here so `--cleanup=verbatim` preserves Markdown headings in the change log.
- **Change log**: The per-version notes rendered by `generate-changelog.sh`, written to a
  runner-temp file (`${{ runner.temp }}/changelog.md`) and reused as both the tag message and the
  body on the tag's GitHub Release page. Not persisted to any file in the repository.
- **`CURRENT_VERSION`**: The highest existing `X.Y.Z` tag in the whole repository (via `sort -V`
  over `git tag --list`); the basis for the *next version* (Requirement 2.2).
- **`PREVIOUS_TAG`**: The highest existing `X.Y.Z` tag in `github.sha`'s own history, excluding
  tags on `github.sha` itself (`--no-contains`) (via `git tag --merged "$GITHUB_SHA" --no-contains
  "$GITHUB_SHA"`); the exclusive lower bound of the *change log range*. Computed before the re-run
  check and emitted on both the fresh and already-tagged paths (Requirement 4.4). Normally equal to
  `CURRENT_VERSION`; differs only when a higher tag sits on a commit outside `main`'s history
  (Requirement 5.8).
- **Re-run safety**: The rule that a commit already carrying a semantic-version tag reuses that
  version and skips change log generation and tag creation, making repeated workflow runs
  idempotent.
- **GitHub App identity**: The existing pipeline automation account
  (`vars.PIPELINE_APP_ID`/`secrets.PIPELINE_APP_PRIVATE_KEY`) used to mint a token via
  `actions/create-github-app-token`; its `app-slug` output derives the tagger identity
  (`<app-slug>[bot]`).
- **`main-deployment-gate`**: A job present in both `ci-be.yaml` and `ci-fe-deploy.yaml` that
  validates a `workflow_call`-supplied `semantic-version` and confirms its git tag points at
  `github.sha` before any production job in that workflow proceeds.
- **Reusable workflow call**: `ci-main-versioning.yaml` invoking `ci-be.yaml` /
  `ci-fe-deploy.yaml` via `uses: ./.github/workflows/<file>.yaml` with `workflow_call` inputs and
  `secrets: inherit`, rather than those workflows triggering off their own `push` event for `main`.
- **Coalescing contract**: The rule that GitHub's concurrency group keeps at most one pending
  versioning run per group, so a burst of pushes to `main` is versioned as its newest pending run
  only; the superseded runs' commits are still covered by that run's change log and still deployed
  (Requirement 12.4).

## Non-Goals / Out of Scope

The following items are explicitly excluded from this specification:
- DIT→UAT environment sync jobs and promotion workflows
- Deployment/<tag> branch flow and UAT bugfix patch versioning
- Hotfix/<tag> Demo P1/P2 emergency fix workflows
- Deployment to any environment other than dev (UAT, DIT, demo, perf, production) via this flow
- Per-service or per-stack independent versioning schemes
- Major version bumps and patch version bumps (only minor-on-push is in scope)
- A committed `VERSION` file or `CHANGELOG.md` — the version lives only in git tags, and the
  change log lives only in the annotated tag message and the body on the tag's GitHub Release page
- Any commit back to `main` from automation, and any commit-message marker or loop guard for it —
  there is no second push to guard against
- The `main-test` canary trigger and its duplicate/parallel GitOps sync path used during
  development of this feature — removed once production activation landed on `main`

Note: build-and-deploy on push to `main` **is in scope** (via the reusable-workflow calls to
`ci-be.yaml` / `ci-fe-deploy.yaml`), because the source proposal defines it as part of the same
atomic rule. This spec does not redefine what those pipelines deploy or where — only the version
they build with and the gate that authorizes their production path.

## Requirements

### Requirement 1: Main Branch Trigger, Single Run
**User Story:** As a development team, I want versioning automation to run exactly once per push
to `main`, so that version increments occur at the correct integration point without a second
triggering push.

#### Acceptance Criteria
1.1. THE system SHALL trigger `ci-main-versioning.yaml` only on `push` events to the `main` branch
1.2. THE system SHALL NOT trigger `ci-main-versioning.yaml` on pull requests, other branches,
tag pushes, or `workflow_dispatch`
1.3. THE system SHALL fail fast if the checked-out ref is not exactly `refs/heads/main`, even if
the trigger fired ("Validate fixed production target")
1.4. THE system SHALL perform versioning, tagging, change log publication, and the backend/frontend
reusable-workflow calls within a single triggered workflow run — there SHALL be no second,
automation-authored push to `main` and therefore no loop-guard/marker logic is needed
1.5. THE existing `ci-be.yaml` / `ci-fe-deploy.yaml` production build/deploy path SHALL run only
when invoked via `workflow_call` from `ci-main-versioning.yaml` with a `semantic-version` input —
`main` SHALL be removed from their own `push` trigger branches so a raw push to `main` does not
independently trigger their production path
1.6. THE pull-request / release / hotfix / perf build behaviour of `ci-be.yaml` /
`ci-fe-deploy.yaml` SHALL remain unchanged by this feature

### Requirement 2: Tag-Only Source of Truth
**User Story:** As a development team, I want a single authoritative version for the entire
monorepo derived purely from git tags, so that there is no separate version file to keep in sync
or accidentally diverge from reality.

#### Acceptance Criteria
2.1. THE system SHALL NOT maintain a root-level `VERSION` file or a `CHANGELOG.md` file
2.2. THE system SHALL derive the current version as the **highest** existing tag matching
`^[0-9]+\.[0-9]+\.[0-9]+$`, computed with `sort -V` over `git tag --list`, NOT with `git describe`
(`git describe` only considers tags reachable from the current `HEAD` and could under-report the
true highest version)
2.3. IF no semantic-version tag exists in the repository THEN the system SHALL initialise the
version at `INITIAL_VERSION` (`1.3.0`) — the first push to `main` after activation tags itself
automatically; no manual seed tag is required
2.4. THE system SHALL ignore existing per-stack versioning configurations (frontend Changesets,
backend Maven `${revision}`/hardcoded `1.0.0`, GraphQL package.json versions) for the purpose of
computing the repo-wide version
2.5. THE repo-wide semantic version SHALL be the value passed as the production image tag on the
`main` build path (via `SEMANTIC_VERSION` to `resolve-image-tag.sh`), replacing the
SHA+timestamp image tag used on other paths

### Requirement 3: Automatic Minor Version Increment
**User Story:** As a development team, I want the version to automatically increment on every
push to main, so that each integration has a unique version identifier.

#### Acceptance Criteria
3.1. THE system SHALL automatically increment the minor version on every versioning run for `main`
(i.e. every run of `ci-main-versioning.yaml` that is not skipped by the coalescing contract,
Requirement 12.4) that is not a re-run of an already-tagged commit
3.2. WHEN incrementing a minor version THE system SHALL reset the patch version to 0 (e.g.,
1.3.0 → 1.4.0)
3.3. THE system SHALL increment the minor version irrespective of commit content or conventional
commit types
3.4. THE system SHALL follow the pattern X.Y.Z → X.(Y+1).0 for all version transitions
3.5. IF the current highest tag is 1.3.0 THEN the next version SHALL be 1.4.0
3.6. WHEN parsing the minor component for arithmetic, THE system SHALL force base-10 parsing
(bash `10#` prefix) so a minor number with a leading zero (e.g. `08`) is never misinterpreted as
octal
3.7. IF the current highest tag does not match `^[0-9]+\.[0-9]+\.[0-9]+$` THEN
`compute-next-version.sh` SHALL fail hard with an actionable message, rather than silently
resetting to `INITIAL_VERSION`
3.8. THE system SHALL apply a collision-avoidance loop: WHILE the candidate version already
exists as a tag, THE system SHALL bump the minor version again before selecting it
3.9. THE system SHALL compute the next version BEFORE generating the change log, because the
change log/tag message is headed by the new version number

### Requirement 4: Re-run Safety (Idempotent Versioning)
**User Story:** As a platform team, I want a re-run of the versioning workflow on a commit that
was already versioned to be a safe no-op for version selection, so that re-running a failed or
partially-completed job does not create a duplicate or skipped version.

#### Acceptance Criteria
4.1. IF `github.sha` already carries one or more tags matching `^[0-9]+\.[0-9]+\.[0-9]+$` THEN
the system SHALL reuse the highest such tag as the version for this run rather than computing a
new one
4.2. WHEN reusing an already-tagged version THEN the system SHALL skip change log generation
and tag creation entirely
4.3. WHEN reusing an already-tagged version THEN the system SHALL still proceed to publish (or
re-confirm) the change log on the tag's GitHub Release page and to call the backend/frontend
reusable workflows with that version
4.4. WHEN reusing an already-tagged version THEN the system SHALL still output `PREVIOUS_TAG` (the
highest earlier `X.Y.Z` tag in `github.sha`'s history, excluding the reused tag) so the step
summary (Requirement 7.6) reports the correct previous tag

### Requirement 5: Change Log Generation
**User Story:** As a development team, I want an automatically generated change log for each new
version, so that I can track what changes are included, without maintaining a checked-in
changelog file.

#### Acceptance Criteria
5.1. THE system SHALL generate a change log for each newly computed version (skipped per
Requirement 4.2 on a re-run), using the version already computed in Requirement 3 as the heading
5.2. THE system SHALL render the change log using `conventional-changelog-cli` pinned to an
**exact** version (`5.0.0`, no version range), with the `conventionalcommits` preset
5.3. THE system SHALL invoke the tool with `--release-count 1` and a `--context` file supplying
the already-computed version, so the rendered heading is that version rather than being derived
from any `package.json`
5.4. THE system SHALL write the rendered change log to a file in the runner's temp directory
(`NOTES_FILE`, `${{ runner.temp }}/changelog.md`), NOT to a `CHANGELOG.md` in the repository
5.5. IF there is no previous version tag (first version) THEN the system SHALL write a short
"First versioned change log of the monorepo." entry instead of running the tool against the
entire repository history
5.6. IF the tool produces no output for a version that does have a previous tag THEN the system
SHALL fall back to writing a minimal dated heading (`## <version> (<date>)`) rather than leaving
the notes file empty
5.7. THE system SHALL NOT generate the change log before the next version is known (ordering
contract, Requirement 3.9)
5.8. THE system SHALL compute two distinct tag references before generating the change log:
`CURRENT_VERSION` (the highest `X.Y.Z` tag in the whole repository, used for the next version per
Requirement 2.2) and `PREVIOUS_TAG` (the highest `X.Y.Z` tag in `github.sha`'s own history,
excluding tags on `github.sha` itself (`--no-contains`), via `git tag --merged "$GITHUB_SHA"
--no-contains "$GITHUB_SHA" | sort -V | tail -n1`, used only as the change log's lower bound);
these normally coincide and differ only if a higher tag sits on a commit outside `main`'s history
5.9. THE system SHALL pass `PREVIOUS_TAG` and `TO_REF` (`github.sha`) to `generate-changelog.sh`,
and the change log SHALL cover exactly the commit range `(PREVIOUS_TAG, TO_REF]`
5.10. IF `PREVIOUS_TAG` is non-empty and is NOT an ancestor of `TO_REF`
(`git merge-base --is-ancestor "$PREVIOUS_TAG" "$TO_REF"` fails) THEN `generate-changelog.sh`
SHALL exit with status `2` and refuse to render a change log over an unrelated range
5.11. THE system SHALL pin the rendered range explicitly by generating a `--config` file whose
`gitRawCommitsOpts.from`/`.to` read from the `CHANGELOG_FROM`/`CHANGELOG_TO` environment variables
(never interpolated directly into the generated JavaScript), rather than letting
`conventional-changelog-cli` select its own "latest tag" as the range start
5.12. THE first-version case (Requirement 5.5) SHALL remain unaffected by Requirements 5.8–5.11:
an empty `PREVIOUS_TAG` SHALL still produce the short first-version entry with no CLI invocation
and no config file generated

### Requirement 6: Git Tag Creation
**User Story:** As a development team, I want an annotated git tag created for each version
directly on the pushed commit, so that I can reference specific versions for builds and
rollbacks without any intermediate marker commit.

#### Acceptance Criteria
6.1. THE system SHALL create the tag (skipped per Requirement 4.2 on a re-run) only after the
change log has been rendered
6.2. THE tag name SHALL be exactly the version number with no prefix (e.g., `1.4.0`)
6.3. THE tag SHALL point directly at `github.sha` — the pushed commit itself, NOT a new
marker commit
6.4. THE tag SHALL be an **annotated** tag created with `--cleanup=verbatim`, so Markdown headings
in the change log (used as the tag message) are preserved rather than stripped as comments
6.5. THE tagger identity SHALL be derived from the GitHub App token's `app-slug` output
(`<app-slug>[bot]`) as the git `user.name`/`user.email`; IF the `app-slug` output is empty THEN
the system SHALL fail fast with an actionable error before attempting to tag
6.6. THE system SHALL push ONLY the tag ref (`refs/tags/<version>`) to the remote — there SHALL be
no corresponding branch push, since no commit is created on `main`
6.7. THE tag push SHALL go through the `retry-push.sh` retry helper (see Requirement 13.3)
6.8. IF tag creation or the tag push fails THEN the system SHALL fail the job and report the
failure; because the tag is the only artifact of this step, there is no partially-written commit
state to roll back

### Requirement 7: Change Log Publication on GitHub
**User Story:** As a development team and QA, I want each version's change log published on its
tag's GitHub Release page, so that versions and their changes are discoverable through the
GitHub UI/API without a repository file.

Note: the GitHub Releases REST API's object is called a "release", but here it is used only to
carry the tag and its change log — nothing more. The acceptance criteria below name the API
precisely (`GitHub Release`, the `/releases` endpoint, `already_exists`) where they describe that
API surface, and "change log" everywhere else.

#### Acceptance Criteria
7.1. THE system SHALL publish a GitHub Release for each version via the GitHub Releases REST API
(`POST /repos/{owner}/{repo}/releases`), using the GitHub App token
7.2. THE release's `tag_name` and `name` SHALL both be the version string, and its `body` SHALL be
the rendered change log
7.3. WHEN reusing an already-tagged version (Requirement 4) THEN the system SHALL read the change
log back from the existing tag's contents (`git tag --list --format='%(contents)'`) if the notes
file is not already present, so the release body is populated even when change log generation was
skipped
7.4. IF the release-creation API call returns HTTP 422 with an `already_exists` error code THEN
the system SHALL treat this as success (idempotent re-run), NOT as a failure
7.5. IF the release-creation API call fails with any other non-2xx status THEN the system SHALL
fail the job and log the response body
7.6. THE system SHALL report a step summary including the version, previous tag, tagged commit,
and the URL of the tag's GitHub Release page

### Requirement 8: Reusable-Workflow Orchestration to Backend and Frontend
**User Story:** As a development team, I want the freshly tagged version automatically built and
deployed by the existing backend/frontend pipelines, so that the built artifacts and dev
environment reflect the new version without a duplicate build/deploy path.

#### Acceptance Criteria
8.1. AFTER the version job completes successfully, THE system SHALL call `ci-be.yaml` as a
reusable workflow (`uses: ./.github/workflows/ci-be.yaml`) with `semantic-version` set to the
computed/reused version and `secrets: inherit`
8.2. AFTER the version job completes successfully, THE system SHALL call `ci-fe-deploy.yaml` as a
reusable workflow (`uses: ./.github/workflows/ci-fe-deploy.yaml`) with `semantic-version` set to
the computed/reused version and `secrets: inherit`
8.3. THE calling jobs (`backend`, `frontend`) SHALL each declare the union of permissions that
the respective called workflow's jobs require, because a called workflow cannot hold more
permissions than the calling job grants
8.4. `ci-be.yaml` AND `ci-fe-deploy.yaml` SHALL each declare a `workflow_call` trigger with a
**required** `semantic-version` string input
8.5. `main` SHALL be removed from the `push` trigger `branches` list of both `ci-be.yaml` and
`ci-fe-deploy.yaml`, so their production path never runs from their own direct push trigger
8.6. EACH of `ci-be.yaml` and `ci-fe-deploy.yaml` SHALL use a **literal** concurrency group name
(`BE CI Orchestrator-${{ github.ref }}` and `CI FE Deploy-${{ github.ref }}` respectively) rather
than `${{ github.workflow }}`, because inside a called workflow `github.workflow` resolves to the
**caller's** name (`Main Production Versioning`), which would otherwise incorrectly merge the
backend and frontend concurrency groups

### Requirement 9: Main-Deployment-Gate Validation (Backend and Frontend)
**User Story:** As a platform team, I want the backend and frontend pipelines to independently
verify the version and commit they were called with before doing any production work, so that a
malformed or spoofed `workflow_call` input can never produce an unversioned or mismatched
deployment.

#### Acceptance Criteria
9.1. EACH of `ci-be.yaml` and `ci-fe-deploy.yaml` SHALL implement a `main-deployment-gate` job
that runs only `if: github.event_name == 'push' && github.ref == 'refs/heads/main'`
9.2. THE `main-deployment-gate` job SHALL fail IF the supplied `semantic-version` input is empty
9.3. THE `main-deployment-gate` job SHALL fail IF the supplied `semantic-version` does not match
`^[0-9]+\.[0-9]+\.[0-9]+$`
9.4. THE `main-deployment-gate` job SHALL fail IF `github.sha` is not a well-formed 40-character
hexadecimal commit SHA
9.5. THE `main-deployment-gate` job SHALL fail IF `refs/tags/<semantic-version>^{commit}` does not
resolve to exactly `github.sha` (i.e., the claimed tag does not actually point at the commit being
built)
9.6. ON success, THE `main-deployment-gate` job's `version-tag` step SHALL output
`semantic-version` and `checkout-sha` for downstream jobs to consume
9.7. ALL downstream production jobs in `ci-be.yaml` / `ci-fe-deploy.yaml` (build, deploy, and
related jobs) SHALL depend on `main-deployment-gate` succeeding before performing any production
work; non-main-push triggers (PR, release/*, hotfix/*, perf/*) SHALL bypass this gate unaffected

### Requirement 10: Main-Line Image Tag Equals the Semantic Version
**User Story:** As a development team, I want the production image tag to be exactly the
repo-wide semantic version, so that deployed artifacts are unambiguously traceable to a version.

#### Acceptance Criteria
10.1. WHEN `resolve-image-tag.sh` is invoked on the main-line path with a well-formed
`SEMANTIC_VERSION` (matching `^[0-9]+\.[0-9]+\.[0-9]+$`) THEN it SHALL emit that version verbatim
as the image tag, with no branch, SHA, or timestamp component appended
10.2. THE pull-request/ephemeral image-tag path (`pr-<number>-<sha7>`) SHALL be unaffected by the
`SEMANTIC_VERSION` main-line branch
10.3. THE existing non-semantic main-line fallback (`<app-version>-<ref>-<sha>-<timestamp>`) SHALL
remain available for any caller that does not supply `SEMANTIC_VERSION`

### Requirement 11: Authentication and Permissions
**User Story:** As a platform team, I want the versioning automation to use secure, authorized
git and API operations, so that tag creation and change log publication respect repository
protections.

#### Acceptance Criteria
11.1. THE system SHALL mint a GitHub App token via `actions/create-github-app-token` using
`vars.PIPELINE_APP_ID` / `secrets.PIPELINE_APP_PRIVATE_KEY` with `permission-contents: write`
11.2. THE system SHALL use that token for the checkout, the tag push, and the GitHub Releases API
call
11.3. THE version job SHALL declare `permissions: contents: read` at the job level (write access
is obtained solely through the minted App token, not through elevated job permissions)
11.4. IF the App token generation fails THEN the system SHALL surface the standard actionable
error from `create-github-app-token` and the job SHALL fail before any tag or change log
publication is attempted

### Requirement 12: Concurrency Control
**User Story:** As a development team, I want overlapping pushes to `main` to be serialised, so
that two runs cannot race to tag the same version or interleave partially.

#### Acceptance Criteria
12.1. THE `ci-main-versioning.yaml` workflow SHALL declare a workflow-level `concurrency` group
named literally `production-main-versioning`
12.2. THE concurrency group SHALL set `cancel-in-progress: false`, so a queued run waits for the
currently running one to finish (and therefore always sees its tag) rather than being cancelled
12.3. `ci-be.yaml` and `ci-fe-deploy.yaml` SHALL each use their own literal, per-workflow
concurrency group (Requirement 8.6), independent of the versioning workflow's group
12.4. **Coalescing contract:** BECAUSE the concurrency group keeps at most one pending run per
group, WHEN several pushes land to `main` while a versioning run is in progress, THE system SHALL
version only the newest pending run and replace (not run) the intermediate ones; no commit SHALL
be lost, because the newest commit's tag and change log cover every commit since the previous tag
(including those whose own run was superseded) and the backend/frontend pipelines deploy that
newest commit — versions map to deployed states of `main`, not to individual pushes

### Requirement 13: Failure Handling and Observability
**User Story:** As a development team, I want clear visibility into versioning workflow status
and safe handling of transient failures, so that I can identify and resolve issues quickly without
manual repository surgery.

#### Acceptance Criteria
13.1. THE version job SHALL set `timeout-minutes: 10`
13.2. THE system SHALL log detailed status for each step (target validation, token generation,
version computation, change log generation, tagging, change log publication, summary)
13.3. THE system SHALL retry the tag push through `retry-push.sh`: up to 3 attempts with
exponential backoff (`base_delay * 2^(attempt-1)`, default base delay 2s), then hard failure
13.4. THE system SHALL report workflow failures through GitHub Actions status checks (job/step
failure), including from the `main-deployment-gate` job in `ci-be.yaml` / `ci-fe-deploy.yaml`
13.5. THE system SHALL provide actionable error messages for known failure scenarios (bad target
ref, empty app-slug, malformed tag, failed change log publication API call, failed
`main-deployment-gate` validation)

### Requirement 14: Version Traceability
**User Story:** As a QA team member, I want to identify exactly what changes are included in a
version and where it came from, so that I can test the correct features and track deployments.

#### Acceptance Criteria
14.1. THE git tag SHALL provide a direct reference to the exact commit it versions (`github.sha`,
never a synthetic marker commit)
14.2. THE tag's GitHub Release page SHALL list the rendered change log for that version
14.3. THE version tag and its change log SHALL be discoverable through git history and the GitHub
Releases interface
14.4. THE `main-deployment-gate` job's tag-equals-commit check (Requirement 9.5) SHALL provide a
machine-verifiable audit trail from the built artifact back to the exact tagged commit
14.5. THE version information (semantic version, checkout SHA) SHALL be available to the
backend/frontend pipelines via `workflow_call` inputs and `main-deployment-gate` outputs

## Known Limitations

- **Backend artifact version is not threaded through.** The Maven build still produces
  `${SERVICE_NAME}-1.0.0.jar` and the Docker build still passes `--build-arg APP_VERSION=1.0.0`
  (both hardcoded in `build-service.yaml`). Only the **image tag** carries the repo-wide semantic
  version; the JAR filename and the in-image `APP_VERSION` remain `1.0.0` regardless of the actual
  deployed version.
- **No support for versions beyond 64-bit bash arithmetic.** `compute-next-version.sh` computes
  the next minor with `$(( 10#$minor + 1 ))`, a bash arithmetic expression backed by signed 64-bit
  integers. An existing tag whose minor (or major) component exceeds that range would overflow
  rather than bump correctly; this is not detected or guarded against.
- **First run needs no manual seed tag.** Because `INITIAL_VERSION` is `1.3.0` and is used
  automatically whenever no `^[0-9]+\.[0-9]+\.[0-9]+$` tag exists yet, teams do not need to
  pre-create a `1.3.0` (or any) tag before this workflow's first run on `main`.
