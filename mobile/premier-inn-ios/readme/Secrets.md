# Secrets Management Pattern

## Overview

This document describes the standardized pattern for managing API keys, tokens, and other sensitive configuration values in the Premier Inn iOS app. This pattern ensures secrets are never committed to git, persist correctly in archived builds, and work consistently across local development and CI/CD pipelines.

## Problem Statement

- **Hardcoded secrets**: Secrets committed to source code can be accidentally exposed in version control.
- **Runtime environment variables**: `ProcessInfo.processInfo.environment` does not persist into archived App Store builds.
- **Inconsistent developer setup**: Ad-hoc secret handling leads to onboarding friction and potential leakage.
- **Shared Xcode schemes**: Secrets embedded in shared schemes can be distributed with the project.

## Solution: .xcconfig-based Secret Injection

### Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Base.xcconfig (committed)                 │
│  - Committed to git                                          │
│  - Optionally includes Secrets.xcconfig via #include?      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Secrets.xcconfig (gitignored)            │
│  - Local development: Created by developer                 │
│  - Bitrise CI: Auto-generated from Bitrise Secrets vault    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Xcode Build Settings                     │
│  - Base.xcconfig referenced in project build config         │
│  - Variables injected as $(VARIABLE_NAME) build settings    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Info.plist                               │
│  - Secret values populated via build setting variables      │
│  - Example: <key>UnleashDevToken</key>                      │
│            <string>$(UNLEASH_DEV_TOKEN)</string>            │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Runtime Access                           │
│  - Bundle.main.object(forInfoDictionaryKey:)                │
│  - Secrets baked into compiled app binary                   │
└─────────────────────────────────────────────────────────────┘
```

## Files

### 1. Base.xcconfig (committed)

Located at project root. This file is committed to git and provides the base build configuration. It includes default placeholder values and optionally includes `Secrets.xcconfig` if it exists.

```xcconfig
// Base.xcconfig
// This file is committed to git and provides default build configuration.
// It optionally includes Secrets.xcconfig if it exists (for local development and CI).

// Default placeholder values (overridden by Secrets.xcconfig if present)
UNLEASH_DEV_TOKEN = placeholder-dev-token

// Include secrets file if present (the ? makes it optional - won't fail if missing)
// Values from Secrets.xcconfig will override the defaults above
#include? "Secrets.xcconfig"
```

**Note:** The default placeholder values allow the project to build locally even without `Secrets.xcconfig`. When `Secrets.xcconfig` is present, its values override the defaults.

### 2. Secrets.xcconfig (gitignored)

Located at project root. Contains actual secret values. Never committed to git. Developers create this file manually with their local secret values.

```xcconfig
// Secrets.xcconfig
// DO NOT commit this file to git - it contains sensitive information.

// Feature Flags (e.g., Unleash)
UNLEASH_DEV_TOKEN = your-unleash-dev-token-here
```

### 3. Info.plist and Info-DEV.plist

Located at `PremierInn/Resources/Info.plist` and `PremierInn/Resources/Info-DEV.plist`. Both use build setting variables for secrets.

**Currently configured for Unleash dev token:**

```xml
<key>UnleashDevToken</key>
<string>$(UNLEASH_DEV_TOKEN)</string>
```

**Note:** Dynatrace keys are currently hardcoded in both files and will be migrated to this pattern in the future.

### 4. .gitignore

Contains entry to prevent committing `Secrets.xcconfig`:

```
## Secrets management
Secrets.xcconfig
```

### 5. Bitrise Configuration

The Bitrise YAML is managed directly in the Bitrise platform. Add a script step to the `Primary` workflow to generate `Secrets.xcconfig` from Bitrise Secrets vault before building.

**Add this script step to the `Primary` workflow (after git-clone@8):**

```yaml
- script@1:
    title: Generate Secrets.xcconfig from Bitrise Secrets
    inputs:
      - content: |-
          #!/bin/bash
          set -euo pipefail
          
          # Fail if secret is not set
          : "${UNLEASH_DEV_TOKEN:?UNLEASH_DEV_TOKEN is not set in Bitrise Secrets vault}"
          
          cat > Secrets.xcconfig <<EOF
          UNLEASH_DEV_TOKEN = $UNLEASH_DEV_TOKEN
          EOF
          
          echo "Secrets.xcconfig generated successfully"
```

## Setup Instructions

### For Local Development

1. **Create Secrets.xcconfig**:
   Create `Secrets.xcconfig` in the project root with your secret values:

   ```xcconfig
   UNLEASH_DEV_TOKEN = your-unleash-dev-token-here
   ```

2. **Configure Xcode project** (one-time setup):
   - Open `PremierInn.xcodeproj`
   - Select the project in the navigator
   - Select the "PremierInn" target
   - Go to "Build Settings" tab
   - Search for "Config Files"
   - Under "Based on Configuration File", add `Base.xcconfig` for both Debug and Release configurations
   - **Important**: Do NOT add `Secrets.xcconfig` to "Copy Bundle Resources" build phase

3. **Build and run**:
   Secrets are now injected into your build and accessible via `Bundle.main.object(forInfoDictionaryKey:)`.

### For Bitrise CI/CD

1. **Add secrets to Bitrise Secrets vault**:
   - Go to Bitrise project → Secrets
   - Add `UNLEASH_DEV_TOKEN` (if not already configured)
   - Note: `UNLEASH_DEV_TOKEN` and `UNLEASH_PROD_TOKEN` are already configured in your Bitrise YAML envs

2. **Add script step to Bitrise platform**:
   - Go to your Bitrise project → Workflows
   - Edit the `Primary` workflow
   - Add a new script step after the `git-clone@8` step
   - Use the script content provided in the "Bitrise Configuration" section above

3. **Run build**:
   The script step runs before Xcode archive, generating `Secrets.xcconfig` with values from the Bitrise Secrets vault.

## Runtime Access

Access secrets at runtime using `Bundle.main.object(forInfoDictionaryKey:)`:

```swift
// ✅ Correct: Reads from Info.plist (baked into binary)
let dynatraceAppID = Bundle.main.object(forInfoDictionaryKey: "DTXApplicationID") as? String

// ❌ Incorrect: Environment variables don't persist in archived builds
let dynatraceAppID = ProcessInfo.processInfo.environment["DTX_APPLICATION_ID"]
```

## Adding New Secrets

When integrating a new SDK or service that requires secrets:

1. **Add to local Secrets.xcconfig**:
   ```xcconfig
   NEW_SDK_API_KEY = your-new-sdk-api-key-here
   ```

2. **Add to Info.plist**:
   ```xml
   <key>NewSDKAPIKey</key>
   <string>$(NEW_SDK_API_KEY)</string>
   ```

3. **Add to Bitrise script** (if used in CI):
   Update the script step in the Bitrise platform's `Primary` workflow to include the new secret:
   ```bash
   NEW_SDK_API_KEY = $NEW_SDK_API_KEY
   ```

4. **Add to Bitrise Secrets vault**:
   Add the secret value to Bitrise project Secrets.

5. **Access at runtime**:
   ```swift
   let apiKey = Bundle.main.object(forInfoDictionaryKey: "NewSDKAPIKey") as? String
   ```

## Security Considerations

- **Never commit Secrets.xcconfig**: The file is in `.gitignore` to prevent accidental commits.
- **Use separate values per environment**: Consider environment-specific secrets (dev/prod) if needed.
- **Rotate secrets regularly**: Follow your organization's secret rotation policies.
- **Audit access**: Review who has access to Bitrise Secrets vault and local development secrets.
- **Keychain for runtime storage**: For secrets that need to be stored after app launch (e.g., user tokens), use iOS Keychain, not Info.plist.

## Troubleshooting

### Build fails with "Undefined variable" error

**Cause**: `Secrets.xcconfig` is not referenced in Xcode build settings.

**Solution**: Follow step 2 in "For Local Development" above to configure the project to use `Secrets.xcconfig`.

### Secrets are nil at runtime

**Cause**: Build setting variable name doesn't match Info.plist key.

**Solution**: Ensure the variable name in `Secrets.xcconfig` (e.g., `DTX_APPLICATION_ID`) matches the build setting reference in Info.plist (e.g., `$(DTX_APPLICATION_ID)`).

### Bitrise build fails

**Cause**: Secret not added to Bitrise Secrets vault.

**Solution**: Add the missing secret to Bitrise project Secrets with the exact name used in the script.

## References

- [Apple Documentation: Build Settings](https://developer.apple.com/documentation/xcode/build-settings)
- [Apple Documentation: Info.plist Keys](https://developer.apple.com/documentation/bundleresources/information_property_list)
- [Bitrise Documentation: Secrets](https://devcenter.bitrise.io/en/accounts/secrets)
