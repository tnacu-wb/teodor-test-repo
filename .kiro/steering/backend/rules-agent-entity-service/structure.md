---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/rules-agent-entity-service/**"
---

# Project Structure

Hexagonal architecture (ports & adapters) with in-memory caching.

```
src/main/java/uk/co/whitbread/rules/agent/
├── domain/
│   ├── constants/          # Rule-related constants
│   ├── exception/          # Domain exceptions and error codes
│   ├── logic/              # InPort implementations (business logic)
│   ├── model/
│   │   ├── in/             # Request models
│   │   ├── out/            # Response/domain models
│   │   └── validation/     # Self-validation utilities
│   └── ports/
│       ├── primary/        # Inbound ports (use cases)
│       └── secondary/      # Outbound ports (repository interfaces)
├── infrastructure/
│   ├── config/             # Spring bean configuration, scheduler config
│   ├── repository/         # JPA repositories + in-memory cache implementations
│   │   ├── mapper/         # MapStruct entity mappers
│   │   └── model/          # JPA entity classes
│   ├── rest/controller/    # REST controllers
│   │   ├── documentation/  # OpenAPI annotations
│   │   ├── mapper/         # DTO mappers
│   │   ├── model/          # Request/Response DTOs
│   │   └── validation/     # Custom validators
│   └── scheduler/          # Periodic cache refresh scheduler
└── RulesAgentServiceApplication.java
```

## Key Patterns

- Each rule type has its own InPort, OutPort, Repository, Controller, and Mapper
- Repositories implement caching by loading all data into ConcurrentHashMaps on startup
- Scheduler periodically calls `loadRulesInMemory()` to refresh caches from DB
- Contract tests verify API responses using Spring Cloud Contract + WireMock
