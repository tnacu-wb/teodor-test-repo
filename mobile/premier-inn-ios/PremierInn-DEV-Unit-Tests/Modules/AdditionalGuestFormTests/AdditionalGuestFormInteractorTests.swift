//
//  AdditionalGuestFormInteractorTests.swift
//  PremierInnTests
//
//  Created by Freddie Parks on 16/03/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockPresenter: AdditionalGuestFormInteractorDelegate {

    var guestSavedDidCall = false
    var guestSavedFailedDidCall = false

    func savedGuest(withMessage message: String) {
        guestSavedDidCall = true
    }

    func savedGuestFailed(withError error: Error) {
        guestSavedFailedDidCall = true
    }
}

private class MockRequestsManager: AdditionalGuestsRequestsProtocol {

    enum UpdateSuccess {
        case success
        case fail
    }

    var requestOutcome: UpdateSuccess = .success

	func updateUserAdditionalGuests(for user: User, sensorData: String, completion: @escaping (Bool, Error?) -> Void) {

        if requestOutcome == .success {
            completion(true, nil)
        } else {
            let error = NSError(domain: "", code: 1, userInfo: nil)
            completion(false, error)
        }
    }
}

class AdditionalGuestFormInteractorTests: XCTestCase {

    var interactor: AdditionalGuestFormInteractor?

    fileprivate var presenter: MockPresenter?
    fileprivate var requestsManager: MockRequestsManager = MockRequestsManager()

    override func setUp() {
        super.setUp()

        interactor = AdditionalGuestFormInteractor()
        presenter = MockPresenter()

        interactor?.requestsManager = requestsManager
        interactor?.delegate = presenter

        let user = try? User(dictionary: [
            "contactDetail" : ["title" : "Mr", "firstName": "Pippo", "lastName": "Paperino"],
            "additionalGuests": [
                ["title": "Mr", "firstName": "Marcello", "lastName": "Mascia", "nationality": "Sardinian", "email": "godTierMccree@owl.com"]
            ]
            ], sessionId: "hello")

        guard let loggedUser = user else { return }

        UserSessionManager.sharedInstance.loggedIn(with: loggedUser)
    }

    override func tearDown() {
        super.tearDown()
    }

    func testUpdateUserSuccess() {

        requestsManager.requestOutcome = .success

        let additionalGuest = AdditionalGuest(title: "", firstName: "", lastName: "", nationality: "", email: "")
        interactor?.save(guest: additionalGuest)

        XCTAssert(presenter?.guestSavedDidCall == true)
    }

    func testUpdateUserFail() {

        requestsManager.requestOutcome = .fail

        let additionalGuest = AdditionalGuest(title: "", firstName: "", lastName: "", nationality: "", email: "")
        interactor?.save(guest: additionalGuest)

        XCTAssert(presenter?.guestSavedFailedDidCall == true)
    }

    func testViewContent() {

        XCTAssert(interactor?.viewContent.submitTitle.isNotEmpty == true)
        XCTAssert(interactor?.viewContent.title.isNotEmpty == true)
        XCTAssertNotNil(interactor?.viewContent.submitTitle)
        XCTAssertNotNil(interactor?.viewContent.title)
    }
}
