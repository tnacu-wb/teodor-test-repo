//
//  HomeViewTest.swift
//  PremierInnTests
//
//  Created by Simon Antoine on 19/01/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork

@testable import PremierInn

class HomePresenterMock: HomePresenterInput {
    //Var from Protocol
    var view: HomeView?
    var interactor: HomeInteractorInput?
    var router: HomeRouterInput?
    
    //Var for testing
    var refreshViewCount = 0
    var selectedSuggestion: Suggestion!
    var selectedCriteria: Criteria!
    var foundLocationSuggestion: Suggestion!
    
    func refreshView() {
        refreshViewCount += 1
    }
    
    func selected(suggestion: Suggestion) {
        selectedSuggestion = suggestion
    }
    
    func selected(criteria: Criteria) {
        selectedCriteria = criteria
    }
    
    func foundLocation(suggestion: Suggestion) {
        foundLocationSuggestion = suggestion
    }
    
    //extension HomeViewEventHandler
    var hasAcceptedGDPRChangesTest: Bool!
    var currentCriteriaTest: Criteria!
    var viewIsReadyCount = 0
    var selectedLocationView: UIView!
    var selectedNightView: UIView!
    var selectedGuestsView: UIView!
    var selectedSearchCount = 0
    var showGDPRInfoCount = 0
    var updateCriteria: Criteria!
    var selectedSearchIndex: Int!
    var showBookingId: String!
    var showHotelCalendarId: String!
    var showCheckInOnlineId: String!
    var showAmendBookingId: String!
    var coronaCount = 0
    
    var criteria = Criteria.init()
}

extension HomePresenterMock: HomeViewEventHandler {

    var adobeTrackingCode: String? {
        get { "some_tracking_code" }
        set { }
    }
    var adobeTrackingCodeUpdated: (() -> Void)? { return nil }
    var hasAcceptedGDPRChanges: Bool { return false }
    var currentCriteria: Criteria? { return  criteria}
    func viewIsReady() { viewIsReadyCount += 1 }
    func selectedLocation(sender: UIView) { selectedLocationView = sender }
    func selectedNights(sender: UIView) { selectedNightView = sender }
    func selectedGuests(sender: UIView) { selectedGuestsView = sender }
    func selectedSearch() { selectedSearchCount += 1 }
    func showGDPRInfo() { showGDPRInfoCount += 1 }
    func update(with criteria: Criteria) { updateCriteria = criteria }
    func selectedRecentSearch(at index: Int) { selectedSearchIndex = index }
    func showBooking(with identifier: String) { showBookingId = identifier }
    func showCheckInOnline(with identifier: String) { showCheckInOnlineId = identifier }
    func showAmendBooking(with identifier: String) { showAmendBookingId = identifier }
    func dismissCoronavirusInformationBannerTapped() { coronaCount += 1 }
    func showHotelCalendar(with dashboardHotelDetails: DashboardHotelDetails) { showHotelCalendarId = dashboardHotelDetails.code }
    func switchedSearchesComponent(type: SearchesComponentType) {}
}

class HomeViewTest: XCTestCase {
    // MARK: - Properties

    fileprivate var presenter: HomePresenterMock!
    var controller: HomeView!
    
    override func setUp() {
        presenter = HomePresenterMock()

        controller = HomeView()
        controller.eventHandler = presenter
    }
    
    override func tearDown() {

        presenter = nil
        controller = nil

        super.tearDown()
    }

    func test_ViewDidLoad() {
        controller.viewDidLoad()
        
        XCTAssertEqual(controller.view.backgroundColor, UIColor.ColourLD4)
    }
    
    func test_ViewWillAppear() {
        XCTAssertEqual(presenter.showGDPRInfoCount, 0)
        XCTAssertEqual(presenter.viewIsReadyCount, 0)
        
        controller.viewWillAppear(true)
        
        XCTAssertEqual(presenter.showGDPRInfoCount, 1)
        XCTAssertEqual(presenter.viewIsReadyCount, 1)
    }
    
    func test_showCIOL() {
        controller.showCheckInOnline(with: "1234")
        
        XCTAssertEqual(presenter.showCheckInOnlineId, "1234")
    }

    func test_showAmendBooking() {
        controller.showAmendBooking(with: "1234")

        XCTAssertEqual(presenter.showAmendBookingId, "1234")
    }
}
