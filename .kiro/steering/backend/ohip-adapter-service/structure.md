---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/ohip-adapter-service/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces.

```
src/main/java/uk/co/whitbread/ohip/
├── domain/                        # Pure business logic, no framework dependencies
│   ├── exceptions/                # Domain-specific exceptions
│   ├── logic/                     # Port implementations (use cases)
│   │   └── utils/                 # Domain logic utilities
│   ├── model/                     # Domain models grouped by capability
│   │   ├── amend/                 # Reservation amendment models
│   │   ├── availability/          # Hotel availability models
│   │   ├── changelog/             # Change log models
│   │   ├── checkin/               # Check-in models
│   │   ├── eckoh/                 # Payment tokenisation models
│   │   ├── feature/               # Feature toggle models
│   │   ├── hotel/                 # Hotel info models
│   │   ├── lov/                   # List of values models
│   │   ├── opera/                 # Opera-specific common models
│   │   ├── packages/              # Package/add-on models
│   │   ├── preferences/           # Guest preference models
│   │   ├── profile/               # Guest profile models
│   │   ├── rates/                 # Rate plan models
│   │   ├── reservation/           # Reservation models
│   │   ├── roomallocation/        # Room allocation models
│   │   ├── rules/                 # Business rules models
│   │   ├── udfs/                  # User-defined field models
│   │   └── validation/            # Validation models
│   ├── ports/
│   │   ├── primary/               # Inbound ports (driven by controllers)
│   │   └── secondary/             # Outbound ports (drive OHIP API clients)
│   └── utils/                     # Domain utilities (e.g. SanitizingUtils)
├── infrastructure/                # Framework-specific adapters
│   ├── config/                    # Spring bean wiring, Unleash, caching, thread pools
│   │   └── cache/                 # Redis cache configuration
│   ├── exceptions/                # Infrastructure-level exception types
│   └── rest/
│       ├── client/                # Outbound REST client adapters (WebClient → OHIP)
│       │   ├── amend/             # Amend OHIP client
│       │   ├── availability/      # Availability OHIP client
│       │   ├── changelog/         # Change log OHIP client
│       │   ├── checkin/           # Check-in OHIP client
│       │   ├── eckoh/             # Eckoh payment client
│       │   ├── frontdesk/         # Front desk OHIP client
│       │   ├── hotel/             # Hotel info OHIP client
│       │   ├── lov/               # List of values OHIP client
│       │   ├── ohip/              # Shared OHIP base client utilities
│       │   ├── opera/             # Opera-specific client helpers
│       │   ├── packages/          # Packages OHIP client
│       │   ├── preferences/       # Preferences client
│       │   ├── profile/           # Profile OHIP client
│       │   ├── rates/             # Rates OHIP client
│       │   ├── reservation/       # Reservation OHIP client
│       │   ├── roomallocation/    # Room allocation OHIP client
│       │   ├── rules/             # Rules Agent client
│       │   ├── token/             # OAuth2 token management client
│       │   ├── udfs/              # UDFs OHIP client
│       │   └── utils/             # Client utilities
│       └── controller/            # Inbound REST controllers
│           ├── amend/
│           ├── availability/
│           ├── changelog/
│           ├── checkin/
│           ├── deposit/
│           ├── eckoh/
│           ├── hotel/
│           ├── lov/
│           ├── opera/
│           ├── packages/
│           ├── preferences/
│           ├── profile/
│           ├── rates/
│           ├── reservation/
│           ├── roomallocation/
│           ├── udfs/
│           └── validation/
├── ErrorCode.java                 # Application error code enum
└── OhipAdapterServiceApplication.java  # Spring Boot entry point
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring is done in `InfrastructureBeanConfig`
- **Ports are interfaces** — primary ports define inbound use cases, secondary ports define outbound contracts
- **Each domain capability has matching controller → port → logic → client layers**
- **Infrastructure adapters are Spring components** — annotated with `@Component`, `@Configuration`
- **Mappers use MapStruct** — annotation processor with Lombok binding
- **Models use Lombok** — `@Builder`, `@Data`, `@Value` for DTOs
- **OpenAPI-generated models** go in `*.generated.models` packages (excluded from coverage/mutation)
- **OAuth2 token handling** is centralised in the `client/token/` package

## OpenAPI Specifications

Multiple Opera OHIP API specs live in `src/main/resources/openapi/` and are used for model code generation:
- `operareservationsapi.yaml` / `operareservationsv0api.yaml`
- `operarateapi.yaml` / `operaratev0api.yaml`
- `operacashieringapi.yaml`
- `operacloudinventoryapi.yaml`
- `operacloudcrmapi.yaml`
- `operacloudlistofvaluesmanagement.yaml`
- `operaenterpriseapi.yaml`
- `operahotelconfigv0api.yaml`
- `rules-agent-openapi.yaml` / `rules-agent-room-substitution-openapi.yaml`

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — WireMock-backed controller tests (MockWebServer for WebClient)
- **ArchUnit** — Enforces hexagonal architecture boundaries
- **Contract tests** — Spring Cloud Contract for API verification (`ContractVerifierBaseTest`)
- **Coding conventions** — Whitbread `coding-convention-rules` library

