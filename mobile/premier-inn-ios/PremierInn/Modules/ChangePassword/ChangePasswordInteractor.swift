//
//  ChangePasswordInteractor.swift
//  PremierInn
//
//  Created by Freddie Parks on 14/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import SimpleNetwork
import Foundation

typealias ChangePasswordTracking = (screenName: String, screenType: String)

enum ChangePasswordInteractorError: LocalizedError {
    case currentPasswordInvalid
    case newPasswordsDontMatch
    case newPasswordInvalidLength
    case loginError

    var errorDescription: String? {
        switch self {
        case .currentPasswordInvalid:
            return PILocalizedString("changePasswordCurrentInvalidErrorMessage")
        case .newPasswordsDontMatch:
            return PILocalizedString("changePasswordNewNotSameAsConfirmationErrorMessage")
        case .newPasswordInvalidLength:
            return PILocalizedString("changePasswordNewInvalidErrorMessage")
        case .loginError:
            return PILocalizedString("loginUsernamePasswordError")
        }
    }
}

protocol ChangePasswordInteractorProtocol {
    var viewTitle: String { get }
    var changePasswordTracking: ChangePasswordTracking { get }
    var isBusinessLogin: Bool { get }
    func changePassword(existingPassword: String, newPassword: String, newPasswordConfirm: String) throws
}

protocol ChangePasswordInteractorDelegate: AnyObject {
    func passwordChanged(message: String)
    func passwordChangeFailed(message: String)
    func errorOccuredWhenChangingPassword(error: Error)
}

protocol ChangePasswordDataProvider {
    func login(
        withUsername username: String,
        password: String,
        isBusiness: Bool,
        completion: @escaping (Result<Bool>) -> Void
    )
    func getUser(userId: String, isBusiness: Bool, completion: @escaping (Result<User>) -> Void)
    func refreshStays(for user: User?, shouldAttemptLogin: Bool, completion: @escaping (Bool?) -> Void)
	func changePassword(
	    user: User,
	    existingPassword: String,
	    newPassword: String,
	    sensorData: String,
	    completion: @escaping (_ success: Bool, _ error: Error?) -> Void
	)
	func getCompany(
	    companyId: String,
	    sensorData: String,
	    completion: @escaping (_ company: Company?, _ error: Error?) -> Void
	)
}

extension RequestsManager: ChangePasswordDataProvider {}

class ChangePasswordInteractor {
    var asBusiness: Bool
    var viewTitle: String {
        PILocalizedString("changePasswordScreenTitle", comment: "")
    }
    weak var delegate: ChangePasswordInteractorDelegate?

    private var requestsManager: ChangePasswordDataProvider?

    init(dataProvider: ChangePasswordDataProvider = RequestsManager(), asBusiness: Bool) {
        self.asBusiness = asBusiness
        requestsManager = dataProvider
    }
    var isBusinessLogin: Bool { asBusiness }

    private func handleLoginResult(_ result: Result<Bool>, isBusiness: Bool, completion: @escaping (Error?) -> Void) {
        switch result {
        case .success:
            let email = UserSessionManager.sharedInstance.currentUser?.emailAddress ?? ""
            requestsManager?.getUser(
                userId: email,
                isBusiness: isBusiness
            ) { result in
                DispatchQueue.main.async {
                    let error = result.handle()
                    // For business users, fetch company details
                    if isBusiness, let companyId = UserSessionManager.sharedInstance.currentUser?.companyId {
                        let sensorData = AkamaiProtection.sensorData
                        self.requestsManager?.getCompany(companyId: companyId, sensorData: sensorData) { company, error in
                            if let company = company {
                                UserSessionManager.sharedInstance.currentUser?.company = company
                                BookingDetails.sharedInstance.update(with: company)
                                NotificationCenter.default.post(name: .userDidChange, object: nil)
                            }

                            self.requestsManager?.refreshStays(
                                for: UserSessionManager.sharedInstance.currentUser,
                                shouldAttemptLogin: false
                            ) { _ in
                                // Completion handler intentionally empty - we don't need to handle stays refresh result
                            }

                            completion(error)
                        }
                    } else {
                        // For leisure users, just refresh stays
                        self.requestsManager?.refreshStays(
                            for: UserSessionManager.sharedInstance.currentUser,
                            shouldAttemptLogin: false
                        ) { _ in
                            // Completion handler intentionally empty - we don't need to handle stays refresh result
                        }

                        completion(error)
                    }
                }
            }

        case .failure(let error):
            AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.remoteLoginError)

            DispatchQueue.main.async {
                completion(error)
            }
        }
    }
}

extension ChangePasswordInteractor: ChangePasswordInteractorProtocol {
    var changePasswordTracking: ChangePasswordTracking {
        (PIAnalytics.StateNames.changePassword, PIAnalytics.StateTypes.myPI) }

    func changePassword(existingPassword: String, newPassword: String, newPasswordConfirm: String) throws {
        guard newPasswordConfirm == newPassword else { throw ChangePasswordInteractorError.newPasswordsDontMatch }
        guard let user = UserSessionManager.sharedInstance.currentUser else { return }
        let isBusiness = user.isBusiness
        UserSessionManager.sharedInstance.refreshUser { success, _ in
            guard success else {
                self.delegate?.passwordChangeFailed(message: PILocalizedString("changePasswordFailMessage"))
                return
            }

			let sensorData = AkamaiProtection.sensorData

			self.requestsManager?.changePassword(
			    user: user,
			    existingPassword: existingPassword,
			    newPassword: newPassword,
			    sensorData: sensorData
			) { [weak self] success, error in
                if success {
                    guard let email = user.emailAddress else {
                        self?.delegate?.errorOccuredWhenChangingPassword(error: ChangePasswordInteractorError.loginError)
                        return
                    }

                    self?.requestsManager?
                        .login(withUsername: email, password: newPassword, isBusiness: isBusiness) { result in
                        self?.handleLoginResult(result, isBusiness: isBusiness) { error in
                            if let error = error {
                                self?.delegate?.passwordChangeFailed(message: error.localizedDescription)
                            } else {
                                SettingsManager.sharedInstance.shouldAttemptAutoLogin = true
                                User.saveCredentials((username: email, password: newPassword, business: isBusiness))

                                self?.delegate?.passwordChanged(message: PILocalizedString(
                                    "changePasswordSuccessMessage",
                                    comment: ""
                                ))
                            }
                        }
                    }
                } else {
                    self?.delegate?.passwordChangeFailed(message: PILocalizedString(
                        "changePasswordFailMessage",
                        comment: ""
                    ))
                }
            }
        }
    }
}
