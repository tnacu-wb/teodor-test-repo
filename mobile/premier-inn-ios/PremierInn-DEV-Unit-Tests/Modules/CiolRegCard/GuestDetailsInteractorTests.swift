//
//  GuestDetailsInteractorTests.swift
//  PremierInn
//
//  Created by Raiu, George Marius (Cognizant) on 20.02.2025.
//  Copyright © 2025 Whitbread. All rights reserved.
//

import Testing
import SimpleNetwork
@testable import PremierInn
import Foundation

class GuestDetailsInteractorTests {
    class MockBookingSummaryCIOLViewModel: BookingSummaryCIOLViewModelProtocol {
        var image: URL?

        var hotelName: String?

        var duration: String?

        var summary: String?
    }
    class MockProvider: PreStayDataProvider {
        func reservation(reservationDetails: SimpleNetwork.ReservationDetails, hotelCode: String?, bookingDetails: SimpleNetwork.BookingDetails?, completion: @escaping (SimpleNetwork.Reservation?, (any Error)?) -> Void) {}
        
        func authorizePayment() async throws -> SimpleNetwork.CCCPPaymentProviderResponse? { return nil }

        func attachFileToReservation(params: SimpleNetwork.AuthorizationFileAttachmentParams) async throws -> SimpleNetwork.StatusResult? { return nil }

        func updatePreCheckInStatus(params: SimpleNetwork.UpdatePrecheckInParams) async throws -> SimpleNetwork.StatusResult? { return nil }

        var didCallCheckin = false
        var didCallHoldBooking = false
        var hasError = false
        func getPackages(
            reservationId: String,
            bookingDetails: SimpleNetwork.BookingDetails,
            hotelCode: String, bookingFlowId: String?,
            showMealInclusiveRate: Bool,
            completion: @escaping (([SimpleNetwork.UpsellItem], [SimpleNetwork.UpsellItem], SimpleNetwork.CityTaxResponse, GoshPackage?)?, (any Error)?) -> Void
        ) {

        }

        func performHoldBookingWithGuests(bookingDetails: SimpleNetwork.BookingDetails, isCiolFlow: Bool, isRegCard: Bool, completion: @escaping (Bool, (any Error)?) -> Void) {
didCallHoldBooking = true
completion(!hasError, nil)
        }

        func confirmPreCheckInOut(basketReference: String, type: CiolRequestType, isCiol _: Bool, completion: @escaping (ConfirmPreCheckInOut?, (any Error)?) -> Void) {
            didCallCheckin = true
        }

        func getHotelPreferences(hotelCode: String, completion: @escaping ([SimpleNetwork.HotelPreference]?, (any Error)?) -> Void) {

        }

        func updateReservationPreferences(hotelCode: String, reservationIds: [String], preferencesCollections: [SimpleNetwork.PreferencesCollection], completion: @escaping (PremierInn.PIDictionary?, (any Error)?) -> Void) {

        }

        func ciolBackgroundCharge(basketReference: String, token: String, completion: @escaping (SimpleNetwork.CiolBackgroundChargeResponse?, (any Error)?) -> Void) { }
    }
    class MockOutput: GuestDetailsInteractorOutput {
        var didCallReload = false
        var didCallToCompletion = false
        var didCallToPayment = false
        var didCallToUpsells = false
        var didCallStart = false
        var didCallShowCityTaxDisclaimer = false

        func reload() {
            didCallReload = true
        }

        func goToUpsells(upsellInput: PremierInn.CiolUpsellInputParams) {
            didCallToUpsells = true
        }

        func goToPayment(with inputParams: PremierInn.CiolReviewAndPayInputParams) {
            didCallToPayment = true
        }

        func goToCompletion(ciolConfirmationDetails: PremierInn.CiolConfirmationDetails) {
            didCallToCompletion = true
        }

        func didFinish(error: PremierInn.CIOLError?) {

        }

        func didStart() {
            didCallStart = true
        }

        func stopLoadingUI(error: (any Error)?) {}

        func setupThreeCIpage(for response: SimpleNetwork.CCCPPaymentResponse) {}

        func goToCompletion(error: (any Error)?, ciolConfirmationDetails: PremierInn.CiolConfirmationDetails) {}
        
        func goDirectlyToCompletion(error: (any Error)?, ciolConfirmationDetails: PremierInn.CiolConfirmationDetails) {}

        func reloadBalance() {}

        func startLoading() {}

        func showCityTaxDisclaimer() {
            didCallShowCityTaxDisclaimer = true
        }
    }

    class MockInput: GuestDetailsInputBlueprint {
        
        var prestayInputParams: PremierInn.PreStayInputParams
        var upsellInput: PremierInn.CiolUpsellInputParams?
        var defaultAnalytics: PIDictionary
        var room: Room

        init(prestayInputParams: PremierInn.PreStayInputParams, upsellInput: PremierInn.CiolUpsellInputParams? = nil, room: Room) {
            self.prestayInputParams = prestayInputParams
            self.upsellInput = upsellInput
            self.room = room
            self.defaultAnalytics = [:]
        }
    }
    var sut: GuestDetailsInteractor!
    var output: MockOutput!
    var mockProvider: MockProvider!
    let mockRoom: Room = {
        let room = Room()
        let lead = try? User(title: "title", firstName: "leadfirstname", lastName: "leadlastname")
        lead?.isAccompanyingGuest = false
        let accompanyingGuest = try?  User(title: "title", firstName: "firstname", lastName: "lastname")

        room.guestList = [lead,accompanyingGuest].compactMap { $0 }
        return room
    }()

    let validRoom = {
        let room = Room()
        room.roomId = "1234"
        let lead = try? User(title: "title", firstName: "leadfirstname", lastName: "leadlastname")
        lead?.isAccompanyingGuest = false
        lead?.dob = "01.01.1990"
        lead?.country = .init(code: "UK", name: "UK", isoCode: "code", dialingCode: "+01", flagImage: "flag", passportRequired: true, nationality: "British")
        lead?.passport = .init(number: "123456", countryOfIssue: "UK")
        lead?.address = try? Address(dictionary: ["addressline1": "Royal Victoria Dock", "addressline2": "2 Festoon Way", "addressline3": "London", "countryCode": "GB", "postcode": "E16 1SJ"])
        let accompanyingGuest = try?  User(title: "title", firstName: "firstname", lastName: "lastname")
        lead?.isAccompanyingGuest = true
        accompanyingGuest?.dob = "01.01.1990"
        accompanyingGuest?.country = .init(code: "UK", name: "UK", isoCode: "code", dialingCode: "+01", flagImage: "flag", passportRequired: true, nationality: "British")
        accompanyingGuest?.passport = .init(number: "123456", countryOfIssue: "UK")
        accompanyingGuest?.address = try? Address(dictionary: ["addressline1": "Royal Victoria Dock", "addressline2": "2 Festoon Way", "addressline3": "London", "countryCode": "GB", "postcode": "E16 1SJ"])
        room.guestList = [lead,accompanyingGuest].compactMap { $0 }
        return room
    }()



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

    func inputParams(
        stay: Stay,
        room: Room,
        paymentActions: CiolPaymentActionsResponse? = nil,
        isDirect: Bool = true
    ) -> PreStayInputParams {
        PreStayInputParams(flow: CIOLStartFlow.bookingConfirmation,
                           stay: stay,
                           hotelCode: "LONEUS",
                           hotelBrand: HotelBrand.premierInnGermany,
                           adultsCountDescription: "",
                           adultsCount: 2,
                           childrenCountDescription: "",
                           childrenCount: 2,
                           nightsCountDescription: "",
                           roomsCountDescription: "",
                           rooms: [room],
                           ciolPaymentActions: paymentActions,
                           selectedPreference: nil,
                           isBusinessTrip: false,
                           hasDERegCard: false,
                           isDirect: isDirect)
    }

    @Test func testValidateError() async throws {
        #expect(sut.guests.isNotEmpty)
        sut.validate()
        #expect(output.didCallReload)
        #expect(output.didCallToPayment == false)
        #expect(output.didCallToUpsells == false)
        #expect(output.didCallToCompletion == false)
    }

    @Test func testGuestStatus() async throws {
        #expect(sut.guestStatus(user: try! User(title: "title", firstName: "leadfirstname", lastName: "leadlastname"), guestType: .additional) == .error)
    }

    @Test func testUpdateInput() async throws {
        sut.updateInput(with: .init(flow: .regCard(index: 0)))
        #expect(output.didCallReload)
    }

    @Test func testValid() async throws {
        let prestayInput = inputParams(stay: stay, room: validRoom)
        sut = GuestDetailsInteractor(input: MockInput(prestayInputParams: prestayInput, room: validRoom), provider: self.mockProvider)
        sut.output = self.output
        #expect(sut.guests.isNotEmpty)
        sut.validate()
        #expect(output.didCallReload)
        #expect(mockProvider.didCallHoldBooking)
        #expect(sut.guests.first?.status != .error)
    }

    @Test func testValidWithUpsell() async throws {
        let remoteConfig = MockRemoteConfig(featureCIOLUpsells: true)
        SettingsManager.sharedInstance.piRemoteConfig = remoteConfig
        let upsellInput = CiolUpsellInputParams(availableUpsells: nil, bookedUpsells: nil, rooms: [], hasChildren: true, isMultiRoom: false, nights: 0, priceBreakdownViewModel: MockCIOLPriceBreakdownViewModel(), bookingSummaryViewModel: MockBookingSummaryCIOLViewModel(), flow: .bookingConfirmation, leadBookerFirstName: nil, hotelBrand: nil, address: nil, bookingReference: nil, stay: stay, reservationID: nil, hotelID: "", arrivalDate: nil, departureDate: nil, outstandingBalance: nil, selectedPreference: nil, hotelImage: nil, analyticsParams: [:])

        let prestayInput = inputParams(stay: stay, room: validRoom)
        sut = GuestDetailsInteractor(input: MockInput(prestayInputParams: prestayInput,
                                                      upsellInput: upsellInput,
                                                      room: validRoom), provider: self.mockProvider)
        sut.output = self.output
        #expect(sut.guests.isNotEmpty)
        sut.validate()
        #expect(output.didCallReload)
        #expect(mockProvider.didCallHoldBooking)
        #expect(output.didCallToUpsells)
    }

    @Test func testValidWithPayment() async throws {


        var prestayInput = inputParams(stay: stay, room: validRoom)
        prestayInput.outstandingBalance = .init(amount: 100.0, currencyCode: "EUR")
        sut = GuestDetailsInteractor(input: MockInput(prestayInputParams: prestayInput,
                                                      room: validRoom), provider: self.mockProvider)
        sut.output = self.output
        #expect(sut.guests.isNotEmpty)
        sut.validate()
        #expect(output.didCallReload)
        #expect(mockProvider.didCallHoldBooking)
        #expect(output.didCallToPayment)
    }

    @Test(arguments: [
        (nil, false, false),
        (nil, true, false),
        (CiolPaymentActionsResponse.cityTaxResponse, false, false),
        (CiolPaymentActionsResponse.cityTaxResponse, true, true)
    ])
    func testHandleContinueButtonTapToShowThirdPartyBookingWithCityTaxDisclaimer(
        paymentActions: CiolPaymentActionsResponse?,
        isFeatureFlagOn: Bool,
        expected: Bool
    ) {
        // GIVEN
        let preStayInput = inputParams(
            stay: stay,
            room: validRoom,
            paymentActions: paymentActions
        )
        
        let settingsManager = MockSettingsManager()
        settingsManager.featureThirdPartyPrepaid = isFeatureFlagOn
        
        sut = GuestDetailsInteractor(
            input: MockInput(prestayInputParams: preStayInput, room: validRoom),
            provider: mockProvider,
            settingsManager: settingsManager
        )
        
        let mockOutput = MockOutput()
        sut.output = mockOutput
        
        // WHEN
        sut.handleContinueButtonTap()
        
        // THEN
        #expect(mockOutput.didCallShowCityTaxDisclaimer == expected)
    }

    @Test
    func testPriceVMForThirdPartyOutstandingBalance() {
        // GIVEN
        let preStayInput = inputParams(
            stay: stay,
            room: validRoom,
            paymentActions: .outstandingBalanceResponse,
            isDirect: false
        )
        
        let settingsManager = MockSettingsManager()
        
        sut = GuestDetailsInteractor(
            input: MockInput(prestayInputParams: preStayInput, room: validRoom),
            provider: mockProvider,
            settingsManager: settingsManager
        )
        
        let mockOutput = MockOutput()
        sut.output = mockOutput
        
        // WHEN
        let result = sut.priceVM
        
        // THEN
        #expect(result.ctaTitle == PILocalizedString("Continue"))
        #expect(result.totalValue == "£133.00")
        #expect(result.items.first?.name == PILocalizedString("preStayOutstandingBalance"))
        #expect(result.items.first?.value == CiolPaymentActionsResponse.outstandingBalanceResponse.outstandingBalance)
        #expect(result.items.first?.quantity == 1)
    }

    init() throws {
        let prestayInput = inputParams(stay: stay, room: mockRoom)
        self.output = MockOutput()
        self.mockProvider = MockProvider()
        sut = GuestDetailsInteractor(input: MockInput(prestayInputParams: prestayInput, room: mockRoom), provider: self.mockProvider)
        sut.output = self.output
    }
}
