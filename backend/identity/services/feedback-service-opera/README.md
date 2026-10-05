# Feedback Microservice

This service allows clients to submit customer feedback to Dynamics365.

# Technologies
- Spring cloud parent 4.0.5
- Adal4j 1.6.7
- Java 25
- Docker
- Lombok

# Running the Application
## Run as a Spring Boot local application
`mvn clean install spring-boot:run -Dspring.profiles.active=local`

A profile is necessary since the service is not designed to fall back to any default profile. `local` disables all integration tools like Eureka or Configservice making sure the project runs in isolation

## Run as a Docker Image
`[optional] $ mvn clean install docker:build`

`$ docker run whitbreaddigital/feedback-microservice:version`

Version corresponds to the [docker-hub](https://hub.docker.com/r/whitbreaddigital/feedback-service-opera/) version, if it's been previously generated, no version is needed, it will automatically fallback to `latest`

## Hit the Endpoint
* Hit the endpoint through the gateway: ```http://{GATEWAY_IP}:9033/feedback```
* For more information, you can check swagger: ```http://{GATEWAY_IP}:9033/swagger-ui.html```