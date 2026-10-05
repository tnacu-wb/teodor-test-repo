---
inclusion: fileMatch
fileMatchPattern: "backend/**"
---

# Backend Stack Conventions

Applies to every module under `backend/`. Module-specific detail lives in
`backend/<module>/{product,structure,tech}.md`.

## Language & Runtime

- **Java 25** with the Jakarta EE namespace
- **Spring Boot 4.0.7** with **Spring Cloud 2025.1.1**

## Build System

- **Maven via the wrapper** (`./mvnw` in `backend/`) — never use a locally installed Maven.
- `backend/pom.xml` is a **pure aggregator** (module list + flatten plugin only).
- Dedicated parent POMs live under `backend/parents/`:
  - **`service-parent`** (`digital-monorepo-service-parent`) — for SB4 services (inherits `spring-boot-starter-parent:4.0.7`).
  - **`library-parent`** (`digital-monorepo-library-parent`) — for shared libraries (inherits `spring-boot-starter-parent:4.0.7`, no `spring-boot-maven-plugin`).
- Services use: `<parent><artifactId>digital-monorepo-service-parent</artifactId><relativePath>../../../parents/service-parent/pom.xml</relativePath></parent>`
- Libraries use: `<parent><artifactId>digital-monorepo-library-parent</artifactId><relativePath>../../../parents/library-parent/pom.xml</relativePath></parent>`
- **CI-friendly versioning** — every module uses the `${revision}` property (default `1.0.0`),
  overridable at build time with `-Drevision=...`.
- **flatten-maven-plugin** resolves `${revision}` in installed/deployed POMs.

### Build commands

```bash
# Full build (all modules)
cd backend && ./mvnw clean install

# Single service (with dependencies)
cd backend && ./mvnw clean install -pl <squad>/services/<service-name> -am

# Single library (with dependencies)
cd backend && ./mvnw clean install -pl <squad>/libs/<library-name> -am

# Explicit version override
cd backend && ./mvnw clean install -Drevision=1.1.0

# Skip tests
cd backend && ./mvnw clean install -DskipTests
```

## Layout

```
backend/
├── pom.xml                 # Aggregator POM (module list only)
├── mvnw / .mvn/            # Maven Wrapper
├── parents/
│   ├── service-parent/     # Parent for SB4 services
│   └── library-parent/     # Parent for shared libraries
└── <squad>/
    ├── services/<service-name>/
    └── libs/<library-name>/
```

Squads: `meta`, `discover-search`, `book-pay`, `identity`, `arrive-stay-leave`, `manage-modify`.

## Cross-Cutting Conventions

- **Lombok** for boilerplate reduction (annotation processing: Lombok → MapStruct via
  `lombok-mapstruct-binding`).
- **MapStruct** for object mapping.
- **SpringDoc OpenAPI** for API documentation.
- **Micrometer + Brave** for distributed tracing.
- **Redis cluster** (Lettuce client) for caching.
- **JaCoCo** for coverage and **PIT** for mutation testing.
- SonarQube project keys follow `whitbread-eos_digital-monorepo_<service-name>`.

## Adding a New Service

1. Create `backend/<squad>/services/<service-name>/`.
2. Add `<module><squad>/services/<service-name></module>` to `backend/pom.xml`.
3. Inherit from the service parent: `<relativePath>../../../parents/service-parent/pom.xml</relativePath>`.
4. Add Tier 3 steering at `.kiro/steering/backend/<service-name>/` with `fileMatchPattern:
   "backend/<squad>/services/<service-name>/**"`.
5. CI picks up the new service automatically via path-based change detection.

## Adding a New Library

1. Create `backend/<squad>/libs/<library-name>/`.
2. Add `<module><squad>/libs/<library-name></module>` to `backend/pom.xml`.
3. Inherit from the library parent: `<relativePath>../../../parents/library-parent/pom.xml</relativePath>`.
4. Add Tier 3 steering at `.kiro/steering/backend/<library-name>/` with `fileMatchPattern:
   "backend/<squad>/libs/<library-name>/**"`.
5. CI picks up the new library automatically via path-based change detection.

