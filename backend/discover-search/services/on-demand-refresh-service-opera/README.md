# on-demand refresh service
This microservice is used as follows: 
- By migration service to get Opera related hotel availabilities data into Av Cache.
- By developers in scenarios to sync up data between Opera & Av Cache if there are any issues.
- By nightly quartz job to retrieve Opera hotel availabilities for 365th day from the current date.

# Service dependencies
This service is dependent on Opera REST API to get the required hotel availabilities data.

# How to run
## Run as a Spring Boot local application
`mvn clean install spring-boot:run -Dspring.profiles.active=local`

A profile is necessary since the service is not design to fall back to any default profile. `local` disables all integration tools like Eureka or Configservice making sure the project runs in isolation/

## Run as a Docker Image
`[optional] $ mvn clean install docker:build`

`$ docker run whitbreaddigital/on-demand-refresh-service:version`

# Running the Application

The application can be started with the command `mvn spring-boot:run` along with the desired environment profile, e.g: `mvn spring-boot:run -Dspring.profiles.active=local`

*Profile `local` disables most integration tools and config-service, allowing it to run in isolation.*

**To run locally, this application is dependent on a local instance of Redis to be running at localhost:6379**

*Dependency on Redis can be disabled by adding the Spring profile `disable-caching`, e.g: `mvn spring-boot:run -Dspring.profiles.active=local`*

# Lombok
This application uses Lombok to reduce boilerplate code.

If you use IntelliJ, please install the Lombok plugin.

*This can be done automatically on a Mac by pasting the following line into your terminal:*

*`cd ~/Library/Application\ Support/Idea*;curl -L https://plugins.jetbrains.com/plugin/download?updateId=38691 -o z;unzip z;rm z;`*

# Diagrams

*To regenerate plantuml diagrams please use the command `mvn plantuml:generate`*

# Technologies
- Spring cloud microservices parent v4.0.6
- Java 25
- Maven 3+
- Docker 2+
- Docker-compose v3+
- Quartz

# Upgrade validation
- Run unit and startup checks: `mvn clean test`
- Start locally with profile: `mvn spring-boot:run -Dspring.profiles.active=local`