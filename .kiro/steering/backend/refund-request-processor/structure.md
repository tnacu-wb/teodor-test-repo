---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/refund-request-processor/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces. The primary port is driven by a Kafka consumer (not a REST controller), and secondary ports define outbound contracts for payment execution and event acknowledgement.

```
src/main/java/uk/co/whitbread/refund/processor/
├── domain/                            # Pure business logic, no framework dependencies
│   ├── logic/                         # Port implementations (use cases)
│   ├── model/                         # Domain models (refund request, payment response)
│   └── ports/
│       ├── primary/                   # Inbound ports (use case interfaces)
│       │   └── RefundRequestProcessInPort.java  # Refund processing use case
│       └── secondary/                 # Outbound ports (external system interfaces)
│           ├── BasketAcknowledgeOutPort.java     # Kafka acknowledgement producer contract
│           └── RefundRequestProcessOutPort.java  # 3C Payments refund execution contract
├── infrastructure/                    # Framework-specific adapters
│   ├── config/                        # Spring configuration (Kafka, Jackson, WebClient)
│   ├── queue/                         # Kafka consumer and producer adapters
│   │   ├── config/                    # Kafka consumer/producer configuration
│   │   ├── mapper/                    # Event ↔ domain model mappers
│   │   ├── model/                     # Kafka event DTOs
│   │   ├── BasketAcknowledgeOutPortImpl.java   # Kafka producer (acknowledgements)
│   │   ├── BasketAcknowledgeProducer.java      # Kafka template wrapper
│   │   └── RefundRequestConsumer.java          # Kafka listener (inbound events)
│   └── rest/
│       ├── client/                    # Outbound REST client adapters
│       │   ├── exception/             # Client exception handling
│       │   └── threec/                # 3C Payments API client adapter
│       └── controller/                # Minimal REST controller (health/status)
└── RefundRequestProcessorApplication.java  # Spring Boot entry point
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring is done in infrastructure config
- **Ports are interfaces** — primary port is driven by Kafka consumer, secondary ports define outbound contracts
- **Kafka consumer as primary adapter** — `RefundRequestConsumer` implements the inbound driving adapter (not a REST controller)
- **Kafka producer for acknowledgements** — `BasketAcknowledgeOutPortImpl` publishes completion events
- **Infrastructure adapters are Spring components** — annotated with `@Component`, `@Configuration`
- **Mappers use MapStruct** — interface-based mappers for event ↔ domain model conversions
- **Models use Lombok** — `@Builder`, `@Data`, `@Value` for DTOs
- **OpenAPI-generated models** — payment API DTOs generated from `payments-service-openapi.yaml`
- **Brave Kafka instrumentation** — tracing propagated across Kafka messages

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — Spring Boot test with embedded Kafka (`spring-kafka-test`)
- **Contract tests** — Spring Cloud Contract (verifier)
- **ArchUnit** — Enforces hexagonal architecture boundaries via `coding-convention-rules`
- **JaCoCo** — Code coverage with standard exclusions for models, config, mappers, generated code
