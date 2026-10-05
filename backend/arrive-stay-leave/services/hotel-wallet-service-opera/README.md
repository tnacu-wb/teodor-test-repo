# Hotel Wallet Service Opera
This service allows clients to generate post booking wallet for iOS phones

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

* Have it's own package, (e.g. uk.co.whitbread.wallet)

## How to run

### Run as a Spring Boot local application

`mvn clean install spring-boot:run -Dspring.profiles.active=local`

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
- Spring Boot 4.0.3 (Spring Framework 7.x, Spring Security 7.x)
- Java 25
- Maven 3+
- Docker 17+
- Docker-compose v1+

## Updates

- spring-cloud-microservice-parent updated to 4.0.5
    * Upgraded from Spring Boot 3.4.1 to Spring Boot 4.0.3
    * Java version updated to 25
    * Spring Boot starters renamed to 4.0 conventions:
        - `spring-boot-starter-web-services` → `spring-boot-starter-webservices`
        - `spring-boot-starter-oauth2-client` → `spring-boot-starter-security-oauth2-client`
        - `spring-boot-starter-oauth2-resource-server` → `spring-boot-starter-security-oauth2-resource-server`
    * Thymeleaf upgraded from `thymeleaf-spring5` to `thymeleaf-spring6`
    * Removed pinned `spring-security-web` version — now managed by Spring Boot 4.0 (Security 7.x)
    * Replaced deprecated `ExchangeStrategies` with `WebClient.Builder.codecs()`
    * Added `spring-boot-jackson2` compatibility module for Jackson 2 support
    * Removed `mockito-inline` (bundled in Mockito 5+)
    * Upgraded Lombok to 1.18.38 for Java 25 compatibility
    * Upgraded springdoc-openapi to 2.8.6
    * Upgraded openapi-generator-maven-plugin to 7.12.0
    * Upgraded maven-surefire-plugin to 3.5.2
    * Upgraded checkstyle to 10.21.4
    * Removed `slf4j-simple` dependency (conflicts with Boot-managed logging)
    * Moved `spring.application.name` from `bootstrap.yml` to `application.yml`
- added ArchUnit tests - rules are defined in the coding-convention-rules lib

