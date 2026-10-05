# Whitbread Basket Async Order Processor &micro;Service

The existing limitations of the Opera system, plus the requirements for interoperability of the
Whitbread booking
platform advocate for the introduction of a basket/cart feature to the existing solution. The basket
would allow a user
to select multiple offerings, of different types (i.e. stay reservations, concert tickets. WI-FI
packages etc.) and pay
for all of them once. All purchased items would be identifiable by a single reference that is
managed and owned by the
Whitbread systems.

The basket feature will be modeled as a distinct micro-service that will interact mainly with the
Reservation Entity
micro-service to manage any interactions performed by the end-users against a stay definition. Based
on the user’s
selection, the Reservation entity will be responsible for creating the necessary number of cart
items in the basket (
i.e. one Opera reservation for each room). The same Reservation micro-service must provide all the
details that are
relevant for the items placed in the basket.

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

* Have it's own package, (e.g. uk.co.whitbread.basket)

## How to run

### Run as a Spring Boot local application

`mvn clean install spring-boot:run -Dspring.profiles.active=local`

A profile is necessary since the service is not design to fall back to any default profile. `local`
disables all
integration tools like Eureka or Configservice making sure the project runs in isolation/

## Hexagonal Architecture

`domain` - Models the domain of the API

`ports` - Defines any primary ports which act as input to the API and any secondary ports which are
external
dependencies/resources

`adapters` - Implementations of ports

`config` - Package that contains any Spring Bean configuration

## Project Folder Structure

The arrangement below provides the structure within which the development effort will be
accomplished.

    .
    ├── docs                        # Documentation files
    ├── src                         # Application sources
    │   ├── main                    # Source files
    │   └── test                    # Test files
    ├── .gitignore                  # Files/folders to be ignored by Git
    ├── google-checkstyle.xml       # Checkstyle configuration
    ├── pom.xml                     # Configuration details used by Maven to build the project
    └── README.md                   # Brief description of underlying GitHub project

## Technologies

- [Spring cloud microservices parent v2.0.0](https://github.com/whitbread-eos/spring-cloud-microservice-parent/releases/tag/v2.0.0)
- Maven 3+
- Docker 17+
- Docker-compose v1+

## Updates
