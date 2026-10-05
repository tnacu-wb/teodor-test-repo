---
inclusion: fileMatch
fileMatchPattern: "backend/manage-modify/services/reservations-manager-entity-service/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces. Primary ports define inbound use cases (driven by REST controllers), and secondary ports define outbound contracts (implemented by REST clients, Feign clients, and AWS adapters).

```
src/main/java/uk/co/whitbread/booking/
├── domain/                            # Pure business logic, no framework dependencies
│   ├── logic/                         # Port implementations (use cases)
│   ├── model/                         # Domain models (reservation, booking, guest)
│   ├── ports/
│   │   ├── primary/                   # Inbound ports (use case interfaces)
│   │   │   └── BookingInPort.java     # Booking management use case
│   │   └── secondary/                 # Outbound ports (external system interfaces)
│   │       ├── BasketOutPort.java     # Basket service contract
│   │       ├── BookingOutPort.java    # OHIP/booking adapter contract
│   │       ├── CdhOutPort.java        # CDH adapter contract
│   │       ├── HotelInfoOutPort.java  # Hotel info service contract
│   │       ├── PaymentInfoOutPort.java # Payment info contract
│   │       └── ReservationOutPort.java # Reservation adapter contract
│   └── properties/                    # Domain configuration properties
├── infrastructure/                    # Framework-specific adapters
│   ├── config/                        # Spring configuration (Redis, Security, WebClient, Unleash)
│   ├── exceptions/                    # Infrastructure exception handling
│   └── rest/
│       ├── client/                    # Outbound REST/Feign client adapters
│       │   ├── aws/                   # AWS S3 adapter for invoice storage
│       │   ├── basket/                # Basket Service client
│       │   ├── booking/               # OHIP/booking adapter client
│       │   ├── cdh/                   # CDH Service client (legacy)
│       │   ├── cdhadapter/            # CDH Adapter Service client (OpenAPI-generated models)
│       │   ├── config/                # WebClient and Feign configuration
│       │   ├── content/               # Content Entity Service client
│       │   ├── ohip/                  # OHIP Adapter Service client
│       │   └── reservation/           # Reservation adapter client
│       ├── controller/                # Inbound REST controllers
│       └── utils/                     # REST utilities
└── ReservationsManagerEntityServiceApplication.java  # Spring Boot entry point
```

## OpenAPI Code Generation

The service generates model classes from multiple OpenAPI specifications at build time:

| Spec File | Generated Package |
|-----------|------------------|
| `basket-service-openApi.yaml` | `uk.co.whitbread.hotel.ohip.adapter.generated.models.basket` |
| `cdh-adapter-service-openApi.yaml` | `uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models` |

Generated sources use a `javax` → `jakarta` replacement step during the `generate-sources` phase.

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring is done in infrastructure config
- **Ports are interfaces** — primary ports define inbound use cases, secondary ports define outbound contracts
- **Infrastructure adapters are Spring components** — annotated with `@Component`, `@Configuration`
- **WebClient and Feign** — WebClient for reactive HTTP calls, Feign for declarative client interfaces
- **Mappers use MapStruct** — interface-based mappers for domain ↔ DTO conversions
- **Models use Lombok** — `@Builder`, `@Data`, `@Value` for DTOs
- **Thymeleaf + OpenHTMLToPDF** — HTML template rendering to PDF for invoices/confirmations
- **Redis caching** — cluster mode with Lettuce client
- **Feature flags via Unleash** — progressive rollouts controlled by feature flag keys
- **AWS S3** — generated PDFs stored in S3 buckets via AWS SDK v2

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — Spring Boot test with WireMock standalone stubs
- **Contract tests** — Spring Cloud Contract (base class: `ContractVerifierBaseTest`)
- **ArchUnit** — Enforces hexagonal architecture boundaries via `coding-convention-rules`
- **JaCoCo** — Code coverage with standard exclusions for models, config, mappers, generated code
