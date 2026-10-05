# Requirements Document

## Introduction

The operational tool `ddb-stream-basket-to-dt` (the PI basket DynamoDB stream → Dynatrace business-events processor) currently lives in the standalone repository `whitbread-eos/ddb-stream-basket-to-dt` and is built and released by that repository's own CI. This feature moves the tool into `digital-monorepo` under `tools/python/ddb-stream-basket-to-dt/` as a history-preserving import, and builds the monorepo-side CI it needs so that the monorepo becomes the source of truth for the tool's **code and image build** while the gitops repositories continue to own deployment.

This is Jira issue **SRE-338**. It is a clean cut-over, not a code-only copy: a copy that left the standalone repo releasing in parallel was considered and rejected because it creates two sources of truth and invites the drift already visible on the Java side. The tool is small, self-contained, and has no Maven coupling, so it doubles as the pilot for `tools/` CI in the monorepo without touching the Java estate.

The scope of this spec is **phase 1 (land the code)** and **phase 2 (CI in the monorepo)**. Phase 3 (automated deployment to prod) and its hard prerequisite — the prod Docker Hub → ECR image switch tracked by SRE-319 — are documented here as sequenced follow-on work but are **out of scope** for the implementation this spec drives. Phase 4 (steering under `.kiro/`) is already merged to `main` and is out of scope except as context.

### Authoritative reference: the merged steering files

Phase 4 landed three steering files that are now the authoritative description of the Tool's behaviour on `main`, and this spec defers to them rather than re-deriving the tool's semantics:

- `.kiro/steering/stacks/tools.md` — conventions for the `tools/` stack (toolchain independence, `uv` not `pip`, stdlib `unittest`, non-root container at uid/gid 9001, ECR pull-through base images, and that tools are built by nothing until a path classification and build workflow are added).
- `.kiro/steering/tools/python/ddb-stream-basket-to-dt/product.md` — the event contract, the PII allow-list, at-least-once delivery, and the `LATEST`-on-restart gap that argues for a **gated** prod bump rather than a fully automatic one.
- `.kiro/steering/tools/python/ddb-stream-basket-to-dt/tech.md` and `structure.md` — the runtime/config, exit codes, Dockerfile conventions (`BUILD_TIMESTAMP`, pip removed), the shard-per-process model, the iterator-advance-only-on-success rule, and the test seams.

Where this document states a fact about the Tool's behaviour, container, or deployment, the cited steering file is the source of truth; if the two ever disagree, the steering wins and this spec is corrected.

The standalone repository must remain operational and releasable throughout, and is archived (not deleted) only after the monorepo pipeline has produced an image that has run in prod — an event that falls under phase 3 and is therefore out of scope here.

## Glossary

- **Monorepo**: The repository `whitbread-eos/digital-monorepo`, containing the stacks `backend`, `frontend`, `graphql`, `mobile`, `qa`, `infra`, and `tools`.
- **Tool**: The `ddb-stream-basket-to-dt` operational utility being migrated.
- **Tool_Directory**: The target path `tools/python/ddb-stream-basket-to-dt/` in the Monorepo after import.
- **Source_Repository**: The standalone Git repository `whitbread-eos/ddb-stream-basket-to-dt` that currently hosts the Tool and continues to exist and release during the transition.
- **Upstream_SHA**: The pre-filter tip commit of the Source_Repository branch being imported — the canonical upstream commit, recorded for traceability but not reachable from the imported history (because `git filter-repo` rewrites SHAs).
- **Imported_SHA**: The post-filter tip commit that `git subtree add` imports and records in its `git-subtree-split` trailer.
- **Tools_Stack**: The `tools/` top-level directory of the Monorepo, holding internal operational tools that own their own toolchains and are not part of the Maven reactor. At migration time its only other member is `opera-ohip-app`.
- **BE_Orchestrator**: The backend CI workflow `.github/workflows/ci-be.yaml`, which dispatches per-module build jobs via change-detection scripts.
- **Change_Detection**: The scripts under `.github/workflows/scripts/` that classify changed paths into build matrices. The main-line detector is `detect-all.sh`; the PR detector is `detect-changed-eph.sh` (which sources `detect-changed-modules.sh`).
- **Tool_CI**: The reusable/dispatched workflow(s) in the Monorepo that test and image-build a Python tool under `tools/`.
- **Image_Build_Template**: The reusable workflow `whitbread-eos/wbd-workflows-templates/.github/workflows/ci-image-build-python.yaml`, used today by the Source_Repository to build and push the Tool's image.
- **ECR**: The Whitbread AWS Elastic Container Registry account `327347623470` in `eu-west-1`, target of the Tool's current image push.
- **Prisma_Scan**: The Palo Alto Prisma Cloud image vulnerability scan gate applied by the Image_Build_Template.
- **Build_Timestamp**: The `BUILD_TIMESTAMP` Docker build argument written to `/build-timestamp.txt` in the image and surfaced on every Dynatrace event as `build.timestamp`.
- **Gitops_Repositories**: `microservices-gitops` (nonprod) and `microservices-gitops-prod` (prod), which own the Tool's Kustomize deployment manifests and image tags.

## Requirements

### Requirement 1: History-Preserving Import into tools/

**User Story:** As a platform engineer, I want the Tool imported into the Monorepo with its full Git history preserved, so that authorship, commit messages, and timestamps remain auditable under the new path and match the Monorepo's existing import convention.

#### Acceptance Criteria

> **On SHAs and `git filter-repo`.** The exclusion in criterion 5 is performed with `git filter-repo`, which **rewrites commit SHAs** (and drops commits that touched only excluded paths). Two distinct SHAs therefore matter and are named here to avoid conflating them:
> - **Upstream_SHA** — the pre-filter tip of the Source_Repository branch (the canonical upstream commit). Recorded for traceability but not present in the imported history.
> - **Imported_SHA** — the post-filter tip that `git subtree add` actually imports and records in its `git-subtree-split` trailer.
>
> Filter-repo preserves *content and metadata* (author identity, message text, author and committer timestamps) but not commit SHAs. The criteria below are written accordingly: they require metadata preservation, not SHA preservation.

1. WHEN the import has completed, THE Monorepo SHALL contain a directory `tools/python/ddb-stream-basket-to-dt/` populated with the source tree of the Source_Repository branch as of the Upstream_SHA (minus the excluded paths in criterion 5).
2. THE import SHALL be performed using `git subtree add` without the `--squash` flag, so that the imported commits remain reachable in the Monorepo (note: `git log -- tools/python/ddb-stream-basket-to-dt/` applies path-based history simplification and may show only the merge commit; the imported commits remain reachable via `git log <Imported_SHA>` and `git log --all`, matching the existing import convention in this Monorepo).
3. WHEN the import is performed, THE Monorepo SHALL preserve, for every commit reachable from the filtered branch tip, the original author identity, original commit message text, original author timestamp, and original committer timestamp. Commit SHAs are NOT preserved, because `git filter-repo` rewrites them when excluding the criterion-5 paths; SHA rewriting is the only permitted modification.
4. WHEN the import has completed, THE import merge commit SHALL contain a `git-subtree-split` trailer holding the full 40-character hexadecimal **Imported_SHA** (the post-filter tip), captured automatically by `git subtree` with no manual amendment. THE **Upstream_SHA** (pre-filter tip) SHALL be recorded separately for traceability in the merge commit body, since it is not otherwise reachable from the imported history.
5. THE import SHALL exclude the paths `.venv/`, `__pycache__/`, `.vscode/`, `.github/`, and the `build*.log` files from the Tool_Directory, applied via `git filter-repo --invert-paths` against a scratch clone before the subtree add, for those of the paths that are tracked in the Source_Repository. (`.github/` is always tracked and is always excluded — see Requirement 2.1/2.2; `.venv/`, `__pycache__/`, and `build*.log` are typically gitignored and excluded only if tracked.)
6. WHEN the import has completed, THE Tool_Directory SHALL contain at least the files `Dockerfile`, `compose.yaml`, `pyproject.toml`, `uv.lock`, `.python-version`, `.pylintrc`, `README.md`, `basket_stream_to_dynatrace.py`, and `test_basket_stream_to_dynatrace.py`, and the directory `test-data/`.
7. IF `tools/python/ddb-stream-basket-to-dt/` already exists in the Monorepo with any tracked content prior to the import, THEN THE import SHALL abort without creating commits or modifying any tracked file, AND SHALL surface an error indicating the target path is already populated.
8. THE import SHALL NOT modify any file in the Source_Repository and SHALL NOT alter the Source_Repository's CI, release cadence, or archival state.

### Requirement 2: Repository Hygiene After Import

**User Story:** As a maintainer, I want the imported tool cleaned of standalone-repo artifacts that do not belong in the Monorepo, so that the Monorepo's own conventions apply without duplication or conflict.

#### Acceptance Criteria

1. WHEN the import has completed, THE Tool_Directory SHALL NOT contain a `.github/` directory carried over from the Source_Repository; the Source_Repository's `.github/workflows/ci.yaml` and `.github/dependabot.yml` SHALL NOT be imported into the Tool_Directory.
2. WHEN the import has completed, THE Tool_Directory SHALL NOT contain `.github/copilot-instructions.md`; its guidance is already folded into the Monorepo steering files under `.kiro/steering/tools/python/ddb-stream-basket-to-dt/`.
3. THE imported `Dockerfile`, `compose.yaml`, `pyproject.toml`, `uv.lock`, `.python-version`, and `.pylintrc` SHALL be preserved byte-for-byte from their content at the Imported_SHA (which, for these files, is identical to their content at the Upstream_SHA, since filter-repo touches only the excluded paths), with no edits applied as part of this migration.
4. WHERE the Source_Repository tracked a `.gitignore`, THE import SHALL preserve it inside the Tool_Directory so that `.venv/`, `__pycache__/`, and build logs remain ignored locally.

### Requirement 3: Dependabot Coverage in the Monorepo

**User Story:** As a maintainer, I want Dependabot to keep the Tool's dependencies current in the Monorepo, so that the security posture the Source_Repository had is not lost in the move.

#### Acceptance Criteria

1. WHEN the migration has completed, THE Monorepo SHALL contain a `.github/dependabot.yml` file at the repository root.
2. IF no `.github/dependabot.yml` existed in the Monorepo before this migration, THEN THE created file SHALL be a new file scoped to the Tool_Directory rather than enabling Dependabot across unrelated parts of the Monorepo.
3. THE `.github/dependabot.yml` SHALL declare a `uv` package-ecosystem update entry whose `directory` is `/tools/python/ddb-stream-basket-to-dt`.
4. THE `.github/dependabot.yml` SHALL declare a `docker` package-ecosystem update entry whose `directory` is `/tools/python/ddb-stream-basket-to-dt`, covering the Tool's `Dockerfile`.
5. THE `.github/dependabot.yml` SHALL declare a `docker-compose` package-ecosystem update entry whose `directory` is `/tools/python/ddb-stream-basket-to-dt`, covering the Tool's `compose.yaml`, IF the installed Dependabot version supports the `docker-compose` ecosystem.
6. EACH update entry in `.github/dependabot.yml` SHALL declare a `schedule.interval` of `weekly`, matching the cadence used by the Source_Repository.
7. THE `.github/dependabot.yml` SHALL be valid against the Dependabot v2 schema such that GitHub does not report a configuration error on the file.

### Requirement 4: Path-Based CI Triggering for the Tool

**User Story:** As a developer, I want changes under the Tool_Directory to trigger the Tool's CI and nothing else, so that editing this tool never rebuilds the Java estate and editing the Java estate never triggers this tool.

#### Acceptance Criteria

1. WHEN a pull request or push modifies one or more files under `tools/python/ddb-stream-basket-to-dt/**`, THE Monorepo CI SHALL run the Tool_CI test job for the Tool.
2. WHEN a pull request or push modifies only files outside `tools/python/ddb-stream-basket-to-dt/**`, THE Monorepo CI SHALL NOT run the Tool_CI job for the Tool.
3. WHEN a change to `tools/python/ddb-stream-basket-to-dt/**` triggers CI, THE trigger mechanism SHALL NOT add the Tool to any `backend` services or libraries build matrix, and SHALL NOT cause any Maven module to build.
4. THE Tool_CI trigger SHALL be implemented either (a) by extending Change_Detection to emit a `tools` classification from paths matching `^tools/[^/]+/` and dispatching a matrix job, OR (b) by a standalone workflow keyed on `on.<event>.paths` covering the Tool — either scoped to `tools/python/ddb-stream-basket-to-dt/**` alone or, more generically, to `tools/python/**` with per-tool change detection so the same workflow serves future Python tools without edit; the chosen mechanism SHALL be documented in the design.
5. WHERE Change_Detection is extended to classify `tools/`, THE extension SHALL NOT alter the existing `services`, `libraries`, `graphql`, or `helm` outputs for any change that does not touch `tools/`.

### Requirement 5: Tool Test Job in the Monorepo

**User Story:** As a developer, I want the Tool's unit tests to run in the Monorepo on every change to it, so that regressions are caught at PR time exactly as they were in the standalone repo.

#### Acceptance Criteria

1. THE Tool_CI test job SHALL install the Tool's dependencies with `uv sync --locked --no-build` executed with `tools/python/ddb-stream-basket-to-dt/` as the working directory.
2. THE Tool_CI test job SHALL run the Tool's unit tests with `uv run --locked --no-build python -m unittest` (auto-discovery, so the generic workflow works for any tool) executed with the tool directory (`tools/python/ddb-stream-basket-to-dt/`) as the working directory. Auto-discovery finds `test_basket_stream_to_dynatrace.py`.
3. THE Tool_CI test job SHALL provision Python 3.14 (matching the Tool's `.python-version` and `requires-python`), using `uv` to install the interpreter rather than `pip`.
4. IF the Tool's unit tests report any failure or error, THEN THE Tool_CI test job SHALL terminate with a non-zero exit code and SHALL NOT proceed to the image-build job.
5. THE Tool_CI test job SHALL run on pull requests without performing any image build or registry push.

### Requirement 6: Tool Image Build and ECR Push in the Monorepo

**User Story:** As a platform engineer, I want the Monorepo to build the Tool's container image and push it to ECR on merge to main, so that the Monorepo produces the deployable artifact that the gitops repositories consume.

#### Acceptance Criteria

1. WHEN a change to `tools/python/ddb-stream-basket-to-dt/**` is merged to `main`, THE Tool_CI SHALL build the Tool's container image using the Tool_Directory as the Docker build context.
2. THE Tool_CI image build SHALL push the built image to ECR and SHALL NOT push to Docker Hub, matching the Source_Repository's current `ecr-push: true` / `dockerhub-push: false` configuration.
3. THE Tool_CI image build SHALL retain the image name `ddb-stream-basket-to-dt` (under the existing `whitbreaddigital/` namespace) and the existing ECR repository, so that Gitops_Repositories changes are limited to the image tag value.
4. THE Tool_CI image build SHALL pass the Build_Timestamp build argument to `docker build`, so that `/build-timestamp.txt` and the `build.timestamp` event field continue to reflect the build.
5. THE Tool_CI image build SHALL apply the Prisma_Scan gate to the built image, matching the gate applied by the Source_Repository today.
6. WHERE the Image_Build_Template is reused, THE reuse SHALL supply the Tool_Directory as a build-context / working-directory input, because the template hard-codes `./` as the build context; IF the template cannot accept such an input, THEN a Monorepo-local equivalent workflow SHALL be provided that builds from the Tool_Directory.
7. THE Tool_CI image build SHALL NOT run on pull requests; it SHALL run only on merge/push to `main`, matching the Source_Repository behaviour.
8. THE Tool_CI SHALL NOT write to any Gitops_Repository and SHALL NOT modify any deployment manifest or image tag; deployment remains owned by the Gitops_Repositories.

### Requirement 7: Tool Buildability and Verification

**User Story:** As a developer, I want to verify the imported tool builds, tests, and lints cleanly in its new location, so that the migration can be merged with confidence.

#### Acceptance Criteria

1. WHEN `uv sync --locked --no-build` is executed from `tools/python/ddb-stream-basket-to-dt/` after import, THE command SHALL terminate with exit code `0` and resolve dependencies from the committed `uv.lock` without modifying it.
2. WHEN `uv run --locked --no-build python -m unittest` (auto-discovery) is executed from `tools/python/ddb-stream-basket-to-dt/` after import, THE command SHALL terminate with exit code `0` and report zero test failures and zero errors.
3. WHEN `docker build` is executed with `tools/python/ddb-stream-basket-to-dt/` as the build context after import, THE build SHALL terminate with exit code `0` and produce an image that runs as the non-root `runner` user (uid/gid 9001).
4. WHEN `uv run --with pylint pylint --rcfile=.pylintrc basket_stream_to_dynatrace.py` is executed from `tools/python/ddb-stream-basket-to-dt/` after import, THE command SHALL evaluate against the Tool's own `.pylintrc` and SHALL NOT report a fatal or error-category message introduced by the move. (pylint is run ephemerally via `uv run --with` because it is not a project dependency — this keeps `pyproject.toml`/`uv.lock` byte-for-byte per Requirement 2.3 while still enforcing the lint convention in CI.)
5. WHEN the full Monorepo backend CI runs on a change confined to `tools/python/ddb-stream-basket-to-dt/**`, THE backend `services` and `libraries` build matrices SHALL remain empty, confirming the tool change does not enter the Java build.

### Requirement 8: Alignment with the Authoritative Tools Steering

**User Story:** As a developer, I want the migrated tool and its CI to conform to the already-merged `tools/` steering, which is the source of truth for how this tool works, so that the documentation on `main` and the implementation do not diverge.

#### Acceptance Criteria

1. THE Tool_Directory layout after import SHALL conform to `.kiro/steering/stacks/tools.md` — a tool owning its own language, build tool, and test runner, with a Dockerfile at its own root and no Maven POM — and SHALL match the flat single-module layout enumerated in `.kiro/steering/tools/python/ddb-stream-basket-to-dt/structure.md`.
2. THE Tool SHALL NOT be added under `backend/<squad>/services/` or `backend/<squad>/libs/`, and SHALL NOT be added as a `<module>` to any `pom.xml`, per `.kiro/steering/stacks/tools.md` ("do not give it a Maven POM, and do not add it to the reactor").
3. THE Tool_CI SHALL preserve the runtime and container conventions documented in `.kiro/steering/tools/python/ddb-stream-basket-to-dt/tech.md`: `uv` not `pip`, stdlib `unittest`, non-root uid/gid 9001, `pip` absent from the image, and the Build_Timestamp preserved. WHERE this spec's requirements restate any such convention, the `tech.md` statement is the authoritative source.
4. WHERE `.kiro/steering/stacks/tools.md` names the Change_Detection script `detect-changed-modules.sh` — which remains part of the detection chain (sourced by `detect-changed-eph.sh`) but is not invoked directly by the BE_Orchestrator (whose direct entry points are `detect-all.sh` and `detect-changed-eph.sh`) — THE design SHALL note this wording nuance so the steering can optionally be clarified; adjusting the steering text is out of scope for this spec's implementation.
5. THE out-of-scope prod-deployment guidance in this spec (Requirement 9) SHALL be consistent with `.kiro/steering/tools/python/ddb-stream-basket-to-dt/product.md`, which documents that on restart existing shards resume at `LATEST` (dropping records written during the gap) and therefore that automated prod rollouts "want a gate rather than a fully automatic image bump".

### Requirement 9: Sequenced Follow-On Work (Out of Scope, Documented)

**User Story:** As a platform engineer, I want the prod-deployment prerequisites and follow-on phases captured, so that the boundaries of this spec are explicit and the next work is unambiguous.

#### Acceptance Criteria

1. THE spec SHALL record that prod deployment (phase 3) is out of scope and depends on the prod Deployment being switched from the Docker Hub image reference `whitbreaddigital/ddb-stream-basket-to-dt` to the ECR reference, tracked by SRE-319. THE spec SHALL further record that, per `.kiro/steering/tools/python/ddb-stream-basket-to-dt/product.md`, the `LATEST`-on-restart delivery gap means any phase-3 prod rollout mechanism SHALL favour a human-gated image bump over a fully automatic one.
2. THE spec SHALL record that archiving the Source_Repository is out of scope and SHALL occur only after the Monorepo pipeline has produced an image that has run in prod.
3. THE spec SHALL record that until phase 2 lands, the Monorepo cannot build or release the Tool, so the Source_Repository stays live and releasable throughout phases 1 and 2.
4. THE implementation driven by this spec SHALL NOT archive the Source_Repository, SHALL NOT edit any Gitops_Repository, and SHALL NOT change any prod image reference.
