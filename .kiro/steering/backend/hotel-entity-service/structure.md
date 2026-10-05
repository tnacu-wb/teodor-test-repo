---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/hotel-entity-service/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces.

```
src/main/java/uk/co/whitbread/
├── HotelEntityServiceApplication.java
├── domain/                            # Pure business logic, no framework dependencies
│   ├── constants/                     # Domain constants
│   ├── exceptions/                    # Domain-specific exceptions
│   ├── logic/                         # Port implementations (use cases)
│   │   ├── HotelAvailabilitiesInPortImpl
│   │   ├── HotelAvailabilitiesInPortV2Impl
│   │   ├── HotelAvailabilityInPortImpl
│   │   ├── AvailabilityCacheSearchInPortImpl
│   │   ├── DistanceFromSearchInPortImpl
│   │   ├── GroupBookingInPortImpl
│   │   ├── HotelInfoInPortImpl
│   │   ├── ListOfValuesInPortImpl
│   │   ├── PackagesInPortImpl
│   │   ├── RulesAgentInPortImpl
│   │   └── (utility classes: sorting, MLOS, city tax, soft bundles)
│   ├── model/                         # Domain models organised by subdomain
│   │   ├── availability/              # Availability request/response models
│   │   ├── availabilitycache/         # Cache-specific models
│   │   ├── basket/                    # Basket integration models
│   │   ├── distance/                  # Distance calculation models
│   │   ├── feature/                   # Feature flag models
│   │   ├── groupbooking/              # Group booking models
│   │   ├── hotel/                     # Hotel info models
│   │   ├── lov/                       # List-of-values models
│   │   ├── migrationstatus/           # HMS migration status models
│   │   ├── opera/                     # Opera/OHIP models
│   │   ├── packages/                  # Packages/extras models
│   │   ├── promotion/                 # Promotion models
│   │   ├── rulesagent/                # Rules agent models
│   │   ├── searchrules/               # AEM search rules models
│   │   ├── srp/                       # SRP aggregation models
│   │   └── validation/                # Validation models
│   └── ports/
│       ├── primary/                   # Inbound ports (driven by controllers)
│       └── secondary/                 # Outbound ports (drive external services)
├── infrastructure/                    # Framework-specific adapters
│   ├── config/                        # Spring bean wiring, properties, security
│   │   ├── cache/                     # Redis configuration
│   │   └── snowdrop/                  # Snowdrop client config
│   └── rest/
│       ├── client/                    # REST client adapters (WebClient)
│       │   ├── accounts/              # Company/account service client
│       │   ├── availability/          # OHIP availability client
│       │   ├── availabilitycache/     # Availability cache client
│       │   ├── availabilitycachev1/   # Availability cache v1 client
│       │   ├── basket/                # Basket service client
│       │   ├── cache/                 # Cache service client
│       │   ├── content/               # Content service client
│       │   ├── distance/              # Distance/Snowdrop client
│       │   ├── groupbooking/          # Dynamics 365 client
│       │   ├── hotel/                 # Hotel info/OHIP client
│       │   ├── hotelsearch/           # Hotel search client
│       │   ├── lov/                   # List-of-values client
│       │   ├── microsoftoauth/        # Microsoft OAuth token client
│       │   ├── migrationstatus/       # Migration status client
│       │   ├── ohip/                  # OHIP adapter client
│       │   ├── opera/                 # Opera client
│       │   ├── packages/              # Packages client
│       │   ├── promotion/             # Promotion service client
│       │   ├── rulesagent/            # Rules agent client
│       │   └── utils/                 # Client utilities
│       └── controller/                # REST controllers (inbound adapters)
│           ├── availability/          # Availability endpoints
│           ├── distance/              # Distance endpoints
│           ├── groupbooking/          # Group booking endpoints
│           ├── hotel/                 # Hotel info endpoints
│           ├── lov/                   # List-of-values endpoints
│           ├── packages/              # Packages endpoints
│           ├── srp/                   # SRP aggregation endpoints
│           └── validation/            # Validation endpoints
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring is done in `InfrastructureBeanConfig`
- **Ports are interfaces** — primary ports define inbound use cases, secondary ports define outbound contracts
- **Infrastructure adapters are Spring components** — annotated with `@Component`, `@Configuration`
- **Mappers use MapStruct** — interface-based mappers in client subdirectories
- **Models use Lombok** — `@Builder`, `@Data`, `@Value` for DTOs
- **OpenAPI-generated models** go in `*.generated.models` packages (excluded from coverage/mutation)
- **Feature flags** evaluated via Unleash with per-channel fallback defaults

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — MockWebServer/WireMock for HTTP client tests, Embedded Redis for cache tests
- **ArchUnit** — Enforces hexagonal architecture boundaries
- **Contract tests** — Spring Cloud Contract for API verification

