//
//  HotelDetailsPresenterTests.swift
//  PremierInn
//
//  Automatically Created by Ophion
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SwiftUI
import SimpleNetwork
@testable import PremierInn

private class MockView: HotelDetailsViewProtocol {

    func updateRoomImage(withDesiredRoomIndex desiredRoomIndex: Int) {}
    func updateHotelAndAvailability(toAvailable available: Bool) {}
    func updateViewModelTo(_ viewModel: HotelDetailsViewModel?) {}

    func startDisplayingLoadingElements() {}
    func stopDisplayingLoadingElements() {}
    func hotelUpdateFailed(with error: Error) {}

    func showDirectionsScreen(with viewModel: DirectionsViewModel) {}
    func openPrivacyPolicy() {}
    func callHotel(withPhoneNumber phoneNumber: String) {}
    func dismissCurrentOverlay() {}
    func emailCustomerService() {}

    func scrollTo(rateSection: RateSection) {}
    func scrollToTripAdvisorSection() {}

    var onPresent: (() -> Void)?
    private(set) var presentedViewController: UIViewController?
    func present(_ viewController: UIViewController) {
        presentedViewController = viewController
        onPresent?()
    }
    func push(_ viewController: UIViewController) {}
    
    func provideHapticFeedback() {}
    func showNotAllowedToBookPrompt() {}
    func showAlert(with title: String, message: String, confirmTitle: String, cancelTitle: String, confirmAction: @escaping (() -> Void)) {}
}

private class MockRouter: HotelDetailsRouterProtocol {

    var selectedRateDidCall = false

    func goBackToHomeScreen() {}
    
    func handleBARTDowntimeError(errorToCheck: Error) {}

    func goBack(withCriteriaToCheck criteriaToCheck: Criteria) {}

    func showCalendar(withArrivalDate arrivalDate: Date, andNumberOfNights numberOfNights: Int) {}

    func goBackToResultsScreenIfPossible() {}

    func showGuestsAndRooms() {}

    func selectedRate(withRooms rooms: [Room]?, accessibleRoomImages: [URL], twinRoomImages: [URL], withUpsells: Bool, andShouldShowRoomSelectionScreen shouldShowRoomSelectionScreen: Bool) {
        selectedRateDidCall = true
    }

    func showHoldRateError() {}

    func showDisabledAccess() {}

    func showBlockerAlert(withTitle title: String, body: String, andEmailSubject emailSubject: String?) {}

    func showOurRooms(with hotel: Hotel, lettingType: String?) {}
}

private protocol MockHotelDetailsInteractorProtocol: HotelDetailsInteractorProtocol {

    var hotel: Hotel? { get set }
}

private class MockInteractor: MockHotelDetailsInteractorProtocol {
    var discountCodeViewModel: HotelDetailsDiscountCodeViewModel?

    private var _hotel: Hotel?

    var holdBookingDidCall = false

    var viewModel: HotelDetailsViewModel? { return nil }
    var directionsViewModel: DirectionsViewModel? { return nil }

    // need to set the hotel from these tests
    var hotel: Hotel? {
        get {
            return _hotel
        }

        set {
            _hotel = newValue
        }
    }
    var phoneNumber: String? { return nil }
    var suggestion: Suggestion? { return nil }
    var carouselImageURLs: [URL] { return [] }
    var roundelDesigns: [RoundelDesign]? { return [] }
    var criteria: Criteria { return BookingDetails.sharedInstance.criteria }
    var arrivalDate: Date { return Date() }
    var numberOfNights: Int { return 0 }
    var bookingAllowed: Bool { return false }
    var hasUpsells: Bool { return false } // no upsells required to wait for hold booking to finish
    var userIsAllowedToMakeBookings: Bool { return true }
    var accessibleRoomImages: [URL]? { return nil }
    var twinRoomImages: [URL]? { return nil }

    var screenName: String { return "" }
    var screenType: String { return "" }
    var shouldTrackScreen: Bool { return false }
    var shouldShowYouNeedToObtainCVVInformation: Bool { return false }

    func updateBookingAllowed(to bookingAllowed: Bool) {}
    func updateSuggestion(to suggestion: Suggestion) {}
    func applyUserEnteredDiscountCodeIfNeeded() {}
    func loadAvailability(datesChanged: Bool) {}
    func datesChanged(withNewArrivalDate newArrivalDate: Date, andNumberOfNights numberOfNights: Int) {}
    func criteriaChanged(to newCriteria: Criteria) {}

    func setCriteria(withRateID rateID: UUID, and lettingType: String?) {}
    func roomsForRateID(_ rateID: UUID, and lettingType: String?) -> [Room] {
        return hotel?.rates.first?.rooms ?? [Room]()
    }
    func shouldShowEmployeeOfferInformation(for rateID: UUID) -> Bool {
        return false
    }
    func holdBooking(withRateID rateID: UUID, completion: @escaping (Bool) -> Void) {
        holdBookingDidCall = true
        completion(true)
    }
    func customerDismissedCoronavirusInformation() {}
    func validateUserCanBookRate(with id: UUID, completion: @escaping (Result<Bool>) -> Void) {
        completion(.success(result: true))
    }
    func userLoggedOut() {}
}

class HotelDetailsPresenterTests: XCTestCase {

    private var view: MockView!
    private var presenter: HotelDetailsPresenter!
    private var router: MockRouter!
    private var interactor: MockInteractor!

    override func setUp() {

        view = MockView()
        router = MockRouter()
        interactor = MockInteractor()

        guard let interactor = interactor else { return }

        presenter = HotelDetailsPresenter(with: interactor, andRouter: router, showCalendarFirst: false)
        presenter.interactor = interactor
        presenter.view = view
        presenter.router = router

        BookingDetails.sharedInstance.isBookingHold = true
    }

    override func tearDown() {

        presenter = nil
        view = nil
        router = nil
        interactor = nil

        super.tearDown()
    }
    
    // MARK: selectedRate tests - must always end up calling router.selectedRate unless errors occur - do not call holdBooking before (Bath)room selection

    // here we enter accessible rooms for half of the current scenarios and empty array for the rest where it doesn't matter
    private func hotelDictionary(with rooms: [PIDictionary]) -> PIDictionary {

        return [
            "hotelInfo": [
                "name": "Hotel Name",
                "code": "LONLEI",
                "prepaymentAllowed": true,
                "address": ["postcode": "a", "addressline1": "a", "addressline2": "a", "addressline3": "a", "country": "a"],
                "images": [["fileReference": "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"]],
                "contactDetails": ["hotelNationalPhone": "fakeNumber"]
                ],
            "brand": "PI",
            "ratePlans": [[
                "code": "FLEX",
                "name": "Flex",
                "classification": "FLEX",
                "rooms": rooms
            ]]
        ]
    }

    // don't wait for hold booking + no room selection
    func testSelectedRate_NoRoomSelection_DontWaitForHoldBooking() {

        do {
            interactor.hotel = try Hotel(dictionary: hotelDictionary(with: []))
        } catch {
            XCTFail("hotel initialisation failed")
        }

        guard let uuid = interactor?.hotel?.rates.first?.uniqueID else { return XCTFail("rate initialisation failed") }

        presenter.selectedRate(withRateID: uuid, lettingType: "lettingType")

        wait(for: .ocd, description: #function)

        XCTAssertTrue(interactor?.holdBookingDidCall == true)
        // to allow the completion handlers to run first before we check
        do {
            XCTAssertTrue(router.selectedRateDidCall)
        }
    }

    // don't wait for hold booking + room selection
    func testSelectedRate_GoToRoomSelection_DontWaitForHoldBooking() {

        var accessibleRoomsDictionary: [PIDictionary] {
            return [[
                "type": "DIS",
                "options": [[
                    "specialRequests": ["LOWB"]
                ]]
            ]]
        }

        // opera hotel required to wait for hold booking to finish
        do {
            interactor.hotel = try Hotel(dictionary: hotelDictionary(with: accessibleRoomsDictionary))
        } catch {
            XCTFail("hotel initialisation failed")
        }
        guard let uuid = interactor?.hotel?.rates.first?.uniqueID else { return XCTFail("rate initialisation failed") }

        presenter.selectedRate(withRateID: uuid, lettingType: "lettingType")

        wait(for: .ocd, description: #function)

        XCTAssertTrue(interactor?.holdBookingDidCall == false)
        // to allow the completion handlers to run first before we check
        do {
            XCTAssertTrue(router.selectedRateDidCall)
        }
    }

    // wait for hold booking + no room selection
    func testSelectedRate_NoRoomSelection_WaitForHoldBooking() {

        // opera hotel required to wait for hold booking to finish
        do {
            interactor.hotel = try Hotel(dictionary: hotelDictionary(with: []))
        } catch {
            XCTFail("hotel initialisation failed")
        }

        // user with company required to wait for hold booking to finish
        let user = try! User(title: "Mr", firstName: "Test", lastName: "User")
        UserSessionManager.sharedInstance.loggedIn(with: user)

        var testCompany: Company? {
            let emptyCompanyDict: PIDictionary = [:]
            let companyData = try! JSONSerialization.data(withJSONObject: emptyCompanyDict, options: .prettyPrinted)
            return try! JSONDecoder().decode(Company.self, from: companyData)
        }
        UserSessionManager.sharedInstance.currentUser?.company = testCompany

        guard let uuid = interactor?.hotel?.rates.first?.uniqueID else { return XCTFail("rate initialisation failed") }

        presenter.selectedRate(withRateID: uuid, lettingType: "lettingType")

        wait(for: .ocd, description: #function)

        XCTAssertTrue(interactor?.holdBookingDidCall == true)
        XCTAssertTrue(router.selectedRateDidCall)
    }

    // wait for hold booking + room selection
    func testSelectedRate_GoToRoomSelection_WaitForHoldBooking() {

        var accessibleRoomsDictionary: [PIDictionary] {
            return [[
                "type": "DIS",
                "options": [[
                    "specialRequests": ["LOWB"]
                ]]
            ]]
        }

        // opera hotel required to wait for hold booking to finish
        do {
            interactor.hotel = try Hotel(dictionary: hotelDictionary(with: accessibleRoomsDictionary))
        } catch {
            XCTFail("hotel initialisation failed")
        }

        // user with company required to wait for hold booking to finish
        let user = try! User(title: "Mr", firstName: "Test", lastName: "User")
        UserSessionManager.sharedInstance.loggedIn(with: user)

        var testCompany: Company? {
            let emptyCompanyDict: PIDictionary = [:]
            let companyData = try! JSONSerialization.data(withJSONObject: emptyCompanyDict, options: .prettyPrinted)
            return try! JSONDecoder().decode(Company.self, from: companyData)
        }
        UserSessionManager.sharedInstance.currentUser?.company = testCompany

        guard let uuid = interactor?.hotel?.rates.first?.uniqueID else { return XCTFail("rate initialisation failed") }

        presenter.selectedRate(withRateID: uuid, lettingType: "lettingType")

        wait(for: .ocd, description: #function)

        XCTAssertTrue(interactor?.holdBookingDidCall == false)
        XCTAssertTrue(router.selectedRateDidCall)
    }

    func testDiscountCodeButtonDidTap() {
        // GIVEN presenter with mocked dependencies. Wait for expectation
        let expectation = expectation(description: "Present called on main queue")

        view.onPresent = { expectation.fulfill() }

        // WHEN discountCodeButtonDidTap is called
        presenter.discountCodeButtonDidTap()

        // THEN
        wait(for: [expectation], timeout: 1.0)

        guard let presented = view.presentedViewController else {
            return XCTFail("Expected a view controller to be presented")
        }

        XCTAssertTrue(presented is UIHostingController<HotelDetailsDiscountCodeView>,
                      "Should be equal to `UIHostingController<HotelDetailsDiscountCodeView>`")
    }
}

