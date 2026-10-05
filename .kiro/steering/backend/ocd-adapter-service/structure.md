---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/ocd-adapter-service/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces.

```
src/main/java/uk/co/whitbread/ocd/
├── domain/
│   ├── logic/                         # Port implementations (use cases)
│   ├── model/                         # Domain models
│   │   ├── availability/              # Hotel availability models
│   │   ├── offers/                    # Offer/rate detail models
│   │   ├── tax/                       # Tax information models
│   │   └── validation/                # Self-validation pattern
│   └── ports/
│       ├── primary/                   # Inbound ports (use case interfaces)
│       └── secondary/                 # Outbound ports (OCD API, tax service)
├── infrastructure/
│   ├── config/                        # Spring configuration (Security, WebClient, Redis, OAuth2)
│   └── rest/
│       ├── client/                    # Outbound REST client adapters (OCD, tax)
│       │   ├── ocd/                   # OCD API client with OAuth2
│       │   └── tax/                   # Tax service client
│       └── controller/                # Inbound REST controllers
│           ├── availability/          # Availability endpoints
│           ├── offers/                # Offer detail endpoints
│           ├── tax/                   # Tax info endpoints
│           └── validation/            # Request validators
└── OcdAdapterServiceApplication.java
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring in `infrastructure/config/`
- **Ports are interfaces** — primary ports define use cases, secondary ports define outbound contracts
- **WebClient for HTTP** — via `spring-boot-starter-webclient` for outbound calls to OCD
- **OAuth2 client credentials** — automatic token management for OCD API calls
- **Mappers use MapStruct** — interface-based for domain ↔ DTO conversions
- **ModelMapper** — used for some OCD response transformations
- **Models use Lombok** — `@Builder`, `@Data`, `@Value`
- **Redis caching** — cached OCD responses to reduce external API calls
- **Contract-first API design** — OpenAPI spec under `src/main/resources/openapi/`
- **Generated models** — OCD distribution shop API models generated from spec
- **Replacer plugin** — converts `javax` → `jakarta` in generated sources

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — OkHttp MockWebServer for OCD API stubs
- **Contract tests** — Spring Cloud Contract (base class: `ContractVerifierBaseTest`, suffix: `IT`)
- **ArchUnit** — Enforces hexagonal architecture boundaries via `coding-convention-rules`
- **Request validation tests** — Date format and arrival/departure validators
