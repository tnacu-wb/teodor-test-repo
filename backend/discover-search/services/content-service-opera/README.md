# **Content Service**
Get content from AEM and return the response in the json format.
This service has been created for getting room types from AEM for different brands and languages.

### **Service Description:**

This service provides 3 endpoints:

 1. /content/roomtypes :
 Given country code, language code and brand, this end point returns a list of all room types with their details, for that brand.
 The default values are country - "gb" , language - "en", brand - "pi"

 2. /content/roomtypes/{roomTypeCode} :
 Given country code, language code, brand and roomTypeCode, this end point returns the room type with roomTypeCode given in the parameter
 E.g. /content/roomtypes?country=gb&language=en&brand=pi
 
 3. /content/rateclassifications :
 Gets the information for different rates based on hotelCode, language, brand
 The default values are hotelCode - "", language - "en", brand - "pi"
 E.g /content/rateclassifications?hotelCode=CARROA&language=en&brand=pi
 
 4. /content/cookiepolicies :
 Gets cookie policies based on language, brand, and subBrand
 The default values are language - "en" , brand - "pi", subBrand - "none" 
 E.g. /content/cookiepolicies?language=en&brand=pi&subBrand=none

### **How to run**
Run as a Spring Boot local application
mvn clean install spring-boot:run -Dspring.profiles.active=local

A profile is necessary since the service is not design to fall back to any default profile. local disables all integration tools

### **Technologies**
Spring cloud microservices parent v3.3.1
Maven 3+
Docker 0.12+
Docker-compose v3+
Lombok
Java 17

This application uses Lombok to reduce boilerplate code.

If you use IntelliJ, please install the Lombok plugin.

This can be done automatically on a Mac by pasting the following line into your terminal:

cd ~/Library/Application\ Support/Idea*;curl -L https://plugins.jetbrains.com/plugin/download?updateId=38691 -o z;unzip z;rm z;