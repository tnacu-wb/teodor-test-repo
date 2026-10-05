//
//  AccountInteractorTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 22/02/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

@testable import PremierInn

fileprivate class MockAccountRequestsManager: AccountDataProvider {

    func getUser(userId: String, isBusiness: Bool, completion: @escaping (Result<User>) -> Void) {
        
        if let user = try? User(title: "Mr", firstName: "Nichola", lastName: "Twisp", email: "nicepins@me.com") {
            return completion(.success(result: user))
        }
    }

    func autoLogin(completion: @escaping (SimpleNetwork.User?) -> Void) {
        completion(nil)
    }
}

class AccountInteractorTests: XCTestCase {

    fileprivate var interactor: AccountInteractor?

    private var didCallUpdateUserCompletionHandler = false

    override func setUp() {
        super.setUp()

        interactor = AccountInteractor()
        if let user = try? User(title: "Mr", firstName: "Nichola", lastName: "Twisp", email: "nicepins@me.com") {
            UserSessionManager.sharedInstance.piLoggedIn(with: user)
        }

        interactor?.accountDataProvider = MockAccountRequestsManager()
    }

    override func tearDown() {
        super.tearDown()
    }

    func testUserLoggedOut() {

        interactor?.userLoggedOut()
        XCTAssertNil(UserSessionManager.sharedInstance.currentUser)
    }

    func testViewModelLoggedInUser() {

        let viewModel = interactor?.accountViewModel

        XCTAssertNotNil(viewModel)
        XCTAssertNotNil(viewModel?.username)
        XCTAssertNotNil(viewModel?.email)
        XCTAssert(viewModel?.userLoggedIn == true)
    }

    func testViewModelNoUser() {

        interactor?.userLoggedOut()

        let viewModel = interactor?.accountViewModel

        XCTAssertNotNil(viewModel)
        XCTAssertNil(viewModel?.username)
        XCTAssertNil(viewModel?.email)
        XCTAssert(viewModel?.userLoggedIn == false)
    }

    func testAccountCreatedMessage() {

        XCTAssertNotNil(interactor)
        XCTAssert(interactor?.accountCreatedMessage.isNotEmpty == true)
    }

    func testDetailsChangedMessage() {

        XCTAssertNotNil(interactor)
        XCTAssert(interactor?.detailsChangedMessage.isNotEmpty == true)
    }

    func testPasswordChangedMessage() {

        XCTAssertNotNil(interactor)
        XCTAssert(interactor?.passwordChangedMessage.isNotEmpty == true)
    }

    private func updateUserCompletionHandler() -> Void {
        didCallUpdateUserCompletionHandler = true
    }

    func testUpdateUser() {

        interactor?.updateUser(shouldAttemptLogin: true, completion: updateUserCompletionHandler)
        XCTAssertTrue(didCallUpdateUserCompletionHandler)
    }

    // logged out should still go into paymentMethods/addCard
    func testUpdateUserWithoutLoggedInUser() {

        UserSessionManager.sharedInstance.piUserLoggedOut()
        interactor?.updateUser(shouldAttemptLogin: true, completion: updateUserCompletionHandler)
        XCTAssertTrue(didCallUpdateUserCompletionHandler)
    }
}
