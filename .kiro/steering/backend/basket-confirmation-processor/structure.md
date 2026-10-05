---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/basket-confirmation-processor/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces.

```
src/main/java/uk/co/whitbread/basket/confirmation/processor/
├── domain/                        # Pure business logic, no framework dependencies
│   ├── logic/                     # Port implementations (use cases)
│   ├── model/
│   │   └── in/                    # Inbound domain models
│   └── ports/
│       ├── primary/               # Inbound ports (driven by infrastructure)
│       └── secondary/             # Outbound ports (drive external systems)
├── infrastructure/                # Framework-specific adapters
│   ├── config/                    # Spring bean wiring, tracing config
│   ├── queue/                     # Kafka consumer adapter
│   │   ├── config/                # Kafka-specific configuration and properties
│   │   ├── mapper/                # MapStruct mappers (event ↔ domain)
│   │   └── model/
│   │       └── in/                # Kafka event DTOs
│   └── rest/
│       └── client/                # REST client adapters (WebClient)
│           ├── basket/            # Basket service client config and properties
│           │   ├── config/        # WebClient bean configuration
│           │   └── properties/    # Basket service connection properties
│           ├── exception/         # Client-specific exceptions
│           ├── mapper/            # Response mappers
│           ├── model/
│           │   └── in/            # REST request/response models
│           └── utils/             # WebClient utilities
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring is done in `InfrastructureBeanConfig`
- **Ports are interfaces** — primary ports define inbound use cases, secondary ports define outbound contracts
- **Infrastructure adapters are Spring components** — annotated with `@Component`, `@Configuration`
- **Mappers use MapStruct** — interface-based mappers in `mapper/` packages
- **Models use Lombok** — `@Builder`, `@Data`, `@Value` for DTOs
- **OpenAPI-generated models** go in `*.generated.models` packages (excluded from coverage/mutation)

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — Embedded Kafka for consumer tests
- **ArchUnit** — Enforces hexagonal architecture boundaries via `coding-convention-rules`
- **Contract tests** — Spring Cloud Contract for API verification
