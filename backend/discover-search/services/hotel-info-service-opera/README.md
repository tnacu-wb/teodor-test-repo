# Whitbread Hotel Info &micro;Service

## Service Description

The intention of this service is to get hotel information from AEM and return it as is without response validation.

It will serve as abstraction layer between user and AEM and will:
* Validate input parameters and provide meaningful error messages
* Return pre-defined error code if AEM cannot be reached
* Return pre-defined error code when no information found for provided HotelCode

As described at [https://whitbreadis.atlassian.net/wiki/display/MID/Hotel+Info+formats](https://whitbreadis.atlassian.net/wiki/display/MID/Hotel+Info+formats)
this service will call following URLs for short and long formats:

* /content/{hotelBrandCode}/websites/hoteldirectory/gb/en/{hotelCodeInitial}/{hotelCode}.summary.data
* /content/{hotelBrandCode}/websites/hoteldirectory/gb/en/{hotelCodeInitial}/{hotelCode}.complete.data

Version corresponds to the [docker-hub](https://hub.docker.com/r/whitbreaddigital/hotel-info-service/) version, if it's been previously generated, no version is needed, it will automatically fallback to `latest`

# Running the Application

The application can be started with the command `mvn spring-boot:run` along with the desired environment profile, e.g: `mvn spring-boot:run -Dspring.profiles.active=local`

**To run locally, this application is dependent on a local instance of Redis to be running at localhost:6379**

**docker run --name some-redis -p 6379:6379 -d redis**

# Technologies
- Spring cloud microservices parent v4.0.6
- Maven 3.9.14
- Docker-compose v3+
- Java 25