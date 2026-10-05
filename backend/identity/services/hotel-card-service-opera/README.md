# Hotel Card Service Opera
This service exposes Company and Customer payment card related operations. They include:
 - Getting customer card details
 - Creating customer cards
 - Updating customer cards
 - Deleting customer cards

 - Getting company card details
 - Creating company cards
 - Updating company cards
 - Deleting company cards

# How to run
## Run as a Spring Boot local application
`mvn clean install spring-boot:run -Dspring.profiles.active=local`

A profile is necessary since the service is not design to fall back to any default profile. `local` disables all integration tools like Eureka or Configservice making sure the project runs in isolation/

## Run as a Docker Image
`[optional] $ mvn clean install docker:build`

`$ docker run whitbreaddigital/business-card:version`

### Local Unleash for development

You can start up Unleash by running docker-compose up or podman-compose up

Version corresponds to the [docker-hub](https://hub.docker.com/repository/docker/whitbreaddigital/hotel-card-service) version, if it's been previously generated, no version is needed, it will automatically fallback to `latest`
# Technologies
- [Spring cloud microservices parent v3.0.1]
- Maven 3+
- Docker 0.12+
- Docker-compose v1+
- Java 17