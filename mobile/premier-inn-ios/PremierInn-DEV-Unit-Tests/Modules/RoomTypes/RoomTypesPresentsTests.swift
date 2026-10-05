//
//  RoomTypesPresentsTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Georgios Aikaterinakis on 11/06/2019.
//  Copyright © 2019 Whitbread. All rights reserved.
//

import XCTest

import SimpleNetwork
@testable import PremierInn

private class MockView: RoomTypesViewProtocol {
    
    var setupDidCall = false
    var updateDidCall = false
    var updateSegmentDidCall = false
    
    func setup(with viewModel: RoomTypesViewModel) {
        setupDidCall = true
    }
    
    func update(with viewModel: RoomTypesViewModel) {
        updateDidCall = true
    }
    
    func updateSegment(with viewModel: RoomTypesViewModel, selectedIndex: Int) {
        updateSegmentDidCall = true
    }
    
}

private class MockRouter: RoomTypesRouterProtocol {
    
    var closeButtonDidTapDidCall = false
    
    func closeButtonDidTap() {
        closeButtonDidTapDidCall = true
    }
}

private class MockInteractor: RoomTypesInteractorProtocol {

    var viewModel: RoomTypesViewModel? = RoomTypesViewModel(
        screenName: "screen name",
        roomCategoryViewModels: [],
        roomTypeSegmentData: RoomTypeSegmentsViewModel(
            backgroundColour: UIColor.white,
            selectorColor: UIColor.white,
            normalTextAttributes: [:],
            selectedTextAttributes: [:],
            informationSegments: [],
            defaultSegmentIndex: 0
        ), disclaimerText: ""
    )
}

class RoomTypesPresentsTests: XCTestCase {
    
    private var view: MockView!
    private var presenter: RoomTypesPresenter!
    private var router: MockRouter!
    private var interactor: MockInteractor!
    
    override func setUp() {
        
        view = MockView()
        router = MockRouter()
        interactor = MockInteractor()
        
        presenter = RoomTypesPresenter()
        presenter.view = view
        presenter.router = router
        presenter.interactor = interactor
    }
    
    override func tearDown() {
        
        interactor = nil
        view = nil
        router = nil
        presenter = nil
        
        super.tearDown()
    }
    
    func testSetupView() {
        presenter.viewNeedsSetup()
        
        XCTAssertTrue(view.setupDidCall)
        XCTAssertFalse(view.updateDidCall)
    }
    
    func testUpdateView() {
        presenter.viewIsReady()
        
        XCTAssertFalse(view.setupDidCall)
        XCTAssertTrue(view.updateDidCall)
    }
    
    func testUpdateSegment() {
        presenter.segmentChanged(selectedIndex: 0)
        
        XCTAssertTrue(view.updateSegmentDidCall)
    }
    
    func testCloseButtonDidTap() {
        presenter.closeButtonDidTap()
        
        XCTAssertTrue(router.closeButtonDidTapDidCall)
    }
}
