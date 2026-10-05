//
//  InitiatePaymentMapperTests.swift
//  SimpleNetworkTests
//
//  Created by Louis Faria-Softly on 26/08/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation
import SimpleNetwork
import XCTest

class InitiatePaymentTests: XCTestCase {

    var sut = PIDictionary()

    override func setUp() {

        let fileURL = Bundle.module.url(forResource: "graphQLInitiatePayment", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let initiatePaymentDictionary = dataDictionary["initiatePayment"] as! PIDictionary

        let initiatePaymentData = try? JSONSerialization.data(withJSONObject: initiatePaymentDictionary, options: .prettyPrinted)
        let paymentResponse = try? JSONDecoder().decode(CCCPPaymentResponse.self, from: data)

        XCTAssertNotNil(paymentResponse?.paymentRequiredDetails?.htmlString)
    }
}
