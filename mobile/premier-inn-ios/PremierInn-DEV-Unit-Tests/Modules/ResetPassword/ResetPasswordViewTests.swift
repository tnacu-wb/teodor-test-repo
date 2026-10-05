//
//  ResetPasswordViewTests.swift
//  PremierInn
//
//  Created by Nick Jones on 09/05/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import Formeka
@testable import PremierInn

private class MockPresenter: ResetPasswordPresenterInput {

    var viewIsReadyDidCall = false

    var screenName: String {
        return ""
    }

    var screenType: String {
        return ""
    }

    var emailAddress: String = ""

    var shouldShowError: Bool = false

    func viewIsReady() {

        viewIsReadyDidCall = true
    }

    func userTappedEmailAddressRow(at indexPath: IndexPath) {}

    func formSubmissionErrorOccured(at rowIndex: IndexPath?) {}

    func genericFormSubmissionErrorOccured(with error: Error) {}

    func userSubmitted(emailAddress: String) {}

    func userClosedSuccessAlert() {}

    func submitButtonTapped() {}
}

class ResetPasswordViewTests: XCTestCase {

    private var presenter: MockPresenter?
    var view: ResetPasswordView?

    override func setUp() {
        super.setUp()

        presenter = MockPresenter()

        view = ResetPasswordView()

        view?.presenter = presenter
    }

    override func tearDown() {

        presenter = nil
        view = nil

        super.tearDown()
    }

    func testViewIsReady() {

        view?.viewDidLoad()
        XCTAssertTrue(presenter?.viewIsReadyDidCall ?? false)
    }
}


