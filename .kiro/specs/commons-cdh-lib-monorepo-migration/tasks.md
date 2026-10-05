# Implementation Plan: commons-cdh-lib Monorepo Migration

## Overview

This plan migrates `uk.co.whitbread.shared:commons-cdh-lib` from its standalone Git repository (`https://github.com/whitbread-eos/commons-cdh-lib`, branch `develop`) into `digital-backend-monorepo` as a Maven reactor module at `backend/libs/commons-cdh-lib/`, while preserving full Git history, leaving the upstream Nexus pipeline untouched, and extending CI so that library changes only rebuild consumer services that actually depend on the library.

The work is sequenced around three hard ordering constraints:

1. **The `git subtree add` import is a one-way gate** — Maven module wiring, library POM refactor, and source-level Jakarta EE migration all require the imported tree to exist on disk first.
2. **Root POM wiring depends on the library POM being aligned** — once the library declares `<parent>digital-backend-monorepo:${revision}</parent>` and is dependency-clean, the root `<modules>` entry can be added safely without breaking the reactor.
3. **CI work and steering files are independent of the import** — they can be authored in parallel from the very first wave; they only need to be in place before merging the migration PR.

PBT is not applicable for this build-system migration (per design "Correctness Properties" section). Verification relies on Maven build assertions, `bats` example tests for the change-detection scripts, and a SonarCloud end-to-end smoke check.

Implementation languages: XML (POMs), Bash (CI scripts), YAML (GitHub Actions), Markdown (steering files). No application code is written in this plan.

## Tasks

- [x] 1. Pre-flight checks and configure upstream remote
  - Verify `backend/libs/commons-cdh-lib/` contains only `.gitkeep` (abort if any other tracked content present).
  - Add Git remote: `git remote add commons-cdh-lib-upstream https://github.com/whitbread-eos/commons-cdh-lib.git`.
  - Run `git ls-remote commons-cdh-lib-upstream` to confirm URL reachability.
  - Run `git fetch commons-cdh-lib-upstream develop` and capture the resolved tip SHA (40-char hex) for use in task 2; verify with `git cat-file -e <SHA>^{commit}`Now what I'm thinking is do we really need to build all services and libraries when the root pump changes Because for example, let's say that we add a new dependency in the root pump maybe we shouldn't We shouldn't Just rebuild all the services and libraries just because we added a new dependency because that dependencies does not affect any service or library Maybe we should just when we remove Dependences yes, then we will have to rebuild but even then we can optimize to only rebuild the services and libraries that are affected for example, let's say that we Change the version of a library of an existing library. Maybe that library is not used by all the services and libraries Maybe they are used only by a few of them Can we do a mechanism of optimizing the current script and detect only the libraries and services thatNow that I'm thinking, I think we have to optimize the script, the detection script even more Because for example, if you add a new dependency in the root of POM, maybe you shouldn't just rebuild all the services and libraries because those are not impacted, but if you change an existing dependency then maybe we should detect all the services and libraries that use that dependency that was changed and only rebuild those because only those are the ones impacted, the other ones are not impacted So how should we go about this? Can we improve and optimize the script even further? Because in the future we will have like 50 services and more than 10 libraries and if we will add something in the root POM, XML and rebuild all of them again, that's gonna take a while, that's gonna take a lot of minutes.
  - Reject placeholder URL/SHA values ("TBD", "TODO", "<url>", empty string).
  - Capture pre-migration `./mvnw -pl backend/services/piba-account-service-opera -am dependency:tree -Dverbose > /tmp/piba-deptree-pre.txt` and the equivalent for `spending-entity-service` for use as the baseline in task 15.4.
  - _Requirements: 1.7, 9.1, 9.2, 9.4, 9.5_
  - _Design: §1 (pre-flight checks), §10 (URL validation), §Risks_

- [x] 2. Import commons-cdh-lib via git subtree
  - [x] 2.1 Run `git subtree add` against the upstream remote
    - Inspect the upstream tree before importing; if any binary artifacts (`target/`, `*.class`, `*.jar`), IDE settings, or generated sources are present, run `git filter-repo --invert-paths --path <path>` against a scratch clone first and use the rewritten repo as the subtree source.
    - Execute `git subtree add --prefix=backend/libs/commons-cdh-lib commons-cdh-lib-upstream <SHA>` (no `--squash`). The SHA is the one captured by task 1 in `/tmp/commons-cdh-lib-import-sha.txt`.
    - Use the merge commit produced by `git subtree add` unmodified — `git subtree` automatically records `git-subtree-dir`, `git-subtree-mainline`, and `git-subtree-split` trailers. No manual commit message amendment is needed; this matches the existing service-import convention in this monorepo.
    - Verify post-conditions: the merge commit has two parents (mainline + upstream tip) and contains a `git-subtree-split: <SHA>` trailer; `git log <imported-SHA>` walks back through the upstream history with original authors, messages, and timestamps intact.
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 9.1, 9.2_
    - _Design: §1 (subtree import sequence)_

- [x] 3. Apply Jakarta EE namespace migration in library source
  - Replace `javax.persistence` → `jakarta.persistence`, `javax.servlet` → `jakarta.servlet`, `javax.validation` → `jakarta.validation`, `javax.ws.rs` → `jakarta.ws.rs`, `javax.annotation` → `jakarta.annotation` (preserve `javax.annotation.processing` — that one stays `javax.*`), `javax.inject` → `jakarta.inject`, `javax.transaction` → `jakarta.transaction` across `backend/libs/commons-cdh-lib/src/**`.
  - Run `grep -r 'javax\.' backend/libs/commons-cdh-lib/src` and confirm only Java SE imports remain (zero Java EE `javax.*` imports left).
  - This task only touches `backend/libs/commons-cdh-lib/src/**`; it does not modify the library POM (handled in task 4).
  - _Requirements: 8.6_
  - _Design: §9 (Standards Alignment & Jakarta Migration)_

- [x] 4. Refactor library POM to monorepo standards
  - Edit `backend/libs/commons-cdh-lib/pom.xml`:
    - Replace upstream `<parent>` (currently `org.springframework.boot:spring-boot-starter-parent:4.0.3`) with the library parent: `<groupId>uk.co.whitbread</groupId><artifactId>digital-monorepo-library-parent</artifactId><version>${revision}</version><relativePath>../../../parents/library-parent/pom.xml</relativePath>`.
    - Set `<groupId>uk.co.whitbread.shared</groupId>`, `<artifactId>commons-cdh-lib</artifactId>` (unchanged), `<version>${revision}</version>` (replacing upstream `17.1.0`), `<packaging>jar</packaging>`.
    - Remove `<distributionManagement>` entirely so `mvn deploy` against the library fails with Maven's "no DistributionManagement" error rather than silently publishing.
    - Remove any upstream `<java.version>` override that conflicts with the monorepo's Java 25 standard (let it inherit from the monorepo parent).
    - Remove any upstream `<annotationProcessorPaths>` block — Lombok / MapStruct / lombok-mapstruct-binding are inherited from the monorepo parent's `<pluginManagement>`.
    - Audit hard-coded versions on parent-managed dependencies (the upstream POM declares explicit versions for `commons-exceptions`, `commons-lang3`, and `lombok` — all of these are managed by the monorepo parent and the explicit `<version>` elements must be removed). Halt and surface an error rather than silently shadowing if any required version cannot be inherited and is also absent from the parent.
  - _Requirements: 2.1, 2.2, 2.6, 2.12, 5.2, 8.1, 8.2, 8.3, 8.5, 8.7_
  - _Design: §1 (library POM skeleton), §7 (no `<distributionManagement>`), §9 (Standards Alignment)_

- [x] 5. Reconcile library dependency declarations with parent dependencyManagement
  - For every `<dependency>` declared in `backend/libs/commons-cdh-lib/pom.xml`, classify as one of:
    1. Already managed in the monorepo root `pom.xml` `<dependencyManagement>` → remove `<version>` from the library POM.
    2. Not managed in the monorepo parent → add an explicit-version `<dependency>` entry to the root `pom.xml` `<dependencyManagement>` (with a corresponding `<…version>` property in `<properties>` if the team's convention is followed), then remove `<version>` from the library POM.
  - Halt the migration and surface an error naming the missing parent element if any required dependency cannot be resolved by either of the two paths above (Req 8.7 — no silent hard-coding).
  - This task is the only step where the root `pom.xml` `<dependencyManagement>` is allowed to grow — the `${revision}` swap on the existing `commons-cdh-lib` entry happens in task 6 to avoid file-conflict with this task.
  - _Requirements: 2.10, 2.11, 8.7_
  - _Design: §2 (Maven Module Wiring — Library POM skeleton), §9 (hard-coded versions audit)_

- [x] 6. Wire commons-cdh-lib into root pom.xml
  - Add `<module>backend/libs/commons-cdh-lib</module>` to the root `pom.xml` `<modules>` block.
  - Update the existing `<dependencyManagement>` entry for `uk.co.whitbread.shared:commons-cdh-lib`:
    - Replace `<version>${wb.commons-cdh.version}</version>` with `<version>${revision}</version>`.
    - Remove the `<wb.commons-cdh.version>17.0.1</wb.commons-cdh.version>` entry from `<properties>`.
  - Optionally add `<sonar.exclusions>backend/libs/**</sonar.exclusions>` to the root POM `<properties>` so library code is not double-attributed to consumer Sonar projects (covers the same intent as the per-service `-Dsonar.exclusions` flag in task 13; either works, root-POM is preferred).
  - _Requirements: 2.3, 2.7, 2.8, 2.9, 5.6_
  - _Design: §2 (Root POM changes), §8 (Sonar exclusion intent)_

- [x] 7. Verify consumer service POMs require no textual edits
  - Confirm `backend/services/piba-account-service-opera/pom.xml` declares `commons-cdh-lib` with no `<version>`, no `<scope>system</scope>`, and no `<systemPath>`.
  - Confirm `backend/services/spending-entity-service/pom.xml` declares `commons-cdh-lib` with no `<version>`, no `<scope>system</scope>`, no `<systemPath>`, and exactly the two existing `<exclusion>` entries (`uk.co.whitbread.shared:commons-exceptions` and `commons-fileupload:commons-fileupload`) — preserved verbatim.
  - Net consumer-POM change-set is intentionally zero textual edits; this is a verification step that the post-migration consumer POMs match the pre-migration state byte-for-byte.
  - _Requirements: 3.1, 3.2, 3.3, 3.6_
  - _Design: §3 (Consumer Service POM Updates)_

- [x] 8. Checkpoint - Local reactor build smoke test
  - Run `./mvnw clean install` from the monorepo root and confirm exit code 0, library and all 3 services built, zero compilation errors, zero unit-test failures.
  - Resolve any breakage by returning to tasks 3–6 before proceeding.
  - Ensure all tests pass, ask the user if questions arise.

- [x] 9. Implement reverse-deps.sh
  - [x] 9.1 Implement reverse-deps.sh
    - Create `.github/workflows/scripts/reverse-deps.sh`.
    - Interface: `reverse-deps.sh <groupId> <artifactId>` → stdout JSON array of consuming service directory names (sorted, deduplicated); exit 0 on success including empty result; exit 1 on malformed POM with stderr message naming the file; exit 124 on internal timeout.
    - Use `xmllint --xpath` with `local-name()` matching to be namespace-agnostic; query for `<dependency>` blocks where `<groupId>` and `<artifactId>` match the arguments. Direct dependencies only — transitive dependencies are out of scope per Req 4.2.
    - Mark the file executable (`chmod +x`).
    - _Requirements: 4.2, 4.7_
    - _Design: §4.1 (Reverse-Dependency Script)_

  - [ ]* 9.2 Add bats unit tests for reverse-deps.sh
    - Create `.github/workflows/scripts/test/reverse-deps.bats`.
    - Test cases: matching service detected; non-matching service absent; multiple services produce sorted deduplicated output; malformed POM exits 1 with file name in stderr; missing `<groupId>`/`<artifactId>` block ignored; zero matches returns `[]` with exit 0.
    - _Requirements: 4.2, 4.7_
    - _Design: §Verification Approach (bats suite)_

- [x] 10. Implement detect-changed-modules.sh
  - [x] 10.1 Implement detect-changed-modules.sh
    - Create `.github/workflows/scripts/detect-changed-modules.sh`.
    - Replicate the existing inline change-detection logic from `ci.yaml` and extend it: classify changed files into root-pom-changed (mark all services), `backend/services/<svc>/**` (direct), `backend/libs/<lib>/**` (changed libraries).
    - For each changed library, extract `<groupId>`/`<artifactId>` from `backend/libs/<lib>/pom.xml` via `xmllint`, then call `timeout 60 .github/workflows/scripts/reverse-deps.sh <group> <artifact>` and union the result into the `services` matrix.
    - Emit two outputs: `services` (deduplicated JSON array) and `libraries` (changed libraries JSON array). Each may independently be `[]`.
    - Propagate any non-zero exit from the timeout/script wrappers (1 = parse error, 124 = timeout) so the change-detection job fails closed.
    - Mark the file executable.
    - _Requirements: 4.1, 4.3, 4.4, 4.5, 4.7, 4.8, 4.9_
    - _Design: §4.2 (`ci.yaml` Changes — script sketch)_

  - [ ]* 10.2 Add bats unit tests for detect-changed-modules.sh
    - Create `.github/workflows/scripts/test/detect-changed-modules.bats`.
    - Test cases: only root pom.xml changed → all services, no libraries; only `backend/services/<svc>/**` changed → that service, no libraries; only `backend/libs/<lib>/**` changed → reverse-deps as services, that library in libraries; mixed service + library change → deduplicated union; malformed library POM → exit 1; reverse-deps timeout → exit 124; library with empty reverse-deps → `services=[]`, `libraries=[<lib>]`.
    - _Requirements: 4.1, 4.4, 4.7, 4.9_
    - _Design: §Verification Approach (bats suite)_

- [x] 11. Update ci.yaml orchestrator for two-matrix output
  - Edit `.github/workflows/ci.yaml`:
    - Replace the inline `Detect changed services` step body with a single shell invocation of `.github/workflows/scripts/detect-changed-modules.sh`.
    - Declare two outputs on the `detect-changes` job: `services` and `libraries`.
    - Add a new `build-libraries` job that calls `./.github/workflows/build-library.yaml` with `library-name: ${{ matrix.library }}`, gated by `if: ${{ needs.detect-changes.outputs.libraries != '[]' }}`.
    - Update the existing `build-services` gate from `services != '[]'` to remain unchanged in semantics; both jobs use `strategy.fail-fast: false` and run in parallel when both matrices are non-empty.
    - Pass `secrets: inherit` on both reusable workflow calls.
  - _Requirements: 4.1, 4.3, 4.4, 4.6, 4.8, 4.9_
  - _Design: §4.2 (`ci.yaml` Changes — full job snippet)_

- [x] 12. Create build-library.yaml reusable workflow
  - Create `.github/workflows/build-library.yaml` modelled on `build-service.yaml` with these differences:
    - Input: `library-name` (description "Name of the library to build (e.g. commons-cdh-lib)").
    - `Maven build` step: `mvn $MAVEN_CLI_OPTS verify -pl "backend/libs/$LIBRARY_NAME"` (no `-am`; libraries currently have no in-monorepo upstream — revisit when lib-on-lib appears).
    - `Sonar scan` step: `mvn $MAVEN_CLI_OPTS sonar:sonar -pl "backend/libs/$LIBRARY_NAME" -Dsonar.projectKey=whitbread-eos_digital-monorepo_$LIBRARY_NAME -Dsonar.projectName="digital-monorepo :: $LIBRARY_NAME" -Dsonar.host.url=https://sonarcloud.io -Dsonar.organization=whitbread-eos`.
    - No `-Dsonar.exclusions=backend/libs/**` here — the library scan must include its own files.
    - No artifact upload step (libraries are consumed via the local Maven reactor, not via uploaded JARs).
    - No Docker/Helm/DIT/release-checks steps (these apply to deployable services only).
    - Reuse the same Java 25 setup, settings.xml writing, Sonar cache, and Maven cache patterns from `build-service.yaml`. Maven cache key uses `LIBRARY_NAME` and hashes `backend/libs/$LIBRARY_NAME/pom.xml` + root `pom.xml`.
  - _Requirements: 4.6, 8.4_
  - _Design: §4.3 (`build-library.yaml`), §8.3 (SonarCloud design response)_

- [x] 13. Update build-service.yaml with libs/** Sonar exclusion
  - Edit `.github/workflows/build-service.yaml`: add `-Dsonar.exclusions=backend/libs/**` to the `Sonar scan` step's `mvn sonar:sonar` invocation, immediately after the `-pl "backend/services/$SERVICE_NAME" -am` flag.
  - This prevents library code from being double-attributed to every consuming service's Sonar project. (If task 6 chose the root-POM `<sonar.exclusions>` approach instead, this task is a no-op — confirm exactly one of the two is in place.)
  - _Requirements: 8.4_
  - _Design: §4.3 (`build-service.yaml` change), §8.3 (Sonar exclusion rationale)_

- [x] 14. Author commons-cdh-lib steering files
  - [x] 14.1 Create product.md
    - Create `.kiro/steering/commons-cdh-lib/product.md`.
    - YAML front-matter: `inclusion: fileMatch` with `fileMatchPattern: "backend/libs/commons-cdh-lib/**"` (matching the convention used by service steering files).
    - Body: library responsibilities, intended consumer services (`piba-account-service-opera`, `spending-entity-service`), integration points with those consumers.
    - _Requirements: 6.1, 6.2, 6.5, 6.6_
    - _Design: §6 (Steering Files)_

  - [x] 14.2 Create tech.md
    - Create `.kiro/steering/commons-cdh-lib/tech.md`.
    - Same front-matter pattern as 14.1.
    - Body: Java 25 / Jakarta EE / Spring Boot 4.0.6 BOM (inherited via monorepo parent), declared dependencies, Maven build commands (`./mvnw clean install -pl backend/libs/commons-cdh-lib`, full reactor, `-am` semantics for consumers).
    - _Requirements: 6.1, 6.3, 6.5, 6.6_
    - _Design: §6 (Steering Files)_

  - [x] 14.3 Create structure.md
    - Create `.kiro/steering/commons-cdh-lib/structure.md`.
    - Same front-matter pattern as 14.1.
    - Body: source code package and directory layout under `backend/libs/commons-cdh-lib/src/main/java/`, architectural conventions used inside the library.
    - _Requirements: 6.1, 6.4, 6.5, 6.6_
    - _Design: §6 (Steering Files)_

- [x] 15. Validate reactor resolution and dependency-tree parity
  - [x] 15.1 Verify full reactor build
    - Run `./mvnw clean install` from the monorepo root.
    - Assert: exit code 0; reactor build summary lists `commons-cdh-lib`, `basket-async-order-processor`, `piba-account-service-opera`, `spending-entity-service` (4 modules) all SUCCESS; library appears in the build order before the two consumer services.
    - _Requirements: 7.1, 2.4_
    - _Design: §5 (Build Verification Flow — full reactor row)_

  - [x] 15.2 Verify per-consumer service builds with `-am`
    - Run `./mvnw clean install -pl backend/services/piba-account-service-opera -am`. Assert exit 0; library built first; zero unit-test/contract-test/Checkstyle failures.
    - Run `./mvnw clean install -pl backend/services/spending-entity-service -am`. Assert exit 0; library built first; zero unit-test/Checkstyle failures.
    - _Requirements: 3.4, 3.5, 7.2, 7.3, 7.4, 7.5, 7.9_
    - _Design: §5 (Build Verification Flow)_

  - [x] 15.3 Verify library-only build
    - Run `./mvnw clean install -pl backend/libs/commons-cdh-lib`. Assert exit 0; library JAR produced under `backend/libs/commons-cdh-lib/target/`; JaCoCo coverage report at `backend/libs/commons-cdh-lib/target/site/jacoco/`; zero Checkstyle violations against the inherited `google-checkstyle.xml` ruleset; flattened POM contains literal version (resolved `${revision}`) rather than the placeholder.
    - _Requirements: 2.6, 7.6, 7.7, 7.8, 8.1_
    - _Design: §5 (Build Verification Flow)_

  - [x] 15.4 Compare dependency:tree pre/post migration
    - Run `./mvnw -pl backend/services/piba-account-service-opera -am dependency:tree -Dverbose > /tmp/piba-deptree-post.txt` and the equivalent for `spending-entity-service`.
    - Diff against the pre-migration baseline captured in task 1 (`/tmp/piba-deptree-pre.txt`, `/tmp/spending-deptree-pre.txt`). Assert: identical `groupId:artifactId:version:classifier` coordinates for every transitive dependency of `commons-cdh-lib`; the `commons-cdh-lib` line itself shows `(reactor)` as resolution source rather than a Nexus URL.
    - Fail the validation if any consumer test resolves the library from Nexus instead of the local reactor.
    - _Requirements: 2.7, 3.7, 7.10, 5.6_
    - _Design: §5 (Build Verification Flow — Resolution origin row), §Risks (transitive dep parity)_

- [x] 16. Verify SonarCloud project provisioning and end-to-end scan
  - Confirm with the platform team that the SonarCloud project `whitbread-eos_digital-monorepo_commons-cdh-lib` exists under the `whitbread-eos` organisation. (Project provisioning itself is a platform-team action and not an executable code task.)
  - On the migration feature branch, confirm the `build-libraries` matrix in `ci.yaml` dispatches `build-library.yaml` and the Sonar scan step completes with HTTP 2xx against `https://sonarcloud.io` for the library project key.
  - Confirm consumer-service Sonar scans (`build-service.yaml`) report only on service code — verify by spot-checking one consumer Sonar dashboard for the absence of files under `backend/libs/commons-cdh-lib/`.
  - _Requirements: 8.4_
  - _Design: §8.1 (Current state), §8.3 (Design response), §Decisions and Open Questions item 6_

- [x] 17. Final checkpoint - All gates green
  - Confirm tasks 8 and 15 ran clean.
  - Confirm `bats` (if executed via 9.2/10.2) passes against the change-detection scripts.
  - Confirm task 16 verified the SonarCloud end-to-end path.
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Tasks marked with `*` are optional and can be skipped for faster MVP delivery; they are the `bats` suites for the two CI scripts. Skipping them ships the migration without automated regression coverage on the change-detection logic — acceptable for the initial cutover, recommended for follow-up.
- Each task references specific requirements clauses for traceability (granular sub-requirements, not just user stories).
- Property-based tests are deliberately omitted: the design's "Correctness Properties" section documents PBT non-applicability for build-system / infrastructure migrations.
- Tasks 1, 9, 10, 12, 13, 14 (CI scripts, workflows, steering files) are independent of the library import and can be authored concurrently from the start.
- Tasks 3, 4, 5, 6 form the critical path through library and root POM edits; they must be sequenced because tasks 4/5/6 all touch overlapping files.
- Validation tasks (15.x, 16) gate the final merge but must not run before the wiring is in place.
- The import follows the existing service-import convention in this monorepo: `git subtree add` produces a 2-parent merge commit with `git-subtree-dir` / `git-subtree-mainline` / `git-subtree-split` trailers. The upstream URL is preserved by the configured `commons-cdh-lib-upstream` remote; no separate copy in the commit message.
- This plan only creates implementation artifacts. The migration PR is opened, reviewed, and merged through the standard team process — not by this task list.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1", "9.1", "12", "13", "14.1", "14.2", "14.3"] },
    { "id": 1, "tasks": ["2.1", "9.2", "10.1"] },
    { "id": 2, "tasks": ["10.2", "11"] },
    { "id": 3, "tasks": ["3", "4"] },
    { "id": 4, "tasks": ["5"] },
    { "id": 5, "tasks": ["6"] },
    { "id": 6, "tasks": ["7"] },
    { "id": 7, "tasks": ["15.1"] },
    { "id": 8, "tasks": ["15.2"] },
    { "id": 9, "tasks": ["15.3"] },
    { "id": 10, "tasks": ["15.4"] },
    { "id": 11, "tasks": ["16"] }
  ]
}
```
