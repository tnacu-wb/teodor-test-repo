# Whitbread OHIP Adapter &micro;Service

The OHIP Adapter Micro-service - this component embeds all the specific logic that is required by
the Oracle Opera property management system. It is responsible for translating the Whitbread data
models onto the Opera data model, and vice versa, plus the handling of the interface integration
with the Opera OHIP APIs. When a new PMS solution is going to be integrated into the Whitbread
booking platform, a similar, new Adapter micro-service will be created to take care of the specifics
of that integration.

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

* Have its own package, (e.g. uk.co.whitbread)

## How to run
### Local Redis

`<docker-compose/podman-compose> -f infrastructure/docker-local/docker-compose.yml up -d`

**Note:** disable SSL via application.yml flag `spring.redis.ssl`

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

- [Spring Cloud Microservices Parent v4.0.5](https://github.com/whitbread-eos/spring-cloud-microservice-parent) (Spring Boot 4.0.3)
- Java 25
- Maven 3+
- Docker 17+
- Docker-compose v1+

## Updates

- TO DO
