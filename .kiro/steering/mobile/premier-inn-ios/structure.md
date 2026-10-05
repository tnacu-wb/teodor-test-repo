---
inclusion: always
---

# Project Structure

## Repository root

```
PremierInn.xcodeproj/       # Xcode project (no .xcworkspace)
PremierInn/                 # Main app source
PremierInnTests/            # Unit tests (mirror module/VIPER layout)
PremierInn-DEV-Unit-Tests/ # Dev-target unit tests
PremierInnDEVEarlGreyTests/# UI tests (EarlGrey); RegressionPack_ prefix = live regression pack
fastlane/                  # Build/test lanes (Fastfile, Scanfile, Appfile)
Scripts/                   # Build/release helpers (firebase plist, swiftlint, symbols, slack upload)
export.plist, exportAdHoc.plist, releaseScript.sh  # Release/export config
readme/                    # Docs (CodeStyleGuide, Workflows, Bitrise, images)
.kiro/                     # Kiro steering & specs

# Local SPM packages (each: Package.swift + Sources/ + Tests/)
SimpleNetwork/             # Networking + domain models + session/storage (Alamofire/Auth0)
SimpleCalendar/            # Calendar UI component
Formeka/                   # Form-building component (drives form-based screens)
TakeMeBack/                # Navigation helper
```

## App source layout (`PremierInn/`)

```
main.swift                # Selects AppDelegate vs UnitTestsAppDelegate (XCTest/EarlGrey detection)
AppDelegate.swift (+Notifications, +MessagingDelegate)   # App lifecycle, bootstrap wiring
SceneDelegate.swift, SceneRouter.swift                   # Scene lifecycle, deep links, shortcuts, push
Bootstrap/                # AppBootstrapper runs ordered [BootstrapTask] (Tasks/)
Modules/                  # Feature modules, one folder per feature (VIPER)
Controllers/              # Shared/legacy UIKit view controllers
Views/ ViewModel/         # Shared reusable views / view models
DomainUseCases/           # Cross-module business logic (e.g. ValidateDiscountCodeUseCase)
Style/                    # Colors/, Fonts/, Images/, LottieAnimation/ — design tokens
Utilities/                # Helpers, extensions, shared managers
GDPR/                     # Consent & privacy
Xibs/                     # Interface Builder XIBs
Base.lproj/, de.lproj/    # Localized strings (English base, German)
Resources/                # Info.plist variants, GoogleService-Info, targets config
Assets.xcassets/          # Images, app icons, colour sets
ImportedSDKs/             # Vendored SDKs (SwiftLint-excluded)
ThirdParty/               # Firebase, Adobe Premier Experience config + docs
```

## Where to put new code

- **Feature work** → a folder under `Modules/<Feature>/` mapped to a product capability; keep the logic in the module.
- **Domain/data models** → they live in `SimpleNetwork/Sources/Model/` (Reservation, Rate, RoomType, Hotel, PaymentCard, User, …). Reuse them; do not redefine app-side DTOs.
- **HTTP / backend calls** → extend `RequestsManager` in `SimpleNetwork` (see networking notes below), not ad hoc URLSession code.
- **Logic reused by more than one module** → `DomainUseCases/` (business logic), `Utilities/` (helpers/extensions), or `Views/` / `ViewModel/` (shared UI).
- **Startup work** → a discrete `BootstrapTask` in `Bootstrap/Tasks/`; do not add startup logic to `AppDelegate`.
- **Theming/colours/fonts** → reuse `Style/` (e.g. `UIColor.BasePurple`, `UIFont.Body()`); never hardcode design values.
- **A new broadly reusable component** → consider a new local SPM package (follow `SimpleNetwork` / `Formeka`).

## Module architecture (VIPER)

Each feature under `Modules/<Feature>/` splits into View / ViewModel / Presenter / Interactor / Router, each file prefixed with the feature name. Reference example (`Login/`):

| File | Role |
|------|------|
| `LoginView.swift` | View — UIKit (often a `FormekaViewController` subclass) or SwiftUI `View`. |
| `LoginView+ViewModel.swift` | View state/bindings; for Formeka screens builds `FormekaModelSection`/`Row`. |
| `LoginPresenter.swift` | Presentation logic; mediates View ↔ Interactor; holds `weak view`, `interactor`, `router`. |
| `LoginInteractor.swift` | Business logic; calls its `dataManager` (a `RequestsManager`) for data. |
| `LoginRouter.swift` | Builds/assembles the module and owns navigation out of it. |

Conventions that hold across modules:

- **Protocol-driven boundaries.** Layers communicate through protocols. Two naming styles coexist: older modules use `<Feature>ViewInput` / `<Feature>PresenterInput` / `<Feature>InteractorInput` / `<Feature>InteractorOutput` / `<Feature>RouterInput`; newer modules use `<Feature>...Protocol`. Match the surrounding module.
- **Router assembles the module.** The router instantiates the view, presenter, interactor, and `dataManager`, then wires their references. Newer modules may use a `<Feature>Module.swift` factory (e.g. `DashboardModule`) or an `enum Router` instead.
- **Interactor → data via `RequestsManager`.** Set `interactor.dataManager = RequestsManager()`; `RequestsManager` conforms to the module's `...InteractorOutput` via an extension.
- **UI tech follows the module.** Form screens use UIKit + Formeka; newer screens use SwiftUI `View` + `ObservableObject` view models, hosted from a UIKit router. Newer modules add `Views/` and `ViewModels/` subfolders. Don't introduce the other UI stack into an existing module.
- **Accessibility identifiers are required.** Set them from `AccessibilityIdentifiers.<Feature>.<element>` — EarlGrey UI tests depend on them.

## Shared managers & singletons

Reuse these rather than re-implementing; most are accessed as singletons:

- `UserSessionManager.sharedInstance` — current user/session (in `SimpleNetwork`).
- `RequestsManager` — networking façade (in `SimpleNetwork`).
- `AnalyticsManager.shared` — tracking; `PIAnalytics.Keys` / `.StateNames` / `.StateTypes` for keys.
- `BookingDetails.sharedInstance`, `SettingsManager.sharedInstance` — booking/app state.
- `BiometricAuthenticationManager` — biometric auth.
- `LinkHandler.sharedInstance` — deep links / app shortcuts.

## App startup & routing

- `main.swift` boots `AppDelegate` normally, or `UnitTestsAppDelegate` under XCTest (EarlGrey still uses `AppDelegate`). Guard test-only paths with `ProcessInfo.isRunningUnitTests`.
- `AppDelegate` runs `FirebaseBootstrap` **first** (legacy code touches Firebase during init), then `AppBootstrapper` runs the remaining tasks in order: Cleanup → Environment → CoreSettings → Analytics → Tracking → Push → Appearance.
- `SceneRouter` (conforming to `SceneRouting`) owns scene lifecycle, deep links (AppsFlyer, Adobe Assurance), push handling, and home-screen shortcuts. Add such handling here, not in views.
- iPhone is portrait-only; iPad supports all orientations (`Main~ipad.storyboard`).

## Tests

- **Unit tests** (`PremierInnTests/`) mirror the app: `Modules/<Feature>/<Feature>InteractorTests.swift`, `...PresenterTests.swift`, `...ViewTests.swift` — one file per VIPER layer. Add tests in the matching path.
- **EarlGrey UI tests** (`PremierInnDEVEarlGreyTests/`) use page objects under `Pages/`, with `API/`, `Biometrics/`, `Tests/`. Classes prefixed `RegressionPack_` run against live services.
- SimpleNetwork mocks live in `SimpleNetwork/Sources/Mocks/*.json`, served via `MockServices` for mock UI runs.

## Conventions

- **Match existing modules.** Mirror the naming, protocol style, and layer split of a comparable module (e.g. `Login/`, `Register/`) when adding code.
- **Localize all user-facing strings** with `PILocalizedString("key", comment:)`; add German (`de.lproj`) alongside English (`Base.lproj`). Never hardcode display copy.
- **Do not move or rename fixed-path files** — the build depends on them: Info.plist variants, `GoogleService-Info*.plist`, and `ThirdParty/Firebase/*`. Check Bitrise workflows and Xcode Build Settings search paths before relocating anything in `Resources/`, `ThirdParty/`, or `ImportedSDKs/`.
- **Treat guest data as sensitive.** Bookings, payments, and account info are personal data — respect GDPR/consent flows and avoid logging PII.
- **Keep `AppDelegate` thin** — push startup work into `Bootstrap/Tasks/`.
