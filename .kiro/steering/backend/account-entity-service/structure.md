---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/account-entity-service/**"
---

# Project Structure

## Architecture

This service follows a **Hexagonal Architecture** (Ports & Adapters) with clear separation between domain logic, primary ports (inbound), and secondary ports (outbound). It uses Spring MVC for REST endpoints and WebClient for outbound HTTP calls.

```
src/main/java/uk/co/whitbread/account/
├── AccountEntityServiceApplication.java      # Spring Boot entry point
├── domain/
│   ├── logic/                                # Domain use-case implementations
│   │   ├── AccountRegistrationPortImpl.java  # Orchestrates account registration
│   │   └── MarketingPreferencesPortImpl.java # Orchestrates preference updates
│   ├── model/
│   │   ├── feature/                          # Feature flag abstractions
│   │   │   ├── FeatureFlag.java
│   │   │   └── UnleashWrapper.java
│   │   ├── in/                               # Inbound domain models
│   │   │   ├── AccountRegistrationRequest.java
│   │   │   ├── Address.java
│   │   │   ├── AddressType.java
│   │   │   ├── ConfirmDoubleOptInRequest.java
│   │   │   ├── ContactDetail.java
│   │   │   ├── ContactSubType.java
│   │   │   ├── ContactType.java
│   │   │   ├── Customer.java
│   │   │   ├── MarketingPreferencesRequest.java
│   │   │   ├── MarketingPreferencesRequestV2.java
│   │   │   ├── Passport.java
│   │   │   ├── SourceChannel.java
│   │   │   ├── SourceDetails.java
│   │   │   ├── SourceLocale.java
│   │   │   └── UserJourney.java
│   │   ├── out/                              # Outbound domain models
│   │   │   └── AccountRegistrationResponse.java
│   │   └── validation/                       # Domain validation logic
│   │       ├── CompanyNameSwitchImpl.java
│   │       ├── Phone.java
│   │       ├── PostcodeConstraint.java
│   │       ├── PostcodeConstraintValidator.java
│   │       ├── SelfValidation.java
│   │       └── ValidatorFactory.java
│   └── ports/
│       ├── primary/                          # Inbound port interfaces
│       │   ├── AccountRegistrationPort.java
│       │   └── MarketingPreferencesPort.java
│       └── secondary/                        # Outbound port interfaces
│           ├── CustomerRegistrationPort.java
│           └── MarketingPreferencesClientPort.java
└── infrastructure/
    ├── config/                               # Spring configuration
    │   ├── InfrastructureBeanConfig.java
    │   ├── JacksonConfig.java
    │   └── UnleashConfig.java
    ├── exception/                            # Error handling
    │   ├── ErrorCode.java
    │   ├── GlobalErrorHandler.java
    │   ├── ServiceException.java
    │   └── ServiceRequestException.java
    └── rest/
        ├── client/                           # Outbound adapters (secondary ports)
        │   ├── config/
        │   │   ├── WebClientConfig.java
        │   │   └── WebClientConstants.java
        │   ├── customers/                    # Hotel-register client
        │   │   ├── CustomerRegistrationPortImpl.java
        │   │   ├── mapper/
        │   │   ├── model/
        │   │   └── service/
        │   └── marketing/                    # Marketing service client
        │       ├── MarketingPreferencesClientPortImpl.java
        │       ├── mapper/
        │       ├── model/
        │       └── service/
        └── controller/                       # Inbound adapters (primary ports)
            └── account/
                ├── AccountRegistrationApi.java
                ├── AccountRegistrationController.java
                ├── MarketingPreferencesApi.java
                ├── MarketingPreferencesController.java
                ├── mapper/
                └── model/
```

## Key Conventions

- **Hexagonal Architecture** — domain logic is independent of infrastructure concerns; ports define boundaries
- **Controllers are thin** — delegate immediately to primary port implementations in `domain/logic/`
- **Domain models are separate from DTOs** — `domain/model/in` and `domain/model/out` vs `controller/model` and `client/model`
- **Models use Lombok** — `@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`
- **MapStruct for object mapping** — interface-based mappers between controller DTOs ↔ domain models ↔ client models
- **Custom validators** — domain-level validation via `PostcodeConstraint`, `Phone`, and feature-flagged `CompanyNameSwitch`
- **Feature flags via Unleash** — wrapped in domain `UnleashWrapper` for testability
- **WebClient for outbound HTTP** — non-blocking calls to hotel-register and marketing services

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — Spring Boot Test with mocked dependencies
- **ArchUnit tests** — Architectural constraint verification via `coding-convention-rules`
- **Test structure mirrors main** — `src/test/java/uk/co/whitbread/account/{domain,infrastructure}/`
