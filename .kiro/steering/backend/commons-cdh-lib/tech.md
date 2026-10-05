---
inclusion: fileMatch
fileMatchPattern: "backend/identity/libs/commons-cdh-lib/**"
---

# Tech Stack

## Language & Runtime

- Java 25 (Jakarta EE namespace — migrated from `javax.*` during monorepo cutover)
- Spring Boot 4.0.6 BOM (inherited via library parent; upstream standalone repo was on 4.0.3 and was bumped to the monorepo standard during migration)
- Spring Cloud 2025.1.0 BOM (inherited via library parent)

## Build System

- Maven via wrapper (`./mvnw`) — never use a local Maven installation
- Inherits from `digital-monorepo-library-parent`
- Parent artifact: `uk.co.whitbread:digital-monorepo-library-parent:${revision}` (currently `1.0.0`)
- `<relativePath>../../../parents/library-parent/pom.xml</relativePath>`
- Library coordinates: `uk.co.whitbread.shared:commons-cdh-lib:${revision}`
- Packaging: `jar`
- No `<distributionManagement>` — the monorepo cannot publish this library; releases continue to flow from the standalone upstream repository to Nexus
- Annotation processors: Lombok (inherited from parent `<pluginManagement>`)
- `flatten-maven-plugin` resolves `${revision}` in installed POMs (`resolveCiFriendliesOnly`)

## Key Libraries

All versions are inherited from the library parent's `<dependencyManagement>` — the library POM declares dependencies version-less.

| Library | Purpose |
|---------|---------|
| Spring Boot Starter Web Services | REST API framework |
| Spring Boot Starter WebFlux | Reactive HTTP client (WebClient) for CDH calls |
| Spring Boot Starter Cache | Caching abstraction |
| Spring Data Redis (Lettuce) | Clustered Redis cache backend |
| Spring Cloud OpenFeign | Declarative HTTP clients |
| Spring Cloud Circuit Breaker (Resilience4j) | Fallback handling for Feign clients |
| Feign HttpClient | Apache HttpClient transport for Feign |
| Jackson Databind | JSON serialisation |
| Jackson Datatype JSR-310 | Java 8 date/time JSON binding |
| Apache Commons Lang 3 | General-purpose utilities |
| Whitbread `commons-exceptions` | Shared exception types (managed by parent) |
| Lombok | Boilerplate reduction |

## Testing

| Tool | Purpose |
|------|---------|
| Spring Boot Starter Test | JUnit 5, Mockito, AssertJ |
| Spring Cloud Contract WireMock | HTTP stub server for CDH integration tests (test scope) |
| JaCoCo | Code coverage reporting |

## Code Quality

- **Checkstyle**: Google style (`google-checkstyle.xml` inherited from library parent), fails on warnings
- **SonarCloud**: project key `whitbread-eos_digital-monorepo_commons-cdh-lib` under organisation `whitbread-eos`
  - Library files are scanned by `build-library.yaml` against the library's own SonarCloud project
  - Consumer service Sonar scans exclude `backend/*/libs/**` so library code is not double-attributed

## Common Commands

```bash
# Build only the library (from backend/ directory)
cd backend && ./mvnw clean install -pl identity/libs/commons-cdh-lib -am

# Full reactor build (library + all consumer services)
cd backend && ./mvnw clean install

# Build a consumer service together with the library
cd backend && ./mvnw clean install -pl identity/services/piba-account-service-opera -am
cd backend && ./mvnw clean install -pl identity/services/spending-entity-service -am

# Run only the library's tests
cd backend && ./mvnw test -pl identity/libs/commons-cdh-lib

# Checkstyle on the library
cd backend && ./mvnw checkstyle:check -pl identity/libs/commons-cdh-lib

# Inspect resolution origin (should show `(reactor)` for commons-cdh-lib, not a Nexus URL)
cd backend && ./mvnw -pl identity/services/spending-entity-service -am dependency:tree -Dverbose
```

## Reactor Resolution

- The aggregator POM at `backend/pom.xml` declares `<module>identity/libs/commons-cdh-lib</module>`. The library-parent's `<dependencyManagement>` pins the library to `${revision}`.
- Consumer services depend on `uk.co.whitbread.shared:commons-cdh-lib` without a `<version>` — Maven sources the version from `<dependencyManagement>` and resolves the artifact from the local reactor rather than Nexus.
- With `-pl <service> -am`, Maven includes the library transitively and builds it before the service in the same reactor session.
- Removing the `<module>` entry would cause Maven to fall back to the Nexus-published copy.
