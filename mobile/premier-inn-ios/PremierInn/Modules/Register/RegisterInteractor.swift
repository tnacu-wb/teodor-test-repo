//
//  RegisterInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 02/01/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork

typealias RegisterTracking = (screenName: String, screenType: String)

private enum RegisterErrorMessages {
    static let duplicateEmailAddress = PILocalizedString("registerRequestAlreadyRegisteredMessage", comment: "")
}

enum RegisterInteractorError: LocalizedError {
    case missingPassword
    case invalidPasswordComplexity
    case missingMarketingOptIn
    case emailAlreadyRegistered
    case automaticLoginFailed
    case genericNetworkError
    case missingTermsAndConditionsRow
    case unknown
    case missingEmailAddress
    case missingAddress

    case userFacing(message: String)

    var errorDescription: String? {
        switch self {
        case .emailAlreadyRegistered:
            return PILocalizedString("registerScreenEmailAlreadyUsedError")
        case .invalidPasswordComplexity:
            return PILocalizedString("changePasswordNewInvalidErrorMessage")
        case .genericNetworkError:
            return PILocalizedString("registerScreenGenericNetworkRequestError")
        case .missingTermsAndConditionsRow:
            return PILocalizedString("missingTermsAndConditionRowErrorMessage")
        case .userFacing(let message):
            return message
        case .automaticLoginFailed:
            return PILocalizedString("registerScreenAutomaticLoginFailed")
        default:
            return PILocalizedString("registerScreenGenericError") +
            "\n\(String(describing: self))"
        }
    }
}

private extension Error {
    func inspect(email: String) -> Error {
        guard let error = self as? RequestsManagerError else { return RegisterInteractorError.genericNetworkError }

        switch error {
        case RequestsManagerError.serverError(let dict):
            guard let messages = dict?["details"] as? [String] else { return RegisterInteractorError.unknown }

            if let code = dict?["code"] as? String, code == "052" {
                return RegisterInteractorError.userFacing(message: messages.joined(separator: "\n"))
            }

            for message in messages {
                switch message {
                case String(format: RegisterErrorMessages.duplicateEmailAddress, email):
                    return RegisterInteractorError.emailAlreadyRegistered
                default:
                    continue
                }
            }

            return RegisterInteractorError.genericNetworkError

        default:
            return error
        }
    }
}

class RegisterInteractor {
    var loginDataManager: LoginInteractorOutput?
    var registerDataManager: RegisterInteractorOutput? = RequestsManager()

    static func registerParameters(values: PIDictionary) throws -> RegisterParameters {
        guard let password = values[RegisterRow.password.rawValue] as? String else {
            throw RegisterInteractorError.missingPassword
        }
        guard let marketingOptIn = values[RegisterRow.marketingOptIn.rawValue] as? Bool else {
            throw RegisterInteractorError.missingMarketingOptIn
        }
        guard let address = try address(withValues: values) else {
            throw RegisterInteractorError.missingAddress
        }
        let user = try User(dictionary: ["contactDetail": values], sessionId: nil)
        user.address = address

        let country = values[CountryActionableRow.country.rawValue] as? Country
        let isoCountryCode = country?.isoCode ?? "GB"
        let doubleOptIn = isoCountryCode == SupportedLanguage.german.countryCode.uppercased()

        return RegisterParameters(
            user: user,
            password: password,
            marketingOptIn: marketingOptIn,
            doubleOptIn: doubleOptIn,
            isoCountryCode: isoCountryCode,
            brandCodes: [.premierInn]
        )
    }

    private static func address(withValues values: PIDictionary?) throws -> Address? {
        var addressDictionary = PIDictionary()

        if let postcode = values?[CountryActionableRow.postCode.rawValue] as? String {
            addressDictionary["postcode"] = postcode
        }
        if let line1 = values?[CountryActionableRow.addressLine1.rawValue] as? String {
            addressDictionary["line1"] = line1
        }
        if let line2 = values?[CountryActionableRow.addressLine2.rawValue] as? String {
            addressDictionary["line2"] = line2
        }
        if let line3 = values?[CountryActionableRow.addressLine3.rawValue] as? String {
            addressDictionary["line3"] = line3
        }
        if let country = values?[CountryActionableRow.country.rawValue] as? Country {
            addressDictionary["countryCode"] = country.isoCode
            addressDictionary["country"] = country.name
        }

        if let company = values?[GuestDetailsRow.companyName.rawValue] as? String {
            addressDictionary["companyName"] = company
        }

        if let type = values?[GuestDetailsRow.addressType.rawValue + Constants.bookerSuffix] as? AddressType {
            addressDictionary["type"] = type
        }
        return try Address(dictionary: addressDictionary)
    }
}

extension RegisterInteractor: RegisterInteractorInput {
    var marketingPreferenceViewModel: MarketingPreferenceViewModelType {
        let isOptIn = LanguageManager.supportedLanguage == .english
        return MarketingPreferenceViewModel(isOptIn: isOptIn)
    }

    var registerTracking: RegisterTracking {
        (
            PIAnalytics.StateNames.register,
            PIAnalytics.StateTypes.myPI
        )
    }
    var shouldUpdateBiometricSettings: Bool { BiometricAuthenticationManager.userSettingNeedsUpdate }

    func performRegistration(with registerParameters: RegisterParameters, completion: @escaping (Error?) -> Void) {
        guard let email = registerParameters.user.emailAddress else {
            completion(RegisterInteractorError.missingEmailAddress)
            return
        }

		let sensorData = AkamaiProtection.sensorData

		registerDataManager?.register(withRegisterParameters: registerParameters, sensorData: sensorData) { success, error in
            if let error = error {
                // shouldn't we logout here too?

                return completion(error.inspect(email: email))
            }

            guard success == true else {
                self.registerDataManager?.logout()

                return completion(error)
            }

            DispatchQueue.main.asyncAfter(deadline: .now() + Constants.loginDelay) {
                self.performDelayedLogin(
                    email: email,
                    password: registerParameters.password,
                    completion: completion
                )
            }
        }
    }

    private func performDelayedLogin(
        email: String,
        password: String,
        completion: @escaping (Error?) -> Void
    ) {
        registerDataManager?.login(
            withUsername: email,
            password: password,
            isBusiness: false
        ) { [weak self] result in
            self?.handleLoginResult(
                result,
                credentials: (
                    username: email,
                    password: password,
                    business: false
                ),
                completion: completion
            )
        }
    }

    private func handleLoginResult(
        _ result: Result<Bool>,
        credentials: AuthCredentials,
        completion: @escaping (Error?) -> Void
    ) {
        switch result {
        case .success:
            // Dont need to handle business yet
            UserDefaults.standard.set(credentials.username, forKey: .storedUsernameKey)
            SettingsManager.sharedInstance.shouldAttemptAutoLogin = true
            User.saveCredentials(credentials)

            registerDataManager?.getUser(
                userId: credentials.username,
                isBusiness: UserSessionManager.sharedInstance.currentUser?.isBusiness ?? false
            ) { result in
                DispatchQueue.main.async {
                    let error = result.handle()
                    self.registerDataManager?
                        .refreshStays(
                            for: UserSessionManager.sharedInstance.currentUser,
                            shouldAttemptLogin: false
                        ) { _ in }
                    completion(error)
                }
            }

        case .failure(let error):
            AnalyticsManager.shared.track(
                error: error,
                name: PIAnalytics.Error.remoteLoginError
            )

            DispatchQueue.main.async {
                completion(RegisterInteractorError.automaticLoginFailed)
            }
        }
    }

    func biometricStatusChanged(enabled: Bool) {
        BiometricAuthenticationManager.status = enabled ? .enabled : .disabled
    }
}

private extension RegisterInteractor {
    struct MarketingPreferenceViewModel: MarketingPreferenceViewModelType {
        let isOptIn: Bool
    }
}
