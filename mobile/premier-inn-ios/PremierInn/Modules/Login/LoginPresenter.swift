//
//  LoginPresenter.swift
//  PremierInn
//
//  Created by Marcello Mascia on 04/09/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import Formeka
import SimpleNetwork
import Foundation


protocol LoginPresenterInput {
	var userName: String? { get }
	var shouldShowLoginError: Bool { get }
	var shouldShowBiometricRow: Bool { get }

    var isBusinessLogin: Bool { get }
    var hasComeFromSplashSscreen: Bool { get }
    var isInBookingFlow: Bool { get }
    var shouldOverrideDefaultingToBusinessTab: Bool { get }

	func viewIsReady()
	func submitForm(viewModel: FormekaViewModel?)
	func authenticateWithBiometric()
	func dismissLoginError()
    func forgotPasswordDidTap(email: String?)
    func dismissLoginDidTap()
    func updateEmail(with email: String)

    func selectedBusinessBooker()
    func selectedPersonalAccount()
}

class LoginPresenter {
    weak var view: LoginViewInput?
    var interactor: LoginInteractorInput?
    var router: LoginRouterInput?
	var loginError: Error?
}

extension LoginPresenter: LoginPresenterInput {
	var shouldShowLoginError: Bool { loginError != nil ? true : false }
	var shouldShowBiometricRow: Bool { interactor?.shouldShowBiometricRow ?? false }
	var userName: String? { interactor?.userName }
    var isBusinessLogin: Bool { interactor?.isBusinessLogin ?? false }
    var isInBookingFlow: Bool { interactor?.isCurrentlyInBookingFlow ?? false }
    var hasComeFromSplashSscreen: Bool { interactor?.hasComeFromSplashSscreen ?? false }
    var shouldOverrideDefaultingToBusinessTab: Bool {
    	interactor?.isCurrentlyInBookingFlow ?? false && interactor?.shouldPromptUserAboutLoggingIntoBBMidFlow ?? false }

    func viewIsReady() {
        view?.setup(withTitle: PILocalizedString("loginTitle", comment: "Login screen: main title"))

        view?.reloadViewModel()
    }

    func submitForm(viewModel: FormekaViewModel?) {
		guard let interactor = interactor else { return }

		view?.stopEditing()

		do {
			let credential = try interactor.validate(viewModel: viewModel)

			view?.lock()
            view?.updateSubmitButton(isLoading: true)

			interactor.login(with: credential) { (error) in
                BARTDowntimeHandler.handle(
                	error: error,
                	withParentNavigationController: self.view?.parentNavigationController
                )

				self.view?.unlock()
				self.view?.updateSubmitButton(isLoading: false)

				if let error = error {
					self.loginError = error

                    AnalyticsManager.shared.track(error: error, name: PIAnalytics.Error.remoteLoginError)

					self.view?.reloadViewModel()
				} else {
                    SettingsManager.sharedInstance.shouldAttemptAutoLogin = true
                    User.saveCredentials(credential)

                    UserDefaults.standard.set(credential.business, forKey: .storedBusinessKey)
                    UserDefaults.standard.set(
                    	credential.username,
                    	forKey: (credential.business ? .storedBusinessUsernameKey : .storedUsernameKey)
                    )

					if self.interactor?.shouldUpdateBiometricSettings == true {
						self.view?.askForBiometricActivation { _ in
                            if interactor.isCurrentlyInBookingFlow && interactor.isBusinessLogin {
                                self.router?.goHome()
                            } else {
                                self.view?.completeSession()
                            }
						}
					} else {
                        if interactor.isCurrentlyInBookingFlow && interactor.isBusinessLogin {
                            self.router?.goHome()
                        } else {
                            self.view?.completeSession()
                        }
					}
				}
			}
		} catch {
			if let error = error as? RowValidatorError, let indexPath = viewModel?.indexPath(for: error.row) {
				view?.focusFormRow(at: indexPath)
			} else {
				view?.presentAlertWith(title: PILocalizedString("loginErrorTitle", comment: "Login screen: error title"), error: error)
			}
		}
	}

	func authenticateWithBiometric() {
		interactor?.authenticateWithBiometricId { (credential, error) in
			if let credential = credential {
				self.view?.biometricAuthenticationDidFinish(credential: credential)
				self.view?.submitButtonDidTap()
			} else {
				self.view?.presentAlertWith(
					title: BiometricAuthenticationManager.biometryTypeAvailable
						.errorTitle ?? PILocalizedString("touchIdErrorAlertTitle", comment: "Touch ID authentication"),
					error: error
				)
			}
		}
    }

	func dismissLoginError() {
		loginError = nil

		view?.reloadViewModel()
	}

    func forgotPasswordDidTap(email: String?) {
        router?.showForgotPasswordModule(email: email, isBusiness: interactor?.isBusinessLogin ?? false)
    }

    func dismissLoginDidTap() {
        router?.dismissLoginView()
    }

    func updateEmail(with email: String) {
        view?.update(with: email)
    }

    func selectedBusinessBooker() {
        interactor?.selectedBusinessBooker()
        view?.reloadViewModel()

        if interactor?.shouldPromptUserAboutLoggingIntoBBMidFlow ?? false {
            view?.promptUserAboutLoggingInAndLosingBookingProgress()
            interactor?.userHasSeenBBLoginPrompt()
        }
    }

    func selectedPersonalAccount() {
        interactor?.selectedPersonalAccount()
        view?.reloadViewModel()
    }
}
