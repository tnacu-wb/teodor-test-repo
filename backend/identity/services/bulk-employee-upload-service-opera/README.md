# Bulk Employee Upload Service
This service exposes bulk employee related operations. They include:
- Get employees list
- Bulk add employees
- Bulk update employees

# Requirements

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

* Have it's own package, (e.g. uk.co.whitbread.employee.bulk)
* Have a _**Jenkinsfile**_ (located at root of the project) this defined the CI/CD pipeline steps


# How to run
##Run as a Spring Boot local application
 `mvn clean install spring-boot:run -Dspring.profiles.active=local`

A profile is necessary since the service is not design to fall back to any default profile. `local` disables all integration tools like Eureka or Configservice making sure the project runs in isolation/

`$ docker run whitbreaddigital/bulk-employee-upload-service:version`

Version corresponds to the [docker-hub](https://hub.docker.com/r/whitbreaddigital/bulk-employee-upload-service/) version, if it's been previously generated, no version is needed, it will automatically fallback to `latest`

# Technologies 
- Spring cloud microservices parent v3.0.1
- Maven 3+
- Docker 17+

# Updates