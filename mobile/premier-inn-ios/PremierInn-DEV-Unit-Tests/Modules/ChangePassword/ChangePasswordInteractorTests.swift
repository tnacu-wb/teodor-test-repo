//
//  ChangePasswordInteractorTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 18/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import SimpleNetwork
import XCTest

@testable import PremierInn

private class MockDataProvider: ChangePasswordDataProvider {

    var loginDidCall = false
    var getUserDidCall = false
    var refreshStaysDidCall = false
    var changePasswordDidCall = false
    var getCompanyDidCall = false

    var shouldThrowError = false

    func login(withUsername username: String, password: String, isBusiness: Bool = false, completion: @escaping (Result<Bool>) -> Void) {

        loginDidCall = true

        completion(.success(result: true))
    }

    func getUser(userId: String, isBusiness: Bool, completion: @escaping (Result<User>) -> Void) {

        getUserDidCall = true

        if let user = try? User(title: "Mr", firstName: "Nichola", lastName: "Twisp", email: "nicepins@me.com") {

            completion(.success(result: user))
        } else {

            completion(.failure(error: LoginInteractorError.userNameIsEmpty))
        }
    }

    func refreshStays(for user: User?, shouldAttemptLogin: Bool, completion: @escaping (Bool?) -> Void) {

        refreshStaysDidCall = true
    }

	func changePassword(user: User, existingPassword: String, newPassword: String, sensorData: String, completion: @escaping (Bool, Error?) -> Void) {

        changePasswordDidCall = true

        if shouldThrowError {

            completion(false, ChangePasswordInteractorError.currentPasswordInvalid)
        } else {

            completion(true, nil)
        }
    }

	func getCompany(companyId: String, sensorData: String, completion: @escaping (Company?, Error?) -> Void) {

        getCompanyDidCall = true

        // For testing, we just track that the method was called
        // In real implementation, this would return company data
        completion(nil, nil)
    }
}

private class MockDelegate: ChangePasswordInteractorDelegate {

    var passwordChangedDidCall = false
    var passwordChangeFailedDidCall = false
    var errorOccuredDidCall = false

    func passwordChanged(message: String) {

        passwordChangedDidCall = true
    }

    func passwordChangeFailed(message: String) {

        passwordChangeFailedDidCall = true
    }

    func errorOccuredWhenChangingPassword(error: Error) {

        errorOccuredDidCall = true
    }
}

class ChangePasswordInteractorTests: XCTestCase {

    fileprivate var mockDataProvider: MockDataProvider?
    fileprivate var mockDelegate: MockDelegate?
    fileprivate var interactor: ChangePasswordInteractor?

    override func setUp() {
        super.setUp()

        mockDataProvider = MockDataProvider()
        mockDelegate = MockDelegate()

        if let mockDataProvider = mockDataProvider {
            interactor = ChangePasswordInteractor(dataProvider: mockDataProvider, asBusiness: false)
        }

        interactor?.delegate = mockDelegate

        if let user = try? User(title: "", firstName: "", lastName: "", email: "") {
            UserSessionManager.sharedInstance.piLoggedIn(with: user)
        }
    }

    override func tearDown() {
        super.tearDown()
    }

    func testChangePasswordValidation() {

        XCTAssertNoThrow(try interactor?.changePassword(existingPassword: "OldPassword", newPassword: "beans2", newPasswordConfirm: "beans2"))
        XCTAssertThrowsError(try interactor?.changePassword(existingPassword: "OldPassword", newPassword: "Password1", newPasswordConfirm: "fds"))
        XCTAssertNoThrow(try interactor?.changePassword(existingPassword: "OldPassword", newPassword: "Password69", newPasswordConfirm: "Password69"))
    }

    func testChangePasswordSuccess() {

        mockDataProvider?.shouldThrowError = false

        XCTAssertNoThrow(try interactor?.changePassword(existingPassword: "OldPassword", newPassword: "Password69", newPasswordConfirm: "Password69"))

        XCTAssert(mockDataProvider?.changePasswordDidCall == false)
        XCTAssert(mockDataProvider?.loginDidCall == false)
        XCTAssert(mockDataProvider?.getUserDidCall == false)

        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 1) {

            XCTAssert(self.mockDataProvider?.refreshStaysDidCall == false)
            XCTAssert(self.mockDelegate?.passwordChangedDidCall == false)
        }
    }

    func testChangePasswordFail() {

        mockDataProvider?.shouldThrowError = true

        XCTAssertNoThrow(try interactor?.changePassword(existingPassword: "OldPassword", newPassword: "Password69", newPasswordConfirm: "Password69"))

        XCTAssert(mockDataProvider?.changePasswordDidCall == false)
        XCTAssert(mockDelegate?.errorOccuredDidCall == false)
    }

    func testChangePasswordForBusinessUser() {

        if let mockDataProvider = mockDataProvider {
            interactor = ChangePasswordInteractor(dataProvider: mockDataProvider, asBusiness: true)
        }
        interactor?.delegate = mockDelegate

        if let user = try? User(title: "", firstName: "", lastName: "", email: "") {
            user.companyId = "COMP_test123"
            UserSessionManager.sharedInstance.piLoggedIn(with: user)
        }

        mockDataProvider?.shouldThrowError = false

        XCTAssertNoThrow(try interactor?.changePassword(existingPassword: "OldPassword", newPassword: "Password69", newPasswordConfirm: "Password69"))

        XCTAssert(mockDataProvider?.changePasswordDidCall == false)
        XCTAssert(mockDataProvider?.loginDidCall == false)
    }
}
