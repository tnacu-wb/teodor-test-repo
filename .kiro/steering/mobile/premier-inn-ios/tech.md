---
inclusion: always
---

# Tech Stack

## Languages & platform

- **Swift** (Swift 6 tools, `swiftLanguageModes: [.v5]`), targeting **iOS 16+**. Write Swift 5-mode-compatible code; do not rely on Swift 6 language-mode-only behaviour.
- UI is a mix of **UIKit** (storyboards, XIBs, `FormekaViewController`-based form screens) and **SwiftUI** (`View` + `ObservableObject` view models, usually hosted from a UIKit router). Match the UI technology of the surrounding module.
- Entry: `main.swift` selects `AppDelegate` or `UnitTestsAppDelegate` (XCTest detection); startup via `Bootstrap/` tasks; navigation via `SceneRouter` and per-module routers. Never navigate ad hoc from views.
- iPhone is portrait-only; iPad supports all orientations.

## Build system & dependency management

- **Xcode project**: `PremierInn.xcodeproj` (no `.xcworkspace`; not CocoaPods-based despite legacy references). Do not add a Podfile or reintroduce CocoaPods.
- **Swift Package Manager (SPM)** for all dependencies. Some packages are fetched over **SSH** and require an SSO-authorised GitHub SSH key (see README Installation).
- **Local SPM packages** in the repo root — prefer extending these over duplicating logic:
  - `SimpleNetwork` — networking layer, **domain models**, session/storage (wraps Alamofire, AlamofireImage, Auth0). Route all HTTP and model definitions through it.
  - `SimpleCalendar` — calendar/date-picker UI component.
  - `Formeka` — form-building component; drives form-based screens.
  - `TakeMeBack` — navigation/back-handling helper.
- **Third-party SDKs**: Firebase (messaging/analytics), Adobe Premier Experience / `AEPAssurance`, AppsFlyer (`AppsFlyerLib`), GooglePlaces, Lottie (animations), PassKit (wallet passes), Auth0 (identity), Zaplox (digital keys). Use Auth0 for all identity flows and Zaplox for all digital-key flows; do not add parallel libraries.

### Resolved dependency versions (SimpleNetwork)

Alamofire 5.0.2 · AlamofireImage 4.0.0 · Auth0.swift 2.16.2 · JWTDecode.swift 3.3.0 (transitive) · SimpleKeychain 1.3.0 (transitive).

### Pinned dependency notes

- Dependencies are pinned with `exact:` versions. **Always** add or update with `exact:` — never use version ranges. Update `Package.resolved` alongside.
- Certain files must keep their exact path and name or the build breaks: Info.plist variants, `GoogleService-Info*.plist`, and `PremierInn/ThirdParty/Firebase/*`. Check Bitrise Workflows and Xcode Build Settings search paths before moving/renaming them.

## Networking (SimpleNetwork)

- `RequestsManager` is the façade, split into category extensions: `+Booking`, `+Amend`, `+Payment`, `+Reservation`, `+HotelSearch`, `+DigitalKey`, `+User`, `+Marketing`, `+RegCard`, `+UpdateCiolStatus`, `+Misc`. Add new endpoints to the matching extension.
- Lower layers: `Webservice`/`WebserviceProtocol`, `Microservices`, `GraphQL`, `Auth0Service`, and `MockServices` (mock runs backed by `Sources/Mocks/*.json`).
- Uses a custom `Result<T>` type and `AuthCredentials`; session via `UserSessionManager`, storage via `SimpleStorageManager`, connectivity via `ReachabilityManager`.
- **TLS certificate pinning** is in place (`Certificates/microservices-live.cer`, `Extensions/URL+Security.swift`) and `PrivacyInfo.xcprivacy` is declared — keep both current when changing networking or data use.

## App conventions

- **Localization**: use `PILocalizedString("key", comment: "…")` — not `NSLocalizedString` directly. Add German (`de.lproj`) alongside English (`Base.lproj`); never hardcode display copy.
- **Analytics**: track via `AnalyticsManager.shared`; view controllers override `screenName`/`screenType`; use keys under `PIAnalytics.Keys` / `.StateNames` / `.StateTypes` / `.Error`.
- **Accessibility identifiers**: set from `AccessibilityIdentifiers.<Feature>.<element>` — EarlGrey UI tests depend on them.
- **Build flavours**: dev-only behaviour is gated with `#if DEV`; guard test-only code paths with `ProcessInfo.isRunningUnitTests`.

## Linting & code style

**SwiftLint** via SPM (`SwiftLintPlugins`) runs automatically as a build phase — no local install needed. Config is `.swiftlint.yml`. Most opt-in rules run at **error** severity, so violations break the build:

- **Limits**: `line_length` max 125, `function_body_length` max 100, `type_body_length` max 1200.
- **`self`**: avoid redundant `self` (`redundant_self`).
- **Returns**: implicit returns (`implicit_return`); no `return` in `Void` functions; no redundant `-> Void`.
- **Access control**: prefer `private` over `fileprivate` (`private_over_fileprivate`, `strict_fileprivate`); use `extension_access_modifier`.
- **Collections**: `.isEmpty` not `count == 0` (`empty_count`); `.isEmpty` for strings (`empty_string`); prefer `.first(where:)` / `.last(where:)` and `.contains` over manual loops/range comparisons.
- **Style**: correct `modifier_order`; whitespace around operators; no trailing whitespace (error, even on blank lines); blank-line rules around braces; `switch_case_alignment`; `toggle_bool`; prefer `.zero` over explicit init; prefer `Self` over `type(of: self)`.
- **Excluded from linting**: `PremierInn/ImportedSDKs`, all test targets, `SimpleNetwork/SimpleNetworkTests`. Disabled rules include `identifier_name`, `type_name`, `file_length`, `todo`.

Reference the team `CodeStyleGuide` under `readme/` for anything not covered here.

## Common commands

Build/test orchestrated by **fastlane** (Ruby 3.3.1, see `.ruby-version`). CI runs on **Bitrise**. Helper scripts live in `Scripts/` (e.g. `run_swiftlint.sh`, `copy_firebase_debug_plist.sh`, `set-build-number.sh`, `upload-symbols`).

```bash
# Install fastlane
brew install fastlane            # or: sudo gem install fastlane -NV

# Unit tests (scheme: "PremierInn (Development)")
fastlane ios unit_tests

# UI tests (mock & live)
fastlane ios ui_tests

# UI tests, mock only
fastlane ios ui_tests_mock

# UI regression pack (classes prefixed RegressionPack_)
fastlane ios ui_tests_regression_pack
```

- Test runner is `scan`; default simulator is iPhone 11 Pro (`fastlane/Scanfile`).
- Local SPM package tests use the bundled `.xctestplan` files (e.g. `FormekaTests.xctestplan`, `SimpleNetworkTests.xctestplan`); the app uses `PremierInn Tests.xctestplan`.
- Do not start long-running watchers or simulators in automation; run these lanes as discrete commands.

## SwiftUI previews

Enable Xcode → Editor → Canvas → **Use Legacy Previews Execution** (per-developer, not tracked in git) to work around a GooglePlaces preview issue.
