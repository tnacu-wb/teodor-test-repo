---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/opera-token-service/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces.

```
src/main/java/uk/co/whitbread/token/
├── domain/
│   ├── logic/
│   │   └── TokenInPortImpl.java           # Use case implementation (token retrieval)
│   ├── model/
│   │   ├── in/                            # Inbound request models
│   │   ├── out/                           # Outbound response models
│   │   └── validation/                    # Self-validation pattern
│   └── ports/
│       ├── primary/
│       │   └── TokenInPort.java           # Inbound port (fetch token use case)
│       └── secondary/
│           └── TokenOutPort.java          # Outbound port (call Opera for token)
├── infrastructure/
│   ├── config/
│   │   ├── CustomTokenResponseClient.java # Custom OAuth2 token response handling
│   │   ├── InfrastructureBeanConfig.java  # Bean wiring for domain services
│   │   ├── OAuthClientConfig.java         # OAuth2 client registration config
│   │   └── SecurityConfig.java            # Spring Security filter chain
│   ├── rest/
│   │   ├── client/
│   │   │   └── token/
│   │   │       ├── ohip/                  # OHIP-specific client models
│   │   │       ├── exception/             # Client error handling
│   │   │       ├── mapper/                # Response mappers
│   │   │       └── TokenOutPortImpl.java  # Secondary port implementation
│   │   └── controller/
│   │       └── token/
│   │           ├── mapper/                # Controller response mappers
│   │           ├── model/                 # API response models
│   │           ├── OperaTokenController.java
│   │           └── OperaTokenApiDocumentation.java
│   ├── util/
│   │   └── LoggingUtils.java             # Structured logging helpers
│   └── validation/
│       ├── ProviderIdValidator.java       # Custom provider ID validation
│       └── ValidProviderId.java           # Validation annotation
├── ErrorCode.java                         # Domain error codes
└── OperaTokenServiceApplication.java
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring in `infrastructure/config/`
- **Ports are interfaces** — primary ports define use cases, secondary ports define outbound contracts
- **WebClient for HTTP** — via Spring Security OAuth2 client for token acquisition
- **Mappers use MapStruct** — interface-based for domain ↔ DTO conversions
- **Models use Lombok** — `@Builder`, `@Data`, `@Value`
- **Custom validation** — `@ValidProviderId` annotation with `ProviderIdValidator`
- **Contract-first API design** — OpenAPI specs under `docs/openApi/`

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Contract tests** — Spring Cloud Contract (base class: `ContractVerifierBaseTest`, suffix: `IT`)
- **Performance tests** — Gatling load tests
- **ArchUnit** — Enforces hexagonal architecture boundaries via `coding-convention-rules`
