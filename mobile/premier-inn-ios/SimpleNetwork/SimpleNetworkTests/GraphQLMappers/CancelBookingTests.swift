//
//  CancelReservationTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 21/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import XCTest

class CancelReservationTests: XCTestCase {
    var sut = PIDictionary()

override func setUp() {
    let fileURL = Bundle.module.url(forResource: "graphQLCancelReservation", withExtension: "json")!
    let data = try! Data(contentsOf: fileURL)
    let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
    let dataDictionary = jsonDictionary["data"] as! PIDictionary
    let cancelReservationDictionary = dataDictionary["cancelReservation"] as! PIDictionary
    
    sut = CancelReservationMapper.map(input: cancelReservationDictionary)
}

    func testCancelReservation() {

        let cancellationId = sut["cancellationId"] as! String

        XCTAssertEqual(cancellationId, "AWM7463354")
    }
}
