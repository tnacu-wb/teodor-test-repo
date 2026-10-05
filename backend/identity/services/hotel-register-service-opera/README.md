# hotel-register-service-opera

## How to run
Before running the application locally please get the values for the variables below from https://manage.auth0.com/dashboard/eu/wbmigrationtest/applications/gBVcZzfXChbowQR1mfH8csbUwya9FzOB/settings:
- AUTH0_CLIENT_ID
- AUTH0_CLIENT_SECRET

Then follow the steps below:

* First compile using ```mvn clean install -s settings.xml```
* Run docker-compose: ```docker-compose -f docker/docker-compose-dev.yml up -d```
* Hit the endpoint through the gateway: ```http://{GATEWAY_IP}:9027/customers/hotels...```
* For more information, you can check swagger: ```http://{GATEWAY_IP}:9027/swagger-ui.html```
* To run locally without Docker, use maven command with these parameters: ```mvn clean install spring-boot:run -Dspring.profiles.active=local```

## Run as a Spring Boot local application
`mvn clean install spring-boot:run -Dspring.profiles.active=local`

## Run as a Docker Image
`[optional] $ mvn clean install docker:build`

`$ docker run whitbreaddigital/hotel-register-service-opera:version`

Version corresponds to the [docker-hub](https://github.com/whitbread-eos/hotel-register-service-opera) version, if it's been previously generated, no version is needed, it will automatically fallback to `latest`

# Technologies
- [Spring cloud microservices parent v3.0.1]
- Maven 3+
- Docker 0.12+
- Docker-compose v1+
- Java 17