//
//  LoginViewTests.swift
//  PremierInn
//
//  Created by Marcello Mascia on 06/09/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import XCTest
import Formeka
@testable import PremierInn

private class MockLoginPresenter: LoginPresenterInput {

    var isInBookingFlow: Bool = false
    var shouldOverrideDefaultingToBusinessTab: Bool = false
    var dismissLoginDidCall = false
    var isBusinessLogin: Bool = false
    var hasComeFromSplashSscreen: Bool = false
    var viewIsReadyDidCall = false
    var submitFormDidCall = false
    var biometricAuthDidCall = false
    var dismissLoginErrorDidCall = false
    var shouldShowTouchRow = false
    var forgotPasswordDidCall = false
    var shouldShowLoginError: Bool { return false }
    var userName: String? { return nil }
    var shouldShowBiometricRow: Bool { return shouldShowTouchRow }

    func viewIsReady() {

        viewIsReadyDidCall = true
    }

    func submitForm(viewModel: FormekaViewModel?) {

        submitFormDidCall = true
    }

    func authenticateWithBiometric() {

        biometricAuthDidCall = true
    }

    func dismissLoginError() {

        dismissLoginErrorDidCall = true
    }

    func forgotPasswordDidTap(email: String?) {

        forgotPasswordDidCall = true
    }

    func dismissLoginDidTap() {

        dismissLoginDidCall = true
    }

    func updateEmail(with email: String) {}

    func selectedBusinessBooker() {}

    func selectedPersonalAccount() {}
}

class LoginViewTests: XCTestCase {

    // MARK: - Properties

    fileprivate var presenter: MockLoginPresenter!
    var controller: LoginViewController!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        presenter = MockLoginPresenter()

        controller = LoginViewController()
        controller.presenter = presenter
    }
    
    override func tearDown() {

        presenter = nil
        controller = nil

        super.tearDown()
    }

    // MARK: - Tests

    func testLoginView() {

        controller.viewDidLoad()

        XCTAssertTrue(presenter.viewIsReadyDidCall)
    }

    func testFormSubmission() {

        controller.submitButtonDidTap()

        XCTAssertTrue(presenter.submitFormDidCall)
    }

    func testBiometricAuth() {

        controller.biometricButtonDidTap()

        XCTAssertTrue(presenter.biometricAuthDidCall)
    }

    func testFormekaViewModel() {

        presenter.shouldShowTouchRow = false
        controller.reloadViewModel()

        let sections = controller.viewModel?.sections

        XCTAssertEqual(sections?.count, 2)

        var tags = [String]()

        for section in sections! {
            tags.append(contentsOf: section.rows.map { $0.tag })
        }

        XCTAssertEqual(tags, [RoomTypeRow.roomTypesSegmentsCell.rawValue, LoginRow.email.rawValue, LoginRow.password.rawValue, LoginRow.submit.rawValue])
    }

    func testFormekaViewModel_Biometric() {

        presenter.shouldShowTouchRow = true
        controller.reloadViewModel()

        let sections = controller.viewModel?.sections

        XCTAssertEqual(sections?.count, 2)

        var tags = [String]()

        for section in sections! {
            tags.append(contentsOf: section.rows.map { $0.tag })
        }

        XCTAssertEqual(tags, [RoomTypeRow.roomTypesSegmentsCell.rawValue, LoginRow.email.rawValue, LoginRow.password.rawValue, LoginRow.submit.rawValue, LoginRow.biometricAuth.rawValue])
    }

    func testDismissLogin() {

        controller.cancelButtonDidTap()
        XCTAssertTrue(presenter.dismissLoginDidCall)
    }

    func testForgotPassword() {

        controller.forgotPasswordButtonDidTap(email: nil)
        XCTAssertTrue(presenter.forgotPasswordDidCall)
    }
}
