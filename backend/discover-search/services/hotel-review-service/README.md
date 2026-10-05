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

* Have it's own package, (e.g. uk.co.whitbread.review)
* Have a _**Jenkinsfile**_ (located at root of the project) this defined the CI/CD pipeline steps

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

- [Spring cloud microservices parent v4.0.5](https://github.com/whitbread-eos/spring-cloud-microservice-parent/releases/tag/v4.0.5) (includes Spring Boot 4.0.3)
- Java 25
- Maven 3+
- Docker 17+
- Docker-compose v1+

## Updates

- spring-cloud-microservice-parent updated to 4.0.5 (Spring Boot 4.0.3)
    * upgraded to Java 25
    * upgraded to Jackson 3.1.1 with new polymorphic type validator API
    * Jakarta EE fully aligned
- spring-cloud-microservice-parent updated to 2.0.0
    * upgraded from jUnit4 to jUnit5
        - Moved from Given-When-Then way to Arrange-Act-Assert way of writing tests.
    * migrated springfox to openapi 3
        - From version 1.1.25 of springdoc-openapi-ui library ```HttpServletResponse```
          and ```HttpServletRequest``` are
          automatically ignored.
- removed unused dependencies
- upgraded to the latest commons-exceptions library version: 1.3.3
- added ArchUnit tests - rules are defined in the coding-convention-rules lib 
