---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/basket-service/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces.

```
src/main/java/uk/co/whitbread/basket/
├── domain/
│   ├── logic/                         # Port implementations (use cases)
│   ├── model/                         # Domain models
│   │   ├── basket/                    # Basket aggregate (create, add items, confirm)
│   │   ├── ccuieckoh/                 # CCUI Eckoh payment models
│   │   ├── email/                     # Email notification models
│   │   ├── feature/                   # Feature flag wrapper (UnleashWrapper)
│   │   ├── payments/                  # Payment domain models
│   │   ├── reservation/               # Reservation models
│   │   └── validation/                # Self-validation pattern
│   └── ports/
│       ├── primary/                   # Inbound ports (use case interfaces)
│       └── secondary/                 # Outbound ports (external system interfaces)
├── infrastructure/
│   ├── config/                        # Spring configuration (Security, Kafka, Redis, Unleash)
│   ├── queue/                         # Kafka producers (outbound events)
│   ├── repository/                    # DynamoDB persistence adapters
│   └── rest/
│       ├── client/                    # Outbound REST client adapters (OHIP, payments, CDH, content)
│       └── controller/                # Inbound REST controllers
│           ├── basket/                # Basket CRUD endpoints
│           ├── ccui/                  # CCUI-specific endpoints (payment, eckoh)
│           └── payments/              # Payment webhook endpoints
└── BasketServiceApplication.java
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring in `infrastructure/config/`
- **Ports are interfaces** — primary ports define use cases, secondary ports define outbound contracts
- **WebClient for HTTP** — via `spring-boot-starter-webclient` for non-blocking outbound calls
- **Mappers use MapStruct** — interface-based for domain ↔ DTO conversions
- **Models use Lombok** — `@Builder`, `@Data`, `@Value`
- **DynamoDB persistence** — DynamoDbEnhancedClient with entity mapper
- **Kafka events** — async confirmation and refund event publishing
- **Contract-first API design** — OpenAPI specs under `src/main/resources/openapi/`
- **Generated models** — 8 OpenAPI specs generate DTOs for downstream service contracts

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — WireMock stubs for external services
- **Contract tests** — Spring Cloud Contract (base class: `ContractVerifierBaseTest`, suffix: `IT`)
- **ArchUnit** — Enforces hexagonal architecture boundaries via `coding-convention-rules`
