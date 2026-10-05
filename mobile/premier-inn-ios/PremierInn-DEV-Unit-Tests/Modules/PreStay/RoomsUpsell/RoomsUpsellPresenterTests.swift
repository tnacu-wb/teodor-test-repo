//
//  RoomsUpsellPresenterTests.swift
//  PremierInn
//
//  Created by Cojocaru, Andrei Gabriel (Cognizant) on 15.11.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn

// Mock View
class MockRoomsUpsellView: RoomsUpsellViewProtocol {

    var customAnalyticsParameters: PIDictionary? = PIDictionary()
    
    var isLoadDataCalled = false
    
    func loadData(with roomsUpsellViewModel: RoomsUpsellViewModel) {
        isLoadDataCalled = true
    }
}

// Mock Interactor
class MockRoomsUpsellInteractor: RoomsUpsellInteractorProtocol {
    var bookingReference: String? = ""
    
    var analyticsParams = PIDictionary()
    
    var editedUpsell: CiolUpsellItemViewModelProtocol = MockCiolUpsellItem()
    var upsellOutput: RoomsUpsellOutput = .init(rooms: [], nights: 0)
    var viewModel: RoomsUpsellViewModel

    var didCallDidUpdateUpsell = false

    init(viewModel: RoomsUpsellViewModel) {
        self.viewModel = viewModel
    }

    func didUpdateUpsell(for item: CiolUpsellItemViewModelProtocol, action: CiolUpsellDetailsOutputAction) {
        didCallDidUpdateUpsell = true
    }

    func getRoomCellConfig(roomID: String) -> PremierInn.CiolUpsellCellSetup {
        return .init(isMultiRoom: false)
    }

}

// Mock Router
class MockRoomsUpsellRouter: RoomsUpsellRouterProtocol {

    
    var isPopControllerCalled = false
    var didCallShowUpsellDetails = false

    func popController() {
        isPopControllerCalled = true
    }

    func showUpsellDetails(input: PremierInn.CiolUpsellDetailsInputParams, outputDelegate: any PremierInn.CiolUpsellDetailsOutputDelegate) {
        didCallShowUpsellDetails = true
    }
}

class MockCIOLPriceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol {
    var ctaTitle: String = "Continue"
    var totalValue: String = "22"
    var displayTotalValue: Bool = true
    var items: [CIOLPriceBreakdownItemViewModelProtocol] = []
}

class MockUpsellRoom: UpsellRoom {
    var adults: [String] = []
    var hasChildren: Bool = false
    var numberOfChildren: Int = 0
    var id: String = ""
    var title: String = ""
    var addedUpsells: [CiolUpsellItemViewModelProtocol] = []
}

// Mock ViewModel for testing
class MockRoomsUpsellViewProtocol: RoomsUpsellViewModel {
    var addedPriceBreakdownViewModels: [CIOLPriceBreakdownItemViewModelProtocol] = []
    var nights: Int = 0
    var rooms: [UpsellRoom] = []
    var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol?
    var didCallUpdateRooms: Bool = false

    func updateRooms(_ rooms: [any PremierInn.UpsellRoom]) {
        didCallUpdateRooms = true
    }
}

class RoomsUpsellPresenterTests: XCTestCase {
    var presenter: RoomsUpsellPresenter!
    var mockView: MockRoomsUpsellView!
    var mockInteractor: MockRoomsUpsellInteractor!
    var mockRouter: MockRoomsUpsellRouter!
    let mockOutputDelegate = MockCiolUpsellInteractor()

    override func setUp() {
        super.setUp()

        mockView = MockRoomsUpsellView()
        mockInteractor = MockRoomsUpsellInteractor(viewModel: MockRoomsUpsellViewProtocol())
        mockRouter = MockRoomsUpsellRouter()

        presenter = RoomsUpsellPresenter()

        presenter.outputDelegate = mockOutputDelegate
        presenter.view = mockView
        presenter.interactor = mockInteractor
        presenter.router = mockRouter
    }

    override func tearDown() {
        presenter = nil
        mockView = nil
        mockInteractor = nil
        mockRouter = nil
        super.tearDown()
    }
    
    func testHandleContinueButtonTap_CallsPerformUpsellChecks() {
        // Act
        presenter.handleContinueButtonTap()
        // Assert
        XCTAssertTrue(mockRouter.isPopControllerCalled, "handleContinueButtonTap should call popBack on the router")
    }
    
    func testDisplayUpsellData_CalledOnView() {
        // Act
        presenter.viewIsReady()
        // Assert
        XCTAssertTrue(mockView.isLoadDataCalled, "loadData should be called on the view when the presenter is ready")
    }

    func testUpdateOutput() {
        presenter.updateOutput()
        XCTAssertTrue(mockOutputDelegate.didCallUpdatedOutput)
    }

    func testShowUpsellDetails() {
        presenter.showUpsellDetails(room: MockUpsellRoom())
        XCTAssertTrue(mockRouter.didCallShowUpsellDetails)
    }
}
