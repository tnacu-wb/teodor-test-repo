//
//  ReservationsPresenterTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Marcello Mascia on 07/12/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockView: ReservationsViewProtocol {
    
    var customAnalyticsParameters: PIDictionary?

    var loadViewModelDidCall = false
    var showErrorMessageDidCall = false
    var showRefreshButtonDidCall = false
    var hideRefreshButtonDidCall = false
    var showGDPRButtonOrNothingDidCall = false
    var toggleLoadingIndicatorDidCall = false
    
    func loadViewModel(with: ActivePastStays) {
        
        loadViewModelDidCall = true
    }
    
    func showErrorMessage(title: String, message: String) {
        
        showErrorMessageDidCall = true
    }
    
    func showRefreshButton() {
        
        showRefreshButtonDidCall = true
    }

    func hideRefreshButton() {
        hideRefreshButtonDidCall = true
    }

    func showGDPRButtonOrNothing() {
        
        showGDPRButtonOrNothingDidCall = true
    }
    func toggleLoadingIndicator(isLoading: Bool, hideTableAsWell: Bool) {
        toggleLoadingIndicatorDidCall = true
    }

    func presentAlertController(_ controller: UIAlertController) {

    }
}

private class MockRouter: ReservationsRouterProtocol {
	func showDigitalKeyButtonDidTap(stay: SimpleNetwork.Stay) {
		
	}
	
    func showKeyPage(with stay: SimpleNetwork.Stay) {
        
    }
    
    func showWifiOptions(freeSSID: String, paidSSID: String) {

    }
    

    var showFindBookingDidCall = false
    var showBookingDetailsDidCall = false
    var showPlanYourTripDidCall = false
    var showSearchHotelDidCall = false
    var showLoginDidCall = false
    var showKeyDetailsDidCall = false
    var showDisclaimerDidCall = false
    var viewController: UIViewController? 
    var showRoomKeyInstructionsDidCall = false

    func showFindBooking() {
        
        showFindBookingDidCall = true
    }
    
    func showBookingDetails(with: Stay) {
        
        showBookingDetailsDidCall = true
    }
    
    func showPlanYourTrip(hotelCode: String) {
        
        showPlanYourTripDidCall = true
    }
    
    func showSearchHotel() {
        
        showSearchHotelDidCall = true
    }
    
    func showLogin() {
        
        showLoginDidCall = true
    }

    func checkInOnline(with identifier: String, surname: String, and arrivalDate: Date, and checkInResponse: CheckInOnlineSessionResponse) {}

    func showKeyDetails(reservationDetails: ReservationDetails) {

        showKeyDetailsDidCall = true
    }

    func showQRCode(with: SimpleNetwork.Stay) {

    }
    
    func showCiolDisclaimer(viewModel: CiolInformationModel) {
        showDisclaimerDidCall = true
    }
    
    func startCheckInOnline(preStayInputParams: PreStayInputParams) {
        
    }

    func showRoomKeyInstructions(model: InstructionsViewModel) {
        showRoomKeyInstructionsDidCall = true
    }

	func addDigitalKeyButtonDidTap(stay: Stay) {

	}
}

private class MockInteractor: ReservationsInteractorProtocol {

    var screenName: String = ""
    var trackScreen: Bool = true
    var environment: String = "debug"
    var loggedIn = LoggedInAnalytic.loggedIn
    var timeZone: String = ""
    var language: String = ""
    var screenType: String = ""
    var customParameters: [String : Any]?
    func applicationDidTakeScreenshot() { }
    
    var stayToBeCheckedId: Stay?
    
    var customAnalyticsParameters: PIDictionary?
    
    var listenToReservationChangesDidCall = false
    var refreshStaysDidCall = false
    var categoryLabelsShouldError = false
    var roomKeyInstructionsDidCall = false
    func listenToReservationChanges(with: @escaping (Result<ActivePastStays>) -> Void) {
        
        listenToReservationChangesDidCall = true
    }

    func refreshStays(completion: @escaping (Bool?) -> Void) {

        refreshStaysDidCall = true
    }

    func startCheckInOnline(completion: @escaping (Bool, PreStayInputParams?) -> Void) {
        
    }

    func getRoomKeyInstructionsModel(stay: Stay) -> InstructionsViewModel? {
        roomKeyInstructionsDidCall = true
        return InstructionsViewModel(
            type: .roomKeyWithQRCode,
            bookingReference: "AJK345987",
            reservationDetails: ReservationDetails(reservationId: "AJK345987", surname: "familyname", arrivalDate: Date.now, business: false, token: nil),
            isQRCodeEnabled: true
        )
    }

    func updateCiolStatus(forStay stay: Stay?, to ciolStatus: CiolStatus) { }
}

class ReservationsPresenterTests: XCTestCase {
    
    private var view: MockView!
    private var router: MockRouter!
    private var interactor: MockInteractor!
    private var presenter: ReservationsPresenter!

    var stay: Stay {
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "LONBLA"
        dictionary["hotelName"] = "Alpha2 London Blackfriars (Fleet Street)"
        dictionary["hotelLatitude"] = 50.823071
        dictionary["hotelLongitude"] = -0.140976
        dictionary["lastName"] = "APPS"
        dictionary["identifier"] = "BBER264250"
        dictionary["arrivalDate"] = "2017-10-09"
        dictionary["checkOutDate"] = "2017-10-10"
        dictionary["imageURL"] = "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"
        
        return try! Stay(dictionary: dictionary)
    }

    override func setUp() {
        
        view = MockView()
        router = MockRouter()
        interactor = MockInteractor()
        
        presenter = ReservationsPresenter()
        presenter.view = view
        presenter.router = router
        presenter.interactor = interactor
    }
    
    override func tearDown() {
        
        presenter = nil
        view = nil
        router = nil
        interactor = nil
        
        super.tearDown()
    }
    
    func testViewIsReady() {
        
        presenter.viewIsReady()
        XCTAssertTrue(view.toggleLoadingIndicatorDidCall)
        XCTAssertTrue(interactor.listenToReservationChangesDidCall)
        XCTAssertTrue(interactor.refreshStaysDidCall)
    }
    
    func testRefreshButton() {
        
        presenter.refreshButtonDidTap()
        XCTAssertTrue(view.toggleLoadingIndicatorDidCall)
        XCTAssertTrue(interactor.refreshStaysDidCall)
    }
    
    func testFindBooking() {
        
        presenter.findBookingButtonDidTap()
        XCTAssertTrue(router.showFindBookingDidCall)
    }
    
    func testBookingDetails() {
        presenter.bookingDetailsButtonDidTap(stay: stay)
        XCTAssertTrue(router.showBookingDetailsDidCall)
    }
    
    func testPlanYourTrip() {
        
        presenter.planTripButtonDidTap(hotelCode: "ASDAS")
        XCTAssertTrue(router.showPlanYourTripDidCall)
    }
    
    func testSearchHotel() {
        
        presenter.searchHotelButtonDidTap()
        XCTAssertTrue(router.showSearchHotelDidCall)
    }
    
    func testLoginButton() {
        
        presenter.loginButtonDidTap()
        XCTAssertTrue(router.showLoginDidCall)
    }
    
    func testHandleChanges() {
        
        UserSessionManager.sharedInstance.loggedIn(with: try! User(title: "Mr", firstName: "Mario", lastName: "Rossi"))
        
        let result = (headerMessage: nil, activeStays: [], pastStays: [], importAction: true) as ActivePastStays
        presenter.handleChanges(result: Result.success(result: result))
        
        XCTAssertTrue(view.toggleLoadingIndicatorDidCall)
        XCTAssertTrue(view.showRefreshButtonDidCall)
        XCTAssertTrue(view.loadViewModelDidCall)
        
        UserSessionManager.sharedInstance.piUserLoggedOut()
    }
    
    func testHandleChanges_Failure() {
        
        UserSessionManager.sharedInstance.piUserLoggedOut()
        
        presenter.handleChanges(result: Result.failure(error: LoginInteractorError.userNameIsEmpty))
        
        XCTAssertTrue(view.toggleLoadingIndicatorDidCall)
        XCTAssertTrue(view.showErrorMessageDidCall)
        XCTAssertTrue(view.hideRefreshButtonDidCall)
    }
    
    func testCheckInButton() {
        presenter.checkInOnlineDidTap(stay: stay)
        XCTAssertTrue(router.showDisclaimerDidCall)
    }
    
    func testInstructionsSuccess() {
        interactor.categoryLabelsShouldError = false
        presenter.viewIsReady()
        presenter.instructionsDidTap(stay: stay)
        XCTAssertTrue(interactor.roomKeyInstructionsDidCall)
        XCTAssertTrue(router.showRoomKeyInstructionsDidCall)
        XCTAssertTrue(view.toggleLoadingIndicatorDidCall)
        XCTAssertFalse(view.showErrorMessageDidCall)
    }

}
