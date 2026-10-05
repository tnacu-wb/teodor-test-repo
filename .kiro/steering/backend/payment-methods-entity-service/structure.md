---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/payment-methods-entity-service/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces.

```
src/main/java/uk/co/whitbread/payments/
├── domain/
│   ├── exception/
│   │   ├── AccountsServiceException.java
│   │   ├── ErrorCode.java
│   │   ├── HotelInfoException.java
│   │   ├── PaymentMethodsException.java
│   │   └── PaymentMethodsValidationException.java
│   ├── logic/
│   │   ├── common/                        # Shared logic utilities
│   │   ├── rule/                          # Business rule evaluation
│   │   ├── PaymentActionsPortImpl.java    # Payment actions use case
│   │   ├── PaymentCcuiMethodsPortImpl.java # CCUI-specific payment methods
│   │   └── PaymentMethodsPortImpl.java    # Core payment methods use case
│   ├── model/
│   │   ├── feature/                       # Feature flag wrappers
│   │   ├── in/                            # Inbound request models
│   │   ├── out/                           # Outbound response models
│   │   └── validation/                    # Self-validation pattern
│   └── ports/
│       ├── primary/                       # Inbound ports (use case interfaces)
│       └── secondary/                     # Outbound ports (external system interfaces)
├── infrastructure/
│   ├── config/
│   │   ├── cache/                         # Redis cache configuration
│   │   ├── BeanConfig.java               # Domain bean wiring
│   │   ├── CustomUnleashConfig.java       # Feature flag configuration
│   │   ├── JacksonConfig.java            # Jackson serialisation config
│   │   └── SecurityConfig.java           # Multi-tenant OAuth2 resource server
│   ├── repository/
│   │   └── paymentmethods/
│   │       ├── mapper/                    # YAML ↔ domain model mappers
│   │       ├── model/                     # Repository data models
│   │       ├── DefaultPaymentMethodsYamlFilePortImpl.java  # YAML file config source
│   │       └── PaymentMethodConfig.java   # Payment method definitions
│   ├── rest/
│   │   ├── client/
│   │   │   ├── basket/                    # Basket service client
│   │   │   ├── config/                    # WebClient configuration
│   │   │   ├── customers/                 # Hotel account / company card clients
│   │   │   ├── hotels/                    # Content service (hotel payment info)
│   │   │   ├── reservation/               # Reservation service client
│   │   │   ├── token/                     # 3C PayPal token client
│   │   │   └── util/                      # Client utilities
│   │   ├── controller/
│   │   │   └── payment/                   # REST controller endpoints
│   │   └── hotelentity/
│   │       ├── model/                     # Hotel entity response models
│   │       ├── properties/                # Hotel entity config properties
│   │       ├── service/                   # Hotel entity client service
│   │       └── HotelInfoPortImpl.java     # Secondary port implementation
└── PaymentMethodsEntityServiceApplication.java
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring in `infrastructure/config/`
- **Ports are interfaces** — primary ports define use cases, secondary ports define outbound contracts
- **WebClient for HTTP** — via `spring-boot-starter-webclient` for non-blocking outbound calls
- **Mappers use MapStruct** — interface-based for domain ↔ DTO conversions
- **Models use Lombok** — `@Builder`, `@Data`, `@Value`
- **YAML-based configuration** — payment method definitions loaded from YAML files
- **Multi-tenant auth** — SecurityConfig supports multiple OAuth2/JWT issuers (CCUI, PI/BB)
- **Redis caching** — Lettuce cluster client with key-prefix isolation
- **Contract-first API design** — OpenAPI specs under `docs/openApi/`

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — WireMock stubs for external services
- **Contract tests** — Spring Cloud Contract (base class: `ContractVerifierBaseTest`, suffix: `IT`)
- **ArchUnit** — Enforces hexagonal architecture boundaries via `coding-convention-rules`
- **Mutation testing** — Pitest with standard mutators
