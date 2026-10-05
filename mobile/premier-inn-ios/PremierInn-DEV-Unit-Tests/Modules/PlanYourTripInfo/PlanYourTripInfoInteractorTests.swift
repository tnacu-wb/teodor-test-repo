//
//  PlanYourTripInfoInteractorTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 07/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest
import CoreLocation
import SimpleNetwork

@testable import PremierInn

private class MockPresenter {

}

class PlanYourTripInfoInteractorTests: XCTestCase {

    fileprivate var mockPresenter: MockPresenter?

    var interactor: PlanYourTripInfoInteractor?

    override func setUp() {
        super.setUp()

        mockPresenter = MockPresenter()

        guard let hotelFileURL = Bundle(for: type(of: self)).url(forResource: "hotelInfo", withExtension: "json") else { return }
        guard let hotelData = try? Data(contentsOf: hotelFileURL) else { return }
        guard let hotelJSON = try? JSONSerialization.jsonObject(with: hotelData, options: .allowFragments) as? PIDictionary else { return }
        guard let hotel = try? Hotel(dictionary: hotelJSON) else { return }

        interactor = PlanYourTripInfoInteractor(hotel: hotel)
    }

    override func tearDown() {
        super.tearDown()

        interactor = nil
    }

    func testViewModel() {

        let viewModel = interactor?.viewModel

        XCTAssertNotNil(viewModel)
        XCTAssert(viewModel?.title?.isNotEmpty == true)
        XCTAssert(viewModel?.hotelAddress?.isNotEmpty == true)
    }
}
