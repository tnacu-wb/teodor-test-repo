//
//  HotelDetailsInteractorTests.swift
//  PremierInn
//
//  Automatically Created by Ophion
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import CoreLocation
import SimpleNetwork
@testable import PremierInn

private final class MockHotelAvailabilityDataProvider: HotelAvailabilityDataProvider {
    var stubbedResult: Result<Hotel>?

    private(set) var capturedHotelCode: String?
    private(set) var capturedShouldCheckAvailability: Bool?
    private(set) var capturedDatesChanged: Bool?

    func loadInfoForHotel(
        withCode hotelCode: String?,
        hotelBrand: HotelBrand?,
        existingAvailability: HotelAvailabilityResponse?,
        andShouldCheckAvailability shouldCheckAvailability: Bool,
        datesChangedByUserInHdp: Bool,
        discountCodeViewModel: HotelDetailsDiscountCodeViewModel?,
        completion: @escaping (Result<Hotel>) -> Void
    ) {
        capturedHotelCode = hotelCode
        capturedShouldCheckAvailability = shouldCheckAvailability
        capturedDatesChanged = datesChangedByUserInHdp
        if let result = stubbedResult { completion(result) }
    }
}

private final class MockHotelDetailsInteractorPresenter: HotelDetailsPresenterProtocol {
    var screenName: String { "" }
    var screenType: String { "" }
    var shouldTrackScreen: Bool { false }
    var hotelDetailsViewModel: HotelDetailsViewModel?
    var continueViewCanShowAtBottom: Bool { false }

    var hotelUpdatedCallCount = 0
    var hotelUpdateFailedCallCount = 0

    func hotelUpdated() {
        hotelUpdatedCallCount += 1
    }

    func hotelUpdateFailed(with error: Error) {
        hotelUpdateFailedCallCount += 1
    }

    func datesChanged(withNewArrivalDate newArrivalDate: Date, andNumberOfNights numberOfNights: Int) {}
    func criteriaWasUpdated(to newCriteria: Criteria) {}
    func closeCurrentOverlay() {}
    func showHoldBookingError() {}
    func tripAdvisorRatingTapped() {}
    func showLoadingIndicator() {}
    func hideLoadingIndicator() {}
}

class HotelDetailsInteractorTests: XCTestCase {

    var interactor: HotelDetailsInteractor?

    let suggestion = PISuggestion(coordinate: CLLocationCoordinate2D(latitude: 1, longitude: 1))

    func createHotel() -> Hotel {
        return try! Hotel(dictionary: [
            "name": "Hotel Name",
            "code": "LONLEI",
            "prepaymentAllowed": true,
            "address": ["postcode": "a", "addressline1": "a", "addressline2": "a", "addressline3": "a", "country": "a"],
            "images": [["fileReference": "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"]],
            "contactDetails": ["hotelNationalPhone": "fakeNumber"]
            ])
    }

    var leisureUser: User {
        let user = try! User(title: "Mr", firstName: "Nichola", lastName: "Twisp", email: "nicepins@me.com")
        user.isBusiness = false
        return user
    }

    var businessUser: User {
        let user = try! User(title: "Mr", firstName: "Nichola", lastName: "Twisp", email: "nicepins@me.com")
        user.isBusiness = true
        return user
    }

    override func setUp() {
      super.setUp()

      //interactor = HotelDetailsInteractor()
    }

    override func tearDown() {
      super.tearDown()
    }

    func createHotelDetailsViewModel(shouldShowCheckAvailability: Bool) -> HotelDetailsViewModel{
        return HotelDetailsViewModel.createFrom(
            hotelDetailsCreateParams: HotelDetailsCreateParams(
                createHotel(),
                criteria: BookingDetails.sharedInstance.criteria,
                suggestion: suggestion,
                discountCodeViewModel: nil,
                shouldShowCheckAvailability: shouldShowCheckAvailability,
                bookingAllowed: true,
                dismissedCoronavirusMessaging: false
            )
        )!
    }

    func testShowBBAlertForOperaHotelAndBBUser() {
        // Scenario - User comes from SRP or Single search

        let remoteConfig = MockRemoteConfig(shouldOperaShowFallBackForBB: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        UserSessionManager.sharedInstance.piLoggedIn(with: businessUser)
        let hotelDetailsViewModel = createHotelDetailsViewModel(shouldShowCheckAvailability: false)

        XCTAssert(hotelDetailsViewModel.shouldShowBBNotAvailableSection == true)
        XCTAssert(hotelDetailsViewModel.shouldShowErrorSection == false)
        XCTAssert(hotelDetailsViewModel.titleAndSummaryViewModel.shouldShowCheckAvailabilityRow == false)


    }

    func testShowBBAlertNonOperaHotelAndBBUser() {
        // Scenario - User comes from SRP or Single search - no availability so show error
        let remoteConfig = MockRemoteConfig(shouldOperaShowFallBackForBB: false)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        UserSessionManager.sharedInstance.piLoggedIn(with: businessUser)

        let hotelDetailsViewModel = createHotelDetailsViewModel(shouldShowCheckAvailability: false)

        XCTAssert(hotelDetailsViewModel.shouldShowBBNotAvailableSection == false)
        // No availability error == true
        XCTAssert(hotelDetailsViewModel.shouldShowErrorSection == true)
        XCTAssert(hotelDetailsViewModel.titleAndSummaryViewModel.shouldShowCheckAvailabilityRow == false)

    }

    func testShowBBAlertOperaHotelAndLeisureUser() {
        // Scenario - User comes from SRP or Single search - no availability so show error
        let remoteConfig = MockRemoteConfig(shouldOperaShowFallBackForBB: false)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        UserSessionManager.sharedInstance.piLoggedIn(with: leisureUser)

        let hotelDetailsViewModel = createHotelDetailsViewModel(shouldShowCheckAvailability: false)

        XCTAssert(hotelDetailsViewModel.shouldShowBBNotAvailableSection == false)
        // No availability error == true
        XCTAssert(hotelDetailsViewModel.shouldShowErrorSection == true)
        XCTAssert(hotelDetailsViewModel.titleAndSummaryViewModel.shouldShowCheckAvailabilityRow == false)

    }

    func testShowBBAlertNonOperaHotelAndLeisureUser() {
        // Scenario - User comes from SRP or Single search - no availability so show error
        let remoteConfig = MockRemoteConfig(shouldOperaShowFallBackForBB: false)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        UserSessionManager.sharedInstance.piLoggedIn(with: leisureUser)

        let hotelDetailsViewModel = createHotelDetailsViewModel(shouldShowCheckAvailability: false)

        XCTAssert(hotelDetailsViewModel.shouldShowBBNotAvailableSection == false)
        // No availability error == true
        XCTAssert(hotelDetailsViewModel.shouldShowErrorSection == true)
        XCTAssert(hotelDetailsViewModel.titleAndSummaryViewModel.shouldShowCheckAvailabilityRow == false)

    }

    func testShowBBAlertOperaHotelAndBusinessUserAndNoDatesSelected() {
        // Scenario - Opera and BB - when going from booking confirmation to HDP and no dates selected we still show the BB alert and not the error section
        let remoteConfig = MockRemoteConfig(shouldOperaShowFallBackForBB: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        UserSessionManager.sharedInstance.piLoggedIn(with: businessUser)

        let hotelDetailsViewModel = createHotelDetailsViewModel(shouldShowCheckAvailability: true)

        XCTAssert(hotelDetailsViewModel.shouldShowBBNotAvailableSection == true)
        XCTAssert(hotelDetailsViewModel.shouldShowErrorSection == false)
        XCTAssert(hotelDetailsViewModel.titleAndSummaryViewModel.shouldShowCheckAvailabilityRow == false)
    }

    func testShowBBAlertNonOperaHotelAndBusinessUserAndNoDatesSelected() {
        // Scenario - BART and BB - when going from booking confirmation to HDP and no dates selected do not show the BB alert and show the check availability error section
        let remoteConfig = MockRemoteConfig(shouldOperaShowFallBackForBB: false)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        
        UserSessionManager.sharedInstance.piLoggedIn(with: businessUser)

        let hotelDetailsViewModel = createHotelDetailsViewModel(shouldShowCheckAvailability: true)

        XCTAssert(hotelDetailsViewModel.shouldShowBBNotAvailableSection == false)
        XCTAssert(hotelDetailsViewModel.shouldShowErrorSection == false)
        XCTAssert(hotelDetailsViewModel.titleAndSummaryViewModel.shouldShowCheckAvailabilityRow == true)
    }


    func testLoadAvailabilityWhenLoaderReturnsHotelTracksAnalyticsProdViewKey() {
        let expectedHotel = createHotel()
        let mockDataProvider = MockHotelAvailabilityDataProvider()
        mockDataProvider.stubbedResult = .success(result: expectedHotel)
        let mockedAnalyticsManager = MockAnalyticsManager()

        let interactor = HotelDetailsInteractor(
            withCode: expectedHotel.code,
            bookingAllowed: true,
            existingAvailability: nil,
            suggestion: nil,
            availabilityProvider: mockDataProvider,
            analytics: mockedAnalyticsManager
        )

        interactor.loadAvailability(datesChanged: false)

        let trackedProdView = mockedAnalyticsManager.dictionaries.first?[PIAnalytics.Keys.prodView]
        XCTAssertEqual(trackedProdView, "1")
    }
    
    func testLoadAvailabilityWhenLoaderReturnsHotelUpdatesHotelAndNotifiesPresenter() {
        let expectedHotel = createHotel()
        let presenter = MockHotelDetailsInteractorPresenter()
        let mockDataProvider = MockHotelAvailabilityDataProvider()
        mockDataProvider.stubbedResult = .success(result: expectedHotel)

        let interactor = HotelDetailsInteractor(
            withCode: expectedHotel.code,
            bookingAllowed: true,
            existingAvailability: nil,
            suggestion: nil,
            availabilityProvider: mockDataProvider,
            analytics: MockAnalyticsManager()
        )
        interactor.presenter = presenter

        interactor.loadAvailability(datesChanged: false)

        XCTAssertEqual(mockDataProvider.capturedHotelCode, expectedHotel.code)
        XCTAssertTrue(mockDataProvider.capturedShouldCheckAvailability == true)
        XCTAssertFalse(mockDataProvider.capturedDatesChanged == true)
        XCTAssertEqual(interactor.hotel?.code, expectedHotel.code)
        XCTAssertGreaterThanOrEqual(presenter.hotelUpdatedCallCount, 1)
        XCTAssertEqual(presenter.hotelUpdateFailedCallCount, 0)
    }
    
    func testTrackAvailabilityIncludesPromoBoxVisibleWhenDiscountCodeSectionIsVisible() {
        let remoteConfig = MockRemoteConfig(featureHDPDiscountCode: true)
        remoteConfig.hdpDiscountCodeAllowedBrands = [HotelBrand.premierInn.rawValue]
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        let mockedAnalyticsManager = MockAnalyticsManager()
        let mockDataProvider = MockHotelAvailabilityDataProvider()
        let expectedHotel = createHotel()
        mockDataProvider.stubbedResult = .success(result: expectedHotel)
        let presenter = MockHotelDetailsInteractorPresenter()

        let interactor = HotelDetailsInteractor(
            withCode: "LONLEI",
            bookingAllowed: false,
            existingAvailability: nil,
            suggestion: nil,
            availabilityProvider: mockDataProvider,
            analytics: mockedAnalyticsManager
        )

        let hotelDetailsViewModel = createHotelDetailsViewModel(shouldShowCheckAvailability: false)
        XCTAssertTrue(hotelDetailsViewModel.shouldShowAddADiscountCodeSection)

        interactor.presenter = presenter
        interactor.loadAvailability(datesChanged: false)
        
        let trackedPromoBoxVisible = mockedAnalyticsManager.dictionaries.first?[PIAnalytics.Keys.promoBoxVisible]
        XCTAssertEqual(trackedPromoBoxVisible, "true")
    }
    
    func testTrackAvailabilityIncludesPromoBoxHiddenWhenDiscountCodeSectionIsHidden() {
        let remoteConfig = MockRemoteConfig(featureHDPDiscountCode: true)
        remoteConfig.hdpDiscountCodeAllowedBrands = ["random hotel string"]
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        let mockedAnalyticsManager = MockAnalyticsManager()
        let mockDataProvider = MockHotelAvailabilityDataProvider()
        let expectedHotel = createHotel()
        mockDataProvider.stubbedResult = .success(result: expectedHotel)
        let presenter = MockHotelDetailsInteractorPresenter()
        BookingDetails.sharedInstance.employeeRatesEnabled = false

        let interactor = HotelDetailsInteractor(
            withCode: "LONLEI",
            bookingAllowed: false,
            existingAvailability: nil,
            suggestion: nil,
            availabilityProvider: mockDataProvider,
            analytics: mockedAnalyticsManager
        )

        let hotelDetailsViewModel = createHotelDetailsViewModel(shouldShowCheckAvailability: false)
        XCTAssertFalse(hotelDetailsViewModel.shouldShowAddADiscountCodeSection)

        interactor.presenter = presenter
        interactor.loadAvailability(datesChanged: false)
        
        let trackedPromoBoxVisible = mockedAnalyticsManager.dictionaries.first?[PIAnalytics.Keys.promoBoxVisible]
        XCTAssertEqual(trackedPromoBoxVisible, "false")
    }

    func testDiscountCodeSectionIsHiddenWhenEmployeeRatesAreEnabled() {
        let remoteConfig = MockRemoteConfig(featureHDPDiscountCode: true)
        remoteConfig.hdpDiscountCodeAllowedBrands = [HotelBrand.premierInn.rawValue]
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        let mockedAnalyticsManager = MockAnalyticsManager()
        let mockDataProvider = MockHotelAvailabilityDataProvider()
        let expectedHotel = createHotel()
        mockDataProvider.stubbedResult = .success(result: expectedHotel)
        let presenter = MockHotelDetailsInteractorPresenter()

        BookingDetails.sharedInstance.employeeRatesEnabled = true

        defer {
            BookingDetails.sharedInstance.employeeRatesEnabled = false
        }

        let interactor = HotelDetailsInteractor(
            withCode: "LONLEI",
            bookingAllowed: false,
            existingAvailability: nil,
            suggestion: nil,
            availabilityProvider: mockDataProvider,
            analytics: mockedAnalyticsManager
        )

        let hotelDetailsViewModel = createHotelDetailsViewModel(shouldShowCheckAvailability: false)

        interactor.presenter = presenter
        interactor.loadAvailability(datesChanged: false)

        let trackedPromoBoxVisible = mockedAnalyticsManager.dictionaries.first?[PIAnalytics.Keys.promoBoxVisible]
        XCTAssertEqual(trackedPromoBoxVisible, "false")
        XCTAssertFalse(hotelDetailsViewModel.shouldShowAddADiscountCodeSection)
    }
    
    func testDiscountCodeSectionIsShowingWhenEmployeeRatesIsDisabled() {
        let remoteConfig = MockRemoteConfig(featureHDPDiscountCode: true)
        remoteConfig.hdpDiscountCodeAllowedBrands = [HotelBrand.premierInn.rawValue]
        remoteConfig.allowEmployeeOfferFeature = false
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        let mockedAnalyticsManager = MockAnalyticsManager()
        let mockDataProvider = MockHotelAvailabilityDataProvider()
        let expectedHotel = createHotel()
        mockDataProvider.stubbedResult = .success(result: expectedHotel)
        let presenter = MockHotelDetailsInteractorPresenter()

        BookingDetails.sharedInstance.employeeRatesEnabled = false

        let interactor = HotelDetailsInteractor(
            withCode: "LONLEI",
            bookingAllowed: false,
            existingAvailability: nil,
            suggestion: nil,
            availabilityProvider: mockDataProvider,
            analytics: mockedAnalyticsManager
        )

        let hotelDetailsViewModel = createHotelDetailsViewModel(shouldShowCheckAvailability: false)

        interactor.presenter = presenter
        interactor.loadAvailability(datesChanged: false)

        XCTAssertTrue(hotelDetailsViewModel.shouldShowAddADiscountCodeSection)
    }

    func testDiscountCodeSectionIsShowingWhenEmployeeRatesEnabledAndEmployeeOfferFeatureIsDisabled() {
        let remoteConfig = MockRemoteConfig(featureHDPDiscountCode: true)
        remoteConfig.hdpDiscountCodeAllowedBrands = [HotelBrand.premierInn.rawValue]
        remoteConfig.allowEmployeeOfferFeature = false
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig

        let mockedAnalyticsManager = MockAnalyticsManager()
        let mockDataProvider = MockHotelAvailabilityDataProvider()
        let expectedHotel = createHotel()
        mockDataProvider.stubbedResult = .success(result: expectedHotel)
        let presenter = MockHotelDetailsInteractorPresenter()

        BookingDetails.sharedInstance.employeeRatesEnabled = true

        defer {
            BookingDetails.sharedInstance.employeeRatesEnabled = false
        }

        let interactor = HotelDetailsInteractor(
            withCode: "LONLEI",
            bookingAllowed: false,
            existingAvailability: nil,
            suggestion: nil,
            availabilityProvider: mockDataProvider,
            analytics: mockedAnalyticsManager
        )

        let hotelDetailsViewModel = createHotelDetailsViewModel(shouldShowCheckAvailability: false)

        interactor.presenter = presenter
        interactor.loadAvailability(datesChanged: false)

        XCTAssertTrue(hotelDetailsViewModel.shouldShowAddADiscountCodeSection)
    }
    
    func testShouldShowCotWarningSection_WhenCotRequestedAndAvailable_FlagIsFalse() throws {
        // Arrange
        let hotel = try makeHotelWithCot(cotRequired: true, cotAvailable: true)
        let hotelParams = HotelDetailsCreateParams(hotel: hotel,
                                                   criteria: BookingDetails.sharedInstance.criteria,
                                                   suggestion: suggestion,
                                                   discountCodeViewModel: nil,
                                                   shouldShowCheckAvailability: false,
                                                   bookingAllowed: true,
                                                   dismissedCoronavirusMessaging: false)

        // Act
        let hotelDetailsViewModel = HotelDetailsViewModel.createFrom(hotelDetailsCreateParams: hotelParams)
        let viewModel = try XCTUnwrap(hotelDetailsViewModel)

        // Assert
        XCTAssertFalse(viewModel.shouldShowCotNotAvailableMessageSection)
    }
    
    func testShouldShowCotWarningSection_WhenCotRequestedAndNotAvailable_FlagIsTrue() throws {
        // Arrange
        let hotel = try makeHotelWithCot(cotRequired: true, cotAvailable: false)
        let hotelParams = HotelDetailsCreateParams(hotel: hotel,
                                                   criteria: BookingDetails.sharedInstance.criteria,
                                                   suggestion: suggestion,
                                                   discountCodeViewModel: nil,
                                                   shouldShowCheckAvailability: false,
                                                   bookingAllowed: true,
                                                   dismissedCoronavirusMessaging: false)

        // Act
        let hotelDetailsViewModel = HotelDetailsViewModel.createFrom(hotelDetailsCreateParams: hotelParams)
        let viewModel = try XCTUnwrap(hotelDetailsViewModel)

        // Assert
        XCTAssertTrue(viewModel.shouldShowCotNotAvailableMessageSection)
    }
}

// MARK: - Helpers

private extension HotelDetailsInteractorTests {
    func makeHotelWithCot(cotRequired: Bool, cotAvailable: Bool) throws -> Hotel {
        let room: PIDictionary = [
            "type": "DBL",
            "cotRequired": cotRequired,
            "options": [[
                "lettingType": "DBL",
                "cotAvailable": cotAvailable,
                "totalCost": [
                    "amount": 100.0,
                    "currency": "GBP"
                ]
            ]]
        ]

        let hotelDictionary: PIDictionary = [
            "hotelInfo": [
                "name": "Hotel Name",
                "code": "LONLEI",
                "prepaymentAllowed": true,
                "address": [
                    "postcode": "a",
                    "addressline1": "a",
                    "addressline2": "a",
                    "addressline3": "a",
                    "country": "a"
                ]
            ],
            "brand": "PI",
            "ratePlans": [[
                "code": "FLEX",
                "name": "Flex",
                "classification": "FLEX",
                "rooms": [room]
            ]]
        ]
        
        return try Hotel(dictionary: hotelDictionary)
    }
}
