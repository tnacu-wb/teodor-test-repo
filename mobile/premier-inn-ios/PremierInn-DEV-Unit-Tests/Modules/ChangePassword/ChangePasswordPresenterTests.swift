//
//  ChangePasswordPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 18/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

private class MockInteractor: ChangePasswordInteractorProtocol {

    var getTitleDidCall = false
    var getTrackingDidCall = false
    var changePasswordDidCall = false

    var shouldFailPasswordChange = false

    var viewTitle: String {

        getTitleDidCall = true

        return ""
    }

    var changePasswordTracking: ChangePasswordTracking {

        getTrackingDidCall = true

        return (
            "",
            ""
        )
    }

    var isBusinessLogin: Bool { return false }


    func changePassword(existingPassword: String, newPassword: String, newPasswordConfirm: String) throws {

        changePasswordDidCall = true

        if shouldFailPasswordChange {
            throw ChangePasswordInteractorError.currentPasswordInvalid
        }
    }
}

private class MockView: ChangePasswordViewProtocol {

    var loadViewModelDidCall = false
    var setTitleDidCall = false
    var showErrorDidCall = false
    var invalidFormatPasswordDidCall = false
    var passwordChangeFailedDidCall = false
    var errorOccuredWhenChangingPasswordDidCall = false

    func loadViewModel() {

        loadViewModelDidCall = true
    }

    func setTitle(title: String) {

        setTitleDidCall = true
    }

    func showError(string: String) {

        showErrorDidCall = true
    }

    func invalidFormatPassword() {

        invalidFormatPasswordDidCall = true
    }

    func passwordChangeFailed(message: String) {

        passwordChangeFailedDidCall = true
    }

    func errorOccuredWhenChangingPassword(error: Error) {

        errorOccuredWhenChangingPasswordDidCall = true
    }
}

private class MockRouter: ChangePasswordRouterProtocol {

    var passwordDidChangeDidCall = false
    var showForgotPasswordModuleDidCall = false

    func passwordDidChange() {

        passwordDidChangeDidCall = true
    }

    func showForgotPasswordModule(email: String?, isBusiness: Bool) {

        showForgotPasswordModuleDidCall = true
    }
}

@testable import PremierInn

class ChangePasswordPresenterTests: XCTestCase {

    // MARK: - Properties

    fileprivate var mockInteractor: MockInteractor?
    fileprivate var mockView: MockView?
    fileprivate var mockRouter: MockRouter?
    fileprivate var presenter: ChangePasswordPresenter?
    fileprivate var analytics: MockAnalyticsManager!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        mockInteractor = MockInteractor()
        mockView = MockView()
        mockRouter = MockRouter()
        presenter = ChangePasswordPresenter()

        presenter?.interactor = mockInteractor
        presenter?.view = mockView
        presenter?.router = mockRouter

        analytics = MockAnalyticsManager()
        presenter?.analytics = analytics
    }

    override func tearDown() {

        presenter = nil
        analytics = nil

        super.tearDown()
    }

    // MARK: - Tests

    func testView_whenPasswordChanged_invokesTrackAction() {

        presenter?.passwordChanged(message: "")

        let action = analytics.actions.first
        let userInfo = analytics.userInfos.first

        XCTAssertEqual(action, "iOS: Password Changed")
        XCTAssertNotNil(userInfo as Any?)
    }

    func testChangePasswordDidTap() {

        XCTAssertNoThrow(presenter?.changePasswordButtonDidTap(existingPassword: "", newPassword: "", newPasswordConfirm: ""))

        mockInteractor?.shouldFailPasswordChange = true

        XCTAssertThrowsError(try mockInteractor?.changePassword(existingPassword: "", newPassword: "", newPasswordConfirm: ""))

        presenter?.changePasswordButtonDidTap(existingPassword: "", newPassword: "", newPasswordConfirm: "")
        XCTAssert(mockView?.showErrorDidCall == true)
    }

    func testPasswordChanged() {

        presenter?.passwordChanged(message: "")

        XCTAssert(mockRouter?.passwordDidChangeDidCall == true)
    }

    func testPasswordChangeFailed() {

        self.presenter?.passwordChangeFailed(message: "")

        DispatchQueue.main.async {
            XCTAssert(self.mockView?.passwordChangeFailedDidCall == true)
        }
    }

    func testErrorOccured() {

        self.presenter?.errorOccuredWhenChangingPassword(error: ChangePasswordInteractorError.currentPasswordInvalid)

        DispatchQueue.main.async {
            XCTAssert(self.mockView?.errorOccuredWhenChangingPasswordDidCall == true)
        }
    }
}
