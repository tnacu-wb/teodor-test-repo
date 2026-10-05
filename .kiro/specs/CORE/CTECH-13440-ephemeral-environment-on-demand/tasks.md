---
parent_requirements: ".kiro/specs/CORE/CTECH-13440-ephemeral-environment-on-demand/requirements.md"
parent_design: ".kiro/specs/CORE/CTECH-13440-ephemeral-environment-on-demand/design.md"
jira: CTECH-13440
---
# Implementation Plan: On-Demand Ephemeral Frontend Environments

## Overview

Convert frontend ephemeral deployment from "always on every PR" to on-demand, via two entry
points that funnel into one reusable `workflow_call` deploy workflow. The plan builds the pure,
independently-testable **gate decision** module first (with its property/unit tests), then the
reusable deploy workflow, then wires the two entry points (POC gate in `ci-fe.yaml`, comment
trigger in `ci-fe-ephemeral-comment.yaml`), and finishes with feedback/cleanup verification and
static/integration checks.

Implementation surfaces:
- **Node/JavaScript** for the pure `decide()` gate module under `.github/frontend/scripts/`
  (mirroring how `identity.sh` is a standalone script), tested with `fast-check`.
- **GitHub Actions YAML** for the reusable and entry-point workflows.

## Tasks

- [x] 1. Create the pure gate decision module
  - _Note: the `ephemeral-gate.js` gate module and its property tests (1.2–1.5) were dropped
    from the shipped change. The gate decision logic lives inline in the entry-point workflows
    (the `gate-ephemeral` bash step in `ci-fe.yaml` and the `authorize` github-script job in
    `ci-fe-ephemeral-comment.yaml`); the design pseudo-code remains the reference spec._
  - [x] 1.1 Implement `decide(TriggerEvent) -> GateDecision` as a pure Node module
    - _(Not shipped: the standalone `ephemeral-gate.js` module was removed; this logic is
      implemented inline in the two entry-point workflows instead.)_
    - Create `.github/frontend/scripts/ephemeral-gate.js` exporting a pure `decide(event)` function
    - Implement constants `POC_SUFFIXES = ["-JIRA-XX", "-Issues-XX", "-TEAMS-XX"]`,
      `TRIGGER_PHRASE = "Ephemeral UP"`, `SUPPORTED_BASES = ["main", "test-deployment"]`,
      `WRITE_LEVELS = ["write", "maintain", "admin"]`
    - `pull_request` branch: return `{ shouldDeploy: true, reason: "poc-suffix" }` iff base is
      supported AND head ends with a POC suffix; else `{ shouldDeploy: false, reason: "not-triggered" }`
    - `issue_comment` branch: exact trimmed, case-sensitive equality on the phrase; then
      permission, then open state, then supported base; on full pass return
      `{ shouldDeploy: true, reason: "authorised-comment", checkoutRef, baseOutOfSync }`
    - Derive `checkoutRef = mergeable === true ? mergeRef : headSha` and
      `baseOutOfSync = mergeable !== true`
    - No I/O, no GitHub API calls — inputs come in as a plain `TriggerEvent` object
    - _Requirements: 1.1, 1.4, 2.1, 3.1, 3.4, 3.7, 3.8, 6.1, 6.2, 6.3_

  - [ ]* 1.2 Write property test for the pull-request POC gate
    - _(Dropped: targets the removed `ephemeral-gate.js` `decide()` module; not shipped.)_
    - **Feature: CTECH-13440-ephemeral-environment-on-demand, Property 1: Pull-request POC gate**
    - Use `fast-check`, minimum 100 iterations; do not hand-roll generators
    - Generators: random base refs (supported + unsupported) × random branch names (with/without
      each POC suffix, including suffix-as-substring-not-ending); assert the iff on `shouldDeploy`
    - **Validates: Requirements 1.1, 1.4, 2.1**

  - [ ]* 1.3 Write property test for the comment-path gate
    - _(Dropped: targets the removed `ephemeral-gate.js` `decide()` module; not shipped.)_
    - **Feature: CTECH-13440-ephemeral-environment-on-demand, Property 2: Comment-path gate**
    - Use `fast-check`, minimum 100 iterations
    - Generators: random comment bodies (exact phrase with random surrounding whitespace, case
      variants, substrings, extra tokens) × permission levels × PR states × base refs; assert the iff
    - **Validates: Requirements 3.1, 3.4, 6.1, 6.2, 6.3**

  - [ ]* 1.4 Write property test for merge-ref versus head derivation
    - _(Dropped: targets the removed `ephemeral-gate.js` `decide()` module; not shipped.)_
    - **Feature: CTECH-13440-ephemeral-environment-on-demand, Property 3: Merge-ref versus head derivation**
    - Use `fast-check`, minimum 100 iterations
    - Generators: authorised comment events × `mergeable ∈ {true, false, null}`; assert
      `checkoutRef`/`baseOutOfSync` mapping and that no merged/rebased ref is fabricated
    - **Validates: Requirements 3.7, 3.8**

  - [ ]* 1.5 Write unit / example tests for the gate decision
    - _(Dropped: targets the removed `ephemeral-gate.js` `decide()` module; not shipped.)_
    - Phrase anchors: `Ephemeral UP` → deploy; `ephemeral up`, `Ephemeral UPGRADE`,
      `Please Ephemeral UP now` → no deploy; ` Ephemeral UP ` → trims → deploy
    - POC-suffix anchors: `feature/x-JIRA-XX`, `feature/x-Issues-XX`, `feature/x-TEAMS-XX` deploy;
      `feature/x` does not
    - _Requirements: 2.1, 1.1, 3.4_

- [x] 2. Verify naming determinism against the existing identity deriver
  - [x] 2.1 Add a naming-determinism check binding to `deployment-identity/identity.sh`
    - Add a Node/shell test harness that invokes the existing
      `.github/frontend/actions/deployment-identity/identity.sh` (unchanged) with fixed
      `(app, branch, pr-number)` inputs and captures `release-name`, `environment-name`, `host-name`
    - Assert derivation depends only on `(app, branch, pr-number)` and is independent of a
      `trigger-source` input, so both entry points produce identical names
    - _Requirements: 4.1, 4.5, 5.3_

  - [ ]* 2.2 Write property test for naming determinism independent of trigger source
    - **Feature: CTECH-13440-ephemeral-environment-on-demand, Property 4: Naming determinism independent of trigger source**
    - Use `fast-check`, minimum 100 iterations
    - Generators: random app ∈ {`premier-inn`, `business-booker`, `ccui`} × random branch × random
      pr-number × trigger-source ∈ {`pull_request`, `comment`}; assert identical
      `(release-name, environment-name, host-name)` across trigger sources (ideally exercised
      against `identity.sh` output)
    - **Validates: Requirements 3.5, 4.5, 5.3**

- [x] 3. Checkpoint - Ensure all gate-logic tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 4. Create the reusable deploy workflow `ci-fe-ephemeral-deploy.yaml`
  - [x] 4.1 Define the `workflow_call` interface and inputs
    - Create `.github/workflows/ci-fe-ephemeral-deploy.yaml` triggered by `workflow_call`
    - Inputs: `pr-number` (req), `branch-name` (req), `trigger-source` (req),
      `business-booker-image-tag`/`ccui-image-tag`/`premier-inn-image-tag` (optional, default ""),
      `checkout-ref` (optional, default ""), `base-out-of-sync` (optional bool, default false)
    - Declare `secrets: inherit` compatibility for callers
    - _Requirements: 3.2, 4.1, 4.2, 4.3, 4.4_

  - [x] 4.2 Add the self-build job for the comment path
    - Add a `build` job gated `if: inputs.premier-inn-image-tag == ''` that runs
      `.github/frontend/actions/build` + `.github/frontend/actions/container-image` from
      `checkout-ref` (push to DockerHub + ECR)
    - Skipped automatically when the caller already supplied image tags (POC path)
    - _Requirements: 3.2, 4.2_

  - [x] 4.3 Add the `resolve-tags` job
    - Emit effective image tags per app: passed-in inputs when present, else the `build` job outputs
    - Downstream deploy reads only from `resolve-tags` so the matrix is identical for both entry points
    - _Requirements: 3.2, 3.5_

  - [x] 4.4 Move the per-app deploy matrix into the reusable workflow verbatim
    - Add the per-app matrix (`fail-fast: false`):
      `deployment-identity` → `setup-helm` → `clear-stale-helm-op.sh` → `helm-deploy.sh`
    - Move the `environment:` block (`name: <app>-PR-<n>`, `url: https://<host>`) with the deploy job
      so environments stay registered on the Environments page
    - Preserve the PR-URL preview comment step, suppressed for `-JIRA-XX`
      (`if: !endsWith(branch-name, '-JIRA-XX')`), and the linked-issue verification comment
    - _Requirements: 1.4, 2.3, 4.3, 4.5, 7.1, 7.4_

  - [x] 4.5 Move the Jira deployment report job into the reusable workflow
    - Move `report-ephemeral-deployment-jira` verbatim, still gated on
      `endsWith(branch-name, '-JIRA-XX')`, so it fires for `-JIRA-XX` branches from either entry point
    - _Requirements: 2.4, 7.2_

  - [ ]* 4.6 Add static-lint assertions for the reusable workflow
    - `actionlint` over `ci-fe-ephemeral-deploy.yaml`
    - Assert the deploy matrix contains all three apps and reuses the existing actions/scripts unchanged
    - _Requirements: 1.4, 4.1, 4.2_

- [x] 5. Wire entry point 1: POC gate in `ci-fe.yaml`
  - [x] 5.1 Add the `gate-ephemeral` decision job
    - Add a `gate-ephemeral` job (`runs-on: ubuntu-slim`, `if: github.event_name == 'pull_request'`)
      that outputs `should-deploy`
    - Match base ∈ {`main`, `test-deployment`} AND head ending in `-JIRA-XX`/`-Issues-XX`/`-TEAMS-XX`
      (reusing the same suffix set as the gate module)
    - _Requirements: 1.1, 2.1_

  - [x] 5.2 Replace the unconditional deploy job with a call to the reusable workflow
    - Replace `deploy-frontend-ephemeral` body with `uses: ./.github/workflows/ci-fe-ephemeral-deploy.yaml`
    - `needs: [image-build, gate-ephemeral]`; `if: always() && image-build success &&
      gate-ephemeral.outputs.should-deploy == 'true'`
    - Pass `pr-number`, `branch-name = github.head_ref`, the three `image-build` image tags,
      `trigger-source: pull_request`, `secrets: inherit`
    - Leave `build`/`image-build` unchanged so POC branches still produce image tags; leave the
      existing `-Issues-XX`/`-JIRA-XX` skip guards on dependency-review/code-analysis/quality/prisma
      jobs untouched (do NOT add `-TEAMS-XX` to those)
    - _Requirements: 1.2, 1.3, 2.2, 2.3, 2.5, 3.2_

  - [ ]* 5.3 Add static-lint assertions for the modified `ci-fe.yaml`
    - `actionlint` over `ci-fe.yaml`
    - Assert the deploy job references `ci-fe-ephemeral-deploy.yaml` and the other jobs' POC skip
      guards are unchanged
    - _Requirements: 2.2, 2.3_

- [x] 6. Checkpoint - Ensure workflows lint and the reusable path is wired
  - Ensure all tests pass, ask the user if questions arise.

- [x] 7. Wire entry point 2: comment trigger `ci-fe-ephemeral-comment.yaml`
  - [x] 7.1 Create the workflow scaffold with trigger, permissions, and concurrency
    - Create `.github/workflows/ci-fe-ephemeral-comment.yaml` on `issue_comment: [created]`
    - `permissions: contents: read, pull-requests: write, issues: write` (no production environment)
    - `concurrency: fe-ephemeral-comment-${{ github.event.issue.number }}`, `cancel-in-progress: false`
    - _Requirements: 3.5, 6.4_

  - [x] 7.2 Implement the `authorize` job (phrase + permission + open/base + ref resolution)
    - Cheap pre-filter `if`: comment is on a PR AND trimmed body `== 'Ephemeral UP'`
    - Check commenter permission via `getCollaboratorPermissionLevel`; authorised iff
      `write`/`maintain`/`admin`
    - When authorised, `pulls.get` to confirm open + supported base (fail fast otherwise), then
      output `pr-number`, `head-ref`, `base-ref`, `checkout-ref`
      (always the immutable PR head SHA — never the mutable `refs/pull/<n>/merge` — to close the
      TOCTOU gap), and `base-out-of-sync`
    - Authorisation runs before any checkout/build
    - _Requirements: 3.1, 3.3, 3.4, 3.7, 3.8, 6.1, 6.2, 6.3, 6.4_

  - [x] 7.3 Implement the `acknowledge` and `ignored` jobs
    - `acknowledge` (needs authorize, authorized == 'true'): add a `rocket` reaction to the comment
    - `ignored` (authorized == 'false'): add a `confused` reaction + `core.warning`, no build/deploy
    - _Requirements: 3.6, 6.2_

  - [x] 7.4 Implement the `notify-out-of-sync` job
    - Runs when authorized AND `base-out-of-sync == 'true'`
    - Post a PR comment that the branch is not in sync with `main` and is being deployed as-is;
      never merge or rebase
    - _Requirements: 3.8_

  - [x] 7.5 Wire the `deploy` job to the reusable workflow
    - `needs: [authorize, acknowledge]`, `if: authorized == 'true'`
    - `uses: ./.github/workflows/ci-fe-ephemeral-deploy.yaml` with
      `pr-number`, `branch-name = head-ref`, `checkout-ref`, `base-out-of-sync`,
      `trigger-source: comment`, `secrets: inherit`
    - Same `branch-name` ensures identical release/environment naming — a comment on a POC branch
      upgrades the same release rather than creating a duplicate
    - _Requirements: 3.1, 3.2, 3.5, 3.6_

  - [ ]* 7.6 Add static-lint assertions for the comment workflow
    - `actionlint` over `ci-fe-ephemeral-comment.yaml`
    - Assert it declares only `contents: read` + PR/issue write and no production environment, and
      that it references `ci-fe-ephemeral-deploy.yaml`
    - _Requirements: 3.2, 6.4_

- [x] 8. Verify feedback, cleanup, and end-to-end wiring
  - [ ]* 8.1 Write integration tests for the no-deploy and POC paths
    - Non-POC PR, no comment → no Helm release, no environment, no preview comment; build/scans still run
    - POC branch push → redeploy from the `pull_request` pipeline
    - _Requirements: 1.2, 1.3, 2.5_

  - [ ]* 8.2 Write integration tests for the comment path
    - Authorised `Ephemeral UP` on a mergeable PR → deploy from the pinned head SHA, reaction added, preview URL posted
    - Authorised comment on a conflicted PR → deploy head as-is + out-of-sync comment
    - Unauthorised comment → no deploy, `confused` reaction
    - Comment on a POC-suffixed PR → same release upgraded, no duplicate environment
    - _Requirements: 3.1, 3.3, 3.5, 3.6, 3.7, 3.8, 6.1, 6.2_

  - [ ]* 8.3 Verify cleanup and status reporting are unaffected
    - PR close after each trigger path → release + environment removed; PR that never deployed → cleanup no-op
    - Confirm `cleanup-pr-fe.yaml` / `cleanup-preview` remain unchanged and target both paths by naming
    - Confirm Teams workflow status reporting still fires for the frontend pipeline
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 7.3, 7.5_

- [-] 9. Final checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional (tests, static-lint, integration checks) and can be skipped
  for a faster MVP; core implementation tasks are never optional.
- Each task references specific requirements (and, for test tasks, a specific design correctness
  property) for traceability.
- The only genuinely pure, input-varying logic is the gate decision and naming derivation —
  those carry the property-based tests (P1–P4). Everything else is CI orchestration over existing,
  unchanged actions, covered by static-lint and integration checks.
- Cleanup workflows are unchanged; task 8.3 verifies rather than modifies them.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "2.1"] },
    { "id": 1, "tasks": ["1.2", "1.3", "1.4", "1.5", "2.2", "4.1"] },
    { "id": 2, "tasks": ["4.2", "4.3"] },
    { "id": 3, "tasks": ["4.4", "4.5"] },
    { "id": 4, "tasks": ["4.6", "5.1"] },
    { "id": 5, "tasks": ["5.2", "7.1"] },
    { "id": 6, "tasks": ["5.3", "7.2"] },
    { "id": 7, "tasks": ["7.3", "7.4", "7.5"] },
    { "id": 8, "tasks": ["7.6", "8.1", "8.2", "8.3"] }
  ]
}
```
