//
//  MapDetailPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 07/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
import MapKit

@testable import PremierInn

private class MockView: MapDetailViewProtocol {

    var updateWithViewModelDidCall = false
    var displayOverlayDidCall = false

    func update(with viewModel: MapDetailViewModel) {

        updateWithViewModelDidCall = true
    }

    func displayOverlayController(controller: UIViewController) throws {

        displayOverlayDidCall = true
    }
}

private class MockInteractor: MapDetailInteractorProtocol {

    var getViewModelDidCall = false
    var getHotelDidCall = false
    var getLayoutDidCall = false

    struct ViewModel: MapDetailViewModel {
        let layout: MapLayout
        let hotelName: String?
        let hotelAnnotation: MKAnnotation?
        let referenceAnnotation: MKAnnotation?
        let mapCompassDesign: MapCompassDesign?
        let mapPinImageName: String
        let distanceText: NSAttributedString?
    }

    var viewModel: MapDetailViewModel? {

        getViewModelDidCall = true

        return ViewModel(
            layout: .map,
            hotelName: "",
            hotelAnnotation: nil,
            referenceAnnotation: nil,
            mapCompassDesign: (UIColor.red, ""),
            mapPinImageName: "",
            distanceText: nil
        )
    }

    var hotel: Hotel? {

        getHotelDidCall = true

        guard let hotelFileURL = Bundle(for: type(of: self)).url(forResource: "hotelInfo", withExtension: "json") else { return nil }
        guard let hotelData = try? Data(contentsOf: hotelFileURL) else { return nil }
        guard let hotelJSON = try? JSONSerialization.jsonObject(with: hotelData, options: .allowFragments) as? PIDictionary else { return nil }
        guard let hotel = try? Hotel(dictionary: hotelJSON) else { return nil }

        return hotel
    }

    var layout: MapLayout {

        getLayoutDidCall = true

        return .journeyPlanner
    }
}

private class MockRouter: MapDetailRouterProtocol {

    var openDirectionsDidCall = false
    var showOverlayDidCall = false

    func openDirections(with hotel: Hotel, withSender sender: UIView) {

        openDirectionsDidCall = true
    }

    func showOverlayController(with hotel: Hotel) {

        showOverlayDidCall = true
    }
}

class MapDetailPresenterTests: XCTestCase {

    fileprivate var mockView: MockView?
    fileprivate var mockInteractor: MockInteractor?
    fileprivate var mockRouter: MockRouter?

    var presenter: MapDetailPresenter?

    override func setUp() {
        super.setUp()

        mockView = MockView()
        mockInteractor = MockInteractor()
        mockRouter = MockRouter()

        presenter = MapDetailPresenter()
        presenter?.view = mockView
        presenter?.interactor = mockInteractor
        presenter?.router = mockRouter
    }

    override func tearDown() {
        super.tearDown()
    }

    func testViewIsReady() {

        presenter?.viewIsReady()

        XCTAssert(mockInteractor?.getViewModelDidCall == true)
        XCTAssert(mockView?.updateWithViewModelDidCall == true)
    }

    func testOpenDirections() {

        presenter?.openDirections(withSender: UIView())

        XCTAssert(mockInteractor?.getHotelDidCall == true)
        XCTAssert(mockRouter?.openDirectionsDidCall == true)
    }

    func testFinishedLoadingHotel() {

        presenter?.finishedLoadingHotel()

        XCTAssert(mockInteractor?.getLayoutDidCall == true)
        XCTAssert(mockInteractor?.getHotelDidCall == true)
        XCTAssert(mockInteractor?.getViewModelDidCall == true)

        XCTAssert(mockView?.updateWithViewModelDidCall == true)
        XCTAssert(mockRouter?.showOverlayDidCall == true)
    }
}
