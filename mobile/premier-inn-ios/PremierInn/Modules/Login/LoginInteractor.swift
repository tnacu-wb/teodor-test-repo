//
//  LoginInteractor.swift
//  PremierInn
//
//  Created by Marcello Mascia on 04/09/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Foundation
import Formeka
import SimpleNetwork

extension String {
	static let storedUsernameKey = "storedUsername"
    static let storedBusinessUsernameKey = "storedBusinessUsername"
    static let storedBusinessKey = "storedBusiness"
}

protocol LoginInteractorOutput {
    func login(
        withUsername username: String,
        password: String,
        isBusiness: Bool,
        completion: @escaping (Result<Bool>) -> Void
    )
    func getUser(userId: String, isBusiness: Bool, completion: @escaping (Result<User>) -> Void)
    func refreshStays(for user: User?, shouldAttemptLogin: Bool, completion: @escaping (Bool?) -> Void)
	func getCompany(
	    companyId: String,
	    sensorData: String,
	    completion: @escaping (_ company: Company?, _ error: Error?) -> Void
	)
}

enum LoginInteractorError: LocalizedError {
	case viewModelNotFound
	case userNameNotAvailable
	case userNameIsEmpty
	case passwordNotAvailable
	case passwordIsEmpty
    case companyIdMissingForBB
    case failedToRetrieveCompany

	var errorDescription: String? {
        switch self {
        case .viewModelNotFound, .companyIdMissingForBB:
            return PILocalizedString("loginScreenGenericError", comment: "Login screen: generic error")
        default:
            return PILocalizedString("loginUsernamePasswordError", comment: "Login screen: email or password error message")
        }
	}
}

protocol LoginInteractorInput {
	var userName: String? { get }
	var shouldShowBiometricRow: Bool { get }
    var shouldUpdateBiometricSettings: Bool { get }

    var isBusinessLogin: Bool { get }
    var hasComeFromSplashSscreen: Bool { get }
    var shouldPromptUserAboutLoggingIntoBBMidFlow: Bool { get }
    var isCurrentlyInBookingFlow: Bool { get }

    func validate(viewModel: FormekaViewModel?) throws -> AuthCredentials
	func login(with credential: AuthCredentials, completion: @escaping (_ error: Error?) -> Void)
	func authenticateWithBiometricId(completion: @escaping (_ credential: AuthCredentials?, _ error: Error?) -> Void)

    func selectedBusinessBooker()
    func selectedPersonalAccount()
    func userHasSeenBBLoginPrompt()
}

class LoginInteractor {
    var asBusiness: Bool
    var fromSplashScreen: Bool
    var isInBookingFlow: Bool

    static var analyticsDictionary: PIDictionary {
        let loggedIn: LoggedInAnalytic = UserSessionManager.sharedInstance.currentUser != nil ? .loggedIn : .notLoggedIn

        var dictionary: PIDictionary = [:]
        dictionary[PIAnalytics.Keys.environment] = AnalyticsConstants.environment
        dictionary[PIAnalytics.Keys.userLogin] = loggedIn.rawValue
        dictionary[PIAnalytics.Keys.timeZone] = TimeZone.current.description
        dictionary[PIAnalytics.Keys.language] = Locale.current.language.languageCode?.identifier ?? "n/a"
        dictionary[PIAnalytics.Keys.screenType] = PIAnalytics.StateTypes.myPI
        dictionary[PIAnalytics.Keys.loginSuccess] = 1
        dictionary[PIAnalytics.Keys.userID] = UserSessionManager.sharedInstance.currentUser?.customerAccountId
        return dictionary
    }

    init(asBusiness: Bool, fromSplashScreen: Bool, andFromBookingFlow fromBookingFlow: Bool) {
        self.asBusiness = asBusiness
        self.fromSplashScreen = fromSplashScreen
        self.isInBookingFlow = fromBookingFlow
    }

    var dataManager: LoginInteractorOutput?
}

extension LoginInteractor: LoginInteractorInput {
    var userName: String? {
        let usernameKey: String = { asBusiness ? .storedBusinessUsernameKey : .storedUsernameKey }()

        return UserDefaults.standard.string(forKey: usernameKey)
    }
	var shouldShowBiometricRow: Bool {
		guard BiometricAuthenticationManager.biometricAuthenticationAvailable else { return false }
		guard BiometricAuthenticationManager.status == .enabled else { return false }

        return BiometricAuthenticationManager.biometricCredentialsAreValid(for: userName, business: asBusiness)
	}
    var shouldUpdateBiometricSettings: Bool { BiometricAuthenticationManager.userSettingNeedsUpdate }

    var hasComeFromSplashSscreen: Bool { fromSplashScreen }
    var isBusinessLogin: Bool { asBusiness }
    var shouldPromptUserAboutLoggingIntoBBMidFlow: Bool {
        self.isInBookingFlow && !(BookingDetails.sharedInstance.hasSeenMidFlowBBLoginPrompt ?? false) }
    var isCurrentlyInBookingFlow: Bool { self.isInBookingFlow }

    func validate(viewModel: FormekaViewModel?) throws -> AuthCredentials {
		guard let viewModel = viewModel else { throw LoginInteractorError.viewModelNotFound }

		try viewModel.validate()

		let values = viewModel.values

		guard let username = values[LoginRow.email.rawValue] as? String
		    else { throw LoginInteractorError.userNameNotAvailable }
		guard username.isNotEmpty else { throw LoginInteractorError.userNameIsEmpty }
		guard let password = values[LoginRow.password.rawValue] as? String
		    else { throw LoginInteractorError.passwordNotAvailable }
		guard password.isNotEmpty else { throw LoginInteractorError.passwordIsEmpty }

        return (username: username, password: password, business: isBusinessLogin)
	}

	func login(with credential: AuthCredentials, completion: @escaping (_ error: Error?) -> Void) {
        dataManager?.login(
            withUsername: credential.username,
            password: credential.password,
            isBusiness: credential.business
        ) { result in
			switch result {
			case .success:

                self.dataManager?.getUser(userId: credential.username, isBusiness: self.isBusinessLogin) { result in
                    DispatchQueue.main.async {
                        let error = result.handle()

                        if error == nil {
                            guard self.isBusinessLogin else {
                                self.dataManager?.refreshStays(
                                    for: UserSessionManager.sharedInstance.currentUser,
                                    shouldAttemptLogin: false
                                ) {_ in }
                                return completion(error)
                            }

                            guard let companyId = UserSessionManager.sharedInstance.currentUser?.companyId
                                else { return completion(error ?? LoginInteractorError.companyIdMissingForBB) }

							let sensorData = AkamaiProtection.sensorData

							self.dataManager?.getCompany(companyId: companyId, sensorData: sensorData) { company, error in
                                guard let company = company else {
                                    self.handleCompanyFailure()
                                    return completion(error ?? LoginInteractorError.failedToRetrieveCompany)
                                }
                                self.dataManager?.refreshStays(
                                    for: UserSessionManager.sharedInstance.currentUser,
                                    shouldAttemptLogin: false
                                ) {_ in }
                                UserSessionManager.sharedInstance.currentUser?.company = company
                                BookingDetails.sharedInstance.update(with: company)
                                NotificationCenter.default.post(name: .userDidChange, object: nil)

                                completion(error)
                            }
                        } else {
                            completion(error)
                        }
                    }
                }

			case .failure(let error):

				DispatchQueue.main.async {
					completion(error)
				}
			}
		}
	}

	func authenticateWithBiometricId(completion: @escaping (_ credential: AuthCredentials?, _ error: Error?) -> Void) {
        BiometricAuthenticationManager.retrieveAuthCredentials(for: userName, business: asBusiness) { result in
            switch result {
            case .success(let credentials):
                completion(
                    (username: credentials.username, password: credentials.password, business: credentials.business),
                    nil
                )
            case .failure(let error):
                completion(nil, error)
            }
        }
    }

    func selectedBusinessBooker() {
        asBusiness = true
    }

    func selectedPersonalAccount() {
        asBusiness = false
    }

    func userHasSeenBBLoginPrompt() {
        BookingDetails.sharedInstance.hasSeenMidFlowBBLoginPrompt = true
    }

    func handleCompanyFailure() {
        // We need to do this as if the company call fails we do not want them logged in so we need to log them out, as we need them logged in to get the company theres no way to avoid a log in and then log out scenario
        UserSessionManager.sharedInstance.piUserLoggedOut()
    }
}

extension RequestsManager: LoginInteractorOutput {
}
