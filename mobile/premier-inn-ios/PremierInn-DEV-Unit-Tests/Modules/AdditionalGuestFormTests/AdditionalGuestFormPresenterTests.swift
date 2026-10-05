//
//  AdditionalGuestFormPresenterTests.swift
//  PremierInnTests
//
//  Created by Freddie Parks on 16/03/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockRouter: AdditionalGuestFormRouterProtocol {

    var selectedSalutationDidCall = false
    var selectedCountryDidCall = false
    var selectedCloseViewDidCall = false

    func selectedSalutation(completion: @escaping (String?) -> Void) {

        selectedSalutationDidCall = true
    }

    func selectedCountry(completion: @escaping (Country?) -> Void) {

        selectedCountryDidCall = true
    }

    func closeView() {

        selectedCloseViewDidCall = true
    }
}

private class MockInteractor: AdditionalGuestFormInteractorProtocol {

    var additionalGuestFormTracking: AdditionalGuestFormTracking { return ("", "") }
    var saveGuestDidCall = false

    var viewContent: AdditionalGuestFormContent {
        return (
            "bada",
            "boom"
        )
    }
    var guest: AdditionalGuest?

    func save(guest: AdditionalGuest) {

        saveGuestDidCall = true
    }
}

class AdditionalGuestFormPresenterTests: XCTestCase {

    var presenter: AdditionalGuestFormPresenter?

    fileprivate var router: MockRouter?
    fileprivate var interactor: MockInteractor?

    override func setUp() {
        super.setUp()

        router = MockRouter()
        interactor = MockInteractor()

        presenter = AdditionalGuestFormPresenter()
        presenter?.router = router
        presenter?.interactor = interactor
    }

    override func tearDown() {
        super.tearDown()
    }

    func testSaveGuest() {

        presenter?.save(user: AdditionalGuest(title: "", firstName: "", lastName: "", nationality: "", email: ""))

        XCTAssert(interactor?.saveGuestDidCall == true)
    }

    func testCloseView() {

        presenter?.cancel()

        XCTAssert(router?.selectedCloseViewDidCall == true)
    }

    func testSelectedSalutation() {

        presenter?.salutationRowDidTap()

        XCTAssert(router?.selectedSalutationDidCall == true)
    }

    func testSelectedCountry() {

        presenter?.countryRowDidTap()

        XCTAssert(router?.selectedCountryDidCall == true)
    }
}
