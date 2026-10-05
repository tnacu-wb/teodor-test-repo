---
inclusion: fileMatch
fileMatchPattern: "backend/manage-modify/services/hotel-reservation-entity-service/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces. Primary ports define inbound use cases (driven by REST controllers), and secondary ports define outbound contracts (implemented by REST clients and cache adapters).

```
src/main/java/uk/co/whitbread/reservation/
├── domain/                            # Pure business logic, no framework dependencies
│   ├── constants/                     # Domain constants (reservation codes, reason for stay)
│   ├── exceptions/                    # Domain-specific exceptions (amend, cancel, not found)
│   ├── logic/                         # Port implementations (use cases)
│   │   └── utils/                     # Domain logic utilities
│   ├── model/                         # Domain models
│   │   ├── amend/                     # Amendment request/response models
│   │   ├── availability/              # Availability domain models
│   │   ├── basket/                    # Basket domain models
│   │   ├── cache/                     # Cache key/value models
│   │   ├── feature/                   # Feature flag models
│   │   ├── in/                        # Inbound request models
│   │   ├── index/                     # Index/lookup models
│   │   ├── out/                       # Outbound response models
│   │   ├── payment/                   # Payment domain models
│   │   ├── promotion/                 # Promotion domain models
│   │   ├── searchrules/               # Search rules models
│   │   ├── srp/                       # SRP (Single Responsibility) models
│   │   └── validator/                 # Validation models
│   ├── ports/
│   │   ├── primary/                   # Inbound ports (use case interfaces)
│   │   └── secondary/                 # Outbound ports (external system interfaces)
│   ├── properties/                    # Domain configuration properties
│   └── utils/                         # Domain utilities (sanitization)
├── infrastructure/                    # Framework-specific adapters
│   ├── config/                        # Spring configuration (Redis, Jackson, Security, Unleash)
│   ├── rest/
│   │   ├── client/                    # Outbound REST client adapters
│   │   │   ├── basket/                # Basket Service client
│   │   │   ├── cache/                 # Redis cache adapter
│   │   │   ├── cdh/                   # CDH Adapter Service client
│   │   │   ├── changelog/             # Change log client
│   │   │   ├── config/                # WebClient configuration
│   │   │   ├── content/               # Content Entity Service client
│   │   │   ├── hotel/                 # Hotel Entity Service client
│   │   │   ├── hotelaccount/          # Hotel Account Service client
│   │   │   ├── ohip/                  # OHIP Adapter Service client
│   │   │   ├── packages/              # Packages client
│   │   │   ├── promotion/             # Promotion Service client
│   │   │   ├── qrcode/               # QR code generation
│   │   │   └── rules/                 # Rules Agent Service client
│   │   ├── controller/                # Inbound REST controllers
│   │   │   ├── amend/                 # Amendment endpoints
│   │   │   ├── changelog/             # Change log endpoints
│   │   │   ├── qrcode/               # QR code endpoints
│   │   │   ├── reservation/           # Core reservation endpoints
│   │   │   └── universallogin/        # Universal login endpoints
│   │   └── utils/                     # REST utilities
│   └── security/                      # API key validation (aspect-based)
├── ErrorCode.java                     # Top-level error codes
└── HotelReservationEntityServiceApplication.java  # Spring Boot entry point
```

## OpenAPI Code Generation

The service generates model classes from multiple OpenAPI specifications at build time:

| Spec File | Generated Package |
|-----------|------------------|
| `ohip-adapter-service-openApi.yaml` | `uk.co.whitbread.hotel.ohip.adapter.generated.models` |
| `basket-service-openApi.yaml` | `...generated.models.basket` |
| `content-entity-service-openapi.yaml` | `...content.entity.service.generated.models.content` |
| `reservation-locking-service-openApi.yaml` | `...generated.models.locking` |
| `rules-agent-entity-service-openApi.yaml` | `...rules.entity.service.generated.models.agent` |
| `cdh-adapter-service-openApi.yaml` | `...cdh.adapter.service.generated.models.reservationSearch` |
| `hotel-entity-service-openapi.yaml` | `...hotel.entity.service.generated.models.hotel` |
| `hotel-account-service-opera-openApi.yaml` | `...hotel.account.service.generated.models` |
| `promo-service-openapi.yaml` | `...promo.service.generated.models.promotion` |

Generated sources use a `javax` → `jakarta` replacement step during the `generate-sources` phase.

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring is done in `InfrastructureBeanConfig`
- **Ports are interfaces** — primary ports define inbound use cases, secondary ports define outbound contracts
- **Infrastructure adapters are Spring components** — annotated with `@Component`, `@Configuration`
- **Mappers use MapStruct** — interface-based mappers in `mapper/` packages within each client
- **Models use Lombok** — `@Builder`, `@Data`, `@Value` for DTOs
- **OpenAPI-generated models** go in `*.generated.models` packages (excluded from coverage/mutation)
- **Multi-tenant Auth0** — security config supports CCUI, PI BB, and Distribution tenants
- **API key protection** — custom `@ApiKeyProtected` annotation with AOP aspect for universal login endpoints
- **Feature flags via Unleash** — progressive rollouts controlled by feature flag keys in `application.yml`
- **Redis caching** — cluster mode with SSL, Lettuce client, adaptive topology refresh

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Integration tests** — Spring Boot test with WireMock stubs for external services
- **Contract tests** — Spring Cloud Contract for API verification (base class: `ContractVerifierBaseTest`)
- **ArchUnit** — Enforces hexagonal architecture boundaries via `coding-convention-rules`
- **Controller coverage** — JaCoCo merges unit + integration test coverage for controllers
