# Company Microservice
This service exposes Company and Employee related operations. They include:
- List all companies
- Update company booking allowances
- Update company booking alerts
- Search for a company
- Retrieve company details
- Update company details
- Retrieve all user questions
- Create a user question
- Remove a user question
- Update a user question
- Retrieve all business questions
- Update a business question
- Retrieve all employees
- Update an employee

# Technologies 
- Spring cloud microservices parent v3.0.1
- Java 17
- Maven 3+ 
- Docker 2+
- Docker-compose v3+

# Service dependencies
- hotel-account

# Lombok
This application uses Lombok to reduce boilerplate code.

If you use IntelliJ, please install the Lombok plugin.

*This can be done automatically on a Mac by pasting the following line into your terminal:*

*`cd ~/Library/Application\ Support/Idea*;curl -L https://plugins.jetbrains.com/plugin/download?updateId=38691 -o z;unzip z;rm z;`*

# How to run
Before running the application locally please get the values for the variables below from https://manage.auth0.com/dashboard/eu/wbmigrationtest/applications/gBVcZzfXChbowQR1mfH8csbUwya9FzOB/settings:
- AUTH0_CLIENT_ID
- AUTH0_CLIENT_SECRET
## Run as a Spring Boot local application
`mvn clean install spring-boot:run -Dspring.profiles.active=local`

A profile is necessary since the service is not designed to fall back to any default profile. `local` disables all integration tools making sure the project runs in isolation.

## Run as a Docker Image
`[optional] $ mvn clean install docker:build`

`$ docker run whitbreaddigital/company-service:version`

**version** corresponds to the [docker-hub](https://hub.docker.com/r/whitbreaddigital/company-service/tags) version, if it's been previously generated; this can be left empty, in which case it will pick up `latest`.