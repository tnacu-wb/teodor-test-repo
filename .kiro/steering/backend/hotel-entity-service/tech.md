---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/hotel-entity-service/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace)
- Spring Boot 4.0.6 (with Spring Cloud, Spring WebFlux, Spring Data Redis)

## Build System

- Maven (inherits from `digital-monorepo-service-parent`)
- Parent artifact: `uk.co.whitbread:digital-monorepo-service-parent:${revision}`
- Service version: `2.0.0`

## Key Libraries

| Library | Purpose |
|---------|---------|
| Spring Boot Starter Web Services | REST API framework |
| Spring Boot Starter WebFlux | Reactive HTTP client (WebClient) for downstream calls |
| Spring Boot Starter Data Redis | Redis caching (Lettuce client, cluster mode) |
| Spring Boot Starter Validation | Bean validation (Jakarta) |
| Spring Cloud Commons | Service discovery and load balancing |
| Spring Cloud Bootstrap | Externalised configuration |
| Micrometer + Brave | Distributed tracing (W3C propagation) |
| Micrometer Prometheus | Metrics export |
| Lombok | Boilerplate reduction (`@Slf4j`, `@RequiredArgsConstructor`, `@Builder`) |
| MapStruct | Object mapping between layers |
| Jackson + jackson-databind-nullable | JSON serialization/deserialization |
| OpenAPI Generator (7.10.0) | Model generation from OpenAPI specs |
| SpringDoc OpenAPI | API documentation (Swagger UI) |
| Unleash (springboot-unleash-starter) | Feature flag management |
| commons-entity-exceptions | Shared Whitbread exception handling |
| commons-logging | Shared Whitbread structured logging |
| common-auth0 | Auth0/JWT multi-tenant authentication |
| commons-validator | Input validation utilities |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Cloud Contract | Contract verification tests |
| WireMock (wiremock-spring-boot) | HTTP service mocking for integration tests |
| MockWebServer (OkHttp) | WebClient integration tests |
| Embedded Redis | Redis integration tests |
| JaCoCo | Code coverage (unit + integration merged report) |
| PIT (Pitest 1.16.1) | Mutation testing |
| ArchUnit | Architecture rule enforcement |
| Whitbread `coding-convention-rules` | Custom coding convention checks |

## Code Quality

- **Checkstyle**: Google style (`google-checkstyle.xml`), fails on warnings
- **SonarQube**: Coverage exclusions for models, config, mappers, exceptions, generated code

## OpenAPI Code Generation

Models are generated from multiple downstream service specs:
- `ohip-adapter-service-openApi.yaml` → `uk.co.whitbread.hotel.ohip.adapter.generated.models`
- `rules-agent-entity-service-openApi.yaml` → `uk.co.whitbread.rules.agent.generated.models`
- `cache-service-openapi.yaml` → `uk.co.whitbread.content.domain.model.hotel.out`
- `content-entity-service-openapi.yaml` → `uk.co.whitbread.hotel.content.generated.models`
- `basket-service-openApi.yaml` → `uk.co.whitbread.basket.generated.models`
- `promo-service-openapi.yaml` → `uk.co.whitbread.promo.generated.models.promotion`

## Common Commands

```bash
# Build and run tests (from backend/ directory)
cd backend && ./mvnw clean install -pl discover-search/services/hotel-entity-service -am

# Run locally (requires 'local' profile)
cd backend && ./mvnw spring-boot:run -pl discover-search/services/hotel-entity-service -Dspring.profiles.active=local

# Run checkstyle only
cd backend && ./mvnw checkstyle:check -pl discover-search/services/hotel-entity-service

# Run mutation tests
cd backend && ./mvnw org.pitest:pitest-maven:mutationCoverage -pl discover-search/services/hotel-entity-service
```

## Application Configuration

- Port: `9102`
- Actuator base path: `/v1/hotels/actuator`
- Profiles: `local`, `opera-dev`, `opera-qa`, `opera-perf`
- Tracing: W3C propagation with baggage fields (`x-amzn-trace-id`, `flow-code`, `wb-session-id`)
- Cache: Redis cluster with Lettuce client, availability TTL 5 minutes, on-sale flag TTL 1 hour
- Auth: Multi-tenant JWT (CCUI Tenant, PI BB Tenant)
- Feature flags: Unleash with per-feature channel-specific fallbacks

