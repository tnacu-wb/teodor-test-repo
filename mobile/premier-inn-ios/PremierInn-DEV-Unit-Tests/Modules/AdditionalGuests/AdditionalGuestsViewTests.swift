//
//  AdditionalGuestsViewTests.swift
//  PremierInnTests
//
//  Created by Freddie Parks on 23/02/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import Formeka
@testable import PremierInn

private class MockPresenter: AdditionalGuestsViewEventHandler {

    var additionalGuestsTracking: AdditionalGuestsTracking { return ("", "") }
    var viewIsReadyDidCall = false
    var selectedAddGuestDidCall = false
    var selectedEditGuestDidCall = false
    var selectedDeleteGuestDidCall = false

    func viewIsReady() {

        viewIsReadyDidCall = true
    }

    func selectedAddGuest() {

        selectedAddGuestDidCall = true
    }

    func selectedEditGuest(atIndex index: Int) {

        selectedEditGuestDidCall = true
    }

    func selectedDeleteGuest(atIndex index: Int) {

        selectedDeleteGuestDidCall = true
    }
}

class AdditionalGuestsViewTests: XCTestCase {

    private var presenter: MockPresenter?
    private var view: AdditionalGuestsView?

    override func setUp() {
        super.setUp()

        presenter = MockPresenter()

        view = AdditionalGuestsView()
        view?.eventHandler = presenter
    }

    override func tearDown() {
        super.tearDown()
    }

    func testViewIsReady() {

        view?.viewDidLoad()

        XCTAssert(presenter?.viewIsReadyDidCall == true)
    }

    func testSelectedAddGuest() {

        view?.submitButtonDidTap(cell: FormekaSubmitButtonCell())

        XCTAssert(presenter?.selectedAddGuestDidCall == true)
    }

    func testSelectedEditGuest() {

        view?.eventHandler?.selectedEditGuest(atIndex: 0)

        XCTAssert(presenter?.selectedEditGuestDidCall == true)
    }

    func testSelectedDeleteGuest() {

        view?.eventHandler?.selectedDeleteGuest(atIndex: 0)

        XCTAssert(presenter?.selectedDeleteGuestDidCall == true)
    }
}
