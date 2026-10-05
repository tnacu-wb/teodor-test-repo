//
//  AmendDatesInteractorTests.swift
//  PremierInnDEVUnitTests
//
//  Created by Freddie Parks on 20/11/2018.
//  Copyright © 2018 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

private class MockDataProvider: AmendDatesDataProvider {

    private let rate = Rate(dictionary: ["cardFeeApplies": true, "classification": "A", "totalCost": ["amount": "191.50", "currency": "GBP"]])

    func hotelAvailabilityForAmendBooking(withHotelCode hotelCode: String, bookingDetails: SimpleNetwork.BookingDetails, reservationID: String, andRoomIDs roomIDs: [String], brand: HotelBrand?, completion: @escaping (HotelAvailabilityResponse?, Error?) -> Void) {

        let hotelAvailabilityResponse: HotelAvailabilityResponse = (
            [rate],
            nil,
            true,
            true,
            nil,
            nil,
            nil,
            nil,
            nil,
            nil
        )
        completion(hotelAvailabilityResponse, nil)
    }

    func holdBooking(bookingDetails: BookingDetails, completion: @escaping (String?, Error?) -> Void) {

        completion("HelloWorld!", nil)
    }

    func amendDates(temporaryReference: String, arrivalDate: Date, departureDate: Date, token: String, completion: @escaping (_ temporaryBookingReference: String?, _ error: Error?) -> Void) {

        completion("fake_reference", nil)
    }

    func amendSummary(reservationDetails: SimpleNetwork.ReservationDetails, tempBookingReference: String, completion: @escaping (SimpleNetwork.AmendSummary?, Error?) -> Void) {
        completion(nil, nil)
    }

    func getPromotionsInformation(criteria: PromotionsInformationCriteria, completion: @escaping (_ response: PromotionsInformation?, _ error: Error?) -> Void) {
        completion(nil, nil)
    }
}

class AmendDatesInteractorTests: XCTestCase {

    // MARK: - Properties

    private var analytics: MockAnalyticsManager!
    var interactor: AmendDatesInteractor?
    var criteria: Criteria!

    // MARK: - Lifecycle

    override func setUp() {

        guard let hotel = try? Hotel(dictionary: [
            "name": "Hotel Name",
            "code": "LONLEI",
            "prepaymentAllowed": true,
            "address": ["postcode": "a", "addressline1": "a", "addressline2": "a", "addressline3": "a", "country": "a"],
            "acceptedCreditCards": [
                [
                    "code": "AC",
                    "feeAmount": "",
                    "feeCurrency": "",
                    "paymentOnly": false,
                    "listOrder": "3",
                    "name": "Mastercard Credit",
                    "schemeLogo": "/content/dam/global/booking/Mastercard.jpg"
                ]
            ]
        ]) else { return }

        criteria = Criteria()

        let cost = Cost(amount: 200, currencyCode: "GBP")
        let amendOperaDetails = AmendOperaDetails(bookingReference: nil, originalBasketReference: nil, temporaryBasketReference: nil, token: nil, previousUpsells: [], brand: nil, bookerEmail: nil, bookerSurname: nil)
        let nightsRestriction = false

        interactor = AmendDatesInteractor(with: (criteria, cost, nightsRestriction, amendOperaDetails, isBusiness: false, promotionDetails: nil, rulesToFollow: Constants.leisureRulesHardCoded))

        interactor?.amendDatesDataProvider = MockDataProvider()

        analytics = MockAnalyticsManager()
        interactor?.analytics = analytics
    }

    override func tearDown() {

        criteria = nil
        analytics = nil
        interactor = nil

        super.tearDown()
    }

    // MARK: - Tests

    func testView_whenDuplicateOfExistingNights_invokesTrackAction() {

        let result = interactor?.duplicateOfExistingNights(nights: 1)

        let action = analytics.actions.first
        let userInfo = analytics.userInfos.first

        XCTAssertNotNil(result)
        XCTAssertEqual(action, "amend.change")
        XCTAssertEqual(userInfo?["amend.change"], "1 nights to 1 nights")
    }

    func testView_whenDuplicateOfExisting_invokesTrackAction() {

        let result = interactor?.duplicateOfExisting(date: Date(), nights: 2)

        let action = analytics.actions.first
        let userInfo = analytics.userInfos.first

        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "dd/MM/yyyy"
        let today = dateFormatter.string(from: Date())

        XCTAssertNotNil(result)
        XCTAssertEqual(action, "amend.change")
        XCTAssertEqual(userInfo?["amend.change"], "Arrival date changed to " + today)
    }

    func testDuplicateOfExistingDate() {

        var criteria = Criteria()

        XCTAssert(interactor?.duplicateOfExisting(date: criteria.arrivalDate, nights: criteria.nights) == true)

        if let futureDate = criteria.arrivalDate.dateByAddingUnit(unitType: .day, number: 10) {
            criteria.arrivalDate = futureDate

            XCTAssert(interactor?.duplicateOfExisting(date: criteria.arrivalDate, nights: criteria.nights) == false)
        }
    }
}
