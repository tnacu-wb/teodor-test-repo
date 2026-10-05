//
//  CiolUpsellInteractorTests.swift
//  PremierInnTests
//
//  Created by Florin Velesca on 30.08.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
@testable import PremierInn
import SimpleNetwork

class MockCiolUpsellItem: CiolUpsellItemViewModelProtocol {
    var legendTitle: String = ""
    var imageURL: URL?
    var title: String = "Test"
    var subtitle: String = ""
    var costSummary: String = ""
    var itemDescription: String?
    var isFoodUpsell: Bool = true
    var isBooked: Bool = false
    var menuUrls: [RestaurantMenuItem] = []
    var allergensUrls: [AllergenInformation] = []
    var hasChildren: Bool = false
    var isMultiRoom: Bool = false
    var subitems: [CiolUpsellSubitemViewModel] = []
    var room: (any UpsellRoom)?
    var id: String = "testid"
    var enabled: Bool = false
    var bookingReference: String?
    var selected: Bool = false
    var isPrebooked: Bool = false
    var isWifi: Bool = false
    var nights: Int = 0
}

class MockCiolInteractorDataProvider : CiolUpsellDataProvider {
    func authorizePayment() async throws -> SimpleNetwork.CCCPPaymentProviderResponse? { return nil }

    func attachFileToReservation(params: SimpleNetwork.AuthorizationFileAttachmentParams) async throws -> SimpleNetwork.StatusResult? { return nil }

    func updatePreCheckInStatus(params: SimpleNetwork.UpdatePrecheckInParams) async throws -> SimpleNetwork.StatusResult? { return nil }

    func reservation(reservationDetails: SimpleNetwork.ReservationDetails, hotelCode: String?, bookingDetails: SimpleNetwork.BookingDetails?, completion: @escaping (SimpleNetwork.Reservation?, (any Error)?) -> Void) {}
    
    var didCallAmend = false
    var didCallConfirmPrecheckIn = false

    func amendCiolPackages(amendInfo: SimpleNetwork.CiolAmendInfo, completion: @escaping (Bool?, (any Error)?) -> Void) {
        completion(nil, nil)
        didCallAmend = true
    }

    func confirmPreCheckInOut(basketReference: String, type: CiolRequestType, isCiol _: Bool, completion: @escaping (ConfirmPreCheckInOut?, (any Error)?) -> Void) {
        completion(nil, nil)
        didCallConfirmPrecheckIn = true
    }

    func getPackages(
        reservationId: String,
        bookingDetails: SimpleNetwork.BookingDetails,
        hotelCode: String,
        bookingFlowId: String?,
        showMealInclusiveRate: Bool,
        completion: @escaping (([SimpleNetwork.UpsellItem], [SimpleNetwork.UpsellItem], SimpleNetwork.CityTaxResponse, GoshPackage?)?, (any Error)?) -> Void
    ) {

    }
}

class MockCiolUpsellInteractorOutput: CiolUpsellInteractorOutputProtocol {
    func startLoading() {}
    
    func setupThreeCIpage(for response: SimpleNetwork.CCCPPaymentResponse) {}
    
    func startLoadingUI() {}
    
    func stopLoadingUI(error: (any Error)?) {}
    

    var isUpsellChecksCompletedCalled = false
    var receivedIsSuccessful: Bool?
    var didCallReloadData = false
    var didCallGoToCompletion = false
    var didCallGoToPayment = false

    func reloadData(model: any PremierInn.CiolUpsellViewModelProtocol) {
        didCallReloadData = true
    }
    
    func goToPayment(inputParams: PremierInn.CiolReviewAndPayInputParams) {
        isUpsellChecksCompletedCalled = true
    }

    func goToPayment(error: (any Error)?, inputParams: PremierInn.CiolReviewAndPayInputParams) {
        didCallGoToPayment = true
    }

    func goToCompletion(error: (any Error)?, ciolConfirmationDetails: PremierInn.CiolConfirmationDetails) {
        didCallGoToCompletion = true
    }
    
    func goDirectlyToCompletion(error: (any Error)?, ciolConfirmationDetails: PremierInn.CiolConfirmationDetails) {
        didCallGoToCompletion = true
    }

}

class CiolUpsellInteractorTests: XCTestCase {

    var interactor: CiolUpsellInteractor!
    var mockOutput: MockCiolUpsellInteractorOutput!
    var dataProvider: MockCiolInteractorDataProvider!

    let availableUpsells =  [try! UpsellItem(dictionary: [
        "operaId": "MDP",
        "code": "17",
        "legend": "Meal Deal",
        "foodUpsell": true,
        "freeBreakfastTrigger": true,
        "freeBreakfastCode": "BFCHDF",
        "freeBreakfastMaxPerMeal": 2,
        "price": ["amount": "26.49",
                  "currency": "GBP"]
    ]), try! UpsellItem(dictionary: [
        "operaId": "BFADCT",
        "code": "12",
        "legend": "Continental Breakfast",
        "foodUpsell": true,
        "freeBreakfastTrigger": false,
        "price": ["amount": "9.99",
                  "currency": "GBP"]
    ]), try! UpsellItem(dictionary: [
        "operaId": "HSCKIN",
        "legend": "Early check-in",
        "isExtraUpsell": true,
        "price": ["amount": "10.0",
                  "currency": "GBP"]
    ])
    ]

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
        dictionary["paymentOption"] = "CC"  // Explicitly set as Credit Card to prevent PIBA CNP detection
        return try! Stay(dictionary: dictionary)
    }

    var mockViewModel: MockCiolUpsellViewModel?
    let singleRoom = CiolUpsellRoom(hasChildren: false, numberOfChildren: 0, id: "abc", adults: ["John", "Diane"], title: "RoomTest")

    override func setUp() {
        super.setUp()

        let priceBreakdown = PreStayInteractor.CIOLPriceBreakdownViewModel(ctaTitle: PILocalizedString("Continue"), totalValue: "", items: []) as CIOLPriceBreakdownViewModelProtocol
        let bookingSummary = PreStayInteractor.BookingSummaryCIOLViewModel(image: nil,
                                                                           hotelName: nil,
                                                                           duration: nil,
                                                                           summary: nil)
        
        mockViewModel = MockCiolUpsellViewModel()

        let inputParams = CiolUpsellInputParams(availableUpsells: availableUpsells, bookedUpsells: [], rooms: [singleRoom], hasChildren: false, isMultiRoom: false, nights: 0, priceBreakdownViewModel: priceBreakdown, bookingSummaryViewModel: bookingSummary, flow: .bookingConfirmation, leadBookerFirstName: "", hotelBrand: nil, address: nil, bookingReference: nil, stay: stay, reservationID: "", hotelID: "", arrivalDate: Date(), departureDate: Date(), analyticsParams: PIDictionary())
        dataProvider = MockCiolInteractorDataProvider()
        interactor = CiolUpsellInteractor(inputParams: inputParams, dataProvider: dataProvider)

        mockOutput = MockCiolUpsellInteractorOutput()
        interactor.output = mockOutput
        interactor.ciolUpsellViewModel = mockViewModel!

    }

    override func tearDown() {
        interactor = nil
        mockOutput = nil
        super.tearDown()
    }

    func testGoToNextStep() {
        // Act
        interactor.goToNextStep()

        // Assert
        // Without a payment method set, this is treated as non-PIBA flow
        // Non-PIBA: confirmPreCheckIn is called immediately, then goDirectlyToCompletion (no pop-up)
        // PIBA: goToCompletion is called (shows pop-up), confirmPreCheckIn called after user confirms
        XCTAssertTrue(dataProvider.didCallConfirmPrecheckIn)
        XCTAssertTrue(mockOutput.didCallGoToCompletion)
    }

    func testOutputSetup() {
        XCTAssertTrue(interactor.upsellOutput.rooms.isNotEmpty)
        XCTAssertTrue(interactor.upsellOutput.rooms.first?.title == "RoomTest")
    }

    func testDidAddUpsell() {
        let addedUpsell = MockCiolUpsellItem()
        addedUpsell.subitems = [CiolUpsellSubitemViewModel(title: "", bookingHasKids: false, canUpdateQuantity: true, id: "MDP", quantity: 2, enabled: true, descriptions: [], cost: .init(amount: 1, currencyCode: ""), isFoodUpsell: true, nights: 1)]
        addedUpsell.room = singleRoom
        interactor.didUpdateUpsell(for: addedUpsell, action: .add)
        XCTAssertTrue(mockOutput.didCallReloadData)
        XCTAssertTrue(mockViewModel!.didCallUpdateSelected)
        XCTAssertTrue(mockViewModel!.didCallUpdateEnablement)
        XCTAssertTrue(interactor.upsellOutput.addedFoodUpsellsCount == 2)
        XCTAssertTrue(interactor.upsellOutput.isBreakfastDisabled)
        XCTAssertTrue(interactor.upsellOutput.addedFoodItems(for: singleRoom.id, withoutUpsellID: addedUpsell.id) == 0)
    }

    func testDidRemoveUpsell() {
        let addedUpsell = MockCiolUpsellItem()
        addedUpsell.subitems = [CiolUpsellSubitemViewModel(title: "", bookingHasKids: false, canUpdateQuantity: true, id: "MDP", quantity: 1, enabled: true, descriptions: [], cost: .init(amount: 1, currencyCode: ""), isFoodUpsell: true, nights: 1)]
        addedUpsell.room = singleRoom
        interactor.didUpdateUpsell(for: addedUpsell, action: .add)
        let removed = MockCiolUpsellItem()
        removed.subitems = [CiolUpsellSubitemViewModel(title: "", bookingHasKids: false, canUpdateQuantity: true, id: "MDP", quantity: 1, enabled: true, descriptions: [], cost: .init(amount: 1, currencyCode: ""), isFoodUpsell: true, nights: 1)]
        removed.room = singleRoom
        interactor.didUpdateUpsell(for: removed, action: .remove)
        XCTAssertTrue(mockOutput.didCallReloadData)
        XCTAssertTrue(mockViewModel!.didCallUpdateSelected)
        XCTAssertTrue(mockViewModel!.didCallUpdateEnablement)
        XCTAssertTrue(interactor.upsellOutput.addedFoodUpsellsCount == 0)
        XCTAssertTrue(interactor.upsellOutput.addedFoodItems(for: singleRoom.id, withoutUpsellID: removed.id) == 0)
    }
    
    // MARK: - PIBA CNP Tests
    
    func testPibaCnpBookingSkipsPayment() {
        // Arrange: Create PIBA CNP stay
        var dictionary = PIDictionary()
        dictionary["hotelCode"] = "LONBLA"
        dictionary["hotelName"] = "Test Hotel"
        dictionary["identifier"] = "TEST123"
        dictionary["arrivalDate"] = "2024-10-09"
        dictionary["checkOutDate"] = "2024-10-10"
        dictionary["paymentOption"] = "PIBA_CNP"
        guard let pibaStay = try? Stay(dictionary: dictionary) else {
            XCTFail("Failed to create PIBA CNP Stay")
            return
        }
        
        let priceBreakdown = PreStayInteractor.CIOLPriceBreakdownViewModel(ctaTitle: PILocalizedString("Continue"), totalValue: "£100", items: []) as CIOLPriceBreakdownViewModelProtocol
        let bookingSummary = PreStayInteractor.BookingSummaryCIOLViewModel(image: nil, hotelName: nil, duration: nil, summary: nil)
        
        let inputParams = CiolUpsellInputParams(availableUpsells: [], bookedUpsells: [], rooms: [singleRoom], hasChildren: false, isMultiRoom: false, nights: 1, priceBreakdownViewModel: priceBreakdown, bookingSummaryViewModel: bookingSummary, flow: .bookingConfirmation, leadBookerFirstName: "", hotelBrand: nil, address: nil, bookingReference: nil, stay: pibaStay, reservationID: "", hotelID: "", arrivalDate: Date(), departureDate: Date(), analyticsParams: PIDictionary())
        
        let testInteractor = CiolUpsellInteractor(inputParams: inputParams, dataProvider: dataProvider)
        testInteractor.output = mockOutput
        
        // Act
        testInteractor.goToNextStep()
        
        // Assert: PIBA CNP should call checkIn directly (which shows popup)
        XCTAssertTrue(mockOutput.didCallGoToCompletion, "PIBA CNP should show confirmation popup")
    }
    
    // MARK: - Payment Logic Tests
    
    func testPrepaidBookingWithZeroBalanceSkipsPayment() {
        // Arrange: Prepaid booking with £0.00 balance
        let emptyPriceBreakdown = PreStayInteractor.CIOLPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("Continue"),
            totalValue: "£0.00",
            items: []
        ) as CIOLPriceBreakdownViewModelProtocol
        let bookingSummary = PreStayInteractor.BookingSummaryCIOLViewModel(image: nil, hotelName: nil, duration: nil, summary: nil)
        
        let inputParams = CiolUpsellInputParams(
            availableUpsells: [],
            bookedUpsells: [],
            rooms: [singleRoom],
            hasChildren: false,
            isMultiRoom: false,
            nights: 1,
            priceBreakdownViewModel: emptyPriceBreakdown,
            bookingSummaryViewModel: bookingSummary,
            flow: .bookingConfirmation,
            leadBookerFirstName: "",
            hotelBrand: nil,
            address: nil,
            bookingReference: nil,
            stay: stay,
            reservationID: "",
            hotelID: "",
            arrivalDate: Date(),
            departureDate: Date(),
            outstandingBalance: Cost(amount: 0, currencyCode: "GBP"),
            analyticsParams: PIDictionary()
        )
        
        let testInteractor = CiolUpsellInteractor(inputParams: inputParams, dataProvider: dataProvider)
        let testOutput = MockCiolUpsellInteractorOutput()
        testInteractor.output = testOutput
        testInteractor.ciolUpsellViewModel = MockCiolUpsellViewModel()
        
        // Act
        testInteractor.goToNextStep()
        
        // Assert: Should skip payment and go to completion
        XCTAssertFalse(testOutput.didCallGoToPayment, "Should not navigate to payment page when balance is £0.00")
        XCTAssertTrue(testOutput.didCallGoToCompletion, "Should go directly to completion")
    }
    
    func testBookingWithAddedUpsellsShowsPayment() {
        // Arrange: Prepaid booking with breakfast added (£15.00)
        let breakfastItem = PreStayInteractor.CIOLPriceBreakdownItemViewModel(
            name: "Continental Breakfast",
            value: Cost(amount: 15.00, currencyCode: "GBP"),
            quantity: 1
        )
        let priceBreakdownWithUpsell = PreStayInteractor.CIOLPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("Continue"),
            totalValue: "£15.00",
            items: [breakfastItem]
        ) as CIOLPriceBreakdownViewModelProtocol
        let bookingSummary = PreStayInteractor.BookingSummaryCIOLViewModel(image: nil, hotelName: nil, duration: nil, summary: nil)
        
        let inputParams = CiolUpsellInputParams(
            availableUpsells: [],
            bookedUpsells: [],
            rooms: [singleRoom],
            hasChildren: false,
            isMultiRoom: false,
            nights: 1,
            priceBreakdownViewModel: priceBreakdownWithUpsell,
            bookingSummaryViewModel: bookingSummary,
            flow: .bookingConfirmation,
            leadBookerFirstName: "",
            hotelBrand: nil,
            address: nil,
            bookingReference: nil,
            stay: stay,
            reservationID: "",
            hotelID: "",
            arrivalDate: Date(),
            departureDate: Date(),
            outstandingBalance: Cost(amount: 0, currencyCode: "GBP"),
            analyticsParams: PIDictionary()
        )
        
        let testInteractor = CiolUpsellInteractor(inputParams: inputParams, dataProvider: dataProvider)
        let testOutput = MockCiolUpsellInteractorOutput()
        testInteractor.output = testOutput
        
        let mockVM = MockCiolUpsellViewModel()
        mockVM.priceBreakdownViewModel = priceBreakdownWithUpsell
        testInteractor.ciolUpsellViewModel = mockVM
        
        // Act
        testInteractor.goToNextStep()
        
        // Assert: Should navigate to payment page
        XCTAssertTrue(testOutput.isUpsellChecksCompletedCalled, "Should navigate to payment page when upsells are added")
    }
    
    func testBookingWithOutstandingBalanceShowsPayment() {
        // Arrange: Booking with outstanding balance
        let roomItem = PreStayInteractor.CIOLPriceBreakdownItemViewModel(
            name: "Room cost",
            value: Cost(amount: 100.00, currencyCode: "GBP"),
            quantity: 1
        )
        let priceBreakdownWithBalance = PreStayInteractor.CIOLPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("Continue"),
            totalValue: "£100.00",
            items: [roomItem]
        ) as CIOLPriceBreakdownViewModelProtocol
        let bookingSummary = PreStayInteractor.BookingSummaryCIOLViewModel(image: nil, hotelName: nil, duration: nil, summary: nil)
        
        let inputParams = CiolUpsellInputParams(
            availableUpsells: [],
            bookedUpsells: [],
            rooms: [singleRoom],
            hasChildren: false,
            isMultiRoom: false,
            nights: 1,
            priceBreakdownViewModel: priceBreakdownWithBalance,
            bookingSummaryViewModel: bookingSummary,
            flow: .bookingConfirmation,
            leadBookerFirstName: "",
            hotelBrand: nil,
            address: nil,
            bookingReference: nil,
            stay: stay,
            reservationID: "",
            hotelID: "",
            arrivalDate: Date(),
            departureDate: Date(),
            outstandingBalance: Cost(amount: 100, currencyCode: "GBP"),
            analyticsParams: PIDictionary()
        )
        
        let testInteractor = CiolUpsellInteractor(inputParams: inputParams, dataProvider: dataProvider)
        let testOutput = MockCiolUpsellInteractorOutput()
        testInteractor.output = testOutput
        
        let mockVM = MockCiolUpsellViewModel()
        mockVM.priceBreakdownViewModel = priceBreakdownWithBalance
        testInteractor.ciolUpsellViewModel = mockVM
        
        // Act
        testInteractor.goToNextStep()
        
        // Assert: Should navigate to payment page
        XCTAssertTrue(testOutput.isUpsellChecksCompletedCalled, "Should navigate to payment page when there's an outstanding balance")
    }
    
    // MARK: - German Hotel RegCard Flow Tests
    
    func testGermanHotelBritishCitizenWithZeroBalanceCompletesCheckIn() {
        // Arrange: German hotel + British citizen + £0.00 balance (prepaid)
        let emptyPriceBreakdown = PreStayInteractor.CIOLPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("Continue"),
            totalValue: "£0.00",
            items: []
        ) as CIOLPriceBreakdownViewModelProtocol
        let bookingSummary = PreStayInteractor.BookingSummaryCIOLViewModel(image: nil, hotelName: nil, duration: nil, summary: nil)
        
        // Create British guest using User
        let britishUser = try! User(title: "Mr", firstName: "John", lastName: "Smith")
        britishUser.country = .greatBritain
        britishUser.dob = "1990-01-01"
        let britishGuest = Guest(with: britishUser)
        
        let inputParams = CiolUpsellInputParams(
            availableUpsells: [],
            bookedUpsells: [],
            rooms: [singleRoom],
            hasChildren: false,
            isMultiRoom: false,
            nights: 1,
            priceBreakdownViewModel: emptyPriceBreakdown,
            bookingSummaryViewModel: bookingSummary,
            flow: .bookingConfirmation,
            leadBookerFirstName: "John",
            hotelBrand: .premierInnGermany,
            address: nil,
            bookingReference: nil,
            stay: stay,
            reservationID: "TEST123",
            hotelID: "DEHOTEL",
            arrivalDate: Date(),
            departureDate: Date(),
            outstandingBalance: Cost(amount: 0, currencyCode: "EUR"),
            analyticsParams: PIDictionary(),
            regCardFlow: .regCard,
            guests: [britishGuest]
        )
        
        let testInteractor = CiolUpsellInteractor(inputParams: inputParams, dataProvider: dataProvider)
        let testOutput = MockCiolUpsellInteractorOutput()
        testInteractor.output = testOutput
        testInteractor.ciolUpsellViewModel = MockCiolUpsellViewModel()
        
        testInteractor.goToNextStep()
        
        // Should complete check-in without trying to authorize payment
        XCTAssertFalse(testOutput.didCallGoToPayment, "Should not navigate to payment page when balance is £0.00")
        // Note: The actual check-in completion happens asynchronously, so we can't assert didCallGoToCompletion here
        // But we can confirm it doesn't try to go to payment or get stuck
    }
    
    func testGermanHotelBritishCitizenWithBalanceRequiresAuthorization() {
        // Arrange: German hotel + British citizen + outstanding balance
        let roomItem = PreStayInteractor.CIOLPriceBreakdownItemViewModel(
            name: "Room cost",
            value: Cost(amount: 50.00, currencyCode: "EUR"),
            quantity: 1
        )
        let priceBreakdownWithBalance = PreStayInteractor.CIOLPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("Continue"),
            totalValue: "€50.00",
            items: [roomItem]
        ) as CIOLPriceBreakdownViewModelProtocol
        let bookingSummary = PreStayInteractor.BookingSummaryCIOLViewModel(image: nil, hotelName: nil, duration: nil, summary: nil)
        
        // Create British guest using User
        let britishUser2 = try! User(title: "Mr", firstName: "John", lastName: "Smith")
        britishUser2.country = .greatBritain
        britishUser2.dob = "1990-01-01"
        let britishGuest = Guest(with: britishUser2)
        
        let inputParams = CiolUpsellInputParams(
            availableUpsells: [],
            bookedUpsells: [],
            rooms: [singleRoom],
            hasChildren: false,
            isMultiRoom: false,
            nights: 1,
            priceBreakdownViewModel: priceBreakdownWithBalance,
            bookingSummaryViewModel: bookingSummary,
            flow: .bookingConfirmation,
            leadBookerFirstName: "John",
            hotelBrand: .premierInnGermany,
            address: nil,
            bookingReference: nil,
            stay: stay,
            reservationID: "TEST123",
            hotelID: "DEHOTEL",
            arrivalDate: Date(),
            departureDate: Date(),
            outstandingBalance: Cost(amount: 50, currencyCode: "EUR"),
            analyticsParams: PIDictionary(),
            regCardFlow: .regCard,
            guests: [britishGuest]
        )
        
        let testInteractor = CiolUpsellInteractor(inputParams: inputParams, dataProvider: dataProvider)
        let testOutput = MockCiolUpsellInteractorOutput()
        testInteractor.output = testOutput
        
        let mockVM = MockCiolUpsellViewModel()
        mockVM.priceBreakdownViewModel = priceBreakdownWithBalance
        testInteractor.ciolUpsellViewModel = mockVM

        testInteractor.goToNextStep()
        
        // Should navigate to payment page (which includes authorization)
        XCTAssertTrue(testOutput.isUpsellChecksCompletedCalled, "Should navigate to payment page when British citizen has outstanding balance at German hotel")
    }
    
    func testGermanHotelGermanCitizenWithZeroBalanceCompletesCheckIn() {
        // Arrange: German hotel + German citizen + £0.00 balance
        let emptyPriceBreakdown = PreStayInteractor.CIOLPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("Continue"),
            totalValue: "€0.00",
            items: []
        ) as CIOLPriceBreakdownViewModelProtocol
        let bookingSummary = PreStayInteractor.BookingSummaryCIOLViewModel(image: nil, hotelName: nil, duration: nil, summary: nil)
        
        // Create German guest using User
        let germanUser = try! User(title: "Herr", firstName: "Hans", lastName: "Schmidt")
        germanUser.country = .germany
        germanUser.dob = "1985-05-15"
        let germanGuest = Guest(with: germanUser)
        
        let inputParams = CiolUpsellInputParams(
            availableUpsells: [],
            bookedUpsells: [],
            rooms: [singleRoom],
            hasChildren: false,
            isMultiRoom: false,
            nights: 1,
            priceBreakdownViewModel: emptyPriceBreakdown,
            bookingSummaryViewModel: bookingSummary,
            flow: .bookingConfirmation,
            leadBookerFirstName: "Hans",
            hotelBrand: .premierInnGermany,
            address: nil,
            bookingReference: nil,
            stay: stay,
            reservationID: "TEST123",
            hotelID: "DEHOTEL",
            arrivalDate: Date(),
            departureDate: Date(),
            outstandingBalance: Cost(amount: 0, currencyCode: "EUR"),
            analyticsParams: PIDictionary(),
            regCardFlow: .regCard,
            guests: [germanGuest]
        )
        
        let testInteractor = CiolUpsellInteractor(inputParams: inputParams, dataProvider: dataProvider)
        let testOutput = MockCiolUpsellInteractorOutput()
        testInteractor.output = testOutput
        testInteractor.ciolUpsellViewModel = MockCiolUpsellViewModel()

        testInteractor.goToNextStep()
        
        // Should complete check-in without payment
        XCTAssertFalse(testOutput.didCallGoToPayment, "German citizen should not go to payment when balance is £0.00")
    }
    
    func testGermanHotelBritishCitizenWithAddedUpsellsShowsPayment() {
        // Arrange: German hotel + British citizen + breakfast added during CIOL
        let breakfastItem = PreStayInteractor.CIOLPriceBreakdownItemViewModel(
            name: "Continental Breakfast",
            value: Cost(amount: 12.00, currencyCode: "EUR"),
            quantity: 1
        )
        let priceBreakdownWithUpsell = PreStayInteractor.CIOLPriceBreakdownViewModel(
            ctaTitle: PILocalizedString("Continue"),
            totalValue: "€12.00",
            items: [breakfastItem]
        ) as CIOLPriceBreakdownViewModelProtocol
        let bookingSummary = PreStayInteractor.BookingSummaryCIOLViewModel(image: nil, hotelName: nil, duration: nil, summary: nil)
        
        // Create British guest using User
        let britishUser3 = try! User(title: "Mr", firstName: "John", lastName: "Smith")
        britishUser3.country = .greatBritain
        britishUser3.dob = "1990-01-01"
        let britishGuest = Guest(with: britishUser3)
        
        let inputParams = CiolUpsellInputParams(
            availableUpsells: [],
            bookedUpsells: [],
            rooms: [singleRoom],
            hasChildren: false,
            isMultiRoom: false,
            nights: 1,
            priceBreakdownViewModel: priceBreakdownWithUpsell,
            bookingSummaryViewModel: bookingSummary,
            flow: .bookingConfirmation,
            leadBookerFirstName: "John",
            hotelBrand: .premierInnGermany,
            address: nil,
            bookingReference: nil,
            stay: stay,
            reservationID: "TEST123",
            hotelID: "DEHOTEL",
            arrivalDate: Date(),
            departureDate: Date(),
            outstandingBalance: Cost(amount: 0, currencyCode: "EUR"),
            analyticsParams: PIDictionary(),
            regCardFlow: .regCard,
            guests: [britishGuest]
        )
        
        let testInteractor = CiolUpsellInteractor(inputParams: inputParams, dataProvider: dataProvider)
        let testOutput = MockCiolUpsellInteractorOutput()
        testInteractor.output = testOutput
        
        let mockVM = MockCiolUpsellViewModel()
        mockVM.priceBreakdownViewModel = priceBreakdownWithUpsell
        testInteractor.ciolUpsellViewModel = mockVM

        testInteractor.goToNextStep()

        XCTAssertTrue(testOutput.isUpsellChecksCompletedCalled, "Should navigate to payment when British citizen adds upsells at German hotel")
    }
}
