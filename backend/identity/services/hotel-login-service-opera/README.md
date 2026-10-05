# hotel-login-microservice-opera
-This service allows clients to carry out the following customer account operations:
-- login
-- logout
-- generate token
-- initiate bart session (Auth0)

# Service dependencies
This service doesn't have any internal dependencies

# How to run
##Environment variables setup
Before running the application locally please get the values for the variables below from https://manage.auth0.com/dashboard/eu/wbmigrationtest/applications/gBVcZzfXChbowQR1mfH8csbUwya9FzOB/settings:
- AUTH0_CLIENT_ID
- AUTH0_CLIENT_SECRET

Also, AUTH0_ENCRYPTION_KEY will be needed for the lazy migration functionality to work as expected.

## Run as a Spring Boot local application
`mvn clean install spring-boot:run -Dspring.profiles.active=local`

A profile is necessary since the service is not design to fall back to any default profile. `local` disables all integration tools like Eureka or Configservice making sure the project runs in isolation/

## Run as a Docker Image
`[optional] $ mvn clean install docker:build`

`$ docker run whitbreaddigital/hotel-login-service-opera:version`

Version corresponds to the [docker-hub](https://hub.docker.com/r/whitbreaddigital/hotel-login-service-opera/) version, if it's been previously generated, no version is needed, it will automatically fallback to `latest`

# Technologies
 - [Spring cloud microservices parent v3.0.1]
 - Maven 3+
 - Docker 0.12+
 - Docker-compose v1+
 - Java 17