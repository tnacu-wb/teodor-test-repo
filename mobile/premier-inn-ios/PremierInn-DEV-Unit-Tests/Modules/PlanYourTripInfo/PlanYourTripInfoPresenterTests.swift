//
//  PlanYourTripInfoPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 07/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest

@testable import PremierInn

private class MockView: PlanYourTripInfoViewProtocol {

    var updateViewModelDidCall = false

    func update(with viewModel: PlanYourTripInfoViewModel) {

        updateViewModelDidCall = true
    }
}

private class MockInteractor: PlanYourTripInfoInteractorProtocol {

    var getViewModelDidCall = false

    struct ViewModel: PlanYourTripInfoViewModel {
        let title: String?
        let hotelAddress: String?
        let directions: String?
        let hotelParking: String?
    }

    var viewModel: PlanYourTripInfoViewModel? {

        getViewModelDidCall = true

        return ViewModel(
            title: "far",
            hotelAddress: "120",
            directions: "go straight ahead",
            hotelParking: "some"
        )
    }
}

private class MockRouter: PlanYourTripInfoRouterProtocol {

    var openDirectionsDidCall = false

    func openDirections(withSender sender: UIView) {

        openDirectionsDidCall = true
    }
}

class PlanYourTripInfoPresenterTests: XCTestCase {

    fileprivate var mockView: MockView?
    fileprivate var mockInteractor: MockInteractor?
    fileprivate var mockRouter: MockRouter?

    private var presenter: PlanYourTripInfoPresenter?

    override func setUp() {
        super.setUp()

        mockView = MockView()
        mockInteractor = MockInteractor()
        mockRouter = MockRouter()

        presenter = PlanYourTripInfoPresenter()
        presenter?.view = mockView
        presenter?.interactor = mockInteractor
        presenter?.router = mockRouter
    }

    override func tearDown() {
        super.tearDown()
    }

    func testViewReady() {

        presenter?.viewIsReady()

        XCTAssert(mockInteractor?.getViewModelDidCall == true)
        XCTAssert(mockView?.updateViewModelDidCall == true)
    }

    func testOpenDirections() {

        presenter?.openDirections(withSender: UIView())

        XCTAssert(mockRouter?.openDirectionsDidCall == true)
    }
}
