//
//  BookingConfirmationPresenterTests.swift
//  PremierInnTests
//
//  Created by Marcello Mascia on 11/10/2017.
//  Copyright © 2017 Whitbread. All rights reserved.
//

import XCTest
import EventKit
import SimpleNetwork
import PassKit
@testable import PremierInn

private class MockView: BookingConfirmationViewInput {

	var updateDidcall = false
    var scrollToTopDidcall = false
	var setTitleDidCall = false
	var showLoadingDidCall = false
	var hideLoadingDidCall = false
	var showErrorDidCall = false
	var disableBackDidCall = false
    var promptUserToEnableCalendarAccessDidCall = false
    var didDeselectRow = false

    var customAnalyticsParameters: PIDictionary?

    func update(with viewModel: BookingConfirmationViewModel) {

		updateDidcall = true
	}

    func scrollToTop() {

        scrollToTopDidcall = true
    }

	func setScreenTitle(title: String) {

		setTitleDidCall = true
	}

	func showLoadingIndicator() {

		showLoadingDidCall = true
	}

	func hideLoadingIndicator() {

		hideLoadingDidCall = true
	}

	func showError(title: String, message: String?, shouldDie: Bool) {

		showErrorDidCall = true
	}

    func disableBackNavigation(shouldEnableGestureSwipe: Bool) {

		disableBackDidCall = true
	}

    func promptUserToEnableCalendarAccess() {

        promptUserToEnableCalendarAccessDidCall = true
    }

    func reload() {

    }

    func showCalendarPrompt(fullAccessAction: @escaping () -> Void, writeOnlyAction: @escaping () -> Void, cancelAction: @escaping () -> Void) {

    }
    
    func deselectRow(animated: Bool) {
        didDeselectRow = true
    }

    func presentAlert(_ alertController: UIAlertController) {}
}

private class MockInteractor: BookingConfirmationInteractorInput {
    var roomKeyInstructionsModel: InstructionsViewModel?

    var customAnalyticsParameters: PIDictionary?

    var reservation: Reservation?
    
    var screenName: String = ""
    
    var trackScreen: Bool = true
    
    var environment: String = ""
    
    var loggedIn: LoggedInAnalytic = .loggedIn
    
    var timeZone: String = ""
    
    var language: String = "en"
    
    var screenType: String = ""
    
    var customParameters: [String : Any]?
    
    func applicationDidTakeScreenshot() {
        
    }
    
    var preStayInputParams: PreStayInputParams? = nil
    
    var bookingConfirmationViewModel: BookingConfirmationViewModel?
	var isCancelledDidCall = false
	var isCheckInOnlineDidCall = false
    var fetchHotelDidCall = false
	var fetchCalendarEventDidCall = false
    var fetchWalletPassDidCall = false
    var fetchWalletPassResult: BookingConfirmationInteractor.WalletPassFetchResult = .noReservationDetails
    var confirmCheckInOutCalled = false
    var resendInvoiceDidCalled = false
    var bookingDoesNotNeedToRefresh: Bool = false

    var amendedStay: Bool = false
    var isOutOfDate: Bool = false
    var isUpdateCiolStatusCalled: Bool = false

    private(set) var ciolStatus: CiolStatus?

    var pass: PKPass?

    var isBookingFlowComplete: Bool {
        return false
    }

    func reloadStay() {

    }

	var isReservationCancelled: Bool {

		isCancelledDidCall = true
		return false
	}

	var isCheckInOnlineAvailable: Bool {

		isCheckInOnlineDidCall = true
		return true
	}

	var summary: Stay {
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
	var hotel: Hotel? {

		return try! Hotel(dictionary: [
			"name": "Hotel Name",
			"code": "LONLEI",
			"prepaymentAllowed": true,
			"address": ["postcode": "a", "addressline1": "a", "addressline2": "a", "addressline3": "a", "country": "a"],
			"images": [["fileReference": "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"]],
			"contactDetails": ["hotelNationalPhone": "fakeNumber"]
			])
	}

    func fetchHotel() {

        fetchHotelDidCall = true
    }

    func fetchHotel(completion: @escaping (Hotel?, Error?) -> Void) {

    }

	func fetchCalendarEvent(calendarPrompt: @escaping CalendarPrompt, completion: @escaping (EKEvent?, EKEventStore?, Error?) -> Void) {

		fetchCalendarEventDidCall = true

		let store = EKEventStore()
		let event = EKEvent(eventStore: store)

		completion(event, store, nil)
	}

    func loadLatestDetails(completion: @escaping (Bool) -> Void) {}

    func fetchWalletPass(completion: @escaping (BookingConfirmationInteractor.WalletPassFetchResult) -> Void) {
        fetchWalletPassDidCall = true
        completion(fetchWalletPassResult)
    }

    func resendInvoice(completion: @escaping () -> Void) {
        resendInvoiceDidCalled = true
    }

    func performOnlineCheckout(completion: @escaping (Bool) -> Void) {
        confirmCheckInOutCalled = true
    }

    func updateCiolStatus(to ciolStatus: CiolStatus) {
        self.ciolStatus = ciolStatus
        isUpdateCiolStatusCalled = true
    }

    func trackAction(action: String) {

    }
}

private class MockRouter: BookingConfirmationRouterInput {
    
    var viewController: UIViewController?
    
	var bookingFinishDidCall = false
	var selectedHotelInfoDidCall = false
	var priceBreakdownDidCall = false
	var amendDidCall = false
	var faqsDidCall = false
	var addToCalendarDidCall = false
    var addToAppleWalletDidCall = false
	var showDirectionsDidCall = false
	var callHotelDidCall = false
    var startCheckInOnlineDidCall = false
    var startCheckOutOnlineDidCall = false

	func bookingFlowDidFinish() {

		bookingFinishDidCall = true
	}

    func selectedHotelInfo(hotel: Hotel?) {

		selectedHotelInfoDidCall = true
	}

    func priceBreakdown(hotel: Hotel?, reservation: Reservation?) {

		priceBreakdownDidCall = true
	}

	func amendAction(stay: Stay?, hotel: Hotel?) {

		amendDidCall = true
	}

    func selectedFaqs(url: URL?) {

		faqsDidCall = true
	}

	func addToCalendar(event: EKEvent, store: EKEventStore) {

		addToCalendarDidCall = true
	}

    func showDirections(hotel: Hotel?, withSender sender: UIView) {

        showDirectionsDidCall = true
    }

	func callHotel(number: String) {

		callHotelDidCall = true
	}

    func askForAppReview() {

    }

    func openWeb(url: URL) {
        
    }

    func addToWallet(pass: PKPass) {

        // this will only be called when the pass if fetched successfully
        addToAppleWalletDidCall = true
    }

    func startOTP(stay: SimpleNetwork.Stay, roomId: String) {

    }

    func openKeyInWalletDidTap(stay: SimpleNetwork.Stay) {

    }

    func startCheckInOnline(inputParams: PreStayInputParams) {
        startCheckInOnlineDidCall = true
    }
    
    func startCheckOutOnline(checkOutDetails: CheckOutDetails) {
        startCheckOutOnlineDidCall = true
    }

    func showKeyPage(stay: Stay) {

    }

    func showQRCode(stay: Stay) { }

    func showRoomKeyInstructions(model: InstructionsViewModel) { }
}

class BookingConfirmationPresenterTests: XCTestCase {

	private var view: MockView!
	private var interactor: MockInteractor!
	private var router: MockRouter!
	var presenter: BookingConfirmationPresenter!

	override func setUp() {
        super.setUp()

		view = MockView()
		interactor = MockInteractor()
		router = MockRouter()

		presenter = BookingConfirmationPresenter()
		presenter.view = view
		presenter.interactor = interactor
		presenter.router = router
    }
    
    override func tearDown() {

		view = nil
		interactor = nil
		presenter = nil

        super.tearDown()
    }
    
    func testViewIsReady() {

		presenter.viewIsReady()

		XCTAssertTrue(view.setTitleDidCall)
		XCTAssertTrue(view.showLoadingDidCall)

        // XCTAssertTrue(interactor.fetchHotelDidCall)
        // XCTAssertTrue(view.hideLoadingDidCall)
        // XCTAssertTrue(view.updateDidcall)
	}

	func testAddToCalendar() {

		presenter.addToCalendar()

		XCTAssertTrue(interactor.fetchCalendarEventDidCall)
		XCTAssertTrue(router.addToCalendarDidCall)
	}

	func testCallHotel() {

        presenter.callHotelButtonDidTap()

		XCTAssertTrue(router.callHotelDidCall)
	}

	func testCancelButton() {

		presenter.cancelNavigationButtonDidTap()

		XCTAssertTrue(router.bookingFinishDidCall)
	}

	func testDirections() {

		presenter.showDirections(withSender: UIView())

		XCTAssertTrue(router.showDirectionsDidCall)
	}

	func testActions() {

		presenter.viewIsReady()

        presenter.hotelInfoDidTap()
		XCTAssertTrue(router.selectedHotelInfoDidCall)

        presenter.priceBreakdownDidTap()
        XCTAssertTrue(router.priceBreakdownDidCall)

        presenter.amendDidTap()
        XCTAssertTrue(router.amendDidCall)

        presenter.faqDidTap(url: nil)
        XCTAssertTrue(router.faqsDidCall)
	}

    func testPromptUserToEnableCalendarAccessDidCall() {

        presenter?.accessPreviouslyDenied()
        XCTAssertTrue(view?.promptUserToEnableCalendarAccessDidCall ?? false)
    }

    func testAddToWallet() {

        presenter.addToWallet()

        XCTAssertTrue(interactor.fetchWalletPassDidCall)
    }
    
    func testTapOnParkingInformationDeselectRow() {
        presenter.viewIsReady()
        presenter.parkingInfoDidTap()
        XCTAssertTrue(view.didDeselectRow)
    }
    
    func testStartCkeckOutOnline() {
        router.startCheckOutOnline(checkOutDetails: CheckOutDetails(bookerFirstName: "Booker"))
        XCTAssertTrue(router.startCheckOutOnlineDidCall)
    }
    
    func test_checkOutCalled() {
        interactor.performOnlineCheckout { _ in }
        XCTAssertTrue(interactor.confirmCheckInOutCalled)
    }

    func test_resendInvoiceCalled() {
        interactor.resendInvoice {  }
        XCTAssertTrue(interactor.resendInvoiceDidCalled)
    }

    func testViewKeyInWallet() {
        presenter.viewKeyInWallet()
        XCTAssertEqual(interactor.ciolStatus, .walletPass, "Should be equal to .walletPass")
        XCTAssertTrue(interactor.isUpdateCiolStatusCalled, "Should be true")
    }

    func testCtaActionWhenCiolInformationTypeCheckIn() {
        interactor.preStayInputParams = .init(
            flow: .bookingConfirmation,
            stay: interactor.summary,
            hotelCode: "",
            adultsCountDescription: "",
            adultsCount: 3,
            childrenCountDescription: "",
            childrenCount: 3,
            nightsCountDescription: "",
            roomsCountDescription: "",
            rooms: [],
            isBusinessTrip: false,
            hasDERegCard: false,
            isDirect: true
        )

        presenter.ctaAction(ciolInformationType: .checkIn)
        XCTAssertEqual(interactor.ciolStatus, .ciolStarted, "should be equal to .ciolStarted")
        XCTAssertTrue(interactor.isUpdateCiolStatusCalled, "Should be true")
    }

    // MARK: - addToWallet tests

    func testAddToWalletWhenContainsPass() {
        // GIVEN
        interactor.fetchWalletPassResult = .containsPass

        // WHEN
        presenter.addToWallet()

        // THEN
        XCTAssertTrue(view.showLoadingDidCall)

        let expectation = predicateExpectation(
            description: #function,
            self.view.hideLoadingDidCall == true &&
            self.view.showErrorDidCall == true
        )

        wait(for: [expectation], timeout: 3)
    }

    func testAddToWalletWhenPassFetchSuccessful() {
        // GIVEN
        interactor.fetchWalletPassResult = .fetchSuccessful(PKPass())

        // WHEN
        presenter.addToWallet()

        // THEN
        XCTAssertTrue(view.showLoadingDidCall)

        let expectation = predicateExpectation(
            description: #function,
            self.view.hideLoadingDidCall == true &&
            self.router.addToAppleWalletDidCall == true
        )

        wait(for: [expectation], timeout: 3)
    }

    func testAddToWalletWhenPassFetchFailed() {
        // GIVEN
        interactor.fetchWalletPassResult = .fetchFailed(NSError(domain: "SomeError", code: 401))

        // WHEN
        presenter.addToWallet()

        // THEN
        XCTAssertTrue(view.showLoadingDidCall)

        let expectation = predicateExpectation(
            description: #function,
            self.view.hideLoadingDidCall == true &&
            self.view.showErrorDidCall == true
        )

        wait(for: [expectation], timeout: 3)
    }

    func testAddToWalletPassWhenNoReservationDetails() {
        // GIVEN
        interactor.fetchWalletPassResult = .noReservationDetails

        // WHEN
        presenter.addToWallet()

        // THEN
        XCTAssertTrue(view.showLoadingDidCall)

        let expectation = predicateExpectation(
            description: #function,
            self.view.hideLoadingDidCall == true &&
            self.view.showErrorDidCall == true
        )

        wait(for: [expectation], timeout: 3)
    }
}
