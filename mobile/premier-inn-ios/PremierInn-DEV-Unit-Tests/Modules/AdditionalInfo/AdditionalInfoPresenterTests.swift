//
//  AdditionalInfoPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 13/03/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest

@testable import PremierInn

private class MockView: AdditionalInfoViewProtocol {

    var updateDidCall = false

    func update(with viewModel: AdditionalInfoViewModel) {

        updateDidCall = true
    }
}

private class MockInteractor: AdditionalInfoInteractorProtocol {

    var getViewModelDidCall = false

    struct ViewModel: AdditionalInfoViewModel {
        var facilityDescriptions: [String]?
        var facilityTitle: String?
        var roomFeatureDescriptions: [String]?
        var roomFeatureTitle: String?
        var parkingDetails: String?
        let infoType: AdditionalInfoType
        let hotelName: String
        let hotelNotes: [String]?
        let hotelDescription: String?
        let hotelDirections: String?
    }

    var viewModel: AdditionalInfoViewModel {

        getViewModelDidCall = true

        return ViewModel(
            infoType: .hotelLocation,
            hotelName: "",
            hotelNotes: nil,
            hotelDescription: nil,
            hotelDirections: nil
        )
    }
}

private class MockRouter: AdditionalInfoRouterProtocol {

    var closeDidCall = false

    func closeButtonDidTap() {

        closeDidCall = true
    }
}

class AdditionalInfoPresenterTests: XCTestCase {

    fileprivate var mockView: MockView?
    fileprivate var mockInteractor: MockInteractor?
    fileprivate var mockRouter: MockRouter?

    var presenter: AdditionalInfoPresenter?

    override func setUp() {
        super.setUp()

        mockView = MockView()
        mockInteractor = MockInteractor()
        mockRouter = MockRouter()

        presenter = AdditionalInfoPresenter()
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
        XCTAssert(mockView?.updateDidCall == true)
    }

    func testCloseButtonDidTap() {

        presenter?.closeButtonDidTap()

        XCTAssert(mockRouter?.closeDidCall == true)
    }
}
