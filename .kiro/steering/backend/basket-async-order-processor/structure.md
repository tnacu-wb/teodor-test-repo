---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/basket-async-order-processor/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces.

```
src/main/java/uk/co/whitbread/basket/processor/
├── domain/                        # Pure business logic, no framework dependencies
│   ├── logic/                     # Port implementations (use cases)
│   ├── model/
│   │   ├── in/                    # Inbound domain models
│   │   └── out/                   # Outbound domain models (requests/responses)
│   └── ports/
│       ├── primary/               # Inbound ports (driven by infrastructure)
│       └── secondary/             # Outbound ports (drive external systems)
├── infrastructure/                # Framework-specific adapters
│   ├── config/                    # Spring bean wiring, Jackson, tracing config
│   ├── queue/                     # Kafka consumers and producers
│   │   ├── config/                # Kafka-specific configuration and properties
│   │   ├── mapper/                # MapStruct mappers (event ↔ domain)
│   │   └── model/                 # Kafka event DTOs
│   │       ├── ack/               # Acknowledgement event models
│   │       └── order/             # Order event models
│   └── rest/
│       └── client/                # REST client adapters (WebClient)
│           ├── exception/         # Client-specific exceptions
│           ├── mapper/            # Response mappers
│           ├── reservation/       # Reservation service client config
│           └── utils/             # Client utilities
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring is done in `InfrastructureBeanConfig`
- **Domain logic uses Java records** — e.g. `BasketOrderProcessInPortImpl` is a record
- **Ports are interfaces** — primary ports define inbound use cases, secondary ports define outbound contracts
- **Infrastructure adapters are Spring components** — annotated with `@Component`, `@Configuration`
- **Mappers use MapStruct** — interface-based mappers in `mapper/` packages
- **Models use Lombok** — `@Builder`, `@Data`, `@Value` for DTOs
- **OpenAPI-generated models** go in `*.generated.models` packages (excluded from coverage/mutation)

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — Embedded Kafka for consumer/producer tests
- **ArchUnit** — Enforces hexagonal architecture boundaries
- **Contract tests** — Spring Cloud Contract for API verification
