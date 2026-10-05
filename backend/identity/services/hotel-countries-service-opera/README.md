# Hotel Countries MS
This service allows clients to retrieve a list of supported countries with their:
 - ISO code
 - Country name
 - Dialing code
 - Link to the country flag image
 - Passport requirements

# Service dependencies
This service has no microservice dependencies and relies on AEM.

# How to run
## Run as a Spring Boot local application
`mvn clean install spring-boot:run -Dspring.profiles.active=local`

# Technologies
 - Spring cloud microservices parent v4.0.5
 - Maven 3+
 - Java 25