//
//  CiolUpsellPresenterTests.swift
//  PremierInnTests
//
//  Created by Florin Velesca on 30.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn
@testable import SimpleNetwork

// Mock View
class MockCiolUpsellView: CiolUpsellViewProtocol {
    var customAnalyticsParameters: PremierInn.PIDictionary? = PIDictionary()
    
    func showLoadingIndicator() {
        didCallShowLoading = true
    }
    
    func hideLoadingIndicator() {
        didCallHideLoading = true
    }
    
    func showError(title: String, message: String?, shouldDie: Bool) {}
    
    var isDataReloaded = false
    var displayedViewModel: CiolUpsellViewModelProtocol?
    var didCallShowError = false
    var didCallHideLoading = false
    var didCallShowLoading = false

    func reloadData(viewModel: CiolUpsellViewModelProtocol) {
        isDataReloaded = true
        displayedViewModel = viewModel
    }
}

// Mock Interactor
class MockCiolUpsellInteractor: CiolUpsellInteractorProtocol {
    var paymentViewLayout: PremierInn.WebViewControllerLayout = .general
    
    func didReceiveAuthorizationMessage(string: String) {}
    
    var customAnalyticsParameters: PremierInn.PIDictionary? = PIDictionary()
    
    var bookingReference: String? = ""
    

    var upsellOutput: RoomsUpsellOutput = .init(rooms: [], nights: 0)
    var isPerformUpsellChecksCalled = false
    var ciolUpsellViewModel: CiolUpsellViewModelProtocol = MockCiolUpsellViewModel()
    var paymentInputParams: CiolReviewAndPayInputParams?

    var didCallAddedFoodItems = false
    var didCallAddedKidsItems = false
    var didCallUpsellFor = false
    var didCallDidUpdateUpsell = false
    var didCallUpdatedOutput = false
    var didCallGoToNextStep = false

    func performUpsellChecks(completion: @escaping (((any Error)?) -> Void)) {
        isPerformUpsellChecksCalled = true
        completion(nil)
    }

    func addedFoodItems(for roomID: String, withoutUpsellID: String) -> Int? {
        didCallAddedFoodItems = true
        return nil
    }

    func addedKidsItems(for roomID: String, withoutUpsellID: String) -> Int? {
        didCallAddedKidsItems = true
        return nil
    }

    func upsell(from roomID: String, upsellID: String) -> CiolUpsellItemViewModelProtocol? {
        didCallAddedKidsItems = true
        return nil
    }

    func didUpdateUpsell(for item: any PremierInn.CiolUpsellItemViewModelProtocol, action: PremierInn.CiolUpsellDetailsOutputAction) {
        didCallUpsellFor = true
    }

    func updated(with roomOutput: RoomsUpsellOutput?) {
        didCallUpdatedOutput = true
    }

    func goToNextStep() {
        didCallGoToNextStep = true
    }

    func getCellSetup(id: String, isSelected: Bool) -> PremierInn.CiolUpsellCellSetup {
        return .init(isMultiRoom: false)
    }

    func refreshForFailedPayment() {}
    
    func trackPriceBreakdownTapAnalytics() {}
    
    func confirmPreCheckIn(basketReference _: String, completion: @escaping (Error?) -> Void) {
        completion(nil)
    }
    
}

extension MockCiolUpsellInteractor: PremierInn.PrestayDelegate {
    func didUpdateBalance(with newBalance: SimpleNetwork.Cost) {}
}

// Mock Router
class MockCiolUpsellRouter: CiolUpsellRouterProtocol {

    var isShowPaymentCalled = false
    var showUpsellDetailsCalled = false
    var isShowUpsellRoomsCalled = false

    func showPayment() {
        isShowPaymentCalled = true
    }

    func showUpsellRooms(_ roomsUpsellInputParams: RoomsUpsellInputParams, roomsOutputDelegate: RoomsUpsellOutputDelegate) {
        isShowUpsellRoomsCalled = true
    }

    func showUpsellDetails(input: CiolUpsellDetailsInputParams, outputDelegate: any PremierInn.CiolUpsellDetailsOutputDelegate) {
        showUpsellDetailsCalled = true
    }

    func goToPayment(inputParams: PremierInn.CiolReviewAndPayInputParams, failedPaymentDelegate: any PremierInn.CiolUpsellPayDelegate) {}
    func goToCompletion(ciolConfirmationDetails: PremierInn.CiolConfirmationDetails) {}
    func processPayment(with cccpiPageParams: PremierInn.ThreeCiPageParams, using threeCiPageDelegate: any PremierInn.ThreeCiPageDelegate, and webDelegate: any PremierInn.WebViewControllerDelegate, authorizationDelegate: any PremierInn.AuthorizationDelegate, webviewLayout: PremierInn.WebViewControllerLayout) {}
}

// Mock ViewModel for testing
class MockCiolUpsellViewModel: CiolUpsellViewModelProtocol {
    var shouldShowCloseoutMessage: Bool = false

    func resetUpsells(available: [any PremierInn.CiolUpsellItemViewModelProtocol],
                      booked: [any PremierInn.CiolUpsellItemViewModelProtocol]) {}

    var prebookedUpsells: [CiolUpsellItemViewModelProtocol]? = nil
    var priceBreakdownViewModel: CIOLPriceBreakdownViewModelProtocol? = PreStayInteractor.CIOLPriceBreakdownViewModel(ctaTitle: PILocalizedString("Continue"), totalValue: "", items: []) as CIOLPriceBreakdownViewModelProtocol
    var displayedPriceBreakdownModel: CIOLPriceBreakdownViewModelProtocol? {
        return priceBreakdownViewModel
    }
    var addedPriceBreakdownViewModels: [CIOLPriceBreakdownItemViewModelProtocol] = []
    var didCallUpdateEnablement = false
    var didCallUpdateSelected = false
    var selectedUpsells: [any CiolUpsellItemViewModelProtocol] = []

    var availableUpsells: [CiolUpsellItemViewModelProtocol]? = []
    var rooms: [UpsellRoom] = []
    var hasChildren: Bool = false
    var isMultiRoom: Bool = false
    var nights: Int?
    var upsellsAddOnEnabled: Bool = true

    func updateEnablement(isMealDealDisabled: Bool, isBreakfastDisabled: Bool) {
        didCallUpdateEnablement = true
    }

    func updateSelected(statuses: [String : Bool]) {
        didCallUpdateSelected = true
    }
}

class CiolUpsellPresenterTests: XCTestCase {
    struct MockCiolUpsellItemViewModel: CiolUpsellItemViewModelProtocol {
        var legendTitle: String = ""
        var imageURL: URL? = nil
        var title: String = ""
        var costSummary: String = ""
        var itemDescription: String? = ""
        var isFoodUpsell: Bool = false
        var isBooked: Bool = false
        var menuUrls: [RestaurantMenuItem] = [RestaurantMenuItem(name: "Test", description: nil, disclaimer: nil, path: "")]
        var allergensUrls: [AllergenInformation] = [("Test", "")]
        var subtitle: String = ""
        var hasChildren: Bool = false
        var isMultiRoom: Bool = false
        var subitems: [CiolUpsellSubitemViewModel] = []
        var room: UpsellRoom?
        var id: String = ""
        var enabled: Bool = false
        var bookingReference: String? = ""
        var selected: Bool = false
        var isPrebooked: Bool = false
        var isWifi: Bool = false
        var nights: Int = 0
    }

    var presenter: CiolUpsellPresenter!
    var mockView: MockCiolUpsellView!
    var mockInteractor: MockCiolUpsellInteractor!
    var mockRouter: MockCiolUpsellRouter!

    override func setUp() {
        super.setUp()

        mockView = MockCiolUpsellView()
        let mockViewModel = MockCiolUpsellViewModel()
        mockViewModel.rooms = [CiolUpsellRoom(hasChildren: false, numberOfChildren: 0, id: "abc", adults: ["a", "b"], title: "Room1")]
        mockInteractor = MockCiolUpsellInteractor()
        mockInteractor.ciolUpsellViewModel = mockViewModel
        mockRouter = MockCiolUpsellRouter()

        presenter = CiolUpsellPresenter()
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

    func testDisplayUpsellData_CalledOnView() {
        // Act
        presenter.viewIsReady()

        // Assert
        XCTAssertTrue(mockView.isDataReloaded, "reloadData should be called on the view when the presenter is ready")
    }
    
    func testShowUpsellDetails_CalledOnHandleUpsellsRowTapped() {
        // Act
        presenter.handleUpsellsRowTapped(MockCiolUpsellItemViewModel())

        // Assert
        XCTAssertTrue(mockRouter.showUpsellDetailsCalled, "showUpsellDetails should be called on the router when presenter handleUpsellsRowTapped is trigerred")
    }
    
    func testShowUpsellRooms_CalledOnHandleUpsellsRowTapped() {
        // Act
        presenter.handleUpsellsRowTapped(MockCiolUpsellItemViewModel(isMultiRoom: true))

        // Assert
        XCTAssertTrue(mockRouter.isShowUpsellRoomsCalled, "isShowUpsellRoomsCalled should be called on the router when presenter handleUpsellsRowTapped is trigerred and isMultiRoom is true")
    }

    func testPerformUpsellChecks() {
        presenter.handleContinueButtonTap()
        XCTAssertTrue(mockView.didCallShowLoading)
        XCTAssertTrue(mockInteractor.didCallGoToNextStep)
    }
}
