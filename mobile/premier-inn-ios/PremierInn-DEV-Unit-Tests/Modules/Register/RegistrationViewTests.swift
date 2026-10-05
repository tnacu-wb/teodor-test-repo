//
//  RegistrationViewTests.swift
//  PremierInnTests
//
//  Created by Freddie Parks on 27/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import Formeka
@testable import PremierInn

private class MockPresenter: RegisterPresenterInput {

    var registerTracking: RegisterTracking { return ("", "") }
    var viewIsReadyDidCall = false
    var submitFormDidCall = false
    var dismissRegisterDidCall = false
    var postcodeSearchDidTapDidCall = false
    var fetchMarketingPreferenceDidCall = false

    func viewIsReady() {

        viewIsReadyDidCall = true
    }

    func submitForm(viewModel: FormekaViewModel?) {

        submitFormDidCall = true
    }

    func dismissRegisterDidTap() {

        dismissRegisterDidCall = true
    }

    func postcodeSearchDidTap(with postcode: String?) {

        postcodeSearchDidTapDidCall = true
    }

    func biometricActivationCompleted(activated: Bool) {}

    func salutationRowDidTap() {

	}

    func termsAndConditionsDidTap() {}
    
    func countryChanged(countryCode: String) {}
}

class RegistrationViewTests: XCTestCase {

    fileprivate var presenter: MockPresenter?

    var view: RegisterViewController?

    override func setUp() {
        super.setUp()

        presenter = MockPresenter()

        view = RegisterViewController()
        view?.presenter = presenter
    }

    override func tearDown() {
        super.tearDown()
    }

    func testViewIsReady() {

        view?.viewDidLoad()

        XCTAssert(presenter?.viewIsReadyDidCall == true)
    }

    func testSubmitFormDidCall() {

        view?.submitButtonDidTap(cell: FormekaSubmitButtonCell())

        XCTAssert(presenter?.submitFormDidCall == true)
    }

    func testDismissView() {

        view?.cancelButtonDidTap()

        XCTAssert(presenter?.dismissRegisterDidCall == true)
    }
}
