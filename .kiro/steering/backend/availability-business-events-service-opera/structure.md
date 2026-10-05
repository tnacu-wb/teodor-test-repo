---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/availability-business-events-service-opera/**"
---

# Project Structure

## Architecture

This service follows **Hexagonal Architecture** (Ports & Adapters). The domain layer defines business logic and port interfaces, while the infrastructure layer provides Spring-based implementations for database access, external API calls, and WebSocket subscriptions.

```
src/main/java/uk/co/whitbread/avail/business/events/
├── domain/                        # Pure business logic, no framework dependencies
│   ├── logic/                     # Application runners and use-case orchestration
│   │   └── SubscribeBusinessEventsColdStartSvc.java  # Cold-start event subscription runner
│   ├── model/                     # Domain models
│   │   └── feature/               # Feature flag wrapper models
│   └── ports/
│       └── secondary/             # Outbound port interfaces
│           ├── ContentOutPort.java
│           ├── HotelAvailabilityDbBatchPort.java
│           ├── OcdAdapterOutPort.java
│           └── OperaAuthenticationPort.java
├── infrastructure/                # Framework-specific adapters
│   ├── adapters/                  # Port implementations
│   │   ├── HotelAvailabilityDbBatchService.java   # Core event processing logic
│   │   └── OperaAuthenticationService.java        # OAuth2 token management
│   ├── client/                    # Outbound API/WebSocket clients
│   │   ├── content/               # Content Entity Service client
│   │   │   ├── ContentOutPortImpl.java
│   │   │   ├── exception/
│   │   │   └── service/
│   │   │       ├── ContentClient.java
│   │   │       └── properties/
│   │   ├── ocd/                   # OCD Adapter Service client
│   │   ├── EventSubscriptionWebSocketClient.java  # OHIP GraphQL subscription client
│   │   ├── OperaRestClient.java                   # Opera REST API client
│   │   └── WebSocketGraphqlClientProvider.java    # WebSocket client factory
│   ├── config/                    # Spring configuration
│   │   ├── datasource/            # Multi-datasource configuration
│   │   ├── BeanConfiguration.java
│   │   ├── CustomUnleashConfiguration.java
│   │   ├── OhipGraphQlMockProperties.java
│   │   ├── OperaProperties.java
│   │   ├── WebClientConfig.java
│   │   └── WebConfig.java
│   ├── entity/                    # JPA entities
│   │   ├── HotelEntity.java
│   │   ├── ProcessedEventEntity.java
│   │   ├── RatePlanEntity.java
│   │   └── RoomEntity.java
│   ├── mapper/                    # Event-to-entity mapping logic
│   │   ├── ApplyDailyRatesMapper.java
│   │   ├── ProcessedEventMapper.java
│   │   ├── RateRestrictionsMapper.java
│   │   └── SummaryTotalMapper.java
│   ├── model/                     # Infrastructure DTOs
│   │   ├── enums/                 # Enum types
│   │   ├── opera/                 # Opera event models (EventHeader, etc.)
│   │   ├── BusinessEventType.java
│   │   ├── OauthTokenResponse.java
│   │   ├── RateDataElement.java
│   │   ├── RateDataElementValue.java
│   │   ├── RoomDataElement.java
│   │   ├── SelectAllFromHotelAcRoomAndRateResponse.java
│   │   └── SummaryTotalEventValues.java
│   ├── repository/                # JPA repositories
│   │   ├── HotelEntityJpaRepository.java
│   │   ├── HotelEntityRepository.java
│   │   ├── ProcessedEventJpaRepository.java
│   │   ├── RateEntityJpaRepository.java
│   │   └── RoomEntityJpaRepository.java
│   └── utils/                     # Infrastructure utilities
│       ├── HashingUtils.java
│       ├── OperaSubscriptionErrors.java
│       └── WebClientUtils.java
└── AvailabilityBusinessEventApplication.java  # Spring Boot entry point
```

## Key Conventions

- **Domain layer has no Spring annotations** — bean wiring is done in `BeanConfiguration`
- **Ports are interfaces** — secondary ports define outbound contracts (no primary/inbound REST controllers since this is an event consumer)
- **No REST controllers** — this service is event-driven, subscribing via WebSocket on startup (ApplicationRunner)
- **Event processing is switch-based** — `HotelAvailabilityDbBatchService.processDbBatchUpdate()` dispatches by event type
- **Mappers are static utility classes** — not MapStruct; manual mapping from Opera event models to JPA entities
- **Models use Lombok** — `@Data`, `@Builder`, `@AllArgsConstructor` for DTOs and entities
- **OpenAPI-generated models** for Content Entity Service and OCD Adapter client responses
- **OAuth2 token handling** is centralised in `OperaAuthenticationService`
- **Offset tracking** via `ProcessedEventEntity` enables subscription resumption after restarts

## OpenAPI Specifications

Located in `src/main/resources/openapi/`:
- `content-entity-service-openApi.yaml` — Content service models for global config
- `ocd-adapter-service-openApi.yaml` — OCD adapter models for tax calculation

## Testing Patterns

- **Unit tests** — Mockito-based, testing mappers, batch service logic, and clients
- **Integration tests** — Testcontainers (PostgreSQL) for repository layer
- **H2 in-memory** — Fallback for simpler data layer tests
- **Schema-based setup** — `src/test/resources/schema.sql` initialises test database
