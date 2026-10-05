//
//  BiometricAuthenticationManager.swift
//  PremierInn
//
//  Created by Vasileios Loumanis on 09/12/2016.
//  Copyright © 2016 Whitbread. All rights reserved.
//

import UIKit
import LocalAuthentication
import SimpleNetwork

enum BiometricAuthenticationStatus: String {
    case unknown
    case enabled
    case disabled
}

enum BiometricError: LocalizedError {
    case notAvailable
    case notEnabled
    case credentialsNotAvailable

    var errorDescription: String? {
        switch self {
        default:
            return String(describing: self)
        }
    }
}

enum BiometryTypeRetro {
    case touchId
    case faceId
    case none
}

private extension String {
    static let biometricAuthStatusKey = "TouchIDAuthenticationStatus"
}

extension BiometryTypeRetro {
    var title: String? {
        switch self {
        case .touchId:
            return PILocalizedString("touchIdButtonTitle", comment: "Touch ID button title")
        case .faceId:
            return PILocalizedString("faceIdButtonTitle", comment: "Face ID button title")
        default:
            return nil
        }
    }

    var loginMessage: String {
        switch self {
        case .touchId:
            return PILocalizedString("touchIdLoginMessage", comment: "Touch ID login message")
        case .faceId:
            return PILocalizedString("faceIdLoginMessage", comment: "Face ID login message")
        default:
            return ""
        }
    }

    var image: UIImage? {
        switch self {
        case .touchId:
            return #imageLiteral(resourceName: "touchID").withRenderingMode(.alwaysOriginal)
        case .faceId:
            return #imageLiteral(resourceName: "faceId").withRenderingMode(.alwaysOriginal)
        default:
            return nil
        }
    }

    var buttonTitle: String? {
        switch self {
        case .touchId:
            return PILocalizedString("loginTouchIdButtonTitle", comment: "Login screen: Touch ID button title")
        case .faceId:
            return PILocalizedString("loginFaceIdButtonTitle", comment: "Login screen: Face ID button title")
        default:
            return nil
        }
    }

    var enableTitle: String? {
        switch self {
        case .touchId:
            return PILocalizedString("touchIdAlertTitle", comment: "Enable Touch ID")
        case .faceId:
            return PILocalizedString("faceIdAlertTitle", comment: "Enable Face ID")
        default:
            return nil
        }
    }

    var enableMessage: String? {
        switch self {
        case .touchId:
            return PILocalizedString("touchIdAlertMessage", comment: "Enable Touch ID message")
        case .faceId:
            return PILocalizedString("faceIdAlertMessage", comment: "Enable Face ID message")
        default:
            return nil
        }
    }

    var enableAction: String? {
        switch self {
        case .touchId:
            return PILocalizedString("touchIdAlertEnableAction", comment: "Enable Touch ID action")
        case .faceId:
            return PILocalizedString("faceIdAlertEnableAction", comment: "Enable Face ID action")
        default:
            return nil
        }
    }

    var cancelAction: String? {
        switch self {
        case .touchId:
            return PILocalizedString("touchIdAlertCancelAction", comment: "Cancel Touch ID action")
        case .faceId:
            return PILocalizedString("faceIdAlertCancelAction", comment: "Cancel Face ID action")
        default:
            return nil
        }
    }

    var errorTitle: String? {
        switch self {
        case .touchId:
            return PILocalizedString("touchIdErrorAlertTitle", comment: "authenticate Touch ID title")
        case .faceId:
            return PILocalizedString("faceIdErrorAlertTitle", comment: "authenticate Face ID title")
        default:
            return nil
        }
    }
}

enum BiometricAuthenticationManager {
    // MARK: - Properties

    static var status: BiometricAuthenticationStatus {
        get {
            guard let value = UserDefaults.standard.string(forKey: .biometricAuthStatusKey) else { return .unknown }

            return BiometricAuthenticationStatus(rawValue: value) ?? .unknown
        }
        set {
            if newValue == .disabled {
                User.removedStoredCredentials()
            }

            UserDefaults.standard.set(newValue.rawValue, forKey: .biometricAuthStatusKey)
        }
    }

    static var biometricAuthenticationAvailable: Bool { LAContext().canEvaluatePolicy(
        .deviceOwnerAuthenticationWithBiometrics,
        error: nil
    ) }

    static var userSettingNeedsUpdate: Bool {
        guard biometricAuthenticationAvailable else { return false }

        return status == .enabled ? false : true
    }

    static var biometryTypeAvailable: BiometryTypeRetro {
        guard biometricAuthenticationAvailable else { return .none }

        let context = LAContext()
        context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: nil)

        switch context.biometryType {
        case .faceID:
            return .faceId
        case .touchID:
            return .touchId
        default:
            return .none
        }
    }

    // MARK: - Methods

    static func biometricCredentialsAreValid(for username: String?, business: Bool = false) -> Bool {
        User.storedCredentials(for: username, business: business) != nil
    }

    static func retrieveAuthCredentials(
        for username: String?,
        business: Bool,
        completion: @escaping (Result<AuthCredentials>) -> Void
    ) {
        guard biometricAuthenticationAvailable else {
            completion(.failure(error: BiometricError.notAvailable))
            return
        }

        guard status == .enabled else {
            completion(.failure(error: BiometricError.notEnabled))
            return
        }

        LAContext().evaluatePolicy(
            LAPolicy.deviceOwnerAuthenticationWithBiometrics,
            localizedReason: biometryTypeAvailable.loginMessage
        ) { success, error in
            DispatchQueue.main.async {
                if let error = error, success == false {
                    completion(.failure(error: error))
                } else {
                    guard let credentials = User.storedCredentials(for: username, business: business)
                        else { return completion(.failure(error: BiometricError.credentialsNotAvailable)) }

                    completion(.success(result: credentials))
                }
            }
        }
    }
}
