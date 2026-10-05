# Piba Account Microservice
This service exposes PIBA Account endpoints. They include:
- Customer view current balance
- Customer view transactions


# How to run
## Run as a Spring Boot local application
`mvn clean install spring-boot:run -Dspring.profiles.active=local`

A profile is necessary since the service is not design to fall back to any default profile. `local` disables all integration tools like Eureka or Configservice making sure the project runs in isolation/

## Run as a Docker Image
`[optional] $ mvn clean install docker:build`

`$ docker run whitbreaddigital/piba-account-service:version`

# Technologies
- Spring cloud microservices parent v3.3.1
- Maven 3+ 
- Docker 2+
- Docker-compose v3+
- Java 17