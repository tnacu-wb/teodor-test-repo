# Requirements Document

## Introduction

This specification covers the restructuring of the `digital-backend-monorepo` into `digital-monorepo`. The restructuring introduces a `backend/` top-level directory to namespace backend services and libraries, imports three external repositories (ohip-adapter-service, hotel-entity-service, content-entity-service) via git subtree to preserve commit history, updates all CI/CD pipelines to accommodate the new paths, updates steering and spec files to reflect the new structure, and validates that the full build passes after migration.

## Glossary

- **Monorepo**: The multi-module Maven repository containing all Whitbread digital backend microservices and shared libraries
- **Root_POM**: The top-level `pom.xml` that defines modules, shared properties, dependency management, and plugin management for the entire monorepo
- **Service_POM**: The `pom.xml` within each microservice directory that inherits from the Root_POM
- **CI_Pipeline**: The GitHub Actions workflow (`ci.yaml`) that orchestrates change detection, parallel matrix builds, SonarQube analysis, and artifact uploads
- **Change_Detection_Script**: The shell script (`detect-changed-modules.sh`) that identifies which modules have changed and need rebuilding
- **Reverse_Deps_Script**: The shell script (`reverse-deps.sh`) that finds services depending on a changed library
- **Git_Subtree**: A Git mechanism for importing an external repository into a subdirectory while preserving its full commit history
- **Build_Service_Workflow**: The reusable GitHub Actions workflow (`build-service.yaml`) that compiles, tests, scans, and uploads artifacts for a single service
- **Build_Library_Workflow**: The reusable GitHub Actions workflow (`build-library.yaml`) that compiles and tests a single shared library
- **Steering_File**: A Kiro steering markdown file (located under `.kiro/steering/`) that provides contextual guidance for a service or the monorepo, activated via `fileMatchPattern` or `inclusion: always`

## Requirements

### Requirement 1: Repository Rename

**User Story:** As a platform engineer, I want to rename the repository from `digital-backend-monorepo` to `digital-monorepo`, so that it reflects the broader scope of the repository beyond just backend services.

#### Acceptance Criteria

1. THE Root_POM SHALL use `digital-monorepo` as the `<artifactId>` value
2. THE Root_POM SHALL use `digital-monorepo` as the `<name>` value
3. THE Root_POM SHALL use `Whitbread Digital Monorepo` as the `<description>` value
4. WHEN the Build_Service_Workflow runs a Sonar scan, THE Build_Service_Workflow SHALL use the project key pattern `whitbread-eos_digital-monorepo_<service-name>`
5. WHEN the Build_Service_Workflow runs a Sonar scan, THE Build_Service_Workflow SHALL use the project name pattern `digital-monorepo :: <service-name>`

### Requirement 2: Backend Directory Structure

**User Story:** As a platform engineer, I want backend services and libraries moved under a `backend/` top-level directory with `services/` and `libs/` subdirectories, so that the repository is structured to accommodate future non-backend modules (e.g., frontend, infrastructure).

#### Acceptance Criteria

1. THE Monorepo SHALL contain all backend microservice modules under the path `backend/services/<service-name>/`
2. THE Monorepo SHALL contain all shared library modules under the path `backend/libs/<library-name>/`
3. THE Root_POM SHALL declare module paths using the `backend/services/<service-name>` format for services
4. THE Root_POM SHALL declare module paths using the `backend/libs/<library-name>` format for libraries
5. WHEN a Service_POM references its parent, THE Service_POM SHALL use `<relativePath>../../../parents/service-parent/pom.xml</relativePath>` to resolve the service parent POM
6. WHEN a library POM references its parent, THE library POM SHALL use `<relativePath>../../../parents/library-parent/pom.xml</relativePath>` to resolve the library parent POM
7. THE Monorepo SHALL NOT contain any service or library module directories directly under the repository root `services/` or `libs/` paths after migration
8. WHEN `./mvnw clean install` is executed from the repository root, THE build SHALL resolve all module paths under `backend/` and complete without module-not-found errors

### Requirement 3: Import External Repositories via Git Subtree

**User Story:** As a platform engineer, I want to import `ohip-adapter-service`, `hotel-entity-service`, and `content-entity-service` into the monorepo using git subtree, so that their full commit history is preserved and they follow the same structure as existing services.

#### Acceptance Criteria

1. WHEN importing an external repository, THE Monorepo SHALL use `git subtree add` with the `--prefix=backend/services/<service-name>` option to place the service under the correct path
2. WHEN importing an external repository, THE Monorepo SHALL use `git subtree add` without the `--squash` flag so that the full commit history of the imported repository is preserved as individual commits in the monorepo history
3. WHEN the `ohip-adapter-service` repository has been imported, THE Root_POM SHALL include `<module>backend/services/ohip-adapter-service</module>` in its `<modules>` section
4. WHEN the `hotel-entity-service` repository has been imported, THE Root_POM SHALL include `<module>backend/services/hotel-entity-service</module>` in its `<modules>` section
5. WHEN the `content-entity-service` repository has been imported, THE Root_POM SHALL include `<module>backend/services/content-entity-service</module>` in its `<modules>` section
6. WHEN an imported service is placed in the monorepo, THE Service_POM SHALL inherit from the service parent using `<relativePath>../../../parents/service-parent/pom.xml</relativePath>`
7. WHEN an imported service has standalone CI configuration files (Jenkinsfile, .github/workflows/ directory, or .gitlab-ci.yml), THE Monorepo SHALL remove those standalone CI configuration files from the imported service directory
8. WHEN all three services have been imported and their POMs updated, THE Monorepo SHALL pass `./mvnw clean install -pl backend/services/ohip-adapter-service,backend/services/hotel-entity-service,backend/services/content-entity-service -am` with all modules compiling successfully

### Requirement 4: Update GitHub Actions for New Structure

**User Story:** As a platform engineer, I want the CI pipeline updated to work with the new `backend/services/` and `backend/libs/` directory structure, so that change detection, builds, and scans continue to function correctly.

#### Acceptance Criteria

1. WHEN detecting changed modules, THE Change_Detection_Script SHALL discover all service directories by scanning `backend/services/` instead of `services/` for service-level `pom.xml` files
2. WHEN detecting changed modules, THE Change_Detection_Script SHALL discover all library directories by scanning `backend/libs/` instead of `libs/` for library-level `pom.xml` files
3. WHEN detecting changed files that match service paths, THE Change_Detection_Script SHALL match the pattern `backend/services/<service-name>/` instead of `services/<service-name>/`
4. WHEN detecting changed files that match library paths, THE Change_Detection_Script SHALL match the pattern `backend/libs/<library-name>/` instead of `libs/<library-name>/`
5. WHEN building a service, THE Build_Service_Workflow SHALL use `-pl "backend/services/$SERVICE_NAME"` in Maven build and verify commands
6. WHEN building a service, THE Build_Service_Workflow SHALL upload artifacts from `backend/services/${{ inputs.service-name }}/target/*.jar`
7. WHEN building a service, THE Build_Service_Workflow SHALL use the cache key hash path `backend/services/{0}/pom.xml`
8. WHEN building a library, THE Build_Library_Workflow SHALL use `-pl "backend/libs/$LIBRARY_NAME"` in Maven build and verify commands
9. WHEN building a library, THE Build_Library_Workflow SHALL use the cache key hash path `backend/libs/{0}/pom.xml`
10. WHEN the Reverse_Deps_Script searches for dependent services, THE Reverse_Deps_Script SHALL scan `backend/services/` instead of `services/` for service POMs containing a matching dependency declaration
11. WHEN the Build_Service_Workflow runs a Sonar scan, THE Build_Service_Workflow SHALL use `-pl "backend/services/$SERVICE_NAME"` and exclude `backend/libs/**` from analysis
12. WHEN a library POM path is resolved for reverse-dependency coordinate extraction, THE Change_Detection_Script SHALL use `backend/libs/<library-name>/pom.xml` instead of `libs/<library-name>/pom.xml`
13. IF the root `pom.xml` is changed, THEN THE Change_Detection_Script SHALL output all service names discovered under `backend/services/` and all library names discovered under `backend/libs/` for a full rebuild

### Requirement 5: Build Validation

**User Story:** As a platform engineer, I want to validate that the full Maven build passes after restructuring, so that I have confidence no module references or dependencies are broken.

#### Acceptance Criteria

1. WHEN the restructuring is complete, THE Monorepo SHALL pass a full `./mvnw clean install` build with all modules compiled successfully and zero compilation errors
2. WHEN the restructuring is complete, THE Monorepo SHALL pass all unit tests across all modules with zero test failures
3. WHEN a single service is built with `-pl backend/services/<service-name> -am`, THE Monorepo SHALL resolve all inter-module dependencies correctly and produce a packaged artifact in `backend/services/<service-name>/target/`
4. WHEN a library is built with `-pl backend/libs/<library-name> -am`, THE Monorepo SHALL resolve the parent POM correctly and produce a packaged artifact in `backend/libs/<library-name>/target/`
5. IF the full build fails after restructuring, THEN THE Monorepo SHALL have all `<relativePath>` values corrected until the build passes
6. WHEN the GitHub Actions CI workflow is executed against the restructured repository, THE CI_Pipeline SHALL detect all modules under `backend/services/` and `backend/libs/` and complete without workflow-level errors

### Requirement 6: Update Steering and Spec Files

**User Story:** As a platform engineer, I want the Kiro steering files and spec files updated to reflect the new `backend/` directory structure, so that IDE contextual guidance and existing migration specs remain accurate.

#### Acceptance Criteria

1. WHEN the restructuring is complete, THE monorepo Steering_File (`.kiro/steering/monorepo.md`) SHALL reference `backend/services/<service-name>` in all path examples, build commands, and the repository layout diagram
2. WHEN the restructuring is complete, THE monorepo Steering_File SHALL instruct engineers to create new service directories under `backend/services/` in the "When Adding a New Service" section
3. WHEN the restructuring is complete, THE monorepo Steering_File SHALL document `<relativePath>../../../parents/service-parent/pom.xml</relativePath>` as the correct parent reference for new services
4. WHEN the restructuring is complete, THE monorepo Steering_File SHALL document new service steering file patterns as `fileMatchPattern: "backend/services/<service-name>/**"`
5. WHEN an existing service steering file uses `fileMatchPattern: "services/<service-name>/**"`, THE Steering_File SHALL be updated to use `fileMatchPattern: "backend/services/<service-name>/**"`
6. WHEN the `ohip-adapter-service` has been imported, THE Monorepo SHALL contain steering files at `.kiro/steering/ohip-adapter-service/` with at minimum `product.md`, `structure.md`, and `tech.md`, each using `fileMatchPattern: "backend/services/ohip-adapter-service/**"`
7. WHEN the `hotel-entity-service` has been imported, THE Monorepo SHALL contain steering files at `.kiro/steering/hotel-entity-service/` with at minimum `product.md`, `structure.md`, and `tech.md`, each using `fileMatchPattern: "backend/services/hotel-entity-service/**"`
8. WHEN the `content-entity-service` has been imported, THE Monorepo SHALL contain steering files at `.kiro/steering/content-entity-service/` with at minimum `product.md`, `structure.md`, and `tech.md`, each using `fileMatchPattern: "backend/services/content-entity-service/**"`
9. WHEN the existing spec for `commons-cdh-lib-monorepo-migration` references paths using `libs/` or `services/` prefixes, THE spec files SHALL be updated to use `backend/libs/` or `backend/services/` prefixes respectively
