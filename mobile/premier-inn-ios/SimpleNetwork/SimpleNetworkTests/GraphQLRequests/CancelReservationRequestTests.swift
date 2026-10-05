//
//  CancelReservationRequestTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 14/11/2023.
//  Copyright © 2023 Whitbread. All rights reserved.
//

import XCTest
import Alamofire
@testable import SimpleNetwork

final class CancelReservationRequestTests: XCTestCase {

    let webservice = Webservice.developmentGraphQL

    func testCancelReservation() {

        let reservationDetails = ReservationDetails(reservationId: "", surname: "", arrivalDate: Date(), business: false, token: nil)
        do {
        _  = try webservice.cancelReservation(reservationDetails: reservationDetails, hotelCode: nil)
            XCTFail()
        } catch {
            XCTAssertEqual(error as! GraphQLError, GraphQLError.missingHotelCode)
        }
    }
}
