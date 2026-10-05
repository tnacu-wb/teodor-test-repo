# Company Employee Microservice
 This service exposes Employee related operations. They include: 
- Activate an travel manager
- Retrieve all employees
- Retrieve a specific employee  
- Update employee info
- Update employee access level
- Add an employee
- Invite an employee
- Activate an employee
- Retrieve employee's booking preferences
- Update employee's booking preferences
- Approve or Reject an employee
- Update employee's password

# How to run
Before running the application locally please get the values for the variables below from https://manage.auth0.com/dashboard/eu/wbmigrationtest/applications/gBVcZzfXChbowQR1mfH8csbUwya9FzOB/settings:
- AUTH0_CLIENT_ID
- AUTH0_CLIENT_SECRET

Also, AUTH0_ENCRYPTION_KEY will be needed for the lazy migration functionality to work as expected.

## Run as a Spring Boot local application
 `mvn clean install spring-boot:run -Dspring.profiles.active=local`

A profile is necessary since the service is not design to fall back to any default profile. `local` disables all integration tools like Eureka or Configservice making sure the project runs in isolation/

## Run as a Docker Image 
`[optional] $ mvn clean install docker:build`  

`$ docker run whitbreaddigital/company-service:version`

Version corresponds to the [docker-hub](https://hub.docker.com/r/whitbreaddigital/company-employee-service/) version, if it's been previously generated, no version is needed, it will automatically fallback to `latest` 

# Service dependencies
- hotel-account

# Technologies 
- Spring cloud microservices parent v3.0.1
- Java 17
- Maven 3+ 
- Docker 2+
- Docker-compose v3+