---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/spending-entity-service/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer is framework-agnostic and communicates with infrastructure through port interfaces.

```
src/main/java/uk/co/whitbread/spending/
├── SpendingServiceApplication.java        # Spring Boot entry point
├── domain/                                # Pure business logic, no framework dependencies
│   ├── logic/                             # Port implementations (use cases)
│   │   ├── SpendingInPortImpl.java        # Spending operations orchestrator
│   │   └── ReportingInPortImpl.java       # CSV report generation
│   ├── model/
│   │   ├── in/                            # Inbound domain models (requests, enums)
│   │   │   └── worldline/                 # Worldline-specific models
│   │   ├── out/                           # Outbound domain models (responses)
│   │   │   ├── worldline/                 # Worldline response models
│   │   │   ├── pibaaccountservice/        # PIBA Account Service response models
│   │   │   └── cdh/                       # CDH response models
│   │   └── validation/                    # Domain validation utilities
│   ├── ports/
│   │   ├── primary/                       # Inbound ports
│   │   │   ├── SpendingInPort.java        # Spending use cases
│   │   │   └── ReportingInPort.java       # Reporting use cases
│   │   └── secondary/                     # Outbound ports
│   │       ├── WorldlineOutPort.java      # Worldline integration
│   │       ├── PibaAccountServiceOutPort.java  # PIBA Account Service
│   │       ├── CdhOutPort.java            # CDH integration
│   │       └── EmployeeSpendOutPort.java  # CDH Adapter Service
│   ├── exceptions/                        # Domain exception classes
│   └── utils/                             # Domain constants
├── infrastructure/                        # Framework-specific adapters
│   ├── config/                            # Spring configuration
│   │   ├── InfrastructureBeanConfig.java  # Bean wiring for domain logic
│   │   ├── SpendingRedisConfig.java       # Redis cluster configuration
│   │   ├── SecurityConfig.java           # Security configuration
│   │   ├── WebConfig.java                # Web/CORS configuration
│   │   └── WorldlineProperties.java      # Worldline config properties
│   └── rest/
│       ├── client/                        # Outbound REST adapters
│       │   ├── worldline/                 # Worldline Feign client + port impl
│       │   ├── pibaaccountservice/        # PIBA Account Service Feign client
│       │   ├── cdh/                       # CDH REST client + mappers
│       │   ├── cdhadapterservice/         # CDH Adapter Feign client
│       │   └── config/                    # Feign error decoder config
│       ├── controller/
│       │   └── spending/                  # REST controller layer
│       │       ├── SpendingController.java # Main controller
│       │       ├── SpendingApi.java       # OpenAPI-annotated interface
│       │       ├── mapper/                # Request/response DTO mappers
│       │       └── model/                 # Controller DTOs (in/out)
│       └── utils/                         # REST utilities (IP extraction)
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring is done in `InfrastructureBeanConfig`
- **Ports are interfaces** — primary ports define inbound use cases, secondary ports define outbound contracts
- **Infrastructure adapters are Spring components** — annotated with `@Component`, `@Configuration`
- **Controller implements API interface** — `SpendingController` implements `SpendingApi` (OpenAPI annotations)
- **Mappers use MapStruct** — interface-based mappers in `mapper/` packages at each layer boundary
- **Models use Lombok** — `@Data`, `@Builder`, `@RequiredArgsConstructor` for DTOs
- **Security via `@PreAuthorize`** — method-level authorization on controller endpoints
- **Content negotiation** — `accountSpending` endpoint returns JSON or CSV based on Accept header

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **ArchUnit** — Enforces hexagonal architecture boundaries (via `coding-convention-rules`)
- **Model validation tests** — Jakarta Bean Validation on request DTOs
- **No contract tests** — this service is a consumer, not a provider
