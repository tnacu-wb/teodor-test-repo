//
//  BookingInfoTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 21/08/2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import XCTest
import SimpleNetwork
final class BookingInfoTests: XCTestCase {

    var bookingInformation: BookingInformation? = nil

    override func setUp() {
        let fileURL = Bundle.module.url(forResource: "graphQLBookingInfo", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let bookingInfoDict = dataDictionary["bookingInformation"] as! PIDictionary

        do {
            let bookingInfoData = try JSONSerialization.data(withJSONObject: bookingInfoDict, options: .prettyPrinted)

            let decoder = JSONDecoder()
            bookingInformation = try decoder.decode(BookingInformation.self, from: bookingInfoData)
        } catch {
            XCTFail("error thrown when decoding HeaderInformation")
        }
    }

    func testError() {
        XCTAssert(bookingInformation!.bookingFlowId == "booking-ct-a1")


    }

}
