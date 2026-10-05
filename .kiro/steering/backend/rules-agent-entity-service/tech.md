---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/rules-agent-entity-service/**"
---

# Tech Stack

- **Java 25**, Spring Boot 4.0.7, Spring Cloud 2025.1.1 (all inherited from `digital-monorepo-service-parent`)
- Spring Data JPA (PostgreSQL) — DDL mode: validate (no migrations, schema managed externally)
- MapStruct (entity-DTO mapping)
- Springdoc OpenAPI 3.0.2 (Swagger UI), emitting OpenAPI 3.1 (`api-docs.version: OPENAPI_3_1`)
- Spring Cloud Contract (consumer-driven contract tests)
- JUnit 5, Mockito, H2 (in-memory for tests), WireMock (`wiremock-standalone`)
- Reactor Core (for context propagation in tracing)

## Parent and dependency versions

The service inherits from the standard `digital-monorepo-service-parent`
(`../../../parents/service-parent/pom.xml`) like every other Spring Boot 4 service. Whitbread
shared libraries are built from the reactor at `${revision}` and are declared **without an
explicit `<version>`** — `commons-entity-exceptions`, `commons-logging` and
`coding-convention-rules` all come from the parent's `<dependencies>`, so the service POM does
not declare them at all.

## Service-specific POM configuration

Everything else is inherited. The POM only keeps what is genuinely local to this service:

- `application.port` 9108 and `sonar.coverage.exclusions`
- `springdoc-openapi-maven-plugin` generating `docs/openApi/rules-agent-entity-service-openApi.yaml`
- `spring-cloud-contract-maven-plugin` with `baseClassForTests=uk.co.whitbread.rules.agent.ContractVerifierBaseTest`
- bespoke JaCoCo controller-integration merge/report executions
- `pitest` `excludedClasses` and the `ArchUnitTests` exclusion

## Swagger UI resource handling

`SwaggerUiWebMvcConfig` explicitly registers the Swagger UI webjar resources. This is required
because `GlobalExceptionHandler` in `commons-entity-exceptions` extends
`ResponseEntityExceptionHandler` and intercepts `NoResourceFoundException` before the default
resource handler can serve them. Do not remove it without checking `/swagger-ui/index.html`
still resolves.

## Contract tests

`ContractVerifierBaseTest` starts its own static `WireMockServer` and loads stubs from
`classpath:/stubs/**/*.json` by hand, exposing the port via `@DynamicPropertySource`. The
`@AutoConfigureWireMock` annotation from `spring-cloud-contract-wiremock` is no longer used.

The generated `ContractVerifierIT` is **not run by the default build** — `nameSuffixForTests`
is `IT` and no `maven-failsafe-plugin` is configured anywhere in the monorepo, so surefire's
default includes skip it. To run the contract tests explicitly:

```bash
cd backend && ./mvnw verify -pl discover-search/services/rules-agent-entity-service \
  -Dtest=ContractVerifierIT -Dsurefire.failIfNoSpecifiedTests=false
```

## Build

```bash
cd backend && ./mvnw clean verify -pl discover-search/services/rules-agent-entity-service -am
```

## CI

No `.java-version` file — the workflow defaults to JDK 25.
