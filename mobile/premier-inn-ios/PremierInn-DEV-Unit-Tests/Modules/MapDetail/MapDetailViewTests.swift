//
//  MapDetailViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 08/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest
import MapKit
import SimpleNetwork

@testable import PremierInn

private class MockOverlayView: UIViewController, OverlayProtocol {

    var controller: UIViewController { return self }
    var scrollView: UIScrollView? { return nil }
    var topLayoutPadding: CGFloat { return 20 }
    var collapsedHeight: CGFloat { return 100 }
}

private class MockEventHandler: MapDetailViewEventHandler {

    var viewIsReadyDidCall = false

    func viewIsReady() {

        viewIsReadyDidCall = true
    }
}

class MapDetailViewTests: XCTestCase {

    fileprivate var mockEventHandler: MockEventHandler?

    struct ViewModel: MapDetailViewModel {
        let layout: MapLayout
        let hotelName: String?
        let hotelAnnotation: MKAnnotation?
        let referenceAnnotation: MKAnnotation?
        let mapCompassDesign: MapCompassDesign?
        let mapPinImageName: String
        let distanceText: NSAttributedString?
    }

    private var hotel: Hotel? {
        guard let hotelFileURL = Bundle(for: type(of: self)).url(forResource: "hotelInfo", withExtension: "json") else { return nil }
        guard let hotelData = try? Data(contentsOf: hotelFileURL) else { return nil }
        guard let hotelJSON = try? JSONSerialization.jsonObject(with: hotelData, options: .allowFragments) as? PIDictionary else { return nil }
        guard let hotel = try? Hotel(dictionary: hotelJSON) else { return nil }

        return hotel
    }
    private var viewController: MapViewController?
    private var analytics: MockAnalyticsManager!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        mockEventHandler = MockEventHandler()

        viewController = MapViewController()
        viewController?.resetButton = RoundedCornersButton(frame: CGRect.zero)
        viewController?.hotelCompass = Compass(frame: CGRect.zero)
        viewController?.distanceLabel = UILabel(frame: CGRect.zero)
        viewController?.eventHandler = mockEventHandler

        analytics = MockAnalyticsManager()
        viewController?.analytics = analytics
    }

    override func tearDown() {

        viewController = nil
        analytics = nil

        super.tearDown()
    }

    // MARK: - Tests

    func testView_whenViewAppears_invokesTrackState() {
        UserSessionManager.sharedInstance.piUserLoggedOut()

        viewController?.beginAppearanceTransition(true, animated: false)
        viewController?.endAppearanceTransition()

        let state = analytics.states.first

        XCTAssertEqual(state, "iOS:PI:UK: Map")
    }

    func testViewIsReady() {

        viewController?.viewDidLoad()

        XCTAssert(mockEventHandler?.viewIsReadyDidCall == true)
    }

    func testUpdateViewModel() {

        let viewModel = ViewModel(
            layout: .map,
            hotelName: "Nikola Jones",
            hotelAnnotation: hotel,
            referenceAnnotation: nil,
            mapCompassDesign: (UIColor.Tint8, ""),
            mapPinImageName: "",
            distanceText: nil
        )

        viewController?.update(with: viewModel)
    }

    func testGesture() {

        XCTAssert(viewController?.gestureRecognizer(UIGestureRecognizer(), shouldRecognizeSimultaneouslyWith: UIGestureRecognizer()) == true)
    }

    func testAddOverlay() {

        let overlayView = MockOverlayView()

        XCTAssertNoThrow(try viewController?.displayOverlayController(controller: overlayView))
    }

    // need to add some tests

    func testViewStates() {

        viewController?.viewWillAppear(true)
        viewController?.viewDidAppear(true)
        viewController?.viewDidDisappear(true)
        viewController?.viewDidLayoutSubviews()
    }

    func testMap() {

        viewController?.resetButtonDidTap(UIButton(frame: CGRect.zero))
        viewController?.backToReferenceDidTap(UITapGestureRecognizer())
        viewController?.backToHotelButtonDidTap(UIButton(frame: CGRect.zero))
    }
}
