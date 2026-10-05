# Whitbread Sample &micro;Service

The Opera Token Microservice is responsible for calling the Opera API to generate tokens and will be consumed 
by the Digital services.
Communication to Opera will be secured in the same standard way as other services communicate to the system 
and will require the same secrets to be consumed by the service, containing the Opera username, password, 
application ID and host name.
The service exposes a REST API endpoint to allow clients to fetch the token without needing to call Opera directly. 
This ensures that the number of requests made to Opera is minimized and that tokens are only generated when necessary.

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

* Have it's own package, (e.g. uk.co.whitbread.sample)

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

[Confluence: project structure](https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/3420815378/Sample-Service+structure+description)

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

- Spring Boot 4, managed through `spring-cloud-microservice-parent` v4.0.6
- Spring Cloud Contract for producer contract tests
- Springdoc OpenAPI 3 for API documentation generation
- Maven 3.9.14, managed through the Maven wrapper
- Docker 17+
- Docker-compose v1+

## Updates

- Migrated to `spring-cloud-microservice-parent` v4.0.6 for Spring Boot 4 support.
- Updated the Maven wrapper to Maven 3.9.14.
- Updated Springdoc OpenAPI dependencies to 3.0.2 for Spring Framework 7 compatibility.
- Updated Spring Security OAuth token client integration to use the Spring Security 7 client credentials token response client APIs.
- Added Jackson 2 `ObjectMapper` compatibility configuration for shared libraries that still require Jackson 2 while Spring Boot 4 uses Jackson 3 for HTTP message conversion.
- Updated contract test compatibility with Rest Assured 5.5.7 and Spring Cloud Contract 5.x generated tests.
- Updated dependency versions for Boot 4 compatibility, including `commons-entity-exceptions`, `commons-validator`, `commons-logging`, `nimbus-jose-jwt`, Micrometer, Tomcat, Netty, Lombok, and coding convention rules.
- Added ArchUnit tests - rules are defined in the coding-convention-rules lib.

