# Whitbread Sample &micro;Service

## Requirements

All services should:

* Include _**.gitignore**_
* Include the following section in _src/main/resources/application.yml_

```
info:
     build:
       groupId: @project.groupId@
       artifact: @project.artifactId@
       description: @project.description@
       version: @project.version@
       git:
         branch: @git.branch@
         commit:
           id: @git.commit.id@
           time: @git.commit.time@
           message: @git.commit.message.short@
```

* Have it's own package, (e.g. uk.co.whitbread.reservation)
* Have a _**Jenkinsfile**_ (located at root of the project) this defined the CI/CD pipeline steps

## How to run

### Run as a Spring Boot local application

`mvn clean install spring-boot:run -Dspring.profiles.active=TBD`

A profile is necessary since the service is not design to fall back to any default profile. `local`
disables all integration tools like Eureka or Configservice making sure the project runs in
isolation/

## Hexagonal Architecture

`domain` - Models the domain of the API

`ports` - Defines any primary ports which act as input to the API and any secondary ports which are
external dependencies/resources

`adapters` - Implementations of ports

`config` - Package that contains any Spring Bean configuration

There is provided a 'sample', ready to use, workflow by starting the service.  
Added unit tests and contract tests compliant to this structure.

[Confluence: Table-Reservation-Service Description and project structure](https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/3420815378/Table-Reservation-Service+structure+description)

## Project Folder Structure

The arrangement below provides the structure within which the development effort will be
accomplished.

    .
    ├── docs                        # Documentation files
    ├── infrastructure              # Infrastucture related configuration
    │   ├── docker-local            # Defining and running the systems on which the application depends
    │   ├── docker-release          # Release version of the application image
    │   └── helm                    # Helm charts that describe a related set of Kubernetes resources
    ├── src                         # Application sources
    │   ├── main                    # Source files
    │   └── test                    # Test files
    ├── .gitignore                  # Files/folders to be ignored by Git
    ├── google-checkstyle.xml       # Checkstyle configuration
    ├── Jenkinsfile                 # Definition of a Jenkins Pipeline
    ├── pom.xml                     # Configuration details used by Maven to build the project
    └── README.md                   # Brief description of underlying GitHub project

## Technologies

- [Spring cloud microservices parent v4.0.5](https://github.com/whitbread-eos/spring-cloud-microservice-parent)
- Spring Boot 4.0.3
- Spring Framework 7.x
- Spring Security 7.x
- Java 25
- Jakarta EE 11 (jakarta.servlet 6.1)
- Tomcat 11.x
- Maven 3+
- Docker 17+
- Docker-compose v1+

## Updates

- **spring-cloud-microservice-parent upgraded to 4.0.5** (Spring Boot 4.0.3 / Java 25)
    * Upgraded Java compiler source/target from 17 → 25
    * Removed all hardcoded Spring Boot 2.x / 3.x dependency version pins — now fully managed by parent BOM
    * Replaced `javax.validation:validation-api` (removed namespace) with Jakarta Validation via `spring-boot-starter-validation`
    * Replaced `springdoc-openapi-ui:1.6.5` (Spring Boot 2 only) with `springdoc-openapi-starter-webmvc-ui:2.8.3`
    * Fixed `GroupedOpenApi` import: `org.springdoc.core` → `org.springdoc.core.models`
    * Removed `@EnableDiscoveryClient` — auto-configured by Spring Cloud
    * Updated `common-auth0` from `3.0.1` → `8.0.5` (Spring Security 7 compatible)
    * Removed `common-auth` library — confirmed unused after build verification
    * Upgraded JaCoCo from `0.8.7` → `0.8.12` (required for Java 25 bytecode support)
    * Updated Tomcat embed from `10.1.x` → `11.0.2`
    * Updated Jakarta Servlet API from `4.0.4` → `6.1.0` (Jakarta EE 11)
    * Removed Spring Framework core version overrides (`spring-core`, `spring-aop`, `spring-jcl`) — managed by BOM
    * Removed Spring Security version overrides — managed by BOM
    * Migrated `RestTemplate` → `RestClient` (Spring Boot 4 standard) in all HTTP clients:
        - `ZonalConfigurationProperties` now exposes `@Bean("zonalRestClient") RestClient`
        - `AemConfigurationProperties` now exposes `@Bean("aemRestClient") RestClient`
        - `TableReservationOutPortImpl` and `AemOutPortImpl` fully migrated to `RestClient` fluent API
    * Fixed `SecurityConfig` — replaced deprecated non-lambda `authorizeHttpRequests()` with lambda DSL (required by Spring Security 7)
    * Removed `spring.sleuth` configuration (Spring Cloud Sleuth removed in 2022+); replaced with `management.tracing` via Micrometer
    * Replaced `@MockBean` with `@MockitoBean` in all test classes (`org.springframework.test.context.bean.override.mockito`)
    * Added `ApplicationStartupTest` — verifies context load, health/info actuator endpoints, and OpenAPI docs on every build
