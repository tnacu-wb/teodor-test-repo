# Implementation Plan: Monorepo Finalization

## Overview

This plan converts the digital-backend-monorepo from its current flat layout (services at root level with their own `.git`, `.github`, `.mvn`, etc.) into the target structure with services under `services/`, a single root Maven Wrapper, consolidated `.gitignore`, root README, and self-contained CI workflows. Each task is a discrete, independently verifiable step that builds on the previous ones.

## Tasks

- [x] 1. Import git history via subtree
  - [x] 1.1 Import basket-async-order-processor history
    - Add remote `basket-async-order-processor` pointing to `https://github.com/whitbread-eos/basket-async-order-processor.git`
    - Fetch the remote
    - Run `git subtree add --prefix=services/basket-async-order-processor basket-async-order-processor develop`
    - Remove the remote
    - Verify `git log --oneline services/basket-async-order-processor/` shows imported history
    - _Requirements: 2.1, 2.2_

  - [x] 1.2 Import piba-account-service-opera history
    - Add remote `piba-account-service-opera` pointing to `https://github.com/whitbread-eos/piba-account-service-opera.git`
    - Fetch the remote
    - Run `git subtree add --prefix=services/piba-account-service-opera piba-account-service-opera develop`
    - Remove the remote
    - Verify `git log --oneline services/piba-account-service-opera/` shows imported history
    - _Requirements: 2.1, 2.3_

  - [x] 1.3 Import spending-entity-service history
    - Add remote `spending-entity-service` pointing to `https://github.com/whitbread-eos/spending-entity-service.git`
    - Fetch the remote
    - Run `git subtree add --prefix=services/spending-entity-service spending-entity-service develop`
    - Remove the remote
    - Verify `git log --oneline services/spending-entity-service/` shows imported history
    - _Requirements: 2.1, 2.4_

- [x] 2. Preserve refined service POMs in services/
  - [x] 2.1 Copy refined monorepo-compatible POMs into services/
    - Copy `basket-async-order-processor/pom.xml` to `services/basket-async-order-processor/pom.xml` (overwriting the subtree-imported standalone POM)
    - Copy `piba-account-service-opera/pom.xml` to `services/piba-account-service-opera/pom.xml`
    - Copy `spending-entity-service/pom.xml` to `services/spending-entity-service/pom.xml`
    - Verify each copied POM has `<parent><artifactId>digital-backend-monorepo</artifactId></parent>`
    - _Requirements: 1.5, 3.1, 3.2_

- [x] 3. Remove old root-level service directories
  - [x] 3.1 Remove root-level service directories
    - Delete `basket-async-order-processor/` from the repository root
    - Delete `piba-account-service-opera/` from the repository root
    - Delete `spending-entity-service/` from the repository root
    - Stage removals with `git rm -rf`
    - Commit the removal
    - _Requirements: 1.1, 1.2_

- [x] 4. Clean up per-service artifacts
  - [x] 4.1 Remove per-service git and CI artifacts from services/
    - Remove `services/basket-async-order-processor/.git/` (if present after subtree)
    - Remove `services/piba-account-service-opera/.git/` (if present)
    - Remove `services/spending-entity-service/.git/` (if present)
    - Remove `.github/` directories from all three services
    - Remove `CODEOWNERS` files from all three services (if present)
    - _Requirements: 2.5, 2.6, 2.7_

  - [x] 4.2 Remove per-service .gitignore and Maven Wrapper files from services/
    - Remove `.gitignore` from each service directory under `services/`
    - Remove `mvnw`, `mvnw.cmd`, `.mvn/` from each service directory under `services/`
    - _Requirements: 4.3, 6.3_

- [x] 5. Update root POM module paths
  - [x] 5.1 Update root POM modules to services/ paths
    - Change `<module>basket-async-order-processor</module>` to `<module>services/basket-async-order-processor</module>`
    - Change `<module>piba-account-service-opera</module>` to `<module>services/piba-account-service-opera</module>`
    - Change `<module>spending-entity-service</module>` to `<module>services/spending-entity-service</module>`
    - _Requirements: 1.4_

- [x] 6. Update service POMs
  - [x] 6.1 Update service POMs with correct relativePath and remove version
    - The refined POMs already reference `digital-backend-monorepo` as parent; only path and version adjustments are needed:
      - Change `<relativePath>../pom.xml</relativePath>` to `<relativePath>../../pom.xml</relativePath>`
      - Change parent `<version>1.0.0-SNAPSHOT</version>` to `<version>1.0.0</version>`
      - Remove the project-level `<version>` element (so version is inherited from parent)
    - Apply to: `services/basket-async-order-processor/pom.xml`, `services/piba-account-service-opera/pom.xml`, `services/spending-entity-service/pom.xml`
    - _Requirements: 1.5, 3.1, 3.2_

- [x] 7. Set root POM version to 1.0.0
  - [x] 7.1 Update root POM version
    - Change `<version>1.0.0-SNAPSHOT</version>` to `<version>1.0.0</version>` in the root `pom.xml`
    - _Requirements: 3.1_

- [x] 8. Create libs/.gitkeep
  - [x] 8.1 Create libs directory with .gitkeep
    - Create directory `libs/` at the repository root
    - Create an empty file `libs/.gitkeep`
    - _Requirements: 1.3_

- [x] 9. Create consolidated root .gitignore
  - [x] 9.1 Create root .gitignore
    - Create `.gitignore` at the repository root with consolidated patterns:
      - OS: `*.DS_Store`
      - Maven: `target/`, `pom.xml.tag`, `pom.xml.releaseBackup`, `pom.xml.versionsBackup`, `pom.xml.next`, `release.properties`, `dependency-reduced-pom.xml`, `buildNumber.properties`, `.mvn/timing.properties`, `!.mvn/wrapper/maven-wrapper.jar`
      - Java: `*.class`
      - IntelliJ IDEA: `.idea/`, `*.iws`, `*.iml`, `*.ipr`
    - _Requirements: 4.1, 4.2_

- [x] 10. Install root Maven Wrapper
  - [x] 10.1 Install Maven Wrapper at repository root
    - Run `mvn wrapper:wrapper` from the repository root (or copy `.mvn/wrapper/` from an existing service before cleanup)
    - Ensure `mvnw` and `mvnw.cmd` are present at the root
    - Ensure `mvnw` and `mvnw.cmd` have executable permissions (`chmod +x mvnw mvnw.cmd`)
    - Ensure `.mvn/wrapper/maven-wrapper.properties` exists
    - _Requirements: 6.1, 6.2_

- [x] 11. Create root README
  - [x] 11.1 Create root README.md
    - Create `README.md` at the repository root with sections:
      - Project title and description (Whitbread Digital Backend Monorepo)
      - Directory structure diagram showing `services/` and `libs/`
      - Services list: basket-async-order-processor, piba-account-service-opera, spending-entity-service with brief descriptions
      - Build instructions: full build (`./mvnw clean install`), single service (`./mvnw clean install -pl services/<name> -am`), skip tests (`-DskipTests`)
      - Versioning strategy: lock-step versioning, all modules inherit from root POM
      - CI pipeline overview: change detection, parallel matrix builds, Sonar, CodeQL
      - Prerequisites: JDK 25, Maven via wrapper
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6_

- [x] 12. Create CI workflows
  - [x] 12.1 Create CI orchestrator workflow
    - Create `.github/workflows/ci.yaml` with:
      - Trigger on push to `develop`, `release/*`, `hotfix/*` branches
      - Trigger on pull_request targeting `develop`
      - Job `detect-changes`: use `git diff` or `dorny/paths-filter` to determine which services changed by comparing file paths under `services/`
      - If root `pom.xml` changed, mark all services as changed
      - Output JSON array of changed service names
      - Job `build-services`: matrix strategy using the changed services array, `fail-fast: false`
      - Call `build-service.yaml` reusable workflow for each changed service
      - All jobs use `tooling-default-runner-scale-set` runner
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 7.7, 7.8_

  - [x] 12.2 Create build-service reusable workflow
    - Create `.github/workflows/build-service.yaml` with:
      - Input: `service-name` (string)
      - Steps: checkout code, set up JDK 25, restore Maven cache (`~/.m2`, key from `hashFiles('**/pom.xml')`)
      - Decode `SETTINGS_XML` variable and write to `~/.m2/settings.xml`
      - Run `mvn verify -pl services/<service-name> -am ${{ vars.MAVEN_CLI_OPTS }}`
      - Run SonarQube scan with project key `whitbread-eos_<service-name>`
      - Run CodeQL analysis (Java)
      - Upload JAR artifact from `services/<service-name>/target/*.jar`
      - Explicitly NO Docker build, container push, Helm release, DIT sync, or release-checks
    - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5, 8.6, 8.7_

- [x] 13. Checkpoint - Validate full build
  - [x] 13.1 Run full Maven build validation
    - Run `./mvnw clean install` from the repository root
    - Verify build exits with BUILD SUCCESS
    - Verify all unit and integration tests pass with zero failures
    - Verify JAR artifacts are produced: `basket-async-order-processor-1.0.0.jar`, `piba-account-service-opera-1.0.0.jar`, `spending-entity-service-1.0.0.jar`
    - Verify `git log --oneline services/basket-async-order-processor/` shows imported history
    - Verify `git log --oneline services/piba-account-service-opera/` shows imported history
    - Verify `git log --oneline services/spending-entity-service/` shows imported history
    - Verify no nested `.git/` directories exist under `services/`
    - Ensure all tests pass, ask the user if questions arise.
    - _Requirements: 9.1, 9.2, 9.3_

## Notes

- Each task corresponds to a discrete commit for traceability and easy rollback
- Tasks 1.1–1.3 must be sequential (each subtree add creates a merge commit)
- Task 2.1 must run after subtree imports and before root-level directory removal (preserves refined POMs)
- Tasks 8.1, 9.1, 10.1, 11.1 are independent and can run in parallel after task 7.1
- Task 12.1/12.2 (CI workflows) can also run in parallel with tasks 8–11
- Task 13.1 is the final validation gate depending on all previous tasks
- The `git subtree add` commands require read access to the `whitbread-eos` GitHub org
- The settings.xml for local builds is at: `/Users/856124/Library/CloudStorage/OneDrive-Cognizant/Documents/Whitbread/maven/settings.xml`
- CI runners use `tooling-default-runner-scale-set`

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1"] },
    { "id": 1, "tasks": ["1.2"] },
    { "id": 2, "tasks": ["1.3"] },
    { "id": 3, "tasks": ["2.1"] },
    { "id": 4, "tasks": ["3.1"] },
    { "id": 5, "tasks": ["4.1", "4.2"] },
    { "id": 6, "tasks": ["5.1"] },
    { "id": 7, "tasks": ["6.1"] },
    { "id": 8, "tasks": ["7.1"] },
    { "id": 9, "tasks": ["8.1", "9.1", "10.1", "11.1", "12.1", "12.2"] },
    { "id": 10, "tasks": ["13.1"] }
  ]
}
```
