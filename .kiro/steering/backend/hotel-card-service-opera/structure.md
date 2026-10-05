---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/hotel-card-service-opera/**"
---

# Project Structure

## Architecture

This service follows a **layered architecture** with controller → service → client layers. It uses the Strategy pattern for payment card type handling and Feign clients with Resilience4j circuit breakers for downstream communication.

```
src/main/java/uk/co/whitbread/hotel/card/
├── cache/                             # Redis cache providers
│   └── CustomerCacheProvider.java     # Customer card data caching
├── client/                            # Feign client interfaces for downstream services
│   ├── account/                       # Hotel Account Service client
│   │   ├── model/                     # Account client DTOs
│   │   ├── HotelAccountClient.java    # Feign client interface
│   │   ├── HotelAccountClientFallbackFactory.java
│   │   └── HotelAccountErrorDecodeConfig.java
│   ├── payment/                       # 3C Payment Service client
│   │   ├── model/                     # Payment client DTOs
│   │   ├── Payment3CP.java            # Feign client interface
│   │   └── Payment3CPFallbackFactory.java
│   ├── piba/                          # PIBA Account Service client
│   │   ├── model/                     # PIBA client DTOs
│   │   ├── PibaAccountClient.java     # Feign client interface
│   │   └── PibaAccountClientFallbackFactory.java
│   └── worldline/                     # Worldline REST API client
│       ├── model/                     # Worldline client DTOs
│       ├── WorldlineClient.java       # Feign client interface
│       └── WorldlineFallbackFactory.java
├── config/                            # Spring configuration classes
│   ├── CdhClientConfig.java           # CDH WebClient configuration
│   ├── HotelCardWebClientConfig.java  # WebClient bean definitions
│   ├── JacksonConfig.java             # JSON serialization config
│   ├── SecurityConfig.java            # Auth0 JWT security configuration
│   ├── UnleashConfig.java             # Feature flag configuration
│   ├── WebConfig.java                 # Web MVC configuration
│   └── WorldlineConfig.java           # Worldline client configuration
├── controller/                        # REST API controllers
│   ├── CompanyCardsController.java    # Company card CRUD endpoints
│   ├── CustomerCardsController.java   # Customer card CRUD endpoints
│   ├── HotelCardsController.java      # General hotel card endpoints
│   └── InnBusinessCardsController.java # Inn Business card endpoints
├── exceptions/                        # Custom exception classes
├── filters/                           # Request filters
│   ├── CdhEmployeeFilter.java         # CDH employee validation filter
│   ├── CdhSuperAccessLevelFilter.java # CDH super access level filter
│   └── WorldlinePibaAuthFilter.java   # Worldline PIBA auth filter
├── mapper/                            # MapStruct mappers
│   ├── worldline/                     # Worldline-specific mappers
│   ├── AuthorizeScaMapper.java        # SCA authorization mapping
│   ├── CardMapper.java                # General card mapping
│   ├── CDHMapper.java                 # CDH data mapping
│   ├── EmployeeMapper.java            # Employee data mapping
│   ├── PaymentCardMapper.java         # Payment card mapping
│   └── SaveCardMapper.java            # Save card request mapping
├── model/                             # Domain and DTO models
│   ├── adapter/                       # Adapter-specific models
│   ├── feature/                       # Feature toggle models
│   ├── validation/                    # Validation models
│   └── *.java                         # Card, payment, and Worldline models
├── properties/                        # Configuration properties classes
│   └── WorldlineRestProperties.java   # Worldline REST client properties
├── service/                           # Business logic layer
│   ├── cards/                         # Card type strategies (Strategy pattern)
│   │   ├── BBCentralCardService.java  # BB Central card operations
│   │   ├── BBPersonalCardService.java # BB Personal card operations
│   │   ├── PaymentCardContext.java    # Strategy context (card type resolver)
│   │   ├── PaymentCardStrategy.java   # Strategy interface
│   │   └── PIPersonalCardService.java # PI Personal card operations
│   ├── worldline/                     # Worldline integration service
│   │   ├── converter/                 # Request/response converters
│   │   ├── model/                     # Worldline service models
│   │   ├── validator/                 # Worldline input validators
│   │   └── WorldlineService.java      # Worldline orchestration service
│   ├── CdhAuthorizationService.java   # CDH authorization logic
│   ├── CdhCompanyCardsService.java    # CDH company card operations
│   ├── CdhHotelCardsService.java      # CDH hotel card operations
│   ├── CdhInnBusinessCardsService.java # CDH Inn Business card operations
│   ├── CustomerCardsService.java      # Customer card business logic
│   └── PibaAccountService.java        # PIBA account interaction logic
├── utils/                             # Utility classes
│   ├── CardTokeniser.java             # Card tokenisation utilities
│   ├── EmployeeHeaderDetails.java     # Employee header extraction
│   ├── EnumConverter.java             # Enum conversion utilities
│   ├── FilterUtils.java               # Filter helper utilities
│   ├── NetworkUtils.java              # Network/IP utilities
│   ├── PaymentCardMasker.java         # Card number masking
│   ├── SanitizingUtils.java           # Input sanitisation
│   ├── ValidationUtils.java           # Validation utilities
│   └── WorldlineUtils.java            # Worldline-specific utilities
└── HotelCardServiceApplication.java   # Spring Boot entry point
```

## Key Conventions

- **Layered architecture** — Controller → Service → Client with clear separation of concerns
- **Strategy pattern** for card types — `PaymentCardStrategy` interface with `BBCentral`, `BBPersonal`, `PIPersonal` implementations resolved by `PaymentCardContext`
- **Feign clients** with circuit breaker fallback factories for all downstream services
- **Resilience4j circuit breakers** configured per downstream client (hotelaccount, threecPayment, worldline, pibaaccount)
- **MapStruct mappers** for object mapping between layers
- **Lombok** — `@Data`, `@Builder`, `@Slf4j`, `@RequiredArgsConstructor` for boilerplate reduction
- **Redis caching** — Customer card data cached via `CustomerCacheProvider`
- **Auth0 JWT security** — Token validation with multi-tenant support
- **Unleash feature flags** — Progressive rollout of CDH-related features
- **OpenAPI code generation** — Payment DTOs generated from OpenAPI spec (`threec-payment-service-openApi.yaml`)
- **PMD** — Code quality via `codequality/pmd/` rules
- **Multi-market support** — GB and DE Worldline configurations with market-specific credentials

## Testing Patterns

- **Unit tests** — Mockito-based with JavaFaker for test data generation
- **Integration tests** — WireMock-backed Feign client tests
- **Contract tests** — Spring Cloud Contract for API verification
- **REST Assured** — API endpoint testing
- **Mutation testing** — PIT/Pitest with customised mutators

