---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/content-entity-service/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces.

```
src/main/java/uk/co/whitbread/content/
├── domain/                        # Pure business logic, no framework dependencies
│   ├── logic/                     # Port implementations (use cases)
│   │   └── mapper/                # Domain-level MapStruct mappers
│   ├── model/                     # Domain models organised by subdomain
│   │   ├── apps/                  # Mobile apps content models
│   │   ├── booking/               # Booking flow content models
│   │   ├── cookies/               # Cookie policy models
│   │   ├── countries/             # Country list models
│   │   ├── dlp/                   # Dynamic landing page models
│   │   ├── feature/               # Feature flag models
│   │   ├── footer/                # Footer content models
│   │   ├── globalconfig/          # Global configuration models
│   │   ├── hotel/                 # Hotel information models
│   │   ├── index/                 # Index/header data models
│   │   ├── inn/                   # Inn Business content models
│   │   ├── labels/                # Localised label models
│   │   ├── meals/                 # Meal/extras models
│   │   ├── note/                  # Business notes models
│   │   ├── pricefinder/           # Price finder models
│   │   ├── promoconfig/           # Promotions config models
│   │   ├── roomtype/              # Room type models
│   │   ├── searchresults/         # Search results models
│   │   ├── seo/                   # SEO metadata models
│   │   └── validation/            # Validation models
│   ├── ports/
│   │   ├── primary/               # Inbound ports (driven by controllers)
│   │   └── secondary/             # Outbound ports (drive external systems)
│   └── utils/                     # Domain utility classes (e.g. SanitizingUtils)
├── infrastructure/                # Framework-specific adapters
│   ├── config/                    # Spring bean wiring, cache, Redis, WebClient config
│   │   └── component/             # Spring components for config
│   ├── rest/
│   │   ├── client/                # REST client adapters (WebClient)
│   │   │   ├── aem/               # AEM content client
│   │   │   ├── booking/           # Booking content client
│   │   │   ├── content/           # Generic content client
│   │   │   ├── cookies/           # Cookie policies client
│   │   │   ├── countries/         # Countries client
│   │   │   ├── footer/            # Footer client
│   │   │   ├── inn/               # Inn Business content client
│   │   │   ├── meals/             # Meals client
│   │   │   ├── note/              # Business notes client
│   │   │   ├── ohip/              # OHIP adapter client
│   │   │   ├── roomtype/          # Room type client
│   │   │   ├── snowdrop/          # Snowdrop search client
│   │   │   └── utils/             # Client utilities
│   │   └── controller/            # REST controllers organised by domain
│   │       ├── apps/              # Apps content endpoints
│   │       ├── booking/           # Booking content endpoints
│   │       ├── cookies/           # Cookie policy endpoints
│   │       ├── countries/         # Countries endpoints
│   │       ├── dlp/               # DLP endpoints
│   │       ├── footer/            # Footer endpoints
│   │       ├── globalconfig/      # Global config endpoints
│   │       ├── header/            # Header endpoints
│   │       ├── hotel/             # Hotel endpoints
│   │       ├── inn/               # Inn Business endpoints
│   │       ├── labels/            # Labels endpoints
│   │       ├── meal/              # Meal endpoints
│   │       ├── model/             # Shared controller models
│   │       ├── note/              # Business notes endpoints
│   │       ├── pricefinder/       # Price finder endpoints
│   │       ├── promoconfig/       # Promo config endpoints
│   │       ├── roomtype/          # Room type endpoints
│   │       ├── searchresults/     # Search results endpoints
│   │       └── seo/               # SEO endpoints
│   └── scheduler/                 # Scheduled tasks
│       ├── HotelOpeningSoonScheduler  # Daily refresh of opening-soon hotels
│       └── HotelSearchFiltersScheduler # Weekly refresh of search filters
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring is done in `InfrastructureBeanConfig`
- **Ports are interfaces** — primary ports define inbound use cases, secondary ports define outbound contracts
- **Infrastructure adapters are Spring components** — annotated with `@Component`, `@Configuration`
- **Mappers use MapStruct** — interface-based mappers in `mapper/` packages
- **Models use Lombok** — `@Builder`, `@Data`, `@Value` for DTOs
- **Controllers are organised by subdomain** — each domain area gets its own controller package
- **REST clients mirror the domain structure** — one client package per external content source

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — WireMock for external service stubs
- **ArchUnit** — Enforces hexagonal architecture boundaries
- **Contract tests** — Spring Cloud Contract for API verification (base class: `ContractVerifierBaseTest`)
- **Coding convention tests** — Whitbread `coding-convention-rules` library

