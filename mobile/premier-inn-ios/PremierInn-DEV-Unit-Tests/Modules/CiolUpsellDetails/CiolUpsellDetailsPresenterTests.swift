//
//  CiolUpsellDetailsPresenterTests.swift
//  PremierInnTests
//
//  Created by Oltean Vasile Bogdan on 24.09.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn
@testable import SimpleNetwork

// Mock View
class MockCiolUpsellDetailsView: CiolUpsellDetailsViewProtocol {
    var customAnalyticsParameters: PIDictionary?
    var isDisplayUpsellDataCalled = false
    var displayedViewModel: CiolUpsellDetailsViewModelProtocol?
    var didCallUpdateButton = false
    var didCallUpdateEnablement = false

    func displayUpsellData(viewModel: CiolUpsellDetailsViewModelProtocol) {
        isDisplayUpsellDataCalled = true
        displayedViewModel = viewModel
    }

    func updateButton(state: CiolUpsellDetailsButton.ButtonState) {
        didCallUpdateButton = true
    }

    func updateEnablement(models: [CiolUpsellSubitemViewModel]) {
        didCallUpdateEnablement = true
    }
}

// Mock Interactor
class MockCiolUpsellDetailsInteractor: CiolUpsellDetailsInteractorProtocol {
    func trackAllergensScreenAnalytics() { }
    
    var customAnalyticsParameters: PIDictionary? = [:]
    var ciolUpsellDetailsViewModel: CiolUpsellDetailsViewModelProtocol = MockCiolUpsellDetailsViewModel()
    var addButtonConfig: CiolUpsellDetailsButton.ButtonState = .disabled
    var output: CiolUpsellDetailsOutput = .init(mainItem: MockCiolUpsellItem(), upsells: [], maxValue: 0)
    var didCallDidUpdateUpsell = false
    var didCallSendUpsellOutput = false
    weak var outputDelegate: CiolUpsellDetailsOutputDelegate?

    func trackMenuScreenAnalytics() { }

    func sendUpsellOutput(action: CiolUpsellDetailsOutputAction) {
        didCallSendUpsellOutput = true
    }

    func didUpdateUpsell(with id: String, action: CIOLStepperAction, completion: @escaping () -> ()) {
        didCallDidUpdateUpsell = true
        completion()
    }
}

// Mock Router
class MockCiolUpsellDetailsRouter: CiolUpsellDetailsRouterProtocol {
    var isShowMenuAllergyCalled = false

    func showMenuOrAllergyInfo(url: String) {
        isShowMenuAllergyCalled = true
    }
}

// Mock ViewModel for testing
class MockCiolUpsellDetailsViewModel: CiolUpsellDetailsViewModelProtocol {
    var prebookedItems: [String]? = nil
    
    var addedFoodItems: Int = 0

    var addedKidsItems: Int = 0
    
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
        var subitems: [PremierInn.CiolUpsellSubitemViewModel] = []
        var room: (any PremierInn.UpsellRoom)?
        var id: String = ""
        var enabled: Bool = false
        var bookingReference: String? = ""
        var selected: Bool = false
        var isPrebooked: Bool = false
        var isWifi: Bool = false
        var nights: Int = 0
    }
    
    var upsell: CiolUpsellItemViewModelProtocol = MockCiolUpsellItemViewModel()
    var butonConfig: PremierInn.CiolUpsellDetailsButton.ButtonState = .booked(true)
    var removeButtonConfig: PremierInn.CiolUpsellDetailsButton.ButtonState? = nil
}

class CiolUpsellDetailsPresenterTests: XCTestCase {

    var presenter: CiolUpsellDetailsPresenter!
    var mockView: MockCiolUpsellDetailsView!
    var mockInteractor: MockCiolUpsellDetailsInteractor!
    var mockRouter: MockCiolUpsellDetailsRouter!

    override func setUp() {
        super.setUp()

        mockView = MockCiolUpsellDetailsView()
        mockInteractor = MockCiolUpsellDetailsInteractor()
        mockRouter = MockCiolUpsellDetailsRouter()

        presenter = CiolUpsellDetailsPresenter()
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
        XCTAssertTrue(mockView.isDisplayUpsellDataCalled, "displayUpsellData should be called on the view when the presenter is ready")
    }

    func testShowMenuOrAllergyInfo_CalledOnShowAllergyInfo() {
        // Act
        presenter.showAllergyInfo()

        // Assert
        XCTAssertTrue(mockRouter.isShowMenuAllergyCalled, "showAllergyInfo should call showMenuOrAllergyInfo on the router")
    }

    func testShowMenuOrAllergyInfo_CalledOnShowMenu() {
        // Act
        presenter.showMenu()

        // Assert
        XCTAssertTrue(mockRouter.isShowMenuAllergyCalled, "showMenu should call showMenuOrAllergyInfo on the router")
    }

    func testDidUpdateUpsell() {
        presenter.didUpdateUpsell(with: "", action: .didAdd(1), completion: {})
        XCTAssertTrue(mockInteractor.didCallDidUpdateUpsell)
        XCTAssertTrue(mockView.didCallUpdateEnablement)
        XCTAssertTrue(mockView.didCallUpdateButton)
    }

    func testSendUpsellOutput() {
        presenter.sendUpsellOutput(action: .add)
        XCTAssertTrue(mockInteractor.didCallSendUpsellOutput)
    }
}
