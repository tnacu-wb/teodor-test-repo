//
//  AdditionalGuestFormViewTests.swift
//  PremierInnTests
//
//  Created by Freddie Parks on 16/03/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn_DEV

private class MockPresenter: AdditionalGuestFormViewDelegate {
    var viewIsReadyDidCall = false
    var saveDidCall = false
    var cancelDidCall = false
    var submitFormDidCall = false
    var salutationDidCall = false
    var countryDidCall = false

    func viewIsReady() {
        viewIsReadyDidCall = true
    }

    func save(user: AdditionalGuest?) {
        saveDidCall = true
    }

    func cancel() {
        cancelDidCall = true
    }

    func submitForm() {
        submitFormDidCall = true
    }

    func salutationRowDidTap() {
        salutationDidCall = true
    }

    func countryRowDidTap() {
        countryDidCall = true
    }
}

class AdditionalGuestFormViewTests: XCTestCase {
    var view: AdditionalGuestFormView?

    fileprivate var presenter: MockPresenter?

    override func setUp() {
        super.setUp()

        presenter = MockPresenter()

        view = AdditionalGuestFormView()
        view?.delegate = presenter
    }

    func testViewIsReady() {
        view?.viewDidLoad()
        XCTAssert(presenter?.viewIsReadyDidCall == true)
    }

    func testCancelButtonDidTap() {
        view?.cancelButtonDidTap()
        XCTAssert(presenter?.cancelDidCall == true)
    }
}
