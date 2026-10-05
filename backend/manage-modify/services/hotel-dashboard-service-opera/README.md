# Whitbread App Dashboard &micro;Service
This service generates a dashboard for mobile apps to show on the home screen.

# Technologies
 - Spring cloud microservices parent v4.0.5
 - JDK 25
 - Maven 3+
 - Docker 2+
 - Docker-compose v3+

# Service dependencies
- hotel-reservation
- hotel-info

# Lombok
This application uses Lombok to reduce boilerplate code.

If you use IntelliJ, please install the Lombok plugin.

*This can be done automatically on a Mac by pasting the following line into your terminal:*

*`cd ~/Library/Application\ Support/Idea*;curl -L https://plugins.jetbrains.com/plugin/download?updateId=38691 -o z;unzip z;rm z;`*


# Diagrams
*To regenerate plantuml diagrams please use the command `mvn plantuml:generate`*

Version 2 supports frequent bookings and past bookings
![Alt text](/docs/diagrams/images/dashboardV2.png?raw=true "Generate Dashboard V2")

Dashboard rules
![Alt text](/docs/diagrams/images/dashboard_steps.png?raw=true "Dashboard rules.")

Frequent booking logic
![Alt text](/docs/diagrams/images/frequent_bookings_logic.png?raw=true "Frequent bookings logic.")


# How to run
## Run as a Spring Boot local application
`mvn clean install spring-boot:run -Dspring.profiles.active=local`

A profile is necessary since the service is not designed to fall back to any default profile. `local` disables all integration tools like Eureka or config-service making sure the project runs in isolation.

## Run as a Docker Image
`[optional] $ mvn clean install docker:build`

`$ docker run whitbreaddigital/hotel-dashboard:version`

## Run mutation tests
To execute mutation tests, run:

`mvn test-compile org.pitest:pitest-maven:mutationCoverage`

Generated report can be checked at:  `target/pit-reports/index.html`

This is run as part of the mutation testing reports, which are uploaded as GitHub Pages using a dedicated job, part of develop branch workflow. More details can be checked at:
https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/4552065030/Addition+of+Mutation+Test+PIT+test+to+GitHub+workflow
