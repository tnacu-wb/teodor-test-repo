---
inclusion: fileMatch
fileMatchPattern: "backend/identity/services/hotel-account-service-opera/**"
---

# Project Structure

The service uses a conventional layered Spring MVC structure under `uk.co.whitbread.hotel.account`.

```text
client/       OpenFeign clients and fallback factories for CDH, countries, 3C, PIBA, and Worldline
config/       Security, Redis, Feign, WebClient, Unleash, executor, and web configuration
controller/   Public, internal cache, authentication, InnBusiness, and Universal Login REST APIs
exceptions/   Domain/integration exceptions and the central REST exception handler
mapper/       MapStruct and hand-written mappings between API, CDH, and downstream models
model/        Request, response, customer, stay, payment, and feature-flag models
properties/   Typed configuration properties
security/     API-key annotation and validation aspect
service/      Account orchestration with `auth0/`, `cdh/`, and `worldline/` subpackages
utils/        Pagination, booking, network, reset-password, and account conversion helpers
validation/   Custom Jakarta Bean Validation constraints
```

## Conventions

- Controllers validate and delegate; orchestration belongs in services.
- Add downstream HTTP operations to the matching client package and retain its fallback factory.
- Keep Auth0, CDH, and Worldline-specific logic in their existing service subpackages.
- Use existing MapStruct mappers and custom validators instead of duplicating conversion or validation logic.
- Shared Auth0, CDH, validator, and Azure email behavior belongs in the reactor libraries, not copied into this service.
- If shared subtree code is missing, update that library from its upstream `develop` branch with `git subtree pull`.

## Resources and tests

- Main configuration: `src/main/resources/application.yml` and `bootstrap.yml`.
- The CDH input contract is `src/main/resources/openapi/cdh-adapter-service-openApi.yaml`; generated models live under `target/generated-sources/openapi`.
- Spring Cloud Contract definitions are in `src/test/resources/contracts/`.
- WireMock mappings and payloads are in `src/test/resources/mapping/`, `stubs/`, and `__files/`.
- API and flow documentation lives under `docs/`.