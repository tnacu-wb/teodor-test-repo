# Piba Registration Microservice
 This service exposes PIBA Registration endpoints. They include: 
- Registration Get Info by RegistrationCode


# How to run
 ## Run as a Spring Boot local application
 `mvn clean install spring-boot:run -Dspring.profiles.active=local`

  A profile is necessary since the service is not design to fall back to any default profile. `local` disables all integration tools like Eureka or Configservice making sure the project runs in isolation/

  ## Run as a Docker Image 
`[optional] $ mvn clean install docker:build`  

`$ docker run whitbreaddigital/piba-registration-service:version`

  Version corresponds to the [docker-hub](https://hub.docker.com/r/whitbreaddigital/piba-registration-service/) version, if it's been previously generated, no version is needed, it will automatically fallback to `latest` 

# Technologies 
- Spring cloud microservices parent v3.2.1-1
- Maven 3+  - Docker 0.12+ 
- Docker-compose v1+   
