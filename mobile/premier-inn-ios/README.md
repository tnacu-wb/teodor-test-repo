# ![Premier Inn](readme/images/logo-premier-inn.png) Premier Inn iOS

![develop](https://app.bitrise.io/app/566b5c9b8b344ac2/status.svg?token=wYVlvrxtq3lRpFLuGhHGew&branch=develop)

The Premier Inn iOS mobile app.


## Index

* [Installation](#Installation)
* [SwiftUI Previews](#SwiftUIPreviews)
* [Dependencies](#Dependencies)
* [CI Setup](#CI-Setup)
* [fastlane](fastlane/README.md)
* [Code Style Guide](readme/CodeStyleGuide.md)
* [Workflows](readme/Workflows.md)
* [SwiftLint](#SwiftLint)
* [License](#License)


## Installation
This project uses Swift Package Manager (SPM) and includes dependencies fetched over SSH.
To ensure everything resolves correctly, follow the steps below.

### Generating an SSH ED25519 key

1) Open Terminal

2) Paste the text below, replacing the email used in the example with your GitHub email address

```
> ssh-keygen -t ed25519 -C "your_email@example.com"
```

3) This creates a new SSH key, using the provided email as a label

```
> Generating public/private ALGORITHM key pair.
```

4) When you're prompted to "Enter a file in which to save the key", you can press Enter to accept the default file location

5) At the prompt, type a secure passphrase

```
> Enter passphrase (empty for no passphrase): [Type a passphrase]
> Enter same passphrase again: [Type passphrase again]
```

6) Start the ssh-agent in the background

```
$ eval "$(ssh-agent -s)"
> Agent pid 59566
```

7) Add your SSH private key to the ssh-agent and store your passphrase in the keychain. If you created your key with a different name, or if you are adding an existing key that has a different name, replace id_ed25519 in the command with the name of your private key file

```
ssh-add --apple-use-keychain ~/.ssh/id_ed25519
```

8) Add the SSH public key to your account on GitHub. For more information, see Adding a new SSH key to your GitHub account. 

9) Make sure to Authorise the key under 'Configure SSO', then select 'whitbread-eos'. Follow any steps from there


For a detailed guide, please visit the GitHub link here: [Generating a new SSH key and adding it to the ssh-agent](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent)


## SwiftUI previews

SwiftUI previews previously failed to render in Xcode due to an issue triggered by the `GooglePlaces` Pod.

A fix has been implemented in the project to resolve this problem at the code/config level.

✔️ **Work Completed**

All necessary changes to support SwiftUI previews—excluding Xcode‑local settings—have already been handled in the below PR:

**PR:** [CTECH-5487: Fix SwiftUI previews#3510](https://github.com/whitbread-eos/premier-inn-holborn-ios/pull/3510)

These changes ensure the project itself is fully compatible with SwiftUI previews.

✔️ **Developer Action Required**

The only remaining step must be applied locally by each developer, as this Xcode preference is not stored in git:

1. Open Xcode
2. Navigate to `Editor` → `Canvas` → `Use Legacy Previews Execution`
3. Ensure Use Legacy Previews Execution is enabled


⚠️ Note:
This setting is specific to your Xcode installation and is not tracked by git, so each developer must enable it individually.

## Dependencies

To install/update:
- *Firebase* go [here](PremierInn/ThirdParty/Firebase/Firebase.md).
- *Adobe Premier Experience* go [here](PremierInn/ThirdParty/AdobePremierExperience/AdobePremierExperience.md).

*IMPORTANT*: Some specific build process depends on the folder structure.

Here a list of important files have to remain in the same folder structure with the same name:
- `PremierInn/Resources/Info.plist`
- `PremierInn/Resources/Info-DEV.plist`
- `PremierInn/Resources/Targets/PremierInn/GoogleService-Info.plist`
- `PremierInn/Resources/Targets/PremierInn-DEV/GoogleService-Info-Debug.plist`
- `PremierInn/ThirdParty/Firebase/*`

*Before change those files, please check Workflows on Bitrise and Framework Search Paths/Header Search Paths/Library Search Paths on Build Settings*.


## CI Setup

For Bitrise configuration check [here](readme/bitrise/Bitrise.md).

## SwiftLint
SwiftLint is managed via SPM (SwiftLintPlugins). No local install is required as it runs automatically as part of the project's build phase

## License

Copyright © Whitbread
