# Requirements Document

## Introduction

This feature finalizes the digital-backend-monorepo structure after the BOM restructure is complete. It covers directory reorganization (moving services into a `services/` subdirectory), importing full Git history from original repositories, establishing lock-step versioning, consolidating configuration files (`.gitignore`, Maven Wrapper), creating documentation, and setting up a self-contained GitHub Actions CI pipeline for build and analysis (no deployment). The goal is a production-ready monorepo layout that supports parallel builds, shared versioning, and clean separation of concerns.

## Glossary

- **Monorepo**: The `digital-backend-monorepo` repository containing all services under a single root
- **Root_POM**: The top-level `pom.xml` at the repository root that acts as the parent for all Service_Modules
- **Service_Module**: An individual microservice directory (basket-async-order-processor, piba-account-service-opera, spending-entity-service) declared as a `<module>` in the Root_POM
- **Service_POM**: The `pom.xml` file within each Service_Module
- **Services_Directory**: The `services/` directory at the repository root that contains all Service_Modules
- **Libs_Directory**: The `libs/` directory at the repository root reserved for future shared libraries
- **Lock_Step_Version**: A single version string (`1.0.0`) shared by the Root_POM and all Service_Modules
- **Maven_Wrapper**: The set of files (`.mvn/wrapper/`, `mvnw`, `mvnw.cmd`) that allow building without a pre-installed Maven
- **CI_Orchestrator**: The `.github/workflows/ci.yaml` workflow that detects changes and fans out builds
- **Build_Service_Workflow**: The `.github/workflows/build-service.yaml` reusable workflow that builds a single service
- **Tooling_Runner**: The self-hosted GitHub Actions runner `tooling-default-runner-scale-set` used for CI jobs

## Requirements

### Requirement 1: Directory Restructure

**User Story:** As a developer, I want services organized under a `services/` directory with a `libs/` placeholder, so that the monorepo has a clear, scalable layout separating applications from shared libraries.

#### Acceptance Criteria

1. THE Monorepo SHALL contain a Services_Directory at the repository root
2. THE Services_Directory SHALL contain the directories `basket-async-order-processor`, `piba-account-service-opera`, and `spending-entity-service`, each with their full source tree intact
3. THE Monorepo SHALL contain a Libs_Directory at the repository root with a `.gitkeep` file to preserve the empty directory in Git
4. THE Root_POM SHALL declare modules as `services/basket-async-order-processor`, `services/piba-account-service-opera`, and `services/spending-entity-service`
5. WHEN a Service_Module resides under Services_Directory, THE Service_POM SHALL set `<relativePath>` to `../../pom.xml` in its `<parent>` element

### Requirement 2: Git History Preservation

**User Story:** As a developer, I want the full commit history from each original service repository imported into the monorepo, so that `git log` and `git blame` work correctly for all files.

#### Acceptance Criteria

1. WHEN importing history for a Service_Module, THE Monorepo SHALL use `git subtree add` (or equivalent filter-and-merge approach) to graft the original repository history under `services/<service-name>/`
2. WHEN history import is complete, THE Monorepo SHALL contain the full commit history from `https://github.com/whitbread-eos/basket-async-order-processor.git` under `services/basket-async-order-processor/`
3. WHEN history import is complete, THE Monorepo SHALL contain the full commit history from the `piba-account-service-opera` original repository under `services/piba-account-service-opera/`
4. WHEN history import is complete, THE Monorepo SHALL contain the full commit history from the `spending-entity-service` original repository under `services/spending-entity-service/`
5. WHEN history import is complete, THE Monorepo SHALL NOT contain any nested `.git` directories within Service_Modules
6. WHEN history import is complete, THE Monorepo SHALL NOT contain any nested `.github/` directories within Service_Modules
7. WHEN history import is complete, THE Monorepo SHALL NOT contain any per-service `CODEOWNERS` files within Service_Modules

### Requirement 3: Lock-Step Versioning

**User Story:** As a developer, I want all services to share a single version number inherited from the root POM, so that releases are coordinated and artifact versions are predictable.

#### Acceptance Criteria

1. THE Root_POM SHALL declare its `<version>` as `1.0.0` without a `-SNAPSHOT` suffix
2. WHEN a Service_Module inherits from the Root_POM, THE Service_POM SHALL NOT declare its own `<version>` element, inheriting the version from the Root_POM parent
3. WHEN `mvn package` is executed from the repository root, THE build SHALL produce JAR artifacts named `<artifactId>-1.0.0.jar` for each Service_Module (basket-async-order-processor-1.0.0.jar, piba-account-service-opera-1.0.0.jar, spending-entity-service-1.0.0.jar)

### Requirement 4: Consolidated Root .gitignore

**User Story:** As a developer, I want a single root `.gitignore` that covers all services, so that ignore rules are consistent and per-service `.gitignore` files are eliminated.

#### Acceptance Criteria

1. THE Monorepo SHALL contain a single `.gitignore` file at the repository root
2. THE root `.gitignore` SHALL include patterns for Maven build output (`target/`), IDE files (`.idea/`, `*.iml`, `*.iws`, `*.ipr`), OS files (`*.DS_Store`), Maven temporary files (`pom.xml.tag`, `pom.xml.releaseBackup`, `pom.xml.versionsBackup`, `pom.xml.next`, `release.properties`, `dependency-reduced-pom.xml`, `buildNumber.properties`, `.mvn/timing.properties`), and Java class files (`*.class`)
3. WHEN the root `.gitignore` is in place, THE Monorepo SHALL NOT contain any `.gitignore` files within Service_Module directories

### Requirement 5: Root README Documentation

**User Story:** As a developer, I want a root README that documents the monorepo structure, build instructions, and versioning strategy, so that new team members can onboard quickly.

#### Acceptance Criteria

1. THE Monorepo SHALL contain a `README.md` file at the repository root
2. THE root README SHALL document the directory structure including Services_Directory and Libs_Directory with their purposes
3. THE root README SHALL list all Service_Modules with a brief description of each service
4. THE root README SHALL include build instructions for building all services (`./mvnw clean install`) and individual services (`./mvnw clean install -pl services/<name> -am`)
5. THE root README SHALL describe the lock-step versioning strategy
6. THE root README SHALL include a CI overview section describing the build pipeline

### Requirement 6: Root Maven Wrapper

**User Story:** As a developer, I want a single Maven Wrapper at the repository root, so that all builds use the same Maven version and per-service wrappers are eliminated.

#### Acceptance Criteria

1. THE Monorepo SHALL contain Maven Wrapper files at the repository root (`.mvn/wrapper/maven-wrapper.properties`, `mvnw`, `mvnw.cmd`)
2. THE root `mvnw` and `mvnw.cmd` files SHALL be executable
3. WHEN the root Maven Wrapper is in place, THE Monorepo SHALL NOT contain any Maven Wrapper files (`mvnw`, `mvnw.cmd`, `.mvn/` directory) within Service_Module directories

### Requirement 7: CI Orchestrator Workflow

**User Story:** As a developer, I want a CI orchestrator workflow that detects which services changed and triggers parallel builds only for affected services, so that CI is fast and resource-efficient.

#### Acceptance Criteria

1. THE Monorepo SHALL contain a workflow file at `.github/workflows/ci.yaml`
2. THE CI_Orchestrator SHALL trigger on push events to branches `develop`, `release/*`, and `hotfix/*`
3. THE CI_Orchestrator SHALL trigger on pull request events targeting the `develop` branch
4. WHEN a push or pull request event occurs, THE CI_Orchestrator SHALL determine which Service_Modules have changed by comparing file paths under `services/`
5. WHEN the root `pom.xml` has changed, THE CI_Orchestrator SHALL mark all Service_Modules as changed
6. THE CI_Orchestrator SHALL fan out builds using a GitHub Actions matrix strategy with one parallel job per changed Service_Module
7. THE CI_Orchestrator SHALL set `fail-fast: false` on the matrix strategy so that a failure in one service does not cancel builds of other services
8. THE CI_Orchestrator SHALL use `tooling-default-runner-scale-set` as the runner for all jobs

### Requirement 8: Service Build Jobs

**User Story:** As a developer, I want each service build job to run Maven verify, Sonar scan, CodeQL analysis, and upload artifacts, so that each service gets consistent quality checks.

#### Acceptance Criteria

1. THE Monorepo SHALL contain a reusable workflow file at `.github/workflows/build-service.yaml` that the CI_Orchestrator calls for each changed Service_Module
2. WHEN the Build_Service_Workflow is invoked for a Service_Module, THE workflow SHALL execute `mvn verify -pl services/<service-name> -am`
3. WHEN the Build_Service_Workflow is invoked for a Service_Module, THE workflow SHALL run a SonarQube scan with a service-specific project key following the pattern `whitbread-eos_<service-name>`
4. WHEN the Build_Service_Workflow is invoked for a Service_Module, THE workflow SHALL run CodeQL analysis for Java
5. WHEN the Build_Service_Workflow is invoked for a Service_Module, THE workflow SHALL upload the JAR artifact from `services/<service-name>/target/*.jar`
6. THE Build_Service_Workflow SHALL cache the Maven local repository (`~/.m2`) using a cache key derived from `hashFiles('**/pom.xml')`
7. THE Build_Service_Workflow SHALL NOT build Docker images, push container images, perform Helm releases, update DIT sync tags, or execute release-checks against any cluster

### Requirement 9: Full Build Validation

**User Story:** As a developer, I want to validate that the entire restructured monorepo builds successfully with correct artifact names, so that I have confidence the restructuring is complete and correct.

#### Acceptance Criteria

1. WHEN `mvn clean install` is executed from the repository root after all restructuring, THE build SHALL exit with BUILD SUCCESS status
2. WHEN the full build completes, THE build SHALL have executed all unit and integration tests for all Service_Modules with zero test failures
3. WHEN the full build completes, THE build SHALL have produced JAR artifacts with lock-step version names: `basket-async-order-processor-1.0.0.jar`, `piba-account-service-opera-1.0.0.jar`, `spending-entity-service-1.0.0.jar`
