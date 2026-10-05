//
//  MapDetailRouterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 07/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

@testable import PremierInn

private class MockView: UIViewController, MapDetailViewProtocol {

    var presentControllerDidCall = false
    var displayOverlayDidCall = false

    override func present(_ viewControllerToPresent: UIViewController, animated flag: Bool, completion: (() -> Void)? = nil) {

        presentControllerDidCall = true
    }

    func update(with viewModel: MapDetailViewModel) {}

    func displayOverlayController(controller: UIViewController) throws {

        displayOverlayDidCall = true
    }
}

private class MockPresenter: MapDetailPresenterProtocol {

    var openDirectionsDidCall = false

    func openDirections(withSender sender: UIView) {

        openDirectionsDidCall = true
    }
}

class MapDetailRouterTests: XCTestCase {

    fileprivate var mockView: MockView?
    fileprivate var mockPresenter: MockPresenter?

    private var hotel: Hotel? {
        guard let hotelFileURL = Bundle(for: type(of: self)).url(forResource: "hotelInfo", withExtension: "json") else { return nil }
        guard let hotelData = try? Data(contentsOf: hotelFileURL) else { return nil }
        guard let hotelJSON = try? JSONSerialization.jsonObject(with: hotelData, options: .allowFragments) as? PIDictionary else { return nil }
        guard let hotel = try? Hotel(dictionary: hotelJSON) else { return nil }

        return hotel
    }

    var router: MapDetailRouter?

    override func setUp() {
        super.setUp()

        mockView = MockView()
        mockPresenter = MockPresenter()

        router = MapDetailRouter()
        router?.view = mockView
        router?.presenter = mockPresenter
    }

    override func tearDown() {
        super.tearDown()
    }

    func testOpenDirectionsWithHotel() {

        guard let hotel = hotel else { return }

        router?.openDirections(with: hotel, withSender: UIView())

        XCTAssert(mockView?.presentControllerDidCall == false)
    }

    func testShowOverlay() {

        guard let hotel = hotel else { return }

        router?.showOverlayController(with: hotel)

        XCTAssert(mockView?.displayOverlayDidCall == true)
    }

    func testOpenDirections() {

        router?.openDirections(withSender: UIView())

        XCTAssert(mockPresenter?.openDirectionsDidCall == true)
    }
}
