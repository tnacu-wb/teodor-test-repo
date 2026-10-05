//
//  LoginPresenterTests.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/09/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import XCTest
import Formeka
import SimpleNetwork
@testable import PremierInn

private class MockView: LoginViewInput {

    func promptUserAboutLoggingInAndLosingBookingProgress() {
        return
    }

    var parentNavigationController: UINavigationController? {
        return nil
    }

	var setupDidCall = false
    var reloadModelDidCall = false
	var stopEditingDidCall = false
	var alertDidShow = false
	var focusDidCall = false
	var lockDidCall = false
	var unlockDidCall = false
	var biometricQuestionDidCall = false
	var biometricAuthDidFinshDidCall = false
	var completeSessionDidCall = false
	var updateSubmitButtonDidCall = false
	var submitButtonDidCall = false
    var forgotPasswordDidCall = false

	var formekaViewController: FormekaViewController? {
		return nil
	}

    func setup(withTitle: String) {

		setupDidCall = true
	}

	func stopEditing() {

		stopEditingDidCall = true
	}

	func focusFormRow(at: IndexPath) {

		focusDidCall = true
	}

	func presentAlertWith(title: String, error: Error?) {

		alertDidShow = true
	}

	func lock() {

		lockDidCall = true
	}

	func unlock() {

		unlockDidCall = true
	}

	func askForBiometricActivation(completion: @escaping (BiometricAuthenticationStatus) -> Void) {

		biometricQuestionDidCall = true

		completion(.enabled)
	}

	func biometricAuthenticationDidFinish(credential: AuthCredentials) {

		biometricAuthDidFinshDidCall = true
	}

	func completeSession() {

		completeSessionDidCall = true
	}

    func updateSubmitButton(isLoading: Bool) {

		updateSubmitButtonDidCall = true
    }

    func reloadViewModel() {

        reloadModelDidCall = true
    }

	func submitButtonDidTap() {

		submitButtonDidCall = true
	}

    func forgotPasswordButtonDidTap(email: String?) {

        forgotPasswordDidCall = true
    }

    func update(with email: String) {
        
    }
}

private class MockInteractor: LoginInteractorInput {

    var shouldPromptUserAboutLoggingIntoBBMidFlow: Bool = false
    var isCurrentlyInBookingFlow: Bool = false
    var validateDidCall = false
	var loginDidCall = false
	var authenticateBiometricDidCall = false
    var shouldShowBiometricDidCall = false
    var userNameDidCall = false
    var isBusinessLogin: Bool = false
    var hasComeFromSplashSscreen: Bool = false
    var validationError: Error?
	var loginError: Error?
	var userName: String? {
		userNameDidCall = true

		return nil
	}
	var shouldShowBiometricRow: Bool {
		shouldShowBiometricDidCall = true

		return false
	}
    var shouldUpdateBiometricSettings: Bool { return true }

	func login(with credential: AuthCredentials, completion: @escaping (Error?) -> Void) {

		loginDidCall = true

		if let error = loginError { return completion(error) }

		completion(nil)
	}
    
    func userHasSeenBBLoginPrompt() {
        return
    }

	func userName(isBusiness: Bool) -> String? {

        userNameDidCall = true

		return nil
	}

	func validate(viewModel: FormekaViewModel?) throws -> AuthCredentials {

		if let error = validationError { throw error }

		validateDidCall = true

		return (username: "", password: "", false)
	}

	func authenticateWithBiometricId(completion: @escaping (AuthCredentials?, Error?) -> Void) {

		authenticateBiometricDidCall = true

        if let error = loginError { return completion(nil, error) }

		completion((username: "pippo", password: "baudo", business: false), nil)
	}

    func selectedPersonalAccount() {}

    func selectedBusinessBooker() {}
}

private class MockRouter: LoginRouterInput {

    var dismissDidCall = false
    var forgotPasswordDidCall = false

    var viewController: UIViewController?

    func dismissLoginView() {

        dismissDidCall = true
    }

    func showForgotPasswordModule(email: String?, isBusiness: Bool) {

        forgotPasswordDidCall = true
    }
    
    func goHome() {

        return
    }
}

class LoginPresenterTests: XCTestCase {

	private var view: MockView!
	private var interactor: MockInteractor!
    private var router: MockRouter!
	var presenter: LoginPresenter!

	override func setUp() {
        super.setUp()

		view = MockView()
		interactor = MockInteractor()
		presenter = LoginPresenter()
		presenter.view = view
		presenter.interactor = interactor

        router = MockRouter()
        router.viewController = UIViewController()

        presenter.router = router
    }
    
    override func tearDown() {

		view = nil
		interactor = nil
		presenter = nil
		router = nil

        super.tearDown()
    }
    
    func testLoginPresenter() {

		presenter.viewIsReady()

		XCTAssertTrue(view.setupDidCall)
		XCTAssertTrue(view.reloadModelDidCall)
	}

	func testFormSubmission() {

		presenter.submitForm(viewModel: FormekaViewModel(sections: []))

		XCTAssertTrue(view.stopEditingDidCall)
		XCTAssertTrue(interactor.validateDidCall)
		XCTAssertTrue(view.lockDidCall)
		XCTAssertTrue(view.updateSubmitButtonDidCall)
		XCTAssertTrue(interactor.loginDidCall)
		XCTAssertTrue(view.unlockDidCall)
		XCTAssertTrue(view.completeSessionDidCall)
		XCTAssertTrue(view.biometricQuestionDidCall)
	}

	func testLogin_GenericError() {

		interactor.validationError = LoginInteractorError.passwordNotAvailable

		presenter.submitForm(viewModel: FormekaViewModel(sections: []))

		XCTAssertTrue(view.alertDidShow)
		XCTAssertFalse(view.focusDidCall)
	}

	func testLogin_LoginError() {

		interactor.loginError = LoginInteractorError.passwordNotAvailable

		presenter.submitForm(viewModel: FormekaViewModel(sections: []))

		XCTAssertTrue(view.stopEditingDidCall)
		XCTAssertTrue(interactor.validateDidCall)
		XCTAssertTrue(view.lockDidCall)
		XCTAssertTrue(view.updateSubmitButtonDidCall)
		XCTAssertTrue(interactor.loginDidCall)
		XCTAssertTrue(view.unlockDidCall)
		XCTAssertFalse(view.completeSessionDidCall)
		XCTAssertFalse(view.biometricQuestionDidCall)
	}

	func testBiometricLogin() {

		presenter.authenticateWithBiometric()

		XCTAssertTrue(interactor.authenticateBiometricDidCall)
		XCTAssertTrue(view.biometricAuthDidFinshDidCall)
		XCTAssertTrue(view.submitButtonDidCall)
	}

    func testBiometricLogin_Error() {

        interactor.loginError = LoginInteractorError.passwordNotAvailable

        presenter.authenticateWithBiometric()

        XCTAssertTrue(interactor.authenticateBiometricDidCall)
        XCTAssertFalse(view.biometricAuthDidFinshDidCall)
        XCTAssertFalse(view.reloadModelDidCall)
        XCTAssertTrue(view.alertDidShow)
    }

	func testDismissLoginError() {

		presenter.dismissLoginError()

		XCTAssertTrue(view.reloadModelDidCall)
	}

    func testBiometricRow() {

        _ = presenter.shouldShowBiometricRow

        XCTAssertTrue(interactor.shouldShowBiometricDidCall)
    }

    func testUsername() {

        _ = presenter.userName

        XCTAssertTrue(interactor.userNameDidCall)
    }

    func testLoginError() {

        presenter.loginError = LoginInteractorError.passwordNotAvailable
        XCTAssertEqual(presenter.shouldShowLoginError, true)

        presenter.loginError = nil
        XCTAssertEqual(presenter.shouldShowLoginError, false)
    }

    func testDismiss() {

        presenter.dismissLoginDidTap()
        XCTAssertTrue(router.dismissDidCall)
    }

    func testForgotPassword() {

        presenter.forgotPasswordDidTap(email: nil)
        XCTAssertTrue(router.forgotPasswordDidCall)
    }

}
