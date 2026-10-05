//
//  ReviewAndBookInteractorTests.swift
//  PremierInn
//
//  Created by Filippo Minelle on 25/06/2020.
//  Copyright © 2020 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
@testable import PremierInn

class ReviewAndBookInteractorTests: XCTestCase {

    private var mockBookingConfirmation: BookingConfirmation!
    private var interactor: ReviewAndBookInteractor!
    private var analytics: MockAnalyticsManager!

    // MARK: - Lifecycle

    override func setUp() {
        super.setUp()

        mockBookingConfirmation = BookingConfirmation.mock!

        let bookingDetails = BookingDetails()
        bookingDetails.operaBookingReference = "MOCK666999"

        interactor = ReviewAndBookInteractor(bookingDetails: bookingDetails)

        analytics = MockAnalyticsManager()
        interactor.analytics = analytics
    }

    override func tearDown() {

        mockBookingConfirmation = nil
        interactor = nil
        analytics = nil

        super.tearDown()
    }

    // MARK: - Tests

    func testInteractor_whenBookingConfirmed_invokesAdobeTrackBookingConfirmation() {
        UserSessionManager.sharedInstance.piUserLoggedOut()

        interactor.trackBookingConfirmation(confirmation: mockBookingConfirmation)

        let state = analytics.states.first
        XCTAssertEqual(state, "iOS:PI:UK: Booking Confirmation")

        guard let dictionary = analytics.dictionaries.first else {
            XCTFail()
            return
        }

        // All
        let environment: String? = dictionary["analyticsData.all.environment"]
        let screenType: String? = dictionary["analyticsData.all.screenType"]
        // let userLogin: String? = dictionary["analyticsData.all.userLogin"]
        // let timeZone: String? = dictionary["analyticsData.all.timeZone"]
        let language: String? = dictionary["analyticsData.all.language"]
        let time: String? = dictionary["analyticsData.all.time"]

        XCTAssertEqual(environment, "debug")
        XCTAssertEqual(screenType, "iOS: Booking Flow")
        // XCTAssertEqual(userLogin, "Logged Out")
        // XCTAssertEqual(timeZone, "Europe/London (current)")
        XCTAssertEqual(language, "en")
        let date = DateFormatter.analyticsTimeFormatter.date(from: time!)
        XCTAssertNotNil(date)

        // Conf
        let roomTypes: String? = dictionary["analyticsData.conf.roomTypes"]
        let customerType: String? = dictionary["analyticsData.conf.customerType"]
        let GOSH: String? = dictionary["analyticsData.conf.GOSH"]
        let prepay: String? = dictionary["analyticsData.conf.prepay"]
        let leadDays: String? = dictionary["analyticsData.conf.leadDays"]
        let bookingReference: String? = dictionary["analyticsData.conf.bookingReference"]
        let purchase: String? = dictionary["analyticsData.conf.purchase"]
        // let checkIn: String? = dictionary["analyticsData.conf.check-in"]
        // let checkOut: String? = dictionary["analyticsData.conf.check-out"]

        XCTAssertEqual(roomTypes, "double")
        XCTAssertEqual(customerType, "Leisure")
        XCTAssertEqual(GOSH, nil)
        XCTAssertEqual(prepay, "Non-Prepay")
        XCTAssertEqual(leadDays, "1")
        XCTAssertEqual(bookingReference, "MOCK666999")
        XCTAssertEqual(purchase, "1")
        // XCTAssertEqual(checkIn, "01/07/2020")
        // XCTAssertEqual(checkOut, "02/07/2020")

        // Events
        // let event36: String? = dictionary["analyticsData.conf.events.event36"]
        let event30: String? = dictionary["analyticsData.conf.events.event30"]
        // let event20: String? = dictionary["analyticsData.conf.events.event20"]
        let event31: String? = dictionary["analyticsData.conf.events.event31"]
        // let event37: String? = dictionary["analyticsData.conf.events.event37"]
        let event54: String? = dictionary["analyticsData.conf.events.event54"]

        // XCTAssertEqual(event36, "0")
        XCTAssertEqual(event30, "0")
        // XCTAssertEqual(event20, "0")
        XCTAssertEqual(event31, "1")
        // XCTAssertEqual(event37, "0")
        XCTAssertEqual(event54, "0")

        // Other
        let events: String? = dictionary["&&events"]
        let products: String? = dictionary["&&products"]

        XCTAssertEqual(events, "")
        XCTAssertEqual(products, "")
    }

    func testInteractor_whenBookingConfirmed_invokesFiriebaseTrackBookingConfirmation() {
        UserSessionManager.sharedInstance.piUserLoggedOut()

        interactor.trackBookingConfirmation(confirmation: mockBookingConfirmation)

        let state = analytics.states.first
        XCTAssertEqual(state, "iOS:PI:UK: Booking Confirmation")

        let parameters = analytics.parameters.first
        let numberOfPassengers: Int = parameters?["number_of_passengers"] as! Int
        let travelClass: String = parameters?["travel_class"] as! String
        let userLogin: Int = parameters?["number_of_nights"] as! Int
        // let start_date: String = parameters?["start_date"] as! String
        let number_of_rooms: Int = parameters?["number_of_rooms"] as! Int
        // let endDate: String = parameters?["end_date"] as! String
        let quantity: Int = parameters?["quantity"] as! Int
        let userType: String = parameters?["user_type"] as! String
        let currency: String = parameters?["currency"] as! String
        let location: String = parameters?["location"] as! String
        let value: Int = parameters?["value"] as! Int
        let paymentType: String = parameters?["payment_type"] as! String

        XCTAssertEqual(numberOfPassengers, 1)
        XCTAssertEqual(travelClass, "")
        XCTAssertEqual(userLogin, 1)
        // XCTAssertEqual(start_date, "2020-07-01")
        XCTAssertEqual(number_of_rooms, 1)
        // XCTAssertEqual(endDate, "2020-07-02")
        XCTAssertEqual(quantity, 1)
        XCTAssertEqual(userType, "")
        XCTAssertEqual(currency, "GBP")
        XCTAssertEqual(location, "")
        XCTAssertEqual(value, 0)
        XCTAssertEqual(paymentType, "")
    }
}
