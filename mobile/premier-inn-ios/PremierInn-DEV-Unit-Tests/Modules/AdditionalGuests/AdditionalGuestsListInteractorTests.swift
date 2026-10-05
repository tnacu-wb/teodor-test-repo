//
//  AdditionalGuestsListInteractorTests.swift
//  PremierInnTests
//
//  Created by Freddie Parks on 22/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockPresenter: AdditionalGuestsInteractorDelegate {

    var guestDeletedDidCall = false
    var guestDeleteFailedDidCall = false

    func guestDeleted(withMessage message: String) {

        guestDeletedDidCall = true
    }

    func guestDeleteFailed(withError error: Error) {

        guestDeleteFailedDidCall = true
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

class AdditionalGuestsListInteractorTests: XCTestCase {

    var interactor: AdditionalGuestsInteractor?

    fileprivate var presenter: MockPresenter?
    fileprivate var requestsManager: MockRequestsManager = MockRequestsManager()

    override func setUp() {
        super.setUp()

        interactor = AdditionalGuestsInteractor()
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

    func testDeleteUserSuccess() {

        requestsManager.requestOutcome = .success
        interactor?.deleteGuest(atIndex: 0)

        XCTAssert(presenter?.guestDeletedDidCall == true)
    }

    func testDeleteUserFail() {

        requestsManager.requestOutcome = .fail
        interactor?.deleteGuest(atIndex: 0)

        XCTAssert(presenter?.guestDeleteFailedDidCall == true)
    }

    func testAdditionalGuests() {

        XCTAssertNotNil(interactor?.additionalGuests)

        guard let additionalGuest = interactor?.additionalGuests?.first else { return }

        XCTAssert(additionalGuest.title == "Mr")
        XCTAssert(additionalGuest.firstName == "Marcello")
        XCTAssert(additionalGuest.lastName == "Mascia")
        XCTAssert(additionalGuest.email == "godTierMccree@owl.com")
    }

    func testScreenTitle() {

        XCTAssertNotNil(interactor?.viewTitle)
    }
}
