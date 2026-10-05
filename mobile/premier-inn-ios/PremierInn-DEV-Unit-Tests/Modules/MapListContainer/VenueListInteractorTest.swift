//
//  VenueListInteractorTest.swift
//  PremierInnTests
//
//  Created by Louis Faria-Softly on 13/02/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import MapKit
import SimpleNetwork
@testable import PremierInn

class VenueListInteractorTests: XCTestCase {
    private var interactor: VenuesInteractor!
    private var analytics: MockAnalyticsManager!


    let hotel: Hotel = try! Hotel(dictionary: [
        "name": "Hotel Name",
        "code": "LONLEI",
        "prepaymentAllowed": true,
        "address": ["postcode": "a", "addressline1": "a", "addressline2": "a", "addressline3": "a", "country": "a"],
        "images": [["fileReference": "/content/dam/pi/websites/hotelimages/gb/en/B/BRIPTI/BRIPTI 1.jpg"]]
        ])

    let suggestion = PISuggestion(coordinate: CLLocationCoordinate2D(latitude: 1, longitude: 1))
    override func setUp() {
        super.setUp()

        interactor = VenuesInteractor(hotels: [hotel], suggestion: suggestion, unavailableHotelCode: "")

        analytics = MockAnalyticsManager()
        interactor.analytics = analytics
    }

    func testAnalytics() {
        UserSessionManager.sharedInstance.piUserLoggedOut()

        interactor.trackAvailability(style: .card)
        let state = analytics.states.first
        XCTAssertEqual(state, "iOS:PI:UK: Search Results Map")

        guard let dictionary = analytics.dictionaries.first else {
            XCTFail()
            return
        }

        let time: String? = dictionary["analyticsData.all.time"]

        let date = DateFormatter.analyticsTimeFormatter.date(from: time!)
        XCTAssertNotNil(date)

    }
}
