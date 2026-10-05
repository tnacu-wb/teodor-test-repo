//
//  AdditionalInfoInteractorAndRouterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 13/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

@testable import PremierInn

class AdditionalInfoInteractorTest: XCTestCase {

    private var hotel: Hotel? {
        guard let hotelFileURL = Bundle(for: type(of: self)).url(forResource: "hotelInfo", withExtension: "json") else { return nil }
        guard let hotelData = try? Data(contentsOf: hotelFileURL) else { return nil }
        guard let hotelJSON = try? JSONSerialization.jsonObject(with: hotelData, options: .allowFragments) as? PIDictionary else { return nil }
        guard let hotel = try? Hotel(dictionary: hotelJSON) else { return nil }

        return hotel
    }

    var interactor: AdditionalInfoInteractor?

    override func setUp() {
        super.setUp()

        guard let hotel = hotel else { return }
        interactor = AdditionalInfoInteractor(with: hotel, and: .hotelNotes)
    }

    override func tearDown() {
        super.tearDown()
    }

    func testViewModel() {

        let viewModel = interactor?.viewModel

        XCTAssertNotNil(viewModel)
        XCTAssert(viewModel?.hotelName == "Brighton City Centre")
    }
}

private class MockViewController: UIViewController {

    var dismissDidCall = false

    override func dismiss(animated flag: Bool, completion: (() -> Void)? = nil) {

        dismissDidCall = true
    }
}

class AdditionalInfoRouterTests: XCTestCase {

    fileprivate var mockViewController: MockViewController?

    var router: AdditionalInfoRouter?

    override func setUp() {
        super.setUp()

        mockViewController = MockViewController()

        router = AdditionalInfoRouter()
        router?.viewController = mockViewController
    }

    override func tearDown() {
        super.tearDown()
    }

    func testClose() {

        router?.closeButtonDidTap()

        XCTAssert(mockViewController?.dismissDidCall == true)
    }
}
