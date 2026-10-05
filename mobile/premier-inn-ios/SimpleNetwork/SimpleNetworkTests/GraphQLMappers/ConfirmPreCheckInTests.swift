//
//  ConfirmPreCheckInTests.swift
//  SimpleNetworkTests
//
//  Created by Muresan, Andreea (Cognizant) on 16.10.2024.
//  Copyright © 2024 Whitbread. All rights reserved.
//

import SimpleNetwork
import XCTest

class ConfirmPreCheckInTests: XCTestCase {
    var sut: ConfirmPreCheckInOut?
    
    override func setUp() {
        let fileURL = Bundle.module.url(forResource: "graphQLConfirmPreCheckIn", withExtension: "json")!
        let data = try! Data(contentsOf: fileURL)
        let jsonDictionary = try! JSONSerialization.jsonObject(with: data, options: .allowFragments) as! PIDictionary
        let dataDictionary = jsonDictionary["data"] as! PIDictionary
        let confirmPreCheckIn = dataDictionary["confirmPreCheckIn"] as! PIDictionary
        
        do {
            let confirmPreCheckInData = try JSONSerialization.data(withJSONObject: confirmPreCheckIn, options: .prettyPrinted)

            let decoder = JSONDecoder()
            sut = try decoder.decode(ConfirmPreCheckInOut.self, from: confirmPreCheckInData)
        } catch {
            XCTFail("error thrown when decoding HeaderInformation")
        }
    }
    
    func testconfirmPreCheckIn() {
        XCTAssertNotNil(sut)
        XCTAssertEqual(sut?.basketReference, "AKU-f547a8a5-c0d9-4652-9bdc-24ee366df732")
        XCTAssertEqual(sut?.basketStatus.rawValue, "PRE_CHECKED_IN")
        XCTAssertNil(sut?.basketError)
    }
}
