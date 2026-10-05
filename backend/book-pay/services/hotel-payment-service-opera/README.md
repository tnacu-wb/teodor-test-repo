# Hotel Payment MS

* Validate Card Bin Ranges
* Validate whether 3DS (https://en.wikipedia.org/wiki/3-D_Secure) is required for a payment 
* Handle 3DS V2.1 requirements - https://whitbreadis.atlassian.net/wiki/spaces/BE/pages/2023522314/3DS+v2.1+Changes
   
# Service dependencies
This service doesn't have any internal dependencies

# How to run
## Run as a Spring Boot local application
`mvn clean install spring-boot:run -Dspring.profiles.active=local`

A profile is necessary since the service is not design to fall back to any default profile. `local` disables all integration tools like Eureka or Configservice making sure the project runs in isolation/

# Lombok
This application uses Lombok to reduce boilerplate code.

If you use IntelliJ, please install the Lombok plugin.

*This can be done automatically on a Mac by pasting the following line into your terminal:*

*`cd ~/Library/Application\ Support/Idea*;curl -L https://plugins.jetbrains.com/plugin/download?updateId=38691 -o z;unzip z;rm z;`*

# Diagrams

*To regenerate plantuml diagrams please use the command `mvn plantuml:generate`*

![Payment with 3DS Complete](/infrastructure/diagrams/images/Payment3dsComplete.png?raw=true "Payment with 3ds Complete")

![Pay Now 3DS v2.1 Journey](/infrastructure/diagrams/images/3dsV2.1-PayNow.png?raw=true "Pay Now 3DS v2.1 Journey")

# Technologies
 - Spring cloud microservices parent v4.0.5
 - Java 25