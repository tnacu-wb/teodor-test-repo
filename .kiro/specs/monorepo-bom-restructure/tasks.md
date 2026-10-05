# Implementation Plan: Monorepo BOM Restructure

## Overview

Transform the digital-backend-monorepo from three independent services each parented by `spring-cloud-microservice-parent` into a proper Maven multi-module project. Create a root POM with centralized dependency and plugin management, then transform each service POM to inherit from the root and remove local version declarations.

## Tasks

- [x] 1. Create root POM with centralized dependency and plugin management
  - [x] 1.1 Create root `pom.xml` at repository root
    - Declare `spring-boot-starter-parent` as parent (use the Spring Boot version currently resolved by `spring-cloud-microservice-parent:4.0.5`)
    - Set `<packaging>pom</packaging>`
    - Set `<groupId>uk.co.whitbread</groupId>` and `<artifactId>digital-backend-monorepo</artifactId>`
    - Declare `<modules>` section listing: basket-async-order-processor, piba-account-service-opera, spending-entity-service
    - Define all version properties in `<properties>` section: java.version=25, spring-cloud.version=2024.0.1, all Whitbread shared library versions (wb.commons-logging.version=4.0.4, wb.commons-entity-exceptions.version=3.0.0, wb.commons-exceptions.version=3.0.1, wb.commons-cdh.version=17.0.1, wb.common-auth0.version=8.0.4, wb.worldline-ba-api-lib.version=3.0.0, wb.coding-convention-rules.version=2.0.1), all third-party versions (commons-validator.version=1.7, springdoc-openapi-starter.version=2.7.0, jackson-databind-nullable.version=0.2.6, brave-kafka-clients.version=5.14.1, opencsv.version=5.9, mapstruct.version=1.6.3, poi.version=5.4.1, micrometer-registry-prometheus.version=1.16.3, random-beans.version=3.6.0), transitive overrides (lombok.version=1.18.44, nimbus-jose-jwt.version=10.4, angus-activation.version=2.0.3, commons-lang3.version=3.20.0, commons-text.version=1.13.0, spring-mock-mvc.version=6.0.0, lombok-mapstruct-binding.version=0.2.0), and plugin versions (maven-compiler-plugin.version=3.13.0, maven-checkstyle-plugin.version=3.1.2, checkstyle.version=9.2, jacoco-maven-plugin.version=0.8.12, pitest-maven.version=1.16.1, pitest-junit5-plugin.version=1.2.1, spring-cloud-contract-maven-plugin.version=4.2.1, springdoc-openapi-maven-plugin.version=1.4, openapi-generator.version=6.4.0)
    - _Requirements: 1.1, 1.2, 1.3, 1.7, 2.1, 2.2, 2.3, 2.4_

  - [x] 1.2 Add `<dependencyManagement>` section to root POM
    - Import Spring Cloud BOM: `org.springframework.cloud:spring-cloud-dependencies:${spring-cloud.version}` with `<scope>import</scope>` and `<type>pom</type>`
    - Add entries for all Whitbread shared libraries referencing their property versions (commons-logging, commons-entity-exceptions, commons-exceptions, commons-cdh-lib, common-auth0, worldline-ba-api-lib, coding-convention-rules)
    - Add entries for all third-party libraries with explicit versions (commons-validator, springdoc-openapi-starter-webmvc-ui, jackson-databind-nullable, brave-instrumentation-kafka-clients, opencsv, mapstruct, poi, micrometer-registry-prometheus, random-beans, springdoc-openapi-starter-webmvc-api, springdoc-openapi-starter-common)
    - Add entries for transitive dependency overrides (lombok, nimbus-jose-jwt, angus-activation, commons-lang3, commons-text, spring-mock-mvc)
    - Use highest version for conflicts: commons-entity-exceptions=3.0.0, common-auth0=8.0.4, coding-convention-rules=2.0.1
    - _Requirements: 1.4, 1.5, 1.6, 2.1, 2.2, 2.3_

  - [x] 1.3 Add `<pluginManagement>` section to root POM
    - Define maven-compiler-plugin with version, source=25, target=25, and annotation processor paths for Lombok, MapStruct (mapstruct-processor), and lombok-mapstruct-binding
    - Define spring-boot-maven-plugin (version inherited from spring-boot-starter-parent)
    - Define jacoco-maven-plugin with version
    - Define maven-checkstyle-plugin with version and checkstyle dependency version
    - Define pitest-maven with version and pitest-junit5-plugin dependency
    - Define springdoc-openapi-maven-plugin with version
    - Define openapi-generator-maven-plugin with version
    - Define spring-cloud-contract-maven-plugin with version
    - _Requirements: 4.1, 4.2_

- [x] 2. Transform basket-async-order-processor service POM
  - [x] 2.1 Update parent and remove version management from basket-async-order-processor/pom.xml
    - Replace `<parent>` from `spring-cloud-microservice-parent:4.0.3` to `digital-backend-monorepo` with `<relativePath>../pom.xml</relativePath>`
    - Remove all version properties from `<properties>` (wb.coding-convention-rules.version, commons-entity-exceptions.version, springdoc-openapi-maven-plugin.version, springdoc-openapi-starter.version, commons-validator.version, openapi-generator-version, jackson.databind.version, wb.commons-logging.version, brave-kafka-clients.version)
    - Retain service-specific properties: application.port, sonar.coverage.exclusions
    - Remove `<version>` elements from all dependencies that are managed in root POM (commons-validator, commons-entity-exceptions, commons-logging, jackson-databind-nullable, springdoc-openapi-starter-webmvc-ui, springdoc-openapi-starter-webmvc-api, springdoc-openapi-starter-common, coding-convention-rules, brave-instrumentation-kafka-clients)
    - Remove `<version>` elements from all plugins managed in root pluginManagement (maven-checkstyle-plugin, pitest-maven, springdoc-openapi-maven-plugin, openapi-generator-maven-plugin)
    - Retain all plugin `<configuration>` and `<executions>` elements
    - Retain service-specific maven-compiler-plugin configuration only if it differs from root (remove if identical to root pluginManagement)
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.7, 3.8, 4.3_

- [x] 3. Transform piba-account-service-opera service POM
  - [x] 3.1 Update parent and remove version management from piba-account-service-opera/pom.xml
    - Replace `<parent>` from `spring-cloud-microservice-parent:4.0.5` to `digital-backend-monorepo` with `<relativePath>../pom.xml</relativePath>`
    - Remove all version properties from `<properties>` (commons-cdh.version, commons-validator.version, worldline-ba-api-lib.version, common-auth0.version, org.mapstruct.version, lombok-mapstruct-binding.version, wb-commons-logging.version, opencsv.version, random-beans.version, poi.version, lombok.version, nimbus-jose-jwt.version, commons-exceptions.version, angus-activation.version, commons-lang3.version, spring-mock-mvc.version, commons-text.version, pitest-maven.version, pitest-junit5-plugin.version)
    - Retain service-specific properties: sonar.coverage.exclusions
    - Remove `<version>` elements from all dependencies managed in root POM (commons-validator, commons-logging, commons-cdh-lib, worldline-ba-api-lib, common-auth0, opencsv, mapstruct, poi, random-beans)
    - Remove the entire local `<dependencyManagement>` section (transitive overrides now in root)
    - Remove `<version>` elements from all plugins managed in root pluginManagement (spring-cloud-contract-maven-plugin, pitest-maven, maven-checkstyle-plugin)
    - Remove maven-compiler-plugin `<configuration>` if identical to root pluginManagement (annotation processor paths now centralized)
    - Retain all service-specific plugin `<configuration>` and `<executions>` (jacoco excludes, pitest excludes/mutators, checkstyle config, spring-cloud-contract baseClassForTests)
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 4.3_

- [x] 4. Transform spending-entity-service service POM
  - [x] 4.1 Update parent and remove version management from spending-entity-service/pom.xml
    - Replace `<parent>` from `spring-cloud-microservice-parent:4.0.5` to `digital-backend-monorepo` with `<relativePath>../pom.xml</relativePath>`
    - Remove all version properties from `<properties>` (coding-convention-rules.version, commons-exceptions.version, commons-entity-exceptions.version, commons-validator.version, wb-commons-logging.version, common-auth0.version, commons-cdh.version, springdoc-openapi-maven-plugin.version)
    - Retain service-specific properties: application.port, sonar.coverage.exclusions
    - Remove `<version>` elements from all dependencies managed in root POM (commons-validator, commons-exceptions, commons-entity-exceptions, commons-cdh-lib, common-auth0, commons-logging, coding-convention-rules, micrometer-registry-prometheus)
    - Remove the entire local `<dependencyManagement>` section (commons-lang3 and lombok overrides now in root)
    - Remove `<version>` elements from all plugins managed in root pluginManagement (springdoc-openapi-maven-plugin, pitest-maven, maven-checkstyle-plugin)
    - Remove maven-compiler-plugin `<configuration>` if identical to root pluginManagement (annotation processor paths now centralized)
    - Retain all service-specific plugin `<configuration>` and `<executions>` (jacoco excludes/executions, pitest excludes/mutators, checkstyle config, spring-boot-maven-plugin start/stop)
    - Preserve all dependency exclusions (commons-cdh-lib exclusions, common-auth0 exclusions)
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.8, 4.3, 5.3_

- [x] 5. Checkpoint - Validate structural correctness
  - Ensure all tests pass, ask the user if questions arise.
  - Verify no service POM contains `<dependencyManagement>` sections
  - Verify no service POM references `spring-cloud-microservice-parent`
  - Verify no service POM contains version properties for managed dependencies
  - Verify all three modules are declared in root POM

- [x] 6. Build validation and dependency tree verification
  - [x] 6.1 Run full reactor build from repository root
    - Execute `mvn clean install` from repository root and verify BUILD SUCCESS
    - Verify each service produces a JAR in its `target/` directory
    - _Requirements: 5.1, 5.5_

  - [x] 6.2 Verify individual module builds
    - Execute `mvn clean install -pl basket-async-order-processor` and verify success
    - Execute `mvn clean install -pl piba-account-service-opera` and verify success
    - Execute `mvn clean install -pl spending-entity-service` and verify success
    - _Requirements: 5.2_

  - [x] 6.3 Verify dependency tree equivalence
    - Run `mvn dependency:tree` for each service and compare effective dependencies against pre-restructure baseline
    - Verify all exclusions are preserved (commons-cdh-lib exclusions in spending-entity-service, common-auth0 exclusions in spending-entity-service)
    - Verify all dependency scopes are preserved (test, runtime, provided)
    - _Requirements: 5.3, 5.4_

  - [x] 6.4 Verify plugin execution
    - Confirm checkstyle runs during validate phase for each service
    - Confirm jacoco produces coverage reports
    - Confirm spring-boot-maven-plugin produces executable JARs
    - Run `mvn help:effective-pom` for each service and verify compiler source/target=25, annotation processors include Lombok and MapStruct
    - _Requirements: 5.6, 4.2_

- [x] 7. Final checkpoint - Ensure all builds pass
  - Ensure all tests pass, ask the user if questions arise.

## Notes

- Each task references specific requirements for traceability
- Checkpoints ensure incremental validation
- Version conflict resolution uses highest version: commons-entity-exceptions=3.0.0, common-auth0=8.0.4, coding-convention-rules=2.0.1
- The basket-async-order-processor upgrade from commons-entity-exceptions 2.0.4 to 3.0.0 may require code changes if there are breaking API differences
- Dependency exclusions in spending-entity-service (commons-cdh-lib, common-auth0) must be preserved exactly
- Service-specific plugin configurations (jacoco excludes, pitest mutators, checkstyle rules, openapi-generator config) are retained in service POMs
- The root POM maven-compiler-plugin in pluginManagement centralizes annotation processor paths — services should remove their local compiler config if identical

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1"] },
    { "id": 1, "tasks": ["1.2"] },
    { "id": 2, "tasks": ["1.3"] },
    { "id": 3, "tasks": ["2.1", "3.1", "4.1"] },
    { "id": 4, "tasks": ["6.1"] },
    { "id": 5, "tasks": ["6.2", "6.3"] },
    { "id": 6, "tasks": ["6.4"] }
  ]
}
```
