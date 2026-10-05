---
inclusion: fileMatch
fileMatchPattern: "backend/arrive-stay-leave/services/hotel-wallet-service-opera/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces.

```
src/main/java/uk/co/whitbread/wallet/
├── domain/                        # Pure business logic, no framework dependencies
│   ├── exception/                 # Domain-specific exceptions
│   │   ├── HotelInfoNotFoundException.java
│   │   ├── PassJsonCreationException.java
│   │   ├── ReservationNotFoundException.java
│   │   ├── SignatureException.java
│   │   └── WalletCreationException.java
│   ├── logic/                     # Port implementations (use cases)
│   │   ├── HotelReservationInPortImpl.java
│   │   └── WalletGeneratorInPortImpl.java
│   ├── model/                     # Domain models
│   │   ├── in/                    # Inbound request models
│   │   ├── out/                   # Outbound response models
│   │   └── validation/            # Validation models
│   ├── ports/
│   │   ├── primary/               # Inbound ports (driven by controllers)
│   │   └── secondary/             # Outbound ports (drive REST clients, S3, certs)
│   ├── properties/                # Domain configuration properties
│   │   └── WalletProperties.java
│   └── utils/                     # Domain utilities
│       ├── passkit/               # PassKit-specific utilities
│       ├── LocalFileRetriever.java
│       ├── PassDataMetricsUtils.java
│       └── TemplateUtils.java
├── infrastructure/                # Framework-specific adapters
│   ├── certs/                     # Certificate retrieval adapter (S3)
│   │   ├── properties/            # Certificate config properties
│   │   └── CertsRetrieverOutPortImpl.java
│   ├── config/                    # Spring bean wiring and configuration
│   │   ├── InfrastructureBeanConfig.java
│   │   ├── JPassKitConfig.java
│   │   ├── S3Config.java
│   │   ├── S3ConfigStaticAuthentication.java
│   │   ├── SecurityConfig.java
│   │   └── ThymeleafConfig.java
│   ├── exceptions/                # Infrastructure-level exception types
│   │   ├── CertificateException.java
│   │   ├── FileNotFoundException.java
│   │   └── HotelReservationException.java
│   └── rest/
│       ├── client/                # Outbound REST client adapters (WebClient)
│       │   ├── config/            # WebClient configuration
│       │   ├── content/           # Content Service client
│       │   └── reservations/      # Reservation Service client
│       ├── controller/            # Inbound REST controllers
│       │   └── wallet/            # Wallet generation controller
│       └── utils/                 # REST utilities
│           └── WebClientUtils.java
├── ErrorCode.java                 # Application error code enum
└── WalletServiceApplication.java  # Spring Boot entry point
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring is done in `InfrastructureBeanConfig`
- **Ports are interfaces** — primary ports define inbound use cases, secondary ports define outbound contracts
- **Infrastructure adapters are Spring components** — annotated with `@Component`, `@Configuration`
- **Mappers use MapStruct** — annotation processor with Lombok binding
- **Models use Lombok** — `@Builder`, `@Data`, `@Value` for DTOs
- **OpenAPI-generated models** go in `*.generated.models` packages (excluded from coverage/mutation)
- **Brand templates** live in `src/main/resources/template*` directories (templatePI, templateBB, etc.)
- **Apple certificates** are managed via S3 (retrieved at runtime)

## OpenAPI Specifications

External service API specs for model code generation live in `src/main/resources/openapi/`:
- `reservation-service-openapi.yaml` — Reservation Service models
- `basket-service-api.yaml` — Basket Service models
- `content-service-openapi.yaml` — Content Service models

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — WireMock/MockWebServer for WebClient-based integration tests
- **ArchUnit** — Enforces hexagonal architecture boundaries (via `coding-convention-rules`)
- **Contract tests** — Spring Cloud Contract + Pact for consumer/provider verification
- **Coding conventions** — Whitbread `coding-convention-rules` library
