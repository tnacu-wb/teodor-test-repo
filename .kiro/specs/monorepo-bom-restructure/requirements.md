# Requirements Document

## Introduction

This feature restructures the digital-backend-monorepo project from independent services each using `spring-cloud-microservice-parent` as their parent POM into a proper Maven multi-module monorepo. A root POM acts as a Bill of Materials (BOM) that centrally manages all dependency versions. Individual service modules inherit from the root POM and declare dependencies without specifying versions, ensuring consistency across all services.

## Glossary

- **Root_POM**: The top-level `pom.xml` file at the repository root that acts as the parent for all service modules and centrally manages dependency versions via `<dependencyManagement>`
- **BOM**: Bill of Materials — a Maven pattern where a parent or imported POM defines dependency versions in `<dependencyManagement>` so child modules do not need to specify versions
- **Service_Module**: An individual microservice directory (basket-async-order-processor, piba-account-service-opera, spending-entity-service) that is declared as a `<module>` in the Root_POM
- **Service_POM**: The `pom.xml` file within each Service_Module that declares dependencies without version numbers
- **DependencyManagement_Section**: The `<dependencyManagement>` block in the Root_POM that defines versions for all shared and service-specific dependencies
- **Spring_Boot_Parent**: The `org.springframework.boot:spring-boot-starter-parent` POM that provides Spring Boot dependency management and plugin defaults
- **Plugin_Management**: The `<pluginManagement>` block in the Root_POM that defines shared plugin configurations and versions for all Service_Modules

## Requirements

### Requirement 1: Root POM Structure

**User Story:** As a developer, I want a single root POM that acts as the parent for all services, so that dependency versions are managed in one place and consistency is enforced across the monorepo.

#### Acceptance Criteria

1. THE Root_POM SHALL declare `spring-boot-starter-parent` as its parent with an explicitly specified Spring Boot version property
2. THE Root_POM SHALL use `pom` as its packaging type
3. THE Root_POM SHALL declare all three Service_Modules (basket-async-order-processor, piba-account-service-opera, spending-entity-service) in a `<modules>` section
4. THE Root_POM SHALL define a `<dependencyManagement>` section containing entries with exact version numbers (no version ranges or SNAPSHOT versions) for every direct dependency declared across Service_Modules, including both compile-scoped and test-scoped dependencies
5. THE Root_POM SHALL import the Spring Cloud dependencies BOM in the DependencyManagement_Section using `<scope>import</scope>` and `<type>pom</type>`
6. THE Root_POM SHALL NOT use `spring-cloud-microservice-parent` as its parent or in any import
7. THE Root_POM SHALL declare a `<groupId>` of `uk.co.whitbread` and an `<artifactId>` that identifies it as the monorepo parent
8. WHEN a Service_Module is built as part of the monorepo, THE Service_Module SHALL reference the Root_POM as its `<parent>` using the Root_POM's groupId, artifactId, and version

### Requirement 2: Centralized Dependency Version Management

**User Story:** As a developer, I want all dependency versions defined in the root POM, so that version conflicts between services are eliminated and upgrades happen in one place.

#### Acceptance Criteria

1. THE DependencyManagement_Section SHALL include version-pinned entries for all Whitbread shared libraries (commons-logging, commons-entity-exceptions, commons-exceptions, commons-cdh-lib, common-auth0, worldline-ba-api-lib, coding-convention-rules), where each entry references a Maven property defined in the Root_POM `<properties>` section
2. THE DependencyManagement_Section SHALL include version-pinned entries for all third-party libraries that have explicit versions in current Service_POMs (commons-validator, springdoc-openapi-starter, jackson-databind-nullable, brave-instrumentation-kafka-clients, opencsv, mapstruct, apache-poi, micrometer-registry-prometheus, random-beans), where each entry references a Maven property defined in the Root_POM `<properties>` section
3. THE DependencyManagement_Section SHALL include version-pinned entries for transitive dependency overrides (commons-lang3, lombok, nimbus-jose-jwt, angus-activation, commons-text, spring-mock-mvc), where each entry references a Maven property defined in the Root_POM `<properties>` section
4. THE Root_POM SHALL define all version numbers as Maven properties in a `<properties>` section, using one property per distinct dependency version
5. WHEN a dependency version is managed in the DependencyManagement_Section, THEN THE Service_POMs SHALL omit the `<version>` element for that dependency in their `<dependencies>` section, remove any corresponding version property from their local `<properties>` section, and remove the entire local `<dependencyManagement>` section
6. WHEN a Service_POM previously contained a local `<dependencyManagement>` section solely for transitive dependency overrides, THEN THE Service_POM SHALL remove that entire local `<dependencyManagement>` section rather than retaining it with version properties removed

### Requirement 3: Service POM Restructure

**User Story:** As a developer, I want each service POM to inherit from the root POM and declare dependencies without versions, so that version management is fully delegated to the BOM.

#### Acceptance Criteria

1. WHEN a Service_POM declares a dependency that has a version defined in the Root_POM DependencyManagement_Section, THE Service_POM SHALL NOT specify a `<version>` element for that dependency
2. THE Service_POM SHALL declare the Root_POM as its `<parent>` using the Root_POM's groupId, artifactId, version, and a `<relativePath>` of `../pom.xml`
3. THE Service_POM SHALL retain its own `<artifactId>` and `<version>` elements unchanged from their current values
4. THE Service_POM SHALL retain all service-specific build plugin configurations (spring-boot-maven-plugin, springdoc-openapi-maven-plugin, jacoco-maven-plugin, checkstyle, pitest, openapi-generator, compiler plugin) including their `<configuration>` and `<executions>` elements
5. THE Service_POM SHALL retain all service-specific properties that are not dependency versions (application.port, sonar.coverage.exclusions)
6. THE Service_POM SHALL NOT contain a `<dependencyManagement>` section for version overrides that are already managed in the Root_POM
7. IF a Service_POM previously used `spring-cloud-microservice-parent` as its parent, THEN THE Service_POM SHALL replace it with the Root_POM parent reference
8. THE Service_POM SHALL remove all version-related properties from its `<properties>` section that correspond to dependencies managed in the Root_POM DependencyManagement_Section, even if those properties were previously considered service-specific

### Requirement 4: Shared Plugin Management

**User Story:** As a developer, I want common plugin configurations managed at the root level, so that plugin versions are consistent and service POMs are less verbose.

#### Acceptance Criteria

1. THE Root_POM SHALL define a `<pluginManagement>` section containing an explicit `<version>` element for each of the following plugins: maven-compiler-plugin, spring-boot-maven-plugin, jacoco-maven-plugin, maven-checkstyle-plugin, pitest-maven, springdoc-openapi-maven-plugin, openapi-generator-maven-plugin, spring-cloud-contract-maven-plugin
2. THE Root_POM SHALL define shared compiler settings in Plugin_Management including the Java source and target version set to 25, and annotation processor paths for Lombok, MapStruct (mapstruct-processor), and lombok-mapstruct-binding
3. WHEN a Service_Module uses a plugin defined in Plugin_Management, THE Service_POM SHALL NOT specify the plugin version, even when declaring custom `<configuration>` or `<executions>` elements
4. WHEN a Service_Module uses a plugin defined in Plugin_Management, THE Service_POM SHALL be permitted to declare service-specific `<configuration>` and `<executions>` elements that override or extend the Plugin_Management defaults without redeclaring the plugin version

### Requirement 5: Build Compatibility

**User Story:** As a developer, I want the restructured monorepo to build successfully from the root, so that CI/CD pipelines can build all services with a single command.

#### Acceptance Criteria

1. WHEN `mvn clean install` is executed from the repository root, THE Root_POM SHALL trigger the build of all Service_Modules in reactor dependency order and the command SHALL exit with BUILD SUCCESS status
2. WHEN `mvn clean install -pl basket-async-order-processor` is executed from the repository root, THE Root_POM SHALL build only the specified Service_Module and produce a JAR artifact in that module's target directory
3. THE restructured POMs SHALL preserve all existing dependency exclusions defined in Service_POMs such that the effective dependency tree of each Service_Module contains the same excluded artifacts as before restructuring
4. THE restructured POMs SHALL preserve all existing dependency scopes (test, runtime, provided) defined in Service_POMs such that the effective dependency tree of each Service_Module assigns the same scope to each artifact as before restructuring
5. WHEN `mvn clean install` completes successfully from the repository root, THE Root_POM SHALL have produced a packaged JAR artifact in each Service_Module's target directory (3 total: basket-async-order-processor, piba-account-service-opera, spending-entity-service)
6. THE restructured POMs SHALL preserve all existing build plugin configurations defined in Service_POMs such that each Service_Module executes the same set of Maven plugins (including checkstyle, jacoco, and spring-boot-maven-plugin) during the build lifecycle as before restructuring
