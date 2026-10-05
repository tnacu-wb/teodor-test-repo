---
inclusion: always
---

# Project Structure & Architecture

## Module Layout

```
app/          → Android app module (UI, DI, presentation)
domain/       → Pure Kotlin/JVM module (use cases, models, repository interfaces)
data/         → Android library module (repo impls, API clients, Room DB, mappers)
buildSrc/     → Centralized dependency versions (Dependencies.kt)
libs/         → Local AAR/JAR libraries
```

## Module Dependency Rules

```
app → domain ← data
```

- `:domain` is pure Kotlin/JVM — zero Android framework imports
- `:data` implements `:domain` repository interfaces
- `:app` depends on both `:domain` and `:data`
- NEVER import `app` or `data` classes from `:domain`

## Architecture Patterns

### New Code (mandatory for all new work)

| Layer | Pattern |
|-------|---------|
| ViewModel | `@HiltViewModel`, constructor injection, expose `StateFlow<UiState>` |
| UI State | Sealed class/interface, updated via `MutableStateFlow` |
| Use Case | Single class per file, `suspend operator fun invoke(...)` or returns `Flow` |
| Repository | Interface in `:domain`, impl in `:data`, Hilt binding in `app/di/` |
| Error Handling | `Result` wrapper from `domain/result/` |
| Dispatchers | Inject `AppDispatchers` — never hardcode `Dispatchers.IO`/`Main` |
| UI | Jetpack Compose with Material 3 (BOM-managed) |
| Async | Coroutines + Flow exclusively |

### Legacy Code (read-only context, do not extend)

- MVP with BasePresenter, RxJava 2, AutoValue + AutoValue-Gson

## Hilt DI Rules

- All modules in `app/di/`
- Naming: `{Concern}Module.kt` (e.g., `RepositoryModule`, `NetworkingModule`)
- Bindings: `@Provides @Singleton` returning the domain interface type
- Use `@Named` for ambiguous types, `Provider<T>` for lazy injection

## Package Structure

### App (`app/src/main/java/com/whitbread/premierinn/`)

| Package | Purpose |
|---------|---------|
| `base/` | Base view components |
| `di/` | Hilt modules |
| `common/` | Shared utilities, extensions, analytics constants |
| `compose/` | Shared Compose components (Material 3) |
| `api/` | API config, interceptors, SSL pinning |
| `utils/` | App-wide utilities |
| Feature packages | One per screen/flow (e.g., `ciol/`, `mybookings/`, `payment/`) |

New feature package structure:

```
featurename/
├── analytics/      # Analytics event models
├── fragments/      # Fragment UI (or Compose screens)
├── viewmodel/      # HiltViewModel
│   └── state/      # UiState sealed classes
├── mapper/         # Domain → UI mappers
├── uimodel/        # UI-specific data classes
├── usecase/        # Feature-specific use cases (if not in domain)
└── FeatureActivity.kt
```

### Domain (`domain/src/main/java/com/whitbread/premierinn/domain/`)

Organized by domain area, each containing:
- `entity/` — domain models (Kotlin data classes)
- `repository/` — repository interfaces
- `usecase/` — use case classes

### Data (`data/src/main/java/com/whitbread/premierinn/data/`)

Mirrors domain areas:
- `remote/` — Retrofit API service interfaces
- Feature packages — repo impls, request/response bodies, mappers
- `graphql/` — GraphQL repo implementations
- `common/` — Shared data utilities (persistence, error logging, locale)

## Naming Conventions

| Element | Pattern | Example |
|---------|---------|---------|
| Feature package | lowercase, no separators | `bookingdetails` |
| ViewModel | `{Feature}ViewModel` | `PreStayViewModel` |
| Use Case | `{Verb}{Noun}UseCase` | `ConfirmPreCheckInUseCase` |
| Repository interface | `{Feature}Repository` | `BookingRepository` |
| Repository impl | `{Feature}RepositoryImpl` | `BookingRepositoryImpl` |
| Hilt module | `{Concern}Module` | `RepositoryModule` |
| Domain entity | `{Name}Entity` or `{Name}Domain` | `ThreeCpPaymentDetailsEntity` |
| UI model | `{Name}UiModel` or `{Name}Model` | `PreStayUiModel` |
| Mapper function | `convertTo{Target}` or `mapTo{Target}` | `convertToUiModel()` |
| XML resource ID | `{where}_{description}_{what}` | `review_booking_confirm_button` |
| View type suffix | EditText→`input`, TextView→`text`/`label`/`heading`, ImageView→`image`, Button→`button` |

## Branching Model

| Branch | Purpose |
|--------|---------|
| `develop` | Active development (PR target) |
| `feature/MON-x.x` | Feature work (squash merge into develop) |
| `bugfix/MON-x.x` | Bug fixes |
| `release/x.x` | Release candidates |
| `master` | Production mirror |
| `hotfix/x.x` | Emergency fixes from master |

## Mandatory Rules for New Code

1. All new code in Kotlin — no new Java files
2. Coroutines + Flow for async — no new RxJava usage
3. Jetpack Compose for new screens — no new XML layouts
4. `StateFlow<UiState>` for ViewModel state exposure
5. Repository interfaces in `:domain`, implementations in `:data`
6. Hilt bindings in `app/di/` modules
7. `:domain` must stay free of Android imports
8. Inject `AppDispatchers` for coroutine context
9. Mappers between layers — never leak API/data models to UI
10. One use case per file, single public `invoke` function
