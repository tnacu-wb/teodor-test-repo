---
inclusion: fileMatch
fileMatchPattern: "mobile/*-ios/**"
---

# Mobile — iOS Stack Conventions

Applies to iOS apps under `mobile/*-ios/`. App-specific detail lives in
`mobile/<app>/{product,structure,tech}.md`.

## Build System & Dependencies

- **Xcode** project (`*.xcodeproj`); Swift.
- **Swift Package Manager (SPM)** for dependencies, some fetched over SSH — an SSH ED25519 key
  authorised for the `whitbread-eos` org (SSO) is required to resolve packages.
- **SwiftLint** runs automatically as a build phase via the SPM SwiftLintPlugins plugin — no
  local install needed.

## Conventions

- Follow the repo's `readme/CodeStyleGuide.md`.
- **Folder structure is build-significant.** Certain files must keep their exact path and name,
  including `PremierInn/Resources/Info.plist`, `Info-DEV.plist`, the per-target
  `GoogleService-Info*.plist` files, and `PremierInn/ThirdParty/Firebase/*`. Check Bitrise
  workflows and the Search Paths build settings before moving them.
- **SwiftUI previews** require each developer to enable
  `Editor → Canvas → Use Legacy Previews Execution` locally (not tracked in git).
- **Third-party:** Firebase, Adobe Premier Experience, Google Places — see the per-dependency
  `*.md` docs under `PremierInn/ThirdParty/`.

## CI

- **Bitrise** (config under `readme/bitrise/`), **fastlane** for build/release automation
  (see `fastlane/README.md`).
