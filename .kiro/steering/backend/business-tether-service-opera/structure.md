---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/business-tether-service-opera/**"
---

# Project Structure

## Architecture

This service follows a **layered architecture** with clear separation between controllers, services, clients, and models. It uses Spring MVC for REST endpoints and Spring Web Services (`WebServiceTemplate`) for SOAP communication with the Worldline B2B PI API. The CDH integration is handled via the shared `commons-cdh-lib` library. OpenFeign is used for outbound REST calls to the PIBA Guid service with Resilience4j circuit breaker fallbacks.

```
src/main/java/uk/co/whitbread/business/tether/
├── BusinessTetherServiceApplication.java   # Spring Boot entry point
├── client/                                  # Feign clients for external services
│   ├── PibaGuidClient.java                 # Feign client to PIBA Guid service
│   └── PibaGuidClientFallbackFactory.java  # Circuit breaker fallback
├── config/                                  # Spring configuration classes
│   ├── BeanConfig.java                     # Bean definitions (WebServiceTemplate, JAXB marshaller)
│   ├── JacksonConfiguration.java           # JSON serialization settings
│   ├── PibaGuidErrorDecodeConfig.java      # Feign error decoder for PIBA Guid client
│   ├── SecurityConfig.java                 # Auth0 multi-tenant JWT security
│   └── WebConfig.java                      # Web/CORS configuration
├── controller/                              # REST API controllers
│   └── BusinessTetherController.java       # /business/tether endpoints (tether link, tethered login)
├── converter/                               # Request/response transformers
│   ├── WorldlineBusinessTetherTransformer.java       # Worldline SOAP request builder (current)
│   ├── WorldlineBusinessTetherTransformerLegacy.java # Worldline SOAP request builder (legacy)
│   └── WorldlineTransformer.java                     # Transformer interface
├── exception/                               # Custom exception classes
│   ├── BusinessTetherException.java        # General tether operation error
│   ├── ErrorCodes.java                     # Error code constants
│   ├── InValidTokenException.java          # Auth token validation error
│   └── PibaGuidException.java             # PIBA Guid service error
├── mapper/                                  # MapStruct mappers
│   └── TetherMapper.java                   # Object mapping for tether requests/responses
├── model/                                   # Request/response DTOs and domain models
│   ├── LoginCriteria.java                  # Tethered login request payload
│   ├── PibaTetheredGuidRequest.java        # PIBA Guid save request
│   ├── PibaTetheredGuidResponse.java       # PIBA Guid save response
│   ├── Scheme.java                         # Market scheme enum (GB, DE)
│   ├── TetheredLoginResponse.java          # Login response (sessionId, hash, nonce, timestamp)
│   ├── TetherLinkRequest.java              # Tether link request (linkCode, linkId)
│   └── TetherLinkResponse.java            # Tether link response (guid)
├── properties/                              # Configuration property classes
│   ├── Auth0Properties.java               # Auth0 tenant configuration
│   └── ValidatorProperties.java           # Link code validation rules
├── service/                                 # Business logic layer
│   ├── BusinessTetherLoginService.java     # Worldline tethered login & session refresh
│   ├── BusinessTetherService.java          # Tether-by-account/card orchestration
│   ├── CdhRegistrationService.java         # CDH tethered user registration
│   ├── PibaGuidServiceClient.java          # PIBA Guid persistence wrapper
│   └── SchemeExtractor.java               # Market scheme extraction from request
├── utils/                                   # Utility classes
│   ├── SanitizingUtils.java               # Log input sanitization
│   ├── SessionTokenUtil.java              # Nonce generation and hash computation
│   └── WorldlineUtils.java                # Worldline request/response serialization
└── validation/                              # Validators
    ├── LinkCodeConstraint.java             # Custom Jakarta validation annotation
    ├── LinkCodeValidator.java              # Link code length/format validator
    ├── PibaGuidValidator.java             # PIBA Guid response validation
    └── WorldLineTetherResponseValidator.java # Worldline SOAP response validation
```

## Key Conventions

- **Controller is thin** — delegates immediately to `BusinessTetherService` or `BusinessTetherLoginService`
- **Worldline communication via `WebServiceTemplate`** — SOAP marshal/send/receive with JAXB marshalling
- **Transformer interface pattern** — `WorldlineTransformer` interface with scheme-aware implementations for building SOAP requests
- **Models use Lombok** — `@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`
- **MapStruct for object mapping** — interface-based mapper (`TetherMapper`) for request/response transformations
- **Feign client with fallback factory** — circuit breaker pattern for PIBA Guid calls via Resilience4j
- **Multi-scheme support** — GB/DE market differentiation via `SchemeExtractor` and `Scheme` enum
- **Input sanitization** — `SanitizingUtils` strips newlines and control characters before logging
- **Custom Jakarta validation** — `@LinkCodeConstraint` annotation with `LinkCodeValidator` for link code format enforcement

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Contract tests** — Spring Cloud Contract with `ContractVerifierBaseTest` as base class (suffix `IT`)
- **WireMock** — `wiremock-spring-boot` integration for stubbing external service calls
- **Test resources** — `__files/` for WireMock response stubs, `contracts/` for consumer-driven contracts, `mappings/` for WireMock mappings
- **PIT mutation testing** — Excludes models, config, mappers, constants, exceptions, properties
