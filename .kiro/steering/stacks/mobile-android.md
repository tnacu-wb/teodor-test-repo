---
inclusion: fileMatch
fileMatchPattern: "mobile/*-android/**"
---

# Mobile — Android Stack Conventions

Applies to Android apps under `mobile/*-android/`. App-specific detail lives in
`mobile/<app>/{product,structure,tech}.md`.

## Build System

- **Gradle via the wrapper** (`./gradlew`) — never use a locally installed Gradle.
- Multi-module Gradle build. `settings.gradle` includes `:app`, `:domain`, `:data`
  (clean architecture layering; `:app` depends inward on `:domain` and `:data`).
- Repositories are locked down with `RepositoriesMode.FAIL_ON_PROJECT_REPOS` — declare all
  repositories in `settings.gradle` (`google()`, `mavenCentral()`, `jitpack.io`, and local
  `libs/` via `flatDir`), not in module build files.

```bash
./gradlew assembleDebug        # build debug APK
./gradlew testDebugUnitTest    # unit tests
./gradlew lint                 # Android lint
```

## Conventions

- **Resource naming:** `{WHERE}_{description}_{WHAT}`, e.g. `review_booking_email_input`,
  `review_booking_confirm_button`.
- **View/type mapping:** `EditText → input`, `TextView → text|label|heading`,
  `ImageView → image`, `Button/CallToActionButton → button`.
- **SQLite** follows sqlstyle.guide.
- **Networking:** OkHttp with **SSL certificate pinning** (public-key pins in `CertificatePinner`).
  Rotating a cert requires extracting new SHA-256 pins ahead of the server cert release.
- **Firebase:** Crashlytics + Analytics for release monitoring; Remote Config for FCM push.

## Branching & Release

- Git-flow: `develop` (active), `feature/MON-x.x`, `release/x.x`, `hotfix/x.x`, `master`
  (mirrors production). Squash-and-merge feature branches into `develop`.
- **Bitrise** CI. Tag a commit `Ready` to trigger a tester build to Firebase.
  `release_to_beta` workflow (manual, needs `RELEASE_BUILD_NUMBER`) publishes to the Google
  Play Beta channel; promote to Production via staged rollout (10-15% → 30% → 60% → 100%).
