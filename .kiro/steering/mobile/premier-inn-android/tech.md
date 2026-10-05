---
inclusion: always
---

# Tech Stack & Development Rules

## Hard Constraints (Never Violate)

- **Kotlin only** — do not create new Java files under any circumstances
- **Coroutines + Flow only** — do not introduce new RxJava usage (legacy RxJava exists but must not grow)
- **Jetpack Compose only** — do not create new XML layouts; all new UI uses Compose with Material 3
- **No hardcoded dependency versions** — all versions must live in `buildSrc/src/main/java/Dependencies.kt` via `Versions.*` and `Libs.*` objects
- **No hardcoded dispatchers** — always inject `AppDispatchers`; never reference `Dispatchers.IO` or `Dispatchers.Main` directly
- **Domain module purity** — `:domain` must have zero Android framework imports

## Build System

- Gradle (Groovy DSL), Android Gradle Plugin 8.13.0
- `buildSrc/src/main/java/Dependencies.kt` — single source of truth for dependency versions
- Multi-module: `:app`, `:domain`, `:data`
- Java 17 toolchain / JVM target
- Kotlin 2.1.10, Compose Compiler via Kotlin plugin
- minSdk 28 / targetSdk 35 / compileSdk 36

## Build Flavors

| Flavor | Purpose | Keystore | APIs |
|--------|---------|----------|------|
| `stage` | Dev/QA (default) | debug | Staging |
| `production` | Play Store release | upload | Production |

Runtime differentiation: `BuildConfig.STAGING` (boolean).

## Key Libraries

| Concern | Library | Version | Guidance |
|---------|---------|---------|----------|
| DI | Hilt | 2.56 | Bindings in `app/di/`; use `@Provides @Singleton` |
| Networking | Retrofit + OkHttp + Gson | 2.11 / 4.10 | Use Gson converter (not Moshi) |
| Async | Coroutines + Flow | 1.8.1 | Preferred for all new async work |
| Database | Room | 2.6.1 | |
| UI | Jetpack Compose (BOM 2025.11.01) | Material 3 | All new screens |
| Image loading | Glide | 4.13 | Use Compose integration for new screens |
| Analytics | Adobe SDK (BOM 3.+), Firebase Analytics | — | Adobe uses dynamic versioning intentionally — do not pin |
| Crash reporting | Firebase Crashlytics | — | |
| Auth | Auth0 | — | |
| Payments | PayPal/Braintree 4.51, Datatrans SDK | — | |
| Date/time | ThreeTenABP | 1.2.1 | Use `threetenbp` (no Android) for JVM/unit tests |

## Adding a New Dependency

1. Add the version constant to `Versions` in `Dependencies.kt`
2. Add the Maven coordinate to `Libs` referencing the version
3. Reference `Libs.yourDep` in the target module's `build.gradle`

## Code Style Rules

- Expose ViewModel state as `StateFlow<UiState>` (never `LiveData` for new code)
- Define UI states as `sealed class` or `sealed interface`
- One use case per file with `suspend operator fun invoke(...)`
- Use mappers to convert between layers — never expose API/data models to UI
- Prefer `Result` wrapper from `domain/result/` for error handling
- Compose screens receive state and emit events; no business logic in composables

## Testing

| Type | Framework | Usage |
|------|-----------|-------|
| Unit | JUnit 5 (primary) | Use for all new tests; JUnit 4 via vintage engine for legacy |
| Mocking | MockK | Prefer over Mockito for Kotlin code |
| Assertions | Google Truth | Use `assertThat(...)` style |
| Flow testing | Turbine 1.2 | For `StateFlow` / `Flow` assertions |
| Coroutines | kotlinx-coroutines-test | Wrap tests in `runTest {}` |
| Property-based | jqwik | For formal correctness properties |
| UI | Espresso + AndroidX Test Orchestrator | Instrumented tests |
| Network mocking | OkHttp MockWebServer, RESTMock | Mock API responses in tests |

## Common Commands

```bash
./gradlew assembleStageDebug          # Build stage debug APK
./gradlew testStageDebugUnitTest      # Unit tests (stage debug)
./gradlew test                        # All unit tests across modules
./gradlew connectedStageDebugAndroidTest  # Instrumented tests
./gradlew lintStageDebug              # Lint check
./gradlew jacocoTestReport            # Coverage report
./gradlew dependencyUpdates           # Check for outdated dependencies
```

## Build Config Fields

| Field | Purpose |
|-------|---------|
| `STAGING` | Boolean: true for stage, false for production |
| `ADOBE_APP_ID` | Adobe Launch environment ID |
| `APPSFLYER_KEY` | AppsFlyer attribution key |
| `FCM_TOPIC_REMOTE_CONFIG_PURGE` | FCM topic for remote config refresh |
