//
//  ReservationsViewTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 07/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockPresenter: ReservationsPresenterProtocol {

    var viewIsReadyDidCall = false
    var findBookingButtonDidTapDidCall = false
    var bookingDetailsButtonDidTapDidCall = false
    var planTripButtonDidTapDidCall = false
    var searchHotelButtonDidTapDidCall = false
    var loginButtonDidTapDidCall = false
    var refreshButtonDidTapDidCall = false
    var getKeyButtonDidTapDidCall = false
    var instructionsDidTapDidCall = false

    func viewIsReady() {

        viewIsReadyDidCall = true
    }

    func findBookingButtonDidTap() {

        findBookingButtonDidTapDidCall = true
    }

    func bookingDetailsButtonDidTap(stay: Stay) {

        bookingDetailsButtonDidTapDidCall = true
    }

    func planTripButtonDidTap(hotelCode: String) {

        planTripButtonDidTapDidCall = true
    }

    func searchHotelButtonDidTap() {

        searchHotelButtonDidTapDidCall = true
    }

    func loginButtonDidTap() {

        loginButtonDidTapDidCall = true
    }

    func refreshButtonDidTap() {

        refreshButtonDidTapDidCall = true
    }

    func checkInOnlineDidTap(stay: Stay) {
        
    }

    func getKeyButtonDidTap(stay: Stay) {

        getKeyButtonDidTapDidCall = true
    }

    func qrCodeButtonDidTap(stay: SimpleNetwork.Stay) {

    }
    
    func instructionsDidTap(stay: Stay) {
        instructionsDidTapDidCall = true
    }

    func connectToWifiDidTap(freeSSID: String, paidSSID: String) {

    }

	func addDigitalKeyButtonDidTap(stay: Stay) {

	}

	func showDigitalKeyButtonDidTap(stay: SimpleNetwork.Stay) {

	}
}

class ReservationsViewTests: XCTestCase {

    private var viewController: ReservationsListViewController!
    private var presenter: MockPresenter!
    private var analytics: MockAnalyticsManager!

    // MARK: - Lifecycle

    override func setUp() {

        presenter = MockPresenter()

        viewController = ReservationsListViewController()
        viewController.presenter = presenter

        analytics = MockAnalyticsManager()
        viewController.analytics = analytics
    }

    override func tearDown() {

        presenter = nil
        viewController = nil
        analytics = nil

        super.tearDown()

    }

    // MARK: - Tests

    func testView_whenViewAppears_invokesTrackState() {
        UserSessionManager.sharedInstance.piUserLoggedOut()

        viewController.beginAppearanceTransition(true, animated: false)
        viewController.endAppearanceTransition()

        let state = analytics.states.first

        XCTAssertEqual(state, "iOS:PI:UK: My Bookings")
    }

    func testViewIsReady() {

        viewController.viewWillAppear(false)
        XCTAssertTrue(presenter.viewIsReadyDidCall)
    }

    func testFindBookigButton() {

        viewController.findBookingDidTap(cell: MyBookingsErrorsViewCell())
        XCTAssertTrue(presenter.findBookingButtonDidTapDidCall)
    }

    func testFindBookigButton_OtherButton() {

        viewController.findBookingButtonDidTap()
        XCTAssertTrue(presenter.findBookingButtonDidTapDidCall)
    }

    func testRefreshButton() {

        viewController.refreshButtonDidTap()
        XCTAssertTrue(presenter.refreshButtonDidTapDidCall)
    }

    func testActionButton_SearchHotel() {

        UserSessionManager.sharedInstance.loggedIn(with: try! User(title: "Mr", firstName: "Mario", lastName: "Rossi"))

        viewController.actionButtonDidTap(cell: MyBookingsErrorsViewCell())
        XCTAssertTrue(presenter.searchHotelButtonDidTapDidCall)
        XCTAssertFalse(presenter.loginButtonDidTapDidCall)

        UserSessionManager.sharedInstance.piUserLoggedOut()
    }

    func testActionButton_Login() {

        UserSessionManager.sharedInstance.piUserLoggedOut()

        viewController.actionButtonDidTap(cell: MyBookingsErrorsViewCell())
        XCTAssertFalse(presenter.searchHotelButtonDidTapDidCall)
        XCTAssertTrue(presenter.loginButtonDidTapDidCall)
    }
}
