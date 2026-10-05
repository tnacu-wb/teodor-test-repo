//
//  PlanYourTripInfoViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 07/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest

@testable import PremierInn

private class MockEventHandler: PlanYourTripInfoViewEventHandler {

    var viewIsReadyDidCall = false
    var openDirectionsDidCall = false

    func viewIsReady() {

        viewIsReadyDidCall = true
    }

    func openDirections(withSender sender: UIView) {

        openDirectionsDidCall = true
    }
}

class PlanYourTripInfoViewTests: XCTestCase {

    fileprivate var mockEventHandler: MockEventHandler?

    var view: PlanYourTripInfoView?

    override func setUp() {
        super.setUp()

        mockEventHandler = MockEventHandler()

        view = PlanYourTripInfoView()
        view?.tableView = UITableView()
        view?.eventHandler = mockEventHandler

        let parent = UIViewController()
        if let view = view {
            parent.addChild(view)
        }

        view?.viewDidLoad()
    }

    override func tearDown() {
        super.tearDown()
    }

    func testViewReady() {

        view?.viewDidLoad()

        XCTAssert(mockEventHandler?.viewIsReadyDidCall == true)
    }

    func testDirectionsDidTap() {

        view?.directionsCellDidTap(button: UIButton())

        XCTAssert(mockEventHandler?.openDirectionsDidCall == true)
    }

    func testUpdateViewModelAndTableViewModel() {

        struct ViewModel: PlanYourTripInfoViewModel {
            let title: String?
            let hotelAddress: String?
            let directions: String?
            let hotelParking: String?
        }

        let viewModel = ViewModel(
            title: "20",
            hotelAddress: "120",
            directions: "over there",
            hotelParking: "park behind the bins"
        )

        view?.update(with: viewModel)

        XCTAssertNotNil(view?.tableViewModel)

        if let tableViewModel = view?.tableViewModel {

            XCTAssert(tableViewModel.sections.count > 0)
            XCTAssert(tableViewModel.sections[0].rows.count > 0)
        }
    }
}
