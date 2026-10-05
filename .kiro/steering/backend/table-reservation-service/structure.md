---
inclusion: fileMatch
fileMatchPattern: "backend/book-pay/services/table-reservation-service/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces. Primary ports define inbound use cases (driven by REST controllers), and secondary ports define outbound contracts (implemented by REST clients).

```
src/main/java/uk/co/whitbread/reservation/
├── domain/                            # Pure business logic, no framework dependencies
│   ├── config/                        # Domain configuration (bean wiring)
│   ├── logic/                         # Port implementations (use cases)
│   ├── model/                         # Domain models (reservation, restaurant, slots)
│   └── ports/
│       ├── primary/                   # Inbound ports (use case interfaces)
│       │   ├── AemInPort.java         # AEM content retrieval use case
│       │   └── TableReservationInPort.java  # Table booking CRUD use case
│       └── secondary/                 # Outbound ports (external system interfaces)
│           ├── AemOutPort.java        # AEM content adapter contract
│           └── TableReservationOutPort.java  # LiveRes/Zonal adapter contract
├── infrastructure/                    # Framework-specific adapters
│   ├── config/                        # Spring configuration (Security, RestClient, Jackson, Cache)
│   └── rest/
│       ├── client/                    # Outbound REST client adapters (LiveRes, AEM)
│       └── controller/                # Inbound REST controllers
├── utils/                             # Cross-cutting utilities
└── BookingServiceApplication.java     # Spring Boot entry point
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring is done in `domain/config/`
- **Ports are interfaces** — primary ports define inbound use cases, secondary ports define outbound contracts
- **Infrastructure adapters are Spring components** — annotated with `@Component`, `@Configuration`
- **RestClient for HTTP** — migrated from `RestTemplate` to Spring Boot 4's `RestClient` fluent API
- **Mappers use MapStruct** — interface-based mappers for domain ↔ DTO conversions
- **Models use Lombok** — `@Builder`, `@Data`, `@Value` for DTOs
- **Caffeine caching** — local in-memory cache for AEM content (no Redis)
- **Auth0 security** — JWT validation via `common-auth0` shared library

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — Spring Boot test with WireMock stubs for external services
- **Contract tests** — Spring Cloud Contract (base class: `ContractVerifierBaseTest`, currently skipped)
- **ArchUnit** — Enforces hexagonal architecture boundaries via `coding-convention-rules`
- **ApplicationStartupTest** — Verifies context load, actuator endpoints, and OpenAPI docs
