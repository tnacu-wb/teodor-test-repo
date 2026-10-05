---
inclusion: fileMatch
fileMatchPattern: "backend/arrive-stay-leave/services/kiosk-checkin-service/**"
---

# Tech

- Java 25, Spring Boot 4.0.7, Spring Cloud 2025.1.1.
- Inherits `digital-monorepo-service-parent` via `../../../parents/service-parent/pom.xml`.
- Spring MVC controller with blocking WebClient adapters, Auth0 `common-auth0`, MapStruct, SpringDoc 3, Micrometer/Brave.
- Server port `9120`; actuator base path `/v1/kiosk/actuator`; Swagger UI `/swagger-ui-custom.html`.
- `OHIP_HOST` defaults to `localhost:9100`; `RESERVATION_HOST` defaults to `localhost:9103`.
- Check-in is protected by `@PreAuthorize("isAuthenticated()")`; the endpoint registry otherwise permits requests.
- Graceful shutdown uses a one-minute phase timeout and AWS trace propagation.

## Validation

```bash
cd backend
./mvnw test -pl arrive-stay-leave/services/kiosk-checkin-service -am
./mvnw clean verify -pl arrive-stay-leave/services/kiosk-checkin-service -am
```

The verified import has 66 service tests, OpenAPI 3.1.0 with 8 paths, an executable JAR with 172
runtime libraries, and parent-owned JaCoCo unit and merged reports. Spring Cloud Contract generation
is not enabled because no production contracts exist. Verification must leave no listener on port
9120.
