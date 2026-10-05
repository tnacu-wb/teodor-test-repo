---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/cdh-adapter-service/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces.

```
src/main/java/uk/co/whitbread/cdh/
├── domain/                        # Pure business logic, no framework dependencies
│   ├── constants/                 # Domain constants (e.g. Currency)
│   ├── logic/                     # Port implementations (use cases)
│   │   └── spending/              # Employee spend report logic
│   ├── model/                     # Domain models grouped by capability
│   │   ├── account/               # Company & employee account models
│   │   │   ├── in/                # Inbound request models (search criteria)
│   │   │   └── out/               # Outbound response models (company, employee)
│   │   │       └── employee/      # Employee-specific response models
│   │   ├── booking/               # Reservation & invoice models
│   │   │   ├── in/                # Reservation search criteria, invoice requests
│   │   │   └── out/               # Reservation search results, invoice responses
│   │   ├── feature/               # Feature toggle models (Unleash wrapper)
│   │   ├── report/                # Company report models
│   │   │   ├── in/                # Report request models
│   │   │   └── out/               # Report response models
│   │   └── spending/              # Employee spend models
│   └── ports/
│       ├── primary/               # Inbound ports (driven by controllers)
│       └── secondary/             # Outbound ports (drive CDH API clients)
├── infrastructure/                # Framework-specific adapters
│   ├── config/                    # Spring bean wiring, Unleash configuration
│   ├── rest/
│   │   ├── client/                # Outbound REST client adapters
│   │   │   ├── account/           # Account service clients
│   │   │   │   ├── company/       # Company API client (WebClient)
│   │   │   │   └── employee/      # Employee API client (WebClient)
│   │   │   ├── cdh/               # CDH base client, config, exceptions
│   │   │   ├── config/            # WebClient and Jackson configuration
│   │   │   ├── oauth/             # OAuth2 token management (Feign client)
│   │   │   ├── properties/        # URI header properties
│   │   │   ├── report/            # Management information report client
│   │   │   ├── reservation/       # Reservation search clients (V1, V2, V3)
│   │   │   ├── spending/          # Employee spend report client
│   │   │   └── util/              # Client utilities
│   │   └── controller/            # Inbound REST controllers
│   │       ├── account/           # Company & Employee controllers
│   │       ├── report/            # Report controller
│   │       ├── reservationsearch/ # Reservation search controller
│   │       ├── spending/          # Employee spend report controller
│   │       └── validation/        # Custom validation annotations
│   └── util/                      # Infrastructure utilities
└── CdhAdapterServiceApplication.java  # Spring Boot entry point
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring is done in infrastructure config
- **Ports are interfaces** — primary ports define inbound use cases, secondary ports define outbound contracts
- **Each domain capability has matching controller → port → logic → client layers**
- **Infrastructure adapters are Spring components** — annotated with `@Component`, `@RestController`
- **Mappers use MapStruct** — annotation processor with Lombok binding
- **Models use Lombok** — `@Builder`, `@Data`, `@Value` for DTOs
- **OAuth2 token handling** is centralised in `infrastructure/rest/client/oauth/` using OpenFeign
- **WebClient** used for CDH API calls (reactive, non-blocking)
- **Custom validators** for date format validation (`@DateFormat`, `@MonthYearFormat`)
- **API base path** — all controllers use `/v1/cdh` prefix
- **Checkstyle** — Google style enforcement (`google-checkstyle.xml`)

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — WireMock/MockWebServer-backed controller tests
- **Contract tests** — Spring Cloud Contract for API verification
- **Coding conventions** — Whitbread `coding-convention-rules` library
