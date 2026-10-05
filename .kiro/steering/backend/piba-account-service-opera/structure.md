---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/piba-account-service-opera/**"
---

# Project Structure

## Architecture

This service follows a **layered architecture** with clear separation between controllers, services, clients, and models. It uses Spring MVC for REST endpoints and OpenFeign for outbound HTTP calls.

```
src/main/java/uk/co/whitbread/piba/account/
├── PibaAccountServiceApplication.java   # Spring Boot entry point
├── client/                              # Feign clients for external services
│   ├── PibaGuidClient.java             # PIBA GUID resolution service
│   ├── PibaGuidClientFallbackFactory.java
│   ├── WorldlineClient.java            # Worldline REST API client
│   └── WorldlineClientFallbackFactory.java
├── config/                              # Spring configuration classes
│   ├── BeanConfig.java                 # General bean definitions
│   ├── PibaAccountRedisConfig.java     # Redis cluster configuration
│   ├── PibaGuidErrorDecodeConfig.java  # Feign error decoder for PIBA GUID
│   ├── SecurityConfig.java            # Auth0 security configuration
│   ├── ThreadPoolExecutorsConfig.java  # Async thread pool config
│   └── WebConfig.java                 # Web/CORS configuration
├── controller/                          # REST API controllers
│   ├── PibaAccountController.java      # V1 endpoints (/piba/account/)
│   └── PibaAccountControllerV2.java    # V2 endpoints (/v2/piba/account/)
├── converter/                           # Object converters/transformers
├── diagrams/                            # Architecture diagrams (code-as-docs)
├── exception/                           # Custom exception classes
├── filter/                              # Servlet filters
├── mapper/                              # MapStruct mappers
├── model/                               # Request/response DTOs and domain models
│   └── enums/                          # Enum types (e.g., Scheme: GB, DE)
├── properties/                          # Configuration property classes
├── service/                             # Business logic layer
│   ├── CdhRegistrationService.java     # CDH tethered user registration
│   ├── InvoicesService.java            # Invoice operations
│   ├── PibaAccountService.java         # Main account service (orchestrator)
│   ├── PibaGuidServiceClient.java      # PIBA GUID service wrapper
│   ├── PropertiesLoader.java           # Dynamic properties loading
│   └── WorldLineService.java           # Worldline SOAP/REST operations
├── util/                                # Utility classes
│   ├── LogUtils.java                   # Logging sanitization utilities
│   ├── TransactionFileWriter.java      # CSV/XLS file generation
│   └── WorldlineUtils.java            # Worldline header/IP utilities
└── validation/                          # Request validators
    ├── InvoicesCriteriaValidator.java
    ├── PibaGuidValidator.java
    ├── TransactionsCriteriaValidator.java
    ├── WorldLineAccountBalanceResponseValidator.java
    └── WorldLineAccountTransactionsResponseValidator.java
```

## Key Conventions

- **Controllers are thin** — delegate immediately to service layer
- **Services contain business logic** — orchestrate calls to Worldline, PIBA GUID, and CDH
- **Feign clients with fallback factories** — circuit breaker pattern via Resilience4j
- **Models use Lombok** — `@Data`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`
- **MapStruct for object mapping** — interface-based mappers between layers
- **Validators are custom classes** — not annotation-based; validate Worldline responses and request criteria
- **Multi-scheme support** — GB and DE handled via `Scheme` enum with separate Worldline credentials

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class
- **Contract tests** — Spring Cloud Contract with Groovy DSL in `src/test/resources/contracts/`
- **Contract base class** — `ContractVerifierBaseTest` in the controller test package
- **WireMock** — Used for stubbing external HTTP calls in tests
- **Random Beans** — Test data generation via `io.github.benas:random-beans`
