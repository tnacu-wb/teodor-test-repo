//
//  AmendSummaryTests.swift
//  SimpleNetworkTests
//
//  Created by Santa Gurung on 13/10/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class AmendSummaryTests: XCTestCase {
    
    func testAmendSummaryDecoding() {
        let dataDict = getDictFor(file: "graphQLAmendSummary", in: type(of: self))
        let amendSummaryDict = dataDict["amendSummary"] as! PIDictionary
        let data = try! JSONSerialization.data(withJSONObject: amendSummaryDict)

        let amendSummary = try! JSONDecoder().decode(AmendSummary.self, from: data)
        XCTAssertEqual(amendSummary.paymentOptions?.payNow, true)
        XCTAssertEqual(amendSummary.paymentOptions?.payOnArrival, true)
        XCTAssertEqual(amendSummary.paymentCardDetails?.cardNumberMasked, "XXXXXXXXXXXX1103")
    }

    func testVariablesAreValidForAmendSummary() {
        let arrivalDate = {
            var dateComponents = DateComponents()
            dateComponents.year = 2050
            dateComponents.month = 9
            dateComponents.day = 12
            dateComponents.hour = 12
            dateComponents.timeZone = TimeZone(identifier: "Europe/London")
            return Calendar.current.date(from: dateComponents)!
        }()

        let reservationDetails = ReservationDetails(
            reservationId: "AJK-77c79ec8-e6b4-4696-86d1-186f5c406f72",
            surname: "Surname",
            arrivalDate: arrivalDate,
            business: false,
            token: "RELMIYHxzz7+4igo2biNBvySeed92ZAfDjoIE1BM2zGAQUeiJal6Wt0Md2i6cCsh9k7CIJx6A44/lwzoP791B9y2E/iYJ8HmNn2JQdm7ucDYJ/8="
        )
        let dict = GraphQL.amendSummaryVariables(
            reservationDetails: reservationDetails,
            temporaryReference: "AJK-e057d15b-71bb-4d35-ba19-23341dad4f05"
        )

        XCTAssertEqual(dict["originalBasketRef"] as! String, "AJK-77c79ec8-e6b4-4696-86d1-186f5c406f72")
        XCTAssertEqual(dict["token"] as! String, "RELMIYHxzz7+4igo2biNBvySeed92ZAfDjoIE1BM2zGAQUeiJal6Wt0Md2i6cCsh9k7CIJx6A44/lwzoP791B9y2E/iYJ8HmNn2JQdm7ucDYJ/8=")
        XCTAssertEqual(dict["copyBasketRef"] as! String, "AJK-e057d15b-71bb-4d35-ba19-23341dad4f05")
        let bookingChannel = dict["bookingChannel"] as! PIDictionary
        XCTAssertEqual(bookingChannel["channel"] as! String, "PI")
    }
}
