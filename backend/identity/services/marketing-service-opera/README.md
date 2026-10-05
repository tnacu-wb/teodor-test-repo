# marketing-microservice
The Microservice handling subscriptions to marketing emails.

## How to run

* First compile using ```mvn clean install```
* Run docker-compose: ```docker-compose -f docker/docker-compose-dev.yml up -d```
* Hit the endpoint through the gateway: ```http://{GATEWAY_IP}:10000/marketing/...```
* For more information, you can check swagger: ```http://{GATEWAY_IP}:10000/marketing/swagger-ui.html```
* To run locally without Docker, use maven command with these parameters: ```mvn clean install spring-boot:run -Dspring.profiles.active=local```

# Technologies
 - Java 25
 - Spring cloud service 4.0.5
 - Maven 3+
 - Docker 0.12+
 - Docker-compose v1+
 
 # Diagrams
 
 *To regenerate plantuml diagrams please use the command `mvn plantuml:generate`*
 
 ---
 ## Legacy Flows
 
Subscribe new customers. The data goes to Bart and it sends data to CDH in a batch process. 
The data is usually replicated in 24 hours.
![Subscribe new customers](/docs/diagrams/images/subscribe-new-customers.png?raw=true "Subscribe new customers")
 
 
Edit Permissions. This flow can be used to unsubscribe customers.
![Edit Permissions](/docs/diagrams/images/edit-permissions.png?raw=true "Edit Permissions")


Retrieve Permissions. This flow can be used to get customer permissions.
![Retrieve Permissions](/docs/diagrams/images/retrieve-permissions.png?raw=true "Retrieve Permissions")


Retrieve Subscription Status. This endpoint checks if user is subscribed or not by returning a boolean.
When a user is subscribed in CDH through bart, it might take 24 hours for the data to be reflected in this endpoint.
![Newsletter Subscription Status](/docs/diagrams/images/newsletter-subscription-status.png?raw=true "Newsletter Subscription Status")


Retrieve Subscription Info. This endpoint checks if user is subscribed or not and returns customer info such 
as email, address, subscribed regions etc.
When a user is subscribed in CDH through bart, it might take 24 hours for the data to be reflected in this endpoint.
![Newsletter Subscription Status Info](/docs/diagrams/images/newsletter-subscription-info.png?raw=true "Newsletter Subscription Status Info")

## Permission Management Flows

![Get Preferences](/docs/diagrams/images/PM-getPermissions.png?raw=true "Get Newsletter Preferences")
![Update Preferences](/docs/diagrams/images/PM-updatePermissions.png?raw=true "Update Newsletter Preferences")
![Confirm Double Opt In](/docs/diagrams/images/PM-confirmDoubleOptIn.png?raw=true "Confirm Double Opt In")
![Unsubscribe](/docs/diagrams/images/PM-unsubscribe.png?raw=true "Unsubscribe from Newsletter Preferences")
