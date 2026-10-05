# Whitbread Promo Service 🎟️

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

- [Spring cloud microservices parent v2.0.0](https://github.com/whitbread-eos/spring-cloud-microservice-parent/releases/tag/v2.0.0)
- Maven 3+
- Docker 17+
- Docker-compose v1+

## Updates

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

**How to run**
    Aws s3 docker local setup
*     echo $(aws s3 mb s3://business-booker-tm-mi-report-dev) - to create s3 bucket
*     echo $(aws s3 ls) - to see List S3 Buckets
*     docker-compose up -d - Run as a Docker Image
*     set application.yml settings to the following:
    
    aws:
    s3:
    endpoint: http://localhost.localstack.cloud:4566
    maxRetry: 2
    bucket: promotion-code-dev
    name: 
    region: eu-west-1
    accessKey: localstack
    secretAccessKey: localstack
    
    
