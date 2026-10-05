# Payment Methods Entity Service

Spring Boot-based microservice for payment methods at Whitbread.

## Requirements

All services should:
- Include .gitignore.
- Include the following section in src/main/resources/application.yml:

```yaml
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

- Use its own package namespace (for example: uk.co.whitbread.payments).

## How To Run

### Run locally

Set required environment variables:

```bash
CONTENT_HOST=gateway.poc.opera.whitbread.digital
RESERVATION_HOST=gateway.poc.opera.whitbread.digital
```

Run the service with the local profile:

```bash
./mvnw clean install spring-boot:run -Dspring.profiles.active=local
```

The service requires an explicit Spring profile. The local profile disables integration components such as Eureka and Config Server so the service can run in isolation.

### Local Unleash for development

Start Unleash with docker-compose up or podman-compose up.

## Hexagonal Architecture

- domain: models the domain of the API.
- ports: defines primary input ports and secondary external dependency ports.
- adapters: implementations of ports.
- config: Spring bean configuration.

The project includes a ready-to-use workflow when starting the service, with unit and contract tests aligned to this structure.

[Confluence: Sample-Service Description and project structure](https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/3420815378/Sample-Service+structure+description)

## Project Folder Structure

```
.
|-- docs                        # Documentation files
|-- src                         # Application sources
|   |-- main                    # Source files
|   `-- test                    # Test files
|-- .gitignore                  # Files/folders to be ignored by Git
|-- google-checkstyle.xml       # Checkstyle configuration
|-- pom.xml                     # Maven build configuration
`-- README.md                   # Project overview and usage
```

## Technology Stack (Spring Boot 4)

- [spring-cloud-microservice-parent 4.0.6](https://github.com/whitbread-eos/spring-cloud-microservice-parent)
- Spring Boot 4 generation stack (via parent BOM)
- Spring Security 6.5.x
- Spring Cloud Contract 5.0.2
- Tomcat 11.0.23
- Netty 4.2.15.Final
- Micrometer 1.16.6
- OpenAPI via springdoc-openapi-starter-webmvc 2.0.4
- Maven Wrapper 3.9.14

## Spring Boot 4 Migration Highlights

- Upgraded parent from 3.4.1 to 4.0.6.
- Upgraded Maven Wrapper from 3.8.6 to 3.9.14.
- Upgraded commons-entity-exceptions to 3.0.0 and commons-logging to 4.0.4.
- Removed explicit Spring Security and Logback overrides and aligned with parent/BOM-managed versions.
- Updated Redis auto-configuration reference to DataRedisAutoConfiguration.
- Updated WebClient codec configuration to use JacksonJsonEncoder and JacksonJsonDecoder with JsonMapper.
- Updated cache serialization helpers and tests to JsonMapper and JacksonException.
