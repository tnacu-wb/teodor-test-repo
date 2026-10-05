---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/hotel-register-service-opera/**"
---

# Project Structure

## Architecture

This service follows a **layered architecture** with clear separation between controllers, services, clients, and models. It uses Spring MVC for REST endpoints and OpenFeign for outbound HTTP calls to hotel-countries, hotel-reservation-entity-service, and marketing-service-opera. The CDH integration is handled via the shared `commons-cdh-lib` library. Auth0 user provisioning is managed through dedicated service classes per customer type (leisure, business, InnBusiness). Security is enforced via API key validation (AOP aspect) for Universal Login endpoints and captcha verification for public-facing registration.

```
src/main/java/uk/co/whitbread/hotel/
├── captcha/                             # reCAPTCHA verification
│   ├── exception/                       # Captcha and cypher exceptions
│   └── service/                         # CaptchaService (Google reCAPTCHA validation)
└── register/
    ├── HotelRegisterMicroserviceApplication.java  # Spring Boot entry point
    ├── client/                          # Feign clients for external services
    │   ├── CountriesClient.java         # Feign client to hotel-countries service
    │   ├── HotelReservationEntityClient.java  # Feign client to hotel-reservation-entity-service
    │   └── MarketingServiceOperaClient.java   # Feign client to marketing-service-opera
    ├── config/                          # Spring configuration classes
    ├── controller/                      # REST API controllers
    │   ├── AppsHotelRegisterController.java   # /v1/hotel-register/accounts/register (apps channel)
    │   ├── CypherController.java              # Email encryption endpoints
    │   ├── HotelRegisterController.java       # /customers/hotels (web leisure/business)
    │   ├── InnBRegisterController.java        # /v1/hotel-register/innbusiness (B2B two-step)
    │   └── PIUniversalLoginController.java    # /v1/hotel-register/universal-login/leisure (API key)
    ├── exceptions/                      # Custom exception classes
    ├── mapper/                          # MapStruct mappers
    ├── model/                           # Request/response DTOs and domain models
    ├── properties/                      # Configuration property classes
    ├── security/                        # API key validation
    │   ├── ApiKeyProtected.java         # Custom annotation for API key protection
    │   └── ApiKeyValidationAspect.java  # AOP aspect enforcing API key header
    ├── service/                         # Business logic layer
    │   ├── auth0/                       # Auth0 service implementations per customer type
    │   │   ├── Auth0BusinessService.java      # Auth0 user creation for business customers
    │   │   ├── Auth0InnBusinessService.java   # Auth0 user creation for InnBusiness customers
    │   │   ├── Auth0LeisureService.java       # Auth0 user creation for leisure customers
    │   │   └── Auth0Service.java              # Core Auth0 Management API operations
    │   ├── CdhBbRegisterService.java    # CDH registration for Business Booker customers
    │   ├── CdhPiRegisterService.java    # CDH registration for Premier Inn customers
    │   ├── CountriesService.java        # Country lookup via Feign client
    │   ├── EmailService.java            # Azure email sending for confirmations
    │   ├── EncryptionService.java       # AES encryption for email addresses
    │   ├── HotelRegisterService.java    # Core registration orchestration
    │   ├── MarketingService.java        # Marketing preference handling
    │   └── PIUniversalLoginService.java # Universal Login registration and confirmation
    ├── utils/                           # Utility classes
    └── validation/                      # Business validators
        └── BusinessCustomerValidator.java  # Business customer field validation
```

## Key Conventions

- **Controllers are thin** — validate input (captcha, password policy) then delegate to service layer
- **Auth0 service per customer type** — separate classes for leisure, business, and InnBusiness Auth0 flows
- **CDH service per brand** — `CdhBbRegisterService` (Business Booker) and `CdhPiRegisterService` (Premier Inn)
- **Models use Lombok** — `@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`
- **MapStruct for object mapping** — interface-based mappers between request/response models
- **API key security via AOP** — `@ApiKeyProtected` annotation triggers aspect-based header validation
- **Password policy enforcement** — `PasswordPolicyUtil` utility enforces configurable policies before registration
- **Feature flags via Unleash** — company name validation and CDH API deprecation toggles
- **Multi-channel registration** — separate controllers per channel (web, apps, InnB, universal login)
- **Encryption service** — AES-based encryption for email addresses using configurable secret key, salt, and IV

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Contract tests** — Spring Cloud Contract with WireMock stubs and Groovy DSL contracts
- **Contract base classes** — `ContractVerifierBaseTest` (general) and `Auth0ContractVerifierBaseTest` (auth0-specific)
- **Random Beans** — Test data generation via `io.github.benas:random-beans`
- **REST Assured** — Spring Mock MVC integration for controller testing
- **PIT mutation testing** — Excludes models, config, mappers, constants, exceptions, properties, and ArchUnit tests
