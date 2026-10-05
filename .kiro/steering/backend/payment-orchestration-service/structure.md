---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/payment-orchestration-service/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces.

```
src/main/java/uk/co/whitbread/payment/orchestrator/
├── application/                   # REST controllers, config, DTOs
│   ├── controller/                # PaymentController, WebhookController
│   ├── config/                    # WebConfig, MonitoringConfig, TemporalConfig
│   └── dto/                       # Request/response DTOs with validation
├── domain/                        # Pure business logic, no framework dependencies
│   ├── model/                     # Domain models (PaymentSession, enums)
│   ├── ports/
│   │   ├── primary/               # Inbound ports (PaymentOrchestrationPort, TemporalWorkflowPort)
│   │   └── secondary/            # Outbound ports (DatatransPort, BasketPort, OperaPort)
│   └── workflow/                  # Temporal workflow interfaces
│       ├── SecureFieldsPaymentWorkflow
│       └── MobileSdkPaymentWorkflow
└── infrastructure/                # Framework-specific adapters
    ├── adapter/                   # Adapter implementations for external services
    ├── health/                    # Custom health indicators (Temporal, dependencies)
    └── temporal/                  # Temporal client and worker configuration
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring is done in configuration classes
- **Ports are interfaces** — primary ports define inbound use cases, secondary ports define outbound contracts
- **Infrastructure adapters are Spring components** — annotated with `@Component`, `@Configuration`
- **DTOs use Java records** with validation annotations
- **Mappers use MapStruct** — interface-based mappers between layers. Exception: polymorphic mappings over a sealed hierarchy (e.g. `PaymentInitRequest` → `PaymentInitCommand` in `PaymentController`) use an exhaustive switch with no `default` instead — the compiler then forces every new subtype to be mapped, a guarantee MapStruct's `@SubclassMapping` only provides at runtime
- **Models use Lombok** — `@Builder`, `@Data`, `@Value` for complex DTOs

## API Endpoints

| Controller | Path Prefix | Purpose |
|------------|-------------|---------|
| `PaymentController` | `/api/payments` | Payment initialisation and authorisation |
| `WebhookController` | `/api/payments/webhooks` | Datatrans callback handling |

## Testing Patterns

- **Unit tests** — MockMVC for controllers, Mockito for domain logic
- **Integration tests** — TestRestTemplate for REST endpoints, health checks
- **Contract tests** — Spring Cloud Contract for API verification
