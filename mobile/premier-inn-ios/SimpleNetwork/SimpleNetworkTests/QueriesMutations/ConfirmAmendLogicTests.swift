//
//  ConfirmAmendLogicTests.swift
//  SimpleNetworkTests
//
//  Created by Santa Gurung on 23/10/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
@testable import SimpleNetwork

final class ConfirmAmendLogicTests: XCTestCase {

    func testConfirmAmendLogicDecoding() {
        let dataDict = getDictFor(file: "graphQLConfirmAmendLogicPN", in: type(of: self))
        let confirmAmendLogicDict = dataDict["confirmAmendLogic"] as! PIDictionary
        let data = try! JSONSerialization.data(withJSONObject: confirmAmendLogicDict)

        let confirmAmendLogic = try! JSONDecoder().decode(CreatePaymentAmend.self, from: data)
        XCTAssertEqual(confirmAmendLogic.payment?.status?.rawValue, "PAYMENT_REQUIRED")
        XCTAssertEqual(confirmAmendLogic.payment?.paymentRequiredDetails?.paymentRedirect, "PCFET0NUWVBFIGh0bWw+CjxodG1sPgo8Ym9keSBL2h0bWw+Cg==...")
    }

    func testVariablesAreValidForConfirmAmendLogicWithPayNowSelected() {
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
        let dict = GraphQL.confirmAmendLogicParameters(
            reservationDetails: reservationDetails, 
            tempBookingReference: "AJK-e057d15b-71bb-4d35-ba19-23341dad4f05",
            selectedPaymentOption: "PAY_NOW",
            environment: "https://www.dev.premierinn.digital"
        )

        let confirmAmendLogicCriteria = dict["confirmAmendLogicCriteria"] as! PIDictionary
        XCTAssertEqual(confirmAmendLogicCriteria["originalBookingRef"] as! String, "AJK-77c79ec8-e6b4-4696-86d1-186f5c406f72")
        XCTAssertEqual(confirmAmendLogicCriteria["token"] as! String, "RELMIYHxzz7+4igo2biNBvySeed92ZAfDjoIE1BM2zGAQUeiJal6Wt0Md2i6cCsh9k7CIJx6A44/lwzoP791B9y2E/iYJ8HmNn2JQdm7ucDYJ/8=")
        XCTAssertEqual(confirmAmendLogicCriteria["tempBookingRef"] as! String, "AJK-e057d15b-71bb-4d35-ba19-23341dad4f05")
        XCTAssertEqual(confirmAmendLogicCriteria["paymentOptionSelected"] as! String, "PAY_NOW")
        let bookingChannel = confirmAmendLogicCriteria["bookingChannel"] as! PIDictionary
        XCTAssertEqual(bookingChannel["channel"] as! String, "PI")
    }
}
