# Hotel Account MS
This service allows clients to access customer endpoints such as: 
 - Get customers' stays
 - Get customer's profile
 - Update customer
 - Login/Register new customer
 - Forgotten Password
 - Reset Password
 
# Notes

Due to security concerns regarding exposing whether a customers email address is a valid Premier Inn account or not, 
the forgotten password endpoint will always return a 200 response with `success` set to `true`. This ensures no indication is given
regarding the existence of a given email address. 

# Update memorable word flow

![Alt text](/docs/diagrams/images/update_memorable_word.png?raw=true "Update memorable word flow")

# Service dependencies
This service doesn't have any internal dependencies

# How to run
##Environment variables setup
Before running the application locally please get the values for the variables below from https://manage.auth0.com/dashboard/eu/wbmigrationtest/applications/gBVcZzfXChbowQR1mfH8csbUwya9FzOB/settings:
- AUTH0_CLIENT_ID
- AUTH0_CLIENT_SECRET

## Run as a Spring Boot local application
`mvn clean install spring-boot:run -Dspring.profiles.active=local`

A profile is necessary since the service is not design to fall back to any default profile. `local` disables all integration tools like Eureka or Configservice making sure the project runs in isolation/

## Run as a Docker Image
`[optional] $ mvn clean install docker:build`

`$ docker run whitbreaddigital/hotel-account-service:version`

Version corresponds to the [docker-hub](https://hub.docker.com/r/whitbreaddigital/hotel-account-service/) version, if it's been previously generated, no version is needed, it will automatically fallback to `latest`

### Local Unleash for development

You can start up Unleash by running docker-compose up or podman-compose up

# Technologies
 - Spring cloud microservices parent v3.2.1-1
 - Maven 3+
 - Docker-compose v3+
 - Java 17

# Excluding Integration Tests
  Simply run 'mvn test' instead of 'mvn verify'