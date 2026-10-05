---
inclusion: fileMatch
fileMatchPattern: "backend/discover-search/services/on-demand-refresh-service-opera/**"
---

# Tech

- Java 25, Spring Boot 4.0.7, Spring Cloud 2025.1.1, reactive WebFlux/Netty.
- Inherits `digital-monorepo-service-parent` via `../../../parents/service-parent/pom.xml`.
- Spring Data JPA/JDBC with separate PostgreSQL reader and writer pools; H2 is test/build-startup only.
- Quartz uses JDBC persistence and clustering outside integration; integration uses `RAMJobStore`.
- Outbound Opera authentication uses Spring Security reactive OAuth2 client credentials or the retained password grant mode.
- SpringDoc WebFlux generates OpenAPI 3.1.0; actuator is rooted at `/on-demand-refresh-service/actuator`.
- Resilience includes premature-close retry for Opera GET/PUT calls, database batch retry, Quartz failed-job rescheduling, and a 10-second default time limiter.
- Unleash is wired explicitly by `CustomUnleashConfig`; starter auto-configuration remains enabled for consistency with other monorepo services and backs off when the custom beans exist. A direct `@Import(UnleashAutoConfiguration.class)` is therefore redundant.

## Validation

```bash
cd backend
./mvnw test -pl discover-search/services/on-demand-refresh-service-opera -am
./mvnw clean verify -pl discover-search/services/on-demand-refresh-service-opera -am
```

The verified import has 104 tests with 0 failures/errors and 10 Docker-dependent Testcontainers skips, OpenAPI 3.1.0 with 8 paths, an executable JAR with 206 runtime libraries, and JaCoCo unit/merged reports. No Spring Cloud Contract plugin or stubs JAR exists. Verification must leave no listener on port 9099.