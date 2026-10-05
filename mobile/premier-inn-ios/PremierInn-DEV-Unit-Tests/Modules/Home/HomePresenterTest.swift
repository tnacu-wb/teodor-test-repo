//
//  HomePresenterTest.swift
//  PremierInnTests
//
//  Created by Simon Antoine on 19/01/2021.
//  Copyright © 2021 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

//class HomeViewMock: HomeView, HomeViewInput {
//    var viewModel: HomeViewModel?
//    var eventHandler: HomeViewEventHandler?
//
//    var isBusy: Bool!
//    func setProcessing(is busy: Bool) {
//        isBusy = busy
//    }
//}

class HomeViewMock: HomeView {
    var isProcessing = false
    var didShowEmployeeRatesConfirmationAlert = false
    var shownAlertMessage: (title: String, message: String)?
    var shownErrorAlert: (title: String, error: Error)?

    override func toggleProcessing(isProcessing: Bool, with containerView: UIView?) {
        self.isProcessing = isProcessing
    }

    override func showAlertMessage(title: String, message: String) {
        shownAlertMessage = (title, message)
    }
}

class HomeRouterMock: HomeRouterInput {

    var selectedLocationView: UIView!
    var selectedGuestsView: UIView!
    var selectedNightView: UIView!
    var resultController: UIViewController!
    
    var selectedLocationCounter = 0
    var selectedGuestCounter = 0
    var selectedNightCounter = 0
    var showGDPRInfoCounter = 0
    var searchNeraMeCounter = 0
    var resetNavigationCounter = 0
    var hotelCode: String!
    var availabilityResponse: HotelAvailabilityResponse!
    var suggestion: Suggestion?
    var dataHotelCode: String!
    var showHomeCounter = 0
    var bookingDetailsId: String!
    var showBBSplahCounter = 0
    var cardExpiredMessage: String!
    var cardExpiredCompletion: (() -> Void?)!
    var announcementMessage: NotificationsMessage!
    var ciolStay: Stay!
    var ciolSession: CheckInOnlineSessionResponse!
    var startCheckInOnlineParams: PreStayInputParams?
    var amendBookingId: String!
    var showCalendarIdentifier: String!
    var showFindReservationWasCalled: Bool = false
    
    func selectedLocation(sender: UIView) { selectedLocationView = sender}
    func selectedNights(sender: UIView) { selectedNightView = sender }
    func selectedGuests(sender: UIView) { selectedGuestsView = sender }
    func presentResultController(_ controller: UIViewController) { resultController = controller}
    func selectedLocation() { selectedLocationCounter += 1 }
    func selectedNights() { selectedNightCounter += 1 }
    func selectedGuests() { selectedGuestCounter += 1 }
    func showGDPRInfo() { showGDPRInfoCounter += 1 }
    func searchNearMe() { searchNeraMeCounter += 1 }
    func resetNavigation() { resetNavigationCounter += 1 }
    func showHotelDetails(hotelCode: String, hotelBrand: HotelBrand?, availabilityResponse: HotelAvailabilityResponse, and suggestion: Suggestion?) {
        self.hotelCode = hotelCode
        self.availabilityResponse = availabilityResponse
        self.suggestion = suggestion
    }
    func showDatelessHotelDetails(dashboardHotelDetails: DashboardHotelDetails) { dataHotelCode = dashboardHotelDetails.code }
    func showDatelessHotelDetailsDeepLink(hotelCode: String, hotelBrand: HotelBrand) { dataHotelCode = hotelCode }

    func showHome() { showHomeCounter += 1 }
    func showBookingDetails(identifier: String) { bookingDetailsId = identifier }
    func showBBSplash() { showBBSplahCounter += 1 }
    func showBBCentralCardExpired(with message: String, completion: @escaping () -> Void?) {
        cardExpiredMessage = message
        cardExpiredCompletion = completion
    }
    func showAnnouncementMessage(with message: NotificationsMessage) { announcementMessage = message}
    func checkInOnline(stay: Stay?, checkInSession: CheckInOnlineSessionResponse) {
        ciolStay = stay
        ciolSession = checkInSession
    }
    func showAmendBooking(identifier: String) {
        amendBookingId = identifier
    }
    func showCalendarAndHotel(with dashboardHotelDetails: DashboardHotelDetails) {
        showCalendarIdentifier = dashboardHotelDetails.code
    }
    func showFindReservation(arrivalDate: String, reservationNumber: String, lastName: String?) {
        showFindReservationWasCalled = true
    }
    func showPromotionPopover() {}

    func startCheckInOnline(preStayInputParams: PremierInn.PreStayInputParams, completion: @escaping () -> Void?) {
        startCheckInOnlineParams = preStayInputParams
        completion()
    }
}

class HomeInteractorMock: HomeInteractorInput {

    var screenName: String = ""
    var trackScreen: Bool = true
    var environment: String = "debug"
    var loggedIn = LoggedInAnalytic.loggedIn
    var timeZone: String = ""
    var language: String = ""
    var screenType: String = ""
    var customParameters: [String : Any]?
    func applicationDidTakeScreenshot() { }

    var viewModel: HomeViewModel {
        return (
            "111",
            "222",
            "333",
            "444",
            false,
            "666"
        )
    }
    var suggestion: Suggestion?
    var criteria: Criteria?
    var hasAcceptedGDPR: Bool = false
    var hasShownBBCardExpiredMessage: Bool = false
    var shouldShowBBCardExpiredAlert: Bool = false
    var bbRecentSearchErrorViewModel: (title: String, message: String) {
        return ("title", "message")
    }
    var announcementMessage: NotificationsMessage?
    
    var shouldShowBBRecentSearchErrorIndex: Int!
    var searchSuggestion: Suggestion!
    var searchCriteria: Criteria!
    var searchCompletion: ((Result<UIViewController>) -> Void)!
    var availabilityHotalCode: String!
    var availibilityCriteria: Criteria!
    var availibilityCompletion: ((Result<HotelAvailabilityResponse>) -> Void)!
    var searchIndex: Int!
    var searchResultCompletion: ((Result<UIViewController>) -> Void)!
    var userDismissedCoronavirusInformationBannerCount = 0
    var ciolStay: Stay!
    var ciolCompletion: ((Bool, PremierInn.PreStayInputParams?) -> Void)!
    var returnError = false
    var hotelInformationSlug: String!
    var hotelIncompletion: ((Result<Hotel>) -> Void)!
    func shouldShowBBRecentSearchError(forRecentSearchAt index: Int) -> Bool { shouldShowBBRecentSearchErrorIndex = index
        return returnError
    }
    var employeeRecentSearchErrorViewModel: (title: String, message: String) {
        return ("title", "message")
    }
    func shouldShowEmployeeRateSearchError(forRecentSearchAt index: Int) -> Bool {
        return false
    }
    func search(with suggestion: Suggestion, and criteria: Criteria, completion: @escaping (Result<UIViewController>) -> Void) {
        searchSuggestion = suggestion
        searchCriteria = criteria
        searchCompletion = completion
    }
    func searchAvailability(hotelCode: String, hotelBrand: HotelBrand?, criteria: Criteria, completion: @escaping (Result<HotelAvailabilityResponse>) -> Void) {
        availibilityCriteria = criteria
        availibilityCompletion = completion
        availabilityHotalCode = hotelCode
    }
    func search(withRecentSearchAt index: Int, completion: @escaping (Result<UIViewController>) -> Void) {
        searchIndex = index
        searchResultCompletion = completion
    }
    func userDismissedCoronavirusInformationBanner() {
        userDismissedCoronavirusInformationBannerCount += 1
    }

    func fetchHotelInformation(slug: String, completion: @escaping (Result<Hotel>) -> Void) {
        hotelInformationSlug = slug
    }
    func switchedSearchesComponent(type: SearchesComponentType) {}

    func startCheckInOnline(for stay: SimpleNetwork.Stay, completion: @escaping (Bool, PremierInn.PreStayInputParams?) -> Void) {
        ciolStay = stay
        ciolCompletion = completion
    }
}

class HomePresenterTest: XCTestCase {

    private var view: HomeViewMock!
    private var interactor: HomeInteractorMock!
    private var router: HomeRouterMock!
    var presenter: HomePresenter!

    override func setUp() {
        super.setUp()

        view = HomeViewMock()
        interactor = HomeInteractorMock()
        presenter = HomePresenter()
        presenter.interactor = interactor

        router = HomeRouterMock()

        presenter.router = router
    }
    
    override func tearDown() {
        
        presenter = nil
        router = nil
        interactor = nil
        view = nil
        LinkHandler.adobeTrackingCode = nil
        super.tearDown()
    }
    
    // the feature flag doesn't do anything when the appShortcut is assigned directly since the
    // feature flag now blocks it at the point of identifying the deepLinkShortcut
    func test_Deeplink_HDP() {
        let remoteConfig = MockRemoteConfig(featureDeeplinkHDP: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        presenter.fetchAndHandleHotelInformation(slug: "london-blackfriars-fleet-street.html", criteria: nil)

        XCTAssertEqual("london-blackfriars-fleet-street.html", interactor.hotelInformationSlug)
    }
    
    func test_Deeplink_Ciol_Disabled() {
        let remoteConfig = MockRemoteConfig(featureDeeplinkCIOL: false)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        LinkHandler.sharedInstance.activeAppShortcut = AppShortcut.ciol(arrivalDate: "2024-10-30", reservationNumber: "FAKEAKU1099261", lastName: "Testus")

        XCTAssertFalse(router.showFindReservationWasCalled)
    }
    
    func test_CIOLFail() {
        presenter.view = view

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

        let stay = try! Stay(dictionary: dictionary)
        
        try? presenter.manager.add(stay)
        presenter.showCheckInOnline(with: "BBER264250")
        presenter.ctaAction(ciolInformationType: .checkIn)
        
        XCTAssertEqual(interactor.ciolStay, stay)
        XCTAssertNil(router.startCheckInOnlineParams)

        interactor.ciolCompletion(false, nil)
        XCTAssertNil(router.startCheckInOnlineParams)
    }
    
    func test_CIOLNoIdFound() {
        presenter.showCheckInOnline(with: "111111111111")
        XCTAssertNil(interactor.ciolStay)
        XCTAssertNil(router.ciolStay)
    }
    
    func test_showBooking() {
        presenter.showBooking(with: "1234")
        XCTAssertEqual(router.bookingDetailsId, "1234")
    }
    
    func test_update() {
        var criteria = Criteria()
        criteria.rooms = [Room()]
        presenter.update(with: criteria)
        
        XCTAssertEqual(interactor.criteria, criteria)
//        XCTAssertEqual(interactor.viewModel.location, presenter.view?.viewModel?.location)
    }

    func testPushNotificationTrackingCodeIsSetIfAvailable() {
        // GIVEN
        let expectation = expectation(description: "adobeTrackingCode is set")
        let notificationCenter = NotificationCenter()
        let presenter = HomePresenter(
            notificationCenter: notificationCenter
        )
        presenter.adobeTrackingCodeUpdated = {
            expectation.fulfill()
        }
        let dictionary = [
            "type": "hotel_details",
            "trackingCode": "some_tracking_code",
            "hotelCode": "LONKIN"
        ]
        let shortcut = LinkHandler.handleCustomPushPayload(
            userInfo: dictionary
        )
        let linkHandler = LinkHandler(
            notificationCenter: notificationCenter
        )

        // WHEN
        linkHandler.activeAppShortcut = shortcut
        wait(for: [expectation], timeout: .ocd)

        // THEN
        XCTAssertEqual(presenter.adobeTrackingCode, "some_tracking_code")
    }

    func testPushNotificationTrackingCodeIsNilIfNotAvailable() {
        // GIVEN
        let expectation = expectation(description: "adobeTrackingCode is not set")
        let notificationCenter = NotificationCenter()
        let presenter = HomePresenter(
            notificationCenter: notificationCenter
        )
        presenter.adobeTrackingCodeUpdated = {
            expectation.fulfill()
        }
        let dictionary = [
            "type": "hotel_details",
            "hotelCode": "LONKIN"
        ]
        let shortcut = LinkHandler.handleCustomPushPayload(
            userInfo: dictionary
        )
        let linkHandler = LinkHandler(
            notificationCenter: notificationCenter
        )

        // WHEN
        linkHandler.activeAppShortcut = shortcut
        wait(for: [expectation], timeout: .ocd)

        // THEN
        XCTAssertEqual(presenter.adobeTrackingCode, nil)
    }
}
