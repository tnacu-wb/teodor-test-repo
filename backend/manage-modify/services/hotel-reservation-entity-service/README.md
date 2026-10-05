# Whitbread Hotel Reservation Entity &micro;Service

## Requirements

/Trigger
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

## How to run

### Run as a Spring Boot local application

`mvn clean install spring-boot:run -Dspring.profiles.active=local`

A profile is necessary since the service is not design to fall back to any default profile. `local`
disables all integration tools like Eureka or Configservice making sure the project runs in
isolation/

### Local Unleash for development

You can start up Unleash by running docker-compose up or podman-compose up

## Hexagonal Architecture

`domain` - Models the domain of the API

`ports` - Defines any primary ports which act as input to the API and any secondary ports which are
external dependencies/resources

`adapters` - Implementations of ports

`config` - Package that contains any Spring Bean configuration

There is provided a 'sample', ready to use, workflow by starting the service.  
Added unit tests and contract tests compliant to this structure.

[Confluence: Sample-Service Description and project structure](https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/3420815378/Sample-Service+structure+description)

## Project Folder Structure

The arrangement below provides the structure within which the development effort will be
accomplished.

    .
    ├── docs                        # Documentation files
    ├── infrastructure              # Infrastucture related configuration
    │   └── docker-local            # Defining and running the systems on which the application depends
    ├── src                         # Application sources
    │   ├── main                    # Source files
    │   └── test                    # Test files
    ├── .gitignore                  # Files/folders to be ignored by Git
    ├── google-checkstyle.xml       # Checkstyle configuration
    ├── pom.xml                     # Configuration details used by Maven to build the project
    └── README.md                   # Brief description of underlying GitHub project

## Technologies

- [Spring cloud microservices parent v4.0.5](https://github.com/whitbread-eos/spring-cloud-microservice-parent)
- Spring Boot 4.0.3 / Spring Framework 7.0 / Spring Security 7.0
- Java 25
- Maven 3.9+
- Docker 17+
- Docker-compose v1+

## Updates

- spring-cloud-microservice-parent updated to 4.0.5
    * Spring Boot 3.4.1 → 4.0.3 (Spring Framework 7.0, Spring Security 7.0)
    * Java 17 → 25
    * `CachingConfigurerSupport` → `CachingConfigurer` (RedisConfig)
    * `@EnableGlobalMethodSecurity` → `@EnableMethodSecurity` (SecurityConfig)
    * Security lambda DSL for `authorizeHttpRequests()` (SecurityConfig)
    * `ExchangeStrategies` → `WebClient.Builder.codecs()` (WebClientConfig)
    * `@EnableDiscoveryClient` removed (auto-configured in Spring Cloud 2025.x)
    * `bootstrap.yml` removed — `spring.application.name` moved to `application.yml`
    * `spring-cloud-starter-bootstrap` dependency removed
    * Removed conflicting version overrides (Spring Security, Netty, Tomcat, Jackson) — now managed by parent BOM
    * Updated checkstyle 9.2 → 10.21.4 for Java 25 support
    * Updated springdoc-openapi 2.7.0 → 2.8.8
    * Updated lombok-mapstruct-binding 0.1.0 → 0.2.0
    * `spring-boot-properties-migrator` added temporarily for property rename detection
    * QrCodeUtils refactored — removed redundant `@Autowired` constructor
- spring-cloud-microservice-parent updated to 2.0.0
    * upgraded from jUnit4 to jUnit5
        - Moved from Given-When-Then way to Arrange-Act-Assert way of writing tests.
    * migrated springfox to openapi 3
        - From version 1.1.25 of springdoc-openapi-ui library ```HttpServletResponse```
          and ```HttpServletRequest``` are
          automatically ignored.
- removed unused dependencies
- upgraded to the latest commons-exceptions library version: 1.3.3

## Diagrams

![Create reservation flow](./docs/diagrams/images/create-reservation.png?raw=true "Create reservation flow")\
![Create reservation service communication](./docs/diagrams/images/create-reservation-service-communication.png?raw=true "Create reservation service communication")\
![Get reservations by basket reference flow](./docs/diagrams/images/get-reservations-by-basket-reference.png?raw=true "Get reservations by basket reference flow")\
![Get reservations by basket reference service communication](./docs/diagrams/images/get-reservations-by-basket-reference-service-communication.png?raw=true "Get reservations by basket reference service communication")
