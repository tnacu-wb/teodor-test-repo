---
inclusion: fileMatch
fileMatchPattern: "backend/identity/libs/commons-cdh-lib/**"
---

# Project Structure

## Architecture

`commons-cdh-lib` is a **Spring Boot auto-configured client library** that wraps the Customer Data Hub (CDH) HTTP API. It is consumed as a Maven reactor dependency by services under `backend/*/services/` and is not deployed as a runnable application. There is no `@SpringBootApplication` entry point — instead, configuration classes are picked up by consumers via Spring Boot's auto-configuration mechanism.

The library exposes a **service-facade** layer to consumers (the only public API surface they should call) and keeps the underlying Feign clients, mappers, and configuration internal:

```
src/main/java/uk/co/whitbread/shared/cdh/
├── client/                                # Feign client interfaces (internal)
│   ├── <ResourceName>Client.java          # Feign-annotated interface per CDH endpoint group
│   └── <ResourceName>ClientFallbackFactory.java  # Resilience4j circuit-breaker fallback
├── config/                                # Spring @Configuration classes
│   ├── CdhAutoConfiguration.java          # Top-level auto-configuration entry
│   ├── CdhFeignConfig.java                # Feign decoders / interceptors
│   ├── CdhResilienceConfig.java           # Resilience4j circuit breaker / retry config
│   └── CdhCacheConfig.java                # Redis cache manager wiring
├── mapper/                                # MapStruct mappers (CDH DTO ↔ library model)
├── model/
│   ├── request/                           # Request DTOs sent to CDH
│   └── response/                          # Response DTOs returned from CDH
├── properties/                            # @ConfigurationProperties classes
│   └── CdhProperties.java                 # Endpoint URLs, timeouts, cache TTLs
├── service/                               # Public facade — what consumer services call
│   └── CdhService.java                    # Orchestrates client + cache + mapping
└── exception/                             # Library-specific exception types

src/main/resources/
└── META-INF/spring/
    └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
                                            # Declares CdhAutoConfiguration to consumers

src/test/java/uk/co/whitbread/shared/cdh/   # Mirrors main package layout
├── client/                                # WireMock-backed Feign client tests
├── service/                               # Mockito-based service tests
└── mapper/                                # MapStruct mapper tests
```

> **Note:** the package layout above is the anticipated shape based on the upstream repository's purpose as a CDH wrapper. The authoritative layout becomes whatever lands under `backend/identity/libs/commons-cdh-lib/src/main/java/` when the migration import (task 2 of `commons-cdh-lib-monorepo-migration`) completes. If the imported tree differs (for example, a different root package or additional subpackages), refresh this file as part of the import PR.

## Key Conventions

- **Auto-configured for consumers** — `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` lists `CdhAutoConfiguration`, so adding the library as a dependency wires the beans without requiring `@Import` or `@ComponentScan` changes in the consumer.
- **Service facade is the only public API** — consumers depend on `CdhService` (and its DTOs), not on the underlying Feign client. Feign interfaces, mappers, and config classes are internal to the library.
- **Feign clients use fallback factories** — `<Client>FallbackFactory` provides Resilience4j circuit-breaker behaviour. Fallbacks log and surface a typed exception rather than returning `null`.
- **Cache annotations on service methods** — `@Cacheable` on read paths backed by the consumer's Redis cluster (Lettuce client). TTLs are set via `CdhProperties`, not hard-coded in annotations.
- **Jakarta EE namespace** — all `javax.*` Java EE imports were migrated to `jakarta.*` during the monorepo import (per Requirement 8.6); only `javax.annotation.processing` is allowed to remain on the `javax.*` namespace.
- **Lombok for boilerplate** — `@Data`, `@Builder`, `@RequiredArgsConstructor` on DTOs and service classes. Lombok is inherited from the library parent's `pluginManagement` — the library does not redefine `annotationProcessorPaths`.
- **MapStruct for object mapping** — interface-based mappers between transport DTOs and the library's model types. Mappers are the conversion seam between Feign responses and what the facade returns to consumers.
- **`@ConfigurationProperties` over `@Value`** — all tunables (URLs, timeouts, cache TTLs) live on `CdhProperties` so consumers configure them under a single prefix in their `application.yml`.
- **No `<distributionManagement>`** — the library cannot be deployed from the monorepo. The standalone upstream repository continues to publish to Nexus for external consumers.

## Testing Patterns

- **Unit tests** — Mockito-based, one test class per source class. `service/` and `mapper/` packages are the heaviest test surface.
- **Contract verification via WireMock** — Feign client behaviour (request shape, response decoding, error handling, fallback invocation) is verified by stubbing CDH responses with WireMock rather than hitting a live CDH instance.
- **JaCoCo coverage** — inherited from the library parent's `pluginManagement`, with `<sonar.coverage.exclusions>` set to `**/config/**` (preserved from the upstream repository) so Spring `@Configuration` classes do not skew the coverage metric.
- **Checkstyle** — same `google-checkstyle.xml` ruleset applied to services; zero violations required to pass the library's own quality gate.
- **SonarCloud** — the library has its own project keyed `whitbread-eos_digital-monorepo_commons-cdh-lib`. Service Sonar scans exclude `backend/*/libs/**` so library code is not double-attributed to consumer projects.
- **No integration tests against live CDH** — the library is consumer-driven; end-to-end CDH integration is exercised by consumer services, not by the library itself.
