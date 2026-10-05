---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/address-lookup-entity-service/**"
---

# Tech Stack

- Java 25, Spring Boot 4.0.7, Spring Cloud 2025.1.1
- Spring Web MVC (REST)
- qas-address-lookup-lib (SOAP client for QAS)
- JUnit 5, Mockito, Spring Boot Test

## Build

```bash
./mvnw clean install -pl backend/book-pay/services/address-lookup-entity-service -am
```
