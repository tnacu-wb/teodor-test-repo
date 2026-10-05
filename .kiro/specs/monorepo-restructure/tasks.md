# Implementation Plan: Monorepo Restructure

## Overview

Restructure `digital-backend-monorepo` into `digital-monorepo` by introducing a `backend/` top-level directory, renaming the Maven identity, importing three external repositories via git subtree, updating all CI/CD pipelines and scripts, and updating Kiro steering and spec files. Each task is ordered to build incrementally, with the final tasks wiring everything together and validating the full build.

## Tasks

- [x] 1. Create backend directory structure and move existing modules
  - [x] 1.1 Create `backend/services/` and `backend/libs/` directories, then move existing service and library directories into the new structure
    - Create `backend/services/` and `backend/libs/` directories
    - Move `services/basket-async-order-processor/` → `backend/services/basket-async-order-processor/`
    - Move `services/piba-account-service-opera/` → `backend/services/piba-account-service-opera/`
    - Move `services/spending-entity-service/` → `backend/services/spending-entity-service/`
    - Move `libs/commons-cdh-lib/` → `backend/libs/commons-cdh-lib/`
    - Remove the now-empty `services/` and `libs/` directories at the repository root
    - _Requirements: 2.1, 2.2, 2.7_

  - [x] 1.2 Update the root `pom.xml` identity and module paths
    - Change `<artifactId>` from `digital-backend-monorepo` to `digital-monorepo`
    - Change `<name>` from `digital-backend-monorepo` to `digital-monorepo`
    - Change `<description>` to `Whitbread Digital Monorepo`
    - Update `<modules>` to use `backend/services/<name>` and `backend/libs/<name>` paths
    - _Requirements: 1.1, 1.2, 1.3, 2.3, 2.4_

  - [x] 1.3 Update all existing service and library POM `<parent>` blocks
    - In each of `backend/services/basket-async-order-processor/pom.xml`, `backend/services/piba-account-service-opera/pom.xml`, `backend/services/spending-entity-service/pom.xml`: change `<artifactId>` to `digital-monorepo-service-parent` and `<relativePath>` to `../../../parents/service-parent/pom.xml`
    - In `backend/libs/commons-cdh-lib/pom.xml`: change `<artifactId>` to `digital-monorepo-library-parent` and `<relativePath>` to `../../../parents/library-parent/pom.xml`
    - _Requirements: 2.5, 2.6_

- [x] 2. Checkpoint - Verify build after directory restructure
  - Ensure `./mvnw clean install` passes from the repository root with the new module paths. Ask the user if questions arise.

- [x] 3. Import external repositories via git subtree
  - [x] 3.1 Import `ohip-adapter-service` using `git subtree add --prefix=backend/services/ohip-adapter-service` (no `--squash` flag) to preserve full commit history
    - After import, remove any standalone CI files: `Jenkinsfile`, `.github/workflows/`, `.gitlab-ci.yml`
    - Update `backend/services/ohip-adapter-service/pom.xml` parent block: `<artifactId>digital-monorepo-service-parent</artifactId>`, `<relativePath>../../../parents/service-parent/pom.xml</relativePath>`
    - Add `<module>backend/services/ohip-adapter-service</module>` to root POM
    - _Requirements: 3.1, 3.2, 3.3, 3.6, 3.7_

  - [x] 3.2 Import `hotel-entity-service` using `git subtree add --prefix=backend/services/hotel-entity-service` (no `--squash` flag) to preserve full commit history
    - After import, remove any standalone CI files: `Jenkinsfile`, `.github/workflows/`, `.gitlab-ci.yml`
    - Update `backend/services/hotel-entity-service/pom.xml` parent block: `<artifactId>digital-monorepo-service-parent</artifactId>`, `<relativePath>../../../parents/service-parent/pom.xml</relativePath>`
    - Add `<module>backend/services/hotel-entity-service</module>` to root POM
    - _Requirements: 3.1, 3.2, 3.4, 3.6, 3.7_

  - [x] 3.3 Import `content-entity-service` using `git subtree add --prefix=backend/services/content-entity-service` (no `--squash` flag) to preserve full commit history
    - After import, remove any standalone CI files: `Jenkinsfile`, `.github/workflows/`, `.gitlab-ci.yml`
    - Update `backend/services/content-entity-service/pom.xml` parent block: `<artifactId>digital-monorepo-service-parent</artifactId>`, `<relativePath>../../../parents/service-parent/pom.xml</relativePath>`
    - Add `<module>backend/services/content-entity-service</module>` to root POM
    - _Requirements: 3.1, 3.2, 3.5, 3.6, 3.7_

  - [x] 3.4 Resolve any missing dependencies for imported services
    - Check if imported service POMs reference dependencies not declared in the root POM's `<dependencyManagement>`
    - Add any missing dependency declarations to the root POM
    - _Requirements: 3.8_

- [x] 4. Checkpoint - Verify build after subtree imports
  - Ensure `./mvnw clean install -pl backend/services/ohip-adapter-service,backend/services/hotel-entity-service,backend/services/content-entity-service -am` passes. Ask the user if questions arise.

- [x] 5. Update GitHub Actions workflows and CI scripts
  - [x] 5.1 Update `detect-changed-modules.sh` to use `backend/` prefixed paths
    - Change `find services -maxdepth 2` → `find backend/services -maxdepth 2`
    - Change `find libs -maxdepth 2` → `find backend/libs -maxdepth 2`
    - Change `grep -oE '^services/[^/]+/'` → `grep -oE '^backend/services/[^/]+/'`
    - Change `grep -oE '^libs/[^/]+/'` → `grep -oE '^backend/libs/[^/]+/'`
    - Change `sed 's:^services/::; s:/$::'` → `sed 's:^backend/services/::; s:/$::'`
    - Change `sed 's:^libs/::; s:/$::'` → `sed 's:^backend/libs/::; s:/$::'`
    - Change `libs/$LIB/pom.xml` → `backend/libs/$LIB/pom.xml`
    - Ensure root POM change still triggers full rebuild using all modules under `backend/services/` and `backend/libs/`
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.12, 4.13_

  - [x] 5.2 Update `reverse-deps.sh` to scan `backend/services/`
    - Change `SERVICES_DIR="$REPO_ROOT/services"` → `SERVICES_DIR="$REPO_ROOT/backend/services"`
    - _Requirements: 4.10_

  - [x] 5.3 Update `build-service.yaml` to use `backend/services/` paths and new Sonar identity
    - Change Maven build: `-pl "services/$SERVICE_NAME"` → `-pl "backend/services/$SERVICE_NAME"`
    - Change Sonar scan: `-pl "services/$SERVICE_NAME"` → `-pl "backend/services/$SERVICE_NAME"`
    - Change Sonar exclusions: `-Dsonar.exclusions=libs/**` → `-Dsonar.exclusions=backend/libs/**`
    - Change Sonar project key: `whitbread-eos_digital-backend-monorepo_$SERVICE_NAME` → `whitbread-eos_digital-monorepo_$SERVICE_NAME`
    - Change Sonar project name: `digital-backend-monorepo :: $SERVICE_NAME` → `digital-monorepo :: $SERVICE_NAME`
    - Change artifact upload path: `services/${{ inputs.service-name }}/target/*.jar` → `backend/services/${{ inputs.service-name }}/target/*.jar`
    - Change cache key hash path: `services/{0}/pom.xml` → `backend/services/{0}/pom.xml`
    - _Requirements: 1.4, 1.5, 4.5, 4.6, 4.7, 4.11_

  - [x] 5.4 Update `build-library.yaml` to use `backend/libs/` paths
    - Change Maven build: `-pl "libs/$LIBRARY_NAME"` → `-pl "backend/libs/$LIBRARY_NAME"`
    - Change cache key hash path: `libs/{0}/pom.xml` → `backend/libs/{0}/pom.xml`
    - _Requirements: 4.8, 4.9_

- [x] 6. Checkpoint - Verify CI scripts are syntactically correct
  - Ensure all shell scripts pass `bash -n` syntax check and all YAML files are valid. Ask the user if questions arise.

- [x] 7. Update steering and spec files
  - [x] 7.1 Update `.kiro/steering/monorepo.md` to reflect the new directory structure
    - Update repository layout diagram to show `backend/` top-level directory
    - Update all build command examples from `services/<name>` to `backend/services/<name>`
    - Update "When Adding a New Service" instructions: create under `backend/services/`, use `<relativePath>../../../parents/service-parent/pom.xml</relativePath>`, use pattern `backend/services/<service-name>/**`
    - Update SonarQube project key pattern to `whitbread-eos_digital-monorepo_<service-name>`
    - _Requirements: 6.1, 6.2, 6.3, 6.4_

  - [x] 7.2 Update existing service and library steering files' `fileMatchPattern` values
    - `basket-async-order-processor/` files: change `services/basket-async-order-processor/**` → `backend/services/basket-async-order-processor/**`
    - `piba-account-service-opera/` files: change `services/piba-account-service-opera/**` → `backend/services/piba-account-service-opera/**`
    - `spending-entity-service/` files: change `services/spending-entity-service/**` → `backend/services/spending-entity-service/**`
    - `commons-cdh-lib/` files: change `libs/commons-cdh-lib/**` → `backend/libs/commons-cdh-lib/**`
    - Also update any build commands or path references within these files to use `backend/` prefix
    - _Requirements: 6.5_

  - [x] 7.3 Create steering files for `ohip-adapter-service`
    - Create `.kiro/steering/ohip-adapter-service/product.md` with `fileMatchPattern: "backend/services/ohip-adapter-service/**"`
    - Create `.kiro/steering/ohip-adapter-service/structure.md` with same pattern
    - Create `.kiro/steering/ohip-adapter-service/tech.md` with same pattern
    - Derive content from the imported service's README, POM, and package structure
    - _Requirements: 6.6_

  - [x] 7.4 Create steering files for `hotel-entity-service`
    - Create `.kiro/steering/hotel-entity-service/product.md` with `fileMatchPattern: "backend/services/hotel-entity-service/**"`
    - Create `.kiro/steering/hotel-entity-service/structure.md` with same pattern
    - Create `.kiro/steering/hotel-entity-service/tech.md` with same pattern
    - Derive content from the imported service's README, POM, and package structure
    - _Requirements: 6.7_

  - [x] 7.5 Create steering files for `content-entity-service`
    - Create `.kiro/steering/content-entity-service/product.md` with `fileMatchPattern: "backend/services/content-entity-service/**"`
    - Create `.kiro/steering/content-entity-service/structure.md` with same pattern
    - Create `.kiro/steering/content-entity-service/tech.md` with same pattern
    - Derive content from the imported service's README, POM, and package structure
    - _Requirements: 6.8_

  - [x] 7.6 Update existing spec files in `commons-cdh-lib-monorepo-migration`
    - Replace all `libs/commons-cdh-lib` references with `backend/libs/commons-cdh-lib`
    - Replace all `services/<name>` references with `backend/services/<name>`
    - Replace all `-pl libs/` with `-pl backend/libs/` and `-pl services/` with `-pl backend/services/`
    - _Requirements: 6.9_

- [x] 8. Final build validation
  - [x] 8.1 Run full `./mvnw clean install` and verify all modules compile with zero errors and zero test failures
    - Verify each module produces a packaged artifact in its `target/` directory
    - Verify no `services/` or `libs/` directories remain at the repository root
    - _Requirements: 5.1, 5.2, 2.7, 2.8_

  - [x] 8.2 Run per-module builds to verify inter-module dependency resolution
    - Build each service individually: `./mvnw clean install -pl backend/services/<name> -am`
    - Build each library individually: `./mvnw clean install -pl backend/libs/<name> -am`
    - Verify packaged artifacts exist in `backend/services/<name>/target/` and `backend/libs/<name>/target/`
    - _Requirements: 5.3, 5.4_

  - [x] 8.3 Run stale-reference grep scan across all config and documentation files
    - `grep -rn '"services/' .github/ .kiro/ --include="*.yaml" --include="*.yml" --include="*.md" --include="*.sh"` should return zero matches
    - `grep -rn '"libs/' .github/ .kiro/ --include="*.yaml" --include="*.yml" --include="*.md" --include="*.sh"` should return zero matches
    - Fix any remaining old-style references
    - _Requirements: 5.5, 5.6_

- [x] 9. Final checkpoint - Ensure all builds pass and no stale references remain
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation at key stages
- Git subtree imports must be done on a feature branch (each creates a merge commit)
- The subtree remote URLs and branch names must be provided by the user at execution time
- Property-based testing does not apply — validation is via Maven build and grep-based reference scans
- Tasks 3.1–3.3 each require network access to the external repository remotes

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1"] },
    { "id": 1, "tasks": ["1.2", "1.3"] },
    { "id": 2, "tasks": ["3.1", "3.2", "3.3"] },
    { "id": 3, "tasks": ["3.4"] },
    { "id": 4, "tasks": ["5.1", "5.2", "5.3", "5.4"] },
    { "id": 5, "tasks": ["7.1", "7.2", "7.6"] },
    { "id": 6, "tasks": ["7.3", "7.4", "7.5"] },
    { "id": 7, "tasks": ["8.1"] },
    { "id": 8, "tasks": ["8.2", "8.3"] }
  ]
}
```
