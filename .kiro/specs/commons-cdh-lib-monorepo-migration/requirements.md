# Requirements Document

## Introduction

The internal Whitbread library `uk.co.whitbread.shared:commons-cdh-lib` currently lives in a standalone Git repository and is consumed by monorepo services (`piba-account-service-opera` and `spending-entity-service`) as a Nexus-published Maven artifact. This feature migrates the library source into the monorepo at `backend/libs/commons-cdh-lib/` so the consuming services can build it as a Maven reactor module, eliminating the Nexus round-trip for in-monorepo consumers.

The migration must preserve the full Git history of the original repository using `git subtree` (or `git filter-repo` where path filtering is required) so that the Monorepo can support periodic forward-sync of new commits from the original repository via the standard `git subtree pull` workflow until external consumers migrate, and update CI so that changes under `backend/libs/commons-cdh-lib/**` automatically rebuild every service that depends on the library — discovered dynamically from POMs, not hard-coded — without rebuilding unrelated services.

The original repository must remain operational and may continue publishing to Nexus for external consumers (other repositories or teams) during the transition period. Both consuming services in the monorepo must continue to build, pass all tests, and preserve their existing dependency exclusions after migration.

## Glossary

- **Monorepo**: The Maven multi-module repository at `digital-backend-monorepo` containing services under `backend/services/` and shared libraries under `backend/libs/`.
- **Library_Module**: The new Maven module at `backend/libs/commons-cdh-lib/` containing the migrated `commons-cdh-lib` source code.
- **Source_Repository**: The standalone Git repository that currently hosts `commons-cdh-lib` and continues to exist during the transition.
- **Consumer_Service**: A monorepo service that declares a Maven dependency on `commons-cdh-lib`. At migration time the set is `{piba-account-service-opera, spending-entity-service}` but the set is computed dynamically from POMs.
- **Reactor_Build**: A Maven build that resolves the `commons-cdh-lib` artifact from the local reactor (sibling module) rather than from Nexus.
- **Reverse_Dependency_Map**: A mapping computed from monorepo POMs identifying which services declare a dependency on a given library module.
- **CI_Pipeline**: The GitHub Actions workflows in `.github/workflows/` (`ci.yaml` and `build-service.yaml`) responsible for change detection and per-service builds.
- **Parent_POM**: The root `pom.xml` of the monorepo at `digital-backend-monorepo/pom.xml`.
- **CI_Friendly_Version**: The Maven `${revision}` property (default `1.0.0`) used by the monorepo and resolved by the `flatten-maven-plugin` for installed/deployed POMs.
- **Steering_Directory**: The path `.kiro/steering/commons-cdh-lib/` containing markdown steering files describing the library's product context, tech stack, and structure.
- **Library_Coordinates**: The Maven GAV identifying the library: `groupId=uk.co.whitbread.shared`, `artifactId=commons-cdh-lib`, `version=${revision}`.
- **External_Consumer**: A repository or team outside the monorepo that consumes `commons-cdh-lib` from Nexus and has not yet migrated.
- **Forward_Sync**: The act of pulling new commits from the Source_Repository into the Monorepo's `backend/libs/commons-cdh-lib/` subtree using `git subtree pull` against the `commons-cdh-lib-upstream` Git remote. Performed periodically by maintainers while at least one External_Consumer remains, and follows the same `git subtree pull` workflow already used for other libraries imported into the Monorepo via subtree.

## Requirements

### Requirement 1: Library Import with Preserved Git History

**User Story:** As a platform engineer, I want the `commons-cdh-lib` source imported into the monorepo with full Git history preserved, so that authorship, commit messages, and timestamps remain auditable under the new path.

#### Acceptance Criteria

1. WHEN the import has completed, THE Monorepo SHALL contain a directory `backend/libs/commons-cdh-lib/` populated with the source tree of the Source_Repository branch identified by the SHA recorded in the `git-subtree-split` trailer of the import merge commit.
2. WHEN the import is performed, THE Monorepo SHALL preserve every commit reachable from the imported branch tip of the Source_Repository under the `backend/libs/commons-cdh-lib/` path, retaining original commit author identity, original commit message text, original author timestamp, and original committer timestamp without modification, such that `git log <imported-SHA>` walks back through the upstream history with the original commits intact.
3. THE import SHALL be performed using `git subtree add` without the `--squash` flag, or using `git filter-repo` to rewrite paths into `backend/libs/commons-cdh-lib/` before merging, so that the upstream commits remain reachable in the monorepo (note: `git log -- backend/libs/commons-cdh-lib/` applies path-based history simplification and may show only the merge commit; the upstream commits remain reachable via `git log <imported-SHA>` and via `git log --all`, matching the existing service-import convention in this monorepo).
4. THE Library_Module SHALL retain the Maven coordinates `groupId=uk.co.whitbread.shared` and `artifactId=commons-cdh-lib` so that consumer dependency declarations remain valid without rename.
5. WHEN the import has completed, THE Monorepo SHALL contain exactly one merge commit on the target integration branch produced by `git subtree add`, with the upstream source commit SHA recorded automatically by `git subtree` in the `git-subtree-split` trailer of that merge commit.
6. IF the Source_Repository contains paths that should not be tracked in the Monorepo (such as binary artifacts or generated sources), THEN the import SHALL exclude those paths via `git filter-repo --invert-paths` or an equivalent path filter applied against a scratch clone before the subtree add.
7. IF the directory `backend/libs/commons-cdh-lib/` already exists in the Monorepo with any tracked content prior to the import, THEN THE import SHALL abort without creating commits or modifying any tracked file, AND THE Monorepo SHALL report an error indicating that the target import path is already populated.

### Requirement 2: Reactor-Based Build Integration

**User Story:** As a developer, I want monorepo services to consume `commons-cdh-lib` from the reactor instead of Nexus, so that local source changes are immediately reflected in the consuming services without a publish step.

#### Acceptance Criteria

1. THE Library_Module SHALL declare the Library_Parent_POM as its Maven parent using `<relativePath>../../../parents/library-parent/pom.xml</relativePath>`.
2. THE Library_Module POM SHALL declare `<version>${revision}</version>` and inherit the `revision` property (default `1.0.0`) from the Parent_POM, and WHEN `./mvnw clean install -Drevision=<value>` is executed at the monorepo root, THE Library_Module SHALL be built and installed under that overridden version.
3. THE Parent_POM SHALL list `backend/libs/commons-cdh-lib` as a `<module>` entry so the library is part of the default reactor build executed by `./mvnw clean install` at the monorepo root.
4. WHEN `./mvnw clean install` is executed at the monorepo root, THE Reactor_Build SHALL build the Library_Module before any Consumer_Service that declares a Maven dependency on `uk.co.whitbread.shared:commons-cdh-lib`, as observable in the Maven reactor build order summary.
5. IF the Library_Module build fails during a reactor build, THEN THE Reactor_Build SHALL terminate with a non-zero exit code and SHALL NOT proceed to build any Consumer_Service that depends on `uk.co.whitbread.shared:commons-cdh-lib`.
6. THE Library_Module SHALL be processed by the `flatten-maven-plugin` configured in the Parent_POM so the installed POM in the local Maven repository contains a literal version string equal to the resolved value of `${revision}` rather than the unresolved `${revision}` placeholder.
7. WHEN a Consumer_Service declares an explicit `<dependency>` on `uk.co.whitbread.shared:commons-cdh-lib` and the Library_Module is listed as a `<module>` entry in the Parent_POM, THE Reactor_Build SHALL resolve the dependency from the locally built reactor artifact and SHALL NOT issue a remote download request to Nexus for that artifact during the same Reactor_Build invocation.
8. IF the Library_Module is not listed as a `<module>` entry in the Parent_POM, THEN the Reactor_Build SHALL NOT attempt reactor resolution for `commons-cdh-lib` and SHALL fall back to remote dependency resolution against the configured Nexus repository.
9. THE Parent_POM `<dependencyManagement>` entry for `uk.co.whitbread.shared:commons-cdh-lib` SHALL set its `<version>` to `${revision}` so Consumer_Services inheriting from the Parent_POM resolve the in-monorepo build version without redeclaring the version.
10. WHERE the Library_Module declares dependencies that are also managed in the Parent_POM `<dependencyManagement>`, THE Library_Module POM SHALL omit the `<version>` element and inherit the version from the Parent_POM `<dependencyManagement>`.
11. WHERE the Library_Module declares a dependency not present in the Parent_POM `<dependencyManagement>`, THE Parent_POM `<dependencyManagement>` SHALL be amended as part of this migration to declare that dependency with an explicit version, and THE Library_Module POM SHALL then omit the `<version>` element for that dependency.
12. THE Library_Module SHALL produce a packaged `jar` artifact whose compiled `.class` files target the Java 25 bytecode level (class file major version 69), matching the `maven.compiler.release` value configured in the Parent_POM.

### Requirement 3: Consumer Service POM Updates

**User Story:** As a developer, I want the consuming services updated to use the in-monorepo library, so that they build against the local source without relying on a Nexus-published artifact.

#### Acceptance Criteria

1. THE `backend/services/piba-account-service-opera/pom.xml` SHALL declare exactly one `<dependency>` entry with `groupId=uk.co.whitbread.shared` and `artifactId=commons-cdh-lib` that contains no `<version>` element, no `<scope>system</scope>`, and no `<systemPath>` element, so the version is inherited from the Parent_POM `<dependencyManagement>`.
2. THE `backend/services/spending-entity-service/pom.xml` SHALL declare exactly one `<dependency>` entry with `groupId=uk.co.whitbread.shared` and `artifactId=commons-cdh-lib` that contains no `<version>` element, no `<scope>system</scope>`, and no `<systemPath>` element, so the version is inherited from the Parent_POM `<dependencyManagement>`.
3. THE `backend/services/spending-entity-service/pom.xml` dependency on `commons-cdh-lib` SHALL contain exactly two `<exclusion>` entries with identical groupId and artifactId values to the existing exclusions (`uk.co.whitbread.shared:commons-exceptions` and `commons-fileupload:commons-fileupload`), and SHALL NOT add or remove any other exclusion.
4. WHEN `./mvnw clean install -pl backend/services/piba-account-service-opera -am` is executed from the Monorepo root, THE Reactor_Build SHALL terminate with exit code `0` within 600 seconds, SHALL report zero unit-test failures and zero unit-test errors in the Surefire summary, and SHALL resolve `uk.co.whitbread.shared:commons-cdh-lib` from the local reactor build rather than from Nexus.
5. WHEN `./mvnw clean install -pl backend/services/spending-entity-service -am` is executed from the Monorepo root, THE Reactor_Build SHALL terminate with exit code `0` within 600 seconds, SHALL report zero unit-test failures and zero unit-test errors in the Surefire summary, and SHALL resolve `uk.co.whitbread.shared:commons-cdh-lib` from the local reactor build rather than from Nexus.
6. THE Consumer_Service POMs SHALL NOT contain, for the `commons-cdh-lib` dependency, any `<systemPath>` element, any hard-coded `<version>` value, any `<scope>system</scope>`, or any path string beginning with `/`, `./`, `../`, `${basedir}`, or `${project.basedir}`.
7. WHERE a Consumer_Service relies on transitive dependencies provided by `commons-cdh-lib`, THE Reactor_Build SHALL resolve those transitive dependencies with identical `groupId:artifactId:version:classifier` coordinates to the previous Nexus-based build, verifiable via `./mvnw dependency:tree` against the Consumer_Service.
8. IF the Reactor_Build cannot resolve `uk.co.whitbread.shared:commons-cdh-lib` for a Consumer_Service, THEN THE Reactor_Build SHALL terminate with a non-zero exit code and surface a Maven dependency-resolution error naming the missing artifact.

### Requirement 4: Selective CI Rebuild Based on Reverse Dependencies

**User Story:** As a developer, I want CI to rebuild only the services that depend on `commons-cdh-lib` when the library changes, so that unrelated services are not rebuilt unnecessarily and CI time is minimized.

#### Acceptance Criteria

1. WHEN a push or pull request modifies any file under `backend/libs/commons-cdh-lib/**`, THE CI_Pipeline SHALL compute the Reverse_Dependency_Map from service POMs at runtime, and SHALL complete that computation within 60 seconds.
2. THE CI_Pipeline SHALL compute the Reverse_Dependency_Map by inspecting each `backend/services/*/pom.xml` and selecting services whose declared `<dependency>` entries (including those whose version is inherited from the Parent_POM `<dependencyManagement>` but excluding transitive dependencies) contain a `<groupId>` of `uk.co.whitbread.shared` and an `<artifactId>` of `commons-cdh-lib`.
3. WHEN files under `backend/libs/commons-cdh-lib/**` change and no `backend/services/*/**` files change in the same push or pull request, THE CI_Pipeline SHALL build every Consumer_Service identified by the Reverse_Dependency_Map and SHALL NOT build any service absent from that map.
4. WHEN files under `backend/libs/commons-cdh-lib/**` change in the same push or pull request as files under `backend/backend/services/<service-name>/**`, THE CI_Pipeline SHALL build the deduplicated union of (a) services with direct path changes and (b) Consumer_Services from the Reverse_Dependency_Map, with each service appearing at most once in the build matrix.
5. WHEN a future service adds a dependency on `commons-cdh-lib` to its POM, THE CI_Pipeline SHALL automatically include that service in subsequent CI runs triggered by `backend/libs/commons-cdh-lib/**` changes, without requiring any change to workflow YAML files or hard-coded service lists.
6. WHEN the per-service Maven build runs for a Consumer_Service triggered by a library change, THE CI_Pipeline SHALL invoke Maven with `-pl backend/backend/services/<service-name> -am` so the Library_Module is rebuilt before the service in the same reactor invocation, and THE CI_Pipeline SHALL log the resolved build plan including the service name and the `-am` flag.
7. IF the Reverse_Dependency_Map computation fails (due to a malformed POM or because the 60-second time bound is exceeded), THEN THE CI_Pipeline SHALL fail the change-detection job with a non-zero exit code, SHALL log a message naming the unparseable POM file or the timeout cause, and SHALL NOT start any downstream service-build job.
8. WHEN the root `pom.xml` is modified in the same push or pull request, THE CI_Pipeline SHALL build every service listed as a `<module>` entry in the root `pom.xml` AND every library listed as a `<module>` entry in the root `pom.xml`, consistent with the existing root-pom behaviour.
9. WHEN files under `backend/libs/commons-cdh-lib/**` change and the Reverse_Dependency_Map is empty (no Consumer_Service is identified), THE CI_Pipeline SHALL still build the Library_Module itself via `./mvnw clean install -pl backend/libs/commons-cdh-lib -am` and SHALL log a warning that no Consumer_Service was found.

### Requirement 5: Coexistence with Nexus During Transition

**User Story:** As an External_Consumer, I want `commons-cdh-lib` to remain available on Nexus until I migrate, so that my repository continues to build without disruption.

#### Acceptance Criteria

1. THE Monorepo CI_Pipeline SHALL NOT execute the `mvn deploy` goal, or any equivalent goal that publishes artifacts to a remote repository, against the Library_Module on any branch (including `develop`, `release/*`, and `hotfix/*`).
2. THE Monorepo Library_Module POM SHALL NOT declare a `<distributionManagement>` element targeting a Nexus repository, such that an attempted `./mvnw deploy -pl backend/libs/commons-cdh-lib` invocation terminates with a Maven error indicating the absence of `<distributionManagement>` rather than silently succeeding.
3. WHERE the Source_Repository continues to publish to Nexus, THIS migration SHALL NOT modify any file in the Source_Repository, SHALL NOT modify the Source_Repository CI/CD configuration, and SHALL NOT change the Source_Repository's release cadence or versioning scheme.
4. THE Source_Repository's published artifact and the Monorepo Library_Module SHALL declare identical `groupId` (`uk.co.whitbread.shared`) and identical `artifactId` (`commons-cdh-lib`) values so that an External_Consumer's `<dependency>` declaration resolves to the same artifact identity from either source.
5. IF the resolved value of `${revision}` in the Monorepo differs from the latest version published to Nexus by the Source_Repository, THEN THE Monorepo Library_Module SHALL remain uniquely identifiable by the resolved `${revision}` value (as written into the flattened POM) and SHALL NOT alias to or be confused with the Nexus-published version.
6. WHEN a Consumer_Service is built within the Monorepo reactor, THE Reactor_Build SHALL resolve `uk.co.whitbread.shared:commons-cdh-lib` from the in-monorepo Library_Module regardless of which version is currently latest in Nexus, and SHALL fail with a Maven dependency-resolution error rather than silently fall back to a Nexus-published version of a different `${revision}`.

### Requirement 6: Steering Files for the Library

**User Story:** As a developer, I want steering files describing the library's product context, tech stack, and structure, so that I have the same Kiro guidance available for the library as exists for services.

#### Acceptance Criteria

1. THE Monorepo SHALL contain a Steering_Directory at the path `.kiro/steering/commons-cdh-lib/`.
2. THE Steering_Directory SHALL contain a non-empty `product.md` file that documents (a) the library's responsibilities, (b) its intended consumer services, and (c) its integration points with those consumers.
3. THE Steering_Directory SHALL contain a non-empty `tech.md` file that documents (a) the library's technology stack, (b) its declared dependencies, and (c) the Maven build commands used to build, test, and install the library within the monorepo.
4. THE Steering_Directory SHALL contain a non-empty `structure.md` file that documents (a) the library's source code package and directory layout and (b) the architectural conventions applied within the library.
5. EACH steering file in the Steering_Directory SHALL begin with a YAML front-matter block delimited by `---` lines that contains an `inclusion` key whose `fileMatchPattern` value matches files under the library's source path (for example, `backend/libs/commons-cdh-lib/**`), following the same key structure used by existing service steering files under `.kiro/steering/<service-name>/`.
6. THE Steering_Directory and every file within it SHALL be tracked in Git as committed files in the monorepo.
7. WHEN `./mvnw clean install` is executed at the monorepo root, THE Maven build SHALL complete successfully without requiring any addition, removal, or modification of any `pom.xml` file caused by the presence of the Steering_Directory.

### Requirement 7: Build and Test Validation

**User Story:** As a developer, I want to verify the migration does not break either consuming service, so that I can merge the migration with confidence.

#### Acceptance Criteria

1. WHEN `./mvnw clean install` is executed from a clean working tree at the Monorepo root, THE Reactor_Build SHALL terminate with exit code `0`, SHALL build every module listed as a `<module>` entry in the root `pom.xml` including the Library_Module, SHALL report zero compilation errors, and SHALL report zero unit-test failures and zero unit-test errors aggregated across all modules.
2. WHEN `./mvnw clean install -pl backend/services/piba-account-service-opera -am` is executed from the Monorepo root, THE Reactor_Build SHALL terminate with exit code `0`, SHALL build the Library_Module and `piba-account-service-opera` in the reactor, and SHALL report zero failures across unit tests, contract tests, and Checkstyle violations.
3. IF any unit test, contract test, or Checkstyle check fails during the build of `piba-account-service-opera`, THEN THE Reactor_Build SHALL terminate with a non-zero exit code and SHALL NOT install any artifact for `piba-account-service-opera` to the local Maven repository.
4. WHEN `./mvnw clean install -pl backend/services/spending-entity-service -am` is executed from the Monorepo root, THE Reactor_Build SHALL terminate with exit code `0`, SHALL build the Library_Module and `spending-entity-service` in the reactor, and SHALL report zero failures across unit tests and Checkstyle violations.
5. IF any unit test or Checkstyle check fails during the build of `spending-entity-service`, THEN THE Reactor_Build SHALL terminate with a non-zero exit code and SHALL NOT install any artifact for `spending-entity-service` to the local Maven repository.
6. WHEN `./mvnw clean install -pl backend/libs/commons-cdh-lib` is executed from the Monorepo root, THE Reactor_Build SHALL build the Library_Module in isolation, SHALL terminate with exit code `0`, and SHALL report zero compilation errors and 100% of declared unit tests passing.
7. WHEN the Library_Module build completes, THE Library_Module SHALL produce a JaCoCo coverage report file under its `target/site/jacoco/` directory, and the JaCoCo configuration SHALL match the same coverage thresholds inherited from the Parent_POM `pluginManagement` configuration as applied to services.
8. WHEN the Checkstyle phase executes against the Library_Module, THE Library_Module source SHALL produce zero Checkstyle violations against the same `google-checkstyle.xml` ruleset applied to services via the Parent_POM `pluginManagement`.
9. WHEN the Consumer_Service test suite executes after the migration, AT LEAST one existing test in each Consumer_Service SHALL invoke a public type or method from `uk.co.whitbread.shared:commons-cdh-lib` and SHALL pass against the reactor-resolved Library_Module artifact.
10. IF a Consumer_Service test suite resolves `uk.co.whitbread.shared:commons-cdh-lib` from a non-reactor source (for example, from Nexus instead of the local reactor build), THEN THE Reactor_Build SHALL fail the build with a Maven dependency-resolution diagnostic naming the unexpected resolution source.

### Requirement 8: Maintain Existing Standards in the Library

**User Story:** As a developer, I want the migrated library to follow the monorepo's existing standards, so that it is consistent with the rest of the codebase.

#### Acceptance Criteria

1. THE Library_Module SHALL declare `<java.version>25</java.version>` (or inherit it from the Parent_POM) and SHALL compile successfully against Java 25 with the Jakarta EE namespace, producing zero compilation errors when built via `./mvnw clean install -pl backend/libs/commons-cdh-lib -am`.
2. WHERE the Parent_POM `pluginManagement` already configures annotation processors for Lombok, MapStruct, or any other annotation processor, THE Library_Module SHALL inherit that configuration and SHALL NOT redefine the `annotationProcessorPaths` element or the processor versions in its own POM.
3. WHEN the Library_Module declares a dependency on a Spring Boot artifact, THE Library_Module SHALL omit the `<version>` element on that dependency and SHALL resolve the version from the Parent_POM-managed Spring Boot 4.0.6 BOM.
4. WHERE SonarQube scanning is enabled for the Library_Module, THE Library_Module SHALL declare a SonarQube project key exactly matching the pattern `whitbread-eos_digital-backend-monorepo_commons-cdh-lib`, with no additional prefix, suffix, or whitespace.
5. WHEN the Source_Repository declared a parent POM, Spring Boot version, or Java version that differs from the Monorepo standards (Parent_POM coordinates `uk.co.whitbread:digital-backend-monorepo:${revision}`, Spring Boot 4.0.6, Java 25), THE migration SHALL replace each such declaration with the Monorepo standard value before the migration is considered complete.
6. WHEN the Source_Repository contained one or more `javax.*` Java EE imports (for example `javax.persistence`, `javax.servlet`, `javax.validation`, `javax.ws.rs`, `javax.annotation`, `javax.inject`, `javax.transaction`), THE migration SHALL replace every such import with its `jakarta.*` equivalent, leaving zero `javax.*` Java EE imports in the migrated Library_Module source tree.
7. IF the Library_Module POM cannot inherit a required version, dependency, or plugin configuration from the Parent_POM because the Parent_POM does not define it, THEN THE migration SHALL halt the affected change and surface an error indicating which Parent_POM element is missing, without silently hard-coding a version in the Library_Module POM.

### Requirement 9: Source Repository Reference and Validation

**User Story:** As a platform engineer, I want the Source_Repository URL configured as a Git remote and the import-source SHA captured by the standard `git subtree` mechanism, so that the upstream remains identifiable for auditability and for any future pulls from the original repository — matching the convention used for previously imported services.

#### Acceptance Criteria

1. WHEN the initial import is performed, THE Monorepo SHALL include the Source_Repository as a Git remote named `commons-cdh-lib-upstream`, with the configured remote URL set to a non-empty literal value distinct from placeholder strings such as "TBD", "TODO", "<url>", or an empty string.
2. THE import merge commit produced by `git subtree add` SHALL contain a `git-subtree-split` trailer holding the full 40-character hexadecimal source commit SHA, captured automatically by `git subtree` (no manual amendment required).
3. THE migration SHALL NOT delete or archive the Source_Repository; the decision to retire the Source_Repository is out of scope for this feature and SHALL be made separately once all External_Consumers have migrated.
4. WHEN the initial import is performed, THE Monorepo SHALL verify that the configured `commons-cdh-lib-upstream` remote URL is reachable and that the import-source SHA resolves to a commit on that remote before the initial import is treated as complete.
5. IF the configured `commons-cdh-lib-upstream` remote URL is unreachable, or the import-source SHA cannot be resolved on that remote, during the initial import, THEN THE Monorepo SHALL halt the initial import without producing imported files under `backend/libs/commons-cdh-lib` and SHALL surface an error indicating which value (URL or SHA) failed validation.
